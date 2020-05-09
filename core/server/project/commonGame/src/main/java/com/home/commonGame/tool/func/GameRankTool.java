package com.home.commonGame.tool.func;

import com.home.commonBase.tool.func.RankTool;

public abstract class GameRankTool extends RankTool implements IGameRankTool
{
	public GameRankTool(int funcID,int maxNum,int valueMin)
	{
		super(funcID,maxNum,valueMin);
	}
	
	@Override
	public boolean isCenter()
	{
		return false;
	}
}
