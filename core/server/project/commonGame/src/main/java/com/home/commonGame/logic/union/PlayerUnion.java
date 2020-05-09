package com.home.commonGame.logic.union;

import com.home.commonBase.constlist.generate.RoleGroupChangeType;
import com.home.commonBase.constlist.generate.RoleShowDataPartType;
import com.home.commonBase.data.social.roleGroup.PlayerRoleGroupSaveData;
import com.home.commonBase.data.social.roleGroup.RoleGroupChangeData;
import com.home.commonBase.data.social.roleGroup.RoleGroupSimpleData;
import com.home.commonBase.data.social.union.PlayerUnionSaveData;
import com.home.commonBase.data.social.union.UnionSimpleData;
import com.home.commonGame.logic.func.PlayerRoleGroup;

/** 玩家身上工会 */
public class PlayerUnion extends PlayerRoleGroup
{
	/** 创建角色存库数据 */
	public PlayerRoleGroupSaveData toCreatePlayerRoleGroupSaveData()
	{
		return new PlayerUnionSaveData();
	}
	
	/** 创建公会简版数据 */
	protected RoleGroupSimpleData toCreateRoleGroupSimpleData()
	{
		return new UnionSimpleData();
	}
	
	@Override
	protected void doRoleGroupChange(RoleGroupChangeData data)
	{
		super.doRoleGroupChange(data);
		
		if(data.type==RoleGroupChangeType.Name)
		{
			me.role.refreshPartRoleShowData(RoleShowDataPartType.Union,groupID,_d.name);
		}
	}
}
