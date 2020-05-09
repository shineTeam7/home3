package com.home.commonBase.control;

import com.home.commonBase.global.CommonSetting;
import com.home.shine.control.DateControl;
import com.home.shine.control.ThreadControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.thread.CoreThread;
import com.home.shine.timer.TimeDriver;

public class AbstractLogicExecutor
{
	/** 编号 */
	protected int _index;
	
	private CoreThread _thread;
	
	/** 固定系统时间 */
	private long _fixedTimer;
	/** 固定系统时间戳(1秒更新一次) */
	private long _timeMillis;
	
	private int _secondTick=0;
	
	public AbstractLogicExecutor(int index)
	{
		_index=index;
		_thread=ThreadControl.getPoolThread(_index);
	}
	
	/** 获取序号 */
	public int getIndex()
	{
		return _index;
	}
	
	/** 初始化 */
	public void init()
	{
	
	}
	
	protected void initTick()
	{
		_fixedTimer=Ctrl.getFixedTimer();
		_thread.addTickCall(this::onThreadTick);
		_thread.getTimeDriver().setInterval(this::onFrame,CommonSetting.logicFrameDelay);
	}
	
	/** 线程tick时 */
	protected void onThreadTick()
	{
	
	}
	
	protected void onFrame(int delay)
	{
		_fixedTimer=Ctrl.getFixedTimer();
		_timeMillis=DateControl.getTimeMillis();
		
		if((_secondTick+=delay)>=1000)
		{
			onSecond(_secondTick);
			_secondTick=0;
		}
	}
	
	/** 每秒执行 */
	protected void onSecond(int delay)
	{
	
	}
	
	/** 固定的系统时间 */
	public long getFixedTimer()
	{
		return _fixedTimer;
	}
	
	/** 固定系统时间戳 */
	public long getTimeMillis()
	{
		return _timeMillis;
	}
	
	/** 添加方法执行 */
	public void addFunc(Runnable func)
	{
		_thread.addFunc(func);
	}
	
	/** 获取时间驱动 */
	public TimeDriver getTimeDriver()
	{
		return _thread.getTimeDriver();
	}
	
	/** 刷配置 */
	public void onReloadConfig()
	{
	
	}
}
