using System;
using UnityEditor;
using UnityEngine;
using UnityEngine.Networking;
using Object = UnityEngine.Object;

namespace ShineEngine
{
	/** 加载器(版本控制+www完成) */
	public class ShineLoader
	{
		/** io出错等待间隔(ms) */
		private static int _ioErrorWaitTime=2000;

		/** 加载状态-空闲中 */
		private const int LoadState_Free=0;
		/** 加载状态-加载中 */
		private const int LoadState_Loading=1;
		/** 加载状态-已完成 */
		private const int LoadState_Complete=2;

		/** io错误-出错 */
		private const int IOError_Error=1;
		/** io错误-超时(字节无进度变化) */
		private const int IOError_ByteFrozen=2;
		/** io错误-尺寸不匹配(不完整) */
		private const int IOError_SizeNotMatch=3;

		//--self-//
		/** 绑定序号(如为-1,则自己处理onFrame) */
		private int _index=-1;
		/** www加载对象(必须每次新创建) */
		private WWW _www;
		/** webRequest */
		private UnityWebRequest _webRequest;
		/** AssetBundle加载请求 */
		private AssetBundleCreateRequest _assetBundleCreateRequest;
		/** 自己的加载时间序号 */
		private int _loadTimeIndex=-1;

		//--state--//

		/** 加载状态(0是未加载，1是正在加载，2是加载完成)  */
		private int _loadState=LoadState_Free;
		/** 读取方式 */
		private int _loadMethod=LoadMethodType.Resource;

		/** 路径 */
		private string _url;
		/** 实际使用路径 */
		private string _useURL;
		/** 资源类型 */
		private int _resourceType=ResourceType.Unknown;
		/** 预定尺寸(0:不检测) */
		private int _needSize=0;
		/** 当前保存数据 */
		private ResourceSaveData _currentSaveData;
		/** 是否下载好后，缓存到保存目录 */
		private bool _needCache;

		//--重试相关--//

		/** try等待时间 */
		private int _tryWaitTime=0;
		/** 是否一直尝试 */
		private bool _tryForever=false;


		//call
		/** 完成回调 */
		private Action _completeCall;

		/** 错误回调 */
		private Action _ioErrorCall;

		//count
		///** 开始加载时间 */
		//private int _loadStartTime;
		///** 加载完成时间 */
		//private int _loadCompleteTime;
		//private double _nowLoadSpeed;

		//reload
		/** 检测时间经过 */
		private int _checkTimePass=0;

		/** 上次字节读取数 */
		private int _lastBytesLoaded=0;

		/** 超时次数 */
		private int _timeOutTimes=0;

		/** 重新加载计数 */
		private int _reloadTimes=0;

		public ShineLoader()
		{

		}

		/** 来自Control的构造 */
		public ShineLoader(int index)
		{
			_index=index;
		}

		/// <summary>
		/// 完成回调
		/// </summary>
		public void setCompleteCall(Action func)
		{
			_completeCall=func;
		}

		/// <summary>
		/// io错误回调
		/// </summary>
		public void setIoErrorCall(Action func)
		{
			_ioErrorCall=func;
		}

		/** 每帧调用 */
		public void onFrame(int delay)
		{
			//不是正在加载
			if(_loadState!=LoadState_Loading)
				return;

			if(_tryWaitTime>0)
			{
				if((_tryWaitTime-=delay)<=0)
				{
					_tryWaitTime=0;

					toReload();
				}
			}
			else
			{
				if(_assetBundleCreateRequest!=null)
				{
					if(_assetBundleCreateRequest.isDone)
					{
						toComplete();

						return;
					}
				}
				else if(_www!=null)
				{
					if(_www.isDone)
					{
						//_loadCompleteTime = Ctrl.getTimer();

						if(!string.IsNullOrEmpty(_www.error))
						{
							Ctrl.print("io出错",_www.error,_url,_useURL);

							toIoError(IOError_Error);
							return;
						}
						else
						{
							//有记录
							if(_needSize>0 && ShineSetting.isRelease)
							{
								//不够
								if(_needSize!=_www.bytesDownloaded)
								{
									Ctrl.warnLog("加载不完整:",_url,_needSize,_www.bytesDownloaded);

									toIoError(IOError_SizeNotMatch);
									return;
								}
							}

							toComplete();
						}

						return;
					}
				}
				else
				{
					Ctrl.throwError("没有加载驱动主体");
					return;
				}

				if(_checkTimePass>0)
				{
					if((_checkTimePass-=delay)<=0)
					{
						_checkTimePass=0;
						timeOutCheck();
					}
				}
			}
		}

