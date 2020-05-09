package com.home.commonGame.tool.func;

import com.home.commonBase.data.role.RoleShowChangeData;
import com.home.commonBase.data.role.RoleShowData;
import com.home.commonBase.data.social.rank.PlayerRankData;
import com.home.commonBase.data.social.rank.RankData;
import com.home.commonBase.global.BaseC;
import com.home.commonGame.global.GameC;
import com.home.commonGame.part.gameGlobal.part.GameSocialPart;
import com.home.commonGame.part.player.Player;
import com.home.shine.ctrl.Ctrl;

/** 游戏服角色排行插件 */
public class GamePlayerRankTool extends GameRankTool
{
	public GamePlayerRankTool(int funcID,int maxNum,int valueMin)
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
		((PlayerRankData)data).showData=GameC.global.social.getRoleShowData(data.key);
	}
	
	@Override
	protected void afterCommitRank(long key,int rank,long value)
	{
		Player player=GameC.main.getPlayerByID(key);
		
		if(player!=null)
		{
			player.addFunc(()->
			{
				player.func.getRankTool(_funcID).updateRank(rank,value,true);
			});
		}
		//切换中
		else if(GameC.gameSwitch.isPlayerSwitched(key))
		{
			//推送回目标服
			
		}
	}
	
	@Override
	protected void beforeReset()
	{
		int version=_data.version;

		//推送所有在线角色
		
		GameC.main.getPlayers().forEachValue(v->
		{
			onPlayerReset(v,version);
		});
	}
	
	private void onPlayerReset(Player player,int version)
	{
		player.addFunc(()->
		{
			player.func.getRankTool(_funcID).onReset(version);
		});
	}
	
	/** 获取角色排行数据(没有返回null) */
	public PlayerRankData getPlayerRankData(long playerID)
	{
		return getRankData(playerID);
	}
	
	@Override
	public PlayerRankData getRankData(long key)
	{
		return (PlayerRankData)super.getRankData(key);
	}
	
	/** 绑定翻页显示插件 */
	public void bindPageShowTool(int showMaxNum,int eachPageShowNum)
	{
		GamePageShowTool pageShowTool=new GamePageShowTool(_funcID,showMaxNum,eachPageShowNum);
		pageShowTool.setRankTool(this);
		
		GameC.global.func.registFuncTool(pageShowTool);
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
