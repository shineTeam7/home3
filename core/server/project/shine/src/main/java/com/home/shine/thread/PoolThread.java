package com.home.shine.thread;

import com.home.shine.constlist.ThreadType;
import com.home.shine.support.concurrent.collection.SPSCQueue;

/** 池线程 */
public class PoolThread extends CoreThread
{
	private SPSCQueue _ioQueue=new SPSCQueue();
	
	public PoolThread(int index)
	{
		super("poolThread-"+index,ThreadType.Pool,index);
	}
	
	@Override
	protected void toAddFunc(Runnable func,AbstractThread from)
	{
		if(from!=null)
		{
			if(from.type==ThreadType.IO)
			{
				_ioQueue.addFunc(func);
				return;
			}
		}
		
		super.toAddFunc(func,from);
	}
	
	@Override
	protected void tick(int delay)
	{
		super.tick(delay);
		
		_ioQueue.run();
	}
	
	@Override
	protected void stopRunning()
	{
		super.stopRunning();
		
		_ioQueue.setRunning(false);
	}
	
	@Override
	public void copy(AbstractThread thread)
	{
		super.copy(thread);
		
		PoolThread thd=(PoolThread)thread;
		
		_ioQueue=thd._ioQueue;
	}
}
