using ShineEngine;
using UnityEngine;

/// <summary>
/// common主控制
/// </summary>
[Hotfix]
public class GameMainControl
{
	/** 加载bundle信息 */
	protected const int LoadBundleInfo=1;
	/** 加载配置 */
	protected const int LoadConfig=2;
	/** 加载初次资源 */
	protected const int LoadFirstResource=3;
	/** 账号输入过程(登录口) */
	protected const int InputUser=4;
	/** http登录(到返回serverList) */
	protected const int LoginHttp=5;
	/** 客户端选择服务器 */
	protected const int SelectServer=6;
	/** 发送选择服务器(到收到serverInfo) */
	protected const int LoginSelect=7;
	/** 连接game服(到返回playerList) */
	protected const int ConnectGame=8;
	/** 检查创建角色(到createSuccess) */
	protected const int CheckCreatePlayer=10;
	/** 选择角色登录(到initClient) */
	protected const int PlayerLogin=11;
	/** 游戏预备开始(initClient后到进入初次场景) */
	protected const int PreGameStart=12;
	/** 游戏开始(进入初次场景或空场景后) */
	protected const int GameStart=13;

	/** 流程插件 */
	protected StepTool _stepTool=new StepTool();

	/** 用户名 */
	public string uid="sm001";
	/** 平台类型 */
	public string platform=PlatformType.Visitor;
	/** 区服id */
	public int areaID=1;
	/** 国家ID */
	public int countryID=-1;
	/** 服务器列表 */
	public SList<AreaClientData> serverList;
	/** 上次选择的区服ID(无没有则为-1) */
	public int lastAreaID=-1;

	/** 本次登录数据 */
	private ClientLoginData _loginData;

	/** http登陆重放事务 */
	private AffairInterval _httpLoginAffair;
	/** http登陆选择服务器重放事务 */
	private AffairInterval _httpLoginSelectAffair;
	/** http登陆是否正发送中 */
	private AffairTimeLock _httpLoginSended=new AffairTimeLock(10);
	/** game是否正连接中 */
	private AffairTimeLock _connectGaming=new AffairTimeLock(20);
	/** 等待热更结果(3分钟) */
	private AffairTimeLock _waitHotfixForHttpLogin=new AffairTimeLock(180);

	/** 登录令牌 */
	private int _loginToken;
	/** 逻辑服信息 */
	private ClientLoginServerInfoData _gameInfo;

	private SList<PlayerLoginData> _playerList;

	/** 是否是切换中(否则就是登陆中) */
	private bool _isSwitching=false;

	/** 游戏运行中(连接后) */
	private bool _running=false;
	/** 是否进入过初次场景 */
	private bool _enteredFirstScene=false;

	public GameMainControl()
	{

	}

	public virtual void init()
	{
		_httpLoginAffair=new AffairInterval(doSendLoginHttp,5000);
		_httpLoginSelectAffair=new AffairInterval(doSendLoginSelect,5000);

		TimeDriver.instance.setInterval(onSecond,1000);

		if(CommonSetting.useMultiLanguage)
		{
			int language=GameC.save.getInt(LocalSaveType.Language);

			//未赋值
			if(language<=0)
			{
				GameC.save.setInt(LocalSaveType.Language,language=getCurrentSystemLanguage());
			}

			CommonSetting.languageType=language;
		}
		else
		{
			CommonSetting.languageType=LanguageType.Zh_CN;
		}

		initStep();

		SystemControl.netChangeFunc+=onNetChanged;
		SystemControl.applicationPauseFunc+=onApplicationPause;
	}

