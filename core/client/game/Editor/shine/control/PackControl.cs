using System;
using System.Diagnostics;
using System.IO;
using System.Security.Cryptography;
using ShineEngine;
using ShineEditor;
using Spine.Unity;
using UnityEditor;
using UnityEngine;

namespace ShineEditor
{
	/// <summary>
	/// 打包控制
	/// </summary>
	public class PackControl
	{
		private static PackControl _instance;

		public static PackControl instance()
		{
			if(_instance==null)
				_instance=new PackControl();
			return _instance;
		}

		/** 构造目标类型 */
		private static SMap<BuildTarget,string> _targetMap=new SMap<BuildTarget,string>();

		/** 是否全部执行 */
		private static bool _isAll;
		/** 是否打包发布 */
		private bool _isReleasePack;
		/** 是否新app */
		private bool _isNewApp;
		/** 是否是必须更新的app */
		private bool _isAppNeedUpdate;
		/** 是否有必须更新的资源 */
		private bool _isResourceNeedUpdate;
		/** 显示版本号 */
		private Func<int,int,int,int,string> _versionFunc=null;

		private int _selectTargetIndex;
		/** 本次平台名 */
		private string _targetName;

		private BuildTarget _selectTarget;

		//配置部分
		private BuildConfig[] _buildConfigs;
		/** 配置文件修改时间 */
		private long _buildConfigFileTime;

		/** 忽略扩展名 */
		private static string[] _ignoreEx={"meta","svn","git","cs","DS_Store"};

		//info部分
		/** 待打包bundle数据的字典 */
		private SMap<string,BundleInfoData> _bundleInfoDataList;
		/** 待打包bundle扩展数据字典 */
		private SMap<string,BundleInfoExData> _bundleInfoExDataList;
		/** 资源数据列表 */
		private SMap<int,ResourceInfoData> _resourceInfoDataList;
		/** 资源扩展数据列表 */
		private SMap<int,ResourceInfoExData> _resourceInfoExDataList;
		/** 资源扩展数据根据名字缓存列表 */
		private SMap<string,ResourceInfoExData> _resourceInfoExDataCache;

		/** 资源序号 */
		private int _resourceIndex=0;

		//version相关
		// /** 上次公共版本记录 */
		// private VersionRecordData _lastVersion;
		/** 上次目标平台版本 */
		private VersionSaveExData _lastTargetVersion;

		//配置
		/** 默认存放方式 */
		private int _defaultSaveType=ResourceSaveType.InStreamingAsset;
		/** 标记的资源存储方式 */
		private SMap<string,int> _signedResourceSaveDic=new SMap<string,int>();

		private VersionSaveExData _newVersion;

		/** manifest */
		private AssetBundleManifest _manifest;

		public PackControl()
		{
			init();
		}

		public SMap<BuildTarget,string> getTargetMap()
		{
			return _targetMap;
		}

		private void init()
		{
			_targetMap.put(BuildTarget.StandaloneWindows,"windows");
			_targetMap.put(BuildTarget.Android,"android");
			_targetMap.put(BuildTarget.iOS,"ios");
			_targetMap.put(BuildTarget.StandaloneOSX,"mac");


		}

		/** 获取目标平台路径前缀 */
		private string getTargetSourcePath()
		{
			return ShineToolGlobal.outSourcePath + "/" + _targetName;
		}

		/** 获取目标平台保存路径前缀 */
		private string getTargetSavePath()
		{
			return ShineToolGlobal.clientSavePath + "/" + _targetName;
		}

		/** 获取目标平台路径前缀 */
		private string getTargetCDNSourcePath()
		{
			return ShineToolGlobal.cdnSourcePath + "/" + _targetName;
		}

		/** 默认打包 */
		public void packNormal()
		{
			pack(EditorUserBuildSettings.activeBuildTarget,false,false,false,false,false,null);
		}

