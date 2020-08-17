package com.home.commonBase.control;

import com.home.commonBase.constlist.generate.ItemType;
import com.home.commonBase.constlist.generate.UnitType;
import com.home.commonBase.constlist.system.WorkType;
import com.home.commonBase.data.item.ItemData;
import com.home.commonBase.data.scene.base.BulletData;
import com.home.commonBase.data.scene.role.SceneRoleData;
import com.home.commonBase.data.scene.unit.UnitData;
import com.home.commonBase.data.system.PlayerWorkData;
import com.home.commonBase.dataEx.scene.AttackData;
import com.home.commonBase.dataEx.scene.RingLightBuffCountData;
import com.home.commonBase.dataEx.scene.SceneAOITowerData;
import com.home.commonBase.dataEx.scene.UnitReference;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.logic.unit.UnitFightDataLogic;
import com.home.commonBase.logic.unit.UnitItemDicContainerTool;
import com.home.commonBase.scene.base.Bullet;
import com.home.commonBase.scene.base.Region;
import com.home.commonBase.scene.base.Role;
import com.home.commonBase.scene.base.Unit;
import com.home.shine.dataEx.ExternBuf;
import com.home.shine.support.collection.SList;
import com.home.shine.support.pool.ObjectPool;

/** 执行器基类 */
public class LogicExecutorBase extends AbstractLogicExecutor
{
	//场景部分
	
	/** 攻击数据池(非主角自身数据使用) */
	public ObjectPool<AttackData> attackDataPool=new ObjectPool<>(AttackData::new);
	/** 单位战斗数据逻辑池(非主角自身数据使用) */
	private ObjectPool<UnitFightDataLogic> _fightDataLogicPool;
	/** 单位数据池 */
	private ObjectPool<UnitData>[] _unitDataPoolDic;
	/** 单位池组 */
	private ObjectPool<Unit>[] _unitPoolDic;
	/** 单位记录池 */
	public ObjectPool<UnitReference> unitRecordPool=new ObjectPool<>(UnitReference::new);
	/** 子弹数据池 */
	public ObjectPool<BulletData> bulletDataPool=new ObjectPool<>(BulletData::new);
	/** 子弹池 */
	public ObjectPool<Bullet> bulletPool;
	/** 场景角色数据池 */
	public ObjectPool<SceneRoleData> roleDataPool;
	/** 场景角色池 */
	public ObjectPool<Role> rolePool;
	/** 单位物品字典容器池 */
	public ObjectPool<UnitItemDicContainerTool> unitItemDicContainerToolPool=new ObjectPool<>(UnitItemDicContainerTool::new);
	/** 区域池 */
	public ObjectPool<Region> regionPool;
	/** 光环buff统计数据池 */
	public ObjectPool<RingLightBuffCountData> ringLightBuffCountPool=new ObjectPool<>(RingLightBuffCountData::new);
	/** 单位字典池 */
	public ObjectPool<SceneAOITowerData> towerDataPool=new ObjectPool<>(SceneAOITowerData::new,65536);
	
	//逻辑部分
	/** 物品数据池 */
	private ObjectPool<ItemData>[] _itemDataPool;
	
	/** buf */
	public ExternBuf externBuf=new ExternBuf();
	
	public LogicExecutorBase(int index)
	{
		super(index);
	}
	
