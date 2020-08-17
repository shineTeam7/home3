package com.home.commonScene.scene.scene;

import com.home.commonBase.config.game.FightUnitConfig;
import com.home.commonBase.config.game.enumT.SceneInstanceTypeConfig;
import com.home.commonBase.constlist.generate.SceneForceType;
import com.home.commonBase.constlist.scene.SceneAOIType;
import com.home.commonBase.data.role.RoleShowData;
import com.home.commonBase.data.scene.base.PosDirData;
import com.home.commonBase.data.scene.role.SceneRoleData;
import com.home.commonBase.data.scene.scene.SceneEnterData;
import com.home.commonBase.data.scene.scene.ScenePreInfoData;
import com.home.commonBase.data.scene.unit.UnitAIData;
import com.home.commonBase.data.scene.unit.UnitAvatarData;
import com.home.commonBase.data.scene.unit.UnitData;
import com.home.commonBase.data.scene.unit.UnitFightData;
import com.home.commonBase.data.scene.unit.UnitFightExData;
import com.home.commonBase.data.scene.unit.UnitMoveData;
import com.home.commonBase.data.scene.unit.UnitSimpleData;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.global.Global;
import com.home.commonBase.scene.base.Role;
import com.home.commonBase.scene.base.Scene;
import com.home.commonBase.scene.base.Unit;
import com.home.commonScene.logic.unit.SCharacterUseLogic;
import com.home.commonScene.logic.unit.SMUnitFightDataLogic;
import com.home.commonScene.logic.unit.SMUnitUseLogic;
import com.home.commonScene.net.sceneRequest.scene.EnterSceneForSceneRequest;
import com.home.commonScene.net.sceneRequest.scene.LeaveSceneForSceneRequest;
import com.home.commonScene.net.sceneRequest.scene.PreEnterSceneForSceneRequest;
import com.home.commonScene.net.sceneRequest.scene.PreEnterSceneNextForSceneRequest;
import com.home.commonScene.part.ScenePlayer;
import com.home.commonScene.scene.base.SScene;
import com.home.commonSceneBase.part.IScenePlayer;
import com.home.commonSceneBase.scene.scene.BSceneInoutLogic;
import com.home.commonSceneBase.scene.unit.CharacterIdentityLogic;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.collection.IntSet;
import com.home.shine.support.collection.LongObjectMap;
import com.home.shine.support.collection.SList;
import com.home.shine.support.collection.inter.IObjectConsumer;
import com.home.shine.utils.ObjectUtils;

/** 场景进出逻辑 */
public class SSceneInoutLogic extends BSceneInoutLogic
{
	protected SScene _sScene;
	
	/** 玩家字典(当前实际存在的玩家) */
	protected LongObjectMap<ScenePlayer> _playerDic=new LongObjectMap<>();
	
	/** 保留时间tick */
	private int _keepTimeTick=-1;
	
	//signed
	/** 指定进入玩家List */
	private long[] _signedPlayerIDList;
	/** 指定单位组 */
	private RoleShowData[] _signedUnits;
	
	//--robot--//
	/** 角色使用逻辑字典 */
	private LongObjectMap<SCharacterUseLogic> _logicDic;
	
	@Override
	public void setScene(Scene scene)
	{
		super.setScene(scene);
		
		_sScene=(SScene)scene;
	}
	
	@Override
	public void construct()
	{
		super.construct();
	}
	
	@Override
	public void init()
	{
		super.init();
		
		refreshKeepTime();
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		
		//当前场景的再清理一次
		_scene.getCharacterDic().forEachValueS(v->
		{
			ScenePlayer player=(ScenePlayer)((CharacterIdentityLogic)v.identity).getPlayer();
			
			if(player!=null)
			{
				player.leaveNowScene();
			}
		});
		
		_keepTimeTick=-1;
		
		_signedUnits=null;
		_signedPlayerIDList=null;
		_logicDic=null;
	}
	
	@Override
	public void onFrame(int delay)
	{
		super.onFrame(delay);
	}
	
	@Override
	public void onSecond(int delay)
	{
		super.onSecond(delay);
	}
	
	@Override
	public void onSecondNoMatterPause()
	{
		if(_keepTimeTick>0)
		{
			if(--_keepTimeTick<=0)
			{
				_keepTimeTick=-1;
				
				keepTimeOut();
			}
		}
	}
	
	private void refreshKeepTime()
	{
		_keepTimeTick=Global.sceneKeepTime;
	}
	
