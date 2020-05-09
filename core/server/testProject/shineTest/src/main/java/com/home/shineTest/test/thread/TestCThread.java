package com.home.shineTest.test.thread;

import com.home.shineTest.thread.TCThread;

public class TestCThread extends BaseTestThread
{
	protected void initThread()
	{
		_thread=new TCThread("thread");
	}
}
