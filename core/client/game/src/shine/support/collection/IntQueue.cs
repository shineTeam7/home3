using System;

namespace ShineEngine
{
	/// <summary>
	///
	/// </summary>
	public class IntQueue:BaseQueue
	{
		private int[] _values;

		private int _defaultValue=0;

		public IntQueue()
		{
			init(0);
		}

		public IntQueue(int capacity)
		{
			init(countCapacity(capacity));
		}

		public int[] getValues()
		{
			return _values;
		}

		public int getDefaultValue()
		{
			return _defaultValue;
		}

		public void setDefaultValue(int v)
		{
			_defaultValue=v;
		}

		protected override void init(int capacity)
		{
			_capacity=capacity;

			if(capacity==0)
				_values=ObjectUtils.EmptyIntArr;
			else
				_values=new int[capacity];

			_mark=capacity - 1;
		}

		protected override void remake(int capacity)
		{
			int[] oldArr=_values;
			init(capacity);

			if(_size!=0)
			{
				int[] values=_values;

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
		public void offer(int v)
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
		public int poll()
		{
			if(_size==0)
				return _defaultValue;

			int v=_values[_start];

			if(++_start==_values.Length)
				_start=0;

			--_size;

			return v;
		}

		public int get(int index)
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
	}
}