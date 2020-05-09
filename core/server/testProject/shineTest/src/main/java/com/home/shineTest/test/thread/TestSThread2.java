package com.home.shineTest.test.thread;

import com.home.shineTest.thread.TSThread2;

public class TestSThread2 extends BaseTestThread
{
	protected void initThread()
	{
		_thread=new TSThread2("thread",1024);
	}
}
