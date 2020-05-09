package com.home.shineTest.test.thread;

import com.home.shineTest.thread.TSThread3;

public class TestSThread3 extends BaseTestThread
{
	protected void initThread()
	{
		_thread=new TSThread3("thread",1024);
	}
}
