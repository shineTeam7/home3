package com.home.shine.support.collection;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.inter.IIntConsumer;
import com.home.shine.support.collection.inter.IIntIntConsumer;
import com.home.shine.utils.MathUtils;
import com.koloboke.collect.impl.IntArrays;
import com.koloboke.collect.impl.PrimitiveConstants;
import com.koloboke.collect.impl.UnsafeConstants;

import java.util.ConcurrentModificationException;
import java.util.Iterator;

public class IntIntMap extends BaseHash
{
	private static final long INT_MASK=0xFFFFFFFFL;
	
	private long[] _table;
	
	private int _freeValue;
	
	private EntrySet _entrySet;
	
	public IntIntMap()
	{
	
	}
	
	public IntIntMap(int capacity)
	{
		init(countCapacity(capacity));
	}
	
	private void checkInit()
	{
		if(_table!=null)
			return;
		
		init(_minSize);
	}
	
	/** 初始化设置freeValue */
	public void setFreeValue(int value)
	{
		IntArrays.replaceAllKeys(_table,_freeValue,value);
		_freeValue=value;
	}
	
	public final int getFreeValue()
	{
		return _freeValue;
	}
	
	public final long[] getTable()
	{
		checkInit();
		
		return _table;
	}
	
	private void init(int capacity)
	{
		_maxSize=capacity;
		
		_table=new long[capacity<<1];
		
		if(_freeValue!=0)
		{
			IntArrays.fillKeys(_table,_freeValue);
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
		IntArrays.replaceAllKeys(_table,_freeValue,newFree);
		_freeValue=newFree;
		return newFree;
	}
	
	private int index(int key)
	{
		int free;
		if(key!=(free=_freeValue))
		{
			long[] tab=_table;
			int capacityMask;
			int index;
			int cur;
			if((cur=((int)(tab[(index=hashInt(key) & (capacityMask=(tab.length) - 1))])))==key)
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
						if((cur=((int)(tab[(index=(index - 1) & capacityMask)])))==key)
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
		long[] tab=_table;
		int capacityMask;
		int index;
		int cur;
		keyAbsent:
		if((cur=((int)(tab[(index=hashInt(key) & (capacityMask=(tab.length) - 1))])))!=free)
		{
			if(cur==key)
			{
				return index;
			}
			else
			{
				while(true)
				{
					if((cur=((int)(tab[(index=(index - 1) & capacityMask)])))==free)
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
		tab[index]=(((long)(key)) & (INT_MASK)) | (((long)(value)) << 32);
		postInsertHook(index);
		return -1;
	}
	
	@Override
	protected int toGetLastFreeIndex()
	{
		int free=_freeValue;
		long[] tab=_table;
		
		for(int i=(tab.length) - 1;i >= 0;--i)
		{
			if((int)tab[i]==free)
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
		long[] tab=_table;
		long entry;
		init(newCapacity);
		long[] newTab=_table;
		int capacityMask=(newTab.length) - 1;
		for(int i=(tab.length) - 1;i >= 0;--i)
		{
			int key;
			if((key=((int)(entry=tab[i])))!=free)
			{
				int index;
				if((UnsafeConstants.U.getInt(newTab,(((UnsafeConstants.LONG_BASE) + (UnsafeConstants.INT_KEY_OFFSET)) + (((long)(index=hashInt(key) & capacityMask)) << (UnsafeConstants.LONG_SCALE_SHIFT)))))!=free)
				{
					while(true)
					{
						if((UnsafeConstants.U.getInt(newTab,(((UnsafeConstants.LONG_BASE) + (UnsafeConstants.INT_KEY_OFFSET)) + (((long)(index=(index - 1) & capacityMask)) << (UnsafeConstants.LONG_SCALE_SHIFT)))))==free)
						{
							break;
						}
					}
				}
				newTab[index]=entry;
			}
		}
	}
	
	@SuppressWarnings("restriction")
	public void put(int key,int value)
	{
		checkInit();
		
		int index=insert(key,value);
		
		if(index<0)
		{
			return;
		}
		else
		{
			UnsafeConstants.U.putInt(_table,(((UnsafeConstants.LONG_BASE) + (UnsafeConstants.INT_VALUE_OFFSET)) + (((long)(index)) << (UnsafeConstants.LONG_SCALE_SHIFT))),value);
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
		checkInit();
		
		++_version;
		
		int free;
		if(key==(free=_freeValue))
		{
			free=changeFree();
		}
		long[] tab=_table;
		int capacityMask;
		int index;
		int cur;
		long entry;
		keyPresent:
		if((cur=((int)(entry=tab[(index=hashInt(key) & (capacityMask=tab.length - 1))])))!=key)
		{
			keyAbsent:
			if(cur!=free)
			{
				while(true)
				{
					if((cur=((int)(entry=tab[(index=(index - 1) & capacityMask)])))==key)
					{
						break keyPresent;
					}
					else if(cur==free)
					{
						break keyAbsent;
					}
				}
			}
			
			int newValue=value;//defaultValue
			tab[index]=(((long)(key)) & (PrimitiveConstants.INT_MASK)) | (((long)(newValue)) << 32);
			postInsertHook(index);
			return newValue;
		}
		
		int newValue=((int)((entry >>> 32))) + value;
		UnsafeConstants.U.putInt(tab,(((UnsafeConstants.LONG_BASE) + (UnsafeConstants.INT_VALUE_OFFSET)) + (((long)(index)) << (UnsafeConstants.LONG_SCALE_SHIFT))),newValue);
		return newValue;
	}
	
	/** 如果addValue后，值为0,会直接remove */
	public int addValueR(int key,int value)
	{
		checkInit();
		
		++_version;
		
		int free;
		if(key==(free=_freeValue))
		{
			free=changeFree();
		}
		long[] tab=_table;
		int capacityMask;
		int index;
		int cur;
		long entry;
		keyPresent:
		if((cur=((int)(entry=tab[(index=hashInt(key) & (capacityMask=tab.length - 1))])))!=key)
		{
			keyAbsent:
			if(cur!=free)
			{
				while(true)
				{
					if((cur=((int)(entry=tab[(index=(index - 1) & capacityMask)])))==key)
					{
						break keyPresent;
					}
					else if(cur==free)
					{
						break keyAbsent;
					}
				}
			}
			
			int newValue=value;//defaultValue
			
			if(newValue==free)
				return newValue;
			
			tab[index]=(((long)(key)) & (PrimitiveConstants.INT_MASK)) | (((long)(newValue)) << 32);
			postInsertHook(index);
			return newValue;
		}
		
		int newValue=((int)((entry >>> 32))) + value;
		
		if(newValue!=free)
		{
			UnsafeConstants.U.putInt(tab,(((UnsafeConstants.LONG_BASE) + (UnsafeConstants.INT_VALUE_OFFSET)) + (((long)(index)) << (UnsafeConstants.LONG_SCALE_SHIFT))),newValue);
			return newValue;
		}
		
		int indexToRemove=index;
		int indexToShift=indexToRemove;
		int shiftDistance=1;
		while(true)
		{
			indexToShift=(indexToShift - 1) & capacityMask;
			int keyToShift;
			if((keyToShift=((int)(entry=tab[indexToShift])))==free)
			{
				break;
			}
			if(((hashInt(keyToShift) - indexToShift) & capacityMask) >= shiftDistance)
			{
				tab[indexToRemove]=entry;
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
		UnsafeConstants.U.putInt(tab,(((UnsafeConstants.LONG_BASE) + (UnsafeConstants.INT_KEY_OFFSET)) + (((long)(indexToRemove)) << (UnsafeConstants.LONG_SCALE_SHIFT))),free);
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
			long[] tab=_table;
			int capacityMask;
			int index;
			int cur;
			long entry;
			if((cur=((int)(entry=tab[(index=hashInt(key) & (capacityMask=(tab.length) - 1))])))==key)
			{
				return ((int)(entry >>> 32));
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
						if((cur=((int)(entry=tab[(index=(index - 1) & capacityMask)])))==key)
						{
							return ((int)(entry >>> 32));
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
			long[] tab=_table;
			int capacityMask;
			int index;
			int cur;
			long entry;
			if((cur=((int)(entry=tab[(index=hashInt(key) & (capacityMask=(tab.length) - 1))])))==key)
			{
				return ((int)(entry >>> 32));
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
						if((cur=((int)(entry=tab[(index=(index - 1) & capacityMask)])))==key)
						{
							return ((int)(entry >>> 32));
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
			long[] tab=_table;
			int capacityMask=(tab.length) - 1;
			int index;
			int cur;
			long entry;
			keyPresent:
			if((cur=((int)(entry=tab[(index=hashInt(key) & capacityMask)])))!=key)
			{
				if(cur==free)
				{
					return 0;
				}
				else
				{
					while(true)
					{
						if((cur=((int)(entry=tab[(index=(index - 1) & capacityMask)])))==key)
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
			int val=((int)(entry >>> 32));
			int indexToRemove=index;
			int indexToShift=indexToRemove;
			int shiftDistance=1;
			while(true)
			{
				indexToShift=(indexToShift - 1) & capacityMask;
				int keyToShift;
				if((keyToShift=((int)(entry=tab[indexToShift])))==free)
				{
					break;
				}
				if(((hashInt(keyToShift) - indexToShift) & capacityMask) >= shiftDistance)
				{
					tab[indexToRemove]=entry;
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
			UnsafeConstants.U.putInt(tab,(((UnsafeConstants.LONG_BASE) + (UnsafeConstants.INT_KEY_OFFSET)) + (((long)(indexToRemove)) << (UnsafeConstants.LONG_SCALE_SHIFT))),free);
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
		IntArrays.fillKeys(_table,_freeValue);
	}
	
	public IntList getSortedKeyList()
	{
		IntList re=new IntList();
		
		if(_size==0)
			return re;
		
		int free=_freeValue;
		long[] tab=_table;
		int key;
		
		for(int i=(tab.length) - 1;i >= 0;--i)
		{
			if((key=((int)(tab[i])))!=free)
			{
				re.add(key);
			}
		}
		
		re.sort();
		return re;
	}
	
	/** 扩容 */
	public final void ensureCapacity(int capacity)
	{
		if(capacity>_maxSize)
		{
			int t=countCapacity(capacity);
			
			if(_table==null)
			{
				init(t);
			}
			else if(t>_table.length)
			{
				rehash(t);
			}
		}
	}
	
	public int putIfAbsent(int key,int value)
	{
		checkInit();
		
		int free;
		if(key==(free=_freeValue))
		{
			free=changeFree();
		}
		long[] tab=_table;
		int capacityMask;
		int index;
		int cur;
		long entry;
		if((cur=((int)(entry=tab[(index=hashInt(key) & (capacityMask=(tab.length) - 1))])))==free)
		{
			tab[index]=(((long)(key)) & (PrimitiveConstants.INT_MASK)) | (((long)(value)) << 32);
			postInsertHook(index);
			return 0;
		}
		else
		{
			if(cur==key)
			{
				return ((int)(entry >>> 32));
			}
			else
			{
				while(true)
				{
					if((cur=((int)(entry=tab[(index=(index - 1) & capacityMask)])))==free)
					{
						tab[index]=(((long)(key)) & (PrimitiveConstants.INT_MASK)) | (((long)(value)) << 32);
						postInsertHook(index);
						return 0;
					}
					else if(cur==key)
					{
						return ((int)(entry >>> 32));
					}
				}
			}
		}
	}
	
	/** 与computeIfAbsent一致,都是没有就添加，有就返回 */
	public int putIfAbsent2(int key,int value)
	{
		checkInit();
		
		int free;
		if(key==(free=_freeValue))
		{
			free=changeFree();
		}
		long[] tab=_table;
		int capacityMask;
		int index;
		int cur;
		long entry;
		if((cur=((int)(entry=tab[(index=hashInt(key) & (capacityMask=(tab.length) - 1))])))==free)
		{
			tab[index]=(((long)(key)) & (PrimitiveConstants.INT_MASK)) | (((long)(value)) << 32);
			postInsertHook(index);
			return value;
		}
		else
		{
			if(cur==key)
			{
				return ((int)(entry >>> 32));
			}
			else
			{
				while(true)
				{
					if((cur=((int)(entry=tab[(index=(index - 1) & capacityMask)])))==free)
					{
						tab[index]=(((long)(key)) & (PrimitiveConstants.INT_MASK)) | (((long)(value)) << 32);
						postInsertHook(index);
						return value;
					}
					else if(cur==key)
					{
						return ((int)(entry >>> 32));
					}
				}
			}
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
		long[] tab=_table;
		long entry;
		int key;
		
		for(int i=(tab.length) - 1;i >= 0;--i)
		{
			if((key=((int)(entry=tab[i])))!=free)
			{
				consumer.accept(key,((int)(entry >>> 32)));
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
		long[] tab=_table;
		long entry;
		int key;
		int safeIndex=getLastFreeIndex();
		
		for(int i=safeIndex - 1;i >= 0;--i)
		{
			if((key=((int)(entry=tab[i])))!=free)
			{
				consumer.accept(key,((int)(entry >>> 32)));
				
				if(key!=(int)tab[i])
				{
					++i;
				}
			}
		}
		
		for(int i=tab.length - 1;i > safeIndex;--i)
		{
			if((key=((int)(entry=tab[i])))!=free)
			{
				consumer.accept(key,((int)(entry >>> 32)));
				
				if(key!=(int)tab[i])
				{
					++i;
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
		long[] tab=_table;
		long entry;
		for(int i=(tab.length) - 1;i >= 0;--i)
		{
			if(((int)(entry=tab[i]))!=free)
			{
				consumer.accept((int)(entry >>> 32));
			}
		}
		
		if(version!=_version)
		{
			Ctrl.throwError("ForeachModificationException");
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
		private long[] _tTable;
		private Entry _entry=new Entry();
		
		public EntryIterator()
		{
			_tTable=_table;
			_entry.key=_tFv=_freeValue;
		}
		
		@Override
		public boolean hasNext()
		{
			if(_entry.key!=_tFv && _entry.key!=((int)_tTable[_index]))
			{
				++_index;
			}
			
			int key;
			long entry;
			
			if(_index<=_tSafeIndex)
			{
				while(--_index >= 0)
				{
					if((key=(int)(entry=_tTable[_index]))!=_tFv)
					{
						_entry.key=key;
						_entry.value=(int)(entry>>>32);
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
					if((key=(int)(entry=_tTable[_index]))!=_tFv)
					{
						_entry.key=key;
						_entry.value=(int)(entry>>>32);
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
