package com.home.commonCenter.control;

import com.home.commonBase.data.system.CenterGlobalWorkData;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.func.ObjectCall;

public class BaseCenterWorkControl
{
	protected IntObjectMap<ObjectCall<CenterGlobalWorkData>> _dic=new IntObjectMap<>();
	
	public void init()
	{
		regist();
	}
	
	protected void regist()
	{
	
	}
	
	/** 注册一个 */
	protected void registOne(int dataID,ObjectCall<CenterGlobalWorkData> func)
	{
		_dic.put(dataID,func);
	}
	
	/** 执行一个离线事务数据 */
	public void execute(CenterGlobalWorkData data)
	{
		ObjectCall<CenterGlobalWorkData> func=_dic.get(data.getDataID());
		
		if(func!=null)
		{
			try
			{
				func.apply(data);
			}
			catch(Exception e)
			{
				Ctrl.errorLog(e);
			}
			
			return;
		}
		
		Ctrl.throwError("未找到类型为：" + data.getDataID() + "的离线事务处理");
	}
}
