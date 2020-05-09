using System;
using ShineEngine;
using UnityEngine;

/// <summary>
/// 子弹位置逻辑
/// </summary>
public class BulletPosLogic:BulletLogicBase
{
	/** 自己位置 */
	protected PosData _pos=new PosData();

	private ScenePosLogic _scenePosLogic;
	//cast

	private Unit _targetUnit;
	private int _targetUnitVersion;

	private PosData _targetPosSource;
	/** 目标位置 */
	private PosData _targetPos=new PosData();

	/** 最长持续时间 */
	protected int _maxLastTime=-1;
	/** 剩余时间 */
	protected int _lastTime=-1;
	/** 基元使用移速(每毫秒移动值) */
	protected float _useMoveSpeedM;

	/** 使用移速矢量 */
	protected PosData _useMoveSpeedVector=new PosData();
	/** 指定角度 */
	protected DirData _dir=new DirData();

	/** 攻击配置 */
	protected AttackConfig _attackConfig;
	/** 碰撞半径 */
	protected float _hitRadius;
	/** 碰撞长 */
	private float _hitLength;
	/** 碰撞宽 */
	private float _hitWidth;
	/** 是否触碰有效 */
	private bool _hitEnabled=true;

	/** 打击时间经过 */
	private int _hitTimePass=0;
	/** 打击时间间隔 */
	private int _hitDelay=0;
	/** 单位碰撞计数 */
	private IntIntMap _hitTargetNums;

	//temp

	private SList<Unit> _tempUnitList;

	public override void init()
	{
		base.init();

		_scenePosLogic=_scene.pos;

		initPos();

		refreshShowPos();

		_maxLastTime=Global.bulletMaxLastTime * 1000;

		initBullet();
	}

	public override void dispose()
	{
		base.dispose();

		_scenePosLogic=null;
		_targetUnit=null;
		_targetPosSource=null;
		_attackConfig=null;
		_hitEnabled=true;

		_hitTimePass=0;
		_hitDelay=0;

		if(_hitTargetNums!=null)
			_hitTargetNums.clear();

		if(_tempUnitList!=null)
			_tempUnitList.clear();
	}

	public override void onFrame(int delay)
	{
		base.onFrame(delay);

		if(_maxLastTime>0)
		{
			//最长等待时间
			if((_maxLastTime-=delay)<=0)
			{
				_maxLastTime=0;

				bulletOver();
				return;
			}
		}

		onBulletFrame(delay);
	}

	public PosData getPos()
	{
		return _pos;
	}

	public DirData getDir()
	{
		return _dir;
	}

	protected virtual void initBullet()
	{
		float[] castArgs=_levelConfig.castArgs;

		switch(_levelConfig.castType)
		{
			case BulletCastType.Immediately:
			{
				_lastTime=0;
			}
				break;
			case BulletCastType.LockTargetByTime:
			{
				_lastTime=(int)castArgs[0];

				Unit target=_scene.getFightUnit(_data.targetData.targetInstanceID);

				if(target!=null)
				{
					_targetUnit=target;
					_targetUnitVersion=_targetUnit.version;
					_targetPosSource=_targetUnit.pos.getPos();
				}
				else
				{
					//销毁子弹
					bulletOver();
					return;
				}
			}
				break;
			case BulletCastType.LockGroundByTime:
			{
				_lastTime=(int)castArgs[0];

				_scene.fight.getSkillTargetPos(_targetPos,_data.targetData);
			}
				break;
			case BulletCastType.LockTargetBySpeed:
			{
				_useMoveSpeedM=castArgs[0] / 1000f;

				Unit target=_scene.getFightUnit(_data.targetData.targetInstanceID);

				if(target!=null)
				{
					_targetUnit=target;
					_targetUnitVersion=_targetUnit.version;
					_targetPosSource=_targetUnit.pos.getPos();

					_targetPos.copyPos(_targetPosSource);

					calculateSpeed();
				}
				else
				{
					//销毁子弹
					bulletOver();
					return;
				}
			}
				break;
			case BulletCastType.LockGroundBySpeed:
			{
				_useMoveSpeedM=castArgs[0] / 1000f;

				_scene.fight.getSkillTargetPos(_targetPos,_data.targetData);

				calculateSpeed();
			}
				break;
			case BulletCastType.HitStraight:
			case BulletCastType.HitStraightEx:
			{
				if(_levelConfig.castType==BulletCastType.HitStraight)
				{
					_useMoveSpeedM=castArgs[0] *Global.useMoveSpeedRatio / 1000f;
					_lastTime=(int)(castArgs[1]/_useMoveSpeedM);
				}
				else
				{
					Unit attacker=_unit.fight.getAttackerUnit();

					_useMoveSpeedM=(attacker.fight.getSkillVarValue((int)castArgs[0])/1000f) / 1000f;
					_lastTime=(int)((attacker.fight.getSkillVarValue((int)castArgs[1])/1000f)/_useMoveSpeedM);
				}

				_hitRadius=castArgs[2];

				initBulletForHit();
			}
				break;
			case BulletCastType.HitSelfCircle:
			{
				_lastTime=(int)(castArgs[0]);
				_hitRadius=castArgs[1];

				initBulletForHit();
			}
				break;
			case BulletCastType.HitSelfRect:
			{
				_lastTime=(int)(castArgs[0]);
				_hitLength=castArgs[1];
				_hitWidth=castArgs[2];

				initBulletForHit();
			}
				break;
		}
	}

