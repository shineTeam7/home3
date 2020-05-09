package com.home.commonGame.control;


import com.home.commonBase.config.game.AreaInfoConfig;
import com.home.commonBase.constlist.generate.FlowStepType;
import com.home.commonBase.constlist.generate.InfoCodeType;
import com.home.commonBase.constlist.system.GameAreaDivideType;
import com.home.commonBase.constlist.system.WorkSenderType;
import com.home.commonBase.constlist.system.WorkType;
import com.home.commonBase.control.CodeCheckRecord;
import com.home.commonBase.data.login.ClientLoginData;
import com.home.commonBase.data.login.ClientLoginExData;
import com.home.commonBase.data.login.ClientVersionData;
import com.home.commonBase.data.login.CreatePlayerData;
import com.home.commonBase.data.login.PlayerBindPlatformAWData;
import com.home.commonBase.data.login.PlayerCreatedWData;
import com.home.commonBase.data.login.PlayerDeletedToCenterWData;
import com.home.commonBase.data.login.PlayerDeletedWData;
import com.home.commonBase.data.login.PlayerLoginData;
import com.home.commonBase.data.login.PlayerLoginToEachGameData;
import com.home.commonBase.data.login.RePlayerLoginFromEachGameData;
import com.home.commonBase.data.system.CenterGlobalWorkData;
import com.home.commonBase.data.system.GameServerSimpleInfoData;
import com.home.commonBase.data.system.PlayerToPlayerTCCResultWData;
import com.home.commonBase.data.system.PlayerToPlayerTCCWData;
import com.home.commonBase.data.social.QueryPlayerAWData;
import com.home.commonBase.data.social.ReQueryPlayerOWData;
import com.home.commonBase.data.social.base.QueryPlayerResultData;
import com.home.commonBase.data.social.roleGroup.PlayerRoleGroupData;
import com.home.commonBase.data.social.roleGroup.PlayerRoleGroupSaveData;
import com.home.commonBase.data.social.roleGroup.work.PlayerToRoleGroupTCCResultWData;
import com.home.commonBase.data.social.roleGroup.work.PlayerToRoleGroupTCCWData;
import com.home.commonBase.data.system.AreaGlobalWorkCompleteData;
import com.home.commonBase.data.system.AreaGlobalWorkData;
import com.home.commonBase.data.system.AreaServerData;
import com.home.commonBase.data.system.ClientOfflineWorkData;
import com.home.commonBase.data.system.PlayerWorkCompleteData;
import com.home.commonBase.data.system.PlayerWorkData;
import com.home.commonBase.data.system.UserWorkData;
import com.home.commonBase.data.system.WorkCompleteData;
import com.home.commonBase.data.system.WorkData;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.global.Global;
import com.home.commonBase.part.player.data.SystemPartData;
import com.home.commonBase.part.player.list.PlayerListData;
import com.home.commonBase.table.table.PlayerNameTable;
import com.home.commonBase.table.table.PlayerTable;
import com.home.commonBase.utils.BaseGameUtils;
import com.home.commonGame.constlist.system.PlayerLoginPhaseType;
import com.home.commonGame.constlist.system.PlayerLoginStateType;
import com.home.commonGame.constlist.system.PlayerSwitchStateType;
import com.home.commonGame.dataEx.PlayerLoginEachGameTempData;
import com.home.commonGame.dataEx.PlayerSwitchGameReceiveRecordData;
import com.home.commonGame.dataEx.PlayerSwitchGameToRecordData;
import com.home.commonGame.dataEx.UserLoginRecordData;
import com.home.commonGame.global.GameC;
import com.home.commonGame.logic.func.RoleGroup;
import com.home.commonGame.net.request.login.ClientHotfixRequest;
import com.home.commonGame.net.request.login.CreatePlayerSuccessRequest;
import com.home.commonGame.net.request.login.DeletePlayerSuccessRequest;
import com.home.commonGame.net.request.login.RePlayerListRequest;
import com.home.commonGame.net.request.system.ClientHotfixConfigRequest;
import com.home.commonGame.net.request.system.SendInfoCodeRequest;
import com.home.commonGame.net.serverRequest.center.system.ReceiptWorkToCenterServerRequest;
import com.home.commonGame.net.serverRequest.center.system.SendCenterWorkCompleteToCenterServerRequest;
import com.home.commonGame.net.serverRequest.center.system.SendCenterWorkToCenterServerRequest;
import com.home.commonGame.net.serverRequest.center.system.SendMQueryPlayerWorkResultToCenterServerRequest;
import com.home.commonGame.net.serverRequest.center.system.SendPlayerOnlineNumToCenterServerRequest;
import com.home.commonGame.net.serverRequest.game.login.PlayerCallSwitchBackToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.login.PlayerExitSwitchBackServerRequest;
import com.home.commonGame.net.serverRequest.game.login.PlayerLoginToEachGameServerRequest;
import com.home.commonGame.net.serverRequest.game.login.PlayerPreExitToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.login.RePlayerLoginFromEachGameServerRequest;
import com.home.commonGame.net.serverRequest.game.login.RefreshGameLoginLimitToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.system.ReceiptWorkToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.system.SendAreaWorkCompleteToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.system.SendAreaWorkToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.system.SendPlayerWorkCompleteToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.system.SendPlayerWorkToGameServerRequest;
import com.home.commonGame.net.serverRequest.login.login.ReUserLoginToLoginServerRequest;
import com.home.commonGame.net.serverRequest.login.system.LimitAreaToLoginServerRequest;
import com.home.commonGame.net.serverRequest.login.system.SendUserWorkToLoginServerRequest;
import com.home.commonGame.part.player.Player;
import com.home.commonGame.part.player.part.SystemPart;
import com.home.commonGame.server.GameReceiveSocket;
import com.home.shine.ShineSetup;
import com.home.shine.bytes.BytesReadStream;
import com.home.shine.bytes.BytesWriteStream;
import com.home.shine.constlist.ThreadType;
import com.home.shine.control.DateControl;
import com.home.shine.control.ThreadControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.data.BaseData;
import com.home.shine.data.DateData;
import com.home.shine.dataEx.LogInfo;
import com.home.shine.dataEx.VInt;
import com.home.shine.global.ShineSetting;
import com.home.shine.net.base.BaseRequest;
import com.home.shine.net.socket.BaseSocket;
import com.home.shine.support.collection.IntIntMap;
import com.home.shine.support.collection.IntList;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.collection.IntSet;
import com.home.shine.support.collection.LongObjectMap;
import com.home.shine.support.collection.LongSet;
import com.home.shine.support.collection.SList;
import com.home.shine.support.collection.SMap;
import com.home.shine.support.collection.SSet;
import com.home.shine.support.func.ObjectCall;
import com.home.shine.support.pool.ObjectPool;
import com.home.shine.table.DBConnect;
import com.home.shine.thread.PoolThread;
import com.home.shine.utils.MathUtils;
import com.home.shine.utils.StringUtils;

/** 主控制 */
public class GameMainControl
{
	/** 本服承载区服数据组 */
	private IntObjectMap<AreaServerData> _areaDatas;
	/** 区服老新对照表(全部)(areaID:gameID) */
	private IntIntMap _areaDic=new IntIntMap();
	/** 是否为辅助服 */
	private boolean _isAssist;
	
	//pool
	/** player对象池 */
	private ObjectPool<Player> _playerPool;

	/** 客户端版本 */
	private IntObjectMap<ClientVersionData> _clientVersion;
	
	/** 执行器组(分线程) */
	private LogicExecutor[] _executors;
	
	private ObjectPool<UserLoginRecordData> _userLoginRecordDataPool=new ObjectPool<>(UserLoginRecordData::new);
	
	//userLogin第一阶段
	/** 令牌字典(key:userID) */
	private LongObjectMap<UserLoginRecordData> _userLoginDic=new LongObjectMap<>(UserLoginRecordData[]::new);
	/** 令牌字典(key:token) */
	private IntObjectMap<UserLoginRecordData> _userLoginDicByToken=new IntObjectMap<>(UserLoginRecordData[]::new);
	/** 登录中字典(socketID为key) */
	private IntObjectMap<UserLoginRecordData> _userLoginDicBySocketID=new IntObjectMap<>(UserLoginRecordData[]::new);
	

	
	//--exist--//
	/** 存在角色字典(playerID为key) */
	private LongObjectMap<Player> _existPlayers=new LongObjectMap<>(Player[]::new);
	
	//--online--//
	/** 在线角色字典(playerID为key) */
	private LongObjectMap<Player> _players=new LongObjectMap<>(Player[]::new);
	/** 在线角色字典(socketID为key) */
	private IntObjectMap<Player> _playersBySocketID=new IntObjectMap<>(Player[]::new);
	
	/** 在线唯一角色字典(再上线就替换)(userID->players) */
	private LongObjectMap<Player> _playersByUserID=new LongObjectMap<>(Player[]::new);
	
	///** 在线角色字典(name为key) */
	//private SMap<String,Player> _playersByName=new SMap<>();
	/** 在线角色IP字典 */
	private SMap<String,SSet<Player>> _playersByIP=new SMap<>();
	/** 在线角色数 */
	private int _playerOnlineNum=0;
	/** 上次统计在线时间 */
	private int _lastPlayerOnlineNum=0;
	/** 当前是否登录限制 */
	private boolean _isLoginLimit=false;
	/** 登录限制组 */
	private IntSet _loginLimitDic=new IntSet();
	
	/** 辅助服列表 */
	private IntList _assistGameList;
	/** 当前可用辅助服 */
	private int _nowAssistGame=-1;
	
	//--offline--//
	/** 离线角色字典(key:playerID) */
	private LongObjectMap<Player> _offlinePlayers=new LongObjectMap<>(Player[]::new);
	
	
	
	//--gatAbs--//
	/** 获取角色人物字典(key:playerID) */
	private LongObjectMap<SList<ObjectCall<Player>>> _getPlayerTaskDic=new LongObjectMap<>();
	
	//--delete--//
	/** 删除角色回调字典 */
	private LongObjectMap<Runnable> _deletePlayerCallDic=new LongObjectMap<>();
	
	//operate count
	
	/** 今日新进入角色数目 */
	private int _todayNewPlayerNum=0;
	/** 今日创建角色数目 */
	private int _todayCreatePlayerNum=0;
	/** 今日在线最高峰值 */
	private int _todayTopOnlineNum=0;
	
	/** 流程日志 */
	private LogInfo _flowLog=new LogInfo();
	
	
	
	//temp
	/** 是否在清理中 */
	private boolean _isClearing=false;
	/** 清理角色超时 */
	private int _clearPlayerTimeOut;
	/** 清理完成回调 */
	private Runnable _clearOverFunc;
	
	/** 临时写流 */
	private BytesWriteStream _tempWriteStream=BytesWriteStream.create();
	/** 临时角色ID列表 */
	private LongSet _tempPlayerSet=new LongSet();
	
	/** 临时可注册的区服 */
	private IntSet _canRegisterAreaSet=new IntSet();
	/** 上次最大承载限制 */
	private boolean _lastMaxBearLimit=false;
	
	public void init()
	{
		_playerPool=new ObjectPool<>(()->
		{
			Player player=GameC.factory.createPlayer();
			player.construct();
			return player;
		},CommonSetting.playerPoolSize);
		
		_playerPool.setEnable(CommonSetting.playerUsePool);
		
		_executors=new LogicExecutor[ShineSetting.poolThreadNum];
		
		for(int i=0;i<_executors.length;++i)
		{
			LogicExecutor executor=createExecutor(i);
			_executors[i]=executor;
			
			ThreadControl.addPoolFunc(i,()->
			{
				executor.init();
			});
		}
		
	}
	
	public void initAfterGlobal()
	{
		_areaDatas=new IntObjectMap<>(AreaServerData[]::new);
		
		GameC.server.getInfo().areaIDList.forEach(v->
		{
			AreaServerData sData=new AreaServerData();
			sData.areaID=v;
			sData.name=AreaInfoConfig.getAreaName(v);
			
			countRegistLimit(sData);
			_areaDatas.put(sData.areaID,sData);
		});
		
		//加上当前区服
		if(!_areaDatas.contains(GameC.app.id))
		{
			AreaServerData sData=new AreaServerData();
			sData.areaID=GameC.app.id;
			countRegistLimit(sData);
			_areaDatas.put(sData.areaID,sData);
		}
		
		
		//TODO:后续需要做更新
		GameC.server.getGameSimpleInfoDic().forEach((k,v)->
		{
			//先放自己
			_areaDic.put(k,k);
			
			IntList areaIDList=v.areaIDList;
			
			for(int i=0;i<areaIDList.size();i++)
			{
				//再放承载区服
				_areaDic.put(areaIDList.get(i),k);
			}
		});
		
		
	}
	
	public void initTick()
	{
		ThreadControl.getMainTimeDriver().setInterval(this::onSecond,1000);
		ThreadControl.getMainTimeDriver().setInterval(this::onPlayerOfflineCheck,CommonSetting.playerOfflineCheckDelay * 1000);
		ThreadControl.getMainTimeDriver().setInterval(this::onlinePlayerNumCheck,10000);
	}
	
	/** 析构 */
	public void dispose()
	{
	
	}
	
	/** 是否为辅助服 */
	public boolean isAssist()
	{
		return _isAssist;
	}
	
	/** 每秒 */
	private void onSecond(int delay)
	{
		if(_isClearing)
		{
			checkClearOver();
			
			if(_isClearing && --_clearPlayerTimeOut==0)
			{
				Ctrl.warnLog("清理角色过程超时");
				printClearLast();
				clearOver();
			}
		}
		
		GameC.global.onSecond(delay);
		
		onlinePlayerSecond();
		userLoginSecond();
	}
	
