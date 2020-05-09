package com.home.commonClient.logic.team;

import com.home.commonBase.constlist.generate.FunctionType;
import com.home.commonBase.global.Global;
import com.home.commonClient.logic.func.PlayerRoleGroup;
import com.home.commonClient.tool.func.PlayerRoleGroupTool;

public class PlayerTeamTool extends PlayerRoleGroupTool
{
	public PlayerTeamTool()
	{
		super(FunctionType.Team,Global.teamRoleGroupID);
	}
	
	
	/** 创建角色自身玩家群 */
	@Override
	protected PlayerRoleGroup toCreatePlayerRoleGroup()
	{
		return new PlayerTeam();
	}
}
