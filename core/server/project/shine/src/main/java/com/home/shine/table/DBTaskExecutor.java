package com.home.shine.table;

import com.home.shine.constlist.DBTaskType;
import com.home.shine.control.DBControl;
import com.home.shine.control.DateControl;
import com.home.shine.control.ThreadControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.collection.SList;
import com.home.shine.support.collection.SMap;
import com.home.shine.support.collection.SSet;
import com.home.shine.support.func.ObjectCall;
import com.home.shine.table.task.BaseDBTask;
import com.home.shine.table.task.CustomQueryDBTask;
import com.home.shine.thread.AbstractThread;
import com.home.shine.thread.DBPoolThread;
import com.home.shine.utils.ObjectUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.ConcurrentLinkedQueue;

/** db任务执行器 */
public class DBTaskExecutor
{
	/** DB连接 */
	private DBConnect _connect;
	
	private volatile ConcurrentLinkedQueue<BaseDBTask>[] _tasks;
	
	/** 标记组(copyOnWrite) */
	private volatile int[] _marks=new int[0];
	
	/** 自定义任务组 */
	private ConcurrentLinkedQueue<BaseDBTask> _customTasks=new ConcurrentLinkedQueue<>();
	
	/** 是否有任务标记 */
	private volatile boolean _hasTasks=false;
	
	/** 标记序号 */
	private volatile int _markIndex=0;
	
	public DBTaskExecutor()
	{
	
	}
	
	@SuppressWarnings("unchecked")
	public void init(DBConnect connect)
	{
		_connect=connect;
		_tasks=new ConcurrentLinkedQueue[ShineSetting.tableMaxNum * DBTaskType.num];
	}
	
	/** 添加任务(异步执行)(不保证有序,也不处理事务,没有ACID特性,只有高效) */
	public void addTask(BaseDBTask task)
	{
		if(task==null)
		{
			return;
		}
		
		//未指定就获取一下
		if(task._thread==null)
		{
			AbstractThread thread=ThreadControl.getCurrentShineThread();
			
			if(thread==null)
			{
				Ctrl.throwError("DB操作需要在引擎线程下执行");
				return;
			}
			
			task._thread=thread;
		}
		
		//自定义的
		if(task._type==DBTaskType.CustomQuery)
		{
			_customTasks.offer(task);
		}
		else
		{
			int mark=task.getTableID() << DBTaskType.shift | task._type;
			
			ConcurrentLinkedQueue<BaseDBTask> queue=_tasks[mark];
			
			if(queue==null)
			{
				queue=createTaskQueue(mark);
			}
			
			queue.offer(task);
		}
		
		_hasTasks=true;
	}
	
	/** 同步执行自定义任务 */
	public void executeCustomTaskSync(BaseDBTask task)
	{
		executeCustomTask(null,task,true);
	}
	
	private synchronized ConcurrentLinkedQueue<BaseDBTask> createTaskQueue(int mark)
	{
		ConcurrentLinkedQueue<BaseDBTask> queue=_tasks[mark];
		
		if(queue==null)
		{
			_tasks[mark]=queue=new ConcurrentLinkedQueue<BaseDBTask>();
			
			_marks=ObjectUtils.addIntArray(_marks,mark);
		}
		
		return queue;
	}
	
	/** 找一个执行序号,找不到返回null */
	private ConcurrentLinkedQueue<BaseDBTask> pickOneQueue(DBPoolThread thread)
	{
		ConcurrentLinkedQueue<BaseDBTask> queue;
		
		int[] marks=_marks;
		
		int len=marks.length;
		
		int mark=_markIndex;
		
		//第一轮,从index到len
		for(int i=mark;i<len;++i)
		{
			queue=_tasks[marks[i]];
			
			if(ShineSetting.openCheck && queue==null)
			{
				Ctrl.errorLog("不应该没有DBQueue");
				continue;
			}
			
			if(!queue.isEmpty())
			{
				mark=i + 1;
				
				if(mark>len)
				{
					mark=0;
				}
				
				_markIndex=mark;
				
				return queue;
			}
		}
		
		//第二轮,从0到index
		for(int i=0;i<mark;++i)
		{
			queue=_tasks[marks[i]];
			
			if(ShineSetting.openCheck && queue==null)
			{
				Ctrl.errorLog("不应该没有DB queue");
				continue;
			}
			
			if(!queue.isEmpty())
			{
				mark=i + 1;
				
				if(mark>len)
				{
					mark=0;
				}
				
				_markIndex=mark;
				
				return queue;
			}
		}
		
		return null;
	}
	
