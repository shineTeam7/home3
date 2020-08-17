package com.home.commonGame.control;

import com.home.commonBase.config.game.SceneConfig;
import com.home.commonBase.config.game.enumT.TaskTypeConfig;
import com.home.commonBase.constlist.generate.FlowStepType;
import com.home.commonBase.constlist.generate.MailType;
import com.home.commonBase.constlist.generate.QuestType;
import com.home.commonBase.constlist.generate.SceneEnterConditionType;
import com.home.commonBase.constlist.generate.SceneInstanceType;
import com.home.commonBase.constlist.generate.SceneType;
import com.home.commonBase.control.LogicExecutorBase;
import com.home.commonBase.data.login.ClientLoginServerInfoData;
import com.home.commonBase.data.mail.MailData;
import com.home.commonBase.data.scene.scene.CreateSceneData;
import com.home.commonBase.data.scene.scene.SceneEnterArgData;
import com.home.commonBase.data.scene.scene.SceneLocationData;
import com.home.commonBase.data.system.PlayerWorkData;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.global.Global;
import com.home.commonBase.data.quest.AchievementData;
import com.home.commonBase.data.quest.TaskData;
import com.home.commonGame.global.GameC;
import com.home.commonGame.net.request.login.InitClientRequest;
import com.home.commonGame.net.request.login.SwitchSceneRequest;
import com.home.commonGame.net.request.scene.PreEnterSceneRequest;
import com.home.commonGame.net.serverRequest.scene.login.PlayerEnterServerSceneServerRequest;
import com.home.commonGame.net.serverRequest.scene.login.PlayerLeaveSceneToSceneServerRequest;
import com.home.commonGame.net.serverRequest.scene.login.PlayerSwitchToSceneServerRequest;
import com.home.commonGame.part.player.Player;
import com.home.commonGame.scene.base.GameScene;
import com.home.shine.control.ThreadControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.IndexMaker;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.collection.LongObjectMap;
import com.home.shine.support.func.ObjectCall;
import com.home.shine.support.pool.ObjectPool;
import com.home.shine.utils.MathUtils;

/** 逻辑线执行器 */
public class LogicExecutor extends LogicExecutorBase
{
	/** 角色字典(id为key)(在线) */
	private LongObjectMap<Player> _players=new LongObjectMap<>(Player[]::new);
	
	//场景部分
	/** 场景对象池 */
	private ObjectPool<GameScene>[] _scenePoolDic;
	
	/** 场景组(key:instanceID) */
	private IntObjectMap<GameScene> _scenes=new IntObjectMap<>(GameScene[]::new);
	/** 单例场景字典(key:sceneID) */
	private IntObjectMap<GameScene> _singleInstanceScenes=new IntObjectMap<>(GameScene[]::new);
	/** 分线单例场景字典(key:sceneID*LineNum+line) */
	private IntObjectMap<GameScene> _lineSingleInstanceScenes=new IntObjectMap<>(GameScene[]::new);
	/** 自动分线单例场景字典(key:sceneID*SceneAutoLineMax+line) */
	private IntObjectMap<GameScene> _autoLineSingleInstanceScenes=new IntObjectMap<>(GameScene[]::new);
	/** 场景流水ID生成 */
	private IndexMaker _sceneInstanceIDMaker=new IndexMaker(0,ShineSetting.indexMax,true);
	
	
	//逻辑部分
	/** 任务目标数据池 */
	private ObjectPool<TaskData>[] _taskDataPool;
	/** 邮件数据池 */
	private ObjectPool<MailData>[] _mailDataPool;
	/** 成就数据池 */
	public ObjectPool<AchievementData> achievementDataPool=new ObjectPool<>(AchievementData::new);
	
	/** 临时创建场景数据 */
	private CreateSceneData _createSceneData;
	
	public LogicExecutor(int index)
	{
		super(index);
	}
	
