using System;
using System.Text;
using UnityEngine;

namespace ShineEngine
{
	/// <summary>
	/// 资源信息管理类
	/// </summary>
	public class ResourceInfoControl
	{
		/** 下载器数目 */
		private const int LoadNum=4;

		/** 资源路径 */
		private const int StreamingAssetsPath=1;
		/** 持久化路径 */
		private const int PersistentPath=2;
		/** 临时存储路径 */
		private const int TemporaryCachePath=3;

		private static string _dataPath;
		private static string _streamingAssetsPath;
		private static string _persistentDataPath;
		private static string _temporaryCachePath;

		/** 资源是否是公共资源目录(本地使用) */
		private static Func<string,bool> _isCommonResourceFunc;

		private static Action _overCall;
		private static Action<VersionSaveData> _overCall2;

		/** 加载组 */
		private static ShineLoader _loader;
		/** 加载器组 */
		private static ShineLoader[] _loaders;
		/** 加载中组 */
		private static ResourceSaveData[] _loadings;
		/** 加载数记录 */
		private static int _loadingNum;

		//--初始化部分--//

		/** 版本文件路径 */
		private static string _versionPath;

		/** 版本数据 */
		private static VersionSaveData _versionData;
		/** 包版本数据 */
		private static VersionSaveData _packVersionData;
		/** 版本改变 */
		private static bool _versionModified=false;

		private static BytesWriteStream _versionStream;

		/** 准备下载队列 */
		private static SQueue<ResourceSaveData> _loadQueue;
		/** 需要下载大小 */
		private static int _needLoadSize;
		/** 加载完毕大小 */
		private static int _loadCompleteSize;
		/** 当前下载大小 */
		private static int _currentLoadSize;

		private static int _frameIndex=-1;

		//--初始化部分--//

		public static void init()
		{
			initBase();

			_versionPath=getPersistentPath(ShineGlobal.versionInfoPath,false);

			_loader=new ShineLoader();
			_loaders=new ShineLoader[LoadNum];
			_loadings=new ResourceSaveData[LoadNum];

			for(int i=0;i<LoadNum;i++)
			{
				initLoader(i);
			}

			_frameIndex=TimeDriver.instance.setFrame(onFrame);

			_versionStream=new BytesWriteStream();
		}

		/** 部分初始化 */
		public static void initBase()
		{
			_dataPath=Application.dataPath;
			_streamingAssetsPath=Application.streamingAssetsPath;
			_persistentDataPath=Application.persistentDataPath;
			_temporaryCachePath=Application.temporaryCachePath;
		}

		/** 获取app包扩展名 */
		public static string getAppExName()
		{
			switch(SystemControl.platform)
			{
				case RuntimePlatform.WindowsEditor:
				case RuntimePlatform.WindowsPlayer:
				{
					return "exe";
				}

				case RuntimePlatform.OSXEditor:
				case RuntimePlatform.OSXPlayer:
				{
					return "dmg";
				}
					break;
				case RuntimePlatform.Android:
				{
					return "apk";
				}
				case RuntimePlatform.IPhonePlayer:
				{
					return "ipa";
				}
				default:
				{
					Ctrl.throwError("暂未支持的平台类型",SystemControl.platform);
					return "";
				}
					break;
			}
		}

		/** 获取平台名 */
		public static string getPlatformName()
		{
			switch(SystemControl.platform)
			{
				case RuntimePlatform.WindowsEditor:
				case RuntimePlatform.WindowsPlayer:
				{
					return "windows";
				}

				case RuntimePlatform.OSXEditor:
				case RuntimePlatform.OSXPlayer:
				{
					return "mac";
				}
					break;
				case RuntimePlatform.Android:
				{
					return "android";
				}
				case RuntimePlatform.IPhonePlayer:
				{
					return "ios";
				}
				default:
				{
					Ctrl.throwError("暂未支持的平台类型",SystemControl.platform);
					return "";
				}
					break;
			}
		}

		/** 获取app下载路径 */
		public static string getAppPath(int appVersion)
		{
			return ShineGlobal.cdnSourcePath+'/'+ getPlatformName() + "/app_" + appVersion + "/" + ShineSetting.gameName+"."+getAppExName();
		}

