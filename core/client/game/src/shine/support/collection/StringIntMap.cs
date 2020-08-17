using System;
using System.Collections;
using System.Collections.Generic;

namespace ShineEngine
{
	public class StringIntMap:BaseHash
	{
		private string[] _set;

		private int[] _values;

		private EntrySet _entrySet;

		public StringIntMap()
		{
			init(_minSize);
		}

		public StringIntMap(int capacity)
		{
			init(countCapacity(capacity));
		}

		public string[] getKeys()
		{
			return _set;
		}

		public int[] getValues()
		{
			return _values;
		}

		protected override void init(int capacity)
		{
			_capacity=capacity;

			_set=new string[capacity<<1];

			_values=new int[capacity<<1];
		}

		private int index(string key)
		{
			if(key!=null)
			{
				string[] keys=_set;
				int capacityMask;
				int index;
				string cur;
				if((cur=keys[(index=hashObj(key) & (capacityMask=(keys.Length) - 1))])==key)
				{
					return index;
				}
				else
				{
					if(cur==null)
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
							else if(cur==null)
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
			string[] keys=_set;

			for(int i=keys.Length-1;i >= 0;--i)
			{
				if(keys[i]==null)
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

			string[] keys=_set;
			int[] vals=_values;
			init(newCapacity);
			string[] newKeys=_set;
			int capacityMask=(newKeys.Length) - 1;
			int[] newVals=_values;
			for(int i=(keys.Length) - 1;i>=0;i--)
			{
				string key;
				if((key=keys[i])!=null)
				{
					int index;
					if((newKeys[(index=hashObj(key) & capacityMask)])!=null)
					{
						while(true)
						{
							if((newKeys[(index=(index - 1) & capacityMask)])==null)
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

		private int insert(string key,int value)
		{
			string[] keys=_set;
			int capacityMask;
			int index;
			string cur;
			if((cur=keys[(index=hashObj(key) & (capacityMask=(keys.Length) - 1))])!=null)
			{
				if(cur==key)
				{
					return index;
				}
				else
				{
					while(true)
					{
						if((cur=keys[(index=(index - 1) & capacityMask)])==null)
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

		public void put(string key,int value)
		{
			if(key==null)
			{
				Ctrl.throwError("key不能为空");
				return;
			}

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
		public int addValue(string key,int value)
		{
			if(key==null)
			{
				Ctrl.throwError("key不能为空");
				return 0;
			}

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
		public bool contains(string key)
		{
			if(key==null)
			{
				Ctrl.throwError("key不能为空");
				return false;
			}

			if(_size==0)
				return false;

			return index(key)>=0;
		}

		public int get(string key)
		{
			if(key==null)
			{
				Ctrl.throwError("key不能为空");
				return 0;
			}

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

		public int this[string key]
		{
			get {return this.get(key);}
			set {this.put(key,value);}
		}

		public int getOrDefault(string key,int defaultValue)
		{
			if(key==null)
			{
				Ctrl.throwError("key不能为空");
				return 0;
			}

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

		public int remove(string key)
		{
			if(key==null)
			{
				Ctrl.throwError("key不能为空");
				return 0;
			}

			if(_size==0)
				return 0;

			string[] keys=_set;
			int capacityMask=(keys.Length) - 1;
			int index;
			string cur;
			if((cur=keys[(index=hashObj(key) & capacityMask)])!=key)
			{
				if(cur==null)
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
						else if(cur==null)
						{
							return 0;
						}
					}
				}
			}

			int[] vals=_values;
			int val=vals[index];
			int indexToRemove=index;
			int indexToShift=indexToRemove;
			int shiftDistance=1;
			while(true)
			{
				indexToShift=(indexToShift - 1) & capacityMask;
				string keyToShift;
				if((keyToShift=keys[indexToShift])==null)
				{
					break;
				}

				if(((hashObj(keyToShift) - indexToShift) & capacityMask)>=shiftDistance)
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

			keys[indexToRemove]=null;
			vals[indexToRemove]=0;
			postRemoveHook(indexToRemove);

			return val;
		}

		/** 清空 */
		public override void clear()
		{
			if(_size==0)
				return;

			base.clear();

			for(int i=_set.Length - 1;i>=0;--i)
			{
				_set[i]=null;
			}
		}

		public int putIfAbsent(string key,int value)
		{
			if(key==null)
			{
				Ctrl.throwError("key不能为空");
				return 0;
			}

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

		/** 转化为原生集合 */
		public Dictionary<string,int> toNatureMap()
		{
			Dictionary<string,int> re=new Dictionary<string,int>(size());

			string[] keys=_set;
			int[] vals=_values;
			for(int i=(keys.Length) - 1;i>=0;--i)
			{
				string key;
				if((key=keys[i])!=null)
				{
					re.Add(key,vals[i]);
				}
			}

			return re;
		}

		public void addAll(Dictionary<string,int> map)
		{
			ensureCapacity(map.Count);

			foreach(KeyValuePair<string,int> kv in map)
			{
				this.put(kv.Key,kv.Value);
			}
		}

		/** 遍历 */
		public void forEach(Action<string,int> consumer)
		{
			if(_size==0)
				return;

			int version=_version;
			string[] keys=_set;
			int[] vals=_values;
			for(int i=(keys.Length) - 1;i>=0;--i)
			{
				string key;
				if((key=keys[i])!=null)
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
		public void forEachS(Action<string,int> consumer)
		{
			if(_size==0)
				return;

			string[] keys=_set;
			int[] vals=_values;
			string key;
			int safeIndex=getLastFreeIndex();

			for(int i=safeIndex-1;i>=0;--i)
			{
				if((key=keys[i])!=null)
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
				if((key=keys[i])!=null)
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
			public string key;
			public int value;
		}

		public class EntrySet:IEnumerable<Entry>
		{
			private StringIntMap _map;

			public EntrySet(StringIntMap map)
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

			private string[] _tSet;
			private int[] _tValues;
			private Entry _entry;

			public EntryIterator(StringIntMap map)
			{
				_index=_tSafeIndex=map.getLastFreeIndex();
				if(map._size==0)
					_index=_tSafeIndex+1;

				_tSet=map._set;
				_tValues=map._values;
				_entry=new Entry();
				_entry.key=null;
			}

			public void Reset()
			{
			}

			public void Dispose()
			{
			}

			public bool MoveNext()
			{
				if(_entry.key!=null && _entry.key!=_tSet[_index])
				{
					++_index;
				}

				string key;

				if(_index<=_tSafeIndex)
				{
					while(--_index >= 0)
					{
						if((key=_tSet[_index])!=null)
						{
							_entry.key=key;
							_entry.value=_tValues[_index];
							return true;
						}
					}

					_entry.key=null;
					_index=_tSet.Length;
					return MoveNext();
				}
				else
				{
					while(--_index > _tSafeIndex)
					{
						if((key=_tSet[_index])!=null)
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