	/** 输出清空剩余 */
	private void printClearLast()
	{
		if(!ShineSetting.needDebugLog)
			return;
		
		_players.forEachValue(v->
		{
			Ctrl.debugLog("剩余在线",v.getInfo());
		});
		
		GameC.gameSwitch.printClearLast();
	}
	
	/** 在线角色每秒 */
	private void onlinePlayerSecond()
	{
		Player[] values;
		Player v;
		
		for(int i=(values=_players.getValues()).length-1;i>=0;--i)
		{
			if((v=values[i])!=null)
			{
				try
				{
					if(v.system.exitWaitTime>0)
					{
						if((--v.system.exitWaitTime)==0)
						{
							if(v.system.isStateExiting())
							{
								if(ShineSetting.needDebugLog)
									v.debugLog("退出阶段超时 by exitWaitTime");
								
								playerExitNext(v);
							}
							else
							{
								v.warnLog("exitWaitTime超时时,状态不对",v.system.getLoginState());
							}
						}
					}
					
					PlayerLoginEachGameTempData tData;
					if((tData=v.system.loginEachTempData)!=null)
					{
						if((--tData.waitTime)==0)
						{
							if(v.system.isStateLogining())
							{
								onRePlayerLoginEachGameTimeOut(v);
							}
							else
							{
								v.warnLog("loginGameWaitTime超时时,状态不对",v.system.getLoginState());
								v.system.loginEachTempData=null;
							}
						}
					}
				}
				catch(Exception e)
				{
					v.errorLog(e);
				}
			}
		}
	}
	
	private void userLoginSecond()
	{
		_userLoginDic.forEachValueS(v->
		{
			if(v.loginTime>0)
			{
				if((--v.loginTime)<=0)
				{
					removeUserLoginData(v);
				}
			}
		});
	}
	
	
	//构造接口
	
	/** 创建执行器 */
	protected LogicExecutor createExecutor(int index)
	{
		return new LogicExecutor(index);
	}
	
	//方法
	
	/** 从池中获取角色对象 */
	public Player createPlayer()
	{
		return _playerPool.getOne();
	}
	
	/** 移除存在角色 */
	public void removeExistPlayer(Player player)
	{
		_existPlayers.remove(player.role.playerID);
		releasePlayer(player);
	}
	
	/** 析构并回收角色(主线程) */
	private void releasePlayer(Player player)
	{
		//析构
		try
		{
			player.dispose();
		}
		catch(Exception e)
		{
			Ctrl.errorLog(e);
		}
		
		_playerPool.back(player);
	}
	
	/** 获取对应执行器 */
	public LogicExecutor getExecutor(int index)
	{
		if(index==-1)
		{
			return null;
		}
		
		return _executors[index];
	}
	
	private void countRegistLimit(AreaServerData data)
	{
		data.isLimitRegist=Global.areaDesignRegistNum>0 && GameC.global.system.getAreaRegistNum(data.areaID)>=Global.areaDesignRegistNum;
		
		if(!data.isLimitRegist)
		{
			_canRegisterAreaSet.add(data.areaID);
		}
	}
	
	/** 获取当前服务期区服数据 */
	public IntObjectMap<AreaServerData> getAreaDatas()
	{
		return _areaDatas;
	}
	
	//config
	
	/** 重新加载配置(主线程) */
	public void reloadConfig()
	{
		Ctrl.log("reloadGameConfig");

		BaseC.config.reload(this::onReloadConfig,()->
		{
			Ctrl.log("reloadGameConfigComplete");
			
			byte[] bytes=BaseC.config.getHotfixBytes();
			
			if(bytes!=null)
			{
				//通知角色
				radioAllPlayer(ClientHotfixConfigRequest.create(bytes));
			}
		});
	}
	
	private void onReloadConfig()
	{
		if(ThreadControl.isMainThread())
		{
			//global
			GameC.global.onReloadConfig();
			
			//TODO:这里看要不要加角色的reload
		}
		else
		{
			PoolThread poolThread=(PoolThread)ThreadControl.getCurrentShineThread();
			
			_executors[poolThread.index].onReloadConfig();
		}
	}
	
	/** 角色离线检查 */
	private void onPlayerOfflineCheck(int delay)
	{
		Player[] values;
		Player v;
		
		for(int i=(values=_offlinePlayers.getValues()).length-1;i>=0;--i)
		{
			if((v=values[i])!=null)
			{
				if(v.system.offlineKeepTime>0)
				{
					if((v.system.offlineKeepTime-=CommonSetting.playerOfflineCheckDelay)<=0)
					{
						v.system.offlineKeepTime=0;
						
						removeOfflinePlayer(v);
						
						++i;
					}
				}
			}
		}
	}
	
	/** 在线角色数检查 */
	private void onlinePlayerNumCheck(int delay)
	{
		if(_lastPlayerOnlineNum!=_playerOnlineNum)
		{
			_lastPlayerOnlineNum=_playerOnlineNum;
			
			SendPlayerOnlineNumToCenterServerRequest.create(_playerOnlineNum).send();
			
			//看看是否变化
			boolean isLimit=_lastPlayerOnlineNum>=Global.gameMaxBearNum;
			
			if(isLimit!=_lastMaxBearLimit)
			{
				_lastMaxBearLimit=isLimit;
				
				_canRegisterAreaSet.forEachA(v -> {
					GameC.server.radioLogins(LimitAreaToLoginServerRequest.create(v,_lastMaxBearLimit));
				});
			}
		}
	}
	
	
	//player字典
	
	/** 添加在线角色 */
	public void addPlayer(Player player)
	{
		if(ShineSetting.openCheck)
		{
			if(GameC.gameSwitch.getSwitchReceivePlayerByID(player.role.playerID)!=null)
			{
				player.throwError("添加在线角色时,不该存在切换接收角色");
			}
		}
		
		//存在角色
		_existPlayers.put(player.role.playerID,player);
		
		_players.put(player.role.playerID,player);
		_playersByUserID.put(player.role.userID,player);
		
		_playerOnlineNum=_players.length();
		onlineNumChanged();
		
		//player.debugLog("addPlayer");
		//Ctrl.printStackTrace();
		
		toAddPlayerBySocket(player);
	}
	
	/** 添加player的socket部分 */
	private void toAddPlayerBySocket(Player player)
	{
		_playersBySocketID.put(player.system.socket.id,player);
		
		String ip;
		
		if(!(ip=player.system.socket.remoteIP()).isEmpty())
		{
			_playersByIP.computeIfAbsent(ip,k->new SSet<>()).add(player);
		}
	}
	
	/** 删除在线角色 */
	public void removePlayer(Player player)
	{
		_players.remove(player.role.playerID);
		_playersByUserID.remove(player.role.userID);
		
		_playerOnlineNum=_players.length();
		onlineNumChanged();
		
		//移除player的socket部分
		toRemovePlayerBySocket(player);
	}
	
	private void toRemovePlayerBySocket(Player player)
	{
		_playersBySocketID.remove(player.system.socket.id);
		
		SSet<Player> dic2;
		String ip;
		
		if((dic2=_playersByIP.get(ip=player.system.socket.remoteIP()))!=null)
		{
			dic2.remove(player);
			
			if(dic2.isEmpty())
			{
				_playersByIP.remove(ip);
			}
		}
	}
	
	protected void onlineNumChanged()
	{
		boolean bb=_playerOnlineNum>=Global.gameDesignBearNum;
		
		if(bb!=_isLoginLimit)
		{
			_isLoginLimit=bb;
			
			//辅助服
			if(_isAssist)
			{
				GameC.server.radioGames(RefreshGameLoginLimitToGameServerRequest.create(bb));
			}
		}
	}
	
	/** 是否登录限制 */
	public boolean isLoginLimit()
	{
		return _isLoginLimit;
	}
	
	/** 登录限制改变 */
	public void onLoginLimitChange(int gameID,boolean isLoginLimit)
	{
		if(isLoginLimit)
			_loginLimitDic.add(gameID);
		else
			_loginLimitDic.remove(gameID);
		
		findAvailableAssistGame();
	}
	
	/** 找可用辅助服 */
	private void findAvailableAssistGame()
	{
		_nowAssistGame=-1;
		
		if(!CommonSetting.needPlayerFullTransToOtherGame)
			return;
		
		if(_assistGameList.isEmpty())
			return;
		
		int[] values=_assistGameList.getValues();
		int v;
		
		//按顺序找
		for(int i=0,len=_assistGameList.size();i<len;++i)
		{
			v=values[i];
			
			if(!_loginLimitDic.contains(v))
			{
				_nowAssistGame=v;
				return;
			}
		}
	}
	
	/** 当前游戏服是否承载已满 */
	public boolean isCurrentGameFull()
	{
		return _playerOnlineNum>=Global.gameDesignBearNum;
	}
	
	/** 添加到离线角色 */
	public void addOfflinePlayer(Player player)
	{
		if(ShineSetting.openCheck)
		{
			if(!player.role.isCurrentGame())
			{
				player.throwError("不是当前服,不该加入离线组");
			}
		}
		
		player.system.refreshOfflineKeepTime();
		
		_existPlayers.put(player.role.playerID,player);
		_offlinePlayers.put(player.role.playerID,player);
	}
	
	/** 从离线组删除角色(包括DB操作)(主线程) */
	private void removeOfflinePlayer(Player player)
	{
		//当前服角色
		if(player.isCurrentGame())
		{
			GameC.db.writeOfflinePlayer(player);
			
			GameC.global.social.removePlayerRoleSocial(player.role.playerID);
		}
		else
		{
			player.throwError("不该存在其他服的离线角色");
		}
		
		player.system.offlineKeepTime=0;
		
		//移除
		_offlinePlayers.remove(player.role.playerID);
		
		removeExistPlayer(player);
	}
	
	/** 将角色从离线组扔到在线组 */
	private void changeOfflinePlayerToOnline(Player player)
	{
		player.system.offlineKeepTime=0;
		_offlinePlayers.remove(player.role.playerID);
		addPlayer(player);
	}
	
	
	
	/** 获取存在的角色(所有状态) */
	public Player getExistPlayerByID(long playerID)
	{
		return _existPlayers.get(playerID);
	}
	
	/** 获取在线角色by playerID(找不到返回null) */
	public Player getPlayerByID(long playerID)
	{
		ThreadControl.checkCurrentIsMainThread();
		
		Player re;
		
		if((re=_players.get(playerID))!=null && re.system.isStateOnline())
			return re;
		
		return null;
	}
	
	/** 获取在线角色by playerID(找不到返回null) */
	public Player getPlayerByIDT(long playerID)
	{
		ThreadControl.checkCurrentIsMainThread();
		
		return _players.get(playerID);
	}
	
	/** 角色是否在线(主线程) */
	public boolean isPlayerOnline(long playerID)
	{
		Player re;
		
		if((re=_players.get(playerID))!=null && re.system.isStateOnline())
			return true;
		
		return false;
	}
	
	/** 通过userID和原区id获取在线角色(一个,因为正常只能存在一个) */
	public Player getOnePlayerByUserIDT(long userID)
	{
		ThreadControl.checkCurrentIsMainThread();
		
		return _playersByUserID.get(userID);
	}
	
	/** 获取在线角色by socketID(找不到返回null) */
	public Player getPlayerBySocketIDT(int socketID)
	{
		ThreadControl.checkCurrentIsMainThread();
		
		return _playersBySocketID.get(socketID);
	}
	
	/** 获取离线角色(逻辑层不开放) */
	public Player getOfflinePlayer(long playerID)
	{
		ThreadControl.checkCurrentIsMainThread();
		
		Player player=_offlinePlayers.get(playerID);
		
		if(player!=null)
		{
			player.system.refreshOfflineKeepTime();
		}
		
		return player;
	}
	
	/** 获取当前在线角色 */
	public LongObjectMap<Player> getPlayers()
	{
		ThreadControl.checkCurrentIsMainThread();
		
		return _players;
	}
	
	/** 此logicID是否是本服 */
	public boolean isCurrentGame(long logicID)
	{
		return _areaDatas.contains(BaseC.logic.getAreaIDByLogicID(logicID));
	}
	
	/** 是否包含该区服 */
	public boolean containsArea(int areaID)
	{
		return _areaDatas.contains(areaID);
	}
	
	/** 获取当前区服ID */
	public int getNowGameID(int areaID)
	{
		return _areaDic.getOrDefault(areaID,areaID);
	}
	
	/** 区服id是否存在 */
	public boolean isAreaAvailable(int areaID)
	{
		return _areaDic.get(areaID)>0;
	}
	
	/** 通过逻辑ID获取当前所在源区服 */
	public int getNowGameIDByLogicID(long logicID)
	{
		return getNowGameID(BaseC.logic.getAreaIDByLogicID(logicID));
	}
	
	/** 获取一个登录角色(在线或离线或DB) */
	private void getLoginPlayer(long playerID,ObjectCall<Player> func)
	{
		Player player=getPlayerByIDT(playerID);
		
		if(player!=null)
		{
			func.apply(player);
		}
		else
		{
			getOfflinePlayerOrDB(playerID,func);
		}
	}
	
