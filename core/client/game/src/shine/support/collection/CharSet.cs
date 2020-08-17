using System;
using System.Collections;
using System.Collections.Generic;

namespace ShineEngine
{
	public class CharSet:BaseHash,IEnumerable<char>
	{
		private char[] _set;

		private char _freeValue;

		public CharSet()
		{
			init(_minSize);
		}

		public CharSet(int capacity)
		{
			init(countCapacity(capacity));
		}

		public char getFreeValue()
		{
			return _freeValue;
		}

		public char[] getKeys()
		{
			return _set;
		}

		protected override void init(int capacity)
		{
			_capacity=capacity;

			_set=new char[capacity<<1];

			if(_freeValue!=0)
				ObjectUtils.arrayFill(_set,_freeValue);
		}

		private char changeFree()
		{
			char newFree=findNewFreeOrRemoved();
			ObjectUtils.arrayReplace(_set,_freeValue,newFree);
			_freeValue=newFree;
			return newFree;
		}

		public void setFreeValue(char value)
		{
			_freeValue=value;
		}

		private char findNewFreeOrRemoved()
		{
			char free=_freeValue;

			char newFree;
			{
				do
				{
					newFree=(char)MathUtils.randomInt();
				}
				while((newFree==free) || ((index(newFree))>=0));
			}

			return newFree;
		}

		private int index(char key)
		{
			char free;
			if(key!=(free=_freeValue))
			{
				char[] keys=_set;
				int capacityMask;
				int index;
				char cur;
				if((cur=keys[(index=hashChar(key) & (capacityMask=(keys.Length) - 1))])==key)
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
			char free=_freeValue;
			char[] keys=_set;

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

			char free=_freeValue;
			char[] keys=_set;
			init(newCapacity);
			char[] newKeys=_set;
			int capacityMask=(newKeys.Length) - 1;
			for(int i=(keys.Length) - 1;i>=0;--i)
			{
				char key;
				if((key=keys[i])!=free)
				{
					int index;
					if((newKeys[(index=hashChar(key) & capacityMask)])!=free)
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

		public bool add(char key)
		{
			char free;
			if(key==(free=_freeValue))
			{
				free=changeFree();
			}
			char[] keys=_set;
			int capacityMask;
			int index;
			char cur;
			if((cur=keys[(index=hashChar(key) & (capacityMask=(keys.Length) - 1))])!=free)
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

		public bool contains(char key)
		{
			if(_size==0)
				return false;

			return (index(key))>=0;
		}

		public bool remove(char key)
		{
			if(_size==0)
				return false;

			char free;
			if(key!=(free=_freeValue))
			{
				char[] keys=_set;
				int capacityMask=(keys.Length) - 1;
				int index;
				char cur;
				if((cur=keys[(index=hashChar(key) & capacityMask)])!=key)
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
					char keyToShift;
					if((keyToShift=keys[indexToShift])==free)
					{
						break;
					}

					if(((hashChar(keyToShift) - indexToShift) & capacityMask)>=shiftDistance)
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

		/** 转换数组(带排序) */
		public char[] toArray()
		{
			if(isEmpty())
			{
				return ObjectUtils.EmptyCharArr;
			}

			char[] re=new char[_size];
			int j=0;

			char[] keys=_set;
			char fv=_freeValue;
			char k;

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
		public void forEach(Action<char> consumer)
		{
			if(_size==0)
				return;

			int version=_version;
			char free=_freeValue;
			char[] keys=_set;
			for(int i=(keys.Length) - 1;i>=0;--i)
			{
				char key;
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
		public void forEachS(Action<char> consumer)
		{
			if(_size==0)
				return;

			char free=_freeValue;
			char[] keys=_set;
			char key;

			int capacityMask=keys.Length-1;
			int safeIndex=-1;

			for(int i=capacityMask;i >= 0;--i)
			{
				if(keys[i]==free)
				{
					safeIndex=i;
					break;
				}
			}

			if(safeIndex==-1)
			{
				getLastFreeIndexError();
				return;
			}

			for(int i=(safeIndex-1) & capacityMask;i != safeIndex;i=(i-1)&capacityMask)
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
			private int _index;
			private int _tSafeIndex;

			private char _tFv;
			private char[] _tSet;
			private char _k;

			public ForEachIterator(CharSet map)
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

				char key;

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

			public char Current
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