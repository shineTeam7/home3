package com.home.shine.support.collection;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.inter.IStringIntConsumer;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class StringIntMap extends BaseHash
{
	private String[] _set;
	
	private int[] _values;
	
	private EntrySet _entrySet;
	
	public StringIntMap()
	{
		init(_minSize);
	}
	
	public StringIntMap(int capacity)
	{
		init(countCapacity(capacity));
	}
	
	public final String[] getKeys()
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
		
		_set=new String[capacity<<1];
		
		_values=new int[capacity<<1];
	}
	
	private int index(String key)
	{
		if(key!=null)
		{
			String[] keys=_set;
			int capacityMask;
			int index;
			String cur;
			if(key.equals((cur=keys[(index=hashObj(key) & (capacityMask=(keys.length) - 1))])))
			{
				return index;
			}
			else
			{
				if(cur==null)
				{
					return -1;
				}
				else
				{
					while(true)
					{
						if(key.equals(cur=keys[(index=(index - 1) & capacityMask)]))
						{
							return index;
						}
						else if(cur==null)
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
		String[] keys=_set;
		
		int capacityMask=keys.length-1;
		
		for(int i=capacityMask;i >= 0;--i)
		{
			if(keys[i]==null)
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
		
		String[] keys=_set;
		int[] vals=_values;
		init(newCapacity);
		String[] newKeys=_set;
		int capacityMask=(newKeys.length) - 1;
		int[] newVals=_values;
		for(int i=(keys.length) - 1;i >= 0;i--)
		{
			String key;
			if((key=keys[i])!=null)
			{
				int index;
				if((newKeys[(index=hashObj(key) & capacityMask)])!=null)
				{
					while(true)
					{
						if((newKeys[(index=(index - 1) & capacityMask)])==null)
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
	
	private int insert(String key,int value)
	{
		String[] keys=_set;
		int capacityMask;
		int index;
		String cur;
		if((cur=keys[(index=hashObj(key) & (capacityMask=(keys.length) - 1))])!=null)
		{
			if(key.equals(cur))
			{
				return index;
			}
			else
			{
				while(true)
				{
					if((cur=keys[(index=(index - 1) & capacityMask)])==null)
					{
						break;
					}
					else if(key.equals(cur))
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
	
	public void put(String key,int value)
	{
		if(key==null)
		{
			Ctrl.throwError("key不能为null");
			return;
		}
		
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
	public boolean contains(String key)
	{
		if(key==null)
		{
			Ctrl.throwError("key不能为null");
			return false;
		}
		
		if(_size==0)
			return false;
		
		return index(key) >= 0;
	}
	
	public int get(String key)
	{
		if(key==null)
		{
			Ctrl.throwError("key不能为null");
			return 0;
		}
		
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
	
	public int getOrDefault(String key,int defaultValue)
	{
		if(key==null)
		{
			Ctrl.throwError("key不能为null");
			return defaultValue;
		}
		
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
	
	public int remove(String key)
	{
		if(key==null)
		{
			Ctrl.throwError("key不能为null");
			return 0;
		}
		
		if(_size==0)
			return 0;
		
		String[] keys=_set;
		int capacityMask=(keys.length) - 1;
		int index;
		String cur;
		keyPresent:
		if(!key.equals(cur=keys[(index=hashObj(key) & capacityMask)]))
		{
			if(cur==null)
			{
				return 0;
			}
			else
			{
				while(true)
				{
					if(key.equals(cur=keys[(index=(index - 1) & capacityMask)]))
					{
						break keyPresent;
					}
					else if(cur==null)
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
			String keyToShift;
			if((keyToShift=keys[indexToShift])==null)
			{
				break;
			}
			if(((hashObj(keyToShift) - indexToShift) & capacityMask) >= shiftDistance)
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
		keys[indexToRemove]=null;
		vals[indexToRemove]=0;
		postRemoveHook(indexToRemove);
		
		return val;
	}
	
	/** 清空 */
	public void clear()
	{
		if(_size==0)
		{
			return;
		}
		
		justClearSize();
		
		String[] set=_set;
		
		for(int i=set.length - 1;i >= 0;--i)
		{
			set[i]=null;
			//_values[i]=false;
		}
	}
	
	/** 克隆 */
	public StringIntMap clone()
	{
		if(_size==0)
			return new StringIntMap();
		
		StringIntMap re=new StringIntMap(capacity());
		System.arraycopy(_set,0,re._set,0,_set.length);
		System.arraycopy(_values,0,re._values,0,_values.length);
		re.copyBase(this);
		return re;
	}
	
	public int putIfAbsent(String key,int value)
	{
		if(key==null)
		{
			Ctrl.throwError("key不能为null");
			return 0;
		}
		
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
	
	/** 增加值 */
	public int addValue(String key,int value)
	{
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
	
	/** 转化为原生集合 */
	public HashMap<String,Integer> toNatureMap()
	{
		HashMap<String,Integer> re=new HashMap<>(size());
		
		String[] keys=_set;
		int[] vals=_values;
		for(int i=(keys.length) - 1;i >= 0;--i)
		{
			String key;
			if((key=keys[i])!=null)
			{
				re.put(key,vals[i]);
			}
		}
		
		return re;
	}
	
	public void addAll(Map<String,Integer> map)
	{
		ensureCapacity(map.size());
		map.forEach(this::put);
	}
	
	/** 遍历 */
	public void forEach(IStringIntConsumer consumer)
	{
		if(_size==0)
		{
			return;
		}
		
		int version=_version;
		
		String[] keys=_set;
		int[] vals=_values;
		for(int i=(keys.length) - 1;i >= 0;--i)
		{
			String key;
			if((key=keys[i])!=null)
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
	public void forEachS(IStringIntConsumer consumer)
	{
		if(_size==0)
		{
			return;
		}
		
		String[] keys=_set;
		int[] vals=_values;
		String key;
		int safeIndex=getLastFreeIndex();
		
		for(int i=safeIndex-1;i>=0;--i)
		{
			if((key=keys[i])!=null)
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
			if((key=keys[i])!=null)
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
		public String key;
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
		private String[] _tSet;
		private int[] _tValues;
		private Entry _entry=new Entry();
		
		public EntryIterator()
		{
			_tSet=_set;
			_tValues=_values;
		}
		
		@Override
		public boolean hasNext()
		{
			if(_entry.key!=null && _entry.key!=_tSet[_index])
			{
				++_index;
			}
			
			String key;
			
			if(_index<=_tSafeIndex)
			{
				while(--_index >= 0)
				{
					if((key=_tSet[_index])!=null)
					{
						_entry.key=key;
						_entry.value=_tValues[_index];
						return true;
					}
				}
				
				_entry.key=null;
				_index=_tSet.length;
				return hasNext();
			}
			else
			{
				while(--_index > _tSafeIndex)
				{
					if((key=_tSet[_index])!=null)
					{
						_entry.key=key;
						_entry.value=_tValues[_index];
						return true;
					}
				}
				
				_entry.key=null;
				_entry.value=0;
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
			StringIntMap.this.remove(_entry.key);
		}
	}
}