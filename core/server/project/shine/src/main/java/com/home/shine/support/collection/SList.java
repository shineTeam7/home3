package com.home.shine.support.collection;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.inter.ICreateArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Consumer;

/** SList,增强for循环不支持多线程同时遍历 */
public class SList<V> extends BaseList implements Iterable<V>
{
	private V[] _values;
	
	private ICreateArray<V> _createVArrFunc;
	
	public SList()
	{
		init(_minSize);
	}
	
	public SList(int capacity)
	{
		init(countCapacity(capacity));
	}
	
	public SList(Collection<V> collection)
	{
		if(collection.isEmpty())
			return;
		
		init(countCapacity(collection.size()));
		
		for(V v : collection)
		{
			add(v);
		}
	}
	
	public SList(ICreateArray<V> createVArrFunc)
	{
		_createVArrFunc=createVArrFunc;
		init(_minSize);
	}
	
	public SList(ICreateArray<V> createVArrFunc,int capacity)
	{
		_createVArrFunc=createVArrFunc;
		init(countCapacity(capacity));
	}
	
	public SList(ICreateArray<V> createVArrFunc,Collection<V> collection)
	{
		_createVArrFunc=createVArrFunc;
		
		if(collection.isEmpty())
			return;
		
		init(countCapacity(collection.size()));
		
		for(V v : collection)
		{
			add(v);
		}
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
		if(capacity<_minSize)
			capacity=_minSize;
		
		_capacity=capacity;
		
		_values=createVArray(capacity);
	}
	
	@Override
	protected void remake(int capacity)
	{
		V[] oldArr=_values;
		init(capacity);
		
		if(_size>0)
			System.arraycopy(oldArr,0,_values,0,_size);
	}
	
	/** 添加 */
	public void add(V value)
	{
		if(_size==_capacity)
			remake(_capacity << 1);
		
		_values[_size++]=value;
	}
	
	/** 添加一组 */
	public void addArr(V[] arr)
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
	public void unshift(V value)
	{
		if(_size==_capacity)
			remake(_capacity << 1);
		
		if(_size>0)
			System.arraycopy(_values,0,_values,1,_size);
		
		_values[0]=value;
		_size++;
	}
	
	/** 获取对应元素 */
	public V get(int index)
	{
		if (index >= _size) {
			Ctrl.throwError("indexOutOfBound");
			return null;
		}

		return _values[index];
	}
	
	/** 设置对应元素 */
	public void set(int index,V obj)
	{
		if (index >= _size) {
			Ctrl.throwError("indexOutOfBound");
			return;
		}

		_values[index]=obj;
	}
	
	public V justGet(int index)
	{
		return _values[index];
	}
	
	/** 删除某序号的元素 */
	public V remove(int index)
	{
		if(_size==0)
			return null;
		
		V v=_values[index];
		
		int numMoved=_size - index - 1;
		
		if(numMoved>0)
		{
			System.arraycopy(_values,index + 1,_values,index,numMoved);
		}
		
		_values[--_size]=null;
		
		return v;
	}
	
	/** 移除对象 */
	public boolean removeObj(V obj)
	{
		int index=indexOf(obj);
		
		if(index!=-1)
		{
			remove(index);
			return true;
		}
		
		return false;
	}
	
	/** 从start移除到end(start<=x<end) */
	public void remove(int start,int end)
	{
		if(_size==0)
			return;
		
		int d=end-start;
		
		if(d<=0)
			return;
		
		if(end<_size)
		{
			System.arraycopy(_values,end,_values,start,_size-end);
		}
		
		end=_size-d;
		
		for(int i=_size-1;i>=end;--i)
		{
		    _values[i]=null;
		}
		
		_size=end;
	}
	
	public boolean contains(V obj)
	{
		return indexOf(obj)>=0;
	}
	
	/** 删除最前一个元素 */
	public V shift()
	{
		return remove(0);
	}
	
	/** 移除最后一个元素 */
	public V pop()
	{
		if(_size==0)
			return null;
		
		V v=_values[--_size];
		_values[_size]=null;
		return v;
	}
	
	/** 截取list */
	public SList<V> subList(int from,int end)
	{
		if(from<0)
			from=0;
		
		if(end>=size())
			end=size();
		
		int len=end-from;
		
		if(len<=0)
		{
			Ctrl.throwError("subList,数据非法",from,end);
			return null;
		}
		
		SList<V> re=new SList<>(_createVArrFunc,len);
		System.arraycopy(_values,from,re._values,0,len);
		re.justSetSize(len);
		return re;
	}
	
	/** 获取末尾 */
	public V getLast()
	{
		if(_size==0)
			return null;
		
		return _values[_size-1];
	}
	
	public int indexOf(V value)
	{
		return indexOf(0,value);
	}
	
