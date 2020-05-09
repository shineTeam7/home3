namespace Shine
{
	export class SceneControl
	{
	    /** 场景 */
		protected _scene:Scene;
	
		/** 是否切换场景中 */
		private _isSwitching:boolean=false;
	
		/** 是否还有下一个场景 */
		private  _hasNextScene:boolean;
	
		/** 预进入场景ID(也做是否有下个场景的标记) */
		private _preSceneID:number=-1;
		/** 预进入场景线ID */
		private _preLineID:number=-1;
		/** 预进入信息 */
		private _preInfo:ScenePreInfoData;
		/** 是否已创建了下个场景(切换阶段用) */
		private _createdNextScene:boolean=false;
	
		/** 进入数据 */
		protected _enterData:SceneEnterData;
	
		/** 此次切换场景方式 */
		private _switchType:number=SceneSwitchType.Abs;
		/** 是否可不加载资源直接切换场景 */
		private _isSwitchCross:boolean=false;
	
		/** 帧事件控制索引 */
		private _frameIndex:number;
	
		constructor()
		{
	
		}
	
		public init()
		{
			this._frameIndex=TimeDriver.instance.setFrame(Func.create(this,this.onFrame));
	
			//默认直接去root场景
			this.returnToRootScene();
		}
	
		public dispose():void
		{
			TimeDriver.instance.clearFrame(this._frameIndex);
		}
	
		public onReloadConfig():void
		{
			if(this._scene!=null)
			{
				this._scene.onReloadConfig();
			}
		}
	
		protected toCreateScenePreInfoData():ScenePreInfoData
		{
			return new ScenePreInfoData();
		}
	
		/** 返回root场景 */
		public returnToRootScene():void
		{
			//没有主城
			if(!CommonSetting.hasTown)
			{
				// SceneManager.LoadSceneAsync(ShineSetting.rootSceneName);
			}
		}
	
		public getScene():Scene
		{
			return this._scene;
		}
	
		/** 清空当前场景(needClearSource:是否需要清资源) */
		private clearNowScene( needClearSource:boolean):void
		{
			if(this._scene!=null)
			{
				try
				{
					this._scene.dispose();
				}
				catch(e)
				{
					Ctrl.throwError(e);
				}
	
				if(needClearSource)
				{
					// this._scene.load.disposeSource();
				}
	
				this._scene=null;
			}
		}
	
		// /** 清除预进入场景标记 */
		// private clearSceneMark():void
		// {
		// 	this._createdNextScene=false;
		// 	this._preSceneID=-1;
		// 	this._preLineID=-1;
		// }
	
		// /** 请求进入场景 */
		// public applyEnterScene( id:number, lineID:number=-1, posID:number=-1):void
		// {
		// 	if(this._isSwitching)
		// 		return;
	
		// 	var config:SceneConfig=SceneConfig.get(id);
	
		// 	//客户端单人副本
		// 	if(config.instanceType==SceneInstanceType.ClientDriveSinglePlayerBattle)
		// 	{
		// 		this.preEnterScene(id,-1);
		// 	}
		// 	else
		// 	{
		// 		ApplyEnterSceneRequest.create(id,lineID,posID).send();
		// 	}
		// }
	
		// /** 申请离开当前场景 */
		// public applyLeaveNowScene():void
		// {
		// 	if(this._scene==null)
		// 		return;
	
		// 	//客户端场景
		// 	if(this._scene.isDriveAll())
		// 	{
		// 		//直接移除
		// 		this._scene.removeScene();
		// 	}
		// 	else
		// 	{
		// 		ApplyLeaveSceneRequest.create().send();
		// 	}
		// }
	
		// /** 离开当前场景 */
		// public leaveScene(hasNext:boolean):void
		// {
		// 	Ctrl.print("离开当前场景",hasNext);
	
		// 	this.onLeaveScene(hasNext);
		// }
	
		/** 离开当前场景绝对(不包含UI部分)(回登录口用) */
		public leaveSceneAbs():void
		{
			// this.clearSceneMark();
			// this._isSwitching=true;
	
			// if(this._scene!=null)
			// {
			// 	this._scene.preRemove();
			// 	this.clearNowScene(true);
			//     this.returnToRootScene();
			// }

			
		}
	
		// //中断切换场景
		// private cancelSwitch():void
		// {
	
		// }
	
		// /** 预备进入场景(来自服务器推送) */
		// public preEnterScene(sceneID:number,lineID:number):void
		// {
		// 	this._preSceneID=sceneID;
		// 	this._preLineID=lineID;
		// 	this._createdNextScene=false;
		// 	this._preInfo=null;
		// 	this._isSwitching=true;
		// 	this._isSwitchCross=false;
	
		// 	var nextConfig:SceneConfig=SceneConfig.get(sceneID);
	
		// 	this._switchType=nextConfig.switchType;
	
		// 	if(this._scene!=null)
		// 	{
		// 		//是否跳过加载资源
		// 		if(this.isSkipLoadSource(nextConfig))
		// 		{
		// 			this._isSwitchCross=true;
		// 		}
		// 	}
	
		// 	this.removeNowScene();
		// }
	
		// /** 检查资源是否相同 */
		// protected isSkipLoadSource(nextConfig:SceneConfig):boolean
		// {
		// 	return this._scene.getConfig().sourceT == nextConfig.sourceT;
		// }
	
		// /** 预进入场景第二阶段 */
		// public preEnterSceneNext(info:ScenePreInfoData):void
		// {
		// 	if(this._createdNextScene)
		// 	{
		// 		this._scene.setPreInfo(info);
	
		// 		//不是无切换过程
		// 		if(!this._isSwitchCross)
		// 		{
		// 			this._scene.load.loadNext(info);
		// 		}
		// 	}
		// 	else
		// 	{
		// 		this._preInfo=info;
		// 	}
		// }
	
		// /** 移除当前场景 */
		// protected removeNowScene():void
		// {
		// 	if(this._scene!=null)
		// 	{
		// 		this._scene.preRemove();
	
		// 		GameC.player.dispatch(GameEventType.LeaveScene,this._scene.getConfig().id);
		// 	}
	
		// 	this.onRemoveNowScene();
	
		// 	this.doRemoveSwitch();
		// }
	
		// /** 移除当前场景接口 */
		// protected onRemoveNowScene():void
		// {
		// 	GameC.ui.hideUIByRemoveScene();
		// }
	
		// /** 执行remove切换 */
		// protected doRemoveSwitch():void
		// {
		// 	switch(this._switchType)
		// 	{
		// 		case SceneSwitchType.Abs:
		// 		{
		// 			this.removeNowSceneOver();
		// 		}
		// 			break;
		// 		case SceneSwitchType.Fade:
		// 		{
		// 			//TODO:实现淡入
	
		// 			TimeDriver.instance.setTimeOut(removeNowSceneOver,1000);
		// 		}
		// 			break;
		// 		default:
		// 			break;
		// 	}
		// }
	
		// protected removeNowSceneOver():void
		// {
		// 	this.onRemoveNowSceneOver();
	
		// 	if(!this._isSwitchCross)
		// 	{
		// 		this.clearNowScene(true);
		// 	}
	
		// 	//有下个场景
		// 	if(this._preSceneID>0)
		// 	{
		// 		//创建场景
		// 		//客户端场景不做池了
	
		// 		var config:SceneConfig=SceneConfig.get(this._preSceneID);
	
		// 		this._scene=GameC.factory.createScene();
		// 		this._scene.setType(config.type);
		// 		this._scene.construct();
	
		// 		//初始化场景ID
		// 		this._scene.initSceneID(this._preSceneID);
	
		// 		//设置预进入数据
		// 		if(this._preInfo!=null)
		// 		{
		// 			this._scene.setPreInfo(this._preInfo);
		// 		}
	
		// 		//标记创建了下个场景
		// 		this._createdNextScene=true;
	
		// 		if(this._isSwitchCross)
		// 		{
		// 			this.loadSceneResourceOver();
		// 		}
		// 		else
		// 		{
		// 			this._scene.load.startLoad(loadSceneResourceOver);
		// 		}
	
		// 		//客户端单人副本
		// 		if(config.instanceType==SceneInstanceType.ClientDriveSinglePlayerBattle)
		// 		{
		// 			SignedSceneInOutLogic signedInOutLogic=_scene.getSignedInOutLogic();
	
		// 			//有进出逻辑
		// 			if(signedInOutLogic!=null)
		// 			{
		// 				ScenePreInfoData preInfo=toCreateScenePreInfoData();
		// 				this._scene.getSignedInOutLogic().beforePreEnter();
		// 				this._scene.getSignedInOutLogic().makePreInfo(preInfo);
		// 				this.preEnterSceneNext(preInfo);
		// 			}
		// 			//没有进出逻辑
		// 			else
		// 			{
		// 				this.preEnterSceneNext(null);
		// 			}
		// 		}
		// 	}
		// 	else
		// 	{
		// 		this.leaveSceneOver();
		// 	}
		// }
	
		// protected onRemoveNowSceneOver():void
		// {
		// 	GameC.ui.hideUIByRemoveSceneOver();
		// }
	
		// /** 离开场景结束(返回无场景状态) */
		// protected leaveSceneOver():void
		// {
		// 	this.returnToRootScene();
	
		// 	this._isSwitching=false;
	
		// 	this.onLeaveSceneOver();
	
		// 	GameC.player.dispatch(GameEventType.LeaveLastSceneOver);
		// }
	
		// protected onLeaveSceneOver():void
		// {
		// 	GameC.ui.showUIByLeaveSceneOver();
		// }
	
		// protected loadSceneResourceOver():void
		// {
		// 	//客户端单人副本
		// 	if(SceneConfig.get(this._preSceneID).instanceType==SceneInstanceType.ClientDriveSinglePlayerBattle)
		// 	{
		// 		this.toEnterClientScene();
		// 	}
		// 	else
		// 	{
		// 		//推送到服务器
		// 		PreEnterSceneReadyRequest.create().send();
		// 	}
		// }
	
		// private makeClientHeroData(uData:UnitData):void
		// {
		// 	//给予一个流水
		// 	uData.instanceID=this._scene.getUnitInstanceID();
	
		// 	if(!this._scene.isSimple())
		// 	{
		// 		this._scene.play.makeScenePosData(uData);
		// 	}
	
		// 	this._scene.play.makeCharacterData(uData);
		// }
	
		// /** 执行进入空场景 */
		// public doEnterNoneScene():void
		// {
		// 	//离线游戏中
		// 	if(CommonSetting.useOfflineGame && GameC.main.isEnteredFirstScene())
		// 	{
		// 		//不处理
		// 	}
		// 	else
		// 	{
		// 		this.onEnterNoneScene();
		// 	}
		// }
	
		// /** 进入空场景(初始化阶段用,显示) */
		// public onEnterNoneScene():void
		// {
		// 	this.leaveSceneOver();
	
		// 	GameC.main.checkEnterFirstScene();
		// }
	
		// /** 进入客户端场景 */
		// private toEnterClientScene():void
		// {
		// 	var enterData:SceneEnterData=this._scene.play.createSceneEnterData();
	
		// 	var signedInOutLogic:SignedSceneInOutLogic=this._scene.getSignedInOutLogic();
	
		// 	//有进出逻辑
		// 	if(signedInOutLogic!=null)
		// 	{
		// 		var heroData:UnitData=signedInOutLogic.createCharacterDataSelf();
	
		// 		enterData.hero=heroData;
		// 		enterData.units=new SList<UnitData>();
		// 	}
	
		// 	this._scene.play.makeSceneEnterData(enterData);
	
		// 	this.enterScene(enterData);
		// }
	
		// /** 服务器推送进入场景 */
		// public enterScene(enterData:SceneEnterData):void
		// {
		// 	Ctrl.print("进入场景");
	
		// 	this._enterData=enterData;
	
		// 	//TODO:这里可以再补一次资源存在性校验
	
		// 	//初始化
		// 	this._scene.init();
	
		// 	//初始化进入数据
		// 	this._scene.initEnterData(this._enterData);
	
		// 	GameC.player.dispatch(GameEventType.EnterScene,this._scene.getConfig().id);
	
		// 	this.onEnterScene();
	
		// 	this.doAddSwitch();
		// }
	
		// protected onEnterScene():void
		// {
		// 	GameC.ui.showUIByEnterScene();
		// 	GameC.main.checkEnterFirstScene();
		// }
	
		// /** 执行add切换 */
		// protected doAddSwitch():void
		// {
		// 	switch(this._switchType)
		// 	{
		// 		case SceneSwitchType.Abs:
		// 		{
		// 			this.addSceneOver();
		// 		}
		// 			break;
		// 		case SceneSwitchType.Fade:
		// 		{
		// 			//TODO:实现淡出
		// 			TimeDriver.instance.setTimeOut(this.addSceneOver,1000);
		// 		}
		// 			break;
		// 		default:
		// 			break;
		// 	}
		// }
	
		// /** add场景完毕 */
		// protected addSceneOver():void
		// {
		// 	this._isSwitching=false;
		// 	this._isSwitchCross=false;
		// 	this._switchType=SceneSwitchType.Abs;
	
		// 	GameC.ui.showUIByEnterSceneOver();
		// }
	
		// /** 进入场景失败 */
		// public onEnterSceneFaild():void
		// {
		// 	//TODO:进入场景失败的处理,等几秒再进
	
		// }
	
		// /** 离开场景消息 */
		// public onLeaveScene( hasNextScene:boolean):void
		// {
		// 	//没有了才做处理
		// 	if(!hasNextScene)
		// 	{
		// 		this.clearSceneMark();
	
		// 		if(this._scene!=null)
		// 		{
		// 			this._isSwitching=true;
	
		// 			this._switchType=this._scene.getConfig().switchType;
	
		// 			this.removeNowScene();
		// 		}
		// 	}
		// }
	
		private onFrame(delay:number):void
		{
			// if(this._scene!=null && this._scene.isInit)
			// {
			// 	this._scene.onFrame(delay);
			// }
		}
	
		// private onFixedUpdate()
		// {
		// 	if(this._scene!=null && this._scene.isInit)
		// 	{
		// 		this._scene.onFixedUpdate();
		// 	}
		// }
	
		// //match
	
		// /** 匹配成功 */
		// public onMatchSuccess( funcID:number, index:number,matcheDatas:PlayerMatchData[] ):void
		// {
		// 	//TODO:匹配成功的显示
	
		// 	//TODO:等上1秒
	
		// 	TimeDriver.instance.setTimeOut(()=>
		// 	{
		// 		//直接接受
		// 		FuncAcceptMatchRequest.create(funcID,index).send();
		// 	},1000);
		// }
	
		// /** 重新加入匹配 */
		// public onReAddMatch(funcID:number):void
		// {
	
		// }
	
		// /** 匹配超时 */
		// public onMatchTimeOut(funcID:number):void
		// {
		// 	//处理超时逻辑
		// }
	}
}