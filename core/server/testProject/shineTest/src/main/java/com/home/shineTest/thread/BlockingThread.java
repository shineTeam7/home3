package com.home.shineTest.thread;

import java.util.concurrent.LinkedBlockingQueue;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.pool.StringBuilderPool;

/** 基础线程(不含计时任务处理) */
public class BlockingThread extends Thread implements IThread
{
	protected final LinkedBlockingQueue<Runnable> _queue=new LinkedBlockingQueue<Runnable>();
	
	/** 睡多久 */
	protected int _sleepTime=5;
	/** 是否运行中 */
	protected volatile boolean _running=false;
	
	//额外
	public StringBuilderPool stringBuilderPool=new StringBuilderPool();
	
	public BlockingThread(String name)
	{
		setName(name);
	}
	
	@Override
	public void run()
	{
		Runnable func=null;
		
		_running=true;
		
//		int sleepTime=_sleepTime;
		LinkedBlockingQueue<Runnable> queue=_queue;
		
//		long lastTime=Ctrl.getTimer();
//		long delay;
//		long time;
		
		while(_running)
		{
			try
			{
				func=queue.take();
			}
			catch(InterruptedException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			if(func==null)
				continue;
			
			try
			{
				func.run();
			}
			catch(Exception e)
			{
				Ctrl.throwError(e);
			}
		}
	}
	
	protected void tick(long delay)
	{
		
	}
	
	/** 添加执行方法 */
	public void addFunc(Runnable func)
	{
//		_queue.offer(func);
		
		try
		{
			_queue.put(func);
		}
		catch(InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/** 退出(同步running可见性) */
	public void exit()
	{
		_running=false;
	}
	
	/** 是否在执行 */
	public boolean isRunning()
	{
		return _running;
	}
	
}
