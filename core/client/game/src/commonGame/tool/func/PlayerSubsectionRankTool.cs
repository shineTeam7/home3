using System;
using ShineEngine;

/// <summary>
/// 角色分段排行插件
/// </summary>
public class PlayerSubsectionRankTool:PlayerFuncTool
{
	/** 小组index */
	private int _subsectionSubIndex;
	/** 大组index */
	private int _subsectionIndex;
	
	/** 数据 */
	private PlayerRankData _rankData;

	/** 翻页显示插件 */
	private PlayerSubsectionPageShowTool _pageShowTool;

	public PlayerSubsectionRankTool(int funcID):base(FuncToolType.SubsectionRank,funcID)
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

		SubsectionRankSimpleData dd=(SubsectionRankSimpleData)data;

		_rankData=GameC.factory.createPlayerRankData();
		_rankData.key=me.role.playerID;
		_rankData.showData=me.role.getSelfRoleShowData();

		_rankData.rank=dd.rank;
		_rankData.value=dd.value;

		_subsectionIndex = dd.subsectionIndex;
		_subsectionSubIndex = dd.subsectionSubIndex;
		
		if (_pageShowTool != null)
		{
			_pageShowTool.setSubsectionIndex(_subsectionIndex,_subsectionSubIndex);
		}
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
		
		me.dispatch(GameEventType.FuncRefreshRank,_funcID);
	}

	/** 绑定翻页显示插件 */
	public void bindPageShowTool(int eachShowNum)
	{
		me.func.registFuncTool(_pageShowTool=new PlayerSubsectionPageShowTool(_funcID,eachShowNum));
	}

	/** 获取翻页显示插件 */
	public PlayerSubsectionPageShowTool getPageShowTool()
	{
		return _pageShowTool;
	}
}