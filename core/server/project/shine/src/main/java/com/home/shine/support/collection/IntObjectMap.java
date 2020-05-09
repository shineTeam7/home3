package com.home.shine.support.collection;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.inter.ICreateArray;
import com.home.shine.support.collection.inter.IIntObjectConsumer;
import com.home.shine.support.collection.inter.IObjectConsumer;
import com.home.shine.support.func.ObjectIntFunc;
import com.home.shine.utils.MathUtils;
import com.koloboke.collect.impl.IntArrays;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

public class IntObjectMap<V> extends BaseHash implements Iterable<V>
{
	private int _freeValue;
	
	private int[] _set;
	
	private V[] _values;
	
	private ICreateArray<V> _createVArrFunc;
	
	private EntrySet _entrySet;
	
	public IntObjectMap()
	{
	
	}
	
	public IntObjectMap(int capacity)
	{
		init(countCapacity(capacity));
	}
	
	public IntObjectMap(ICreateArray<V> createVArrFunc)
	{
		_createVArrFunc=createVArrFunc;
	}
	
	public IntObjectMap(ICreateArray<V> createVArrFunc,int capacity)
	{
		_createVArrFunc=createVArrFunc;
		init(countCapacity(capacity));
	}
	
	private void checkInit()
	{
		if(_set!=null)
			return;
		
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
	
	public final V[] getValues()
	{
		checkInit();
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
	
	private void init(int capacity)
	{
		_maxSize=capacity;
		
		_set=new int[capacity<<1];
		
		if(_freeValue!=0)
		{
			Arrays.fill(_set,_freeValue);
		}
		
		_values=createVArray(capacity<<1);
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
		V[] vals=_values;
		init(newCapacity);
		int[] newKeys=_set;
		int capacityMask=(newKeys.length) - 1;
		V[] newVals=_values;
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
	
	private int insert(int key,V value)
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
		keyAbsent:
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
	
	public void put(int key,V value)
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
	
	/** 是否存在 */
	public boolean contains(int key)
	{
		if(_size==0)
			return false;
		
		return index(key) >= 0;
	}
	
	public V get(int key)
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
	
	public V getOrDefault(int key,V defaultValue)
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
	
	public V remove(int key)
	{
		if(_size==0)
			return null;
		
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
		{
			return;
		}
		
		justClearSize();
		
		int fv=_freeValue;
		int[] set=_set;
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
		
		int fv=_freeValue;
		int[] set=_set;
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
	
	public V putIfAbsent(int key,V value)
	{
		checkInit();
		
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
	
	public V computeIfAbsent(int key,ObjectIntFunc<? extends V> mappingFunction)
	{
		checkInit();
		
		if(mappingFunction==null)
		{
			throw new NullPointerException();
		}
		
		int free;
		if(key==(free=_freeValue))
		{
			free=changeFree();
		}
		int[] keys=_set;
		V[] vals=_values;
		int capacityMask;
		int index;
		int cur;
		keyPresent:
		if((cur=keys[(index=hashInt(key) & (capacityMask=(keys.length) - 1))])!=key)
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
	
	public void putAll(IntObjectMap<V> dic)
	{
		if(dic.isEmpty())
			return;
		
		int[] keys=dic.getKeys();
		V[] values=dic.getValues();
		int fv=dic.getFreeValue();
		int k;
		
		for(int i=keys.length-1;i>=0;--i)
		{
			if((k=keys[i])!=fv)
			{
				put(k,values[i]);
			}
		}
	}
	
	public IntObjectMap<V> clone()
	{
		if(_size==0)
			return new IntObjectMap<>(_createVArrFunc);
		
		IntObjectMap<V> re=new IntObjectMap<>(_createVArrFunc,capacity());
		System.arraycopy(_set,0,re._set,0,_set.length);
		System.arraycopy(_values,0,re._values,0,_values.length);
		re.copyBase(this);
		re._freeValue=_freeValue;
		
		return re;
	}
	
	/** 获取key对应set */
	public IntSet getKeySet()
	{
		if(_size==0)
			return new IntSet();
		
		IntSet re=new IntSet(capacity());
		re.copyBase(this);
		re.setFreeValue(_freeValue);
		System.arraycopy(_set,0,re.getKeys(),0,_set.length);
		return re;
	}
	
	/** 获取排序好的List */
	public IntList getSortedKeyList()
	{
		IntList list=new IntList(size());
		
		if(_size==0)
			return list;
		
		int[] values=list.getValues();
		int j=0;
		
		int free=_freeValue;
		int[] keys=_set;
		for(int i=(keys.length) - 1;i >= 0;--i)
		{
			int key;
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
	public void forEach(IIntObjectConsumer<? super V> consumer)
	{
		if(_size==0)
		{
			return;
		}
		
		int version=_version;
		int free=_freeValue;
		int[] keys=_set;
		V[] vals=_values;
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
	public void forEachS(IIntObjectConsumer<? super V> consumer)
	{
		if(_size==0)
		{
			return;
		}
		
		int free=_freeValue;
		int[] keys=_set;
		V[] vals=_values;
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
	
	/** 遍历值(null的不传) */
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
	
	/** 可修改遍历值(null的不传) */
	public void forEachValueS(IObjectConsumer<? super V> consumer)
	{
		if(_size==0)
		{
			return;
		}
		
		int free=_freeValue;
		int[] keys=_set;
		V[] vals=_values;
		int key;
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
			return  re;
		
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
	
	@Override
	public Iterator<V> iterator()
	{
		return new ForEachIterator();
	}
	
	private class ForEachIterator extends BaseIterator implements Iterator<V>
	{
		private int _tFv;
		private int[] _tSet;
		private V[] _tValues;
		private int _k;
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
			
			int key;
			
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
						_k=key;
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
			IntObjectMap.this.remove(_k);
		}
	}
	
	public EntrySet entrySet()
	{
		if(_entrySet!=null)
			return _entrySet;
		
		return _entrySet=new EntrySet();
	}
	
	public class Entry<V>
	{
		public int key;
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
		private int _tFv;
		private int[] _tSet;
		private V[] _tValues;
		private Entry<V> _entry=new Entry<>();
		
		public EntryIterator()
		{
			super();
			
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
			IntObjectMap.this.remove(_entry.key);
		}
	}
}
