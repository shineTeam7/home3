package com.home.commonScene.app;

import com.home.commonBase.app.BaseGameApp;
import com.home.commonBase.extern.ExternMethod;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.global.CommonSetting;
import com.home.commonScene.control.SceneFactoryControl;
import com.home.commonScene.global.SceneC;
import com.home.shine.control.BytesControl;
import com.home.shine.control.WatchControl;
import com.home.shine.global.ShineSetting;

public class SceneApp extends BaseGameApp
{
	public SceneApp(int id)
	{
		super("scene",id);
		
		SceneC.app=this;
	}
	
	@Override
	protected void initBaseFactory()
	{
		if(BaseC.factory==null || CommonSetting.useSceneServer)
			BaseC.factory=createBaseFactoryControl();
	}
	
	@Override
	public void start()
	{
		//不启用就停了
		if(!CommonSetting.useSceneServer)
			return;
		
		super.start();
	}
	
	/** 工厂控制 */
	@Override
	protected SceneFactoryControl createFactoryControl()
	{
		return new SceneFactoryControl();
	}
	
	@Override
	protected void onStartNext()
	{
		super.onStartNext();
		
		BaseC.config.initHotfix();
		
		if(CommonSetting.needExternLib)
		{
			loadLib();
		}
		
		SceneC.main.init();
		SceneC.clientGM.init();
		
		//启动完毕
		startOver();
	}
	
	@Override
	protected void makeControls()
	{
		super.makeControls();
		
		SceneFactoryControl factory;
		_factory=factory=SceneC.factory=createFactoryControl();
		_server=SceneC.server=factory.createServer();
		SceneC.main=factory.createMainControl();
		SceneC.clientGM=factory.createClientGMControl();
	}
	
	protected void loadLib()
	{
		//加载lib
		ExternMethod.init();
	}
	
	@Override
	protected void onExit()
	{
		//关了客户端入口
		SceneC.server.setClientReady(false);
		
		//TODO:等待玩家退出完
		//checkExitNext();
		SceneC.main.clearAllPlayer(this::onClearPlayerOver);
	}
	
	private void onClearPlayerOver()
	{
		doExitNext();
	}
	
	private void doExitNext()
	{
		//关server
		SceneC.server.dispose();
		
		SceneC.main.dispose();
		
		exitOver();
	}
}