	/** 获取一个(离线角色或从DB) */
	private void getOfflinePlayerOrDB(long playerID,ObjectCall<Player> func)
	{
		Player player=getExistPlayerByID(playerID);
		
		if(player!=null)
		{
			if(!player.system.isStateOffline())
			{
				player.throwError("角色的状态不是离线");
			}
			
			func.apply(player);
			
			return;
		}
		
		SList<ObjectCall<Player>> taskList=_getPlayerTaskDic.get(playerID);
		
		if(taskList!=null)
		{
			taskList.add(func);
		}
		else
		{
			taskList=new SList<>(ObjectCall[]::new);
			taskList.add(func);
			_getPlayerTaskDic.put(playerID,taskList);
			
			ObjectCall<PlayerTable> func2=t->
			{
				if(t!=null && (t.data==null || t.data.length==0))
				{
					Ctrl.errorLog("用户表数据为空,序列化时出现未write就写入的情况",playerID);
					t=null;
				}
				
				if(t==null)
				{
					SList<ObjectCall<Player>> taskList2=_getPlayerTaskDic.remove(playerID);
					
					if(taskList2==null)
					{
						Ctrl.errorLog("不应该为空");
						return;
					}
					
					ObjectCall<Player>[] values=taskList2.getValues();
					
					for(int i=0,len=taskList2.size();i<len;i++)
					{
						try
						{
							values[i].apply(null);
						}
						catch(Exception e)
						{
							Ctrl.errorLog(e);
						}
					}
				}
				else
				{
					PlayerTable table=t;
					
					//切IO线程反序列化
					ThreadControl.addIOFunc((int)(playerID & ThreadControl.ioThreadNumMark),()->
					{
						SList<PlayerWorkData> offlineWorkList=CommonSetting.offlineWorkUseTable ? table.offlineWorkDataListMain : null;
						
						if(CommonSetting.offlineWorkUseTable)
						{
							if(offlineWorkList!=null && !offlineWorkList.isEmpty())
							{
								table.offlineWorkDataListMain=new SList<>(PlayerWorkData[]::new);
								table.offlineWorkDirty=true;
							}
						}
						
						PlayerListData listData=BaseC.factory.createPlayerListData();
						BytesReadStream stream=BytesReadStream.create(table.data);
						listData.readBytesFull(stream);
						
						//回主线程
						ThreadControl.addMainFunc(()->
						{
							getOfflinePlayerOrDBNext(table,listData,offlineWorkList);
						});
					});
				}
			};
			
			GameC.db.getPlayerTableAbsByID(playerID,func2);
		}
	}
	
	/** 获取(离线角色或从DB)阶段2(主线程) */
	private void getOfflinePlayerOrDBNext(PlayerTable t,PlayerListData listData,SList<PlayerWorkData> offlineWorkList)
	{
		SList<ObjectCall<Player>> taskList=_getPlayerTaskDic.remove(t.playerID);
		
		if(taskList==null)
		{
			Ctrl.errorLog("不应该为空");
			return;
		}
		
		Player player=createPlayer();
		
		//初始化一下
		player.init();
		
		player.readPlayerTableForOther(t);
		//标记源game
		player.role.sourceGameID=GameC.app.id;
		//读
		player.readListData(listData);
		//检查新增
		player.checkNewAdd();
		//版本检查
		GameC.version.playerVersion.checkVersion(player);
		
		//读后
		player.afterReadData();
		
		if(offlineWorkList!=null && !offlineWorkList.isEmpty())
		{
			for(int i=0,len=offlineWorkList.size();i<len;i++)
			{
				player.system.executeWork(offlineWorkList.get(i),false);
			}
			
			//清空main组
			offlineWorkList.clear();
		}
		
		//添加到离线组
		addOfflinePlayer(player);
		
		//是本服角色
		if(isCurrentGame(player.role.playerID))
		{
			GameC.global.social.addPlayerRoleSocialByPlayer(player);
		}
		
		ObjectCall<Player>[] values=taskList.getValues();
		
		for(int i=0,len=taskList.size();i<len;i++)
		{
			//依旧存在,并且是离线状态
			if(getOfflinePlayer(player.role.playerID)!=null)
			{
				try
				{
					values[i].apply(player);
				}
				catch(Exception e)
				{
					Ctrl.errorLog(e);
				}
			}
			else
			{
				Ctrl.warnLog("getOfflinePlayerOrDBNext时,不在离线组了");
			}
		}
	}
	
	//login
	
	/** 添加用户登录数据 */
	private void addUserLoginData(UserLoginRecordData data)
	{
		_userLoginDic.put(data.exData.userID,data);
		_userLoginDicByToken.put(data.token,data);
		
		if(data.socket!=null)
		{
			_userLoginDicBySocketID.put(data.socket.id,data);
		}
	}
	
	/** 用户预备登录areaID:原区ID(主线程) */
	public void preUserLogin(ClientLoginExData eData,int loginID)
	{
		if(isAssist())
		{
			Ctrl.warnLog("辅助服不可登录");
			return;
		}
		
		addFlowLog(eData.data.uid,FlowStepType.PreUserLogin);
		
		UserLoginRecordData data=_userLoginDic.get(eData.userID);
		
		if(data==null)
		{
			data=_userLoginRecordDataPool.getOne();
			data.version++;
			
			data.exData=eData;
			data.data=eData.data;
			
			data.token=MathUtils.getToken();
			
			while(_userLoginDicByToken.contains(data.token))
			{
				data.token=MathUtils.getToken();
			}
			
			addUserLoginData(data);
		}
		else
		{
			data.exData=eData;
			data.data=eData.data;
		}
		
		data.refreshLoginTime();
		
		ClientLoginData lData=data.data;
		
		Ctrl.log("用户预备登录:",lData.uid,"原区:",eData.areaID,"平台类型:",lData.clientPlatformType,"设备类型：",lData.deviceType,"设备唯一标识:",lData.deviceUniqueIdentifier);
		
		//进入日志
		GameC.log.gameEnter(data.data.uid,eData.data.platform,eData.ip);
		
		//回登录
		ReUserLoginToLoginServerRequest.create(eData.userID,data.token).send(loginID);
	}
	
	/** 移除用户登录数据 */
	private void removeUserLoginData(UserLoginRecordData data)
	{
		_userLoginDic.remove(data.exData.userID);
		_userLoginDicByToken.remove(data.token);
		
		if(data.socket!=null)
		{
			_userLoginDicBySocketID.remove(data.socket.id);
		}
		
		data.version++;
		_userLoginRecordDataPool.back(data);
	}
	
	/** 用户登录(主线程) */
	public void userLogin(GameReceiveSocket socket,int token,int cMsgVersion,int gMsgVersion,int resourceVersion)
	{
		if(isAssist())
		{
			Ctrl.errorLog("辅助服不可登录");
			return;
		}
		
		Ctrl.debugLog("game 客户端登陆",token);

		if(CommonSetting.needClientMessageVersionCheck)
		{
			//校验c数据版本
			if(cMsgVersion!=CodeCheckRecord.msgDataVersion)
			{
				Ctrl.warnLog("客户端通信结构c校验不匹配");
				socket.sendInfoCode(InfoCodeType.LoginGameFailed_dataVersionError);
				return;
			}
			
			//校验g数据版本
			if(gMsgVersion!=BaseC.config.getMsgDataVersion())
			{
				Ctrl.warnLog("客户端通信结构g校验不匹配");
				socket.sendInfoCode(InfoCodeType.LoginGameFailed_dataVersionError);
				return;
			}
		}
		
		UserLoginRecordData data=_userLoginDicByToken.get(token);
		
		if(data==null)
		{
			Ctrl.warnLog("未找到登录数据,可能被挤掉或超时");
			socket.sendInfoCode(InfoCodeType.LoginGameFailed_noLoginData);
			return;
		}
		
		addFlowLog(data.data.uid,FlowStepType.UserLogin);
		
		if(data.userLoginLock.isLocking())
		{
			Ctrl.warnLog("该用户正在登录中");
			socket.sendInfoCode(InfoCodeType.LoginGameFailed_isLogining);
			return;
		}
		
		ClientVersionData clientVersion=getClientVersion(data.data.clientPlatformType);
		
		//客户端资源版本小于最小允许版本
		if(clientVersion!=null && resourceVersion<clientVersion.leastResourceVersion)
		{
			Ctrl.warnLog("客户端资源版本不足");
			socket.sendInfoCode(InfoCodeType.LoginGameFailed_resourceVersionLow);
			return;
		}
		
		//这时候再踢掉
		if(data.socket!=null)
		{
			Ctrl.log("重复登录",data.data.uid);
			//重复登录
			data.socket.send(SendInfoCodeRequest.create(InfoCodeType.LoginGameFailed_repeatLogin));
			data.socket.close();
			
			//移除
			_userLoginDicBySocketID.remove(data.socket.id);
			data.socket=null;
		}
		
		//上锁
		data.userLoginLock.lockOn();
		//赋值socket
		data.socket=socket;
		//添加
		_userLoginDicBySocketID.put(socket.id,data);
		
		data.refreshLoginTime();
		
		Ctrl.log("用户登录:",data.data.uid,"原区:",data.exData.areaID);
		
		Runnable func=()->
		{
			UserLoginRecordData data2=_userLoginDic.get(data.exData.userID);
			
			if(data2==null)
			{
				Ctrl.warnLog("未找到登录数据");
				socket.sendInfoCode(InfoCodeType.LoginGameFailed_noLoginData);
				return;
			}
			
			if(data2.socket==null)
			{
				Ctrl.warnLog("登陆过程中，客户端连接已断开");
				return;
			}
			
			userLoginNext(data2);
		};
		
		//启用
		if(CommonSetting.useReconnectLogin)
		{
			crowedDownLastPlayerForUserLogin(data.exData.userID,func);
		}
		else
		{
			crowedDownLastPlayer(data.exData.userID,-1L,func);
		}
	}
	
	/** 挤掉上个角色(exceptPlayerID:排除角色ID)(主线程) */
	private void crowedDownLastPlayer(long userID,long exceptPlayerID,Runnable func)
	{
		if(!CommonSetting.useReconnectLogin)
		{
			exceptPlayerID=-1L;
		}
		
		//存在接收角色
		if(GameC.gameSwitch.getSwitchReceivePlayerByID(exceptPlayerID)!=null)
		{
			Ctrl.throwError("此时不该有接收角色");
		}
		
		PlayerSwitchGameToRecordData switchRecordData;
		
		if((switchRecordData=GameC.gameSwitch.getOneSwitchToPlayerByUserID(userID))!=null)
		{
			////是排除角色
			//if(switchRecordData.keyData.playerID==exceptPlayerID)
			//{
			//	if(func!=null)
			//		func.run();
			//	return;
			//}
			
			playerExitForSwitchTo(switchRecordData,InfoCodeType.PlayerExit_crowedDown,func);
			
			return;
		}
		
		Player lastPlayer;
		
		//在线的
		if((lastPlayer=getOnePlayerByUserIDT(userID))!=null)
		{
			if(lastPlayer.system.isStateOnline() && lastPlayer.role.playerID==exceptPlayerID)
			{
				if(func!=null)
					func.run();
				return;
			}
			
			crowedDownPlayer(lastPlayer,func);
		}
		//过程中的
		else
		{
			if(func!=null)
				func.run();
		}
	}
	
	/** 挤掉上个角色(登录用)(主线程) */
	private void crowedDownLastPlayerForUserLogin(long userID,Runnable func)
	{
		PlayerSwitchGameToRecordData switchRecordData;
		
		if((switchRecordData=GameC.gameSwitch.getOneSwitchToPlayerByUserID(userID))!=null)
		{
			playerExitForSwitchTo(switchRecordData,InfoCodeType.PlayerExit_crowedDown,func);
			
			return;
		}
		
		Player lastPlayer=getOnePlayerByUserIDT(userID);
		
		//不是在线
		if(lastPlayer!=null && !lastPlayer.system.isStateOnline())
		{
			crowedDownPlayer(lastPlayer,func);
		}
		else
		{
			func.run();
		}
	}
	
	private void crowedDownPlayer(Player player,Runnable func)
	{
		if(player.system.isStateExiting())
		{
			player.system.addExitOverCall(func);
		}
		else
		{
			//还连接着
			if(player.system.socket.isConnect())
			{
				Ctrl.log("被挤下线",player.role.playerID);
			}
			
			//被挤下线
			playerExit(player,InfoCodeType.PlayerExit_crowedDown,func);
		}
	}
	
	/** user登录下一阶段(主线程) */
	private void userLoginNext(UserLoginRecordData data)
	{
		//登录日志
		GameC.log.userLogin(data.data.uid,data.data.platform,data.socket.remoteIP());
		
		int version=data.version;
		long userID=data.exData.userID;
		
		GameC.db.loadPlayerTableByUserIDAndCreateAreaID(userID,data.exData.areaID,list->
		{
			UserLoginRecordData data2=_userLoginDic.get(userID);
			
			if(data2==null)
			{
				Ctrl.log("未找到登录数据2(loadPlayerTable时客户端连接断开导致)");
				return;
			}
			
			if(version!=data2.version)
			{
				Ctrl.warnLog("userLoginNext时,数据版本过期");
				return;
			}
			
			if(data2.socket==null)
			{
				Ctrl.warnLog("userLoginNext时,socket已为空");
				return;
			}
			
			//清空
			data2.playerIDs.clear();
			
			SList<PlayerLoginData> roles=new SList<>();
			
			PlayerTable pt;
			Player player;
			PlayerLoginData rData;
			
			for(int i=0,len=list.size();i<len;++i)
			{
				pt=list.get(i);
				
				//在线
				if((player=getPlayerByIDT(pt.playerID))!=null)
				{
					rData=player.role.createLoginData();
					rData.isOnline=true;
				}
				//离线
				else if((player=getOfflinePlayer(pt.playerID))!=null)
				{
					rData=player.role.createLoginData();
					rData.isOnline=false;
				}
				//在db中
				else
				{
					rData=pt.loginDataT;
					rData.isOnline=false;
				}
				
				roles.add(rData);
				data2.playerIDs.add(rData.playerID);
			}
			
			if(ShineSetting.openCheck)
			{
				//int lastRecord=GameC.global.system.getTempRecord(userID,data.exData.areaID);
				//
				////出现了丢角色
				//if(roles.length()<lastRecord)
				//{
				//	Ctrl.throwError("出现了丢角色(或许是创建角色流程中的报错中断导致):",data.data.uid);
				//}
			}
			
			addFlowLog(data2.data.uid,FlowStepType.RePlayerList);
			
			//回复角色列表
			data2.socket.send(RePlayerListRequest.create(roles,GameC.global.system.getServerBornCode()));
			
		});
	}
	