		/** 获取cdn资源下载路径(WWW用) */
		public static string getCDNResourcePath(string url,int resourceVersion)
		{
			if(!ShineSetting.isRelease)
			{
				return getStreamingAssetsPath(url,true);
			}

			StringBuilder sb=StringBuilderPool.create();

			sb.Append(ShineGlobal.cdnSourcePath);
			sb.Append('/');
			sb.Append(getPlatformName());
			sb.Append("/resource_");
			sb.Append(resourceVersion);
			sb.Append('/');
			sb.Append(url);

			return StringBuilderPool.releaseStr(sb);
		}

		/** 获取资源路径(streamingAsset)(needPlatform:是否需要运行平台前缀)(forWWW:是否为www用) */
		public static string getStreamingAssetsPath(string url,bool forWWW)
		{
			return toGetPath(StreamingAssetsPath,url,forWWW);
		}

		/** 获取持久化资源路径(PersistentPath)(needPlatform:是否需要运行平台前缀)(forWWW:是否为www用) */
		public static string getPersistentPath(string url,bool forWWW)
		{
			return toGetPath(PersistentPath,url,forWWW);
		}

		/** 获取临时资源路径(TemporaryCachePath)(needPlatform:是否需要运行平台前缀)(forWWW:是否为www用) */
		public static string getTemporaryCachePath(string url,bool forWWW)
		{
			return toGetPath(TemporaryCachePath,url,forWWW);
		}

		/** 获取资源当前保存路径 */
		public static string getResourceSavePath(string url)
		{
			ResourceSaveData data=getResourceSaveData(url);

			if(data==null)
			{
				Ctrl.throwError("不该找不到资源数据",url);
				return "";
			}

			if(data.state==ResourceSaveStateType.StreamingAssetsReady)
			{
				return getStreamingAssetsPath(url,false);
			}
			else if(data.state==ResourceSaveStateType.Downloaded)
			{
				return getPersistentPath(url,false);
			}
			else
			{
				Ctrl.throwError("资源数据保存类型不对",url);
				return "";
			}
		}

		private static string toGetRootPath(int type)
		{
			switch(type)
			{
				case StreamingAssetsPath:
					return _streamingAssetsPath;
				case PersistentPath:
					return _persistentDataPath;
				case TemporaryCachePath:
					return _temporaryCachePath;
			}

			return "";
		}

		/** 获取资源路径(streamingAsset)(needPlatform:是否需要运行平台前缀)(forWWW:是否www加载) */
		private static string toGetPath(int type,string url,bool forWWW)
		{
			StringBuilder sb=StringBuilderPool.create();

			if(forWWW && (type!=StreamingAssetsPath || SystemControl.platform!=RuntimePlatform.Android || !ShineSetting.isRelease))
			{
				sb.Append("file:///");
			}

			//调试模式
			if(!ShineSetting.isRelease && type==StreamingAssetsPath)
			{
				sb.Append(_dataPath);
				sb.Append(ShineGlobal.sourcePath);
				sb.Append('/');

				if(_isCommonResourceFunc!=null && _isCommonResourceFunc(url))
				{
					sb.Append("common/");
				}
				else
				{
					sb.Append(getPlatformName());
					sb.Append('/');
				}
			}
			else
			{
				sb.Append(toGetRootPath(type));

				if(type!=StreamingAssetsPath)
					sb.Append("/resource");

				sb.Append('/');
			}

			sb.Append(url);

			return StringBuilderPool.releaseStr(sb);
		}

		/** 设置是否需要平台前缀方法 */
		public static void setIsCommonResourceFunc(Func<string,bool> func)
		{
			_isCommonResourceFunc=func;
		}

		private static void initLoader(int index)
		{
			(_loaders[index]=new ShineLoader()).setCompleteCall(()=>onLoaderComplete(index));
		}

		private static void onLoaderComplete(int index)
		{
			_loadCompleteSize+=_loadings[index].size;
			_loadingNum--;
			runLoadOne(index);
		}