	protected virtual void initStep()
	{
		_stepTool.addStep(LoadBundleInfo,stepLoadBundleInfo);
		_stepTool.addNext(LoadConfig,stepLoadConfig);//要晚于BundleInfo，为了Config的Resource字段
		_stepTool.addNext(LoadFirstResource,stepLoadFirstResource);
		_stepTool.addNext(InputUser,stepInputUser);

		if(!CommonSetting.isSingleGame)
		{
			_stepTool.addNext(LoginHttp,stepLoginHttp);
			_stepTool.addNext(SelectServer,stepSelectServer);
			_stepTool.addNext(LoginSelect,stepLoginSelect);
			_stepTool.addNext(ConnectGame,stepConnectGame);

			_stepTool.addNext(CheckCreatePlayer,stepCheckCreatePlayer);
		}

		_stepTool.addNext(PlayerLogin,stepPlayerLogin);
		_stepTool.addNext(PreGameStart,stepPreGameStart);
		_stepTool.addNext(GameStart,stepGameStart);
	}

	public void dispose()
	{

	}

	protected void clear()
	{
		_running=false;
		_isSwitching=false;
		_enteredFirstScene=false;
		_httpLoginAffair.stop();
		_httpLoginSended.unlock();
		_connectGaming.unlock();
		_waitHotfixForHttpLogin.unlock();
		_httpLoginSelectAffair.stop();

		GameC.server.clear();
	}

	/** 退出游戏(直接) */
	public void exit()
	{
		ShineSetup.exit();
	}

	/** 主动退出游戏(退出进程) */
	public void exitGame()
	{
		toExitGame();

		//1秒后强行
		TimeDriver.instance.setTimeOut(exit,1000);
	}

	private void toExitGame()
	{
		//标记退出
		clear();
		PlayerExitRequest.create().send();
	}

	protected virtual void onSecond(int delay)
	{
		if(GameC.player.system.inited())
		{
			GameC.player.onSecond(delay);
		}
	}

	/// <summary>
	/// 开始登陆流程
	/// </summary>
	public void start()
	{
		Ctrl.print("开始登录流程");

		_stepTool.clearStates();
		_stepTool.checkStep();
	}

	/** 退回到开始登录位置(带退出游戏) */
	public virtual void backToLogin()
	{
		backForHotfix();

		//执行InputUser
		//还原状态
		_stepTool.clearStates();
		_stepTool.doStepAbs(InputUser);
	}

	/** 退回到准备热更 */
	public void backForHotfix()
	{
		if(_running)
		{
			toExitGame();
		}

		clearToLogin();

		//还原状态
		_stepTool.clearStates();
	}

	/** 返回到主界面位置(暂时无用) */
	public void backToMain()
	{
		GameC.scene.leaveSceneAbs();
		GameC.ui.hideAllUI();
	}


	/** 清空到登录 */
	protected virtual void clearToLogin()
	{
		clear();

		GameC.scene.leaveSceneAbs();
		GameC.ui.hideAllUI();

		GameC.server.dispose();

		//初始化过的
		if(GameC.player.system.inited())
		{
			//重建player
			reBuildPlayer(true);
		}
	}

	/** 配置表加载完毕 */
	protected virtual void configLoadOver()
	{
		GameC.app.onConfigInitOver();
	}

	//steps

	protected virtual void stepLoadBundleInfo()
	{
		if(ShineSetting.localLoadWithOutBundle)
		{
			//直接标记完成
			LoadControl.registBundleOver();
			_stepTool.completeStep(LoadBundleInfo);
		}
		else
		{
			ResourceInfoControl.loadBundleInfo(()=>{ _stepTool.completeStep(LoadBundleInfo); });
		}
	}

	protected virtual void stepLoadConfig()
	{
		BaseC.config.load(()=>
		{
			configLoadOver();

			_stepTool.completeStep(LoadConfig);
		});
	}

	protected virtual void stepLoadFirstResource()
	{
		if(ShineSetting.localLoadWithOutBundle)
		{
			_stepTool.completeStep(LoadFirstResource);
			return;
		}

		//此加载内容暂时不删除
		LoadControl.loadList(MarkResourceConfig.firstLoadList,()=>
		{
			_stepTool.completeStep(LoadFirstResource);
		},-1,false);
	}

