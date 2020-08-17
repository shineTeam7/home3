package com.home.commonGame.control;

import com.home.commonBase.constlist.generate.InfoCodeType;
import com.home.commonBase.data.login.ClientLoginServerInfoData;
import com.home.commonBase.data.login.PlayerSwitchGameData;
import com.home.commonBase.data.scene.scene.SceneEnterArgData;
import com.home.commonBase.data.system.GameServerInfoData;
import com.home.commonBase.data.system.PlayerPrimaryKeyData;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.part.player.list.PlayerListData;
import com.home.commonBase.table.table.PlayerTable;
import com.home.commonGame.constlist.system.PlayerLoginStateType;
import com.home.commonGame.constlist.system.PlayerSwitchStateType;
import com.home.commonGame.dataEx.PlayerSwitchGameReceiveRecordData;
import com.home.commonGame.dataEx.PlayerSwitchGameToRecordData;
import com.home.commonGame.dataEx.SaveSwitchPlayerTempData;
import com.home.commonGame.global.GameC;
import com.home.commonGame.net.request.login.SwitchGameRequest;
import com.home.commonGame.net.request.system.CenterTransGameToClientRequest;
import com.home.commonGame.net.serverRequest.game.login.PlayerCallSwitchBackToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.login.PlayerExitOverToSourceServerRequest;
import com.home.commonGame.net.serverRequest.game.login.PlayerExitSwitchBackServerRequest;
import com.home.commonGame.net.serverRequest.game.login.PlayerPreExitToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.login.PlayerPreSwitchGameToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.login.PlayerSwitchGameCompleteToSourceServerRequest;
import com.home.commonGame.net.serverRequest.game.login.PlayerSwitchGameReceiveResultToSourceServerRequest;
import com.home.commonGame.net.serverRequest.game.login.PreSwitchGameServerRequest;
import com.home.commonGame.net.serverRequest.game.login.RePlayerPreExitToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.login.RePlayerPreSwitchGameToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.login.RePreSwitchGameFailedServerRequest;
import com.home.commonGame.net.serverRequest.game.login.RePreSwitchGameServerRequest;
import com.home.commonGame.net.serverRequest.game.system.SaveSwitchedPlayerListServerRequest;
import com.home.commonGame.net.serverRequest.game.system.SendPlayerCenterRequestToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.system.SendPlayerToGameRequestToGameServerRequest;
import com.home.commonGame.net.serverResponse.center.base.PlayerToGameServerResponse;
import com.home.commonGame.net.serverResponse.game.base.PlayerGameToGameServerResponse;
import com.home.commonGame.part.player.Player;
import com.home.commonGame.part.player.part.SystemPart;
import com.home.commonGame.server.GameReceiveSocket;
import com.home.shine.ShineSetup;
import com.home.shine.bytes.BytesReadStream;
import com.home.shine.bytes.BytesWriteStream;
import com.home.shine.control.ThreadControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.net.base.BaseResponse;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.collection.LongObjectMap;
import com.home.shine.support.collection.SList;
import com.home.shine.utils.MathUtils;

/** 跨服控制 */
public class GameSwitchControl
{
	//--switch--//
	
	/** 切换去角色字典(key:playerID) */
	private LongObjectMap<PlayerSwitchGameToRecordData> _switchToPlayers=new LongObjectMap<>(PlayerSwitchGameToRecordData[]::new);
	/** 切换去角色字典(key:userID) */
	private LongObjectMap<PlayerSwitchGameToRecordData> _switchToPlayersByUserID=new LongObjectMap<>(PlayerSwitchGameToRecordData[]::new);
	
	/** 切换接收字典(key:playerID) */
	private LongObjectMap<PlayerSwitchGameReceiveRecordData> _switchReceiveDic=new LongObjectMap<>(PlayerSwitchGameReceiveRecordData[]::new);
	/** 切换接收字典(key:token) */
	private IntObjectMap<PlayerSwitchGameReceiveRecordData> _switchReceiveDicByToken=new IntObjectMap<>(PlayerSwitchGameReceiveRecordData[]::new);
	
	/** 同步 切换后的角色数据保存 */
	private SaveSwitchPlayerTempData _switchSaveData=null;
	/** 同步 切换后的角色数据保存序号 */
	private int _switchSaveIndex;
	
	private BytesReadStream _tempReadStream=BytesReadStream.create();
	
	public void init()
	{
		ThreadControl.getMainTimeDriver().setInterval(this::onSecond,1000);
		ThreadControl.getMainTimeDriver().setInterval(this::onSaveSwitchedPlayer,CommonSetting.dbWriteDelay * 1000);
	}
	
	/** 每秒 */
	private void onSecond(int delay)
	{
		switchToSecond();
		switchReceiveSecond();
		checkSaveSwitchedPlayerSecond();
	}
	
	/** 切换去每秒 */
	private void switchToSecond()
	{
		_switchToPlayers.forEachValueS(v->
		{
			if(v.isSwitching() && v.switchToWaitTime>0)
			{
				//到时
				if((--v.switchToWaitTime)==0)
				{
					playerSwitchToTimeOut(v);
					//playerSwitchToGameNextTimeOut(v);
				}
			}
		});
	}
	
	/** 切换接收每秒 */
	private void switchReceiveSecond()
	{
		_switchReceiveDic.forEachValueS(v->
		{
			//到时
			if((--v.timeOut)==0)
			{
				preSwitchGameReceiveTimeOut(v);
			}
		});
	}
	
	/** 检查保存切换后角色的间隔 */
	private void checkSaveSwitchedPlayerSecond()
	{
		if(_switchSaveData!=null)
		{
			if((--_switchSaveData.timeOut)==0)
			{
				Ctrl.warnLog("同步切换后的角色超时");
				saveSwitchedPlayerLast();
			}
		}
	}
	