		/** 是否正在加载 */
		public bool isLoading()
		{
			return _loadState==LoadState_Loading;
		}

		/** 卸载(当前www) */
		public void unload()
		{
			if(_loadState==LoadState_Free)
				return;

			if(_www!=null)
			{
				_www.Dispose();
				_www=null;
			}

			if(_assetBundleCreateRequest!=null)
			{
				//析构
				_assetBundleCreateRequest=null;
			}

			toEndLoad();

			_loadState=LoadState_Free;

			resetTimeOut();
			clear();
		}

		/** 清除部分数据 */
		private void clear()
		{
			_needSize=0;
			_currentSaveData=null;
			_needCache=false;
		}

		/// <summary>
		/// 加载资源
		/// </summary>
		public void loadResource(string url)
		{
			doLoad(LoadMethodType.Resource,url,true);
		}

		/// <summary>
		/// 加载资源(只包中)
		/// </summary>
		public void loadStreamingAsset(string url)
		{
			doLoad(LoadMethodType.StreamingAssets,url,false);
		}

		/// <summary>
		/// 从其他网络读取
		/// </summary>
		public void loadFromNet(string url)
		{
			doLoad(LoadMethodType.Net,url,false);
		}

		public void loadFromNet(string url,bool tryForever)
		{
			doLoad(LoadMethodType.Net,url,tryForever);
		}
		
		public void loadFromOther(string url,bool tryForever)
		{
			doLoad(10,url,tryForever);
		}

		/** 执行加载 */
		private void doLoad(int loadMethod,string url,bool tryForever)
		{
			if(_loadState==LoadState_Loading)
				return;

			unload();

			_loadMethod=loadMethod;
			_url=url;
			_tryForever=tryForever;

			toLoad();
		}

		/** 执行加载 */
		private void toLoad()
		{
			_needSize=0;

			switch(_loadMethod)
			{
				case LoadMethodType.Resource:
				{
					if(ShineSetting.debugJumpResourceVersion || ShineSetting.localLoadWithOutBundle)
					{
						_needSize=0;
						_useURL=ResourceInfoControl.getStreamingAssetsPath(_url,true);
						_resourceType=ResourceType.Unknown;
					}
					else
					{
						if((_currentSaveData=ResourceInfoControl.getResourceSaveData(_url))==null)
						{
							Ctrl.throwError("不该找不到资源数据",_url);
							return;
						}

						_resourceType=_currentSaveData.getType();
						bool isBundle=_resourceType==ResourceType.Bundle;

						_needSize=_currentSaveData.size;

						//从steamingAsset中加载
						if(_currentSaveData.state==ResourceSaveStateType.StreamingAssetsReady)
						{
							_useURL=ResourceInfoControl.getStreamingAssetsPath(_url,!isBundle);
						}
						//从持久化中加载
						else if(_currentSaveData.state==ResourceSaveStateType.Downloaded)
						{
							_useURL=ResourceInfoControl.getPersistentPath(_url,!isBundle);
						}
						//需要加载
						else
						{
							_useURL=ResourceInfoControl.getCDNResourcePath(_url,_currentSaveData.version);
							_needCache=true;
						}
					}
				}
					break;
				case LoadMethodType.StreamingAssets:
				{
					_useURL=ResourceInfoControl.getStreamingAssetsPath(_url,true);
					_resourceType=ResourceType.Unknown;
				}
					break;
				default:
				{
					_useURL=_url;
					_resourceType=ResourceType.Unknown;
				}
					break;
			}

			_reloadTimes=0;
			//标记开始加载
			_loadState=LoadState_Loading;

			resetTimeOut();
			toDoLoad();
		}

		/** 执行加载 */
		private void toDoLoad()
		{
			if(_resourceType==ResourceType.Bundle && _currentSaveData.state!=ResourceSaveStateType.None)
			{
				_assetBundleCreateRequest=AssetBundle.LoadFromFileAsync(_useURL);
			}
			else
			{
				if(ShineSetting.useUnityWebRequestInsteadOfWWW)
				{
					//TODO:回头做完Bundle支持

					_webRequest=new UnityWebRequest(_useURL);

					// switch(_resourceType)
					// {
					// 	case ResourceType.Bundle:
					// 	{
							// _webRequest.downloadHandler=new DownloadHandlerAssetBundle();
					// 	}
					// }

					// UnityWebRequest aa=new UnityWebRequest(_useURL);
					// aa.downloadHandler=new DownloadHandlerBuffer();
				}
				else
				{
					_www=new WWW(_useURL);
				}
			}

			//检测时间
			_checkTimePass=ShineSetting.loaderTimeOutCheckDelay;

			if(_index==-1)
			{
				if(_loadTimeIndex!=-1)
				{
					TimeDriver.instance.clearFrame(_loadTimeIndex);
				}

				_loadTimeIndex=TimeDriver.instance.setFrame(onFrame);
			}
		}

