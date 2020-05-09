package com.home.shineTest.thread;

import com.home.shine.constlist.ThreadType;
import com.home.shine.thread.CThread;

/** 基础线程(不含计时任务处理) */
public class TCThread extends CThread implements IThread
{
	public TCThread(String name)
	{
		super(name,ThreadType.Pool,0);
	}
	
	@Override
	protected void threadSleep()
	{
		//LockSupport.parkNanos(1L);
		Thread.yield();
	}
}
