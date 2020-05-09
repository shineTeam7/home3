package com.home.commonManager.global;

import com.home.commonManager.app.ManagerApp;
import com.home.commonManager.control.ManagerLogicControl;
import com.home.commonManager.control.ManagerDBControl;
import com.home.commonManager.control.ManagerFactoryControl;
import com.home.commonManager.control.ManagerMainControl;
import com.home.commonManager.control.ManagerSettingControl;
import com.home.commonManager.server.ManagerServer;

/** 管理单例 */
public class ManagerC
{
	/** app */
	public static ManagerApp app;
	/** 工厂 */
	public static ManagerFactoryControl factory;
	/** 主控制 */
	public static ManagerMainControl main;
	/** db */
	public static ManagerDBControl db;
	/** server */
	public static ManagerServer server;
	/** 逻辑部分 */
	public static ManagerLogicControl logic;
	/** 配置中心 */
	public static ManagerSettingControl setting;
}
