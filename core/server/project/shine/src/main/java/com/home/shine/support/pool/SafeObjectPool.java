package com.home.shine.support.pool;

import com.home.shine.global.ShineSetting;
import com.home.shine.support.func.ObjectFunc;

/** 对象池(线程安全) */
public class SafeObjectPool<T> extends ObjectPool<T>
{
	public SafeObjectPool(ObjectFunc createFunc)
	{
		super(createFunc);
	}
	
	public SafeObjectPool(ObjectFunc createFunc,int size)
	{
		super(createFunc,size);
	}
	
	@Override
	public T getOne()
	{
		synchronized(this)
		{
			return super.getOne();
		}
	}
	
	public void back(T obj)
	{
		if(!ShineSetting.useObjectPool)
		{
			return;
		}
		
		synchronized(this)
		{
			super.back(obj);
		}
	}
}
