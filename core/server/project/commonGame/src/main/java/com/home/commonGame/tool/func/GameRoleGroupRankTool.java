package com.home.commonGame.tool.func;

import com.home.commonBase.data.social.rank.RankData;
import com.home.commonBase.data.social.rank.RoleGroupRankData;
import com.home.commonBase.data.social.roleGroup.RoleGroupChangeData;
import com.home.commonBase.data.social.roleGroup.RoleGroupSimpleData;
import com.home.commonBase.global.BaseC;
import com.home.commonGame.global.GameC;
import com.home.commonGame.logic.func.RoleGroup;
import com.home.shine.ctrl.Ctrl;

public class GameRoleGroupRankTool extends GameRankTool implements IGameRoleGroupRankTool
{
	private GameRoleGroupTool _tool;
	
	public GameRoleGroupRankTool(int funcID,GameRoleGroupTool tool,int maxNum,int valueMin)
	{
		super(funcID,maxNum,valueMin);
		_tool=tool;
	}
	
	@Override
	public int getRoleGroupFuncID()
	{
		return _tool.getFuncID();
	}
	
	@Override
	public void init()
	{
		super.init();
		
		if(_tool==null)
		{
			Ctrl.throwError("没有进行玩家群绑定");
		}
	}
	
	@Override
	protected RoleGroupRankData toCreateRankData()
	{
		return BaseC.factory.createRoleGroupRankData();
	}
	
	@Override
	protected void makeRankData(RankData data,long[] args)
	{
		RoleGroup roleGroup=_tool.getRoleGroup(data.key);
		
		if(roleGroup!=null)
		{
			//((RoleGroupRankData)data).simpleData=roleGroup.createSimpleData();
			((RoleGroupRankData)data).simpleData=_tool.getRoleGroupSimpleData(data.key);
		}
	}
	
	
	@Override
	protected void afterCommitRank(long key,int rank,long value)
	{
		RoleGroup roleGroup=_tool.getRoleGroup(key);
		
		if(roleGroup!=null)
		{
			RoleGroupRankTool rankTool=roleGroup.getRankTool(_funcID);
			rankTool.updateRank(rank,value,true);
		}
	}
	
	@Override
	protected void beforeReset()
	{
		int version=_data.version;
		
		//推送所有工会
		
		RankData[] values=_data.list.getValues();
		RankData v;
		
		for(int i=0,len=_data.list.size();i<len;++i)
		{
			v=values[i];
			
			RoleGroup roleGroup=_tool.getRoleGroup(v.key);
			
			if(roleGroup!=null)
			{
				roleGroup.getRankTool(_funcID).onReset(version);
			}
		}
	}
	
	@Override
	public RoleGroupRankData getRankData(long key)
	{
		return (RoleGroupRankData)super.getRankData(key);
	}
	
	public void onRefreshPartRoleGroup(long groupID,RoleGroupChangeData data)
	{
		RoleGroupRankData rankData=getRankData(groupID);
		
		if(rankData!=null)
		{
			rankData.simpleData.onRoleGroupChange(data);
		}
	}
	
	/** 绑定翻页显示插件 */
	public void bindPageShowTool(int showMaxNum,int eachPageShowNum)
	{
		GamePageShowTool pageShowTool=new GamePageShowTool(_funcID,showMaxNum,eachPageShowNum);
		pageShowTool.setRankTool(this);
		pageShowTool.setRoleGroupFuncID(_tool.getFuncID());
		
		GameC.global.func.registFuncTool(pageShowTool);
	}
	

}