	/** 执行任务(dbThread调用)(force的存在是为了防止_hasTasks锁异常时的间隔检查) */
	public void runTask(DBPoolThread thread,boolean force)
	{
		if(!_hasTasks && !force)
		{
			return;
		}
		
		ConcurrentLinkedQueue<BaseDBTask> queue=pickOneQueue(thread);
		
		boolean did=false;
		
		if(queue!=null)
		{
			try
			{
				executeQueue(thread,queue);
			}
			catch(Exception e)
			{
				Ctrl.errorLog(e);
			}
			
			did=true;
		}
		
		if(!_customTasks.isEmpty())
		{
			try
			{
				executeCustomTask(thread,_customTasks.poll(),false);
			}
			catch(Exception e)
			{
				Ctrl.errorLog(e);
			}
			
			did=true;
		}
		
		//置为没有
		if(!did)
		{
			_hasTasks=false;
		}
	}
	
	/** 执行队列 */
	private void executeQueue(DBPoolThread thread,ConcurrentLinkedQueue<BaseDBTask> queue)
	{
		SList<BaseDBTask> _tempList=thread.getTempList();
		_tempList.clear();
		
		DBConnect conP=null;
		Connection con=null;
		
		BaseDBTask task;
		
		//取完
		while(true)
		{
			task=queue.poll();
			
			if(task==null)
				break;
			
			if(con==null)
			{
				//取第一个连接
				con=(conP=_connect).getConnection(thread.index);
				
				//当前连接不可用
				if(con==null)
				{
					Ctrl.warnLog("executeQueue时,获取连接为空");
					queue.offer(task);
					return;
				}
			}
			
			_tempList.add(task);
		}
		
		//被别的处理完了
		if(_tempList.isEmpty())
		{
			if(con!=null)
			{
				conP.reConnection(con,thread.index);
			}
			return;
		}
		
		task=_tempList.get(0);
		
		PreparedStatement ps=null;
		ResultSet rs=null;
		boolean hasError=false;
		
		try
		{
			switch(task._type)
			{
				case DBTaskType.Insert:
				{
					ps=con.prepareStatement(task._table.getInsertStr());
					
					BaseDBTask[] tvs=_tempList.getValues();
					
					for(int i=0, len=_tempList.size();i<len;++i)
					{
						tvs[i].setValues(ps,true);
						
						ps.addBatch();
					}
					
					int[] re=ps.executeBatch();
					int reCode;
					for(int i=0;i<re.length;++i)
					{
						//大于0和-2为通过,因为batch Insert的返回值-2;
						tvs[i].overCallArg=(reCode=re[i])>0 || reCode==-2;
					}
				}
				break;
				case DBTaskType.Delete:
				case DBTaskType.Delete2:
				{
					//第几个
					int index=task._type - DBTaskType.Delete;
					
					ps=con.prepareStatement(task._table.getDeleteStr());
					
					BaseDBTask[] tvs=_tempList.getValues();
					
					for(int i=0, len=_tempList.size();i<len;++i)
					{
						tvs[i].setPrimaryKeyByIndex(ps,index);
						ps.addBatch();
					}
					
					int[] re=ps.executeBatch();
					
					for(int i=re.length - 1;i >= 0;--i)
					{
						tvs[i].overCallArg=re[i] >= 0;
					}
				}
					break;
				case DBTaskType.Update:
				{
					ps=con.prepareStatement(task._table.getUpdateStr());
					
					BaseDBTask[] tvs=_tempList.getValues();
					
					for(int i=0, len=_tempList.size();i<len;++i)
					{
						BaseDBTask t=tvs[i];
						
						t.setValues(ps,false);
						t.setPrimaryKeyByIndex(ps,0);
						
						ps.addBatch();
					}
					
					int[] re=ps.executeBatch();
					
					for(int i=re.length - 1;i >= 0;--i)
					{
						tvs[i].overCallArg=re[i] >= 0;
					}
				}
					break;
				case DBTaskType.Select:
				case DBTaskType.Select2:
				case DBTaskType.Select3:
				{
					//第几个
					int index=task._type - DBTaskType.Select;
					
					SMap<Object,BaseDBTask> map=thread.getTempMap();
					//多个key的
					SSet<Object> keys=thread.getTempSet();
					
					map.clear();
					keys.clear();
					
					int len=0;
					
					BaseDBTask[] tvs=_tempList.getValues();
					
					for(int i=0, len2=_tempList.size();i<len2;++i)
					{
						BaseDBTask t=tvs[i];
						//先给了默认值
						t.overCallArg=false;
						
						Object key=t.getPrimaryKeyByIndex(index);
						
						//重复
						if(map.putIfAbsent(key,t)!=null)
						{
							keys.add(key);
						}
						//不重复
						else
						{
							++len;
						}
					}
					
					StringBuilder sb=thread.stringBuilderPool.getOne();
					
					sb.append(task._table.getSelectInStrByIndex(index));
					
					int i;
					
					for(i=0;i<len;++i)
					{
						if(i>0)
						{
							sb.append(',');
						}
						
						sb.append('?');
					}
					
					sb.append(')');
					
					if(ShineSetting.sqlUseEnd)
					{
						sb.append(';');
					}
					
					ps=con.prepareStatement(sb.toString());
					
					thread.stringBuilderPool.back(sb);
					
					i=1;
					
					Object[] mapT=map.getTable();
					
					for(int j=mapT.length - 2;j >= 0;j-=2)
					{
						if(mapT[j]!=null)
						{
							BaseDBTask t=(BaseDBTask)mapT[j + 1];
							
							t.setPrimaryKeyByIndexAt(ps,index,i);
							
							++i;
						}
					}
					
					rs=ps.executeQuery();
					
					while(rs.next())
					{
						BaseDBResult result=task.createResult();
						
						result.readValues(rs);
						
						Object key=result.getPrimaryKeyByIndex(index);
						
						//多个(只有多个key的才遍历)
						if(keys.contains(key))
						{
							BaseDBTask[] tvs2=_tempList.getValues();
							BaseDBTask t;
							
							for(int i2=_tempList.size()-1;i2>=0;--i2)
							{
								//key相等
								if((t=tvs2[i2]).getPrimaryKeyByIndex(index).equals(key))
								{
									t.setResult(result);
								}
							}
						}
						//单个,或没找到key
						else
						{
							BaseDBTask dbTask=map.get(key);
							
							if(dbTask!=null)
							{
								dbTask.setResult(result);
							}
							else
							{
								Ctrl.warnLog("db select出错，未找到数据，可能是大小写问题",task.getTableName(),key);
								
								//String key1=((String)key).toLowerCase();
								//boolean found=false;
								//
								//BaseDBTask[] tvs2=_tempList.getValues();
								//BaseDBTask t;
								//
								//for(int i2=_tempList.size()-1;i2>=0;--i2)
								//{
								//	String key2=(String)(t=tvs2[i2]).getPrimaryKeyByIndex(index);
								//
								//	//key相等
								//	if(key2.toLowerCase().equals(key1))
								//	{
								//		t.setResult(result);
								//		found=true;
								//	}
								//}
								//
								//if(found)
								//{
								//	Ctrl.warnLog("db select时，出现一次大小写问题",task.getTableName(),key);
								//}
								//else
								//{
								//	Ctrl.errorLog("db select出错，未找到数据",task.getTableName(),key);
								//}
							}
						}
					}
					
					//清空
					map.clear();
					keys.clear();
				}
				break;
			}
		}
		catch(Exception e)
		{
			hasError=true;
			Ctrl.errorLog("DB executeQueue执行出错",e);
		}
		
		if(!DBControl.closeResultSet(rs))
			hasError=true;
		
		if(!DBControl.closeStatement(ps))
			hasError=true;
		
		try
		{
			if(hasError)
			{
				con.rollback();
			}
			else
			{
				con.commit();
			}
		}
		catch(Exception e)
		{
			Ctrl.errorLog("DB commit/rollback执行出错",e);
		}
		
		conP.reConnection(con,thread.index);
		
		long nowTime=DateControl.getTimeMillis();
		
		BaseDBTask[] values=_tempList.getValues();
		
		for(int i=0,len=_tempList.size();i<len;++i)
		{
			if(hasError && ShineSetting.dbTaskNeedRetry)
			{
				(task=values[i]).clear();
				
				//在时间之内,//加回去
				if((nowTime-task.createTime)<ShineSetting.dbTaskMaxTryTime)
				{
					queue.offer(task);
				}
				else
				{
					Ctrl.errorLog("数据库入库失败,tableName:",task.getTableName(),"taskType:",task._type);
				}
			}
			else
			{
				//回调
				values[i].reCall(!hasError);
			}
			
			values[i]=null;
		}
		
		_tempList.justClearSize();
	}
	
	
	/** 执行队列 */
	public boolean executeTaskSync(BaseDBTask task)
	{
		//取连接
		Connection con=_connect.getMainConnection();
		
		if(con==null)
		{
			return false;
		}
		
		//关了回调
		task.overCall=null;
		
		PreparedStatement ps=null;
		ResultSet rs=null;
		boolean hasError=false;
		
		boolean success=false;
		
		try
		{
			switch(task._type)
			{
				case DBTaskType.Insert:
				{
					ps=con.prepareStatement(task._table.getInsertStr());
					
					task.setValues(ps,true);
					task.setOver();
					
					int re=ps.executeUpdate();
					
					success=re >= 0;
				}
				break;
				case DBTaskType.Delete:
				{
					ps=con.prepareStatement(task._table.getDeleteStr());
					
					task.setPrimaryKeyByIndex(ps,0);
					task.setOver();
					
					int re=ps.executeUpdate();
					
					success=re >= 0;
				}
				break;
				case DBTaskType.Update:
				{
					ps=con.prepareStatement(task._table.getUpdateStr());
					
					task.setValues(ps,false);
					task.setPrimaryKeyByIndex(ps,0);
					task.setOver();
					
					int re=ps.executeUpdate();
					
					success=re >= 0;
				}
				break;
				case DBTaskType.Select:
				case DBTaskType.Select2:
				case DBTaskType.Select3:
				{
					//第几个
					int index=task._type - DBTaskType.Select;
					
					String str=task._table.getSelectInStrByIndex(index);
					str+="?)";
					
					if(ShineSetting.sqlUseEnd)
					{
						str+=';';
					}
					
					ps=con.prepareStatement(str);
					
					task.setPrimaryKeyByIndex(ps,index);
					
					rs=ps.executeQuery();
					
					if(rs.next())
					{
						BaseDBResult result=task.createResult();
						
						result.readValues(rs);
						
						task.setResult(result);
						
						task.reBackSync(true);
						
						success=true;
					}
					else
					{
						success=false;
					}
				}
				break;
			}
		}
		catch(Exception e)
		{
			hasError=true;
			success=false;
			Ctrl.errorLog("DB executeQueue执行出错",e);
		}
		
		if(!DBControl.closeResultSet(rs))
		{
			hasError=true;
			success=false;
		}
		
		if(!DBControl.closeStatement(ps))
		{
			hasError=true;
			success=false;
		}
		
		try
		{
			if(hasError)
			{
				con.rollback();
			}
			else
			{
				con.commit();
			}
		}
		catch(Exception e)
		{
			Ctrl.errorLog("DB commit/rollback执行出错",e);
		}
		
		_connect.reMainConnection(con);
		
		return success;
	}
	
