package com.home.shine.support.collection;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.inter.IIntFloatConsumer;
import com.home.shine.utils.MathUtils;
import com.koloboke.collect.impl.IntArrays;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

public class IntFloatMap extends BaseHash
{
	private int _freeValue;
	
	private int[] _set;
	
	private float[] _values;
	
	private EntrySet _entrySet;
	
	public IntFloatMap()
	{
	
	}
	
	public IntFloatMap(int capacity)
	{
		init(countCapacity(capacity));
	}
	
	private void checkInit()
	{
		if(_set!=null)
		{
			return;
		}
		
		init(_minSize);
	}
	
	public final int getFreeValue()
	{
		return _freeValue;
	}
	
	public final int[] getKeys()
	{
		checkInit();
		
		return _set;
	}
	
	public final float[] getValues()
	{
		checkInit();
		
		return _values;
	}
	
	private void init(int capacity)
	{
		_maxSize=capacity;
		
		_set=new int[capacity << 1];
		
		if(_freeValue!=0)
		{
			Arrays.fill(_set,_freeValue);
		}
		
		_values=new float[capacity << 1];
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
		IntArrays.replaceAll(_set,_freeValue,newFree);
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
		float[] vals=_values;
		init(newCapacity);
		int[] newKeys=_set;
		int capacityMask=(newKeys.length) - 1;
		float[] newVals=_values;
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
	
	private int insert(int key,float value)
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
	
	public void put(int key,float value)
	{
		checkInit();
		
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
	
	/** 增加值 */
	public float addValue(int key,float value)
	{
		checkInit();
		
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
	
	/** 是否存在 */
	public boolean contains(int key)
	{
		if(_size==0)
		{
			return false;
		}
		
		return index(key) >= 0;
	}
	
	public float get(int key)
	{
		if(_size==0)
		{
			return 0;
		}
		
		int index=index(key);
		if(index >= 0)
		{
			return _values[index];
		}
		else
		{
			return 0f;
		}
	}
	
	public float getOrDefault(int key,float defaultValue)
	{
		if(_size==0)
		{
			return defaultValue;
		}
		
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
	
	public float remove(int key)
	{
		if(_size==0)
		{
			return 0f;
		}
		
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
					return 0f;
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
							return 0f;
						}
					}
				}
			}
			float[] vals=_values;
			float val=vals[index];
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
			vals[indexToRemove]=0f;
			postRemoveHook(indexToRemove);
			return val;
		}
		else
		{
			return 0f;
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
		}
	}
	
	/** 扩容 */
	public final void ensureCapacity(int capacity)
	{
		if(capacity>_maxSize)
		{
			int t=countCapacity(capacity);
			
			if(_set==null)
			{
				init(t);
			}
			else if(t>_set.length)
			{
				rehash(t);
			}
		}
	}
	
	public float putIfAbsent(int key,float value)
	{
		checkInit();
		
		int index=insert(key,value);
		
		if(index<0)
		{
			return 0L;
		}
		else
		{
			return _values[index];
		}
	}
	
	/** 遍历 */
	public void forEach(IIntFloatConsumer consumer)
	{
		if(_size==0)
		{
			return;
		}
		
		int version=_version;
		int free=_freeValue;
		int[] keys=_set;
		float[] vals=_values;
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
	
	/** 可删除遍历 */
	public void forEachS(IIntFloatConsumer consumer)
	{
		if(_size==0)
		{
			return;
		}
		
		int free=_freeValue;
		int[] keys=_set;
		float[] vals=_values;
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
	
	public EntrySet entrySet()
	{
		if(_entrySet!=null)
			return _entrySet;
		
		return _entrySet=new EntrySet();
	}
	
	public class Entry
	{
		public int key;
		public float value;
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
		private float[] _tValues;
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
			IntFloatMap.this.remove(_entry.key);
		}
	}
}