	/** 输出清空剩余 */
	public void printClearLast()
	{
		if(!ShineSetting.needDebugLog)
			return;
		
		_switchToPlayers.forEachValue(v->
		{
			Ctrl.debugLog("剩余切换去",v.keyData.getInfo());
		});
		
		_switchReceiveDic.forEachValue(v->
		{
			Ctrl.debugLog("剩余切换接收",v.keyData.getInfo());
		});
	}
	
	/** 角色添加到switchTo组 */
	private void addPlayerToSwitchTo(PlayerSwitchGameToRecordData data)
	{
		_switchToPlayers.put(data.keyData.playerID,data);
		_switchToPlayersByUserID.put(data.keyData.userID,data);
	}
	
	/** 通过playerID删除登录缓存数据 */
	public void removePlayerSwitchToByPlayerID(long playerID)
	{
		PlayerSwitchGameToRecordData data=getSwitchToPlayerByID(playerID);
		
		if(data!=null)
		{
			removePlayerFromSwitchTo(data);
		}
	}
	
	/** 角色从switchTo组移除 */
	private void removePlayerFromSwitchTo(PlayerSwitchGameToRecordData data)
	{
		Ctrl.warnLog("移除switchTo",data.keyData.playerID);
		//Ctrl.printStackTrace();
		
		_switchToPlayers.remove(data.keyData.playerID);
		_switchToPlayersByUserID.remove(data.keyData.userID);
	}
	
	/** 角色添加到switchReceive组 */
	private void addPlayerToSwitchReceive(PlayerSwitchGameReceiveRecordData data)
	{
		_switchReceiveDic.put(data.keyData.playerID,data);
		_switchReceiveDicByToken.put(data.token,data);
	}
	
	/** 角色从switchReceive组移除 */
	public void removePlayerFromSwitchReceive(PlayerSwitchGameReceiveRecordData data)
	{
		_switchReceiveDic.remove(data.keyData.playerID);
		_switchReceiveDicByToken.remove(data.token);
	}
	
	/** 获取切换去角色(底层用) */
	public PlayerSwitchGameToRecordData getSwitchToPlayerByID(long playerID)
	{
		return _switchToPlayers.get(playerID);
	}
	
	/** 通过userID获取切换角色 */
	public PlayerSwitchGameToRecordData getOneSwitchToPlayerByUserID(long userID)
	{
		return _switchToPlayersByUserID.get(userID);
	}
	
	/** 获取切换接收角色 */
	public PlayerSwitchGameReceiveRecordData getSwitchReceivePlayerByID(long playerID)
	{
		return _switchReceiveDic.get(playerID);
	}
	
	public LongObjectMap<PlayerSwitchGameToRecordData> getSwitchToPlayers()
	{
		return _switchToPlayers;
	}
	
	public LongObjectMap<PlayerSwitchGameReceiveRecordData> getSwitchReceivePlayers()
	{
		return _switchReceiveDic;
	}
	
	/** 是否角色切换到其他服务器了(切换中也算) */
	public boolean isPlayerSwitched(long playerID)
	{
		PlayerSwitchGameToRecordData sData;
		if((sData=getSwitchToPlayerByID(playerID))!=null)
			return true;
		
		Player re;
		if((re=GameC.main.getPlayerByIDT(playerID))!=null && re.system.isStateSwitching())
			return true;
		
		return false;
	}
	
	/** 保存切换到的角色 */
	private void onSaveSwitchedPlayer(int delay)
	{
		SaveSwitchPlayerTempData tempData=null;
		
		Player[] values;
		Player v;
		
		for(int i=(values=GameC.main.getPlayers().getValues()).length-1;i>=0;--i)
		{
			if((v=values[i])!=null)
			{
				//在线状态,并且不是本服的
				if(v.system.isStateOnline() && !v.isCurrentGame())
				{
					if(tempData==null)
						tempData=new SaveSwitchPlayerTempData();
					
					tempData.players.add(v);
				}
			}
		}
		
		if(tempData!=null)
		{
			_switchSaveData=tempData;
			
			int index=tempData.index=++_switchSaveIndex;
			tempData.playerNum=tempData.players.size();
			tempData.timeOut=CommonSetting.dbWriteOnceMaxTime;
			
			values=tempData.players.getValues();
			
			for(int i=0, len=tempData.players.size();i<len;++i)
			{
				saveOneSwitchedPlayer(index,values[i]);
			}
		}
	}
	
	private void saveOneSwitchedPlayer(int index,Player player)
	{
		player.addFunc(()->
		{
			//写出列表数据
			PlayerListData listData=player.createListData();
			//深拷
			player.writeListData(listData);
			
			long playerID=player.role.playerID;
			int gameID=player.role.sourceGameID;
			
			player.addMainFunc(()->
			{
				saveOneSwitchedPlayerOver(index,playerID,gameID,listData);
			});
		});
	}
	
	/** 保存单个角色数据结束(主线程) */
	private void saveOneSwitchedPlayerOver(int index,long playerID,int gameID,PlayerListData listData)
	{
		if(_switchSaveData==null || _switchSaveData.index!=index)
		{
			Ctrl.warnLog("同步切换后的角色数据时,超时",gameID,playerID);
			return;
		}
		
		_switchSaveData.playersForGameDic.computeIfAbsent(gameID,k->new LongObjectMap<>(PlayerListData[]::new)).put(playerID,listData);
		
		//完了
		if((--_switchSaveData.playerNum)==0)
		{
			saveSwitchedPlayerLast();
		}
	}
	
	/** 保存最后的推送步骤 */
	private void saveSwitchedPlayerLast()
	{
		_switchSaveData.playersForGameDic.forEach((k,v)->
		{
			saveSwitchedPlayerLastToGame(k,v);
		});
		
		_switchSaveData=null;
	}
	