		/** 执行方法 */
		public void pack(BuildTarget target,bool isAll,bool isReleasePack,bool isNewApp,bool isAppNeedUpdate,bool isResourceNeedUpdate,Func<int,int,int,int,string> versionFunc)
		{
			_selectTarget=target;
			_targetName=_targetMap.get(target);

			_isAll=isAll;
			_isReleasePack=isReleasePack;
			_isNewApp=isNewApp;
			_isAppNeedUpdate=isAppNeedUpdate;
			_isResourceNeedUpdate=isResourceNeedUpdate;
			_versionFunc=versionFunc;

			doPack();
		}

		/** 执行打包打包 */
		private void doPack()
		{
			//抽离UI预制
			EditorUIControl.make(_isAll);

			//选择激活BuildTarget
			makeActiveTarget();

			if(_isAll)
			{
				clearAllSign();
			}

			initData();  //初始化数据

			readConfig(); //读取打包配置

			preExecute(); //执行打包预处理

			executeConfigs(); //根据配置生成unity打包信息

			createDepends(); //生成依赖信息数据

			doBuild(); //执行打包

			copyFilesAndInfo(); //生成bundle信息文件，复制bundle文件到source目录

			release();

			Ctrl.print("OK");
		}

		private void makeActiveTarget()
		{
			//不同
			if(_selectTarget!=EditorUserBuildSettings.activeBuildTarget)
			{
				EditorUserBuildSettings.SwitchActiveBuildTarget(_selectTarget);
			}
		}

		public void justBuild()
		{
			_selectTarget=EditorUserBuildSettings.activeBuildTarget;
			doBuild();
		}

		private void doBuild()
		{
			Ctrl.print("doBuild");

			string path=ShineToolGlobal.bundleTempPath + "/" + _targetName;

			if(!Directory.Exists(path))
				Directory.CreateDirectory(path);

			if(_isAll)
				FileUtils.clearDir(path);

			long time=Ctrl.getTimer();
			BuildPipeline.BuildAssetBundles(path, BuildAssetBundleOptions.ChunkBasedCompression|BuildAssetBundleOptions.DeterministicAssetBundle, _selectTarget);
			time=Ctrl.getTimer() - time;

			Ctrl.print("打包用时：" + time);
		}

		/** 生成打包信息文件并复制打包文件到发布目录 */
		private void copyFilesAndInfo()
		{
			//复制bundle文件
			string buildPath=ShineToolGlobal.bundleTempPath + "/" + _targetName;
			string releasePath=getTargetSourcePath()+"/" + ShineToolGlobal.bundleDirName;
			if (!Directory.Exists(releasePath))
				Directory.CreateDirectory(releasePath);

			FileUtils.clearDir(releasePath);

			SList<string> delBundleList=new SList<string>();
			string[] files=Directory.GetFiles(buildPath,"*.bundle");
			for(int i=0;i<files.Length;i++)
			{
				string fromFileName=files[i];	
				string fileName=Path.GetFileName(fromFileName);

				//如果是无效bundle
				if(!_bundleInfoDataList.contains(fileName))
				{
					delBundleList.add(fromFileName);
					delBundleList.add(fromFileName + ".manifest");
					continue;
				}

				string toFileName=Path.Combine(releasePath,fileName);
				File.Copy(fromFileName, toFileName);
			}

			//删除无效bundle以及对应manifest
			foreach(string filePath in delBundleList)
			{
				Ctrl.print("删除无效文件",filePath);
				File.Delete(filePath);
			}

			//生成打包信息文件
			string infoPath=getTargetSourcePath()+"/" + ShineGlobal.bundleInfoPath;
			BytesWriteStream buffer=new BytesWriteStream();
			buffer.writeVersion(ShineGlobal.bundleInfoVersion);

			buffer.writeInt(_bundleInfoDataList.length());
			foreach(BundleInfoData info in _bundleInfoDataList)
			{
				info.writeBytes(buffer);
			}

			buffer.writeInt(_resourceInfoDataList.length());
			foreach(ResourceInfoData info in _resourceInfoDataList)
			{
				info.writeBytes(buffer);
			}

			FileUtils.writeFileForBytes(infoPath,buffer);
		}

