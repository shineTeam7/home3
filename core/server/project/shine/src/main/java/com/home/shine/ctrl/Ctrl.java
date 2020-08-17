package com.home.shine.ctrl;

import com.home.shine.ShineSetup;
import com.home.shine.constlist.SLogType;
import com.home.shine.control.DateControl;
import com.home.shine.control.LogControl;
import com.home.shine.control.ThreadControl;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.pool.StringBuilderPool;
import com.home.shine.thread.BreakException;
import com.home.shine.utils.StringUtils;
import com.home.shine.utils.TimeUtils;

/** 快捷控制 */
public class Ctrl
{
	/** 显示调用栈长度 */
	private static int _stackLength=20;
	/** 显示调用栈字符串长度 */
	private static int _stackStrLength=2000;
	/** 固定系统时间 */
	private static volatile long _fixedTimer=System.currentTimeMillis();
	/** 固定系统时间(s) */
	private static volatile long _fixedSecondTimer=System.currentTimeMillis()/1000;
	
	private static Runtime _runTime=Runtime.getRuntime();
	
	private static long _lastPrintTime=0;
	private static int _printTimeIndex;
	
	/** 获取系统毫微秒时间 */
	public static long getNanoTimer()
	{
		return System.nanoTime();
	}
	
	/** 获取系统时间 */
	public static long getTimer()
	{
		return System.currentTimeMillis();
	}
	
	/** 获取系统秒时间 */
	public static long getSecondTimer()
	{
		return getTimer()/1000;
	}
	
	/** 获取每帧固定的系统时间(ms) */
	public static long getFixedTimer()
	{
		return _fixedTimer;
	}
	
	/** 获取每帧固定的系统时间(s) */
	public static long getFixedSecondTimer()
	{
		return _fixedSecondTimer;
	}
	
	/** 使缓存失效 */
	public static void makeFixDirty()
	{
		_fixedTimer=System.currentTimeMillis();
		_fixedSecondTimer=_fixedTimer/1000L;
	}
	
	/** 获取当前使用堆内存 */
	public static long getMemory()
	{
		return _runTime.totalMemory() - _runTime.freeMemory();
	}
	
	/** 获取内存文字表示 */
	public static String getMemoryStr()
	{
		return StringUtils.toMBString(getMemory());
	}
	
	/** 获取总内存 */
	public static long getTotalMemory()
	{
		return _runTime.totalMemory();
	}
	
	/** 获取内存文字表示 */
	public static String getTotalMemoryStr()
	{
		return StringUtils.toMBString(getTotalMemory());
	}
	
	/** 获取当前使用堆内存(获取前GC,测试数据用) */
	public static long getGCMemory()
	{
		System.gc();
		return getMemory();
	}
	
	/** 输出到控制台 */
	private static void doPrintToConsole(String str)
	{
		//启动了
		if(ShineSetup.isSetuped() && !ShineSetting.printUseCurrentThread)
		{
			ThreadControl.addLogFunc(new PrintToConsoleFunc(str));
		}
		else
		{
			doPrintToConsoleNext(str);
		}
	}
	
	private static void doPrintToConsoleNext(String str)
	{
		System.out.print(str + "\n");
	}
	
	/** 异常转string */
	public static String exceptionToString(Exception e)
	{
		StringBuilder sb=StringBuilderPool.create();
		writeExceptionToString(sb,"",e);
		return StringBuilderPool.releaseStr(sb);
	}
	
	/** 异常转string */
	public static void writeExceptionToString(StringBuilder sb,String str,Exception e)
	{
		if(e==null)
		{
			e=new Exception("void");
		}
		
		if(!str.isEmpty())
		{
			sb.append(str);
			sb.append(' ');
		}
		
		sb.append(e.getMessage());
		sb.append("\n");
		writeStackTrace(sb,e.getStackTrace());
	}
	
	/** 获取调用栈 */
	public static String getStackTrace()
	{
		return stackTraceToString(new Exception().getStackTrace());
	}
	
	private static String stackTraceToString(StackTraceElement[] sts)
	{
		StringBuilder sb=StringBuilderPool.create();
		writeStackTrace(sb,sts);
		return StringBuilderPool.releaseStr(sb);
	}
	
