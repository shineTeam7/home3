package com.home.shine.support.pool;

import com.home.shine.control.ThreadControl;
import com.home.shine.data.BigNumberData;
import com.home.shine.thread.AbstractThread;

/** 大数临时池 */
public class BigNumberPool extends ObjectPool<BigNumberData>
{
	public BigNumberPool()
	{
		super(BigNumberData::new);
	}
	
	/** 取一个 */
	public static BigNumberData create()
	{
		AbstractThread thread=ThreadControl.getCurrentShineThread();
		
		BigNumberData re;
		
		if(thread!=null)
		{
			re=thread.bigNumberPool.getOne();
		}
		else
		{
			re=new BigNumberData();
		}
		
		re.clear();
		return re;
	}
	
	/** 还一个 */
	public static void release(BigNumberData obj)
	{
		AbstractThread thread=ThreadControl.getCurrentShineThread();
		
		if(thread!=null)
		{
			thread.bigNumberPool.back(obj);
		}
	}
}