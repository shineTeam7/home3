package com.home.commonGame.logic.team;

import com.home.commonBase.data.social.roleGroup.CreateRoleGroupData;
import com.home.commonBase.data.social.roleGroup.PlayerRoleGroupData;
import com.home.commonBase.data.social.roleGroup.RoleGroupData;
import com.home.commonBase.data.social.roleGroup.RoleGroupMemberData;
import com.home.commonBase.data.social.roleGroup.RoleGroupSimpleData;
import com.home.commonBase.data.social.team.CreateTeamData;
import com.home.commonBase.data.social.team.PlayerTeamData;
import com.home.commonBase.data.social.team.TeamData;
import com.home.commonBase.data.social.team.TeamMemberData;
import com.home.commonBase.data.social.team.TeamSimpleData;
import com.home.commonGame.logic.func.RoleGroup;

public class Team extends RoleGroup
{
	private TeamData _teamData;
	
	/** 创建成员数据(只创建) */
	@Override
	protected RoleGroupMemberData toCreateRoleGroupMemberData()
	{
		return new TeamMemberData();
	}
	
	/** 创建客户端玩家群数据(只创建) */
	@Override
	protected PlayerRoleGroupData toCreatePlayerRoleGroupData()
	{
		return new PlayerTeamData();
	}
	
	/** 创建公会简版数据 */
	@Override
	protected RoleGroupSimpleData toCreateRoleGroupSimpleData()
	{
		return new TeamSimpleData();
	}
	
	@Override
	public void setData(RoleGroupData data)
	{
		super.setData(data);
		
		_teamData=(TeamData)data;
	}
	
	@Override
	public void onNewCreate(CreateRoleGroupData createData)
	{
		super.onNewCreate(createData);
		
		CreateTeamData tData=(CreateTeamData)createData;
		_teamData.targetID=tData.targetID;
	}
	
	@Override
	protected void makeSimpleData(RoleGroupSimpleData data)
	{
		super.makeSimpleData(data);
		
		TeamSimpleData tData=(TeamSimpleData)data;
		tData.targetID=_teamData.targetID;
	}
}
