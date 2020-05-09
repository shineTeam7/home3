using System;

namespace ShineEngine
{
	/** 时间表达式 */
	public class TimeExpression
	{
		/** 空 */
		private const int None=0;
		/** 秒倒计时 */
		private const int SecondTimeOut=1;
		/** cron表达式 */
		private const int Cron=9;

		/** 类型 */
		private int _type;
		/** 参数 */
		private int _arg;
		/** cron表达式 */
		private CronExpression _cron;

		public TimeExpression(string str)
		{
			if(str==null || str.isEmpty())
			{
				_type=None;
				return;
			}

			int index=str.IndexOf(":");

			if(index!=-1)
			{
				_type=int.Parse(str.slice(0,index));
				_arg=int.Parse(str.slice(index+1,str.Length));
			}
			else
			{
				_type=Cron;

				try
				{
					_cron = TimeUtils.createCronExpression(str);
				}
				catch(Exception e)
				{
					Ctrl.throwError("cron表达式错误",e);
				}
			}
		}

		/** 是否为空 */
		public bool isEmpty()
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

		/** 获取下个时间 */
		public long getNextTime()
		{
			return getNextTime(DateControl.getTimeMillis());
		}

		/** 获取上个时间 */
		public long getPrevTime()
		{
			return getPrevTime(DateControl.getTimeMillis());
		}

		/** 获取下个时间 */
		public static long getNextTimeS(string str,long time)
		{
			return new TimeExpression(str).getNextTime(time);
		}

		/** 获取下个时间 */
		public static long getNextTimeS(string str)
		{
			return new TimeExpression(str).getNextTime();
		}
	}


}