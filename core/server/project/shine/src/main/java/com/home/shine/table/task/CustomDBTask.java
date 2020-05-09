package com.home.shine.table.task;

import com.home.shine.constlist.DBTaskType;
import com.home.shine.support.func.ObjectCall;
import com.home.shine.table.BaseDBResult;
import com.home.shine.table.BaseTable;
import com.home.shine.table.DBConnect;

/** 自定义数据库任务 */
public class CustomDBTask extends BaseDBTask
{
	public CustomDBTask(String sql,ObjectCall overCall)
	{
		_type=DBTaskType.CustomQuery;
		customSql=sql;
		this.overCall=overCall;
	}
	
	@Override
	public BaseTable createTable()
	{
		return null;
	}
	
	@Override
	public BaseDBResult createResult()
	{
		return null;
	}

	@Override
	protected void toSetValues(boolean isInsert)
	{

	}

	@Override
	public Object getPrimaryKeyByIndex(int index)
	{
		return null;
	}

	@Override
	protected void toSetPrimaryKeyByIndex(int index)
	{

	}
	
	/** 异步执行 */
	public void execute(DBConnect connect)
	{
		connect.getExecutor().addTask(this);
	}
	
	/** 同步执行 */
	public void executeSync(DBConnect connect)
	{
		connect.getExecutor().executeTaskSync(this);
	}
}
