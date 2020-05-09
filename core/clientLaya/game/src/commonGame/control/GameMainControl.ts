namespace Shine
{
	export class GameMainControl
	{
		
	    /** 加载bundle信息 */
		protected LoadBundleInfo:number=1;
		/** 加载配置 */
		protected LoadConfig:number=2;
		/** 加载初次资源 */
		protected LoadFirstResource:number=3;
		/** 账号输入过程(登录口) */
		protected InputUser:number=4;
		/** http登录(到返回serverList) */
		protected LoginHttp:number=5;
		/** 选择服务器 */
		protected SelectServer:number=6;
		/** 发送选择服务器(到收到serverInfo) */
		protected LoginSelect:number=7;
		/** 连接game服(到返回playerList) */
		protected ConnectGame:number=9;
		/** 检查创建角色(到createSuccess) */
		protected CheckCreatePlayer:number=10;
		/** 选择角色登录(到initClient) */
		protected PlayerLogin:number=11;
		/** 游戏预备开始(initClient后到进入初次场景) */
		protected PreGameStart:number=12;
		/** 游戏开始(进入初次场景或空场景后) */
		protected GameStart:number=13;
	
		/** 流程插件 */
		protected _stepTool:StepTool=new StepTool();

		/** 用户名 */
		public uid:string="dmg1";
		/** 平台类型 */
		public platform:string=PlatformType.Youke;
		/** 区服id */
		public areaID:number=-1;
		/** 国家ID */
		public countryID:number=-1;
		/** 服务器列表 */
		public serverList:SList<AreaClientData>;
		/** 上次选择的区服ID(无没有则为-1) */
		public lastAreaID:number=-1;
	
		/** 本次登录数据 */
		private _loginData:ClientLoginData;

		/** http登陆重放事务 */
		private _httpLoginAffair:AffairInterval;
		/** http登陆选择服务器重放事务 */
		private _httpLoginSelectAffair:AffairInterval;
		/** http登陆是否正发送中 */
		private _httpLoginSended:AffairTimeLock=new AffairTimeLock(10);
		/** game是否正连接中 */
		private _connectGaming:AffairTimeLock=new AffairTimeLock(20);
		/** 等待热更结果(3分钟) */
		private _waitHotfixForHttpLogin:AffairTimeLock =new AffairTimeLock(180);
	
		/** 登录令牌 */
		private _loginToken:number;
		/** 逻辑服信息 */
		private _gameInfo:ClientLoginServerInfoData;
	
		protected _playerList:SList<PlayerLoginData>;
	
		/** 是否是切换中(否则就是登陆中) */
		private _isSwitching:boolean=false;
	
		/** 游戏运行中(连接后)  */
		private _running:boolean=false;
		/** 是否进入过初次场景 */
		private _enteredFirstScene:boolean=false;
	
		constructor()
		{
		}
	
		public init():void
		{
			this._httpLoginAffair=new AffairInterval(Func.create0(()=>{this.doSendLoginHttp()}),5000);
			this._httpLoginSelectAffair=new AffairInterval(Func.create0(()=>{this.doSendLoginSelect()}),5000);

		
			TimeDriver.instance.setInterval(Func.create(this,this.onSecond),1000);
	
			this.initStep();
		}

	
		protected initStep():void
		{
			this._stepTool.addStep(this.LoadBundleInfo,Func.create(this,this.stepLoadBundleInfo));
			this._stepTool.addNext(this.LoadConfig,Func.create(this,this.stepLoadConfig));//要晚于BundleInfo，为了Config的Resource字段
			this._stepTool.addNext(this.LoadFirstResource,Func.create(this,this.stepLoadFirstResource));
			this._stepTool.addNext(this.InputUser,Func.create(this,this.stepInputUser));
			this._stepTool.addNext(this.LoginHttp,Func.create(this,this.stepLoginHttp));
			this._stepTool.addNext(this.SelectServer,Func.create(this,this.stepSelectServer));
			this._stepTool.addNext(this.LoginSelect,Func.create(this,this.stepLoginSelect));
			this._stepTool.addNext(this.ConnectGame,Func.create(this,this.stepConnectGame));

			this._stepTool.addNext(this.CheckCreatePlayer,Func.create(this,this.stepCheckCreatePlayer));
			this._stepTool.addNext(this.PlayerLogin,Func.create(this,this.stepPlayerLogin));
			this._stepTool.addNext(this.PreGameStart,Func.create(this,this.stepPreGameStart));
			this._stepTool.addNext(this.GameStart,Func.create(this,this.stepGameStart));
		}
	
		public dispose():void
		{
	
		}
	
		private clear():void
		{
			this._running=false;
			this._isSwitching=false;
			this._enteredFirstScene=false;
			this._httpLoginAffair.stop();
			this._httpLoginSended.unlock();
			this._connectGaming.unlock();
			this._waitHotfixForHttpLogin.unlock();
			this._httpLoginSelectAffair.stop();
	
			GameC.server.clear();
		}
	
		/** 退出游戏(直接) */
		public exit():void
		{
			ShineSetup.exit();
		}
	
		/** 主动退出游戏(退出进程) */
		public exitGame():void
		{
			this.toExitGame();
	
			//1秒后强行
			TimeDriver.instance.setTimeOut(Func.create(this,this.exit),1000);
		}
	
		private toExitGame():void
		{
			//标记退出
			this.clear();
			PlayerExitRequest.createPlayerExit().send();
		}
	
		protected onSecond(delay:number):void
		{
			if(GameC.player.system.inited())
			{
				GameC.player.onSecond(delay);
			}
		}

		/** 开始登陆流程 */ 
		public start():void
		{
			Ctrl.print("开始登录流程");
			this._stepTool.clearStates();
			this._stepTool.checkStep();
		}
	
		/** 退回到开始登录位置(带退出游戏) */
		public backToLogin():void
		{
			this.backForHotfix();
			
			//执行InputUser
			//还原状态
			this._stepTool.clearStates();
			this._stepTool.doStepAbs(this.InputUser);
		}
	
		/** 退回到准备热更 */
		public backForHotfix():void
		{
			if(this._running)
			{
				this.toExitGame();
			}
	
			this.clearToLogin();
	
			//还原状态
			this._stepTool.clearStates();
		}
	
		/** 清空到登录 */
		protected clearToLogin()
		{
			this.clear();
	
			GameC.scene.leaveSceneAbs();
			// GameC.ui.hideAllUI();
	
			GameC.server.dispose();
	
			//初始化过的
			if(GameC.player.system.inited())
			{
				//重建player
				this.reBuildPlayer(true);
			}
		}
	
		/** 配置表加载完毕 */
		protected configLoadOver():void
		{
			GameC.app.onConfigInitOver();
		}
	
		//steps
		protected stepLoadBundleInfo():void
		{
			this._stepTool.completeStep(this.LoadBundleInfo);
		}
	
		protected stepLoadConfig():void
		{
			this.configLoadOver();
	
			this._stepTool.completeStep(this.LoadConfig);
		}
	
		protected stepLoadFirstResource():void
		{
			this._stepTool.completeStep(this.LoadFirstResource);
		}
	
		/** 用户账号输入过程 */
		protected stepInputUser():void
		{
			this.doStepInputUser(null,false);
		}
	
		/** 执行用户账号输入(data:上次的记录数据) */
		protected doStepInputUser(data:ClientLoginCacheData,canOffline:boolean):void
		{
			this.inputUserOver(this.createClientLoginData());
		}
	
		/** 账号输入完毕 */
		public inputUserOver(data:ClientLoginData=this.createClientLoginData()):void
		{
			this._loginData=data;
	
			Ctrl.debugLog("开始httpLogin",LocalSetting.loginHttpURL);
	
			this._stepTool.completeStepAbs(this.InputUser);
		}

		private doSendLoginSelect():void
		{
			ClientLoginSelectHttpRequest.createClientLoginSelect(this._loginToken,this.areaID).send();
		}

		/** 选择服务器失败 */
		public onLoginSelectFailed():void
		{
			GameC.ui.notice(TextEnum.ServerNotRespond);
		}

		/** 获取服务器列表成功 */
		public onLoginSelectSuccess(info:ClientLoginServerInfoData):void
		{
			this._httpLoginSelectAffair.stop();
			this._gameInfo=info;
			this._stepTool.completeStepAbs(this.LoginSelect);
		}

		
		/** 选择服务器过程 */
		protected stepSelectServer():void
		{
			this.selectServerOver();

			//显示界面
			// TimeDriver.instance.setTimeOut(Func.create0(()=>{}),100);
		}

		/** 客户端选择服务器结束 */
		protected selectServerOver():void
		{
			this._stepTool.completeStepAbs(this.SelectServer);
		}

		protected stepLoginSelect():void
		{
			this._httpLoginSelectAffair.startAndExecuteOnce();
		}

		protected stepConnectGame():void
		{
			this.toConnectGame();
		}

		public refreshLoginData(uid:string,platform:string):void
		{
			if (this._loginData != null)
			{
				this._loginData.uid = this.uid;
				this._loginData.platform = this.platform;
			}
		}

		/** 登录离线数据 */
		public offlineLogin():void
		{
		}
	
		/** 创建登录数据 */
		protected createClientLoginData():ClientLoginData
		{
			var data:ClientLoginData=GameC.factory.createClientLoginData();
			
	        this.makeClientLoginData(data);
	
			return data;
		}

		/** 获取设备唯一标识 */
		protected getUniqueIdentifier():string
		{
			return "default";
		}

		
		/** 创建一个visitorUID */
		protected createVisitorUID():string
		{
			// string re=GameC.player.getTimeMillis().ToString("X11") + MathUtils.randomInt(4096).ToString("X3");
			var re:string=this.getUniqueIdentifier();

			return re;
		}

		
		protected getVisitorUID():string
		{
			var re:string="";//GameC.save.getString(LocalSaveType.VisitorUID);

			// if(re.isEmpty())
			// {
			// 	re=createVisitorUID();
			// 	GameC.save.setString(LocalSaveType.VisitorUID,re);
			// }

			return re;
		}

		public makeClientLoginData(data:ClientLoginData):void
		{
			// //游客
			// if(this.platform==PlatformType.Visitor)
			// {
			// 	this.uid=this.getVisitorUID();
			// }

			// data.uid=this.uid;
			// data.areaID=this.areaID;
			// data.platform=this.platform;
			// data.clientPlatformType=SystemControl.clientPlatform;
			// data.deviceType=SystemInfo.deviceType.ToString();
			// data.deviceUniqueIdentifier=SystemInfo.deviceUniqueIdentifier;
			// data.visitorUID="";

			// var loginCacheData:ClientLoginCacheData=GameC.save.loadLoginCache();

			// //游客平台,为自动绑定
			// if(loginCacheData!=null && loginCacheData.platform==PlatformType.Visitor && data.platform!=PlatformType.Visitor)
			// {
			// 	data.visitorUID=loginCacheData.uid;
			// }
		}
	
		/** 登录步 */
		protected stepLoginHttp():void
		{
			Ctrl.debugLog("开始httpLogin",LocalSetting.loginHttpURL);

			this.beginLoginHttp();
		}
	
		private beginLoginHttp():void
		{
			//强制完成前置
			this._stepTool.setCurrentDoing(this.LoginHttp);
			this._httpLoginAffair.startAndExecuteOnce();
		}

		protected isHttpLogining():boolean
		{
			return this._httpLoginAffair.isRunning();
		}

		private doSendLoginHttp():void
		{
			//热更等待中不再发送
			if(this._waitHotfixForHttpLogin.isLocking())
				return;


			// //网路不畅通
			// if(!SystemControl.isNetOpen())
			// {
			// 	this.onLoginHttpFailed();
			// }
			// else
			// {
			if(!this._httpLoginSended.isLocking())
			{ 
				this._httpLoginSended.lockOn();
				ClientLoginHttpRequest.createClientLogin(CodeCheckRecord.msgDataVersion,GameC.config.getMsgDataVersion(),this._loginData).send();
			}
			// }
		}
	
		private onNetChanged():void
		{
			// //离线游戏中
			// if(GameC.player.system.inited() && !GameC.player.system.isOnline())
			// {
			// 	//网络恢复
			// 	if(SystemControl.isNetOpen())
			// 	{
			// 		Ctrl.print("开始尝试登录服务器");
			// 		this.stepLoginHttp();
			// 	}
			// 	else
			// 	{
			// 		//停止尝试
			// 		this._httpLogining=false;
			// 	}
			// }
		}
	
		private toConnectGame():void
		{
			//标记运行中
			this._running=true;

			if(!this._connectGaming.isLocking())
			{
				Ctrl.debugLog("开始connectGame",this._gameInfo.host,this._gameInfo.port);
				this._connectGaming.lockOn();

				GameC.server.connect(this._gameInfo.host,this._gameInfo.port);
			}
			else
			{
				Ctrl.debugLog("开始connectGame下");
			}
		}
	
		/** 连接游戏服成功 */
		public connectGameSuccess():void
		{
			if(!this._running)
			return;

			this._connectGaming.unlock();
			Ctrl.debugLog("连接game成功");

	
			if(this._isSwitching)
			{
				this._isSwitching=false;
				PlayerSwitchGameRequest.createPlayerSwitchGame(this._gameInfo.token).send();
			}
			else
			{
				//可以停了
				this._httpLoginAffair.stop();
				var resourceVersion:number= 100000;
				LoginGameRequest.createLoginGame(this._gameInfo.token,CodeCheckRecord.msgDataVersion,GameC.config.getMsgDataVersion(),resourceVersion).send();
			}
		}
	
		/** 连接game失败 */
		public connectGameFailed():void
		{
			if(!this._running)
				return;

			this._connectGaming.unlock();
			Ctrl.log("连接Game失败一次");

			if(!CommonSetting.useOfflineGame || !GameC.player.system.inited())
			{
				Ctrl.log("连接Game失败");

				//需要退出了
				GameC.ui.notice(TextEnum.ServerNotRespond);
			}
		}
	
		/** 回复角色列表 */
		public onRePlayerList(list:SList<PlayerLoginData>,serverBornCode:number):void
		{
			if(!this._running)
				return;

			Ctrl.log("回复角色列表",list.size());

			this._playerList=list;


			var serverChanged:boolean=false;
			if(ShineSetting.openCheck)
			{
				if(this._stepTool.isComplete(this.CheckCreatePlayer))
				{
					Ctrl.errorLog("step不该已完成:CheckCreatePlayer");
				}
			}

			this._stepTool.completeStepAbs(this.ConnectGame);
		}

		protected stepCheckCreatePlayer():void
		{
			if(this._playerList.size()==0)
			{
				Ctrl.log("列表为空需要创建");

				CreatePlayerRequest.createCreatePlayer(this.getCreatePlayerData()).send();
			}
			else
			{
				Ctrl.print("有角色直接登录");

				if(ShineSetting.openCheck)
				{
					if(this._stepTool.isComplete(this.PlayerLogin))
					{
						Ctrl.errorLog("step不该已完成:PlayerLogin");
					}
				}

				this._stepTool.completeStep(this.CheckCreatePlayer);
			}
		}

		/** 创建一个随机名字 */
		public createRandomName():string
		{
			return "name_" + MathUtils.randomInt(1000);
		}

		/** 创建角色数据 */
		protected getCreatePlayerData():CreatePlayerData
		{
			var data:CreatePlayerData=new CreatePlayerData();
			data.name=this.createRandomName();
			data.sex=SexType.Boy;
			data.vocation=VocationType.DefaultVocation;

			return data;
		}

		/** 创角成功 */
		public onCreatePlayerSuccess(pData:PlayerLoginData):void
		{
			Ctrl.print("创建角色成功");
			
			this._playerList.add(pData);

			if(ShineSetting.openCheck)
			{
				if(this._stepTool.isComplete(this.PlayerLogin))
				{
					Ctrl.errorLog("step不该已完成:PlayerLogin");
				}
			}

			this._stepTool.completeStep(this.CheckCreatePlayer);
		}

		/** 选择角色登录 */
		protected stepPlayerLogin():void
		{
			var playerID:number=this._playerList[0].playerID;
			PlayerLoginRequest.createPlayerLogin(playerID).send();
		}

		/** 重建player(isFull:是否完整重建(目前是包括引导)) */
		public reBuildPlayer(isFull:boolean):void
		{
			GameC.player.dispose();
			GameC.player.init();
		}

		/** 初始化player数据(isOnline:是否在线登录,否则是离线登录) */
		public initClient(listData:PlayerListData,isOnline:boolean):void
		{
			var lastOnline:boolean=CommonSetting.useOfflineGame && GameC.player.system.inited();

			//此处不用
			if(lastOnline)
			{
				this.reBuildPlayer(false);
			}

			//读数据
			GameC.player.readListData(listData);

			GameC.player.system.setInited(true);
			GameC.player.system.setOnline(isOnline);

			//读后
			GameC.player.afterReadData();

			GameC.player.beforeLogin();
			if(ShineSetting.openCheck)
			{
				if(this._stepTool.isComplete(this.PreGameStart))
				{
					Ctrl.errorLog("step不该已完成:PlayerLogin");
				}
			}

			this._stepTool.completeStep(this.PlayerLogin);
		}

		/** 连接是否运行中 */
		public isRunning():boolean
		{
			return this._running;
		}

		/** 切换game服 */
		public onSwitchGame(info:ClientLoginServerInfoData):void
		{
			//重新赋值
			this._gameInfo=info;
			this._isSwitching=true;

			GameC.server.close();

			GameC.server.connect(this._gameInfo.host,this._gameInfo.port);
		}

		//enter

		/** 游戏预备 */
		protected stepPreGameStart():void
		{
			Ctrl.print("游戏预备开始");
		}

		/** 检查进入初次场景 */
		public checkEnterFirstScene():void
		{
			if(this._enteredFirstScene)
				return;

			this._enteredFirstScene=true;

			this._stepTool.completeStep(this.PreGameStart);
		}

		/** 是否进入过初次场景 */
		public isEnteredFirstScene():boolean
		{
			return this._enteredFirstScene;
		}

		/** 游戏开始 */
		protected stepGameStart():void
		{
			Ctrl.print("游戏开始");
		}
		
		//--登录方法部分(失败)--//

		/** 标记退出中 */
		public setExit():void
		{
			this._running=false;
		}


	public onLoginHttpSuccess(data:ClientLoginResultData):void
	{

			this._httpLoginSended.unlock();

			//已不在登录中
			if(!this.isHttpLogining())
				return;

			//分区分服的
			if(CommonSetting.areaDivideType==GameAreaDivideType.Split)
			{
				LocalSetting.setLoginURL(data.loginInfo.host,data.loginInfo.port);
				this._loginToken=data.loginInfo.token;
				this.serverList=new SList<AreaClientData>(data.areas.size());

				data.areas.forEach((k,v)=>
				{
					this.serverList.add(v);
				});

	// 			
			this.lastAreaID=data.lastAreaID;
			}
			//自动绑定game
			else if(CommonSetting.areaDivideType==GameAreaDivideType.AutoBindGame)
			{
				this._gameInfo=data.gameInfo;
			}

	// 		//未等待中
			if(!this._waitHotfixForHttpLogin.isLocking())
			{
				this.loginHttpNext();
			}
	// 	}

	}
		private loginHttpNext():void
		{
			//分区分服的
			if(CommonSetting.areaDivideType==GameAreaDivideType.Split)
			{
				//登陆的可以停了
				this._httpLoginAffair.stop();
				this._stepTool.completeStepAbs(this.LoginHttp);
			}
			//自动绑定game
			else if(CommonSetting.areaDivideType==GameAreaDivideType.AutoBindGame)
			{
				//直接完成loginSelect
				this._stepTool.completeStepAbs(this.LoginSelect);
			}
		}
		
			/** 热更完毕继续流程 */
		public hotfixOver():void
		{
			if(this._waitHotfixForHttpLogin.isLocking())
			{
				this._waitHotfixForHttpLogin.unlock();
				this.loginHttpNext();
			}
		}

		/** 客户端登陆失败 */
		public onLoginHttpFailed():void
		{
			this._httpLoginSended.unlock();
			//已不在登录中
			if(!this.isHttpLogining())
				return;

			GameC.ui.notice(TextEnum.ServerNotRespond);
		}

		/** gameSocket断开(过了断线重连) */
		public onGameSocketClosed():void
		{
			if(!this._running)
				return;

			Ctrl.print("onGameSocketClosed");

			if(this.canOfflineMode())
			{
				this.enterOfflineMode();
			}
			else
			{
				Ctrl.print("连接被关闭");

				GameC.ui.alert(TextEnum.SocketClosed,Func.create0(()=>{this.backToLogin()}));
			}

		}

		/** 是否可进入离线模式 */
		public canOfflineMode():boolean
		{
			return CommonSetting.useOfflineGame && GameC.player.system.inited();
		}

		/** 尝试进入离线模式 */
		public tryEnterOfflineMode():void
		{
			//在线游戏中
			if(this.canOfflineMode())
			{
				//关了连接,直接进入离线状态
				GameC.server.close();
				this.enterOfflineMode();
			}
		}

		/** 进入离线模式 */
		public enterOfflineMode():void
		{
			if(!CommonSetting.useOfflineGame)
				return;

			Ctrl.print("进入离线模式");
			GameC.player.system.setOnline(false);

			this.beginLoginHttp();
		}
	}
}