	/** 到时间 */
	private void keepTimeOut()
	{
		_sScene.removeScene();
	}
	
	protected ScenePreInfoData toCreateScenePreInfoData()
	{
		return new ScenePreInfoData();
	}
	
	/** 检查某玩家是否可进入 */
	public boolean checkCanEnter(ScenePlayer player)
	{
		//指定进入的
		if(_signedPlayerIDList!=null)
		{
			return ObjectUtils.longArrayIndexOf(_signedPlayerIDList,player.playerID)!=-1;
		}
		
		return true;
	}
	
	/** 检查玩家是否可离开 */
	public boolean checkCanLeave(ScenePlayer player)
	{
		return true;
	}
	
	/** 玩家预进入前 */
	protected void beforePlayerPreEnter(ScenePlayer player)
	{
	
	}
	
	/** 玩家进入前 */
	protected void beforePlayerEnter(ScenePlayer player)
	{
	
	}
	
	/** 玩家角色进入 */
	protected void onPlayerEnter(ScenePlayer player,Unit unit)
	{
		_keepTimeTick=-1;
	}
	
	/** 玩家角色离开 */
	protected void onPlayerLeave(ScenePlayer player,Unit unit)
	{
		//指定进入的
		if(_signedPlayerIDList!=null)
		{
			SCharacterUseLogic logic;
			
			if(_logicDic!=null)
			{
				for(long v:_signedPlayerIDList)
				{
					logic=_logicDic.get(v);
					
					//自己控制的机器人
					if(logic.isRobot() && logic.getControlPlayer().playerID==player.playerID)
					{
						//logic.setControlPlayer(null);
					}
				}
			}
		}
	}
	
	/** 玩家离开后 */
	protected void afterPlayerLeave(ScenePlayer player)
	{
		//没人了
		if(_playerDic.isEmpty())
		{
			//就绪 并且 需要立刻移除
			if(this.ready && SceneInstanceTypeConfig.get(_scene.getConfig().instanceType).removeAfterPlayerAllLeave)
			{
				//移除当前场景
				_sScene.removeScene();
			}
			else
			{
				refreshKeepTime();
			}
		}
	}
	
	@Override
	public void onAddUnit(Unit unit)
	{
	
	}
	
	@Override
	public void onRemoveUnit(Unit unit)
	{
	
	}
	
	/** 获取该场景的玩家 */
	public ScenePlayer getPlayer(long playerID)
	{
		return _playerDic.get(playerID);
	}
	
	/** 获取玩家字典 */
	public LongObjectMap<ScenePlayer> getPlayerDic()
	{
		return _playerDic;
	}
	
	/** 获取玩家数 */
	public int getPlayerNum()
	{
		return _playerDic.length();
	}
	
	/** 清空全部玩家 */
	public void clearAllPlayer()
	{
		if(!_playerDic.isEmpty())
		{
			_playerDic.forEachValueS(v->
			{
				v.leaveNowScene();
			});
		}
	}
	
	/** 添加角色到组 */
	protected void addPlayerToGroup(LongObjectMap<LongObjectMap<Unit>> groupDic,long groupID,Unit unit)
	{
		groupDic.computeIfAbsent(groupID,k->new LongObjectMap<>(Unit[]::new)).put(unit.identity.playerID,unit);
	}
	
	/** 从组中移除角色 */
	protected void removePlayerFromGroup(LongObjectMap<LongObjectMap<Unit>> groupDic,long groupID,Unit unit)
	{
		LongObjectMap<Unit> dic=groupDic.get(groupID);
		
		if(dic!=null)
		{
			dic.remove(unit.identity.playerID);
			
			if(dic.isEmpty())
			{
				groupDic.remove(groupID);
			}
		}
		else
		{
			Ctrl.errorLog("不该找不到队伍角色字典");
		}
	}
	
	/** 添加到队伍角色 */
	public void toAddTeamPlayer(Unit unit,long groupID)
	{
		LongObjectMap<Unit> dic=_playerTeamUnitDic.computeIfAbsent(groupID,k->new LongObjectMap<>(Unit[]::new));
		
		if(!dic.isEmpty())
		{
			dic.forEachValue(v->
			{
				unit.aoi.bindEachOther(v);
			});
		}
		
		dic.put(unit.identity.playerID,unit);
	}
	
