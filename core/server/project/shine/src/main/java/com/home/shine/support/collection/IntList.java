package com.home.shine.support.collection;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.inter.IIntConsumer;
import com.home.shine.utils.ObjectUtils;

import java.util.Arrays;

public class IntList extends BaseList
{
	private int[] _values;
	
	public IntList()
	{
		_values=ObjectUtils.EmptyIntArr;
	}
	
	public IntList(int capacity)
	{
		init(countCapacity(capacity));
	}
	
	public int capacity()
	{
		return _values.length;
	}
	
	public final int[] getValues()
	{
		return _values;
	}
	
	private void init(int capacity)
	{
		_values=new int[capacity];

		_size=0;
	}
	
	private void remake(int capacity)
	{
		int[] n=new int[capacity];
		if(_values.length>0)
			System.arraycopy(_values,0,n,0,_size);
		_values=n;
	}

	/** 直接设置 */
	public void set(int index,int value)
	{
		if (index >= _size) {
			Ctrl.throwError("indexOutOfBound");
		}

		_values[index]=value;
	}
	
	/** 添加 */
	public void add(int value)
	{
		if(_values.length==0)
			init(_minSize);
		else if(_size==_values.length)
			remake(_values.length<<1);
		
		_values[_size++]=value;
	}
	
	/** 添加2个 */
	public void add2(int v1,int v2)
	{
		if(_values.length==0)
			init(_minSize);
		else if(_size + 2>(_values.length))
			remake(_values.length<<1);
		
		_values[_size++]=v1;
		_values[_size++]=v2;
	}
	
	/** 添加3个 */
	public void add3(int v1,int v2,int v3)
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
	public void unshift(int value)
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
	public int get(int index)
	{
		if(index >= _size)
		{
			Ctrl.throwError("indexOutOfBound");
		}
		
		return _values[index];
	}
	
	public int justGet(int index)
	{
		return _values[index];
	}
	
	/** 删除某序号的元素 */
	public int remove(int index)
	{
		if(_size==0)
		{
			return 0;
		}
		
		int v=_values[index];
		
		int numMoved=_size - index - 1;
		
		if(numMoved>0)
		{
			System.arraycopy(_values,index + 1,_values,index,numMoved);
		}
		
		--_size;
		
		return v;
	}
	
	/** 移除最后一个元素 */
	public int pop()
	{
		if(_size==0)
			return 0;
		
		int re=_values[--_size];
		_values[_size]=0;
		return re;
	}
	
	public int indexOf(int value)
	{
		return indexOf(0,value);
	}

	public int indexOf(int offset,int value)
	{
		if(_size==0)
			return -1;
		
		int[] values=_values;
		
		for(int i=offset, len=_size;i<len;++i)
		{
			if(values[i]==value)
			{
				return i;
			}
		}
		
		return -1;
	}
	
	public int lastIndexOf(int value)
	{
		return lastIndexOf(_size - 1,value);
	}

	public int lastIndexOf(int offset,int value)
	{
		if(_size==0)
			return -1;
		
		int[] values=_values;
		
		for(int i=offset;i >= 0;--i)
		{
			if(values[i]==value)
			{
				return i;
			}
		}
		
		return -1;
	}
	
	public void insert(int offset,int value)
	{
		if(offset>=_size)
		{
			add(value);
			return;
		}
		
		int capacity;
		if(_size + 1>(capacity=_values.length))
		{
			int[] n=new int[capacity==0 ? _minSize : capacity << 1];
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
	
	/** 插入两个 */
	public void insert2(int offset,int value,int value2)
	{
		if(offset>=_size)
		{
			add2(value,value2);
			return;
		}
		
		int capacity;
		if(_size + 2>(capacity=_values.length))
		{
			int[] n=new int[capacity==0 ? _minSize : capacity << 1];
			System.arraycopy(_values,0,n,0,offset);
			System.arraycopy(_values,offset,n,offset + 2,_size - offset);
			
			n[offset]=value;
			n[offset+1]=value2;
			_values=n;
		}
		else
		{
			System.arraycopy(_values,offset,_values,offset + 2,_size - offset);
			
			_values[offset]=value;
			_values[offset+1]=value2;
		}
		
		_size+=2;
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
	
	/** 设置长度 */
	public void setLength(int length)
	{
		ensureCapacity(length);
		
		if(length<_size)
		{
			for(int i=_size-1;i>=length;--i)
			{
			    _values[i]=0;
			}
		}
		
		justSetSize(length);
	}
	
	/** 转换数组 */
	public int[] toArray()
	{
		if(_size==0)
		{
			return ObjectUtils.EmptyIntArr;
		}
		
		int[] re=new int[_size];
		
		System.arraycopy(_values,0,re,0,_size);
		
		return re;
	}
	
	public void forEach(IIntConsumer consumer)
	{
		if(_size==0)
		{
			return;
		}
		
		int[] values=_values;
		
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
	
	/** 倒置 */
	public void reverse()
	{
		if(_size==0)
			return;
		
		int halfSize=_size>>1;
		int max=_size-1;
		
		int[] values=_values;
		int temp;
		
		for(int i=halfSize-1;i>=0;--i)
		{
		    temp=values[i];
		    values[i]=values[max-i];
			values[max-i]=temp;
		}
	}


}
