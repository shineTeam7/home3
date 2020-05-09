package com.home.commonGame.tool.func;

import com.home.commonBase.constlist.generate.FuncToolType;
import com.home.commonBase.data.scene.match.PlayerMatchData;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.tool.func.FuncTool;
import com.home.commonGame.global.GameC;
import com.home.commonGame.net.request.func.match.FuncMatchTimeOutRequest;
import com.home.commonGame.net.request.func.match.FuncStartMatchRequest;
import com.home.commonGame.net.serverRequest.center.func.match.FuncAcceptMatchToCenterServerRequest;
import com.home.commonGame.net.serverRequest.center.func.match.FuncApplyCancelMatchToCenterServerRequest;
import com.home.commonGame.net.serverRequest.center.func.match.FuncApplyMatchToCenterServerRequest;
import com.home.commonGame.part.player.Player;

/** 游戏服到中心服匹配插件 */
public class GameToCenterMatchTool extends FuncTool implements IGameMatchTool
{
	public GameToCenterMatchTool(int funcID)
	{
		super(FuncToolType.Match,funcID);
	}
	
	@Override
	public boolean checkCanMatch(Player player)
	{
		return false;
	}
	
	@Override
	public boolean addPlayer(Player player)
	{
		PlayerMatchData data=createMatchData(player);
		
		FuncApplyMatchToCenterServerRequest.create(player.role.playerID,_funcID,data).send();
		
		player.send(FuncStartMatchRequest.create(_funcID));
		
		return true;
	}
	
	@Override
	public boolean cancelMatch(long playerID)
	{
		FuncApplyCancelMatchToCenterServerRequest.create(playerID,_funcID).send();
		
		return true;
	}
	
	@Override
	public void acceptMatch(int index,long playerID)
	{
		FuncAcceptMatchToCenterServerRequest.create(playerID,_funcID,index).send();
	}
	
	/** 构造匹配数据 */
	protected void makeMatchData(Player player,PlayerMatchData data)
	{
	
	}
	
	private PlayerMatchData createMatchData(Player player)
	{
		PlayerMatchData data=BaseC.factory.createPlayerMatchData();
		player.role.makeMatchData(data);
		makeMatchData(player,data);
		
		return data;
	}
	
	/** 匹配超时来自中心服(池线程) */
	public void onMatchTimeOutFromCenter(Player player)
	{
		//匹配完成
		player.scene.setMatchingFuncID(-1);
		
		doMatchTimeOut(player);
	}
	
	/** 执行匹配超时(逻辑线程) */
	protected void doMatchTimeOut(Player player)
	{
		//推送
		player.send(FuncMatchTimeOutRequest.create(_funcID));
	}
}
