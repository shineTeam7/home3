package com.home.shine.support.collection;

import com.home.shine.utils.ObjectUtils;

public class LongQueue extends BaseQueue
{
	private long[] _values;
	
	private long _defaultValue=-1L;
	
	public LongQueue()
	{
		init(0);
	}
	
	public LongQueue(int capacity)
	{
		init(countCapacity(capacity));
	}
	
	public final long[] getValues()
	{
		return _values;
	}
	
	public long getDefaultValue()
	{
		return _defaultValue;
	}
	
	public void setDefaultValue(long v)
	{
		_defaultValue=v;
	}
	
	@Override
	protected void init(int capacity)
	{
		_capacity=capacity;
		
		if(capacity==0)
			_values=ObjectUtils.EmptyLongArr;
		else
			_values=new long[capacity];
		
		_mark=capacity-1;
	}
	
	@Override
	protected void remake(int capacity)
	{
		long[] oldArr=_values;
		init(capacity);
		
		if(_size!=0)
		{
			long[] values=_values;
			
			if(_start<_end)
			{
				System.arraycopy(oldArr,_start,values,0,_end - _start);
			}
			else
			{
				int d=oldArr.length - _start;
				System.arraycopy(oldArr,_start,values,0,d);
				System.arraycopy(oldArr,0,values,d,_end);
			}
		}
		
		_start=0;
		_end=_size;
	}
	
	/** 放入 */
	public void offer(long v)
	{
		addCapacity();
		
		_values[_end]=v;
		
		if(++_end==_values.length)
		{
			_end=0;
		}
		
		++_size;
	}
	
	/** 查看顶元素 */
	public long peek()
	{
		if(_size==0)
			return 0;
		
		return _values[_start];
	}
	
	/** 取出 */
	public long poll()
	{
		if(_size==0)
		{
			return _defaultValue;
		}
		
		long v=_values[_start];
		
		if(++_start==_values.length)
		{
			_start=0;
		}
		
		--_size;
		
		return v;
	}
	
	public long get(int index)
	{
		if(index>=_size)
			return 0;
		
		return _values[(_start + index) & _mark];
	}
	
	/** 清空 */
	public void clear()
	{
		if(_size==0)
		{
			return;
		}
		
		_size=0;
		_start=0;
		_end=0;
	}
}