	private static void writeStackTrace(StringBuilder sb,StackTraceElement[] sts)
	{
		int i=0;
		int len=sts.length>_stackLength ? _stackLength : sts.length;
		
		for(i=0;i<len;++i)
		{
			sb.append(sts[i]);
			sb.append("\n");
		}
		
		if(sb.length()>_stackStrLength)
		{
			sb.setLength(_stackStrLength);
			sb.append("...");
		}
	}
	
	/** 抛错(引擎用) */
	public static void toThrowError(StringBuilder sb,Throwable e,int deep)
	{
		if(sb.length()>0)
		{
			sb.append("\n");
		}
		
		if(e!=null)
		{
			sb.append(e.toString());
			sb.append("\n");
		}
		else
		{
			e=new Exception(sb.toString());
		}
		
		String es=null;
		
		if(ShineSetting.needError)
		{
			es=sb.toString();
		}
		
		StackTraceElement[] sts=e.getStackTrace();
		
		int i=0;
		int len=sts.length>_stackLength ? _stackLength : sts.length;
		
		for(i=0;i<len;++i)
		{
			sb.append(sts[i]);
			sb.append("\n");
		}
		
		toLog(sb,SLogType.Error,deep+1);
		
		if(ShineSetting.needExitAfterError)
		{
			ShineSetup.exit();
		}
		else
		{
			if(ShineSetting.needError)
			{
				throw new RuntimeException(es);
			}
		}
	}
	
	public static void throwBreak(String str)
	{
		throw new BreakException(str);
	}
	
	//log
	
	public static void toLog(String str,int type,int deep)
	{
		toLog(StringBuilderPool.create(str),type,deep+1);
	}
	
	public static void toLog(StringBuilder sb,int type,int deep)
	{
		if(type==SLogType.Debug)
		{
			if(!ShineSetting.needDebugLog)
				return;
		}
		
		if(ShineSetting.needLogLineNumber)
		{
			StackTraceElement stackTrace=new Exception().getStackTrace()[deep+1];
			sb.insert(0," ");
			
			if(ShineSetting.isLogLineNeedPackage)
			{
				sb.insert(0,stackTrace.toString());
			}
			else
			{
				String clsName=stackTrace.getClassName();
				StringBuilder sb2=StringBuilderPool.create();
				sb2.append(clsName,clsName.lastIndexOf('.')+1,clsName.length());
				sb2.append('.');
				sb2.append(stackTrace.getMethodName());
				sb2.append('(');
				sb2.append(stackTrace.getFileName());
				sb2.append(':');
				sb2.append(stackTrace.getLineNumber());
				sb2.append(')');
				sb.insert(0,StringBuilderPool.releaseStr(sb2));
			}
		}
		
		if(ShineSetting.logNeedConsole)
		{
			if(ShineSetting.consoleNeedTimestamp)
			{
				sb.insert(0," ");
				sb.insert(0,TimeUtils.getTimeStr(DateControl.getTimeMillis()));
			}
			
			doPrintToConsole(sb.toString());
		}
		
		switch(type)
		{
			case SLogType.Running:
			{
				LogControl.runningLog(sb.toString());
			}
				break;
			case SLogType.Action:
			{
				LogControl.actionLog(sb.toString());
			}
				break;
			default:
			{
				sb.insert(0,SLogType.getMark(type));
				String str=sb.toString();
				LogControl.log(str);
				
				if(type==SLogType.Error)
				{
					LogControl.errorLog(str);
					
					if(ShineSetting.needExitAfterError)
					{
						ShineSetup.exit();
					}
				}
			}
				break;
		}
		
		StringBuilderPool.release(sb);
	}
	
	/** 输出到控制台 */
	private static class PrintToConsoleFunc implements Runnable
	{
		private String _str;
		
		public PrintToConsoleFunc(String str)
		{
			_str=str;
		}
		
		@Override
		public void run()
		{
			doPrintToConsoleNext(_str);
		}
	}
	
	/** 强制退出 */
	public static void exit()
	{
		System.exit(0);
	}
	
	/** 强制退出 */
	public static void exit(String str)
	{
		Ctrl.throwError(str);
		exit();
	}
	
