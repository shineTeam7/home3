using System;
using System.Collections;
using System.Collections.Generic;

namespace ShineEngine
{
	/// <summary>
	/// set结构
	/// </summary>
	public class SSet<K>:BaseHash,IEnumerable<K>
	{
		private K _defaultKey;

		private K[] _set;

		public SSet()
		{
			_defaultKey=default(K);
			init(_minSize);
		}

		public SSet(int capacity)
		{
			_defaultKey=default(K);
			init(countCapacity(capacity));
		}

		/** 获取表 */
		public K[] getKeys()
		{
			return _set;
		}

		protected override void init(int capacity)
		{
			_capacity=capacity;

			_set=new K[capacity<<1];
		}

		public bool add(K key)
		{
			if(Equals(_defaultKey,key))
			{
				Ctrl.throwError("key不能为空");
				return false;
			}

			K[] keys=_set;
			int capacityMask;
			int index;
			K cur;
			if(!Equals(_defaultKey,cur=keys[(index=hashObj(key) & (capacityMask=(keys.Length) - 1))]))
			{
				if(key.Equals(cur))
				{
					return false;
				}
				else
				{
					while(true)
					{
						if(Equals(_defaultKey,cur=keys[(index=(index - 1) & capacityMask)]))
						{
							break;
						}
						else if(key.Equals(cur))
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

		protected override int toGetLastFreeIndex()
		{
			K[] keys=_set;

			for(int i=keys.Length-1;i >= 0;--i)
			{
				if(Equals(keys[i],_defaultKey))
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
			K[] keys=_set;
			init(newCapacity);
			K[] newKeys=_set;
			int capacityMask=(newKeys.Length) - 1;
			for(int i=(keys.Length) - 1;i >= 0;i--)
			{
				K key;
				if(!Equals(_defaultKey,key=keys[i]))
				{
					int index;
					if(!Equals(_defaultKey,newKeys[(index=hashObj(key) & capacityMask)]))
					{
						while(true)
						{
							if(Equals(_defaultKey,newKeys[(index=(index - 1) & capacityMask)]))
							{
								break;
							}
						}
					}
					newKeys[index]=key;
				}
			}
		}

		private int index(K key)
		{
			K k=key;
			K[] keys=_set;
			int capacityMask;
			int index;
			K cur;
			if(k.Equals((cur=keys[(index=hashObj(k) & (capacityMask=(keys.Length) - 1))])))
			{
				return index;
			}
			else
			{
				if(Equals(_defaultKey,cur))
				{
					return -1;
				}
				else
				{
					if(k.Equals(cur))
					{
						return index;
					}
					else
					{
						while(true)
						{
							if(k.Equals((cur=keys[(index=(index - 1) & capacityMask)])))
							{
								return index;
							}
							else if(Equals(_defaultKey,cur))
							{
								return -1;
							}
						}
					}
				}
			}
		}

		public bool contains(K key)
		{
			if(Equals(_defaultKey,key))
			{
				Ctrl.throwError("key不能为空");
				return false;
			}

			if(_size==0)
				return false;

			return index(key) >= 0;
		}

		public bool remove(K key)
		{
			if(Equals(_defaultKey,key))
			{
				Ctrl.throwError("key不能为空");
				return false;
			}

			if(_size==0)
				return false;

			K k=key;
			K[] keys=_set;
			int capacityMask=(keys.Length) - 1;
			int index;
			K cur;
			if(!k.Equals((cur=keys[(index=hashObj(k) & capacityMask)])))
			{
				if(Equals(_defaultKey,cur))
				{
					return false;
				}
				else
				{
					while(true)
					{
						if(k.Equals((cur=keys[(index=(index - 1) & capacityMask)])))
						{
							break;
						}
						else if(Equals(_defaultKey,cur))
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
				K keyToShift;
				if(Equals(_defaultKey,keyToShift=keys[indexToShift]))
				{
					break;
				}
				K castedKeyToShift=keyToShift;
				if(((hashObj(castedKeyToShift) - indexToShift) & capacityMask) >= shiftDistance)
				{
					keys[indexToRemove]=castedKeyToShift;
					indexToRemove=indexToShift;
					shiftDistance=1;
				}
				else
				{
					shiftDistance++;
				}
			}

			keys[indexToRemove]=default(K);
			postRemoveHook(indexToRemove);

			return true;
		}

		public override void clear()
		{
			if(_size==0)
			{
				return;
			}

			base.clear();

			ObjectUtils.arrayFill(_set,default(K));
		}

		/** 添加一组 */
		public void addAll(SList<K> list)
		{
			if(list.isEmpty())
				return;

			K[] values=list.getValues();

			for(int i=0,len=list.size();i<len;++i)
			{
				add(values[i]);
			}
		}

		/** 添加一组 */
		public void addAll(SSet<K> set)
		{
			if(set.isEmpty())
				return;

			K[] keys=set.getKeys();
			K k;

			for(int i=keys.Length-1;i>=0;--i)
			{
				if(!Equals(_defaultKey,k=keys[i]))
				{
					add(k);
				}
			}
		}

		/** 添加一组 */
		public void addAll(K[] arr)
		{
			foreach(K k in arr)
			{
				add(k);
			}
		}

		/** 获取排序好的List */
		public SList<K> getSortedList()
		{
			SList<K> list=new SList<K>(_size);

			if(_size==0)
				return list;

			K[] values=list.getValues();
			int j=0;

			K[] keys=_set;
			K k;

			for(int i=keys.Length-1;i>=0;--i)
			{
				if((k=keys[i])!=null)
				{
					values[j++]=k;
				}
			}

			list.justSetSize(size());

			list.sort();

			return list;
		}

		public SSet<K> clone()
		{
			if(_size==0)
				return new SSet<K>();

			SSet<K> re=new SSet<K>(capacity());
			Array.Copy(_set,0,re._set,0,_set.Length);
			re._defaultKey=_defaultKey;
			re.copyBase(this);
			return re;
		}

		/** 转化为原生集合 */
		public HashSet<K> toNatureList()
		{
			HashSet<K> re=new HashSet<K>();

			K[] set=_set;
			K key;

			for(int i=set.Length - 1;i >= 0;--i)
			{
				if(!Equals(_defaultKey,key=set[i]))
				{
					re.Add(key);
				}
			}

			return re;
		}

		public void addAll(HashSet<K> map)
		{
			ensureCapacity(map.Count);

			foreach(K v in map)
			{
				this.add(v);
			}
		}

		/** 遍历 */
		public void forEach(Action<K> consumer)
		{
			if(_size==0)
				return;

			int version=_version;
			K[] set=_set;
			K key;

			for(int i=set.Length - 1;i >= 0;--i)
			{
				if(!Equals(_defaultKey,key=set[i]))
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
		public void forEachS(Action<K> consumer)
		{
			if(_size==0)
				return;

			K[] keys=_set;
			K key;
			int safeIndex=getLastFreeIndex();

			for(int i=safeIndex-1;i>=0;--i)
			{
				if(!Equals(_defaultKey,key=keys[i]))
				{
					consumer(key);

					if(!key.Equals(keys[i]))
					{
						++i;
					}
				}
			}

			for(int i=keys.Length-1;i>safeIndex;--i)
			{
				if(!Equals(_defaultKey,key=keys[i]))
				{
					consumer(key);

					if(!key.Equals(keys[i]))
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

		IEnumerator<K> IEnumerable<K>.GetEnumerator()
		{
			return new ForEachIterator(this);
		}

		IEnumerator IEnumerable.GetEnumerator()
		{
			return new ForEachIterator(this);
		}

		public struct ForEachIterator:IEnumerator<K>
		{
			private int _index;
			private int _tSafeIndex;

			private K[] _tSet;
			private K _k;

			public ForEachIterator(SSet<K> map)
			{
				_index=_tSafeIndex=map.getLastFreeIndex();
				if(map._size==0)
					_index=_tSafeIndex+1;

				_tSet=map._set;
				_k=default(K);
			}

			public void Reset()
			{
			}

			public void Dispose()
			{
			}

			public bool MoveNext()
			{
				if(!Equals(_k,default(K)) && !Equals(_k,_tSet[_index]))
				{
					++_index;
				}

				K key;

				if(_index<=_tSafeIndex)
				{
					while(--_index >= 0)
					{
						if(!Equals(key=_tSet[_index],default(K)))
						{
							_k=key;
							return true;
						}
					}

					_k=default(K);
					_index=_tSet.Length;
					return MoveNext();
				}
				else
				{
					while(--_index > _tSafeIndex)
					{
						if(!Equals(key=_tSet[_index],default(K)))
						{
							_k=key;
							return true;
						}
					}

					_k=default(K);
					return false;
				}
			}

			public K Current
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