package com.home.shine.control;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.data.DateData;
import com.home.shine.global.ShineSetting;
import com.home.shine.utils.TimeUtils;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class DateControl
{
	static
	{
		//因为其他工具场合也需要用到时间戳，所以static init
		init();
	}
	
	/** 偏移时间(毫秒) */
	private static volatile long _offTime=0;
	
	/** 缓存当前毫秒数 */
	private static volatile long _timeMillis;
	/** 缓存当前秒数 */
	private static volatile long _timeSeconds;
	/** 下个0点时间(更新间隔低,所以volatile) */
	private static volatile long _nextDailyTime;
	
	private static int _dateFixDelayTick;
	
	/** 初始化 */
	public static void init()
	{
		TimeUtils.zoneOffset = TimeZone.getTimeZone(ShineSetting.timeZone).getRawOffset();
		
		//Ctrl.print("初始化时区",ShineSetting.timeZone,TimeUtils.zoneOffset);
		
		long time=getCurrentTimeMillis();
		
		_timeMillis=time;
		_timeSeconds=time/1000L;
		
		_nextDailyTime=TimeUtils.getNextDailyTime(_timeMillis);
	}
	
	/** 设置当前时间(毫秒)(来自服务器) */
	public static void setCurrentTime(long time)
	{
		setOffTime(time-System.currentTimeMillis());
	}
	
	/** 设置偏移时间(毫秒) */
	public static void setOffTime(long offTime)
	{
		_offTime=offTime;
		
		makeFixDirty();
	}
	
	/** 获取当前时间偏移 */
	public static long getOffTime()
	{
		return _offTime;
	}
	
	/** 使缓存失效 */
	public static void makeFixDirty()
	{
		_timeSeconds=(_timeMillis=getCurrentTimeMillis())/1000L;
		
		if(_timeMillis>=_nextDailyTime)
		{
			_nextDailyTime=TimeUtils.getNextDailyTime(_timeMillis);
		}
	}
	
	/** 获取实时毫秒时间 */
	public static long getCurrentTimeMillis()
	{
		return System.currentTimeMillis() + _offTime;
	}
	
	/** 获取当前时间的毫秒时间(缓存的,一帧一次) */
	public static long getTimeMillis()
	{
		return _timeMillis;
	}
	
	/** 获取当前时间的秒时间(缓存的) */
	public static long getTimeSeconds()
	{
		return _timeSeconds;
	}
	
	/** 获取下个0点时间(ms) */
	public static long getNextDailyTime()
	{
		return _nextDailyTime;
	}
	
	/** 获取当前时间 */
	public static DateData getNow()
	{
		DateData date=new DateData();
		
		date.initByTimeSeconds(_timeSeconds);
		
		return date;
	}
}
