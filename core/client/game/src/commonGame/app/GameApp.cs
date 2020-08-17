using ShineEngine;
using UnityEngine;

/// <summary>
/// 逻辑工程入口
/// </summary>
[Hotfix]
public class GameApp:App
{
	private bool _configInitedForEditor=false;

	public GameApp()
	{
		GameC.app=this;
	}

	protected override void preInit()
	{
		initSetting();
	}

	/** 初始化设置组 */
	protected virtual void initSetting()
	{

	}

	/** 构造必需的control组 */
	protected override void makeControls()
	{
		base.makeControls();

		GameFactoryControl factory;
		GameC.factory=factory=createGameFactoryControl();

		//注册数据
		factory.createDataRegister().regist();

		BaseC.config=factory.createConfigControl();
		BaseC.config.preInit();
		BaseC.logic=factory.createBaseLogicControl();
		BaseC.constlist=factory.createBaseConstControl();

		GameC.scene=factory.createSceneControl();
		GameC.player=factory.createPlayer();
		GameC.server=factory.createGameServer();
		if(CommonSetting.useSceneServer)
		{
			GameC.sceneServer=factory.createSceneServer();
		}
		GameC.keyboard=factory.createKeyboardControl();
		GameC.playerSave=factory.createPlayerSaveControl();

		GameC.main=factory.createGameMainControl();
		GameC.touch=factory.createTouchControl();
		GameC.ui=factory.createGameUIControl();
		GameC.info=factory.createInfoControl();
		GameC.redPoint=factory.createRedPointControl();
		GameC.offline=factory.createGameOfflineControl();
		GameC.playerVersion=factory.createPlayerVersionControl();
		GameC.log=factory.createGameLogControl();
		GameC.pool=factory.createGamePoolControl();
		GameC.clientGm=factory.createClientGMControl();
		GameC.trigger=factory.createTriggerControl();
		GameC.guide=factory.createGuideControl();

	}

	protected override void onStart()
	{
		base.onStart();

		GameC.trigger.init();

		//角色存储
		GameC.playerSave.init();
		//角色版本
		GameC.playerVersion.init();
		
		//引导构造
		GameC.guide.construct();
		//角色构造
		GameC.player.preConstruct();

		if(!CommonSetting.isSingleGame)
		{
			//server
			GameC.server.init();

			if(CommonSetting.useSceneServer)
				GameC.sceneServer.init();
		}
		else
		{
			GameC.server.initMessage();

			if(CommonSetting.useSceneServer)
				GameC.sceneServer.initMessage();
		}

		//日志初始化
		GameC.log.init();
		//客户端gm
		GameC.clientGm.init();
		//登录初始化
		GameC.main.init();

		//场景初始化
		GameC.scene.init();
		//离线游戏
		GameC.offline.init();

		//显示部分
		if(ShineSetting.isWholeClient)
		{
			//键盘控制
			GameC.keyboard.init();
			//触摸控制
			GameC.touch.init();
			//ui的实际初始化延后
			GameC.ui.init();
		}

		//开始登陆
		GameC.main.start();
	}

	protected override void onStartForEditor()
	{
		GameC.trigger.init();
	}

	/** 配置表初始化好时刻 */
	public void onConfigInitOver()
	{
		//角色初始化
		GameC.player.construct();
		GameC.player.init();

		//池化
		GameC.pool.init();
		//逻辑初始化
		BaseC.logic.init();

		//显示部分
		if(ShineSetting.isWholeClient)
		{
			//初始化主摄像机配置
			GameC.touch.initByConfig();
			//构造UI组
			GameC.ui.makeUIs();
		}

		//引导部分构建
		GameC.guide.init();
	}

	/** 初始化配置for Editor */
	public void initConfigForEditor()
	{
		if(!ShineSetting.isEditor)
			return;

		if(_configInitedForEditor)
			return;

		_configInitedForEditor=true;

		long now=Ctrl.getTimer();

		//TODO:暂时不要bundleInfo了
		// ResourceInfoControl.loadBundleInfoSync();
		// //同步加载配置
		BaseC.config.loadSyncForEditor();

		Ctrl.print("editor加载配置完成,耗时:",Ctrl.getTimer()-now);
	}

	/** 游戏工厂控制 */
	protected virtual GameFactoryControl createGameFactoryControl()
	{
		return new GameFactoryControl();
	}
}