	/** 用户账号输入过程 */
	protected virtual void stepInputUser()
	{
		if(CommonSetting.isSingleGame)
		{
			// doStepInputUser
			doStepInputUser(null,true);
		}
		else
		{
			//先读取历史信息
			ClientLoginCacheData cacheData=GameC.save.loadLoginCache();

			//读取数据
			if(cacheData!=null)
			{
				uid=cacheData.uid;
				platform=cacheData.platform;
				areaID=cacheData.areaID;
			}

			bool hasCache=cacheData!=null && CommonSetting.useOfflineGame && GameC.save.hasLastLoginPlayer();

			if(SystemControl.isNetOpen())
			{
				doStepInputUser(cacheData,hasCache);
			}
			else
			{
				if(CommonSetting.useOfflineGame)
				{
					doStepInputUser(cacheData,hasCache);
				}
				else
				{
					//TODO:此处暂时未做网络恢复的继续工作,如以后有需要就补上
					GameC.ui.alert(TextEnum.NetNotOpen,exit);
				}
			}
		}

	}

	/** 执行用户账号输入(data:上次的记录数据) */
	protected virtual void doStepInputUser(ClientLoginCacheData data,bool canOffline)
	{
		TimeDriver.instance.setTimeOut(inputUserOver,100);
	}

	public void inputUserOver()
	{
		inputUserOver(createClientLoginData());
	}

	/** 账号输入完毕 */
	public void inputUserOver(ClientLoginData data)
	{
		if(data.uid.isEmpty())
		{
			Ctrl.throwError("uid不能为空",data.uid);
			return;
		}

		if(data.platform.isEmpty())
		{
			Ctrl.throwError("platform不能为空",data.platform);
			return;
		}

		_loginData=data;

		GameC.save.saveByLogin(data);

		_stepTool.completeStep(InputUser);
	}

	private void doSendLoginSelect()
	{
		//网路不畅通
		if(!SystemControl.isNetOpen())
		{
			onLoginSelectFailed();
		}
		else
		{
			ClientLoginSelectHttpRequest.create(_loginToken,areaID).send();
		}
	}

	/** 选择服务器失败 */
	public void onLoginSelectFailed()
	{
		GameC.ui.notice(TextEnum.ServerNotRespond);
	}

	/** 获取服务器列表成功 */
	public void onLoginSelectSuccess(ClientLoginServerInfoData info)
	{
		_httpLoginSelectAffair.stop();
		_gameInfo=info;
		_stepTool.completeStepAbs(LoginSelect);
	}

	/** 选择服务器过程 */
	protected virtual void stepSelectServer()
	{
		//显示界面
		TimeDriver.instance.setTimeOut(()=>
		{
			//默认选1服
			selectServerOver(areaID);
		},100);
	}

	/** 客户端选择服务器结束 */
	protected void selectServerOver(int areaID)
	{
		this.areaID=areaID;
		GameC.save.saveByAreaID(areaID);

		GameC.log.addFlowLog(FlowStepType.SelectServerOver);
		_stepTool.completeStepAbs(SelectServer);
	}

	protected void stepLoginSelect()
	{
		_httpLoginSelectAffair.startAndExecuteOnce();
	}

	protected void stepConnectGame()
	{
		toConnectGame();
	}

	public void refreshLoginData(string uid,string platform)
	{
		if (_loginData != null)
		{
			_loginData.uid = uid;
			_loginData.platform = platform;
		}

		this.uid = uid;
		this.platform = platform;
	}

	/** 登录离线数据 */
	public bool offlineLogin()
	{
		ClientLoginCacheData data=GameC.save.loadLoginCache();

		if(data==null)
		{
			Ctrl.errorLog("不能找不到缓存数据");
			return false;
		}

		if(data.lastPlayerID<=0)
		{
			Ctrl.errorLog("不该没有上次登录角色ID");
			return false;
		}

		GameC.offline.loadPlayer(data.lastPlayerID);

		PlayerListData listData=GameC.offline.getListData();

		if(listData==null)
		{
			Ctrl.errorLog("找不到角色缓存数据,或角色数据失效");
			return false;
		}

		GameC.player.readListData(listData);

		if(GameC.player.hasEmptyData())
		{
			Ctrl.errorLog("离线登录时，存在空数据");
			return false;
		}

		//初始化离线数据
		initClient(listData,false);

		//先切换到预备游戏
		_stepTool.doStepAbs(PreGameStart);

		//不考虑有场景的情况，目前直接进入空场景
		GameC.scene.onEnterNoneScene();

		beginLoginHttp();

		return true;
	}

