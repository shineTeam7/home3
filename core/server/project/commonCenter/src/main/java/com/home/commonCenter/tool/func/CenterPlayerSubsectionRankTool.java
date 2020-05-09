package com.home.commonCenter.tool.func;

import com.home.commonBase.data.role.RoleShowChangeData;
import com.home.commonBase.data.role.RoleShowData;
import com.home.commonBase.data.social.rank.PlayerRankData;
import com.home.commonBase.data.social.rank.RankData;
import com.home.commonBase.global.BaseC;
import com.home.commonCenter.global.CenterC;
import com.home.commonCenter.net.serverRequest.game.func.rank.subsection.FuncRefreshSubsectionIndexToGameServerRequest;
import com.home.commonCenter.part.centerGlobal.part.CenterSocialPart;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.SList;

/** 中心服角色分段排行插件 */
public class CenterPlayerSubsectionRankTool extends CenterSubsectionRankTool
{
	public CenterPlayerSubsectionRankTool(int funcID, int maxNum, int valueMin)
	{
		super(funcID,maxNum,valueMin);
	}
	
	@Override
	protected PlayerRankData toCreateRankData()
	{
		return BaseC.factory.createPlayerRankData();
	}
	
	@Override
	protected void makeRankData(RankData data,long[] args)
	{
		((PlayerRankData)data).showData=CenterC.global.social.getRoleShowData(data.key);
	}
	
	@Override
	protected void onRemoveRank(long key)
	{
		CenterC.global.social.tryRemovePlayerRoleSocial(key);
	}

	/** 刷新原址数据 */
	@Override
	protected void refreshSubsectionIndex(long key,int subsectionIndex,int subsectionSubIndex)
	{
		FuncRefreshSubsectionIndexToGameServerRequest.create(_funcID,subsectionIndex,subsectionSubIndex).sendToPlayer(key);
	}
	
	public void onRefreshRoleShow(long playerID,RoleShowChangeData data)
	{
		RankData rankData=getRankData(playerID);
		
		if(rankData!=null)
		{
			((PlayerRankData)rankData).showData.onChange(data);
		}
	}
}