		private void readConfig()
		{
			// EditorUtility.DisplayProgressBar("请耐心等待","正在读取配置文件...",0.0f);

			long configTime=File.GetLastWriteTime(ShineToolGlobal.buildConfigPath).Ticks;
			if(_buildConfigs==null || _buildConfigFileTime!=configTime)
			{
				_buildConfigFileTime=configTime;
				string[][] re=ToolFileUtils.readFileForExcelFirstSheet(ShineToolGlobal.buildConfigPath);

				_buildConfigs=new BuildConfig[re.Length-1];

				for(int i=1;i<re.Length;i++)
				{
					BuildConfig config=new BuildConfig();
					config.readConfig(re[i]);

					_buildConfigs[i - 1]=config;
				}
			}

			EditorUtility.ClearProgressBar();
		}

		/** 初始化数据信息 */
		private void initData()
		{
			_bundleInfoDataList=new SMap<string,BundleInfoData>();
			_bundleInfoExDataList=new SMap<string,BundleInfoExData>();
			_resourceInfoDataList=new SMap<int,ResourceInfoData>();
			_resourceInfoExDataList=new SMap<int,ResourceInfoExData>();
			_resourceInfoExDataCache=new SMap<string,ResourceInfoExData>();
			_resourceIndex=1;
		}

		/** 执行打包预处理 */
		private void preExecute()
		{

		}

		/** 执行打包配置 */
		private void executeConfigs()
		{
			EditorUtility.DisplayProgressBar("请耐心等待","正在生成bundle信息...",0.0f);

			int progressNum=_buildConfigs.Length + 1;
			int curNum=0;
			foreach(BuildConfig config in _buildConfigs)
			{
				executeOneConfig(config);
				++curNum;
				EditorUtility.DisplayProgressBar("请耐心等待","正在生成bundle信息...",(float)curNum / progressNum);
			}

			EditorUtility.ClearProgressBar();
		}

		/** 执行单个配置 */
		private void executeOneConfig(BuildConfig config)
		{
			string path=ShineToolGlobal.assetsPath +"/" +config.path;

			if(!Directory.Exists(path))
				return;

			switch(config.packType)
			{
				case PackType.One:
				{
					doDeepOne(path,config.front + ShineToolGlobal.bundleFileExName,config.exNames);
				}
					break;
				case PackType.Single:
				{
					SList<string> list;

					if(config.exNames!=null)
					{
						list=FileUtils.getFileMutipleList(path,config.exNames);
					}
					else
					{
						list=FileUtils.getFileListForIgnore(path,_ignoreEx);
					}

					foreach(string s in list)
					{
						if(s.StartsWith("."))
							continue;

						string bundleName=config.front + FileUtils.getFileNameWithoutEx(s) + ShineToolGlobal.bundleFileExName;
						bundleName=bundleName.ToLower();//小写名字
						createBundleData(bundleName);
						doSingleOne(s,bundleName);
						_bundleInfoDataList[bundleName].assets=_bundleInfoExDataList[bundleName].assets.toArray();
					}
				}
					break;
				case PackType.Directory:
				{
					string[] dics=Directory.GetDirectories(path);

					foreach(string dicPath in dics)
					{
						String tt=FileUtils.fixPath(dicPath);

						String fName=tt.Substring(tt.LastIndexOf('/') + 1);

						doDeepOne(dicPath,config.front + fName + ShineToolGlobal.bundleFileExName,config.exNames);
					}
				}
					break;
			}
		}

		private void checkBundleRepeat(string name)
		{
			if(_bundleInfoDataList.contains(name))
			{
				Ctrl.throwError("bundle名重复",name);
			}
		}

