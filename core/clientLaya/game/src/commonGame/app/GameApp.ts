namespace Shine
{
	/** 应用入口 */
	export class GameApp extends App
	{
		constructor()
		{
			super();

			GameC.app=this;
		}

		public start():void
		{
			this.countIsRelease();
			this.initSetting();

			ShineSetup.setup(Func.create(this,this.onExit));
			super.start();
		}

		/** 初始化设置组 */
		protected initSetting():void
		{
			// AppSetting.init();
		}

		protected countIsRelease():void
		{
			//TODO:分辨出ShineSetting.isRelease
			ShineSetting.isRelease=false;
		}

		/** 构造必需的control组 */
		protected makeControls():void
		{
			super.makeControls();

			var factory:GameFactoryControl;
			GameC.factory=factory=this.createGameFactoryControl();

			//注册数据
			factory.createDataRegister().regist();

			GameC.config=factory.createConfigControl();
			GameC.config.makeConst();

			GameC.logic=factory.createBaseLogicControl();
			GameC.scene=factory.createSceneControl();
			GameC.player=factory.createPlayer();
			GameC.server=factory.createGameServer();
			GameC.keyboard=factory.createKeyboardControl();
			// GameC.playerSave=factory.createPlayerSaveControl();

			GameC.main=factory.createGameMainControl();
			// GameC.touch=factory.createTouchControl();
			GameC.ui=factory.createGameUIControl();
			GameC.info=factory.createInfoControl();
			// GameC.redPoint=factory.createRedPointControl();
			// GameC.offline=factory.createGameOfflineControl();
			// GameC.playerVersion=factory.createPlayerVersionControl();
			// GameC.log=factory.createGameLogControl();
			GameC.pool=factory.createGamePoolControl();
			// GameC.clientGm=factory.createClientGMControl();
		}

		protected onStart():void
		{
			super.onStart();

			//键盘控制
			GameC.keyboard.init();
			//触摸控制
			// GameC.touch.init();
			//角色存储
			// GameC.playerSave.init();
			//角色版本
			// GameC.playerVersion.init();
			//server
			GameC.server.init();
			//角色初始化
			GameC.player.construct();
			GameC.player.init();
			//日志初始化
			// GameC.log.init();
			//客户端gm
			// GameC.clientGm.init();
			//登录初始化
			GameC.main.init();
			//ui的实际初始化延后
			// GameC.ui.init();
			//场景初始化
			GameC.scene.init();
			//离线游戏
			// GameC.offline.init();

			//开始登陆
			// GameC.main.start();
		}

		/** 退出时刻 */
		protected onExit():void
		{

		}

		/** 配置表初始化好时刻 */
		public onConfigInitOver():void
		{
			//池化
			GameC.pool.init();
			//逻辑初始化
			GameC.logic.init();
			//初始化主摄像机配置
			// GameC.touch.initByConfig();
			//构造UI组
			// GameC.ui.makeUIs();
		}

		protected createGameFactoryControl():GameFactoryControl
		{
			return new GameFactoryControl();
		}

	}
}