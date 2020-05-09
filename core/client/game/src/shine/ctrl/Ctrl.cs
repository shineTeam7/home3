using System;
using System.Text;
using UnityEngine;

namespace ShineEngine
{
	/// <summary>
	/// 控制类
	/// </summary>
	public class Ctrl
	{
		/** 显示调用栈字符串长度 */
		private static int _stackStrLength=2000;
		/** sendLogMax */
		private static int _sendLogMax=4000;
		/** 固定时间 */
		private static long _fixedTimer;
		/** 当前帧序号(初始值-2，为了与profiler对应) */
		private static int _currentFrame=-2;

		private static Action<string> _printFunc;
		/** 发送日志回调 */
		private static Action<int,string> _sendLogFunc;
		/** 日志暂停 */
		private static bool _logPause=false;

		/// <summary>
		/// 获取系统时间(毫秒)
		/// </summary>
		public static long getTimer()
		{
			return Environment.TickCount;
		}

		/// <summary>
		/// 获取毫微秒()
		/// </summary>
		/// <returns></returns>
		public static long getNanoTime()
		{
			return DateTime.Now.Ticks;
		}

		/// <summary>
		/// 获取固定系统时间
		/// </summary>
		public static long getFixedTimer()
		{
			return _fixedTimer;
		}

		/// <summary>
		/// 获取当前帧序号(调试profiler用)
		/// </summary>
		public static int getCurrentFrame()
		{
			return _currentFrame;
		}

		/// <summary>
		/// 设置固定系统时间
		/// </summary>
		public static void makeFixDirty()
		{
			++_currentFrame;
			_fixedTimer=getTimer();
		}

		public static void setLogPause(bool value)
		{
			_logPause=value;
		}

		/** 设置输出方法 */
		public static void setPrintFunc(Action<string> func)
		{
			_printFunc=func;
		}

		/** 设置输出方法 */
		public static void setSendLogFunc(Action<int,string> func)
		{
			_sendLogFunc=func;
		}

		/** 异常转string */
		public static void writeExceptionToString(StringBuilder sb,string str,Exception e)
		{
			if(e==null)
			{
				e=new Exception("void");
			}


			if(!str.isEmpty())
			{
				sb.Append(str);
				sb.Append(' ');
			}

			sb.Append(e.Message);
			sb.Append("\n");
			sb.Append(stackTraceToString(e));
			sb.Append("\n");
		}

		/** 输出到控制台 */
		private static void doPrintToConsole(string str,bool isError)
		{
			if(ShineSetting.consoleNeedTimestamp)
			{
				str=TimeUtils.getTimeStr(DateControl.getTimeMillis())+" "+str;
			}

			if(isError)
			{
				Debug.LogError(str);
			}
			else
			{
				Debug.Log(str);
			}

			if(_printFunc!=null)
				_printFunc(str);
		}

		/** 发送log */
		private static void toSendLog(int type,string str)
		{
			if(!ShineSetting.needLogSendServer || _logPause)
				return;

			if(str.Length>_sendLogMax)
				str=str.Substring(0,_sendLogMax)+"...";


			if(_sendLogFunc!=null)
			{
				ThreadControl.addMainFunc(()=>
				{
					_sendLogFunc(type,str);
				});
			}
		}

		public static void warnLog(string str)
		{
			toLog(str,SLogType.Warning);
		}

		/// <summary>
		/// 警告日志
		/// </summary>
		public static void warnLog(params object[] args)
		{
			toLog(StringUtils.objectsToString(args),SLogType.Warning);
		}

		public static void toLog(String str,int type)
		{
			if(!ShineSetting.needLog)
				return;

			if(ShineSetting.logNeedConsole)
			{
				doPrintToConsole(str,SLogType.isErrorLog(type));
			}

			toSendLog(type,str);
		}

		public static void toLog(StringBuilder sb,int type)
		{
			string str=sb.ToString();

			if(ShineSetting.logNeedConsole)
			{
				doPrintToConsole(str,SLogType.isErrorLog(type));
			}

			toSendLog(type,str);
		}

		public static void print(string str)
		{
			toLog(str,SLogType.Normal);
		}

		/// <summary>
		/// 打印
		/// </summary>
		public static void print(params object[] args)
		{
			if(!ShineSetting.needLog)
				return;

			toLog(StringUtils.objectsToString(args),SLogType.Normal);
		}

		/** 普通日志 */
		public static void log(string str)
		{
			toLog(str,SLogType.Normal);
		}

		/** 普通日志 */
		public static void log(params object[] objs)
		{
			if(!ShineSetting.needLog)
				return;

			toLog(StringUtils.objectsToString(objs),SLogType.Normal);
		}

