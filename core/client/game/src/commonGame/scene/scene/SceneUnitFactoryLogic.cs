using System;
using ShineEngine;

/// <summary>
/// 场景单位工厂逻辑
/// </summary>
public class SceneUnitFactoryLogic:SceneLogicBase
{
	/** 初始化添加单位 */
	public void initFirst()
	{
		ScenePlaceElementConfig[] values;
		ScenePlaceElementConfig v;

		bool isClientDrive=_scene.getConfig().instanceType==SceneInstanceType.ClientDriveSinglePlayerBattle;

		for(int i=(values=_scene.getPlaceConfig().elements.getValues()).Length-1;i>=0;--i)
		{
			if((v=values[i])!=null)
			{
				if(BaseC.constlist.sceneElement_isUnit(v.type) && v.isInitAdd)
				{
					if(BaseC.constlist.sceneElement_isClientCreate(v.type) || isClientDrive)
					{
						createAddUnitByPlace(v.instanceID);
					}
				}
			}
		}
	}

	//接口部分

	protected MonsterIdentityData toCreateMonsterIdentityData()
	{
		return new MonsterIdentityData();
	}

	protected NPCIdentityData toCreateNPCIdentityData()
	{
		return new NPCIdentityData();
	}

	protected PuppetIdentityData toCreatePuppetIdentityData()
	{
		return new PuppetIdentityData();
	}

	//--创建部分--//

	//基础部分

	/** 初始化基础单位数据 */
	public void initBaseUnitData(UnitData data)
	{
		(data.pos=new UnitPosData()).initDefault();
		(data.normal=new UnitNormalData()).initDefault();
	}

	/** 初始化战斗单位数据 */
	protected void initFightUnitData(UnitData data)
	{
		(data.avatar=new UnitAvatarData()).initDefault();
		(data.move=new UnitMoveData()).initDefault();
		(data.fight=new UnitFightData()).initDefault();

		data.fightEx=new UnitFightExData();
		data.fightDataLogic=GameC.pool.createUnitFightDataLogic();
//		//绑定场景
//		data.fightDataLogic.setScene(_scene);
		data.fightDataLogic.setIsDriveAll(true);
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
	public Unit createAddUnitByPlace(int placeInstanceID,bool giveNewInstanceID)
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
	public UnitData createUnitDataByPlace(int placeInstanceID,bool giveNewInstanceID)
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
			default:
			{
				Ctrl.throwError("未支持的SceneElement类型:",config.type);
				//TODO:补充完
			}
				break;
		}

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

	/** 创建怪物数据(客户端场景用) */
	public UnitData createMonsterData(int id,int level)
	{
		UnitData data=GameC.factory.createUnitData();

		MonsterIdentityData iData;

		if(data.identity==null)
		{
			data.identity=iData=toCreateMonsterIdentityData();
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
		UnitData data=GameC.factory.createUnitData();

		NPCIdentityData iData;

		if(data.identity==null)
		{
			data.identity=iData=toCreateNPCIdentityData();
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

	//傀儡

	/** 创建傀儡数据 */
	public UnitData createPuppetData(int id,int level,Unit master,int lastTime)
	{
		UnitData data=GameC.factory.createUnitData();

		PuppetIdentityData iData;

		if(data.identity==null)
		{
			data.identity=iData=toCreatePuppetIdentityData();
			iData.type=UnitType.Puppet;
			//初始化部分
			initBaseUnitData(data);
			initFightUnitData(data);
		}
		else
		{
			iData=(PuppetIdentityData)data.identity;
		}

		iData.id=id;
		iData.level=level;
		iData.masterInstanceID=master.instanceID;

		FightUnitIdentityData masterIdentity=master.getUnitData().getFightIdentity();

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

	/** 创建场景特效 */
	public UnitData createSceneEffectData(int id)
	{
		UnitData data=GameC.factory.createUnitData();

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
		iData.signedEffectID=-1;

		return data;
	}

	/** 创建并添加场景特效 */
	public Unit createAddSceneEffect(int id,PosData pos,DirData dir)
	{
		return addNewUnit(createSceneEffectData(id),pos,dir);
	}

	/** 创建并添加场景特效(通过指定特效ID) */
	public Unit createAddSceneEffectBySignEffect(int effectID,PosData pos,DirData dir)
	{
		UnitData data=createSceneEffectData(0);
		data.getSceneEffectIdentity().signedEffectID=effectID;

		return addNewUnit(data,pos,dir);
	}

	/** 创建并添加场景特效 */
	public Unit createAddSceneEffect(int id,float[] pos)
	{
		return addNewUnit(createSceneEffectData(id),pos);
	}

	/** 创建掉落物品数据 */
	public UnitData createFieldItemData(ItemData item)
	{
		UnitData data=GameC.factory.createUnitData();

		FieldItemIdentityData iData;

		if(data.identity==null)
		{
			data.identity=iData=new FieldItemIdentityData();
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
		return createFieldItemData(BaseC.logic.createItem(id,num));
	}

	/** 创建并添加掉落物品 */
	public Unit createAddFieldItem(ItemData item,PosData pos)
	{
		return addNewUnit(createFieldItemData(item),pos);
	}

}