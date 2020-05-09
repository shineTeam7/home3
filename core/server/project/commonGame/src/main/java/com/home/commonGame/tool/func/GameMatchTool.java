package com.home.commonGame.tool.func;

import com.home.commonBase.data.scene.match.PlayerMatchData;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.tool.func.MatchTool;
import com.home.commonGame.global.GameC;
import com.home.commonGame.net.request.func.match.FuncMatchSuccessRequest;
import com.home.commonGame.net.request.func.match.FuncMatchTimeOutRequest;
import com.home.commonGame.net.request.func.match.FuncReAddMatchRequest;
import com.home.commonGame.net.request.func.match.FuncSendAcceptMatchRequest;
import com.home.commonGame.net.request.func.match.FuncStartMatchRequest;
import com.home.commonGame.part.player.Player;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.net.base.BaseRequest;

/** 匹配插件(个人个伙) */
public abstract class GameMatchTool extends MatchTool implements IGameMatchTool
{
	public GameMatchTool(int funcID,int batchNum,int dValue,int waitTimeMax,boolean needWaitAccept)
	{
		super(funcID,batchNum,dValue,waitTimeMax,needWaitAccept);
	}
	
	private void radioMatchs(PlayerMatchData[] matches,BaseRequest request)
	{
		request.write();
		
		Player player;
		
		for(PlayerMatchData v:matches)
		{
			if((player=GameC.main.getPlayerByID(v.showData.playerID))!=null)
			{
				player.send(request);
			}
		}
	}
	
	/** 匹配结束一组 */
	@Override
	protected void matchOverOne(PlayerMatchData[] matches)
	{
		GameC.scene.onMatchOnce(_funcID,matches);
	}
	
	/** 广播匹配成功 */
	@Override
	protected void radioMatchSuccess(PlayerMatchData[] matches,int matchIndex)
	{
		radioMatchs(matches,FuncMatchSuccessRequest.create(_funcID,matchIndex,matches));
	}
	
	/** 广播接收匹配 */
	@Override
	protected void radioAcceptMatch(PlayerMatchData[] matches,long playerID)
	{
		radioMatchs(matches,FuncSendAcceptMatchRequest.create(_funcID,playerID));
	}
	
	@Override
	protected void sendReAddMatch(long playerID)
	{
		Player player;
		
		if((player=GameC.main.getPlayerByID(playerID))!=null)
		{
			player.send(FuncReAddMatchRequest.create(_funcID));
		}
	}
	
	@Override
	protected void onMatchTimeOut(long playerID)
	{
		Player player;
		
		if((player=GameC.main.getPlayerByID(playerID))!=null)
		{
			//切到自己的线程再进入目标场景
			player.addFunc(()->
			{
				//匹配完成
				player.scene.setMatchingFuncID(-1);
				
				doMatchTimeOut(player);
			});
		}
	}
	
	/** 执行匹配超时(逻辑线程) */
	protected void doMatchTimeOut(Player player)
	{
		//推送
		player.send(FuncMatchTimeOutRequest.create(_funcID));
	}
	
	/** 检查是否可匹配(逻辑线程) */
	public boolean checkCanMatch(Player player)
	{
		return true;
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
	
	/** 添加玩家到匹配中(主线程) */
	public boolean addPlayer(Player player)
	{
		boolean re=add(createMatchData(player));
		
		if(re)
		{
			player.send(FuncStartMatchRequest.create(_funcID));
		}
		else
		{
			Ctrl.warnLog("添加匹配失败!",player.getInfo());
		}
		
		return re;
	}
}