	/** 初始化(池线程) */
	public void init()
	{
		super.init();
		
		initTick();
		
		(_fightDataLogicPool=new ObjectPool<UnitFightDataLogic>(()->
		{
			UnitFightDataLogic logic=BaseC.factory.createUnitFightDataLogic();
			logic.construct();
			return logic;
		})).setEnable(CommonSetting.sceneLogicUsePool);
		
		_unitDataPoolDic=new ObjectPool[UnitType.size];
		
		for(int i=0;i<UnitType.size;++i)
		{
			(_unitDataPoolDic[i]=createUnitDataPool(i)).setEnable(CommonSetting.sceneLogicUsePool);
		}
		
		_unitPoolDic=new ObjectPool[UnitType.size];
		
		for(int i=0;i<_unitPoolDic.length;++i)
		{
			(_unitPoolDic[i]=createUnitPool(i)).setEnable(CommonSetting.sceneLogicUsePool);
		}
		
		(bulletPool=new ObjectPool<Bullet>(()->
		{
			Bullet bullet=BaseC.factory.createBullet();
			bullet.construct();
			return bullet;
		})).setEnable(CommonSetting.sceneLogicUsePool);
		
		(roleDataPool=new ObjectPool<SceneRoleData>(()->
		{
			SceneRoleData data=BaseC.factory.createSceneRoleData();
			return data;
		})).setEnable(CommonSetting.sceneLogicUsePool);
		
		(rolePool=new ObjectPool<Role>(()->
		{
			Role role=BaseC.factory.createRole();
			role.construct();
			return role;
		})).setEnable(CommonSetting.sceneLogicUsePool);
		
		(regionPool=new ObjectPool<Region>(()->
		{
			Region region=BaseC.factory.createRegion();
			return region;
		})).setEnable(CommonSetting.sceneLogicUsePool);
		
		attackDataPool.setEnable(CommonSetting.sceneLogicUsePool);
		unitRecordPool.setEnable(CommonSetting.sceneLogicUsePool);
		bulletDataPool.setEnable(CommonSetting.sceneLogicUsePool);
		ringLightBuffCountPool.setEnable(CommonSetting.sceneLogicUsePool);
		
		//逻辑部分
		
		_itemDataPool=new ObjectPool[ItemType.size];
		
		for(int i=0;i<_itemDataPool.length;++i)
		{
			(_itemDataPool[i]=createItemDataPool(i)).setEnable(CommonSetting.logicUsePool);
		}
		
		unitItemDicContainerToolPool.setEnable(CommonSetting.logicUsePool);
	}
	
	private ObjectPool<UnitData> createUnitDataPool(int type)
	{
		return new ObjectPool<UnitData>(()->BaseC.factory.createUnitData());
	}
	
	private ObjectPool<Unit> createUnitPool(int type)
	{
		ObjectPool<Unit> re=new ObjectPool<Unit>(()->
		{
			Unit unit=BaseC.factory.createUnit();
			unit.setType(type);
			unit.construct();
			return unit;
		});
		
		return re;
	}
	
	private ObjectPool<ItemData> createItemDataPool(int type)
	{
		ObjectPool<ItemData> re=new ObjectPool<ItemData>(()->
		{
			ItemData data=BaseC.factory.createItemData();
			data.initIdentityByType(type);
			return data;
		});
		
		return re;
	}

	
	
	//--单位部分--//
	
	/** 创建战斗数据逻辑(非主角自身数据使用) */
	public UnitFightDataLogic createUnitFightDataLogic()
	{
		return _fightDataLogicPool.getOne();
	}
	
	/** 回收战斗数据逻辑(非主角自身数据使用) */
	public void releaseUnitFightDataLogic(UnitFightDataLogic logic)
	{
		_fightDataLogicPool.back(logic);
	}
	
	/** 创建单位数据 */
	public UnitData createUnitData(int type)
	{
		return _unitDataPoolDic[type].getOne();
	}
	
	/** 回收单位数据 */
	public void releaseUnitData(UnitData data)
	{
		_unitDataPoolDic[data.identity.type].back(data);
	}
	
	/** 创建单位(从池中) */
	public Unit createUnit(int type)
	{
		Unit unit=_unitPoolDic[type].getOne();
		++unit.version;
		return unit;
	}
	
	/** 回收单位 */
	public void releaseUnit(Unit unit)
	{
		++unit.version;
		_unitPoolDic[unit.getType()].back(unit);
	}
	
	/** 创建物品数据 */
	public ItemData createItemData(int type)
	{
		return _itemDataPool[type].getOne();
	}
	
	/** 回收物品数据 */
	public void releaseItemData(ItemData data)
	{
		_itemDataPool[data.identity.type].back(data);
	}
	
	/** 添加角色离线事务数据(池线程) */
	public void addPlayerOfflineWork(long playerID,PlayerWorkData data)
	{
		addPlayerWork(WorkType.PlayerOffline,playerID,data);
	}
	
	/** 添加一个角色的在线事务数据(池线程)(只对在线角色有效,如目标离线则收不到) */
	public void addPlayerOnlineWork(long playerID,PlayerWorkData data)
	{
		addPlayerWork(WorkType.PlayerOnline,playerID,data);
	}
	
	/** 添加一个角色的立即事务数据(池线程) */
	public void addPlayerAbsWork(long playerID,PlayerWorkData data)
	{
		addPlayerWork(WorkType.PlayerAbs,playerID,data);
	}
	
	/** 添加角色事务 */
	public void addPlayerWork(int type,long playerID,PlayerWorkData data)
	{
	
	}
}
