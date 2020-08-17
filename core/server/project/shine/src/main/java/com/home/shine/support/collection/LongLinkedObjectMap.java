package com.home.shine.support.collection;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.collection.inter.ILongObjectConsumer;
import com.home.shine.support.collection.inter.IObjectConsumer;
import com.home.shine.support.func.ObjectLongFunc;

import java.util.ConcurrentModificationException;
import java.util.Iterator;

public class LongLinkedObjectMap<V> extends BaseHash implements Iterable<V>
{
	private SList<Node<V>> _pool=new SList<>(Node[]::new);
	
	private Node<V>[] _values;
	
	private Node<V> _head;
	
	private Node<V> _tail;
	
	private EntrySet _entrySet;
	
	public LongLinkedObjectMap()
	{
		this(_minSize);
	}
	
	public LongLinkedObjectMap(int capacity)
	{
		init(countCapacity(capacity));
	}
	
	@Override
	protected void init(int capacity)
	{
		_capacity=capacity;
		
		_values=new Node[capacity<<1];
	}
	
	private Node<V> createNode()
	{
		if(!_pool.isEmpty())
		{
			return _pool.pop();
		}
		
		return new Node<V>();
	}
	
	private int index(long key)
	{
		Node[] nodes=_values;
		int capacityMask;
		int index;
		Node cur;
		
		if((cur=nodes[(index=hashLong(key) & (capacityMask=(nodes.length) - 1))])!=null && cur.key==key)
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
					if((cur=nodes[(index=(index - 1) & capacityMask)])!=null && cur.key==key)
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
				if((newNodes[(index=(hashLong(cur.key) & capacityMask))])!=null)
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
	
	private Node addNewNode(long key,V value)
	{
		Node node=createNode();
		node.key=key;
		node.value=value;
		addToList(node);
		
		return node;
	}
	
	private int insert(long key,V value)
	{
		Node[] keys=_values;
		int capacityMask;
		int index;
		Node cur;
		keyAbsent:
		if((cur=keys[(index=hashLong(key) & (capacityMask=(keys.length) - 1))])!=null)
		{
			if(cur.key==key)
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
					else if(cur.key==key)
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
	
	public void put(long key,V value)
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
	public void putOrMoveToTail(long key,V value)
	{
		int index=insert(key,value);
		
		if(index<0)
		{
			return;
		}
		else
		{
			Node<V> node=_values[index];
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
	public boolean contains(long key)
	{
		if(_size==0)
			return false;
		
		return index(key) >= 0;
	}
	
	public V get(long key)
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
	
	public V getAndMoveToTail(long key)
	{
		if(_size==0)
			return null;
		
		int index=index(key);
		if(index >= 0)
		{
			Node<V> node=_values[index];
			
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
	
	public V getOrDefault(long key,V defaultValue)
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
	
	public V remove(long key)
	{
		if(_size==0)
			return null;
		
		Node[] keys=_values;
		int capacityMask=(keys.length) - 1;
		int index;
		Node cur;
		keyPresent:
		if((cur=keys[(index=hashLong(key) & capacityMask)])==null || cur.key!=key)
		{
			if(cur==null)
			{
				return null;
			}
			else
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
			if(((hashLong(keyToShift.key) - indexToShift) & capacityMask) >= shiftDistance)
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
	public void moveToTail(long key)
	{
		int index=index(key);
		
		if(index >= 0)
		{
			Node<V> node=_values[index];
			
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
	
	public V putIfAbsent(long key,V value)
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
	
	public V computeIfAbsent(long key,ObjectLongFunc<? extends V> mappingFunction)
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
		if((cur=keys[(index=hashLong(key) & (capacityMask=(keys.length) - 1))])==null || cur.key!=key)
		{
			keyAbsent:
			if(cur!=null)
			{
				while(true)
				{
					if((cur=keys[(index=(index - 1) & capacityMask)])!=null && cur.key==key)
					{
						break keyPresent;
					}
					else if(cur==null)
					{
						break keyAbsent;
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
	public Node<V> getHead()
	{
		return _head;
	}
	
	/** 遍历 */
	public void forEach(ILongObjectConsumer<? super V> consumer)
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
			consumer.accept(node.key,(V)node.value);
			node=node.next;
		}
		
		if(version!=_version)
		{
			Ctrl.throwError("ForeachModificationException");
		}
	}
	
	/** 遍历 */
	public void forEachS(ILongObjectConsumer<? super V> consumer)
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
			consumer.accept(node.key,(V)node.value);
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
	
	public static class Node<V>
	{
		public Node prev;
		
		public Node next;
		
		public long key;
		
		public V value;
	}
	
	@Override
	public Iterator<V> iterator()
	{
		return new ForEachIterator();
	}
	
	private class ForEachIterator implements Iterator<V>
	{
		private Node<V> _node;
		private Node<V> _current;
		
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
			LongLinkedObjectMap.this.remove(_current.key);
		}
	}
	
	public EntrySet entrySet()
	{
		if(_entrySet!=null)
			return _entrySet;
		
		return _entrySet=new EntrySet();
	}
	
	public class Entry<V>
	{
		public long key;
		public V value;
	}
	
	public class EntrySet implements Iterable<Entry<V>>
	{
		@Override
		public Iterator<Entry<V>> iterator()
		{
			return new EntryIterator();
		}
	}
	
	private class EntryIterator implements Iterator<Entry<V>>
	{
		private Node<V> _node;
		private Entry<V> _entry=new Entry<>();
		
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
				_entry.key=0;
				_entry.value=null;
				return false;
			}
			
			_entry.key=_node.key;
			_entry.value=_node.value;
			
			_node=_node.next;
			return true;
		}
		
		@Override
		public Entry<V> next()
		{
			return _entry;
		}
		
		@Override
		public void remove()
		{
			LongLinkedObjectMap.this.remove(_entry.key);
		}
	}
}
