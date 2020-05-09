package com.home.commonGame.tool.func;

import com.home.commonBase.constlist.generate.FuncToolType;
import com.home.commonBase.constlist.generate.FunctionType;
import com.home.commonBase.data.func.FuncToolData;
import com.home.commonBase.data.social.rank.PlayerRankToolData;
import com.home.commonBase.data.social.rank.RankData;
import com.home.commonGame.global.GameC;
import com.home.commonGame.net.request.func.rank.FuncRefreshRankRequest;
import com.home.commonGame.net.request.func.rank.FuncResetRankRequest;
import com.home.commonGame.net.serverRequest.game.func.rank.FuncCommitRankValueToSourceGameServerRequest;

/** 角色排行插件(挂在Player上的) */
public class PlayerRankTool extends PlayerFuncTool
{
	/** 下限值 */
	private long _valueMin;
	
	/** 数据 */
	protected PlayerRankToolData _data;
	/** 自身排行 */
	private int _rank=-1;
	/** 翻页插件 */
	private PlayerPageShowTool _pageTool;
	
	public PlayerRankTool(int funcID,long valueMin)
	{
		super(FuncToolType.Rank,funcID);
		
		_valueMin=valueMin;
	}
	
	@Override
	protected void toSetData(FuncToolData data)
	{
		super.toSetData(data);
		_data=(PlayerRankToolData)data;
	}
	
	public PlayerRankToolData getData()
	{
		return _data;
	}
	
	@Override
	public void init()
	{
		super.init();
		
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		
		_data=null;
		_rank=-1;
	}
	
	@Override
	public void beforeLoginOnMain()
	{
		IGameRankTool rankTool=GameC.global.func.getRankToolBase(_funcID);
		
		RankData rankData=rankTool.getRankData(me.role.playerID);
		
		if(rankData!=null)
		{
			onSetValue(rankData.rank,rankData.value);
		}
	}
	
	@Override
	public void beforeEnterOnMain()
	{
		IGameRankTool mainRankTool=GameC.global.func.getRankToolBase(_funcID);
		
		//中心服或本服
		if(mainRankTool.isCenter() || me.isCurrentGame())
		{
			int mainVersion=mainRankTool.getVersion();
			
			if(_data.version!=mainVersion)
			{
				onReset(mainVersion);
			}
			else
			{
				//非中心服且本服才处理
				if(!mainRankTool.isCenter())
				{
					//先取上一次排名
					_rank=mainRankTool.getRank(me.role.playerID);
					
					//大于下限值
					if(_data.value>=_valueMin)
					{
						//再提交一次
						GameC.global.func.getRankToolBase(_funcID).commitRank(_data.version,me.role.playerID,_data.value,_data.args);
					}
				}
			}
		}
	}
	
	protected FuncToolData createToolData()
	{
		return new PlayerRankToolData();
	}
	
	@Override
	public void onNewCreate()
	{
		super.onNewCreate();
		_data.version=1;
	}
	
	/** 获取排行 */
	public int getRank()
	{
		return _rank;
	}
	
	/** 提交匹配值(导致排序的数据) */
	public void commitValue(long value)
	{
		commitValue(value,null);
	}
	
	/** 提交匹配值(导致排序的数据) */
	public void commitValue(long value,long... args)
	{
		//大于下限值
		if(value>=_valueMin)
		{
			toCommitRank(value,args);
		}
		else
		{
			removeRank();
		}
	}
	
	private void toCommitRank(long value,long[] args)
	{
		//重复提交
		if(value==_data.value && args==null)
			return;
		
		//先修改自身匹配值,为跨服
		_data.value=value;
		_data.args=args;
		
		//提交排行数据
		int version=_data.version;
		long playerID=me.role.playerID;
		
		IGameRankTool gameRankTool=GameC.global.func.getRankToolBase(_funcID);
		
		if(gameRankTool==null)
			return;
		
		//在榜或超过下限值
		if(_rank!=-1 || value>=gameRankTool.getValueLimit())
		{
			if(gameRankTool.isCenter())
			{
				//提交数据
				me.social.commitRoleSocialForCenter();
			}
			else
			{
				//不是本服
				if(!me.isCurrentGame())
				{
					FuncCommitRankValueToSourceGameServerRequest.create(_funcID,me.role.playerID,version,value,args).send(me.role.sourceGameID);
					return;
				}
				
				//提交数据
				//me.social.commitRoleSocialForGame();
			}
			
			me.addMainFunc(()->
			{
				gameRankTool.commitRank(version,playerID,value,args);
			});
		}
	}
	
	/** 更新排行 */
	public void updateRank(int rank,long value)
	{
		updateRank(rank,value,false);
	}
	
	/** 更新排行 */
	public void updateRank(int rank,long value,boolean abs)
	{
		if(!abs && _rank==rank && _data.value==value)
			return;
		
		_rank=rank;
		_data.value=value;
		
		me.send(FuncRefreshRankRequest.create(_funcID,rank,value));
		
		onRankChange();
	}
	
	/** 更新排行数据 */
	public void updateRank(RankData data)
	{
		if(data!=null)
		{
			updateRank(data.rank,data.value);
		}
		else
		{
			updateRank(-1,0);
		}
	}
	
	/** 设置值(来自中心服)(不推送) */
	public void onSetValue(int rank,long value)
	{
		_rank=rank;
		_data.value=value;
	}
	
	/** 移除排行 */
	public void removeRank()
	{
		//当前有排名
		if(_rank!=-1)
		{
			//提交排行数据
			int version=_data.version;
			long playerID=me.role.playerID;
			
			me.addMainFunc(()->
			{
				GameC.global.func.getRankToolBase(_funcID).removeRankData(version,playerID);
				//先移除一下
				updateRank(-1,0);
			});
		}
	}
	
	/** 重置 */
	public void onReset(int version)
	{
		_rank=-1;
		int oldVersion=_data.version;
		_data.version=version;
		_data.value=0;
		
		if(_pageTool!=null)
			_pageTool.clear();
		
		me.send(FuncResetRankRequest.create(_funcID));
		
		onVersionChange(oldVersion,version);
	}
	
	/** 排行版本改变(主线程) */
	protected void onVersionChange(int oldVersion,int newVersion)
	{
	
	}
	
	/** 排名改变 */
	protected void onRankChange()
	{
	
	}
	
	/** 绑定翻页显示插件 */
	public void bindPageShowTool(int eachPageShowNum)
	{
		_pageTool=new PlayerPageShowTool(_funcID,eachPageShowNum);
		me.func.registFuncTool(_pageTool);
	}
}
