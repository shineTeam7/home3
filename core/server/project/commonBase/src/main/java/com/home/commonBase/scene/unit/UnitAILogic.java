package com.home.commonBase.scene.unit;

import com.home.commonBase.config.game.FightUnitConfig;
import com.home.commonBase.config.game.FightUnitLevelConfig;
import com.home.commonBase.config.game.SkillConfig;
import com.home.commonBase.constlist.generate.SkillTargetType;
import com.home.commonBase.constlist.generate.UnitAIFightStateType;
import com.home.commonBase.constlist.generate.UnitAIModeType;
import com.home.commonBase.constlist.generate.UnitMoveType;
import com.home.commonBase.data.scene.base.DirData;
import com.home.commonBase.data.scene.base.PosData;
import com.home.commonBase.data.scene.base.SkillData;
import com.home.commonBase.data.scene.fight.SkillTargetData;
import com.home.commonBase.dataEx.scene.UnitReference;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.global.Global;
import com.home.commonBase.scene.base.Unit;
import com.home.commonBase.scene.base.UnitLogicBase;
import com.home.commonBase.scene.scene.ScenePosLogic;
import com.home.shine.support.collection.IntLongMap;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.utils.MathUtils;

/** 单位基础AI逻辑 */
public class UnitAILogic extends UnitLogicBase
{
	/** 逃跑角度(30) */
	private static final float escapeDir=MathUtils.fPI/6;
	
	//参数部分
	/** ai模式 */
	protected int _aiMode=UnitAIModeType.None;
	
	/** 是否需要激活策略 */
	private boolean _needActiveAILogic=false;
	/** 是否激活 */
	protected boolean _isActive=true;
	
	private int _activeIndex=CommonSetting.monsterAIActiveTime;
	
	/** 是否在idle下徘徊 */
	private boolean _isWanderOnIdle=false;
	
	//数据准备
	
	/** 战斗单位配置 */
	protected FightUnitConfig _fightUnitConfig;
	/** 战斗逻辑 */
	protected UnitFightLogic _fightLogic;
	/** 指令逻辑 */
	protected UnitAICommandLogic _commandLogic;
	/** 单位技能组 */
	protected IntObjectMap<SkillData> _unitSkills;
	
	protected SkillData _defaultSkill;
	
	//移动部分
	
	/** 是否徘徊中(free状态) */
	private boolean _isWandering=false;
	/** 徘徊等待时间(s) */
	private int _wanderWaitTime=-1;
	/** 原点半径 */
	private float _anchorRadius=0f;
	/** 原点是否为单位 */
	private boolean _isAnchorUseUnit=false;
	/** AI原点单位 */
	private UnitReference _anchorUnit=new UnitReference();
	/** AI原点 */
	private PosData _anchorPos=new PosData();
	
	//
	/** 上次被攻击时间 */
	private long _lastBeAttackTime=-1;
	
	//战斗部分
	
	/** 受到伤害组 */
	private IntLongMap _damageDic=new IntLongMap();
	
	/** AI状态 */
	protected int _fightState=UnitAIFightStateType.None;
	
	private UnitReference _pursueUnit=new UnitReference();
	/** 追击技能目标数据(command:skillTo用) */
	private SkillTargetData _pursueSkillTarget;
	/** 追击技能 */
	private SkillData _pursueSkill;
	/** 追击技能距离 */
	private float _pursueSkillDistance;
	/** 追击碰撞距离 */
	private float _pursueCollideDistance;
	/** 追击距离平方 */
	private float _pursueDistance;
	/** 追击距离平方 */
	private float _pursueDistanceSq;
	/** 最大追击距离平方 */
	private float _maxPursueDistanceSq;
	
	/** 追击等待时间(寻路不到) */
	private int _pursueWaitTime=0;
	
	private boolean _pursueResetPos=false;
	private boolean _pursueMoved=false;
	
	private boolean _needCrowedGrid;
	
	//temp
	private PosData _tempPos=new PosData();
	private PosData _tempPos2=new PosData();
	
	private DirData _tempDir=new DirData();
	private UnitSwitchTempData _tempSwitchData=new UnitSwitchTempData();
	
