package com.home.commonGame.logic.union;

import com.home.commonBase.constlist.generate.ChatChannelType;
import com.home.commonBase.constlist.generate.FunctionType;
import com.home.commonBase.constlist.generate.RoleShowDataPartType;
import com.home.commonBase.global.Global;
import com.home.commonGame.logic.func.PlayerRoleGroup;
import com.home.commonGame.tool.func.PlayerRoleGroupTool;

/** 玩家工会工具 */
public class PlayerUnionTool extends PlayerRoleGroupTool
{
	public PlayerUnionTool()
	{
		super(FunctionType.Union,Global.unionRoleGroupID);
	}
	
	/** 创建角色自身玩家群 */
	protected PlayerUnion toCreatePlayerRoleGroup()
	{
		return new PlayerUnion();
	}
	
	@Override
	public PlayerUnion getOnlyOne()
	{
		return (PlayerUnion)super.getOnlyOne();
	}
	
	@Override
	protected void onJoinRoleGroup(PlayerRoleGroup roleGroup)
	{
		super.onJoinRoleGroup(roleGroup);
		
		me.role.refreshPartRoleShowData(RoleShowDataPartType.Union,roleGroup.groupID,roleGroup.getData().name);
	}
	
	@Override
	protected void onLeaveRoleGroup(PlayerRoleGroup roleGroup)
	{
		super.onLeaveRoleGroup(roleGroup);
		
		me.social.clearChatChannel(ChatChannelType.Union);
		me.role.refreshPartRoleShowData(RoleShowDataPartType.Union,-1,"");
	}
}
