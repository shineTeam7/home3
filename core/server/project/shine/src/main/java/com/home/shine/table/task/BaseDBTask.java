package com.home.shine.table.task;

import com.home.shine.bytes.BytesWriteStream;
import com.home.shine.constlist.CustomDBTaskType;
import com.home.shine.constlist.DBTaskType;
import com.home.shine.control.DBControl;
import com.home.shine.control.DateControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.data.DateData;
import com.home.shine.support.collection.SList;
import com.home.shine.support.func.ObjectCall;
import com.home.shine.table.BaseDBResult;
import com.home.shine.table.BaseTable;
import com.home.shine.thread.AbstractThread;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** 数据库任务基类 */
public abstract class BaseDBTask
{
	/** 对应表 */
	public BaseTable _table;
	/** 任务类型(DBTaskType) */
	public int _type;
	/** 自定义任务类型(只有type为customQuery有意义) */
	public int _customType=-1;
	/** 绑定线程 */
	public AbstractThread _thread;
	///** DB连接 */
	//public DBConnect _connect;
	
	/** 自定义查询参数(为CustomQuery) */
	public int customKeyIndex;
	/** 自定义语句 */
	public String customSql;
	/** 是否执行成功 */
	private boolean _success=false;
	
	/** 回调 */
	@SuppressWarnings("rawtypes")
	public ObjectCall overCall;
	/** 回调参数 */
	public Object overCallArg;
	
	/** 创建时间 */
	public long createTime;
	
	protected PreparedStatement _ps;
	
	protected boolean _isBatchUpdate=false;
	/** 位置 */
	protected int _pos=1;
	/** 字节长度 */
	protected int _size=0;
	
	public BaseDBTask()
	{
		createTime=DateControl.getTimeMillis();
	}
	
	/** 表ID */
	public int getTableID()
	{
		return _table.getTableID();
	}
	
	/** 表名 */
	public String getTableName()
	{
		return _table.getTableName();
	}
	
	/** 清空,为了下次继续用 */
	public void clear()
	{
		_pos=1;
		_size=0;
	}
	
	public void setIsBatchUpdate(boolean value)
	{
		_isBatchUpdate=value;
	}
	
	/** 获取写过的二进制长度 */
	public int getWriteSize()
	{
		return _size;
	}
	
	/** 创建表对象 */
	public abstract BaseTable createTable();
	
	/** 获取一个结果对象 */
	public abstract BaseDBResult createResult();
	
	/** 设置select结果,并置成功(DBPool线程) */
	public void setResult(BaseDBResult result)
	{
		overCallArg=true;
		
		if(DBTaskType.isSelect(_type))
		{
			if(result!=null)
			{
				_table.readByResult(result);
				_table.afterRead();
			}
		}
	}
	
	/** 回归调用线程(DB线程) */
	public void reCall(boolean success)
	{
		_success=success;
		
		if(_thread!=null)
		{
			_thread.addFunc(this::reCallNext);
		}
	}
	
	/** 回归调用(同步) */
	public void reBackSync(boolean success)
	{
		_success=success;
		reCallNext();
	}
	
	/** 回归原线程(逻辑线程) */
	@SuppressWarnings("unchecked")
	private void reCallNext()
	{
		//回调
		if(overCall!=null)
		{
			if(DBTaskType.isBooleanCallback(_type))
			{
				if(!_success)
				{
					overCallArg=false;
				}
				else
				{
					if(overCallArg==null)
					{
						Ctrl.errorLog("overCallArg还是存在问题");
					}
				}
			}
			
			overCall.apply(overCallArg);
		}
	}
	
	/** 设置boolean */
	protected void setBoolean(boolean value)
	{
		try
		{
			_ps.setBoolean(_pos,value);
			
		}
		catch(SQLException e)
		{
			Ctrl.errorLog(e);
		}
		
		++_pos;
		_size++;
	}
	
	/** 设置byte */
	protected void setByte(int value)
	{
		try
		{
			_ps.setByte(_pos,(byte)value);
			
		}
		catch(SQLException e)
		{
			Ctrl.errorLog(e);
		}
		
		++_pos;
		_size++;
	}
	
	/** 设置short */
	protected void setShort(int value)
	{
		try
		{
			_ps.setShort(_pos,(short)value);
			
		}
		catch(SQLException e)
		{
			Ctrl.errorLog(e);
		}
		
		++_pos;
		_size+=(_isBatchUpdate ? 6 : 2);
	}
	
	/** 设置int */
	protected void setInt(int value)
	{
		try
		{
			_ps.setInt(_pos,value);
			
		}
		catch(SQLException e)
		{
			Ctrl.errorLog(e);
		}
		
		++_pos;
		_size+=(_isBatchUpdate ? 9 : 4);
	}
	
