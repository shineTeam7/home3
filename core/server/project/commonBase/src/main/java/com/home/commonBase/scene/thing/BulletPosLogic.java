package com.home.commonBase.scene.thing;

import com.home.commonBase.config.game.AttackConfig;
import com.home.commonBase.constlist.generate.BulletCastHitColliderType;
import com.home.commonBase.constlist.generate.BulletCastType;
import com.home.commonBase.constlist.generate.MapMoveType;
import com.home.commonBase.constlist.generate.StatusType;
import com.home.commonBase.constlist.system.SceneDriveType;
import com.home.commonBase.data.scene.base.DirData;
import com.home.commonBase.data.scene.base.PosData;
import com.home.commonBase.data.scene.fight.SkillTargetData;
import com.home.commonBase.dataEx.scene.AttackData;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.global.Global;
import com.home.commonBase.scene.base.BulletLogicBase;
import com.home.commonBase.scene.base.Unit;
import com.home.commonBase.scene.scene.ScenePosLogic;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.IntIntMap;
import com.home.shine.support.collection.SList;

/** 子弹位置逻辑 */
public class BulletPosLogic extends BulletLogicBase
{
	/** 子弹位置 */
	private PosData _pos=new PosData();
	
	private ScenePosLogic _scenePosLogic;
	
	//cast
	
	private Unit _targetUnit;
	private int _targetUnitVersion;
	
	/** 目标位置源 */
	private PosData _targetPosSource;
	/** 目标位置 */
	private PosData _targetPos=new PosData();
	
	/** 最长持续时间 */
	private int _maxLastTime=-1;
	/** 剩余时间 */
	private int _lastTime=-1;
	/** 基元使用移速(每毫秒移动值) */
	private float _useMoveSpeedM;
	/** 配装障碍物类型 */
	private int _hitColliderType=BulletCastHitColliderType.Ignore;
	
	/** 使用移速矢量 */
	private PosData _useMoveSpeedVector=new PosData();
	/** 指定角度 */
	private DirData _dir=new DirData();
	
	/** 攻击配置 */
	private AttackConfig _attackConfig;
	/** 碰撞半径 */
	private float _hitRadius;
	/** 碰撞长 */
	private float _hitLength;
	/** 碰撞宽 */
	private float _hitWidth;
	
	/** 是否触碰有效 */
	private boolean _hitEnabled=true;
	/** 打击时间经过 */
	private int _hitTimePass=0;
	/** 打击时间间隔 */
	private int _hitDelay=0;
	/** 单位碰撞计数 */
	private IntIntMap _hitTargetNums;
	
	//temp
	
	private SList<Unit> _tempUnitList;
	
	@Override
	public void init()
	{
		super.init();
		
		_scenePosLogic=_scene.pos;
		
		initPos();
		
		if(_scene.driveType==SceneDriveType.ServerDriveDamage)
		{
			_maxLastTime=Global.bulletMaxLastTime*1000;
			return;
		}
		
		initBullet();
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		
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
	
	@Override
	public void onFrame(int delay)
	{
		if(_scene.driveType==SceneDriveType.ServerDriveDamage)
		{
			if(_maxLastTime>0)
			{
				if((_maxLastTime-=delay)<=0)
				{
					_maxLastTime=-1;
					
					bulletOver();
				}
			}
		}
		else
		{
			onBulletFrame(delay);
		}
	}
	
	/** 初始化位置和朝向 */
	protected void initPos()
	{
		_pos.copyPos(_unit.pos.getPos());
		_scene.fight.getSkillTargetDirection(_dir,_pos,_data.targetData);
		
		float[] castOff;
		if((castOff=_config.castOff).length>0)
		{
			_scenePosLogic.addPolar2D(_pos,castOff[0],_dir);
			
			//服务器不需要y off
			
			//if(castOff.length>1)
			//{
			//	_pos.y+=castOff[1];
			//}
		}
	}
	
	/** 初始化子弹 */
	protected void initBullet()
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
				_useMoveSpeedM=castArgs[0] * Global.useMoveSpeedRatio / 1000f;
				
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
				_useMoveSpeedM=castArgs[0] * Global.useMoveSpeedRatio / 1000f;
				
				_scene.fight.getSkillTargetPos(_targetPos,_data.targetData);
				
				calculateSpeed();
			}
				break;
			case BulletCastType.HitStraight:
			case BulletCastType.HitStraightEx:
			{
				if(_levelConfig.castType==BulletCastType.HitStraight)
				{
					_useMoveSpeedM=castArgs[0] * Global.useMoveSpeedRatio / 1000f;
					_lastTime=(int)(castArgs[1]/_useMoveSpeedM);
				}
				else
				{
					Unit attacker=_unit.fight.getAttackerUnit();
					
					_useMoveSpeedM=(attacker.fight.getSkillVarValue((int)castArgs[0])/1000f) / 1000f;
					_lastTime=(int)((attacker.fight.getSkillVarValue((int)castArgs[1])/1000f)/_useMoveSpeedM);
				}
				
				_hitRadius=castArgs[2];
				_hitColliderType=castArgs.length>3 ? (int)castArgs[3] : BulletCastHitColliderType.Ignore;
				
				if(CommonSetting.isClient || _scene.isServerDriveAttackHappen())
				{
					//_scene.unit.getSkillTargetDirection(_dir,_pos,_data.targetData);
					//calculateSpeedByAngle();
					
					initBulletForHit();
				}
			}
				break;
			case BulletCastType.HitSelfCircle:
			{
				_lastTime=(int)(castArgs[0]);
				_hitRadius=castArgs[1];
				
				if(CommonSetting.isClient || _scene.isServerDriveAttackHappen())
				{
					initBulletForHit();
				}
			}
				break;
			case BulletCastType.HitSelfRect:
			{
				_lastTime=(int)(castArgs[0]);
				_hitLength=castArgs[1];
				_hitWidth=castArgs[2];
				
				if(CommonSetting.isClient || _scene.isServerDriveAttackHappen())
				{
					initBulletForHit();
				}
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
		
		_attackConfig=AttackConfig.get(_config.attackID);
		
		_hitTimePass=0;
		_hitDelay=_config.hitDelay;
		
		if(_hitTargetNums==null)
			_hitTargetNums=new IntIntMap();
		
		if(_tempUnitList==null)
			_tempUnitList=new SList<>(Unit[]::new);
	}
	
