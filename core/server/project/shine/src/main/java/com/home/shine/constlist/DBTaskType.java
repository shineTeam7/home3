package com.home.shine.constlist;

/** 数据库任务类型 */
public class DBTaskType
{
	/** 增 */
	public static final int Insert=0;
	/** 改 */
	public static final int Update=1;
	/** 删 */
	public static final int Delete=2;
	/** 删 */
	public static final int Delete2=3;
	/** 查 */
	public static final int Select=4;
	/** 查(索引1) */
	public static final int Select2=5;
	/** 查(索引2) */
	public static final int Select3=6;
	/** 自定义query */
	public static final int CustomQuery=7;
	
	/** 标准数(2^n) */
	public static final int num=8;
	/** 标准数(2^n) */
	public static final int shift=3;
	
	/** 是否是查语句 */
	public static boolean isSelect(int type)
	{
		return type >= Select && type<=Select3;
	}
	
	/** 是否是bool返回 */
	public static boolean isBooleanCallback(int type)
	{
		return type >= Insert && type<=Select3;
	}
}
