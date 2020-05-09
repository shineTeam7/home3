package com.home.commonCenter.tool.func;

import com.home.commonBase.data.social.rank.RankData;
import com.home.commonBase.data.system.KeyData;
import com.home.commonBase.tool.func.PageShowTool;
import com.home.commonCenter.net.serverRequest.game.func.pageShow.FuncReGetPageShowToPlayerServerRequest;
import com.home.shine.support.collection.SList;

/** 中心服翻页显示插件 */
public class CenterPageShowTool extends PageShowTool
{
	public CenterPageShowTool(int funcID,int showMaxNum,int eachPageShowNum)
	{
		super(funcID,showMaxNum,eachPageShowNum);
	}
	
	/** 获取翻页显示 */
	public void getPageShow(long playerID,int page,int arg)
	{
		SList<KeyData> list=getPageShowList(page,arg);
		
		boolean needRank=_rankTool!=null;
		RankData rankData=needRank ? _rankTool.getRankData(playerID) : null;
		
		FuncReGetPageShowToPlayerServerRequest.create(_funcID,page,arg,list,rankData).sendToPlayer(playerID);
	}
}
