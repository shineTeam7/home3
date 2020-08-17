package com.home.commonBase.scene.unit;

import com.home.commonBase.config.game.AttackConfig;
import com.home.commonBase.config.game.AttackLevelConfig;
import com.home.commonBase.config.game.BulletConfig;
import com.home.commonBase.config.game.BulletLevelConfig;
import com.home.commonBase.config.game.FightUnitConfig;
import com.home.commonBase.config.game.ModelConfig;
import com.home.commonBase.config.game.ScenePlaceElementConfig;
import com.home.commonBase.config.game.SkillBarConfig;
import com.home.commonBase.config.game.SkillConfig;
import com.home.commonBase.config.game.SkillLevelConfig;
import com.home.commonBase.config.game.SkillStepConfig;
import com.home.commonBase.config.game.SkillStepLevelConfig;
import com.home.commonBase.config.game.enumT.SceneForceTypeConfig;
import com.home.commonBase.constlist.generate.AttributeType;
import com.home.commonBase.constlist.generate.BuffOverActionType;
import com.home.commonBase.constlist.generate.BuffSubNumType;
import com.home.commonBase.constlist.generate.DropDecideType;
import com.home.commonBase.constlist.generate.SkillActionType;
import com.home.commonBase.constlist.generate.SkillAttackType;
import com.home.commonBase.constlist.generate.SkillBarType;
import com.home.commonBase.constlist.generate.SkillCostType;
import com.home.commonBase.constlist.generate.SkillInfluenceType;
import com.home.commonBase.constlist.generate.SkillStepSwitchType;
import com.home.commonBase.constlist.generate.SkillTargetType;
import com.home.commonBase.constlist.generate.SkillUseConditionType;
import com.home.commonBase.constlist.generate.SkillUseType;
import com.home.commonBase.constlist.generate.SkillVarSourceType;
import com.home.commonBase.constlist.generate.StatusType;
import com.home.commonBase.constlist.generate.UnitAgainstType;
import com.home.commonBase.constlist.generate.UnitDeadType;
import com.home.commonBase.constlist.generate.UnitReviveType;
import com.home.commonBase.constlist.generate.UnitType;
import com.home.commonBase.constlist.system.SceneDriveType;
import com.home.commonBase.data.scene.base.BuffData;
import com.home.commonBase.data.scene.base.BulletData;
import com.home.commonBase.data.scene.base.CDData;
import com.home.commonBase.data.scene.base.DirData;
import com.home.commonBase.data.scene.base.PosData;
import com.home.commonBase.data.scene.base.PosDirData;
import com.home.commonBase.data.scene.base.SkillData;
import com.home.commonBase.data.scene.fight.DamageOneData;
import com.home.commonBase.data.scene.fight.SkillTargetData;
import com.home.commonBase.data.scene.unit.UnitData;
import com.home.commonBase.data.scene.unit.UnitFightExData;
import com.home.commonBase.data.scene.unit.identity.FightUnitIdentityData;
import com.home.commonBase.dataEx.scene.AttackData;
import com.home.commonBase.dataEx.scene.BuffIntervalActionData;
import com.home.commonBase.dataEx.scene.RingLightBuffCountData;
import com.home.commonBase.dataEx.scene.SRect;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.global.Global;
import com.home.commonBase.logic.unit.AttributeDataLogic;
import com.home.commonBase.logic.unit.BuffDataLogic;
import com.home.commonBase.logic.unit.CDDataLogic;
import com.home.commonBase.logic.unit.StatusDataLogic;
import com.home.commonBase.logic.unit.UnitFightDataLogic;
import com.home.commonBase.scene.base.Bullet;
import com.home.commonBase.scene.base.Role;
import com.home.commonBase.scene.base.Unit;
import com.home.commonBase.scene.base.UnitLogicBase;
import com.home.commonBase.scene.scene.ScenePosLogic;
import com.home.commonBase.utils.BaseGameUtils;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.data.DIntData;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.IndexMaker;
import com.home.shine.support.collection.IntBooleanMap;
import com.home.shine.support.collection.IntIntMap;
import com.home.shine.support.collection.IntList;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.collection.SList;
import com.home.shine.utils.MathUtils;
import com.home.shine.utils.ObjectUtils;

/** 单位战斗逻辑 */
public class UnitFightLogic extends UnitLogicBase
{
	/** 战斗单位身份数据 */
	private FightUnitIdentityData _fightIdentityData;
	/** 数据逻辑 */
	private UnitFightDataLogic _dataLogic;
	/** 状态数据逻辑 */
	protected StatusDataLogic _statusDataLogic;
	/** 属性数据逻辑 */
	protected AttributeDataLogic _attributeDataLogic;
	/** 冷却数据逻辑 */
	protected CDDataLogic _cdDataLogic;
	/** buff数据逻辑 */
	protected BuffDataLogic _buffDataLogic;
	/** 附加战斗数据 */
	protected UnitFightExData _fightExData;
	/** 战斗单位配置 */
	protected FightUnitConfig _fightUnitConfig;
	
	//战斗状态计时
	/** 战斗状态剩余时间 */
	protected int _fightStateLastTime=0;
	
	/** 当前技能是否本端主动使用(否则为远端释放) */
	private boolean _currentSkillIsInitiative=false;
	
	/** 当前释放技能配置 */
	protected SkillConfig _currentSkillConfig;
	/** 当前技能等级配置 */
	protected SkillLevelConfig _currentSkillLevelConfig;
	/** 当前技能剩余时间 */
	private int _currentSkillLastTime;
	/** 当前技能延迟抵御时间(ms)(提前计算时间) */
	private int _currentSkillLagDefenceTime=0;
	
	//--子弹--//
	
	/** 子弹流水ID构造器(服务器用前一半) */
	private IndexMaker _bulletInstanceIDMaker;
	/** 子弹组 */
	private IntObjectMap<Bullet> _bullets=new IntObjectMap<>(Bullet[]::new);
	
	/** 缓存的子弹组 */
	private SList<BulletData> _saveBullets;
	
	//--步骤(动作)部分--//
	
	private float _useAttackSpeed;
	private float _useCastSpeed;
	
	/** 当前步骤配置 */
	private SkillStepConfig _currentSkillStepConfig;
	/** 当前步骤等级配置(可能为空) */
	private SkillStepLevelConfig _currentSkillStepLevelConfig;
	/** 动作总时间 */
	private float _stepTimeMax;
	/** 动作时间经过 */
	protected float _stepTimePass;
	/** 上个动作时间经过时间 */
	protected int _lastStepTimePass;
	/** 上个动作总时间 */
	private int _lastStepTimeMax;
	/** 动作经过剩余时间 */
	protected float _stepLastTimeMillis=0f;
	/** 动作时间比率 */
	private float _stepTimeSpeed;
	
	private int _stepAttackIndex;
	private int _stepBulletIndex;
	private int _stepFrameActionIndex;
	
	/** 当前步是否需要attack */
	private boolean _currentStepNeedAttack;
	
	/** 下个步骤标记 */
	private boolean _nextStepSign=false;
	/** 下个步骤时间(skillCommand攻) */
	private int _nextStepTime=0;
	
	//--dead--//
	/** 死亡保留时间 */
	private int _deadKeepTime=0;
	
	//--skillBar--//
	/** 当前的技能条配置 */
	private SkillBarConfig _currentSkillBarConfig;
	/** 条总时间 */
	private int _barTimeMax;
	
	//--伤害统计--//
	/** 是否需要伤害统计 */
	private boolean _needDamageRecord=false;
	/** 伤害统计 */
	private IntIntMap _damageRecordDic;
	
	//--光环--//
	/** 自身产生光环组 */
	private IntObjectMap<RingLightBuffCountData> _ringLightDic=new IntObjectMap<>(RingLightBuffCountData[]::new);
	
	private int _tickRingLightBuff;
	
	//temp
	protected PosData _tempPos=new PosData();
	
	protected DirData _tempDir=new DirData();
	
	@Override
	public void construct()
	{
		super.construct();
		
		if(CommonSetting.isClient)
			_bulletInstanceIDMaker=new IndexMaker(ShineSetting.indexMaxHalf,ShineSetting.indexMax,true);
		else
			_bulletInstanceIDMaker=new IndexMaker(0,ShineSetting.indexMaxHalf,true);
		
		if(!CommonSetting.isClient && CommonSetting.isClientDriveSimpleBulletForServerDriveMost)
		{
			_saveBullets=new SList<>(BulletData[]::new);
		}
	}
	
	@Override
	public void init()
	{
		super.init();
		
		UnitData unitData=_unit.getUnitData();
		
		UnitFightDataLogic dataLogic=_dataLogic=unitData.fightDataLogic;
		_statusDataLogic=dataLogic.status;
		_attributeDataLogic=dataLogic.attribute;
		_cdDataLogic=dataLogic.cd;
		_buffDataLogic=dataLogic.buff;
		_fightExData=unitData.fightEx;
		_fightIdentityData=unitData.getFightIdentity();
		_fightUnitConfig=FightUnitConfig.get(_fightIdentityData.getFightUnitID());
		
		calculateUseAttackSpeed();
		calculateUseCastSpeed();
		
		_needDamageRecord=false;
		
		if(_unit.identity.isMonster() && _unit.getMonsterIdentityLogic().getMonsterConfig().dropDecideType==DropDecideType.MostDamage)
		{
			_needDamageRecord=true;
			
			if(_damageRecordDic==null)
				_damageRecordDic=new IntIntMap();
			else
				_damageRecordDic.clear();
		}
	}
	
	@Override
	public void afterInit()
	{
		super.afterInit();
		
		UnitFightExData fData;
		
		//在释放技能中
		if((fData=_fightExData).currentSkillID>0)
		{
			//算被动释放
			toUseSkillNext(fData.currentSkillID,fData.currentSkillLevel,fData.currentTarget,false,false);
		}
	}
	
	public void afterAOIAdd()
	{
		IntObjectMap<int[]> ringLightActions=_buffDataLogic.getRingLightActions();
		
		if(!ringLightActions.isEmpty())
		{
			ringLightActions.forEach((k,v)->
			{
				onAddRingLightBuff(_buffDataLogic.getBuffData(k),v);
			});
		}
		
		countOtherRingLightBuff();
	}
	
	@Override
	public void onReloadConfig()
	{
		_fightUnitConfig=FightUnitConfig.get(_fightIdentityData.getFightUnitID());
		
		_dataLogic.reloadConfig();
	}
	
	@Override
	public void preRemove()
	{
		toStopSkill(false,false);
		
		super.preRemove();
	}
	
	@Override
	public void dispose()
	{
		int fv=_bullets.getFreeValue();
		int[] keys=_bullets.getKeys();
		int key;
		
		for(int i=keys.length - 1;i >= 0;--i)
		{
			if((key=keys[i])!=fv)
			{
				removeBullet(key,false,false);
				++i;
			}
		}
		
		_bulletInstanceIDMaker.reset();
		
		
		_dataLogic=null;
		_statusDataLogic=null;
		_attributeDataLogic=null;
		_cdDataLogic=null;
		_buffDataLogic=null;
		_fightExData=null;
		_fightUnitConfig=null;
		_fightIdentityData=null;
		_deadKeepTime=0;
		
		if(!CommonSetting.isClient && CommonSetting.isClientDriveSimpleBulletForServerDriveMost)
		{
			_saveBullets.clear();
		}
		
		_needDamageRecord=false;
		if(_damageRecordDic!=null)
			_damageRecordDic.clear();
		
		super.dispose();
	}
	
