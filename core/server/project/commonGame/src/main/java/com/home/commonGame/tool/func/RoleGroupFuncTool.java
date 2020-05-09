package com.home.commonGame.tool.func;

import com.home.commonBase.tool.func.FuncTool;
import com.home.commonGame.logic.func.RoleGroup;

public class RoleGroupFuncTool extends FuncTool implements IRoleGroupFuncTool
{
	/** 玩家群 */
	public RoleGroup me;
	
	public RoleGroupFuncTool(int type,int funcID)
	{
		super(type,funcID);
	}
	
	@Override
	public void setMe(RoleGroup roleGroup)
	{
		me=roleGroup;
	}
	
	@Override
	public void afterReadDataSecond()
	{
	
	}
	
	@Override
	public void beforeLogin()
	{
	
	}
	
	@Override
	public long countFightForce()
	{
		return 0;
	}
}