	/** 初始化(池线程) */
	@Override
	public void init()
	{
		super.init();
		
		_scenePoolDic=new ObjectPool[SceneType.size];
		
		for(int i=0;i<SceneType.size;++i)
		{
			(_scenePoolDic[i]=createScenePool(i)).setEnable(CommonSetting.sceneUsePool);
		}
		
		towerDataPool.setEnable(CommonSetting.sceneLogicUsePool);
		
		//逻辑部分
		
		TaskTypeConfig typeConfig;
		
		_taskDataPool=new ObjectPool[QuestType.size];
		(_taskDataPool[0]=createTaskDataPool(0)).setEnable(CommonSetting.logicUsePool);
		
		for(int i=0;i<_taskDataPool.length;++i)
		{
			if((typeConfig=TaskTypeConfig.get(i))!=null && typeConfig.needCustomTask)
			{
				(_taskDataPool[i]=createTaskDataPool(i)).setEnable(CommonSetting.logicUsePool);
			}
		}
		
		_mailDataPool=new ObjectPool[MailType.size];
		
		for(int i=0;i<_mailDataPool.length;++i)
		{
			(_mailDataPool[i]=createMailDataPool(i)).setEnable(CommonSetting.logicUsePool);
		}
		
		_createSceneData=BaseC.factory.createCreateSceneData();
	}
	
	private ObjectPool<GameScene> createScenePool(int type)
	{
		ObjectPool<GameScene> re=new ObjectPool<GameScene>(()->
		{
			GameScene scene=GameC.factory.createScene();
			scene.setType(type);
			scene.construct();
			return scene;
		},CommonSetting.scenePoolSize);
		
		return re;
	}
	
	private ObjectPool<TaskData> createTaskDataPool(int type)
	{
		ObjectPool<TaskData> re=new ObjectPool<TaskData>(()->
		{
			return BaseC.logic.createTaskData(type);
		});
		
		return re;
	}
	
	private ObjectPool<MailData> createMailDataPool(int type)
	{
		ObjectPool<MailData> re=new ObjectPool<MailData>(()->
		{
			return BaseC.logic.createMailData(type);
		});
		
		return re;
	}
	
	@Override
	protected void onFrame(int delay)
	{
		super.onFrame(delay);
		
		if(delay<=0)
			return;
		
		if(!_scenes.isEmpty())
		{
			GameScene[] values;
			GameScene v;
			
			for(int i=(values=_scenes.getValues()).length-1;i>=0;--i)
			{
				if((v=values[i])!=null)
				{
					try
					{
						v.onFrame(delay);
					}
					catch(Exception e)
					{
						Ctrl.errorLog(e);
					}
					
					if(v!=values[i])
					{
						++i;
					}
				}
			}
		}
	}
	
	@Override
	protected void onPiece(int delay)
	{
		super.onPiece(delay);
		
		if(!_players.isEmpty())
		{
			int index=_index;
			Player[] values;
			Player v;
			
			for(int i=(values=_players.getValues()).length-1;i>=0;--i)
			{
				if((v=values[i])!=null)
				{
					//当前执行器才执行
					if(v.system.isCurrentExecutor(index))
					{
						try
						{
							v.onPiece(delay);
						}
						catch(Exception e)
						{
							Ctrl.errorLog(e);
						}
					}
					
					if(v!=values[i])
					{
						++i;
					}
				}
			}
		}
	}
	
	@Override
	protected void onSecond(int delay)
	{
		super.onSecond(delay);
		
		if(!_players.isEmpty())
		{
			int index=_index;
			Player[] values;
			Player v;
			
			for(int i=(values=_players.getValues()).length-1;i>=0;--i)
			{
				if((v=values[i])!=null)
				{
					//当前执行器才执行
					if(v.system.isCurrentExecutor(index))
					{
						try
						{
							v.onSecond(delay);
						}
						catch(Exception e)
						{
							Ctrl.errorLog(e);
						}
					}
					
					if(v!=values[i])
					{
						++i;
					}
				}
			}
		}
		
	}
	
	/** 刷配置 */
	@Override
	public void onReloadConfig()
	{
		super.onReloadConfig();
		
		_scenes.forEachValue(v->
		{
			v.reloadConfig();
		});
		
		//玩家部分(在线)
		_players.forEachValue(v->
		{
			v.onReloadConfig();
		});
	}
	