	/** 设置long */
	protected void setLong(long value)
	{
		try
		{
			_ps.setLong(_pos,value);
			
		}
		catch(SQLException e)
		{
			Ctrl.errorLog(e);
		}
		
		++_pos;
		_size+=(_isBatchUpdate ? 19 : 8);
	}
	
	/** 设置String */
	protected void setString(String value)
	{
		//容错
		if(value==null)
			value="";
		
		try
		{
			_ps.setString(_pos,value);
			
		}
		catch(SQLException e)
		{
			Ctrl.errorLog(e);
		}
		
		++_pos;
		_size+=(value.length()*2+2);//简单的认为每个char，2字节,不走getBytes
	}
	
	/** 设置字节数组 */
	protected void setBytes(byte[] bytes)
	{
		try
		{
			_ps.setBytes(_pos,bytes);
			
		}
		catch(SQLException e)
		{
			Ctrl.errorLog(e);
		}
		
		++_pos;
		_size+=(_isBatchUpdate ?  bytes.length * 2 : bytes.length);
		_size+=BytesWriteStream.getLenSize(bytes.length);//此处采用shine的计算方式，只会比mysql的大不会小
	}
	
	/** 设置日期 */
	protected void setDate(DateData date)
	{
		try
		{
			_ps.setDate(_pos,new Date(date.getTimeMillis()));
		}
		catch(SQLException e)
		{
			Ctrl.errorLog(e);
		}
		
		++_pos;
		_size+=(_isBatchUpdate ?  19 : 8);
	}
	
	/** 设置内容结束 */
	public void setOver()
	{
		if(_isBatchUpdate)
		{
		
		}
		else
		{
			_size+=11;
			int n=_pos-2;
			_size+=((n / 8)+1);
			_size+=(n*2)+2;
		}
	}
	
	/** 设置statement */
	public void setValues(PreparedStatement ps,boolean isInsert)
	{
		_ps=ps;
		toSetValues(isInsert);
	}
	
	protected abstract void toSetValues(boolean isInsert);
	
	//PrimaryKey
	
	/** 获取第几主键/索引 */
	public abstract Object getPrimaryKeyByIndex(int index);
	
	/** 设置主键 */
	public void setPrimaryKeyByIndex(PreparedStatement ps,int index)
	{
		_ps=ps;
		toSetPrimaryKeyByIndex(index);
	}
	
	/** 设置主键加位置 */
	public void setPrimaryKeyByIndexAt(PreparedStatement ps,int index,int pos)
	{
		_ps=ps;
		_pos=pos;
		toSetPrimaryKeyByIndex(index);
	}
	
	protected abstract void toSetPrimaryKeyByIndex(int index);
	
	/** 自定义执行(只有类型为customQuery时有意义)(DBPool线程)(可复写) */
	public boolean executeCustom(Connection con)
	{
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		boolean result=true;
		
		try
		{
			switch(_customType)
			{
				//读取全部
				case CustomDBTaskType.LoadAll:
				{
					ps=con.prepareStatement(_table.getSelectStr());
					
					rs=ps.executeQuery();
					
					int len=rs.getMetaData().getColumnCount();
					
					SList<BaseTable> list=new SList<>(BaseTable[]::new,len);
					
					while(rs.next())
					{
						BaseTable table=createTable();
						
						table.readValues(rs);
						table.afterRead();
						list.add(table);
					}
					
					overCallArg=list;
				}
					break;
				//读取主键
				case CustomDBTaskType.LoadCustomKey:
				{
					String sql=_table.getSelectInStrByIndex(customKeyIndex);
					
					ps=con.prepareStatement(sql);
					
					this.setPrimaryKeyByIndex(ps,customKeyIndex);
					
					rs=ps.executeQuery();
					
					int len=rs.getMetaData().getColumnCount();
					
					SList<BaseTable> list=new SList<>(BaseTable[]::new,len);
					
					while(rs.next())
					{
						BaseTable table=createTable();
						
						table.readValues(rs);
						table.afterRead();
						
						list.add(table);
					}
					
					overCallArg=list;
				}
					break;
				case CustomDBTaskType.LoadCustomSql:
				{
					ps=con.prepareStatement(customSql);
					
					rs=ps.executeQuery();
					
					int len=rs.getMetaData().getColumnCount();
					
					SList<BaseTable> list=new SList<>(BaseTable[]::new,len);
					
					while(rs.next())
					{
						BaseTable table=createTable();
						
						table.readValues(rs);
						table.afterRead();
						
						list.add(table);
					}
					
					overCallArg=list;
				}
					break;
			}
		}
		catch(SQLException e)
		{
			Ctrl.errorLog(e);
			result=false;
		}
		
		if(!DBControl.closeResultSet(rs))
			result=false;
		
		if(!DBControl.closeStatement(ps))
			result=false;
		
		return result;
	}
}