	/** 创建登录数据 */
	public ClientLoginData createClientLoginData()
	{
		ClientLoginData data=GameC.factory.createClientLoginData();
		makeClientLoginData(data);

		return data;
	}

	/** 获取设备唯一标识 */
	protected virtual string getUniqueIdentifier()
	{
		return SystemInfo.deviceUniqueIdentifier;
	}

	/** 创建一个visitorUID */
	protected virtual string createVisitorUID()
	{
		// string re=GameC.player.getTimeMillis().ToString("X11") + MathUtils.randomInt(4096).ToString("X3");
		string re=getUniqueIdentifier();

		return re;
	}

	protected string getVisitorUID()
	{
		string re=GameC.save.getString(LocalSaveType.VisitorUID);

		if(re.isEmpty())
		{
			re=createVisitorUID();
			GameC.save.setString(LocalSaveType.VisitorUID,re);
		}

		return re;
	}

	public virtual void makeClientLoginData(ClientLoginData data)
	{
		//游客
		if(platform==PlatformType.Visitor)
		{
			uid=getVisitorUID();
		}

		data.uid=uid;
		data.countryID=countryID;
		data.platform=platform;
		data.clientPlatformType=SystemControl.clientPlatform;
		data.deviceType=SystemInfo.deviceType.ToString();
		data.deviceUniqueIdentifier=SystemInfo.deviceUniqueIdentifier;
		data.visitorUID="";

		VersionSaveData localVersionData=ResourceInfoControl.getVersion();

		data.appVersion=localVersionData.appVersion;
		data.resourceVersion=localVersionData.resourceVersion;

		ClientLoginCacheData loginCacheData=GameC.save.loadLoginCache();

		//游客平台,为自动绑定
		if(loginCacheData!=null && loginCacheData.platform==PlatformType.Visitor && data.platform!=PlatformType.Visitor)
		{
			data.visitorUID=loginCacheData.uid;
		}
	}

	/** 登录步 */
	protected virtual void stepLoginHttp()
	{
		Ctrl.debugLog("开始httpLogin",LocalSetting.loginHttpURL);

		beginLoginHttp();
	}

	private void beginLoginHttp()
	{
		//强制完成前置
		_stepTool.setCurrentDoing(LoginHttp);
		_httpLoginAffair.startAndExecuteOnce();
	}

	protected bool isHttpLogining()
	{
		return _httpLoginAffair.isRunning();
	}

	private void doSendLoginHttp()
	{
		//热更等待中不再发送
		if(_waitHotfixForHttpLogin.isLocking())
			return;

		//网路不畅通
		if(!SystemControl.isNetOpen())
		{
			onLoginHttpFailed();
		}
		else
		{
			if(!_httpLoginSended.isLocking())
			{
				_httpLoginSended.lockOn();
				ClientLoginHttpRequest.create(CodeCheckRecord.msgDataVersion,BaseC.config.getMsgDataVersion(),_loginData).send();
			}
		}
	}

	private void onNetChanged()
	{
		//不是单机
		if(!CommonSetting.isSingleGame)
		{
			//网络恢复
			if(SystemControl.isNetOpen())
			{
				//离线游戏中
				if(GameC.player.system.isOfflineRunning())
				{
					Ctrl.print("开始尝试登录服务器");
					beginLoginHttp();
				}
			}
			else
			{
				tryEnterOfflineMode();
			}
		}
	}

