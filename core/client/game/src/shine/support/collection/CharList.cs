using System;
using System.Collections;
using System.Collections.Generic;

namespace ShineEngine
{
	public class CharList:BaseList,IEnumerable<char>
	{
		private char[] _values;

		public CharList()
		{
			init(0);
		}

		public CharList(int capacity)
		{
			init(countCapacity(capacity));
		}

		public char[] getValues()
		{
			return _values;
		}

		protected override void init(int capacity)
		{
			_capacity=capacity;

			if(capacity==0)
				_values=ObjectUtils.EmptyCharArr;
			else
				_values=new char[capacity];
		}

		protected override void remake(int capacity)
		{
			char[] oldValues=_values;
			init(capacity);

			if(oldValues.Length>0 && _size>0)
				Array.Copy(oldValues,0,_values,0,_size);
		}

		public void set(int index,char value)
		{
			if(index>=_size)
			{
				Ctrl.throwError("indexOutOfBound");
			}

			_values[index]=value;
		}

		/** 添加 */
		public void add(char value)
		{
			addCapacity();

			_values[_size++]=value;
		}

		/** 添加2个 */
		public void add2(char v1,char v2)
		{
			addCapacity(2);

			_values[_size++]=v1;
			_values[_size++]=v2;
		}

		/** 添加3个 */
		public void add3(char v1,char v2,char v3)
		{
			addCapacity(3);

			_values[_size++]=v1;
			_values[_size++]=v2;
			_values[_size++]=v3;
		}

		/** 添加一组 */
		public void addArr(char[] arr)
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
		public void unshift(char value)
		{
			addCapacity();

			if(_size>0)
				Array.Copy(_values,0,_values,1,_size);

			_values[0]=value;
			_size++;
		}

		/** 获取对应元素 */
		public char get(int index)
		{
			if(index>=_size)
			{
				Ctrl.throwError("indexOutOfBound");
			}

			return _values[index];
		}

		public char this[int key]
		{
			get {return this.get(key);}
		}

		public char justGet(int index)
		{
			return _values[index];
		}

		/** 删除某序号的元素 */
		public char remove(int index)
		{
			if(_size==0)
				return default(char);

			char v=_values[index];

			int numMoved=_size - index - 1;

			if(numMoved>0)
			{
				Array.Copy(_values,index + 1,_values,index,numMoved);
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

			for(int i=offset,len=_size;i<len;++i)
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

			for(int i=offset;i>=0;--i)
			{
				if(values[i]==value)
				{
					return i;
				}
			}

			return -1;
		}

		public bool contains(char value)
		{
			return indexOf(value)!=-1;
		}

		public void insert(int offset,char value)
		{
			if(offset>=_size)
			{
				add(value);
				return;
			}

			if(_size + 1>_values.Length)
			{
				char[] n=new char[_values.Length << 1];
				Array.Copy(_values,0,n,0,offset);
				Array.Copy(_values,offset,n,offset + 1,_size - offset);

				n[offset]=value;
				_values=n;
				_capacity=n.Length;
			}
			else
			{
				Array.Copy(_values,offset,_values,offset + 1,_size - offset);

				_values[offset]=value;
			}

			++_size;
		}

		/** 转换数组 */
		public char[] toArray()
		{
			if(_size==0)
			{
				return ObjectUtils.EmptyCharArr;
			}

			char[] re=new char[_size];

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

		public void forEach(Action<char> consumer)
		{
			if(_size==0)
				return;

			char[] values=_values;

			for(int i=0,len=_size;i<len;++i)
			{
				consumer(values[i]);
			}
		}

		public ForEachIterator GetEnumerator()
		{
			return new ForEachIterator(this);
		}

		IEnumerator<char> IEnumerable<char>.GetEnumerator()
		{
			return new ForEachIterator(this);
		}

		IEnumerator IEnumerable.GetEnumerator()
		{
			return new ForEachIterator(this);
		}

		public struct ForEachIterator:IEnumerator<char>
		{
			private int _tSize;

			private char[] _tValues;

			private int _index;

			private char _v;

			public ForEachIterator(CharList list)
			{
				_tValues=list._values;
				_tSize=list._size;
				_index=0;
				_v=default(char);
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

				_v=default(char);
				return false;
			}

			public char Current
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