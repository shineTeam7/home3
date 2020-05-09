package com.home.shine.support.collection;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.inter.ICharConsumer;
import com.home.shine.utils.ObjectUtils;

import java.util.Arrays;
import java.util.Iterator;

public class CharList extends BaseList implements Iterable<Character>
{
	private char[] _values;
	
	public CharList()
	{
		_values=ObjectUtils.EmptyCharArr;
	}
	
	public CharList(int capacity)
	{
		init(countCapacity(capacity));
	}
	
	public int capacity()
	{
		return _values.length;
	}
	
	public final char[] getValues()
	{
		return _values;
	}
	
	private void init(int capacity)
	{
		_values=new char[capacity];
		
		_size=0;
	}
	
	private void remake(int capacity)
	{
		char[] n=new char[capacity];
		if(_values.length>0)
			System.arraycopy(_values,0,n,0,_size);
		_values=n;
	}
	
	/** 直接设置 */
	public void set(int index,char value)
	{
		if (index >= _size) {
			Ctrl.throwError("indexOutOfBound");
		}
		
		_values[index]=value;
	}
	
	/** 添加 */
	public void add(char value)
	{
		if(_values.length==0)
			init(_minSize);
		else if(_size==_values.length)
			remake(_values.length<<1);
		
		_values[_size++]=value;
	}
	
	/** 添加2个 */
	public void add2(char v1,char v2)
	{
		if(_values.length==0)
			init(_minSize);
		else if(_size + 2>(_values.length))
			remake(_values.length<<1);
		
		_values[_size++]=v1;
		_values[_size++]=v2;
	}
	
	/** 添加3个 */
	public void add3(char v1,char v2,char v3)
	{
		if(_values.length==0)
			init(_minSize);
		else if(_size + 3>(_values.length))
			remake(_values.length<<1);
		
		_values[_size++]=v1;
		_values[_size++]=v2;
		_values[_size++]=v3;
	}
	
	/** 添加一组 */
	public void addArr(int[] arr)
	{
		int d=_size + arr.length;
		
		if(d>_values.length)
		{
			remake(countCapacity(d));
		}
		
		System.arraycopy(arr,0,_values,_size,arr.length);
		_size=d;
	}
	
	/** 添加元素到头 */
	public void unshift(char value)
	{
		if(_values.length==0)
			init(_minSize);
		else if(_size==_values.length)
			remake(_values.length<<1);
		
		if(_size>0)
			System.arraycopy(_values,0,_values,1,_size);
		
		_values[0]=value;
		_size++;
	}
	
	/** 获取对应元素 */
	public char get(int index)
	{
		if(index >= _size)
		{
			Ctrl.throwError("indexOutOfBound");
		}
		
		return _values[index];
	}
	
	public char justGet(int index)
	{
		return _values[index];
	}
	
	/** 删除某序号的元素 */
	public char remove(int index)
	{
		if(_size==0)
		{
			return 0;
		}
		
		char v=_values[index];
		
		int numMoved=_size - index - 1;
		
		if(numMoved>0)
		{
			System.arraycopy(_values,index + 1,_values,index,numMoved);
		}
		
		--_size;
		
		return v;
	}
	
	public int indexOf(char value)
	{
		return indexOf(0,value);
	}
	
	public int indexOf(int offset,char value)
	{
		if(_size==0)
			return -1;
		
		char[] values=_values;
		
		for(int i=offset, len=_size;i<len;++i)
		{
			if(values[i]==value)
			{
				return i;
			}
		}
		
		return -1;
	}
	
	public int lastIndexOf(char value)
	{
		return lastIndexOf(_size - 1,value);
	}
	
	public int lastIndexOf(int offset,char value)
	{
		if(_size==0)
			return -1;
		
		char[] values=_values;
		
		for(int i=offset;i >= 0;--i)
		{
			if(values[i]==value)
			{
				return i;
			}
		}
		
		return -1;
	}
	
	public void insert(int offset,char value)
	{
		if(offset>=_size)
		{
			add(value);
			return;
		}
		
		int capacity;
		if(_size + 1>(capacity=_values.length))
		{
			char[] n=new char[capacity==0 ? _minSize : capacity << 1];
			System.arraycopy(_values,0,n,0,offset);
			System.arraycopy(_values,offset,n,offset + 1,_size - offset);
			
			n[offset]=value;
			_values=n;
		}
		else
		{
			System.arraycopy(_values,offset,_values,offset + 1,_size - offset);
			
			_values[offset]=value;
		}
		
		++_size;
	}
	
	public void clear()
	{
		_size=0;
	}
	
	/** 扩容 */
	public void ensureCapacity(int capacity)
	{
		if(capacity>_values.length)
		{
			remake(countCapacity(capacity));
		}
	}
	
	/** 转换数组 */
	public char[] toArray()
	{
		if(_size==0)
		{
			return ObjectUtils.EmptyCharArr;
		}
		
		char[] re=new char[_size];
		
		System.arraycopy(_values,0,re,0,_size);
		
		return re;
	}
	
	public void forEach(ICharConsumer consumer)
	{
		if(_size==0)
		{
			return;
		}
		
		char[] values=_values;
		
		for(int i=0, len=_size;i<len;++i)
		{
			consumer.accept(values[i]);
		}
	}
	
	/** 排序 */
	public void sort()
	{
		if(_size==0)
			return;
		
		Arrays.sort(_values,0,_size);
	}
	
	@Override
	public Iterator<Character> iterator()
	{
		return new ForEachIterator();
	}
	
	private class ForEachIterator implements Iterator<Character>
	{
		private int _index;
		private int _tSize;
		private char[] _tValues;
		
		private char _v;
		
		public ForEachIterator()
		{
			_index=0;
			_tSize=_size;
			_tValues=_values;
		}
		
		@Override
		public boolean hasNext()
		{
			if(_index<_tSize)
			{
				_v=_tValues[_index++];
				return true;
			}
			
			_v=0;
			return false;
		}
		
		@Override
		public Character next()
		{
			return _v;
		}
	}
}