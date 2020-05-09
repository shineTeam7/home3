package com.home.shine.table.task;

import com.home.shine.control.DBControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.func.ObjectCall;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/** 自定义query执行 */
public class CustomQueryDBTask extends CustomDBTask
{
	public CustomQueryDBTask(String sql,ObjectCall overCall)
	{
		super(sql,overCall);
	}
	
	@Override
	public boolean executeCustom(Connection con)
	{
		overCallArg=true;
		Statement ps=null;
		
		boolean result=true;
		
		try
		{
			ps=con.createStatement();
			ps.execute(customSql);
		}
		catch(Exception e)
		{
			overCallArg=false;
			Ctrl.errorLog(e);
			result=false;
		}
		
		if(!DBControl.closeStatement(ps))
			result=false;
		
		return result;
	}
}
