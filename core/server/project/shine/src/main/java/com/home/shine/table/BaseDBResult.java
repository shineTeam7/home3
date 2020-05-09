package com.home.shine.table;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.data.BaseData;
import com.home.shine.data.DateData;

/** DB结果 */
public abstract class BaseDBResult extends BaseData
{
	protected ResultSet _rs;
	
	protected int _pos=1;
	
	/** 读boolean */
	protected boolean readBoolean()
	{
		boolean re=false;
		
		try
		{
			re=_rs.getBoolean(_pos);
		}
		catch(SQLException e)
		{
			Ctrl.errorLog(e);
		}
		
		++_pos;
		
		return re;
	}
	
	/** 读byte */
	protected int readByte()
	{
		int re=0;
		
		try
		{
			re=_rs.getByte(_pos);
			
		}
		catch(SQLException e)
		{
			Ctrl.errorLog(e);
		}
		
		++_pos;
		
		return re;
	}
	
	/** 读short */
	protected int readShort()
	{
		int re=0;
		
		try
		{
			re=_rs.getShort(_pos);
			
		}
		catch(SQLException e)
		{
			Ctrl.errorLog(e);
		}
		
		++_pos;
		
		return re;
	}
	
	/** 读整形 */
	protected int readInt()
	{
		int re=0;
		
		try
		{
			re=_rs.getInt(_pos);
			
		}
		catch(SQLException e)
		{
			Ctrl.errorLog(e);
		}
		
		++_pos;
		
		return re;
	}
	
	/** 读整形 */
	protected long readLong()
	{
		long re=0;
		
		try
		{
			re=_rs.getLong(_pos);
			
		}
		catch(SQLException e)
		{
			Ctrl.errorLog(e);
		}
		
		++_pos;
		
		return re;
	}
	
	/** 读String */
	protected String readString()
	{
		String re="";
		
		try
		{
			re=_rs.getString(_pos);
			
		}
		catch(SQLException e)
		{
			Ctrl.errorLog(e);
		}
		
		++_pos;
		
		return re;
	}
	
	/** 读取字节数组 */
	protected byte[] readBytes()
	{
		byte[] re=null;
		
		try
		{
			re=_rs.getBytes(_pos);
			
		}
		catch(SQLException e)
		{
			Ctrl.errorLog(e);
		}
		
		++_pos;
		
		return re;
	}
	
	/** 读日期 */
	protected DateData readDate()
	{
		Date d=null;
		
		try
		{
			d=_rs.getDate(_pos);
			
		}
		catch(SQLException e)
		{
			Ctrl.errorLog(e);
		}
		
		++_pos;
		
		if(d!=null)
		{
			Calendar c=Calendar.getInstance();
			c.setTime(d);
			
			DateData re=new DateData();
			re.initByCalendar(c);
			
			return re;
		}
		
		return null;
	}
	
	/** 读数据组 */
	public void readValues(ResultSet rs)
	{
		_rs=rs;
		_pos=1;
		
		toReadValues();
		
		_rs=null;
	}
	
	protected abstract void toReadValues();
	
	/** 获取第几主键/索引值(这个接口BaseTable不复写了,因为没必要) */
	public abstract Object getPrimaryKeyByIndex(int index);
}