		private void doDeepOne(string path,string bundleName,string[] exNames)
		{
			//小写名字
			bundleName=bundleName.ToLower();

			createBundleData(bundleName);

			SList<string> list;

			if(exNames!=null)
			{
				list=FileUtils.getDeepFileMutipleList(path,exNames);
			}
			else
			{
				list=FileUtils.getDeepFileListForIgnore(path,_ignoreEx);
			}

			foreach(string s in list)
			{
				doSingleOne(s,bundleName);
			}

			_bundleInfoDataList[bundleName].assets=_bundleInfoExDataList[bundleName].assets.toArray();
		}

		/** 执行单个 */
		private void doSingleOne(string path,string bundleName)
		{
			if(path.StartsWith(ShineToolGlobal.gamePath))
			{
				String tt=path.Substring(ShineToolGlobal.gamePath.Length + 1);

				//使用资源名
				String useName=path.Substring(ShineToolGlobal.assetSourcePath.Length + 1);

				//取资源信息，并生产index
				int id=getResourceId();
				_resourceInfoDataList[id]=new ResourceInfoData {id=id,name=useName,type=0};
				ResourceInfoExData resourceInfoExData=new ResourceInfoExData {id=id,name=tt};
				_resourceInfoExDataList[id]=resourceInfoExData;
				_resourceInfoExDataCache[resourceInfoExData.name]=resourceInfoExData;
				_bundleInfoExDataList[bundleName].assets.add(id);
				if(Path.GetExtension(path)==".unity")
				{
					_resourceInfoDataList[_bundleInfoDataList[bundleName].id].type=ResourceType.Scene;
				}

				AssetImporter importer = AssetImporter.GetAtPath(tt);
				if(importer==null)
				{
					Ctrl.throwError("没有importer",tt);
				}
				else
				{
					importer.SetAssetBundleNameAndVariant(bundleName,"");
				}
			}
			else
			{
				Ctrl.throwError("不该出现的情况",path);
			}
		}

		/** 清除所有bundle信息 */
		private void clearAllSign()
		{
			EditorUtility.DisplayProgressBar("请耐心等待","正在清除旧bundle信息...",0.0f);

			string[] allAssetBundleNames = AssetDatabase.GetAllAssetBundleNames();
			int progressNum=allAssetBundleNames.Length + 1;
			int curNum=0;
			foreach (var assetBundleName in allAssetBundleNames)
			{
				AssetDatabase.RemoveAssetBundleName(assetBundleName, true);
				++curNum;
				EditorUtility.DisplayProgressBar("请耐心等待","正在清除旧bundle信息...",(float)curNum / progressNum);
			}

			AssetDatabase.RemoveUnusedAssetBundleNames();

			AssetDatabase.SaveAssets();
			AssetDatabase.Refresh();

			EditorUtility.ClearProgressBar();
		}

		/** 生成依赖 */
		private void createDepends()
		{
			EditorUtility.DisplayProgressBar("请耐心等待","正在检测bundle依赖...",0.0f);

			int progressNum=_bundleInfoExDataList.length();
			int curNum=0;
			
			foreach(BundleInfoExData bundleInfoExData in _bundleInfoExDataList)
			{
				string[] dependencies=AssetDatabase.GetAssetBundleDependencies(bundleInfoExData.name,true);
				string[] depends=new string[dependencies.Length];
				
				for(int i=0;i<dependencies.Length;i++)
				{
					string name=dependencies[i];
					bundleInfoExData.depends.add(_resourceInfoExDataCache[name].id);
				}
				
				EditorUtility.DisplayProgressBar("请耐心等待","正在检测bundle依赖...",(float)++curNum / progressNum);

				_bundleInfoDataList[bundleInfoExData.name].depends=bundleInfoExData.depends.toArray();
			}

			EditorUtility.ClearProgressBar();

			EditorUtility.DisplayProgressBar("请耐心等待","正在检测bundle循环依赖...",0.0f);
			
			chechResourceLoopDepend();
		}

