using System;
using System.Collections;
using System.Collections.Generic;

namespace ShineEngine
{
	public class CharObjectMap<V>:BaseHash,IEnumerable<V>
	{
		private char _freeValue;

		private char[] _set;

		private V[] _values;

		private EntrySet _entrySet;

		public CharObjectMap()
		{
			init(_minSize);
		}

		public CharObjectMap(int capacity)
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

		public V[] getValues()
		{
			return _values;
		}

		protected override void init(int capacity)
		{
			_capacity=capacity;

			_set=new char[capacity<<1];

			if(_freeValue!=0)
			{
				ObjectUtils.arrayFill(_set,_freeValue);
			}

			_values=new V[capacity<<1];
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

		private char changeFree()
		{
			char newFree=findNewFreeOrRemoved();

			ObjectUtils.arrayReplace(_set,_freeValue,newFree);

			_freeValue=newFree;
			return newFree;
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
			V[] vals=_values;
			init(newCapacity);
			char[] newKeys=_set;
			int capacityMask=(newKeys.Length) - 1;
			V[] newVals=_values;
			for(int i=(keys.Length) - 1;i>=0;i--)
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
					newVals[index]=vals[i];
				}
			}
		}

		private int insert(char key,V value)
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

		public void put(char key,V value)
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

		/** 是否存在 */
		public bool contains(char key)
		{
			if(_size==0)
				return false;
			return index(key)>=0;
		}

		public V get(char key)
		{
			if(_size==0)
				return default(V);

			int idx=index(key);
			if(idx>=0)
			{
				return _values[idx];
			}
			else
			{
				return default(V);
			}
		}

		public V this[char key]
		{
			get {return this.get(key);}
			set {this.put(key,value);}
		}


		public V getOrDefault(char key,V defaultValue)
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