	public int indexOf(int offset,V value)
	{
		if(_size==0)
			return -1;
		
		V[] values=_values;
		V v;
		
		for(int i=offset, len=_size;i<len;++i)
		{
			if(value==(v=values[i]) || value.equals(v))
			{
				return i;
			}
		}
		
		return -1;
	}
	
	public int lastIndexOf(V value)
	{
		return lastIndexOf(_size - 1,value);
	}
	
	public int lastIndexOf(int offset,V value)
	{
		if(_size==0)
			return -1;
		
		V[] values=_values;
		V v;
		
		for(int i=offset;i >= 0;--i)
		{
			if(value==(v=values[i]) || value.equals(v))
			{
				return i;
			}
		}
		
		return -1;
	}
	
	public void insert(int offset,V value)
	{
		if(offset>=_size)
		{
			add(value);
			return;
		}
		
		if(_size + 1>_values.length)
		{
			V[] n=createVArray(_values.length << 1);
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
	
	/** 二分查找插入 */
	public void binaryInsert(V value,Comparator<? super V> comparator)
	{
		if(isEmpty())
		{
			add(value);
			return;
		}
		
		ensureCapacity(_size+1);
		
		//TODO:实现完
		
		//int index=Arrays.binarySearch(_values,0,_size,comparator);
		//
		//if(index>=0)
		//{
		//
		//}
	}
	
	public void clear()
	{
		if(_size==0)
			return;
		
		V[] values=_values;
		
		for(int i=_size - 1;i >= 0;--i)
		{
			values[i]=null;
		}
		
		_size=0;
	}
	
	/** 遍历并清空 */
	public void forEachAndClear(Consumer<? super V> consumer)
	{
		if(_size==0)
			return;
		
		V[] values=_values;
		
		for(int i=_size - 1;i >= 0;--i)
		{
			consumer.accept(values[i]);
			values[i]=null;
		}
		
		_size=0;
	}
	
	/** 尺寸扩容 */
	public void growSize(int size)
	{
		if(_size<size)
		{
			ensureCapacity(size);
			_size=size;
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
				_values[i]=null;
			}
		}
		
		justSetSize(length);
	}
	
	/** 转换数组 */
	public V[] toArray()
	{
		V[] re=createVArray(_size);
		
		if(_size==0)
			return re;
		
		System.arraycopy(_values,0,re,0,_size);
		
		return re;
	}
	
	/** 填充到数组 */
	public void fillArray(V[] arr)
	{
		if(_size==0)
			return;
		
		System.arraycopy(_values,0,arr,0,_size);
	}
	
	/** 添加一组 */
	public void addAll(SList<? extends V> list)
	{
		if(list.isEmpty())
			return;
		
		int d=_size + list._size;
		
		ensureCapacity(d);
		
		System.arraycopy(list._values,0,this._values,this._size,list._size);
		
		_size=d;
	}
	
	/** 克隆 */
	public SList<V> clone()
	{
		if(_size==0)
			return new SList<>(_createVArrFunc);
		
		SList<V> re=new SList<>(_createVArrFunc,_size);
		System.arraycopy(_values,0,re._values,0,_size);
		re._size=_size;
		return re;
	}
	
	@Override
	public void forEach(Consumer<? super V> consumer)
	{
		if(_size==0)
		{
			return;
		}
		
		V[] values=_values;
		
		for(int i=0, len=_size;i<len;++i)
		{
			consumer.accept(values[i]);
		}
	}
	
	public void sort()
	{
		if(_size==0)
			return;
		
		Arrays.sort((V[])_values,0,_size);
	}
	
	@SuppressWarnings("unchecked")
	public void sort(Comparator<? super V> comparator)
	{
		if(_size==0)
			return;
		
		Arrays.sort((V[])_values,0,_size,comparator);
	}
	
	/** 截断到某长度 */
	public void cutToLength(int length)
	{
		if(_size<=length)
			return;
		
		V[] values=_values;
		
		for(int i=length;i<_size;++i)
		{
			values[i]=null;
		}
		
		_size=length;
	}
	
	/** 转化为原生集合 */
	public ArrayList<V> toNatureList()
	{
		ArrayList<V> re=new ArrayList<>(size());
		
		V[] values=_values;
		
		for(int i=0, len=_size;i<len;++i)
		{
			re.add(values[i]);
		}
		
		return re;
	}
	
	public void addAll(Collection<V> collection)
	{
		ensureCapacity(collection.size());
		collection.forEach(this::add);
	}
	
	@Override
	public Iterator<V> iterator()
	{
		return new ForEachIterator();
	}
	
	private class ForEachIterator implements Iterator<V>
	{
		private int _index;
		private int _tSize;
		private V[] _tValues;
		
		private V _v;
		
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
			
			_v=null;
			return false;
		}
		
		@Override
		public V next()
		{
			return _v;
		}
	}
}
