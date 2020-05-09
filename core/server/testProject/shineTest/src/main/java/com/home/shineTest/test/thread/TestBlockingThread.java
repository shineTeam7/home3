package com.home.shineTest.test.thread;

import com.home.shineTest.thread.BlockingThread;

public class TestBlockingThread extends BaseTestThread
{
	protected void initThread()
	{
		_thread=new BlockingThread("thread");
	}
}