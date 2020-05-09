package com.home.shineTool.reflect;

import com.home.shineTool.constlist.VisitType;

import java.util.ArrayList;
import java.util.List;

/** 构造函数信息(唯一) */
public class MainMethodInfo extends MethodInfo
{
	/** 基类构造函数传参 */
	public List<String> superArgs=new ArrayList<String>();
	//	/** 除去super的剩余内容(没有super值为null) */
	//	public String lastContent=null;
	
	public MainMethodInfo()
	{
		this.visitType=VisitType.Public;
	}
}