		//检测循环依赖
		private void chechResourceLoopDepend()
		{
			int progressNum=_bundleInfoExDataList.length();
			int curNum=0;
			
			foreach(BundleInfoExData bundleInfoExData in _bundleInfoExDataList)
			{
				doChechResourceLoopDepend(bundleInfoExData.name,bundleInfoExData.name);
				EditorUtility.DisplayProgressBar("请耐心等待","正在检测bundle循环依赖...",(float)++curNum / progressNum);
			}
		}

		private void doChechResourceLoopDepend(string path,string bundleName)
		{
			if(!ShineSetting.openBundleCheckLoop)
				return;
			
			string[] dependencies=AssetDatabase.GetAssetBundleDependencies(bundleName,true);

			for(int i=0;i<dependencies.Length;i++)
			{
				string localName = dependencies[i];
				
				if(path.IndexOf(localName)!=-1)
				{
					path += "-->" + localName;
					//检测到循环
					Ctrl.errorLog("检测到循环依赖",path);
					return;
				}
				
				path += "-->" + localName;
			
				doChechResourceLoopDepend(path,localName);
			}
		}
		
		/** 创建一个bundle数据信息 */
		private void createBundleData(string bundleName)
		{
			int id=getResourceId();
			checkBundleRepeat(bundleName);
			_bundleInfoDataList[bundleName]=new BundleInfoData {id=id};
			_bundleInfoExDataList[bundleName]=new BundleInfoExData {id=id,name=bundleName};
			_resourceInfoDataList[id]=new ResourceInfoData {id=id,name="bundle/" + bundleName,type=ResourceType.Bundle};
			ResourceInfoExData resourceInfoExData=new ResourceInfoExData {id=id,name=bundleName};
			_resourceInfoExDataList[id]=resourceInfoExData;
			_resourceInfoExDataCache[resourceInfoExData.name]=resourceInfoExData;
		}

		/** 获取一个资源id */
		private int getResourceId()
		{
			return _resourceIndex++;
		}

		/** 打包配置 */
		private class BuildConfig
		{
			/** 路径 */
			public string path;
			/** 前缀 */
			public string front;
			/** 打包方式 */
			public int packType;

			public string[] exNames;

			public void readConfig(string[] arr)
			{
				path=arr[0];
				front=arr[1];
				packType=Convert.ToInt32(arr[2]);

				//3是说明
				if(arr.Length>4 && arr[4].Length>0)
				{
					exNames=arr[4].Split(',');

					if(exNames.Length==0)
						exNames=null;
				}
				else
				{
					exNames=null;
				}
			}
		}

		//--发布--//

		/** 执行发布 */
		public void release()
		{
			readLastReleaseInfo();

			readLastBundleInfo();

			readResourceInfoConfig();

			copyFilesBeforeDoVersion();

			doVersion();

			writeVersion();

			copyFilesForRelease();

			writeVersionToServer();

			Ctrl.print("ReleaseOK,appVersion:"+_newVersion.appVersion,"resourceVersion:"+_newVersion.resourceVersion);
		}

		/** 读取上次发布信息 */
		private void readLastReleaseInfo()
		{
			string targetVersionPath=getTargetSavePath()+"/" + ShineGlobal.versionInfoPath;

			_lastTargetVersion=new VersionSaveExData();

			BytesReadStream stream=FileUtils.readFileForBytesReadStream(targetVersionPath);

			//存在
			if(stream!=null && stream.checkVersion(ShineGlobal.versionInfoVersion))
			{
				_lastTargetVersion.readBytes(stream);
			}
			else
			{
				_isNewApp=true;
				_isAppNeedUpdate=true;
				_lastTargetVersion.version="";
				_lastTargetVersion.appVersion=0;
				_lastTargetVersion.resourceVersion=0;
				_lastTargetVersion.leastResourceVersion=1;//最小1版
			}
		}

