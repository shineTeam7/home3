package com.home.shine.support.collection;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.inter.IIntBooleanConsumer;
import com.home.shine.utils.MathUtils;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class IntBooleanMap extends BaseHash
{
	private int _freeValue;
	
	private int[] _set;
	
	private boolean[] _values;
	
	private EntrySet _entrySet;
	
	public IntBooleanMap()
	{
		init(_minSize);
	}

	public IntBooleanMap(int capacity)
	{
		init(countCapacity(capacity));
	}
	
	public final int getFreeValue()
	{
		return _freeValue;
	}

	public final int[] getKeys()
	{
		return _set;
	}

	public final boolean[] getValues()
	{
		return _values;
	}

	@Override
	protected void init(int capacity)
	{
		_capacity=capacity;

		_set=new int[capacity<<1];
		
		if(_freeValue!=0)
		{
			Arrays.fill(_set,_freeValue);
		}

		_values=new boolean[capacity<<1];
	}

	private int index(int key)
	{
		int free;
		if(key!=(free=_freeValue))
		{
			int[] keys=_set;
			int capacityMask;
			int index;
			int cur;
			if((cur=keys[(index=hashInt(key) & (capacityMask=(keys.length) - 1))])==key)
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
	
	private int findNewFreeOrRemoved()
	{
		int free=_freeValue;

		int newFree;
		{
			do
			{
				newFree=MathUtils.randomInt();
			}
			while((newFree==free) || ((index(newFree)) >= 0));
		}
		return newFree;
	}
	
	private int changeFree()
	{
		int newFree=findNewFreeOrRemoved();
		
		int free=_freeValue;
		int[] keys=_set;
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
		int free=_freeValue;
		int[] keys=_set;
		
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
		
		int free=_freeValue;
		int[] keys=_set;
		boolean[] vals=_values;
		init(newCapacity);
		int[] newKeys=_set;
		int capacityMask=(newKeys.length) - 1;
		boolean[] newVals=_values;
		for(int i=(keys.length) - 1;i >= 0;i--)
		{
			int key;
			if((key=keys[i])!=free)
			{
				int index;
				if((newKeys[(index=hashInt(key) & capacityMask)])!=free)
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
	
	private int insert(int key,boolean value)
	{
		int free;
		if(key==(free=_freeValue))
		{
			free=changeFree();
		}
		int[] keys=_set;
		int capacityMask;
		int index;
		int cur;
		if((cur=keys[(index=hashInt(key) & (capacityMask=(keys.length) - 1))])!=free)
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
						break;
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
	
	public void put(int key,boolean value)
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
	public boolean contains(int key)
	{
		if(_size==0)
			return false;
		
		return index(key) >= 0;
	}
	
	public boolean get(int key)
	{
		if(_size==0)
			return false;
		
		int index=index(key);
		if(index >= 0)
		{
			return _values[index];
		}
		else
		{
			return false;
		}
	}
	
	public boolean getOrDefault(int key,boolean defaultValue)
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
	
	public boolean remove(int key)
	{
		if(_size==0)
			return false;
		
		int free;
		if(key!=(free=_freeValue))
		{
			int[] keys=_set;
			int capacityMask=(keys.length) - 1;
			int index;
			int cur;
			keyPresent:
			if((cur=keys[(index=hashInt(key) & capacityMask)])!=key)
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
			boolean[] vals=_values;
			boolean val=vals[index];
			int indexToRemove=index;
			int indexToShift=indexToRemove;
			int shiftDistance=1;
			while(true)
			{
				indexToShift=(indexToShift - 1) & capacityMask;
				int keyToShift;
				if((keyToShift=keys[indexToShift])==free)
				{
					break;
				}
				if(((hashInt(keyToShift) - indexToShift) & capacityMask) >= shiftDistance)
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
			vals[indexToRemove]=false;
			postRemoveHook(indexToRemove);
			return val;
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
		
		int[] set=_set;
		
		for(int i=set.length - 1;i >= 0;--i)
		{
			set[i]=_freeValue;
			//_values[i]=false;
		}
	}
	
	public boolean putIfAbsent(int key,boolean value)
	{
		int index=insert(key,value);
		
		if(index<0)
		{
			return false;
		}
		else
		{
			return _values[index];
		}
	}
	
	/** 遍历 */
	public void forEach(IIntBooleanConsumer consumer)
	{
		if(_size==0)
		{
			return;
		}

		int version=_version;
		
		int free=_freeValue;
		int[] keys=_set;
		boolean[] vals=_values;
		for(int i=(keys.length) - 1;i >= 0;--i)
		{
			int key;
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
	public void forEachS(IIntBooleanConsumer consumer)
	{
		if(_size==0)
		{
			return;
		}
		
		int free=_freeValue;
		int[] keys=_set;
		boolean[] vals=_values;
		int key;
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
	public HashMap<Integer,Boolean> toNatureMap()
	{
		HashMap<Integer,Boolean> re=new HashMap<>(size());
		
		int free=_freeValue;
		int[] keys=_set;
		boolean[] vals=_values;
		for(int i=(keys.length) - 1;i >= 0;--i)
		{
			int key;
			if((key=keys[i])!=free)
			{
				re.put(key,vals[i]);
			}
		}
		
		return re;
	}
	
	public void addAll(Map<Integer,Boolean> map)
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
		public int key;
		public boolean value;
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
		private int _tFv;
		private int[] _tSet;
		private boolean[] _tValues;
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
			
			int key;
			
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
				
				_entry.key=_tFv;
				_entry.value=false;
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
			IntBooleanMap.this.remove(_entry.key);
		}
	}
}