	/** 添加到队伍角色 */
	public void toRemoveTeamPlayer(Unit unit,long groupID)
	{
		LongObjectMap<Unit> dic=_playerTeamUnitDic.get(groupID);
		
		if(dic!=null)
		{
			dic.remove(unit.identity.playerID);
			
			if(dic.isEmpty())
			{
				_playerTeamUnitDic.remove(groupID);
			}
			else
			{
				dic.forEachValue(v->
				{
					unit.aoi.unBindEachOther(v);
				});
			}
		}
		else
		{
			Ctrl.errorLog("不该找不到队伍角色字典");
		}
	}
	
	//--流程部分--//
	
	/** 玩家预备进入场景 */
	public void playerPreEnter(ScenePlayer player)
	{
		if(!checkCanEnter(player))
		{
			player.log("场景不可进入pre");
			player.sceneMiss();
			return;
		}
		
		if(!_sScene.bMethod.checkCanEnter(player))
		{
			player.log("场景不可进入pre,forPlay");
			player.sceneMiss();
			return;
		}
		
		player.setPreScene(_sScene);
		
		if(player.needSupplyPreEnterScene())
		{
			player.setNeedSupplyPreEnterScene(false);
			player.send(PreEnterSceneForSceneRequest.create(_scene.getSceneID(),_scene.getLineID()));
		}
		
		//预进入前
		beforePlayerPreEnter(player);
		
		ScenePreInfoData info=toCreateScenePreInfoData();
		
		makePreInfo(player,info);
		
		player.send(PreEnterSceneNextForSceneRequest.create(info));
		
		player.checkPreEnter();
	}
	
	/** 玩家预备进入(重连登录) */
	public void playerPreEnterForReconnect(ScenePlayer player)
	{
		player.setPreScene(_sScene);
		
		//当前有场景,标记重连进入
		if(player.getScene()!=null)
		{
			player.setReconnectEnter(true);
		}
		
		//标记切换中
		player.setSwitching(true);
		
		ScenePreInfoData info=toCreateScenePreInfoData();
		
		makePreInfo(player,info);
		
		//连发
		player.send(PreEnterSceneForSceneRequest.create(_scene.getSceneID(),_scene.getLineID()));
		player.send(PreEnterSceneNextForSceneRequest.create(info));
		
		player.checkPreEnter();
	}
	
	/** 构建预备进入数据 */
	protected void makePreInfo(ScenePlayer player,ScenePreInfoData infoData)
	{
		if(_signedUnits!=null)
		{
			infoData.signedPlayers=_signedUnits;
		}
	}
	
	/** 初始化场景角色数据 */
	protected void makeRoleData(SceneRoleData data,ScenePlayer player)
	{
	
	}
	
	protected void updateRole(Role role,ScenePlayer player)
	{
	
	}
	
	/** 玩家进入场景 */
	public void playerEnter(ScenePlayer player)
	{
		if(!checkCanEnter(player))
		{
			player.warnLog("场景不可进入enter");
			player.sceneMiss();
			return;
		}
		
		//绑定场景
		player.setScene(_sScene);
		
		//进入前
		beforePlayerEnter(player);
		
		if(_scene.role.needRole())
		{
			Role role=_scene.role.getRole(player.playerID);
			
			//不存在
			if(role==null)
			{
				SceneRoleData roleData=createRoleDataByShowData(player.createRoleShowData());
				roleData.force.force=SceneForceType.Player;
				makeRoleData(roleData,player);
				
				role=_scene.role.addRole(roleData);
			}
			else
			{
				updateRole(role,player);
			}
			
			player.setRole(role);
		}
		
		long playerID=player.playerID;
		
		//加入玩家组
		_playerDic.put(playerID,player);
		
		//添加主角单位
		UnitData uData=createCharacterData(player);
		Unit unit=null;
		
		if(uData!=null)
		{
			//添加
			unit=_scene.toAddUnit(uData);
			
			if(unit==null)
			{
				player.warnLog("添加玩家单位失败");
				player.sceneMiss();
				return;
			}
			
			//连接暂停
			unit.identity.socketReady=false;
			
			CharacterIdentityLogic cIdentity=(CharacterIdentityLogic)unit.identity;
			
			//角色身份逻辑绑定单位
			cIdentity.setPlayer(player);
			
			//激活
			_scene.toActiveUnit(unit,false);
			
			player.setUnit(unit);
			
			//连接恢复
			unit.identity.socketReady=true;
		}
		
		sendEnterScene(player);
		
		onPlayerEnter(player,unit);
		
		_sScene.bMethod.onPlayerEnter(player,unit);
	}
	