		private static void onFrame(int delay)
		{
			saveVersion();

			//有加载数
			if(_loadingNum>0)
			{
				int size=_loadCompleteSize;

				ShineLoader loader;

				for(int i=0;i<LoadNum;i++)
				{
					loader=_loaders[i];

					if(loader.isLoading())
					{
						size+=loader.getByteLoaded();
					}

					loader.onFrame(delay);
				}

				if(size>_currentLoadSize)
					_currentLoadSize=size;
			}
		}

		/** 加载资源信息 */
		public static void loadBundleInfo(Action overCall)
		{
			_overCall=overCall;

			_loader.setCompleteCall(initOver);
			_loader.loadResource(ShineGlobal.bundleInfoPath);
		}

		/** 加载资源信息同步(forEditor) */
		public static void loadBundleInfoSync()
		{
			BytesReadStream stream=FileUtils.readFileForBytesReadStream(getStreamingAssetsPath(ShineGlobal.bundleInfoPath,false));

			//存在才加载
			if(stream!=null && stream.checkVersion(ShineGlobal.bundleInfoVersion))
			{
				readBundleInfo(stream);
			}
		}

		private static void initOver()
		{
			BytesReadStream stream=_loader.getBytesRead();

			//检测版本
			if(!stream.checkVersion(ShineGlobal.bundleInfoVersion))
			{
				ShineSetup.exit("bundleInfo结构版本检测不对");
				return;
			}

			readBundleInfo(stream);

			_loader.unload();

			Action func=_overCall;
			_overCall=null;

			if(func!=null)
				func();
		}

		/** 读取bundle信息 */
		public static void readBundleInfo(BytesReadStream stream)
		{
			LoadControl.clearResourceInfo();

			int len=stream.readInt();
			for(int i=0;i<len;i++)
			{
				BundleInfoData data=new BundleInfoData();
				data.readBytes(stream);

				LoadControl.registBundleInfo(data);
			}

			len=stream.readInt();
			for(int i=0;i<len;i++)
			{
				ResourceInfoData data=new ResourceInfoData();
				data.readBytes(stream);

				LoadControl.registResource(data.name,data.id,data.type);
			}

			LoadControl.registBundleOver();
		}

		/** 读取本地版本数据(如已存在则不再读取) */
		public static void loadVersion(Action overFunc)
		{
			if(_versionData!=null)
			{
				if(overFunc!=null)
					overFunc();
				return;
			}

			_overCall=overFunc;

			BytesReadStream stream=FileUtils.readFileForBytesReadStream(_versionPath);

			//存在
			if(stream!=null && stream.checkVersion(ShineGlobal.versionInfoVersion))
			{
				_versionData=new VersionSaveData();
				_versionData.readBytes(stream);

				//本地纠正
				if(!ShineSetting.isRelease)
				{
					makeVersionFirst();
				}
			}
			else
			{
				_versionData=null;
			}

			if(!ShineSetting.isRelease && ShineSetting.localLoadWithOutBundle)
			{
				if(_versionData==null)
				{
					_versionData=new VersionSaveData();
					_versionData.appVersion=1;
					_versionData.resourceVersion=1;
					_versionData.version="1.01";
				}

				if(overFunc!=null)
					overFunc();
				return;
			}

			_loader.setCompleteCall(loadPackVersionOver);
			_loader.loadStreamingAsset(ShineGlobal.versionInfoPath);
		}

		private static void makeVersionFirst()
		{
			_versionData.resourceDic.forEachValue(v=>
			{
				//需要进包的部分
				if(v.saveType==ResourceSaveType.InStreamingAsset && v.state!=ResourceSaveStateType.Downloaded)
				{
					v.state=ResourceSaveStateType.StreamingAssetsReady;
				}
			});

			_versionModified=true;
		}