	/** 初始化碰撞系列的子弹所需 */
	protected void initBulletForHit()
	{
		//自身打击跳过
		if(!BaseC.constlist.bulletCast_isSelfHit(_levelConfig.castType))
		{
			calculateSpeedByAngle();
		}

		if(!_unit.isSelfDriveAttackHapen())
		{
			return;
		}

		_attackConfig=AttackConfig.get(_config.attackID);

		_hitTimePass=0;
		_hitDelay=_config.hitDelay;

		if(_hitTargetNums==null)
			_hitTargetNums=new IntIntMap();

		if(_tempUnitList==null)
			_tempUnitList=new SList<Unit>();
	}


	protected virtual void onBulletFrame(int delay)
	{
		switch(_levelConfig.castType)
		{
			case BulletCastType.Immediately:
			{
				bulletHit();
				bulletOver();
			}
				break;
			case BulletCastType.LockTargetByTime:
			{
				if((_lastTime-=delay)<=0)
				{
					_lastTime=0;

					bulletHit();
					bulletOver();
				}
			}
				break;
			case BulletCastType.LockGroundByTime:
			{
				if((_lastTime-=delay)<=0)
				{
					_lastTime=0;
					bulletHit();
					bulletOver();
				}
			}
				break;
			case BulletCastType.LockTargetBySpeed:
			{
				Unit target=getTarget();

				if(target==null)
				{
					bulletOver();
				}
				else
				{
					if(!_targetPos.isEquals(_targetPosSource))
					{
						calculateSpeed();
					}

					moveOnce(delay);
				}
			}
				break;
			case BulletCastType.LockGroundBySpeed:
			{
				moveOnce(delay);
			}
				break;
			case BulletCastType.HitStraight:
			case BulletCastType.HitStraightEx:
			{
				if((_lastTime-=delay)<=0)
				{
					_lastTime=0;
					bulletOver();
					return;
				}

				_scenePosLogic.addPosByVector(_pos,_useMoveSpeedVector,delay);

				refreshShowPos();

				if((_hitTimePass+=delay)>=_hitDelay)
				{
					_hitTimePass=0;

					bulletHitFrame();
				}
			}

				break;
			case BulletCastType.HitSelfCircle:
			case BulletCastType.HitSelfRect:
			{
				if((_lastTime-=delay)<=0)
				{
					_lastTime=0;
					bulletOver();
					return;
				}

				_pos.copyPos(_unit.pos.getPos());

				refreshShowPos();

				if((_hitTimePass+=delay)>=_hitDelay)
				{
					_hitTimePass=0;

					bulletHitFrame();
				}
			}
				break;
		}
	}

	/** 目标单位 */
	private Unit getTarget()
	{
		if(_targetUnit==null)
			return null;

		if(_targetUnit.version!=_targetUnitVersion)
		{
			_targetUnit=null;
			return null;
		}

		return _targetUnit;
	}

	/** 获取自身子弹移动朝向 */
	protected virtual void getMoveDirectionBySelf(DirData re)
	{
		re.copyDir(_unit.pos.getDir());
	}

	private void calculateSpeed()
	{
		_scenePosLogic.calculateDirByPos(_dir,_pos,_targetPos);

		calculateSpeedByAngle();
	}

	private void calculateSpeedByAngle()
	{
		_scenePosLogic.calculateVectorByDir(_useMoveSpeedVector,_dir,_useMoveSpeedM);
	}

	/** 移动一次 */
	private void moveOnce(int delay)
	{
		float sq=_scenePosLogic.calculatePosDistanceSq(_targetPos,_pos);

		//到达
		if(_useMoveSpeedM*delay >= sq)
		{
			moveOver();
		}
		else
		{
			_scenePosLogic.addPosByVector(_pos,_useMoveSpeedVector,delay);
			refreshShowPos();
		}
	}

