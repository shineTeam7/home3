package com.home.shine.support.collection;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.utils.MathUtils;
import com.koloboke.collect.impl.hash.LHash;

/** 基于koloboke的hash集合组基类 */
public class BaseHash
{
	protected static final int _minSize=2;
	
	protected int _size;
	
	/** 给基类使用 */
	protected int _maxSize;
	
	/** 遍历版本 */
	protected int _version=0;
	
	/** 上个free序号 */
	protected int _lastFreeIndex=-1;
	
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
	
	public int capacity()
	{
		return _maxSize;
	}
	
	/** 获取数组长度 */
	protected int getArrCapacity()
	{
		return _maxSize<<1;
	}
	
	/** 是否空 */
	public final boolean isEmpty()
	{
		return _size==0;
	}
	
	/** 只清空size(前提是其他数据自行清理了,只底层用) */
	public final void justClearSize()
	{
		_size=0;
		++_version;
		_lastFreeIndex=-1;
	}
	
	protected final int hashChar(char arg)
	{
		return LHash.SeparateKVCharKeyMixing.mix(arg);
	}
	
	protected final int hashLong(long arg)
	{
		return LHash.SeparateKVLongKeyMixing.mix(arg);
	}
	
	protected final int hashInt(int arg)
	{
		return LHash.SeparateKVIntKeyMixing.mix(arg);
	}
	
	protected final int hashObj(Object obj)
	{
		return LHash.ParallelKVObjKeyMixing.mix(obj.hashCode());
	}
	
	/** 拷贝基础,为了clone系列 */
	public void copyBase(BaseHash target)
	{
		_size=target._size;
		_maxSize=target._maxSize;
		_version=0;
	}
	
	/** 清空 */
	public void clear()
	{
		_size=0;
		++_version;
		_lastFreeIndex=-1;
	}
	
	protected void rehash(int size)
	{
		++_version;
		_lastFreeIndex=-1;
	}
	
	protected final void postInsertHook(int index)
	{
		++_version;
		
		if(_lastFreeIndex>=0 && _lastFreeIndex==index)
		{
			_lastFreeIndex=-1;
		}
		
		if((++_size)>(_maxSize))
		{
			rehash(getArrCapacity() << 1);
		}
	}
	
	protected final void postRemoveHook(int index)
	{
		--_size;
		++_version;
		
		if(_lastFreeIndex>=0 && index>_lastFreeIndex)
		{
			_lastFreeIndex=-1;
		}
	}
	
	/** 获取上个自由index */
	public int getLastFreeIndex()
	{
		if(isEmpty())
			return -1;
		
		if(_lastFreeIndex==-1)
			return _lastFreeIndex=toGetLastFreeIndex();
		
		return _lastFreeIndex;
	}
	
	protected int toGetLastFreeIndex()
	{
		return -1;
	}
	
	protected void getLastFreeIndexError()
	{
		Ctrl.errorLog("impossible");
	}
	
	public class BaseIterator
	{
		protected int _index;
		protected int _tSafeIndex;
		
		public BaseIterator()
		{
			_index=_tSafeIndex=getLastFreeIndex();
			if(_size==0)
				_index=_tSafeIndex+1;
		}
	}
}
