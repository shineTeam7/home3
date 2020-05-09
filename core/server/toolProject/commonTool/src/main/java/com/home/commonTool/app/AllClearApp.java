package com.home.commonTool.app;

import com.home.shineTool.global.ShineToolSetting;

public class AllClearApp
{
	public static void main(String[] args)
	{
		ShineToolSetting.needTrace=true;
		
		AllToolsApp.main(new String[]{"allClear"});
	}
}
