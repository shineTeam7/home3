package com.home.commonGMClient.app;

import com.home.commonBase.app.App;
import com.home.commonGMClient.control.GMClientFactoryControl;
import com.home.commonGMClient.global.GMClientC;
import com.home.shine.serverConfig.ServerConfig;

/** gm客户端app */
public class GMClientApp extends App
{
	public GMClientApp()
	{
		super("gmClient",0);
		
		GMClientC.app=this;
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		
		//服务器配置
		ServerConfig.init();
		
		GMClientC.main.init();
		
		//启动完毕
		startOver();
	}
	
	@Override
	protected void makeControls()
	{
		super.makeControls();
		
		GMClientFactoryControl factory;
		_factory=factory=GMClientC.factory=createFactoryControl();
		
		GMClientC.main=factory.createMainControl();
	}
	
	@Override
	protected GMClientFactoryControl createFactoryControl()
	{
		return new GMClientFactoryControl();
	}
}
