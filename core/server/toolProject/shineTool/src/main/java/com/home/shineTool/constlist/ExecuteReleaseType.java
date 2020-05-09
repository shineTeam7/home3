package com.home.shineTool.constlist;

/** 工具执行发布期方式 */
public class ExecuteReleaseType
{
	/** 非线上 */
	public static final int Debug=0;
	/** 标准 */
	public static final int Normal=1;
	/** 强更协议 */
	public static final int WithOutMessage=2;
	/** 强更全部(协议,数据库,配置表) */
	public static final int AllRefresh=3;
	
	/** 全清空 */
	public static final int AllClear=4;
	/** 全清空包括c层 */
	public static final int AllClearAndCommon=5;
	
	/** 是发布模式 */
	public static boolean isRelease(int type)
	{
		switch(type)
		{
			case Normal:
			case WithOutMessage:
			case AllRefresh:
				return true;
		}
		
		return false;
	}
}
