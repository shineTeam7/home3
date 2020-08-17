using System;
using System.Collections;
using System.Collections.Generic;

namespace ShineEngine
{
	public class IntFloatMap:BaseHash
	{
		private int _freeValue;

		private int[] _set;

		private float[] _values;

		private EntrySet _entrySet;

		public IntFloatMap()
		{
			init(_minSize);
		}

		public IntFloatMap(int capacity)
		{
			init(countCapacity(capacity));
		}

		public int getFreeValue()
		{
			return _freeValue;
		}

		public int[] getKeys()
		{
			return _set;
		}

		public float[] getValues()
		{
			return _values;
		}

		protected override void init(int capacity)
		{
			_capacity=capacity;

			_set=new int[capacity<<1];

			if(_freeValue!=0)
			{
				ObjectUtils.arrayFill(_set,_freeValue);
			}

			_values=new float[capacity<<1];
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

		private int changeFree()
		{
			int newFree=findNewFreeOrRemoved();

			ObjectUtils.arrayReplace(_set,_freeValue,newFree);

			_freeValue=newFree;
			return newFree;
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
			base.rehash(newCapacity);

			int free=_freeValue;
			int[] keys=_set;
			float[] vals=_values;
			init(newCapacity);
			int[] newKeys=_set;
			int capacityMask=(newKeys.Length) - 1;
			float[] newVals=_values;
			for(int i=(keys.Length) - 1;i>=0;i--)
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
					newVals[index]=vals[i];
				}
			}
		}

		private int insert(int key,float value)
		{
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
					return index;
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
							return index;
						}
					}
				}
			}

			keys[index]=key;
			_values[index]=value;
			postInsertHook(index);
			return -1;
		}

		public void put(int key,float value)
		{
			int index=insert(key,value);

			if(index<0)
			{
				return;
			}
			else
			{
				_values[index]=value;
				return;
			}
		}

		/** 加值 */
		public float addValue(int key,float value)
		{
			int index=insert(key,value);

			if(index<0)
			{
				return value;
			}
			else
			{
				return _values[index]+=value;
			}
		}

		/** 是否存在 */
		public bool contains(int key)
		{
			if(_size==0)
				return false;

			return index(key)>=0;
		}

		public float get(int key)
		{
			if(_size==0)
				return 0f;

			int idx=index(key);
			if(idx>=0)
			{
				return _values[idx];
			}
			else
			{
				return 0f;
			}
		}

		public float this[int key]
		{
			get {return this.get(key);}
			set {this.put(key,value);}
		}

		public float getOrDefault(int key,float defaultValue)
		{
			if(_size==0)
				return defaultValue;

			int idx=index(key);

			if(idx>=0)
			{
				return _values[idx];
			}
			else
			{
				return defaultValue;
			}
		}

		public float remove(int key)
		{
			if(_size==0)
				return 0f;

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
						return 0L;
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
								return 0L;
							}
						}
					}
				}

				float[] vals=_values;
				float val=vals[index];
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
						vals[indexToRemove]=vals[indexToShift];
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
				vals[indexToRemove]=0L;
				postRemoveHook(indexToRemove);

				return val;
			}
			else
			{
				return 0L;
			}
		}

		/** 清空 */
		public override void clear()
		{
			if(_size==0)
				return;

			base.clear();

			for(int i=_set.Length - 1;i>=0;--i)
			{
				_set[i]=_freeValue;
			}
		}

		public float putIfAbsent(int key,float value)
		{
			int index=insert(key,value);

			if(index<0)
			{
				return 0L;
			}
			else
			{
				return _values[index];
			}
		}

		/** 转化为原生集合 */
		public Dictionary<int,float> toNatureMap()
		{
			Dictionary<int,float> re=new Dictionary<int,float>(size());

			int free=_freeValue;
			int[] keys=_set;
			float[] vals=_values;
			for(int i=(keys.Length) - 1;i >= 0;--i)
			{
				int key;
				if((key=keys[i])!=free)
				{
					re.Add(key,vals[i]);
				}
			}

			return re;
		}

		public void addAll(Dictionary<int,float> map)
		{
			ensureCapacity(map.Count);

			foreach(KeyValuePair<int,float> kv in map)
			{
				this.put(kv.Key,kv.Value);
			}
		}

		/** 遍历 */
		public void forEach(Action<int,float> consumer)
		{
			if(_size==0)
				return;

			int version=_version;
			int free=_freeValue;
			int[] keys=_set;
			float[] vals=_values;
			for(int i=(keys.Length) - 1;i>=0;--i)
			{
				int key;
				if((key=keys[i])!=free)
				{
					consumer(key,vals[i]);
				}
			}

			if(version!=_version)
			{
				Ctrl.throwError("ForeachModificationException");
			}
		}

		/** 可修改遍历 */
		public void forEachS(Action<int,float> consumer)
		{
			if(_size==0)
				return;

			int free=_freeValue;
			int[] keys=_set;
			float[] vals=_values;
			int key;
			int safeIndex=getLastFreeIndex();

			for(int i=safeIndex-1;i>=0;--i)
			{
				if((key=keys[i])!=free)
				{
					consumer(key,vals[i]);

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
					consumer(key,vals[i]);

					if(key!=keys[i])
					{
						++i;
					}
				}
			}
		}

		public EntrySet entrySet()
		{
			if(_entrySet!=null)
				return _entrySet;

			return _entrySet=new EntrySet(this);
		}

		public struct Entry
		{
			public int key;
			public float value;
		}

		public class EntrySet:IEnumerable<Entry>
		{
			private IntFloatMap _map;

			public EntrySet(IntFloatMap map)
			{
				_map=map;
			}

			public EntryIterator GetEnumerator()
			{
				return new EntryIterator(_map);
			}

			IEnumerator<Entry> IEnumerable<Entry>.GetEnumerator()
			{
				return new EntryIterator(_map);
			}

			IEnumerator IEnumerable.GetEnumerator()
			{
				return new EntryIterator(_map);
			}
		}

		public struct EntryIterator:IEnumerator<Entry>
		{
			private int _index;
			private int _tSafeIndex;

			private int _tFv;
			private int[] _tSet;
			private float[] _tValues;
			private Entry _entry;

			public EntryIterator(IntFloatMap map)
			{
				_index=_tSafeIndex=map.getLastFreeIndex();
				if(map._size==0)
					_index=_tSafeIndex+1;

				_tSet=map._set;
				_tValues=map._values;
				_entry=new Entry();
				_entry.key=_tFv=map._freeValue;
			}

			public void Reset()
			{
			}

			public void Dispose()
			{
			}

			public bool MoveNext()
			{
				if(_entry.key!=_tFv && _entry.key!=_tSet[_index])
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
							_entry.key=key;
							_entry.value=_tValues[_index];
							return true;
						}
					}

					_entry.key=_tFv;
					_index=_tSet.Length;
					return MoveNext();
				}
				else
				{
					while(--_index > _tSafeIndex)
					{
						if((key=_tSet[_index])!=_tFv)
						{
							_entry.key=key;
							_entry.value=_tValues[_index];
							return true;
						}
					}

					return false;
				}
			}

			public Entry Current
			{
				get {return _entry;}
			}

			object IEnumerator.Current
			{
				get {return _entry;}
			}
		}
	}
}