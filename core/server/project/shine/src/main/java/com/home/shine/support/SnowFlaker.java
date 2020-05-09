package com.home.shine.support;

import com.home.shine.control.DateControl;
import com.home.shine.ctrl.Ctrl;

/** 雪花ID生成 */
public class SnowFlaker
{
	/** 41bit时间戳 */
	private static final long timeMark=(1L<<41)-1;
	/** 12bit序号(4096) */
	private static final int indexMax=1<<12;
	
	/** 主ID */
	private int _id;
	/** 自增序号 */
	private int _index;
	/** 时间进位 */
	private int _timeOff;
	/** 上次时间戳 */
	private long _lastTime=0;
	
	private long _head;
	
	public SnowFlaker(int id)
	{
		//10bit工作ID
		if(id>=1024)
		{
			Ctrl.throwError("工作id超出限制");
		}
		
		_id=id;
	}
	
	private void makeHead()
	{
		_head=(((_lastTime+_timeOff) & timeMark) << 22) | (_id<<12);
	}
	
	/** 获取一个ID */
	public long getOne()
	{
		long currentTime=DateControl.getCurrentTimeMillis();
		
		if(currentTime>_lastTime)
		{
			long d=currentTime - _lastTime;
			
			_lastTime=currentTime;
			
			if(d>_timeOff)
			{
				_timeOff=0;
				_index=0;
			}
			else
			{
				_timeOff-=d;
			}
			
			makeHead();
		}
		
		if((++_index)>=indexMax)
		{
			Ctrl.warnLog("出现snowIndex超出max一次");
			_index=0;
			_timeOff++;
			makeHead();
		}
		
		return _head | _index;
	}
}
