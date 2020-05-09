package com.home.commonTest.test;

import com.home.commonBase.data.social.rank.RankData;
import com.home.commonBase.tool.func.RankTool;

public class TRankTool extends RankTool
{
	public TRankTool(int funcID,int maxNum,int valueMin)
	{
		super(funcID,maxNum,valueMin);
	}

	@Override
	protected void reMakeData()
	{

	}
	
	@Override
	protected RankData toCreateRankData()
	{
		return null;
	}
	
	@Override
	protected void makeRankData(RankData data,long[] args)
	{
	
	}
	
	@Override
	protected void afterCommitRank(long key,int rank,long value)
	{
	
	}
	
	@Override
	protected void beforeReset()
	{
	
	}
}
