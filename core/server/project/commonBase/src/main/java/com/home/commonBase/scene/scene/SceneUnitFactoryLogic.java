package com.home.commonBase.scene.scene;

import com.home.commonBase.config.game.ScenePlaceElementConfig;
import com.home.commonBase.constlist.generate.BuildingStateType;
import com.home.commonBase.constlist.generate.OperationStateType;
import com.home.commonBase.constlist.generate.SceneElementType;
import com.home.commonBase.constlist.generate.SceneForceType;
import com.home.commonBase.constlist.generate.SceneInstanceType;
import com.home.commonBase.constlist.generate.UnitType;
import com.home.commonBase.data.activity.ActivityServerData;
import com.home.commonBase.data.item.ItemData;
import com.home.commonBase.data.scene.base.DirData;
import com.home.commonBase.data.scene.base.PosData;
import com.home.commonBase.data.scene.base.PosDirData;
import com.home.commonBase.data.scene.base.RegionData;
import com.home.commonBase.data.scene.unit.UnitAIData;
import com.home.commonBase.data.scene.unit.UnitAvatarData;
import com.home.commonBase.data.scene.unit.UnitData;
import com.home.commonBase.data.scene.unit.UnitFightData;
import com.home.commonBase.data.scene.unit.UnitFightExData;
import com.home.commonBase.data.scene.unit.UnitMoveData;
import com.home.commonBase.data.scene.unit.UnitNormalData;
import com.home.commonBase.data.scene.unit.UnitPosData;
import com.home.commonBase.data.scene.unit.identity.BuildingIdentityData;
import com.home.commonBase.data.scene.unit.identity.FieldItemBagIdentityData;
import com.home.commonBase.data.scene.unit.identity.FieldItemIdentityData;
import com.home.commonBase.data.scene.unit.identity.FightUnitIdentityData;
import com.home.commonBase.data.scene.unit.identity.MonsterIdentityData;
import com.home.commonBase.data.scene.unit.identity.NPCIdentityData;
import com.home.commonBase.data.scene.unit.identity.OperationIdentityData;
import com.home.commonBase.data.scene.unit.identity.PuppetIdentityData;
import com.home.commonBase.data.scene.unit.identity.SceneEffectIdentityData;
import com.home.commonBase.data.scene.unit.identity.VehicleIdentityData;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.scene.base.Region;
import com.home.commonBase.scene.base.Role;
import com.home.commonBase.scene.base.SceneLogicBase;
import com.home.commonBase.scene.base.Unit;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.SList;

/** 场景单位工厂逻辑 */
public class SceneUnitFactoryLogic extends SceneLogicBase
{
	@Override
	public void construct()
	{
	
	}
	
	@Override
	public void init()
	{
	
	}
	
	@Override
	public void afterInit()
	{
		boolean isClientDrive=_scene.getConfig().instanceType==SceneInstanceType.ClientDriveSinglePlayerBattle;
		
		_scene.getPlaceConfig().elements.forEachValue(v->
		{
			if(BaseC.constlist.sceneElement_isUnit(v.type) && v.isInitAdd)
			{
				if(CommonSetting.isClient)
				{
					if(BaseC.constlist.sceneElement_isClientCreate(v.type) || isClientDrive)
					{
						createAddUnitByPlace(v.instanceID);
					}
				}
				else
				{
					if(!BaseC.constlist.sceneElement_isClientCreate(v.type))
					{
						createAddUnitByPlace(v.instanceID);
					}
				}
			}
			//区域
			else if(v.type==SceneElementType.Region)
			{
				createAddRegionByPlace(v.instanceID);
			}
		});
	}
	
	@Override
	public void dispose()
	{
	
	}
	
	@Override
	public void onFrame(int delay)
	{
	
	}
	
	//--创建部分--//
	
	//基础部分
	
	/** 初始化基础单位数据(位置) */
	public void initBaseUnitData(UnitData data)
	{
		(data.pos=new UnitPosData()).initDefault();
		(data.normal=new UnitNormalData()).initDefault();
	}
	
	/** 初始化战斗单位数据 */
	public void initFightUnitData(UnitData data)
	{
		(data.avatar=new UnitAvatarData()).initDefault();
		(data.move=new UnitMoveData()).initDefault();
		(data.fight=new UnitFightData()).initDefault();
		
		data.fightEx=new UnitFightExData();
		data.fightDataLogic=_scene.getExecutor().createUnitFightDataLogic();
		////绑定场景
		//data.fightDataLogic.setScene(_scene);
		//绑定数据
		data.fightDataLogic.setData(data.fight,data.avatar);
		
		data.ai=new UnitAIData();
	}
	