	private void toConnectGame()
	{
		//标记运行中
		_running=true;

		if(!_connectGaming.isLocking())
		{
			Ctrl.debugLog("开始connectGame",_gameInfo.host,_gameInfo.port);
			_connectGaming.lockOn();

			GameC.server.connect(_gameInfo.host,_gameInfo.port);
		}
		else
		{
			Ctrl.debugLog("开始connectGame下");
		}
	}

	/** 连接游戏服成功 */
	public void connectGameSuccess()
	{
		if(!_running)
			return;

		_connectGaming.unlock();
		Ctrl.debugLog("连接game成功");

		if(_isSwitching)
		{
			_isSwitching=false;
			PlayerSwitchGameRequest.create(_gameInfo.token).send();
		}
		else
		{
			//可以停了
			_httpLoginAffair.stop();

			VersionSaveData versionSaveData=ResourceInfoControl.getVersion();
			int resourceVersion=versionSaveData!=null ? versionSaveData.resourceVersion : 100000;
			LoginGameRequest.create(_gameInfo.token,CodeCheckRecord.msgDataVersion,BaseC.config.getMsgDataVersion(),resourceVersion).send();
		}
	}

	/** 连接game失败 */
	public void connectGameFailed()
	{
		if(!_running)
			return;

		_connectGaming.unlock();
		Ctrl.log("连接Game失败一次");

		if(!CommonSetting.useOfflineGame || !GameC.player.system.inited())
		{
			Ctrl.log("连接Game失败");

			//需要退出了
			GameC.ui.notice(TextEnum.ServerNotRespond);
		}
	}

	/** 回复角色列表 */
	public void onRePlayerList(SList<PlayerLoginData> list,int serverBornCode)
	{
		if(!_running)
			return;

		Ctrl.log("回复角色列表",list.length());

		_playerList=list;

		ClientLoginCacheData loadLoginCache=GameC.save.loadLoginCache();

		bool serverChanged=loadLoginCache!=null && loadLoginCache.serverBornCode!=-1 && loadLoginCache.serverBornCode!=serverBornCode;

		//保存出生码
		GameC.save.saveByServerBornCode(serverBornCode);

		//有数据
		if(CommonSetting.useOfflineGame && GameC.player.system.isOfflineRunning() && !_playerList.isEmpty())
		{
			stepPlayerLogin();
		}
		else
		{
			if(ShineSetting.openCheck)
			{
				if(_stepTool.isComplete(CheckCreatePlayer))
				{
					Ctrl.errorLog("step不该已完成:CheckCreatePlayer");
				}
			}

			_stepTool.completeStepAbs(ConnectGame);
		}
	}

	protected virtual void stepCheckCreatePlayer()
	{
		if(_playerList.Count==0)
		{
			Ctrl.log("列表为空需要创建");

			CreatePlayerRequest.create(getCreatePlayerData()).send();
		}
		else
		{
			Ctrl.print("有角色直接登录");

			if(ShineSetting.openCheck)
			{
				if(_stepTool.isComplete(PlayerLogin))
				{
					Ctrl.errorLog("step不该已完成:PlayerLogin");
				}
			}

			_stepTool.completeStep(CheckCreatePlayer);
		}
	}

	/** 创建一个随机名字 */
	public virtual string createRandomName()
	{
		return "name_" + MathUtils.randomInt(1000);
	}

	/** 创建角色数据 */
	protected virtual CreatePlayerData getCreatePlayerData()
	{
		CreatePlayerData data=new CreatePlayerData();
		data.name=createRandomName();
		data.sex=SexType.Boy;
		data.vocation=VocationType.DefaultVocation;

		return data;
	}

	/** 创角成功 */
	public virtual void onCreatePlayerSuccess(PlayerLoginData pData)
	{
		Ctrl.print("创建角色成功");
		
		_playerList.add(pData);

		if(ShineSetting.openCheck)
		{
			if(_stepTool.isComplete(PlayerLogin))
			{
				Ctrl.errorLog("step不该已完成:PlayerLogin");
			}
		}

		_stepTool.completeStep(CheckCreatePlayer);
	}

