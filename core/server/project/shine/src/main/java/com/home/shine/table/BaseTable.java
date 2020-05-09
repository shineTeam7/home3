package com.home.shine.table;

import com.home.shine.bytes.BytesReadStream;
import com.home.shine.bytes.BytesWriteStream;
import com.home.shine.constlist.CustomDBTaskType;
import com.home.shine.constlist.DBTaskType;
import com.home.shine.constlist.TableStateType;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.SList;
import com.home.shine.support.func.ObjectCall;
import com.home.shine.table.task.BaseDBTask;
import com.home.shine.thread.AbstractThread;

import java.sql.PreparedStatement;

/** 表基类 */
public abstract class BaseTable extends BaseDBResult
{
	/** 表名 */
	protected String _tableName="";
	/** 是否是多主键表 */
	protected boolean _isMultiPrimaryKey=false;
	
	/** 表状态 */
	public int tableState=TableStateType.Normal;
	
	/** 在写出后移除 */
	private boolean _needRemoveAfterWrite=false;
	
	public BaseTable()
	{
		
	}
	
	/** 表名(暂时无用) */
	public String getTableName()
	{
		return _tableName;
	}
	
	/** 表ID */
	public int getTableID()
	{
		return _dataID;
	}
	
	/** 构造执行task */
	private void makeTask(DBConnect connect,ObjectCall<Boolean> overCall,int type,AbstractThread signedThread)
	{
		BaseDBTask task=createTask();
		task._table=this;
		task._type=type;
		task._thread=signedThread;
		task.overCall=overCall;
		
		connect.getExecutor().addTask(task);
	}
	
	/** 同步执行task */
	private boolean executeTaskSync(DBConnect connect,int type)
	{
		BaseDBTask task=createTask();
		task._table=this;
		task._type=type;
		
		return connect.getExecutor().executeTaskSync(task);
	}
	
	/** 增 */
	public void insert(DBConnect connect)
	{
		insert(connect,null);
	}
	
	/** 增 */
	public void insert(DBConnect connect,ObjectCall<Boolean> overCall)
	{
		makeTask(connect,overCall,DBTaskType.Insert,null);
	}
	
	/** 增 */
	public void insert(DBConnect connect,ObjectCall<Boolean> overCall,AbstractThread thread)
	{
		makeTask(connect,overCall,DBTaskType.Insert,thread);
	}
	
	/** 删 */
	public void delete(DBConnect connect)
	{
		delete(connect,null);
	}
	
	/** 删 */
	public void delete(DBConnect connect,ObjectCall<Boolean> overCall)
	{
		makeTask(connect,overCall,DBTaskType.Delete,null);
	}
	
	/** 删 */
	public void delete2(DBConnect connect,ObjectCall<Boolean> overCall)
	{
		makeTask(connect,overCall,DBTaskType.Delete2,null);
	}
	
	/** 删 */
	public void delete(DBConnect connect,ObjectCall<Boolean> overCall,AbstractThread thread)
	{
		makeTask(connect,overCall,DBTaskType.Delete,thread);
	}
	
	/** 改 */
	public void update(DBConnect connect)
	{
		update(connect,null);
	}
	
	/** 改 */
	public void update(DBConnect connect,ObjectCall<Boolean> overCall)
	{
		makeTask(connect,overCall,DBTaskType.Update,null);
	}
	
	/** 改 */
	public void update(DBConnect connect,ObjectCall<Boolean> overCall,AbstractThread thread)
	{
		makeTask(connect,overCall,DBTaskType.Update,thread);
	}
	
	/** 查 */
	public void load(DBConnect connect,ObjectCall<Boolean> overCall)
	{
		if(_isMultiPrimaryKey)
		{
			Ctrl.throwError("多主键表目前不支持batch load");
			overCall.apply(false);
			return;
		}
		
		makeTask(connect,overCall,DBTaskType.Select,null);
	}
	
	/** 查(指定线程回调) */
	public void load(DBConnect connect,ObjectCall<Boolean> overCall,AbstractThread thread)
	{
		if(_isMultiPrimaryKey)
		{
			Ctrl.throwError("多主键表目前不支持batch load");
			thread.addFunc(()->
			{
				overCall.apply(false);
			});
			return;
		}
		
		makeTask(connect,overCall,DBTaskType.Select,thread);
	}
	
	/** 查(第二主键) */
	public void load2(DBConnect connect,ObjectCall<Boolean> overCall)
	{
		makeTask(connect,overCall,DBTaskType.Select2,null);
	}
	
	/** 查(第二主键)(指定线程回调) */
	public void load2(DBConnect connect,ObjectCall<Boolean> overCall,AbstractThread thread)
	{
		makeTask(connect,overCall,DBTaskType.Select2,thread);
	}
	
	/** 查(第三主键) */
	public void load3(DBConnect connect,ObjectCall<Boolean> overCall)
	{
		makeTask(connect,overCall,DBTaskType.Select3,null);
	}
	
	/** 查(第三主键)(指定线程回调) */
	public void load3(DBConnect connect,ObjectCall<Boolean> overCall,AbstractThread thread)
	{
		makeTask(connect,overCall,DBTaskType.Select3,thread);
	}
	
	/** 增(同步) */
	public boolean insertSync(DBConnect connect)
	{
		return executeTaskSync(connect,DBTaskType.Insert);
	}
	
	/** 删(同步) */
	public boolean deleteSync(DBConnect connect)
	{
		return executeTaskSync(connect,DBTaskType.Delete);
	}
	
	/** 改(同步) */
	public boolean updateSync(DBConnect connect)
	{
		return executeTaskSync(connect,DBTaskType.Update);
	}
	
