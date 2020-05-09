package com.home.shine.support.concurrent.collection;

import java.util.concurrent.ConcurrentLinkedQueue;

public class NatureConcurrentQueue extends BaseConcurrentQueue
{
	private ConcurrentLinkedQueue<Runnable> _queue=new ConcurrentLinkedQueue<>();
	
	@Override
	public void run()
	{
		ConcurrentLinkedQueue<Runnable> queue=_queue;
		Runnable func;
		
		int num=0;
		
		while(_running && (func=queue.poll())!=null)
		{
			func.run();
			
			++num;
		}
		
		_executeNum=num;
	}
	
	public Runnable poll()
	{
		if(!_running)
			return null;
		
		return _queue.poll();
	}
	
	@Override
	public void addFunc(Runnable func)
	{
		_queue.add(func);
	}
	
	@Override
	public void clear()
	{
		_queue.clear();
	}
	
	/** 是否为空 */
	public boolean isEmpty()
	{
		return _queue.isEmpty();
	}
	
	
}