		/** 读取bundleInfo */
		private void readLastBundleInfo()
		{
			string bundlePath=getTargetSourcePath()+"/" + ShineGlobal.bundleInfoPath;

			//存在
			if(!File.Exists(bundlePath))
			{
				Ctrl.throwError("请先执行打包!");
			}
			else
			{
				BytesReadStream stream=FileUtils.readFileForBytesReadStream(bundlePath);

				if(!stream.checkVersion(ShineGlobal.bundleInfoVersion))
				{
					Ctrl.throwError("请先执行打包!");
					return;
				}

				ResourceInfoControl.readBundleInfo(stream);
			}
		}

		/** 读取bundle配置 */
		private void readResourceInfoConfig()
		{
			string[][] re=ToolFileUtils.readFileForExcelFirstSheet(ShineToolGlobal.resourceInfoPath);

			_defaultSaveType=int.Parse(re[0][1]);
			_signedResourceSaveDic=new SMap<string,int>();

			for(int i=2;i<re.Length;i++)
			{
				_signedResourceSaveDic.put(re[i][0],int.Parse(re[i][1]));
			}
		}

		private void copyFilesBeforeDoVersion()
		{
			string dllPath=FileUtils.fixAndFullPath(ShineToolGlobal.gamePath + "/../hotfix/Temp/bin/Release/hotfix.dll");

			//存在
			if(FileUtils.fileExists(dllPath))
			{
				FileUtils.copyFile(dllPath,ShineToolGlobal.sourceCommonPath+"/hotfix.dll");
			}
		}

		private void doVersion()
		{
			AssetBundle ab = AssetBundle.LoadFromFile(ShineToolGlobal.bundleTempPath + "/" + _targetName+"/"+_targetName);
			_manifest = ab.LoadAsset<AssetBundleManifest>("AssetBundleManifest");
			
			_newVersion=new VersionSaveExData();

			_newVersion.appVersion=_isNewApp ? _lastTargetVersion.appVersion+1 : _lastTargetVersion.appVersion;
			_newVersion.leastAppVersion=_isAppNeedUpdate ? _newVersion.appVersion : _lastTargetVersion.appVersion;
			_newVersion.resourceVersion=_lastTargetVersion.resourceVersion+1;
			_newVersion.leastResourceVersion=_isResourceNeedUpdate ? _newVersion.resourceVersion : _lastTargetVersion.leastResourceVersion;
			
			if (_versionFunc == null)
			{
				_newVersion.version = EditorPrefs.GetString("AssetBundleWindow_version");
			}
			else
			{
				//版本显示
				_newVersion.version=_versionFunc.Invoke(_newVersion.appVersion,_newVersion.leastAppVersion,_newVersion.resourceVersion,_newVersion.leastResourceVersion);
			}
			
			_newVersion.isRelease=_isReleasePack;

			//bundle部分
			foreach(BundleInfoData v in LoadControl.getBundleInfoDic())
			{
				string name=LoadControl.getResourceNameByID(v.id);

				int type=_signedResourceSaveDic.get(name);

				if(type<=0)
					type=_defaultSaveType;

				doBundleSaveOne(name,type);
			}

			//common部分
			SList<string> list=FileUtils.getDeepFileListForIgnore(ShineToolGlobal.sourceCommonPath,_ignoreEx);

			foreach(string fName in list) 
			{
				String name=fName.Substring(ShineToolGlobal.sourceCommonPath.Length + 1);

				int type=_signedResourceSaveDic.get(name);

				if(type<=0)
					type=_defaultSaveType;

				if(type!=ResourceSaveType.OnlyStreamingAsset)
				{
					doFileSaveData(ShineToolGlobal.sourceCommonPath,name,type);
				}
			}

			//bundleInfo.bin
			doFileSaveData(getTargetSourcePath(),ShineGlobal.bundleInfoPath,ResourceSaveType.InStreamingAsset);

			//其余额外部分
		}

