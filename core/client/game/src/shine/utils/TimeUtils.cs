using System;
using System.Text;

using Quartz;

namespace ShineEngine
{
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
		/** C#时间缩放 */
		public static int timeScale=10000;
		/** 启始时间(ms)(除过的) */
		public static long startTime=new DateTime(1970,1,1).Ticks/timeScale; //启始时间

		/** 一天的毫秒数 */
		private static long _dayTime=60 * 60 * 24 * 1000;

		/** 1970年1月1日8点 */
		private static DateTimeOffset _zeroDate=new DateTimeOffset(0L,TimeSpan.Zero);

		/** 时间偏移(默认东八区) */
		public static int zoneOffset=28800000;
		
		/** 当前服务器时区(用于计算活动) */
		public static TimeZoneInfo curTimeZone;
		
		/** 通过DateTime获取毫秒数 */
		public static long getTimeMillisByDateTime(DateTime date)
		{
			return date.Ticks/timeScale-startTime;
		}

		/** 通过DateTime获取毫秒数 */
		public static long getTimeSecondsByDateTime(DateTime date)
		{
			return (date.Ticks/timeScale-startTime)/1000L;
		}

		/** 创建cron表达式 */
		public static CronExpression createCronExpression(string cron)
		{
			CronExpression ex=new CronExpression(cron);
			ex.TimeZone=curTimeZone;
			return ex;
		}

		/** 获取下一个cron时刻(ms)出错或过去时间返回-1 */
		public static long getNextCronTime(CronExpression cron,long time)
		{
			long re;

			try
			{
				DateTimeOffset date=cron.GetNextValidTimeAfter(new DateTimeOffset((time+startTime)*timeScale,TimeSpan.Zero));
				re=(date.Ticks/timeScale-startTime);
			}
			catch(Exception e)
			{
				re=-1L;
			}

			return re;
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
				bool has=false;
				long next;

				while(true)
				{
					next=getNextCronTime(cron,ut);

					if(next==-1)
						break;

					if(next>time)
					{
						if(has)
						{
							return ut;
						}

						break;
					}

					ut=next;
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
		public static long getNextCronTimeFromNow(string cron)
		{
			return getNextCronTime(cron,DateControl.getTimeMillis());
		}

		/** 获取下一个cron时刻(ms) */
		public static DateData getNextCronDate(string cron,DateData data)
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
			long zero = (now  / _dayTime + 1) * _dayTime;
			//转化成utc时间
			zero-=zoneOff;
			return zero;
		}
		
		/** 获取明天0点(ms) */
		public static long getNextDailyTimeFromNow()
		{
			return getNextDailyTime(DateControl.getTimeMillis());
		}

		/// <summary>
		/// 根据秒数获取时间显示，格式XX:XX:XX
		/// </summary>
		/// <param name="second">秒数</param>
		/// <param name="autoLength">是否自动长度，如果否，则永远显示XX:XX:XX</param>
		/// <param name="needHour">是否需要小时</param>
		/// <returns></returns>
		public static string getTimeStringBySecond(int seconds, bool autoLength = false,bool needHour = true)
		{
			int hour = seconds / 3600;
			int minute = (seconds - hour * 3600) / 60;
			int second = seconds - hour * 3600 - minute * 60;

			StringBuilder sb = new StringBuilder();
			if(autoLength)
			{
				if(needHour && hour > 0)
				{
					sb.Append(hour.ToString("D2"));
					sb.Append(":");
				}
				if(minute > 0)
				{
					sb.Append(minute.ToString("D2"));
					sb.Append(":");
				}
				if(seconds >= 10)
				{
					sb.Append(second.ToString("D2"));
				}
				else
				{
					sb.Append(second.ToString());
				}
			}
			else
			{
				if (needHour && hour > 0)
				{
					sb.Append(hour.ToString("D2"));
					sb.Append(":");
				}

				sb.Append(minute.ToString("D2"));
				sb.Append(":");
				sb.Append(second.ToString("D2"));
			}

			return sb.ToString();
		}

		/// <summary>
		/// 根据毫秒数获取时间显示，格式XX:XX:XX
		/// </summary>
		/// <param name="second">毫秒数</param>
		/// <param name="autoLength">是否自动长度，如果否，则永远显示XX:XX:XX</param>
		/// <returns></returns>
		public static string getTimeStringByMillis(long time, bool autoLength = false,bool needHour = true)
		{
			return getTimeStringBySecond((int)(time / 1000),autoLength,needHour);
		}

		/** 根据毫秒数获取时间显示，格式 xx day OR XX:XX:XX */
        public static string getDayOrTimeStringByMillis(long time, bool autoLength = false, bool needHour = true)
        {
            int day = (int)(time / 86400000);
            if (day >= 2)
            {
                return day + "days";
            }
            else
            {
                return TimeUtils.getTimeStringByMillis(time,autoLength,needHour);
            }
        }
		
		/** 根据毫秒数获取时间显示，格式 xx day OR XX:XX:XX */
		public static string getDayOrTimeStringByMillis2(long time, bool autoLength = false, bool needHour = true)
		{
			int day = (int)(time / 86400000);
			if (day >= 2)
			{
				return day + " days";
			}
			else
			{
				return TimeUtils.getTimeStringByMillis(time,autoLength,needHour);
			}
		}

		/** 写入时间字符串表示(ms),onlyDay仅仅需要显示到天 */
		public static void writeTimeStr(StringBuilder sb,long time,bool onlyDay=false)
		{
			DateData date=new DateData();
			date.initByTimeMillis(time);
			date.writeToStringBuilder(sb,onlyDay);
		}

		/** 获取字符串时间表达(ms) */
		public static string getTimeStr(long time,bool onlyDay=false)
		{
			StringBuilder sb=StringBuilderPool.create();
			writeTimeStr(sb,time,onlyDay);
			return StringBuilderPool.releaseStr(sb);
		}
		
		/** 获取是否为同一天 */
		public static bool isSameDay(long time1,long time2)
		{
			return getDisBetweenDay(time1,time2)==0;
		}
		
		/** 获取是否为同一天 */
		public static bool isSameZoneDay(long startTime,long endTime,long zoneOff)
		{
			int disDay = (int) ((getNextDailyTime(endTime,zoneOff) - getNextDailyTime(startTime,zoneOff)) / _dayTime);
			
			return disDay==0;
		}
	
		/** 获取两个日期相差几天 startTime小,endTime大,如果反了，返回负值，使用时自行判定*/
		public static int getDisBetweenDay(long startTime,long endTime)
		{
			return (int)((getNextDailyTime(endTime)-getNextDailyTime(startTime))/_dayTime);
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
		
		/** 天转毫秒 */
		public static long dayToMillisecond(int day)
		{
			return hourToMillisecond(day*24);
		}
        
        /** 时区时间转化为本地时区时间 */
        public static long zoneTimeToLocalTimne(long zoneTime)
        {
	        //未完
	        return 0;
        }
	}
}