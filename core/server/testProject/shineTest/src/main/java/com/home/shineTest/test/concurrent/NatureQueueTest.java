package com.home.shineTest.test.concurrent;

public class NatureQueueTest extends BaseTestQueue
{
	public NatureQueueTest()
	{
		_pNum=4;
		_cNum=4;
	}
	
	protected void initQueue()
	{
		_queue=new NatureQueue<>();
	}
}