	/** 创建新角色(主线程) */
	public void createNewPlayer(GameReceiveSocket socket,CreatePlayerData createData)
	{
		if(isAssist())
		{
			Ctrl.errorLog("辅助服不可创建角");
			return;
		}
		
		ThreadControl.checkCurrentIsMainThread();
		
		UserLoginRecordData data=_userLoginDicBySocketID.get(socket.id);
		
		if(data==null)
		{
			Ctrl.warnLog("未找到登录数据");
			socket.sendInfoCode(InfoCodeType.LoginGameFailed_noLoginData);
			return;
		}
		
		addFlowLog(data.data.uid,FlowStepType.CreatePlayer);
		
		//正在创建中
		if(data.creatingLock.isLocking())
		{
			Ctrl.warnLog("正在创建中");
			socket.sendInfoCode(InfoCodeType.CreatePlayerFailed_isCreating);
			return;
		}
		
		//达到上限
		if(data.playerIDs.size() >= Global.ownPlayerNum)
		{
			Ctrl.warnLog("达到角色创建数目上限");
			socket.sendInfoCode(InfoCodeType.CreatePlayerFailed_ownPlayerNumMax);
			return;
		}
		
		checkAndGiveName(createData.name,data.exData.areaID,socket,nn->
		{
			if(nn==null)
				return;
			
			createData.name=nn;
			
			toCreateNewPlayer(data,createData);
		});
	}
	
	/** 检查并给与名字(返回null则为失败) */
	private void checkAndGiveName(String name,int createAreaID,GameReceiveSocket socket,ObjectCall<String> func)
	{
		//名字超限
		if(StringUtils.getCharMachineNum(name)>Global.playerNameLength)
		{
			Ctrl.warnLog("名字过长",name);
			socket.sendInfoCode(InfoCodeType.CreatePlayerFailed_nameTooLong);
			func.apply(null);
			return;
		}
		
		//名字前缀
		if(Global.isNameUseAreaIDAsFront)
		{
			if(name.indexOf('.')!=-1)
			{
				Ctrl.warnLog("非法字符",name);
				socket.sendInfoCode(InfoCodeType.CreatePlayerFailed_nameNotAllowed);
				func.apply(null);
				return;
			}
		}
		
		//敏感字
		if(BaseGameUtils.hasSensitiveWord(name))
		{
			Ctrl.warnLog("名字敏感",name);
			socket.sendInfoCode(InfoCodeType.CreatePlayerFailed_nameIsSensitive);
			func.apply(null);
			return;
		}
		
		//使用名
		String nn=(Global.isNameUseAreaIDAsFront && CommonSetting.isAreaSplit()) ? "s" + createAreaID + "." + name : name;
		
		//不可重名
		if(!Global.canPlayerNameRepeat)
		{
			PlayerNameTable pt=new PlayerNameTable();
			pt.name=name;
			pt.load(GameC.db.getPlayerNameConnect(),b->
			{
				if(b)
				{
					Ctrl.warnLog("名字重名",name);
					socket.sendInfoCode(InfoCodeType.CreatePlayerFailed_nameIsRepeat);
					func.apply(null);
				}
				else
				{
					func.apply(nn);
				}
			});
			
			return;
		}
		
		func.apply(nn);
	}
	
	/** 删除玩家名字 */
	private void deletePlayerName(String name)
	{
		//不可重名 并且 不是分服
		if(!Global.canPlayerNameRepeat)
		{
			PlayerNameTable pt=new PlayerNameTable();
			pt.name=name;
			pt.delete(GameC.db.getPlayerNameConnect(),null);
		}
	}
	
	/** 检查并给与玩家名字(返回null则为失败) */
	private void useAndInsertPlayerName(String name,long playerID,GameReceiveSocket socket,ObjectCall<Boolean> func)
	{
		if(!Global.canPlayerNameRepeat)
		{
			PlayerNameTable pt=new PlayerNameTable();
			pt.name=name;
			pt.playerID=playerID;
			pt.insert(GameC.db.getPlayerNameConnect(),b->
			{
				if(b)
				{
					func.apply(true);
				}
				else
				{
					Ctrl.warnLog("插入时名字重名",name);
					socket.sendInfoCode(InfoCodeType.CreatePlayerFailed_nameIsRepeat);
					func.apply(false);
				}
			});
		}
		else
		{
			func.apply(true);
		}
	}
	
	/** 执行创建角色(判断过各种前置条件的)(主线程) */
	private void toCreateNewPlayer(UserLoginRecordData data,CreatePlayerData createData)
	{
		//角色ID
		long newPlayerID=GameC.global.system.getNewPlayerID(data.exData.areaID);
		
		//没有可用的角色ID了
		if(newPlayerID==-1L)
		{
			Ctrl.throwError("没有可用的角色ID了");
			data.socket.sendInfoCode(InfoCodeType.CreatePlayerFailed_areaIsFull);
			return;
		}
		
		useAndInsertPlayerName(createData.name,newPlayerID,data.socket,b->
		{
			if(b)
			{
				toCreateNewPlayer2(newPlayerID,data,createData);
			}
		});
	}
	
	/** 执行创建角色(判断过各种前置条件的)(主线程) */
	private void toCreateNewPlayer2(long newPlayerID,UserLoginRecordData data,CreatePlayerData createData)
	{
		int createAreaID=data.exData.areaID;
		
		//创建角色
		Player player=createPlayer();
		//初始化一下
		player.init();
		//新一份数据
		player.newInitData();
		
		//设置基础键组
		player.role.playerID=newPlayerID;
		player.role.name=createData.name;
		player.role.userID=data.exData.userID;
		player.role.uid=data.data.uid;
		player.role.createAreaID=createAreaID;
		player.role.platform=data.data.platform;
		player.role.sourceGameID=GameC.app.id;//标记源game
		player.role.getPartData().isAdult=data.exData.isAdult;
		
		onNewCreateSetLoginData(player,data.exData);
		
		//逻辑相关
		player.role.doCreatePlayer(createData);
		
		//创建调用
		player.onNewCreate();
		
		//读完数据一次
		player.afterReadData();
		
		//创建角色日志
		GameC.log.createPlayer(player);
		Ctrl.log("创建角色:",player.getInfo());
		
		//检查一次功能开启
		player.func.checkAllFunctions();
		
		//global上创建
		GameC.global.onPlayerCreate(player);
		
		//添加到离线组
		addOfflinePlayer(player);
		
		if(ShineSetting.openCheck)
		{
			GameC.global.system.addTempRecord(data.exData.userID,createAreaID);
		}
		
		PlayerTable table=BaseC.factory.createPlayerTable();
		
		player.writePlayerTableForOther(table);
		//loginData
		table.loginDataT=player.role.createLoginData();
		table.saveDate=DateData.getNow();

		if(CommonSetting.offlineWorkUseTable)
		{
			table.offlineWorkDataListT=new SList<>(PlayerWorkData[]::new);
			table.offlineWorkDataListMain=new SList<>(PlayerWorkData[]::new);
		}
		
		//插入数据库
		GameC.db.insertNewPlayer(table);
		
		if(CommonSetting.needSaveDBAfterCreateNewPlayer)
		{
			PlayerListData listData=player.createListData();
			player.writeListData(listData);
			
			_tempWriteStream.clear();
			listData.writeBytesFull(_tempWriteStream);
			table.data=_tempWriteStream.getByteArray();
		}
		
		//补到data里
		data.playerIDs.add(player.role.playerID);
		
		++_todayCreatePlayerNum;
		
		PlayerCreatedWData wData=new PlayerCreatedWData();
		wData.uid=player.role.uid;
		wData.userID=player.role.userID;
		wData.playerID=player.role.playerID;
		
		addUserWork(wData);
		
		//超过承载数
		if(Global.areaDesignRegistNum>0 && GameC.global.system.getAreaRegistNum(createAreaID)>=Global.areaDesignRegistNum)
		{
			AreaServerData areaServerData;
			if(!(areaServerData=_areaDatas.get(createAreaID)).isLimitRegist)
			{
				areaServerData.isLimitRegist=true;
				
				_canRegisterAreaSet.remove(createAreaID);
				
				//通知所有登陆服
				GameC.server.radioLogins(LimitAreaToLoginServerRequest.create(createAreaID,true));
			}
		}
		
		//日志
		
		//解锁
		data.creatingLock.unlock();
		
		PlayerLoginData loginData=player.role.createLoginData();
		
		addFlowLog(data.data.uid,FlowStepType.CreatePlayerSuccess);
		
		data.socket.send(CreatePlayerSuccessRequest.create(loginData));
		
		onCreateNewPlayer(player);
	}
	
	/** 新角色创建设置登录数据 */
	protected void onNewCreateSetLoginData(Player player,ClientLoginExData exData)
	{
		SystemPartData systemPartData=player.system.getPartData();
		systemPartData.createDate=DateData.getNow();//记录创建时间
		systemPartData.lastLoginClientPlatformType=exData.data.clientPlatformType;
		systemPartData.lastLoginDeviceType=exData.data.deviceType;
		systemPartData.lastLoginDeviceUniqueIdentifier=exData.data.deviceUniqueIdentifier;
	}
	
	/** 创建了新角色 */
	protected void onCreateNewPlayer(Player player)
	{
	
	}
	
	/** 角色登录(主线程) */
	public void playerLogin(GameReceiveSocket socket,long playerID,SList<ClientOfflineWorkData> clientRecords)
	{
		if(isAssist())
		{
			Ctrl.errorLog("辅助服不可登录");
			return;
		}
		
		Ctrl.log("playerLogin:",playerID);
		
		UserLoginRecordData data=_userLoginDicBySocketID.get(socket.id);
		
		if(data==null)
		{
			Ctrl.warnLog("找不到登录数据");
			socket.sendInfoCode(InfoCodeType.LoginGameFailed_noLoginData);
			return;
		}
		
		addFlowLog(data.data.uid,playerID,FlowStepType.PlayerLogin);
		
		if(!data.playerIDs.contains(playerID))
		{
			Ctrl.warnLog("playerID不匹配");
			socket.sendInfoCode(InfoCodeType.LoginGameFailed_playerIDNotMatch);
			return;
		}
		
		//正在登录中
		if(data.playerLoginLock.isLocking())
		{
			Ctrl.warnLog("正在登录中");
			socket.sendInfoCode(InfoCodeType.LoginGameFailed_isLogining);
			return;
		}
		
		if(ShineSetup.isExiting())
		{
			Ctrl.warnLog("服务器关闭中");
			socket.sendInfoCode(InfoCodeType.LoginGameFailed_serverClose);
			return;
		}
		
		if(!CommonSetting.useReconnectLogin)
		{
			if(getPlayerByIDT(playerID)!=null)
			{
				Ctrl.warnLog("角色已登录");
				socket.sendInfoCode(InfoCodeType.LoginGameFailed_playerAlreadyLogined);
				return;
			}
		}
		
		//删除中
		if(isDeleting(playerID))
		{
			socket.sendInfoCode(InfoCodeType.LoginGameFailed_playerNotExist);
			return;
		}
		
		//挤掉上个角色
		crowedDownLastPlayer(data.exData.userID,playerID,()->
		{
			//TODO:兼容跨服中的
			
			getLoginPlayer(playerID,player->
			{
				data.playerLoginLock.lockOn();
				
				//移除
				if(data.socket!=null)
				{
					_userLoginDicBySocketID.remove(data.socket.id);
					data.socket=null;
				}
				
				player.log("playerLogin getLoginPlayer后");
				
				doPlayerLoginNext(player,socket,data.token,data.data,clientRecords);
			});
		});
	}
	
	private void doPlayerLoginNext(Player player,GameReceiveSocket socket,int token,ClientLoginData loginData,SList<ClientOfflineWorkData> clientRecords)
	{
		long playerID=player.role.playerID;
		
		//再判断一次删除中
		if(isDeleting(playerID))
		{
			socket.sendInfoCode(InfoCodeType.LoginGameFailed_playerNotExist);
			return;
		}
		
		Player lastPlayer;
		//居然此时还有别的角色(瞬时重复登录)
		if((lastPlayer=getOnePlayerByUserIDT(player.role.userID))!=null && lastPlayer.role.playerID!=playerID)
		{
			Ctrl.warnLog("玩家重复登录,罕见的");
			socket.sendInfoCode(InfoCodeType.LoginGameFailed_playerNotExist);
			return;
		}
		
		SystemPart sPart=player.system;
		
		//封号
		if(sPart.getPartData().isBlock)
		{
			Ctrl.warnLog("被封号");
			socket.sendInfoCode(InfoCodeType.LoginGameFailed_isBlock);
			return;
		}
		
		int loginState=sPart.getLoginState();
		
		if(loginState==PlayerLoginStateType.Exiting || loginState==PlayerLoginStateType.Logining)
		{
			Ctrl.warnLog("正在登录/登出中");
			socket.sendInfoCode(InfoCodeType.LoginGameFailed_isLogining);
			return;
		}
		
		if(loginState==PlayerLoginStateType.Switching)
		{
			Ctrl.warnLog("正在跨服中");
			socket.sendInfoCode(InfoCodeType.LoginGameFailed_isLogining);
			return;
		}
		
		if(loginData!=null)
		{
			sPart.recordLoginInfo(loginData);
		}
		
		if(clientRecords!=null)
			sPart.setTempClientOfflineWorks(clientRecords);
		
		//在线角色
		if(loginState==PlayerLoginStateType.Online)
		{
			if(!CommonSetting.useReconnectLogin)
			{
				Ctrl.throwError("不启用快速登录,不该走到这里");
			}
			
			sPart.setLoginToken(token);
			
			doPlayerReconnectLogin(player,socket);
		}
		else
		{
			//清下回调
			sPart.clearFuncs();
			
			//硬件信息记录
			
			//登录中
			sPart.setLoginState(PlayerLoginStateType.Logining);
			sPart.setLoginToken(token);
			
			//暂时先关
			sPart.setSocketReady(false);
			sPart.socket=socket;
			
			//标记源game
			player.role.sourceGameID=GameC.app.id;
			
			//切到过程组
			changeOfflinePlayerToOnline(player);
			
			playerLoginEachGame(player);
		}
	}
	