	/** 执行自定义任务(DBPool线程) */
	private void executeCustomTask(DBPoolThread thread,BaseDBTask task,boolean sync)
	{
		if(task==null)
			return;
		
		//取连接
		
		Connection con;
		int index=-1;
		
		if(thread!=null)
		{
			con=_connect.getConnection(index=thread.index);
		}
		else
		{
			con=_connect.getMainConnection();
		}
		
		if(con==null)
		{
			Ctrl.warnLog("executeCustomTask时,获取连接为空");
			if(!sync)
			{
				//加回去
				_customTasks.offer(task);
			}
			
			return;
		}
		
		boolean success=task.executeCustom(con);
		
		try
		{
			if(success)
			{
				con.commit();
			}
			else
			{
				con.rollback();
			}
		}
		catch(Exception e)
		{
			Ctrl.errorLog("DB commit/rollback执行出错",e);
		}
		
		if(thread!=null)
		{
			_connect.reConnection(con,index);
		}
		else
		{
			_connect.reMainConnection(con);
		}
		
		if(success)
		{
			if(sync)
			{
				task.reBackSync(true);
			}
			else
			{
				task.reCall(true);
			}
		}
		else
		{
			if(sync)
			{
				//依然同步回调
				task.reBackSync(false);
			}
			else
			{
				if(ShineSetting.dbTaskNeedRetry)
				{
					_customTasks.offer(task);
				}
				else
				{
					task.reCall(false);
				}
			}
		}
	}
	
