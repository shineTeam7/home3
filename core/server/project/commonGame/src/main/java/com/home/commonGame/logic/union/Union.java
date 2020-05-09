package com.home.commonGame.logic.union;

import com.home.commonBase.data.social.roleGroup.PlayerRoleGroupData;
import com.home.commonBase.data.social.roleGroup.RoleGroupMemberData;
import com.home.commonBase.data.social.roleGroup.RoleGroupSimpleData;
import com.home.commonBase.data.social.union.PlayerUnionData;
import com.home.commonBase.data.social.union.UnionMemberData;
import com.home.commonBase.data.social.union.UnionSimpleData;
import com.home.commonGame.logic.func.RoleGroup;

/** 工会逻辑服实体 */
public class Union extends RoleGroup
{
	/** 创建成员数据(只创建) */
	protected RoleGroupMemberData toCreateRoleGroupMemberData()
	{
		return new UnionMemberData();
	}
	
	/** 创建客户端玩家群数据(只创建) */
	protected PlayerRoleGroupData toCreatePlayerRoleGroupData()
	{
		return new PlayerUnionData();
	}
	
	/** 创建公会简版数据 */
	protected RoleGroupSimpleData toCreateRoleGroupSimpleData()
	{
		return new UnionSimpleData();
	}
}