	/** 角色登陆中，从各个逻辑服拿数据阶段 */
	protected void playerLoginEachGame(Player player)
	{
		player.system.loginPhase=PlayerLoginPhaseType.LoginEachGame;
		
		PlayerLoginToEachGameData data=null;
		
		try
		{
			data=player.makeLoginEachGameData();
		}
		catch(Exception e)
		{
			player.errorLog(e);
		}
		
		PlayerLoginEachGameTempData tData=new PlayerLoginEachGameTempData();
		tData.waitTime=ShineSetting.affairDefaultExecuteTime;
		tData.result=BaseC.factory.createRePlayerLoginFromEachGameData();
		tData.result.initDefault();
		
		splitLoginEachGame(tData.dic,data);
		
		long playerID=player.role.playerID;
		
		player.system.loginEachTempData=tData;
		
		//有需要
		if(!tData.dic.isEmpty())
		{
			tData.dic.forEachS((k,v)->
			{
				//本服
				if(k==GameC.app.id)
				{
					makePlayerLoginEachGame(v,re->
					{
						//延迟一帧执行
						ThreadControl.getMainTimeDriver().callLater(()->
						{
							onReceiveOtherLoginEachGameResult(k,playerID,re);
						});
					});
				}
				else
				{
					BaseSocket gameSocket=GameC.server.getGameSocket(k);
					
					if(gameSocket!=null)
					{
						PlayerLoginToEachGameServerRequest.create(playerID,v).send(k);
					}
					else
					{
						tData.dic.remove(k);
					}
				}
			});
		}
		
		if(tData.dic.isEmpty())
		{
			//结束
			onRePlayerLoginEachGame(player,tData.result);
		}
	}
	
	/** 构造角色登陆逻辑服数据(当前服) */
	protected void makePlayerLoginEachGame(PlayerLoginToEachGameData data,ObjectCall<RePlayerLoginFromEachGameData> result)
	{
		RePlayerLoginFromEachGameData re=BaseC.factory.createRePlayerLoginFromEachGameData();
		re.initDefault();
		
		VInt vInt=new VInt(2);
		
		Runnable func=()->
		{
			if((--vInt.value)==0)
			{
				result.apply(re);
			}
		};
		
		GameC.global.social.makePlayerLoginEachGame(data,re,func);
		
		GameC.global.func.makePlayerLoginEachGame(data,re,func);
	}
	
	/** 拆分数据 */
	protected void splitLoginEachGame(IntObjectMap<PlayerLoginToEachGameData> dic,PlayerLoginToEachGameData data)
	{
		data.needRoleSocials.forEachA(k->
		{
			int gameID;
			PlayerLoginToEachGameData tData;
			gameID=getNowGameID(BaseC.logic.getAreaIDByLogicID(k));
			
			if((tData=dic.get(gameID))==null)
			{
				dic.put(gameID,tData=BaseC.factory.createPlayerLoginToEachGameData());
				tData.initDefault();
				tData.selfData=data.selfData;
			}
			
			tData.needRoleSocials.add(k);
		});
		
		data.roleGroups.forEach((k1,v1)->
		{
			v1.forEachValue(v->
			{
				int gameID2=getNowGameID(BaseC.logic.getAreaIDByLogicID(v.groupID));
				
				PlayerLoginToEachGameData tData2;
				if((tData2=dic.get(gameID2))==null)
				{
					dic.put(gameID2,tData2=BaseC.factory.createPlayerLoginToEachGameData());
					tData2.initDefault();
					tData2.selfData=data.selfData;
				}
				
				tData2.roleGroups.computeIfAbsent(k1,k2->new LongObjectMap<>(PlayerRoleGroupSaveData[]::new)).put(v.groupID,v);
			});
		});
	}
	
	/** 合并数据(data合到source上) */
	protected void joinLoginEachGame(RePlayerLoginFromEachGameData source,RePlayerLoginFromEachGameData data)
	{
		if(data!=null)
		{
			data.roleSocialDatas.forEach((k,v)->
			{
				source.roleSocialDatas.put(k,v);
			});
			
			data.roleGroups.forEach((k,v)->
			{
				source.roleGroups.computeIfAbsent(k,k2->new LongObjectMap<>(PlayerRoleGroupData[]::new)).putAll(v);
			});
		}
		else
		{
			//返回数据为空的情况
		}
	}
	
	/** 收到其他角色登陆消息 */
	public void onReceiveOtherLoginEachGame(int gameID,long playerID,PlayerLoginToEachGameData data)
	{
		makePlayerLoginEachGame(data,v->
		{
			RePlayerLoginFromEachGameServerRequest.create(playerID,v).send(gameID);
		});
	}
	
	/** 收到目标服务器回复的登陆结果 */
	public void onReceiveOtherLoginEachGameResult(int fromGameID,long playerID,RePlayerLoginFromEachGameData data)
	{
		Player player=getPlayerByIDT(playerID);
		
		if(player==null)
		{
			Ctrl.warnLog("onReceiveOtherLoginEachGameResult时,角色丢失",playerID);
			return;
		}
		
		if(!player.system.isStateLogining())
		{
			player.warnLog("onReceiveOtherLoginEachGameResult时,状态不是登陆中");
			return;
		}
		
		PlayerLoginEachGameTempData tData=player.system.loginEachTempData;
		
		if(tData==null)
		{
			player.warnLog("onReceiveOtherLoginEachGameResult时,tempData已为空");
			return;
		}
		
		try
		{
			joinLoginEachGame(tData.result,data);
		}
		catch(Exception e)
		{
			player.errorLog(e);
		}
		
		tData.dic.remove(fromGameID);
		
		if(tData.dic.isEmpty())
		{
			//清空
			player.system.loginEachTempData=null;
			//结束
			onRePlayerLoginEachGame(player,tData.result);
		}
	}
	
	/** 等待回复角色登录各个逻辑服时,超时 */
	private void onRePlayerLoginEachGameTimeOut(Player player)
	{
		if(ShineSetting.needDebugLog)
			player.debugLog("等待回复角色登录来自各个逻辑服时,超时");
		
		//直接下一步
		PlayerLoginEachGameTempData tData=player.system.loginEachTempData;
		player.system.loginEachTempData=null;
		tData.result.timeOutGames=tData.dic.getKeySet();
		onRePlayerLoginEachGame(player,tData.result);
	}
	
	/** 角色登陆各个逻辑服返回 */
	public void onRePlayerLoginEachGame(Player player,RePlayerLoginFromEachGameData data)
	{
		if(!player.system.isStateLogining())
		{
			Ctrl.errorLog("onRePlayerLoginEachGame时,不是登陆状态");
			return;
		}
		
		//数据clone一下，与主线程的断开联系
		data=(RePlayerLoginFromEachGameData)data.clone();
		
		player.system.loginEachTempData=null;
		
		//登录数据
		UserLoginRecordData loginData=_userLoginDic.get(player.role.userID);
		
		if(loginData!=null)
		{
			//登录完毕
			loginData.loginOver();
		}
		else
		{
			//Ctrl.warnLog("收到中心服返回角色登录时,找不到登录数据");
		}
		
		//需要退出了
		if(checkNeedExitThird(player))
			return;
		
		SystemPart sPart=player.system;
		
		//清下回调
		sPart.clearFuncs();
		
		//此时开重连
		if(CommonSetting.clientOpenReconnect)
		{
			sPart.socket.setOpenReconnect(true);
		}
		
		//执行离线数据
		sPart.flushProcessOfflineWork();
		
		try
		{
			//客户端离线事务
			player.system.flushClientOfflineWorks();
			
			//记录上线时间
			player.system.recordLoginDate();
			
			player.beforeLoginForEachGame(data);
			
			//主线程登录时刻
			player.beforeLoginOnMain();
		}
		catch(Exception e)
		{
			player.errorLog(e);
		}
		
		player.system.loginPhase=PlayerLoginPhaseType.LogicThread;
		
		int targetGameID=GameC.app.id;
		
		if(CommonSetting.needPlayerFullTransToOtherGame && isCurrentGameFull())
		{
			targetGameID=_nowAssistGame!=-1 ? _nowAssistGame : GameC.app.id;
		}
		
		//本服的
		if(targetGameID==GameC.app.id)
		{
			try
			{
				//global进入
				GameC.global.onPlayerEnter(player);
				player.beforeEnterOnMain();
			}
			catch(Exception e)
			{
				player.errorLog(e);
			}
			
			//socket绑定
			sPart.socket.setPlayer(player);
			
			//该去的线程号
			int index=player.scene.getExecutorIndexByData();
			
			LogicExecutor executor=getExecutor(index);
			
			if(executor==null)
			{
				Ctrl.throwError("不该找不到执行器");
			}
			else
			{
				//切到对应逻辑线程登录
				executor.addFunc(()->
				{
					try
					{
						executor.playerLogin(player);
					}
					catch(Exception e)
					{
						player.errorLog(e);
					}
					
					//切主线程
					player.addMainFunc(()->
					{
						playerLoginOver(player);
					});
				});
			}
		}
		else
		{
			//socket绑定
			sPart.socket.setPlayer(player);
			player.system.setSwitchLogin(true);
			
			//切换到目标游戏服
			GameC.gameSwitch.playerSwitchToGame(player,targetGameID);
		}
	}
	
	/** 角色重连登录(主线程) */
	public void playerReconnectLogin(GameReceiveSocket socket,long playerID,int token,int cMsgVersion,int gMsgVersion,int resourceVersion,SList<ClientOfflineWorkData> clientRecords)
	{
		Ctrl.log("收到playerReconnect消息socketID:",socket.id,"playerID:",playerID);
		
		Player player=getPlayerByID(playerID);
		
		if(player==null)
		{
			Ctrl.log("收到playerReconnect时,角色不在线",playerID);
			return;
		}
		
		if(CommonSetting.needClientMessageVersionCheck)
		{
			//校验c数据版本
			if(cMsgVersion!=CodeCheckRecord.msgDataVersion)
			{
				Ctrl.warnLog("客户端通信结构c校验不匹配");
				socket.sendInfoCode(InfoCodeType.LoginGameFailed_dataVersionError);
				return;
			}
			
			//校验g数据版本
			if(gMsgVersion!=BaseC.config.getMsgDataVersion())
			{
				Ctrl.warnLog("客户端通信结构g校验不匹配");
				socket.sendInfoCode(InfoCodeType.LoginGameFailed_dataVersionError);
				return;
			}
		}
		
		ClientVersionData clientVersion=getClientVersion(player.system.getPartData().lastLoginClientPlatformType);
		
		//客户端资源版本小于最小允许版本
		if(clientVersion!=null && resourceVersion<clientVersion.leastResourceVersion)
		{
			Ctrl.warnLog("客户端资源版本不足");
			socket.sendInfoCode(InfoCodeType.LoginGameFailed_resourceVersionLow);
			return;
		}
		
		if(player.system.getLoginToken()!=token)
		{
			socket.sendInfoCode(InfoCodeType.PlayerReconnectLoginFailed_tokenNotMatch);
			return;
		}
		
		doPlayerLoginNext(player,socket,token,null,clientRecords);
	}
	
	/** 执行角色重连登录(第二形态)(主线程) */
	private void doPlayerReconnectLogin(Player player,GameReceiveSocket socket)
	{
		//登录数据
		UserLoginRecordData loginData=_userLoginDic.get(player.role.userID);
		
		if(loginData!=null)
		{
			//登录完毕
			loginData.loginOver();
		}
		else
		{
			Ctrl.warnLog("收到中心服返回角色登录时,找不到登录数据");
		}
		
		//先移除socket部分
		toRemovePlayerBySocket(player);
		
		GameReceiveSocket oldSocket=player.system.socket;
		oldSocket.sendInfoCode(InfoCodeType.PlayerExit_crowedDown);
		oldSocket.close();
		
		//标记关
		player.system.setSocketReady(false);
		//直接赋值
		player.system.socket=socket;
		socket.setPlayer(player);
		
		//再添加socket部分
		toAddPlayerBySocket(player);
		
		//此时开重连
		if(CommonSetting.clientOpenReconnect)
		{
			socket.setOpenReconnect(true);
		}
		
		if(checkNeedExit(player))
			return;
		
		player.addFunc(()->
		{
			//重连上线
			player.getExecutor().playerReconnectLogin(player);
		});
	}
	
	/** 角色登录完毕(主线程) */
	public boolean playerLoginOver(Player player)
	{
		if(checkNeedExit(player))
			return false;
		
		//登陆过程直接跨服
		if(player.system.isStateSwitching())
			return false;

		//不是登录中
		if(!player.system.isStateLogining())
		{
			Ctrl.warnLog("标记在线时，不是登录状态",player.system.getLoginState());
			return false;
		}

		//在线
		player.system.setLoginState(PlayerLoginStateType.Online);
		return true;
	}
	
