package com.home.commonLogin.app;

import com.home.commonBase.app.App;
import com.home.commonBase.app.BaseGameApp;
import com.home.commonBase.data.login.LoginInitServerData;
import com.home.commonLogin.control.LoginFactoryControl;
import com.home.commonLogin.global.LoginC;
import com.home.shine.global.ShineSetting;

public class LoginApp extends BaseGameApp
{
	private boolean _initNext=false;
	
	private boolean _initLast=false;
	
	public LoginApp(int id)
	{
		super("login",id);
		
		LoginC.app=this;
	}
	
	@Override
	protected void preInit()
	{
		super.preInit();
		
		if(!ShineSetting.isAllInOne)
		{
			//login自身线程数目修改
			ShineSetting.poolThreadNum=ShineSetting.ioThreadNum=ShineSetting.dbThreadNum=8;
		}
	}
	
	@Override
	protected void onStartNext()
	{
		super.onStartNext();
		
		//login
		LoginC.main.init();
		//user事务
		LoginC.userWork.init();
	}
	
	/** 构造control组 */
	protected void makeControls()
	{
		super.makeControls();
		
		LoginFactoryControl factory;
		_factory=factory=LoginC.factory=createFactoryControl();
		_server=LoginC.server=factory.createServer();
		LoginC.db=factory.createDBControl();
		LoginC.main=factory.createMainControl();
		LoginC.userWork=factory.createUserWorkControl();
	}
	
	@Override
	protected void onExit()
	{
		//按顺序析构
		LoginC.server.dispose();
		
		LoginC.main.dispose();
		
		LoginC.db.dispose();
		
		exitOver();
	}
	
	/** 创建工厂控制 */
	protected LoginFactoryControl createFactoryControl()
	{
		return new LoginFactoryControl();
	}
	
	public boolean isInitNext()
	{
		return _initNext;
	}
	
	public boolean isInitLast()
	{
		return _initLast;
	}
	
	public void initNext()
	{
		if(_initNext)
			return;
		
		_initNext=true;
		
		//db
		LoginC.db.init();
		
		LoginC.main.initNext();
		
		//server后续
		LoginC.server.initNext();
	}
	
	public void initLast()
	{
		if(_initLast)
			return;
		
		_initLast=true;
		
		LoginC.server.openClient();
		
		//启动完毕
		startOver();
	}
}
