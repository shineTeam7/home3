using System;
using ShineEngine;

/// <summary>
/// 池化对象管理
/// </summary>
public class GamePoolControl
{
	/** 攻击数据池 */
	public ObjectPool<AttackData> attackDataPool;

	/** 单位战斗数据逻辑池 */
	private ObjectPool<UnitFightDataLogic> _fightDataLogicPool;

	/** 单位池 */
	private ObjectPool<Unit>[] _unitPoolDic;

	/** 子弹池 */
	public ObjectPool<Bullet> bulletPool;

	/** 单位特效池 */
	public ObjectPool<UnitEffect> unitEffectPool;

	/** buff数据池 */
	public ObjectPool<BuffData> buffDataPool=new ObjectPool<BuffData>(()=>GameC.factory.createBuffData());
	/** buff间隔动作池 */
	public ObjectPool<BuffIntervalActionData> buffIntervalActionDataPool=new ObjectPool<BuffIntervalActionData>(()=>new BuffIntervalActionData());

	/** 物品数据吃 */
	private ObjectPool<ItemData>[] _itemDataPool;
	/** 任务目标数据池 */
	private ObjectPool<TaskData>[] _taskDataPool;

	public ObjectPool<PetUseLogic> petUseLogicPool=new ObjectPool<PetUseLogic>(()=>new PetUseLogic());

	public ObjectPool<Role> rolePool;


	public virtual void init()
	{
		(attackDataPool=new ObjectPool<AttackData>(()=>{ return new AttackData(); })).setEnable(CommonSetting.sceneLogicUsePool);

		(_fightDataLogicPool=new ObjectPool<UnitFightDataLogic>(()=>
		{
			UnitFightDataLogic logic=GameC.factory.createUnitFightDataLogic();
			logic.construct();
			return logic;
		})).setEnable(CommonSetting.sceneLogicUsePool);

		_unitPoolDic=new ObjectPool<Unit>[UnitType.size];

		for(int i=0;i<UnitType.size;++i)
		{
			(_unitPoolDic[i]=createUnitPool(i)).setEnable(CommonSetting.sceneLogicUsePool);
		}

		(bulletPool=new ObjectPool<Bullet>(()=>
		{
			Bullet bullet=GameC.factory.createBullet();
			bullet.construct();
			return bullet;
		})).setEnable(CommonSetting.sceneLogicUsePool);

		(unitEffectPool=new ObjectPool<UnitEffect>(()=>
		{
			UnitEffect effect=GameC.factory.createUnitEffect();
			effect.construct();
			return effect;
		})).setEnable(CommonSetting.sceneLogicUsePool);

		(rolePool=new ObjectPool<Role>(()=>
		{
			Role role=GameC.factory.createRole();
			role.construct();
			return role;
		})).setEnable(CommonSetting.sceneLogicUsePool);

		buffDataPool.setEnable(CommonSetting.sceneLogicUsePool);
		buffIntervalActionDataPool.setEnable(CommonSetting.sceneLogicUsePool);

		//logic
		_itemDataPool=new ObjectPool<ItemData>[ItemType.size];

		for(int i=0;i<_itemDataPool.Length;++i)
		{
			(_itemDataPool[i]=createItemDataPool(i)).setEnable(CommonSetting.logicUsePool);
		}

		TaskTypeConfig typeConfig;

		_taskDataPool=new ObjectPool<TaskData>[QuestType.size];
		(_taskDataPool[0]=createTaskDataPool(0)).setEnable(CommonSetting.logicUsePool);

		for(int i=0;i<_taskDataPool.Length;++i)
		{
			if((typeConfig=TaskTypeConfig.get(i))!=null && typeConfig.needCustomTask)
			{
				(_taskDataPool[i]=createTaskDataPool(i)).setEnable(CommonSetting.logicUsePool);
			}
		}

		petUseLogicPool.setEnable(CommonSetting.logicUsePool);
	}

	private ObjectPool<Unit> createUnitPool(int type)
	{
		ObjectPool<Unit> re=new ObjectPool<Unit>(()=>
		{
			Unit unit=GameC.factory.createUnit();
			unit.type=type;
			unit.isHero=false;
			unit.construct();
			return unit;
		});

		return re;
	}

	private ObjectPool<ItemData> createItemDataPool(int type)
	{
		ObjectPool<ItemData> re=new ObjectPool<ItemData>(()=>
		{
			ItemData data=GameC.factory.createItemData();
			data.initIdentityByType(type);
			return data;
		});

		return re;
	}

	private ObjectPool<TaskData> createTaskDataPool(int type)
	{
		ObjectPool<TaskData> re=new ObjectPool<TaskData>(()=>
		{
			return BaseC.logic.createTaskData(type);
		});

		return re;
	}

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

	/** 创建单位(都按不是自己的单位算) */
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
		_unitPoolDic[unit.type].back(unit);
	}

	/** 回收子弹 */
	public void releaseUnitEffect(UnitEffect effect)
	{
		unitEffectPool.back(effect);
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

	/** 创建任务数据 */
	public virtual TaskData createTaskData(int type)
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
}