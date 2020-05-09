package com.home.commonGame.tool.func;

import com.home.commonBase.constlist.generate.FuncToolType;
import com.home.commonBase.data.social.rank.RankData;
import com.home.commonBase.data.social.rank.RoleGroupRankData;
import com.home.commonBase.data.system.KeyData;
import com.home.commonGame.logic.func.PlayerRoleGroup;
import com.home.commonGame.net.request.func.rank.FuncReGetPageShowListRequest;
import com.home.commonGame.net.request.func.rank.FuncReGetPageShowRequest;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.collection.IntSet;
import com.home.shine.support.collection.LongIntMap;
import com.home.shine.support.collection.SList;

/** 翻页显示插件 */
public class PlayerPageShowTool extends PlayerFuncTool
{
	/** 每页显示数 */
	private int _eachPageShowNum;
	/** 是否需要缓存 */
	private boolean _needCache=true;
	
	/** key位置字典(key->page) */
	private LongIntMap _keyDic=new LongIntMap();
	/** 数据(index->data) */
	private IntObjectMap<KeyData> _dic=new IntObjectMap<>(KeyData[]::new);
	
	/** 玩家群功能ID */
	private int _roleGroupFuncID=-1;
	
	public PlayerPageShowTool(int funcID,int eachPageShowNum)
	{
		this(funcID,eachPageShowNum,true);
	}
	
	public PlayerPageShowTool(int funcID,int eachPageShowNum,boolean needCache)
	{
		super(FuncToolType.PageShow,funcID);
		
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
		_keyDic.clear();
		_dic.clear();
	}
	
	/** 绑定玩家群功能ID */
	public void setRoleGroupFuncID(int funcID)
	{
		_roleGroupFuncID=funcID;
	}
	
	/** 接收 */
	public void onReceive(int page,int arg,SList<KeyData> list,RankData rankData)
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
			PlayerRankTool rankTool=me.func.getRankTool(_funcID);
			
			if(rankTool!=null)
			{
				rankTool.updateRank(rankData);
			}
		}
		
		toReceivePage(page,arg,list);
	}
	
	/** 接受翻页数据 */
	private void toReceivePage(int page,int arg,SList<KeyData> list)
	{
		if(_needCache)
		{
			KeyData[] values=list.getValues();
			KeyData v;
			int index;
			long key;
			int oldPage;
			
			
			int pageShowNum=_eachPageShowNum;
			int indexStart=page*pageShowNum;
			
			IntObjectMap<KeyData> dic=_dic;
			LongIntMap keyDic=_keyDic;
			
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
			
			me.send(FuncReGetPageShowRequest.create(_funcID,page,arg,sendDic,changedPageSet));
		}
		else
		{
			me.send(FuncReGetPageShowListRequest.create(_funcID,page,arg,list));
		}
	}
	
	
}
