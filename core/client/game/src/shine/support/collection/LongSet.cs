using System;
using System.Collections;
using System.Collections.Generic;

namespace ShineEngine
{
	public class LongSet:BaseHash,IEnumerable<long>
	{
		private long[] _set;

		private long _freeValue;

		public LongSet()
		{
			init(_minSize);
		}

		public LongSet(int capacity)
		{
			init(countCapacity(capacity));
		}


		public long getFreeValue()
		{
			return _freeValue;
		}

		public long[] getKeys()
		{
			return _set;
		}

		protected override void init(int capacity)
		{
			_capacity=capacity;

			_set=new long[capacity<<1];

			if(_freeValue!=0)
				ObjectUtils.arrayFill(_set,_freeValue);
		}

		private long changeFree()
		{
			long newFree=findNewFreeOrRemoved();
			ObjectUtils.arrayReplace(_set,_freeValue,newFree);
			_freeValue=newFree;
			return newFree;
		}

		public void setFreeValue(long value)
		{
			_freeValue=value;
		}

		private long findNewFreeOrRemoved()
		{
			long free=_freeValue;

			long newFree;
			{
				do
				{
					newFree=MathUtils.randomInt();
				}
				while((newFree==free) || ((index(newFree))>=0));
			}

			return newFree;
		}

		private int index(long key)
		{
			long free;
			if(key!=(free=_freeValue))
			{
				long[] keys=_set;
				int capacityMask;
				int index;
				long cur;
				if((cur=keys[(index=hashLong(key) & (capacityMask=(keys.Length) - 1))])==key)
				{
					return index;
				}
				else
				{
					if(cur==free)
					{
						return -1;
					}
					else
					{
						while(true)
						{
							if((cur=keys[(index=(index - 1) & capacityMask)])==key)
							{
								return index;
							}
							else if(cur==free)
							{
								return -1;
							}
						}
					}
				}
			}
			else
			{
				return -1;
			}
		}

		protected override int toGetLastFreeIndex()
		{
			long free=_freeValue;
			long[] keys=_set;

			for(int i=keys.Length-1;i >= 0;--i)
			{
				if(keys[i]==free)
				{
					return i;
				}
			}

			getLastFreeIndexError();

			return -1;
		}

		protected override void rehash(int newCapacity)
		{
			base.rehash(newCapacity);

			long free=_freeValue;
			long[] keys=_set;
			init(newCapacity);
			long[] newKeys=_set;
			int capacityMask=(newKeys.Length) - 1;
			for(int i=(keys.Length) - 1;i>=0;--i)
			{
				long key;
				if((key=keys[i])!=free)
				{
					int index;
					if((newKeys[(index=hashLong(key) & capacityMask)])!=free)
					{
						while(true)
						{
							if((newKeys[(index=(index - 1) & capacityMask)])==free)
							{
								break;
							}
						}
					}

					newKeys[index]=key;
				}
			}
		}

		public bool add(long key)
		{
			long free;
			if(key==(free=_freeValue))
			{
				free=changeFree();
			}
			long[] keys=_set;
			int capacityMask;
			int index;
			long cur;
			if((cur=keys[(index=hashLong(key) & (capacityMask=(keys.Length) - 1))])!=free)
			{
				if(cur==key)
				{
					return false;
				}
				else
				{
					while(true)
					{
						if((cur=keys[(index=(index - 1) & capacityMask)])==free)
						{
							break;
						}
						else if(cur==key)
						{
							return false;
						}
					}
				}
			}

			keys[index]=key;
			postInsertHook(index);
			return true;
		}

		public bool contains(long key)
		{
			if(_size==0)
				return false;

			return (index(key))>=0;
		}

