package com.home.shine.control;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.table.DBTaskExecutor;
import com.home.shine.thread.DBPoolThread;
import com.home.shine.utils.ObjectUtils;
import com.mysql.cj.jdbc.ConnectionImpl;
import com.mysql.cj.protocol.a.NativePacketPayload;
import org.apache.commons.dbcp2.DelegatingConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/** db控制 */
public class DBControl
{
	private static volatile DBTaskExecutor[] _executors=new DBTaskExecutor[0];
	
	public static void addExecutor(DBTaskExecutor executor)
	{
		_executors=ObjectUtils.addArray(_executors,executor,DBTaskExecutor[]::new);
	}
	
	/** 执行任务(dbThread调用)(force的存在是为了防止_hasTasks锁异常时的间隔检查) */
	public static void runTask(DBPoolThread thread,boolean force)
	{
		for(DBTaskExecutor executor : _executors)
		{
			executor.runTask(thread,force);
		}
	}
	
	/** 关闭statement */
	public static boolean closeStatement(Statement ps)
	{
		if(ps==null)
			return true;
		
		try
		{
			ps.close();
		}
		catch(Exception e)
		{
			Ctrl.errorLog(e);
			return false;
		}
		
		return true;
	}
	
	/** 关闭resultSet */
	public static boolean closeResultSet(ResultSet rs)
	{
		if(rs==null)
			return true;
		
		try
		{
			rs.close();
		}
		catch(Exception e)
		{
			Ctrl.errorLog(e);
			return false;
		}
		
		return true;
	}
	
	/** 获取连接尺寸 */
	public static NativePacketPayload getNativePacketPayload(Connection con)
	{
		//TODO:后续或许还需补充linux的获取
		
		try
		{
			Connection r;
			if(con instanceof DelegatingConnection)
			{
				r=((DelegatingConnection)con).getInnermostDelegateInternal();
			}
			else
			{
				r=con;
			}
			
			if(r instanceof ConnectionImpl)
			{
				ConnectionImpl impl=(ConnectionImpl)r;
				
				return impl.getSession().getProtocol().getSharedSendPacket();
			}
		}
		catch(Exception e)
		{
			Ctrl.errorLog(e);
		}
		
		return null;
	}
}
