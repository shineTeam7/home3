package com.home.commonLogin.control;

import com.home.commonBase.data.system.UserWorkData;
import com.home.commonBase.table.table.UserTable;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.func.ObjectCall2;

public class BaseUserWorkControl
{
	protected IntObjectMap<ObjectCall2<UserTable,UserWorkData>> _dic=new IntObjectMap<>();
	
	public void init()
	{
		regist();
	}
	
	protected void regist()
	{
	
	}
	
	/** 注册一个 */
	protected void registOne(int dataID,ObjectCall2<UserTable,UserWorkData> func)
	{
		_dic.put(dataID,func);
	}
	
	/** 执行一个离线事务数据 */
	public void execute(UserTable table,UserWorkData data)
	{
		ObjectCall2<UserTable,UserWorkData> func=_dic.get(data.getDataID());
		
		if(func==null)
		{
			Ctrl.throwError("未找到类型为：" + data.getDataID() + "的离线事务处理");
		}
		else
		{
			try
			{
				func.apply(table,data);
			}
			catch(Exception e)
			{
				Ctrl.errorLog(e);
			}
		}
	}
}
