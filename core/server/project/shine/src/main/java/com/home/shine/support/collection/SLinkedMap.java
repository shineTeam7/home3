package com.home.shine.support.collection;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.collection.inter.IObjectConsumer;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class SLinkedMap<K,V> extends BaseHash implements Iterable<V>
{
	private SList<Node<K,V>> _pool=new SList<>(Node[]::new);
	
	private Node<K,V>[] _values;
	
	private Node<K,V> _head;
	
	private Node<K,V> _tail;
	
	private EntrySet _entrySet;
	
	public SLinkedMap()
	{
		this(_minSize);
	}
	
	public SLinkedMap(int capacity)
	{
		init(countCapacity(capacity));
	}
	
	@Override
	protected void init(int capacity)
	{
		_capacity=capacity;
		
		_values=new Node[capacity<<1];
	}
	
	private Node<K,V> createNode()
	{
		if(!_pool.isEmpty())
		{
			return _pool.pop();
		}
		
		return new Node<>();
	}
	
	private int index(K key)
	{
		Node[] nodes=_values;
		int capacityMask;
		int index;
		Node cur;
		
		if((cur=nodes[(index=hashObj(key) & (capacityMask=(nodes.length) - 1))])!=null && cur.key==key)
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
				if(key.equals(cur.key))
				{
					return index;
				}
				else
				{
					while(true)
					{
						if((cur=nodes[(index=(index - 1) & capacityMask)])!=null && cur.key==key)
						{
							return index;
						}
						else if(cur==null)
						{
							return -1;
						}
						else if(key.equals(cur.key))
						{
							return index;
						}
					}
				}
			}
		}
	}
	
	@Override
	protected void rehash(int newCapacity)
	{
		super.rehash(newCapacity);
		
		Node[] nodes=_values;
		init(newCapacity);
		Node[] newNodes=_values;
		Node cur;
		int capacityMask=(newNodes.length) - 1;
		for(int i=(nodes.length) - 1;i >= 0;i--)
		{
			if((cur=nodes[i])!=null)
			{
				int index;
				if((newNodes[(index=(hashObj(cur.key) & capacityMask))])!=null)
				{
					while(true)
					{
						if((newNodes[(index=(index - 1) & capacityMask)])==null)
						{
							break;
						}
					}
				}
				newNodes[index]=cur;
			}
		}
	}
	
	private void addToList(Node node)
	{
		if(_head==null)
		{
			_tail=node;
			_head=node;
		}
		else
		{
			_tail.next=node;
			node.prev=_tail;
			_tail=node;
		}
	}
	
	private void deleteOne(Node node)
	{
		removeFromList(node);
		node.key=0;
		node.value=null;
		_pool.add(node);
	}
	
	private void removeFromList(Node node)
	{
		if(node.prev!=null)
		{
			if(ShineSetting.openCheck)
			{
				if(node.prev.next!=node)
				{
					Ctrl.errorLog("链表节点不对");
				}
			}
			
			node.prev.next=node.next;
		}
		
		if(node.next!=null)
		{
			if(ShineSetting.openCheck)
			{
				if(node.next.prev!=node)
				{
					Ctrl.errorLog("链表节点不对2");
				}
			}
			
			node.next.prev=node.prev;
		}
		
		if(node==_tail)
		{
			_tail=node.prev;
		}
		
		if(node==_head)
		{
			_head=node.next;
		}
		
		node.prev=null;
		node.next=null;
	}
	
	private Node addNewNode(K key,V value)
	{
		Node node=createNode();
		node.key=key;
		node.value=value;
		addToList(node);
		
		return node;
	}
	
	private int insert(K key,V value)
	{
		Node[] keys=_values;
		int capacityMask;
		int index;
		Node cur;
		keyAbsent:
		if((cur=keys[(index=hashObj(key) & (capacityMask=(keys.length) - 1))])!=null)
		{
			if(cur.key==key || key.equals(cur.key))
			{
				return index;
			}
			else
			{
				while(true)
				{
					if((cur=keys[(index=(index - 1) & capacityMask)])==null)
					{
						break keyAbsent;
					}
					else if(cur.key==key || key.equals(cur.key))
					{
						return index;
					}
				}
			}
		}
		
		keys[index]=addNewNode(key,value);
		postInsertHook(index);
		return -1;
	}
	
	public void put(K key,V value)
	{
		int index=insert(key,value);
		
		if(index<0)
		{
			return;
		}
		else
		{
			_values[index].value=value;
			return;
		}
	}
	
	/** 添加或移动到末尾 */
	public void putOrMoveToTail(K key,V value)
	{
		int index=insert(key,value);
		
		if(index<0)
		{
			return;
		}
		else
		{
			Node<K,V> node=_values[index];
			node.value=value;
			
			//不是末尾
			if(node!=_tail)
			{
				removeFromList(node);
				addToList(node);
			}
			
			return;
		}
	}
	
	/** 是否存在 */
	public boolean contains(K key)
	{
		if(_size==0)
			return false;
		
		return index(key) >= 0;
	}
	
	public V get(K key)
	{
		if(_size==0)
			return null;
		
		int index=index(key);
		if(index >= 0)
		{
			return _values[index].value;
		}
		else
		{
			return null;
		}
	}
	
	public V getAndMoveToTail(K key)
	{
		if(_size==0)
			return null;
		
		int index=index(key);
		if(index >= 0)
		{
			Node<K,V> node=_values[index];
			
			//不是末尾
			if(node!=_tail)
			{
				removeFromList(node);
				addToList(node);
			}
			
			return node.value;
		}
		else
		{
			return null;
		}
	}
	
	public V getOrDefault(K key,V defaultValue)
	{
		if(_size==0)
			return defaultValue;
		
		int index=index(key);
		
		if(index >= 0)
		{
			return _values[index].value;
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
		
		Node[] vals=_values;
		Node v;
		for(int i=vals.length - 1;i >= 0;--i)
		{
			if((v=vals[i])!=null)
			{
				return (V)v.value;
			}
		}
		
		return null;
	}
	
	public V remove(K key)
	{
		if(_size==0)
			return null;
		
		Node[] keys=_values;
		int capacityMask=(keys.length) - 1;
		int index;
		Node cur;
		keyPresent:
		if((cur=keys[(index=hashObj(key) & capacityMask)])==null || cur.key!=key)
		{
			if(cur==null)
			{
				return null;
			}
			else
			{
				if(!key.equals(cur.key))
				{
					while(true)
					{
						if((cur=keys[(index=(index - 1) & capacityMask)])!=null && cur.key==key)
						{
							break keyPresent;
						}
						else if(cur==null)
						{
							return null;
						}
						else if(key.equals(cur.key))
						{
							break keyPresent;
						}
					}
				}
				
			}
		}
		
		V re=(V)cur.value;
		
		int indexToRemove=index;
		int indexToShift=indexToRemove;
		int shiftDistance=1;
		while(true)
		{
			indexToShift=(indexToShift - 1) & capacityMask;
			Node keyToShift;
			if((keyToShift=keys[indexToShift])==null)
			{
				break;
			}
			if(((hashObj(keyToShift.key) - indexToShift) & capacityMask) >= shiftDistance)
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
					throw new ConcurrentModificationException();
				}
			}
		}
		
		keys[indexToRemove]=null;
		deleteOne(cur);
		postRemoveHook(indexToRemove);
		return re;
	}
	
	/** 将某key的值移到末尾 */
	public void moveToTail(K key)
	{
		int index=index(key);
		
		if(index >= 0)
		{
			Node<K,V> node=_values[index];
			
			//不是末尾
			if(node!=_tail)
			{
				removeFromList(node);
				addToList(node);
			}
		}
	}
	
	/** 清空 */
	public void clear()
	{
		if(_size==0)
		{
			return;
		}
		
		super.clear();
		
		Node[] values=_values;
		
		for(int i=_values.length - 1;i >= 0;--i)
		{
			deleteOne(values[i]);
			values[i]=null;
		}
	}
	
	public V putIfAbsent(K key,V value)
	{
		int index=insert(key,value);
		
		if(index<0)
		{
			return null;
		}
		else
		{
			return _values[index].value;
		}
	}
	
	public V computeIfAbsent(K key,Function<? super K,? extends V> mappingFunction)
	{
		if(mappingFunction==null)
		{
			throw new NullPointerException();
		}
		
		Node[] keys=_values;
		int capacityMask;
		int index;
		Node cur;
		keyPresent:
		if((cur=keys[(index=hashObj(key) & (capacityMask=(keys.length) - 1))])==null || cur.key!=key)
		{
			if(cur!=null)
			{
				if(!key.equals(cur.key))
				{
					while(true)
					{
						if((cur=keys[(index=(index - 1) & capacityMask)])!=null && cur.key==key)
						{
							break keyPresent;
						}
						else if(cur==null)
						{
							break;
						}
						else if(key.equals(cur.key))
						{
							break keyPresent;
						}
					}
				}
				
			}
			
			V value=mappingFunction.apply(key);
			
			if(value!=null)
			{
				keys[index]=addNewNode(key,value);
				postInsertHook(index);
				return value;
			}
			else
			{
				return null;
			}
		}
		
		
		V value=mappingFunction.apply(key);
		keys[index].value=value;
		return value;
	}
	
	public LongObjectMap<V> clone()
	{
		Ctrl.throwError("not support");
		return null;
	}
	
	/** 获取头对象 */
	public Node<K,V> getHead()
	{
		return _head;
	}
	
	/** 遍历 */
	public void forEach(BiConsumer<? super K,? super V> consumer)
	{
		if(_size==0)
		{
			return;
		}
		
		if(_head==null)
			return;
		
		int version=_version;
		Node node=_head;
		
		while(node!=null)
		{
			consumer.accept((K)node.key,(V)node.value);
			node=node.next;
		}
		
		if(version!=_version)
		{
			Ctrl.throwError("ForeachModificationException");
		}
	}
	
	/** 遍历 */
	public void forEachS(BiConsumer<? super K,? super V> consumer)
	{
		if(_size==0)
		{
			return;
		}
		
		if(_head==null)
			return;
		
		Node node=_head;
		Node next;
		
		while(node!=null)
		{
			next=node.next;
			consumer.accept((K)node.key,(V)node.value);
			node=next;
		}
	}
	
	/** 遍历值(null的不传) */
	public void forEachValue(IObjectConsumer<? super V> consumer)
	{
		if(_size==0)
		{
			return;
		}
		
		int version=_version;
		Node node=_head;
		
		while(node!=null)
		{
			consumer.accept((V)node.value);
			node=node.next;
		}
		
		if(version!=_version)
		{
			Ctrl.throwError("ForeachModificationException");
		}
		
	}
	
	/** 遍历值(null的不传) */
	public void forEachValueS(IObjectConsumer<? super V> consumer)
	{
		if(_size==0)
		{
			return;
		}
		
		Node node=_head;
		Node next;
		
		while(node!=null)
		{
			next=node.next;
			consumer.accept((V)node.value);
			node=next;
		}
	}
	
	public static class Node<K,V>
	{
		public Node prev;
		
		public Node next;
		
		public K key;
		
		public V value;
	}
	
	@Override
	public Iterator<V> iterator()
	{
		return new ForEachIterator();
	}
	
	private class ForEachIterator implements Iterator<V>
	{
		private Node<K,V> _node;
		private Node<K,V> _current;
		
		public ForEachIterator()
		{
			super();
			if(_size==0)
				return;
			
			_node=_head;
		}
		
		@Override
		public boolean hasNext()
		{
			if(_node==null)
				return false;
			
			_current=_node;
			_node=_node.next;
			
			return true;
		}
		
		@Override
		public V next()
		{
			return _current.value;
		}
		
		@Override
		public void remove()
		{
			SLinkedMap.this.remove(_current.key);
		}
	}
	
	public EntrySet entrySet()
	{
		if(_entrySet!=null)
			return _entrySet;
		
		return _entrySet=new EntrySet();
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
	
	private class EntryIterator implements Iterator<Entry<K,V>>
	{
		private Node<K,V> _node;
		private Entry<K,V> _entry=new Entry<>();
		
		public EntryIterator()
		{
			super();
			if(_size==0)
				return;
			
			_node=_head;
		}
		
		@Override
		public boolean hasNext()
		{
			if(_node==null)
			{
				_entry.key=null;
				_entry.value=null;
				return false;
			}
			
			_entry.key=_node.key;
			_entry.value=_node.value;
			
			_node=_node.next;
			return true;
		}
		
		@Override
		public Entry<K,V> next()
		{
			return _entry;
		}
		
		@Override
		public void remove()
		{
			SLinkedMap.this.remove(_entry.key);
		}
	}
}