	/** 检查是否需要退出了 */
	public boolean checkNeedExitThird(Player player)
	{
		//需要退出了
		if(player.system.isNeedExitNext())
		{
			if(ShineSetting.needDebugLog)
				player.debugLog("角色需要退出了,形态二");
			
			//修改标记为退出中
			player.system.setLoginState(PlayerLoginStateType.Exiting);
			
			player.system.setNowExitCode(player.system.getNextExitCode());
			player.system.setNeedExitNext(false,-1);
			
			//发送退出码
			player.sendInfoCode(player.system.getNowExitCode());
			
			//解绑socket
			player.system.socket.unbindPlayer();
			player.system.setSocketReady(false);
			
			playerExitThird(player);
			return true;
		}
		
		return false;
	}
	
	/** 检查是否需要退出了 */
	public boolean checkNeedExit(Player player)
	{
		//需要退出了
		if(player.system.isNeedExitNext())
		{
			if(ShineSetting.needDebugLog)
				player.debugLog("角色需要退出了,形态一");
			
			//先归到在线状态,然后直接退出
			player.system.setLoginState(PlayerLoginStateType.Online);
			playerExit(player,player.system.getNextExitCode());
			return true;
		}

		return false;
	}
	
	/** 角色中断切换到退出(主线程) */
	private void cutPlayerToExit(Player player)
	{
		if(ShineSetting.needDebugLog)
			player.debugLog("角色中断切换到退出");
		
		player.system.setLoginState(PlayerLoginStateType.Online);
		playerExit(player,player.system.getNextExitCode());
	}
	
	/** 切换到的角色退出 */
	public void playerExitForSwitchTo(PlayerSwitchGameToRecordData data,int code,Runnable overCall)
	{
		if(data.isExiting)
		{
			if(overCall!=null)
			{
				data.exitOverCalls.add(overCall);
			}
			
			return;
		}
		
		data.isExiting=true;
		data.debugLog("playerExitForSwitchTo标记退出",1);
		data.exitCode=code;
		data.startWait();
		
		if(overCall!=null)
		{
			data.exitOverCalls.add(overCall);
		}
		
		//阶段
		switch(data.state)
		{
			case PlayerSwitchStateType.NoticeToSource:
			{
				Player lastPlayer;
				
				//在线的
				if((lastPlayer=getPlayerByIDT(data.keyData.playerID))!=null)
				{
					//设置为在线，然后直接退出
					lastPlayer.system.setLoginState(PlayerLoginStateType.Online);
					crowedDownPlayer(lastPlayer,overCall);
				}
				else
				{
					data.errorLog("不该找不到在线角色");
				}
			}
				break;
			case PlayerSwitchStateType.SwitchCurrent:
			{
				Player lastPlayer;
				
				//在线的
				if((lastPlayer=getPlayerByIDT(data.keyData.playerID))!=null)
				{
					crowedDownPlayer(lastPlayer,overCall);
				}
				else
				{
					data.errorLog("不该找不到在线角色");
				}
			}
				break;
			case PlayerSwitchStateType.SendToTarget:
			case PlayerSwitchStateType.WaitClient:
			{
				if(ShineSetting.openCheck)
				{
					if(getOfflinePlayer(data.keyData.playerID)!=null)
					{
						Ctrl.throwError("此时不该还有离线角色",data.keyData.getInfo());
					}
				}
				
				if(ShineSetting.needDebugLog)
					Ctrl.debugLog("发出退出召唤",data.keyData.getInfo());
				
				if(data.targetGameID<=0)
				{
					data.errorLog("不该没有targetGameID");
					return;
				}
				
				if(data.targetGameID==GameC.app.id)
				{
					onCallPlayerExit(data.keyData.playerID);
				}
				else
				{
					PlayerCallSwitchBackToGameServerRequest.create(data.keyData.playerID).send(data.targetGameID);
				}
			}
				break;
			case PlayerSwitchStateType.SourceWait:
			case PlayerSwitchStateType.SourceReady:
			{
				if(ShineSetting.openCheck)
				{
					if(getOfflinePlayer(data.keyData.playerID)!=null)
					{
						Ctrl.throwError("此时不该还有离线角色",data.keyData.getInfo());
					}
				}
				
				if(ShineSetting.needDebugLog)
					Ctrl.debugLog("发出退出召唤",data.keyData.getInfo());
				
				if(data.nowGameID<=0)
				{
					data.errorLog("不该没有nowGameID");
					return;
				}
				
				if(data.nowGameID==GameC.app.id)
				{
					onCallPlayerExit(data.keyData.playerID);
				}
				else
				{
					PlayerCallSwitchBackToGameServerRequest.create(data.keyData.playerID).send(data.nowGameID);
				}
			}
				break;
		}
	}
	
	/** 收到预备退出回调 */
	public void onRePlayerPreExit(long playerID)
	{
		Player player=getPlayerByIDT(playerID);
		
		if(player==null)
		{
			Ctrl.warnLog("rePlayerPreExit时,角色为空");
			return;
		}
		
		//不是退出中
		if(!player.system.isStateExiting())
		{
			player.warnLog("rePlayerPreExit时,角色状态不对");
			return;
		}
		
		if(player.system.exitWaitTime==0)
		{
			player.warnLog("rePlayerPreExit时,已到时");
			return;
		}
		
		//倒计时清0
		player.system.exitWaitTime=0;
		
		if(ShineSetting.needDebugLog)
			player.debugLog("onRePlayerPreExit收到预备退出调用Next");
		
		playerExitNext(player);
	}
	
	/** 角色下线逻辑(主线程) */
	public void playerExit(Player player,int code)
	{
		playerExit(player,code,null);
	}
	
	/** 角色下线逻辑(主线程) */
	public void playerExit(Player player,int code,Runnable overCall)
	{
		SystemPart sPart=player.system;
		
		//已离线
		if(sPart.isStateOffline())
		{
			if(overCall!=null)
				overCall.run();
			
			return;
		}
		
		//退出中
		if(sPart.isStateExiting())
		{
			if(overCall!=null)
			{
				sPart.addExitOverCall(overCall);
			}
			
			return;
		}
		
		//切换中
		if(sPart.isStateSwitching())
		{
			if(overCall!=null)
			{
				sPart.addExitOverCall(overCall);
			}
			
			sPart.setNeedExitNext(true,code);
			
			return;
		}
		
		//登录中
		if(sPart.isStateLogining())
		{
			if(overCall!=null)
			{
				sPart.addExitOverCall(overCall);
			}
			
			sPart.setNeedExitNext(true,code);
			
			return;
		}
		
		player.debugLog("playerExit",code);
		
		//修改标记为退出中
		sPart.setLoginState(PlayerLoginStateType.Exiting);
		
		player.sendInfoCode(code);
		//设置当前退出码
		player.system.setNowExitCode(code);
		
		if(overCall!=null)
		{
			sPart.addExitOverCall(overCall);
		}
		
		//是当前服的,或者跨服回不再发到中心服
		if(player.isCurrentGame() || code==InfoCodeType.PlayerExit_callSwitchBack)
		{
			if(ShineSetting.needDebugLog)
				player.debugLog("playerExit角色下线调用Next");
			
			//不是主动退出就关闭连接
			playerExitNext(player);
		}
		else
		{
			player.system.exitWaitTime=ShineSetting.affairDefaultExecuteTime;
			
			PlayerPreExitToGameServerRequest.create(player.role.playerID).send(player.role.sourceGameID);
		}
	}
	
	/** 召唤角色退出回(只有个服务器关闭原因) */
	public void onCallPlayerExit(long playerID)
	{
		if(ShineSetting.needDebugLog)
			Ctrl.debugLog("收到退出召唤",playerID);
		
		Player player;
		
		//在线角色
		if((player=getPlayerByIDT(playerID))!=null)
		{
			Ctrl.debugLog("退出召唤上",playerID);
			//退出该角色
			playerExit(player,InfoCodeType.PlayerExit_callSwitchBack);
			return;
		}
		
		PlayerSwitchGameReceiveRecordData rData;
		
		if((rData=GameC.gameSwitch.getSwitchReceivePlayerByID(playerID))!=null)
		{
			Ctrl.debugLog("退出召唤上",playerID);
			
			GameC.gameSwitch.removePlayerFromSwitchReceive(rData);
			
			if(rData.keyData.sourceGameID==GameC.app.id)
			{
				GameC.gameSwitch.onPlayerExitSwitchBack(playerID,rData.listData);
			}
			else
			{
				//发送到源game服
				PlayerExitSwitchBackServerRequest.create(playerID,rData.listData).send(rData.keyData.sourceGameID);
			}
			
			return;
		}
		
		//离线角色
		if((player=getOfflinePlayer(playerID))!=null)
		{
			player.log("角色召唤退出回时,已完成离线");
			return;
		}
		
		if(ShineSetting.needDebugLog)
			Ctrl.debugLog("角色召唤退出回时,角色不存在(收到重复的CallExit)",playerID);
		
		//不做处理
	}
	
	/** 角色退出第二部(主线程) */
	private void playerExitNext(Player player)
	{
		SystemPart sPart=player.system;
		
		//解绑socket
		sPart.socket.unbindPlayer();
		
		try
		{
			//global退出
			GameC.global.onPlayerLeave(player);
			
			//登出(先与onLogout)
			player.onLogoutOnMain();
		}
		catch(Exception e)
		{
			player.errorLog(e);
		}
		
		if(ShineSetting.needDebugLog)
			player.debugLog("playerExitNext_1");
		
		if(ShineSetting.openCheck)
		{
			//没有执行器，并且不是切换中
			if(player.system.getExecutor()==null && !player.system.isSwitchingExecutor())
			{
				player.throwError("playerExitNext时出错，已经丢失了执行器");
			}
		}
		
		//切逻辑线程
		player.addFunc(()->
		{
			if(ShineSetting.needDebugLog)
				player.debugLog("playerExitNext_2");
			
			sPart.getExecutor().playerExit(player);
			
			player.addMainFunc(()->
			{
				playerExitThird(player);
			});
		});
	}
	
	/** 角色退出第三步(主线程) */
	private void playerExitThird(Player player)
	{
		if(ShineSetting.needDebugLog)
			player.debugLog("playerExitThird_1");
		
		//不是登出过程
		if(!player.system.isStateExiting())
		{
			player.throwError("不应该不是退出过程");
			return;
		}
		
		player.log("角色下线完成");
		
		removePlayer(player);
		
		if(player.system.socket!=null)
		{
			player.system.socket.close();
			
			player.system.socket=null;
		}
		
		GameC.gameSwitch.removePlayerSwitchToByPlayerID(player.role.playerID);
		
		//先标记退出
		player.system.setLoginState(PlayerLoginStateType.Offline);
		//再执行一下离线事务
		player.system.flushProcessOfflineWork();
		
		//先获取好
		SList<Runnable> list=player.system.getExitOverCalls();
		
		//是当前的
		if(player.isCurrentGame())
		{
			addOfflinePlayer(player);
			
			GameC.gameSwitch.sourceSwitchToExit(player.role.playerID,false);
		}
		else
		{
			//写出列表数据
			PlayerListData listData=player.createListData();
			//潜拷
			player.writeListData(listData);
			
			//发送到源game服
			PlayerExitSwitchBackServerRequest.create(player.role.playerID,listData).send(player.role.sourceGameID);
			
			removeExistPlayer(player);
		}
		
		Runnable[] values=list.getValues();
		
		for(int i=0, len=list.size();i<len;++i)
		{
			values[i].run();
		}
	}
	
	/** 客户端连接断开(主线程) */
	public void clientSocketClosed(BaseSocket socket)
	{
		Ctrl.log("客户端连接断开",socket.id);

		UserLoginRecordData data=_userLoginDicBySocketID.get(socket.id);
		
		if(data!=null)
		{
			_userLoginDicBySocketID.remove(socket.id);
			data.socket=null;
		}
		
		Player player=getPlayerBySocketIDT(socket.id);
		
		if(player==null)
		{
			//Ctrl.warnLog("客户端连接断开时找不到玩家");
			return;
		}
		
		//解绑socket
		player.system.socket.unbindPlayer();
		
		//退出中
		if(player.system.isStateExiting())
		{
			return;
		}
		
		player.log("客户端连接断开,下线");
		
		playerExit(player,InfoCodeType.PlayerExit_socketClose);
	}
	
	/** 踢下线(主线程) */
	public void kickPlayer(long playerID,Runnable func)
	{
		PlayerSwitchGameToRecordData switchRecordData;
		
		if((switchRecordData=GameC.gameSwitch.getSwitchToPlayerByID(playerID))!=null)
		{
			playerExitForSwitchTo(switchRecordData,InfoCodeType.PlayerExit_beKicked,func);
			
			return;
		}
		
		Player lastPlayer;
		
		//在线的
		if((lastPlayer=getPlayerByIDT(playerID))!=null)
		{
			//被挤下线
			playerExit(lastPlayer,InfoCodeType.PlayerExit_beKicked,func);
			return;
		}
		else
		{
			if(func!=null)
				func.run();
		}
	}
	
	/** 清空全部角色(下线时使用,并全部写库) */
	public void clearAllPlayer(Runnable overCall)
	{
		_clearOverFunc=overCall;
		_isClearing=true;
		_clearPlayerTimeOut=CommonSetting.clearPlayerMaxDelay;
		
		Ctrl.log("开始清空全部角色");
		
		checkClearOver();
	}
	
	public boolean isClearing()
	{
		return _isClearing;
	}
	
	/** 角色是否删除中 */
	private boolean isDeleting(long playerID)
	{
		return _deletePlayerCallDic.get(playerID)!=null;
	}
	