	@Override
	public void onFrame(int delay)
	{
		super.onFrame(delay);
		
		tickSkill(delay);
		tickBullet(delay);
		tickRingLightBuff(delay);
		
		if(_fightStateLastTime>0)
		{
			if((_fightStateLastTime-=delay)<=0)
			{
				leaveFightState();
			}
		}
		
		//服务器或者客户端驱动
		if(_scene.isDriveAll())
		{
			UnitFightDataLogic dataLogic;
			
			if((dataLogic=_dataLogic)!=null)
			{
				dataLogic.onPiece(delay);
			}
			
			if(_deadKeepTime>0)
			{
				if((_deadKeepTime-=delay)<=0)
				{
					_deadKeepTime=0;
					
					onDeadOver();
				}
			}
		}
	}
	
	private void tickSkill(int delay)
	{
		if(!isSkilling())
			return;
		
		boolean needOver=false;
		
		if(_currentSkillLastTime>0)
		{
			if((_currentSkillLastTime-=delay)<=0)
			{
				_currentSkillLastTime=0;
				
				needOver=true;
			}
		}
		
		//当前在技能动作中
		if(_fightExData.currentSkillStep>0)
		{
			if(_stepTimeMax>0f)
			{
				_stepTimePass+=(delay*_stepTimeSpeed);
				
				stepFrameTime(_stepTimePass);
				
				if(_stepTimePass>=_stepTimeMax)
				{
					_stepLastTimeMillis=_stepTimePass-_stepTimeMax;
					onStepOver();
				}
				//有标记
				else if(_nextStepSign && _stepTimePass>=_nextStepTime)
				{
					_stepLastTimeMillis=_stepTimePass-_stepTimeMax;
					nextStep();
				}
			}
		}
		
		//当前在读条中
		if(_fightExData.currentSkillBarID>0)
		{
			if((_fightExData.currentSkillBarTimePass+=delay)>=_barTimeMax)
			{
				skillBarComplete();
			}
		}
		
		if(needOver)
		{
			skillOver(true);
		}
	}
	
	private void tickBullet(int delay)
	{
		if(_bullets.isEmpty())
			return;
		
		_bullets.forEachValueS(bullet->
		{
			if(bullet.enabled)
			{
				try
				{
					bullet.onFrame(delay);
				}
				catch(Exception e)
				{
					Ctrl.errorLog(e);
				}
			}
		});
	}
	
	/** tick光环类buff */
	private void tickRingLightBuff(int delay)
	{
		if(_ringLightDic.isEmpty())
			return;
		
		//不累计
		if((_tickRingLightBuff+=delay)>=Global.ringLightBuffRefreshTime)
		{
			_tickRingLightBuff=0;
			
			RingLightBuffCountData[] values;
			RingLightBuffCountData v;
			
			for(int i=(values=_ringLightDic.getValues()).length-1;i>=0;--i)
			{
				if((v=values[i])!=null)
				{
					refreshOneRingLightBuff(v);
				}
			}
		}
	}
	
	@Override
	public void onSecond(int delay)
	{
		super.onSecond(delay);
		
		if(!CommonSetting.isClient)
		{
			SList<BulletData> list;
			
			if(CommonSetting.isClientDriveSimpleBulletForServerDriveMost && !(list=_saveBullets).isEmpty())
			{
				BulletData[] values=list.getValues();
				
				for(int i=list.size() - 1;i >= 0;--i)
				{
					if(--(values[i].lastTime)<0)
					{
						list.remove(i);
					}
				}
			}
		}
	}
	
	@Override
	public void onPiece(int delay)
	{
		super.onPiece(delay);
		
		UnitFightDataLogic dataLogic;
		
		if((dataLogic=_dataLogic)!=null)
		{
			dataLogic.onPieceEx(delay);
		}
	}
	
	//数据方法
	
	/** 单位数据逻辑 */
	public UnitFightDataLogic getDataLogic()
	{
		return _dataLogic;
	}
	
	/** 状态数据逻辑 */
	public StatusDataLogic getStatusLogic()
	{
		return _statusDataLogic;
	}
	
	/** 属性数据逻辑 */
	public AttributeDataLogic getAttributeLogic()
	{
		return _attributeDataLogic;
	}
	
	/** buff数据逻辑 */
	public BuffDataLogic getBuffLogic()
	{
		return _buffDataLogic;
	}
	
	/** CD数据逻辑 */
	public CDDataLogic getCDDataLogic()
	{
		return _cdDataLogic;
	}
	
	/** 获取战斗单位配置 */
	public FightUnitConfig getFightUnitConfig()
	{
		return _fightUnitConfig;
	}
	
	/** 获取战斗单位等级 */
	public int getLevel()
	{
		return _fightIdentityData.level;
	}
	
	/** 获取势力 */
	public int getForce()
	{
		return _fightIdentityData.force;
	}
	
	/** 是否活着 */
	public boolean isAlive()
	{
		return _statusDataLogic.isAlive();
	}
	
	/** 是否可称为目标 */
	public boolean cantBeTarget()
	{
		return !_statusDataLogic.cantBeTarget();
	}
	
	/** 切换写入为某角色ID(控制角色) */
	public void switchSend(long playerID)
	{
		if(_fightIdentityData.controlPlayerID==playerID)
		{
			switchSendSelf();
		}
		else
		{
			switchSendOther();
		}
	}
	
	public void switchSendSelf()
	{
		_dataLogic.switchSendSelf();
	}
	
	public void switchSendOther()
	{
		_dataLogic.switchSendOther();
	}
	
	/** 结束切换写入 */
	public void endSwitchSend()
	{
		_dataLogic.endSwitchSend();
	}
	
	public void beforeWrite()
	{
		//赋值
		_fightExData.currentSkillStepTimePass=(int)_stepTimePass;
	}
	
	//源
	
	/** 获取判定源单位(如有角色,就是角色，否则就是攻击者本身) */
	public Unit getSourceUnit()
	{
		Unit sourceCharacter;
		
		if((sourceCharacter=getSourceCharacter())!=null)
			return sourceCharacter;
		
		return _unit;
	}
	
	/** 获取判定源单位(角色) */
	public Unit getSourceCharacter()
	{
		long playerID;
		
		if((playerID=_fightIdentityData.playerID)==-1)
			return null;
		
		//获取
		return _scene.getCharacterByPlayerID(playerID);
	}
	
	/** 获取攻击源单位 */
	public Unit getAttackerUnit()
	{
		if(_unit.getType()==UnitType.Puppet)
		{
			//这个不递归
			return _unit.getPuppetIdentityLogic().getAttacker();
		}
		
		return _unit;
	}
	
	//战斗状态
	/** 进入战斗状态 */
	public void enterFightState()
	{
		if(!_statusDataLogic.getStatus(StatusType.OnFight))
		{
			_statusDataLogic.setStatus(StatusType.OnFight,true);
		}
		
		updateFightState();
	}
	
	public void updateFightState()
	{
		_fightStateLastTime=Global.fightStateOnceLastTime;
	}
	
	/** 离开战斗状态 */
	public void leaveFightState()
	{
		if(!_statusDataLogic.getStatus(StatusType.OnFight))
			return;
		
		_statusDataLogic.setStatus(StatusType.OnFight,false);
		_fightStateLastTime=0;
	}
	
	/** 是否处于战斗状态 */
	public boolean isFighting()
	{
		return _statusDataLogic.getStatus(StatusType.OnFight);
	}
	
	//技能方法
	
	/** 计算使用攻速 */
	private void calculateUseAttackSpeed()
	{
		_useAttackSpeed=1f+_attributeDataLogic.getRealAttackSpeed()/1000f;
	}
	
	/** 计算使用施法速度 */
	private void calculateUseCastSpeed()
	{
		_useCastSpeed=1f+_attributeDataLogic.getRealCastSpeed()/1000f;
	}
	
	/** 获取自身矩形，2D使用 */
	public SRect getSelfBox()
	{
		ModelConfig modelConfig;
		
		if((modelConfig=_unit.avatar.getModelConfig())!=null)
			return modelConfig.defaultHitRectT;
		
		return null;
	}
	
	public SRect getAttackBox(AttackLevelConfig config)
	{
		return config.hitRect;
	}
	
	public SRect getAttackAround2DBox(float radius,float height)
	{
		return null;
	}

	/** 停止释放技能(中断) */
	public void stopSkill()
	{
		toStopSkill(true,true,false);
	}
	
	/** 停止释放技能(中断) */
	public void stopSkillAndBreak()
	{
		toStopSkill(true,true,true);
	}
	
	/** 停止释放技能 */
	private void toStopSkill(boolean reIdle,boolean needSend)
	{
		toStopSkill(reIdle,needSend,false);
	}
	
	/** 停止释放技能 */
	private void toStopSkill(boolean reIdle,boolean needSend,boolean needBreak)
	{
		if(!isSkilling())
		{
			return;
		}
		
		if(_scene.isDriveAll())
		{
			//有所属组
			if(_currentSkillConfig.groups.length>0)
			{
				_buffDataLogic.subBuffNumArr(BuffSubNumType.UseSkillFromGroup,_currentSkillConfig.groups);
			}
		}
		
		UnitFightExData fData=_fightExData;
		
		fData.currentSkillID=-1;
		fData.currentSkillLevel=0;
		fData.currentTarget=null;
		fData.currentSkillStep=0;
		fData.currentSkillStepTimePass=0;
		
		_currentSkillConfig=null;
		_currentSkillLevelConfig=null;
		
		_currentSkillStepConfig=null;
		_currentSkillStepLevelConfig=null;
		
		_currentSkillLastTime=0;
		
		_stepTimePass=0f;
		_stepLastTimeMillis=0f;
		_lastStepTimePass=0;
		_lastStepTimeMax=0;
		
		cancelSkillBar();
		
		if(needSend && _unit.isSelfControl())
		{
			sendSkillOver(needBreak);
		}
		
		if(reIdle)
		{
			_unit.move.reIdle();
		}
		
		if(needBreak)
		{
			onSkillBreak();
		}
	}
	
	/** 检查单个技能释放条件 */
	protected boolean checkOneSkillUseCondition(int[] args)
	{
		switch(args[0])
		{
			case SkillUseConditionType.OnStatus:
			{
				return _statusDataLogic.getStatus(args[1]);
			}
			case SkillUseConditionType.OffStatus:
			{
				return !_statusDataLogic.getStatus(args[1]);
			}
			case SkillUseConditionType.AttributeLessThan:
			{
				return _attributeDataLogic.getAttribute(args[1])<args[2];
			}
			case SkillUseConditionType.AttributeMoreThan:
			{
				return _attributeDataLogic.getAttribute(args[1])>args[2];
			}
			case SkillUseConditionType.AttributeNotMoreThan:
			{
				return _attributeDataLogic.getAttribute(args[1])<=args[2];
			}
			case SkillUseConditionType.AttributeNotLessThan:
			{
				return _attributeDataLogic.getAttribute(args[1])>=args[2];
			}
		}
		
		return true;
	}
	
