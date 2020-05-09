using System;
using System.Collections;
using System.Collections.Generic;

namespace ShineEngine
{
	/// <summary>
	///
	/// </summary>
	public class LongList:BaseList,IEnumerable<long>
	{
		private long[] _values;

		public LongList()
		{
			_values=ObjectUtils.EmptyLongArr;
		}

		public LongList(int capacity)
		{
			init(countCapacity(capacity));
		}

		public int capacity()
		{
			return _values.Length;
		}

		public long[] getValues()
		{
			return _values;
		}

		private void init(int capacity)
		{
			_values=new long[capacity];

			_size=0;
		}

		private void remake(int capacity)
		{
			long[] n=new long[capacity];
			Array.Copy(_values,0,n,0,_size);
			_values=n;
		}

		/** 添加 */
		public void add(long value)
		{
			if(_values.Length==0)
				init(_minSize);
			else if(_size==_values.Length)
				remake(_values.Length<<1);

			_values[_size++]=value;
		}

		/** 添加一组 */
		public void addArr(long[] arr)
		{
			int d=_size + arr.Length;

			if(d>_values.Length)
			{
				remake(countCapacity(d));
			}

			Array.Copy(arr,0,_values,_size,arr.Length);
			_size=d;
		}

		/** 添加元素到头 */
		public void unshift(long value)
		{
			if(_values.Length==0)
				init(_minSize);
			else if(_size==_values.Length)
				remake(_values.Length<<1);

			if(_size>0)
				Array.Copy(_values,0,_values,1,_size);
		
			_values[0]=value;
			_size++;
		}

		public void set(int index,long value)
		{
			if(index>=_size)
			{
				Ctrl.throwError("indexOutOfBound");
			}

			_values[index]=value;
		}
		
		public long get(int index)
		{
			if(index>=_size)
			{
				Ctrl.throwError("indexOutOfBound");
			}

			return _values[index];
		}

		public long this[int key]
		{
			get {return this.get(key);}
		}

		public long justGet(int index)
		{
			return _values[index];
		}

		/** 删除某序号的元素 */
		public long remove(int index)
		{
			if(_size==0)
				return 0;

			long v=_values[index];

			int numMoved=_size - index - 1;

			if(numMoved>0)
			{
				Array.Copy(_values,index + 1,_values,index,numMoved);
			}

			--_size;

			return v;
		}

		public int indexOf(long value)
		{
			return indexOf(0,value);
		}

		public int indexOf(int offset,long value)
		{
			if(_size==0)
				return -1;

			long[] values=_values;

			for(int i=offset,len=_size;i<len;++i)
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
			if(_size==0)
				return -1;

			long[] values=_values;

			for(int i=offset;i>=0;--i)
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

			if(_size + 1>_values.Length)
			{
				long[] n=new long[_values.Length << 1];
				Array.Copy(_values,0,n,0,offset);
				Array.Copy(_values,offset,n,offset + 1,_size - offset);

				n[offset]=value;
				_values=n;
			}
			else
			{
				Array.Copy(_values,offset,_values,offset + 1,_size - offset);

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
			if(capacity>_values.Length)
			{
				remake(countCapacity(capacity));
			}
		}

		/** 转换数组 */
		public long[] toArray()
		{
			if(_size==0)
			{
				return ObjectUtils.EmptyLongArr;
			}

			long[] re=new long[_size];

			Array.Copy(_values,0,re,0,_size);

			return re;
		}

		/** 排序 */
		public void sort()
		{
			if(_size==0)
				return;

			Array.Sort(_values,0,_size);
		}

		public void forEach(Action<long> consumer)
		{
			if(_size==0)
				return;

			long[] values=_values;

			for(int i=0,len=_size;i<len;++i)
			{
				consumer(values[i]);
			}
		}

		public ForEachIterator GetEnumerator()
		{
			return new ForEachIterator(this);
		}

		IEnumerator<long> IEnumerable<long>.GetEnumerator()
		{
			return new ForEachIterator(this);
		}

		IEnumerator IEnumerable.GetEnumerator()
		{
			return new ForEachIterator(this);
		}

		public struct ForEachIterator:IEnumerator<long>
		{
			private int _tSize;

			private long[] _tValues;

			private int _index;

			private long _v;

			public ForEachIterator(LongList list)
			{
				_tValues=list._values;
				_tSize=list._size;
				_index=0;
				_v=0;
			}

			public void Reset()
			{
			}

			public void Dispose()
			{
			}

			public bool MoveNext()
			{
				if(_index<_tSize)
				{
					_v=_tValues[_index++];
					return true;
				}

				_v=0;
				return false;
			}

			public long Current
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