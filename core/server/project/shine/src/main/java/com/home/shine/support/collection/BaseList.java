package com.home.shine.support.collection;

import com.home.shine.utils.MathUtils;

public class BaseList
{
	protected static final int _minSize=4;
	
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
