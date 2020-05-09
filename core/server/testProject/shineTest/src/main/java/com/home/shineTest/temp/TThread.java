package com.home.shineTest.temp;

import com.home.shineTest.thread.IThread;
import com.home.shine.thread.SThread2;

public class TThread extends SThread2 implements IThread
{
	public TThread(String name,int queueSize)
	{
		super(name,queueSize,0);
	}
	
	@Override
	protected void threadSleep()
	{
//		Thread.yield();
	}
}
