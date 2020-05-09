using System;

namespace ShineEngine
{
	/// <summary>
	/// 
	/// </summary>
	public class BaseList
	{
		protected static int _minSize=4;

		protected int _size;

		protected int countCapacity(int capacity)
		{
			capacity=MathUtils.getPowerOf2(capacity);

			if(capacity<_minSize)
				capacity=_minSize;

			return capacity;
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