		private static void loadPackVersionOver()
		{
			BytesReadStream stream=_loader.getBytesRead();

			if(stream!=null && stream.checkVersion(ShineGlobal.versionInfoVersion))
			{
				_packVersionData=new VersionSaveData();
				_packVersionData.readBytes(stream);
			}

			_loader.unload();

			//初次下载
			if(_versionData==null)
			{
				_versionData=_packVersionData;

				if(_versionData==null)
				{
					ShineSetup.exit("没有可用的versionInfo数据,请重新打包");
					return;
				}

				makeVersionFirst();
			}
			//更新app或调试版
			else if(_packVersionData.resourceVersion>_versionData.resourceVersion || !_packVersionData.isRelease)
			{
				if(!ShineSetting.isRelease && ShineSetting.needDebugResourceVersion)
				{
					//其他动作
				}
				else
				{
					//新包覆盖
					if(ShineSetting.newPackNeedCover)
					{
						_versionData=_packVersionData;
						// deleteAllCache();
						deleteAllPackageCache();
						makeVersionFirst();
					}
					else
					{
						mergeVersion(_packVersionData);
						checkResourceExists();
					}
				}
			}

			Action func=_overCall;
			_overCall=null;

			if(func!=null)
				func();
		}

		/** 获取上次版本 */
		public static VersionSaveData getVersion()
		{
			return _versionData;
		}

		/** 标记资源改变 */
		public static void versionModified()
		{
			_versionModified=true;
		}

		/** 保存版本信息 */
		public static void saveVersion()
		{
			//写一次
			if(_versionModified)
			{
				_versionModified=false;

				if(!ShineSetting.debugJumpResourceVersion)
				{
					_versionStream.clear();
					_versionStream.writeVersion(ShineGlobal.versionInfoVersion);
					_versionData.writeBytes(_versionStream);
					FileUtils.writeFileForBytesWriteStream(_versionPath,_versionStream);
					_versionStream.clear();
				}
			}
		}

		/** 获取某资源的信息 */
		public static ResourceSaveData getResourceSaveData(string name)
		{
			return _versionData.resourceDic.get(name);
		}

		/** 与旧版本资源合并(并且删除旧资源) */
		public static void mergeVersion(VersionSaveData data)
		{
			data.resourceDic.forEachValue(v=>
			{
				ResourceSaveData oldData=_versionData.resourceDic.get(v.name);

				if(oldData!=null)
				{
					oldData.dided=true;
					//版本不足(或调试版)
					if(oldData.version<v.version || !data.isRelease)
					{
						//删除
						deleteCacheResoruce(oldData.name);
					}
					//相同版本
					else
					{
						//且已下载好的
						if(oldData.state==ResourceSaveStateType.Downloaded)
						{
							v.state=ResourceSaveStateType.Downloaded;
						}
					}

					//需要进包的部分
					if(v.saveType==ResourceSaveType.InStreamingAsset)
					{
						ResourceSaveData pData=_packVersionData.resourceDic.get(v.name);

						//包内资源相同
						if(pData!=null && pData.version==v.version)
						{
							v.state=ResourceSaveStateType.StreamingAssetsReady;
						}
					}
				}
			});

			_versionData.resourceDic.forEachValue(v=>
			{
				if(!v.dided)
				{
					//删除
					deleteCacheResoruce(v.name);
				}

				v.dided=false;
			});

			_versionData=data;
			_versionModified=true;
		}

		/** 检查资源存在 */
		private static void checkResourceExists()
		{
			ResourceSaveData[] values;
			ResourceSaveData v;

			for(int i=(values=_versionData.resourceDic.getValues()).Length-1;i>=0;--i)
			{
				if((v=values[i])!=null)
				{
					//下载好的不存在了
					if(v.state==ResourceSaveStateType.Downloaded && !FileUtils.fileExists(getPersistentPath(v.name,false)))
					{
						v.state=ResourceSaveStateType.None;//回归
					}
				}
			}
		}

		private static void deleteAllCache()
		{
			ResourceSaveData[] values;
			ResourceSaveData v;

			for(int i=(values=_versionData.resourceDic.getValues()).Length-1;i>=0;--i)
			{
				if((v=values[i])!=null)
				{
					deleteCacheResoruce(v.name);
				}
			}
		}

		/** 删除所有包内 类型为包资源的缓存 */
		private static void deleteAllPackageCache()
		{
			ResourceSaveData[] values;
			ResourceSaveData v;

			for(int i=(values=_versionData.resourceDic.getValues()).Length-1;i>=0;--i)
			{
				if((v=values[i])!=null)
				{
					if(v.saveType==ResourceSaveType.InStreamingAsset || v.saveType==ResourceSaveType.OnlyStreamingAsset)
					{
						deleteCacheResoruce(v.name);
					}
				}
			}
		}

