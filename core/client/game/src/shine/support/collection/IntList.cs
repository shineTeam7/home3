using System;
using System.Collections;
using System.Collections.Generic;

namespace ShineEngine
{
	/// <summary>
	/// 
	/// </summary>
	public class IntList:BaseList,IEnumerable<int>
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
			return _values.Length;
		}

		public int[] getValues()
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
			Array.Copy(_values,0,n,0,_size);
			_values=n;
		}

		public void set(int index,int value)
		{
			if(index>=_size)
			{
				Ctrl.throwError("indexOutOfBound");
			}

			_values[index]=value;
		}

		/** 添加 */
		public void add(int value)
		{
			if(_values.Length==0)
				init(_minSize);
			else if(_size==_values.Length)
				remake(_values.Length<<1);

			_values[_size++]=value;
		}

		/** 添加2个 */
		public void add2(int v1,int v2)
		{
			if(_values.Length==0)
				init(_minSize);
			else if(_size + 2>_values.Length)
				remake(_values.Length<<1);

			_values[_size++]=v1;
			_values[_size++]=v2;
		}

		/** 添加3个 */
		public void add3(int v1,int v2,int v3)
		{
			if(_values.Length==0)
				init(_minSize);
			else if(_size + 3>_values.Length)
				remake(_values.Length<<1);

			_values[_size++]=v1;
			_values[_size++]=v2;
			_values[_size++]=v3;
		}

		/** 添加一组 */
		public void addArr(int[] arr)
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
		public void unshift(int value)
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

		/** 获取对应元素 */
		public int get(int index)
		{
			if(index>=_size)
			{
				Ctrl.throwError("indexOutOfBound");
			}

			return _values[index];
		}

		public int this[int key]
		{
			get {return this.get(key);}
		}

		public int justGet(int index)
		{
			return _values[index];
		}

		/** 删除某序号的元素 */
		public int remove(int index)
		{
			if(_size==0)
				return 0;

			int v=_values[index];

			int numMoved=_size - index - 1;

			if(numMoved>0)
			{
				Array.Copy(_values,index + 1,_values,index,numMoved);
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

			for(int i=offset,len=_size;i<len;++i)
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

			for(int i=offset;i>=0;--i)
			{
				if(values[i]==value)
				{
					return i;
				}
			}

			return -1;
		}

		public bool contains(int value)
		{
			return indexOf(value)!=-1;
		}

		public void insert(int offset,int value)
		{
			if(offset>=_size)
			{
				add(value);
				return;
			}

			if(_size + 1>_values.Length)
			{
				int[] n=new int[_values.Length << 1];
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

		public void forEach(Action<int> consumer)
		{
			if(_size==0)
				return;

			int[] values=_values;

			for(int i=0,len=_size;i<len;++i)
			{
				consumer(values[i]);
			}
		}

		public ForEachIterator GetEnumerator()
		{
			return new ForEachIterator(this);
		}

		IEnumerator<int> IEnumerable<int>.GetEnumerator()
		{
			return new ForEachIterator(this);
		}

		IEnumerator IEnumerable.GetEnumerator()
		{
			return new ForEachIterator(this);
		}

		public struct ForEachIterator:IEnumerator<int>
		{
			private int _tSize;

			private int[] _tValues;

			private int _index;

			private int _v;

			public ForEachIterator(IntList list)
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

			public int Current
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