package com.home.shine.constlist;

/** 表状态类型 */
public class TableStateType
{
	/** 正常(执行update) */
	public static final int Normal=1;
	/** 需要插入 */
	public static final int NeedInsert=2;
	/** 需要删除 */
	public static final int NeedDelete=3;
	/** 需要移除(插入前就删除，所以只移除即可) */
	public static final int Remove=4;
}
