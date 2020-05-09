using System;
using System.IO;
using System.Reflection;
using ShineEngine;
using UnityEngine;

/// <summary>
/// 主工程登录控制
/// </summary>
public class MainLoginControl
{
	/** 显示logo过程(同时给与ios用户设置网络连接方式(wifi and 4G),以及各种权限，通知) */
	protected const int ShowLogo=1;
	/** 读取设置文件 */
	protected const int LoadSetting=2;
	/** 选择服务器(调试才有) */
	protected const int SelectServer=3;
	/** 显示loading界面 */
	protected const int ShowLoading=4;
	/** 连接服务器获取版本信息 */
	protected const int GetVersion=5;
	/** 资源更新过程 */
	protected const int UpdateResource=6;
	/** 加载并进入game */
	protected const int LoadGame=7;

	//boardType
	/** 没有网络连接 */
	protected const int Board_NoNet=1;
	//alertType
	/** 需要更新app */
	protected const int Alert_GetNewApp=1;
	/** 需要更新app */
	protected const int Alert_GetNewAppMust=2;
	/** 资源必须更新 */
	protected const int Alert_GetNewResourceMust=3;
	/** 资源可选更新 */
	protected const int Alert_GetNewResource=4;

	//noticeType
	/** 获取版本号失败 */
	protected const int Notice_GetVersionFailed=1;

	/** 流程插件 */
	protected StepTool _stepTool=new StepTool();

	/** 版本数据(服务器端) */
	protected ClientVersionData _versionData;

	private ShineLoader _loader;
	private ILRuntime.Runtime.Enviorment.AppDomain _app;

	private bool _noNetAlertShowed=false;

	protected bool _getVersionSended=false;

	//args
	protected string _downloadSize="";

	/** 获取版本信息回复数据 */
	protected VersionReData _versionRe=new VersionReData();

	private Func<int,string,bool> _gameSendLogFunc;


	//initUIs


	/** 初始化 */
	public void init()
	{
		TimeDriver.instance.setInterval(onSecond,1000);

		ResourceInfoControl.setIsCommonResourceFunc(isResourceNeedPlatform);

		initStep();
	}

	/** 资源是否需要平台前缀 */
	protected virtual bool isResourceNeedPlatform(string url)
	{
		//包含了config.bin
		if(url.StartsWith(ShineGlobal.configDirPath)
		   || url.Equals(ShineGlobal.configForEditorPath)
		   || url.Equals(ShineGlobal.hotfixDllPath)
		   || url.Equals(ShineGlobal.settingPath)
		   || url.Equals(ShineGlobal.serverListPath))
			return true;

		return false;
	}

	/** 设置game发送log方法 */
	public void setGameSendLogFunc(Func<int,string,bool> func)
	{
		_gameSendLogFunc=func;
	}

	/** 开始 */
	public void start()
	{
		Ctrl.print("开始流程",SystemControl.platform);

		_stepTool.checkStep();
	}

	protected virtual void initStep()
	{
		_stepTool.addStep(ShowLogo,stepShowLogo);
		_stepTool.addStep(LoadSetting,stepLoadSetting);
		_stepTool.addStep(SelectServer,stepSelectServer,ShowLogo,LoadSetting);
		_stepTool.addNext(ShowLoading,stepShowLoading);
		_stepTool.addNext(GetVersion,stepGetVersion);

		_stepTool.addNext(UpdateResource,stepLoadResource);
		_stepTool.addNext(LoadGame,stepLoadGame);
	}

	protected virtual void onSecond(int delay)
	{

	}

	//steps

	protected virtual void stepShowLogo()
	{
		//显示logo
		GameObject logoObj;
		if((logoObj=GameObject.Find(CommonSetting.uiLogoName))!=null)
		{
			logoObj.SetActive(true);
		}

		TimeDriver.instance.setTimeOut(()=>{ _stepTool.completeStep(ShowLogo); },100);
	}

	/** 加载设置 */
	protected virtual void stepLoadSetting()
	{
		LocalSetting.load(()=>
		{
			//绑定发送方法
			Ctrl.setSendLogFunc(toSendLog);

			_stepTool.completeStep(LoadSetting);
		});
	}

	/** 选择服务器 */
	protected virtual void stepSelectServer()
	{
		if(ShineSetting.isOfficial || CommonSetting.isSingleGame)
		{
			selectServerOver();
			return;
		}

		LocalSetting.loadServerList(()=>
		{
			GameC.nativeUI.getSelectServerUI().initAndShow();
		});
	}