	/** 玩家进入场景(重连进入) */
	public void playerEnterForReconnect(ScenePlayer player)
	{
		Unit unit=_scene.getCharacterByPlayerID(player.playerID);
		
		if(unit==null)
		{
			player.warnLog("重连进场景时,找不到主角色");
			player.sceneMiss();
			return;
		}
		
		IntSet controlUnits=((CharacterIdentityLogic)unit.identity).getControlUnits();
		//所有控制单位
		controlUnits.forEachA(v->
		{
			Unit tempU;
			if((tempU=_scene.getUnit(v))!=null)
			{
				//预备重连进入
				tempU.fight.preReconnectEnter();
				//socket标记
				tempU.makeSocketDirty();
			}
		});
		
		//推客户端
		sendEnterScene(player);
	}
	
	/** 推送进入场景 */
	protected void sendEnterScene(ScenePlayer player)
	{
		Ctrl.log("进入场景",player.name, player.playerID,  _sScene.getConfig().id, "位置:", player.enterArg.posID, " 场景实例ID:",_sScene.instanceID);
		
		//推客户端
		SceneEnterData enterData=_sScene.bMethod.createSceneEnterData();
		
		_sScene.battle.makeSceneEnterData(enterData);
		_sScene.bMethod.makeSceneEnterData(player,enterData);
		
		//完整场景
		boolean isSimple=_scene.isSimple();
		
		long playerID=player.playerID;
		Unit unit=player.getUnit();
		
		if(unit!=null)
		{
			unit.beforeWrite();
			
			if(!isSimple)
			{
				unit.fight.switchSend(playerID);
			}
			
			enterData.hero=unit.getUnitData();
			
			//simple单位组
			IntObjectMap<Unit> bindVisionUnits=unit.aoi.getBindVisionUnits();
			
			if(!bindVisionUnits.isEmpty())
			{
				enterData.bindVisionUnits=new IntObjectMap<>(UnitSimpleData[]::new);
				
				Unit[] values;
				Unit v;
				
				for(int i=(values=bindVisionUnits.getValues()).length-1;i>=0;--i)
				{
					if((v=values[i])!=null)
					{
						if(unit.aoi.isSee(v))
						{
							enterData.bindVisionUnits.put(v.instanceID,null);
						}
						else
						{
							enterData.bindVisionUnits.put(v.instanceID,v.createSimpleUnitData());
						}
					}
				}
			}
		}
		
		SList<UnitData> dataList=new SList<>();
		
		IObjectConsumer<Unit> func=v->
		{
			//不是自己
			if(v!=unit)
			{
				v.beforeWrite();
				
				if(v.canFight())
				{
					v.fight.switchSend(playerID);
				}
				
				dataList.add(v.getUnitData());
				
				if(unit!=null)
				{
					unit.aoi.recordAddUnit(v.instanceID);
				}
			}
		};
		
		if(unit!=null)
		{
			unit.aoi.clearMsg();
			unit.aoi.forEachCanSeeUnits(func);
			
			//自己也记录
			unit.aoi.recordAddUnit(unit.instanceID);
		}
		else
		{
			//目前无角色只支持全可见模式
			if(_scene.aoi.getAOIType()==SceneAOIType.All)
			{
				_scene.getUnitDic().forEachValue(func);
			}
			else
			{
				Ctrl.throwError("不支持无角色的，其他AOI模式");
			}
		}
		
		enterData.units=dataList;
		
		LongObjectMap<SceneRoleData> roleDic=new LongObjectMap<>(SceneRoleData[]::new);
		
		if(_scene.role.needRole())
		{
			_scene.role.getRoleDic().forEachValue(v->
			{
				roleDic.put(v.playerID,v.getData());
			});
		}
		
		enterData.roles=roleDic;
		
		enterData.selfBindFieldItemBags=_scene.role.getSelfFieldItemBagDic(playerID);
		
		if(unit!=null)
		{
			//连接恢复
			unit.identity.socketReady=true;
		}
		
		player.send(EnterSceneForSceneRequest.create(enterData));
		
		if(!isSimple)
		{
			if(unit!=null)
			{
				//还原主角
				unit.fight.endSwitchSend();
			}
			
			//还原
			dataList.forEach(k->
			{
				//可战斗
				if(BaseC.constlist.unit_canFight(k.identity.type))
				{
					k.fightDataLogic.endSwitchSend();
				}
			});
		}
	}
	
