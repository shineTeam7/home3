package com.home.shineTool;

import com.home.shine.global.ShineSetting;
import com.home.shineTool.global.ShineToolGlobal;
import com.home.shineTool.global.ShineToolSetting;

/** 引擎工具启动项 */
public class ShineToolSetup
{
	/** 工具初始化项 */
	public static void init()
	{
		ShineToolSetting.init();
		
		//shine设置部分
		
		ShineSetting.needError=true;
		ShineSetting.logNeedConsole=true;
		//debug下需要断点,所以以下两项关闭
		ShineSetting.needThreadWatch=false;
		ShineSetting.needPingCut=false;
		ShineSetting.needLogLineNumber=false;
		
		if(ShineToolGlobal.isRelease)
		{
			//报错退出
			ShineSetting.needExitAfterError=true;
		}
	}
}
