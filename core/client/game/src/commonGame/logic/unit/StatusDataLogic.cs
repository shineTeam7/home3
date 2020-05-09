using System;
using ShineEngine;

/// <summary>
/// 状态数据逻辑
/// </summary>
public class StatusDataLogic:StatusTool
{
	private UnitFightDataLogic _parent;

	public StatusDataLogic(UnitFightDataLogic parent)
	{
		_parent=parent;
		setInfo(AttributeControl.status);
	}

	protected override void toDispatch(bool[] changeSet)
	{
		_parent.onStatusChange(changeSet);
	}

	//--快捷方式--//

	/** 是否活着 */
	public bool isAlive()
	{
		return !getStatus(StatusType.IsDead);
	}

	/** 不可成为目标 */
	public bool cantBeTarget()
	{
		return getStatus(StatusType.CantBeTarget);
	}

	/** 不可攻击 */
	public bool cantBeAttackTarget()
	{
		return getStatus(StatusType.CantBeAttackTarget);
	}

	/** 不可移动 */
	public bool cantMove()
	{
		return getStatus(StatusType.IsDead) || getStatus(StatusType.CantMove) || getStatus(StatusType.Vertigo);
	}

	/** 不可转身 */
	public bool cantTurn()
	{
		return getStatus(StatusType.IsDead) || getStatus(StatusType.CantTurn) || getStatus(StatusType.Vertigo);
	}

	/** 不可攻击 */
	public bool cantAttack()
	{
		return getStatus(StatusType.IsDead) || getStatus(StatusType.CantAttack) || getStatus(StatusType.Vertigo);
	}

	/** 不可施法 */
	public bool cantCast()
	{
		return getStatus(StatusType.IsDead) || getStatus(StatusType.CantCast) || getStatus(StatusType.Vertigo);
	}

	/** 不可成为敌对目标 */
	public bool cantByEmemyTarget()
	{
		return getStatus(StatusType.CantBeEnemyTarget) || getStatus(StatusType.Invincible);
	}

	/** 控制免疫 */
	public bool isControlImmun()
	{
		return getStatus(StatusType.ControlImmun) || getStatus(StatusType.Invincible);
	}

	/** 伤害免疫 */
	public bool isDamageImmun()
	{
		return getStatus(StatusType.DamageImmun) || getStatus(StatusType.Invincible);
	}

	/** 无法被伤害击杀 */
	public bool cantBeKillByDamage()
	{
		return getStatus(StatusType.CantBeKillByDamage) || getStatus(StatusType.Invincible);
	}

	/** 不可被治疗 */
	public bool cantBeHeal()
	{
		return getStatus(StatusType.CantBeHeal);
	}


}