	/** 添加自身带参回调 */
	public void addSelfFunc(ObjectCall<LogicExecutor> func)
	{
		addFunc(()->
		{
			func.apply(this);
		});
	}
	
	
	/** 获取玩家 */
	public Player getPlayer(long playerID)
	{
		return _players.get(playerID);
	}
	
	/** 获取角色数目 */
	public int getPlayerNum()
	{
		return _players.size();
	}
	
	/** 获取场景数目 */
	public int getSceneNum()
	{
		return _scenes.size();
	}
	
	/** 角色登录(与角色进入不同)(逻辑线程) */
	public void playerLogin(Player player)
	{
		//不是登录中状态
		if(!player.system.isStateLogining())
		{
			//中途被踢出
			Ctrl.warnLog("logicExecutor角色登录时，不是登录状态");
			return;
		}
		
		if(ShineSetting.openCheck)
		{
			if(_players.contains(player.role.playerID))
			{
				player.throwError("此时executor不该有角色");
			}
		}

		//添加到组
		_players.put(player.role.playerID,player);
		GameC.main.setExecutorPlayerOnlineNumDirty();
		player.system.setExecutor(this);

		try
		{
			//登录前
			player.beforeLogin();
			player.beforeEnter();
		}
		catch(Exception e)
		{
			player.errorLog(e);
		}
		
		//检查每日
		player.system.checkDaily();

		//推送客户端
		InitClientRequest request=InitClientRequest.create(player.makeClientListData(),BaseC.config.getHotfixBytes());
		
		player.system.setSocketReady(true);
		
		player.addFlowLog(FlowStepType.InitClient);
		
		//推送消息
		player.send(request);
		
		try
		{
			//推送完后的数据处理
			player.afterSendClientListData();
			
			
			player.onEnter();
			player.login();
		}
		catch(Exception e)
		{
			player.errorLog(e);
		}
		
		player.log("角色上线");
		
		//进入登录场景
		player.scene.enterLoginScene();
	}
	
	/** 角色重连登录(与角色进入不同)(逻辑线程) */
	public void playerReconnectLogin(Player player)
	{
		//不是在线中状态
		if(!player.system.isStateOnline())
		{
			//中途被踢出
			Ctrl.warnLog("logicExecutor角色重连登录时，不是在线状态");
			return;
		}
		
		//客户端离线事务
		player.system.flushClientOfflineWorks();
		
		//重连登陆前
		player.beforeReconnectLogin();
		
		//推送客户端
		InitClientRequest request=InitClientRequest.create(player.makeClientListData(),BaseC.config.getHotfixBytes());
		
		//开
		player.system.setSocketReady(true);
		
		//推送消息
		player.send(request);
		
		//推送完后的数据处理
		player.afterSendClientListData();
		
		player.log("角色重连上线");
		
		//重连登录
		player.onReconnectLogin();
		
		//重置连接
		player.scene.onReconnect();
		
		//不在切换中
		if(!player.scene.isSwitching())
		{
			if(player.scene.isInScene())
			{
				if(!CommonSetting.useSceneServer)
				{
					GameScene scene;
					
					if((scene=player.scene.getScene())!=null)
					{
						//快速进入场景
						scene.gameInOut.playerPreEnterForReconnect(player);
					}
				}
			}
			else
			{
				//进入登录场景
				player.scene.enterLoginScene();
			}
		}
		else
		{
			GameScene preScene=player.scene.getPreScene();
			
			//有预备场景
			if(preScene!=null)
			{
				preScene.gameInOut.playerPreEnterForReconnect(player);
			}
			else
			{
				//需要补发切换
				player.scene.setNeedSupplyPreEnterScene(true);
				return;
			}
		}
		
	}
	
	/** 角色离开(与角色退出不同)(逻辑线程) */
	public void playerExit(Player player)
	{
		toPlayerLeave(player,true);
	}
	