	private void saveSwitchedPlayerLastToGame(int gameID,LongObjectMap<PlayerListData> dic)
	{
		//切IO线程操作
		ThreadControl.addIOFunc(gameID & ThreadControl.ioThreadNumMark,()->
		{
			LongObjectMap<byte[]> reDic=new LongObjectMap<>(byte[][]::new,dic.size());
			
			BytesWriteStream stream=BytesWriteStream.create();
			
			dic.forEach((k,v)->
			{
				stream.clear();
				v.writeBytesFull(stream);
				reDic.put(k,stream.getByteArray());
			});
			
			//推送消息
			SaveSwitchedPlayerListServerRequest.create(reDic).send(gameID);
		});
	}
	
	/** 收到保存切换后角色列表消息 */
	public void onReceiveSaveSwitchedPlayerList(LongObjectMap<byte[]> reDic)
	{
		SList<PlayerTable> list=new SList<>(PlayerTable[]::new,reDic.size());
		
		reDic.forEach((k,v)->
		{
			PlayerTable table=GameC.db.getPlayerTable(k);
			
			if(table==null)
			{
				Ctrl.warnLog("不该没有角色表",k);
			}
			else
			{
				list.add(table);
			}
		});
		
		//切DB写线程
		ThreadControl.addDBWriteFunc(()->
		{
			PlayerTable[] values=list.getValues();
			PlayerTable v;
			
			for(int i=0,len=list.size();i<len;++i)
			{
				v=values[i];
				v.data=reDic.get(v.playerID);
			}
		});
	}
	
	//--switch源服部分--//
	
	/** 源服收到预备切换game服 */
	public void onSourceReceivePreSwitchGameTo(long playerID,int gameID,int fromGameID)
	{
		PlayerSwitchGameToRecordData sData=getSwitchToPlayerByID(playerID);
		
		if(sData==null)
		{
			Ctrl.warnLog("源服收到预备切换game服时，找不到switchTo数据",playerID);
			//RePlayerPreSwitchGameToGameServerRequest.create(playerID,true).send(fromGameID);
			return;
		}
		
		if(sData.isExiting)
		{
			sData.warnLog("源服playerSwitchGame时,已在退出中");
			RePlayerPreSwitchGameToGameServerRequest.create(playerID,true).send(fromGameID);
			return;
		}
		
		//正在切换中
		if(sData.isSwitching())
		{
			sData.warnLog("源服playerSwitchGame时,正在切换中");
			return;
		}
		
		sData.targetGameID=gameID;
		sData.state=PlayerSwitchStateType.SourceWait;
		sData.startWait();
		
		RePlayerPreSwitchGameToGameServerRequest.create(playerID,false).send(fromGameID);
	}
	
	/** 玩家进入（源服通知）(成功进入) */
	public void playerSwitchGameCompleteToSource(long playerID,int gameID)
	{
		PlayerSwitchGameToRecordData sData=getSwitchToPlayerByID(playerID);
		
		if(sData==null)
		{
			Ctrl.warnLog("源切换完成时,记录为空");
			return;
		}
		
		//不是切换中
		if(sData.isExiting)
		{
			sData.warnLog("源服playerSwitchGame时,已在退出中");
			return;
		}
		
		//不是切换中
		if(!sData.isSwitching())
		{
			sData.warnLog("源服playerSwitchGame时,不在切换中");
			return;
		}
		
		sData.clearSwitchGame();
		sData.nowGameID=gameID;
		sData.state=PlayerSwitchStateType.SourceReady;
		
		if(ShineSetting.needDebugLog)
		{
			sData.debugLog("切换游戏服完成");
		}
		
		//清离线事务
		sData.flushSwitch(gameID);
		
		//依旧free
		if(!sData.isSwitching())
		{
			//TODO:做好下个切换场景部分
			
			//进入下一个场景
			SceneEnterArgData nextScene;
			if((nextScene=sData.getNextEnterSceneLocation())!=null)
			{
				sData.setNextSceneLocation(null);
				sourcePlayerEnterSignedScene(playerID,nextScene);
			}
		}
	}
	
	/** 源服角色登录场景 */
	private void sourcePlayerEnterSignedScene(long playerID,SceneEnterArgData nextScene)
	{
	
	}
	
	/** 玩家预备退出 */
	public void sourcePlayerPreExit(long playerID,int gameID)
	{
		PlayerSwitchGameToRecordData sData=getSwitchToPlayerByID(playerID);
		
		if(sData==null)
		{
			Ctrl.warnLog("sourcePlayerPreExit时,找不到switch数据",playerID);
			return;
		}
		
		if(!sData.isExiting)
		{
			if(ShineSetting.needDebugLog)
				sData.debugLog("角色预备,playerPreExit");
			
			//退出切换
			sData.isExiting=true;
			sData.debugLog("playerExitForSwitchTo标记退出",2);
			sData.startWait();
			
			if(!sData.isSwitching())
			{
				if(ShineSetting.needDebugLog)
					sData.debugLog("预备退出,强制设置切换game为源服");
				
				sData.targetGameID=sData.keyData.sourceGameID;//标记源服
			}
		}
		
		RePlayerPreExitToGameServerRequest.create(playerID).send(gameID);
	}
	
	/** 源服切换数据退出 */
	public void sourceSwitchToExit(long playerID,boolean needWarn)
	{
		PlayerSwitchGameToRecordData sData=getSwitchToPlayerByID(playerID);
		
		if(sData==null)
		{
			if(needWarn)
			{
				Ctrl.debugLog("sourceSwitchToExit时,找不到switch数据",playerID);
			}
			
			return;
		}
		
		sData.clearSwitchGame();
		
		Player player;
		
		if((player=GameC.main.getPlayerByIDT(playerID))!=null)
		{
			sData.doFlushToPlayer(player,true);
		}
		else if((player=GameC.main.getOfflinePlayer(playerID))!=null)
		{
			sData.doFlushToPlayer(player,false);
		}
		
		removePlayerFromSwitchTo(sData);
	}
	
