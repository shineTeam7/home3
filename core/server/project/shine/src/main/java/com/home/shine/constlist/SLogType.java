package com.home.shine.constlist;

/** clientLog类型 */
public class SLogType
{
	/** 标准日志 */
	public static final int Normal=1;
	/** 警告日志 */
	public static final int Warning=2;
	/** 错误日志 */
	public static final int Error=3;
	
	/** debug日志 */
	public static final int Debug=4;
	/** 运行日志 */
	public static final int Running=5;
	/** 行为日志 */
	public static final int Action=6;
	
	/** 获取标记头 */
	public static String getMark(int type)
	{
		switch(type)
		{
			case Normal:
				return "";
			case Warning:
				return "warn:";
			case Error:
				return "error:";
			case Debug:
				return "debug:";
			case Running:
				return "running:";
		}
		
		return "";
	}
	
	/** 获取标记头 */
	public static String getClientMark(int type)
	{
		switch(type)
		{
			case Normal:
				return "client:";
			case Warning:
				return "clientWarn:";
			case Error:
				return "clientError:";
		}
		
		return "client:";
	}
}
