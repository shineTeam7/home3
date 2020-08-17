package com.home.shine.support.collection;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.inter.ILongConsumer;
import com.home.shine.utils.ObjectUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class LongList extends BaseList
{
	private long[] _values;
	
	public LongList()
	{
		init(0);
	}
	
	public LongList(int capacity)
	{
		init(countCapacity(capacity));
	}
	
	public final long[] getValues()
	{
		return _values;
	}
	
	@Override
	protected void init(int capacity)
	{
		_capacity=capacity;
		
		if(capacity==0)
			_values=ObjectUtils.EmptyLongArr;
		else
			_values=new long[capacity];
	}
	
	protected void remake(int capacity)
	{
		long[] oldValues=_values;
		init(capacity);
		
		if(oldValues.length>0 && _size>0)
			System.arraycopy(oldValues,0,_values,0,_size);
	}

	public void set(int index,long value)
	{
		if(index >= _size)
		{
			Ctrl.throwError("indexOutOfBound");
		}

		_values[index]=value;
	}
	
	/** 添加 */
	public void add(long value)
	{
		addCapacity();
		
		_values[_size++]=value;
	}
	
	/** 添加2个 */
	public void add2(long v1,long v2)
	{
		addCapacity(2);
		
		_values[_size++]=v1;
		_values[_size++]=v2;
	}
	
	/** 添加一组 */
	public void addArr(long[] arr)
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
	public void unshift(long value)
	{
		addCapacity();
		
		if(_size>0)
			System.arraycopy(_values,0,_values,1,_size);
		
		_values[0]=value;
		_size++;
	}
	
	public long get(int index)
	{
		if(index >= _size)
		{
			Ctrl.throwError("indexOutOfBound");
		}
		
		return _values[index];
	}
	
	public long justGet(int index)
	{
		return _values[index];
	}
	
	/** 删除某序号的元素 */
	public long remove(int index)
	{
		if(_size==0)
		{
			return 0;
		}
		
		long v=_values[index];
		
		int numMoved=_size - index - 1;
		
		if(numMoved>0)
		{
			System.arraycopy(_values,index + 1,_values,index,numMoved);
		}
		
		--_size;
		
		return v;
	}
	
	/** 删除一定范围的内容(start<=index<end) */
	public void removeRange(int start,int end)
	{
		if(_size==0)
		{
			return;
		}
		
		if(end-start<=0)
			return;
		
		if(end==start+1)
		{
			remove(start);
			return;
		}
		
		int len=end-start;
		
		int numMoved=_size - end;
		
		if(numMoved>0)
		{
			System.arraycopy(_values,end,_values,start,numMoved);
		}
		
		_size-=len;
	}
	
	public int indexOf(long value)
	{
		return indexOf(0,value);
	}
	
	public int indexOf(int offset,long value)
	{
		if(isEmpty())
			return -1;
		
		long[] values=_values;
		
		for(int i=offset, len=_size;i<len;++i)
		{
			if(values[i]==value)
			{
				return i;
			}
		}
		
		return -1;
	}
	
	public int lastIndexOf(long value)
	{
		return lastIndexOf(_size - 1,value);
	}
	
	public int lastIndexOf(int offset,long value)
	{
		if(isEmpty())
			return -1;
		
		long[] values=_values;
		
		for(int i=offset;i >= 0;--i)
		{
			if(values[i]==value)
			{
				return i;
			}
		}
		
		return -1;
	}
	
	public void insert(int offset,long value)
	{
		if(offset>=_size)
		{
			add(value);
			return;
		}
		
		int capacity;
		if(_size + 1>(capacity=_values.length))
		{
			long[] n=new long[capacity==0 ? _minSize : capacity << 1];
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
	public void insert2(int offset,long value,long value2)
	{
		if(offset>=_size)
		{
			add2(value,value2);
			return;
		}
		
		int capacity;
		if(_size + 2>(capacity=_values.length))
		{
			long[] n=new long[capacity==0 ? _minSize : capacity << 1];
			System.arraycopy(_values,0,n,0,offset);
			System.arraycopy(_values,offset,n,offset + 2,_size - offset);
			
			n[offset]=value;
			n[offset+1]=value2;
			_values=n;
			_capacity=n.length;
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
	
	/** 转换数组 */
	public long[] toArray()
	{
		if(_size==0)
		{
			return ObjectUtils.EmptyLongArr;
		}
		
		long[] re=new long[_size];
		
		System.arraycopy(_values,0,re,0,_size);
		
		return re;
	}
	
	/** 转化为原生集合 */
	public ArrayList<Long> toNatureList()
	{
		ArrayList<Long> re=new ArrayList<>(size());
		
		long[] values=_values;
		
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
	
	public void forEach(ILongConsumer consumer)
	{
		if(_size==0)
		{
			return;
		}
		
		long[] values=_values;
		
		for(int i=0, len=_size;i<len;++i)
		{
			consumer.accept(values[i]);
		}
	}
	
	/** 排序 */
	public void sort()
	{
		if(isEmpty())
			return;
		
		Arrays.sort(_values,0,_size);
	}
}
