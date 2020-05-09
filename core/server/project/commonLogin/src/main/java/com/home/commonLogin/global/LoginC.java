package com.home.commonLogin.global;

import com.home.commonLogin.app.LoginApp;
import com.home.commonLogin.control.LoginDBControl;
import com.home.commonLogin.control.LoginFactoryControl;
import com.home.commonLogin.control.LoginMainControl;
import com.home.commonLogin.control.UserWorkControl;
import com.home.commonLogin.server.LoginServer;

/** common登录服控制组 */
public class LoginC
{
	/** app */
	public static LoginApp app;
	/** 工厂控制 */
	public static LoginFactoryControl factory;
	/** db控制 */
	public static LoginDBControl db;
	/** 主控制 */
	public static LoginMainControl main;
	/** server */
	public static LoginServer server;
	/** 角色事务 */
	public static UserWorkControl userWork;
}