	/** 创建并添加单位通过摆放配置 */
	public Unit createAddUnitByPlace(int placeInstanceID)
	{
		return createAddUnitByPlace(placeInstanceID,false);
	}
	
	/** 创建并添加单位通过摆放配置(giveNewInstanceID:是否分配新的实例ID,否则使用配置中的实例ID) */
	public Unit createAddUnitByPlace(int placeInstanceID,boolean giveNewInstanceID)
	{
		UnitData data=createUnitDataByPlace(placeInstanceID,giveNewInstanceID);
		
		if(data==null)
			return null;
		
		return _scene.addUnit(data);
	}
	
	/** 通过配置创建单位数据 */
	public UnitData createUnitDataByPlace(int placeInstanceID)
	{
		return createUnitDataByPlace(placeInstanceID,false);
	}
	
	/** 通过配置创建单位数据(giveNewInstanceID:是否分配新的实例ID,否则使用配置中的实例ID) */
	public UnitData createUnitDataByPlace(int placeInstanceID,boolean giveNewInstanceID)
	{
		ScenePlaceElementConfig config=_scene.getPlaceConfig().elements.get(placeInstanceID);
		
		//不存在的
		if(config==null)
		{
			Ctrl.throwError("找不到场景摆放配置",placeInstanceID);
			return null;
		}
		
		//不是单位类型
		if(!BaseC.constlist.sceneElement_isUnit(config.type))
		{
			Ctrl.throwError("不是单位类型",placeInstanceID);
			return null;
		}
		
		UnitData data=toCreateUnitDataByPlace(config);
		
		if(data!=null)
		{
			data.instanceID=giveNewInstanceID ? _scene.getUnitInstanceID() : config.instanceID;
			//位置
			data.pos.setByFArr(config.pos);
			
			//设定势力
			data.identity.force=config.force;
		}
		
		return data;
	}
	
	protected UnitData toCreateUnitDataByPlace(ScenePlaceElementConfig config)
	{
		UnitData data=null;
		
		switch(config.type)
		{
			case SceneElementType.Monster:
			{
				data=createMonsterData(config.id,config.level);
			}
				break;
			case SceneElementType.Npc:
			{
				data=createNPCData(config.id);
			}
				break;
			case SceneElementType.SceneEffect:
			{
				data=createSceneEffectData(config.id);
			}
				break;
			case SceneElementType.FieldItem:
			{
				data=createFieldItemData(config.id,config.iArgs[0]);
			}
				break;
			case SceneElementType.Building:
			{
				Ctrl.throwError("暂不支持直接种Building，需要先把Role相关功能做完");
				//_scene.role.get
				//data=createBuildingData(config.id,config.level,config.iArgs[0]);
			}
				break;
			case SceneElementType.Operation:
			{
				data=createOperationData(config.id);
			}
				break;
			default:
			{
				Ctrl.throwError("未支持的SceneElement类型:",config.type);
				//TODO:补充完
			}
				break;
		}
		
		data.placeConfig=config;
		
		return data;
	}
	
	/** 添加新单位 */
	public Unit addNewUnit(UnitData data,float[] posArr)
	{
		//新流水ID
		data.instanceID=_scene.getUnitInstanceID();
		//位置
		data.pos.setByFArr(posArr);
		
		return _scene.addUnit(data);
	}
	
	/** 添加新单位 */
	public Unit addNewUnit(UnitData data,PosData pos)
	{
		//新流水ID
		data.instanceID=_scene.getUnitInstanceID();
		//位置
		data.pos.pos.copyPos(pos);
		
		return _scene.addUnit(data);
	}
	
	/** 添加新单位 */
	public Unit addNewUnit(UnitData data,PosData pos,DirData dir)
	{
		//新流水ID
		data.instanceID=_scene.getUnitInstanceID();
		//位置
		data.pos.pos.copyPos(pos);
		
		if(dir!=null)
		{
			data.pos.dir.copyDir(dir);
		}
		
		return _scene.addUnit(data);
	}
	
	/** 添加新单位 */
	public Unit addNewUnit(UnitData data,PosDirData pdData)
	{
		//新流水ID
		data.instanceID=_scene.getUnitInstanceID();
		//位置
		data.pos.pos.copyPos(pdData.pos);
		data.pos.dir.copyDir(pdData.dir);
		
		return _scene.addUnit(data);
	}
	
	//--具体部分--//
	