		private void doBundleSaveOne(string bundleName,int type)
		{
			ResourceSaveExData data=_newVersion.getBundleEx(bundleName);

			if(data!=null)
			{
				if(ShineToolSetting.bundlePackNeedPutDependAlsoIntoStreamingAssets && type==ResourceSaveType.InStreamingAsset && data.saveType!=type)
				{
					data.saveType=type;
				}
			}
			else
			{
				data=doFileSaveData(getTargetSourcePath(),bundleName,type);

				if(ShineToolSetting.bundlePackNeedPutDependAlsoIntoStreamingAssets)
				{
					int resourceID=LoadControl.getResourceIDByName(data.name);

					BundleInfoData bundleInfoData=LoadControl.getBundleInfo(resourceID);

					foreach(int d in bundleInfoData.depends)
					{
						string pName=LoadControl.getResourceNameByID(d);

						//父类型
						doBundleSaveOne(pName,type);
					}
				}
			}
		}

		private ResourceSaveExData doFileSaveData(string front,string fileName,int type)
		{
			//本地强制进包
			if(!_isReleasePack)
				type=ResourceSaveType.InStreamingAsset;

			string path=front + "/" + fileName;

			string md5=FileUtils.getFileMD5(path);
//			string md5=_manifest.GetAssetBundleHash(FileUtils.getFileName(fileName)).ToString();

			FileInfo fileInfo=new FileInfo(path);

			ResourceSaveExData lastData=_lastTargetVersion.getBundleEx(fileName);

			ResourceSaveExData data=new ResourceSaveExData();
			data.name=fileName;
			data.size=fileInfo.Exists ? (int)fileInfo.Length : 0; //强转int，最大支持2G文件
			data.saveType=type;
			data.md5=md5;

			if(lastData!=null)
			{
				if(lastData.md5.Equals(md5))
				{
					data.version=lastData.version;
				}
				else
				{
					data.version=_newVersion.resourceVersion;
				}

				//进包变成不进包
				if(ResourceSaveType.isStreamingAsset(lastData.saveType) && !ResourceSaveType.isStreamingAsset(data.saveType))
				{
					//更新资源版本
					data.version=_newVersion.resourceVersion;
				}
			}
			else
			{
				data.version=_newVersion.resourceVersion;
			}

			_newVersion.resourceDic.put(data.name,data);

			return data;
		}

		/** 写出版本 */
		private void writeVersion()
		{
			VersionSaveData versionSaveData=_newVersion.createOriginalData();
			BytesWriteStream stream=new BytesWriteStream();
			stream.writeVersion(ShineGlobal.versionInfoVersion);
			versionSaveData.writeBytes(stream);
			FileUtils.writeFileForBytesWriteStream(getTargetSourcePath()+"/"+ShineGlobal.versionInfoPath,stream);

			// XML vXml=new XML();
			// vXml.name="info";
			// vXml.setProperty("version",ShineToolSetting.bundlePackVersion);
			// FileUtils.writeFileForXML(ShineToolGlobal.clientBundleRecordPath,vXml);

			//发布包
			if(_isReleasePack)
			{
				EditorPrefs.SetString("AssetBundleWindow_version",_newVersion.version);

				stream.clear();
				stream.writeVersion(ShineGlobal.versionInfoVersion);
				_newVersion.writeBytes(stream);

				FileUtils.writeFileForBytesWriteStream(getTargetSavePath()+"/" + ShineGlobal.versionInfoPath,stream);

				VersionRecordData recordData=_newVersion.createRecordData();
				XML xml=recordData.writeXML();
				FileUtils.writeFileForXML(getTargetSavePath()+"/"+"versionRecord.xml",xml);

				Ctrl.print("已覆盖原记录,当前版本为:",_newVersion.version);
			}
		}

