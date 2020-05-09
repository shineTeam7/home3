package com.home.shine.tool;

import com.home.shine.ShineSetup;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.SList;
import com.home.shine.support.collection.SMap;
import com.home.shine.support.func.BooleanCall;
import com.home.shine.support.func.ObjectCall;
import com.home.shine.support.pool.IPoolObject;
import com.home.shine.support.pool.ObjectPool;
import com.home.shine.table.BaseTable;
import com.home.shine.table.DBConnect;

/** 表操作工具 */
public abstract class TableOperateTool<K,T extends BaseTable>
{
	/** 无 */
	private static final int None=0;
	/** 读取过程(load,insert) */
	private static final int Reading=1;
	/** 写入过程(update,delete) */
	private static final int Writing=2;
	
	private ObjectPool<OneNode> _nodePool=new ObjectPool<>(OneNode::new);
	
	private DBConnect _connect;
	/** user表操作队列 */
	private SMap<K,OneNode> _nodeDic=new SMap<>();
	
	public TableOperateTool(DBConnect connect)
	{
		if(connect==null)
		{
			if(!ShineSetup.isExiting())
			{
				Ctrl.throwError("传入的connect为空");
			}
		}
		
		_connect=connect;
	}
	
	/** 构造表 */
	abstract protected T makeTable(K key);
	
	/** 某key是否执行中 */
	public boolean isDoing(K key)
	{
		return _nodeDic.get(key)!=null;
	}
	
	/** 读取 */
	public void load(K key,ObjectCall<T> writeFunc)
	{
		load(key,writeFunc,null);
	}
	
	/** 读取 */
	public void load(K key,ObjectCall<T> writeFunc,Runnable overCall)
	{
		OneNode node=_nodeDic.get(key);
		
		if(node==null)
		{
			_nodeDic.put(key,node=_nodePool.getOne());
			node.key=key;
			node.table=makeTable(key);
		}
		
		WriteOneObj obj=new WriteOneObj();
		obj.writeFunc=writeFunc;
		obj.overCall=overCall;
		
		node.list.add(obj);
		
		checkLoad(node);
	}
	
	/** 插入新节点 */
	public void insert(K key,T table,ObjectCall<T> func)
	{
		if(_nodeDic.contains(key))
		{
			Ctrl.errorLog("出严重错误，已包含节点",key);
			
			if(func!=null)
				func.apply(null);
			
			return;
		}
		
		OneNode node=_nodePool.getOne();
		node.key=key;
		node.table=table;
		
		_nodeDic.put(key,node);
		
		node.state=Reading;
		
		node.table.insert(_connect,b->
		{
			if(!b)
			{
				node.table=null;
			}
			
			if(func!=null)
			{
				func.apply(node.table);
			}
			
			onTableLoad(node);
		});
	}
	
	/** 直接删除当前节点 */
	public void deleteCurrentNode(K key)
	{
		OneNode node=_nodeDic.get(key);
		
		if(node!=null)
		{
			node.isDeleted=true;
		}
	}
	
	/** 删除某主键 */
	public void delete(K key,BooleanCall func)
	{
		OneNode node=_nodeDic.get(key);
		
		//存在
		if(node!=null)
		{
			WriteOneObj obj=new WriteOneObj();
			obj.writeFunc=t->
			{
				if(t==null)
				{
					if(func!=null)
						func.call(false);
					
					return;
				}
				
				OneNode node2=_nodeDic.get(key);
				
				if(node2==null)
				{
					Ctrl.errorLog("不该找不到node",key);
					return;
				}
				
				node2.isDeleted=true;
			};
			
			node.list.add(obj);
		}
		else
		{
			OneNode node2=_nodePool.getOne();
			node2.key=key;
			node2.table=makeTable(key);
			node2.isDeleted=true;
			
			_nodeDic.put(key,node2);
			
			node2.state=Writing;
			
			node2.table.delete(_connect,b->
			{
				if(!b)
				{
					node2.table=null;
				}
				
				if(func!=null)
					func.call(b);
				
				nodeOver(node2);
			});
		}
	}
	