	/** 客户端主动删除角色 */
	public void deletePlayerForClient(long playerID,GameReceiveSocket socket)
	{
		UserLoginRecordData data=_userLoginDicBySocketID.get(socket.id);
		
		//不存在
		if(data==null)
		{
			socket.sendInfoCode(InfoCodeType.DeletePlayer_notExit);
			return;
		}
		
		//不存在
		if(!data.playerIDs.contains(playerID))
		{
			socket.sendInfoCode(InfoCodeType.DeletePlayer_notExit);
			return;
		}
		
		deletePlayer(playerID,()->
		{
			data.playerIDs.remove(playerID);
			//删除角色成功
			socket.send(DeletePlayerSuccessRequest.create(playerID));
		});
	}
	
	/** 删除角色(真删除) */
	public void deletePlayer(long playerID,Runnable overFunc)
	{
		//踢下线先
		kickPlayer(playerID,()->
		{
			if(overFunc!=null)
			{
				if(ShineSetting.openCheck)
				{
					if(_deletePlayerCallDic.get(playerID)!=null)
					{
						Ctrl.warnLog("deletePlayer时,已有回调");
					}
				}
				
				_deletePlayerCallDic.put(playerID,overFunc);
			}
			
			getOfflinePlayerOrDB(playerID,player->
			{
				GameC.global.onPlayerDelete(player);
				player.onDeletePlayer();
				
				removeOfflinePlayer(player);
				
				PlayerDeletedWData wData=new PlayerDeletedWData();
				wData.uid=player.role.uid;
				wData.userID=player.role.userID;
				wData.playerID=player.role.playerID;
				
				addUserWork(wData);
				
				if(GameC.global.social.needRoleSocialCenter(playerID))
				{
					PlayerDeletedToCenterWData cwData=new PlayerDeletedToCenterWData();
					cwData.playerID=playerID;
					addCenterWork(cwData);
				}
				
				//移除名字占用
				deletePlayerName(player.role.name);
				
				//移除表
				GameC.db.deletePlayerTable(playerID);
				
				//回调
				Runnable func=_deletePlayerCallDic.remove(playerID);
				
				if(func!=null)
				{
					func.run();
				}
			});
		});
	}
	
	private void checkClearOver()
	{
		LongObjectMap<PlayerSwitchGameToRecordData> switchToPlayers=GameC.gameSwitch.getSwitchToPlayers();
		LongObjectMap<PlayerSwitchGameReceiveRecordData> switchReceivePlayers=GameC.gameSwitch.getSwitchReceivePlayers();
		
		if(ShineSetting.needDebugLog)
		{
			Ctrl.debugLog("清理玩家中",_players.size(),switchToPlayers.size(),switchReceivePlayers.size());
		}
		
		if(_players.isEmpty() && switchToPlayers.isEmpty() && switchReceivePlayers.isEmpty())
		{
			clearOver();
		}
		else
		{
			_players.forEachValueS(v->
			{
				playerExit(v,InfoCodeType.PlayerExit_serverClose,null);
			});
			
			switchToPlayers.forEachValueS(v->
			{
				playerExitForSwitchTo(v,InfoCodeType.PlayerExit_serverClose,null);
			});
			
			switchReceivePlayers.forEachValueS(v->
			{
				GameC.gameSwitch.switchReceiveExit(v);
			});
		}
	}
	
	private void clearOver()
	{
		if(_isClearing)
		{
			_isClearing=false;
			_clearPlayerTimeOut=0;
			
			Ctrl.debugLog("停服GameControl清理玩家阶段完成");
			
			//清离线
			clearOfflinePlayers();
			
			_clearOverFunc.run();
		}
	}
	
	/** 清离线组(退出时) */
	private void clearOfflinePlayers()
	{
		_offlinePlayers.forEachValueS(player->
		{
			removeOfflinePlayer(player);
		});
	}
	
	/** 添加角色事务(主线程)(包括switched)(已clone过的) */
	private void toAddPlayerWork(PlayerWorkData data)
	{
		data.timestamp=DateControl.getTimeMillis();
		data.senderIndex=BaseGameUtils.getWorkSenderIndex(WorkSenderType.Game,GameC.app.id);
		
		int areaID=BaseC.logic.getAreaIDByLogicID(data.receivePlayerID);
		
		//不存在的区
		if(!isAreaAvailable(areaID))
		{
			onWorkResult(data,InfoCodeType.WorkError);
			return;
		}
		
		//不是在线事务
		if(data.workType!=WorkType.PlayerOnline)
		{
			//记录事务
			GameC.global.system.getWorkSendTool().recordWork(data);
		}
		
		ThreadControl.checkCurrentIsMainThread();
		
		doAddPlayerWork(data);
	}
	
	/** 重放事务 */
	public void resendWork(WorkData data)
	{
		if(data instanceof PlayerWorkData)
		{
			doAddPlayerWork((PlayerWorkData)data);
		}
		else if(data instanceof AreaGlobalWorkData)
		{
			doAddAreaWork((AreaGlobalWorkData)data);
		}
		else if(data instanceof UserWorkData)
		{
			doSendUserWork((UserWorkData)data);
		}
		else if(data instanceof CenterGlobalWorkData)
		{
			doSendCenterWork((CenterGlobalWorkData)data);
		}
	}
	
	/** 完成事务 */
	public void completeWork(WorkData data)
	{
		if(data instanceof PlayerWorkData)
		{
			onPlayerWorkComplete(((PlayerWorkData)data).createCompleteData());
		}
		else if(data instanceof AreaGlobalWorkData)
		{
			onAreaWorkComplete(((AreaGlobalWorkData)data).createCompleteData());
		}
		else if(data instanceof CenterGlobalWorkData)
		{
			onCenterWorkComplete(data.createCompleteData());
		}
		else if(data instanceof UserWorkData)
		{
			//user事务，不发送第三部回执，因为login上没有存储
		}
		
		else
		{
			Ctrl.throwError("不支持的事务类型2",data.getDataClassName());
		}
	}
	
	/** 事务执行结果 */
	protected void onWorkResult(WorkData data,int result)
	{
		if(data instanceof PlayerToRoleGroupTCCWData)
		{
			PlayerToRoleGroupTCCWData wData=(PlayerToRoleGroupTCCWData)data;
			
			PlayerToRoleGroupTCCResultWData wData2=new PlayerToRoleGroupTCCResultWData();
			wData2.workData=wData;
			wData2.result=result;
			
			addPlayerOfflineWork(wData.sendPlayerID,wData2);
			return;
		}
		
		if(data instanceof PlayerToPlayerTCCWData)
		{
			PlayerToPlayerTCCWData wData=(PlayerToPlayerTCCWData)data;
			
			PlayerToPlayerTCCResultWData wData2=new PlayerToPlayerTCCResultWData();
			
			wData2.workData=wData;
			wData2.result=result;

			addPlayerOfflineWork(wData.sendPlayerID,wData2);
			return;
		}
		
		//失败
		if(result!=InfoCodeType.Success)
		{
			//角色查询事务，给与false返回
			if(data instanceof QueryPlayerAWData)
			{
				QueryPlayerAWData wData=(QueryPlayerAWData)data;
				
				QueryPlayerResultData reData=new QueryPlayerResultData();
				reData.queryPlayerID=wData.receivePlayerID;
				reData.queryType=wData.type;
				reData.queryArgs=wData.args;
				reData.isSuccess=false;
				
				ReQueryPlayerOWData wData2=new ReQueryPlayerOWData();
				wData2.result=reData;
				
				addPlayerOnlineWork(wData.sendPlayerID,wData2);
			}
		}
	}
	
	/** 添加一组角色事务数据 */
	public void onAddPlayerWorkList(SList<PlayerWorkData> list)
	{
		PlayerWorkData[] values=list.getValues();
		PlayerWorkData v;
		
		for(int i=0,len=list.size();i<len;++i)
		{
			v=values[i];
			
			doAddPlayerWork(v);
		}
	}
	
	/** 执行添加(发起者) */
	private void doAddPlayerWork(PlayerWorkData data)
	{
		long playerID=data.receivePlayerID;
		int type=data.workType;
		
		Player player;
		//在线
		if((player=getPlayerByID(playerID))!=null)
		{
			player.system.onAddWorkForMain(data);
			return;
		}
		
		int areaID=BaseC.logic.getAreaIDByLogicID(playerID);
		
		//本服的
		if(containsArea(areaID))
		{
			//切换中
			if(GameC.gameSwitch.isPlayerSwitched(playerID))
			{
				PlayerSwitchGameToRecordData sData=GameC.gameSwitch.getSwitchToPlayerByID(playerID);
				
				if(sData==null)
				{
					Ctrl.errorLog("此时不该找不到switchTo数据");
					return;
				}
				
				if(sData.isSwitching())
				{
					//if(sData.hasReceive)
					//{
					//	SendPlayerWorkToGameServerRequest.create(data).send(sData.targetGameID);
					//}
					//else
					//{
					sData.addWork(data);
					//}
				}
				else
				{
					SendPlayerWorkToGameServerRequest.create(data).send(sData.nowGameID);
				}
			}
			else
			{
				//在线就结束
				if(type==WorkType.PlayerOnline)
					return;
				
				//存在角色
				if((player=getExistPlayerByID(playerID))!=null)
				{
					if(player.system.isStateOffline())
					{
						player.system.executeWork(data);
					}
					else
					{
						player.system.addProcessOfflineWork(data);
					}
				}
				else
				{
					//离线角色
					addPlayerWorkToDBPlayer(type,playerID,data);
					return;
				}
			}
		}
		else
		{
			//发回源服
			SendPlayerWorkToGameServerRequest.create(data).send(getNowGameID(areaID));
		}
	}
	
	/** 添加一组角色事务数据 */
	public void onPlayerWorkCompleteList(SList<PlayerWorkCompleteData> list)
	{
		PlayerWorkCompleteData[] values=list.getValues();
		PlayerWorkCompleteData v;
		
		for(int i=0,len=list.size();i<len;++i)
		{
			v=values[i];
			
			onPlayerWorkComplete(v);
		}
	}
	
	/** 执行添加(发起者)(主线程) */
	public void onPlayerWorkComplete(PlayerWorkCompleteData data)
	{
		long playerID=data.receivePlayerID;
		
		Player player;
		//在线
		if((player=getPlayerByID(playerID))!=null)
		{
			player.system.onWorkCompleteForMain(data);
			return;
		}
		
		//本服的
		if(isCurrentGame(playerID))
		{
			//切换中
			if(GameC.gameSwitch.isPlayerSwitched(playerID))
			{
				PlayerSwitchGameToRecordData sData=GameC.gameSwitch.getSwitchToPlayerByID(playerID);
				
				if(sData==null)
				{
					Ctrl.errorLog("此时不该找不到switchTo数据");
					return;
				}
				
				if(sData.isSwitching())
				{
					//if(sData.hasReceive)
					//{
					//	SendPlayerWorkCompleteToGameServerRequest.create(data).send(sData.targetGameID);
					//}
					//else
					//{
						sData.addWorkComplete(data);
					//}
				}
				else
				{
					SendPlayerWorkCompleteToGameServerRequest.create(data).send(sData.nowGameID);
				}
			}
			else
			{
				//存在角色
				if((player=getExistPlayerByID(playerID))!=null)
				{
					if(player.system.isStateOffline())
					{
						player.system.onWorkComplete(data);
					}
					else
					{
						Ctrl.warnLog("onPlayerWorkComplete时,角色处于其他状态",player.system.getLoginState());
					}
				}
			}
		}
		else
		{
			//发回源服
			SendPlayerWorkCompleteToGameServerRequest.create(data).sendToSourceGameByLogicID(playerID);
		}
	}
	
	/** 添加离线事务执行到当前存在的角色上 */
	private boolean addOfflineWorkToExistPlayer(long playerID,PlayerWorkData data)
	{
		Player player;
		
		//在线
		if((player=getExistPlayerByID(playerID))!=null)
		{
			//在线
			if(player.system.isStateOnline())
			{
				player.system.onAddWorkForMain(data);
			}
			//离线
			else if(player.system.isStateOffline())
			{
				player.system.executeWork(data);
			}
			else
			{
				player.system.addProcessOfflineWork(data);
			}
			
			return true;
		}
		
		return false;
	}
	
	/** 添加离线事务到db角色上 */
	private void addPlayerWorkToDBPlayer(int type,long playerID,PlayerWorkData data)
	{
		//不启用table或abs类型
		if(!CommonSetting.offlineWorkUseTable || type==WorkType.PlayerAbs)
		{
			getOfflinePlayerOrDB(playerID,v->
			{
				if(v==null)
				{
					Ctrl.warnLog("添加离线事务到db角色上时,不存在的角色:",playerID);
					receiptPlayerWork(data,InfoCodeType.Work_playerNotExit);
				}
				else
				{
					if(!addOfflineWorkToExistPlayer(playerID,data))
					{
						Ctrl.throwError("离线事务执行失败");
					}
				}
			});
		}
		else
		{
			
			PlayerTable table;
			
			if((table=GameC.db.getPlayerTable(playerID))!=null)
			{
				table.offlineWorkDataListMain.add(data);
				table.offlineWorkDirty=true;
				receiptPlayerWork(data,InfoCodeType.Success);
			}
			else
			{
				GameC.db.getPlayerTableAbsByID(playerID,t->
				{
					if(t==null)
					{
						Ctrl.warnLog("添加离线事务到db角色上时,不存在的角色:",playerID);
						receiptPlayerWork(data,InfoCodeType.Work_playerNotExit);
						return;
					}
					
					//再补一次
					if(addOfflineWorkToExistPlayer(playerID,data))
						return;
					
					t.offlineWorkDataListMain.add(data);
					t.offlineWorkDirty=true;
					receiptPlayerWork(data,InfoCodeType.Success);
				});
			}
		}
	}
	
