package com.home.shine.thread;

import com.home.shine.timer.TimeDriver;

/** 核心线程(带时间驱动的线程) */
public class CoreThread extends BaseThread
{
	/** 时间驱动 */
	private TimeDriver _timeDriver;
	
	public CoreThread(String name,int type,int index)
	{
		super(name,type,index);
		
		_timeDriver=new TimeDriver();
	}
	
	@Override
	protected void tick(int delay)
	{
		super.tick(delay);
		
		_timeDriver.tick(delay);
	}
	
	/** 获取时间驱动 */
	public TimeDriver getTimeDriver()
	{
		return _timeDriver;
	}
	
	@Override
	public void copy(AbstractThread thread)
	{
		super.copy(thread);
		
		CoreThread thd=(CoreThread)thread;
		_timeDriver=thd._timeDriver;
	}
}