	/** 角色切换目标服务器数据接收成功 */
	public void sourcePlayerSwitchReceiveResult(long playerID,int fromGameID,boolean isSuccess)
	{
		PlayerSwitchGameToRecordData sData=getSwitchToPlayerByID(playerID);
		
		if(sData==null)
		{
			Ctrl.warnLog("sourcePlayerSwitchReceiveResult时,找不到switch数据",playerID);
			return;
		}
		
		if(fromGameID!=sData.nowGameID)
		{
			sData.errorLog("发来的服不该与nowGameID不一致");
		}
		
		//标记接收成功
		sData.hasReceive=true;
		
		//不成功就取消切换
		if(!isSuccess)
		{
			sData.clearSwitchGame();
		}
		else
		{
			//推送
		}
		
		//标记了要退出
		if(sData.isExiting)
		{
			int toGameID=sData.isSwitching() ? sData.targetGameID : sData.nowGameID;
			
			if(ShineSetting.needDebugLog)
				sData.debugLog("推送退出召回",toGameID);
			
			if(toGameID==GameC.app.id)
			{
				GameC.main.onCallPlayerExit(playerID);
			}
			else
			{
				//推送到当前的game服
				PlayerCallSwitchBackToGameServerRequest.create(playerID).send(toGameID);
			}
		}
	}
	
	//--switch过程--//
	
	/** 玩家切换游戏服(发起第一步)(已经从上个场景脱离)(主线程) */
	public void playerSwitchToGame(Player player,int gameID)
	{
		if(GameC.main.checkNeedExit(player))
			return;
		
		//退出中
		if(player.system.isStateExiting())
			return;
		
		//不是在线或登录中状态
		if(!player.system.isStateOnlineOrLogining())
		{
			player.errorLog("playerSwitchToGame时,状态不对");
			return;
		}
		
		//标记切换
		player.system.setLoginState(PlayerLoginStateType.Switching);
		
		//加入到切换组
		PlayerSwitchGameToRecordData recordData=new PlayerSwitchGameToRecordData();
		recordData.nowGameID=GameC.app.id;
		recordData.targetGameID=gameID;
		recordData.keyData=player.role.createPrimaryKeyData();
		recordData.state=PlayerSwitchStateType.NoticeToSource;
		recordData.startWait();
		
		recordData.socket=player.system.socket;
		
		//添加了为了待会返回的接收
		addPlayerToSwitchTo(recordData);
		
		if(ShineSetting.needDebugLog)
			player.debugLog("playerSwitchToGame第一步");
		
		//当前服是源服
		if(player.role.isCurrentGame())
		{
			onRePlayerPreSwitchToGame(player.role.playerID,false);
		}
		else
		{
			//发到源服
			PlayerPreSwitchGameToGameServerRequest.create(player.role.playerID,gameID).send(player.role.sourceGameID);
		}
	}
	
	/** 切换到数据超时 */
	protected void playerSwitchToTimeOut(PlayerSwitchGameToRecordData data)
	{
		//TODO:处理好所有的timeOut问题
		
		if(ShineSetting.needDebugLog)
			data.debugLog("playerSwitchToTimeOut",data.state);
		
		switch(data.state)
		{
			case PlayerSwitchStateType.NoticeToSource:
			{
				//移除
				removePlayerFromSwitchTo(data);
				
				Player player=GameC.main.getPlayerByID(data.keyData.playerID);
				
				if(player!=null)
				{
					if(!player.system.isStateSwitching())
					{
						Ctrl.warnLog("playerSwitchToTimeOut超时时，角色状态不对",player.system.getLoginState());
						return;
					}
					
					//回归在线状态
					player.system.setLoginState(PlayerLoginStateType.Online);
					
					GameC.main.checkNeedExit(player);
				}
				else
				{
					data.warnLog("playerSwitchToTimeOut时,不该找不到角色");
				}
			}
				break;
			case PlayerSwitchStateType.SourceWait:
			{
				//从switch移除
				removePlayerFromSwitchTo(data);
				
				if(ShineSetting.needDebugLog)
					Ctrl.debugLog("角色切换游戏服超时,SourceWait",data.keyData.getInfo());
				
				data.callExitOvers();
				
				data.clearSwitchGame();
				
				//data.flushSwitch();
			}
				break;
			case PlayerSwitchStateType.SendToTarget:
			{
				playerSwitchToGameRebuild(data);
			}
				break;
			case PlayerSwitchStateType.WaitClient:
			{
				playerSwitchToGameNextTimeOut(data);
			}
				break;
			default:
			{
				Ctrl.errorLog("未处理的switchTo超时类型",data.state);
			}
				break;
		}
	}
	
