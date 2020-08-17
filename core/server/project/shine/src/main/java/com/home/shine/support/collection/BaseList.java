package com.home.shine.support.collection;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.utils.MathUtils;

public class BaseList
{
	protected static final int _minSize=4;
	
	/** 容量 */
	protected int _capacity=0;
	
	protected int _size=0;
	
	protected int countCapacity(int capacity)
	{
		capacity=MathUtils.getPowerOf2(capacity);
		
		if(capacity<_minSize)
		{
			capacity=_minSize;
		}
		
		return capacity;
	}
	
	protected void init(int capacity)
	{
		_capacity=capacity;
		
		Ctrl.throwError("should be override");
	}
	
	public void clear()
	{
		_size=0;
	}
	
	/** 清空+缩容 */
	public void reset()
	{
		if(_size==0)
		{
			if(_capacity<=_minSize)
				return;
		}
		else
		{
			_size=0;
		}
		
		init(0);
	}
	
	protected void remake(int size)
	{
	
	}
	
	/** 扩容 */
	public final void ensureCapacity(int capacity)
	{
		if(capacity>_capacity)
		{
			remake(countCapacity(capacity));
		}
	}
	
	/** 缩容 */
	public final void shrink()
	{
		if(_size==0)
		{
			if(_capacity<=_minSize)
				return;
			
			init(0);
		}
		else
		{
			int capacity=countCapacity(_size);
			
			if(capacity<_capacity)
			{
				remake(capacity);
			}
		}
	}
	
	protected void addCapacity()
	{
		if(_capacity==0)
			init(_minSize);
		else if(_size==_capacity)
			remake(_capacity<<1);
	}
	
	protected void addCapacity(int n)
	{
		if(_capacity==0)
			init(_minSize);
		else if(_size+n>_capacity)
			remake(_capacity<<1);
	}
	
	public final int capacity()
	{
		return _capacity;
	}
	
	/** 尺寸 */
	public final int size()
	{
		return _size;
	}
	
	/** 尺寸 */
	public final int length()
	{
		return _size;
	}
	
	/** 是否空 */
	public final boolean isEmpty()
	{
		return _size==0;
	}
	
	/** 只清空size(前提是其他数据自行清理了,只看懂底层的用) */
	public final void justClearSize()
	{
		_size=0;
	}
	
	/** 只设置尺寸 */
	public final void justSetSize(int value)
	{
		_size=value;
	}
}
