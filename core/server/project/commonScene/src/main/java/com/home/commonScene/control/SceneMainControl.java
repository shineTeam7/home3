package com.home.commonScene.control;

import com.home.commonBase.config.game.SceneConfig;
import com.home.commonBase.constlist.generate.InfoCodeType;
import com.home.commonBase.constlist.generate.SceneInstanceType;
import com.home.commonBase.data.login.CenterInitServerData;
import com.home.commonBase.data.login.SceneInitServerData;
import com.home.commonBase.data.role.RoleShowData;
import com.home.commonBase.data.scene.scene.CreateSceneData;
import com.home.commonBase.data.scene.scene.SceneEnterArgData;
import com.home.commonBase.data.scene.scene.SceneLocationData;
import com.home.commonBase.data.scene.scene.SceneServerEnterData;
import com.home.commonBase.data.scene.scene.SceneServerExitData;
import com.home.commonBase.data.system.ServerInfoData;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.part.player.list.PlayerListData;
import com.home.commonScene.constlist.system.ScenePlayerLoginStateType;
import com.home.commonScene.global.SceneC;
import com.home.commonScene.net.serverRequest.game.login.PlayerLeaveSceneOverToGameServerRequest;
import com.home.commonScene.net.serverRequest.game.login.PlayerSwitchToSceneOverServerRequest;
import com.home.commonScene.net.serverRequest.game.login.RePlayerSwitchToSceneServerRequest;
import com.home.commonScene.part.ScenePlayer;
import com.home.commonScene.scene.base.SScene;
import com.home.commonScene.server.SceneReceiveSocket;
import com.home.shine.control.ThreadControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.net.socket.BaseSocket;
import com.home.shine.support.collection.IntIntMap;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.collection.LongObjectMap;
import com.home.shine.support.collection.SList;
import com.home.shine.support.pool.ObjectPool;
import com.home.shine.utils.MathUtils;

/** 场景主控制 */
public class SceneMainControl
{
	/** 区服老新对照表(areaID:gameID)(如需修改，必须copyOnWrite) */
	private IntIntMap _areaDic=new IntIntMap();
	/** 场景角色池 */
	private ObjectPool<ScenePlayer> _scenePlayerPool=new ObjectPool<>(SceneC.factory::createScenePlayer);
	
	/** 执行器组(分线程) */
	private SceneLogicExecutor[] _executors;
	/** 场景数目变更标记 */
	private boolean _sceneNumForExecutorDirty=true;
	/** 上个场景最少的executor */
	private int _lastLeastExecutor=-1;
	
	/** 场景角色组 */
	private LongObjectMap<ScenePlayer> _players=new LongObjectMap<>(ScenePlayer[]::new);
	/** 场景角色组(token主键) */
	private IntObjectMap<ScenePlayer> _playersByToken=new IntObjectMap<>(ScenePlayer[]::new);
	/** 在线角色字典(socketID为key) */
	private IntObjectMap<ScenePlayer> _playersBySocketID=new IntObjectMap<>(ScenePlayer[]::new);
	
	/** 临时创建场景数据 */
	private CreateSceneData _createSceneData;
	
	
	//temp
	/** 是否在清理中 */
	private boolean _isClearing=false;
	/** 清理角色超时 */
	private int _clearPlayerTimeOut;
	/** 清理完成回调 */
	private Runnable _clearOverFunc;
	
	public void init()
	{
		_executors=new SceneLogicExecutor[ShineSetting.poolThreadNum];
		
		for(int i=0;i<_executors.length;++i)
		{
			SceneLogicExecutor executor=createExecutor(i);
			_executors[i]=executor;
			
			ThreadControl.addPoolFunc(i,()->
			{
				executor.init();
			});
		}
		
		ThreadControl.getMainTimeDriver().setInterval(this::onSecond,1000);
	}
	
