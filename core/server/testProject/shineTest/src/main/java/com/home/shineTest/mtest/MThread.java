package com.home.shineTest.mtest;

import com.home.shine.ctrl.Ctrl;
import com.home.shineTest.thread.IThread;

import java.util.concurrent.ConcurrentLinkedQueue;

public class MThread extends Thread implements IThread
{
	private final ConcurrentLinkedQueue<Runnable> _queue=new ConcurrentLinkedQueue<>();
	
	private boolean _running=true;
	
	@Override
	public void addFunc(Runnable func)
	{
		_queue.offer(func);
	}
	
	@Override
	public void start()
	{
		_running=true;
		
		super.start();
	}
	
	@Override
	public void run()
	{
		Runnable func;
		
		ConcurrentLinkedQueue<Runnable> queue=_queue;
		
		while(_running)
		{
			while(_running)
			{
				func=queue.poll();
				
				if(func==null)
				{
					break;
				}
				
				try
				{
					func.run();
				}
				catch(Exception e)
				{
					Ctrl.throwError(e);
				}
			}
			
			//最后睡
			threadSleep();
		}
	}
	
	/** 休息 */
	protected void threadSleep()
	{
		//LockSupport.parkNanos(1L);
		Thread.yield();
	}
}
