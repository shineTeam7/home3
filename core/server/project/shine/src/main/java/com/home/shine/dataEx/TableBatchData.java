package com.home.shine.dataEx;

import com.home.shine.constlist.TableStateType;
import com.home.shine.control.DBControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.collection.SList;
import com.home.shine.table.BaseTable;
import com.home.shine.table.task.BaseDBTask;
import com.mysql.cj.protocol.a.NativePacketPayload;

import java.sql.Connection;
import java.sql.PreparedStatement;

/** 批量操作数据 */
public class TableBatchData
{
	private Connection _con;
	
	private String _name="";
	
	/** 插入组 */
	private SList<OneBatchData> _insertPsList=new SList<>(OneBatchData[]::new);
	/** 更新组 */
	private SList<OneBatchData> _updatePsList=new SList<>(OneBatchData[]::new);
	
	private OneBatchData _insertBatch;
	private OneBatchData _updateBatch;
	private PreparedStatement _deletePs;
	
	private int _totalSize;
	
	/** 是否出错 */
	public boolean hasError=false;
	
	private SList<BaseTable> _insertTables=new SList<>(BaseTable[]::new);
	
	public void setConnection(Connection con)
	{
		_con=con;
	}
	
	public void setName(String name)
	{
		_name=name;
	}
	
	public void addTable(BaseTable table)
	{
		if(hasError)
			return;
		
		BaseDBTask task;
		
		try
		{
			switch(table.tableState)
			{
				case TableStateType.NeedInsert:
				{
					table.setNormal();
					_insertTables.add(table);
					
					if(_insertBatch==null)
					{
						_insertBatch=new OneBatchData();
						_insertBatch.ps=_con.prepareStatement(table.getInsertStr());
						_insertPsList.add(_insertBatch);
						_insertBatch.querySize=table.getInsertStr().length();
					}
					
					task=table.insertToPs(_insertBatch.ps);
					_insertBatch.ps.addBatch();
					//_insertBatch.size+=(task.getWriteSize()+_insertBatch.querySize);
					_insertBatch.size+=task.getWriteSize();
					_insertBatch.num++;
					
					if(_insertBatch.size>=ShineSetting.mysqlMaxAllowedPacket)
					{
						_insertBatch=null;
					}
				}
					break;
				case TableStateType.NeedDelete:
				{
					if(_deletePs==null)
					{
						_deletePs=_con.prepareStatement(table.getDeleteStr());
					}
					
					table.deleteToPs(_deletePs);
					_deletePs.addBatch();
				}
					break;
				case TableStateType.Normal:
				{
					if(_updateBatch==null)
					{
						_updateBatch=new OneBatchData();
						_updateBatch.ps=_con.prepareStatement(table.getUpdateStr());
						_updatePsList.add(_updateBatch);
						_updateBatch.querySize=table.getSelectStr().length();
					}
					
					task=table.updateToPs(_updateBatch.ps,true);
					_updateBatch.ps.addBatch();
					_updateBatch.size+=(task.getWriteSize()+_updateBatch.querySize);
					_updateBatch.num++;
					
					if(_updateBatch.size>=ShineSetting.mysqlMaxAllowedPacket)
					{
						_updateBatch=null;
					}
				}
					break;
			}
		}
		catch(Exception e)
		{
			hasError=true;
			Ctrl.errorLog(e);
		}
	}
	
	/** 批次执行 */
	public void executeBatch()
	{
		if(hasError)
			return;
		
		_totalSize=0;
		
		try
		{
			
			
			OneBatchData[] values=_insertPsList.getValues();
			OneBatchData v;
			
			for(int i=0,len=_insertPsList.size();i<len;++i)
			{
				v=values[i];
				
				NativePacketPayload pp=DBControl.getNativePacketPayload(v.ps.getConnection());
				
				v.ps.executeBatch();
				
				int realSize=pp!=null ? pp.getPosition() : 0;
				
				if(ShineSetting.needDetailLog)
				{
					Ctrl.runningLog("tableBatchInsert countSize:"+v.size+" realSize:"+realSize);
				}
			}
			
			
			values=_updatePsList.getValues();
			
			for(int i=0,len=_updatePsList.size();i<len;++i)
			{
				v=values[i];
				
				NativePacketPayload pp=DBControl.getNativePacketPayload(v.ps.getConnection());
				
				v.ps.executeBatch();
				
				int realSize=pp!=null ? pp.getPosition() : 0;
				
				if(ShineSetting.needDetailLog)
				{
					Ctrl.runningLog("tableBatchUpdate countSize:"+v.size+" realSize:"+realSize);
				}
			}
			
			if(_deletePs!=null)
				_deletePs.executeBatch();
		}
		catch(Exception e)
		{
			hasError=true;
			Ctrl.errorLog(e);
		}
	}
	
	public void closePs()
	{
		OneBatchData[] values=_insertPsList.getValues();
		OneBatchData v;
		
		for(int i=0,len=_insertPsList.size();i<len;++i)
		{
			v=values[i];
			
			if(!DBControl.closeStatement(v.ps))
				hasError=true;
		}
		
		values=_updatePsList.getValues();
		
		for(int i=0,len=_updatePsList.size();i<len;++i)
		{
			v=values[i];
			
			if(!DBControl.closeStatement(v.ps))
				hasError=true;
		}
		
		if(!DBControl.closeStatement(_deletePs))
			hasError=true;
	}
	
	public void end(boolean hasError)
	{
		if(hasError)
		{
			//还原insert
			if(!_insertTables.isEmpty())
			{
				_insertTables.forEachAndClear(v->
				{
					v.setNeedInsert();
				});
			}
		}
		else
		{
			_insertTables.clear();
		}
		
		clear();
	}
	
	public void clear()
	{
		_insertBatch=null;
		_updateBatch=null;
		_deletePs=null;
		_con=null;
		hasError=false;
		_insertTables.clear();
		
		_insertPsList.clear();
		_updatePsList.clear();
		
		_totalSize=0;
	}
	
	public int getTotalWriteSize()
	{
		return _totalSize;
	}
	
	private class OneBatchData
	{
		public PreparedStatement ps;
		
		public int num;
		/** size */
		public int size;
		/** 查询头size */
		public int querySize;
	}
}
