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
			init(0);
		}

		public LongQueue(int capacity)
		{
			init(countCapacity(capacity));
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

		protected override void init(int capacity)
		{
			_capacity=capacity;

			_values=new long[capacity];
			_mark=capacity - 1;
			_size=0;
		}

		protected override void remake(int capacity)
		{
			long[] oldArr=_values;
			init(capacity);

			if(_size!=0)
			{
				long[] values=_values;

				if(_start<_end)
				{
					Array.Copy(oldArr,_start,values,0,_end - _start);
				}
				else
				{
					int d=oldArr.Length - _start;
					Array.Copy(oldArr,_start,values,0,d);
					Array.Copy(oldArr,0,values,d,_end);
				}
			}

			_start=0;
			_end=_size;
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
		public override void clear()
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