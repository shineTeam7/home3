package com.home.commonGame.tool.func;

import com.home.commonBase.data.role.RoleShowChangeData;
import com.home.commonBase.data.social.rank.PlayerRankData;
import com.home.commonGame.global.GameC;
import com.home.commonGame.part.player.Player;

/** 游戏服到中心服排行插件 */
public class GameToCenterPlayerRankTool extends GameToCenterRankTool
{
	public GameToCenterPlayerRankTool(int funcID,int maxNum,long valueMin)
	{
		super(funcID,maxNum,valueMin);
	}
	
	/** 重置排行榜 */
	public void onReset(int version)
	{
		_data.list.clear();
		_dic.clear();
		_data.version=version;
		
		if(_pageShowTool!=null)
			_pageShowTool.clear();
		
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
	}
	
	/** 获取角色排行数据 */
	public PlayerRankData getPlayerRankData(long key)
	{
		return (PlayerRankData)_dic.get(key);
	}
	
	/** 刷新角色外显数据(来自中心服) */
	public void onRefreshRankRoleSocialDataFromCenter(long key,RoleShowChangeData data)
	{
		PlayerRankData rData=getPlayerRankData(key);
		
		if(rData!=null)
		{
			//更新
			rData.showData.onChange(data);
		}
	}
}
