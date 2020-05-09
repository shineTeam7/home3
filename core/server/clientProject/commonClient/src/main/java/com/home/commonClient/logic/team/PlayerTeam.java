package com.home.commonClient.logic.team;

import com.home.commonBase.data.social.roleGroup.RoleGroupSimpleData;
import com.home.commonBase.data.social.team.TeamSimpleData;
import com.home.commonClient.logic.func.PlayerRoleGroup;

public class PlayerTeam extends PlayerRoleGroup
{
	/** 创建公会简版数据 */
	@Override
	protected RoleGroupSimpleData toCreateRoleGroupSimpleData()
	{
		return new TeamSimpleData();
	}
}
