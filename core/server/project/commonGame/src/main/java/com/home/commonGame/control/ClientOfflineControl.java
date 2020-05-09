package com.home.commonGame.control;

import com.home.commonBase.data.system.ClientOfflineWorkData;
import com.home.commonGame.part.player.Player;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.func.ObjectFunc3;

/** 客户端离线模式控制 */
public class ClientOfflineControl
{
	protected IntObjectMap<Obj> _dic=new IntObjectMap<>();
	
	public void init()
	{
	
	}
	
	/** 注册一个 */
	protected void registOne(int dataID,ObjectFunc3<Boolean,Player,ClientOfflineWorkData> func)
	{
		Obj obj=new Obj();
		obj.func=func;
		_dic.put(dataID,obj);
	}
	
	/** 执行一个离线事务数据 */
	public boolean execute(Player player,ClientOfflineWorkData data)
	{
		Obj obj=_dic.get(data.getDataID());
		
		if(obj==null)
		{
			Ctrl.throwError("未找到类型为：" + data.getDataID() + "的离线事务处理");
			return false;
		}
		else
		{
			return obj.call(player,data);
		}
	}
	
	protected class Obj
	{
		public ObjectFunc3<Boolean,Player,ClientOfflineWorkData> func;
		
		public boolean call(Player me,ClientOfflineWorkData data)
		{
			try
			{
				if(func!=null)
				{
					return func.apply(me,data);
				}
			}
			catch(Exception e)
			{
				Ctrl.errorLog(e);
			}
			
			return false;
		}
	}
	
	//离线事务组
}