		public V remove(char key)
		{
			if(_size==0)
				return default(V);

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
						return default(V);
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
					char keyToShift;
					if((keyToShift=keys[indexToShift])==free)
					{
						break;
					}

					if(((hashChar(keyToShift) - indexToShift) & capacityMask)>=shiftDistance)
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
				vals[indexToRemove]=default(V);
				postRemoveHook(indexToRemove);

				return val;
			}
			else
			{
				return default(V);
			}
		}

		/** 清空 */
		public override void clear()
		{
			if(_size==0)
				return;

			base.clear();

			char fv=_freeValue;
			char[] set=_set;
			V[] values=_values;

			for(int i=set.Length - 1;i>=0;--i)
			{
				set[i]=fv;
				values[i]=default(V);
			}
		}

		public V putIfAbsent(char key,V value)
		{
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

		public V computeIfAbsent(char key,Func<int,V> mappingFunction)
		{
			int free;
			if(key==(free=_freeValue))
			{
				free=changeFree();
			}
			char[] keys=_set;
			V[] vals=_values;
			int capacityMask;
			int index;
			char cur;

			if((cur=keys[(index=hashChar(key) & (capacityMask=(keys.Length) - 1))])!=key)
			{
				bool goOn=true;

				if(cur!=free)
				{
					while(true)
					{
						if((cur=keys[(index=(index - 1) & capacityMask)])==key)
						{
							goOn=false;
							break;
						}
						else if(cur==free)
						{
							break;
						}
					}
				}

				if(goOn)
				{
					V value=mappingFunction(key);

					if(value!=null)
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

			if((val=vals[index])!=null)
			{
				return val;
			}
			else
			{
				V value=mappingFunction(key);
				if(value!=null)
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

		public CharObjectMap<V> clone()
		{
			if(_size==0)
				return new CharObjectMap<V>();

			CharObjectMap<V> re=new CharObjectMap<V>(capacity());
			//双拷贝
			Array.Copy(_set,0,re._set,0,_set.Length);
			Array.Copy(_values,0,re._values,0,_values.Length);
			re.copyBase(this);
			re._freeValue=_freeValue;

			return re;
		}

		/** 获取key对应set */
		public CharSet getKeySet()
		{
			if(_size==0)
				return new CharSet();

			CharSet re=new CharSet(capacity());
			re.copyBase(this);
			re.setFreeValue(_freeValue);
			Array.Copy(_set,0,re.getKeys(),0,_set.Length);
			return re;
		}

		/** 获取排序好的List */
		public CharList getSortedKeyList()
		{
			CharList list=new CharList(size());

			if(_size==0)
				return list;

			char[] values=list.getValues();
			int j=0;

			char free=_freeValue;
			char[] keys=_set;
			for(int i=(keys.Length) - 1;i >= 0;--i)
			{
				char key;
				if((key=keys[i])!=free)
				{
					values[j++]=key;
				}
			}

			list.justSetSize(size());

			list.sort();

			return list;
		}

		/** 遍历 */
		public void forEach(Action<char,V> consumer)
		{
			if(_size==0)
				return;

			int version=_version;
			char free=_freeValue;
			char[] keys=_set;
			V[] vals=_values;
			for(int i=(keys.Length) - 1;i>=0;--i)
			{
				char key;
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
		public void forEachS(Action<char,V> consumer)
		{
			if(_size==0)
				return;

			char free=_freeValue;
			char[] keys=_set;
			V[] vals=_values;
			char key;
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

		/** 遍历值(null的不传) */
		public void forEachValue(Action<V> consumer)
		{
			if(_size==0)
				return;

			int version=_version;
			V[] vals=_values;
			V v;
			for(int i=vals.Length - 1;i>=0;--i)
			{
				if((v=vals[i])!=null)
				{
					consumer(v);
				}
			}

			if(version!=_version)
			{
				Ctrl.throwError("ForeachModificationException");
			}
		}

		/** 可修改遍历值(null的不传) */
		public void forEachValueS(Action<V> consumer)
		{
			if(_size==0)
				return;

			char free=_freeValue;
			char[] keys=_set;
			V[] vals=_values;
			char key;
			int safeIndex=getLastFreeIndex();

			for(int i=safeIndex-1;i>=0;--i)
			{
				if((key=keys[i])!=free)
				{
					consumer(vals[i]);

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
					consumer(vals[i]);

					if(key!=keys[i])
					{
						++i;
					}
				}
			}
		}

		/** 获取值组 */
		public SList<V> getValueList()
		{
			SList<V> list=new SList<V>(_size);

			if(_size==0)
				return list;

			V[] vals=_values;
			V v;
			for(int i=vals.Length - 1;i>=0;--i)
			{
				if((v=vals[i])!=null)
				{
					list.add(v);
				}
			}

			return list;
		}

		/** 获取值数组 */
		public V[] getValueArr()
		{
			V[] re=new V[_size];

			if(_size==0)
				return re;

			int j=0;

			V[] vals=_values;
			V v;
			for(int i=vals.Length - 1;i>=0;--i)
			{
				if((v=vals[i])!=null)
				{
					re[j++]=v;
				}
			}

			return re;
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

			private char _tFv;
			private char[] _tSet;
			private V[] _tValues;
			private char _k;
			private V _v;

			public ForEachIterator(CharObjectMap<V> map)
			{
				_index=_tSafeIndex=map.getLastFreeIndex();
				if(map._size==0)
					_index=_tSafeIndex+1;

				_k=_tFv=map._freeValue;
				_tSet=map._set;
				_tValues=map._values;
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
							_v=_tValues[_index];
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

		public struct Entry<V>
		{
			public char key;
			public V value;
		}

		public class EntrySet:IEnumerable<Entry<V>>
		{
			private CharObjectMap<V> _map;

			public EntrySet(CharObjectMap<V> map)
			{
				_map=map;
			}

			public EntryIterator GetEnumerator()
			{
				return new EntryIterator(_map);
			}

			IEnumerator<Entry<V>> IEnumerable<Entry<V>>.GetEnumerator()
			{
				return new EntryIterator(_map);
			}

			IEnumerator IEnumerable.GetEnumerator()
			{
				return new EntryIterator(_map);
			}
		}

		public struct EntryIterator:IEnumerator<Entry<V>>
		{
			private int _index;
			private int _tSafeIndex;

			private char _tFv;
			private char[] _tSet;
			private V[] _tValues;
			private Entry<V> _entry;

			public EntryIterator(CharObjectMap<V> map)
			{
				_index=_tSafeIndex=map.getLastFreeIndex();
				if(map._size==0)
					_index=_tSafeIndex+1;

				_tSet=map._set;
				_tValues=map._values;
				_entry=new Entry<V>();
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

				char key;

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

					_entry.value=default(V);
					return false;
				}
			}

			public Entry<V> Current
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