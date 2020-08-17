package com.home.shine.support.collection;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.inter.ILongIntConsumer;
import com.home.shine.utils.MathUtils;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/** longIntMap */
public class LongIntMap extends BaseHash
{
	private long _freeValue;
	
	private long[] _set;
	
	private int[] _values;
	
	private EntrySet _entrySet;
	
	public LongIntMap()
	{
		init(_minSize);
	}
	
	public LongIntMap(int capacity)
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
	
	public final int[] getValues()
	{
		return _values;
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
		
		_values=new int[capacity<<1];
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
		super.rehash(newCapacity);
		
		long free=_freeValue;
		long[] keys=_set;
		int[] vals=_values;
		init(newCapacity);
		long[] newKeys=_set;
		int capacityMask=(newKeys.length) - 1;
		int[] newVals=_values;
		for(int i=(keys.length) - 1;i >= 0;i--)
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
				newVals[index]=vals[i];
			}
		}
	}
	
	private int insert(long key,int value)
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
				return index;
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
						return index;
					}
				}
			}
		}
		keys[index]=key;
		_values[index]=value;
		postInsertHook(index);
		return -1;
	}
	
	public void put(long key,int value)
	{
		int index=insert(key,value);
		if(index<0)
		{
			return;
		}
		else
		{
			_values[index]=value;
			return;
		}
	}
	
	/** 是否存在 */
	public boolean contains(long key)
	{
		if(_size==0)
			return false;
		
		return index(key) >= 0;
	}
	
	public int get(long key)
	{
		if(_size==0)
			return 0;
		
		int index=index(key);
		if(index >= 0)
		{
			return _values[index];
		}
		else
		{
			return 0;
		}
	}
	
	public int getOrDefault(long key,int defaultValue)
	{
		if(_size==0)
			return defaultValue;
		
		int index=index(key);
		
		if(index >= 0)
		{
			return _values[index];
		}
		else
		{
			return defaultValue;
		}
	}
	
	public int remove(long key)
	{
		if(_size==0)
			return 0;
		
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
					return 0;
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
							return 0;
						}
					}
				}
			}
			int[] vals=_values;
			int val=vals[index];
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
					vals[indexToRemove]=vals[indexToShift];
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
			vals[indexToRemove]=0;
			postRemoveHook(indexToRemove);
			return val;
		}
		else
		{
			return 0;
		}
	}
	
	/** 清空 */
	public void clear()
	{
		if(_size==0)
			return;
		
		justClearSize();
		
		long fv=_freeValue;
		long[] set=_set;
		int[] values=_values;
		
		for(int i=set.length - 1;i >= 0;--i)
		{
			set[i]=fv;
			values[i]=0;
		}
	}
	
	/** 增加值 */
	public int addValue(long key,int value)
	{
		++_version;
		
		int index=insert(key,value);
		
		if(index<0)
		{
			return value;
		}
		else
		{
			return _values[index]+=value;
		}
	}
	
	public int putIfAbsent(long key,int value)
	{
		int index=insert(key,value);
		
		if(index<0)
		{
			return 0;
		}
		else
		{
			return _values[index];
		}
	}
	
	/** 获取key对应set */
	public LongSet getKeySet()
	{
		if(_size==0)
			return new LongSet();
		
		LongSet re=new LongSet(capacity());
		re.copyBase(this);
		re.setFreeValue(_freeValue);
		System.arraycopy(_set,0,re.getKeys(),0,_set.length);
		return re;
	}
	
	/** 遍历 */
	public void forEach(ILongIntConsumer consumer)
	{
		if(_size==0)
			return;
		
		int version=_version;
		long free=_freeValue;
		long[] keys=_set;
		int[] vals=_values;
		for(int i=(keys.length) - 1;i >= 0;--i)
		{
			long key;
			if((key=keys[i])!=free)
			{
				consumer.accept(key,vals[i]);
			}
		}
		
		if(version!=_version)
		{
			Ctrl.throwError("ForeachModificationException");
		}
	}
	
	/** 可修改遍历 */
	public void forEachS(ILongIntConsumer consumer)
	{
		if(_size==0)
			return;
		
		long free=_freeValue;
		long[] keys=_set;
		int[] vals=_values;
		long key;
		int safeIndex=getLastFreeIndex();
		
		for(int i=safeIndex-1;i>=0;--i)
		{
			if((key=keys[i])!=free)
			{
				consumer.accept(key,vals[i]);
				
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
				consumer.accept(key,vals[i]);
				
				if(key!=keys[i])
				{
					++i;
				}
			}
		}
	}
	
	/** 转化为原生集合 */
	public HashMap<Long,Integer> toNatureMap()
	{
		HashMap<Long,Integer> re=new HashMap<>(size());
		
		long free=_freeValue;
		long[] keys=_set;
		int[] vals=_values;
		for(int i=(keys.length) - 1;i >= 0;--i)
		{
			long key;
			if((key=keys[i])!=free)
			{
				re.put(key,vals[i]);
			}
		}
		
		return re;
	}
	
	public void addAll(Map<Long,Integer> map)
	{
		ensureCapacity(map.size());
		map.forEach(this::put);
	}
	
	public EntrySet entrySet()
	{
		if(_entrySet!=null)
			return _entrySet;
		
		return _entrySet=new EntrySet();
	}
	
	public class Entry
	{
		public long key;
		public int value;
	}
	
	public class EntrySet implements Iterable<Entry>
	{
		@Override
		public Iterator<Entry> iterator()
		{
			return new EntryIterator();
		}
	}
	
	private class EntryIterator extends BaseIterator implements Iterator<Entry>
	{
		private long _tFv;
		private long[] _tSet;
		private int[] _tValues;
		private Entry _entry=new Entry();
		
		public EntryIterator()
		{
			_tSet=_set;
			_tValues=_values;
			_entry.key=_tFv=_freeValue;
		}
		
		@Override
		public boolean hasNext()
		{
			if(_entry.key!=_tFv && _entry.key!=_tSet[_index])
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
						_entry.key=key;
						_entry.value=_tValues[_index];
						return true;
					}
				}
				
				_entry.key=_tFv;
				_index=_tSet.length;
				return hasNext();
			}
			else
			{
				while(--_index > _tSafeIndex)
				{
					if((key=_tSet[_index])!=_tFv)
					{
						_entry.key=key;
						_entry.value=_tValues[_index];
						return true;
					}
				}
				
				return false;
			}
		}
		
		@Override
		public Entry next()
		{
			return _entry;
		}
		
		@Override
		public void remove()
		{
			LongIntMap.this.remove(_entry.key);
		}
	}
}
