package com.home.shine.support.collection;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.inter.ICreateArray;
import com.home.shine.support.collection.inter.ILongObjectConsumer;
import com.home.shine.support.collection.inter.IObjectConsumer;
import com.home.shine.support.func.ObjectLongFunc;
import com.home.shine.utils.MathUtils;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LongObjectMap<V> extends BaseHash implements Iterable<V>
{
	private long _freeValue;
	
	private long[] _set;
	
	private V[] _values;
	
	private ICreateArray<V> _createVArrFunc;
	
	private EntrySet _entrySet;
	
	public LongObjectMap()
	{
		init(_minSize);
	}

	public LongObjectMap(int capacity)
	{
		init(countCapacity(capacity));
	}

	public LongObjectMap(ICreateArray<V> createVArrFunc)
	{
		_createVArrFunc=createVArrFunc;
		init(_minSize);
	}

	public LongObjectMap(ICreateArray<V> createVArrFunc,int capacity)
	{
		_createVArrFunc=createVArrFunc;
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

	public final V[] getValues()
	{
		return _values;
	}

	@SuppressWarnings("unchecked")
	private V[] createVArray(int length)
	{
		if(_createVArrFunc!=null)
		{
			return _createVArrFunc.create(length);
		}
		return ((V[])(new Object[length]));
	}

	@Override
	protected void init(int capacity)
	{
		_capacity=capacity;

		_set=new long[capacity<<1];
		if(_freeValue!=0L)
		{
			Arrays.fill(_set,_freeValue);
		}

		_values=createVArray(capacity<<1);
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
		V[] vals=_values;
		init(newCapacity);
		long[] newKeys=_set;
		int capacityMask=(newKeys.length) - 1;
		V[] newVals=_values;
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
	
	private int insert(long key,V value)
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
	
	public void put(long key,V value)
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
	
	public V get(long key)
	{
		if(_size==0)
			return null;
		
		int index=index(key);
		if(index >= 0)
		{
			return _values[index];
		}
		else
		{
			return null;
		}
	}
	
	/** 获取任意一个 */
	public V getEver()
	{
		if(_size==0)
			return null;
		
		V[] vals=_values;
		V v;
		for(int i=vals.length - 1;i >= 0;--i)
		{
			if((v=vals[i])!=null)
			{
				return v;
			}
		}
		
		return null;
	}
	
	public V getOrDefault(long key,V defaultValue)
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
	
	public V remove(long key)
	{
		if(_size==0)
			return null;
		
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
					return null;
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
							return null;
						}
					}
				}
			}
			V[] vals=_values;
			V val=vals[index];
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
			vals[indexToRemove]=null;
			postRemoveHook(indexToRemove);
			return val;
		}
		else
		{
			return null;
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
		V[] values=_values;
		
		for(int i=set.length - 1;i >= 0;--i)
		{
			set[i]=fv;
			values[i]=null;
		}
	}
	
	/** 遍历并清空 */
	public void forEachValueAndClear(IObjectConsumer<? super V> consumer)
	{
		if(_size==0)
		{
			return;
		}
		
		justClearSize();
		
		long fv=_freeValue;
		long[] set=_set;
		V[] values=_values;
		
		for(int i=set.length - 1;i >= 0;--i)
		{
			if(set[i]!=fv)
			{
				consumer.accept(values[i]);
				set[i]=fv;
				values[i]=null;
			}
		}
	}
	
	public V putIfAbsent(long key,V value)
	{
		int index=insert(key,value);
		
		if(index<0)
		{
			return null;
		}
		else
		{
			return _values[index];
		}
	}
	
	public V computeIfAbsent(long key,ObjectLongFunc<? extends V> mappingFunction)
	{
		if(mappingFunction==null)
		{
			throw new NullPointerException();
		}

		long free;
		if(key==(free=_freeValue))
		{
			free=changeFree();
		}
		long[] keys=_set;
		V[] vals=_values;
		int capacityMask;
		int index;
		long cur;
		keyPresent:
		if((cur=keys[(index=(hashLong(key)) & (capacityMask=(keys.length) - 1))])!=key)
		{
			keyAbsent:
			if(cur!=free)
			{
				while(true)
				{
					if((cur=keys[(index=(index - 1) & capacityMask)])==key)
					{
						break keyPresent;
					}
					else if(cur==free)
					{
						break keyAbsent;
					}
				}
			}
			V value=mappingFunction.apply(key);
			if(value!=null)
			{
				keys[index]=key;
				vals[index]=value;
				postInsertHook(index);
				return value;
			}
			else
			{
				return null;
			}
		}
		V val;
		if((val=vals[index])!=null)
		{
			return val;
		}
		else
		{
			V value=mappingFunction.apply(key);
			if(value!=null)
			{
				vals[index]=value;
				return value;
			}
			else
			{
				return null;
			}
		}
	}
	
	public void putAll(LongObjectMap<V> dic)
	{
		if(dic.isEmpty())
			return;
		
		long[] keys=dic.getKeys();
		V[] values=dic.getValues();
		long fv=dic.getFreeValue();
		long k;
		
		for(int i=keys.length-1;i>=0;--i)
		{
			if((k=keys[i])!=fv)
			{
				put(k,values[i]);
			}
		}
	}
	
	public LongObjectMap<V> clone()
	{
		if(_size==0)
			return new LongObjectMap<>(_createVArrFunc);
		
		LongObjectMap<V> re=new LongObjectMap<>(_createVArrFunc,capacity());
		System.arraycopy(_set,0,re._set,0,_set.length);
		System.arraycopy(_values,0,re._values,0,_values.length);
		re.copyBase(this);
		re._freeValue=_freeValue;
		
		return re;
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
	
	/** 获取值组 */
	public SList<V> getValueList()
	{
		SList<V> list=new SList<>(_createVArrFunc,_size);
		
		if(_size==0)
			return list;
		
		V[] vals=_values;
		V v;
		for(int i=vals.length - 1;i >= 0;--i)
		{
			if((v=vals[i])!=null)
			{
				list.add(v);
			}
		}
		
		return list;
	}
	
	/** 写值组 */
	public void writeValueList(SList<V> list)
	{
		list.clear();
		
		if(_size==0)
			return;
		
		V[] vals=_values;
		V v;
		for(int i=vals.length - 1;i >= 0;--i)
		{
			if((v=vals[i])!=null)
			{
				list.add(v);
			}
		}
	}
	
	/** 获取值组 */
	public V[] getValueArr()
	{
		if(_createVArrFunc==null)
		{
			Ctrl.throwError("没有数组构造方法,不能使用该方法");
			return null;
		}
		
		V[] re=_createVArrFunc.create(_size);
		
		if(_size==0)
			return re;
		
		int j=0;
		
		V[] vals=_values;
		V v;
		for(int i=vals.length - 1;i >= 0;--i)
		{
			if((v=vals[i])!=null)
			{
				re[j++]=v;
			}
		}
		
		return re;
	}
	
	/** 获取排序好的List */
	public LongList getSortedKeyList()
	{
		LongList list=new LongList(size());
		
		if(_size==0)
			return list;
		
		long[] values=list.getValues();
		int j=0;
		
		long free=_freeValue;
		long[] keys=_set;
		for(int i=(keys.length) - 1;i >= 0;--i)
		{
			long key;
			if((key=keys[i])!=free)
			{
				values[j++]=key;
			}
		}
		
		list.justSetSize(size());
		
		list.sort();
		
		return list;
	}
	
	/** 遍历 */
	public void forEach(ILongObjectConsumer<? super V> consumer)
	{
		if(_size==0)
			return;

		int version=_version;
		long free=_freeValue;
		long[] keys=_set;
		V[] vals=_values;
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
	public void forEachS(ILongObjectConsumer<? super V> consumer)
	{
		if(_size==0)
			return;
		
		long free=_freeValue;
		long[] keys=_set;
		V[] vals=_values;
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
	
	/** 遍历值(null不传) */
	public void forEachValue(IObjectConsumer<? super V> consumer)
	{
		if(_size==0)
		{
			return;
		}

		int version=_version;
		V[] vals=_values;
		V v;
		for(int i=vals.length - 1;i >= 0;--i)
		{
			if((v=vals[i])!=null)
			{
				consumer.accept(v);
			}
		}
		
		if(version!=_version)
		{
			Ctrl.throwError("ForeachModificationException");
		}
	}
	
	/** 可修改遍历值(null不传) */
	public void forEachValueS(IObjectConsumer<? super V> consumer)
	{
		if(_size==0)
		{
			return;
		}
		
		long free=_freeValue;
		long[] keys=_set;
		V[] vals=_values;
		long key;
		int safeIndex=getLastFreeIndex();
		
		for(int i=safeIndex-1;i>=0;--i)
		{
			if((key=keys[i])!=free)
			{
				consumer.accept(vals[i]);
				
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
				consumer.accept(vals[i]);
				
				if(key!=keys[i])
				{
					++i;
				}
			}
		}
	}
	
	@Override
	public Iterator<V> iterator()
	{
		return new ForEachIterator();
	}
	
	private class ForEachIterator extends BaseIterator implements Iterator<V>
	{
		private long _tFv;
		private long[] _tSet;
		private V[] _tValues;
		private long _k;
		private V _v;
		
		public ForEachIterator()
		{
			_tSet=_set;
			_tValues=_values;
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
						_v=_tValues[_index];
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
						_v=_tValues[_index];
						return true;
					}
				}
				
				_v=null;
				return false;
			}
		}
		
		@Override
		public V next()
		{
			return _v;
		}

		@Override
		public void remove()
		{
			LongObjectMap.this.remove(_k);
		}
	}
	
	/** 转化为原生集合 */
	public HashMap<Long,V> toNatureMap()
	{
		HashMap<Long,V> re=new HashMap<>(size());
		
		long free=_freeValue;
		long[] keys=_set;
		V[] vals=_values;
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
	
	public void addAll(Map<Long,V> map)
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
	
	public class Entry<V>
	{
		public long key;
		public V value;
	}
	
	public class EntrySet implements Iterable<Entry<V>>
	{
		@Override
		public Iterator<Entry<V>> iterator()
		{
			return new EntryIterator();
		}
	}
	
	private class EntryIterator extends BaseIterator implements Iterator<Entry<V>>
	{
		private long _tFv;
		private long[] _tSet;
		private V[] _tValues;
		private Entry<V> _entry=new Entry<>();
		
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
				
				_entry.value=null;
				return false;
			}
		}
		
		@Override
		public Entry<V> next()
		{
			return _entry;
		}
		
		@Override
		public void remove()
		{
			LongObjectMap.this.remove(_entry.key);
		}
	}
}
