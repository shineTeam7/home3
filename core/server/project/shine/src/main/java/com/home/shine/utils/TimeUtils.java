package com.home.shine.utils;

import com.home.shine.control.DateControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.data.DateData;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.CronExpression;
import com.home.shine.support.pool.StringBuilderPool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/** 时间方法组 */
public class TimeUtils
{
	/** 一天的毫秒数 */
	public static long dayTime=60 * 60 * 24*1000;
	/** 一小时的毫秒数 */
	public static long hourTime=60 * 60 *1000;
	/** 一分的毫秒数 */
	public static long minuteTime=60 *1000;
	/** 一秒的毫秒数 */
	public static long secondTime=1000;
	/** 0点的时间偏移(如别的时区0点，则对应设置) */
	public static long dailyOffTime=60 * 60 * 8*1000;

	/** 1970年1月1日8点 */
	private static Date _zeroDate=new Date(0L);

	/** 时间偏移 */
	public static int zoneOffset=0;

	/** 字符串转Date*/
	private static String DEFAULT_DATE_FORMAT="yyyy-MM-dd HH:mm:ss";

	/** 字符串转Date2*/
	public static String DEFAULT_DATE_FORMAT2="yyyy/MM/dd HH:mm:ss";
	
	/** 字符串转Date3*/
	public static String DEFAULT_DATE_FORMAT3="yyyy.MM.dd";
	
	/** 创建cron表达式 */
	public static CronExpression createCronExpression(String cron)
	{
		CronExpression ex=null;

		try
		{
			ex=new CronExpression(cron);
			ex.setTimeZone(TimeZone.getTimeZone(ShineSetting.timeZone));
		}
		catch(ParseException e)
		{
			Ctrl.errorLog(e);
		}

		return ex;
	}

	/** 获取下一个cron时刻(ms)(出错或过去时间返回-1) */
	public static long getNextCronTime(CronExpression cron,long time)
	{
		Date date=cron.getNextValidTimeAfter(new Date(time));

		//出错
		if(date==null)
		{
			return -1L;//
		}

		return date.getTime();
	}

	/** 获取上一个cron时刻(ms)(出错或过去时间返回-1) */
	public static long getPrevCronTime(CronExpression cron,long time)
	{
		long t=ShineSetting.cronExpressionPrevTime;

		for(int i=0;i<ShineSetting.cronExpressionPrevTimeRound;i++)
		{
			if(i>0)
				t<<=1;//扩一倍

			long ut=time-t;
			boolean has=false;

			long next;

			while(true)
			{
				next=getNextCronTime(cron,ut);

				if(next==-1)
					break;

				if(next>=time)
				{
					if(has)
					{
						return ut;
					}

					break;
				}

				ut=next;
				has=true;
			}
		}

		return -1;
	}

	/** 获取下一个cron时刻(ms) */
	public static long getNextCronTime(String cron,long time)
	{
		return getNextCronTime(createCronExpression(cron),time);
	}

	/** 获取上一个cron时刻(ms)(出错或过去时间返回-1) */
	public static long getPrevCronTime(String cron,long time)
	{
		return getPrevCronTime(createCronExpression(cron),time);
	}

	/** 获取下一个cron时刻(ms) */
	public static long getNextCronTimeFromNow(CronExpression cron)
	{
		return getNextCronTime(cron,DateControl.getTimeMillis());
	}

	/** 获取下一个cron时刻,从当前开始(ms) */
	public static long getNextCronTimeFromNow(String cron)
	{
		return getNextCronTime(cron,DateControl.getTimeMillis());
	}

	/** 获取下一个cron时刻 */
	public static DateData getNextCronDate(String cron,DateData data)
	{
		DateData date=new DateData();

		date.initByTimeMillis(getNextCronTime(cron,data.getTimeMillis()));

		return date;
	}

	/** 获取下一个日期(ms)(每天0点) */
	public static long getNextDailyTime(long now)
	{
		return getNextDailyTime(now,zoneOffset);
	}

	/** 获取指定时区下一个日期(ms)(每天0点) */
	public static long getNextDailyTime(long now,long zoneOff)
	{
		//先从utc转成对应时区的时间
		now+=zoneOff;
		//计算对应时区的0点
		long zero = (now  / dayTime + 1) * dayTime;
		//转化成utc时间
		zero-=zoneOff;
		return zero;
	}

	/** 获取明天0点(秒) */
	public static long getNextDailyTimeFromNow()
	{
		return getNextDailyTime(DateControl.getTimeMillis());
	}

	//显示部分

	/** 获取时间字符串表示(精确到秒) */
	public static String getTimeStr(Calendar calendar)
	{
		StringBuilder sb=StringBuilderPool.create();
		writeTimeStr(sb,calendar);
		return StringBuilderPool.releaseStr(sb);
	}