	//log接口部分
	
	/** 抛错 */
	public static void throwError(String str)
	{
		throwError(str,null);
	}
	
	/** 抛错 */
	public static void throwError(Object... args)
	{
		StringBuilder sb=StringBuilderPool.create();
		
		StringUtils.writeObjectsToStringBuilder(sb,args);
		
		toThrowError(sb,null,1);
	}
	
	/** 抛错 */
	public static void throwError(String str,Throwable e)
	{
		StringBuilder sb=StringBuilderPool.create();
		
		if(str!=null)
		{
			sb.append(str);
		}
		
		toThrowError(sb,e,1);
	}
	
	/** 输出调用栈 */
	public static void printStackTrace()
	{
		printStackTrace(new Throwable().getStackTrace());
	}
	
	/** 输出调用栈 */
	public static void printStackTrace(StackTraceElement[] sts)
	{
		StringBuilder sb=StringBuilderPool.create();
		writeStackTrace(sb,sts);
		toLog(sb,SLogType.Normal,1);
	}
	
	/** 打印(普通日志) */
	public static void print(String str)
	{
		toLog(str,SLogType.Normal,1);
	}
	
	/** 打印(普通日志) */
	public static void print(Object... objs)
	{
		StringBuilder sb=StringBuilderPool.create();
		StringUtils.writeObjectsToStringBuilder(sb,objs);
		toLog(sb,SLogType.Normal,1);
	}
	
	/** 普通日志 */
	public static void log(String str)
	{
		toLog(str,SLogType.Normal,1);
	}
	
	/** 普通日志 */
	public static void log(Object... objs)
	{
		StringBuilder sb=StringBuilderPool.create();
		StringUtils.writeObjectsToStringBuilder(sb,objs);
		toLog(sb,SLogType.Normal,1);
	}
	
	/** 调试日志 */
	public static void debugLog(String str)
	{
		toLog(str,SLogType.Debug,1);
	}
	
	/** 调试日志 */
	public static void debugLog(Object... objs)
	{
		StringBuilder sb=StringBuilderPool.create();
		StringUtils.writeObjectsToStringBuilder(sb,objs);
		toLog(sb,SLogType.Debug,1);
	}
	
	/** 警告日志 */
	public static void warnLog(String str)
	{
		toLog(str,SLogType.Warning,1);
	}
	
	/** 警告日志 */
	public static void warnLog(Object... objs)
	{
		StringBuilder sb=StringBuilderPool.create();
		StringUtils.writeObjectsToStringBuilder(sb,objs);
		toLog(sb,SLogType.Warning,1);
	}
	
	/** 错误日志 */
	public static void errorLog(String str)
	{
		toLog(str,SLogType.Error,1);
	}
	
	/** 错误日志 */
	public static void errorLog(Object... objs)
	{
		StringBuilder sb=StringBuilderPool.create();
		StringUtils.writeObjectsToStringBuilder(sb,objs);
		toLog(sb,SLogType.Error,1);
	}
	
	/** 错误日志输出错误 */
	//关注点  错误日志
	public static void errorLog(Exception e)
	{
		if(e instanceof BreakException)
		{
			return;
		}
		
		StringBuilder sb=StringBuilderPool.create();
		writeExceptionToString(sb,"",e);
		toLog(sb,SLogType.Error,1);
	}
	
	public static void errorLog(String str,Exception e)
	{
		if(e instanceof BreakException)
		{
			return;
		}
		
		StringBuilder sb=StringBuilderPool.create();
		writeExceptionToString(sb,str,e);
		toLog(sb,SLogType.Error,1);
	}
	
	/** 错误日志输出调用栈 */
	public static void errorLogStackTrace(StackTraceElement[] sts)
	{
		StringBuilder sb=StringBuilderPool.create();
		writeStackTrace(sb,sts);
		toLog(sb,SLogType.Error,1);
	}
	
	/** 运行日志 */
	public static void runningLog(String str)
	{
		toLog(str,SLogType.Running,1);
	}
	
	/** 客户端行为日志 */
	public static void actionLog(String str)
	{
		toLog(str,SLogType.Action,1);
	}
}
