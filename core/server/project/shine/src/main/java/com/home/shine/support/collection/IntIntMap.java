package com.home.shine.support.collection;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.inter.IIntConsumer;
import com.home.shine.support.collection.inter.IIntIntConsumer;
import com.home.shine.utils.MathUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class IntIntMap extends BaseHash
{
	private int[] _table;
	
	private int _freeValue;
	
	private EntrySet _entrySet;
	
	public IntIntMap()
	{
		init(_minSize);
	}
	
	public IntIntMap(int capacity)
	{
		init(countCapacity(capacity));
	}
	
	/** 初始化设置freeValue */
	public void setFreeValue(int value)
	{
		int[] table=_table;
		
		for(int i=table.length - 2;i >= 0;i-=2)
		{
			if(table[i]==_freeValue)
			{
				table[i]=value;
			}
		}
		
		_freeValue=value;
	}
	
	public final int getFreeValue()
	{
		return _freeValue;
	}
	
	public final int[] getTable()
	{
		return _table;
	}
	
	@Override
	protected void init(int capacity)
	{
		_capacity=capacity;
		
		_table=new int[capacity<<2];
		
		if(_freeValue!=0)
		{
			int[] table=_table;
			
			for(int i=table.length - 2;i >= 0;i-=2)
			{
				table[i]=_freeValue;
			}
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
		
		int[] table=_table;
		
		for(int i=table.length - 2;i >= 0;i-=2)
		{
			if(table[i]==_freeValue)
			{
				table[i]=newFree;
			}
		}
		
		_freeValue=newFree;
		return newFree;
	}
	
	private int index(int key)
	{
		int free;
		if(key!=(free=_freeValue))
		{
			int[] tab=_table;
			int capacityMask;
			int index;
			int cur;
			if((cur=((tab[(index=hashInt(key) & (capacityMask=(tab.length) - 2))])))==key)
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
						if((cur=((tab[(index=(index - 2) & capacityMask)])))==key)
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
	
	private int insert(int key,int value)
	{
		int free;
		if(key==(free=_freeValue))
		{
			free=changeFree();
		}
		int[] tab=_table;
		int capacityMask;
		int index;
		int cur;
		
		if((cur=((tab[(index=hashInt(key) & (capacityMask=(tab.length) - 2))])))!=free)
		{
			if(cur==key)
			{
				return index;
			}
			else
			{
				while(true)
				{
					if((cur=((tab[(index=(index - 2) & capacityMask)])))==free)
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
		tab[index]=key;
		tab[index+1]=value;
		postInsertHook(index);
		return -1;
	}
	
	@Override
	protected int toGetLastFreeIndex()
	{
		int free=_freeValue;
		int[] tab=_table;
		
		for(int i=(tab.length) - 2;i >= 0;i-=2)
		{
			if(tab[i]==free)
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
		
		int free=_freeValue;
		int[] tab=_table;
		init(newCapacity);
		int[] newTab=_table;
		int capacityMask=(newTab.length) - 2;
		for(int i=(tab.length) - 2;i >= 0;i-=2)
		{
			int key;
			if((key=(tab[i]))!=free)
			{
				int index;
				if(((newTab[(index=hashInt(key) & capacityMask)]))!=free)
				{
					while(true)
					{
						if(((newTab[(index=(index - 2) & capacityMask)]))==free)
						{
							break;
						}
					}
				}
				
				newTab[index]=key;
				newTab[index+1]=tab[i+1];
			}
		}
	}
	
	@SuppressWarnings("restriction")
	public void put(int key,int value)
	{
		int index=insert(key,value);
		
		if(index<0)
		{
			return;
		}
		else
		{
			_table[index+1]=value;
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
	
	@SuppressWarnings("restriction")
	public int addValue(int key,int value)
	{
		++_version;
		
		int free;
		if(key==(free=_freeValue))
		{
			free=changeFree();
		}
		int[] tab=_table;
		int capacityMask;
		int index;
		int cur;
		keyPresent:
		if((cur=((tab[(index=hashInt(key) & (capacityMask=tab.length - 2))])))!=key)
		{
			keyAbsent:
			if(cur!=free)
			{
				while(true)
				{
					if((cur=((tab[(index=(index - 2) & capacityMask)])))==key)
					{
						break keyPresent;
					}
					else if(cur==free)
					{
						break keyAbsent;
					}
				}
			}
			
			tab[index]=key;
			tab[index+1]=value;
			postInsertHook(index);
			return value;
		}
		
		int newValue=(tab[index+1]+=value);
		return newValue;
	}
	
	/** 如果addValue后，值为0,会直接remove */
	public int addValueR(int key,int value)
	{
		++_version;
		
		int free;
		if(key==(free=_freeValue))
		{
			free=changeFree();
		}
		int[] tab=_table;
		int capacityMask;
		int index;
		int cur;
		keyPresent:
		if((cur=((tab[(index=hashInt(key) & (capacityMask=tab.length - 2))])))!=key)
		{
			keyAbsent:
			if(cur!=free)
			{
				while(true)
				{
					if((cur=((tab[(index=(index - 2) & capacityMask)])))==key)
					{
						break keyPresent;
					}
					else if(cur==free)
					{
						break keyAbsent;
					}
				}
			}
			
			if(value==free)
				return value;
			
			tab[index]=key;
			tab[index+1]=value;
			postInsertHook(index);
			return value;
		}
		
		int newValue=tab[index+1] + value;
		
		if(newValue!=free)
		{
			tab[index+1]=newValue;
			return newValue;
		}
		
		int indexToRemove=index;
		int indexToShift=indexToRemove;
		int shiftDistance=2;
		while(true)
		{
			indexToShift=(indexToShift - 2) & capacityMask;
			int keyToShift;
			if((keyToShift=((tab[indexToShift])))==free)
			{
				break;
			}
			
			if(((hashInt(keyToShift) - indexToShift) & capacityMask) >= shiftDistance)
			{
				tab[indexToRemove]=keyToShift;
				tab[indexToRemove+1]=tab[indexToShift+1];
				indexToRemove=indexToShift;
				shiftDistance=2;
			}
			else
			{
				shiftDistance+=2;
				
				//if(indexToShift==(2 + index))
				//{
				//	throw new ConcurrentModificationException();
				//}
			}
		}
		
		tab[indexToRemove]=free;
		tab[indexToRemove+1]=0;
		
		postRemoveHook(indexToRemove);
		
		return newValue;
	}
	
	/** 获取 */
	public int get(int key)
	{
		if(_size==0)
			return 0;
		
		int free;
		if(key!=(free=_freeValue))
		{
			int[] tab=_table;
			int capacityMask;
			int index;
			int cur;
			if((cur=((tab[(index=hashInt(key) & (capacityMask=(tab.length) - 2))])))==key)
			{
				return tab[index+1];
			}
			else
			{
				if(cur==free)
				{
					return 0;
				}
				else
				{
					while(true)
					{
						if((cur=((tab[(index=(index - 2) & capacityMask)])))==key)
						{
							return tab[index+1];
						}
						else if(cur==free)
						{
							return 0;
						}
					}
				}
			}
		}
		else
		{
			return 0;
		}
	}
	
	public int getOrDefault(int key,int defaultValue)
	{
		if(_size==0)
			return defaultValue;
		
		int free;
		if(key!=(free=_freeValue))
		{
			int[] tab=_table;
			int capacityMask;
			int index;
			int cur;
			if((cur=((tab[(index=hashInt(key) & (capacityMask=(tab.length) - 2))])))==key)
			{
				return tab[index+1];
			}
			else
			{
				if(cur==free)
				{
					return defaultValue;
				}
				else
				{
					while(true)
					{
						if((cur=((tab[(index=(index - 2) & capacityMask)])))==key)
						{
							return tab[index+1];
						}
						else if(cur==free)
						{
							return defaultValue;
						}
					}
				}
			}
		}
		else
		{
			return defaultValue;
		}
	}
	
	@SuppressWarnings("restriction")
	public int remove(int key)
	{
		if(_size==0)
			return 0;
		
		int free;
		if(key!=(free=_freeValue))
		{
			int[] tab=_table;
			int capacityMask=(tab.length) - 2;
			int index;
			int cur;
			
			if((cur=((tab[(index=hashInt(key) & capacityMask)])))!=key)
			{
				if(cur==free)
				{
					return 0;
				}
				else
				{
					while(true)
					{
						if((cur=((tab[(index=(index - 2) & capacityMask)])))==key)
						{
							break;
						}
						else if(cur==free)
						{
							return 0;
						}
					}
				}
			}
			
			int val=tab[index+1];
			int indexToRemove=index;
			int indexToShift=indexToRemove;
			int shiftDistance=2;
			while(true)
			{
				indexToShift=(indexToShift - 2) & capacityMask;
				int keyToShift;
				if((keyToShift=((tab[indexToShift])))==free)
				{
					break;
				}
				
				if(((hashInt(keyToShift) - indexToShift) & capacityMask) >= shiftDistance)
				{
					tab[indexToRemove]=keyToShift;
					tab[indexToRemove+1]=tab[indexToShift+1];
					indexToRemove=indexToShift;
					shiftDistance=2;
				}
				else
				{
					shiftDistance+=2;
					
					//if(indexToShift==(2 + index))
					//{
					//	throw new ConcurrentModificationException();
					//}
				}
			}
			
			tab[indexToRemove]=free;
			tab[indexToRemove+1]=0;
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
		
		int free=_freeValue;
		int[] tab=_table;
		
		for(int i=(tab.length) - 2;i >= 0;i-=2)
		{
			tab[i]=free;
			tab[i+1]=0;
		}
	}
	
	public IntList getSortedKeyList()
	{
		IntList re=new IntList();
		
		if(_size==0)
			return re;
		
		int free=_freeValue;
		int[] tab=_table;
		int key;
		
		for(int i=(tab.length) - 2;i >= 0;i-=2)
		{
			if((key=tab[i])!=free)
			{
				re.add(key);
			}
		}
		
		re.sort();
		return re;
	}
	
	public int putIfAbsent(int key,int value)
	{
		int index=insert(key,value);
		
		if(index<0)
		{
			return 0;
		}
		else
		{
			return _table[index + 1];
		}
	}
	
	/** 与computeIfAbsent一致,都是没有就添加，有就返回 */
	public int putIfAbsent2(int key,int value)
	{
		int index=insert(key,value);
		
		if(index<0)
		{
			return value;
		}
		else
		{
			return _table[index + 1];
		}
	}
	
	public IntIntMap clone()
	{
		if(_size==0)
			return new IntIntMap();
		
		IntIntMap re=new IntIntMap(capacity());
		System.arraycopy(_table,0,re._table,0,_table.length);
		re.copyBase(this);
		re._freeValue=_freeValue;
		
		return re;
	}
	
	/** 遍历 */
	public void forEach(IIntIntConsumer consumer)
	{
		if(_size==0)
		{
			return;
		}
		
		int version=_version;
		int free=_freeValue;
		int[] tab=_table;
		int key;
		
		for(int i=(tab.length) - 2;i >= 0;i-=2)
		{
			if((key=((tab[i])))!=free)
			{
				consumer.accept(key,tab[i+1]);
			}
		}
		
		if(version!=_version)
		{
			Ctrl.throwError("ForeachModificationException");
		}
	}
	
	/** 可修改遍历 */
	public void forEachS(IIntIntConsumer consumer)
	{
		if(_size==0)
		{
			return;
		}
		
		int free=_freeValue;
		int[] tab=_table;
		int key;
		int safeIndex=getLastFreeIndex();
		
		for(int i=safeIndex - 2;i >= 0;i-=2)
		{
			if((key=((tab[i])))!=free)
			{
				consumer.accept(key,tab[i+1]);
				
				if(key!=tab[i])
				{
					i+=2;
				}
			}
		}
		
		for(int i=tab.length - 2;i > safeIndex;i-=2)
		{
			if((key=((tab[i])))!=free)
			{
				consumer.accept(key,tab[i+1]);
				
				if(key!=tab[i])
				{
					i+=2;
				}
			}
		}
	}
	
	/** 遍历值 */
	public void forEachValue(IIntConsumer consumer)
	{
		if(_size==0)
		{
			return;
		}
		
		int version=_version;
		int free=_freeValue;
		int[] tab=_table;
		for(int i=(tab.length) - 1;i >= 0;--i)
		{
			if(((tab[i]))!=free)
			{
				consumer.accept(tab[i+1]);
			}
		}
		
		if(version!=_version)
		{
			Ctrl.throwError("ForeachModificationException");
		}
	}
	
	/** 转化为原生集合 */
	public HashMap<Integer,Integer> toNatureMap()
	{
		HashMap<Integer,Integer> re=new HashMap<>(size());
		
		int free=_freeValue;
		int[] tab=_table;
		int key;
		
		for(int i=(tab.length) - 2;i >= 0;i-=2)
		{
			if((key=((tab[i])))!=free)
			{
				re.put(key,tab[i+1]);
			}
		}
		
		return re;
	}
	
	public void addAll(Map<Integer,Integer> map)
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
		private int _tFv;
		private int[] _tTable;
		private Entry _entry=new Entry();
		
		public EntryIterator()
		{
			_tTable=_table;
			_entry.key=_tFv=_freeValue;
		}
		
		@Override
		public boolean hasNext()
		{
			if(_entry.key!=_tFv && _entry.key!=(_tTable[_index]))
			{
				_index+=2;
			}
			
			int key;
			
			if(_index<=_tSafeIndex)
			{
				while(--_index >= 0)
				{
					if((key=(_tTable[_index]))!=_tFv)
					{
						_entry.key=key;
						_entry.value=_tTable[_index+1];
						return true;
					}
				}
				
				_entry.key=_tFv;
				_index=_tTable.length;
				return hasNext();
			}
			else
			{
				while(--_index > _tSafeIndex)
				{
					if((key=(_tTable[_index]))!=_tFv)
					{
						_entry.key=key;
						_entry.value=_tTable[_index+1];
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
			IntIntMap.this.remove(_entry.key);
		}
	}
}
