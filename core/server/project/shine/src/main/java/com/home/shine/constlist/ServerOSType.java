package com.home.shine.constlist;

/** 服务器操作系统类型 */
public class ServerOSType
{
	public static final int Windows=1;
	
	public static final int Linux=2;
	
	public static final int Mac=3;
	
	public static int getType(String osName)
	{
		String s=osName.toLowerCase();
		
		if(s.contains("windows"))
			return Windows;
		
		if(s.contains("mac"))
			return Mac;
		
		return Linux;
	}
	
	public static String getLibFolder(int type)
	{
		switch(type)
		{
			case Windows:
				return "windows";
			case Linux:
				return "linux";
			case Mac:
				return "mac";
		}
		
		return "";
	}
	
	public static String getLibName(String name,int type)
	{
		switch(type)
		{
			case Windows:
				return name+".dll";
			case Linux:
				return "lib"+name+".so";
			case Mac:
				return "lib"+name+".dylib";
		}
		
		return "";
	}
}