	/** 创建怪物数据(没有instnaceID和pos) */
	public UnitData createMonsterData(int id,int level)
	{
		UnitData data=_scene.getExecutor().createUnitData(UnitType.Monster);
		
		MonsterIdentityData iData;
		
		if(data.identity==null)
		{
			data.identity=iData=BaseC.factory.createMonsterIdentityData();
			iData.type=UnitType.Monster;
			//初始化部分
			initBaseUnitData(data);
			initFightUnitData(data);
		}
		else
		{
			iData=(MonsterIdentityData)data.identity;
		}
		
		iData.force=SceneForceType.None;
		iData.playerID=-1;//无归属
		iData.id=id;
		iData.level=level;
		
		data.fightDataLogic.initByFightUnit(iData);
		
		return data;
	}
	
	/** 创建怪物 */
	public Unit createAddMonster(int id,int level,PosData pos)
	{
		return addNewUnit(createMonsterData(id,level),pos);
	}
	
	/** 创建怪物 */
	public Unit createAddMonster(int id,int level,PosData pos,DirData dir)
	{
		return addNewUnit(createMonsterData(id,level),pos,dir);
	}
	
	/** 创建npc数据(没有instnaceID和pos) */
	public UnitData createNPCData(int id)
	{
		UnitData data=_scene.getExecutor().createUnitData(UnitType.Npc);
		
		NPCIdentityData iData;
		
		if(data.identity==null)
		{
			data.identity=iData=BaseC.factory.createNPCIdentityData();
			iData.type=UnitType.Npc;
			//初始化部分
			initBaseUnitData(data);
		}
		else
		{
			iData=(NPCIdentityData)data.identity;
		}
		
		iData.force=SceneForceType.None;
		iData.playerID=-1;//无归属
		iData.id=id;
		
		return data;
	}
	
	/** 创建场景特效(没有instnaceID和pos) */
	public UnitData createSceneEffectData(int id)
	{
		UnitData data=_scene.getExecutor().createUnitData(UnitType.SceneEffect);
		
		SceneEffectIdentityData iData;
		
		if(data.identity==null)
		{
			data.identity=iData=new SceneEffectIdentityData();
			iData.type=UnitType.SceneEffect;
			//初始化部分
			initBaseUnitData(data);
		}
		else
		{
			iData=(SceneEffectIdentityData)data.identity;
		}
		
		iData.force=SceneForceType.None;
		iData.playerID=-1;//无归属
		iData.id=id;
		
		return data;
	}
	
	
	//傀儡
	
	/** 创建傀儡数据 */
	public UnitData createPuppetData(int id,int level,Unit master,int lastTime)
	{
		UnitData data=_scene.getExecutor().createUnitData(UnitType.Puppet);
		
		PuppetIdentityData iData;
		
		if(data.identity==null)
		{
			data.identity=iData=BaseC.factory.createPuppetIdentityData();
			iData.type=UnitType.Puppet;
			//初始化部分
			initBaseUnitData(data);
			initFightUnitData(data);
		}
		else
		{
			iData=(PuppetIdentityData)data.identity;
		}
		
		FightUnitIdentityData masterIdentity=master.getUnitData().getFightIdentity();
		
		iData.id=id;
		iData.level=level;
		iData.masterInstanceID=master.instanceID;
		
		//归属和控制
		iData.force=masterIdentity.force;
		iData.playerID=masterIdentity.playerID;
		iData.controlPlayerID=masterIdentity.controlPlayerID;
		
		data.fightDataLogic.initByFightUnit(iData);
		
		//剩余时间
		iData.lastTime=lastTime;
		
		return data;
	}
	
	/** 创建傀儡 */
	public Unit createAddPuppet(int id,int level,PosData pos,Unit master,int lastTime)
	{
		return createAddPuppet(id,level,pos,master.pos.getDir(),master,lastTime);
	}
	
	/** 创建傀儡 */
	public Unit createAddPuppet(int id,int level,PosData pos,DirData dir,Unit master,int lastTime)
	{
		return addNewUnit(createPuppetData(id,level,master,lastTime),pos,dir);
	}
	
	//场景特效
	
	/** 创建并添加场景特效 */
	public Unit createAddSceneEffect(int id,PosData pos,DirData dir)
	{
		return addNewUnit(createSceneEffectData(id),pos,dir);
	}
	
	/** 创建并添加场景特效 */
	public Unit createAddSceneEffect(int id,float[] pos)
	{
		return addNewUnit(createSceneEffectData(id),pos);
	}
	
	//怪物
	