	/** 目标单位 */
	private Unit getTarget()
	{
		if(_targetUnit==null)
		{
			return null;
		}
		
		if(_targetUnit.version!=_targetUnitVersion)
		{
			_targetUnit=null;
			return null;
		}
		
		return _targetUnit;
	}
	
	/** 子弹每帧 */
	protected void onBulletFrame(int delay)
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
				_lastTime-=delay;
				
				if(_lastTime<=0)
				{
					_lastTime=0;
					
					bulletHit();
					bulletOver();
				}
			}
			break;
			case BulletCastType.LockGroundByTime:
			{
				_lastTime-=delay;
				
				if(_lastTime<=0)
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
					//不相等了
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
				
				if(CommonSetting.isClient || _scene.isServerDriveAttackHappen())
				{
					_scenePosLogic.addPosByVector(_pos,_useMoveSpeedVector,delay);
					
					switch(_hitColliderType)
					{
						case BulletCastHitColliderType.Remove:
						{
							//TODO:子弹需要跟随主角的移动方式
							
							if(!_scenePosLogic.isPosEnabled(MapMoveType.Land,_pos))
							{
								bulletOverInitiative();
								return;
							}
						}
							break;
						case BulletCastHitColliderType.Bounce:
						{
							Ctrl.errorLog("暂未实现反弹");
						}
							break;
					}
					
					if((_hitTimePass+=delay)>=_hitDelay)
					{
						_hitTimePass=0;
						
						bulletHitFrame();
					}
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
				
				if(CommonSetting.isClient || _scene.isServerDriveAttackHappen())
				{
					if((_hitTimePass+=delay)>=_hitDelay)
					{
						_hitTimePass=0;
						
						_pos.copyPos(_unit.pos.getPos());
						_dir.copyDir(_unit.pos.getDir());
						
						bulletHitFrame();
					}
				}
			}
			break;
		}
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
		float sq=_scenePosLogic.calculatePosDistance(_targetPos,_pos);
		
		//到达
		if(_useMoveSpeedM*delay >= sq)
		{
			moveOver();
		}
		else
		{
			_scenePosLogic.addPosByVector(_pos,_useMoveSpeedVector,delay);
		}
	}
	
	private void moveOver()
	{
		bulletHit();
		bulletOver();
	}
	
	protected void bulletHitFrame()
	{
		if(_config.attackID<=0)
			return;
		
		//自己不能控制伤害
		if(!_unit.isSelfDriveAttackHappen())
			return;
		
		SList<Unit> tempUnitList;
		
		getFrameHitUnits(tempUnitList=_tempUnitList);
		
		if(tempUnitList.isEmpty())
			return;
		
		//是否可吸收
		boolean canAbsorb=!_config.cantAbsorb;
		
		Unit[] values=tempUnitList.getValues();
		Unit unit;
		
		int eachHitMax=_levelConfig.eachHitNum;
		int maxInfluenceNum=_levelConfig.maxInfluenceNum;
		
		IntIntMap hitTargetNums=_hitTargetNums;
		
		for(int i=0,len=tempUnitList.size();i<len;i++)
		{
			unit=values[i];
			
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
		
		tempUnitList.clear();
	}
	
	private void bulletHit()
	{
		boolean needAttack=false;
		
		if(_scene.isDriveAll())
		{
			needAttack=true;
		}
		else
		{
			//客户端驱动
			if(_data.isInitiative)
			{
				_unit.fight.sendClientBulletHit(_data.id,_data.level,_data.targetData);
			}
			else
			{
				if(_unit.isSelfDriveAttackHappen())
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
	
	private void bulletOver()
	{
		_unit.fight.removeBullet(_data.instanceID,false,false);
	}
	
	
	/** 主动子弹结束 */
	public void bulletOverInitiative()
	{
		_unit.fight.removeBullet(_data.instanceID,true,true);
	}
	
	/** 主动子弹结束 */
	public void bulletOverInitiativeForClient()
	{
		_unit.fight.removeBullet(_data.instanceID,true,false);
	}
	
	/** 获取碰撞单位 */
	protected void getFrameHitUnits(SList<Unit> list)
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