	@Override
	public void init()
	{
		super.init();
		
		_fightLogic=_unit.fight;
		_commandLogic=_unit.aiCommand;
		_fightUnitConfig=_fightLogic.getFightUnitConfig();
		
		if(_scene.isDriveAll())
		{
			setAIMode(_fightUnitConfig.defaultAIMode);
			setWanderOnIdle(_fightUnitConfig.isWanderOnIdle);
		}
		else
		{
			setAIMode(UnitAIModeType.None);
		}
		
		_maxPursueDistanceSq=_fightUnitConfig.maxPursueRadius*_fightUnitConfig.maxPursueRadius;
		
		_unitSkills=_unit.getUnitData().fight.skills;
		
		FightUnitLevelConfig levelConfig=FightUnitLevelConfig.get(_fightUnitConfig.id,_fightLogic.getLevel());
		
		if(levelConfig.skills.length>0)
		{
			_defaultSkill=_unitSkills.get(levelConfig.skills[0].key);
		}
		else
		{
			_defaultSkill=null;
		}
	}
	
	@Override
	public void afterInit()
	{
		super.afterInit();
		
		_needCrowedGrid=_unit.pos.needCrowedGrid();
		
		if(CommonSetting.needMonsterAIActive && _unit.isMonster() && !_unit.getMonsterIdentityLogic().getMonsterConfig().notUseAOIActive)
		{
			_needActiveAILogic=true;
			_isActive=false;
		}
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		
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
		//_lastBeAttackTime=-1;
		
		_needCrowedGrid=false;
		
		_needActiveAILogic=false;
		_isActive=true;
		_activeIndex=CommonSetting.monsterAIActiveTime;
	}
	
	@Override
	public void onFrame(int delay)
	{
		super.onFrame(delay);
	}
	
