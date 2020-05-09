package com.home.shineTest.control;

import com.home.shine.ctrl.Ctrl;

public class TestTool
{
	private static int _warmUpLoop=1000;
	
	private static int _executeLoop=1000;
	
	private Runnable[] _arr=new Runnable[100];
	
	private int _size=0;
	
	private long[] _times=new long[100];
	
	public void addMethod(Runnable run)
	{
		_arr[_size++]=run;
	}
	
	public void executeOld()
	{
		int size=_size;
		int wLen=_warmUpLoop;
		int eLen=_executeLoop;
		
		for(int i=0;i<size;++i)
		{
			for(int j=0;j<wLen;++j)
			{
				_arr[i].run();
			}
		}
		
		for(int i=0;i<size;++i)
		{
			long t=Ctrl.getNanoTimer();
			
			for(int j=0;j<eLen;++j)
			{
				_arr[i].run();
			}
			
			_times[i]+=Ctrl.getNanoTimer()-t;
		}
		
		for(int i=size-1;i>=0;--i)
		{
			long t=Ctrl.getNanoTimer();
			
			for(int j=0;j<eLen;++j)
			{
				_arr[i].run();
			}
			
			_times[i]+=Ctrl.getNanoTimer()-t;
		}
		
		for(int i=0;i<size;++i)
		{
			Ctrl.print("耗时:",_times[i]);
		}
	}
	
	public void execute()
	{
		int size=_size;
		int wLen=_warmUpLoop;
		int eLen=_executeLoop;
		
		for(int i=0;i<size;++i)
		{
			for(int j=0;j<wLen;++j)
			{
				_arr[i].run();
			}
			
			long t=Ctrl.getNanoTimer();
			
			for(int j=0;j<eLen;++j)
			{
				_arr[i].run();
			}
			
			_times[i]+=Ctrl.getNanoTimer()-t;
		}
		
		for(int i=size-1;i>=0;--i)
		{
			for(int j=0;j<wLen;++j)
			{
				_arr[i].run();
			}
			
			long t=Ctrl.getNanoTimer();
			
			for(int j=0;j<eLen;++j)
			{
				_arr[i].run();
			}
			
			_times[i]+=Ctrl.getNanoTimer()-t;
		}
		
		for(int i=0;i<size;++i)
		{
			Ctrl.print("耗时:",_times[i]);
		}
	}
}