	/** 收到源服返回结果，角色预备切换消息(发起第二步) */
	public void onRePlayerPreSwitchToGame(long playerID,boolean needExit)
	{
		PlayerSwitchGameToRecordData sData=getSwitchToPlayerByID(playerID);
		
		if(sData==null)
		{
			Ctrl.warnLog("角色预备切换game时,switchTo数据不存在",playerID);
			return;
		}
		
		Player player=GameC.main.getPlayerByIDT(playerID);
		
		if(player==null)
		{
			Ctrl.warnLog("角色预备切换game时,为空",playerID);
			return;
		}
		
		SystemPart sPart=player.system;
		
		//不是切换中状态
		if(!sPart.isStateSwitching())
		{
			player.warnLog("角色预备切换game时,不是切换中状态(停服阶段正常,已被改为退出状态)");
			return;
		}
		
		if(sData.switchToWaitTime==0)
		{
			player.warnLog("角色预备切换game时,已到时");
			return;
		}
		
		//倒计时清0
		sData.switchToWaitTime=0;
		
		if(GameC.main.checkNeedExit(player))
			return;
		
		//需要退出
		if(needExit)
		{
			GameC.main.playerExit(player,InfoCodeType.PlayerExit_callSwitchBack);
			return;
		}
		
		int gameID=sData.targetGameID;
		
		if(gameID<=0)
		{
			Ctrl.errorLog("不该没有目标gameID");
			return;
		}
		
		sData.state=PlayerSwitchStateType.SwitchCurrent;
		sData.startWait();
		
		//解绑socket
		sPart.socket.unbindPlayer();
		
		//global退出
		GameC.global.onPlayerLeave(player);
		
		if(ShineSetting.needDebugLog)
			player.debugLog("onRePlayerPreSwitchToGame第二步");
		
		//需要切服登录
		if(player.system.isSwitchLogin())
		{
			//写出列表数据
			PlayerListData listData=player.createListData();
			//深拷
			player.writeListData(listData);
			
			PlayerSwitchGameData switchData=player.writeSwitchGameData();
			
			if(ShineSetting.needDebugLog)
				player.debugLog("onRePlayerPreSwitchToGame第二步(2)_mainFunc");
			
			playerSwitchToGameNext(player,gameID,listData,switchData);
		}
		else
		{
			//切逻辑线程
			player.addFunc(()->
			{
				if(ShineSetting.needDebugLog)
					player.debugLog("onRePlayerPreSwitchToGame第二步_playerFunc");
				
				sPart.getExecutor().playerSwitchGameOut(player);
				
				//写出列表数据
				PlayerListData listData=player.createListData();
				//潜拷
				player.writeListData(listData);
				
				PlayerSwitchGameData switchData=player.writeSwitchGameData();
				
				player.addMainFunc(()->
				{
					if(ShineSetting.needDebugLog)
						player.debugLog("onRePlayerPreSwitchToGame第二步_mainFunc");
					
					playerSwitchToGameNext(player,gameID,listData,switchData);
				});
			});
		}
		
		
	}
	
	/** 角色切换到Game下一步(发起第三步)(主线程) */
	private void playerSwitchToGameNext(Player player,int gameID,PlayerListData listData,PlayerSwitchGameData switchData)
	{
		//不是切换过程
		if(!player.system.isStateSwitching())
		{
			Ctrl.warnLog("切换到Game next时，不是切换状态");
			return;
		}
		
		if(GameC.main.checkNeedExitThird(player))
			return;
		
		PlayerSwitchGameToRecordData sData=getSwitchToPlayerByID(player.role.playerID);
		
		if(sData==null)
		{
			Ctrl.warnLog("切换到Game next时,switchTo数据不存在",player.role.playerID);
			return;
		}
		
		if(ShineSetting.needDebugLog)
			player.debugLog("playerSwitchToGameNext第三步",gameID);
		
		GameC.main.removePlayer(player);
		
		//深拷一下
		switchData=(PlayerSwitchGameData)switchData.clone();
		
		//标记离线
		player.system.setLoginState(PlayerLoginStateType.Offline);
		
		//加入到切换组
		sData.listData=listData;
		sData.switchData=switchData;
		sData.socket=player.system.socket;//绑定socket
		sData.startWait();
		
		player.system.socket=null;
		
		GameC.main.removeExistPlayer(player);
		
		sData.state=PlayerSwitchStateType.SendToTarget;
		sData.startWait();
		
		//发送到目标服
		PreSwitchGameServerRequest.create(sData.keyData,listData,switchData).send(gameID);
	}
	
	/** 角色切换到目标游戏服失败(因目标服务器退出中) */
	public void onPlayerSwitchToGameFailed(long playerID)
	{
		PlayerSwitchGameToRecordData sData=getSwitchToPlayerByID(playerID);
		
		if(sData==null)
		{
			Ctrl.errorLog("onPlayerSwitchToGameFailed时,找不到角色切换去数据",playerID);
			return;
		}
		
		PlayerSwitchGameReceiveResultToSourceServerRequest.create(playerID,false).send(sData.keyData.sourceGameID);
		
		//重建
		playerSwitchToGameRebuild(sData);
	}
	
	/** 切换去game服后,目标服务器一直没有返回,超时 */
	private void playerSwitchToGameNextTimeOut(PlayerSwitchGameToRecordData data)
	{
		if(ShineSetting.needDebugLog)
			Ctrl.debugLog("切换去game服后,目标服务器一直没有返回,超时(停服阶段此情况正常)",data.keyData.getInfo(),data.isExiting);
		
		GameC.main.playerExitForSwitchTo(data,InfoCodeType.PlayerExit_callSwitchBack,null);
	}
	
	/** 切换去game失败，数据重建 */
	private void playerSwitchToGameRebuild(PlayerSwitchGameToRecordData data)
	{
		if(GameC.main.getPlayerByIDT(data.keyData.playerID)!=null)
		{
			Ctrl.throwError("此时角色不该在线");
			return;
		}
		
		if(ShineSetting.needDebugLog)
			Ctrl.debugLog("playerSwitchToGameRebuild",data.keyData.getInfo());
		
		doSwitchReceive(data.socket,data.keyData,data.listData,data.switchData,false);
	}
	
