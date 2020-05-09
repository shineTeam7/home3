package com.home.shine.support.collection;

import com.home.shine.utils.ObjectUtils;

/** 整形队列 */
public class IntQueue extends BaseQueue
{
	private int[] _values;
	
	private int _defaultValue=0;
	
	public IntQueue()
	{
		_values=ObjectUtils.EmptyIntArr;
	}
	
	public IntQueue(int capacity)
	{
		init(countCapacity(capacity));
	}
	
	public int capacity()
	{
		return _values.length;
	}
	
	public final int[] getValues()
	{
		return _values;
	}
	
	public int getDefaultValue()
	{
		return _defaultValue;
	}
	
	public void setDefaultValue(int v)
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
		_values=new int[capacity];
		_mark=capacity-1;
		_size=0;
	}
	
	private void remake(int capacity)
	{
		int[] n=new int[capacity];
		
		if(_size!=0)
		{
			int[] values=_values;
			
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
	public void offer(int v)
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
	
	/** 取出 */
	public int poll()
	{
		if(_size==0)
		{
			return _defaultValue;
		}
		
		int v=_values[_start];

		if(++_start==_values.length)
		{
			_start=0;
		}
		
		--_size;
		
		return v;
	}
	
	public int get(int index)
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
