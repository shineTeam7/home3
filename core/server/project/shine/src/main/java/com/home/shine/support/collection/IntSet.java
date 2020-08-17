package com.home.shine.support.collection;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.inter.IIntConsumer;
import com.home.shine.utils.MathUtils;
import com.home.shine.utils.ObjectUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;

public class IntSet extends BaseHash implements Iterable<Integer>
{
	private int[] _set;

	private int _freeValue;
	
	public IntSet()
	{
		init(_minSize);
	}
	
	public IntSet(int capacity)
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

	@Override
	protected void init(int capacity)
	{
		_capacity=capacity;

		_set=new int[capacity<<1];
		
		if(_freeValue!=0)
		{
			Arrays.fill(_set,_freeValue);
		}
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
	
	public void setFreeValue(int value)
	{
		_freeValue=value;
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
	
	@Override
	protected void rehash(int newCapacity)
	{
		super.rehash(newCapacity);
		
		int free=_freeValue;
		int[] keys=_set;
		init(newCapacity);
		int[] newKeys=_set;
		int capacityMask=(newKeys.length) - 1;
		for(int i=(keys.length) - 1;i >= 0;--i)
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
			}
		}
	}
	
	public boolean add(int key)
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
	
	public void addAll(int[] arr)
	{
		if(arr==null)
			return;
		
		for(int v:arr)
		{
			add(v);
		}
	}
	
	public void addAll(IntList list)
	{
		if(list==null)
			return;
		
		int[] values=list.getValues();
		
		for(int i=0,len=list.size();i<len;++i)
		{
			add(values[i]);
		}
	}
	
	public boolean contains(int key)
	{
		if(_size==0)
			return false;
		
		return (index(key)) >= 0;
	}
	
	/** 清空 */
	public void clear()
	{
		if(_size==0)
		{
			return;
		}
		
		justClearSize();
		Arrays.fill(_set,_freeValue);
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
	
	public IntSet clone()
	{
		if(_size==0)
			return new IntSet();
		
		IntSet re=new IntSet(this.capacity());
		System.arraycopy(_set,0,re._set,0,_set.length);
		re.copyBase(this);
		re._freeValue=_freeValue;
		
		return re;
	}
	
	/** 转换数组(带排序) */
	public int[] toArray()
	{
		if(_size==0)
		{
			return ObjectUtils.EmptyIntArr;
		}
		
		int[] re=new int[_size];
		int j=0;
		
		int[] keys=_set;
		int fv=_freeValue;
		int k;
		
		for(int i=keys.length-1;i>=0;--i)
		{
			if((k=keys[i])!=fv)
			{
				re[j++]=k;
			}
		}
		
		Arrays.sort(re);
		
		return re;
	}
	
	/** 转换到list */
	public IntList toList()
	{
		if(_size==0)
		{
			return new IntList();
		}
		
		IntList re=new IntList(_size);
		
		int[] keys=_set;
		int fv=_freeValue;
		int k;
		
		for(int i=keys.length-1;i>=0;--i)
		{
			if((k=keys[i])!=fv)
			{
				re.add(k);
			}
		}
		
		re.sort();
		
		return re;
	}
	
	/** 转化为原生集合 */
	public HashSet<Integer> toNatureSet()
	{
		HashSet<Integer> re=new HashSet<>(size());
		
		int[] keys=_set;
		int fv=_freeValue;
		int k;
		
		for(int i=keys.length-1;i>=0;--i)
		{
			if((k=keys[i])!=fv)
			{
				re.add(k);
			}
		}
		
		return re;
	}
	
	public void addAll(Collection<Integer> collection)
	{
		ensureCapacity(collection.size());
		collection.forEach(this::add);
	}
	
	/** 遍历 */
	public void forEachA(IIntConsumer consumer)
	{
		if(_size==0)
		{
			return;
		}

		int version=_version;
		int free=_freeValue;
		int[] keys=_set;
		for(int i=(keys.length) - 1;i >= 0;--i)
		{
			int key;
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
	
	/** 可修改的遍历 */
	public void forEachS(IIntConsumer consumer)
	{
		if(_size==0)
		{
			return;
		}
		
		int free=_freeValue;
		int[] keys=_set;
		int key;
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
	public void forEachAndClear(IIntConsumer consumer)
	{
		int free=_freeValue;
		int[] keys=_set;
		for(int i=(keys.length) - 1;i >= 0;i--)
		{
			int key;
			if((key=keys[i])!=free)
			{
				consumer.accept(key);
				keys[i]=free;
			}
		}
		
		justClearSize();
	}
	
	@Override
	public Iterator<Integer> iterator()
	{
		return new ForEachIterator();
	}
	
	private class ForEachIterator extends BaseIterator implements Iterator<Integer>
	{
		private int _tFv;
		private int[] _tSet;
		private int _k;
		
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
			
			int key;
			
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
				
				return false;
			}
		}
		
		@Override
		public Integer next()
		{
			return _k;
		}
		
		@Override
		public void remove()
		{
			IntSet.this.remove(_k);
		}
	}
}
