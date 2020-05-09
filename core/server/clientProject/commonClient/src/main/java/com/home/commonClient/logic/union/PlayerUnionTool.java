package com.home.commonClient.logic.union;

import com.home.commonBase.constlist.generate.FunctionType;
import com.home.commonBase.global.Global;
import com.home.commonClient.logic.func.PlayerRoleGroup;
import com.home.commonClient.tool.func.PlayerRoleGroupTool;

public class PlayerUnionTool extends PlayerRoleGroupTool
{
	public PlayerUnionTool()
	{
		super(FunctionType.Union,Global.unionRoleGroupID);
	}
	
	
	/** 创建角色自身玩家群 */
	@Override
	protected PlayerRoleGroup toCreatePlayerRoleGroup()
	{
		return new PlayerUnion();
	}
}
