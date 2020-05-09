package com.home.commonBase.control;

import com.home.commonBase.tool.DataRegister;
import com.home.shine.control.WatchControl;

public class FactoryControl
{
	/** 观测控制 */
	public WatchControl createWatchControl()
	{
		return new WatchControl();
	}
	
	/** 创建data注册机 */
	public DataRegister createDataRegister()
	{
		return new DataRegister();
	}
}
