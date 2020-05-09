using System;
using ShineEngine;
using UnityEngine;

/// <summary>
/// 单位AI逻辑
/// </summary>
public class UnitAILogic:UnitLogicBase
{
	/** 逃跑角度(30) */
	private static float escapeDir=MathUtils.fPI/6;

	//参数部分
	/** ai模式 */
	protected int _aiMode=UnitAIModeType.None;

	/** 是否在idle下徘徊 */
	private bool _isWanderOnIdle=false;

	//数据准备

	/** 战斗单位配置 */
	protected FightUnitConfig _fightUnitConfig;
	/** 战斗逻辑 */
	private UnitFightLogic _fightLogic;
	/** 指令逻辑 */
	protected UnitAICommandLogic _commandLogic;
	/** 单位技能组 */
	protected IntObjectMap<SkillData> _unitSkills;

	protected SkillData _defaultSkill;

	//移动部分

	/** 是否徘徊中 */
	private bool _isWandering=false;
	/** 徘徊等待时间(s) */
	private int _wanderWaitTime=-1;

	/** 原点半径 */
	private float _anchorRadius=0f;
	/** 原点是否为单位 */
	private bool _isAnchorUseUnit=false;
	/** AI原点单位 */
	private UnitReference _anchorUnit=new UnitReference();
	/** AI原点 */
	private PosData _anchorPos=new PosData();

	//战斗部分

	/** 仇恨组 */
	private IntLongMap _damageDic=new IntLongMap();

	/** AI状态 */
	protected int _fightState=UnitAIFightStateType.None;
	/** 追击目标 */
	private UnitReference _pursueUnit;

	/** 追击技能目标数据(command:skillTo用) */
	private SkillTargetData _pursueSkillTarget;
	/** 追击技能 */
	private SkillData _pursueSkill;
	/** 追击距离平方 */
	private float _pursueDistanceSq;
	/** 最大追击距离平方 */
	private float _maxPursueDistanceSq;

	//temp
	private PosData _tempPos=new PosData();
	private PosData _tempPos2=new PosData();

	private DirData _tempDir=new DirData();

	public override void init()
	{
		base.init();

		_fightLogic=_unit.fight;
		_commandLogic=_unit.aiCommand;
		_fightUnitConfig=_fightLogic.getFightUnitConfig();

		if(_unit.isSelfControl())
		{
			// setAIMode(_fightUnitConfig.defaultAIMode);
			setAIMode(UnitAIModeType.Base);
			setWanderOnIdle(_fightUnitConfig.isWanderOnIdle);
		}
		else
		{
			setAIMode(UnitAIModeType.None);
		}

		setWanderOnIdle(_fightUnitConfig.isWanderOnIdle);
		_maxPursueDistanceSq=_fightUnitConfig.maxPursueRadius*_fightUnitConfig.maxPursueRadius;

		_unitSkills=_unit.getUnitData().fight.skills;

		FightUnitLevelConfig levelConfig=FightUnitLevelConfig.get(_fightUnitConfig.id,_fightLogic.getLevel());

		if(levelConfig.skills.Length>0)
		{
			_defaultSkill=_unitSkills.get(levelConfig.skills[0].key);
		}
		else
		{
			_defaultSkill=null;
		}
	}

	public override void dispose()
	{
		base.dispose();

		clearFightAI();

		_anchorPos.clear();
		_anchorUnit.clear();
		_isAnchorUseUnit=false;

		_wanderWaitTime=-1;
		_isWandering=false;

		_fightUnitConfig=null;
		_fightLogic=null;
		_unitSkills=null;
		_defaultSkill=null;
	}

	public override void onPiece(int delay)
	{
		base.onPiece(delay);

		//没开AI
		if(_aiMode==UnitAIModeType.None)
			return;

		if(!_commandLogic.isCommandEmpty())
		{
			updateAI(delay);
		}
	}

	public void onThree(int delay)
	{
		//没开AI
		if(_aiMode==UnitAIModeType.None)
			return;

		if(!_commandLogic.isCommandEmpty())
		{
			if(_commandLogic.isCommandCanFight())
			{
				updateFightThree();
			}
		}
	}

