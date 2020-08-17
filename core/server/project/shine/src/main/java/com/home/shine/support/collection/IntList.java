package com.home.shine.support.collection;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.inter.IIntConsumer;
import com.home.shine.utils.ObjectUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class IntList extends BaseList
{
	private int[] _values;
	
	public IntList()
	{
		init(0);
	}
	
	public IntList(int capacity)
	{
		init(countCapacity(capacity));
	}
	
	public final int[] getValues()
	{
		return _values;
	}
	
	@Override
	protected void init(int capacity)
	{
		_capacity=capacity;
		
		if(capacity==0)
			_values=ObjectUtils.EmptyIntArr;
		else
			_values=new int[capacity];
	}
	
	@Override
	protected void remake(int capacity)
	{
		int[] oldValues=_values;
		init(capacity);
		
		if(oldValues.length>0 && _size>0)
			System.arraycopy(oldValues,0,_values,0,_size);
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
		addCapacity();
		
		_values[_size++]=value;
	}
	
	/** 添加2个 */
	public void add2(int v1,int v2)
	{
		addCapacity(2);
		
		_values[_size++]=v1;
		_values[_size++]=v2;
	}
	
	/** 添加3个 */
	public void add3(int v1,int v2,int v3)
	{
		addCapacity(3);
		
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
		addCapacity();
		
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
			_capacity=n.length;
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
	
	/** 转化为原生集合 */
	public ArrayList<Integer> toNatureList()
	{
		ArrayList<Integer> re=new ArrayList<>(size());
		
		int[] values=_values;
		
		for(int i=0, len=_size;i<len;++i)
		{
			re.add(values[i]);
		}
		
		return re;
	}
	
	public void addAll(Collection<Integer> collection)
	{
		ensureCapacity(collection.size());
		collection.forEach(this::add);
	}
}
