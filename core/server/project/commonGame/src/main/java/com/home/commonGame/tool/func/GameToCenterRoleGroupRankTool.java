package com.home.commonGame.tool.func;

import com.home.commonBase.data.role.RoleShowChangeData;
import com.home.commonBase.data.social.rank.PlayerRankData;
import com.home.commonBase.data.social.rank.RankData;
import com.home.commonBase.data.social.rank.RoleGroupRankData;
import com.home.commonBase.data.social.roleGroup.RoleGroupChangeData;
import com.home.commonBase.data.social.roleGroup.RoleGroupSimpleData;
import com.home.commonGame.global.GameC;
import com.home.shine.ctrl.Ctrl;

/** 游戏服到中心服玩家群排行插件 */
public class GameToCenterRoleGroupRankTool extends GameToCenterRankTool implements IGameRoleGroupRankTool
{
	private GameRoleGroupTool _tool;
	
	public GameToCenterRoleGroupRankTool(int funcID,GameRoleGroupTool tool,int maxNum,long valueMin)
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
	protected void afterCommitRank(long key,int rank,long value)
	{
		_tool.getRoleGroupAbs(key,v->
		{
			if(v!=null)
			{
				v.getRankTool(_funcID).updateRank(rank,value,true);
			}
		});
	}
	
	/** 获取角色排行数据 */
	public RoleGroupRankData getRoleGroupRankData(long key)
	{
		return (RoleGroupRankData)_dic.get(key);
	}
	
	/** 刷新角色外显数据(来自中心服) */
	public void onRefreshRoleSimpleDataFromCenter(long key,RoleGroupChangeData data)
	{
		RoleGroupRankData rData=getRoleGroupRankData(key);
		
		if(rData!=null)
		{
			//更新
			rData.simpleData.onRoleGroupChange(data);
		}
	}
	
	/** 全部构造数据 */
	public void reMakeData()
	{
		RankData[] values=_data.list.getValues();
		RoleGroupRankData v;
		
		RoleGroupSimpleData sData;
		
		//显示数据赋值
		for(int i=0,len=_data.list.size();i<len;++i)
		{
			remakeOne(values[i]);
		}
	}
	
	protected void remakeOne(RankData data)
	{
		RoleGroupSimpleData sData;
		if((sData=_tool.getRoleGroupSimpleData(data.key))!=null)
		{
			((RoleGroupRankData)data).simpleData=sData;
		}
	}
	
	
}
