package com.home.shineTool.constlist;

import com.home.shine.utils.ObjectUtils;

import java.util.Arrays;

/** 变量类型定义 */
public class VarType
{
	/** 空 */
	public static final int Null=0;
	public static final int Object=1;
	
	//基础类型
	public static final int Boolean=2;
	public static final int Byte=3;
	public static final int UByte=4;
	public static final int Short=5;
	public static final int UShort=6;
	public static final int Int=7;
	public static final int Long=8;
	public static final int Float=9;
	public static final int Double=10;
	public static final int String=11;
	public static final int Char=12;
	
	//集合类型
	public static final int Array=13;
	public static final int List=14;
	public static final int Set=15;
	public static final int Map=16;
	public static final int Queue=17;
	
	/** 自定义对象 */
	public static final int CustomObject=18;
	
	public static final int size=20;
	
	private static int[] _upgrateArr=new int[]{Byte,Short,Int,Long};
	
	/** 是否是基础类型(包括String) */
	public static boolean isBaseType(int type)
	{
		return type >= Boolean && type<=String;
	}
	
	/** 是基础类型或自定义类型 */
	public static boolean isBaseOrCustomType(int type)
	{
		return isBaseType(type) || type==CustomObject;
	}
	
	/** 类型是否可升级 */
	public static boolean canUpgrade(int from,int to)
	{
		int index1=ObjectUtils.intArrayIndexOf(_upgrateArr,from);
		
		if(index1==-1)
			return false;
		
		int index2=ObjectUtils.intArrayIndexOf(_upgrateArr,to);
		
		if(index2==-1)
			return false;
		
		return index2>=index1;
	}
	
}
