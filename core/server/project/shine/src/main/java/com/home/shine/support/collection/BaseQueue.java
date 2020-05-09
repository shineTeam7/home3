package com.home.shine.support.collection;

public class BaseQueue extends BaseList
{
	protected int _mark;
	
	protected int _start;
	
	protected int _end;
	
	/** 获取长度标记 */
	public int getMark()
	{
		return _mark;
	}
	
	public int getStart()
	{
		return _start;
	}
	
	public int getEnd()
	{
		return _end;
	}
}
