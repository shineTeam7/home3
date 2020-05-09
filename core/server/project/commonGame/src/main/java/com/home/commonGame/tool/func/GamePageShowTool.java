package com.home.commonGame.tool.func;

import com.home.commonBase.data.social.rank.RankData;
import com.home.commonBase.data.system.KeyData;
import com.home.commonBase.tool.func.PageShowTool;
import com.home.commonGame.logic.func.PlayerRoleGroup;
import com.home.commonGame.net.serverRequest.game.func.pageShow.FuncReGetPageShowGameToPlayerServerRequest;
import com.home.commonGame.net.serverRequest.game.func.pageShow.FuncSendGetPageShowToGameServerRequest;
import com.home.commonGame.part.player.Player;
import com.home.shine.support.collection.SList;

public class GamePageShowTool extends PageShowTool implements IGamePageShowTool
{
	public GamePageShowTool(int funcID,int showMaxNum,int eachPageShowNum)
	{
		super(funcID,showMaxNum,eachPageShowNum);
	}
	
	/** 获取本服或中心副的排行数据 */
	protected RankData getRankData(long key)
	{
		return _rankTool!=null ? _rankTool.getRankData(key) :null;
	}
	
	/** 获取翻页显示 */
	public void getPageShow(Player player,int page,int arg)
	{
		SList<KeyData> list=getPageShowList(page,arg);
		
		boolean isCenter=false;
		
		long key=player.role.playerID;
		
		if(_rankTool!=null)
		{
			isCenter=((IGameRankTool)_rankTool).isCenter();
			
			if(_rankTool instanceof GamePlayerRankTool)
			{
				key=player.role.playerID;
			}
			else if(_rankTool instanceof IGameRoleGroupRankTool)
			{
				PlayerRoleGroup roleGroup=player.func.getRoleGroupTool(_roleGroupFuncID).getOnlyOne();
				
				key=roleGroup!=null ? roleGroup.groupID : -1;
			}
		}
		
		if(isCenter || player.isCurrentGame())
		{
			RankData rankData=getRankData(key);
			
			player.addFunc(()->
			{
				player.func.getPageShowTool(_funcID).onReceive(page,arg,list,rankData);
			});
		}
		else
		{
			FuncSendGetPageShowToGameServerRequest.create(_funcID,player.role.playerID,page,arg,key).sendToSourceGameByLogicID(player.role.playerID);
		}
	}
	
	/** 收到其他服务器的获取翻页显示 */
	public void onGetPageShow(long playerID,int page,int arg,long key)
	{
		SList<KeyData> list=getPageShowList(page,arg);
		RankData rankData=getRankData(key);
		
		FuncReGetPageShowGameToPlayerServerRequest.create(_funcID,page,arg,list,rankData).sendToPlayer(playerID);
	}
}