	/** 收到预备切换Game(接收第一步/发送第四步)(主线程) */
	public void preSwitchGameReceive(int fromGameID,PlayerPrimaryKeyData keyData,PlayerListData listData,PlayerSwitchGameData switchData)
	{
		//已是退出中
		if(ShineSetup.isExiting())
		{
			if(ShineSetting.needDebugLog)
				Ctrl.debugLog("退出状态立即返回,preSwitchGameReceive",keyData.getInfo());
			
			RePreSwitchGameFailedServerRequest.create(keyData.playerID).send(fromGameID);
			return;
		}
		
		Player player;
		
		if((player=GameC.main.getPlayerByIDT(keyData.playerID))!=null)
		{
			player.errorLog("此时不该有角色在线,preSwitchGameReceive");
			return;
		}
		
		if(ShineSetting.needDebugLog)
			Ctrl.debugLog("接收切换数据",fromGameID,keyData.getInfo(),Ctrl.getTimer());
		
		PlayerSwitchGameReceiveRecordData data;
		
		//不为空
		if((data=getSwitchReceivePlayerByID(keyData.playerID))!=null)
		{
			Ctrl.warnLog("收到预备切换时,存在旧数据,可能是中途客户端下线导致");
			removePlayerFromSwitchReceive(data);
		}
		else
		{
			data=new PlayerSwitchGameReceiveRecordData();
		}
		
		data.keyData=keyData;
		data.listData=listData;
		data.switchData=switchData;
		
		data.token=MathUtils.getToken();
		
		while(_switchReceiveDicByToken.contains(data.token))
		{
			data.token=MathUtils.getToken();
		}
		
		data.timeOut=ShineSetting.affairDefaultExecuteTime;
		
		addPlayerToSwitchReceive(data);
		
		//回登录
		GameServerInfoData info=GameC.server.getInfo();
		RePreSwitchGameServerRequest.create(keyData.playerID,data.token,info.clientHost,info.clientPort).send(fromGameID);
	}
	
	/** 接收到切换游戏服数据后，客户端一直没有登录,超时 */
	private void preSwitchGameReceiveTimeOut(PlayerSwitchGameReceiveRecordData data)
	{
		removePlayerFromSwitchReceive(data);
		
		if(ShineSetting.needDebugLog)
			Ctrl.debugLog("接收到切换游戏服数据后，客户端一直没有登录,超时",data.keyData.getInfo());
		
		if(data.keyData.sourceGameID==GameC.app.id)
		{
			onPlayerExitSwitchBack(data.keyData.playerID,data.listData);
		}
		else
		{
			//发送到源game服
			PlayerExitSwitchBackServerRequest.create(data.keyData.playerID,data.listData).send(data.keyData.sourceGameID);
		}
	}
	
	/** 接受数据退出 */
	public void switchReceiveExit(PlayerSwitchGameReceiveRecordData data)
	{
		removePlayerFromSwitchReceive(data);
		
		if(data.keyData.sourceGameID==GameC.app.id)
		{
			onPlayerExitSwitchBack(data.keyData.playerID,data.listData);
		}
		else
		{
			//发送到源game服
			PlayerExitSwitchBackServerRequest.create(data.keyData.playerID,data.listData).send(data.keyData.sourceGameID);
		}
	}
	
	/** 收到预切换game消息(目标服务器传来)(收到接收第二步) */
	public void rePreSwitchToGame(long playerID,int token,String host,int port)
	{
		PlayerSwitchGameToRecordData recordData=getSwitchToPlayerByID(playerID);
		
		if(recordData==null)
		{
			if(ShineSetting.needDebugLog)
				Ctrl.debugLog("rePreSwitchGame时,角色信息不存在",playerID);
			return;
		}
		
		if(GameC.main.isCurrentGame(playerID))
		{
			sourcePlayerSwitchReceiveResult(playerID,recordData.keyData.sourceGameID,true);
		}
		else
		{
			//通知源服数据接收
			PlayerSwitchGameReceiveResultToSourceServerRequest.create(playerID,true).send(recordData.keyData.sourceGameID);
		}
		
		
		recordData.switchToWaitTime=0;
		
		//不是当前服
		if(recordData.keyData.sourceGameID!=GameC.app.id)
		{
			if(ShineSetting.needDebugLog)
				Ctrl.debugLog("移除switchTo数据",recordData.keyData.getInfo());
			
			//移除
			removePlayerFromSwitchTo(recordData);
			
			//退出状态
			if(recordData.isExiting)
			{
				recordData.callExitOvers();
				return;
			}
		}
		
		//退出状态不执行
		if(recordData.isExiting)
		{
			if(ShineSetting.needDebugLog)
				Ctrl.debugLog("rePreSwitchGame时,已是退出状态",recordData.keyData.getInfo());
			return;
		}
		
		if(ShineSetting.needDebugLog)
			Ctrl.debugLog("开始切换客户端部分",recordData.keyData.getInfo(),port);
		
		//倒计时结束
		recordData.state=PlayerSwitchStateType.WaitClient;
		recordData.startWait();
		
		recordData.socket.send(SwitchGameRequest.create(ClientLoginServerInfoData.create(token,host,port)));
		//由服务器断开连接
		recordData.socket.close();
	}
	
	/** 角色切换game服(客户端连接到接收第三步) */
	public void playerSwitchGameReceive(GameReceiveSocket socket,int token)
	{
		PlayerSwitchGameReceiveRecordData data=_switchReceiveDicByToken.get(token);
		
		if(data==null)
		{
			Ctrl.warnLog("未找到切换数据,可能超时");
			socket.sendInfoCode(InfoCodeType.SwitchGameFailed_noSwitchData);
			return;
		}
		
		Player player;
		
		if((player=GameC.main.getPlayerByIDT(data.keyData.playerID))!=null)
		{
			player.throwError("此时角色不该在线,playerSwitchGameReceive");
			return;
		}
		
		//清人中
		if(GameC.main.isClearing())
		{
			socket.sendInfoCode(InfoCodeType.PlayerExit_serverClose);
			socket.close();
			return;
		}
		
		//先移除
		removePlayerFromSwitchReceive(data);
		
		if(ShineSetting.needDebugLog)
			Ctrl.debugLog("playerSwitchGameReceive第三步",data.keyData.getInfo());
		
		doSwitchReceive(socket,data.keyData,data.listData,data.switchData,true);
	}
	
