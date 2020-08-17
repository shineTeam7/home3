using System;
using System.Collections;
using System.Collections.Generic;

namespace ShineEngine
{
	/// <summary>
	/// 列表
	/// </summary>
	public class SList<V>:BaseList,IEnumerable<V>
	{
		private V[] _values;

		private CustomComparer<V> _comparer;

		public SList()
		{
			init(_minSize);
		}

		public SList(int capacity)
		{
			init(countCapacity(capacity));
		}

		public V[] getValues()
		{
			return _values;
		}

		public V[] getArr()
		{
			V[] re=new V[_size];
			if(_size>0)
				Array.Copy(_values,0,re,0,_size);
			return re;
		}

		protected override void init(int capacity)
		{
			if(capacity<_minSize)
				capacity=_minSize;

			_capacity=capacity;

			_values=new V[capacity];
		}

		protected override void remake(int capacity)
		{
			V[] oldArr=_values;
			init(capacity);

			if(_size>0)
				Array.Copy(oldArr,0,_values,0,_size);
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
			int d=_size + arr.Length;

			if(d>_values.Length)
			{
				remake(countCapacity(d));
			}

			Array.Copy(arr,0,_values,_size,arr.Length);
			_size=d;
		}

		/** 添加元素到头 */
		public void unshift(V value)
		{
			if(_size==_capacity)
				remake(_capacity << 1);
		
			Array.Copy(_values,0,_values,1,_size);
		
			_values[0]=value;
			_size++;
		}
		
		/** 获取对应元素 */
		public V get(int index)
		{
			if(index>=_size)
			{
				Ctrl.throwError("indexOutOfBound");
			}

			return _values[index];
		}

		/** 设置对应元素 */
		public void set(int index,V obj)
		{
			if(index>=_size)
			{
				Ctrl.throwError("indexOutOfBound");
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
			{
				return default(V);
			}

			V v=_values[index];

			int numMoved=_size - index - 1;

			if(numMoved>0)
			{
				Array.Copy(_values,index + 1,_values,index,numMoved);
			}

			_values[--_size]=default(V);

			return v;
		}

		/** 移除对象 */
		public bool removeObj(V obj)
		{
			int index=indexOf(obj);

			if(index!=-1)
			{
				remove(index);
				return true;
			}

			return false;
		}

		/** 移除一定范围(from<=index<end) */
		public bool removeRange(int from,int end)
		{
			if(isEmpty())
				return false;

			int size=_size;

			if(size<end)
				return false;

			if(end<=from)
				return false;

			int len=end - from;

			V[] values=_values;

			int copyLen=size - end;
			if(copyLen>0)
			{
				Array.Copy(values,end,values,from,copyLen);
			}

			int f=from + copyLen;

			for(int i=len;i>0;--i)
			{
				values[size - i]=default(V);
			}

			_size-=len;

			return true;
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
				return default(V);

			V v=_values[--_size];
			_values[_size]=default(V);
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

			if(len==0)
				return new SList<V>();

			if(len<0)
			{
				Ctrl.throwError("subList,数据非法",from,end);
				return new SList<V>();
			}

			SList<V> re=new SList<V>(len);
			Array.Copy(_values,from,re._values,0,len);
			re.justSetSize(len);
			return re;
		}

		/** 获取末尾 */
		public V getLast()
		{
			if(_size==0)
				return default(V);

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

			for(int i=offset, len=_size;i<len;++i)
			{
				if(values[i].Equals(value))
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

			for(int i=offset;i >= 0;--i)
			{
				if(values[i].Equals(value))
				{
					return i;
				}
			}

			return -1;
		}

		public bool contains(V value)
		{
			return indexOf(value)!=-1;
		}

		public void insert(int offset,V value)
		{
			if(offset>=_size)
			{
				add(value);
				return;
			}

			if(_size + 1>_values.Length)
			{
				V[] n=new V[_values.Length << 1];
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

		public V this[int index]
		{
			get {return this.get(index);}
			set {this.set(index,value);}
		}

		public override void clear()
		{
			if(_size==0)
			{
				return;
			}

			V[] values=_values;

			for(int i=_size - 1;i >= 0;--i)
			{
				values[i]=default(V);
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

		/** 转换数组 */
		public V[] toArray()
		{
			if(isEmpty())
			{
				return new V[0];
			}

			V[] re=new V[_size];

			Array.Copy(_values,0,re,0,_size);

			return re;
		}

		/** 填充到数组 */
		public void fillArray(V[] arr)
		{
			if(_size>0)
				Array.Copy(_values,0,arr,0,_size);
		}

		/** 添加一组 */
		public void addAll(SList<V> list)
		{
			if(list.isEmpty())
			{
				return;
			}

			int d=_size + list._size;

			ensureCapacity(d);

			Array.Copy(list._values,0,this._values,this._size,list._size);

			_size=d;
		}

		public SList<V> clone()
		{
			if(_size==0)
				return new SList<V>();

			SList<V> re=new SList<V>(_size);
			Array.Copy(_values,0,re._values,0,_size);
			re._size=_size;
			return re;
		}

		public void forEach(Action<V> consumer)
		{
			if(_size==0)
			{
				return;
			}

			V[] values=_values;

			for(int i=0, len=_size;i<len;++i)
			{
				consumer(values[i]);
			}
		}

		public void forEachAndClear(Action<V> consumer)
		{
			if(_size==0)
			{
				return;
			}

			V[] values=_values;

			for(int i=0, len=_size;i<len;++i)
			{
				consumer(values[i]);
				values[i]=default(V);
			}

			_size=0;
		}

		public void sort()
		{
			if(_size==0)
				return;

			Array.Sort(_values,0,_size);
		}

		public void sort(IComparer<V> comparator)
		{
			if(_size==0)
				return;

			Array.Sort(_values,0,_size,comparator);
		}

		public void sort(Comparison<V> comparator)
		{
			if(_size==0)
				return;

			if(_comparer==null)
				_comparer=new CustomComparer<V>();

			_comparer.setCompare(comparator);
			sort(_comparer);
			_comparer.setCompare(null);
		}

		/** 截断到某长度 */
		public void cutToLength(int length)
		{
			if(_size<=length)
				return;

			V[] values=_values;

			for(int i=length;i<_size;++i)
			{
				values[i]=default(V);
			}

			_size=length;
		}

		/** 转化为原生集合 */
		public List<V> toNatureList()
		{
			List<V> re=new List<V>(size());

			V[] values=_values;

			for(int i=0,len=_size;i<len;++i)
			{
				re.Add(values[i]);
			}

			return re;
		}

		public void addAll(List<V> map)
		{
			ensureCapacity(map.Count);

			foreach(V v in map)
			{
				this.add(v);
			}
		}

		public ForEachIterator GetEnumerator()
		{
			return new ForEachIterator(this);
		}

		IEnumerator<V> IEnumerable<V>.GetEnumerator()
		{
			return new ForEachIterator(this);
		}

		IEnumerator IEnumerable.GetEnumerator()
		{
			return new ForEachIterator(this);
		}

		public struct ForEachIterator:IEnumerator<V>
		{
			private V[] _tValues;
			private int _tSize;
			private int _index;
			private V _v;

			public ForEachIterator(SList<V> list)
			{
				_tValues=list._values;
				_tSize=list._size;
				_index=0;
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
				if(_index<_tSize)
				{
					_v=_tValues[_index++];
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