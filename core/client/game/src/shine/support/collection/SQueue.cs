using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

namespace ShineEngine
{
	/// <summary>
	/// 队列
	/// </summary>
	public class SQueue<V>:BaseQueue,IEnumerable<V>
	{
		private V[] _values;

		public SQueue()
		{

		}

		public SQueue(int capacity)
		{
			init(countCapacity(capacity));
		}

		private void checkInit()
		{
			if(_values!=null)
				return;

			init(_minSize);
		}

		public int capacity()
		{
			checkInit();
			return _values.Length;
		}

		public V[] getValues()
		{
			checkInit();
			return _values;
		}

		/** 扩容 */
		public void ensureCapacity(int capacity)
		{
			if(_values==null)
			{
				init(countCapacity(capacity));
			}
			else if(capacity>_values.Length)
			{
				remake(countCapacity(capacity));
			}
		}

		private void init(int capacity)
		{
			_values=new V[capacity];
			_mark=capacity - 1;
			_size=0;
		}

		private void remake(int capacity)
		{
			V[] n=new V[capacity];
			V[] values=_values;

			if(_size!=0)
			{
				if(_start<_end)
				{
					Array.Copy(values,_start,n,0,_end - _start);
				}
				else
				{
					int d=values.Length - _start;
					Array.Copy(values,_start,n,0,d);
					Array.Copy(values,0,n,d,_end);
				}
			}

			_start=0;
			_end=_size;

			_values=n;
			_mark=capacity - 1;
		}

		public void add(V v)
		{
			this.offer(v);
		}

		/** 放入 */
		public bool offer(V v)
		{
			checkInit();

			if(_size==_values.Length)
				remake(_values.Length<<1);


			int end=_end;

			_values[end++]=v;

			if(end==_values.Length)
			{
				end=0;
			}

			_end=end;

			++_size;

			return true;
		}

		/** 从头放入 */
		public bool unshift(V v)
		{
			checkInit();

			if(_size==_values.Length)
				remake(_values.Length<<1);

			int start=_start;

			if((--start)<0)
			{
				start=_values.Length - 1;
			}

			_values[start]=v;

			_start=start;

			++_size;

			return true;
		}

		/** 查看顶元素 */
		public V peek()
		{
			if(_size==0)
				return default(V);

			return _values[_start];
		}

		/** 取出 */
		public V poll()
		{
			if(_size==0)
				return default(V);

			int start=_start;

			V v=_values[start];
			_values[start]=default(V);

			if(++start==_values.Length)
			{
				start=0;
			}

			_start=start;

			--_size;

			return v;
		}

		/** 查看底元素 */
		public V tail()
		{
			if(_size==0)
				return default(V);

			int index=_end==0 ? _values.Length-1 : _end-1;
			return _values[index];
		}

		/** 取出末尾 */
		public V pop()
		{
			if(_size==0)
				return default(V);

			int index=_end==0 ? _values.Length-1 : _end-1;
			V v=_values[index];
			_values[index]=default(V);

			_end=index;

			--_size;

			return v;
		}

		public V get(int index)
		{
			if(index>=_size)
				return default(V);

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

			for(int i=values.Length - 1;i >= 0;--i)
			{
				values[i]=default(V);
			}

			_size=0;
			_start=0;
			_end=0;
		}

		public SList<V> toList()
		{
			SList<V> re=new SList<V>(_size);

			if(_size==0)
				return re;

			V[] rValues=re.getValues();

			V[] values=_values;

			//正常的
			if(_end>_start)
			{
				Array.Copy(values,_start,rValues,0,_end-_start);
			}
			else
			{
				int d=values.Length - _start;

				Array.Copy(values,_start,rValues,0,d);
				Array.Copy(values,0,rValues,d,_end);
			}

			re.justSetSize(_size);

			return re;
		}

		public void forEach(Action<V> consumer)
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
					consumer(values[i]);
				}
			}
			else
			{
				for(int i=_start,end=values.Length;i<end;++i)
				{
					consumer(values[i]);
				}

				for(int i=0,end=_end;i<end;++i)
				{
					consumer(values[i]);
				}
			}
		}

		public ForEachIterator GetEnumerator()
		{
			return new ForEachIterator(this);
		}

		IEnumerator<V> IEnumerable<V>.GetEnumerator()
		{
			return GetEnumerator();
		}

		IEnumerator IEnumerable.GetEnumerator()
		{
			return GetEnumerator();
		}

		public struct ForEachIterator:IEnumerator<V>
		{
			private int _index;
			private int _tMark;
			private int _tEnd;
			private int _tSize;
			private V[] _tValues;

			private V _v;

			public ForEachIterator(SQueue<V> queue)
			{
				_index=queue._start;
				_tMark=queue._mark;
				_tEnd=queue._end;
				_tSize=queue._size;
				_tValues=queue._values;
				_v=default(V);
			}

			public void Reset()
			{

			}

			public void Dispose()
			{

			}

			public bool MoveNext()
			{
				if( _tSize>0 && _index!=_tEnd)
				{
					_v=_tValues[_index];
					_index=(_index+1) & _tMark;
					return true;
				}

				_v=default(V);
				return false;
			}

			public V Current
			{
				get {return _v;}
			}

			object IEnumerator.Current
			{
				get {return _v;}
			}
		}
	}
}