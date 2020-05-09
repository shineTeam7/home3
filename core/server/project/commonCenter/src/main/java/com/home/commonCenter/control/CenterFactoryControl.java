package com.home.commonCenter.control;

import com.home.commonBase.control.FactoryControl;
import com.home.commonBase.tool.DataRegister;
import com.home.commonCenter.part.centerGlobal.CenterGlobal;
import com.home.commonCenter.server.CenterServer;
import com.home.commonCenter.tool.CenterDataRegister;
import com.home.commonCenter.tool.func.CenterUnionTool;
import com.home.shine.control.WatchControl;

/** 中心服工厂 */
public class CenterFactoryControl extends FactoryControl
{
	//--控制组--//
	
	@Override
	public DataRegister createDataRegister()
	{
		return new CenterDataRegister();
	}
	
	@Override
	public WatchControl createWatchControl()
	{
		return new CenterWatchControl();
	}
	
	/** db */
	public CenterDBControl createDBControl()
	{
		return new CenterDBControl();
	}
	
	/** main */
	public CenterMainControl createMainControl()
	{
		return new CenterMainControl();
	}
	
	/** Server */
	public CenterServer createServer()
	{
		return new CenterServer();
	}
	
	/** global */
	public CenterGlobal createGlobal()
	{
		return new CenterGlobal();
	}
	
	/** unit */
	public CenterSceneControl createScene()
	{
		return new CenterSceneControl();
	}
	
	/** clientGM */
	public CenterClientGMControl createClientGMControl()
	{
		return new CenterClientGMControl();
	}
	
	/** 版本控制 */
	public CenterVersionControl createVersionControl()
	{
		return new CenterVersionControl();
	}
	
	/** work控制 */
	public CenterWorkControl createCenterWorkControl()
	{
		return new CenterWorkControl();
	}
	
	/** 中心服逻辑控制 */
	public CenterLogicControl createCenterLogicControl()
	{
		return new CenterLogicControl();
	}
	
	//--逻辑组--//
	
	/** 中心服工会工具 */
	public CenterUnionTool createCenterUnionTool()
	{
		return new CenterUnionTool();
	}
	
}
