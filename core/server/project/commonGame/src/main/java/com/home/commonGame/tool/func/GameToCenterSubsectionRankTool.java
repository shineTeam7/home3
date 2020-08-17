package com.home.commonGame.tool.func;

import com.home.commonBase.config.game.SubsectionRankConfig;
import com.home.commonBase.constlist.generate.FuncToolType;
import com.home.commonBase.data.social.rank.RankData;
import com.home.commonBase.data.social.rank.RankToolData;
import com.home.commonBase.data.social.rank.SubsectionRankToolData;
import com.home.commonBase.tool.func.FuncTool;
import com.home.commonGame.global.GameC;
import com.home.commonGame.net.serverRequest.center.func.rank.subsection.FuncCommitSubsectionRankValueToCenterServerRequest;
import com.home.commonGame.net.serverRequest.center.func.rank.subsection.FuncRemoveSubsectionRankToCenterServerRequest;
import com.home.commonGame.part.player.Player;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.data.DIntData;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.collection.IntLongMap;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.collection.LongObjectMap;
import com.home.shine.support.collection.SList;

public class GameToCenterSubsectionRankTool extends FuncTool implements IGameSubsectionRankTool
{
	/** 最大尺寸 */
	private int _maxNum;
	/** 最小排行值 */
	private long _valueMin;
	/** 数据 */
	protected SubsectionRankToolData _data;
	/** 当前字典 */
	protected LongObjectMap<RankData> _dic=new LongObjectMap<>(RankData[]::new);
	/** 排行值下限 */
	private volatile long _valueLimit=0L;
	/** 翻页插件 */
	protected GameSubsectionPageShowTool _pageShowTool;
	/** 排行榜失效时间(s),间隔同步一次 */
	private int _invalidTime=0;

	public GameToCenterSubsectionRankTool(int funcID, int maxNum, long valueMin)
	{
		super(FuncToolType.SubsectionRank,funcID);
		
		_maxNum=maxNum;
		_valueMin=valueMin;
	}
	
	@Override
	public boolean isCenter()
	{
		return true;
	}
	
	/** 绑定翻页显示插件 */
	public GameSubsectionPageShowTool bindPageShowTool(int showMaxNum,int eachPageShowNum)
	{
		//这里还是用GamePageShowTool,不从中心服取
		_pageShowTool=GameC.factory.createGameSubsectionPageShowTool(_funcID,showMaxNum,eachPageShowNum);
		_pageShowTool.setRankTool(this);
		
		GameC.global.func.registFuncTool(_pageShowTool);
		
		return _pageShowTool;
	}
	
	@Override
	public SList<RankData> getList(int subsectionIndex,int subsectionSubIndex)
	{
		if(!_data.listListMap.contains(subsectionIndex))
			return null;

		if(_data.listListMap.get(subsectionIndex).length()<=subsectionSubIndex)
			return null;

		return _data.listListMap.get(subsectionIndex).get(subsectionSubIndex);
	}

	private SList<RankData> getList2(int subsectionIndex,int subsectionSubIndex)
	{
		SList<SList<RankData>> sListSList=null;

		if(!_data.listListMap.contains(subsectionIndex))
		{
			_data.listListMap.put(subsectionIndex,sListSList=new SList<>());
		}
		else
		{
			sListSList = _data.listListMap.get(subsectionIndex);
		}

		if (subsectionSubIndex < sListSList.length())
		{
			return sListSList.get(subsectionSubIndex);
		}
		else
		{
			for(int i=sListSList.length()-1;i<subsectionSubIndex;++i)
			{
				sListSList.add(new SList<>(RankData[]::new));
			}

			return sListSList.getLast();
		}
	}

	@Override
	public int getVersion()
	{
		return _data.version;
	}
	
	@Override
	public long getValueLimit(int subsectionIndex,int subsectionSubIndex)
	{
		return _valueLimit;
	}
	
	@Override
	public int getRank(long key)
	{
		RankData data;
		
		if((data=_dic.get(key))!=null)
		{
			return data.rank;
		}
		
		return -1;
	}
	
	/** 获取排行数据 */
	public RankData getRankData(long key)
	{
		return _dic.get(key);
	}
	
	@Override
	public void commitRank(int subsectionIndex,int subsectionSubIndex,int version,long key,long value,long[] args)
	{
		//版本号不对
		if(version!=_data.version)
			return;

		//首次
		if (subsectionIndex == -1)
		{
			subsectionIndex=makeSubsectionIndex(key);
		}

		//还不能入榜
		if (subsectionIndex != -1)
		{
			FuncCommitSubsectionRankValueToCenterServerRequest.create(key,_funcID,subsectionIndex,subsectionSubIndex,version,value,args).send();
		}
	}
	
	@Override
	public void removeRankData(int version,long key)
	{
		//版本号不对
		if(version!=_data.version)
			return;
		
		FuncRemoveSubsectionRankToCenterServerRequest.create(key,_funcID,version).send();
	}

	private int makeSubsectionIndex(long key)
	{
		SubsectionRankConfig subsectionRankConfig=SubsectionRankConfig.get(_funcID);

		if(subsectionRankConfig.subsectionConditions.length==0)
			return 0;

		for(int i=subsectionRankConfig.subsectionConditions.length-1;i>=0;--i)
		{
			boolean matched=true;

			for(int j=0;j<subsectionRankConfig.subsectionConditions[i].length;++j)
			{
				if(!checkOneFuncCondition(key,subsectionRankConfig.subsectionConditions[i][j]))
				{
					matched=false;
					break;
				}
			}

			if (matched)
			{
				return i;
			}
		}

		return -1;
	}