	protected void toPlayerLeave(Player player,boolean isExit)
	{
		if(ShineSetting.openCheck)
		{
			if(!_players.contains(player.role.playerID))
			{
				player.throwError("此时executor不该没有角色");
				return;
			}
		}
		
		try
		{
			if(!CommonSetting.useSceneServer)
			{
				GameScene scene=player.scene.getScene();
				
				if(scene!=null)
				{
					//退出时认为是还有下个场景
					scene.gameInOut.playerLeave(player,true);
				}
			}
			
			//清空func调用
			player.system.callAllFuncs();
			
			//离开
			player.onLeave();
			
			if(isExit)
			{
				player.onLogout();
			}
		}
		catch(Exception e)
		{
			player.errorLog(e);
		}
		
		_players.remove(player.role.playerID);
		GameC.main.setExecutorPlayerOnlineNumDirty();
		player.system.setExecutor(null);
	}
	
	/** 角色切换出game(与playerExit一致) */
	public void playerSwitchGameOut(Player player)
	{
		toPlayerLeave(player,false);
	}
	
	/** 角色切换进game(逻辑线程) */
	public void playerSwitchGameIn(Player player)
	{
		//不是登录中状态
		if(!player.system.isStateLogining())
		{
			//中途被踢出
			Ctrl.warnLog("logicExecutor切换进game时，不是登录状态");
			return;
		}
		
		if(ShineSetting.openCheck)
		{
			if(_players.contains(player.role.playerID))
			{
				player.throwError("此时executor不该有角色");
			}
		}
		
		//添加到组
		_players.put(player.role.playerID,player);
		GameC.main.setExecutorPlayerOnlineNumDirty();
		player.system.setExecutor(this);
		
		//登录前
		player.beforeEnter();

		//socket此时赋值
		player.system.setSocketReady(true);
		
		if(ShineSetting.needDebugLog)
			player.debugLog("角色切换到game");
		
		//进入
		player.onEnter();
		
		SceneEnterArgData currentSceneEnterArg;
		
		if((currentSceneEnterArg=player.scene.getPartData().currentSceneEnterArg)!=null)
		{
			//进入目标场景
			playerEnterSignedScene(player,currentSceneEnterArg);
		}
		else
		{
			//进登录场景
			player.scene.enterLoginScene();
		}
	}
	
	/** 玩家切出(逻辑线程) */
	protected void playerSwitchOut(Player player)
	{
		if(ShineSetting.openCheck)
		{
			if(!_players.contains(player.role.playerID))
			{
				player.throwError("此时executor不该没有角色");
				return;
			}
		}
		
		//添加到组
		_players.remove(player.role.playerID);
		GameC.main.setExecutorPlayerOnlineNumDirty();
		player.system.setExecutor(null);
		player.system.setSwitchingExecutor(true);
	}
	
	/** 玩家切入(逻辑线程) */
	protected void playerSwitchIn(Player player)
	{
		if(ShineSetting.openCheck)
		{
			if(_players.contains(player.role.playerID))
			{
				player.throwError("此时executor不该有角色");
			}
		}
		
		//添加到组
		_players.put(player.role.playerID,player);
		GameC.main.setExecutorPlayerOnlineNumDirty();
		player.system.setExecutor(this);
		player.system.setSwitchingExecutor(false);
		
	}
	
	//--场景部分--//
	
	/** 取一个单位实例ID */
	private int getSceneInstanceID()
	{
		int re;
		
		while(_scenes.contains(re=_sceneInstanceIDMaker.get()));
		
		return re;
	}
	
	/** 获取场景 */
	public GameScene getScene(int instanceID)
	{
		return _scenes.get(instanceID);
	}
	
	/** 创建场景(实际创建)(已init) */
	public GameScene createScene(int sceneID)
	{
		_createSceneData.sceneID=sceneID;
		return createScene(_createSceneData);
	}
	
	/** 创建场景(实际创建)(已init) */
	public GameScene createScene(CreateSceneData data)
	{
		GameScene scene=_scenePoolDic[SceneConfig.get(data.sceneID).type].getOne();
		
		//绑定执行器
		scene.setExecutor(this);
		scene.instanceID=getSceneInstanceID();
		Ctrl.print("添加场景",scene.instanceID);
		_scenes.put(scene.instanceID,scene);
		
		//初始化ID
		scene.initCreate(data);
		scene.init();
		
		return scene;
	}
	
