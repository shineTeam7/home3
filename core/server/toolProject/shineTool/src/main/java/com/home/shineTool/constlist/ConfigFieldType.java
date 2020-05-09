package com.home.shineTool.constlist;

/** 配置表属性类型 */
public class ConfigFieldType
{
	public static final int Boolean=1;
	
	public static final int Byte=2;
	
	public static final int UByte=3;
	
	public static final int Short=4;
	
	public static final int UShort=5;
	
	public static final int Int=6;
	
	public static final int Long=7;
	
	public static final int Float=8;
	
	public static final int Double=9;
	
	public static final int String=10;
	/** 双整形 */
	public static final int DInt=11;
	/** 千分位ThousandsPercent(暂未实现) */
	public static final int TP=12;
	/** 万分位WanPercent(暂未实现) */
	public static final int WP=13;
	/** 大数(Float) */
	public static final int BigFloat=14;
	
	//collections
	/** 数组 */
	public static final int Array=20;
	
	/** 是否数字类型 */
	public static boolean isNumberType(int type)
	{
		switch(type)
		{
			case Byte:
			case UByte:
			case Short:
			case UShort:
			case Int:
			case Long:
			case Float:
			case Double:
				return true;
		}
		
		return false;
	}
}
