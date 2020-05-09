package com.home.shineTest.thread;

import com.home.shine.thread.SThread3;

import java.util.concurrent.locks.LockSupport;

public class TSThread3 extends SThread3 implements IThread
{
	public  TSThread3(String name,int queueSize)
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