	/** 选择服务器完毕 */
	public void selectServerOver()
	{
		_stepTool.completeStep(SelectServer);
	}

	/** 显示加载界面 */
	protected virtual void stepShowLoading()
	{
		_stepTool.completeStep(ShowLoading);
	}

	/** 获取客户端版本号 */
	protected virtual void stepGetVersion()
	{
		if(ShineSetting.isWholeClient)
		{
			//读取本地版本
			ResourceInfoControl.loadVersion(()=>{ preGetVersion(); });
		}
		else
		{
			preGetVersion();
		}
	}

	protected virtual void preGetVersion()
	{
		SystemControl.netChangeFunc+=onNetChanged;

		//有网络 或 调试模式
		if(SystemControl.isNetOpen())
		{
			doGetVersion();
		}
		else
		{
			noNetHandler();
		}
	}

	protected virtual void stepLoadResource()
	{
		if(ShineSetting.debugJumpResourceVersion || ShineSetting.localLoadWithOutBundle)
		{
			versionUpdateOver();
			return;
		}

		//本地版本数据
		VersionSaveData localVersionData=ResourceInfoControl.getVersion();

		//离线游戏 或 服务器无此版本
		if(_versionData==null)
		{
			Ctrl.log("离线游戏 或 服务器无此版本");

			_versionData=new ClientVersionData();
			_versionData.version=localVersionData.version;
			_versionData.currentAppVersion=localVersionData.appVersion;
			// _versionData.leastAppVersion=localVersionData.currentAppVersion;
			_versionData.currentResourceVersion=localVersionData.resourceVersion;
			// _versionData.leastResourceVersion=localVersionData.leastResourceVersion;

			doVersionNext(true);
			return;
		}

		//最低app版本不足
		if(localVersionData.appVersion<_versionData.leastAppVersion && ShineSetting.isRelease)
		{
			showAlert(Alert_GetNewAppMust,toGetNewApp,exitGame);
			return;
		}

		//有新的app
		if(localVersionData.appVersion<_versionData.currentAppVersion && ShineSetting.isRelease)
		{
			showAlert(Alert_GetNewApp,toGetNewApp,()=>{ continueCurrentApp(localVersionData); });
			return;
		}

		continueCurrentApp(localVersionData);
	}

	/** 继续使用当前的app版本 */
	private void continueCurrentApp(VersionSaveData localVersionData)
	{
		//当前版本够
		if(localVersionData.resourceVersion>=_versionData.currentResourceVersion)
		{
			doVersionNext(true);
		}
		else
		{
			//读取cdn版本
			ResourceInfoControl.loadCDNVersion(_versionData.currentResourceVersion,cdnVersion=>
			{
				//需要强制资源更新或之前未完全更完
				if(localVersionData.resourceVersion<_versionData.leastResourceVersion || !ResourceInfoControl.isVersionDataReady())
				{
					ResourceInfoControl.mergeVersion(cdnVersion);
					doVersionNext(true);
					return;
				}

				//计算更新量
				ResourceInfoControl.countNewVersionSize(cdnVersion,_versionRe);

				_downloadSize=StringUtils.toMBString(_versionRe.size);

				//更新资源选择
				showAlertInWifi(Alert_GetNewResource,()=>
				{
					ResourceInfoControl.mergeVersion(cdnVersion);
					doVersionNext(false);
				},versionUpdateOver);

			});
		}
	}

	/** 加载逻辑DLL */
	protected virtual void stepLoadGame()
	{
		toLoadGame(false);
	}

	/** 同步加载game(Editor用) */
	public void loadGameSync()
	{
		toLoadGame(true);
	}

