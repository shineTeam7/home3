package com.home.commonManager.control;

import com.home.commonBase.control.FactoryControl;
import com.home.commonManager.server.ManagerServer;

public class ManagerFactoryControl extends FactoryControl
{
	/** 主 */
	public ManagerMainControl createMainControl()
	{
		return new ManagerMainControl();
	}
	
	/** 查询db */
	public ManagerDBControl createDBControl()
	{
		return new ManagerDBControl();
	}
	
	/** server */
	public ManagerServer createServer()
	{
		return new ManagerServer();
	}
	
	/** 逻辑部分 */
	public ManagerLogicControl createManagerLogicControl()
	{
		return new ManagerLogicControl();
	}
	
	/** setting控制 */
	public ManagerSettingControl createManagerSettingControl()
	{
		return new ManagerSettingControl();
	}
}