	/** 删除场景 */
	public void removeScene(int instanceID)
	{
		GameScene scene=_scenes.remove(instanceID);
		
		if(scene==null)
		{
			Ctrl.throwError("未找到场景",instanceID);
			return;
		}
		
		SceneConfig config=scene.getConfig();
		
		switch(config.instanceType)
		{
			case SceneInstanceType.SingleInstance:
			{
				_singleInstanceScenes.remove(config.id);
			}
				break;
			case SceneInstanceType.LinedSingleInstance:
			{
				int key=config.id*Global.sceneLineNum+scene.getLineID();
				_lineSingleInstanceScenes.remove(key);
			}
				break;
			case SceneInstanceType.AutoLinedScene:
			{
				int key=config.id*CommonSetting.sceneAutoLineMax+scene.getLineID();
				_autoLineSingleInstanceScenes.remove(key);
			}
				break;
			default:
			{
			
			}
				break;
		}
		
		//析构
		scene.preDispose();
		
		_scenePoolDic[scene.getType()].back(scene);
	}
	
	/** 获取单例场景 */
	public GameScene getSingleInstanceScene(int sceneID)
	{
		GameScene scene=_singleInstanceScenes.get(sceneID);
		
		if(scene==null)
		{
			scene=createScene(sceneID);
			
			_singleInstanceScenes.put(sceneID,scene);
		}
		
		return scene;
	}
	
	/** 获取单例场景 */
	public GameScene getLineSingleInstanceScene(int sceneID,int lineID)
	{
		int key=sceneID*Global.sceneLineNum+lineID;
		
		int index;
		if((index=(key & ThreadControl.poolThreadNumMark))!=_index)
		{
			Ctrl.errorLog("获取分线单例场景时,散列值不对",_index,index);
			return null;
		}
		
		GameScene scene=_lineSingleInstanceScenes.get(key);
		
		if(scene==null)
		{
			scene=createScene(sceneID);
			scene.setLineID(lineID);
			
			_lineSingleInstanceScenes.put(key,scene);
		}
		
		return scene;
	}
	
	/** 获取自动分线单例场景 */
	public GameScene getAutoLinedSingleInstanceScene(int sceneID,int lineID)
	{
		int key=sceneID*CommonSetting.sceneAutoLineMax+lineID;
		
		int index;
		if((index=(key & ThreadControl.poolThreadNumMark))!=_index)
		{
			Ctrl.errorLog("获取自动分线单例场景时,散列值不对",_index,index);
			return null;
		}
		
		GameScene scene=_autoLineSingleInstanceScenes.get(key);
		
		if(scene==null)
		{
			scene=createScene(sceneID);
			scene.setLineID(lineID);
			
			_autoLineSingleInstanceScenes.put(key,scene);
		}
		
		return scene;
	}
	
	/** 获取指定场景执行器号 */
	private void makeSignedSceneExecutorIndex(Player player,SceneLocationData data)
	{
		SceneConfig config=SceneConfig.get(data.sceneID);
		
		switch(config.instanceType)
		{
			case SceneInstanceType.SingleInstance:
			{
				data.executorIndex=GameC.scene.getSingleInstanceSceneExecutorIndex(data.sceneID);
			}
				break;
			case SceneInstanceType.SinglePlayerBattle:
			{
				data.executorIndex=_index;
			}
				break;
			case SceneInstanceType.LinedSingleInstance:
			{
				data.executorIndex=GameC.scene.getLinedSingleInstanceSceneExecutorIndex(data.sceneID,data.lineID);
			}
				break;
			case SceneInstanceType.AutoLinedScene:
			{
				GameC.scene.makeAutoLinedSingleInstanceSceneLineID(player,data);
				if(data.executorIndex==-1)
				{
					data.executorIndex=GameC.scene.getAutoLinedSingleInstanceSceneExecutorIndexByLineID(data.sceneID,data.lineID);
				}
			}
				break;
			default:
			{
				Ctrl.throwError("其他类型必须指定场景执行器");
			}
		}
	}
	
