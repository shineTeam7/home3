package com.home.shineTool.constlist;

import com.home.shine.ctrl.Ctrl;

/** 代码类型 */
public class CodeType
{
	/** as3 */
	public static final int AS3=1;
	/** java */
	public static final int Java=2;
	/** C# */
	public static final int CS=3;
	/** TS */
	public static final int TS=4;
	//	/** lua */
	//	public static final int Lua=5;
	
	/** 获取扩展名 */
	public static String getExName(int type)
	{
		String re="";
		
		switch(type)
		{
			case AS3:
			{
				re="as";
			}
				break;
			case Java:
			{
				re="java";
			}
				break;
			case CS:
			{
				re="cs";
			}
				break;
			case TS:
			{
				re="ts";
			}
				break;
			default:
			{
				Ctrl.throwError("不支持的语言",type);
			}
				break;
		}
		
		return re;
	}
	
	/** 通过扩展名获取类型 */
	public static int getTypeByExName(String exName)
	{
		int re=0;
		
		switch(exName)
		{
			case "as":
			{
				re=AS3;
			}
				break;
			case "java":
			{
				re=Java;
			}
				break;
			case "cs":
			{
				re=CS;
			}
				break;
			case "ts":
			{
				re=TS;
			}
				break;
			default:
			{
				Ctrl.throwError("不支持的语言",exName);
			}
				break;
		}
		
		return re;
	}
	
	/** 接口方法是否需要加override */
	public static boolean isInterfaceMethodNeedOverride(int type)
	{
		if(type==CS)
		{
			return false;
		}
		
		return true;
	}
	
	/** 是否能获取到class类 */
	public static boolean canGetClass(int type)
	{
		switch(type)
		{
			case Java:
			case CS:
				return true;
		}
		
		return true;
	}
	
	/** 使用S系列集合 */
	public static boolean useSCollection(int type)
	{
		switch(type)
		{
			case Java:
			case CS:
				return true;
		}
		
		return true;
	}
}