	public void dispose()
	{
	
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
				clearOver();
			}
		}
	}
	
	/** 创建执行器 */
	protected SceneLogicExecutor createExecutor(int index)
	{
		return new SceneLogicExecutor(index);
	}
	
	/** 设置初始化数据 */
	public void setInitData(SceneInitServerData initData)
	{
		_areaDic=initData.areaDic;
		SceneC.server.setSelfInfo(initData.info);
	}
	
	/** 获取在线角色数 */
	public int getPlayerNum()
	{
		return _players.size();
	}
	
	/** 获取人数最少的执行器号(主线程) */
	public int getLeastExecutor()
	{
		if(_sceneNumForExecutorDirty)
		{
			_sceneNumForExecutorDirty=false;
			
			int n=-1;
			int index=-1;
			
			for(int i=0,len=_executors.length;i<len;i++)
			{
				int playerNum=_executors[i].getSceneNum();
				
				if(index==-1 || playerNum<n)
				{
					index=i;
					n=playerNum;
				}
			}
			
			_lastLeastExecutor=index;
		}
		
		return _lastLeastExecutor;
	}
	
	/** 获取对应执行器 */
	public SceneLogicExecutor getExecutor(int index)
	{
		if(index==-1)
		{
			return null;
		}
		
		return _executors[index];
	}
	
	/** 通过playerID获取场景玩家 */
	public ScenePlayer getPlayerByID(long playerID)
	{
		return _players.get(playerID);
	}
	
	/** 角色预备登录(主线程) */
	public void playerPreSwitch(long playerID,RoleShowData showData,SceneEnterArgData enterArg,int fromGameID)
	{
		ScenePlayer player=_players.get(playerID);
		
		if(player==null)
		{
			player=_scenePlayerPool.getOne();
			player.playerID=playerID;
			player.token=MathUtils.getToken();
			player.construct();
			
			while(_playersByToken.contains(player.token))
			{
				player.token=MathUtils.getToken();
			}
			
			_players.put(playerID,player);
			_playersByToken.put(player.token,player);
		}
		
		player.gameID=fromGameID;
		player.loginState=ScenePlayerLoginStateType.WaitConnect;
		player.enterArg=enterArg;
		player.showData=showData;
		
		player.name=showData.name;
		
		ServerInfoData info=SceneC.server.getSelfInfo();
		RePlayerSwitchToSceneServerRequest.create(playerID,player.token,info.clientHost,info.clientPort).send(fromGameID);
	}
	
	/** 角色切换game服(客户端连接到接收) */
	public void playerSwitchSceneReceive(SceneReceiveSocket socket,int token)
	{
		ScenePlayer player=_playersByToken.get(token);
		
		if(player==null)
		{
			Ctrl.warnLog("场景服不存在的token");
			socket.close();
			return;
		}
		
		SceneEnterArgData enterArg=player.enterArg;
		
		SceneLocationData location=enterArg.location;
		
		if(location.serverID!=SceneC.app.id)
		{
			Ctrl.errorLog("场景服id不匹配");
			socket.close();
			return;
		}
		
		if(player.socket!=null)
		{
			_playersBySocketID.remove(player.socket.id);
			player.socket.close();
			player.socket=null;
		}
		
		player.loginState=ScenePlayerLoginStateType.WaitGameInfo;
		//相互绑定
		player.socket=socket;
		
		_playersBySocketID.put(socket.id,player);
		
		//未分配
		if(location.executorIndex==-1)
		{
			makeSignedSceneExecutorIndex(location);
		}
		
		SceneLogicExecutor executor=getExecutor(location.executorIndex);
		
		socket.setPlayer(player);
		
		executor.addFunc(()->
		{
			executor.playerSwitchIn(player);
			
			SScene scene=executor.getOrCreateScene(location);
			
			player.preSceneInstanceID=scene.instanceID;
			
			SceneLocationData sceneLocation=scene.createLocationData();
			
			PlayerSwitchToSceneOverServerRequest.create(player.playerID,sceneLocation).send(player.gameID);
		});
	}
	
	public void playerEnterServerScene(ScenePlayer player,SceneServerEnterData data)
	{
		player.loginState=ScenePlayerLoginStateType.Running;
		
		player.addFunc(()->
		{
			player.enterPreScene(data);
		});
	}
	
	/** 获取指定场景执行器号 */
	private void makeSignedSceneExecutorIndex(SceneLocationData data)
	{
		SceneConfig config=SceneConfig.get(data.sceneID);
		
		switch(config.instanceType)
		{
			case SceneInstanceType.SinglePlayerBattle:
			{
				data.executorIndex=getLeastExecutor();
			}
				break;
			default:
			{
				Ctrl.throwError("不支持的场景类型");
			}
		}
	}
	
	/** 清空所有角色 */
	public void clearAllPlayer(Runnable overCall)
	{
		_clearOverFunc=overCall;
		_isClearing=true;
		_clearPlayerTimeOut=CommonSetting.clearPlayerMaxDelay;
		
		Ctrl.log("scene开始清空全部角色");
		
		checkClearOver();
	}
	
	private void checkClearOver()
	{
		if(ShineSetting.needDebugLog)
		{
			Ctrl.debugLog("scene清理玩家中",_players.size());
		}
		
		if(_players.isEmpty())
		{
			clearOver();
		}
		else
		{
			_players.forEachValueS(v->
			{
				playerExit(v.playerID);
			});
		}
	}
	
	private void clearOver()
	{
		if(_isClearing)
		{
			_isClearing=false;
			_clearPlayerTimeOut=0;
			
			Ctrl.debugLog("停服SceneMainControl清理玩家阶段完成");
			
			//TODO:场景持久化
			
			_clearOverFunc.run();
		}
	}
	
	/** 玩家退出 */
	public void playerExit(long playerID)
	{
		//InfoCodeType.PlayerExit_socketClose
		
		ScenePlayer player=getPlayerByID(playerID);
		
		if(player==null)
		{
			if(ShineSetting.needDebugLog)
			{
				player.debugLog("玩家退出时，找不到ScenePlayer");
			}
			
			return;
		}
		
		//已在退出中
		if(player.loginState==ScenePlayerLoginStateType.Exiting)
			return;
		
		player.loginState=ScenePlayerLoginStateType.Exiting;
		
		player.addFunc(()->
		{
			player.getExecutor().playerExit(player);
			
			player.addMainFunc(()->
			{
				playerExitLast(player);
			});
		});
	}
	
	/** 角色退出最后一步(主线程) */
	public void playerExitLast(ScenePlayer player)
	{
		player.log("scene角色退出场景服");
		
		//已删除过
		if(_players.remove(player.playerID)==null)
			return;
		
		SceneServerExitData data=player.createSceneServerExitData();
		long playerID=player.playerID;
		
		int gameID=player.gameID;
		
		_playersByToken.remove(player.token);
		
		if(player.socket!=null)
		{
			_playersBySocketID.remove(player.socket.id);
			
			player.socket.close();
			player.socket=null;
		}
		
		player.dispose();
		
		_scenePlayerPool.back(player);
		
		PlayerLeaveSceneOverToGameServerRequest.create(playerID,data).send(gameID);
	}
	
	/** 客户端连接断开(主线程) */
	public void clientSocketClosed(BaseSocket socket)
	{
		Ctrl.log("scene客户端连接断开",socket.id);
		
		ScenePlayer player=_playersBySocketID.get(socket.id);
		
		if(player==null)
		{
			//Ctrl.warnLog("客户端连接断开时找不到玩家");
			return;
		}
		
		//解绑socket
		player.socket.unbindPlayer();
		
		//退出中
		if(player.loginState==ScenePlayerLoginStateType.Exiting)
		{
			return;
		}
		
		player.log("客户端连接断开,下线");
		
		playerExit(player.playerID);
	}
}