	/** 玩家申请进入场景(已检查过条件)(逻辑线程)(本服场景) */
	public void playerApplyEnterScene(Player player,int sceneID,int lineID,int posID)
	{
		if(lineID!=-1)
		{
			if((lineID<0 || lineID>=Global.sceneLineNum))
			{
				player.warnLog("场景线号非法");
				return;
			}
			
			//同时直接设置线
			player.scene.setLineID(lineID);
		}
		
		SceneLocationData data=new SceneLocationData();
		data.sceneID=sceneID;
		data.lineID=lineID!=-1 ? lineID : player.scene.getLineID();
		
		SceneEnterArgData eData=player.scene.makeSceneEnterArg(data,posID);
		
		if(CommonSetting.isTestCenterTown)
		{
			//data.gameID=MathUtils.randomInt(3)+1;//随机gameID
			data.serverID=MathUtils.randomInt(2)+1;//随机gameID
		}
		
		playerEnterSignedScene(player,eData);
	}
	
	/** 角色进入指定场景(角色自身逻辑线程)(检查条件)(服务器用) */
	public void playerEnterSignedSceneAndCheck(Player player,SceneEnterArgData data,boolean isNext)
	{
		SceneConfig config=SceneConfig.get(data.location.sceneID);
		
		if(!player.role.checkRoleConditions(config.enterConditions,true))
		{
			player.warnLog("场景进入条件未达成");
			
			if(isNext)
			{
				player.scene.sceneMiss();
			}
			
			return;
		}
		
		if(isNext)
		{
			player.scene.setSwitching(false);
		}
		
		playerEnterSignedScene(player,data);
	}
	
	/** 角色进入指定场景(角色自身逻辑线程)(不检查条件)(服务器用) */
	public void playerEnterSignedScene(Player player,SceneLocationData data)
	{
		playerEnterSignedScene(player,player.scene.makeSceneEnterArgByLocation(data));
	}
	
	/** 角色进入指定场景(角色自身逻辑线程)(不检查条件)(服务器用) */
	public void playerEnterSignedScene(Player player,SceneEnterArgData data)
	{
		if(player.system.getExecutor()!=this)
		{
			Ctrl.throwError("不是当前执行器!");
			return;
		}
		
		if(player.scene.isSwitching())
		{
			//指定下个要进入的场景
			player.scene.setNextSceneLocation(data);
			return;
		}
		
		SceneLocationData location=data.location;
		
		//未指定
		if(location.instanceID==-1)
		{
			if(location.lineID==-1)
				location.lineID=player.scene.getLineID();
			
			//独立场景服的，由场景服自己处理
			if(location.executorIndex==-1 && !CommonSetting.useSceneServer)
			{
				makeSignedSceneExecutorIndex(player,location);
			}
		}
		//指定场景
		else
		{
			if(location.executorIndex==-1)
			{
				Ctrl.throwError("执行器ID为空");
				return;
			}
		}
		
		//游戏服ID
		int serverID=location.serverID;
		
		if(serverID==-1)
		{
			if(CommonSetting.useSceneServer)
			{
				serverID=getAvailableSceneServerID();
				//并且赋值
				data.location.serverID=serverID;
			}
			else
			{
				serverID=GameC.app.id;
			}
		}
		
		preEnterScene(player,data);
		leaveNowSceneForSwitch(player);
		player.scene.setCurrentEnterArg(data);
		
		if(serverID==-1)
		{
			Ctrl.errorLog("找到需要的场景服务器id");
			player.scene.sceneMiss();
			return;
		}
		
		if(CommonSetting.useSceneServer)
		{
			player.scene.doLeaveNowSceneAbs(()->
			{
				PlayerSwitchToSceneServerRequest.create(player.role.playerID,player.role.createRoleShowData(),data).send(location.serverID);
			});
		}
		else
		{
			//是本游戏服
			if(serverID==GameC.app.id)
			{
				preEnterSceneForCurrent(player,data);
				
				toEnterScene(player,data);
			}
			else
			{
				//--切换游戏服流程--//
				
				player.addMainFunc(()->
				{
					//切换到目标游戏服
					GameC.gameSwitch.playerSwitchToGame(player,location.serverID);
				});
			}
		}
	}
	