	private void moveOver()
	{
		bulletHit();
		bulletOver();
	}

	/** 初始化位置和朝向 */
	protected virtual void initPos()
	{
		_pos.copyPos(_unit.pos.getPos());

		if(!_data.isInitiative && _data.targetData.dir!=null)
		{
			_dir.copyDir(_data.targetData.dir);
		}
		else
		{
			//这两种取自己的
			if(_data.targetData.type==SkillTargetType.None || _data.targetData.type==SkillTargetType.AttackScope)
			{
				getMoveDirectionBySelf(_dir);
			}
			else
			{
				_scene.fight.getSkillTargetDirection(_dir,_pos,_data.targetData);
			}
		}

		float[] castOff;
		if((castOff=_config.castOff).Length>0)
		{
			_scenePosLogic.addPolar2D(_pos,castOff[0],_dir);

			if(castOff.Length>1)
			{
				_pos.y+=castOff[1];
			}
		}
	}

	protected void refreshShowPos()
	{
		_bullet.show.setPos(_pos);
	}

	private void bulletHit()
	{
		bool needAttack=false;

		if(_scene.isDriveAll())
		{
			needAttack=true;
		}
		else
		{
			//客户端驱动
			if(_data.isInitiative)
			{
				CUnitBulletHitRequest.create(_unit.instanceID,_data.id,_data.level,_data.targetData).send();
			}
			else
			{
				if(_unit.isSelfDriveAttackHapen())
				{
					needAttack=true;
				}
			}
		}

		if(needAttack)
		{
			if(_config.attackID>0)
			{
				_unit.fight.createAndExecuteAttack(_config.attackID,_data.level,_data.targetData);
			}
		}
	}

	protected void bulletOver()
	{
		_unit.fight.removeBullet(_data.instanceID);
	}

	/** 主动清除子弹 */
	private void bulletOverInitiative()
	{
		//子弹结束
		if(_unit.isSelfControl() && _scene.driveType==SceneDriveType.ServerDriveDamage)
		{
			CUnitRemoveBulletRequest.create(_unit.instanceID,_data.instanceID).send();
		}

		bulletOver();
	}

	//hit

	/** 打击每帧 */
	protected void bulletHitFrame()
	{
		if(_config.attackID<=0)
			return;

		//自己不能控制伤害
		if(!_unit.isSelfDriveAttackHapen())
			return;

		//不该走到这里
		if(BaseC.constlist.bulletCast_isSimpleBullet(_levelConfig.castType))
			return;

		SList<Unit> tempUnitList;

		getFrameHittedUnits(tempUnitList=_tempUnitList);

		if(tempUnitList.isEmpty())
			return;

		//是否可吸收
		bool canAbsorb=!_config.cantAbsorb;

		int eachHitMax=_levelConfig.eachHitNum;
		int maxInfluenceNum=_levelConfig.maxInfluenceNum;

		IntIntMap hitTargetNums=_hitTargetNums;

		foreach(Unit unit in tempUnitList)
		{
			int instanceID=unit.instanceID;

			//打击次数内
			if(eachHitMax==0 || hitTargetNums.get(instanceID)<eachHitMax)
			{
				//吸收子弹
				if(canAbsorb && unit.fight.getStatusLogic().getStatus(StatusType.AbsorbBullet))
				{
					bulletOverInitiative();
					return;
				}

				if(_hitEnabled)
				{
					AttackData aData=_unit.fight.createAttackData(_config.attackID,_data.level,SkillTargetData.createByTargetUnit(instanceID));

					aData.isBulletFirstHit=hitTargetNums.isEmpty();

					hitTargetNums.addValue(instanceID,1);

					_scene.fight.executeAndReleaseAttack(aData);

					//到达上限了
					if(maxInfluenceNum>0 && hitTargetNums.size()>=maxInfluenceNum)
					{
						_hitEnabled=false;

						if(!_config.keepAtMaxHit)
						{
							bulletOverInitiative();
						}

						return;
					}
				}
			}
		}

		_tempUnitList.clear();
	}

	/** 获取碰撞单位 */
	protected virtual void getFrameHittedUnits(SList<Unit> list)
	{
		switch(_levelConfig.castType)
		{
			case BulletCastType.HitSelfRect:
			{
				_scene.fight.getRectFightUnits(list,_pos,_dir,_hitLength,_hitWidth,_hitLength,_unit,_attackConfig.influenceTypeT);
			}
				break;
			default:
			{
				_scene.fight.getCircleFightUnits(list,_pos,_hitRadius,_hitRadius,_unit,_attackConfig.influenceTypeT);
			}
				break;
		}
	}
}