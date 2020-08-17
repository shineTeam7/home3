package com.home.shine.thread;

import com.home.shine.ShineSetup;
import com.home.shine.constlist.ThreadType;
import com.home.shine.control.ThreadControl;
import com.home.shine.control.WatchControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.collection.SMap;

/** 观测线程 */
public class ThreadWatcher extends BaseThread
{
	/** 线程检测间隔(1s) */
	private static final int _threadWatchDelay=1000;
	/** 死循环检测 */
	private SMap<AbstractThread,DeadWatchData> _threadWatchDic=new SMap<>();
	
	private volatile boolean _started=false;
	
	/** 检测时间经过 */
	private int _threadWatchTimePass=0;
	
	
	public ThreadWatcher()
	{
		super("watcher",ThreadType.Watcher,0);
		
		setTickDelay(100);
	}
	
	@SuppressWarnings("deprecation")
	protected void tick(int delay)
	{
		//super.tick(delay);
		
		WatchControl.tick(delay);
		
		if((_threadWatchTimePass+=delay)>_threadWatchDelay)
		{
			_threadWatchTimePass-=_threadWatchDelay;
			
			_threadWatchDic.forEachS((thread,data)->
			{
				//需要检测
				if(thread.needDeadCheck())
				{
					//还是同一个
					if(thread.getRunIndex()==data.lastRunIndex)
					{
						data.sameTimes++;
						
						//20秒
						if(data.sameTimes >= ShineSetting.watchCheckDeadTreadTime)
						{
							Ctrl.errorLog("进程:",thread.getName(),"陷入死循环");
							Ctrl.errorLogStackTrace(thread.getStackTrace());
							
							thread.stop();
							return;
						}
					}
					else
					{
						data.lastRunIndex=thread.getRunIndex();
						data.sameTimes=0;
					}
				}
				
				//线程已死亡
				if(!thread.isAlive() && !ShineSetup.isExiting())
				{
					ThreadControl.restartThread(thread.type,thread.index);
					data.sameTimes=0;
				}
			});
		}
	}
	
	/** 添加被观测线程 */
	public void addThread(AbstractThread thread)
	{
		DeadWatchData data=_threadWatchDic.get(thread);
		
		if(data==null)
		{
			data=new DeadWatchData();
			_threadWatchDic.put(thread,data);
		}
		
		data.lastRunIndex=thread.getRunIndex();
		data.sameTimes=0;
	}
	
	/** 移除观测 */
	public void removeThread(AbstractThread thread)
	{
		_threadWatchDic.remove(thread);
	}
	
	@Override
	public synchronized void start()
	{
		if(_started)
		{
			return;
		}
		
		_started=true;
		
		_threadWatchDic.forEach((k,v)->
		{
			v.lastRunIndex=k.getRunIndex();
			v.sameTimes=0;
		});
		
		super.start();
	}
	
	/** 是否已启动 */
	public boolean isStarted()
	{
		return _started;
	}
	
	@Override
	public void copy(AbstractThread thread)
	{
		super.copy(thread);
		
		ThreadWatcher thd=(ThreadWatcher)thread;
		_threadWatchDic=thd._threadWatchDic;
	}
	
	private class DeadWatchData
	{
		public int lastRunIndex=0;
		
		public int sameTimes=0;
	}
}