	/** 执行切换落地(主线程) */
	private void doSwitchReceive(GameReceiveSocket socket,PlayerPrimaryKeyData keyData,PlayerListData listData,PlayerSwitchGameData switchData,boolean isSuccess)
	{
		if(ShineSetting.needDebugLog)
			Ctrl.debugLog("接收切换游戏服数据",keyData.getInfo());
		
		Player player=null;
		
		//看切换到数据
		PlayerSwitchGameToRecordData switchToRecordData=getSwitchToPlayerByID(keyData.playerID);
		
		//其他服的要下线的 切换去数据 不创建角色实例
		if(!(switchToRecordData!=null && switchToRecordData.isExiting && keyData.sourceGameID!=GameC.app.id))
		{
			player=GameC.main.createPlayer();
			//初始化一下
			player.init();
			
			player.role.readPrimaryKeyData(keyData);
			//读
			player.readListData(listData);
			//读后
			player.afterReadData();
			//读切换数据
			player.readSwitchGameData(switchData);
		}
		
		if(switchToRecordData!=null)
		{
			if(player!=null)
			{
				switchToRecordData.doFlushToPlayer(player,false);
			}
			
			//要退出的
			if(switchToRecordData.isExiting)
			{
				if(socket!=null)
				{
					//发送退出码并且退出
					socket.sendInfoCode(switchToRecordData.exitCode);
					socket.close();
				}
				
				if(ShineSetting.needDebugLog)
					Ctrl.debugLog("接收切换游戏服数据时,需要下线了",switchToRecordData.keyData.getInfo());
				
				if(player!=null)
				{
					//直接离线
					player.system.setLoginState(PlayerLoginStateType.Offline);
					player.onLogout();
					
					GameC.main.addOfflinePlayer(player);
					
					player.log("角色下线(switch回),isExiting的");
					
					if(player.role.isCurrentGame())
					{
						GameC.gameSwitch.sourceSwitchToExit(player.role.playerID,true);
					}
					else
					{
						PlayerExitOverToSourceServerRequest.create(player.role.playerID).send(player.role.sourceGameID);
					}
				}
				else
				{
					PlayerExitSwitchBackServerRequest.create(switchToRecordData.keyData.playerID,switchToRecordData.listData).send(switchToRecordData.keyData.sourceGameID);
				}
				
				switchToRecordData.callExitOvers();
				
				//移除数据
				removePlayerFromSwitchTo(switchToRecordData);
				return;
			}
			
			//移除数据
			removePlayerFromSwitchTo(switchToRecordData);
		}
		else
		{
			//是当前服,回归
			if(player.role.isCurrentGame())
			{
				player.throwError("回归的时候不该没数据");
			}
		}
		
		//下面是继续登录
		SystemPart sPart=player.system;
		
		//封号
		if(sPart.getPartData().isBlock)
		{
			Ctrl.warnLog("被封号");
			if(socket!=null)
			{
				socket.sendInfoCode(InfoCodeType.LoginGameFailed_isBlock);
				socket.close();
			}
			return;
		}
		
		if(sPart.getLoginState()!=PlayerLoginStateType.Offline)
		{
			Ctrl.warnLog("角色状态不对",sPart.getLoginState());
			
			if(socket!=null)
			{
				socket.sendInfoCode(InfoCodeType.SwitchGameFailed_notRightState);
			}
			
			return;
		}
		
		//清下回调
		sPart.clearFuncs();
		
		//登录中
		sPart.setLoginState(PlayerLoginStateType.Logining);
		
		GameC.main.addPlayerToLoginingDic(player);
		
		if(socket!=null)
		{
			//此时开重连
			if(CommonSetting.clientOpenReconnect)
			{
				socket.setOpenReconnect(true);
			}
			
			sPart.setSocketReady(false);
			//socket绑定
			socket.setPlayer(player);
			//赋值socket(因为接下来需要用)
			sPart.socket=socket;
		}
		
		boolean isSwitchLogin=player.system.isSwitchLogin();
		
		//添加到在线组
		GameC.main.addPlayer(player);
		
		//global进入
		GameC.global.onPlayerEnter(player);
		//主线程登录时刻
		player.beforeEnterOnMain();
		
		if(player.role.isCurrentGame())
		{
			//无需处理
		}
		else
		{
			//发中心服
			PlayerSwitchGameCompleteToSourceServerRequest.create(player.role.playerID).send(player.role.sourceGameID);
		}
		
		//该去的线程号
		int index;
		
		if(isSwitchLogin)
		{
			//该去的线程号
			index=player.scene.getExecutorIndexByData();
		}
		else
		{
			if(isSuccess)
			{
				SceneEnterArgData sceneEnterArg=player.scene.getPartData().currentSceneEnterArg;
				
				if(sceneEnterArg==null)
				{
					player.throwError("切换到game时,不能没有场景位置数据");
					return;
				}
				
				player.scene.makeSceneEnterArgExecutorIndex(sceneEnterArg);
				index=sceneEnterArg.location.executorIndex;
			}
			else
			{
				//清除当前要进入场景信息
				player.scene.clearCurrentSceneInfo();
				
				//该去的线程号
				index=player.scene.getExecutorIndexByData();
			}
		}
		
		//未指定场景
		if(index==-1)
		{
			Ctrl.throwError("跨服返回主城的执行器ID没做完");
		}
		
		LogicExecutor executor=GameC.main.getExecutor(index);
		
		Player playerT=player;
		
		//切到对应逻辑线程登录
		executor.addFunc(()->
		{
			if(isSwitchLogin)
			{
				//清楚状态
				playerT.system.setSwitchLogin(false);
				
				executor.playerLogin(playerT);
				
				//切主线程
				playerT.addMainFunc(()->
				{
					GameC.main.playerLoginOver(playerT);
				});
			}
			else
			{
				executor.playerSwitchGameIn(playerT);
				
				//切主线程
				playerT.addMainFunc(()->
				{
					GameC.main.playerLoginOver(playerT);
				});
			}
		});
	}
	