	/** 玩家切换到场景服下一步 */
	public void playerSwitchToSceneNext(Player player,int sceneServerID,int token,String host,int port)
	{
		//不在切换中
		if(!player.scene.isSwitching())
		{
			Ctrl.debugLog("玩家不在切换场景中1");
			return;
		}
		
		SceneEnterArgData currentEnterArg=player.scene.getCurrentEnterArg();
		SceneLocationData location=currentEnterArg.location;
		
		//场景服id不符合
		if(location.serverID!=-1 && location.serverID!=sceneServerID)
		{
			Ctrl.errorLog("玩家切换场景时，sceneServerID不符合");
			player.scene.sceneMiss();
			return;
		}
		
		player.send(SwitchSceneRequest.create(ClientLoginServerInfoData.create(token,host,port)));
	}
	
	/** 玩家切换到场景服第三部(SceneServer用) */
	public void playerSwitchToSceneThird(Player player,SceneLocationData location)
	{
		//不在切换中
		if(!player.scene.isSwitching())
		{
			Ctrl.debugLog("玩家不在切换场景中2");
			return;
		}
		
		SceneEnterArgData currentEnterArg=player.scene.getCurrentEnterArg();
		SceneLocationData oldLocation=currentEnterArg.location;
		
		//场景服id不符合
		if(oldLocation.serverID!=-1 && oldLocation.serverID!=location.serverID)
		{
			Ctrl.errorLog("玩家切换场景时，sceneServerID不符合2");
			player.scene.sceneMiss();
			return;
		}
		
		player.scene.setSwitching(false);
		//绑定location
		player.scene.setSceneLocation(location);
		
		SceneEnterArgData nextData;
		
		if((nextData=player.scene.getNextEnterSceneLocation())!=null)
		{
			player.warnLog("切换场景时,执行下一个进场景请求");
			
			player.scene.setNextSceneLocation(null);
			
			if(GameC.scene.isLeaveSceneEnterArg(nextData))
			{
				//直接发送离开
				PlayerLeaveSceneToSceneServerRequest.create(player.role.playerID).send(location.serverID);
			}
			else
			{
				playerEnterSignedSceneAndCheck(player,nextData,true);
			}
			
			return;
		}
		
		PlayerEnterServerSceneServerRequest.create(player.role.playerID,player.scene.createEnterServerSceneData()).send(location.serverID);
	}

	/** 获取一个可用的场景服ID */
	private int getAvailableSceneServerID()
	{
		return GameC.main.getLeastSceneServerID();
	}
	
	/** 推送预进入场景 */
	private void preEnterScene(Player player,SceneEnterArgData data)
	{
		player.send(PreEnterSceneRequest.create(data.location.sceneID,data.location.lineID));
	}
	
	/** 本服的预备进入 */
	private void preEnterSceneForCurrent(Player player,SceneEnterArgData data)
	{
		if(SceneConfig.get(data.location.sceneID).instanceType==SceneInstanceType.AutoLinedScene)
		{
			long playerID=player.role.playerID;
			int sceneID=data.location.sceneID;
			int lineID=data.location.lineID;
			
			player.addMainFunc(()->
			{
				GameC.scene.addAutoLinedScenePreOne(playerID,sceneID,lineID);
			});
		}
	}
	
	//private void pre
	
	/** 玩家离开旧场景 */
	private void leaveNowSceneForSwitch(Player player)
	{
		//切换中
		player.scene.setSwitching(true);
		
		if(!CommonSetting.useSceneServer)
		{
			//离开当前场景
			GameScene nowScene=player.scene.getScene();
			
			if(nowScene!=null)
			{
				if(ShineSetting.openCheck)
				{
					if(nowScene.getGameExecutor()!=this)
					{
						Ctrl.throwError("当前场景不在当前执行器");
						return;
					}
				}
				
				if(ShineSetting.needDebugLog)
					player.debugLog("toSwitchScene,离开旧场景");
				//退出当前场景
				nowScene.gameInOut.playerLeave(player,true);
			}
		}
		
		player.scene.clearPreEnterScene();
	}
	