		private void toEndLoad()
		{
			if(_index==-1 && _loadTimeIndex!=-1)
			{
				TimeDriver.instance.clearFrame(_loadTimeIndex);
				_loadTimeIndex=-1;
			}
		}

		/** 重新加载 */
		private void toReload()
		{
			resetTimeOut();

			++_reloadTimes;
			toDoLoad();
		}

		/// <summary>
		/// 超时检测
		/// </summary>
		private void timeOutCheck()
		{
			if(_loadState!=LoadState_Loading)
				return;

			if (_www == null)
				return;

			if(_lastBytesLoaded==_www.bytesDownloaded)
			{
				++_timeOutTimes;
			}
			else
			{
				_timeOutTimes=0;
			}

			_lastBytesLoaded=_www.bytesDownloaded;

			//3次
			if(_timeOutTimes>=3)
			{
				toIoError(IOError_ByteFrozen);
			}
		}

		/** 重置超时检测 */
		private void resetTimeOut()
		{
			_checkTimePass=0;
			_lastBytesLoaded=0;
			_timeOutTimes=0;
		}

		/** 缓存到持久化目录 */
		private void resourceCache()
		{
			if(ShineSetting.debugJumpResourceVersion)
				return;

			if(_www!=null)
			{
				//缓存
				FileUtils.writeFileForBytes(ResourceInfoControl.getPersistentPath(_url,false),_www.bytes);
				_currentSaveData.state=ResourceSaveStateType.Downloaded;
				ResourceInfoControl.versionModified();
			}
			else if(_webRequest!=null)
			{
				// _webRequest.
			}
			else
			{
				Ctrl.errorLog("ShineLoader,resourceCache时,没有可用对象");
			}
		}

		/** 完成 */
		private void toComplete()
		{
			if(_loadState==LoadState_Complete)
				return;

			toEndLoad();
			resetTimeOut();

			//完毕
			_loadState=LoadState_Complete;

			if(_needCache)
			{
				_needCache=false;
				resourceCache();
			}

			if(_completeCall!=null)
				_completeCall();
		}

		/** io出错(1:wwwError,2:超时,3:不完整) */
		private void toIoError(int type)
		{
			//把www停了
			if(_www!=null)
			{
				_www.Dispose();
				_www=null;
			}

			Ctrl.print("io出错:" + type,_url);

			//本地缓存资源被删除了
			if(_loadMethod==LoadMethodType.Resource && _currentSaveData!=null && _currentSaveData.state==ResourceSaveStateType.Downloaded)
			{
				//改成cdn下载
				_currentSaveData.state=ResourceSaveStateType.None;
				ResourceInfoControl.versionModified();
				_useURL=ResourceInfoControl.getCDNResourcePath(_url,_currentSaveData.version);
				_needCache=true;

				toReload();
			}
			else
			{
				if(_tryForever)
				{
					//等2秒
					_tryWaitTime=_ioErrorWaitTime;
				}
				else
				{
					unload();

					if(_ioErrorCall!=null)
						_ioErrorCall();
				}
			}
		}

		//get

		public AssetBundle getAssetBundle()
		{
			if(_assetBundleCreateRequest!=null)
				return _assetBundleCreateRequest.assetBundle;

			if(_www!=null)
				return _www.assetBundle;

			return null;
		}

		public byte[] getBytes()
		{
			if(_www!=null)
				return _www.bytes;

			return null;
		}

		/** 获取字节读取进度 */
		public int getByteLoaded()
		{
			if(_assetBundleCreateRequest!=null)
				return (int)(_assetBundleCreateRequest.progress*_needSize);

			if(_www!=null)
				return _www.bytesDownloaded;

			return 0;
		}

		/** 获取加载的xml对象 */
		public XML getXML()
		{
			return XML.readXMLByString(_www.text);
		}

		/** 获取加载的文本文件 */
		public string getText()
		{
			return _www.text;
		}

		/** 获取加载的二进制文件 */
		public BytesReadStream getBytesRead()
		{
			return new BytesReadStream(_www.bytes);
		}
	}
}