package com.home.shineTool.constlist;

/** 构造方法类型 */
public class MakeMethodType
{
	/** 完整读 */
	public static final int readFull=1;
	/** 完整写 */
	public static final int writeFull=2;
	/** 简版读 */
	public static final int readSimple=3;
	/** 简版写 */
	public static final int writeSimple=4;
	/** 复制(潜拷) */
	public static final int shadowCopy=5;
	/** 复制(深拷) */
	public static final int copy=6;
	/** 自身复制(深拷) */
	public static final int copySelf=7;
	/** 数据相等判定 */
	public static final int dataEquals=8;
	/** 输出数据字符串 */
	public static final int toWriteDataString=9;
	/** 初始化初值 */
	public static final int initDefault=10;
	/** 初始化属性组(ListData用) */
	public static final int initFields=11;
	/** 清空 */
	public static final int clear=12;
	/** 析构 */
	public static final int release=13;
	
	/** 数目 */
	public static final int num=14;
	
	public static boolean useMData(int type)
	{
		switch(type)
		{
			case shadowCopy:
			case copy:
			case copySelf:
			case dataEquals:
			{
				return true;
			}
		}
		
		return false;
	}
	
	/** 是否必须写(反之就是可空隐藏) */
	public static boolean mustWrite(int type)
	{
		switch(type)
		{
			case readFull:
			case writeFull:
			case initFields:
			{
				return true;
			}
		}
		
		return false;
	}
}