	private void checkLoad(OneNode node)
	{
		if(node.state==None)
		{
			node.state=Reading;
			
			node.table.load(_connect,b->
			{
				if(!b)
				{
					node.table=null;
				}
				
				onTableLoad(node);
			});
		}
	}
	
	/** 表加载好的回调 */
	private void onTableLoad(OneNode node)
	{
		if(_nodeDic.get(node.key)!=node)
		{
			Ctrl.errorLog("TableWriteTool加载完表时，找不到node",node.key);
			return;
		}
		
		node.swap();
		node.state=Writing;
		
		WriteOneObj v;
		
		for(int i=0,len=node.writeList.size();i<len;++i)
		{
			v=node.writeList.get(i);
			
			//操作
			if(v.writeFunc!=null)
			{
				try
				{
					v.writeFunc.apply(node.isDeleted ? null : node.table);
				}
				catch(Exception e)
				{
					Ctrl.errorLog("TableWriteTool write出错",e);
				}
			}
		}
		
		
		if(node.table!=null)
		{
			if(node.isDeleted)
			{
				node.table.delete(_connect,b->
				{
					if(!b)
					{
						node.table=null;
						Ctrl.errorLog("TableWriteTool删除表时，失败:",node.key);
					}
					
					nodeOver(node);
				});
			}
			else
			{
				node.table.update(_connect,b->
				{
					if(!b)
					{
						node.table=null;
						Ctrl.errorLog("TableWriteTool更新表时，失败:",node.key);
					}
					
					nodeOver(node);
				});
			}
			
		}
		else
		{
			//Ctrl.errorLog("TableWriteTool加载表时，不存在:",key);
			nodeOver(node);
		}
	}
	
	private void nodeOver(OneNode node)
	{
		node.state=None;
		
		WriteOneObj v;
		
		if(!node.writeList.isEmpty())
		{
			for(int i=0,len=node.writeList.size();i<len;++i)
			{
				v=node.writeList.get(i);
				
				if(v.overCall!=null)
				{
					try
					{
						v.overCall.run();
					}
					catch(Exception e)
					{
						Ctrl.errorLog("TableWriteTool over出错",e);
					}
				}
			}
			
			node.writeList.clear();
		}
		
		//还是空，可回收
		if(node.list.isEmpty())
		{
			_nodeDic.remove(node.key);
			_nodePool.back(node);
		}
		else
		{
			if(node.isDeleted)
			{
				while(!node.list.isEmpty())
				{
					node.swap();
					
					for(int i=0,len=node.writeList.size();i<len;++i)
					{
						v=node.writeList.get(i);
						
						//操作
						if(v.writeFunc!=null)
						{
							try
							{
								v.writeFunc.apply(null);
							}
							catch(Exception e)
							{
								Ctrl.errorLog("TableWriteTool write出错",e);
							}
						}
					}
				}
				
				_nodeDic.remove(node.key);
				_nodePool.back(node);
			}
			else
			{
				checkLoad(node);
			}
		}
	}
	
	/** 单个节点 */
	private class OneNode implements IPoolObject
	{
		/** 状态 */
		public int state=None;
		
		public K key;
		/** 表 */
		public T table;
		/** 待办组 */
		public SList<WriteOneObj> list=new SList<>();
		/** 写中组 */
		public SList<WriteOneObj> writeList=new SList<>();
		
		/** 是否已删除 */
		public boolean isDeleted=false;
		
		/** 交换 */
		public void swap()
		{
			SList<WriteOneObj> temp=writeList;
			writeList=list;
			list=temp;
			temp.clear();
		}
		
		@Override
		public void clear()
		{
			key=null;
			table=null;
			state=None;
			list.clear();
			writeList.clear();
			isDeleted=false;
		}
	}
	
	/** 操作方法对象 */
	private class WriteOneObj
	{
		/** 操作方法 */
		public ObjectCall<T> writeFunc;
		/** 完成回调 */
		public Runnable overCall;
	}
}
