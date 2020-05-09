using System;
using ShineEngine;

/// <summary>
/// 角色排行插件
/// </summary>
public class PlayerRankTool:PlayerFuncTool
{
	/** 数据 */
	private PlayerRankData _rankData;

	/** 翻页显示插件 */
	private PlayerPageShowTool _pageShowTool;

	public PlayerRankTool(int funcID):base(FuncToolType.Rank,funcID)
	{
		
	}

	/** 获取显示排行 */
	public int getRank()
	{
		return _rankData.rank;
	}

	/** 获取排行数据 */
	public PlayerRankData getRankData()
	{
		return _rankData;
	}

	protected override void toSetData(FuncToolData data)
	{
		base.toSetData(data);

		RankSimpleData dd=(RankSimpleData)data;

		_rankData=GameC.factory.createPlayerRankData();
		_rankData.key=me.role.playerID;
		_rankData.showData=me.role.getSelfRoleShowData();

		_rankData.rank=dd.rank;
		_rankData.value=dd.value;
	}

	/** 刷新排行 */
	public void onRefreshRank(int rank,long value)
	{
		_rankData.rank=rank;
		_rankData.value=value;

		me.dispatch(GameEventType.FuncRefreshRank,_funcID);
	}

	/** 重置排行榜 */
	public void onResetRank()
	{
		_rankData.value=0L;
		_rankData.rank=-1;
		
		if(_pageShowTool!=null)
			_pageShowTool.clear();
		
		me.dispatch(GameEventType.FuncResetRank,_funcID);
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