	protected void toLoadGame(bool isForEditor)
	{
		if(ShineSetting.useHotFix)
		{
			if(!ShineSetting.isRelease)
			{
				bool useRelease=true;
				string dd=useRelease ? "Release" : "Debug";

				//TODO:临时读取路径
				ShineGlobal.hotfixDllPath="../../hotfix/Temp/bin/" + dd + "/hotfix.dll";
				// ShineGlobal.hotfixPDBPath="../hotfix/Temp/bin/"+dd+"/hotfix.pdb";
			}

			//android或者本地开启反射
			if(Application.platform==RuntimePlatform.Android || ShineSetting.useReflectDll)
			{
				string mName=isForEditor ? "mainForEditor" : "main";

				string dllPath;

				if(ShineSetting.isRelease)
				{
					dllPath=ResourceInfoControl.getResourceSavePath(ShineGlobal.hotfixDllPath);
				}
				else
				{
					dllPath=ResourceInfoControl.getStreamingAssetsPath(ShineGlobal.hotfixDllPath,false);
				}

				MethodInfo method=Assembly.LoadFrom(dllPath).GetType("HGameApp").GetMethod(mName);

				if(method==null)
				{
					Ctrl.throwError("不能没有入口方法:HGameApp::"+mName);
					return;
				}

				method.Invoke(null,null);

				if(!isForEditor)
					_stepTool.completeStep(LoadGame);
			}
			else
			{
				if(isForEditor)
				{
					byte[] dllBytes=FileUtils.readFileForBytes(ResourceInfoControl.getStreamingAssetsPath(ShineGlobal.hotfixDllPath,false));
					onLogicDllLoaded(dllBytes,null,isForEditor);
				}
				else
				{
					_loader=new ShineLoader();
					_loader.setCompleteCall(()=>
					{
						byte[] dllBytes=_loader.getBytes();
						_loader.unload();

						onLogicDllLoaded(dllBytes,null,isForEditor);
						//
						//					if(!ShineSetting.isRelease)
						//					{
						//						_loader.setCompleteCall(()=>
						//						{
						//							byte[] pdbBytes=_loader.getWWW().bytes;
						//							_loader.unload();
						//
						//							onLogicDllLoaded(dllBytes,pdbBytes);
						//						});
						//
						//						_loader.loadFromFileWithoutPlatform(MainSetting.logicPDBPath);
						//					}
						//					else
						//					{
						//						onLogicDllLoaded(dllBytes,null);
						//					}
					});

					//不是发布模式
					if(ShineSetting.isRelease)
					{
						_loader.loadResource(ShineGlobal.hotfixDllPath);
					}
					else
					{
						_loader.loadStreamingAsset(ShineGlobal.hotfixDllPath);
					}
				}
			}
		}
		else
		{
			callGAppMain(isForEditor);

			if(!isForEditor)
				_stepTool.completeStep(LoadGame);
		}
	}

	protected virtual void callGAppMain(bool isForEditor)
	{

		string clsName=CommonSetting.clientNeedHotfix ? "HGameApp" : "GGameApp";
		string mName=isForEditor ? "mainForEditor" : "main";

		Assembly callingAssembly=Assembly.GetCallingAssembly();

		Type type=callingAssembly.GetType(clsName);

		//当前域
		MethodInfo method=Assembly.GetCallingAssembly().GetType(clsName).GetMethod(mName);

		if(method==null)
		{
			Ctrl.throwError("不能没有入口方法:HGameApp::"+mName);
			return;
		}

		try
		{
			method.Invoke(null,null);
		}
		catch(Exception e)
		{
			Ctrl.throwError(e);
		}
	}

	//methods

	private void onLogicDllLoaded(byte[] dllBytes,byte[] pdbBytes,bool isForEditor)
	{
		if(_app==null)
		{
			_app=new ILRuntime.Runtime.Enviorment.AppDomain();
		}

		//发布
		if(pdbBytes==null)
		{
			_app.LoadAssembly(new MemoryStream(dllBytes));
		}
		//调试
		else
		{
			_app.LoadAssembly(new MemoryStream(dllBytes),new MemoryStream(pdbBytes),new Mono.Cecil.Pdb.PdbReaderProvider());
		}

		//初始化
		GameC.ILRuntime.initILRuntime(_app);

		string mName=isForEditor ? "mainForEditor" : "main";

		//调用
		_app.Invoke("HGameApp",mName, null, null);

		if(!isForEditor)
			_stepTool.completeStep(LoadGame);
	}

	/** 无网络的处理 */
	protected virtual void noNetHandler()
	{
		Ctrl.print("无网络");

		//可以离线登录
		if(GameC.save.canOfflineLogin())
		{
			onGetVersionSuccess(null,"");
		}
		else
		{
			if(!_noNetAlertShowed)
			{
				_noNetAlertShowed=true;
				showBoard(Board_NoNet);
			}
		}
	}

	/** 网络改变 */
	private void onNetChanged()
	{
		//在执行getVersion中
		if(_stepTool.isDoing(GetVersion))
		{
			doGetVersion();
		}
	}