		/** 发布前的各种拷贝文件 */
		private void copyFilesForRelease()
		{
			//streamingAssets部分

			if(!Directory.Exists(ShineToolGlobal.streamingAssetsPath))
				Directory.CreateDirectory(ShineToolGlobal.streamingAssetsPath);

			//清空目标路径
			FileUtils.clearDir(ShineToolGlobal.streamingAssetsPath);

			//发布包
			if(_isReleasePack)
				FileUtils.clearDir(getTargetCDNSourcePath()+"/resource_"+_newVersion.resourceVersion);

			//common部分
			SList<string> list=FileUtils.getDeepFileListForIgnore(ShineToolGlobal.sourceCommonPath,_ignoreEx);

			foreach(string fName in list)
			{
				//跳过configEditor
				if(!fName.EndsWith(ShineGlobal.configForEditorPath))
				{
					doCopyOne(ShineToolGlobal.sourceCommonPath,fName);
				}
			}

			string targetPath=getTargetSourcePath();

			//target部分
			list=FileUtils.getDeepFileListForIgnore(targetPath,_ignoreEx);

			foreach(string fName in list)
			{
				doCopyOne(targetPath,fName);
			}
		}

		private void doCopyOne(string targetPath,string fileName)
		{
			string tt=fileName.Substring(targetPath.Length + 1);

			ResourceSaveExData data=_newVersion.getBundleEx(tt);

			//data==null的时候,比如version.bin的情况

			//复制到streamingAssets
			if(data==null || data.saveType==ResourceSaveType.InStreamingAsset)
			{
				FileUtils.copyFile(fileName,ShineToolGlobal.streamingAssetsPath+"/"+tt);
			}

			bool needCopyToCDN=false;

			//发布版本
			if(_isReleasePack)
			{
				//没有数据 或 (是最新版本,并且不是 全更app中的包资源)
				if(data==null || (data.version==_newVersion.resourceVersion && !(_isAppNeedUpdate && ResourceSaveType.isStreamingAsset(data.saveType))))
				{
					needCopyToCDN=true;
				}
			}

			if(needCopyToCDN)
			{
				FileUtils.copyFile(fileName,getTargetCDNSourcePath()+"/resource_"+_newVersion.resourceVersion+"/"+tt);
			}
		}

		/** 写客户端版本到服务器配置 */
		private void writeVersionToServer()
		{
			XML xml=FileUtils.readFileForXML(ShineToolGlobal.serverVersionPath);

			if(xml==null)
			{
				xml=new XML();
				xml.name="version";
			}

			XML tt=xml.getChildrenByNameAndPropertyOne("platform","type",_targetName);

			if(tt==null)
			{
				tt=new XML();
				tt.name="platform";
				tt.setProperty("type",_targetName);
				xml.appendChild(tt);
			}

			tt.setProperty("currentAppVersion",_newVersion.appVersion.ToString());
			tt.setProperty("leastAppVersion",_newVersion.leastAppVersion.ToString());
			tt.setProperty("currentResourceVersion",_newVersion.resourceVersion.ToString());
			tt.setProperty("leastResourceVersion",_newVersion.leastResourceVersion.ToString());
			tt.setProperty("version",_newVersion.version);

			FileUtils.writeFileForXML(ShineToolGlobal.serverVersionPath,xml);
		}

		//build

		public void buildNormal()
		{
			build(EditorUserBuildSettings.activeBuildTarget);
		}

		/** 执行方法 */
		public void build(BuildTarget target)
		{
			_selectTarget=target;
			_targetName=_targetMap.get(target);

			SList<EditorBuildSettingsScene> sceneList=new SList<EditorBuildSettingsScene>();

			EditorBuildSettingsScene[] scenes=EditorBuildSettings.scenes;

			foreach(EditorBuildSettingsScene scene in scenes)
			{
				if(isSceneNeedPack(scene.path))
				{
					sceneList.add(scene);
				}
			}

			string path="../release/" + _targetName + "/" + "game";

			BuildPipeline.BuildPlayer(sceneList.toArray(),path,target,BuildOptions.None);

			Ctrl.print("OK");
		}

		protected bool isSceneNeedPack(string name)
		{
			return !name.StartsWith(ShineGlobal.sourceHeadU + "scene/");
		}


		public static void buildOneKey()
		{
			PackControl pc=instance();
			pc.packNormal();
			pc.buildNormal();
		}
	}
}