package com.home.commonCenter.tool.func;

import com.home.commonBase.data.scene.match.PlayerMatchData;
import com.home.commonBase.tool.func.MatchTool;
import com.home.commonCenter.global.CenterC;
import com.home.commonCenter.net.base.CenterRequest;
import com.home.commonCenter.net.centerRequest.func.match.FuncMatchSuccessFromCenterRequest;
import com.home.commonCenter.net.centerRequest.func.match.FuncSendAcceptMatchFromCenterRequest;
import com.home.commonCenter.net.centerRequest.func.match.FuncSendReAddMatchFromCenterRequest;
import com.home.commonCenter.net.serverRequest.game.func.match.FuncMatchTimeOutToGameServerRequest;

public abstract class CenterMatchTool extends MatchTool
{
	public CenterMatchTool(int funcID,int batchNum,int dValue,int waitTimeMax,boolean needWaitAccept)
	{
		super(funcID,batchNum,dValue,waitTimeMax,needWaitAccept);
	}
	
	private void radioMatchs(PlayerMatchData[] matches,CenterRequest request)
	{
		request.write();
		
		for(PlayerMatchData v:matches)
		{
			request.sendToPlayer(v.showData.playerID);
		}
	}
	
	/** 匹配结束一组 */
	@Override
	protected void matchOverOne(PlayerMatchData[] matches)
	{
		CenterC.scene.onMatchOnce(this,matches);
	}
	
	/** 广播匹配成功 */
	@Override
	protected void radioMatchSuccess(PlayerMatchData[] matches,int matchIndex)
	{
		radioMatchs(matches,FuncMatchSuccessFromCenterRequest.create(_funcID,matchIndex,matches));
	}
	
	/** 广播接收匹配 */
	@Override
	protected void radioAcceptMatch(PlayerMatchData[] matches,long playerID)
	{
		radioMatchs(matches,FuncSendAcceptMatchFromCenterRequest.create(_funcID,playerID));
	}
	
	@Override
	protected void sendReAddMatch(long playerID)
	{
		//FuncSendReAddMatchFromCenterRequest.create(_funcID).sendToPlayer(playerID);
		
		FuncSendReAddMatchFromCenterRequest.create(_funcID).sendToPlayer(playerID);
	}
	
	@Override
	protected void onMatchTimeOut(long playerID)
	{
		FuncMatchTimeOutToGameServerRequest.create(_funcID).sendToPlayer(playerID);
	}
}