	private void doGetVersion()
	{
		if(SystemControl.isNetOpen())
		{
			if(_noNetAlertShowed)
			{
				_noNetAlertShowed=false;
				hideBoard(Board_NoNet);
			}

			sendGetVersion();
		}
		else
		{
			if(!_noNetAlertShowed)
			{
				_noNetAlertShowed=true;
				showBoard(Board_NoNet);
			}
		}
	}

	private void sendGetVersion()
	{
		if(!_getVersionSended)
		{
			_getVersionSended=true;

			doSendGetVersion();
		}
	}

	protected virtual void doSendGetVersion()
	{
		//本地版本数据
		VersionSaveData localVersionData=ResourceInfoControl.getVersion();
		//发送当前的本地版本
		ClientGetVersionHttpRequest.create(SystemControl.clientPlatform,localVersionData.resourceVersion).send();
	}

	/** 获取版本号失败 */
	public void onGetVersionFailed()
	{
		if(CommonSetting.isSingleGame)
		{
			onGetVersionSuccess(null,"");
		}
		else
		{
			//可以离线登录
			if(GameC.save.canOfflineLogin())
			{
				onGetVersionSuccess(null,"");
			}
			else
			{
				_getVersionSended=false;

				doGetVersionFailed();
			}
		}
	}

	/** 获取版本号失败 */
	protected virtual void doGetVersionFailed()
	{
		notice(Notice_GetVersionFailed);
		//2秒继续
		TimeDriver.instance.setTimeOut(doGetVersion,2000);
	}

	/** 获取版本号成功(有可能返回空) */
	public void onGetVersionSuccess(ClientVersionData vData,string redirectURL)
	{
		_getVersionSended=false;
		_versionData=vData;

		//有值
		if(!string.IsNullOrEmpty(redirectURL))
		{
			//重定向url
			LocalSetting.loginHttpURL=redirectURL;
			//再次发送
			sendGetVersion();
		}
		else
		{
			SystemControl.netChangeFunc-=onNetChanged;
			_stepTool.completeStep(GetVersion);
		}
	}

	/** 获取当前版本数据 */
	public ClientVersionData getVersionData()
	{
		return _versionData;
	}

	/** 获取新版本链接 */
	protected virtual void toGetNewApp()
	{
		//下载路径
		string path=ResourceInfoControl.getAppPath(_versionData.currentAppVersion);

		//TODO:跳到appStore的api
	}

	/** 执行版本更新下一步 */
	protected void doVersionNext(bool needAlert)
	{
		doVersionNext(needAlert,versionUpdateOver);
	}

	/** 执行版本更新下一步 */
	protected void doVersionNext(bool needAlert,Action overFunc)
	{
		int size=ResourceInfoControl.preDownLoad();

		//需要下载
		if(size>0)
		{
			//不是wifi或者是wifi但是需要弹出
			if(needAlert && ShineSetting.isRelease)
			{
				_downloadSize=StringUtils.toMBString(size);

				//更新资源选择
				showAlertInWifi(Alert_GetNewResourceMust,()=>
				{
					startVersionDownload(overFunc);

				},exitGame);
			}
			else
			{
				startVersionDownload(overFunc);
			}
		}
		else
		{
			if(overFunc!=null)
				overFunc();
		}
	}

	/** 看看wifi下是否还要弹出资源下载提示 */
	private void showAlertInWifi(int type,Action okFunc,Action cancelFunc)
	{
		if (!SystemControl.isSomeNetWork(NetworkReachability.ReachableViaLocalAreaNetwork) ||
		    ShineSetting.needAlertInWifiMode)
		{
			showAlert(type,okFunc,cancelFunc);
		}
		else
		{
			okFunc();
		}
	}
	
	/** 开始资源下载 */
	protected virtual void startVersionDownload(Action overFunc)
	{
		//UI显示
		ResourceInfoControl.doDownload(overFunc);

		//期间可以通过ResourceInfoControl.getCurrentLoadSize和getNeedLoadSize获取下载的资源进度
		//再通过StringUtils.toMBString转字符串显示
	}

	/** 版本更新步骤结束 */
	protected virtual void versionUpdateOver()
	{
		//关闭UI显示
		_stepTool.completeStep(UpdateResource);
	}

