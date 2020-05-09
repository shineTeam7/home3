package com.home.shineTest.thread;

import com.home.shine.thread.SThread;

public class TSThread extends SThread implements IThread
{
	public TSThread(String name,int queueSize)
	{
		super(name,queueSize,0);
	}
	
	@Override
	protected void threadSleep()
	{
		//LockSupport.parkNanos(1L);
		Thread.yield();
	}
}
