package com.home.shine.thread;

import com.home.shine.ctrl.Ctrl;

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
		
		_running=true;
		
		ConcurrentLinkedQueue<Runnable> queue=_queue;
		
		long lastTime=Ctrl.getTimer();
		long time;
		long sleepTime=Ctrl.getTimer();
		int delay;
		int tickMax=_tickDelay;
		int tickTime=0;
		int sleepD;
		
		while(_running)
		{
			time=Ctrl.getTimer();
			sleepD=(int)(time-sleepTime);
			
			if(sleepD>0)
			{
				_restTime+=sleepD;
			}
			
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
				
				delay=(int)(time - lastTime);
				lastTime=time;
				
				if(delay>0)
				{
					if((tickTime+=delay)>=tickMax)
					{
						try
						{
							tick(tickTime);
						}
						catch(Exception e)
						{
							Ctrl.errorLog("线程tick出错",e);
						}
						
						tickTime=0;
					}
					
					_passTime+=delay;
				}
				
				//try
				//{
				//	runEx();
				//}
				//catch(Exception e)
				//{
				//	Ctrl.errorLog("线程runEx出错",e);
				//}
				
				//再事务
				
				while(_running)
				{
					if((func=queue.poll())==null)
						break;
					
					try
					{
						func.run();
					}
					catch(Exception e)
					{
						Ctrl.errorLog(e);
					}
					
					++_funcNum;
				}
			}
			
			sleepTime=Ctrl.getTimer();
			//最后睡
			threadSleep();
		}
		
		if(!queue.isEmpty())
		{
			System.out.println("线程关闭后，队列还有残留:"+getName()+" num:"+queue.size());
			
			//while(true)
			//{
			//	if((func=queue.poll())!=null)
			//	{
			//		try
			//		{
			//			func.run();
			//		}
			//		catch(Exception e)
			//		{
			//			Ctrl.errorLog(e);
			//		}
			//	}
			//	else
			//	{
			//		break;
			//	}
			//}
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
