package com.home.commonManager.app;

import com.home.commonManager.control.ManagerFactoryControl;
import com.home.commonManager.global.ManagerC;
import com.home.commonBase.app.App;
import com.home.shine.serverConfig.ServerConfig;

/** 管理App */
public class ManagerApp extends App
{
	public ManagerApp()
	{
		super("manager",1);
		
		ManagerC.app=this;
	}
	
	@Override
	protected void messageRegist()
	{
		super.messageRegist();
		
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		
		//配置
		ManagerC.setting.init();
		//服务器配置
		ServerConfig.init();
		
		//数据库
		ManagerC.db.init();
		//main
		ManagerC.main.init();
		//server
		ManagerC.server.init();
		//激活码
		ManagerC.logic.init();
		
		//启动完毕
		startOver();
	}
	
	/** 构造必需的control组 */
	protected void makeControls()
	{
		super.makeControls();
		
		ManagerFactoryControl factory;
		_factory=factory=ManagerC.factory=createFactoryControl();
		
		ManagerC.setting=factory.createManagerSettingControl();
		ManagerC.db=factory.createDBControl();
		ManagerC.main=factory.createMainControl();
		ManagerC.server=factory.createServer();
		ManagerC.logic=factory.createManagerLogicControl();
	}
	
	@Override
	protected void onExit()
	{
		//按顺序析构
		ManagerC.server.dispose();
		
		ManagerC.main.dispose();
		
		ManagerC.db.dispose();
		
		exitOver();
	}
	
	/** 创建game工厂 */
	protected ManagerFactoryControl createFactoryControl()
	{
		return new ManagerFactoryControl();
	}
}
