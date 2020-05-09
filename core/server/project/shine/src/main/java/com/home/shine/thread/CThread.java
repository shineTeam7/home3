package com.home.shine.thread;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;

import java.util.concurrent.ConcurrentLinkedQueue;

public class CThread extends AbstractThread
{
	private ConcurrentLinkedQueue<Runnable> _queue=new ConcurrentLinkedQueue<>();
	
	public CThread(String name,int type,int index)
	{
		super(name,type,index);
	}
	
	@Override
	public void run()
	{
		Runnable func=null;
		int num=0;
		int max=ShineSetting.cThreadRoundFuncMax;
		
		if(max<=0)
		{
			max=Integer.MAX_VALUE;
		}
		
		_running=true;
		
		ConcurrentLinkedQueue<Runnable> queue=_queue;
		
		long lastTime=Ctrl.getTimer();
		int delay;
		long time;
		
		while(_running)
		{
			if(_pause)
			{
				if(_resume)
				{
					_resume=false;
					_pause=false;
					
					Runnable resumeFunc=_resumeFunc;
					_resumeFunc=null;
					if(resumeFunc!=null)
					{
						try
						{
							resumeFunc.run();
						}
						catch(Exception e)
						{
							Ctrl.errorLog(e);
						}
					}
				}
			}
			else
			{
				++_runIndex;
				
				time=Ctrl.getTimer();
				delay=(int)(time - lastTime);
				lastTime=time;
				
				//防止系统时间改小
				if(delay<0)
					delay=0;
				
				_passTime+=delay;
				++_roundNum;
				
				//先时间
				
				try
				{
					tick(delay);
				}
				catch(Exception e)
				{
					Ctrl.errorLog("线程tick出错",e);
				}
				
				//再事务
				num=0;
				func=null;
				
				while(_running && num<max)
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
						Ctrl.errorLog(e);
					}
					
					++num;
				}
				
				if(num>_maxFuncNum)
				{
					_maxFuncNum=num;
				}
			}
			
			//最后睡
			threadSleep();
		}
		
		if(!queue.isEmpty())
		{
			System.out.println("线程关闭后，队列还有残留:"+getName());
		}
	}

	@Override
	protected void toAddFunc(Runnable func,AbstractThread from)
	{
		_queue.offer(func);
	}
	
	@Override
	public void copy(AbstractThread thread)
	{
		super.copy(thread);
		
		CThread thd=(CThread)thread;
		_queue=thd._queue;
	}
}
