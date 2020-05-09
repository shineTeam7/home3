package com.home.shine.support.concurrent.collection;

/** 事务队列(基于SThread) */
public class SFuncQueue extends SBatchQueue<Runnable>
{
	public SFuncQueue(int queueSize)
	{
		super(queueSize);
	}
	
	@Override
	protected void acceptOne(Runnable obj)
	{
		obj.run();
	}
}
