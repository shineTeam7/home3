package com.home.shine.support.collection;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.inter.ILongConsumer;
import com.home.shine.utils.MathUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class LongSet extends BaseHash implements Iterable<Long>
{
	private long[] _set;

	private long _freeValue;
	
	public LongSet()
	{
		init(_minSize);
	}
	
	public LongSet(int capacity)
	{
		init(countCapacity(capacity));
	}
	
	public final long getFreeValue()
	{
		return _freeValue;
	}

	public final long[] getKeys()
	{
		return _set;
	}
	
	@Override
	protected void init(int capacity)
	{
		_capacity=capacity;

		_set=new long[capacity<<1];
		
		if(_freeValue!=0)
		{
			Arrays.fill(_set,_freeValue);
		}
	}
	
	private long changeFree()
	{
		long newFree=findNewFreeOrRemoved();
		
		long free=_freeValue;
		long[] keys=_set;
		for(int i=(keys.length) - 1;i >= 0;--i)
		{
			if(keys[i]==free)
			{
				keys[i]=newFree;
			}
		}
		
		_freeValue=newFree;
		return newFree;
	}
	
	public void setFreeValue(long value)
	{
		_freeValue=value;
	}
	
	private long findNewFreeOrRemoved()
	{
		long free=_freeValue;

		long newFree;
		{
			do
			{
				newFree=MathUtils.randomInt();
			}
			while((newFree==free) || ((index(newFree)) >= 0));
		}
		return newFree;
	}
	
	private int index(long key)
	{
		long free;
		if(key!=(free=_freeValue))
		{
			long[] keys=_set;
			int capacityMask;
			int index;
			long cur;
			if((cur=keys[(index=hashLong(key) & (capacityMask=(keys.length) - 1))])==key)
			{
				return index;
			}
			else
			{
				if(cur==free)
				{
					return -1;
				}
				else
				{
					while(true)
					{
						if((cur=keys[(index=(index - 1) & capacityMask)])==key)
						{
							return index;
						}
						else if(cur==free)
						{
							return -1;
						}
					}
				}
			}
		}
		else
		{
			return -1;
		}
	}
	
	@Override
	protected int toGetLastFreeIndex()
	{
		long free=_freeValue;
		long[] keys=_set;
		
		int capacityMask=keys.length-1;
		
		for(int i=capacityMask;i >= 0;--i)
		{
			if(keys[i]==free)
			{
				return i;
			}
		}
		
		getLastFreeIndexError();
		
		return -1;
	}
	
	@Override
	protected void rehash(int newCapacity)
	{
		++_version;
		long free=_freeValue;
		long[] keys=_set;
		init(newCapacity);
		long[] newKeys=_set;
		int capacityMask=(newKeys.length) - 1;
		for(int i=(keys.length) - 1;i >= 0;--i)
		{
			long key;
			if((key=keys[i])!=free)
			{
				int index;
				if((newKeys[(index=hashLong(key) & capacityMask)])!=free)
				{
					while(true)
					{
						if((newKeys[(index=(index - 1) & capacityMask)])==free)
						{
							break;
						}
					}
				}
				newKeys[index]=key;
			}
		}
	}
	
	public boolean add(long key)
	{
		long free;
		if(key==(free=_freeValue))
		{
			free=changeFree();
		}
		long[] keys=_set;
		int capacityMask;
		int index;
		long cur;
		keyAbsent:
		if((cur=keys[(index=hashLong(key) & (capacityMask=(keys.length) - 1))])!=free)
		{
			if(cur==key)
			{
				return false;
			}
			else
			{
				while(true)
				{
					if((cur=keys[(index=(index - 1) & capacityMask)])==free)
					{
						break keyAbsent;
					}
					else if(cur==key)
					{
						return false;
					}
				}
			}
		}
		keys[index]=key;
		postInsertHook(index);
		return true;
	}
	
	public boolean contains(long key)
	{
		if(_size==0)
			return false;
		
		return (index(key)) >= 0;
	}
	
	public boolean remove(long key)
	{
		if(_size==0)
			return false;
		
		long free;
		if(key!=(free=_freeValue))
		{
			long[] keys=_set;
			int capacityMask=(keys.length) - 1;
			int index;
			long cur;
			keyPresent:
			if((cur=keys[(index=hashLong(key) & capacityMask)])!=key)
			{
				if(cur==free)
				{
					return false;
				}
				else
				{
					while(true)
					{
						if((cur=keys[(index=(index - 1) & capacityMask)])==key)
						{
							break keyPresent;
						}
						else if(cur==free)
						{
							return false;
						}
					}
				}
			}
			int indexToRemove=index;
			int indexToShift=indexToRemove;
			int shiftDistance=1;
			while(true)
			{
				indexToShift=(indexToShift - 1) & capacityMask;
				long keyToShift;
				if((keyToShift=keys[indexToShift])==free)
				{
					break;
				}
				if(((hashLong(keyToShift) - indexToShift) & capacityMask) >= shiftDistance)
				{
					keys[indexToRemove]=keyToShift;
					indexToRemove=indexToShift;
					shiftDistance=1;
				}
				else
				{
					shiftDistance++;
					if(indexToShift==(1 + index))
					{
						throw new ConcurrentModificationException();
					}
				}
			}
			keys[indexToRemove]=free;
			postRemoveHook(indexToRemove);
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/** 清空 */
	public void clear()
	{
		if(_size==0)
			return;
		
		justClearSize();
		
		Arrays.fill(_set,_freeValue);
	}
	
	public LongSet clone()
	{
		if(_size==0)
			return new LongSet();
		
		LongSet re=new LongSet(this.capacity());
		System.arraycopy(_set,0,re._set,0,_set.length);
		re.copyBase(this);
		re._freeValue=_freeValue;
		
		return re;
	}
	
	/** 遍历 */
	public void forEachA(ILongConsumer consumer)
	{
		if(_size==0)
		{
			return;
		}

		int version=_version;
		long free=_freeValue;
		long[] keys=_set;
		for(int i=(keys.length) - 1;i >= 0;i--)
		{
			long key;
			if((key=keys[i])!=free)
			{
				consumer.accept(key);
			}
		}
		
		if(version!=_version)
		{
			Ctrl.throwError("ForeachModificationException");
		}
	}
	
	/** 可修改遍历 */
	public void forEachS(ILongConsumer consumer)
	{
		if(_size==0)
		{
			return;
		}
		
		long free=_freeValue;
		long[] keys=_set;
		long key;
		int safeIndex=getLastFreeIndex();
		
		for(int i=safeIndex-1;i>=0;--i)
		{
			if((key=keys[i])!=free)
			{
				consumer.accept(key);
				
				if(key!=keys[i])
				{
					++i;
				}
			}
		}
		
		for(int i=keys.length-1;i>safeIndex;--i)
		{
			if((key=keys[i])!=free)
			{
				consumer.accept(key);
				
				if(key!=keys[i])
				{
					++i;
				}
			}
		}
	}
	
	/** 遍历并清空 */
	public void forEachAndClear(ILongConsumer consumer)
	{
		if(_size==0)
		{
			return;
		}
		
		long free=_freeValue;
		long[] keys=_set;
		for(int i=(keys.length) - 1;i >= 0;i--)
		{
			long key;
			if((key=keys[i])!=free)
			{
				consumer.accept(key);
				keys[i]=free;
			}
		}
		
		justClearSize();
	}
	
	/** 转化为原生集合 */
	public HashSet<Long> toNatureSet()
	{
		HashSet<Long> re=new HashSet<>(size());
		
		long[] keys=_set;
		long fv=_freeValue;
		long k;
		
		for(int i=keys.length-1;i>=0;--i)
		{
			if((k=keys[i])!=fv)
			{
				re.add(k);
			}
		}
		
		return re;
	}
	
	public void addAll(Collection<Long> collection)
	{
		ensureCapacity(collection.size());
		collection.forEach(this::add);
	}
	
	@Override
	public Iterator<Long> iterator()
	{
		return new ForEachIterator();
	}
	
	private class ForEachIterator extends BaseIterator implements Iterator<Long>
	{
		private long _tFv;
		private long[] _tSet;
		private long _k;
		
		public ForEachIterator()
		{
			_tSet=_set;
			_k=_tFv=_freeValue;
		}
		
		@Override
		public boolean hasNext()
		{
			if(_k!=_tFv && _k!=_tSet[_index])
			{
				++_index;
			}
			
			long key;
			
			if(_index<=_tSafeIndex)
			{
				while(--_index >= 0)
				{
					if((key=_tSet[_index])!=_tFv)
					{
						_k=key;
						return true;
					}
				}
				
				_k=_tFv;
				_index=_tSet.length;
				return hasNext();
			}
			else
			{
				while(--_index > _tSafeIndex)
				{
					if((key=_tSet[_index])!=_tFv)
					{
						_k=key;
						return true;
					}
				}
				
				_k=_tFv;
				return false;
			}
		}
		
		@Override
		public Long next()
		{
			return _k;
		}
		
		@Override
		public void remove()
		{
			LongSet.this.remove(_k);
		}
	}
}
