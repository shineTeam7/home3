using System;

namespace ShineEngine
{
	/// <summary>
	/// 日期控制
	/// </summary>
	public class DateControl
	{
		/** 偏移时间(ms) */
		private static long _offTime=0;

		/** 缓存当前毫秒数 */
		private static long _timeMillis;
		/** 缓存当前秒数 */
		private static long _timeSeconds;

		/** 下个0点时间 */
		private static long _nextDailyTime;
		
		/** 初始化 */
		public static void init()
		{
			//计算时区
			try
			{
				TimeUtils.curTimeZone=TimeZoneInfo.FindSystemTimeZoneById(ShineSetting.timeZone);
				TimeUtils.zoneOffset = (int) TimeUtils.curTimeZone.BaseUtcOffset.TotalMilliseconds;
			}
			catch (Exception e)
			{
				TimeUtils.curTimeZone = TimeZoneInfo.CreateCustomTimeZone(ShineSetting.timeZone,new TimeSpan(TimeUtils.zoneOffset/3600000,0,0), ShineSetting.timeZone,ShineSetting.timeZone);
			}
			
			long time=getCurrentTimeMillis();

			_timeMillis=time;
			_timeSeconds=time / 1000L;

			_nextDailyTime=TimeUtils.getNextDailyTime(_timeMillis);
		}

		/** 设置当前时间(毫秒)(来自服务器) */
		public static void setCurrentTime(long time)
		{
			setOffTime(time-getNativeTimeMillis());
		}

		/** 设置偏移时间(毫秒) */
		public static void setOffTime(long offTime)
		{
			_offTime=offTime;

			makeFixDirty();
		}

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

		/** 获取原生本地时间 */
		private static long getNativeTimeMillis()
		{
			return DateTime.UtcNow.Ticks / TimeUtils.timeScale - TimeUtils.startTime;
		}

		/** 获取当前时间的毫秒时间 */
		public static long getCurrentTimeMillis()
		{
			return getNativeTimeMillis() + _offTime;
		}

		/** 获取毫秒时间(缓存的) */
		public static long getTimeMillis()
		{
			return _timeMillis;
		}

		/** 获取秒时间(缓存的) */
		public static long getTimeSeconds()
		{
			return _timeSeconds;
		}

		/** 获取下个0点时间(ms) */
		public static long getNextDailyTime()
		{
			return _nextDailyTime;
		}

		/** 获取当前日期 */
		public static DateData getNow()
		{
			DateTime dt=DateTime.Now;

			DateData data=new DateData();
			data.initByDateTime(dt);

			return data;
		}
	}
}