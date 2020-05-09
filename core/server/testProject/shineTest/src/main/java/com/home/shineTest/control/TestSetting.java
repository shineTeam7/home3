package com.home.shineTest.control;

import com.home.shine.data.DIntData;

public class TestSetting
{
	public static int Len=1000;
	
	public static Object[] objs;
	
	public static DIntData[] datas;
	
	static
	{
		objs=new Object[TestSetting.Len];
		
		for(int i=0;i<objs.length;++i)
		{
			objs[i]=new Object();
		}
		
		datas=new DIntData[TestSetting.Len];
		
		for(int i=0;i<datas.length;++i)
		{
			datas[i]=DIntData.create(i,i);
		}
	}
}