		private static void toDebugLog(String str)
		{
			if(ShineSetting.logNeedConsole)
			{
				doPrintToConsole(str,false);
			}

			toSendLog(SLogType.Normal,str);
		}

		public static void debugLog(string str)
		{
			if(!ShineSetting.needDebugLog)
				return;

			toDebugLog(str);
		}

		/// <summary>
		/// debug日志
		/// </summary>
		public static void debugLog(params object[] args)
		{
			if(!ShineSetting.needLog)
				return;

			if(!ShineSetting.needDebugLog)
				return;

			toDebugLog(StringUtils.objectsToString(args));
		}

		private static String exceptionToString(string str,Exception e)
		{
			StringBuilder sb=StringBuilderPool.createForThread();

			writeExceptionToString(sb,str,e);

			return StringBuilderPool.releaseStrForThread(sb);
		}

		/** 错误日志 */
		public static void errorLog(string str)
		{
			toLog(str,SLogType.Error);
		}

		/** 错误日志 */
		public static void errorLog(Exception e)
		{
			if(!ShineSetting.needLog)
				return;

			toLog(exceptionToString("",e),SLogType.Error);
		}

		/** 错误日志 */
		public static void errorLog(string str,Exception e)
		{
			if(!ShineSetting.needLog)
				return;

			toLog(exceptionToString(str,e),SLogType.Error);
		}

		/** 错误日志 */
		public static void errorLog(params object[] args)
		{
			if(!ShineSetting.needLog)
				return;

			toLog(StringUtils.objectsToString(args),SLogType.Error);
		}


		// /// <summary>
		// /// 抛错
		// /// </summary>
		// public static void throwError(Exception e)
		// {
		// 	throwError(null,e);
		// }

		/// <summary>
		/// 抛错
		/// </summary>
		public static void throwError(string str)
		{
			throwError(str,null);
		}

		/// <summary>
		/// 抛错
		/// </summary>
		public static void throwError(params object[] args)
		{
			StringBuilder sb=StringBuilderPool.createForThread();

			StringUtils.writeObjectsToStringBuilder(sb,args);

			toThrowError(sb,null);
		}

		/// <summary>
		/// 抛错
		/// </summary>
		/// <param name="e"></param>
		public static void throwError(string str,Exception e)
		{
			StringBuilder sb=StringBuilderPool.createForThread();

			if(str!=null)
			{
				sb.Append(str);
			}

			toThrowError(sb,e);
		}

		public static void toThrowError(StringBuilder sb,Exception e)
		{
			if(sb.Length>0)
			{
				sb.Append("\n");
			}

			if(e!=null)
			{
				sb.Append(e.Message);
				sb.Append("\n");
			}
			else
			{
				e=new Exception(sb.ToString());
			}

			string es=null;

			if(ShineSetting.needError)
			{
				es=sb.ToString();
			}

			sb.Append(stackTraceToString(e));
			sb.Append("\n");

			string str=StringBuilderPool.releaseStrForThread(sb);

			toLog(str,SLogType.Error);

			if(ShineSetting.needError)
			{
				throw new Exception(es);
			}
		}

		/** stackTrace转string */
		private static string stackTraceToString(Exception e)
		{
			string str=e.StackTrace;

			if(str==null)
				return "";

			if(str.Length>_stackStrLength)
				str=str.Substring(0,_stackStrLength)+"...";

			return str;
		}

		/// <summary>
		/// 打印当前调用栈
		/// </summary>
		public static void printNowStackTrace()
		{
			print(stackTraceToString(new Exception()));
		}

		public static string getStackTrace()
		{
			return stackTraceToString(new Exception());
		}

		//io

		public static void printForIO(string str)
		{
			ThreadControl.addMainFunc(()=>
			{
				toLog(str,SLogType.Normal);
			});
		}

		public static void printForIO(params object[] args)
		{
			if(!ShineSetting.needLog)
				return;

			printForIO(StringUtils.objectsToString(args));
		}

		public static void printExceptionForIO(Exception e)
		{
			ThreadControl.addMainFunc(()=>
			{
				toLog(exceptionToString("",e),SLogType.Normal);
			});
		}

		public static void debugLogForIO(params object[] args)
		{
			ThreadControl.addMainFunc(()=>{ debugLog(args); });
		}

		/** 错误日志 */
		public static void errorLogForIO(params object[] args)
		{
			ThreadControl.addMainFunc(()=>{ errorLog(args); });
		}
		
		public static void warnLogForIO(params object[] args)
		{
			ThreadControl.addMainFunc(()=>{ warnLog(args); });
		}
	}
}