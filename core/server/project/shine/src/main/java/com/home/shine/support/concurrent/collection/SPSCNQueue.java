package com.home.shine.support.concurrent.collection;

import java.util.concurrent.ConcurrentLinkedQueue;

public class SPSCNQueue extends BaseSPSCQueue
{
	private ConcurrentLinkedQueue<Runnable> _queue=new ConcurrentLinkedQueue<>();
	
	@Override
	public void run()
	{
		ConcurrentLinkedQueue<Runnable> queue=_queue;
		Runnable func;
		
		while(_running && (func=queue.poll())!=null)
		{
			func.run();
		}
	}
	
	@Override
	public void addFunc(Runnable func)
	{
		super.addFunc(func);
		
		_queue.add(func);
	}
}