		public bool remove(long key)
		{
			if(_size==0)
				return false;

			long free;
			if(key!=(free=_freeValue))
			{
				long[] keys=_set;
				int capacityMask=(keys.Length) - 1;
				int index;
				long cur;
				if((cur=keys[(index=hashLong(key) & capacityMask)])!=key)
				{
					if(cur==free)
					{
						return false;
					}
					else
					{
						while(true)
						{
							if((cur=keys[(index=(index - 1) & capacityMask)])==key)
							{
								break;
							}
							else if(cur==free)
							{
								return false;
							}
						}
					}
				}

				int indexToRemove=index;
				int indexToShift=indexToRemove;
				int shiftDistance=1;
				while(true)
				{
					indexToShift=(indexToShift - 1) & capacityMask;
					long keyToShift;
					if((keyToShift=keys[indexToShift])==free)
					{
						break;
					}

					if(((hashLong(keyToShift) - indexToShift) & capacityMask)>=shiftDistance)
					{
						keys[indexToRemove]=keyToShift;
						indexToRemove=indexToShift;
						shiftDistance=1;
					}
					else
					{
						shiftDistance++;
						if(indexToShift==(1 + index))
						{
							Ctrl.throwError("ConcurrentModificationException");
						}
					}
				}

				keys[indexToRemove]=free;
				postRemoveHook(indexToRemove);

				return true;
			}
			else
			{
				return false;
			}
		}

		/** 清空 */
		public override void clear()
		{
			if(_size==0)
				return;

			base.clear();

			ObjectUtils.arrayFill(_set,_freeValue);
		}

		/** 转化为原生集合 */
		public HashSet<long> toNatureList()
		{
			HashSet<long> re=new HashSet<long>();

			long free=_freeValue;
			long[] keys=_set;
			for(int i=(keys.Length) - 1;i>=0;--i)
			{
				long key;
				if((key=keys[i])!=free)
				{
					re.Add(key);
				}
			}

			return re;
		}

		public void addAll(HashSet<long> map)
		{
			ensureCapacity(map.Count);

			foreach(long v in map)
			{
				this.add(v);
			}
		}

		/** 遍历 */
		public void forEach(Action<long> consumer)
		{
			if(_size==0)
				return;

			int version=_version;
			long free=_freeValue;
			long[] keys=_set;
			for(int i=keys.Length - 1;i>=0;--i)
			{
				long key;
				if((key=keys[i])!=free)
				{
					consumer(key);
				}
			}

			if(version!=_version)
			{
				Ctrl.throwError("ForeachModificationException");
			}
		}

		/** 可修改遍历 */
		public void forEachS(Action<long> consumer)
		{
			if(_size==0)
				return;

			long free=_freeValue;
			long[] keys=_set;
			for(int i=keys.Length - 1;i>=0;--i)
			{
				long key;
				if((key=keys[i])!=free)
				{
					consumer(key);

					if(key!=keys[i])
					{
						++i;
					}
				}
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
			private int _index;
			private int _tSafeIndex;

			private long _tFv;
			private long[] _tSet;
			private long _k;

			public ForEachIterator(LongSet map)
			{
				_index=_tSafeIndex=map.getLastFreeIndex();
				if(map._size==0)
					_index=_tSafeIndex+1;

				_tSet=map._set;
				_k=_tFv=map._freeValue;
			}

			public void Reset()
			{
			}

			public void Dispose()
			{
			}

			public bool MoveNext()
			{
				if(_k!=_tFv && _k!=_tSet[_index])
				{
					++_index;
				}

				long key;

				if(_index<=_tSafeIndex)
				{
					while(--_index >= 0)
					{
						if((key=_tSet[_index])!=_tFv)
						{
							_k=key;
							return true;
						}
					}

					_k=_tFv;
					_index=_tSet.Length;
					return MoveNext();
				}
				else
				{
					while(--_index > _tSafeIndex)
					{
						if((key=_tSet[_index])!=_tFv)
						{
							_k=key;
							return true;
						}
					}

					_k=default;
					return false;
				}
			}

			public long Current
			{
				get {return _k;}
			}

			object IEnumerator.Current
			{
				get {return _k;}
			}
		}
	}
}