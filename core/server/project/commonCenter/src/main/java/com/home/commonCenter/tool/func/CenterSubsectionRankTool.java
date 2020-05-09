package com.home.commonCenter.tool.func;

import com.home.commonBase.data.social.rank.RankData;
import com.home.commonBase.tool.func.SubsectionRankTool;
import com.home.commonCenter.global.CenterC;
import com.home.commonCenter.net.serverRequest.game.func.rank.subsection.FuncAddSubsectionRankToGameServerRequest;
import com.home.commonCenter.net.serverRequest.game.func.rank.subsection.FuncRefreshSubsectionRankToGameServerRequest;
import com.home.commonCenter.net.serverRequest.game.func.rank.subsection.FuncRemoveSubsectionRankToGameServerRequest;
import com.home.commonCenter.net.serverRequest.game.func.rank.subsection.FuncResetSubsectionRankToGameServerRequest;
import com.home.shine.data.DIntData;

/** 中心服排行榜基类 */
public abstract class CenterSubsectionRankTool extends SubsectionRankTool
{
	public CenterSubsectionRankTool(int funcID, int maxNum, int valueMin)
	{
		super(funcID,maxNum,valueMin);
	}
	
	@Override
	public void commitRank(int subsectionIndex,int subsectionSubIndex,int version,long key,long value,long[] args)
	{
		//需要先取old数据才能判定
		RankData oldData=getRankData(key);
		
		int re=toCommitRank(subsectionIndex,subsectionSubIndex,version,key,value,args,null);

		DIntData intData = getSubsectionIndexData(key);

		//暂时都从game服过来取
		if(re!=-1)
		{
			if(oldData==null)
			{
				CenterC.server.radioGames(FuncAddSubsectionRankToGameServerRequest.create(_funcID,intData.key,intData.value,getRankData(key)));
			}
			else
			{
				CenterC.server.radioGames(FuncRefreshSubsectionRankToGameServerRequest.create(_funcID,intData.key,intData.value,key,re,value,args));
			}
		}
		else
		{
			CenterC.server.radioGames(FuncRemoveSubsectionRankToGameServerRequest.create(_funcID,intData.key,intData.value,key));
			onRemoveRank(key);
		}
	}
	
	protected void onRemoveRank(long key)
	{
	
	}
	
	@Override
	protected void afterCommitRank(int subsectionIndex,int subsectionSubIndex,long key,int rank,long value)
	{
		//只处理移除
		if(rank==-1)
		{
			CenterC.server.radioGames(FuncRemoveSubsectionRankToGameServerRequest.create(_funcID,subsectionIndex,subsectionSubIndex,key));
			onRemoveRank(key);
		}
	}
	
	@Override
	protected void beforeReset()
	{
		CenterC.server.radioGames(FuncResetSubsectionRankToGameServerRequest.create(_funcID,getVersion()));
		
		//TODO:这里做排行清空后的center的roleSocial的remove处理
	}
}
