package com.home.shine.support.collection;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.inter.ICreateArray;
import com.home.shine.support.collection.inter.IQueue;

import java.util.Iterator;
import java.util.function.Consumer;

public class SQueue<V> extends BaseQueue implements Iterable<V>,IQueue<V>
{
	private V[] _values;
	
	private ICreateArray<V> _createVArrFunc;
	
	public SQueue()
	{
		init(_minSize);
	}
	
	public SQueue(int capacity)
	{
		init(countCapacity(capacity));
	}
	
	public SQueue(ICreateArray<V> createVArrFunc)
	{
		_createVArrFunc=createVArrFunc;
		init(_minSize);
	}
	
	public SQueue(ICreateArray<V> createVArrFunc,int capacity)
	{
		_createVArrFunc=createVArrFunc;
		init(countCapacity(capacity));
	}
	
	public final V[] getValues()
	{
		return _values;
	}
	
	@SuppressWarnings("unchecked")
	private V[] createVArray(int length)
	{
		if(_createVArrFunc!=null)
			return _createVArrFunc.create(length);
		
		return ((V[])(new Object[length]));
	}
	
	@Override
	protected void init(int capacity)
	{
		if(capacity<_minSize)
			capacity=_minSize;
		
		_capacity=capacity;
		
		_values=createVArray(capacity);
		_mark=capacity-1;
	}
	
	@Override
	protected void remake(int capacity)
	{
		V[] oldArr=_values;
		init(capacity);
		
		if(_size!=0)
		{
			V[] values=_values;
			
			if(_start<_end)
			{
				System.arraycopy(oldArr,_start,values,0,_end - _start);
			}
			else
			{
				int d=oldArr.length - _start;
				System.arraycopy(oldArr,_start,values,0,d);
				System.arraycopy(oldArr,0,values,d,_end);
			}
		}
		
		_start=0;
		_end=_size;
	}
	
	/** 放入 */
	public boolean offer(V v)
	{
		if(_size==_capacity)
			remake(_capacity << 1);
		
		int end=_end;
		
		_values[end++]=v;
		
		if(end==_values.length)
		{
			end=0;
		}
		
		_end=end;
		
		++_size;
		
		return true;
	}
	
	public void add(V v)
	{
		this.offer(v);
	}
	
	/** 查看顶元素 */
	public V peek()
	{
		if(_size==0)
			return null;
		
		return _values[_start];
	}
	
	/** 查看顶元素 */
	public V head()
	{
		if(_size==0)
			return null;
		
		return _values[_start];
	}
	
	/** 查看底元素 */
	public V tail()
	{
		if(_size==0)
			return null;
		
		int index=_end==0 ? _values.length-1 : _end-1;
		return _values[index];
	}
	
	/** 取出头部 */
	public V poll()
	{
		if(_size==0)
		{
			return null;
		}
		
		int start=_start;
		
		V v=_values[start];
		_values[start]=null;

		if(++start==_values.length)
		{
			start=0;
		}
		
		_start=start;
		
		--_size;
		
		return v;
	}
	
	/** 取出末尾 */
	public V pop()
	{
		if(_size==0)
		{
			return null;
		}
		
		int index=_end==0 ? _values.length-1 : _end-1;
		V v=_values[index];
		_values[index]=null;
		
		_end=index;
		
		--_size;
		
		return v;
	}
	
	public V get(int index)
	{
		if(index>=_size)
			return null;
		
		return _values[(_start + index) & _mark];
	}
	
	/** 移除前部 len */
	public void removeFront(int len)
	{
		if(isEmpty())
			return;
		
		if(len>_size)
		{
			Ctrl.throwError("indexOutOfBound");
			return;
		}
		
		int last=_size-len;
		
		while(_size>last)
		{
			poll();
		}
	}
	
	/** 移除后部 index到末尾 */
	public void removeBack(int len)
	{
		if(isEmpty())
			return;
		
		if(len>_size)
		{
			Ctrl.throwError("indexOutOfBound");
			return;
		}
		
		int last=_size-len;
		
		while(_size>last)
		{
			pop();
		}
	}
	
	/** 清空 */
	public void clear()
	{
		if(_size==0)
		{
			return;
		}
		
		V[] values=_values;
		
		for(int i=values.length - 1;i >= 0;--i)
		{
			values[i]=null;
		}
		
		_size=0;
		_start=0;
		_end=0;
	}
	
	public SList<V> toList()
	{
		SList<V> re=new SList<V>(_createVArrFunc,_size);
		
		if(_size==0)
			return re;
		
		V[] rValues=re.getValues();
		
		V[] values=_values;
		
		//正常的
		if(_end>_start)
		{
			System.arraycopy(values,_start,rValues,0,_end-_start);
		}
		else
		{
			int d=values.length - _start;
			
			System.arraycopy(values,_start,rValues,0,d);
			System.arraycopy(values,0,rValues,d,_end);
		}
		
		re.justSetSize(_size);
		
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
		
		//正常的
		if(_end>_start)
		{
			for(int i=_start,end=_end;i<end;++i)
			{
				consumer.accept(values[i]);
			}
		}
		else
		{
			for(int i=_start,end=values.length;i<end;++i)
			{
				consumer.accept(values[i]);
			}
			
			for(int i=0,end=_end;i<end;++i)
			{
				consumer.accept(values[i]);
			}
		}
	}
	
	/** 遍历并清空 */
	public void forEachAndClear(Consumer<? super V> consumer)
	{
		if(_size==0)
		{
			return;
		}
		
		V[] values=_values;
		
		//正常的
		if(_end>_start)
		{
			for(int i=_start,end=_end;i<end;++i)
			{
				consumer.accept(values[i]);
				values[i]=null;
			}
		}
		else
		{
			for(int i=_start,end=values.length;i<end;++i)
			{
				consumer.accept(values[i]);
				values[i]=null;
			}
			
			for(int i=0,end=_end;i<end;++i)
			{
				consumer.accept(values[i]);
				values[i]=null;
			}
		}
		
		_size=0;
	}
	
	@Override
	public Iterator<V> iterator()
	{
		return new ForEachIterator();
	}
	
	private class ForEachIterator implements Iterator<V>
	{
		private int _index;
		private int _tEnd;
		private int _tSize;
		private V[] _tValues;
		
		private V _v;
		
		public ForEachIterator()
		{
			_index=_start;
			_tEnd=_end;
			_tSize=_size;
			_tValues=_values;
		}
		
		@Override
		public boolean hasNext()
		{
			if(_tSize>0 && _index!=_tEnd)
			{
				_v=_tValues[_index];
				_index=(_index+1) & _mark;
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
