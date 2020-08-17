using System.Collections.Generic;
using System;
using System.Collections;

namespace ShineEngine
{
	/// <summary>
	/// 字典
	/// </summary>
	public class SMap<K,V>:BaseHash,IEnumerable<V>
	{
		private K _defaultKey;

		private V _defaultValue;

		private K[] _set;

		private V[] _values;

		private EntrySet _entrySet;

		public SMap()
		{
			_defaultKey=default(K);
			_defaultValue=default(V);
			init(_minSize);
		}

		public SMap(int capacity)
		{
			_defaultKey=default(K);
			_defaultValue=default(V);
			init(countCapacity(capacity));
		}

		/** 获取表 */
		public K[] getKeys()
		{
			return _set;
		}

		/** 获取表 */
		public V[] getValues()
		{
			return _values;
		}

		protected override void init(int capacity)
		{
			_capacity=capacity;

			_set=new K[capacity<<1];
			_values=new V[capacity<<1];
		}

		public void put(K key,V value)
		{
			if(Equals(key,_defaultKey))
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

		protected int insert(K key,V value)
		{
			K[] keys=_set;
			int capacityMask;
			int index;
			K cur;

			if(!Equals(_defaultKey,cur=keys[(index=hashObj(key) & (capacityMask=(keys.Length) - 1))]))
			{
				if(cur.Equals(key))
				{
					return index;
				}
				else
				{
					while(true)
					{
						if(Equals(_defaultKey,cur=keys[(index=(index - 1) & capacityMask)]))
						{
							break;
						}
						else if(cur.Equals(key))
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
			base.rehash(newCapacity);

			K[] keys=_set;
			V[] vals=_values;
			init(newCapacity);
			K[] newKeys=_set;
			int capacityMask=(newKeys.Length) - 1;
			V[] newVals=_values;
			for(int i=(keys.Length) - 1;i>=0;i--)
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
					newVals[index]=vals[i];
				}
			}
		}

		public V get(K key)
		{
			if(Equals(_defaultKey,key))
			{
				Ctrl.throwError("key不能为空");
				return default(V);
			}

			if(_size==0)
				return _defaultValue;

			int idx=index(key);

			if(idx >= 0)
			{
				return _values[idx];
			}
			else
			{
				return _defaultValue;
			}
		}

		protected int index(K key)
		{
			if(!Equals(_defaultKey,key))
			{
				K[] keys=_set;
				int capacityMask;
				int index;
				K cur;
				if(key.Equals((cur=keys[(index=hashObj(key) & (capacityMask=(keys.Length) - 1))])))
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
						while(true)
						{
							if(key.Equals((cur=keys[(index=(index - 1) & capacityMask)])))
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
			else
			{
				return -1;
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

		public V getOrDefault(K key,V defaultValue)
		{
			if(_size==0)
				return defaultValue;

			int idx=index(key);

			if(idx >= 0)
			{
				return _values[idx];
			}
			else
			{
				return defaultValue;
			}
		}

		/** 获取任意一个 */
		public V getEver()
		{
			if(_size==0)
				return _defaultValue;

			V[] vals=_values;
			V v;
			for(int i=vals.Length - 1;i >= 0;--i)
			{
				if(!Equals((v=vals[i]),_defaultValue))
				{
					return v;
				}
			}

			return _defaultValue;
		}

		public bool tryGetValue(K key,out V value)
		{
			if(_size==0)
			{
				value=_defaultValue;
				return false;
			}

			int idx=index(key);

			if(idx >= 0)
			{
				value=_values[idx];
				return true;
			}
			else
			{
				value=_defaultValue;
				return false;
			}
		}

		public V remove(K key)
		{
			if(_size==0)
				return _defaultValue;

			K[] keys=_set;
			int capacityMask=(keys.Length) - 1;
			int index;
			K cur;

			if(!key.Equals((cur=keys[(index=hashObj(key) & capacityMask)])))
			{
				if(Equals(_defaultKey,cur))
				{
					return default(V);
				}
				else
				{
					while(true)
					{
						if(key.Equals((cur=keys[(index=(index - 1) & capacityMask)])))
						{
							break;
						}
						else if(Equals(_defaultKey,cur))
						{
							return default(V);
						}
					}
				}
			}

			V[] vals=_values;
			V val=vals[index];
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

			keys[indexToRemove]=default(K);
			vals[indexToRemove]=default(V);
			postRemoveHook(indexToRemove);
			return val;
		}

		/** 清空 */
		public void clear()
		{
			if(_size==0)
				return;

			K[] set=_set;
			V[] values=_values;

			for(int i=set.Length - 1;i>=0;--i)
			{
				set[i]=default(K);
				values[i]=default(V);
			}

			_size=0;
			++_version;
		}

		public V this[K key]
		{
			get {return this.get(key);}
			set {put(key,value);}
		}

		/** 没有就赋值(成功添加返回null,否则返回原值) */
		public V putIfAbsent(K key,V value)
		{
			if(Equals(_defaultKey,key))
			{
				Ctrl.throwError("key不能为空");
				return default(V);
			}

			int index=insert(key,value);

			if(index<0)
			{
				return default(V);
			}
			else
			{
				return _values[index];
			}
		}

		public V computeIfAbsent(K key,Func<K,V> mappingFunction)
		{
			K[] keys=_set;
			V[] vals=_values;
			int capacityMask;
			int index;
			K cur;

			if(!key.Equals((cur=keys[(index=hashObj(key) & (capacityMask=(keys.Length) - 1))])))
			{
				bool goOn=true;

				if(!Equals(_defaultKey,cur))
				{
					while(true)
					{
						if(key.Equals((cur=keys[(index=(index - 1) & capacityMask)])))
						{
							goOn=false;
							break;
						}
						else if(Equals(_defaultKey,cur))
						{
							break;
						}
					}
				}

				if(goOn)
				{
					V value=mappingFunction(key);

					if(!Equals(_defaultValue,value))
					{
						keys[index]=key;
						vals[index]=value;
						postInsertHook(index);
						return value;
					}
					else
					{
						return default(V);
					}
				}
			}

			V val;

			if(!Equals(_defaultValue,val=vals[index]))
			{
				return val;
			}
			else
			{
				V value=mappingFunction(key);
				if(!Equals(_defaultValue,value))
				{
					vals[index]=value;
					return value;
				}
				else
				{
					return default(V);
				}
			}
		}

		public void putAll(SMap<K,V> dic)
		{
			if(dic.isEmpty())
				return;

			K[] keys=_set;
			V[] vals=_values;
			K key;

			for(int i=(keys.Length) - 1;i>=0;--i)
			{
				if(!Equals(_defaultKey,key=keys[i]))
				{
					put(key,vals[i]);
				}
			}
		}

		public K[] getSortedMapKeys()
		{
			K[] re=new K[_size];

			if(_size==0)
				return re;

			K[] keys=_set;
			K key;
			int index=0;

			for(int i=(keys.Length) - 1;i>=0;--i)
			{
				if(!Equals(_defaultKey,key=keys[i]))
				{
					re[index++]=key;
				}
			}

			Array.Sort(re);
			return re;
		}

		public SMap<K,V> clone()
		{
			if(_size==0)
				return new SMap<K,V>();

			SMap<K,V> re=new SMap<K,V>(capacity());

			Array.Copy(_set,0,re._set,0,_set.Length);
			Array.Copy(_values,0,re._values,0,_values.Length);
			re._defaultKey=_defaultKey;
			re._defaultValue=_defaultValue;
			re.copyBase(this);
			return re;
		}

		/** 转化为原生集合 */
		public Dictionary<K,V> toNatureMap()
		{
			Dictionary<K,V> re=new Dictionary<K,V>(size());

			K[] keys=_set;
			V[] vals=_values;
			K key;

			for(int i=(keys.Length) - 1;i>=0;--i)
			{
				if(!Equals(_defaultKey,key=keys[i]))
				{
					re.Add(key,vals[i]);
				}
			}

			return re;
		}

		public void addAll(Dictionary<K,V> map)
		{
			ensureCapacity(map.Count);

			foreach(KeyValuePair<K,V> kv in map)
			{
				this.put(kv.Key,kv.Value);
			}
		}

		public void forEach(Action<K,V> consumer)
		{
			if(_size==0)
				return;

			int version=_version;
			K[] keys=_set;
			V[] vals=_values;
			K key;

			for(int i=(keys.Length) - 1;i>=0;--i)
			{
				if(!Equals(_defaultKey,key=keys[i]))
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
		public void forEachS(Action<K,V> consumer)
		{
			if(_size==0)
				return;

			K[] keys=_set;
			V[] vals=_values;
			K key;
			int safeIndex=getLastFreeIndex();

			for(int i=safeIndex-1;i>=0;--i)
			{
				if(!Equals(_defaultKey,key=keys[i]))
				{
					consumer(key,vals[i]);

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
					consumer(key,vals[i]);

					if(!key.Equals(keys[i]))
					{
						++i;
					}
				}
			}
		}

		/** 遍历值 */
		public void forEachValue(Action<V> consumer)
		{
			if(_size==0)
				return;

			int version=_version;
			V[] vals=_values;
			V v;
			for(int i=vals.Length - 1;i>=0;--i)
			{
				if(!Equals(_defaultValue,v=vals[i]))
				{
					consumer(v);
				}
			}

			if(version!=_version)
			{
				Ctrl.throwError("ForeachModificationException");
			}
		}

		/** 可修改遍历值 */
		public void forEachValueS(Action<V> consumer)
		{
			if(_size==0)
				return;

			K[] keys=_set;
			V[] vals=_values;
			K key;
			int safeIndex=getLastFreeIndex();

			for(int i=safeIndex-1;i>=0;--i)
			{
				if(!Equals(_defaultKey,key=keys[i]))
				{
					consumer(vals[i]);

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
					consumer(vals[i]);

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

		IEnumerator<V> IEnumerable<V>.GetEnumerator()
		{
			return new ForEachIterator(this);
		}

		IEnumerator IEnumerable.GetEnumerator()
		{
			return new ForEachIterator(this);
		}

		public struct ForEachIterator:IEnumerator<V>
		{
			private int _index;
			private int _tSafeIndex;

			private K[] _tSet;
			private V[] _tValues;

			private K _k;
			private V _v;

			public ForEachIterator(SMap<K,V> map)
			{
				_index=_tSafeIndex=map.getLastFreeIndex();
				if(map._size==0)
					_index=_tSafeIndex+1;

				_tSet=map._set;
				_tValues=map._values;
				_k=default(K);
				_v=default(V);
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
							_v=_tValues[_index];
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
							_v=_tValues[_index];
							return true;
						}
					}

					_v=default(V);
					return false;
				}
			}

			public V Current
			{
				get {return _v;}
			}

			object IEnumerator.Current
			{
				get {return _v;}
			}
		}

		public EntrySet entrySet()
		{
			if(_entrySet!=null)
				return _entrySet;

			return _entrySet=new EntrySet(this);
		}

		public struct Entry<K,V>
		{
			public K key;
			public V value;
		}

		public class EntrySet:IEnumerable<Entry<K,V>>
		{
			private SMap<K,V> _map;

			public EntrySet(SMap<K,V> map)
			{
				_map=map;
			}

			public EntryIterator GetEnumerator()
			{
				return new EntryIterator(_map);
			}

			IEnumerator<Entry<K,V>> IEnumerable<Entry<K,V>>.GetEnumerator()
			{
				return new EntryIterator(_map);
			}

			IEnumerator IEnumerable.GetEnumerator()
			{
				return new EntryIterator(_map);
			}
		}

		public struct EntryIterator:IEnumerator<Entry<K,V>>
		{
			private int _index;
			private int _tSafeIndex;

			private K[] _tSet;
			private V[] _tValues;
			private Entry<K,V> _entry;

			public EntryIterator(SMap<K,V> map)
			{
				_index=_tSafeIndex=map.getLastFreeIndex();
				if(map._size==0)
					_index=_tSafeIndex+1;

				_tSet=map._set;
				_tValues=map._values;
				_entry=new Entry<K,V>();
				_entry.key=default(K);
			}

			public void Reset()
			{
			}

			public void Dispose()
			{
			}

			public bool MoveNext()
			{
				if(!Equals(_entry.key,default(K)) && !Equals(_entry.key,_tSet[_index]))
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
							_entry.key=key;
							_entry.value=_tValues[_index];
							return true;
						}
					}

					_entry.key=default(K);
					_index=_tSet.Length;
					return MoveNext();
				}
				else
				{
					while(--_index > _tSafeIndex)
					{
						if(!Equals(key=_tSet[_index],default(K)))
						{
							_entry.key=key;
							_entry.value=_tValues[_index];
							return true;
						}
					}

					_entry.value=default(V);
					return false;
				}
			}

			public Entry<K,V> Current
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