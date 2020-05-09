package com.home.shine.dataEx;

import com.home.shine.control.DateControl;
import com.home.shine.global.ShineSetting;

/** 事务计时锁(s) */
public class AffairTimeLock
{
	private int _timeMax;
	
	/** 上次检查时间(s) */
	private long _lastCheckTime=-1L;
	
	public AffairTimeLock()
	{
		this(ShineSetting.affairDefaultExecuteTime);
	}
	
	public AffairTimeLock(int time)
	{
		_timeMax=time;
		_lastCheckTime=-1L;
	}
	
	/** 锁定 */
	public void lockOn()
	{
		_lastCheckTime=DateControl.getTimeSeconds();
	}
	
	/** 解锁 */
	public void unlock()
	{
		_lastCheckTime=-1L;
	}
	
	/** 是否锁定中 */
	public boolean isLocking()
	{
		return _lastCheckTime!=-1L && (DateControl.getTimeSeconds()-_lastCheckTime)<_timeMax;
	}
}