	public override void onSecond()
	{
		base.onSecond();

		if(isAIRunning())
		{
			if(_unit.isDriveAll())
			{
				if(_fightState==UnitAIFightStateType.Back && _unit.identity.isMonster())
				{
					if(_unit.getMonsterIdentityLogic().getMonsterConfig().needRecoverAtBack)
					{
						_unit.fight.getAttributeLogic().addHPPercent(Global.monsterBackAddHPPercent);
					}
				}
			}


			if(_wanderWaitTime>0)
			{
				if(--_wanderWaitTime==0)
				{
					_wanderWaitTime=-1;

					wanderRandomMove();
				}
			}
		}
	}

	public void setFightState(int state)
	{
		_fightState=state;
	}

	public int getFightState()
	{
		return _fightState;
	}

	/** 设置AI模式 */
	public void setAIMode(int mode)
	{
		_aiMode=mode;
	}

	/** 设置是否在Idle时徘徊 */
	public void setWanderOnIdle(bool value)
	{
		_isWanderOnIdle=value;
	}

	/** 是否AI执行中 */
	public bool isAIRunning()
	{
		return _aiMode!=UnitAIModeType.None;
	}

	/** 设置当前位置为锚点位置 */
	public void setCurrentAnchorPos()
	{
		setAnchorPos(_unit.pos.getPos(),0f);
	}

	public void setAnchorPos(PosData pos,float radius)
	{
		_anchorPos.copyPos(pos);
		_anchorRadius=radius;
		_isAnchorUseUnit=false;
		_anchorUnit.clear();
	}

	/** 设置锚点单位 */
	public void setAnchorUnit(Unit unit,float radius)
	{
		_anchorRadius=radius;
		_isAnchorUseUnit=true;
		_anchorUnit.setUnit(unit);
	}

	/** 单位技能组 */
	public IntObjectMap<SkillData> getUnitSkills()
	{
		return _unitSkills;
	}

	/** 移动点结束(系统调用) */
	public void onMoveToOver(bool isSuccess)
	{
		if(!isAIRunning())
			return;

		switch(_fightState)
		{
			case UnitAIFightStateType.Idle:
			{
				if(_isWandering)
				{
					wanderWaitTime();
				}

				_commandLogic.onCommandMoveToOverOnIdle(isSuccess);
			}
				break;
			case UnitAIFightStateType.Move:
			{
				_commandLogic.onCommandMoveToOver(isSuccess);
			}
				break;
			case UnitAIFightStateType.Back:
			{
				stopMoveAndReIdle();
			}
				break;
		}
	}

	/** 技能释放结束 */
	public void onSkillOver()
	{
		if(!isAIRunning())
			return;

		if(_fightState==UnitAIFightStateType.Attack)
		{
			if(!_commandLogic.onCommandSkillOver())
			{
				//到追击状态
				_fightState=UnitAIFightStateType.Pursue;
			}
		}
	}

	/** 技能释放结束 */
	public void onSkillBreak()
	{
		if(!isAIRunning())
			return;

		if(_fightState==UnitAIFightStateType.Attack)
		{
			if(!_commandLogic.onCommandSkillOver())
			{
				//到追击状态
				_fightState=UnitAIFightStateType.Pursue;
			}
		}
	}

	/** 清理战斗AI数据 */
	public void clearFightAI()
	{
		_fightState=UnitAIFightStateType.Idle;
		_pursueUnit.clear();
		_pursueSkill=null;
		_pursueDistanceSq=0f;
		_pursueSkillTarget=null;
	}

