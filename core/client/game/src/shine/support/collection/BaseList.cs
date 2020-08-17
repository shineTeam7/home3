using System;

namespace ShineEngine
{
	/// <summary>
	/// 
	/// </summary>
	public class BaseList
	{
		protected static int _minSize=4;

		/** 容量 */
		protected int _capacity=0;

		protected int _size=0;

		protected int countCapacity(int capacity)
		{
			capacity=MathUtils.getPowerOf2(capacity);

			if(capacity<_minSize)
				capacity=_minSize;

			return capacity;
		}

		protected virtual void init(int capacity)
		{
			_capacity=capacity;
		}

		public virtual void clear()
		{
			_size=0;
		}

		/** 清空+缩容 */
		public virtual void reset()
		{
			if(_size==0)
			{
				if(_capacity<=_minSize)
					return;
			}
			else
			{
				_size=0;
			}

			init(0);
		}

		protected virtual void remake(int size)
		{

		}

		/** 扩容 */
		public void ensureCapacity(int capacity)
		{
			if(capacity>_capacity)
			{
				remake(countCapacity(capacity));
			}
		}

		/** 缩容 */
		public void shrink()
		{
			if(_size==0)
			{
				if(_capacity<=_minSize)
					return;

				init(0);
			}
			else
			{
				int capacity=countCapacity(_size);

				if(capacity<_capacity)
				{
					remake(capacity);
				}
			}
		}

		protected void addCapacity()
		{
			if(_capacity==0)
				init(_minSize);
			else if(_size==_capacity)
				remake(_capacity<<1);
		}

		protected void addCapacity(int n)
		{
			if(_capacity==0)
				init(_minSize);
			else if(_size+n>_capacity)
				remake(_capacity<<1);
		}

		public int capacity()
		{
			return _capacity;
		}

		/** 尺寸 */
		public int size()
		{
			return _size;
		}

		/** 尺寸 */
		public int length()
		{
			return _size;
		}

		/** 尺寸 */
		public int Count
		{
			get {return _size;}
		}

		/** 是否空 */
		public bool isEmpty()
		{
			return _size==0;
		}

		/** 只清空size(前提是其他数据自行清理了,只看懂底层的用) */
		public void justClearSize()
		{
			_size=0;
		}

		/** 只设置尺寸 */
		public void justSetSize(int value)
		{
			_size=value;
		}
	}
}