	/** 执行本服的指定场景进入 */
	private void toEnterScene(Player player,SceneEnterArgData data)
	{
		//是当前线程
		if(_index==data.location.executorIndex)
		{
			toEnterSceneNext(player,data);
		}
		else
		{
			//切出
			playerSwitchOut(player);
			
			LogicExecutor targetExecutor=GameC.main.getExecutor(data.location.executorIndex);
			
			targetExecutor.addFunc(()->
			{
				//切入
				targetExecutor.playerSwitchIn(player);
				
				targetExecutor.toEnterSceneNext(player,data);
			});
		}
	}
	
	/** 执行进入场景,已在对应执行器时 */
	protected void toEnterSceneNext(Player player,SceneEnterArgData data)
	{
		SceneEnterArgData nextData;
		
		if((nextData=player.scene.getNextEnterSceneLocation())!=null)
		{
			player.warnLog("切换场景时,执行下一个进场景请求");
			
			player.scene.setNextSceneLocation(null);
			playerEnterSignedSceneAndCheck(player,nextData,true);
			return;
		}
		
		if(ShineSetting.openCheck)
		{
			if(player.scene.getScene()!=null)
			{
				player.throwError("此时不该还有旧场景!");
			}
		}
		
		SceneLocationData location=data.location;
		
		GameScene scene;
		
		if(location.instanceID!=-1)
		{
			scene=getScene(location.instanceID);
		}
		else
		{
			int sceneID=location.sceneID;
			int lineID=location.lineID!=-1 ? location.lineID : player.scene.getLineID();
			
			SceneConfig config=SceneConfig.get(sceneID);
			
			switch(config.instanceType)
			{
				case SceneInstanceType.SingleInstance:
				{
					scene=getSingleInstanceScene(sceneID);
				}
					break;
				case SceneInstanceType.LinedSingleInstance:
				{
					scene=getLineSingleInstanceScene(sceneID,lineID);
				}
					break;
				case SceneInstanceType.SinglePlayerBattle:
				{
					scene=createScene(sceneID);
					scene.gameInOut.setSignedPlayerIDs(new long[]{player.role.playerID});
				}
					break;
				case SceneInstanceType.AutoLinedScene:
				{
					scene=getAutoLinedSingleInstanceScene(sceneID,lineID);
				}
					break;
				default:
				{
					player.throwError("场景实例ID为空时,不支持的场景实例类型");
					return;
				}
			}
		}
		
		//场景不存在了
		if(scene==null)
		{
			player.scene.sceneMiss();
			return;
		}
		
		//玩家进入
		scene.gameInOut.playerPreEnter(player);
	}
	
	protected boolean checkOneEnterConditions(Player player,int[] conditions)
	{
		boolean re=false;
		
		switch(conditions[0])
		{
			case SceneEnterConditionType.Level:
			{
				re=true;
			}
			break;
		}
		
		return re;
	}
	
	//逻辑部分
	
	/** 创建任务目标数据 */
	public TaskData createTaskData(int type)
	{
		if(TaskTypeConfig.get(type).needCustomTask)
		{
			return _taskDataPool[type].getOne();
		}
		else
		{
			return _taskDataPool[0].getOne();
		}
	}
	
	/** 回收任务目标数据 */
	public void releaseTaskData(int type,TaskData data)
	{
		if(TaskTypeConfig.get(type).needCustomTask)
		{
			_taskDataPool[type].back(data);
		}
		else
		{
			_taskDataPool[0].back(data);
		}
	}
	
	/** 创建邮件数据 */
	public MailData createMailData(int type)
	{
		return _mailDataPool[type].getOne();
	}
	
	/** 回收邮件数据 */
	public void releaseMailData(int type,MailData data)
	{
		_mailDataPool[type].back(data);
	}
	
	/** 添加角色事务 */
	@Override
	public void addPlayerWork(int type,long playerID,PlayerWorkData data)
	{
		Player player;
		//当前线程
		if((player=getPlayer(playerID))!=null)
		{
			player.system.executeWork(data);
			return;
		}
		
		GameC.main.addPlayerWork(type,playerID,data);
	}
}
