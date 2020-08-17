package com.home.shine.support.collection;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.inter.ICreateArray;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.function.Consumer;

public class SSet<K> extends BaseHash implements Iterable<K>
{
	private K[] _set;
	
	private ICreateArray<K> _createKArrFunc;
	
	public SSet()
	{
		init(_minSize);
	}
	
	public SSet(int capacity)
	{
		init(countCapacity(capacity));
	}
	
	public SSet(ICreateArray<K> createKArrFunc)
	{
		_createKArrFunc=createKArrFunc;
		init(_minSize);
	}
	
	public SSet(ICreateArray<K> createKArrFunc,int capacity)
	{
		_createKArrFunc=createKArrFunc;
		init(countCapacity(capacity));
	}
	
	/** 获取表 */
	public K[] getKeys()
	{
		return _set;
	}
	
	@SuppressWarnings("unchecked")
	private K[] createVArray(int length)
	{
		if(_createKArrFunc!=null)
			return _createKArrFunc.create(length);
		
		return ((K[])(new Object[length]));
	}
	
	@Override
	protected void init(int capacity)
	{
		_capacity=capacity;

		_set=createVArray(capacity<<1);
	}
	
	public boolean add(K key)
	{
		if(key==null)
		{
			Ctrl.errorLog("key不能为空");
			return false;
		}
		
		K[] keys=_set;
		int capacityMask;
		int index;
		K cur;
		keyAbsent:
		if((cur=keys[(index=hashObj(key) & (capacityMask=(keys.length) - 1))])!=null)
		{
			if((cur==key) || key.equals(cur))
			{
				return false;
			}
			else
			{
				while(true)
				{
					if((cur=keys[(index=(index - 1) & capacityMask)])==null)
					{
						break keyAbsent;
					}
					else if((cur==key) || key.equals(cur))
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
	
	@Override
	protected int toGetLastFreeIndex()
	{
		K[] set=_set;
		
		for(int i=set.length - 1;i >= 0;--i)
		{
			if(set[i]==null)
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
		K[] keys=_set;
		init(newCapacity);
		K[] newKeys=_set;
		int capacityMask=(newKeys.length) - 1;
		for(int i=(keys.length) - 1;i >= 0;i--)
		{
			K key;
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
			}
		}
	}
	
	private int index(K key)
	{
		K k=key;
		K[] keys=_set;
		int capacityMask;
		int index;
		K cur;
		if((cur=keys[(index=hashObj(k) & (capacityMask=(keys.length) - 1))])==k)
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
				if(k.equals(cur))
				{
					return index;
				}
				else
				{
					while(true)
					{
						if((cur=keys[(index=(index - 1) & capacityMask)])==k)
						{
							return index;
						}
						else if(cur==null)
						{
							return -1;
						}
						else if(k.equals(cur))
						{
							return index;
						}
					}
				}
			}
		}
	}
	
	public boolean contains(K key)
	{
		if(key==null)
		{
			Ctrl.throwError("key不能为空");
			return false;
		}
		
		if(_size==0)
			return false;
		
		return index(key) >= 0;
	}
	
	public boolean remove(K key)
	{
		if(key==null)
		{
			Ctrl.throwError("key不能为空");
			return false;
		}
		
		if(_size==0)
			return false;
		
		K k=key;
		K[] keys=_set;
		int capacityMask=(keys.length) - 1;
		int index;
		K cur;
		keyPresent:
		if((cur=keys[(index=hashObj(k) & capacityMask)])!=k)
		{
			if(cur==null)
			{
				return false;
			}
			else
			{
				if(k.equals(cur))
				{
					break keyPresent;
				}
				else
				{
					while(true)
					{
						if((cur=keys[(index=(index - 1) & capacityMask)])==k)
						{
							break keyPresent;
						}
						else if(cur==null)
						{
							return false;
						}
						else if(k.equals(cur))
						{
							break keyPresent;
						}
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
			K keyToShift;
			if((keyToShift=keys[indexToShift])==null)
			{
				break;
			}
			K castedKeyToShift=keyToShift;
			if(((hashObj(castedKeyToShift) - indexToShift) & capacityMask) >= shiftDistance)
			{
				keys[indexToRemove]=castedKeyToShift;
				indexToRemove=indexToShift;
				shiftDistance=1;
			}
			else
			{
				shiftDistance++;
			}
		}
		keys[indexToRemove]=null;
		postRemoveHook(indexToRemove);
		return true;
	}
	
	public void clear()
	{
		if(_size==0)
		{
			return;
		}
		
		justClearSize();
		
		Arrays.fill(_set,null);
	}
	
	/** 遍历并清空 */
	public void forEachAndClear(Consumer<? super K> consumer)
	{
		if(_size==0)
		{
			return;
		}
		
		K[] set=_set;
		K key;
		
		for(int i=set.length - 1;i >= 0;--i)
		{
			if((key=set[i])!=null)
			{
				consumer.accept(key);
				
				set[i]=null;
			}
		}
		
		justClearSize();
	}
	
	/** 添加一组 */
	public void addAll(SList<? extends K> list)
	{
		if(list.isEmpty())
		{
			return;
		}
		
		K[] values=list.getValues();
		
		for(int i=0,len=list.size();i<len;++i)
		{
			add(values[i]);
		}
	}
	
	/** 添加一组 */
	public void addAll(SSet<? extends K> set)
	{
		if(set.isEmpty())
		{
			return;
		}
		
		K[] keys=set.getKeys();
		K k;
		
		for(int i=keys.length-1;i>=0;--i)
		{
			if((k=keys[i])!=null)
			{
				add(k);
			}
		}
	}
	
	/** 添加一组 */
	public void addAll(K[] arr)
	{
		for(K k : arr)
		{
			add(k);
		}
	}
	
	/** 转化为原生集合 */
	public HashSet<K> toNatureSet()
	{
		HashSet<K> re=new HashSet<>(size());
		
		K[] set=_set;
		K key;
		
		for(int i=set.length - 1;i >= 0;--i)
		{
			if((key=set[i])!=null)
			{
				re.add(key);
			}
		}
		
		return re;
	}
	
	public void addAll(Collection<K> collection)
	{
		ensureCapacity(collection.size());
		collection.forEach(this::add);
	}
	
	/** 获取排序好的List */
	public SList<K> getSortedList()
	{
		SList<K> list=new SList<>(_createKArrFunc,size());
		
		if(_size==0)
			return list;
		
		K[] values=list.getValues();
		int j=0;
		
		K[] keys=_set;
		K k;
		
		for(int i=keys.length-1;i>=0;--i)
		{
			if((k=keys[i])!=null)
			{
				values[j++]=k;
			}
		}
		
		list.justSetSize(size());
		
		list.sort();
		
		return list;
	}
	
	/** 克隆 */
	public SSet<K> clone()
	{
		if(_size==0)
			return new SSet<>(_createKArrFunc);
		
		SSet<K> re=new SSet<>(_createKArrFunc,capacity());
		System.arraycopy(_set,0,re._set,0,_set.length);
		re.copyBase(this);
		return re;
	}
	
	/** 遍历 */
	public void forEach(Consumer<? super K> consumer)
	{
		if(_size==0)
		{
			return;
		}

		int version=_version;
		K[] set=_set;
		K key;
		
		for(int i=set.length - 1;i >= 0;--i)
		{
			if((key=set[i])!=null)
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
	public void forEachS(Consumer<? super K> consumer)
	{
		if(_size==0)
		{
			return;
		}
		
		K[] keys=_set;
		K key;
		int safeIndex=getLastFreeIndex();
		
		for(int i=safeIndex-1;i>=0;--i)
		{
			if((key=keys[i])!=null)
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
			if((key=keys[i])!=null)
			{
				consumer.accept(key);
				
				if(key!=keys[i])
				{
					++i;
				}
			}
		}
	}

	@Override
	public Iterator<K> iterator()
	{
		return new ForEachIterator();
	}
	
	private class ForEachIterator extends BaseIterator implements Iterator<K>
	{
		private K[] _tSet;
		private K _k;
		
		public ForEachIterator()
		{
			_tSet=_set;
		}
		
		@Override
		public boolean hasNext()
		{
			if(_k!=null && _k!=_tSet[_index])
			{
				++_index;
			}
			
			K key;
			
			if(_index<=_tSafeIndex)
			{
				while(--_index >= 0)
				{
					if((key=_tSet[_index])!=null)
					{
						_k=key;
						return true;
					}
				}
				
				_k=null;
				_index=_tSet.length;
				return hasNext();
			}
			else
			{
				while(--_index > _tSafeIndex)
				{
					if((key=_tSet[_index])!=null)
					{
						_k=key;
						return true;
					}
				}
				
				_k=null;
				return false;
			}
		}

		@Override
		public K next()
		{
			return _k;
		}

		@Override
		public void remove()
		{
			SSet.this.remove(_k);
		}
	}
	
	
}