	/** 检查单个技能消耗条件 */
	protected boolean checkOneSkillCostCondition(DIntData data)
	{
		switch(data.key)
		{
			case SkillCostType.Hp:
			{
				return _attributeDataLogic.getHp() >= data.value;
			}
			case SkillCostType.Mp:
			{
				return _attributeDataLogic.getMp() >= data.value;
			}
		}
		
		return true;
	}
	
	/** 检查是否敌对 */
	public boolean checkIsEnemy(Unit target)
	{
		return checkAgainst(target)==UnitAgainstType.Enemy;
	}
	
	/** 检查与目标敌对关系 */
	public int checkAgainst(Unit target)
	{
		boolean isNeutral=false;
		
		long playerID;
		long targetPlayerID;
		//是同一角色归属
		if((playerID=_fightIdentityData.playerID)!=-1)
		{
			if((targetPlayerID=target.identity.playerID)!=-1)
			{
				if(playerID==targetPlayerID)
				{
					return UnitAgainstType.Friend;
				}
			}
		}
		else
		{
			//都是非玩家单位
			if(target.identity.playerID==-1)
			{
				isNeutral=true;
			}
		}
		
		int selfForce;
		int targetForce;
		if((targetForce=target.fight.getForce())==(selfForce=getForce()))
		{
			if(isNeutral)
				return UnitAgainstType.Friend;
			
			return checkAgainstSameForce(target);
		}
		else
		{
			return SceneForceTypeConfig.get(selfForce).againstSet[targetForce];
		}
	}
	
	/** 检查目标单位敌对关系(在势力相同情况下) */
	protected int checkAgainstSameForce(Unit target)
	{
		return UnitAgainstType.Enemy;
	}
	
	/** 检查目标影响类型(可检查非战斗单位) */
	public boolean checkTargetInfluence(Unit target,boolean[] influences)
	{
		if(target==null)
			return false;
		
		//目标状态逻辑
		StatusDataLogic targetStatus=null;
		
		if(target.canFight())
		{
			targetStatus=target.fight.getStatusLogic();
			
			//不是自己并且不可选
			if(target!=_unit && targetStatus.cantBeTarget())
				return false;
			
			//已死
			if(!targetStatus.isAlive())
			{
				//不影响死亡单位
				if(!influences[SkillInfluenceType.Death])
				{
					return false;
				}
			}
		}
		
		switch(target.getType())
		{
			case UnitType.Character:
			{
				if(!influences[SkillInfluenceType.Character])
					return false;
			}
				break;
			case UnitType.Monster:
			{
				if(!influences[SkillInfluenceType.Monster])
					return false;
			}
				break;
			case UnitType.FieldItem:
			{
				if(!influences[SkillInfluenceType.FieldItem])
					return false;
			}
				break;
			case UnitType.Building:
			{
				if(!influences[SkillInfluenceType.Building])
					return false;
			}
				break;
		}
		
		//自己
		if(target==_unit)
		{
			return influences[SkillInfluenceType.Self];
		}
		else
		{
			if(target.canFight())
			{
				int aType=checkAgainst(target);
				
				switch(aType)
				{
					case UnitAgainstType.Neutral:
					{
						if(!influences[SkillInfluenceType.Nuetral])
							return false;
					}
						break;
					case UnitAgainstType.Enemy:
					{
						if(!influences[SkillInfluenceType.Enemy])
							return false;
						
						//不可成为敌对目标
						if(targetStatus.cantByEnemyTarget())
							return false;
						
						//需要跳过技能免疫
						if(influences[SkillInfluenceType.NeedSkillImmun] && targetStatus.isSkillImmun())
							return false;
					}
						break;
					case UnitAgainstType.Friend:
					{
						if(!influences[SkillInfluenceType.Friend])
							return false;
					}
						break;
				}
			}
		}
		
		if(!checkTargetInfluenceEx(target,influences))
			return false;
		
		return true;
	}
	
	/** 额外影响类型检查(包括非战斗单位) */
	protected boolean checkTargetInfluenceEx(Unit target,boolean[] influences)
	{
		return true;
	}
	
	/** 检查是否可使用技能 */
	public boolean checkCanUseSkill(SkillData data)
	{
		return checkCanUseSkill(data.id,data.level,false);
	}
	
	/** 检查是否可使用技能 */
	public boolean checkCanUseSkill(int skillID,int skillLevel)
	{
		return checkCanUseSkill(skillID,skillLevel,false);
	}
	
