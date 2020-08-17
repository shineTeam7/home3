package com.home.commonBase.scene.scene;

import com.home.commonBase.constlist.generate.UnitReviveType;
import com.home.commonBase.data.scene.scene.SceneEnterData;
import com.home.commonBase.dataEx.scene.AttackData;
import com.home.commonBase.scene.base.SceneLogicBase;
import com.home.commonBase.scene.base.Unit;
import com.home.shine.net.base.BaseRequest;

/** 场景方法逻辑 */
public class SceneMethodLogic extends SceneLogicBase
{
	@Override
	public void construct()
	{
	
	}
	
	/** 场景开始 */
	public void onSceneStart()
	{
	
	}
	
	@Override
	public void init()
	{
	
	}
	
	@Override
	public void dispose()
	{
	
	}
	
	@Override
	public void onFrame(int delay)
	{
	
	}
	
	/** 当前是否可操作 */
	public boolean canOperate()
	{
		if(!_scene.battle.canOperate())
			return false;
		
		return true;
	}
	
	/** 单位死亡结束 */
	public void onUnitDeadOver(Unit unit)
	{
		switch(unit.fight.getFightUnitConfig().reviveType)
		{
			case UnitReviveType.Remove:
			case UnitReviveType.ReviveAtTime:
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
	
	/** 单位死亡(attacker为空为系统击杀)(sourcer可能为空)(真死亡) */
	public void onUnitRealDead(Unit unit,Unit attaker,int type)
	{
	
	}
	
	/** 单位受到伤害(sourcer可能为空) */
	public void onUnitTakeDamage(Unit unit,int realDamage,Unit attacker)
	{
	
	}
	
	/** 单位击中任意目标 */
	public void onUnitAttackOnHitAnyTarget(Unit unit,Unit target,AttackData data)
	{
	
	}
	
	/** 创建场景进入数据(只创建) */
	public SceneEnterData createSceneEnterData()
	{
		return new SceneEnterData();
	}
	
	/** 实际广播消息(排除controlID为selfID的) */
	public void doRadioMessage(long selfID,BaseRequest request,Unit[] values,int length)
	{
	
	}
}
