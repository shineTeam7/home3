package com.home.commonGame.tool.func;

import com.home.commonBase.constlist.generate.FuncToolType;
import com.home.commonBase.data.social.rank.RankData;
import com.home.commonBase.data.social.rank.RoleGroupRankData;
import com.home.commonBase.data.system.KeyData;
import com.home.commonGame.logic.func.PlayerRoleGroup;
import com.home.commonGame.net.request.func.rank.FuncReGetPageShowListRequest;
import com.home.commonGame.net.request.func.rank.FuncReGetPageShowRequest;
import com.home.commonGame.net.request.func.rank.subsection.FuncReGetSubsectionPageShowListRequest;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.collection.IntSet;
import com.home.shine.support.collection.LongIntMap;
import com.home.shine.support.collection.LongObjectMap;
import com.home.shine.support.collection.SList;

/** 分段翻页显示插件 */
public class PlayerSubsectionPageShowTool extends PlayerFuncTool
{
	/** 每页显示数 */
	private int _eachPageShowNum;
	/** 是否需要缓存 */
	private boolean _needCache=true;
	/** 玩家群功能ID */
	private int _roleGroupFuncID=-1;
	/** 翻页缓存数据 */
	private LongObjectMap<CachePageData> _cachePageDataMap=new LongObjectMap<>();

	class CachePageData
	{
		/** key位置字典(key->page) */
		LongIntMap keyDic=new LongIntMap();
		/** 数据(index->data) */
		IntObjectMap<KeyData> dic=new IntObjectMap<>(KeyData[]::new);
	}

	public PlayerSubsectionPageShowTool(int funcID, int eachPageShowNum)
	{
		this(funcID,eachPageShowNum,true);
	}

	public PlayerSubsectionPageShowTool(int funcID, int eachPageShowNum, boolean needCache)
	{
		super(FuncToolType.SubsectionPageShow,funcID);
		
		_eachPageShowNum=eachPageShowNum;
		_needCache=needCache;
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		
		clear();
	}
	
	/** 清空显示记录 */
	public void clear()
	{
		_cachePageDataMap.clear();
	}
	
	/** 绑定玩家群功能ID */
	public void setRoleGroupFuncID(int funcID)
	{
		_roleGroupFuncID=funcID;
	}
	
	/** 接收 */
	public void onReceive(int subsectionIndex,int subsectionSubIndex,int page,int arg,SList<KeyData> list,RankData rankData)
	{
		if(_roleGroupFuncID!=-1)
		{
			PlayerRoleGroupTool roleGroupTool=me.func.getRoleGroupTool(_roleGroupFuncID);
			
			if(roleGroupTool!=null)
			{
				RoleGroupRankData rData=(RoleGroupRankData)rankData;
				
				PlayerRoleGroup roleGroup=roleGroupTool.getOnlyOne();
				
				if(roleGroup!=null)
				{
					roleGroup.onRefreshRank(_funcID,rData);
				}
			}
		}
		else
		{
			PlayerSubsectionRankTool rankTool=me.func.getSubsectionRankTool(_funcID);
			
			if(rankTool!=null)
			{
				rankTool.updateRank(rankData);
			}
		}
		
		toReceivePage(subsectionIndex,subsectionSubIndex,page,arg,list);
	}
	
	/** 接受翻页数据 */
	private void toReceivePage(int subsectionIndex,int subsectionSubIndex,int page,int arg,SList<KeyData> list)
	{
		if(_needCache)
		{
			long cacheKey=(long)subsectionIndex << 16 | (long)subsectionSubIndex;

			CachePageData cachePageData;

			if(!_cachePageDataMap.contains(cacheKey))
			{
				_cachePageDataMap.put(cacheKey,cachePageData=new CachePageData());
			}
			else
			{
				cachePageData=_cachePageDataMap.get(cacheKey);
			}

			KeyData[] values=list.getValues();
			KeyData v;
			int index;
			long key;
			int oldPage;
			
			
			int pageShowNum=_eachPageShowNum;
			int indexStart=page*pageShowNum;
			
			IntObjectMap<KeyData> dic=cachePageData.dic;
			LongIntMap keyDic=cachePageData.keyDic;
			
			IntSet changedPageSet=null;
			IntObjectMap<KeyData> sendDic=null;
			
			int len=list.size();
			int max=_eachPageShowNum;
			
			for(int i=0;i<max;++i)
			{
				index=i+indexStart;
				
				if(i<len)
				{
					v=values[i];
					
					oldPage=keyDic.getOrDefault((key=v.key),-1);
					
					if(oldPage==-1)
					{
						keyDic.put(key,page);
					}
					else
					{
						if(oldPage!=page)
						{
							if(changedPageSet==null)
								changedPageSet=new IntSet();
							
							changedPageSet.add(oldPage);
							
							keyDic.put(key,page);
						}
					}
					
					//不一致
					if(!v.dataEquals(dic.get(index)))
					{
						dic.put(index,v);
						
						if(sendDic==null)
							sendDic=new IntObjectMap<>();
						
						sendDic.put(index,v);
					}
				}
				else
				{
					if(dic.get(index)!=null)
					{
						dic.remove(index);
						
						if(sendDic==null)
							sendDic=new IntObjectMap<>();
						
						sendDic.put(index,null);
					}
				}
			}
			
//			me.send(FuncReGetPageShowRequest.create(_funcID,page,arg,sendDic,changedPageSet));
		}
		else
		{
			me.send(FuncReGetSubsectionPageShowListRequest.create(_funcID,subsectionIndex,subsectionSubIndex,page,arg,list));
		}
	}
}
