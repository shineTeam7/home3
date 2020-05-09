package com.home.commonGame.dataEx;

import com.home.commonBase.data.login.PlayerSwitchGameData;
import com.home.commonBase.data.scene.scene.SceneEnterArgData;
import com.home.commonBase.data.system.PlayerPrimaryKeyData;
import com.home.commonBase.data.system.PlayerWorkCompleteData;
import com.home.commonBase.data.system.PlayerWorkData;
import com.home.commonBase.part.player.list.PlayerListData;
import com.home.commonGame.constlist.system.PlayerSwitchStateType;
import com.home.commonGame.global.GameC;
import com.home.commonGame.net.serverRequest.game.system.SendPlayerCenterRequestListToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.system.SendPlayerToGameRequestListToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.system.SendPlayerWorkCompleteListToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.system.SendPlayerWorkListToGameServerRequest;
import com.home.commonGame.part.player.Player;
import com.home.commonGame.server.GameReceiveSocket;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.collection.SList;
import com.home.shine.support.pool.StringBuilderPool;
import com.home.shine.utils.StringUtils;

/** 角色切换游戏服发起记录数据 */
public class PlayerSwitchGameToRecordData
{
	/** 主键数据 */
	public PlayerPrimaryKeyData keyData;
	/** 列表数据 */
	public PlayerListData listData;
	/** 切换数据 */
	public PlayerSwitchGameData switchData;
	
	/** 目标gameID(也作为切换中判定) */
	public int targetGameID=-1;
	/** 当前状态 */
	public int state=PlayerSwitchStateType.None;
	
	
	/** 退出回调组 */
	public SList<Runnable> exitOverCalls=new SList<>(Runnable[]::new);
	/** 是否退出中 */
	public boolean isExiting=false;
	/** 退出码 */
	public int exitCode;
	/** 客户端连接 */
	public GameReceiveSocket socket;
	/** 切换到的等待响应时间 */
	public int switchToWaitTime;
	
	//以下是源服部分
	/** 当前gameID */
	public int nowGameID=-1;
	/** 是否已接收(目前未启用 ) */
	public boolean hasReceive;
	
	/** 下个要进入的场景信息 */
	private SceneEnterArgData _nextEnterSceneLocation;
	
	/** 事务组 */
	private SList<PlayerWorkData> _workList=new SList<>(PlayerWorkData[]::new);
	/** 事务完成组 */
	private SList<PlayerWorkCompleteData> _workCompleteList=new SList<>(PlayerWorkCompleteData[]::new);
	/** 中心服消息缓存 */
	private SList<byte[]> _centerRequestCache=new SList<>(byte[][]::new);
	/** 逻辑服到角色消息缓存 */
	private SList<byte[]> _playerToGameRequestCache=new SList<>(byte[][]::new);
	
	/** 调用退出回调 */
	public void callExitOvers()
	{
		if(exitOverCalls.isEmpty())
			return;
		
		SList<Runnable> exitOverCalls=this.exitOverCalls;
		this.exitOverCalls=new SList<>(Runnable[]::new);
		
		Runnable[] values=exitOverCalls.getValues();
		
		for(int i=0, len=exitOverCalls.size();i<len;++i)
		{
			values[i].run();
		}
	}
	
	public void startWait()
	{
		switchToWaitTime=ShineSetting.affairDefaultExecuteTime;
	}
	
	/** 是否切换过程中 */
	public boolean isSwitching()
	{
		return targetGameID>0;
	}
	
	/** 写描述信息 */
	public void writeInfo(StringBuilder sb)
	{
		sb.append("uid:");
		sb.append(keyData.uid);
		sb.append(" playerID:");
		sb.append(keyData.playerID);
		sb.append(" 名字:");
		sb.append(keyData.name);
		sb.append(" 源服:");
		sb.append(keyData.sourceGameID);
	}
	
	/** 调试日志 */
	public void debugLog(String str)
	{
		if(!ShineSetting.needDebugLog)
			return;
		
		StringBuilder sb=StringBuilderPool.create();
		sb.append(str);
		sb.append(' ');
		writeInfo(sb);
		
		Ctrl.debugLog(StringBuilderPool.releaseStr(sb));
	}
	
	/** 调试日志 */
	public void debugLog(Object...args)
	{
		if(!ShineSetting.needDebugLog)
			return;
		
		debugLog(StringUtils.objectsToString(args));
	}
	
	/** 警告日志 */
	public void warnLog(String str)
	{
		StringBuilder sb=StringBuilderPool.create();
		sb.append(str);
		sb.append(' ');
		writeInfo(sb);
		
		Ctrl.warnLog(StringBuilderPool.releaseStr(sb));
	}
	
