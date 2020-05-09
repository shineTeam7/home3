using System;
using System.Collections;
using System.Collections.Generic;

namespace ShineEngine
{
	/// <summary>
	/// 
	/// </summary>
	public class IntSet:BaseHash,IEnumerable<int>
	{
		private int[] _set;

		private int _freeValue;

		public IntSet()
		{

		}

		public IntSet(int capacity)
		{
			init(countCapacity(capacity));
		}

		private void checkInit()
		{
			if(_set!=null)
				return;

			init(_minSize);
		}

		public int getFreeValue()
		{
			return _freeValue;
		}

		public int[] getKeys()
		{
			checkInit();
			return _set;
		}

		private void init(int capacity)
		{
			_maxSize=capacity;

			_set=new int[capacity<<1];

			if(_freeValue!=0)
				ObjectUtils.arrayFill(_set,_freeValue);
		}

		private int changeFree()
		{
			int newFree=findNewFreeOrRemoved();
			ObjectUtils.arrayReplace(_set,_freeValue,newFree);
			_freeValue=newFree;
			return newFree;
		}

		public void setFreeValue(int value)
		{
			_freeValue=value;
		}

		private int findNewFreeOrRemoved()
		{
			int free=_freeValue;

			int newFree;
			{
				do
				{
					newFree=MathUtils.randomInt();
				}
				while((newFree==free) || ((index(newFree))>=0));
			}

			return newFree;
		}

		private int index(int key)
		{
			int free;
			if(key!=(free=_freeValue))
			{
				int[] keys=_set;
				int capacityMask;
				int index;
				int cur;
				if((cur=keys[(index=hashInt(key) & (capacityMask=(keys.Length) - 1))])==key)
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
			int free=_freeValue;
			int[] keys=_set;

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
			++_version;
			int free=_freeValue;
			int[] keys=_set;
			init(newCapacity);
			int[] newKeys=_set;
			int capacityMask=(newKeys.Length) - 1;
			for(int i=(keys.Length) - 1;i>=0;--i)
			{
				int key;
				if((key=keys[i])!=free)
				{
					int index;
					if((newKeys[(index=hashInt(key) & capacityMask)])!=free)
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

		public bool add(int key)
		{
			checkInit();

			int free;
			if(key==(free=_freeValue))
			{
				free=changeFree();
			}
			int[] keys=_set;
			int capacityMask;
			int index;
			int cur;
			if((cur=keys[(index=hashInt(key) & (capacityMask=(keys.Length) - 1))])!=free)
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

		public void addAll(int[] arr)
		{
			if(arr==null)
				return;

			foreach(int v in arr)
			{
				add(v);
			}
		}

		public void addAll(IntList list)
		{
			if(list==null)
				return;

			int[] values=list.getValues();

			for(int i=0,len=list.size();i<len;++i)
			{
				add(values[i]);
			}
		}

		public bool contains(int key)
		{
			if(_size==0)
				return false;

			return (index(key))>=0;
		}

		public bool remove(int key)
		{
			if(_size==0)
				return false;

			int free;
			if(key!=(free=_freeValue))
			{
				int[] keys=_set;
				int capacityMask=(keys.Length) - 1;
				int index;
				int cur;
				if((cur=keys[(index=hashInt(key) & capacityMask)])!=key)
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
					int keyToShift;
					if((keyToShift=keys[indexToShift])==free)
					{
						break;
					}

					if(((hashInt(keyToShift) - indexToShift) & capacityMask)>=shiftDistance)
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
							Ctrl.print("遍历中删除");
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

		/** 扩容 */
		public void ensureCapacity(int capacity)
		{
			if(capacity>_maxSize)
			{
				int t=countCapacity(capacity);

				if(_set==null)
				{
					init(t);
				}
				else if(t>_set.Length)
				{
					rehash(t);
				}
			}
		}

		/** 转换数组(带排序) */
		public int[] toArray()
		{
			if(isEmpty())
			{
				return ObjectUtils.EmptyIntArr;
			}

			int[] re=new int[_size];
			int j=0;

			int[] keys=_set;
			int fv=_freeValue;
			int k;

			for(int i=keys.Length-1;i>=0;--i)
			{
				if((k=keys[i])!=fv)
				{
					re[j++]=k;
				}
			}

			Array.Sort(re);

			return re;
		}

		/** 遍历 */
		public void forEach(Action<int> consumer)
		{
			if(_size==0)
				return;

			int version=_version;
			int free=_freeValue;
			int[] keys=_set;
			for(int i=(keys.Length) - 1;i>=0;--i)
			{
				int key;
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
		public void forEachS(Action<int> consumer)
		{
			if(_size==0)
				return;

			int free=_freeValue;
			int[] keys=_set;
			int key;
			int safeIndex=getLastFreeIndex();

			for(int i=safeIndex-1;i>=0;--i)
			{
				if((key=keys[i])!=free)
				{
					consumer(key);

					if(key!=keys[i])
					{
						++i;
					}
				}
			}

			for(int i=keys.Length-1;i>safeIndex;--i)
			{
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
			private int _index;
			private int _tSafeIndex;

			private int _tFv;
			private int[] _tSet;
			private int _k;

			public ForEachIterator(IntSet map)
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

				int key;

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

					_k=_tFv;
					return false;
				}
			}

			public int Current
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