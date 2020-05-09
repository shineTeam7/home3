using System;

namespace ShineEngine
{
	/// <summary>
	///
	/// </summary>
	public class LongQueue:BaseQueue
	{
		private long[] _values;

		private long _defaultValue=-1L;

		public LongQueue()
		{
			_values=ObjectUtils.EmptyLongArr;
		}

		public LongQueue(int capacity)
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

		public long getDefaultValue()
		{
			return _defaultValue;
		}

		public void setDefaultValue(long v)
		{
			_defaultValue=v;
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
			_values=new long[capacity];
			_mark=capacity - 1;
			_size=0;
		}

		private void remake(int capacity)
		{
			long[] n=new long[capacity];
			long[] values=_values;

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

		/** 放入 */
		public void offer(long v)
		{
			if(_values.Length==0)
				init(_minSize);
			else if(_size==_values.Length)
				remake(_values.Length<<1);

			_values[_end]=v;

			if(++_end==_values.Length)
				_end=0;

			++_size;
		}

		/** 取出 */
		public long poll()
		{
			if(_size==0)
				return _defaultValue;

			long v=_values[_start];

			if(++_start==_values.Length)
				_start=0;

			--_size;

			return v;
		}

		public long get(int index)
		{
			if(index>=_size)
				return 0;

			return _values[(_start + index) & _mark];
		}

		/** 清空 */
		public void clear()
		{
			if(_size==0)
				return;

			_size=0;
			_start=0;
			_end=0;
		}

		/** 遍历 */
		public void forEach(Action<long> consumer)
		{
			if(_size==0)
				return;

			long[] values=_values;

			for(int i=_start,end=_end,mask=values.Length - 1;i<end;i=(i + 1) & mask)
			{
				consumer(values[i]);
			}
		}
	}
}