		/** 删除缓存资源 */
		private static void deleteCacheResoruce(string name)
		{
			// //调试模式不删
			// if(!ShineSetting.isRelease)
			// 	return;

			FileUtils.deleteFile(getPersistentPath(name,false));
		}

		/** 读取cdn版本信息 */
		public static void loadCDNVersion(int resourceVersion,Action<VersionSaveData> callFunc)
		{
			//下载路径
			string path=getCDNResourcePath(ShineGlobal.versionInfoPath,resourceVersion);

			_overCall2=callFunc;
			_loader.setCompleteCall(loadCDNVersionOver);
			_loader.loadFromNet(path,true);
		}

		private static void loadCDNVersionOver()
		{
			BytesReadStream stream=_loader.getBytesRead();

			VersionSaveData data=null;

			if(stream!=null && stream.checkVersion(ShineGlobal.versionInfoVersion))
			{
				data=new VersionSaveData();
				data.readBytes(stream);
			}

			_loader.unload();

			Action<VersionSaveData> func=_overCall2;
			_overCall2=null;

			if(func!=null)
				func(data);
		}

		/** 当前版本数据是全下载好的 */
		public static bool isVersionDataReady()
		{
			ResourceSaveData[] values;
			ResourceSaveData v;

			for(int i=(values=_versionData.resourceDic.getValues()).Length-1;i>=0;--i)
			{
				if((v=values[i])!=null)
				{
					//还存在此两种
					if(v.saveType==ResourceSaveType.InStreamingAsset || v.saveType==ResourceSaveType.InPersistentAsset)
						return false;
				}
			}

			return true;
		}

		/** 计算新的更新包的资源大小 */
		public static void countNewVersionSize(VersionSaveData data,VersionReData re)
		{
			re.size=0;
			re.isOnlyConfig=true;

			data.resourceDic.forEachValue(v=>
			{
				ResourceSaveData oldData=_versionData.resourceDic.get(v.name);

				if(oldData!=null)
				{
					//需要下载的部分
					if(oldData.version<v.version
					   || (v.state==ResourceSaveStateType.None && ResourceSaveType.needFirst(v.saveType) && oldData.state==ResourceSaveStateType.None))
					{
						re.size+=v.size;

						if(re.isOnlyConfig && !v.name.StartsWith(ShineGlobal.configDirPath))
						{
							re.isOnlyConfig=false;
						}
					}
				}
			});

			if(re.size==0)
			{
				re.isOnlyConfig=false;
			}
		}

		/** 预备下载 */
		public static int preDownLoad()
		{
			_needLoadSize=0;
			_loadQueue=new SQueue<ResourceSaveData>();

			_versionData.resourceDic.forEachValue(v=>
			{
				//未处理的且需要下载的部分
				if(v.state==ResourceSaveStateType.None && ResourceSaveType.needFirst(v.saveType))
				{
					_loadQueue.offer(v);
					_needLoadSize+=v.size;
				}
			});

			return _needLoadSize;
		}

		/** 执行下载更新资源 */
		public static void doDownload(Action overFunc)
		{
			_overCall=overFunc;

			for(int i=0;i<LoadNum;i++)
			{
				runLoadOne(i);
			}
		}

		/** 需要下载大小 */
		public static int getNeedLoadSize()
		{
			return _needLoadSize;
		}

		/** 获取当前下载大小 */
		public static int getCurrentLoadSize()
		{
			return _currentLoadSize;
		}

		private static void runLoadOne(int index)
		{
			if(_loadQueue.isEmpty())
			{
				_loadings[index]=null;
				_loaders[index].unload();

				//完毕
				if(_loadingNum==0)
				{
					downloadOver();
				}

				return;
			}

			_loadingNum++;
			_loaders[index].loadResource((_loadings[index]=_loadQueue.poll()).name);
		}

		private static void downloadOver()
		{
			//为了下次，重置为0
			_needLoadSize=0;
			_loadCompleteSize=0;
			_currentLoadSize=0;
			
			saveVersion();

			Action func=_overCall;

			if(func!=null)
				func();
		}
	}
}