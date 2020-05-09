package com.home.shine.support.collection;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.inter.IObjectConsumer;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.Function;

/** 默认HashMap */
public class SMap<K,V> extends BaseHash implements Iterable<V>
{
	protected Object[] _table;
	
	private EntrySet _entrySet;
	
	public SMap()
	{
	
	}
	
	public SMap(int capacity)
	{
		init(countCapacity(capacity));
	}
	
	private void checkInit()
	{
		if(_table!=null)
			return;
		
		init(_minSize);
	}
	
	/** 获取表 */
	public final Object[] getTable()
	{
		checkInit();
		
		return _table;
	}

	protected void init(int capacity)
	{
		_maxSize=capacity;

		_table=new Object[capacity << 2];
	}

	public void put(K key,V value)
	{
		if(key==null)
		{
			Ctrl.throwError("key不能为空");
			return;
		}
		
		checkInit();
		
		int index=insert(key,value);
		
		if(index<0)
		{
			return;
		}
		else
		{
			_table[index + 1]=value;
			return;
		}
	}
	
	protected int insert(K key,V value)
	{
		Object[] tab=_table;
		int capacityMask;
		int index;
		Object cur;
		if((cur=tab[(index=hashObj(key) & (capacityMask=(tab.length) - 2))])!=null)
		{
			if(cur==key || key.equals(cur))
			{
				return index;
			}
			else
			{
				while(true)
				{
					if((cur=tab[(index=(index - 2) & capacityMask)])==null)
					{
						break;
					}
					else if(cur==key || key.equals(cur))
					{
						return index;
					}
				}
			}
		}
		tab[index]=key;
		tab[(index + 1)]=value;
		postInsertHook(index);
		return -1;
	}
	
	@Override
	protected int toGetLastFreeIndex()
	{
		Object[] table=_table;
		
		for(int i=table.length - 2;i >= 0;i-=2)
		{
			if(table[i]==null)
			{
				return i;
			}
		}
		
		getLastFreeIndexError();
		
		return -1;
	}
	
