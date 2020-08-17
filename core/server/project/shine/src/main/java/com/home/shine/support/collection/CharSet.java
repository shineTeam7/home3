package com.home.shine.support.collection;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.inter.ICharConsumer;
import com.home.shine.utils.MathUtils;
import com.home.shine.utils.ObjectUtils;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

public class CharSet extends BaseHash implements Iterable<Character>
{
	private char[] _set;
	
	private char _freeValue;
	
	public CharSet()
	{
		init(_minSize);
	}
	
	public CharSet(int capacity)
	{
		init(countCapacity(capacity));
	}
	
	public final char getFreeValue()
	{
		return _freeValue;
	}
	
	public final char[] getKeys()
	{
		return _set;
	}
	
	@Override
	protected void init(int capacity)
	{
		_capacity=capacity;
		
		_set=new char[capacity<<1];
		
		if(_freeValue!=0)
		{
			Arrays.fill(_set,_freeValue);
		}
	}
	
	private char changeFree()
	{
		char newFree=findNewFreeOrRemoved();
		
		char free=_freeValue;
		char[] keys=_set;
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
	
	public void setFreeValue(char value)
	{
		_freeValue=value;
	}
	
	private char findNewFreeOrRemoved()
	{
		char free=_freeValue;
		
		char newFree;
		{
			do
			{
				newFree=(char)(MathUtils.randomInt());
			}
			while((newFree==free) || ((index(newFree)) >= 0));
		}
		return newFree;
	}
	
	private int index(char key)
	{
		char free;
		if(key!=(free=_freeValue))
		{
			char[] keys=_set;
			int capacityMask;
			int index;
			char cur;
			if((cur=keys[(index=hashChar(key) & (capacityMask=(keys.length) - 1))])==key)
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
	protected int toGetLastFreeIndex()
	{
		char free=_freeValue;
		char[] keys=_set;
		
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
	
	protected void rehash(int newCapacity)
	{
		super.rehash(newCapacity);
		
		char free=_freeValue;
		char[] keys=_set;
		init(newCapacity);
		char[] newKeys=_set;
		int capacityMask=(newKeys.length) - 1;
		for(int i=(keys.length) - 1;i >= 0;--i)
		{
			char key;
			if((key=keys[i])!=free)
			{
				int index;
				if((newKeys[(index=hashChar(key) & capacityMask)])!=free)
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
	
	public boolean add(char key)
	{
		char free;
		if(key==(free=_freeValue))
		{
			free=changeFree();
		}
		char[] keys=_set;
		int capacityMask;
		int index;
		char cur;
		keyAbsent:
		if((cur=keys[(index=hashChar(key) & (capacityMask=(keys.length) - 1))])!=free)
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
	
	public boolean contains(char key)
	{
		if(_size==0)
			return false;
		
		return (index(key)) >= 0;
	}
	
	public boolean remove(char key)
	{
		if(_size==0)
			return false;
		
		char free;
		if(key!=(free=_freeValue))
		{
			char[] keys=_set;
			int capacityMask=(keys.length) - 1;
			int index;
			char cur;
			keyPresent:
			if((cur=keys[(index=hashChar(key) & capacityMask)])!=key)
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
				char keyToShift;
				if((keyToShift=keys[indexToShift])==free)
				{
					break;
				}
				if(((hashChar(keyToShift) - indexToShift) & capacityMask) >= shiftDistance)
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
	
	/** 清空 */
	public void clear()
	{
		if(_size==0)
			return;
		
		justClearSize();
		
		Arrays.fill(_set,_freeValue);
	}
	
	/** 转换数组(带排序) */
	public char[] toArray()
	{
		if(_size==0)
		{
			return ObjectUtils.EmptyCharArr;
		}
		
		char[] re=new char[_size];
		int j=0;
		
		char[] keys=_set;
		int fv=_freeValue;
		char k;
		
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
	
	/** 遍历 */
	public void forEach(ICharConsumer consumer)
	{
		if(_size==0)
		{
			return;
		}
		
		int version=_version;
		char free=_freeValue;
		char[] keys=_set;
		for(int i=(keys.length) - 1;i >= 0;--i)
		{
			char key;
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
	public void forEachS(ICharConsumer consumer)
	{
		if(_size==0)
		{
			return;
		}
		
		char free=_freeValue;
		char[] keys=_set;
		char key;
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
	
	@Override
	public Iterator<Character> iterator()
	{
		return new ForEachIterator();
	}
	
	private class ForEachIterator extends BaseIterator implements Iterator<Character>
	{
		private char _tFv;
		private char[] _tSet;
		private char _k;
		
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
			
			char key;
			
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
				
				_k=_tFv;
				return false;
			}
		}
		
		@Override
		public Character next()
		{
			return _k;
		}
		
		@Override
		public void remove()
		{
			CharSet.this.remove(_k);
		}
	}
}
