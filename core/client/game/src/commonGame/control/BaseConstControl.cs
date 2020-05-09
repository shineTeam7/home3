using System;
using ShineEngine;
using UnityEngine;
using UnityEngine.AI;

/// <summary>
/// 常量方法控制
/// </summary>
public class BaseConstControl
{
	/// <summary>
	/// 是否为简单子弹
	/// </summary>
	public virtual bool bulletCast_isSimpleBullet(int type)
	{
		return (type>=BulletCastType.Immediately && type<=BulletCastType.LockGroundBySpeed) || type==BulletCastType.StraightForShow;
	}

	/// <summary>
	/// 是否自身碰撞子弹
	/// </summary>
	public virtual bool bulletCast_isSelfHit(int type)
	{
		return type==BulletCastType.HitSelfCircle || type==BulletCastType.HitSelfRect;
	}

	/** 是否需要返回 */
	public virtual bool roleGroupHandleResult_needReback(int type)
	{
		return type==RoleGroupHandleResultType.Agree || type==RoleGroupHandleResultType.Refuse;
	}

	/// <summary>
	/// 是否是单位类型
	/// </summary>
	public virtual bool sceneElement_isUnit(int type)
	{
		switch(type)
		{
			case SceneElementType.Point:
			case SceneElementType.Region:
				return false;
		}

		return true;
	}

	/// <summary>
	/// 是否由客户端创建初始化创建的单位
	/// </summary>
	public virtual bool sceneElement_isClientCreate(int type)
	{
		switch(type)
		{
			case SceneElementType.Npc:
			case SceneElementType.SceneEffect:
				return true;
		}

		return false;
	}

	/// <summary>
	/// 是否为限定进入场景
	/// </summary>
	public virtual bool sceneInstance_isSignedIn(int type)
	{
		switch(type)
		{
			case SceneInstanceType.SingleInstance:
			case SceneInstanceType.LinedSingleInstance:
				return false;
		}

		return true;
	}

	/// <summary>
	/// 是否为多实例场景
	/// </summary>
	public virtual bool sceneInstance_isMultiInstance(int type)
	{
		return type!=SceneInstanceType.SingleInstance && type!=SceneInstanceType.SinglePlayerBattle;
	}

	/// <summary>
	/// 是否为限定场景
	/// </summary>
	public virtual bool sceneInstance_isFinite(int type)
	{
		return type==SceneInstanceType.PreBattle || type==SceneInstanceType.FiniteBattle || type==SceneInstanceType.FiniteBattleWithFrameSync;
	}

	/// <summary>
	/// 是否是简版场景
	/// </summary>
	public virtual bool sceneInstance_isSimple(int type)
	{
		return type==SceneInstanceType.PreBattle;
	}

	/// <summary>
	/// 客户端是否可主动申请进入
	/// </summary>
	public virtual bool sceneInstance_canClientApplyEnter(int type)
	{
		switch(type)
		{
			case SceneInstanceType.SingleInstance:
			case SceneInstanceType.SinglePlayerBattle:
			case SceneInstanceType.LinedSingleInstance:
			{
				return true;
			}
		}

		return false;
	}

	/// <summary>
	/// 获取消耗类型对应属性,如不是属性,返回-1
	/// </summary>
	public virtual int skillCost_getCostAttributeType(int type)
	{
		switch(type)
		{
			case SkillCostType.Hp:
				return AttributeType.Hp;
			case SkillCostType.Mp:
				return AttributeType.Mp;
		}

		return -1;
	}

	/// <summary>
	/// 是否需要检查施法距离
	/// </summary>
	public virtual bool skillTarget_needCheckDistance(int type)
	{
		switch(type)
		{
			case SkillTargetType.Single:
			case SkillTargetType.Ground:
				return true;
		}

		return false;
	}

	/// <summary>
	/// 是否是目标的类型
	/// </summary>
	public virtual bool skillVarSource_isTarget(int type)
	{
		switch(type)
		{
			case SkillVarSourceType.TargetAttribute:
			case SkillVarSourceType.TargetLevel:
			case SkillVarSourceType.TargetCurrentAttributePercent:
			case SkillVarSourceType.TargetCurrentAttributeLostPercent:
			case SkillVarSourceType.TargetBuffFloor:
			{
				return true;
			}
		}

		return false;
	}

	/// <summary>
	/// 是否可主动寻找战斗目标
	/// </summary>
	public virtual bool unitAICommand_canInitiativeSearchTarget(int type)
	{
		switch(type)
		{
			case UnitAICommandType.Protect:
			case UnitAICommandType.AttackMoveTo:
				return true;
		}

		return false;
	}

	/// <summary>
	/// 是否可参与战斗
	/// </summary>
	public virtual bool unit_canFight(int type)
	{
		switch(type)
		{
			case UnitType.Character:
			case UnitType.Monster:
			case UnitType.Pet:
			case UnitType.Puppet:
			case UnitType.Vehicle:
			case UnitType.Building:
				return true;
		}

		return false;
	}

	/// <summary>
	/// 是否主单位
	/// </summary>
	public virtual bool unit_isMUnit(int type)
	{
		switch(type)
		{
			case UnitType.Character:
			case UnitType.Pet:
				return true;
		}

		return false;
	}

	/// <summary>
	/// 是否需要单位头
	/// </summary>
	public virtual bool unit_needHead(int type)
	{
		switch(type)
		{
			case UnitType.Character:
			case UnitType.Monster:
			case UnitType.Pet:
			case UnitType.Npc:
				return true;
		}

		return false;
	}

	/// <summary>
	/// 地图移动类型，获取mask
	/// </summary>
	public virtual int mapMoveType_getMask(int type)
	{
		//TODO:后续补充
		return NavMesh.AllAreas;
	}
}