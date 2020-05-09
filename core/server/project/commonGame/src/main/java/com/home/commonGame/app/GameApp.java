package com.home.commonGame.app;

import com.home.commonBase.app.BaseGameApp;
import com.home.commonBase.data.login.GameInitServerData;
import com.home.commonBase.data.login.GameLoginData;
import com.home.commonBase.extern.ExternMethod;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.global.CommonSetting;
import com.home.commonGame.constlist.generate.GameRequestType;
import com.home.commonGame.constlist.generate.GameResponseType;
import com.home.commonGame.control.GameFactoryControl;
import com.home.commonGame.global.GameC;
import com.home.shine.control.BytesControl;
import com.home.shine.control.WatchControl;
import com.home.shine.global.ShineSetting;

public class GameApp extends BaseGameApp
{
	private GameLoginData _loginData;
	
	private boolean _initNext=false;
	
	private boolean _initLast=false;
	
	public GameApp(int id)
	{
		super("game",id);
		
		GameC.app=this;
	}
	
	@Override
	protected void messageRegist()
	{
		super.messageRegist();
		
		//关注点 基础支持的一些功能协议注册
		BytesControl.addMessageConst(GameRequestType.class,true,false);
		BytesControl.addMessageConst(GameResponseType.class,false,false);
	}
	
	@Override
	protected void onStartNext()
	{
		super.onStartNext();
		
		//逻辑服才需要
		BaseC.config.initHotfix();
		
		if(CommonSetting.needExternLib)
		{
			loadLib();
		}
		
		//version
		GameC.version.init();

		//clientGM注册
		GameC.clientGM.init();
		//离线事务
		GameC.playerWork.init();
		//区服事务
		GameC.areaWork.init();
		//客户端离线模式
		GameC.clientOffline.init();
		
		//beforeServer
		initControlsBeforeServer();
	}
	
	/** 构造必需的control组 */
	protected void makeControls()
	{
		super.makeControls();
		
		GameFactoryControl factory;
		_factory=factory=GameC.factory=createFactoryControl();
		
		//工厂最先
		if(ShineSetting.isAllInOne)
		{
			//再构建一次
			WatchControl.instance=_factory.createWatchControl();
			BaseC.factory=createBaseFactoryControl();
		}
		
		_server=GameC.server=factory.createServer();
		
		GameC.version=factory.createGameVersionControl();
		GameC.db=factory.createDBControl();
		GameC.log=factory.createLogControl();
		GameC.main=factory.createMainControl();
		GameC.gameSwitch=factory.createSwitchControl();
		GameC.scene=factory.createSceneControl();
		GameC.global=factory.createGlobal();
		GameC.clientGM=factory.createClientGMControl();
		GameC.playerWork=factory.createPlayerWorkControl();
		GameC.areaWork=factory.createAreaWorkControl();
		GameC.clientOffline=factory.createClientOfflineControl();
		GameC.gameJoin=factory.createGameJoinControl();
		GameC.logic=factory.createGameLogicControl();
	}
	
	/** 初始化在server前 */
	protected void initControlsBeforeServer()
	{
		
	}
	
	public boolean isInitNext()
	{
		return _initNext;
	}
	
	public boolean isInitLast()
	{
		return _initLast;
	}
	
	protected void loadLib()
	{
		//加载lib
		ExternMethod.init();
	}
	
	/** 取到初始化数据 */
	public void onGetInitData(GameInitServerData initData)
	{
		CommonSetting.isOfficial=initData.isOfficial;
		
		GameC.main.setClientVersion(initData.clientVersion);
		GameC.server.setInfos(initData);
		
		GameC.main.afterSetInfos();
	}
	
	/** 初始化下一阶段(取到center数据后) */
	public void initNext(GameLoginData data)
	{
		_loginData=data;
		
		GameC.clientGM.setCenterCmdSet(data.clientGMSet);
		
		if(_initNext)
		{
			GameC.global.readCenterLoginData(_loginData,false);
			GameC.global.sendLoginToCenter();
			return;
		}
		
		_initNext=true;
		
		//db
		GameC.db.initWithAppID(GameC.app.id);
		//main
		GameC.main.init();
		
		GameC.gameSwitch.init();
		
		GameC.logic.init();
		
		
		//global
		GameC.global.load();
		GameC.global.readCenterLoginData(_loginData,true);
		
		GameC.main.initAfterGlobal();
		
		GameC.db.initLast();
		
		//server后续
		GameC.server.initNext();
	}
	
	/** 初始化最后阶段(start) */
	public void initLast()
	{
		if(_initLast)
			return;
		
		_initLast=true;
		
		GameC.main.initTick();
		
		//unit
		GameC.scene.init();
		
		GameC.server.openClient();
		
		GameC.global.sendLoginToCenter();
		//启动完毕
		startOver();
		
		//开始启动
		GameC.global.onStart();
		afterStartOver();
	}
	
	/** 启动完毕 */
	protected void afterStartOver()
	{
	
	}
	
	@Override
	protected void onExit()
	{
		//关了客户端入口
		GameC.server.setClientReady(false);
		
		GameC.main.clearAllPlayer(this::onClearPlayerOver);
	}
	
	private void onClearPlayerOver()
	{
		doExitNext();
		//ThreadControl.getMainTimeDriver().setTimeOut(this::doExitNext,500);//给各服务器间一点时间
	}
	
	private void doExitNext()
	{
		//关server
		GameC.server.dispose();
		
		GameC.scene.dispose();
		
		GameC.main.dispose();
		
		GameC.db.writeForExit(this::onWriteDBOver);
	}
	
	private void onWriteDBOver()
	{
		//关了DB连接
		GameC.db.dispose();
		
		exitOver();
	}

	/** 创建游戏工厂 */
	protected GameFactoryControl createFactoryControl()
	{
		return new GameFactoryControl();
	}
}
