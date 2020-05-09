package com.home.commonCenter.global;

import com.home.commonCenter.app.CenterApp;
import com.home.commonCenter.control.CenterLogicControl;
import com.home.commonCenter.control.CenterWorkControl;
import com.home.commonCenter.part.centerGlobal.CenterGlobal;
import com.home.commonCenter.control.CenterClientGMControl;
import com.home.commonCenter.control.CenterDBControl;
import com.home.commonCenter.control.CenterFactoryControl;
import com.home.commonCenter.control.CenterMainControl;
import com.home.commonCenter.control.CenterSceneControl;
import com.home.commonCenter.control.CenterVersionControl;
import com.home.commonCenter.server.CenterServer;

/** common中心服控制组(顺序不能乱,影响初始化顺序) */
public class CenterC
{
	/** app */
	public static CenterApp app;
	/** 工厂控制 */
	public static CenterFactoryControl factory;
	/** 版本控制 */
	public static CenterVersionControl version;
	/** db控制 */
	public static CenterDBControl db;
	/** 主控制 */
	public static CenterMainControl main;
	/** server */
	public static CenterServer server;
	/** 全局数据 */
	public static CenterGlobal global;
	/** 场景控制 */
	public static CenterSceneControl scene;
	/** 客户端GM指令控制 */
	public static CenterClientGMControl clientGM;
	/** 中心服事务 */
	public static CenterWorkControl centerWork;
	/** 中心服逻辑控制 */
	public static CenterLogicControl logic;
}
