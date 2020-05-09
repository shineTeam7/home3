package com.home.shine.control;

import com.home.shine.global.ShineSetting;
import com.home.shine.support.SLogger;
import com.home.shine.support.func.ObjectFunc;

/** 日志控制 */
public class LogControl
{
	/** 主日志 */
	private static SLogger _log=null;
	/** 错误日志 */
	private static SLogger _errorLog=null;
	/** 客户端主日志 */
	private static SLogger _clientLog=null;
	/** 客户端错误日志 */
	private static SLogger _clientErrorLog=null;
	/** 运行日志 */
	private static SLogger _runningLog=null;
	/** 消息统计日志 */
	private static SLogger _messageLog=null;
	/** 客户端行为日志 */
	private static SLogger _actionLog=null;
	
	/** 构建logger方法 */
	public static ObjectFunc<SLogger> createLoggerFunc=SLogger::new;
	
	/** 初始化 */
	public static void init()
	{
		if(ShineSetting.needLog)
		{
			SLogger.makeRootLogger();
			
			_log=SLogger.getMain();
			
			_errorLog=SLogger.create("errorLog",false);
			
			_clientLog=SLogger.create("clientLog",false);
			
			_clientErrorLog=SLogger.create("clientErrorLog",false);
			
			_runningLog=SLogger.create("runningLog",false);
			
			_messageLog=SLogger.create("messageLog",false);
			
			_actionLog=SLogger.create("actionLog",true);
		}
	}
	
	/** 启动(也视为初始化后续) */
	public static void start()
	{
		ThreadControl.getLogThread().getTimeDriver().setInterval(SLogger::onTick,ShineSetting.logFlushDelay);
	}
	
	/** 日志输出 */
	public static void log(String info)
	{
		if(_log==null)
			return;
		
		_log.log(info);
	}
	
	/** 错误日志输出 */
	public static void errorLog(String info)
	{
		if(_errorLog==null)
			return;
		
		_errorLog.log(info);
	}
	
	/** 运行日志输出 */
	public static void runningLog(String info)
	{
		if(_runningLog==null)
			return;
		
		_runningLog.log(info);
	}
	
	/** 消息日志输出 */
	public static void messageLog(String info)
	{
		if(_messageLog==null)
			return;
		
		_messageLog.log(info);
	}
	
	/** 客户端日志输出 */
	public static void clientLog(String info)
	{
		if(_clientLog==null)
			return;
		
		_clientLog.log(info);
	}
	
	/** 客户端错误日志输出 */
	public static void clientErrorLog(String info)
	{
		if(_clientErrorLog==null)
			return;
		
		_clientErrorLog.log(info);
	}
	
	/** 客户端行为日志 */
	public static void actionLog(String info)
	{
		if(_actionLog==null)
			return;
		
		_actionLog.log(info);
	}
}
