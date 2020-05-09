package com.home.commonGame.control;

import com.home.commonBase.constlist.generate.InfoCodeType;
import com.home.commonBase.data.social.roleGroup.work.PlayerToRoleGroupTCCWData;
import com.home.commonBase.data.social.roleGroup.work.RoleGroupWorkData;
import com.home.commonBase.data.system.AreaGlobalWorkData;
import com.home.commonGame.logic.func.RoleGroup;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.func.ObjectCall;
import com.home.shine.support.func.ObjectCall2;
import com.home.shine.support.func.ObjectFunc3;

public class BaseAreaWorkControl
{
	protected IntObjectMap<ObjectCall<AreaGlobalWorkData>> _dic=new IntObjectMap<>();
	
	protected IntObjectMap<ObjectCall2<RoleGroup,RoleGroupWorkData>> _roleGroupDic=new IntObjectMap<>();
	
	protected IntObjectMap<ObjectFunc3<Integer,RoleGroup,PlayerToRoleGroupTCCWData>> _roleGroupTCCDic=new IntObjectMap<>();
	
	public void init()
	{
		regist();
		registRoleGroup();
		registRoleGroupTCC();
	}
	
	protected void regist()
	{
	
	}
	
	/** 注册玩家群事务 */
	protected void registRoleGroup()
	{
	
	}
	
	/** 注册玩家群TCC事务 */
	protected void registRoleGroupTCC()
	{
	
	}
	
	/** 注册一个 */
	protected void registOne(int dataID,ObjectCall<AreaGlobalWorkData> func)
	{
		_dic.put(dataID,func);
	}
	
	/** 注册一个玩家群事务 */
	protected void registOneRoleGroup(int dataID,ObjectCall2<RoleGroup,RoleGroupWorkData> func)
	{
		_roleGroupDic.put(dataID,func);
	}
	
	/** 注册一个玩家群事务 */
	protected void registOneRoleGroupTCC(int dataID,ObjectFunc3<Integer,RoleGroup,PlayerToRoleGroupTCCWData> func)
	{
		_roleGroupTCCDic.put(dataID,func);
	}
	
	/** 执行一个离线事务数据 */
	public void execute(AreaGlobalWorkData data)
	{
		ObjectCall<AreaGlobalWorkData> func=_dic.get(data.getDataID());
		
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
	
	/** 执行一个离线事务数据 */
	public int executeRoleGroup(RoleGroup roleGroup,RoleGroupWorkData data)
	{
		ObjectFunc3<Integer,RoleGroup,PlayerToRoleGroupTCCWData> func3=_roleGroupTCCDic.get(data.getDataID());
		
		if(func3!=null)
		{
			int re=InfoCodeType.Success;
			
			try
			{
				re=func3.apply(roleGroup,(PlayerToRoleGroupTCCWData)data);
			}
			catch(Exception e)
			{
				Ctrl.errorLog(e);
			}
			
			return re;
		}
		
		ObjectCall2<RoleGroup,RoleGroupWorkData> func2=_roleGroupDic.get(data.getDataID());
		
		if(func2!=null)
		{
			try
			{
				func2.apply(roleGroup,data);
			}
			catch(Exception e)
			{
				Ctrl.errorLog(e);
			}
			
			return InfoCodeType.Success;
		}
		
		Ctrl.throwError("未找到类型为：" + data.getDataID() + "的离线事务处理");
		return InfoCodeType.WorkError;
	}
}
