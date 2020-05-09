package com.home.shine.dataEx;

import com.home.shine.control.DateControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.CronExpression;
import com.home.shine.timer.DefaultTimeEntity;
import com.home.shine.timer.ITimeEntity;
import com.home.shine.utils.TimeUtils;

import java.text.ParseException;

/** 时间表达式 */
public class TimeExpression
{
	/** 空 */
	private static final int None=0;
	/** 秒倒计时 */
	private static final int SecondTimeOut=1;
	/** cron表达式 */
	private static final int Cron=9;
	
	/** 类型 */
	private int _type;
	/** 参数 */
	private int _arg;
	/** cron表达式 */
	private CronExpression _cron;
	
	public TimeExpression(String str)
	{
		if(str==null || str.isEmpty())
		{
			_type=None;
			return;
		}
		
		int index=str.indexOf(":");
		
		if(index!=-1)
		{
			_type=Integer.parseInt(str.substring(0,index));
			_arg=Integer.parseInt(str.substring(index+1,str.length()));
		}
		else
		{
			_type=Cron;
			_cron=TimeUtils.createCronExpression(str);
		}
	}
	
	/** 是否为空 */
	public boolean isEmpty()
	{
		return _type==None;
	}

	/** 获取下个时间(ms)(如为空返回-1L) */
	public long getNextTime(long from)
	{
		switch(_type)
		{
			case None:
			{
				return -1L;
			}
			case SecondTimeOut:
			{
				return from+(_arg*1000);
			}
			case Cron:
			{
				return TimeUtils.getNextCronTime(_cron,from);
			}
			default:
			{
				Ctrl.throwError("不支持的时间表达式类型",_type);
			}
			break;
		}

		return from;
	}
	
	/** 获取上个时间(ms)(如为空返回-1L) */
	public long getPrevTime(long from)
	{
		switch(_type)
		{
			case None:
			{
				return -1L;
			}
			case SecondTimeOut:
			{
				return from-(_arg*1000);
			}
			case Cron:
			{
				return TimeUtils.getPrevCronTime(_cron,from);
			}
			default:
			{
				Ctrl.throwError("不支持的时间表达式类型",_type);
			}
			break;
		}
		
		return from;
	}
	
	/** 获取下个时间(ms) */
	public static long getNextTimeS(String str,long time)
	{
		return new TimeExpression(str).getNextTime(time);
	}

	/** 获取下个时间(ms) */
	public long getNextTime(ITimeEntity entity)
	{
		return getNextTime(entity.getTimeMillis());
	}
	
	/** 获取下个时间(ms) */
	public long getNextTime()
	{
		return getNextTime(DateControl.getTimeMillis());
	}
	
	/** 获取上个时间(ms) */
	public long getPrevTime()
	{
		return getPrevTime(DateControl.getTimeMillis());
	}
	
	/** 获取上个时间(ms) */
	public long getPrevTime(ITimeEntity entity)
	{
		return getPrevTime(entity.getTimeMillis());
	}
	
	/** 获取下个时间(ms) */
	public static long getNextTimeS(String str)
	{
		return new TimeExpression(str).getNextTime();
	}
}