	/** 选择角色登录 */
	protected virtual void stepPlayerLogin()
	{
		if(CommonSetting.isSingleGame)
		{
			GameC.offline.loadPlayer(1);

			PlayerListData listData=GameC.offline.getListData();

			showSinglePlayerLogin(listData);
		}
		else
		{
			long playerID=_playerList[0].playerID;

			if(CommonSetting.useOfflineGame)
			{
				GameC.offline.loadPlayer(playerID);

				ClientOfflineWorkListData listData=GameC.offline.getOfflineWorkListData();

				SList<ClientOfflineWorkData> list=new SList<ClientOfflineWorkData>();

				if(listData!=null)
				{
					list.addAll(listData.list);
				}

				Ctrl.log("发送离线登录");

				PlayerLoginForOfflineRequest.create(playerID,list,listData!=null ? listData.clientRandomSeedIndex : 0).send();
			}
			else
			{
				//登录
				PlayerLoginRequest.create(playerID).send();
			}
		}
	}

	/** 通过 listData 选择 */
	protected virtual void showSinglePlayerLogin(PlayerListData listData)
	{
		if(listData==null)
		{
			listData=createSinglePlayer(createCreatePlayerData());
		}

		singleLogin(listData);
	}

	/** 创建登录数据 */
	public CreatePlayerData createCreatePlayerData()
	{
		CreatePlayerData data=GameC.factory.createCreatePlayerData();
		makeCreatePlayerData(data);

		return data;
	}

	public virtual void makeCreatePlayerData(CreatePlayerData data)
	{
		data.name=getUniqueIdentifier();
		data.sex=SexType.Boy;
		data.vocation=VocationType.DefaultVocation;
	}

	/** 创建单人角色 */
	protected virtual PlayerListData createSinglePlayer(CreatePlayerData createData)
	{
		Player player=GameC.player;

		player.newInitData();

		RolePartData rolePartData=player.role.getPartData();

		//设置基础键组
		rolePartData.playerID=1;
		rolePartData.name=createData.name;
		rolePartData.userID=1;
		rolePartData.uid=getVisitorUID();
		rolePartData.createAreaID=1;
		rolePartData.platform=PlatformType.Visitor;
		rolePartData.isAdult=true;

		SystemPartData systemPartData=player.system.getPartData();
		systemPartData.createDate=DateData.getNow();//记录创建时间

		//逻辑相关
		player.role.doCreatePlayer(createData);

		//创建调用
		player.onNewCreate();

		//创建角色日志
		GameC.log.createPlayer(player);

		PlayerListData listData=player.createListData();
		player.writeListData(listData);

		return listData;
	}

	/** 单人模式登录 */
	public void singleLogin(PlayerListData listData)
	{
		//初始化离线数据
		initClient(listData,false);

		//先切换到预备游戏
		_stepTool.doStepAbs(PreGameStart);

		//不考虑有场景的情况，目前直接进入空场景
		GameC.scene.onEnterNoneScene();
	}

	/** 重建player(isFull:是否完整重建(目前是包括引导)) */
	public virtual void reBuildPlayer(bool isFull)
	{
		if(isFull)
		{
			GameC.guide.dispose();
		}

		GameC.player.dispose();
		GameC.playerSave.unloadPlayer();
		GameC.player.init();

		if(isFull)
		{
			GameC.guide.init();
		}
	}