	/** 警告日志 */
	public void warnLog(Object...args)
	{
		warnLog(StringUtils.objectsToString(args));
	}
	
	/** 错误日志 */
	public void errorLog(String str)
	{
		StringBuilder sb=StringBuilderPool.create();
		sb.append(str);
		sb.append(' ');
		writeInfo(sb);
		
		Ctrl.errorLog(StringBuilderPool.releaseStr(sb));
	}
	
	/** 错误日志 */
	public void errorLog(Object...args)
	{
		warnLog(StringUtils.objectsToString(args));
	}
	
	/** 清楚切换game数据 */
	public void clearSwitchGame()
	{
		targetGameID=-1;
		switchToWaitTime=0;
		hasReceive=false;
	}
	
	/** 添加缓存事务 */
	public void addWork(PlayerWorkData data)
	{
		_workList.add(data);
	}
	
	/** 添加缓存事务完成 */
	public void addWorkComplete(PlayerWorkCompleteData data)
	{
		_workCompleteList.add(data);
	}
	
	/** 添加缓存的消息 */
	public void addCacheCenterRequest(byte[] data)
	{
		_centerRequestCache.add(data);
	}
	
	/** 添加缓存的消息 */
	public void addCachePlayerToGameRequest(byte[] data)
	{
		_playerToGameRequestCache.add(data);
	}
	
	/** 释放阻塞的离线事务和消息(发送到nowGame服上) */
	public void flushSwitch(int gameID)
	{
		if(!_workList.isEmpty())
		{
			SendPlayerWorkListToGameServerRequest.create(_workList).send(gameID);
			_workList.clear();
		}
		
		if(!_workCompleteList.isEmpty())
		{
			SendPlayerWorkCompleteListToGameServerRequest.create(_workCompleteList).send(gameID);
			_workCompleteList.clear();
		}
		
		if(!_centerRequestCache.isEmpty())
		{
			SendPlayerCenterRequestListToGameServerRequest.create(keyData.playerID,_centerRequestCache.toArray()).send(gameID);
			_centerRequestCache.clear();
		}
		
		if(!_playerToGameRequestCache.isEmpty())
		{
			SendPlayerToGameRequestListToGameServerRequest.create(keyData.playerID,_playerToGameRequestCache.toArray()).send(gameID);
			_playerToGameRequestCache.clear();
		}
	}
	
	/** 刷到角色上(useLogicThread:是否使用逻辑线程处理事务) */
	public void doFlushToPlayer(Player player,boolean useLogicThread)
	{
		if(!_workList.isEmpty())
		{
			if(useLogicThread)
			{
				SList<PlayerWorkData> workList=_workList;
				_workList=new SList<>(PlayerWorkData[]::new);
				
				player.addFunc(()->
				{
					player.system.flushOfflineWorkList(workList);
				});
			}
			else
			{
				player.system.flushOfflineWorkList(_workList);
				_workList.clear();
			}
		}
		
		if(!_workCompleteList.isEmpty())
		{
			if(useLogicThread)
			{
				SList<PlayerWorkCompleteData> workCompleteList=_workCompleteList;
				_workCompleteList=new SList<>(PlayerWorkCompleteData[]::new);
				player.addFunc(()->
				{
					player.system.onWorkCompleteList(workCompleteList);
				});
			}
			else
			{
				player.system.onWorkCompleteList(_workCompleteList);
				_workCompleteList.clear();
			}
		}
		
		if(!_centerRequestCache.isEmpty())
		{
			byte[][] values=_centerRequestCache.getValues();
			byte[] v;
			
			for(int i=0,len=_centerRequestCache.size();i<len;++i)
			{
				v=values[i];
				
				GameC.gameSwitch.doPlayerCenterRequest(player,v);
			}
			
			_centerRequestCache.clear();
		}
		
		if(!_playerToGameRequestCache.isEmpty())
		{
			byte[][] values=_playerToGameRequestCache.getValues();
			byte[] v;
			
			for(int i=0,len=_playerToGameRequestCache.size();i<len;++i)
			{
				v=values[i];
				
				GameC.gameSwitch.doPlayerToGameRequest(player,v);
			}
			
			_playerToGameRequestCache.clear();
		}
	}
	
	/** 设置下个要进入的场景信息 */
	public void setNextSceneLocation(SceneEnterArgData data)
	{
		_nextEnterSceneLocation=data;
	}
	
	/** 获取下个要进入的数据 */
	public SceneEnterArgData getNextEnterSceneLocation()
	{
		return _nextEnterSceneLocation;
	}
}
