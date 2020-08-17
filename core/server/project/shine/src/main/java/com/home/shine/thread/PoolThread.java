package com.home.shine.thread;

import com.home.shine.constlist.ThreadType;

/** 池线程 */
public class PoolThread extends CoreThread
{
	public PoolThread(int index)
	{
		super("poolThread-"+index,ThreadType.Pool,index);
	}
	
}