	/** 初始化player数据(isOnline:是否在线登录,否则是离线登录) */
	public void initClient(PlayerListData listData,bool isOnline)
	{
		bool lastOnline=CommonSetting.useOfflineGame && GameC.player.system.inited();

		//此处不用
		if(lastOnline)
		{
			reBuildPlayer(false);
		}

		//读数据
		GameC.player.readListData(listData);

		if(CommonSetting.useOfflineGame)
		{
			//离线事务
			GameC.offline.loadPlayer(GameC.player.role.playerID);

			// bool hasError=GameC.player.system.getPartData().clientOfflineWorkReceiveIndex!=GameC.player.system.getPartData().clientOfflineWorkIndex;

			GameC.offline.receiveIndex(GameC.player.system.getPartData().clientOfflineWorkReceiveIndex);

			// if(lastOnline && hasError)
			// {
			// 	GameC.info.showInfoCode(InfoCodeType.OfflineWorkFailed);
			// 	return;
			// }
		}

		//加载好本地缓存数据
		GameC.playerSave.loadPlayer(GameC.player.role.playerID);

		GameC.player.system.setInited(true);
		GameC.player.system.setOnline(isOnline);

		if(CommonSetting.useOfflineGame && !isOnline)
		{
			//数据版本升级
			GameC.playerVersion.checkVersion(GameC.player);
		}

		//读后
		GameC.player.afterReadData();

		if(CommonSetting.isSingleGame)
		{
			//检查一次功能开启
			GameC.player.func.checkAllFunctions();
		}

		GameC.player.beforeLogin();

		//保存本次登录角色
		GameC.save.cacheLoginPlayer(GameC.player.role.uid,GameC.player.role.playerID,GameC.player.system.getPartData().serverBornCode);

		//初始化红点
		GameC.redPoint.initFirst();

		if(CommonSetting.useOfflineGame)
		{
			GameC.offline.saveOnce();
		}

		Ctrl.log("initClient后半段");

		if(lastOnline)
		{
			//派发消息
			GameC.player.dispatch(GameEventType.InitClient);
		}
		else
		{
			if(ShineSetting.openCheck)
			{
				if(_stepTool.isComplete(PreGameStart))
				{
					Ctrl.errorLog("step不该已完成:PlayerLogin");
				}
			}

			_stepTool.completeStep(PlayerLogin);
		}
	}

	/** 连接是否运行中 */
	public bool isRunning()
	{
		return _running;
	}

	/** 切换game服 */
	public void onSwitchGame(ClientLoginServerInfoData info)
	{
		//重新赋值
		_gameInfo=info;
		_isSwitching=true;

		GameC.server.close();

		GameC.server.connect(_gameInfo.host,_gameInfo.port);
	}

	//enter

	/** 游戏预备 */
	protected virtual void stepPreGameStart()
	{
		Ctrl.print("游戏预备开始");
	}

	/** 检查进入初次场景 */
	public void checkEnterFirstScene()
	{
		if(_enteredFirstScene)
			return;

		_enteredFirstScene=true;

		_stepTool.completeStep(PreGameStart);
	}

	/** 是否进入过初次场景 */
	public bool isEnteredFirstScene()
	{
		return _enteredFirstScene;
	}

	/** 游戏开始 */
	protected virtual void stepGameStart()
	{
		Ctrl.print("游戏开始");
		GameC.guide.triggerEvent(TriggerEventType.OnGameStart);
	}

	//config

	/** 重新加载配置 */
	public virtual void reloadConfig()
	{
		BaseC.config.load(onReloadConfig);
	}

	public void onReloadConfig()
	{
		//刷配置
		GameC.player.onReloadConfig();

		GameC.scene.onReloadConfig();

		GameC.player.dispatch(GameEventType.ReloadConfig);
	}

	//--登录方法部分(失败)--//

	/** 标记退出中 */
	public void setExit()
	{
		_running=false;
	}

	public virtual void onLoginHttpSuccess(ClientLoginResultData data)
	{
		_httpLoginSended.unlock();

		//已不在登录中
		if(!isHttpLogining())
			return;

		//分区分服的
		if(CommonSetting.isAreaSplit())
		{
			LocalSetting.setLoginURL(data.loginInfo.host,data.loginInfo.port);

			_loginToken=data.loginInfo.token;
			serverList=new SList<AreaClientData>(data.areas.size());

			foreach(int k in data.areas.getSortedKeyList())
			{
				serverList.add(data.areas.get(k));
			}

			this.lastAreaID=data.lastAreaID;
		}
		else
		{
			_gameInfo=data.gameInfo;
		}

		//未等待中
		if(!_waitHotfixForHttpLogin.isLocking())
		{
			//比较更新
			if(GameC.mainLogin.clientHotfix(data.version))
			{
				loginHttpNext();
			}
			else
			{
				_waitHotfixForHttpLogin.lockOn();
			}
		}
	}

