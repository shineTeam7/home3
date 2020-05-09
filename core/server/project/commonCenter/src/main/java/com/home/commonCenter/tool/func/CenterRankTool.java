package com.home.commonCenter.tool.func;

import com.home.commonBase.data.social.rank.RankData;
import com.home.commonBase.tool.func.RankTool;
import com.home.commonCenter.global.CenterC;
import com.home.commonCenter.net.serverRequest.game.func.rank.FuncAddRankToGameServerRequest;
import com.home.commonCenter.net.serverRequest.game.func.rank.FuncRefreshRankToGameServerRequest;
import com.home.commonCenter.net.serverRequest.game.func.rank.FuncRemoveRankToGameServerRequest;
import com.home.commonCenter.net.serverRequest.game.func.rank.FuncResetRankToGameServerRequest;

/** 中心服排行榜基类 */
public abstract class CenterRankTool extends RankTool
{
	public CenterRankTool(int funcID,int maxNum,int valueMin)
	{
		super(funcID,maxNum,valueMin);
	}
	
	@Override
	public void commitRank(int version,long key,long value,long[] args)
	{
		//需要先取old数据才能判定
		RankData oldData=getRankData(key);
		
		int re=toCommitRank(version,key,value,args,null);
		
		if(re!=-1)
		{
			if(oldData==null)
			{
				CenterC.server.radioGames(FuncAddRankToGameServerRequest.create(_funcID,getRankData(key)));
			}
			else
			{
				CenterC.server.radioGames(FuncRefreshRankToGameServerRequest.create(_funcID,key,re,value,args));
			}
		}
		else
		{
			CenterC.server.radioGames(FuncRemoveRankToGameServerRequest.create(_funcID,key));
			onRemoveRank(key);
		}
	}
	
	protected void onRemoveRank(long key)
	{
	
	}
	
	@Override
	protected void afterCommitRank(long key,int rank,long value)
	{
		//只处理移除
		if(rank==-1)
		{
			CenterC.server.radioGames(FuncRemoveRankToGameServerRequest.create(_funcID,key));
			onRemoveRank(key);
		}
	}
	
	@Override
	protected void beforeReset()
	{
		CenterC.server.radioGames(FuncResetRankToGameServerRequest.create(_funcID,getVersion()));
		
		//TODO:这里做排行清空后的center的roleSocial的remove处理
	}
}
