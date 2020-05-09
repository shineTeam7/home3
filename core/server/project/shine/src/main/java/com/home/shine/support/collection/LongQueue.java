package com.home.shine.support.collection;

import com.home.shine.utils.ObjectUtils;

public class LongQueue extends BaseQueue
{
	private long[] _values;
	
	private long _defaultValue=-1L;
	
	public LongQueue()
	{
		_values=ObjectUtils.EmptyLongArr;
	}
	
	public LongQueue(int capacity)
	{
		init(countCapacity(capacity));
	}
	
	public int capacity()
	{
		return _values.length;
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
	
	/** 扩容 */
	public void ensureCapacity(int capacity)
	{
		if(_values==null)
		{
			init(countCapacity(capacity));
		}
		else if(capacity>_values.length)
		{
			remake(countCapacity(capacity));
		}
	}
	
	private void init(int capacity)
	{
		_values=new long[capacity];
		_mark=capacity-1;
		_size=0;
	}
	
	private void remake(int capacity)
	{
		long[] n=new long[capacity];
		long[] values=_values;
		
		if(_size!=0)
		{
			if(_start<_end)
			{
				System.arraycopy(values,_start,n,0,_end - _start);
			}
			else
			{
				int d=values.length - _start;
				System.arraycopy(values,_start,n,0,d);
				System.arraycopy(values,0,n,d,_end);
			}
		}
		
		_start=0;
		_end=_size;
		
		_values=n;
		_mark=capacity-1;
	}
	
	/** 放入 */
	public void offer(long v)
	{
		if(_values.length==0)
			init(_minSize);
		else if(_size==_values.length)
			remake(_values.length<<1);
		
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
