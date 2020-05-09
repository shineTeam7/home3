package com.home.shine.support;

import com.home.shine.global.ShineSetting;
import com.home.shine.support.pool.StringBuilderPool;

/** 字符写出类 */
public class DataWriter
{
	/** 回车 */
	private static final String Enter="\r\n";
	/** 换行 */
	private static final String Tab="\t";
	
	public StringBuilder sb;
	
	protected int _off=0;
	
	public DataWriter()
	{
		sb=StringBuilderPool.create();
	}
	
	/** 释放并获取string */
	public String releaseStr()
	{
		return StringBuilderPool.releaseStr(sb);
	}
	
	private void writeSomeTab(int num)
	{
		if(ShineSetting.needDataStringOneLine)
			return;
		
		if(num==0)
			return;
		
		for(int i=0;i<num;i++)
		{
			sb.append(Tab);
		}
	}
	
	/** 写左边大括号(右缩进) */
	public void writeEnter()
	{
		if(ShineSetting.needDataStringOneLine)
		{
			//换空格
			sb.append(' ');
			return;
		}
		
		sb.append(Enter);
	}
	
	public void writeTabs()
	{
		writeSomeTab(_off);
	}
	
	/** 写左边大括号(右缩进) */
	public void writeLeftBrace()
	{
		writeSomeTab(_off);
		sb.append("{");
		writeEnter();
		_off++;
	}
	
	/** 写右边大括号(右缩进) */
	public void writeRightBrace()
	{
		_off--;
		writeSomeTab(_off);
		sb.append("}");
		//writeEnter();
		//TODO:这里不空格,在外面调空格,如有问题再改
	}
	
	/** 写空行 */
	public void writeEmptyLine()
	{
		writeSomeTab(_off);
		writeEnter();
	}
	
	/** 写自定义行 */
	public void writeCustom(String content)
	{
		writeSomeTab(_off);
		sb.append(content);
		writeEnter();
	}
}
