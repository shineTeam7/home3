package com.home.shineTest.test.thread;

import com.home.shineTest.thread.TSThread;

public class TestSThread extends BaseTestThread
{
	protected void initThread()
	{
		_thread=new TSThread("thread",1024);
	}
}