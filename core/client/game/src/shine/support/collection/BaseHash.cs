namespace ShineEngine
{
	/// <summary>
	/// 基于koloboke的hash集合组基类
	/// </summary>
	public class BaseHash
	{
		protected const int _minSize=2;

		protected int _size;

		protected int _maxSize;

		protected int _version=0;

		/** 上个free序号 */
		protected int _lastFreeIndex=-1;

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

		public int capacity()
		{
			return _maxSize;
		}

		/** 获取数组长度 */
		protected int getArrCapacity()
		{
			return _maxSize<<1;
		}

		/** 是否空 */
		public bool isEmpty()
		{
			return _size==0;
		}

		/** 只清空size(前提是其他数据自行清理了,只底层用) */
		public void justClearSize()
		{
			_size=0;
			++_version;
			_lastFreeIndex=-1;
		}

		/** 尺寸减1(系统用) */
		public void minusSize()
		{
			--_size;
		}

		protected int hashChar(char arg)
		{
			int h = arg * -1640531527;
			return h ^ h >> 10;
		}

		protected int hashLong(long arg)
		{
			long h = arg * -7046029254386353131L;
			h ^= h >> 32;
			return (int)(h ^ h >> 16);
		}

		protected int hashInt(int arg)
		{
			int h = arg * -1640531527;
			return h ^ h >> 16;
		}

		protected int hashObj(object obj)
		{
			int hash=obj.GetHashCode();
			return hash ^ hash >> 16;
		}

		/** 拷贝基础,为了clone系列 */
		public void copyBase(BaseHash target)
		{
			_size=target._size;
			_maxSize=target._maxSize;
			_version=0;
		}

		/** 清空 */
		public virtual void clear()
		{
			_size=0;
			++_version;
			_lastFreeIndex=-1;
		}

		protected virtual void rehash(int size)
		{
			++_version;
			_lastFreeIndex=-1;
		}

		protected void postInsertHook(int index)
		{
			++_version;

			if(_lastFreeIndex>=0 && _lastFreeIndex==index)
			{
				_lastFreeIndex=-1;
			}

			if((++_size)>(_maxSize))
			{
				rehash(getArrCapacity() << 1);
			}
		}

		protected void postRemoveHook(int index)
		{
			--_size;
			++_version;

			if(_lastFreeIndex>=0 && index>_lastFreeIndex)
			{
				_lastFreeIndex=-1;
			}
		}

		/** 获取上个自由index */
		public int getLastFreeIndex()
		{
			if(isEmpty())
				return -1;
			
			if(_lastFreeIndex==-1)
				return _lastFreeIndex=toGetLastFreeIndex();

			return _lastFreeIndex;
		}

		protected virtual int toGetLastFreeIndex()
		{
			return -1;
		}

		protected void getLastFreeIndexError()
		{
			Ctrl.errorLog("impossible");
		}
	}
}