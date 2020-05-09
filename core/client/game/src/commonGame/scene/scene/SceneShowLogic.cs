using System;
using ShineEngine;
using UnityEngine;

/// <summary>
/// 场景显示逻辑基类
/// </summary>
public class SceneShowLogic:SceneLogicBase
{
	private GameObject _unitLayer;
	private GameObject _effectLayer;

	private Transform _unitHeadRoot;
	private Transform _frontUIRoot;

	public SceneShowLogic()
	{

	}

	public override void construct()
	{

	}

	public override void init()
	{
		_unitLayer=new GameObject("unitLayer");
		_effectLayer=new GameObject("effectLayer");

		if ((_unitHeadRoot=UIControl.getUIContainer().transform.Find("sceneUnitHeadRoot")) != null)
		{
			_unitHeadRoot.gameObject.SetActive(true);
		}

		if ((_frontUIRoot=UIControl.getUIContainer().transform.Find("sceneFrontUIRoot")) != null)
		{
			_frontUIRoot.gameObject.SetActive(true);
		}

		GameC.audio.playMusic(_scene.getMapConfig().musicT);
	}

	public override void dispose()
	{
		_unitLayer=null;
		_effectLayer=null;

		_unitHeadRoot=null;
		_frontUIRoot=null;
	}

	/** 获取单位头根 */
	public Transform getUnitHeadRoot()
	{
		return _unitHeadRoot;
	}

	/** 获取场景UI前层根 */
	public Transform getFrontUIRoot()
	{
		return _frontUIRoot;
	}

	public override void onFrame(int delay)
	{

	}

	/** 单位聊天 */
	public virtual void onUnitChat(Unit unit,ChatData data)
	{

	}

	public override void preRemove()
	{
		base.preRemove();

		GameC.audio.stopMusic();
	}

	public GameObject getUnitLayer()
	{
		return _unitLayer;
	}

	public GameObject getEffectLayer()
	{
		return _effectLayer;
	}

	public virtual void onAttackDamage(Unit from,AttackConfig config,SkillTargetData targetData)
	{
		if(config.attackEffect>0)
		{
			Unit target=null;

			switch(targetData.type)
			{
				case SkillTargetType.None:
				{
					target=from;
				}
					break;
				case SkillTargetType.Single:
				{
					target=_scene.getFightUnit(targetData.targetInstanceID);
				}
					break;
				case SkillTargetType.Ground:
				{

				}
					break;
				default:
				{
					target=from;
				}
					break;
			}

			if(target!=null)
			{
				target.show.playEffect(config.attackEffect);
			}

		}

		if(config.attackGroundEffect>0)
		{
			PosData pos=null;
			DirData dir=null;

			switch(targetData.type)
			{
				case SkillTargetType.None:
				{
					pos=from.pos.getPos();
					dir=from.pos.getDir();
				}
					break;
				case SkillTargetType.Single:
				{
					Unit target=_scene.getFightUnit(targetData.targetInstanceID);

					if(target!=null)
					{
						pos=target.pos.getPos();
						dir=target.pos.getDir();
					}
				}
					break;
				case SkillTargetType.Ground:
				{
					pos=targetData.pos;
					dir=from.pos.getDir();
				}
					break;
				default:
				{
					pos=from.pos.getPos();
					dir=from.pos.getDir();
				}
					break;
			}

			if(pos!=null)
			{
				playSceneEffect(config.attackGroundEffect,pos,dir);
			}
		}
	}

	/** 播放场景特效 */
	public void playSceneEffect(int effectID,PosData pos,DirData dir)
	{
		_scene.unitFactory.createAddSceneEffectBySignEffect(effectID,pos,dir);
	}

	/** 单位受伤 */
	public virtual void onUnitDamage(Unit unit,Unit from,AttackConfig config,DamageOneData data)
	{
		//需要显示
		if(unit.isSelfControl() || from.isSelfControl())
		{
			foreach(DIntData v in data.damages)
			{
				Ctrl.print("受击伤害",v.value);

				showOneDamage(unit,v.key,v.value);
			}
		}
	}

	public virtual void showOneDamage(Unit unit,int damageType,int damageValue)
	{

	}
}