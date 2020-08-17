package com.home.shine.thread;

import com.home.shine.control.ThreadControl;
import com.home.shine.ctrl.Ctrl;

/** 基础线程(不含计时任务处理) */
//实测了CThrea和SThread差别十分小,而且SThread每帧至少执行3000+的事务,环至少有8192的长才合适，所以默认还是采用CThread
//IOThread启用SThread,这里就必须是CThread,防止死锁
public class BaseThread extends CThread
//public class BaseThread extends SThread
//public class BaseThread extends SThread2
{
	private int _watchCheckIndex=1000;
	
	public BaseThread(String name,int type,int index)
	{
		super(name,type,index);
	}
	
	@Override
	protected void tick(int delay)
	{
		super.tick(delay);
		
		if((--_watchCheckIndex)==0)
		{
			_watchCheckIndex=1000;
			
			ThreadControl.checkWatchAlive();
		}
	}
	
}