	/** 玩家离开场景(只离开当前场景) */
	public void playerLeave(ScenePlayer player)
	{
		Unit unit=player.getUnit();
		
		if(unit==null)
		{
			player.warnLog("未找到角色单位");
		}
		
		onPlayerLeave(player,unit);
		
		//不是简单也不是限制场景
		if(!_scene.isSimple() && !_scene.isFinite())
		{
			if(unit!=null)
			{
				SMUnitFightDataLogic dataLogic=(SMUnitFightDataLogic)unit.fight.getDataLogic();
				
				//清空当前场景部分
				dataLogic.clearDataForCurrentScene();
			}
		}
		
		_sScene.bMethod.onPlayerLeave(player,unit);
		
		//移除玩家组
		_playerDic.remove(player.playerID);
		
		
		if(unit!=null)
		{
			_scene.toRemoveUnit(unit,false);
		}
		
		Role role=player.getRole();
		
		if(role!=null)
		{
			_scene.role.toRemoveRole(role);
		}
		
		//当前值置空
		player.clearNowScene();
		
		//离开场景
		player.send(LeaveSceneForSceneRequest.create());
		
		afterPlayerLeave(player);
	}
	
	/** 构造进入单位数据 */
	protected void makeEnterUnitData(UnitData data,ScenePlayer player)
	{
		//给予一个流水
		data.instanceID=_scene.getUnitInstanceID();
		
		//完整场景
		boolean isSimple=_scene.isSimple();
		
		if(!isSimple)
		{
			
			int posID=player!=null ? player.enterArg.posID : -1;
			
			_sScene.bMethod.makeScenePosData(data,posID);
		}
		
		//机器人不处理
		if(player!=null)
		{
			_sScene.bMethod.makeCharacterData(player,data);
		}
	}
	
	/** 创建角色单位数据(可能为空) */
	private UnitData createCharacterData(ScenePlayer player)
	{
		Unit unit=player.getUnit();
		
		if(unit!=null)
		{
			player.throwError("角色单位已存在");
		}
		
		UnitData data=createUnitDataByMUnitUseLogic(player.useLogic);
		makeEnterUnitData(data,player);
		return data;
	}
	
	/** 通过主单位使用数据创建单位数据 */
	protected UnitData createUnitDataByMUnitUseLogic(SMUnitUseLogic logic)
	{
		//构造符合主角的数据
		UnitData data=BaseC.factory.createUnitData();
		
		//当前身份
		data.identity=logic.createIdentityData();
		
		//完整场景
		if(!_scene.isSimple())
		{
			//位置数据出去赋值
			_scene.unitFactory.initBaseUnitData(data);
			
			(data.move=new UnitMoveData()).initDefault();
			//战斗附加数据
			data.fightEx=new UnitFightExData();
			//AI数据
			data.ai=new UnitAIData();
			
			if(!_scene.isFinite())
			{
				//取角色的数据和数据逻辑
				data.fight=logic.getData().fight;
				data.avatar=logic.getData().avatar;
				data.fightDataLogic=logic.getFightLogic();
				////绑定场景
				//data.fightDataLogic.setScene(_scene);
			}
			else
			{
				//拷贝数据
				data.fight=(UnitFightData)logic.getData().fight.clone();
				data.avatar=(UnitAvatarData)logic.getData().avatar.clone();
				
				data.fightDataLogic=_scene.getExecutor().createUnitFightDataLogic();
				////绑定场景
				//data.fightDataLogic.setScene(_scene);
				//绑定数据
				data.fightDataLogic.setData(data.fight,data.avatar);
			}
		}
		
		return data;
	}
	
	//--signed--//
	/** 设定指定角色组(多人用) */
	public void setSignedPlayers(RoleShowData[] units)
	{
		_signedUnits=units;
		long[] playerIDs=new long[units.length];
		
		RoleShowData data;
		
		for(int i=units.length-1;i>=0;--i)
		{
			data=units[i];
			
			playerIDs[i]=data.playerID;
			
			if(_scene.role.needRole())
			{
				_scene.role.addRoleByShowData(data);
			}
		}
		
		setSignedPlayerIDs(playerIDs);
	}
	
	/** 只设置指定角色组(单人用) */
	public void setSignedPlayerIDs(long[] playerIDs)
	{
		_signedPlayerIDList=playerIDs;
		
		_sScene.bMethod.endSigned();
	}
	
