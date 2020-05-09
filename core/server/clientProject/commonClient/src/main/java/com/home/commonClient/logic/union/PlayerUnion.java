package com.home.commonClient.logic.union;

import com.home.commonBase.data.social.roleGroup.RoleGroupSimpleData;
import com.home.commonBase.data.social.union.UnionSimpleData;
import com.home.commonClient.logic.func.PlayerRoleGroup;

public class PlayerUnion extends PlayerRoleGroup
{
	/** 创建公会简版数据 */
	@Override
	protected RoleGroupSimpleData toCreateRoleGroupSimpleData()
	{
		return new UnionSimpleData();
	}
}