	/** 刷战斗状态 */
	protected void updateFight()
	{
		Unit target;

		switch(_fightState)
		{
			case UnitAIFightStateType.Pursue:
			{
				if(_commandLogic.isCommandCanFight())
				{
					//有施法数据
					if(_pursueSkillTarget!=null)
					{
						switch(_pursueSkillTarget.type)
						{
							//地面
							case SkillTargetType.Ground:
							{
								toPursue(_pursueSkillTarget.pos);
							}
								break;
							default:
							{
								attackSkillTarget();
							}
								break;
						}
					}
					else
					{
						//目标存在
						if((target=getPursueUnit())!=null)
						{
							toPursue(target.pos.getPos());
						}
					}
				}
				else
				{
					_fightState=UnitAIFightStateType.Idle;
				}
			}
				break;
			case UnitAIFightStateType.Attack:
			{
				if(_commandLogic.isCommandCanFight())
				{
					//没在释放技能
					if(!_fightLogic.isSkilling())
					{
						//回归追击状态
						_fightState=UnitAIFightStateType.Pursue;
						//重选技能
						selectPursueSkill();
					}
				}
				else
				{
					_fightState=UnitAIFightStateType.Idle;
				}
			}
				break;
			case UnitAIFightStateType.Back:
			{
				//没有移动中
				if(!_unit.move.isMoving())
				{
					//向目标移动
					_unit.move.moveTo(_anchorPos);
				}
			}
				break;
		}

		//上文有return
	}

	/** 刷战斗状态 */
	protected void updateFightThree()
	{
		Unit target;

		switch(_fightState)
		{
			case UnitAIFightStateType.Idle:
			{
				if(checkNeedBack())
					return;

				if((target=searchTarget())!=null)
				{
					_isWandering=false;

					_fightState=UnitAIFightStateType.Pursue;
					setPursueUnit(target);
					selectPursueSkill();
					continuePursue();
				}
				else
				{
					if(needWander())
					{
						_isWandering=true;

						if(_wanderWaitTime==-1)
						{
							if(!_unit.move.isMoving())
							{
								if(_unit.move.isAllFreeNow())
								{
									wanderWaitTime();
								}
							}
						}
					}
				}
			}
				break;
			case UnitAIFightStateType.Pursue:
			{
				if(_commandLogic.isCommandCanFight())
				{
					//无施法数据
					if(_pursueSkillTarget==null)
					{
						//目标丢失或者影响类型不匹配
						if((target=getPursueUnit())==null || !_fightLogic.checkTargetInfluence(target,_fightUnitConfig.attackInfluenceTypeT))
						{
							//又找到目标
							if((target=searchTarget())!=null)
							{
								setPursueUnit(target);
								selectPursueSkill();
								continuePursue();
							}
							else
							{
								pursueOver();
							}
						}
						else
						{
							if(_commandLogic.isCommandCanChangePursueUnit())
							{
								//1秒3次
								if(_fightUnitConfig.needHateSwitchTarget)
								{
									//寻找其他目标
									reSelectPursueTarget();

									//目标变化
									if(_pursueUnit.getUnit()!=target)
									{
										selectPursueSkill();
										continuePursue();
										return;
									}
								}
							}

							if(_commandLogic.isCommandCanChangePursueSkill())
							{
								SkillData purseSkill=_pursueSkill;
								selectPursueSkill();

								if(_pursueSkill!=purseSkill)
								{
									continuePursue();
									return;
								}
							}

							//toPursue(target.pos.getPos());
						}
					}
				}
				else
				{
					_fightState=UnitAIFightStateType.Idle;
				}
			}
				break;
		}
	}

	/** 是否需要徘徊 */
	protected bool needWander()
	{
		return _isWanderOnIdle && _commandLogic.isCommandNeedWander();
	}

	/** 更新AI(1秒10次) */
	public void updateAI(int delay)
	{
		if(_commandLogic.isCommandCanFight())
		{
			updateFight();
		}

		_commandLogic.updateCommand(delay);
	}

	/** 逃跑更新 */
	public void updateEscape()
	{
		switch(_fightState)
		{
			case UnitAIFightStateType.Idle:
			{
				Unit target;
				if((target=searchTarget())!=null)
				{
					_fightState=UnitAIFightStateType.Move;
					setPursueUnit(target);
					toPursueForEscape(target);
				}
			}
				break;
			case UnitAIFightStateType.Move:
			{
				//没有移动中
				if(!_unit.move.isMoving())
				{
					Unit target=getPursueUnit();

					if(target==null)
					{
						stopMoveAndReIdle();
					}
					else
					{
						if(_scene.pos.calculatePosDistanceSq2D(_unit.pos.getPos(),target.pos.getPos())>=_maxPursueDistanceSq)
						{
							stopMoveAndReIdle();
						}
						else
						{
							toPursueForEscape(target);
						}
					}
				}
			}
				break;
		}
	}

