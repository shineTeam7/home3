using System;
using ShineEngine;

/// <summary>
/// 场景玩法逻辑
/// </summary>
public class SceneMethodLogic:SceneLogicBase
{
	public override void construct()
	{
	}

	/** 设置了场景配置之后 */
	public virtual void onSetConfig()
	{

	}

	public override void init()
	{
	}

	public override void dispose()
	{
	}

	public override void onFrame(int delay)
	{

	}

	/** 当前是否可操作 */
	public virtual bool canOperate()
	{
		if(!_scene.battle.canOperate())
			return false;

		return true;
	}

	public virtual SceneEnterData createSceneEnterData()
	{
		return new SceneEnterData();
	}

	/** 构造场景进入数据(客户端场景)(激活单位后) */
	public virtual void makeSceneEnterData(SceneEnterData data)
	{

	}

	/** 构造预进入位置(客户端场景用) */
	public virtual void makeScenePosData(UnitData data)
	{

	}

	/** 构造角色数据(客户端场景用) */
	public virtual void makeCharacterData(UnitData data)
	{

	}
	
	/** 单位死亡 */
	public virtual void onUnitDead(Unit unit,Unit attacker)
	{
	
	}

	public virtual void onUnitDeadOver(Unit unit)
	{
		switch(unit.fight.getFightUnitConfig().reviveType)
		{
			case UnitReviveType.Remove:
			{
				unit.removeLater();
			}
				break;
			case UnitReviveType.ReviveAtDeadOver:
			{
				unit.fight.doRevive();
			}
				break;
		}
	}

	/** 单位受到伤害 */
	public virtual void onUnitTakeDamage(Unit unit,int realDamage,Unit attacker)
	{

	}


	/** 初始化进入数据 */
	public virtual void initEnterData(SceneEnterData data)
	{

	}
}