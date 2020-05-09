package com.home.commonTool.app;

import com.home.commonTool.extern.ToolExternMethodNative;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineGlobal;
import com.home.shineTool.ShineToolSetup;
import com.home.shineTool.global.ShineToolGlobal;

public class ExportNavApp
{
	public static void doMapID(int mapID)
	{
		if(mapID<=0)
		{
			Ctrl.errorLog("mapID 非法");
			return;
		}
		
		ShineGlobal.loadLib();
		
		String configPath=ShineToolGlobal.mapInfoPath+"/" + "navConfig.bin";
		String dataPath=ShineToolGlobal.mapInfoPath+"/" + mapID+"_navData.bin";
		String outPath=ShineToolGlobal.mapInfoPath+"/" + mapID+"_nav.bin";
		
		ToolExternMethodNative.exportNav(configPath,dataPath,outPath);
		
		Ctrl.print("OK");
	}
	
	public static void main(String[] args)
	{
		ShineToolSetup.init();
		doMapID(2);
	}
}
