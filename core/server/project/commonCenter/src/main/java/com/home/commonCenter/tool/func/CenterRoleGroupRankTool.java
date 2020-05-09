package com.home.commonCenter.tool.func;

import com.home.commonBase.data.social.rank.PlayerRankData;
import com.home.commonBase.data.social.rank.RankData;
import com.home.commonBase.data.social.rank.RoleGroupRankData;
import com.home.commonBase.data.social.roleGroup.RoleGroupChangeData;
import com.home.commonBase.data.social.roleGroup.RoleGroupSimpleData;
import com.home.commonBase.global.BaseC;
import com.home.commonCenter.global.CenterC;
import com.home.shine.ctrl.Ctrl;

/** 中心服玩家群排行插件 */
public class CenterRoleGroupRankTool extends CenterRankTool
{
	private CenterRoleGroupTool _tool;
	
	public CenterRoleGroupRankTool(int funcID,CenterRoleGroupTool tool,int maxNum,int valueMin)
	{
		super(funcID,maxNum,valueMin);
		_tool=tool;
	}
	
	@Override
	public void init()
	{
		super.init();
		
		if(_tool==null)
		{
			Ctrl.throwError("没有进行RoleGroup绑定");
		}
	}
	
	public int getRoleGroupFuncID()
	{
		return _tool.getFuncID();
	}
	
	@Override
	protected RankData toCreateRankData()
	{
		return BaseC.factory.createRoleGroupRankData();
	}
	
	@Override
	protected void makeRankData(RankData data,long[] args)
	{
		RoleGroupRankData rData=(RoleGroupRankData)data;
		rData.simpleData=_tool.getRoleGroup(rData.key);
	}
	
	@Override
	public RoleGroupRankData getRankData(long key)
	{
		return (RoleGroupRankData)super.getRankData(key);
	}
	
	/** 提交匹配值(导致排序的数据) */
	public void commitValue(RoleGroupSimpleData rData,long value)
	{
		commitValue(rData,value,null);
	}
	
	/** 提交匹配值(导致排序的数据) */
	public void commitValue(RoleGroupSimpleData rData,long value,long... args)
	{
		//大于下限值
		if(value>=_valueMin)
		{
			commitRank(_data.version,rData.groupID,value,args);
		}
		else
		{
			removeRankData(_data.version,rData.groupID);
		}
	}
	
	public void onRefreshPartRoleGroup(long groupID,RoleGroupChangeData data)
	{
		RoleGroupRankData rankData=getRankData(groupID);
		
		if(rankData!=null)
		{
			rankData.simpleData.onRoleGroupChange(data);
		}
	}
}