	//掉落物品
	/** 创建掉落物品数据 */
	public UnitData createFieldItemData(ItemData item)
	{
		UnitData data=_scene.getExecutor().createUnitData(UnitType.FieldItem);
		
		FieldItemIdentityData iData;
		
		if(data.identity==null)
		{
			data.identity=iData=BaseC.factory.createFieldItemIdentityData();
			iData.type=UnitType.FieldItem;
			//初始化部分
			initBaseUnitData(data);
		}
		else
		{
			iData=(FieldItemIdentityData)data.identity;
		}
		
		iData.playerID=-1;//无归属
		iData.item=item;
		
		return data;
	}
	
	/** 创建掉落物品数据 */
	public UnitData createFieldItemData(int id,int num)
	{
		return createFieldItemData(BaseC.logic.createItem(id,num,_scene));
	}
	
	/** 创建并添加掉落物品 */
	public Unit createAddFieldItem(ItemData item,PosData pos)
	{
		return addNewUnit(createFieldItemData(item),pos);
	}
	
	//建筑
	
	/** 额外构造建筑身份数据 */
	protected void makeBuildingIdentityData(BuildingIdentityData data,Role role)
	{
	
	}
	
	/** 额外构造建筑身份数据 */
	protected void makeBuildingIdentityData(UnitData data,Unit master)
	{
	
	}
	
	/** 额外构造建筑身份数据 */
	protected void makeBuildingIdentityData(UnitData data,long playerId)
	{
	
	}
	
	private UnitData createBuildingDataFirst(int id,int level,boolean isReady)
	{
		UnitData data=_scene.getExecutor().createUnitData(UnitType.Building);
		
		BuildingIdentityData iData;
		
		if(data.identity==null)
		{
			data.identity=iData=BaseC.factory.createBuildingIdentityData();
			iData.type=UnitType.Building;
			//初始化部分
			initBaseUnitData(data);
			initFightUnitData(data);
		}
		else
		{
			iData=(BuildingIdentityData)data.identity;
		}
		
		iData.id=id;
		iData.level=level;
		
		iData.passTime=0;
		iData.state=isReady ? BuildingStateType.Ready : BuildingStateType.Building;
		
		data.fightDataLogic.initByFightUnit(iData);
		
		return data;
	}
	
	/** 创建建筑数据为中立 */
	public UnitData createBuildingDataForNeutral(int id,int level,boolean isReady)
	{
		UnitData data=createBuildingDataFirst(id,level,isReady);
		
		BuildingIdentityData iData=(BuildingIdentityData)data.identity;
		
		iData.force=SceneForceType.None;
		iData.playerID=-1;//无归属
		iData.id=id;
		
		return data;
	}
	
	/** 创建建筑数据为玩家 */
	public UnitData createBuildingData(int id,int level,long playerID,boolean isReady)
	{
		UnitData data=createBuildingDataFirst(id,level,isReady);
		
		BuildingIdentityData iData=(BuildingIdentityData)data.identity;
		
		iData.force=SceneForceType.Player;
		iData.playerID=playerID;
		iData.controlPlayerID=playerID;
		iData.id=id;
		
		makeBuildingIdentityData(data,playerID);
		
		return data;
	}
	
	/** 创建建筑数据 */
	public UnitData createBuildingData(int id,int level,Unit master,boolean isReady)
	{
		UnitData data=createBuildingDataFirst(id,level,isReady);
		
		BuildingIdentityData iData=(BuildingIdentityData)data.identity;
		
		FightUnitIdentityData masterIdentity=master.getUnitData().getFightIdentity();
		
		//归属和控制
		iData.force=masterIdentity.force;
		iData.playerID=masterIdentity.playerID;
		iData.controlPlayerID=masterIdentity.controlPlayerID;
		
		makeBuildingIdentityData(data,master);
		
		return data;
	}
	
	/** 创建建筑数据 */
	public UnitData createBuildingData(int id,int level,Role role,boolean isReady)
	{
		UnitData data=createBuildingDataFirst(id,level,isReady);
		
		BuildingIdentityData iData=(BuildingIdentityData)data.identity;
		
		//绑定归属
		iData.force=role.force.force;
		iData.playerID=role.playerID;
		iData.controlPlayerID=role.playerID;
		
		makeBuildingIdentityData(iData,role);
		
		return data;
	}
	
	/** 创建中立建筑 */
	public Unit createAddBuildingForNeutral(int id,int level,PosDirData pos)
	{
		return addNewUnit(createBuildingDataForNeutral(id,level,false),pos);
	}
	