	/** 添加角色事务(主/池线程)(包括switched)(已clone过的) */
	public void addPlayerWork(int type,long playerID,PlayerWorkData data)
	{
		data.receivePlayerID=playerID;
		data.workType=type;
		
		int threadType=ThreadControl.getCurrentThreadType();
		
		if(threadType==ThreadType.Main)
		{
			toAddPlayerWork(data);
		}
		else if(threadType==ThreadType.Pool)
		{
			//此处clone号，逻辑层可无需
			PlayerWorkData tData=(PlayerWorkData)data.clone();
			
			ThreadControl.addMainFunc(()->
			{
				toAddPlayerWork(tData);
			});
		}
		else
		{
			Ctrl.throwError("不支持的线程类型"+threadType);
		}
	}
	
	/** 添加角色组事务(主线程)(包括switched)(已clone过的) */
	public void addPlayerWorkList(int type,LongSet list,PlayerWorkData data)
	{
		int threadType=ThreadControl.getCurrentThreadType();
		
		if(threadType==ThreadType.Main)
		{
			doAddPlayerWorkList(type,list,data);
		}
		else if(threadType==ThreadType.Pool)
		{
			ThreadControl.addMainFunc(()->
			{
				doAddPlayerWorkList(type,list,data);
			});
		}
		else
		{
			Ctrl.throwError("不支持的线程类型"+threadType);
		}
	}
	
	private void doAddPlayerWorkList(int type,LongSet list,PlayerWorkData data)
	{
		list.forEachA(k->
		{
			PlayerWorkData tData=(PlayerWorkData)data.clone();
			tData.workType=type;
			tData.receivePlayerID=k;
			
			toAddPlayerWork(tData);
		});
	}
	
	/** 添加角色事务来自中心服 */
	public void onAddPlayerWork(PlayerWorkData data)
	{
		doAddPlayerWork(data);
	}
	
	//work 接口部分
	
	/** 添加中心服事务(主线程) */
	public void addCenterWork(CenterGlobalWorkData data)
	{
		ThreadControl.checkCurrentIsMainThread();
		
		data.timestamp=DateControl.getTimeMillis();
		data.senderIndex=BaseGameUtils.getWorkSenderIndex(WorkSenderType.Game,GameC.app.id);
		data.workType=WorkType.Center;
		
		//记录事务
		GameC.global.system.getWorkSendTool().recordWork(data);
		
		doSendCenterWork(data);
	}
	
	/** 执行发送中心服事务 */
	private void doSendCenterWork(CenterGlobalWorkData data)
	{
		//发送
		SendCenterWorkToCenterServerRequest.create(data).send();
	}
	
	/** 添加离线事务(主/池线程)(包括switched)(已clone过的) */
	public void addPlayerOfflineWork(long playerID,PlayerWorkData data)
	{
		addPlayerWork(WorkType.PlayerOffline,playerID,data);
	}
	
	/** 添加离线事务组(主/池线程)(包括switched)(已clone过的) */
	public void addPlayerOfflineWorkList(LongSet list,PlayerWorkData data)
	{
		addPlayerWorkList(WorkType.PlayerOffline,list,data);
	}
	
	/** 添加角色在线事务(主/池线程)(包括switched) */
	public void addPlayerOnlineWork(long playerID,PlayerWorkData data)
	{
		addPlayerWork(WorkType.PlayerOnline,playerID,data);
	}
	
	/** 添加角色在线事务(主/池线程)(包括switched) */
	public void addPlayerOnlineWorkList(LongSet list,PlayerWorkData data)
	{
		addPlayerWorkList(WorkType.PlayerOnline,list,data);
	}
	
	/** 添加立即事务(主/池线程)(包括switched) */
	public void addPlayerAbsWork(long playerID,PlayerWorkData data)
	{
		addPlayerWork(WorkType.PlayerAbs,playerID,data);
	}
	
	/** 角色事务回执(主线程) */
	public void receiptPlayerWork(PlayerWorkData data,int result)
	{
		//没有实例ID
		if(data.workInstanceID<=0L)
			return;
		
		int type=BaseGameUtils.getWorkSenderType(data.senderIndex);
		
		//中心服
		if(type==WorkSenderType.Center)
		{
			ReceiptWorkToCenterServerRequest.create(data.workInstanceID,result).send();
		}
		//game服
		else if(type==WorkSenderType.Game)
		{
			int senderID=BaseGameUtils.getWorkSenderID(data.senderIndex);
			
			//当前区
			if(containsArea(senderID))
			{
				onReceiptWork(data.senderIndex,data.workInstanceID,result);
			}
			else
			{
				ReceiptWorkToGameServerRequest.create(data.senderIndex,data.workInstanceID,result).send(getNowGameID(senderID));
			}
		}
	}
	
	/** 收到回执 */
	public void onReceiptWork(int senderIndex,long instanceID,int result)
	{
		WorkData workData=GameC.global.system.onReceiptWork(senderIndex,instanceID);
		
		if(workData!=null)
		{
			onWorkResult(workData,result);
		}
	}
	
	/** 返回查询结果 */
	public void reQueryResult(int httpID,BaseData data)
	{
		SendMQueryPlayerWorkResultToCenterServerRequest.create(httpID,data).send();
	}
	
	/** 修改玩家名字(主线程) */
	public void changePlayerName(long playerID,String name)
	{
		ThreadControl.checkCurrentIsMainThread();
		
		//在线才能改名
		Player player=getPlayerByID(playerID);
		
		if(player==null)
		{
			Ctrl.warnLog("修改玩家名字时,未取到角色");
			return;
		}
		
		if(!player.isCurrentGame())
		{
			Ctrl.warnLog("修改玩家名字时,不是当前服");
			return;
		}
		
		//使用名
		checkAndGiveName(name,player.role.createAreaID,player.system.socket,nn->
		{
			//不可重名
			if(nn==null)
				return;
			
			//在线才能改名
			Player player2=getPlayerByID(playerID);
			
			if(player2==null)
				return;
			
			String oldName=player2.role.name;
			
			useAndInsertPlayerName(nn,player2.role.playerID,player2.system.socket,b->
			{
				if(b)
				{
					//在线才能改名
					Player player3=getPlayerByID(playerID);
					
					if(player3==null)
					{
						deletePlayerName(nn);
						return;
					}
					
					deletePlayerName(oldName);
					
					player3.role.name=nn;
					//改名
					GameC.global.social.onPlayerChangeName(playerID,nn);
					
					GameC.db.changePlayerTableName(playerID,nn);
					
					//在线状态
					if(player3.system.isStateOnline())
					{
						player3.addFunc(()->
						{
							player3.role.onChangeName(nn);
						});
					}
				}
			});
		});
	}
	
	/** 添加区服全局事务(主线程) */
	public void addAreaWork(AreaGlobalWorkData data)
	{
		data.timestamp=DateControl.getTimeMillis();
		data.senderIndex=BaseGameUtils.getWorkSenderIndex(WorkSenderType.Game,GameC.app.id);
		data.workType=WorkType.Game;
		
		ThreadControl.checkCurrentIsMainThread();
		
		if(isAreaAvailable(data.receiveAreaID))
		{
			//记录事务
			GameC.global.system.getWorkSendTool().recordWork(data);
			
			doAddAreaWork(data);
		}
		else
		{
			onWorkResult(data,InfoCodeType.WorkError);
		}
	}
	
	/** 收到添加区服事务 */
	public void onAddAreaWork(AreaGlobalWorkData data)
	{
		doAddAreaWork(data);
	}
	
	/** 执行添加区服事务 */
	private void doAddAreaWork(AreaGlobalWorkData data)
	{
		int nowGameID=getNowGameID(data.receiveAreaID);
		
		//是当前服
		if(nowGameID==GameC.app.id)
		{
			GameC.global.system.executeAreaWork(data);
		}
		else
		{
			SendAreaWorkToGameServerRequest.create(data).send(nowGameID);
		}
	}
	
	/** 执行添加(发起者)(主线程) */
	public void onAreaWorkComplete(AreaGlobalWorkCompleteData data)
	{
		int nowGameID=getNowGameID(data.receiveAreaID);
		
		//是当前服
		if(nowGameID==GameC.app.id)
		{
			GameC.global.system.getWorkReceiveTool().onCompleteReceipt(data.workInstanceID,data.senderIndex);
		}
		else
		{
			SendAreaWorkCompleteToGameServerRequest.create(data).send(nowGameID);
		}
	}
	
	/** 执行添加(发起者)(主线程) */
	public void onCenterWorkComplete(WorkCompleteData data)
	{
		SendCenterWorkCompleteToCenterServerRequest.create(data).send();
	}
	
	/** 添加user事务 */
	public void addUserWork(UserWorkData data)
	{
		data.timestamp=DateControl.getTimeMillis();
		data.senderIndex=BaseGameUtils.getWorkSenderIndex(WorkSenderType.Game,GameC.app.id);
		data.workType=WorkType.User;
		
		GameC.global.system.getWorkSendTool().recordWork(data);
		doSendUserWork(data);
	}
	
	private void doSendUserWork(UserWorkData data)
	{
		SendUserWorkToLoginServerRequest.create(data).sendByUID(data.uid);
	}
	
	//operate count
	
	/** 今日新进入角色数目 */
	public int getTodayNewPlayerNum()
	{
		return _todayNewPlayerNum;
	}
	
	/** 今日创建角色数目 */
	public int getTodayCreatePlayerNum()
	{
		return _todayCreatePlayerNum;
	}
	
	/** 今日在线最高峰值 */
	public int getTodayTopOnlineNum()
	{
		return _todayTopOnlineNum;
	}
	
	/** 当前在线人数 */
	public int getPlayerOnlineNum()
	{
		return _playerOnlineNum;
	}
	
	/** 广播所有在线角色(主线程) */
	public void radioAllPlayer(BaseRequest request)
	{
		ThreadControl.checkCurrentIsMainThread();
		
		request.write();
		
		Player[] values;
		Player v;
		
		for(int i=(values=_players.getValues()).length-1;i>=0;--i)
		{
			if((v=values[i])!=null)
			{
				v.send(request);
			}
		}
		
		//TODO:这里需要处理不在线角色的问题
	}
	
	/** 添加到所有执行器，进行执行的方法 */
	public void addAllExecutorFunc(ObjectCall<LogicExecutor> func)
	{
		for(LogicExecutor executor : _executors)
		{
			executor.addSelfFunc(func);
		}
	}
	
	/** 添加到所有在线角色所在执行器，并对角色进行执行的方法 */
	public void addAllPlayerFunc(ObjectCall<Player> func)
	{
		Player[] values;
		Player v;
		
		for(int i=(values=_players.getValues()).length-1;i>=0;--i)
		{
			if((v=values[i])!=null)
			{
				v.system.addSelfFunc(func);
			}
		}
	}

	/** 客户端版本数据(验证用) */
	public ClientVersionData getClientVersion(int type)
	{
		return _clientVersion.get(type);
	}

	public void setClientVersion(IntObjectMap<ClientVersionData> versionDic)
	{
		_clientVersion = versionDic;
	}
	
	/** 设置信息后 */
	public void afterSetInfos()
	{
		_isAssist=GameC.server.getInfo().isAssist;
		
		_assistGameList=new IntList();
		
		IntObjectMap<GameServerSimpleInfoData> dic=GameC.server.getGameSimpleInfoDic();
		
		for(int k:GameC.server.getGameList())
		{
			if(dic.get(k).isAssist)
			{
				_assistGameList.add(k);
			}
		}
		
		findAvailableAssistGame();
	}

	/** 角色绑定到平台 */
	public void onPlayerBindPlatform(long playerID,String uid,String platform)
	{
		PlayerBindPlatformAWData data=new PlayerBindPlatformAWData();
		data.uid=uid;
		data.platform=platform;
		
		addPlayerAbsWork(playerID,data);
	}
	
	/** 广播热更 */
	public void radioHotfix()
	{
		IntObjectMap<ClientHotfixRequest> dic=new IntObjectMap<>();
		
		ClientHotfixRequest request;
		int type;
		
		Player[] values;
		Player v;
		
		for(int i=(values=_players.getValues()).length-1;i>=0;--i)
		{
			if((v=values[i])!=null)
			{
				if((request=dic.get((type=v.system.getClientPlatformType())))==null)
				{
					request=ClientHotfixRequest.create(_clientVersion.get(type));
					request.write();
					dic.put(type,request);
				}
				
				v.send(request);
			}
		}
	}
	
	/** 添加账号流程日志 */
	public void addFlowLog(String uid,int step)
	{
		BaseC.logic.addFlowLog(_flowLog,uid,step);
	}
	
	/** 添加账号流程日志 */
	public void addFlowLog(String uid,long playerID,int step)
	{
		BaseC.logic.addFlowLog(_flowLog,uid,playerID,step);
	}
	
	/** 对某角色发送信息码(包括其他服的playerID)(主线程/池线程) */
	public void sendInfoCodeToPlayer(long playerID,int code)
	{
		Player player=getPlayerByID(playerID);
		
		if(player!=null)
		{
			player.sendInfoCode(code);
			return;
		}
		
		SendInfoCodeRequest.create(code).sendToPlayer(playerID);
	}
	
	/** 获取玩家群快捷方式 */
	public RoleGroup getRoleGroup(int funcID,long groupID)
	{
		return GameC.global.func.getRoleGroupTool(funcID).getRoleGroup(groupID);
	}
	
	/** 获取玩家群快捷方式 */
	public void getRoleGroupAbs(int funcID,long groupID,ObjectCall<RoleGroup> func)
	{
		GameC.global.func.getRoleGroupTool(funcID).getRoleGroupAbs(groupID,func);
	}
}