	/** 查(同步) */
	public boolean loadSync(DBConnect connect)
	{
		return executeTaskSync(connect,DBTaskType.Select);
	}
	
	/** 将update写到一个ps里(不调用addBatch) */
	public BaseDBTask updateToPs(PreparedStatement ps,boolean isBatch)
	{
		BaseDBTask task=createTask();
		task.setIsBatchUpdate(isBatch);
		task.setValues(ps,false);
		task.setPrimaryKeyByIndex(ps,0);
		task.setOver();
		return task;
	}
	
	/** 将insert写到一个ps里(不调用addBatch) */
	public BaseDBTask insertToPs(PreparedStatement ps)
	{
		BaseDBTask task=createTask();
		task.setValues(ps,true);
		task.setOver();
		return task;
	}
	
	/** 将delete写到一个ps里(不调用addBatch) */
	public BaseDBTask deleteToPs(PreparedStatement ps)
	{
		return deleteIndexToPs(0,ps);
	}
	
	/** 将delelteIndex写到一个ps里(不调用addBatch) */
	public BaseDBTask deleteIndexToPs(int index,PreparedStatement ps)
	{
		BaseDBTask task=createTask();
		task.setPrimaryKeyByIndex(ps,index);
		task.setOver();
		return task;
	}
	
	/** 读取全部(同步)(启动流程中使用) */
	@SuppressWarnings("unchecked")
	public SList<BaseTable> loadAllSync(DBConnect connect)
	{
		BaseDBTask task=createTask();
		task._table=this;
		task._type=DBTaskType.CustomQuery;
		task._customType=CustomDBTaskType.LoadAll;
		
		connect.getExecutor().executeCustomTaskSync(task);
		
		return (SList<BaseTable>)task.overCallArg;
	}
	
	/** 读取全部 */
	public void loadAll(DBConnect connect,ObjectCall<SList<BaseTable>> overCall)
	{
		loadAll(connect,overCall,null);
	}
	
	/** 读取全部 */
	public void loadAll(DBConnect connect,ObjectCall<SList<BaseTable>> overCall,AbstractThread thread)
	{
		BaseDBTask task=createTask();
		task._table=this;
		task._type=DBTaskType.CustomQuery;
		task._customType=CustomDBTaskType.LoadAll;
		task.overCall=overCall;
		
		connect.getExecutor().addTask(task);
	}
	
	/** 自定义读取(keyIndexs,需要的key序号) */
	public void loadCustomKeys(DBConnect connect,int keyIndex,ObjectCall<SList<BaseTable>> overCall)
	{
		loadCustomKeys(connect,keyIndex,overCall,null);
	}
	
	/** 自定义读取(keyIndexs,需要的key序号) */
	public void loadCustomKeys(DBConnect connect,int keyIndex,ObjectCall<SList<BaseTable>> overCall,AbstractThread thread)
	{
		BaseDBTask task=createTask();
		task._table=this;
		task._type=DBTaskType.CustomQuery;
		task._customType=CustomDBTaskType.LoadCustomKey;
		task.customKeyIndex=keyIndex;
		task.overCall=overCall;
		task._thread=thread;
		
		connect.getExecutor().addTask(task);
	}
	
	/** 自定义sql读取 */
	public void loadCustomSql(DBConnect connect,String sql,ObjectCall<SList<BaseTable>> overCall)
	{
		BaseDBTask task=createTask();
		task._table=this;
		task._type=DBTaskType.CustomQuery;
		task._customType=CustomDBTaskType.LoadCustomSql;
		task.customSql=sql;
		task.overCall=overCall;
		task._thread=null;
		
		connect.getExecutor().addTask(task);
	}
	
	/** insertStr */
	public abstract String getInsertStr();
	
	/** deleteStr */
	public abstract String getDeleteStr();
	
	/** updateStr */
	public abstract String getUpdateStr();
	
	/** selectStr */
	public abstract String getSelectStr();
	
	/** selectInStr(首) */
	public abstract String getSelectInStrByIndex(int index);
	
	/** db任务(赋值好的) */
	protected abstract BaseDBTask createTask();
	
	/** 设置select结果 */
	public abstract void readByResult(BaseDBResult result);
	
	/** 读取后 */
	public void afterRead()
	{
	
	}
	
	
	@Override
	public Object getPrimaryKeyByIndex(int index)
	{
		return null;
	}
	
	/** 标记插入 */
	public void setNeedInsert()
	{
		if(tableState==TableStateType.NeedDelete)
			tableState=TableStateType.Normal;
		else
			tableState=TableStateType.NeedInsert;
	}
	
	/** 标记常态 */
	public void setNormal()
	{
		tableState=TableStateType.Normal;
	}
	
	/** 标记删除 */
	public void setNeedDelete()
	{
		if(tableState==TableStateType.NeedInsert)
			tableState=TableStateType.Remove;
		else
			tableState=TableStateType.NeedDelete;
	}
	
	public boolean needRemove()
	{
		return tableState==TableStateType.NeedDelete || tableState==TableStateType.Remove;
	}
	
	public boolean needRemoveAfterWrite()
	{
		return needRemove() || _needRemoveAfterWrite;
	}
	
	public void setNeedRemoveAfterWrite(boolean value)
	{
		_needRemoveAfterWrite=value;
	}
	
	/** 写入到流中，带标记 */
	public void writeTableStream(BytesWriteStream stream)
	{
		stream.writeInt(tableState);
		
		writeBytesFull(stream);
	}
	
	/** 从流中读取，带标记 */
	public void readTableStream(BytesReadStream stream)
	{
		tableState=stream.readInt();
		readBytesFull(stream);
	}
}