	/** 创建建筑 */
	public Unit createAddBuilding(int id,int level,long playerID,boolean isReady,PosDirData pos)
	{
		return addNewUnit(createBuildingData(id,level,playerID,isReady),pos);
	}
	
	/** 创建建筑 */
	public Unit createAddBuilding(int id,int level,Unit master,boolean isReady,PosDirData pos)
	{
		return addNewUnit(createBuildingData(id,level,master,isReady),pos);
	}
	
	/** 创建建筑 */
	public Unit createAddBuilding(int id,int level,Role role,boolean isReady,PosDirData pos)
	{
		return addNewUnit(createBuildingData(id,level,role,isReady),pos);
	}
	
	/** 创建中立建筑 */
	public Unit createAddBuilding(int id,int level,long playerID,PosDirData pos)
	{
		return addNewUnit(createBuildingDataForNeutral(id,level,false),pos);
	}
	
	//操作体
	
	/** 创建操作体数据 */
	public UnitData createOperationData(int id)
	{
		UnitData data=_scene.getExecutor().createUnitData(UnitType.Operation);
		
		OperationIdentityData iData;
		
		if(data.identity==null)
		{
			data.identity=iData=BaseC.factory.createOperationIdentityData();
			iData.type=UnitType.Operation;
			//初始化部分
			initBaseUnitData(data);
		}
		else
		{
			iData=(OperationIdentityData)data.identity;
		}
		
		iData.force=SceneForceType.None;
		iData.playerID=-1;//无归属
		iData.id=id;
		iData.state=OperationStateType.Ready;//默认ready
		
		makeOperationExData(data);
		
		return data;
	}
	
	/** 构造额外操作体数据 */
	protected void makeOperationExData(UnitData data)
	{
	
	}
	
	/** 创建并添加操作体 */
	public Unit createAddOperation(int id,float[] pos)
	{
		return addNewUnit(createOperationData(id),pos);
	}
	
	//掉落包
	
	/** 创建掉落包单位数据 */
	public UnitData createFieldItemBagData(SList<ItemData> list,int fromMonsterID)
	{
		UnitData data=_scene.getExecutor().createUnitData(UnitType.FieldItemBag);
		
		FieldItemBagIdentityData iData;
		
		if(data.identity==null)
		{
			data.identity=iData=BaseC.factory.createFieldItemBagIdentityData();
			iData.type=UnitType.FieldItemBag;
			//初始化部分
			initBaseUnitData(data);
		}
		else
		{
			iData=(FieldItemBagIdentityData)data.identity;
		}
		
		iData.force=SceneForceType.None;
		iData.playerID=-1;//无归属
		
		//data.func
		
		return data;
	}
	
	/** 创建区域 */
	public RegionData createRegion(int id,float[] args)
	{
		RegionData data=new RegionData();
		data.id=id;
		data.args=args;
		return data;
	}
	
	/** 创建并添加单位通过摆放配置 */
	public Region createAddRegionByPlace(int placeInstanceID)
	{
		ScenePlaceElementConfig config=_scene.getPlaceConfig().elements.get(placeInstanceID);
		
		//不存在的
		if(config==null)
		{
			Ctrl.throwError("找不到场景摆放配置",placeInstanceID);
			return null;
		}
		
		RegionData data=createRegion(config.id,config.fArgs);
		data.instanceID=config.instanceID;
		return _scene.addRegion(data);
	}
	
	//载具
	
	/** 创建载具数据 */
	public UnitData createVehicleData(int id,int level)
	{
		UnitData data=_scene.getExecutor().createUnitData(UnitType.Vehicle);
		
		VehicleIdentityData iData;
		
		if(data.identity==null)
		{
			data.identity=iData=BaseC.factory.createVehicleIdentityData();
			iData.initDefault();
			
			iData.type=UnitType.Vehicle;
			//初始化部分
			initBaseUnitData(data);
			initFightUnitData(data);
		}
		else
		{
			iData=(VehicleIdentityData)data.identity;
		}
		
		iData.force=SceneForceType.None;
		iData.playerID=-1;//无归属
		iData.id=id;
		iData.level=level;
		
		data.fightDataLogic.initByFightUnit(iData);
		
		return data;
	}
	
	/** 创建载具 */
	public Unit createAddVehicle(int id,int level,PosData pos,Unit master)
	{
		UnitData data=createVehicleData(id,level);
		
		if(master!=null)
		{
			data.getFightIdentity().controlPlayerID=data.identity.playerID=master.getUnitData().identity.playerID;
		}
		
		return addNewUnit(data,pos);
	}
}
