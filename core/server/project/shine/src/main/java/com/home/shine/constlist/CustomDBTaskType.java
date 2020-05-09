package com.home.shine.constlist;

/** 自定义数据任务类型(需分表执行的,主要是自定义select) */
public class CustomDBTaskType
{
	/** 读取全部 */
	public static final int LoadAll=0;
	/** 读取指定主键 */
	public static final int LoadCustomKey=1;
	/** 读取自定义语句 */
	public static final int LoadCustomSql=2;
}
