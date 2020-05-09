package com.home.commonBase.logic.unit;

import com.home.commonBase.constlist.generate.StatusType;
import com.home.commonBase.control.AttributeControl;
import com.home.commonBase.tool.StatusTool;
import com.home.shine.support.collection.IntBooleanMap;

/** 状态数据逻辑 */
public class StatusDataLogic extends StatusTool
{
	private UnitFightDataLogic _parent;
	
	public void setParent(UnitFightDataLogic parent)
	{
		_parent=parent;
		
		setInfo(AttributeControl.status);
	}
	
	@Override
	protected void toSendSelf(IntBooleanMap dic)
	{
		_parent.sendSelfStatus(dic);
	}
	
	@Override
	protected void toSendOther(IntBooleanMap dic)
	{
		_parent.sendOtherStatus(dic);
	}
	
	@Override
	protected void toDispatch(boolean[] changeSet)
	{
		_parent.dispatchStatus(changeSet);
	}
	
	//--快捷方式--//
	
	/** 是否活着 */
	public boolean isAlive()
	{
		return !getStatus(StatusType.IsDead);
	}
	
	/** 不可成为目标 */
	public boolean cantBeTarget()
	{
		return getStatus(StatusType.CantBeTarget);
	}
	
	/** 不可被攻击 */
	public boolean cantBeAttackTarget()
	{
		return getStatus(StatusType.CantBeAttackTarget);
	}
	
	/** 不可移动 */
	public boolean cantMove()
	{
		return getStatus(StatusType.IsDead) || getStatus(StatusType.CantMove) || getStatus(StatusType.Vertigo);
	}
	
	/** 不可转身 */
	public boolean cantTurn()
	{
		return getStatus(StatusType.IsDead) || getStatus(StatusType.CantTurn) || getStatus(StatusType.Vertigo);
	}
	
	/** 不可攻击 */
	public boolean cantAttack()
	{
		return getStatus(StatusType.IsDead) || getStatus(StatusType.CantAttack) || getStatus(StatusType.Vertigo);
	}
	
	/** 不可施法 */
	public boolean cantCast()
	{
		return getStatus(StatusType.IsDead) || getStatus(StatusType.CantCast) || getStatus(StatusType.Vertigo);
	}
	
	/** 不可成为敌对目标 */
	public boolean cantByEnemyTarget()
	{
		return getStatus(StatusType.CantBeEnemyTarget) || getStatus(StatusType.Invincible);
	}
	
	/** 控制免疫 */
	public boolean isControlImmun()
	{
		return getStatus(StatusType.ControlImmun) || getStatus(StatusType.Invincible);
	}
	
	/** 伤害免疫 */
	public boolean isDamageImmun()
	{
		return getStatus(StatusType.DamageImmun) || getStatus(StatusType.Invincible);
	}
	
	/** 技能免疫 */
	public boolean isSkillImmun()
	{
		return getStatus(StatusType.SkillImmun) || getStatus(StatusType.Invincible);
	}
	
	/** 无法被伤害击杀 */
	public boolean cantBeKillByDamage()
	{
		return getStatus(StatusType.CantBeKillByDamage) || getStatus(StatusType.Invincible);
	}
	
	/** 不可被治疗 */
	public boolean cantBeHeal()
	{
		return getStatus(StatusType.CantBeHeal);
	}
	
	
}