	/** 客户端版本更新(true:为不需要更新) */
	public bool clientHotfix(ClientVersionData vData)
	{
		if(ShineSetting.debugJumpResourceVersion || ShineSetting.localLoadWithOutBundle)
			return true;

		//已处理过
		if(_versionData!=null && vData!=null && _versionData.currentResourceVersion>=vData.currentResourceVersion)
			return true;

		VersionSaveData localVersionData=ResourceInfoControl.getVersion();

		if(vData==null)
		{
			Ctrl.log("离线游戏 或 服务器无此版本2");

			vData=new ClientVersionData();
			vData.version=localVersionData.version;
			vData.currentAppVersion=localVersionData.appVersion;
			// _versionData.leastAppVersion=localVersionData.currentAppVersion;
			vData.currentResourceVersion=localVersionData.resourceVersion;
		}

		_versionData=vData;

		if(!ShineSetting.isRelease)
		{
			return true;
		}
		else
		{
			//最低app版本不足
			if(localVersionData.appVersion<_versionData.leastAppVersion)
			{
				showAlert(Alert_GetNewAppMust,toGetNewApp,exitGame);
				return false;
			}

			//有新的app
			if(localVersionData.appVersion<_versionData.currentAppVersion)
			{
				showAlert(Alert_GetNewApp,toGetNewApp,()=>{ hotfixNext(vData); });
				return false;
			}

			hotfixNext(vData);
			return false;
		}
	}

	private void hotfixNext(ClientVersionData vData)
	{
		//读取cdn版本
		ResourceInfoControl.loadCDNVersion(vData.currentResourceVersion,sData=>
		{
			//计算更新量
			ResourceInfoControl.countNewVersionSize(sData,_versionRe);

			//只有配置
			if(_versionRe.isOnlyConfig)
			{
				ResourceInfoControl.mergeVersion(sData);
				doVersionNext(false,()=>
				{
					if(GameC.main!=null)
					{
						//热更配置
						GameC.main.reloadConfig();
						GameC.main.hotfixOver();
					}
				});
			}
			//返回登录界面开始热更
			else
			{
				VersionSaveData localVersionData=ResourceInfoControl.getVersion();

				_downloadSize=StringUtils.toMBString(_versionRe.size);
				
				//需要强制更新
				if(localVersionData.resourceVersion<vData.leastResourceVersion)
				{
					//更新资源选择,这里是在游戏内，必须弹窗确认
					showAlert(Alert_GetNewResourceMust,()=>
					{
						doHotFix(sData);

					},exitGame);
				}
				else
				{
					//更新资源选择,这里是在游戏内，必须弹窗确认
					showAlert(Alert_GetNewResource,()=>{ doHotFix(sData); },()=>
					{
						if(GameC.main!=null)
						{
							GameC.main.hotfixOver();
						}
					});
				}
			}
		});
	}

	private void doHotFix(VersionSaveData sData)
	{
		_getVersionSended=false;

		if(GameC.main!=null)
		{
			//回到登录界面
			GameC.main.backForHotfix();
		}

		//清空信息
		LoadControl.clearAllResource();
		LoadControl.clearResourceInfo();

		_stepTool.clearStates();
		_stepTool.completeStepPre(UpdateResource);

		ResourceInfoControl.mergeVersion(sData);
		doVersionNext(false);
	}

	protected void toSendLog(int type,string str)
	{
		//game层处理掉了
		if(_gameSendLogFunc!=null && _gameSendLogFunc(type,str))
			return;

		string uid=GameC.main!=null ? GameC.main.uid : "";

		if(SystemControl.isNetOpen())
		{
			try
			{
				Ctrl.setLogPause(true);
				SendHttpClientLogHttpRequest.create(uid,type,str).send();
			}
			catch(Exception e)
			{

			}

			Ctrl.setLogPause(false);
		}
	}

	//--显示部分--//

	/** 显示板子 */
	protected virtual void showBoard(int type)
	{

	}

	/** 隐藏板子 */
	protected virtual void hideBoard(int type)
	{

	}

	/** 显示弹窗 */
	protected virtual void showAlert(int type,Action okFunc,Action cancelFunc)
	{
		TimeDriver.instance.setTimeOut(okFunc,1000);
	}

	/** 显示通知tips */
	protected virtual void notice(int type)
	{

	}

	/** 退出游戏 */
	protected virtual void exitGame()
	{
		ShineSetup.exit();
	}
}