	private void loginHttpNext()
	{
		//分区分服的
		if(CommonSetting.isAreaSplit())
		{
			//登陆的可以停了
			_httpLoginAffair.stop();
			GameC.log.addFlowLog(FlowStepType.BeginSelectServer);
			_stepTool.completeStepAbs(LoginHttp);
		}
		else
		{
			//直接完成loginSelect
			_stepTool.completeStepAbs(LoginSelect);
		}
	}

	/** 热更完毕继续流程 */
	public void hotfixOver()
	{
		if(_waitHotfixForHttpLogin.isLocking())
		{
			_waitHotfixForHttpLogin.unlock();
			loginHttpNext();
		}
	}
	
	public virtual void onLoginHttpFailed(int errorCode)
	{
		onLoginHttpFailed();
	}

	/** 客户端登陆失败 */
	public void onLoginHttpFailed()
	{
		_httpLoginSended.unlock();
		//已不在登录中
		if(!isHttpLogining())
			return;

		if(CommonSetting.useOfflineGame && GameC.save.canOfflineLogin())
		{
			if(!GameC.player.system.inited())
			{
				if(!offlineLogin())
				{
					GameC.ui.notice(TextEnum.ServerNotRespond);
				}
			}
		}
		else
		{
			GameC.ui.notice(TextEnum.ServerNotRespond);
		}
	}

	/** gameSocket断开(过了socket的断线重连,开始逻辑层断线重连) */
	public virtual void onGameSocketClosed()
	{
		if(!_running)
			return;

		//切换中忽略
		if(_isSwitching)
			return;

		Ctrl.print("onGameSocketClosed");

		doGameSocketClose();
	}

	/** 执行连接断开 */
	protected virtual void doGameSocketClose()
	{
		if(canOfflineMode())
		{
			enterOfflineMode();
		}
		else
		{
			Ctrl.print("连接被关闭");

			GameC.ui.alert(TextEnum.SocketClosed,backToLogin);
		}
	}

	/** 是否可进入离线模式 */
	public bool canOfflineMode()
	{
		return CommonSetting.useOfflineGame && GameC.player.system.inited();
	}

	/** 尝试进入离线模式 */
	public void tryEnterOfflineMode()
	{
		//在线游戏中
		if(canOfflineMode())
		{
			//关了连接,直接进入离线状态
			GameC.server.close();
			enterOfflineMode();
		}
	}

	/** 进入离线模式 */
	public void enterOfflineMode()
	{
		if(!CommonSetting.useOfflineGame)
			return;

		Ctrl.print("进入离线模式");
		GameC.player.system.setOnline(false);

		beginLoginHttp();
	}

	/** 获取当前系统语言 */
	public virtual int getCurrentSystemLanguage()
	{
		int re=LanguageType.Zh_CN;

		switch(Application.systemLanguage)
		{
			case SystemLanguage.Chinese:
			case SystemLanguage.ChineseSimplified:
			{
				re=LanguageType.Zh_CN;
			}
				break;
			case SystemLanguage.English:
			{
				re=LanguageType.En_US;
			}
				break;
		}

		return re;
	}

	/** 选择语言 */
	public void selectLanguage(int type)
	{
		if(CommonSetting.languageType==type)
			return;

		GameC.save.setInt(LocalSaveType.Language,CommonSetting.languageType=type);
		BaseC.config.refreshConfigForLanguage();

		FontControl.refreshLanguage();
		GameC.player.dispatch(GameEventType.RefreshLanguage);
	}

	private void onApplicationPause(bool pauseStatus)
	{
		//前后台切换事件！！！
		Player player = GameC.player;

		if (player != null)
		{
			player.dispatch(GameEventType.ApplicationPause,pauseStatus);
		}
	}
}