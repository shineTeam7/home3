package com.home.shine.support.pool;

import com.home.shine.bytes.BytesWriteStream;
import com.home.shine.control.ThreadControl;
import com.home.shine.global.ShineSetting;
import com.home.shine.thread.AbstractThread;

/** 字节写流池 */
public class BytesWriteStreamPool extends ObjectPool<BytesWriteStream>
{
	public BytesWriteStreamPool()
	{
		super(BytesWriteStream::create);
	}
	
	@Override
	public void back(BytesWriteStream obj)
	{
		if(obj.getBuf().length>ShineSetting.bytesWriteStreamPoolKeepSize)
			return;
		
		super.back(obj);
	}
	
	/** 取一个 */
	public static BytesWriteStream create()
	{
		AbstractThread thread=ThreadControl.getCurrentShineThread();
		
		if(thread!=null)
		{
			return thread.bytesWriteStreamPool.getOne();
		}
		else
		{
			return BytesWriteStream.create();
		}
	}
	
	/** 还一个 */
	public static void release(BytesWriteStream obj)
	{
		AbstractThread thread=ThreadControl.getCurrentShineThread();
		
		if(thread!=null)
		{
			thread.bytesWriteStreamPool.back(obj);
		}
	}
}