	protected boolean checkOneFuncCondition(long key,int[] args)
	{
		return false;
	}

	private void checkWhole()
	{
//		_dic.forEachValue(v->
//		{
//			RankData rankData=_data.list.get(v.rank - 1);
//
//			if(rankData!=v)
//			{
//				Ctrl.log("出问题");
//			}
//		});
	}
	
	/** 添加排行数据 */
	public void onAddRankFromCenter(int subsectionIndex,int subsectionSubIndex,RankData data)
	{
		if(ShineSetting.openCheck)
		{
			//已在
			if(_dic.get(data.key)!=null)
			{
				Ctrl.throwError("添加排行数据时,已存在");
			}
		}
		
		_dic.put(data.key,data);
		
		SList<RankData> list=getList2(subsectionIndex,subsectionSubIndex);
		
		list.insert(data.rank-1,data);
		
		RankData[] values=list.getValues();
		
		for(int i=data.rank;i<list.size();++i)
		{
			values[i].rank=i+1;
		}
		
		//有上限
		if(_maxNum>0)
		{
			if(list.size()>_maxNum)
			{
				//移除
				RankData pop=list.pop();
				_dic.remove(pop.key);
			}
			
			//满了
			if(list.size()==_maxNum)
			{
				_valueLimit=list.getLast().value;
			}
		}
		
		if(ShineSetting.openCheck)
		{
			checkWhole();
		}
		
		afterCommitRank(data.key,data.rank,data.value);
	}
	
	/** 更新排行(来自中心服) */
	public void onRefreshRankFromCenter(int subsectionIndex,int subsectionSubIndex,long key,long value,int rank)
	{
		RankData data=_dic.get(key);
		
		if(data==null)
		{
			Ctrl.throwError("更新排行数据时，不存在");
			return;
		}
		
		SList<RankData> list=getList2(subsectionIndex,subsectionSubIndex);
		
		if(ShineSetting.openCheck)
		{
			RankData tData=list.get(data.rank - 1);
			
			if(tData==null || tData.key!=data.key)
			{
				Ctrl.throwError("出错,排行数据错误2");
			}
		}
		
		RankData[] values=list.getValues();
		
		//更新值
		data.value=value;
		
		if(data.rank!=rank)
		{
			int index=rank-1;
			int oldIndex=data.rank-1;
			
			//掉
			if(index>oldIndex)
			{
				for(int i=oldIndex;i<index;++i)
				{
					(values[i]=values[i+1]).rank=i+1;
				}
			}
			else
			{
				for(int i=oldIndex;i>index;--i)
				{
					(values[i]=values[i-1]).rank=i+1;
				}
			}
			
			(values[index]=data).rank=rank;
			
			//有上限
			if(_maxNum>0 && list.size()==_maxNum)
			{
				_valueLimit=list.getLast().value;
			}
		}
		
		if(ShineSetting.openCheck)
		{
			checkWhole();
		}
		
		afterCommitRank(key,rank, value);
	}
	
	/** 移除排行 */
	public void onRemoveRankFromCenter(int subsectionIndex,int subsectionSubIndex,long key)
	{
		RankData data=_dic.get(key);
		
		if(data==null)
		{
			Ctrl.throwError("移除排行数据时，不存在");
			return;
		}
		
		_dic.remove(key);
		
		SList<RankData> list=getList2(subsectionIndex,subsectionSubIndex);
		
		int oldIndex=data.rank-1;
		
		list.remove(oldIndex);
		
		RankData[] values=list.getValues();
		int len=list.size();
		
		//修改名次
		for(int i=oldIndex;i<len;++i)
		{
			values[i].rank=i+1;
		}
		
		if(_maxNum>0)
		{
			//归零
			_valueLimit=0L;
		}
		
		if(ShineSetting.openCheck)
		{
			checkWhole();
		}
		
		afterCommitRank(key,-1, 0);
	}
	
	protected void afterCommitRank(long key,int rank,long value)
	{
	
	}
	
	/** 重置排行榜 */
	public void onReset(int version)
	{
	
	}
	
	/** 从中心服读取数据 */
	public void readFromCenter(SubsectionRankToolData data)
	{
		_data=data;
		
		readDicByList(_dic,_data.listListMap);
	}
	
	/** 通过list读取数据到dic */
	private void readDicByList(LongObjectMap<RankData> dic,IntObjectMap<SList<SList<RankData>>> listListMap)
	{
		dic.clear();

		listListMap.forEach((kv,vv)->{
			for(int i=0;i<vv.length();++i) {
				SList<RankData> dataSList = vv.get(i);

				RankData[] values = dataSList.getValues();
				RankData v;

				for (int j = dataSList.length() - 1; j >= 0; --j)
				{
					(v=values[j]).rank=j+1;
					dic.put(v.key,v);
					remakeOne(v);
				}

				if(_maxNum>0 && dataSList.size()==_maxNum)
				{
					_valueLimit=dataSList.getLast().value;
				}
				else
				{
					_valueLimit=0L;
				}
			}
		});
	}
	
	protected void remakeOne(RankData data)
	{
	
	}
}
