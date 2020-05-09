package com.home.shineTest.thread;

import com.home.shine.thread.SThread2;

import java.util.concurrent.locks.LockSupport;

public class TSThread2 extends SThread2 implements IThread
{
	public TSThread2(String name,int queueSize)
	{
		super(name,queueSize,0);
	}
	
	@Override
	protected void threadSleep()
	{
		LockSupport.parkNanos(1L);
		//Thread.yield();
	}
}