	/** 设置绑定角色逻辑组(客户端机器人AI用)(包括自己) */
	public void setSignedLogics(SCharacterUseLogic[] logics)
	{
		_logicDic=new LongObjectMap<>();
		
		int i=0;
		RoleShowData[] signedUnits=new RoleShowData[logics.length];
		
		for(SCharacterUseLogic v:logics)
		{
			_logicDic.put(v.getPlayerID(),v);
			
			signedUnits[i++]=v.createRoleShowData();
		}
		
		//设置指定角色组
		setSignedPlayers(signedUnits);
	}
	
	/** 获取角色逻辑 */
	public SCharacterUseLogic getCharacterLogic(long playerID)
	{
		if(_logicDic==null)
			return null;
		
		return _logicDic.get(playerID);
	}
	
	/** 是否是机器人角色 */
	public boolean isRobot(long playerID)
	{
		SCharacterUseLogic logic=getCharacterLogic(playerID);
		
		if(logic==null)
			return false;
		
		return logic.isRobot();
	}
	
	/** 创建机器人角色 */
	public SCharacterUseLogic createUseLogicForRobot(ScenePlayer player)
	{
		Ctrl.throwError("需要实现");
		return null;
	}
	
	/** 获取指定进入玩家IDList */
	public long[] getSignedPlayerIDList()
	{
		return _signedPlayerIDList;
	}
	
	/** 获取指定单位组 */
	public RoleShowData[] getSignedUnits()
	{
		return _signedUnits;
	}
	
	/** 获取玩家序号 */
	public int getPlayerIndex(long playerID)
	{
		for(int i=_signedPlayerIDList.length-1;i>=0;--i)
		{
			if(_signedPlayerIDList[i]==playerID)
				return i;
		}
		
		return -1;
	}
	
	/** 玩家是否都进入完毕() */
	@Override
	public boolean isPlayerAllExist()
	{
		if(_signedPlayerIDList!=null)
		{
			//改用角色字典,除非以后有多角色的需求
			return _scene.getCharacterDic().size()==_signedPlayerIDList.length;
			//return getPlayerDic().size()==_signedPlayerIDList.length;
		}
		
		return true;
	}
	
	/** 通过主角添加主单位 */
	public Unit addMUnitByMaster(Unit master,SMUnitUseLogic logic,boolean needSendSelf)
	{
		UnitData data=createMUnitData(logic);
		//朝向一致
		data.pos.dir.copyDir(master.pos.getDir());
		
		FightUnitConfig unitConfig=data.getFightIdentity().getFightUnitConfig();
		
		if(unitConfig.followRadius>0)
			_scene.pos.getRandomWalkablePos(unitConfig.mapMoveType,data.pos.pos,master.pos.getPos(),unitConfig.followRadius);
		else
			data.pos.pos.copyPos(master.pos.getPos());
		
		Unit unit=_scene.toAddUnit(data);
		
		_scene.toActiveUnit(unit,needSendSelf);
		
		logic.unit=unit;
		
		master.aoi.bindEachOther(unit);
		return unit;
	}
	
	/** 创建M单位数据 */
	public UnitData createMUnitData(SMUnitUseLogic logic)
	{
		UnitData data=createUnitDataByMUnitUseLogic(logic);
		//给予一个流水
		data.instanceID=_scene.getUnitInstanceID();
		
		return data;
	}
	
	/** 创建机器人角色数据(不包括自己) */
	public UnitData createCharacterDataForRobot(long playerID)
	{
		SCharacterUseLogic logic=_logicDic.get(playerID);
		
		UnitData data=createUnitDataByMUnitUseLogic(logic);
		makeEnterUnitData(data,null);
		return data;
	}
	
	/** 通过自身数据创建一个机器人数据 */
	public RoleShowData createRobotRoleShowData(RoleShowData selfData)
	{
		RoleShowData re=BaseC.factory.createRoleShowData();
		re.playerID=getRobotPlayerID();
		re.createAreaID=selfData.createAreaID;
		//TODO:随机个好名字
		re.name="robot_"+getCurrentRobotPlayerIndex();
		re.sex=selfData.sex;
		re.fightForce=selfData.fightForce;
		re.level=selfData.level;
		
		makeRobotRoleShowData(re,selfData);
		return re;
	}
	
	protected void makeRobotRoleShowData(RoleShowData re,RoleShowData selfData)
	{
	
	}
}