	/** 检查是否能取消技能 */
	protected boolean checkCanCancelSkill(SkillConfig config)
	{
		int[] canCancelSkillGroups=_currentSkillConfig.canCancelSkillGroups;
		
		if(canCancelSkillGroups.length>0)
		{
			for(int v:config.groups)
			{
				if(ObjectUtils.intArrayContains(canCancelSkillGroups,v))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	/** 检查是否可使用技能 */
	public boolean checkCanUseSkill(int skillID,int skillLevel,boolean isClient)
	{
		//死了不能放技能
		if(!isAlive())
		{
			return false;
		}
		
		SkillConfig config=SkillConfig.get(skillID);
		
		//被缴械
		if(config.attackType==SkillAttackType.Attack && _statusDataLogic.cantAttack())
		{
			return false;
		}
		
		//被沉默
		if(config.attackType==SkillAttackType.Cast && _statusDataLogic.cantCast())
		{
			return false;
		}
		
		if(CommonSetting.isClient || _scene.isServerDriveAttackHappen())
		{
			if(isSkilling())
			{
				boolean can=checkCanCancelSkill(config);
				
				if(!can)
				{
					return false;
				}
			}
		}
		
		//检查技能释放条件
		for(int[] v : config.useConditions)
		{
			if(!checkOneSkillUseCondition(v))
			{
				return false;
			}
		}
		
		//冷却中
		if(isClient)
		{
			if(_dataLogic.cd.getSkillLastCD(skillID) - ShineSetting.timeCheckDeviation>0)
				return false;
		}
		else
		{
			if(!_dataLogic.cd.isSkillReady(skillID))
				return false;
		}
		
		//允许时间
		if(config.inGroupCD>0)
		{
			//没在冷却中
			if(!_dataLogic.cd.isInGroupCD(config.inGroupCD))
				return false;
		}
		
		SkillLevelConfig levelConfig=SkillLevelConfig.get(skillID,skillLevel);
		
		//检查技能消耗条件
		for(DIntData v : levelConfig.cost)
		{
			if(!checkOneSkillCostCondition(v))
			{
				return false;
			}
		}
		
		return true;
	}
	
	/** 技能消费 */
	public void costSkill(int id,int level)
	{
		SkillLevelConfig levelConfig=SkillLevelConfig.get(id,level);
		
		for(DIntData v : levelConfig.cost)
		{
			costOneSkill(v);
		}
	}
	
	protected void costOneSkill(DIntData data)
	{
		switch(data.key)
		{
			case SkillCostType.Hp:
			{
				_attributeDataLogic.subOneAttribute(AttributeType.Hp,data.value);
			}
				break;
			case SkillCostType.Mp:
			{
				_attributeDataLogic.subOneAttribute(AttributeType.Mp,data.value);
			}
				break;
		}
	}
	
	/** 获取技能目标数据的(战斗)单位(使用影响类型判定) */
	private Unit getSkillTargetDataUnit(SkillConfig config,SkillTargetData tData)
	{
		Unit targetUnit=null;
		
		switch(config.targetType)
		{
			case SkillTargetType.None:
			{
				targetUnit=_unit;
			}
				break;
			case SkillTargetType.Single:
			case SkillTargetType.Ground:
			case SkillTargetType.Direction:
			case SkillTargetType.AttackScope:
			{
				targetUnit=_scene.getUnit(tData.targetInstanceID);
				
				if(targetUnit!=null)
				{
					//不符合影响类型
					if(!checkTargetInfluence(targetUnit,config.influenceTypeT))
					{
						targetUnit=null;
					}
				}
			}
				break;
		}
		
		return targetUnit;
	}
	
	/** 当前技能目标数据位置 */
	public void getCurrentSkillTargetDataPos(PosData re)
	{
		if(isSkilling())
		{
			getSkillTargetDataPos(re,_currentSkillConfig,_fightExData.currentTarget);
		}
	}
	
	/** 获取技能目标数据位置 */
	private void getSkillTargetDataPos(PosData re,SkillConfig config,SkillTargetData tData)
	{
		Unit targetUnit;
		
		if((targetUnit=getSkillTargetDataUnit(config,tData))!=null)
		{
			re.copyPos(targetUnit.pos.getPos());
			return;
		}
		
		if(tData.pos!=null)
		{
			re.copyPos(tData.pos);
			return;
		}
		
		re.copyPos(_unit.pos.getPos());
	}
	
	/** 获取技能目标数据朝向弧度 */
	private void getSkillTargetDataDirection(DirData re,SkillConfig config,SkillTargetData tData)
	{
		Unit targetUnit;
		
		if((targetUnit=getSkillTargetDataUnit(config,tData))!=null)
		{
			if(targetUnit==_unit)
			{
				re.copyDir(_unit.pos.getDir());
			}
			else
			{
				_scene.pos.calculateDirByPos(re,_unit.pos.getPos(),targetUnit.pos.getPos());
			}
			
			return;
		}
		
		if(tData.pos!=null)
		{
			_scene.pos.calculateDirByPos(re,_unit.pos.getPos(),tData.pos);
			return;
		}
		
		if(tData.dir!=null)
		{
			re.copyDir(tData.dir);
			return;
		}
		
		re.copyDir(_unit.pos.getDir());
	}
	
	/** 随机几率(传几率ID) */
	public boolean randomProb(int probID)
	{
		return MathUtils.randomProb(_buffDataLogic.getUseSkillProb(probID));
	}
	
	/** 当前正在释放技能中 */
	public boolean isSkilling()
	{
		return _fightExData.currentSkillID>0;
	}
	
	/** 当前技能是否可移动(包括定身状态) */
	public boolean isSkillCanMove()
	{
		if(!isAlive())
			return false;
		
		if(_statusDataLogic.cantMove())
			return false;
		
		if(isSkilling())
			return _currentSkillConfig.canMove;
		
		return true;
	}
	
	/** 获取某技能的施法距离 */
	public float getSkillUseDistance(int skillID,int skillLevel)
	{
		return getSkillUseDistance(SkillLevelConfig.get(skillID,skillLevel));
	}
	
	/** 获取某技能的施法距离 */
	public float getSkillUseDistance(SkillData sData)
	{
		return getSkillUseDistance(SkillLevelConfig.get(sData.id,sData.level));
	}
	
	/** 获取某技能的施法距离 */
	public float getSkillUseDistance(SkillLevelConfig config)
	{
		float re=config.useDistance;
		
		if(config.useDistanceVar>0)
		{
			re+=(_dataLogic.getSkillVarValue(config.useDistanceVar)/1000f);
		}
		
		if(re<0f)
			re=0f;
		
		return re;
	}
	
	/** 是否施法距离满足条件(服务器用) */
	public boolean isSkillDistanceAble(SkillData sData,SkillTargetData tData)
	{
		return isSkillDistanceAble(sData.id,sData.level,tData,false);
	}
	
	/** 是否施法距离满足条件 */
	public boolean isSkillDistanceAble(int skillID,int skillLevel,SkillTargetData tData,boolean isClient)
	{
		return isSkillDistanceAble(SkillConfig.get(skillID),SkillLevelConfig.get(skillID,skillLevel),tData,isClient);
	}
	
	/** 是否施法距离满足条件 */
	private boolean isSkillDistanceAble(SkillConfig config,SkillLevelConfig levelConfig,SkillTargetData tData,boolean isClient)
	{
		float deviation=isClient ? Global.distanceCheckDeviation : 0f;
		float sDis=getSkillUseDistance(levelConfig)+deviation;
		
		if(config.targetType==SkillTargetType.Single)
		{
			Unit targetUnit=getSkillTargetDataUnit(config,tData);
			
			if(targetUnit==null)
				return false;
			
			float disSq=_scene.pos.calculatePosDistanceSq(_unit.pos.getPos(),targetUnit.pos.getPos());
			
			if(BaseC.constlist.unit_canFight(targetUnit.getType()))
			{
				float d=sDis+targetUnit.avatar.getCollideRadius();
				return disSq<=d*d;
			}
			else
			{
				return disSq<=sDis*sDis;
			}
		}
		else if(config.targetType==SkillTargetType.Ground)
		{
			float dq=_unit.pos.calculateDistanceSq(tData.pos);
			
			return dq<=sDis*sDis;
		}
		
		return true;
	}
	
	/** 收到客户端释放技能 */
	public void onClientUseSkill(int skillID,SkillTargetData tData,PosDirData posDir,int ping)
	{
		onClientUseSkill(skillID,tData,posDir,ping,-1,0);
	}
	
	/** 收到客户端释放技能 */
	public void onClientUseSkill(int skillID,SkillTargetData tData,PosDirData posDir,int ping,int useSkillID,int seedIndex)
	{
		if(!isAlive())
		{
			sendClientSkillFailed(skillID);
			return;
		}
		
		SkillData sData=_dataLogic.getSkill(skillID);
		
		if(sData==null)
		{
			_unit.warnLog("客户端单位使用技能时,不存在的技能",skillID);
			sendClientSkillFailed(skillID);
			return;
		}
		
		//TODO:检查强制关系
		
		//位置不合法
		if(!_unit.move.checkPosDir(posDir))
		{
			//这里直接走拉回了
			return;
		}
		
		if(CommonSetting.openLagDefence)
		{
			if(ping<=0)
			{
				_currentSkillLagDefenceTime=CommonSetting.lagDefenceMaxTime;
			}
			else
			{
				_currentSkillLagDefenceTime=ping;//1来回 //CommonSetting.logicFrameDelay +1帧
				
				if(_currentSkillLagDefenceTime>CommonSetting.lagDefenceMaxTime)
				{
					_currentSkillLagDefenceTime=CommonSetting.lagDefenceMaxTime;
				}
			}
		}
		else
		{
			_currentSkillLagDefenceTime=0;
		}
		
		preUseSkill(skillID,sData.level,tData,false,false,useSkillID,seedIndex);
	}
	
	/** 对目标释放技能 */
	public void useSkillTo(SkillData sData,int targetInstanceID)
	{
		useSkill(sData,SkillTargetData.createByTargetUnit(targetInstanceID));
	}
	
	/** 主动使用技能 */
	public void useSkill(int id)
	{
		SkillData sData=_data.fight.skills.get(id);
		
		if(sData!=null)
		{
			useSkill(sData.id,sData.level);
		}
	}
	
	/** 主动释放技能 */
	public void useSkill(int id,int level)
	{
		SkillConfig config=SkillConfig.get(id);
		
		SkillTargetData tData=new SkillTargetData();
		tData.type=config.targetType;
		
		switch(config.targetType)
		{
			case SkillTargetType.Single:
			{
				tData.targetInstanceID=_unit.instanceID;
			}
				break;
			case SkillTargetType.Ground:
			{
				tData.pos=new PosData();
				tData.pos.copyPos(_unit.pos.getPos());
			}
				break;
			case SkillTargetType.Direction:
			case SkillTargetType.AttackScope:
			{
				tData.dir=new DirData();
				tData.dir.copyDir(_unit.pos.getDir());
			}
				break;
		}
		
		preUseSkill(id,level,tData,true,false,-1,0);
	}
	
	/** 主动释放技能 */
	public void useSkill(SkillData sData,SkillTargetData tData)
	{
		preUseSkill(sData.id,sData.level,tData,true,false,-1,0);
	}

	/** 主动释放技能 */
	public void useSkill(SkillData sData,SkillTargetData tData,boolean isSuspend)
	{
		preUseSkill(sData.id,sData.level,tData,true,isSuspend,-1,0);
	}
	
	/** 预使用技能 */
	private void preUseSkill(int skillID,int skillLevel,SkillTargetData tData,boolean isInitiative,boolean isSuspend,int clientUseSkillID,int seedIndex)
	{
		if(isInitiative && CommonSetting.openLagDefence)
		{
			_currentSkillLagDefenceTime=0;
		}
		
		//技能等级(替换前的技能ID)
		skillLevel=_buffDataLogic.getSkillUseLevel(skillID,skillLevel);
		//技能替换
		int replaceID=_buffDataLogic.getSkillReplaceID(skillID);
		
		//是否客户端使用技能到服务器
		boolean isClientUse=!CommonSetting.isClient && !isInitiative;
		
		SkillConfig config=SkillConfig.get(replaceID);
		
		if(isClientUse && config.targetType!=tData.type)
		{
			_unit.warnLog("客户端释放技能时,技能目标类型不匹配");
			sendClientSkillFailed(skillID);
			
			return;
		}
		
		makeSkillTarget(tData,config);
		
		//服务器或客户端主动使用
		if(!CommonSetting.isClient || isInitiative)
		{
			if(!checkCanUseSkill(replaceID,skillLevel,isClientUse))
			{
				if(isClientUse)
				{
					//_unit.warnLog("客户端释放技能时,当前不可使用技能");
					sendClientSkillFailed(skillID);
				}
				
				return;
			}
			
			//是客户端或者服务器驱动攻击触发
			if(CommonSetting.isClient || _scene.isServerDriveAttackHappen())
			{
				if(!isSkillDistanceAble(replaceID,skillLevel,tData,isClientUse))
				{
					if(isClientUse)
					{
						_unit.warnLog("客户端释放技能时,施法距离不匹配");
						sendClientSkillFailed(skillID);
					}
					
					return;
				}
			}
		}
		
		boolean hasProbReplace=false;
		int useID=replaceID;
		
		//没有固定替换
		if(replaceID==skillID)
		{
			SList<int[]> list=_buffDataLogic.getSkillProbReplaceList(skillID);
			
			if(list!=null && !list.isEmpty())
			{
				//几率替换
				if((useID=doSkillProbReplace(list,isInitiative,skillID,clientUseSkillID,seedIndex))==-1)
				{
					return;
				}
				
				hasProbReplace=true;
			}
		}
		
		//技能消耗
		if(_scene.isDriveAll())
		{
			//技能消费
			for(DIntData v : SkillLevelConfig.get(replaceID,skillLevel).cost)
			{
				costOneSkill(v);
			}
		}
		
		//服务器或者主动使用
		if(!CommonSetting.isClient || isInitiative)
		{
			//走CD
			_cdDataLogic.startSkillCD(replaceID,skillLevel,isInitiative);
		}
		
		if(CommonSetting.isClient)
		{
			if(isInitiative && !_scene.isDriveAll())
			{
				sendClientUseSkill(skillID,useID,tData,hasProbReplace);
			}
		}
		
		toUseSkill(useID,skillLevel,tData,isInitiative,isSuspend);
	}
	
	/** 修复目标类型(为pos和dir拍照当前数据) */
	private void makeSkillTarget(SkillTargetData tData,SkillConfig config)
	{
		Unit mTarget=null;
		
		if(tData.targetInstanceID>0)
			mTarget=_scene.getFightUnit(tData.targetInstanceID);
		
		tData.type=config.targetType;
		
		switch(config.targetType)
		{
			case SkillTargetType.Ground:
			{
				if(tData.pos==null)
				{
					tData.pos=new PosData();
					
					if(mTarget!=null)
					{
						tData.pos.copyPos(mTarget.pos.getPos());
					}
					else
					{
						tData.pos.copyPos(_unit.pos.getPos());
					}
				}
			}
				break;
			case SkillTargetType.Direction:
			{
				if(tData.dir==null)
				{
					tData.dir=new DirData();
					
					if(tData.pos!=null)
					{
						_scene.pos.calculateDirByPos2D(tData.dir,_unit.pos.getPos(),tData.pos);
					}
					else if(mTarget!=null && mTarget!=_unit)
					{
						_scene.pos.calculateDirByPos2D(tData.dir,_unit.pos.getPos(),mTarget.pos.getPos());
					}
					else
					{
						tData.dir.copyDir(_unit.pos.getDir());
					}
				}
			}
				break;
		}
	}
	
	protected int doSkillProbReplace(SList<int[]> list,boolean isInitiative,int skillID,int clientUseSkillID,int seedIndex)
	{
		//客户端释放
		if(!isInitiative)
		{
			if(!checkSkillProbReplaceResult(list,skillID,clientUseSkillID,seedIndex))
			{
				_unit.warnLog("客户端释放技能时,几率替换技能结果不正确");
				return -1;
			}
			
			return clientUseSkillID;
		}
		else
		{
			int[] v;
			
			for(int i=0,len=list.size();i<len;++i)
			{
				v=list.get(i);
				
				if(MathUtils.randomProb(_buffDataLogic.getUseSkillProb(v[3])))
				{
					skillID=v[2];
				}
			}
		}
		
		return skillID;
	}
	
	/** 检查技能几率替换结果 */
	protected boolean checkSkillProbReplaceResult(SList<int[]> list,int skillID,int clientUseSkillID,int seedIndex)
	{
		return true;
	}
	
	/** 释放技能(isInitiative:是否主动释放)(客户端发来的是false) */
	private void toUseSkill(int skillID,int skillLevel,SkillTargetData tData,boolean isInitiative,boolean isSuspend)
	{
		if(isSkilling())
		{
			//客户端主动的,并且非强制技能，直接完成剩余行为
			if(!isSuspend && !isInitiative && _unit.isSelfDriveAttackHappen())
			{
				playOverCurrentMotion();
			}
			
			toStopSkill(false,false);
		}
		
		UnitFightExData fData=_fightExData;
		fData.currentSkillID=skillID;
		fData.currentSkillLevel=skillLevel;
		fData.currentTarget=tData;
		
		//默认第一步骤
		toUseSkillNext(skillID,skillLevel,tData,isInitiative,true);
	}
	
	/** 释放技能下一阶段(isNewOne:是否新的一次，否:来自afterInit) */
	private void toUseSkillNext(int skillID,int skillLevel,SkillTargetData tData,boolean isInitiative,boolean isNewOne)
	{
		_currentSkillIsInitiative=isInitiative;
		
		SkillConfig config=_currentSkillConfig=SkillConfig.get(skillID);
		SkillLevelConfig levelConfig=_currentSkillLevelConfig=SkillLevelConfig.get(skillID,skillLevel);
		
		if(config==null || levelConfig==null)
		{
			Ctrl.throwError("技能配置不存在");
			toStopSkill(false,false);
			return;
		}
		
		//补赋值类型
		_fightExData.currentTarget.type=config.targetType;
		
		getSkillTargetDataDirection(_tempDir,config,tData);
		
		//不可移动施法
		if(!config.canMove)
		{
			//停移动
			_unit.move.stopMoveWithOutPose();
			
			//设置朝向
			_unit.pos.setDir(_tempDir);
		}
		
		//引导技能
		if(config.useType==SkillUseType.Facilitation)
		{
			if((_currentSkillLastTime=levelConfig.lastTime)<1)
				_currentSkillLastTime=1;
		}
		
		if(!CommonSetting.isClient)
		{
			sendServerUseSkill(skillID,skillLevel,tData,_unit.pos.getPosDir(),isInitiative);
		}
		
		onUseSkill();
		
		if(_unit.isDriveAll())
		{
			IntObjectMap<int[]> useSkillProbActions=_buffDataLogic.getUseSkillProbActions();
			
			if(!useSkillProbActions.isEmpty())
			{
				int[][] values;
				int[] v;
				
				for(int i=(values=useSkillProbActions.getValues()).length-1;i>=0;--i)
				{
					if((v=values[i])!=null)
					{
						if(v[1]<=0 || _currentSkillConfig.hasGroup(v[1]))
						{
							if(randomProb(v[2]))
							{
								doOneSkillAction(v,3);
							}
						}
					}
				}
			}
		}
		
		if(isNewOne)
		{
			_fightExData.currentSkillStep=-1;
			
			//有吟唱
			if(levelConfig.singBarID>0)
			{
				doSkillBar(levelConfig.singBarID,0);
			}
			else
			{
				doSkillStep(1,0);
			}
		}
		else
		{
			//吟唱中
			if(_fightExData.currentSkillBarID>0)
			{
				doSkillBar(_fightExData.currentSkillBarID,_fightExData.currentSkillBarTimePass);
			}
			else
			{
				doSkillStep(_fightExData.currentSkillStep,_fightExData.currentSkillStepTimePass);
			}
		}
	}
	
	/** 执行步骤 */
	private void doSkillStep(int step,float startTime)
	{
		SkillStepConfig stepConfig=_currentSkillStepConfig=SkillStepConfig.get(_currentSkillConfig.id,step);
		
		//clear
		_stepAttackIndex=-1;
		_stepBulletIndex=-1;
		_stepFrameActionIndex=-1;
		
		//没有步骤配置
		if(stepConfig==null)
		{
			skillOver(false);
		}
		else
		{
			if(CommonSetting.isClient)
			{
				_currentStepNeedAttack=_unit.isSelfDriveAttackHappen() && _currentSkillIsInitiative;
			}
			else
			{
				_currentStepNeedAttack=_currentSkillIsInitiative || SceneDriveType.isServerDriveAttackHappen(_scene.driveType);
			}
			
			_fightExData.currentSkillStep=step;
			
			_currentSkillStepLevelConfig=SkillStepLevelConfig.get(_currentSkillConfig.id,step,_currentSkillLevelConfig.level);
			
			_nextStepSign=false;
			_nextStepTime=0;
			
			//没有持续时间
			if((_stepTimeMax=stepConfig.time)<=0)
			{
				//走0帧
				resetStep(0f);
				
				onStepOver();
			}
			else
			{
				int[] switchType=stepConfig.switchType;
				
				//如果是技能切换的
				if(switchType.length>0 && switchType[0]==SkillStepSwitchType.SkillCommand)
				{
					_nextStepTime=switchType[1];
					
					//多等200ms
					_stepTimeMax+=CommonSetting.skillStepSwitchSkillCommandKeepTime;
				}
				
				calculateStepSpeed();
				
				resetStep(startTime);
				
				//到时间直接结束
				if(startTime>=_stepTimeMax)
				{
					onStepOver();
				}
			}
		}
	}
	
	/** 重置当前步骤 */
	private void resetStep(float startTime)
	{
		_stepTimePass=startTime;
		
		if(_stepLastTimeMillis>0f)
		{
			_stepTimePass+=_stepLastTimeMillis;
			_stepLastTimeMillis=0f;
		}
		
		_stepAttackIndex=-1;
		
		DIntData[] attacks;
		
		if((attacks=_currentSkillStepConfig.attacks).length>0)
		{
			for(int i=0;i<attacks.length;i++)
			{
				//第一个未到时间
				if(startTime<=attacks[i].key)
				{
					_stepAttackIndex=i;
					break;
				}
			}
		}
		
		_stepBulletIndex=-1;
		
		DIntData[] bullets;
		
		if((bullets=_currentSkillStepConfig.bullets).length>0)
		{
			for(int i=0;i<bullets.length;i++)
			{
				//第一个未到时间
				if(startTime<=bullets[i].key)
				{
					_stepBulletIndex=i;
					break;
				}
			}
		}
		
		_stepFrameActionIndex=-1;
		
		if(_currentSkillStepLevelConfig!=null)
		{
			int[][] frameActions=_currentSkillStepLevelConfig.frameActions;
			
			for(int i=0;i<frameActions.length;i++)
			{
				//第一个未到时间
				if(startTime<=frameActions[i][0])
				{
					_stepFrameActionIndex=i;
					break;
				}
			}
		}
		
		//if(CommonSetting.openSkillPreventNetDelay)
		//{
		//	startTime+=Global.preventNetDelayTime;
		//}
		
		stepFrameTime(startTime);
	}
	
	/** 计算fps */
	private void calculateStepSpeed()
	{
		_stepTimeSpeed=1f;
		
		//攻击类技能
		switch(_currentSkillConfig.attackType)
		{
			case SkillAttackType.Attack:
			{
				_stepTimeSpeed*=_useAttackSpeed;
			}
				break;
			case SkillAttackType.Cast:
			{
				_stepTimeSpeed*=_useCastSpeed;
			}
				break;
		}
	}
	
	/** 立刻播完当前motion(走完最后一帧) */
	private void playOverCurrentMotion()
	{
		stepFrameTime(_stepTimeMax);
	}
	
	/** 步骤每帧时间 */
	private void stepFrameTime(float time)
	{
		if(CommonSetting.openLagDefence)
		{
			time+=_currentSkillLagDefenceTime;
		}
		
		int skillLevel=_currentSkillLevelConfig.level;
		
		if(_currentStepNeedAttack && _stepAttackIndex!=-1)
		{
			DIntData[] attacks=_currentSkillStepConfig.attacks;
			DIntData v;
			//到时间
			while(time>=(v=attacks[_stepAttackIndex]).key)
			{
				doAttack(v.value,skillLevel);
				
				//超了
				if((++_stepAttackIndex)>=attacks.length)
				{
					_stepAttackIndex=-1;
					break;
				}
			}
		}
		
		if(_stepBulletIndex!=-1)
		{
			DIntData[] bullets=_currentSkillStepConfig.bullets;
			DIntData v;
			//到时间
			while(time>=(v=bullets[_stepBulletIndex]).key)
			{
				doBullet(v.value,skillLevel);
				
				//超了
				if((++_stepBulletIndex)>=bullets.length)
				{
					_stepBulletIndex=-1;
					break;
				}
			}
		}
		
		if(_stepFrameActionIndex!=-1)
		{
			int[][] frameActions=_currentSkillStepLevelConfig.frameActions;
			int[] v;
			
			while(time>=(v=frameActions[_stepFrameActionIndex])[0])
			{
				doOneSkillAction(v,1);
				
				//超了
				if((++_stepFrameActionIndex)>=frameActions.length)
				{
					_stepFrameActionIndex=-1;
					break;
				}
			}
		}
	}
	
	/** 步骤结束 */
	private void onStepOver()
	{
		//引导技能不靠这个结束
		if(_currentSkillConfig.useType==SkillUseType.Facilitation)
		{
			//还有剩余时间，才继续
			if(_currentSkillLastTime>0)
			{
				resetStep(0);
			}
			
			return;
		}
		
		nextStep();
	}
	
	private void nextStep()
	{
		_lastStepTimePass=(int)_stepTimePass;
		_lastStepTimeMax=_currentSkillStepConfig.time;
		
		int[] switchType=_currentSkillStepConfig.switchType;
		
		//无
		if(switchType.length==0)
		{
			//客户端自行停止技能
			skillOver(false);
		}
		else
		{
			switch(switchType[0])
			{
				case SkillStepSwitchType.Auto:
				{
					//下个步骤
					doSkillStep(switchType[1],0f);
				}
					break;
				case SkillStepSwitchType.SkillCommand:
				{
					if(_nextStepSign)
					{
						doSkillStep(switchType[1],0f);
					}
					else
					{
						//超时停止
						skillOver(false);
					}
				}
					break;
			}
		}
	}
	
	/** 释放技能接口 */
	protected void onUseSkill()
	{
	
	}
	
	/** 技能释放完毕 */
	private void skillOver(boolean needSend)
	{
		//是服务器
		if(!CommonSetting.isClient)
		{
			//自己的技能在damage模式不停
			if(!_currentSkillIsInitiative && _scene.driveType==SceneDriveType.ServerDriveDamage)
			{
				return;
			}
		}
		else
		{
			//damage模式
			if(_scene.driveType==SceneDriveType.ServerDriveDamage && _currentSkillIsInitiative)
			{
				sendClientSkillOver();
			}
		}
		
		toStopSkill(true,needSend);
		
		onSkillOver();
	}
	
	/** 技能释放完毕 */
	protected void onSkillOver()
	{
		UnitAILogic ai;
		if((ai=_unit.ai)!=null)
			ai.onSkillOver();
	}
	
	/** 技能中断 */
	protected void onSkillBreak()
	{
		UnitAILogic ai;
		if((ai=_unit.ai)!=null)
			ai.onSkillBreak();
	}
	
	/** 执行一个技能帧动作 */
	protected void doOneSkillAction(int[] args,int off)
	{
		boolean isDriveAll=_scene.isDriveAll();
		
		switch(args[off])
		{
			case SkillActionType.SpecialMove:
			{
				if(!isDriveAll)
					return;
				
				_unit.move.specialMove(args[off+1],null);
			}
				break;
			case SkillActionType.SelfAddBuff:
			{
				if(!isDriveAll)
					return;
				
				_buffDataLogic.addBuff(args[off+1],args[off+2]);
			}
				break;
			case SkillActionType.AddPuppetAtTarget:
			{
				if(!isDriveAll)
					return;
				
				getSkillTargetDataPos(_tempPos,_currentSkillConfig,_fightExData.currentTarget);
				
				_scene.unitFactory.createAddPuppet(args[off+1],args[off+2],_tempPos,_unit,args.length>off+3 ? args[off+3] : 0);
			}
				break;
			case SkillActionType.SelfAddAttribute:
			{
				if(!isDriveAll)
					return;
				
				_attributeDataLogic.addOneAttribute(args[off+1],args[off+2]);
			}
				break;
			case SkillActionType.SelfAddAttributeVar:
			{
				if(!isDriveAll)
					return;
				
				_attributeDataLogic.addOneAttribute(args[off+1],getSkillVarValueT(args[off+2],-1));
			}
				break;
			case SkillActionType.KillSelf:
			{
				preKillSelfBySkill();
			}
				break;
			case SkillActionType.AddGroupTimePass:
			{
				if(!isDriveAll)
					return;
				
				_cdDataLogic.addGroupTimePass(args[off+1],args[off+2]);
			}
				break;
			case SkillActionType.RemoveGroupCD:
			{
				if(!isDriveAll)
					return;
				
				_cdDataLogic.removeGroupCD(args[off+1]);
			}
				break;
			case SkillActionType.CreateSelectBuilding:
			{
				if(!isDriveAll)
					return;
				
				int arg=_fightExData.currentTarget.arg;
				
				boolean isReady=args.length>off+1 && args[off+1]==1;
				
				Role role=_unit.getRole();
				
				if(role!=null)
				{
					if(role.build.checkCanBuild(arg))
					{
						PosDirData posDirData=new PosDirData();
						posDirData.pos=_fightExData.currentTarget.pos;
						posDirData.dir=_fightExData.currentTarget.dir;
						
						role.build.createBuilding(arg,isReady,posDirData);
					}
					else
					{
						//中断技能
						stopSkillAndBreak();
					}
				}
				else
				{
					if(_scene.role.checkCanBuild(_unit,arg))
					{
						//执行建造消耗
						_scene.role.doBuildCost(_unit,arg);
						
						//通过单位创建
						PosDirData posDirData=new PosDirData();
						posDirData.pos=_fightExData.currentTarget.pos;
						posDirData.dir=_fightExData.currentTarget.dir;
						
						_scene.unitFactory.createAddBuilding(arg,1,_unit,isReady,posDirData);
					}
					else
					{
						//中断技能
						stopSkillAndBreak();
					}
					
				}
			}
				break;
			case SkillActionType.SelfRemoveGroupBuff:
			{
				if(!isDriveAll)
					return;
				
				_buffDataLogic.removeBuffByGroup(args[off+1]);
			}
				break;
			case SkillActionType.PickFieldItem:
			{
				if(!isDriveAll)
					return;
				
				_scene.role.pickUpItem(_unit,_fightExData.currentTarget.targetInstanceID);
			}
				break;
			case SkillActionType.Operate:
			{
				if(!isDriveAll)
					return;
				
				_scene.role.operate(_unit,_fightExData.currentTarget.targetInstanceID);
			}
				break;
		}
	}
	
	/** 执行单个buff结束动作 */
	public void doOneBuffOverActionEx(int[] args)
	{
		boolean isDriveAll=_scene.isDriveAll();
		
		switch(args[0])
		{
			case BuffOverActionType.KillSelf:
			{
				if(!isDriveAll)
					return;
				
				doDead(_unit,UnitDeadType.Skill);
			}
				break;
		}
	}
	
	//--子弹部分--//
	
	/** 执行bullet(动作上附带的)(不广播的) */
	private void doBullet(int id,int level)
	{
		BulletLevelConfig levelConfig;
		
		if((levelConfig=BulletLevelConfig.get(id,level))==null)
		{
			//找不到取1级的
			if((levelConfig=BulletLevelConfig.get(id,1))==null)
			{
				Ctrl.throwError("找不到子弹levelConfig配置");
				return;
			}
		}
		
		toCreateAndExecuteBullet(levelConfig,_fightExData.currentTarget,true,_currentSkillIsInitiative);
	}
	
	//--伤害部分--//
	
	/** 执行attack */
	private void doAttack(int id,int level)
	{
		AttackLevelConfig levelConfig;
		
		if((levelConfig=AttackLevelConfig.get(id,level))==null)
		{
			//找不到取1级
			if((levelConfig=AttackLevelConfig.get(id,1))==null)
			{
				Ctrl.throwError("找不到攻击c配置");
				return;
			}
		}
		
		createAndExecuteAttack(levelConfig.id,levelConfig.level,_fightExData.currentTarget);
	}
	
	/** 执行attack在指定单位组上 */
	protected void doAttackOnTargets(int id,int level,SkillTargetData tData,IntList targets,boolean isBulletFirstHit)
	{
		AttackData data=createAttackData(id,level,tData);
		
		data.isBulletFirstHit=isBulletFirstHit;
		
		//执行攻击数据
		_scene.fight.executeAttack(data,targets);
		
		_scene.getExecutor().attackDataPool.back(data);
	}
	
	/** 客户端使用下个攻击 */
	public void clientAttack(int id,int level,SkillTargetData tData,IntList targets,boolean isBulletFirstHit)
	{
		//模式限定
		if(_scene.driveType!=SceneDriveType.ServerDriveDamage)
			return;
		
		if(!isAlive())
			return;
		
		doAttackOnTargets(id,level,tData,targets,isBulletFirstHit);
	}
	
	/** 客户端子弹命中 */
	public void clientBulletHit(int id,int level,SkillTargetData targetData)
	{
		SList<BulletData> list;
		
		if((list=_saveBullets).isEmpty())
		{
			return;
		}
		
		BulletData[] values=list.getValues();
		BulletData data;
		
		for(int i=0, len=list.size();i<len;++i)
		{
			data=values[i];
			
			//相等
			if(data.id==id && data.level==level && data.targetData.isEquals(targetData))
			{
				list.remove(i);
				
				BulletConfig config=BulletConfig.get(id);
				
				if(config.attackID>0)
				{
					createAndExecuteAttack(config.attackID,level,targetData);
				}
				
				return;
			}
		}
	}
	
	/** 客户端子弹结束(damage模式用) */
	public void clientBulletOver(int bulletInstanceID)
	{
		if(_scene.driveType!=SceneDriveType.ServerDriveDamage)
			return;
		
		Bullet bullet=_bullets.get(bulletInstanceID);
		
		if(bullet==null)
		{
			if(ShineSetting.openCheck)
			{
				Ctrl.throwError("未找到子弹");
			}
			
			return;
		}
		
		bullet.pos.bulletOverInitiativeForClient();
	}
	
	/** 客户端技能步 */
	public void clientSkillStep()
	{
		//没在技能中
		if(!isSkilling())
			return;
		
		//不是客户端释放
		if(_currentSkillIsInitiative)
			return;
		
		int[] switchType=_currentSkillStepConfig.switchType;
		
		//技能驱动
		if(switchType.length>0 && switchType[0]==SkillStepSwitchType.SkillCommand)
		{
			_nextStepSign=true;
			
			//超过切换时间
			if(_stepTimePass>=switchType[2])
			{
				nextStep();
			}
		}
	}
	
	/** 客户端技能结束 */
	public void clientSkillOver()
	{
		//没在技能中
		if(!isSkilling())
			return;
		
		//不是damage模式
		if(_scene.driveType!=SceneDriveType.ServerDriveDamage)
			return;
		
		//不是客户端释放
		if(_currentSkillIsInitiative)
			return;
		
		playOverCurrentMotion();
		toStopSkill(true,false);
	}
	
	/** 客户端自杀 */
	public void clientKillSelf()
	{
		//不是damage模式
		if(_scene.driveType!=SceneDriveType.ServerDriveDamage)
			return;
		
		doDead(_unit,UnitDeadType.KillSelf);
	}
	
	//--计算--//
	
	/** 获取某变量值表示的值(总值)(自身) */
	public int getSkillVarValue(int varID)
	{
		return BaseGameUtils.calculateSkillVarValueFull(varID,_dataLogic,null);
	}
	
	/** 获取某技能变量值(总值,战斗用) */
	public int getSkillVarValueT(int varID,int adderID)
	{
		UnitFightDataLogic selfLogic=null;
		
		if(adderID==-1)
		{
			selfLogic=_dataLogic;
		}
		else
		{
			Unit self=_scene.getFightUnit(adderID);
			
			if(self!=null)
			{
				selfLogic=self.fight.getDataLogic();
			}
		}
		
		//计算
		return BaseGameUtils.calculateSkillVarValueFull(varID,selfLogic,_dataLogic);
	}
	
	/** 获取变量源值(场景层补充) */
	public int toGetSkillVarSourceValue(int[] args,boolean isSelf)
	{
		if(isSelf)
		{
			switch(args[0])
			{
				case SkillVarSourceType.SelfLastSkillStepTime:
				{
					return _lastStepTimePass;
				}
				case SkillVarSourceType.SelfLastSkillStepTimePercent:
				{
					return _lastStepTimePass>=_lastStepTimeMax ? 1000 : _lastStepTimePass*1000/_lastStepTimeMax;
				}
			}
		}
		
		return 0;
	}
	
	//--攻击--//
	
	/** 创建攻击数据 */
	public AttackData createAttackData(int id,int level,SkillTargetData tData)
	{
		AttackData aData=_scene.getExecutor().attackDataPool.getOne();
		aData.config=AttackConfig.get(id);
		
		if((aData.levelConfig=AttackLevelConfig.get(id,level))==null)
		{
			Ctrl.throwError("找不到攻击等级配置");
		}
		
		aData.fromInstanceID=_unit.instanceID;
		aData.targetData=tData;
		
		return aData;
	}
	
	/** 创建并执行attack(快捷方式) */
	public void createAndExecuteAttack(int id,int level,SkillTargetData tData)
	{
		if(!_unit.isSelfDriveAttackHappen())
			return;
		
		_scene.fight.executeAndReleaseAttack(createAttackData(id,level,tData));
	}
	
	//--子弹--//
	
	public void createAndExecuteBullet(int id,int level,SkillTargetData tData)
	{
		BulletLevelConfig levelConfig;
		
		if((levelConfig=BulletLevelConfig.get(id,level))==null)
		{
			Ctrl.throwError("找不到子弹levelConfig配置");
			return;
		}
		
		toCreateAndExecuteBullet(levelConfig,tData,false,false);
	}
	
	/** 创建并执行attack(快捷方式) */
	private void toCreateAndExecuteBullet(BulletLevelConfig levelConfig,SkillTargetData tData,boolean fromMotion,boolean isInitiative)
	{
		int castType=levelConfig.castType;
		
		//不是服务器的
		if(!BaseC.constlist.bulletCast_isBulletOnServer(castType))
			return;
		
		//是否简单子弹
		boolean isSimple=BaseC.constlist.bulletCast_isSimpleBullet(castType);
		
		boolean needBullet=false;
		boolean needSend=false;
		
		//客户端
		if(CommonSetting.isClient)
		{
			if(_scene.isDriveAll())
			{
				needBullet=true;
			}
			else
			{
				if(_scene.driveType==SceneDriveType.ServerDriveDamage)
				{
					if(fromMotion)
					{
						if(isSimple)
						{
							needBullet=true;
						}
						else
						{
							if(isInitiative)
							{
								needBullet=true;
								needSend=true;
							}
						}
					}
					else
					{
						needBullet=true;
					}
				}
				else
				{
					if(isSimple)
					{
						needBullet=true;
					}
				}
			}
		}
		else
		{
			if(_scene.driveType==SceneDriveType.ServerDriveDamage)
			{
				if(fromMotion)
				{
					if(isInitiative)
					{
						needBullet=true;
						needSend=!isSimple;
					}
				}
				else
				{
					needBullet=true;
					needSend=true;
				}
			}
			else
			{
				if(fromMotion)
				{
					if(isSimple && !isInitiative && CommonSetting.isClientDriveSimpleBulletForServerDriveMost)
					{
						//简版
						BulletData data=_scene.getExecutor().bulletDataPool.getOne();
						data.id=levelConfig.id;
						data.level=levelConfig.level;
						data.targetData=_fightExData.currentTarget;
						data.lastTime=Global.bulletMaxLastTime;
						data.isInitiative=false;
						
						_saveBullets.add(data);
						return;
					}
					
					needBullet=true;
					needSend=!isSimple;
				}
				else
				{
					needBullet=true;
					needSend=true;
				}
			}
		}
		
		if(!needBullet)
			return;
		
		BulletData data=_scene.getExecutor().bulletDataPool.getOne();
		data.instanceID=_bulletInstanceIDMaker.get();
		data.id=levelConfig.id;
		data.level=levelConfig.level;
		data.targetData=tData;
		data.isInitiative=isInitiative;
		
		//不是简单类型
		if(!isSimple)
		{
			//没有朝向就搞个朝向
			if(tData.dir==null)
			{
				tData.dir=new DirData();
				tData.dir.copyDir(_unit.pos.getDir());
			}
		}
		
		if(needSend)
		{
			sendClientAddBullet(data);
		}
		
		addBullet(data,needSend,true);
	}
	
	/** 添加bullet */
	public Bullet addBullet(BulletData data,boolean needSend,boolean needSelf)
	{
		if(ShineSetting.openCheck)
		{
			if(_bullets.contains(data.instanceID))
			{
				Ctrl.throwError("子弹已存在",_unit.instanceID,data.instanceID,data.id,data.level);
				return null;
			}
		}
		
		Bullet bullet=_scene.getExecutor().bulletPool.getOne();
		
		bullet.setData(data);
		bullet.setScene(_scene);
		bullet.setUnit(_unit);
		
		_bullets.put(data.instanceID,bullet);
		
		bullet.enabled=true;
		
		bullet.init();
		
		if(needSend)
		{
			sendAddBullet(data, needSelf);
		}
		
		return bullet;
	}
	
	/** 删除子弹 */
	public final void removeBullet(int bulletInstanceID,boolean needRadio,boolean needSelf)
	{
		Bullet bullet=_bullets.get(bulletInstanceID);
		
		if(bullet==null)
		{
			Ctrl.warnLog("移除子弹时,子弹不存在",_unit.getInfo());
			
			return;
		}
		
		if(needRadio)
		{
			sendRemoveBullet(bulletInstanceID,needSelf);
		}
		
		bullet.dispose();
		
		bullet.enabled=false;
		
		_bullets.remove(bulletInstanceID);
		
		bullet.setScene(null);
		bullet.setUnit(null);
		
		BulletData data=bullet.getData();
		bullet.setData(null);
		
		_scene.getExecutor().bulletDataPool.back(data);
		_scene.getExecutor().bulletPool.back(bullet);
	}
	
	/** 移除所有客户端子弹 */
	private void removeAllClientBullet()
	{
		if(_bullets.isEmpty())
			return;
		
		_bullets.forEachValueS(bullet->
		{
			int instanceID;
			//判定为客户端子弹
			if((instanceID=bullet.getData().instanceID)>=ShineSetting.indexMaxHalf)
			{
				removeBullet(instanceID,false,false);
			}
		});
	}
	
	/** 预备重连进入 */
	public void preReconnectEnter()
	{
		removeAllClientBullet();
	}
	
	//--其他部分--//
	
	/** 执行死亡(attacker为空为系统击杀) */
	public void doDead(Unit attacker)
	{
		doDead(attacker,UnitDeadType.System,false);
	}
	
	/** 执行死亡(attacker为空为系统击杀) */
	public void doDead(Unit attacker,int type)
	{
		doDead(attacker,type,false);
	}
	
	/** 执行死亡(attacker为空为系统击杀) */
	public void doDead(Unit attacker,int type,boolean isAbs)
	{
		//已死
		if(!isAlive())
			return;
		
		//标记死亡
		_statusDataLogic.setStatus(StatusType.IsDead,true);
		_statusDataLogic.refreshStatus();//立即更新
		
		//TODO:判断是否有复活
		boolean isReal=true;
		
		//死亡消息
		sendUnitDead(attacker!=null ? attacker.instanceID : -1,isReal,type);
		
		onDead(attacker,type,isReal);
	}
	
	/** 执行复活 */
	public void doRevive()
	{
		//活着
		if(isAlive())
		{
			return;
		}
		
		_statusDataLogic.setStatus(StatusType.IsDead,false);
		_statusDataLogic.refreshStatus();
		
		_attributeDataLogic.fillHpMp();
		_attributeDataLogic.refreshAttributes();
		
		sendUnitRevive();
		
		onRevive();
	}
	
	/** 通过技能自杀 */
	public void preKillSelfBySkill()
	{
		//模式限定
		if(_scene.driveType==SceneDriveType.ServerDriveDamage)
		{
			if(CommonSetting.isClient)
			{
				if(_unit.isSelfDriveAttackHappen())
				{
					sendClientKillSelf();
				}
				else
				{
					return;
				}
			}
			else
			{
				return;
			}
		}
		
		if(!_unit.isDriveAll())
			return;
		
		doDead(_unit,UnitDeadType.KillSelf);
	}
	
	//推送
	
	//主角部分
	
	/** 推送他人属性 */
	public void sendOtherAttribute(IntIntMap dic)
	{
	
	}
	
	/** 推送他人状态 */
	public void sendOtherStatus(IntBooleanMap dic)
	{
	
	}
	
	/** 推送状态变化(给别人) */
	public void onStatusChange(boolean[] changeSet)
	{
		//自己驱动
		if(_unit.isSelfControl())
		{
			if(changeSet[StatusType.CantMove] || changeSet[StatusType.Vertigo])
			{
				//停止移动
				if(!_statusDataLogic.cantMove())
				{
					_unit.move.stopMoveByStatus();
				}
			}
			
			if(changeSet[StatusType.CantAttack] || changeSet[StatusType.Vertigo])
			{
				//不可攻击
				if(_statusDataLogic.cantAttack() && isSkilling() && _currentSkillConfig.attackType==SkillAttackType.Attack)
				{
					stopSkill();
				}
			}
			
			if(changeSet[StatusType.CantAttack] || changeSet[StatusType.Vertigo])
			{
				//不可施法
				if(_statusDataLogic.cantCast() && isSkilling() && _currentSkillConfig.attackType==SkillAttackType.Cast)
				{
					stopSkill();
				}
			}
		}
	}
	
	/** 推送属性变化 */
	public void onAttributeChange(int[] changeList,int num,boolean[] changeSet)
	{
		if(changeSet[AttributeType.MoveSpeed])
		{
			_unit.move.onMoveSpeedChange();
		}
		
		if(changeSet[AttributeType.AttackSpeed])
		{
			calculateUseAttackSpeed();
			
			if(_currentSkillStepConfig!=null)
			{
				calculateStepSpeed();
			}
		}
		
		if(changeSet[AttributeType.CastSpeed])
		{
			calculateUseCastSpeed();
			
			if(_currentSkillStepConfig!=null)
			{
				calculateStepSpeed();
			}
		}
	}
	
	/** 开始一个组CD */
	public void onStartGroupCD(int groupID)
	{
	
	}
	
	/** 结束一个组CD */
	public void onEndGroupCD(int groupID)
	{
	
	}
	
	/** 推送CD */
	public void sendStartCDs(SList<CDData> cds)
	{
		
	}
	
	/** 推送移除组CD */
	public void sendRemoveGroupCD(int groupID)
	{
		
	}
	
	/** 推送增加组CD上限百分比 */
	public void sendAddGroupTimeMaxPercent(int groupID,int value)
	{
	
	}
	
	/** 推送增加组CD上限值 */
	public void sendAddGroupTimeMaxValue(int groupID,int value)
	{
	
	}
	
	/** 推送增加组CD时间经过 */
	public void sendAddGroupTimePass(int groupID,int value)
	{
	
	}
	
	/** 推送添加buff */
	public void sendAddBuff(BuffData data)
	{

	}
	
	/** 推送删除buff */
	public void sendRemoveBuff(int instanceID)
	{

	}
	
	/** 推送刷新buff */
	public void sendRefreshBuff(int instanceID,int lastTime,int lastNum)
	{
	
	}
	
	/** 推送刷新buff剩余次数 */
	public void sendRefreshBuffLastNum(int instanceID,int num)
	{
	
	}
	
	//战斗单位部分
	
	/** 推送服务器使用技能 */
	protected void sendServerUseSkill(int skillID,int skillLevel,SkillTargetData tData,PosDirData posDir,boolean needSelf)
	{
		
	}
	
	protected void sendClientSkillFailed(int skillID)
	{
	
	}
	
	/** 推送攻击伤害 */
	public void sendAttackDamage(int id,int level,SkillTargetData targetData,SList<DamageOneData> damageList,SList<Unit> targets)
	{
		
	}
	
	/** 推送单位死亡 */
	public void sendUnitDead(int attackerInstanceID,boolean isReal,int type)
	{
		
	}
	
	/** 复活消息 */
	protected void sendUnitRevive()
	{
		
	}
	
	/** 推送添加子弹 */
	protected void sendAddBullet(BulletData data,boolean needSelf)
	{
	
	}
	
	/** 推送移除子弹 */
	protected void sendRemoveBullet(int bulletInstanceID,boolean needSelf)
	{
	
	}
	
	/** 推送技能结束
	 * @param needBreak*/
	protected void sendSkillOver(boolean needBreak)
	{
	
	}
	
	/** 添加光环类技能 */
	public void onAddRingLightBuff(BuffData data,int[] args)
	{
		RingLightBuffCountData cData=_scene.getExecutor().ringLightBuffCountPool.getOne();
		cData.data=data;
		cData.distance=getSkillVarValue(args[3]) / 1000f;
		cData.addBuffID=args[1];
		cData.addBuffLevel=args[2];
		
		_ringLightDic.put(data.instanceID,cData);
		
		//自身符合影响类型
		if(_unit.fight.checkTargetInfluence(_unit,cData.data.config.influenceTypeT))
		{
			_unit.fight.getBuffLogic().addBuffForRingLight(cData.addBuffID,cData.addBuffLevel,_unit.instanceID,true);//直接添加buff
		}
		
		//直接刷一次
		refreshOneRingLightBuff(cData);
	}
	
	/** 移除光环类技能 */
	public void onRemoveRingLightBuff(BuffData data,int[] args)
	{
		RingLightBuffCountData cData=_ringLightDic.remove(data.instanceID);
		
		if(cData!=null)
		{
			//自身符合影响类型
			if(_unit.fight.checkTargetInfluence(_unit,cData.data.config.influenceTypeT))
			{
				_unit.fight.getBuffLogic().changeRingLightBuffForRemove(cData.addBuffID);
				
				//TODO:立即移除类的
			}
			
			_scene.getExecutor().ringLightBuffCountPool.back(cData);
		}
	}
	
	/** 刷新光环影响(包括周围的别人，只在出现大幅位置更新后调用) */
	public void refreshRingLightBuffByMove()
	{
		_tickRingLightBuff=0;
		
		if(!_ringLightDic.isEmpty())
		{
			RingLightBuffCountData[] values;
			RingLightBuffCountData v;
			
			for(int i=(values=_ringLightDic.getValues()).length-1;i>=0;--i)
			{
				if((v=values[i])!=null)
				{
					refreshOneRingLightBuff(v);
				}
			}
		}
		
		countOtherRingLightBuff();
	}
	
	/** 统计他人光环技能影响 */
	private void countOtherRingLightBuff()
	{
		if(CommonSetting.needCalculateRingLightBuffAbs)
		{
			PosData pos=_unit.pos.getPos();
			
			ScenePosLogic posLogic=_scene.pos;
			
			_unit.aoi.forEachAroundUnits(k->
			{
				if(k.canFight())
				{
					IntObjectMap<RingLightBuffCountData> ringLightDic=k.fight._ringLightDic;
					
					if(!ringLightDic.isEmpty())
					{
						PosData kPos=k.pos.getPos();
						
						RingLightBuffCountData[] values2;
						RingLightBuffCountData v2;
						
						for(int i2=(values2=ringLightDic.getValues()).length-1;i2>=0;--i2)
						{
							if((v2=values2[i2])!=null)
							{
								//在范围内
								if(posLogic.calculatePosDistanceSq2D(kPos,pos)<=(v2.distance*v2.distance))
								{
									//符合影响类型
									if(k.fight.checkTargetInfluence(_unit,v2.data.config.influenceTypeT))
									{
										_unit.fight.getBuffLogic().addBuffForRingLight(v2.addBuffID,v2.addBuffLevel,k.instanceID,false);//直接添加buff
									}
								}
							}
						}
					}
				}
			});
		}
	}
	
	private void refreshOneRingLightBuff(RingLightBuffCountData cData)
	{
		SList<Unit> tempUnitList=_scene.fight.getTempUnitList();
		
		_scene.fight.getCircleFightUnits(tempUnitList,_unit.pos.getPos(),cData.distance,0,_unit,cData.data.config.influenceTypeT);
		
		tempUnitList.forEachAndClear(v->
		{
			//不是自己
			if(v!=_unit)
			{
				v.fight.getBuffLogic().addBuffForRingLight(cData.addBuffID,cData.addBuffLevel,_unit.instanceID,false);//直接添加buff
			}
		});
	}
	
	/** 执行一次间隔攻击 */
	public void doBuffIntervalAttack(BuffIntervalActionData mData)
	{
		if(!_unit.isSelfDriveAttackHappen())
			return;
		
		int adderInstanceID;
		Unit attacker;
		
		if((adderInstanceID=mData.adderInstanceID)==-1)
		{
			attacker=_unit;
		}
		else
		{
			attacker=_scene.getFightUnit(adderInstanceID);
		}
		
		//攻击者丢失
		if(attacker==null)
			return;
		
		if(CommonSetting.isClient)
		{
			if(_scene.driveType!=SceneDriveType.ServerDriveDamage)
				return;
		}
		else
		{
			//客户端自己来
			if(_scene.driveType==SceneDriveType.ServerDriveDamage)
				return;
		}
		
		AttackData aData=attacker.fight.createAttackData(mData.key,mData.value,SkillTargetData.createByTargetUnit(_unit.instanceID));
		
		mData.setAttackData(aData);
		
		_scene.fight.executeAndReleaseAttack(aData);
	}
	
	/** 执行buff攻击 */
	public void doBuffAttack(int id,int level)
	{
		if(!_unit.isSelfDriveAttackHappen())
			return;
		
		createAndExecuteAttack(id,level,SkillTargetData.createByTargetUnit(_unit.instanceID));
	}
	
	/** 受到伤害接口 */
	public void onDamage(int realDamage,Unit from,Unit attacker,AttackConfig config)
	{
		if(realDamage>0 && _needDamageRecord)
		{
			Unit sourceUnit=attacker.fight.getSourceUnit();
			
			if(sourceUnit!=null)
			{
				_damageRecordDic.addValue(sourceUnit.instanceID,realDamage);
			}
		}
		
		onDamageForAttack(attacker,config,realDamage);
	}
	
	public void onDamageForAttack(Unit attacker,AttackConfig config,int realDamage)
	{
		if(_unit.ai.isAIRunning())
		{
			//非增益
			if(attacker!=null && !config.isGain)
			{
				enterFightState();
				attacker.fight.enterFightState();
				
				_unit.ai.beAttack(attacker,realDamage);
			}
		}
	}
	
	/** 单位死亡(执行动作)(attacker为空为系统击杀) */
	public void onDead(Unit attacker,int type,boolean isReal)
	{
		//停止移动(不推送)
		_unit.move.toStopMove(true,false);
		//停止当前技能(不推送)
		toStopSkill(true,false);
		
		_unit.move.onDead();
		
		if(_unit.isDriveAll())
		{
			//移除buff
			_buffDataLogic.removeBuffAtDead();
			
			_deadKeepTime=_fightUnitConfig.deathKeepTime;
			
			//至少1毫秒,为下一帧
			if(_deadKeepTime<=0)
				_deadKeepTime=1;
		}
		
		if(isReal)
		{
			Unit source=null;
			
			if(attacker!=null)
			{
				source=attacker.fight.getSourceUnit();
			}
			
			onRealDead(source,type);
			//玩法单位死亡
			_scene.method.onUnitRealDead(_unit,attacker,type);
		}
	}
	
	/** 单位真死亡 */
	protected void onRealDead(Unit source,int type)
	{
		//怪物
		if(_unit.getType()==UnitType.Monster)
		{
			MonsterIdentityLogic monsterIdentityLogic=_unit.getMonsterIdentityLogic();
			monsterIdentityLogic.beKill(source);
			
			if(_fightUnitConfig.reviveType==UnitReviveType.ReviveAtTime)
			{
				ScenePlaceElementConfig placeConfig=_data.placeConfig;
				
				if(placeConfig!=null)
				{
					_scene.inout.addUnitRevive(placeConfig.instanceID,_scene.getTimeMillis()+_fightUnitConfig.reviveWaitTime);
				}
				else
				{
					_unit.warnLog("复活类型为ReviveAtTime时,找不到placeConfig");
				}
			}
		}
	}
	
	/** 单位死亡结束 */
	protected void onDeadOver()
	{
		_scene.method.onUnitDeadOver(_unit);
	}
	
	/** 单位复活 */
	public void onRevive()
	{
	
	}
	
	/** 获取距离最近的距离(找不到返回null) */
	public Unit getNearestEnemy(float radius)
	{
		return _scene.fight.getNearestFightUnits(_unit.pos.getPos(),radius,Global.attackScopeDefaultHeight,_unit,SkillInfluenceType.enemyE);
	}
	
	/** 清除伤害统计 */
	public void clearDamageRecord()
	{
		_damageRecordDic.clear();
	}
	
	public IntIntMap getDamageRecordDic()
	{
		return _damageRecordDic;
	}
	
	/** 获取造成伤害最高的单位 */
	public int getMostDamageUnitInstanceID()
	{
		if(_damageRecordDic==null || _damageRecordDic.isEmpty())
			return -1;
		
		
		int re=-1;
		int damage=0;
		
		for(IntIntMap.Entry kv : _damageRecordDic.entrySet())
		{
			if(kv.value>damage)
			{
				re=kv.key;
				damage=kv.value;
			}
		}
		
		return re;
	}
	
	//--skillBar--//
	
	/** 当前是否技能读条中 */
	public boolean isSkillBaring()
	{
		return _fightExData.currentSkillBarID>0;
	}
	
	/** 开始技能读条 */
	public void startSkillBar(int barID)
	{
		doSkillBar(barID,0);
	}
	
	protected void doSkillBar(int barID,int time)
	{
		_fightExData.currentSkillBarID=barID;
		_currentSkillBarConfig=SkillBarConfig.get(barID);
		_barTimeMax=_currentSkillBarConfig.time;
		_fightExData.currentSkillBarTimePass=time;
	}
	
	protected void toStopSkillBar()
	{
		_currentSkillBarConfig=null;
		_barTimeMax=0;
		_fightExData.currentSkillBarID=-1;
		_fightExData.currentSkillBarTimePass=0;
	}
	
	/** 取消技能读条 */
	private void cancelSkillBar()
	{
		if(!isSkillBaring())
			return;
		
		toStopSkillBar();
	}
	
	/** 技能条结束 */
	protected void skillBarComplete()
	{
		SkillBarConfig config=_currentSkillBarConfig;
		
		toStopSkillBar();
		
		//技能读条
		if(config.type==SkillBarType.NormalSkill)
		{
			if(!isSkilling())
			{
				Ctrl.errorLog("此时居然没在技能中");
				return;
			}
			
			doSkillStep(1,0);
		}
	}
	
	/** 收到攻击打断 */
	public void beAttackBreakSkill(int groupID)
	{
		//当前不在技能中
		if(!isSkilling())
			return;
		
		//控制免疫
		if(_statusDataLogic.isControlImmun())
			return;
		
		//不满足组包含
		if(!ObjectUtils.intArrayContains(_currentSkillConfig.groups,groupID))
			return;
		
		stopSkillAndBreak();
	}
	
	//--building--//
	
	/** 检查是否可建造某建筑 */
	public boolean checkCanBuild(int id,int level)
	{
		//return checkCanBuild(id,level);
		return true;
	}
	
	//--服务器部分--//
	
	/** 服务器释放技能 */
	public void onServerUseSkill(int skillID,int skillLevel,SkillTargetData tData)
	{
		toUseSkill(skillID,skillLevel,tData,false,false);
	}
	
	/** 服务器停止技能 */
	public void onServerStopSkill()
	{
		toStopSkill(true,false);
	}
	
	/** 服务器技能失败 */
	public void onServerSkillFailed(int skillID)
	{
		if(!isSkilling())
			return;
		
		//停止技能
		toStopSkill(true,false);
	}
	
	//--客户端推送部分--//
	
	/** 推送服务器使用技能 */
	protected void sendClientUseSkill(int skillID,int useSkillID,SkillTargetData tData,boolean hasProbReplace)
	{
	
	}
	
	/** 推送客户端使用技能结束 */
	protected void sendClientSkillOver()
	{
	
	}
	
	protected void sendClientKillSelf()
	{
	
	}
	
	/** 推送客户端主动发起攻击 */
	public void sendCUnitAttack(int id,int level,SkillTargetData tData,SList<Unit> targets,boolean isBulletFirstHit)
	{
	
	}
	
	/** 客户端发送子弹(机器人用) */
	protected void sendClientAddBullet(BulletData data)
	{
	
	}
	
	/** 客户端发送子弹命中(机器人用) */
	public void sendClientBulletHit(int id,int level,SkillTargetData tData)
	{
	
	}
}
