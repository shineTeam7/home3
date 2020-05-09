package com.home.shine.support.pool;

import com.home.shine.control.ThreadControl;
import com.home.shine.global.ShineSetting;
import com.home.shine.thread.AbstractThread;

/** stringBuilder池 */
public class StringBuilderPool extends ObjectPool<StringBuilder>
{
	/** 回车 */
	public static final String Enter="\r\n";
	/** 换行 */
	public static final String Tab="\t";
	
	public StringBuilderPool()
	{
		super(StringBuilder::new);
	}
	
	@Override
	public void back(StringBuilder obj)
	{
		//超出的不回收了
		if(obj.length()>= ShineSetting.stringBuilderPoolKeepSize)
			return;
		
		obj.setLength(0);
		
		super.back(obj);
	}
	
	/** 取一个 */
	public static StringBuilder create()
	{
		AbstractThread thread=ThreadControl.getCurrentShineThread();
		
		if(thread!=null)
		{
			return thread.stringBuilderPool.getOne();
		}
		else
		{
			return new StringBuilder();
		}
	}
	
	/** 取一个 */
	public static StringBuilder create(String str)
	{
		StringBuilder sb=create();
		sb.append(str);
		return sb;
	}
	
	/** 还一个 */
	public static void release(StringBuilder obj)
	{
		AbstractThread thread=ThreadControl.getCurrentShineThread();
		
		if(thread!=null)
		{
			thread.stringBuilderPool.back(obj);
		}
	}
	
	/** 还一个并返回该StringBuilder的字符串 */
	public static String releaseStr(StringBuilder obj)
	{
		String v=obj.toString();

		release(obj);

		return v;
	}
}