	@Override
	public void onPiece(int delay)
	{
		super.onPiece(delay);
		
		if(!_isActive)
			return;
		
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
		if(!_isActive)
			return;
		
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
	
	@Override
	public void onSecond(int delay)
	{
		super.onSecond(delay);
		
		if(!_isActive)
			return;
		
		if(isAIRunning())
		{
			if(_fightState==UnitAIFightStateType.Back && _unit.identity.isMonster())
			{
				if(_unit.getMonsterIdentityLogic().getMonsterConfig().needRecoverAtBack)
				{
					_unit.fight.getAttributeLogic().addHPPercent(Global.monsterBackAddHPPercent);
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
			
			if(_needActiveAILogic)
			{
				if(canDisactiveAI())
				{
					if((--_activeIndex)<=0)
					{
						//反激活
						_activeIndex=0;
						_isActive=false;
					}
				}
				else
				{
					_activeIndex=CommonSetting.monsterAIActiveTime;
				}
			}
		}
	}
	
	/** 是否可关闭AI */
	protected boolean canDisactiveAI()
	{
		if(!_unit.aoi.isNoCharacterAround())
			return false;
		
		if(_unit.fight.isFighting())
			return false;
		
		if(!isIdle())
			return false;
		
		if(!(_unit.aiCommand.isCommandNeedWander() || _unit.aiCommand.isCommandEmpty()))
			return false;
		
		return true;
	}
	
	/** 激活ai */
	public void activeAI()
	{
		if(!_needActiveAILogic)
			return;
		
		if(_isActive)
		{
			_activeIndex=CommonSetting.monsterAIActiveTime;
			return;
		}
		
		_isActive=true;
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
	public void setWanderOnIdle(boolean value)
	{
		_isWanderOnIdle=value;
	}
	
	/** 是否AI执行中 */
	public boolean isAIRunning()
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
	public void onMoveToOver(boolean isSuccess)
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
			case UnitAIFightStateType.Pursue:
			{
				if(isSuccess)
				{
					_pursueWaitTime=0;
					pursueMoveOver(_unit.pos.getPos());
				}
				else
				{
					_pursueWaitTime=10;//1s
				}
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
				//重选技能
				selectPursueSkill();
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
		_pursueSkillDistance=0f;
		_pursueCollideDistance=0f;
		_pursueDistance=0f;
		_pursueDistanceSq=0f;
		_pursueSkillTarget=null;
		_pursueWaitTime=0;
		_pursueResetPos=false;
		_pursueMoved=false;
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
					
					startPursue(target,true);
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
	protected boolean needWander()
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
		if(_unit.identity.isMonster())
		{
			_unit.getMonsterIdentityLogic().reIdle();
		}
		
		clearFightAI();
		_fightState=UnitAIFightStateType.Idle;
		
		//清空仇恨
		clearHate();
		
		if(needWander())
		{
			startWander();
		}
	}
	
	/** 清空仇恨 */
	private void clearHate()
	{
		_damageDic.clear();
	}
	
	/** 设置攻击目标 */
	public void setPursueUnit(Unit target)
	{
		_pursueUnit.setUnit(target);
		_pursueCollideDistance=target.avatar.getCollideRadius();
		pursueDistanceSqChange();
	}
	
	/** 开始追击 */
	protected void startPursue(Unit target,boolean isInitiative)
	{
		_fightState=UnitAIFightStateType.Pursue;
		setPursueUnit(target);
		selectPursueSkill();
		continuePursue();
		
		//主动，怪物唤醒
		if(isInitiative && _unit.identity.isMonster())
		{
			monsterWakeUpAround(target);
		}
	}
	
	/** 设置追击技能 */
	public void setPursueSkill(SkillData data)
	{
		_pursueSkill=data;
		
		if(data!=null)
		{
			float d=_fightLogic.getSkillUseDistance(data.id,data.level);
			
			_pursueSkillDistance=d;
		}
		else
		{
			_pursueSkillDistance=0f;
		}
		
		pursueDistanceSqChange();
	}
	
	private void pursueDistanceSqChange()
	{
		float d=_pursueDistance=_pursueSkillDistance+_pursueCollideDistance;
		_pursueDistanceSq=d*d;
	}
	
	/** 取攻击目标 */
	protected Unit getPursueUnit()
	{
		return _pursueUnit.getUnit();
	}
	
	private Unit searchTarget()
	{
		if(_fightUnitConfig.initiativeAttackRadius<=0)
			return null;
		
		if(!_commandLogic.isCommandCanSearchTarget())
			return null;
		
		return doSearchTarget();
	}
	
	/** 执行搜索单位 */
	protected Unit doSearchTarget()
	{
		return _scene.fight.getNearestFightUnits(_unit.pos.getPos(),_fightUnitConfig.initiativeAttackRadius,0,_unit,_fightUnitConfig.attackInfluenceTypeT);
	}
	
	/** 重新选择追击目标 */
	protected void reSelectPursueTarget()
	{
		Unit target=getPursueUnit();
		
		if(target==null)
			return;
		
		UnitSwitchTempData t=_tempSwitchData;
		t.clear();
		
		boolean[] influences=_fightUnitConfig.attackInfluenceTypeT;
		
		ScenePosLogic posLogic=_scene.pos;
		float sq=_fightUnitConfig.initiativeAttackRadiusT;
		
		PosData pos=_unit.pos.getPos();
		
		_unit.aoi.forEachAroundUnits(k->
		{
			if(k.canFight())
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
						float dis=(float)Math.sqrt(q);
						float distanceCost=0f;
						if(dis<Global.unitSwitchBaseDistance)
						{
							distanceCost=Global.unitSwitchDistanceCost*(Global.unitSwitchBaseDistance-dis);
						}
						
						float switchCost=target==k ? 0f: Global.unitSwitchFixedCost;
						
						float cost=unitHateSwitchCost+distanceCost+switchCost;
						
						if(t.unit==null || cost>t.cost)
						{
							t.unit=k;
							t.cost=cost;
						}
					}
				}
			}
		});
		
		if(t.unit!=null)
		{
			Unit re=t.unit;
			t.clear();
			
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
	protected SkillData toSelectPursueSkill()
	{
		SkillData[] values;
		SkillData v;
		
		SkillData re=null;
		int priority=0;
		
		SkillConfig config;
		int tp;
		
		for(int i=(values=_unitSkills.getValues()).length-1;i>=0;--i)
		{
			if((v=values[i])!=null)
			{
				//可用技能
				if(_fightLogic.checkCanUseSkill(v.id,v.level) && checkSkillCanUseEx(v))
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
	
	/** 检查某技能是否可用(可复写) */
	protected boolean checkSkillCanUseEx(SkillData v)
	{
		return true;
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
		
		if(_pursueResetPos)
			return;
		
		//距离够了
		if(_unit.pos.calculateDistanceSq(pos)<=_pursueDistanceSq)
		{
			pursueMoveOver(pos);
		}
		else
		{
			if(_pursueWaitTime<=0)
			{
				_pursueMoved=true;
				//向目标移动
				_unit.move.moveTo(pos);
			}
			else
			{
				--_pursueWaitTime;
			}
		}
	}
	
	private void pursueMoveOver(PosData pos)
	{
		if(checkCanPursueAttackEx())
		{
			if(!_needCrowedGrid || _pursueResetPos)
			{
				pursueAttack();
			}
			else
			{
				//先停止移动
				_unit.move.stopMove();
				
				if(!_pursueMoved)
				{
					pursueAttack();
				}
				else
				{
					//位置没变化再攻击
					if(!_unit.move.tryResetPos(pos,_pursueDistance))
					{
						pursueAttack();
					}
					else
					{
						_pursueResetPos=true;
					}
				}
			}
		}
	}
	
	/** 是否可攻击，额外检查 */
	protected boolean checkCanPursueAttackEx()
	{
		return true;
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
			boolean isN=(i & 1)==1;
			
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
	private boolean checkNeedBack()
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
		_pursueMoved=false;
		_pursueResetPos=false;
		
		if(_pursueSkill==null)
			return;
		
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
				if(_fightLogic.checkCanUseSkill(_pursueSkill))
				{
					_fightState=UnitAIFightStateType.Attack;
					_commandLogic.setCurrentCommandSkillIndex();
					_unit.fight.useSkillTo(_pursueSkill,pursueUnit.instanceID);
				}
				else
				{
					setPursueSkill(null);
				}
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
		
		_isWandering=true;
		wanderWaitTime();
	}
	
	/** 是否free中 */
	public boolean isIdle()
	{
		return _fightState==UnitAIFightStateType.Idle;
	}
	
	/** 徘徊等待时间结束 */
	private void wanderRandomMove()
	{
		if(!needWander())
			return;
		
		_scene.pos.getRandomWalkablePos(_unit.move.getMoveType(),_tempPos,_anchorPos,Global.wanderMoveRadius);
		
		_unit.move.moveTo(UnitMoveType.Walk,_tempPos);
	}
	
	/** 开始徘徊倒计时 */
	private void wanderWaitTime()
	{
		_wanderWaitTime=MathUtils.randomRange(Global.wanderWaitTimeMin,Global.wanderWaitTimeMax);
	}
	
	/** 受到攻击 */
	public void beAttack(Unit attacker,int realDamage)
	{
		if(_fightUnitConfig.needHateSwitchTarget)
		{
			_damageDic.addValue(attacker.instanceID,realDamage);
		}
		
		//在徘徊中,并且是敌对
		if(isIdle())
		{
			beAttackOnWander(attacker,true);
		}
	}
	
	protected void monsterWakeUpAround(Unit target)
	{
		if(_fightUnitConfig.wakeUpCompanionAttackRadius>0)
		{
			float sq=_fightUnitConfig.wakeUpCompanionAttackRadiusT;
			
			_unit.aoi.forEachAroundUnits(v->
			{
				//不是自己
				if(v!=_unit)
				{
					//怪物,徘徊中
					if(v.identity.isMonster() && v.ai.isIdle())
					{
						if(_scene.pos.calculatePosDistanceSq2D(v.pos.getPos(),_unit.pos.getPos())<sq)
						{
							v.ai.beAttackOnWander(target,false);
						}
					}
				}
			});
		}
	}
	
	protected void beAttackOnWander(Unit attacker,boolean isInitiative)
	{
		if(!_unit.fight.checkIsEnemy(attacker))
			return;
		
		if(!_commandLogic.isCommandCanSearchTarget())
			return;
		
		//超出被动追击半径
		if(_fightUnitConfig.passiveAttackRadius>0 && _scene.pos.calculatePosDistanceSq2D(attacker.pos.getPos(),_unit.pos.getPos())<_fightUnitConfig.passiveAttackRadiusT)
		{
			startPursue(attacker,isInitiative);
		}
		else
		{
			//TODO:要不要就近徘徊躲避一下
		}
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
				_pursueCollideDistance=0f;
				pursueDistanceSqChange();
			}
				break;
		}
	}
	
	private static class UnitSwitchTempData
	{
		public Unit unit;
		
		public float cost;
		
		public void clear()
		{
			unit=null;
			cost=0f;
		}
	}
}
