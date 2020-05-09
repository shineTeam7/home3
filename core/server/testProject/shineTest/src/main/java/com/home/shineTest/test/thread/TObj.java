package com.home.shineTest.test.thread;

public class TObj
{
	private static final TObj _instance=new TObj();
	
	public static TObj instance()
	{
		return _instance;
	}
	
	public int a=0;
}
