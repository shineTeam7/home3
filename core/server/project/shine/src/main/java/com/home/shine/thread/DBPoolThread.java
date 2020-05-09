package com.home.shine.thread;

import com.home.shine.constlist.ThreadType;
import com.home.shine.control.DBControl;
import com.home.shine.support.collection.SList;
import com.home.shine.support.collection.SMap;
import com.home.shine.support.collection.SSet;
import com.home.shine.table.task.BaseDBTask;

/** DB池线程 */
public class DBPoolThread extends BaseThread
{
	private int _tickTimes=0;
	
	private SList<BaseDBTask> _tempList=new SList<>(BaseDBTask[]::new);
	
	private SMap<Object,BaseDBTask> _tempMap=new SMap<>();
	
	private SSet<Object> _tempSet=new SSet<>();
	
	public DBPoolThread(int index)
	{
		super("dbPoolThread-"+index,ThreadType.DBPool,index);
		
		//1秒50转就好
		setSleepTime(20);
	}
	
	@Override
	protected void tick(int delay)
	{
		//super.tick(delay);
		
		++_tickTimes;
		
		//100一次强制
		if(_tickTimes==100)
		{
			_tickTimes=0;
			DBControl.runTask(this,true);
		}
		else
		{
			DBControl.runTask(this,false);
		}
	}
	
	/** 临时组 */
	public SList<BaseDBTask> getTempList()
	{
		return _tempList;
	}
	
	/** 临时Map */
	public SMap<Object,BaseDBTask> getTempMap()
	{
		return _tempMap;
	}
	
	/** 临时Set */
	public SSet<Object> getTempSet()
	{
		return _tempSet;
	}
	
	@Override
	public void copy(AbstractThread thread)
	{
		super.copy(thread);
		
		DBPoolThread thd=(DBPoolThread)thread;
		_tempList=thd._tempList;
		_tempMap=thd._tempMap;
		_tempSet=thd._tempSet;
	}
}
