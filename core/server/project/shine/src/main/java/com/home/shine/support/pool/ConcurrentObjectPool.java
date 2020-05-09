package com.home.shine.support.pool;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.home.shine.global.ShineSetting;

public abstract class ConcurrentObjectPool<T> implements IPool<T>
{
	/** 是否开启 */
	private boolean _enable=true;
	
	private ConcurrentLinkedQueue<T> _queue=new ConcurrentLinkedQueue<>();
	
	private int _size;
	
	private AtomicInteger _length=new AtomicInteger(0);
	
	public ConcurrentObjectPool()
	{
		this(512);
	}
	
	public ConcurrentObjectPool(int size)
	{
		_queue=new ConcurrentLinkedQueue<>();
		_size=size;
		
		if(!ShineSetting.useObjectPool)
		{
			_enable=false;
		}
	}
	
	public void setEnable(boolean value)
	{
		if(!ShineSetting.useObjectPool)
		{
			_enable=false;
		}
		else
		{
			_enable=value;
		}
	}
	
	abstract protected T createOne();
	
	/** 取出一个 */
	public T getOne()
	{
		if(!_enable)
		{
			return createOne();
		}
		
		T obj=_queue.poll();
		
		if(obj!=null)
		{
			_length.decrementAndGet();
			
			return obj;
		}
		else
		{
			return createOne();
		}
	}
	
	/** 放回一个 */
	public void back(T obj)
	{
		if(!_enable)
		{
			return;
		}
		
		int length=_length.get();
		
		if(length >= _size)
		{
			return;
		}
		
		//池化对象
		if(obj instanceof IPoolObject)
		{
			((IPoolObject)obj).clear();
		}
		
		_length.incrementAndGet();
		
		_queue.offer(obj);
	}
}
