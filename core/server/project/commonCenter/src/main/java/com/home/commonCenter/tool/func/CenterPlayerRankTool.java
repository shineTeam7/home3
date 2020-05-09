package com.home.commonCenter.tool.func;

import com.home.commonBase.data.role.RoleShowChangeData;
import com.home.commonBase.data.role.RoleShowData;
import com.home.commonBase.data.social.rank.PlayerRankData;
import com.home.commonBase.data.social.rank.RankData;
import com.home.commonBase.global.BaseC;
import com.home.commonCenter.global.CenterC;
import com.home.commonCenter.part.centerGlobal.part.CenterSocialPart;
import com.home.shine.ctrl.Ctrl;

/** 中心服角色排行插件 */
public class CenterPlayerRankTool extends CenterRankTool
{
	public CenterPlayerRankTool(int funcID,int maxNum,int valueMin)
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
		RoleShowData roleShowData=CenterC.global.social.getRoleShowData(data.key);
		
		if(roleShowData==null)
		{
			Ctrl.warnLog("CenterPlayerRank找不到RoleShowData",data.key);
		}
		
		((PlayerRankData)data).showData=roleShowData;
	}
	
	@Override
	protected void onRemoveRank(long key)
	{
		CenterC.global.social.tryRemovePlayerRoleSocial(key);
	}
	
	@Override
	public PlayerRankData getRankData(long key)
	{
		return (PlayerRankData)super.getRankData(key);
	}
	
	public void onRefreshRoleShow(long playerID,RoleShowChangeData data)
	{
		PlayerRankData rankData=getRankData(playerID);
		
		if(rankData!=null)
		{
			rankData.showData.onChange(data);
		}
	}
}
