package com.home.commonClient.tool.func;

import com.home.commonBase.constlist.generate.FuncToolType;
import com.home.commonBase.constlist.generate.GameEventType;
import com.home.commonBase.data.func.FuncToolData;
import com.home.commonBase.data.social.rank.PlayerRankData;
import com.home.commonBase.data.social.rank.RankSimpleData;
import com.home.commonBase.global.BaseC;

public class PlayerRankTool extends PlayerFuncTool
{
	/** 数据 */
	private PlayerRankData _rankData=null;
	
	/** 翻页显示插件 */
	private PlayerPageShowTool _pageShowTool;
	
	public PlayerRankTool(int funcID)
	{
		super(FuncToolType.Rank,funcID);
	}
	
	protected PlayerRankData toCreateRankData()
	{
		return new PlayerRankData();
	}
	
	/** 获取自身排行 */
	public int getRank()
	{
		return _rankData.rank;
	}
	
	/** 获取排行数据 */
	public PlayerRankData getRankData()
	{
		return _rankData;
	}
	
	@Override
	protected void toSetData(FuncToolData data)
	{
		super.toSetData(data);
		
		RankSimpleData dd=(RankSimpleData)data;
		
		_rankData=BaseC.factory.createPlayerRankData();
		_rankData.key=me.role.playerID;
		_rankData.showData=me.role.getSelfRoleShowData();
		
		_rankData.rank=dd.rank;
		_rankData.value=dd.value;
	}
	
	/** 刷新排行 */
	public void onRefreshRank(int rank)
	{
		_rankData.rank=rank;
		
		me.dispatch(GameEventType.FuncRefreshRank,_funcID);
	}
	
	/** 重置排行榜 */
	public void onResetRank()
	{
		_rankData.value=0L;
		_rankData.rank=-1;
		
		me.dispatch(GameEventType.FuncRefreshRank,_funcID);
	}
	
	/** 绑定翻页显示插件 */
	public void bindPageShowTool(int eachShowNum)
	{
		me.func.registFuncTool(_pageShowTool=new PlayerPageShowTool(_funcID,eachShowNum));
	}
	
	/** 获取翻页显示插件 */
	public PlayerPageShowTool getPageShowTool()
	{
		return _pageShowTool;
	}
}
