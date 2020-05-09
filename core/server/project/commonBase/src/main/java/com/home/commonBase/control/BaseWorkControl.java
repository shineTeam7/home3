package com.home.commonBase.control;

import com.home.commonBase.data.system.WorkData;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.func.ObjectCall;

/** 基础事务控制 */
public class BaseWorkControl
{
	protected IntObjectMap<ObjectCall<WorkData>> _dic=new IntObjectMap<>();
	
	public void init()
	{
		regist();
	}
	
	protected void regist()
	{
	
	}
	
	/** 注册一个 */
	protected void registOne(int dataID,ObjectCall<WorkData> func)
	{
		_dic.put(dataID,func);
	}
	
	/** 执行一个事务数据 */
	public void execute(WorkData data)
	{
		ObjectCall<WorkData> func=_dic.get(data.getDataID());
		
		if(func==null)
		{
			Ctrl.throwError("未找到类型为：" + data.getDataID() + "的事务处理");
		}
		else
		{
			try
			{
				func.apply(data);
			}
			catch(Exception e)
			{
				Ctrl.errorLog(e);
			}
		}
	}
}
