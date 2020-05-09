package com.home.shine.table.task;

import com.home.shine.control.DBControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.func.ObjectCall;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/** 自定义读取执行 */
public abstract class CustomLoadDBTask extends CustomDBTask
{
	public CustomLoadDBTask(String sql,ObjectCall overCall)
	{
		super(sql,overCall);
	}
	
	@Override
	public boolean executeCustom(Connection con)
	{
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		boolean result=true;
		
		try
		{
			ps=con.prepareStatement(customSql);
			
			rs=ps.executeQuery();
			
			readResult(rs);
		}
		catch(Exception e)
		{
			overCallArg=false;
			Ctrl.errorLog(e);
			result=false;
		}
		
		if(!DBControl.closeResultSet(rs))
			result=false;
		
		if(!DBControl.closeStatement(ps))
			result=false;
		
		return result;
	}
	
	/** 读取结果组 */
	protected abstract void readResult(ResultSet rs);
}
