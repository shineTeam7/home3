package com.home.commonGame.tool.func;

import com.home.commonBase.data.social.rank.RankData;
import com.home.commonBase.data.system.KeyData;
import com.home.commonBase.tool.func.SubsectionPageShowTool;
import com.home.commonGame.logic.func.PlayerRoleGroup;
import com.home.commonGame.net.serverRequest.game.func.pageShow.FuncSendGetPageShowToGameServerRequest;
import com.home.commonGame.part.player.Player;
import com.home.shine.support.collection.SList;

public class GameSubsectionPageShowTool extends SubsectionPageShowTool
{
	public GameSubsectionPageShowTool(int funcID, int showMaxNum, int eachPageShowNum)
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
		getPageShow(player,-1,-1,page,arg);
	}

	/** 获取翻页显示 */
	public void getPageShow(Player player,int subsectionIndex,int subsectionSubIndex,int page,int arg)
	{
		if (subsectionIndex < 0 || subsectionSubIndex < 0)
		{
			subsectionIndex=player.func.getSubsectionRankTool(_funcID)._data.subsectionIndex;
			subsectionSubIndex=player.func.getSubsectionRankTool(_funcID)._data.subsectionSubIndex;
		}

		int subsectionIndex1=subsectionIndex;
		int subsectionSubIndex1=subsectionSubIndex;

		SList<KeyData> list=getPageShowList(subsectionIndex,subsectionSubIndex,page,arg);
		
		boolean isCenter=false;
		
		long key=player.role.playerID;
		
		if(_rankTool!=null)
		{
			isCenter=((IGameSubsectionRankTool)_rankTool).isCenter();
		}
		
		if(isCenter || player.isCurrentGame())
		{
			RankData rankData=getRankData(key);
			
			player.addFunc(()->
			{
				player.func.getSubsectionPageShowTool(_funcID).onReceive(subsectionIndex1,subsectionSubIndex1,page,arg,list,rankData);
			});
		}
		else
		{
//			FuncSendGetPageShowToGameServerRequest.create(_funcID,player.role.playerID,page,arg,key).sendToSourceGameByLogicID(player.role.playerID);
		}
	}
}
