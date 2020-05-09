package com.home.commonGame.logic.team;

import com.home.commonBase.constlist.generate.ChatChannelType;
import com.home.commonBase.constlist.generate.FunctionType;
import com.home.commonBase.global.Global;
import com.home.commonBase.scene.base.Unit;
import com.home.commonGame.logic.func.PlayerRoleGroup;
import com.home.commonGame.scene.base.GameScene;
import com.home.commonGame.tool.func.PlayerRoleGroupTool;

/** 玩家队伍工具 */
public class PlayerTeamTool extends PlayerRoleGroupTool
{
	public PlayerTeamTool()
	{
		super(FunctionType.Team,Global.teamRoleGroupID);
	}
	
	/** 创建玩家队伍 */
	@Override
	protected PlayerTeam toCreatePlayerRoleGroup()
	{
		return new PlayerTeam();
	}
	
	/** 获取唯一队伍 */
	@Override
	public PlayerTeam getOnlyOne()
	{
		return (PlayerTeam)super.getOnlyOne();
	}
	
	@Override
	protected void onJoinRoleGroup(PlayerRoleGroup roleGroup)
	{
		super.onJoinRoleGroup(roleGroup);
		
		GameScene scene;
		Unit unit;
		
		if((scene=me.scene.getScene())!=null && (unit=me.scene.getUnit())!=null)
		{
			scene.gameInOut.toAddTeamPlayer(unit,roleGroup.groupID);
		}
	}
	
	@Override
	protected void onLeaveRoleGroup(PlayerRoleGroup roleGroup)
	{
		super.onLeaveRoleGroup(roleGroup);
		
		me.social.clearChatChannel(ChatChannelType.Team);
		
		GameScene scene;
		Unit unit;
		
		if((scene=me.scene.getScene())!=null && (unit=me.scene.getUnit())!=null)
		{
			scene.gameInOut.toRemoveTeamPlayer(unit,roleGroup.groupID);
		}
	}
}
