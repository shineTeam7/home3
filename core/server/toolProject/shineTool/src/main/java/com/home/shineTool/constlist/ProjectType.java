package com.home.shineTool.constlist;

import com.home.shineTool.global.ShineToolSetting;

/** 工程类型(main,common,game) */
public class ProjectType
{
	/** common工程 */
	public static final int Common=1;
	/** game工程 */
	public static final int Game=2;
	/** 热更工程 */
	public static final int HotFix=3;
	
	public static int getLastProject()
	{
		if(ShineToolSetting.needHotfix)
			return HotFix;
		else
			return Game;
	}
	
	/** 是否是叶节点的工程 */
	public static boolean isLastProject(int type)
	{
		return type==getLastProject();
	}
	
	/** 获取工程前缀 */
	public static String getProjectFront(int type)
	{
		switch(type)
		{
			case ProjectType.Common:
				return "";
			case ProjectType.Game:
				return "G";
			case ProjectType.HotFix:
				return "H";
		}
		
		return "";
	}
	
	/** 获取某类的枚举类名 */
	public static String getEnumQName(String clsName,int projectType)
	{
		switch(projectType)
		{
			case ProjectType.Common:
				return  ShineToolSetting.globalPackage+"commonBase.constlist.generate."+clsName;
			case ProjectType.Game:
				return  ShineToolSetting.globalPackage+"base.constlist.generate.G"+clsName;
			case ProjectType.HotFix:
				return  ShineToolSetting.globalPackage+"base.constlist.generate.H"+clsName;
		}
		
		return "";
	}
}
