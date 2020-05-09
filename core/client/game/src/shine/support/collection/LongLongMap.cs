using System;
using System.Collections;
using System.Collections.Generic;

namespace ShineEngine
{
	public class LongLongMap:BaseHash
	{
		private long _freeValue;

		private long[] _set;

		private long[] _values;

		private EntrySet _entrySet;

		public LongLongMap()
		{

		}

		public LongLongMap(int capacity)
		{
			init(countCapacity(capacity));
		}

		private void checkInit()
		{
			if(_set!=null)
				return;

			init(_minSize);
		}

		public long getFreeValue()
		{
			return _freeValue;
		}

		public long[] getKeys()
		{
			checkInit();
			return _set;
		}

		public long[] getValues()
		{
			checkInit();
			return _values;
		}

		private void init(int capacity)
		{
			_maxSize=capacity >> 1;

			_set=new long[capacity];

			if(_freeValue!=0)
			{
				ObjectUtils.arrayFill(_set,_freeValue);
			}

			_values=new long[capacity];
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
				if((cur=keys[(index=(hashLong(key)) & (capacityMask=(keys.Length) - 1))])==key)
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

		private long findNewFreeOrRemoved()
		{
			long free=_freeValue;

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

		private long changeFree()
		{
			long newFree=findNewFreeOrRemoved();

			ObjectUtils.arrayReplace(_set,_freeValue,newFree);

			_freeValue=newFree;
			return newFree;
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
			long[] vals=_values;
			init(newCapacity);
			long[] newKeys=_set;
			int capacityMask=(newKeys.Length) - 1;
			long[] newVals=_values;
			for(int i=(keys.Length) - 1;i>=0;i--)
			{
				long key;
				if((key=keys[i])!=free)
				{
					int index;
					if((newKeys[(index=(hashLong(key)) & capacityMask)])!=free)
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

		private int insert(long key,long value)
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

			if((cur=keys[(index=(hashLong(key)) & (capacityMask=(keys.Length) - 1))])!=free)
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

		public void put(long key,long value)
		{
			checkInit();

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

		/** 是否存在 */
		public bool contains(long key)
		{
			if(_size==0)
				return false;

			return index(key)>=0;
		}

		public long get(long key)
		{
			if(_size==0)
				return 0;

			int idx=index(key);
			if(idx>=0)
			{
				return _values[idx];
			}
			else
			{
				return 0;
			}
		}

		public long this[long key]
		{
			get {return this.get(key);}
			set {this.put(key,value);}
		}

		public long getOrDefault(long key,long defaultValue)
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

		public long remove(long key)
		{
			if(_size==0)
				return 0;

			long free;
			if(key!=(free=_freeValue))
			{
				long[] keys=_set;
				int capacityMask=(keys.Length) - 1;
				int index;
				long cur;

				if((cur=keys[(index=(hashLong(key)) & capacityMask)])!=key)
				{
					if(cur==free)
					{
						return 0;
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
								return 0;
							}
						}
					}
				}

				long[] vals=_values;
				long val=vals[index];
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

					if((((keyToShift) - indexToShift) & capacityMask)>=shiftDistance)
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
							Ctrl.throwError("不能在遍历中删除");
						}
					}
				}

				keys[indexToRemove]=free;
				vals[indexToRemove]=0;
				postRemoveHook(indexToRemove);
				return val;
			}
			else
			{
				return 0;
			}
		}

		/** 清空 */
		public override void clear()
		{
			if(_size==0)
				return;

			base.clear();

			long fv=_freeValue;
			long[] set=_set;
			long[] values=_values;

			for(int i=set.Length - 1;i>=0;--i)
			{
				set[i]=fv;
				values[i]=0;
			}
		}

		/** 加值 */
		public long addValue(long key,long value)
		{
			checkInit();

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

		public long putIfAbsent(long key,long value)
		{
			checkInit();

			int index=insert(key,value);

			if(index<0)
			{
				return 0;
			}
			else
			{
				return _values[index];
			}
		}

		/** 获取key对应set */
		public LongSet getKeySet()
		{
			if(_size==0)
				return new LongSet();

			LongSet re=new LongSet(capacity());
			re.copyBase(this);
			re.setFreeValue(_freeValue);
			Array.Copy(_set,0,re.getKeys(),0,_set.Length);
			return re;
		}

		/** 遍历 */
		public void forEach(Action<long,long> consumer)
		{
			if(_size==0)
				return;

			int version=_version;
			long free=_freeValue;
			long[] keys=_set;
			long[] vals=_values;
			for(int i=(keys.Length) - 1;i>=0;--i)
			{
				long key;
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

		/** 遍历 */
		public void forEachS(Action<long,long> consumer)
		{
			if(_size==0)
				return;

			long free=_freeValue;
			long[] keys=_set;
			long[] vals=_values;
			long key;
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
			public long key;
			public long value;
		}

		public class EntrySet:IEnumerable<Entry>
		{
			private LongLongMap _map;

			public EntrySet(LongLongMap map)
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
			private int _tVersion;
			private int _index;
			private int _tSafeIndex;

			private long _tFv;
			private long[] _tSet;
			private long[] _tValues;
			private LongLongMap _map;
			private Entry _entry;

			public EntryIterator(LongLongMap map)
			{
				_map=map;
				_tVersion=map._version;
				_index=_tSafeIndex=map.getLastFreeIndex();

				_tFv=map._freeValue;
				_tSet=map._set;
				_tValues=map._values;
				_entry=new Entry();

				if(map._size==0)
					_tSet=null;
			}

			public void Reset()
			{
			}

			public void Dispose()
			{
			}

			public bool MoveNext()
			{
				if(_tSet==null)
					return false;

				long key;

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

					if(_tVersion!=_map._version)
					{
						Ctrl.throwError("ForeachModificationException");
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