package com.home.commonLogin.control;

import com.home.commonBase.control.FactoryControl;
import com.home.commonLogin.server.LoginServer;

/** 登录工厂 */
public class LoginFactoryControl extends FactoryControl
{
	//controls
	
	/** 创建db */
	public LoginDBControl createDBControl()
	{
		return new LoginDBControl();
	}
	
	/** 创建主控制 */
	public LoginMainControl createMainControl()
	{
		return new LoginMainControl();
	}
	
	/** 创建Server */
	public LoginServer createServer()
	{
		return new LoginServer();
	}
	
	/** 创建user事务 */
	public UserWorkControl createUserWorkControl()
	{
		return new UserWorkControl();
	}
}