	/** 停止移动并回归idle状态 */
	public void stopMoveAndReIdle()
	{
		_unit.move.stopMove();
		reIdle();
	}

	/** 回归idle状态 */
	public void reIdle()
	{
		if(_unit.isDriveAll())
		{
			if(_unit.identity.isMonster() && _unit.getMonsterIdentityLogic().getMonsterConfig().needRecoverAtBack)
			{
				//补满血蓝
				_unit.fight.getAttributeLogic().fillHpMp();
			}
		}

		clearFightAI();
		_fightState=UnitAIFightStateType.Idle;

		if(needWander())
		{
			startWander();
		}
	}

	/** 设置攻击目标 */
	private void setPursueUnit(Unit target)
	{
		_pursueUnit.setUnit(target);
	}

	/** 开始追击 */
	protected void startPursue(Unit target)
	{
		_fightState=UnitAIFightStateType.Move;
		setPursueUnit(target);
		selectPursueSkill();
		continuePursue();
	}

	/** 设置追击技能 */
	public void setPursueSkill(SkillData data)
	{
		_pursueSkill=data;

		if(data!=null)
		{
			float d=_fightLogic.getSkillUseDistance(data.id,data.level);
			_pursueDistanceSq=d*d;
		}
		else
		{
			_pursueDistanceSq=0f;
		}
	}

	/** 取攻击目标 */
	private Unit getPursueUnit()
	{
		return _pursueUnit.getUnit();
	}

	private Unit searchTarget()
	{
		float radius;

		if((radius=_fightUnitConfig.initiativeAttackRadius)<=0)
			return null;

		if(!_commandLogic.isCommandCanSearchTarget())
			return null;

		return _scene.fight.getNearestFightUnits(_unit.pos.getPos(),radius,0,_unit,_fightUnitConfig.attackInfluenceTypeT);
	}

	/** 重新选择追击目标 */
	private void reSelectPursueTarget()
	{
		Unit target=getPursueUnit();

		if(target==null)
			return;

		Unit re=null;
		float reCost=0f;

		bool[] influences=_fightUnitConfig.attackInfluenceTypeT;

		ScenePosLogic posLogic=_scene.pos;
		float sq=_fightUnitConfig.initiativeAttackRadiusT;

		PosData pos=_unit.pos.getPos();

		Unit[] values;
		Unit k;

		for(int i=(values=_scene.getFightUnitDic().getValues()).Length - 1;i>=0;--i)
		{
			if((k=values[i])!=null)
			{
				PosData kPos=k.pos.getPos();

				float q;
				//在范围内
				if((q=posLogic.calculatePosDistanceSq2D(kPos,pos))<=sq)
				{
					//符合影响类型
					if(_unit.fight.checkTargetInfluence(k,influences))
					{
						float unitHateSwitchCost=getUnitHateSwitchCost(k);
						float dis=(float)Math.Sqrt(q);
						float distanceCost=0f;
						if(dis<Global.unitSwitchBaseDistance)
						{
							distanceCost=Global.unitSwitchDistanceCost*(Global.unitSwitchBaseDistance-dis);
						}

						float switchCost=target==k ? 0f: Global.unitSwitchFixedCost;

						float cost=unitHateSwitchCost+distanceCost+switchCost;

						if(re==null || cost>reCost)
						{
							re=k;
							reCost=cost;
						}
					}
				}
			}
		}

		if(re!=null)
		{
			//设置新的追击目标
			setPursueUnit(re);
		}
	}

	/** 获取目标仇恨切换成本 */
	private float getUnitHateSwitchCost(Unit target)
	{
		float re=_damageDic.get(target.instanceID) * 1000 / _unit.fight.getAttributeLogic().getHpMax();
		return re*Global.unitHateDamagePercentRatio*Global.unitSwitchHateCost;
	}

