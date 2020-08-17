package com.home.shine.support;

import com.home.shine.control.DateControl;
import com.home.shine.control.LogControl;
import com.home.shine.control.ThreadControl;
import com.home.shine.global.ShineGlobal;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.collection.SList;
import com.home.shine.support.pool.StringBuilderPool;
import com.home.shine.utils.TimeUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.util.Calendar;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

/** 日志操作类 */
public class SLogger
{
	private static final String Enter="\r\n";
	/** 日志序号递增 */
	private static AtomicInteger _logAtomic=new AtomicInteger(1);
	
	private static SList<SLogger> _list=new SList<>(SLogger[]::new);
	
	/** 日志 */
	private Logger _log=null;
	/** 缓存 */
	private StringBuilder _sb=new StringBuilder();
	
	private Calendar _lastCalendar=Calendar.getInstance();
	
	private String _timeStr=null;
	
	protected boolean _needTime=true;
	
	public SLogger()
	{
		
	}
	
	private void makeTimeStr()
	{
		_lastCalendar.setTimeInMillis(DateControl.getTimeMillis());
		
		StringBuilder sb=StringBuilderPool.create();
		TimeUtils.writeTimeStr(sb,_lastCalendar);
		_timeStr=StringBuilderPool.releaseStr(sb);
	}
	
	/** 输出(日志线程) */
	private void flush()
	{
		//有
		if(_sb.length()>0)
		{
			String v=_sb.toString();
			_log.info(v);
			
			if(_sb.length()>ShineSetting.logStringBuilderKeepSize)
			{
				_sb=new StringBuilder();
			}
			else
			{
				_sb.setLength(0);
			}
		}
		
		//dirty
		_timeStr=null;
	}
	
	/** 输出 */
	public void log(String str)
	{
		ThreadControl.addLogFunc(()->
		{
			doLog(_sb,str);
		});
	}
	
	/** 执行添加日志 */
	protected void doLog(StringBuilder sb,String str)
	{
		if(_needTime)
		{
			if(_timeStr==null)
			{
				makeTimeStr();
			}
			
			sb.append(_timeStr);
			
			sb.append(' ');
		}
		
		doLogEx(sb);
		
		sb.append(str);
		sb.append(Enter);
	}
	
	/** 额外的日志补充(日志线程) */
	protected void doLogEx(StringBuilder sb)
	{
	
	}
	
	/** 每个时间间隔(目前1秒) */
	public static void onTick(int delay)
	{
		SLogger[] values=_list.getValues();
		
		for(int i=_list.size() - 1;i >= 0;--i)
		{
			values[i].flush();
		}
	}
	
	/** 立刻刷一次 */
	public static void flushOnce()
	{
		ThreadControl.getLogThread().addFunc(()->
		{
			onTick(0);
		});
	}
	
	/** 构造根logger(放置warning) */
	public static void makeRootLogger()
	{
		Properties pp=new Properties();
		
		pp.setProperty("log4j.rootLogger","info,file");
		pp.setProperty("log4j.appender.file","org.apache.log4j.DailyRollingFileAppender");
		pp.setProperty("log4j.appender.file.File",getLogPath("log",ShineSetting.logNeedDir));
		pp.setProperty("log4j.appender.file.encoding","UTF-8");
		pp.setProperty("log4j.appender.file.layout","org.apache.log4j.PatternLayout");
		pp.setProperty("log4j.appender.file.layout.ConversionPattern","%m");
		
		PropertyConfigurator.configure(pp);
	}

	/** 获取一个log路径 */
	private static String getLogPath(String name,boolean needDir)
	{
		if(needDir)
			return ShineGlobal.logPath + File.separator + name + File.separator + name + "_" + ShineGlobal.processName + ".log";
		else
			return ShineGlobal.logPath + File.separator + name + "_" + ShineGlobal.processName + ".log";
	}
	
	/** 获取一个logger(isText:是否纯文字,不走格式) */
	private static Logger getLogger(String name,boolean needDir)
	{
		return getLoggerForPath(name,getLogPath(name,needDir));
	}
	
	/** 通过完整路径获取log */
	private static Logger getLoggerForPath(String name,String path)
	{
		//String name="file" + _logAtomic.getAndIncrement();
		Properties pp=new Properties();
		
		pp.setProperty("log4j.logger." + name,"info," + name);
		pp.setProperty("log4j.additivity." + name,"false");
		pp.setProperty("log4j.appender." + name,"org.apache.log4j.DailyRollingFileAppender");
		pp.setProperty("log4j.appender." + name + ".File",path);
		pp.setProperty("log4j.appender." + name + ".encoding","UTF-8");
		pp.setProperty("log4j.appender." + name + ".layout","org.apache.log4j.PatternLayout");
		
		pp.setProperty("log4j.appender." + name + ".layout.ConversionPattern","%m");
		
		PropertyConfigurator.configure(pp);
		
		return Logger.getLogger(name);
		//		return LogManager.getLogger(name);
	}
	
	/** 获取主日志 */
	public static synchronized SLogger getMain()
	{
		SLogger re=LogControl.createLoggerFunc.apply();
		re._log=Logger.getLogger("file");
		re._needTime=true;
		_list.add(re);
		
		return re;
	}
	
	/** 创建logger */
	public static synchronized SLogger create(String name,boolean needDir)
	{
		SLogger re=LogControl.createLoggerFunc.apply();
		re._log=getLogger(name,needDir);
		
		_list.add(re);
		
		return re;
	}
}