	@Override
	protected void rehash(int newCapacity)
	{
		super.rehash(newCapacity);
		
		Object[] tab=_table;
		
		init(newCapacity);
		
		Object[] newTab=_table;
		int capacityMask=(newTab.length) - 2;
		for(int i=(tab.length) - 2;i >= 0;i-=2)
		{
			K key;
			if((key=((K)tab[i]))!=null)
			{
				int index;
				if(((K)(newTab[(index=hashObj(key) & capacityMask)]))!=null)
				{
					while(true)
					{
						if(((K)(newTab[(index=(index - 2) & capacityMask)]))==null)
						{
							break;
						}
					}
				}
				newTab[index]=key;
				newTab[(index + 1)]=((V)(tab[(i + 1)]));
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public V get(K key)
	{
		if(key==null)
		{
			Ctrl.throwError("key不能为空");
			return null;
		}
		
		if(_size==0)
			return null;
		
		int index=index(key);
		
		if(index >= 0)
		{
			return ((V)(_table[(index + 1)]));
		}
		else
		{
			return null;
		}
	}
	
	protected int index(K key)
	{
		Object[] tab=_table;
		int capacityMask;
		int index;
		Object cur;
		if((cur=tab[(index=hashObj(key) & (capacityMask=(tab.length) - 2))])==key)
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
				if(key.equals(cur))
				{
					return index;
				}
				else
				{
					while(true)
					{
						if((cur=tab[(index=(index - 2) & capacityMask)])==key)
						{
							return index;
						}
						else if(cur==null)
						{
							return -1;
						}
						else if(key.equals(cur))
						{
							return index;
						}
					}
				}
			}
		}
	}
	
	public boolean contains(K key)
	{
		if(key==null)
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
		
		int index=index(key);
		
		if(index >= 0)
		{
			return (V)_table[index+1];
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
			return null;
		
		Object[] table=_table;
		
		for(int i=table.length - 2;i >= 0;i-=2)
		{
			if(table[i]!=null)
			{
				return (V)table[i + 1];
			}
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public V remove(K key)
	{
		if(key==null)
		{
			Ctrl.throwError("key不能为空");
			return null;
		}
		
		if(_size==0)
			return null;
		
		Object[] tab=_table;
		int capacityMask=(tab.length) - 2;
		int index;
		Object cur;
		
		if((cur=tab[(index=hashObj(key) & capacityMask)])!=key)
		{
			if(cur==null)
			{
				return null;
			}
			else
			{
				if(!key.equals(cur))
				{
					while(true)
					{
						if((cur=tab[(index=(index - 2) & capacityMask)])==key)
						{
							break;
						}
						else if(cur==null)
						{
							return null;
						}
						else if(key.equals(cur))
						{
							break;
						}
					}
				}
			}
		}
		
		V val=((V)(tab[(index + 1)]));
		int indexToRemove=index;
		int indexToShift=indexToRemove;
		int shiftDistance=2;
		while(true)
		{
			indexToShift=(indexToShift - 2) & capacityMask;
			Object keyToShift;
			if((keyToShift=tab[indexToShift])==null)
			{
				break;
			}
			K castedKeyToShift=((K)(keyToShift));
			if(((hashObj(castedKeyToShift) - indexToShift) & capacityMask) >= shiftDistance)
			{
				tab[indexToRemove]=castedKeyToShift;
				tab[(indexToRemove + 1)]=((V)(tab[(indexToShift + 1)]));
				indexToRemove=indexToShift;
				shiftDistance=2;
			}
			else
			{
				shiftDistance+=2;
			}
		}
		tab[indexToRemove]=null;
		tab[(indexToRemove + 1)]=null;
		postRemoveHook(indexToRemove);
		return val;
	}
	
	/** 清空 */
	public void clear()
	{
		if(_size==0)
			return;
		
		justClearSize();
		
		Arrays.fill(_table,null);
	}
	
	/** 扩容 */
	public final void ensureCapacity(int capacity)
	{
		if(capacity>_maxSize)
		{
			int t=countCapacity(capacity);
			
			if(_table==null)
			{
				init(t);
			}
			else if(t>(_table.length >> 1))
			{
				rehash(t);
			}
		}
	}
	
	public SMap<K,V> clone()
	{
		if(_size==0)
			return new SMap<>();
		
		SMap<K,V> re=new SMap<>(capacity());
		System.arraycopy(_table,0,re._table,0,_table.length);
		re.copyBase(this);
		return re;
	}
	
	/** 没有就赋值(成功添加返回null,否则返回原值) */
	@SuppressWarnings("unchecked")
	public V putIfAbsent(K key,V value)
	{
		if(key==null)
		{
			Ctrl.throwError("key不能为空");
			return null;
		}
		
		checkInit();
		
		int index=insert(key,value);
		
		if(index<0)
		{
			return null;
		}
		else
		{
			return (V)_table[index + 1];
		}
	}
	
	@SuppressWarnings("unchecked")
	public V computeIfAbsent(K key,Function<? super K,? extends V> mappingFunction)
	{
		if(mappingFunction==null)
		{
			return null;
		}
		
		if(key==null)
		{
			Ctrl.throwError("key不能为空");
			return null;
		}
		
		checkInit();
		
		Object[] tab;
		int index;
		Object val;
		label59:
		{
			tab=this._table;
			int capacityMask;
			Object cur;
			if((cur=tab[index=hashObj(key) & (capacityMask=tab.length - 2)])!=key)
			{
				if(cur==null)
				{
					break label59;
				}

				if(!key.equals(cur))
				{
					while((cur=tab[index=index - 2 & capacityMask])!=key)
					{
						if(cur==null)
						{
							break label59;
						}

						if(key.equals(cur))
						{
							break;
						}
					}
				}
			}

			if((val=tab[index + 1])!=null)
			{
				return (V)val;
			}

			Object value=mappingFunction.apply(key);
			if(value!=null)
			{
				tab[index + 1]=value;
				return (V)value;
			}

			return null;
		}

		val=mappingFunction.apply(key);
		
		if(val!=null)
		{
			tab[index]=key;
			tab[index + 1]=val;
			this.postInsertHook(index);
			return (V)val;
		}
		else
		{
			return null;
		}
	}
	
	public void putAll(SMap<K,V> dic)
	{
		if(dic.isEmpty())
			return;
		
		Object[] table=_table;
		Object key;
		
		for(int i=table.length - 2;i >= 0;i-=2)
		{
			if((key=table[i])!=null)
			{
				put((K)key,(V)table[i + 1]);
			}
		}
	}
	
	/** 获取排序好的List */
	public SList<K> getSortedKeyList()
	{
		SList<K> list=new SList<>(size());
		
		if(_size==0)
			return list;
		
		Object[] values=list.getValues();
		int j=0;
		
		Object[] table=_table;
		Object key;
		
		for(int i=table.length - 2;i >= 0;i-=2)
		{
			if((key=table[i])!=null)
			{
				values[j++]=key;
			}
		}
		
		list.justSetSize(size());
		
		list.sort();
		
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public void forEach(BiConsumer<? super K,? super V> consumer)
	{
		if(_size==0)
			return;
		
		int version=_version;
		Object[] table=_table;
		Object key;
		
		for(int i=table.length - 2;i >= 0;i-=2)
		{
			if((key=table[i])!=null)
			{
				consumer.accept((K)key,(V)table[i + 1]);
			}
		}
		
		if(version!=_version)
		{
			Ctrl.throwError("ForeachModificationException");
		}
	}
	
	/** 可修改遍历 */
	public void forEachS(BiConsumer<? super K,? super V> consumer)
	{
		if(_size==0)
		{
			return;
		}
		
		Object[] table=_table;
		Object key;
		int safeIndex=getLastFreeIndex();
		
		for(int i=safeIndex-2;i>=0;i-=2)
		{
			if((key=table[i])!=null)
			{
				consumer.accept((K)key,(V)table[i + 1]);
				
				if(key!=table[i])
				{
					i+=2;
				}
			}
		}
		
		for(int i=table.length-2;i>safeIndex;i-=2)
		{
			if((key=table[i])!=null)
			{
				consumer.accept((K)key,(V)table[i + 1]);
				
				if(key!=table[i])
				{
					i+=2;
				}
			}
		}
	}
	
	/** 遍历值 */
	public void forEachValue(IObjectConsumer<? super V> consumer)
	{
		if(_size==0)
		{
			return;
		}
		
		int version=_version;
		Object[] table=_table;
		
		for(int i=table.length - 2;i >= 0;i-=2)
		{
			if(table[i]!=null)
			{
				consumer.accept((V)table[i + 1]);
			}
		}
		
		if(version!=_version)
		{
			Ctrl.throwError("ForeachModificationException");
		}
	}
	
	/** 可修改遍历值 */
	@SuppressWarnings("unchecked")
	public void forEachValueS(IObjectConsumer<? super V> consumer)
	{
		if(_size==0)
		{
			return;
		}
		
		Object[] table=_table;
		Object key;
		int safeIndex=getLastFreeIndex();
		
		for(int i=safeIndex-2;i>=0;i-=2)
		{
			if((key=table[i])!=null)
			{
				consumer.accept((V)table[i + 1]);
				
				if(key!=table[i])
				{
					i+=2;
				}
			}
		}
		
		for(int i=table.length-2;i>safeIndex;i-=2)
		{
			if((key=table[i])!=null)
			{
				consumer.accept((V)table[i + 1]);
				
				if(key!=table[i])
				{
					i+=2;
				}
			}
		}
	}
	
	@Override
	public Iterator<V> iterator()
	{
		return new ForEachIterator();
	}
	
	private class ForEachIterator extends BaseIterator implements Iterator<V>
	{
		private Object[] _tTable;
		private K _k;
		private V _v;
		
		public ForEachIterator()
		{
			_tTable=_table;
		}
		
		@Override
		public boolean hasNext()
		{
			if(_k!=null && _k!=_table[_index])
			{
				_index+=2;
			}
			
			Object key;
			
			if(_index<=_tSafeIndex)
			{
				while((_index-=2) >= 0)
				{
					if((key=_tTable[_index])!=null)
					{
						_k=(K)key;
						_v=(V)_tTable[_index + 1];
						return true;
					}
				}
				
				_k=null;
				_index=_tTable.length;
				return hasNext();
			}
			else
			{
				
				while((_index-=2) > _tSafeIndex)
				{
					if((key=_tTable[_index])!=null)
					{
						_k=(K)key;
						_v=(V)_tTable[_index + 1];
						return true;
					}
				}
				
				_v=null;
				return false;
			}
		}
		
		@Override
		public V next()
		{
			return _v;
		}

		@Override
		public void remove()
		{
			SMap.this.remove(_k);
		}
	}
	
	public EntrySet entrySet()
	{
		if(_entrySet==null)
			_entrySet=new EntrySet();
		
		return _entrySet;
	}
	
	public class Entry<K,V>
	{
		public K key;
		public V value;
	}
	
	public class EntrySet implements Iterable<Entry<K,V>>
	{
		@Override
		public Iterator<Entry<K,V>> iterator()
		{
			return new EntryIterator();
		}
	}
	
	private class EntryIterator extends BaseIterator implements Iterator<Entry<K,V>>
	{
		private Object[] _tTable;
		private Entry<K,V> _entry;
		
		public EntryIterator()
		{
			_tTable=_table;
			_entry=new Entry<>();
		}
		
		@Override
		public boolean hasNext()
		{
			if(_entry.key!=null && _entry.key!=_table[_index])
			{
				_index+=2;
			}
			
			Object key;
			
			if(_index<=_tSafeIndex)
			{
				while((_index-=2) >= 0)
				{
					if((key=_tTable[_index])!=null)
					{
						_entry.key=(K)key;
						_entry.value=(V)_tTable[_index + 1];
						return true;
					}
				}
				
				_entry.key=null;
				_index=_tTable.length;
				return hasNext();
			}
			else
			{
				
				while((_index-=2) > _tSafeIndex)
				{
					if((key=_tTable[_index])!=null)
					{
						_entry.key=(K)key;
						_entry.value=(V)_tTable[_index + 1];
						return true;
					}
				}
				
				_entry.key=null;
				_entry.value=null;
				return false;
			}
		}
		
		@Override
		public Entry<K,V> next()
		{
			return _entry;
		}
		
		@Override
		public void remove()
		{
			SMap.this.remove(_entry.key);
		}
	}
}