	/** 写入时间字符串表示(精确到秒) */
	public static void writeTimeStr(StringBuilder sb,Calendar calendar)
	{
		sb.append(calendar.get(Calendar.YEAR));
		sb.append('-');
		StringUtils.writeIntWei(sb,calendar.get(Calendar.MONTH) + 1,2);
		sb.append('-');
		StringUtils.writeIntWei(sb,calendar.get(Calendar.DAY_OF_MONTH),2);
		sb.append(' ');
		StringUtils.writeIntWei(sb,calendar.get(Calendar.HOUR_OF_DAY),2);
		sb.append(':');
		StringUtils.writeIntWei(sb,calendar.get(Calendar.MINUTE),2);
		sb.append(':');
		StringUtils.writeIntWei(sb,calendar.get(Calendar.SECOND),2);
	}

	/** 写入时间字符串表示(ms) */
	public static void writeTimeStr(StringBuilder sb,long time,boolean onlyDay)
	{
		DateData date=new DateData();
		date.initByTimeMillis(time);
		date.writeToStringBuilder(sb,onlyDay);
	}

	/** 写入时间字符串表示(ms) */
	public static void writeTimeStr(StringBuilder sb,long time)
	{
		writeTimeStr(sb,time,false);
	}

	/** 获取字符串时间表达(ms) */
	public static String getTimeStr(long time,boolean onlyDay)
	{
		StringBuilder sb=StringBuilderPool.create();
		writeTimeStr(sb,time,onlyDay);
		return StringBuilderPool.releaseStr(sb);
	}

	/** 获取字符串时间表达(ms) */
	public static String getTimeStr(long time)
	{
		return getTimeStr(time,false);
	}

	/** 获取是否为同一天 */
	public static boolean isSameDay(long time1,long time2)
	{
		return getDisBetweenDay(time1,time2)==0;
	}

	/** 获取是否为同一天 */
	public static boolean isSameZoneDay(long startTime,long endTime,long zoneOff)
	{
		int disDay = (int) ((getNextDailyTime(endTime,zoneOff) - getNextDailyTime(startTime,zoneOff)) / dayTime);

		return disDay==0;
	}

	/** 获取两个日期相差几天 startTime小,endTime大,如果反了，返回负值，使用时自行判定*/
	public static int getDisBetweenDay(long startTime,long endTime)
	{
		return (int)((getNextDailyTime(endTime)-getNextDailyTime(startTime))/ dayTime);
	}

	/** 小时转毫秒 */
	public static long hourToMillisecond(int hour)
	{
		return hour * 60 * 60 * 1000;
	}

	/** 秒转毫秒 */
	public static long secondToMillisecond(int second)
	{
		return second * 1000;
	}

	/** 时区时间转化为本地时区时间 */
	public static long zoneTimeToLocalTime(long zoneTime)
	{
		//未完
		return 0;
	}

	/** 是否是周末(正常意义上的周六，周日) */
	public static boolean isWeekEnd()
	{
		ZonedDateTime now=getZonedDateTime();
		return now.getDayOfWeek()==DayOfWeek.SUNDAY || now.getDayOfWeek()==DayOfWeek.SATURDAY;
	}

	public static ZonedDateTime getZonedDateTime()
	{
		TimeZone timeZone = TimeZone.getTimeZone(ShineSetting.timeZone);
		return ZonedDateTime.now(timeZone.toZoneId());
	}
	
	public static ZonedDateTime getZonedDateTime(int offSet)
	{
		ZoneOffset zoneOffset=ZoneOffset.ofTotalSeconds(offSet / 1000);
		return ZonedDateTime.now(zoneOffset);
	}

	/** 字符串转日期 */
	public static Date strToDate(String dateStr){
		return strToDate(dateStr,DEFAULT_DATE_FORMAT);
	}

	/** 字符串转日期 */
	public static Date strToDate(String dateStr,String formatStr) {

		SimpleDateFormat format = new SimpleDateFormat(formatStr);
		Date date = null;
		try {
			date = format.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	/** 日期转字符串 */
	public static String dateToStr(Date date) {
		SimpleDateFormat format = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
		return format.format(date);
	}
	
	/** 日期转字符串 */
	public static String dateToStr(Date date,String formatStr) {
		SimpleDateFormat format = new SimpleDateFormat(formatStr);
		return format.format(date);
	}

	/** 将时间从一个时区转换为另一个时区时间 */
	public static long zoneTimeToZoneTime(long fromZoneTime,TimeZone fromZone,TimeZone toZone)
	{
		return fromZoneTime+(toZone.getRawOffset()-fromZone.getRawOffset());
	}

	/** 将时间从一个时区转换为utc时区时间 */
	public static long zoneTimeToUtcZoneTime(long fromZoneTime,TimeZone fromZone)
	{
		return fromZoneTime+(-fromZone.getRawOffset());
	}
}