	/** 回复召回为空 */
	public void onReCallPlayerExitNone(long playerID)
	{
		PlayerSwitchGameToRecordData recordData=getSwitchToPlayerByID(playerID);
		
		if(recordData==null)
		{
			Ctrl.warnLog("回复召回为空时,找不到switch角色记录信息");
			return;
		}
		
		recordData.switchToWaitTime=0;
		
		if(ShineSetting.needDebugLog)
			Ctrl.debugLog("角色召回时,目标服务器为空",recordData.keyData.getInfo());
		
		onPlayerExitSwitchBack(playerID,recordData.listData);
	}
	
	/** 角色退出切换回 */
	public void onPlayerExitSwitchBack(long playerID,PlayerListData listData)
	{
		PlayerSwitchGameToRecordData recordData=getSwitchToPlayerByID(playerID);
		
		if(recordData==null)
		{
			Ctrl.warnLog("角色切换回时,找不到switch角色记录信息");
			return;
		}
		
		if(ShineSetting.needDebugLog)
			Ctrl.debugLog("removePlayerFromSwitchTo，onPlayerExitSwitchBack",recordData.keyData.getInfo());
		
		//从switch移除
		removePlayerFromSwitchTo(recordData);
		
		//创建角色
		Player player=GameC.main.createPlayer();
		//初始化一下
		player.init();
		
		player.role.readPrimaryKeyData(recordData.keyData);
		player.readListData(listData);
		//读后
		player.afterReadData();
		
		player.log("角色下线(switch回)");
		
		player.system.setLoginState(PlayerLoginStateType.Offline);
		
		recordData.doFlushToPlayer(player,false);
		
		player.onLogout();
		
		GameC.main.addOfflinePlayer(player);
		
		if(player.role.isCurrentGame())
		{
			sourceSwitchToExit(playerID,true);
		}
		else
		{
			PlayerExitOverToSourceServerRequest.create(player.role.playerID).send(player.role.sourceGameID);
		}
		
		recordData.callExitOvers();
	}
	
	public void doPlayerCenterRequest(Player player,byte[] data)
	{
		CenterTransGameToClientRequest request=CenterTransGameToClientRequest.create();
		request.setData(data);
		player.send(request);
	}
	
	/** 添加角色中心服直达消息 */
	public void addPlayerCenterRequest(long playerID,byte[] data)
	{
		Player player;
		
		if((player=GameC.main.getPlayerByID(playerID))!=null)
		{
			doPlayerCenterRequest(player,data);
			return;
		}
		
		if(GameC.main.isCurrentGame(playerID))
		{
			PlayerSwitchGameToRecordData sData=getSwitchToPlayerByID(playerID);
			
			if(sData!=null)
			{
				if(sData.isSwitching())
				{
					//if(sData.hasReceive)
					//{
					//	SendPlayerCenterRequestToGameServerRequest.create(playerID,data).send(sData.targetGameID);
					//}
					//else
					//{
						sData.addCacheCenterRequest(data);
					//}
				}
				else
				{
					SendPlayerCenterRequestToGameServerRequest.create(playerID,data).send(sData.nowGameID);
				}
			}
		}
		else
		{
			SendPlayerCenterRequestToGameServerRequest.create(playerID,data).send(GameC.main.getNowGameIDByLogicID(playerID));
		}
	}
	
	public void doPlayerToGameRequest(Player player,byte[] data)
	{
		_tempReadStream.setBuf(data);
		
		int mid=_tempReadStream.natureReadUnsignedShort();
		
		BaseResponse response=GameC.server.createServerResponse(mid);
		
		if(response==null)
		{
			Ctrl.errorLog("未找到mid为",mid,"的PlayerToGameResponse");
		}
		
		if(response instanceof PlayerToGameServerResponse)
		{
			((PlayerToGameServerResponse)response).me=player;
		}
		else if(response instanceof PlayerGameToGameServerResponse)
		{
			((PlayerGameToGameServerResponse)response).me=player;
		}
		
		//ThreadControl.addIOFunc(this.hashCode() & ThreadControl.ioThreadNumMark,()->
		//{
		//
		//});
		
		response.readFromStream(_tempReadStream,GameC.server);
		
		response.dispatch();
	}
	
	/** 添加角色中心服直达消息 */
	public void addPlayerToGameRequest(long playerID,byte[] data)
	{
		Player player;
		
		if((player=GameC.main.getPlayerByID(playerID))!=null)
		{
			doPlayerToGameRequest(player,data);
			return;
		}
		
		if(GameC.main.isCurrentGame(playerID))
		{
			PlayerSwitchGameToRecordData sData=getSwitchToPlayerByID(playerID);
			
			if(sData!=null)
			{
				if(sData.isSwitching())
				{
					//if(sData.hasReceive)
					//{
					//	SendPlayerToGameRequestToGameServerRequest.create(playerID,data).send(sData.targetGameID);
					//}
					//else
					//{
						sData.addCachePlayerToGameRequest(data);
					//}
				}
				else
				{
					SendPlayerToGameRequestToGameServerRequest.create(playerID,data).send(sData.nowGameID);
				}
			}
		}
		else
		{
			SendPlayerToGameRequestToGameServerRequest.create(playerID,data).send(GameC.main.getNowGameIDByLogicID(playerID));
		}
	}
}