	/** 选择追击技能 */
	private void selectPursueSkill()
	{
		if(!_commandLogic.isCommandCanChangePursueSkill())
			return;

		//必须有追击目标
		if(_pursueUnit.isEmpty())
		{
			setPursueSkill(null);
			return;
		}

		SkillData re=toSelectPursueSkill();

		if(re==null)
			re=_defaultSkill;

		if(re==_pursueSkill)
			return;

		setPursueSkill(re);
	}

	/** 常规挑选追击技能(可复写) */
	protected virtual SkillData toSelectPursueSkill()
	{
		SkillData[] values;
		SkillData v;

		SkillData re=null;
		int priority=0;

		SkillConfig config;
		int tp;

		for(int i=(values=_unitSkills.getValues()).Length-1;i>=0;--i)
		{
			if((v=values[i])!=null)
			{
				//可用技能
				if(_fightLogic.checkCanUseSkill(v.id,v.level))
				{
					config=SkillConfig.get(v.id);
					//影响类型匹配
					//TODO:这里补充，目标的减益技能或自己的增益技能

					if((tp=config.aiPriority)>=0)// && _fightLogic.checkTargetInfluence(_pursueUnit,config.influenceType)
					{
						if(re==null || tp>priority)
						{
							re=v;
							priority=tp;
						}
					}
				}
			}
		}

		return re;
	}

	/** 继续追击 */
	private void continuePursue()
	{
		//没有追击技能
		if(_pursueSkill==null)
			return;

		_fightState=UnitAIFightStateType.Pursue;

		Unit target=getPursueUnit();

		if(target==null)
			return;

		toPursue(target.pos.getPos());
	}

	private void toPursue(PosData pos)
	{
		//没有追击技能
		if(_pursueSkill==null)
			return;

		if(checkNeedBack())
			return;

		float d;
		//距离够了
		if((d=_unit.pos.calculateDistanceSq(pos))<=_pursueDistanceSq)
		{
			pursueAttack();
		}
		else
		{
			//没有移动中
			if(!_unit.move.isMoving())
			{
				//向目标移动
				_unit.move.moveTo(pos);
			}
			else
			{
				//TODO:根据位置改变路线
			}
		}
	}

	/** 逃跑追击过程 */
	private void toPursueForEscape(Unit target)
	{
		if(!_unit.move.canMoveNow())
			return;

		float length=_fightUnitConfig.initiativeAttackRadius;

		float lengthQ=length*length*0.9f;//90%即可

		PosData from=_unit.pos.getPos();
		_scene.pos.calculateDirByPos(_tempDir,target.pos.getPos(),from);

		float dir=_tempDir.direction;

		_tempPos2.copyPos(from);
		float dis=0f;

		for(int i=0;i<7;i++)
		{
			int n=(i+1)>>1;
			bool isN=(i & 1)==1;

			if(isN)
			{
				dir-=n*escapeDir;
			}
			else
			{
				dir+=n*escapeDir;
			}

			dir=dir-(escapeDir/2)+MathUtils.randomFloat()*escapeDir;

			dir=MathUtils.directionCut(dir);

			_scene.pos.findRayPos(_unit.move.getMoveType(),_tempPos,from,dir,length);

			float tt;
			//尺寸达标
			if((tt=_scene.pos.calculatePosDistanceSq2D(from,_tempPos))>=lengthQ)
			{
				dis=tt;
				_tempPos2.copyPos(_tempPos);
				break;
			}
			else
			{
				if(tt>dis)
				{
					dis=tt;
					_tempPos2.copyPos(_tempPos);
				}
			}
		}

		//死胡同了
		if(dis==0f)
		{
			//TODO:锁定处理
		}
		else
		{
			_unit.move.moveTo(_tempPos2);
		}
	}

	/** 追击结束 */
	private void pursueOver()
	{
		clearFightAI();

		_commandLogic.onCommandPursueOver();
	}

