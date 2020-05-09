package com.home.shine.dataEx;

import com.home.shine.global.ShineSetting;

/** 超时事务(秒/毫秒都可) */
public class AffairTimeOut
{
	private int _timeMax;
	
	private int _time;
	
	private Runnable _func;
	
	public AffairTimeOut()
	{
	
	}
	
	public AffairTimeOut(Runnable func)
	{
		this(func,ShineSetting.affairDefaultExecuteTime);
	}
	
	public AffairTimeOut(Runnable func,int time)
	{
		_time=0;
		_timeMax=time;
		_func=func;
	}
	
	
	
	/** 开始 */
	public void start()
	{
		_time=_timeMax;
	}
	
	/** 开始(传时间) */
	public void start(int timeMax)
	{
		_time=_timeMax=timeMax;
	}
	
	public void setTimeMax(int value)
	{
		_timeMax=value;
	}
	
	/** 开始 */
	public void start(Runnable func,int timeMax)
	{
		_func=func;
		start(timeMax);
	}
	
	/** 停止 */
	public void stop()
	{
		_time=0;
	}
	
	public boolean isRunning()
	{
		return _time>0;
	}
	
	public void onDelay(int delay)
	{
		if(_time==0)
			return;
		
		if((_time-=delay)<=0)
		{
			_time=0;
			
			if(_func!=null)
				_func.run();
		}
	}
	
	public void onSecond()
	{
		onDelay(1);
	}
}