	/** 执行query(异步) */
	public void executeQuery(String sql,ObjectCall<Boolean> overCall)
	{
		new CustomQueryDBTask(sql,overCall).execute(_connect);
	}
	
	/** 执行query(同步) */
	public boolean executeQuerySync(String sql)
	{
		Connection con=_connect.getMainConnection();
		
		if(con==null)
		{
			return false;
		}
		
		boolean re=true;
		
		try
		{
			Statement s=con.createStatement();
			
			s.execute(sql);
			
			s.close();
		}
		catch(Exception e)
		{
			re=false;
			Ctrl.errorLog(e);
		}
		
		_connect.reMainConnection(con);
		
		return re;
	}
	
	/** 执行query(同步) */
	public boolean executeQueryHasResultSync(String sql)
	{
		ResultSet re=executeQueryResultSync(sql);
		
		if(re==null)
		{
			return false;
		}
		
		boolean rr=false;
		
		try
		{
			if(re.next())
			{
				rr=true;
			}
		}
		catch(Exception e1)
		{
			e1.printStackTrace();
		}
		
		return rr;
	}
	
	/** 执行query(同步) */
	public ResultSet executeQueryResultSync(String sql)
	{
		Connection con=_connect.getMainConnection();
		
		if(con==null)
		{
			return null;
		}
		
		ResultSet re=null;
		
		try
		{
			Statement s=con.createStatement();
			
			re=s.executeQuery(sql);
			
		}
		catch(Exception e)
		{
			Ctrl.errorLog(e);
		}
		
		_connect.reMainConnection(con);
		
		return re;
	}
}