	/** 退回 */
	public void reBack()
	{
		clearFightAI();
		_fightState=UnitAIFightStateType.Back;

		if(_isAnchorUseUnit)
		{
			Unit unit=_anchorUnit.getUnit();
			if(unit!=null)
			{
				_unit.move.moveToUnit(unit,_anchorRadius);
			}
			else
			{
				setCurrentAnchorPos();
				stopMoveAndReIdle();
			}
		}
		else
		{
			_unit.move.moveTo(_anchorPos,_anchorRadius);
		}
	}

	/** 检查是否需要回归 */
	private bool checkNeedBack()
	{
		if(_fightState==UnitAIFightStateType.Back)
			return false;

		if(_commandLogic.isCommandNeedCheckBack())
		{
			if(_isAnchorUseUnit)
			{
				Unit unit=_anchorUnit.getUnit();

				if(unit==null)
				{
					//标记自己
					setCurrentAnchorPos();
				}
				else
				{
					//超了
					if(_unit.pos.calculateDistanceSq(unit.pos.getPos())>= _maxPursueDistanceSq)
					{
						reBack();
						return true;
					}
				}
			}
			else
			{
				//超了
				if(_unit.pos.calculateDistanceSq(_anchorPos)>= _maxPursueDistanceSq)
				{
					reBack();
					return true;
				}
			}
		}

		return false;
	}

	/** 追击攻击 */
	private void pursueAttack()
	{
		if(_pursueSkill==null)
			return;

		//技能目标
		if(_pursueSkillTarget!=null)
		{
			attackSkillTarget();
		}
		//技能单位
		else
		{
			Unit pursueUnit;
			if((pursueUnit=getPursueUnit())!=null)
			{
				_fightState=UnitAIFightStateType.Attack;
				_commandLogic.setCurrentCommandSkillIndex();
				_unit.fight.useSkillTo(_pursueSkill,pursueUnit.instanceID);
			}
			else
			{
				pursueOver();
			}
		}
	}

	private void attackSkillTarget()
	{
		if(_fightLogic.checkCanUseSkill(_pursueSkill))
		{
			_fightState=UnitAIFightStateType.Attack;
			_commandLogic.setCurrentCommandSkillIndex();
			_unit.fight.useSkill(_pursueSkill,_pursueSkillTarget);
		}
	}


	//--//

	/** 开始徘徊 */
	private void startWander()
	{
		//全free的时候
		if(!_unit.move.isAllFreeNow())
			return;

		if(!_isWanderOnIdle)
			return;

		_isWandering=true;
		wanderWaitTime();
	}

	/** 是否free中 */
	public bool isIdle()
	{
		return _fightState==UnitAIFightStateType.Idle;
	}

	/** 徘徊等待时间结束 */
	private void wanderRandomMove()
	{
		if(!needWander())
			return;

		//TODO:指令的update

		_scene.pos.getRandomWalkablePos(_unit.move.getMoveType(),_tempPos,_unit.pos.getPos(),Global.wanderMoveRadius);

		_unit.move.moveTo(UnitMoveType.Walk,_tempPos);
	}

	/** 开始徘徊倒计时 */
	private void wanderWaitTime()
	{
		if(!needWander())
			return;

		_wanderWaitTime=MathUtils.randomRange(Global.wanderWaitTimeMin,Global.wanderWaitTimeMax);
	}

	/** 受到攻击 */
	public void beAttack(Unit attacker)
	{
		if(_fightUnitConfig.passiveAttackRadius<=0)
			return;

		if(!_commandLogic.isCommandCanSearchTarget())
			return;

		if(!_unit.fight.checkIsEnemy(attacker))
			return;

		startPursue(attacker);
	}

	/** 根据指令目标，设置追击 */
	public void setPursueCommandTarget(SkillTargetData tData)
	{
		switch(tData.type)
		{
			case SkillTargetType.Single:
			{
				Unit targetUnit=_scene.getFightUnit(tData.targetInstanceID);

				if(targetUnit!=null)
				{
					setPursueUnit(targetUnit);
				}
				else
				{
					_commandLogic.commandOver();
				}
			}
				break;
			default:
			{
				_pursueSkillTarget=tData;
			}
				break;
		}
	}
}