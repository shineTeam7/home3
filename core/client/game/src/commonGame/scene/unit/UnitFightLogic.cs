using System;
using ShineEngine;

/// <summary>
/// 单位战斗逻辑
/// </summary>
public class UnitFightLogic:UnitLogicBase
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

	/** 当前技能是本端主动使用(否则为接收远端消息(服务器)) */
	private bool _currentSkillIsInitiative=false;


	/** 当前释放技能配置 */
	protected SkillConfig _currentSkillConfig;
	/** 当前技能等级配置 */
	protected SkillLevelConfig _currentSkillLevelConfig;
	/** 当前技能剩余时间 */
	private int _currentSkillLastTime;

	//--子弹--//

	/** 子弹流水ID构造器(服务器用前一半) */
	private IndexMaker _bulletInstanceIDMaker;
	/** 子弹组 */
	private IntObjectMap<Bullet> _bullets=new IntObjectMap<Bullet>();

	//--动作部分--//

	private float _useAttackSpeed;
	private float _useCastSpeed;

	/** 当前步骤配置 */
	private SkillStepConfig _currentSkillStepConfig;
	/** 当前步骤等级配置(可能为空) */
	private SkillStepLevelConfig _currentSkillStepLevelConfig;
	/** 动作总时间 */
	private float _stepTimeMax;
	/** 动作时间经过 */
	private float _stepTimePass;
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
	private bool _currentStepNeedAttack;

	/** 下个步骤标记 */
	private bool _nextStepSign=false;
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

	/** 是否需要伤害统计 */
	private bool _needDamageRecord=false;

	//temp
	private PosData _tempPos=new PosData();

	private DirData _tempDir=new DirData();

	public UnitFightLogic()
	{

	}

	public override void construct()
	{
		base.construct();

		_bulletInstanceIDMaker=new IndexMaker(ShineSetting.indexMaxHalf,ShineSetting.indexMax,true);
	}

	public override void init()
	{
		base.init();

		_dataLogic=_data.fightDataLogic;
		_statusDataLogic=_dataLogic.status;
		_attributeDataLogic=_dataLogic.attribute;
		_cdDataLogic=_dataLogic.cd;
		_buffDataLogic=_dataLogic.buff;
		_fightExData=_data.fightEx;
		_fightIdentityData=_data.getFightIdentity();
		_fightUnitConfig=FightUnitConfig.get(_fightIdentityData.getFightUnitID());

		calculateUseAttackSpeed();
		calculateUseCastSpeed();
	}

	public override void afterInit()
	{
		base.afterInit();

		UnitFightExData fData;

		//在释放技能中
		if((fData=_fightExData).currentSkillID>0)
		{
			//算被动释放
			toUseSkillNext(fData.currentSkillID,fData.currentSkillLevel,fData.currentTarget,false,false);
		}
	}

	public override void onReloadConfig()
	{
		_fightUnitConfig=FightUnitConfig.get(_fightIdentityData.getFightUnitID());

		_dataLogic.reloadConfig();
	}

	public override void preRemove()
	{
		toStopSkill(false);

		base.preRemove();
	}

	public override void dispose()
	{
		if(!_bullets.isEmpty())
		{
			int fv=_bullets.getFreeValue();
			int[] keys=_bullets.getKeys();
			int key;

			for(int i=keys.Length - 1;i>=0;--i)
			{
				if((key=keys[i])!=fv)
				{
					removeBullet(key);
					++i;
				}
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

		base.dispose();
	}

	public override void onFrame(int delay)
	{
		base.onFrame(delay);

		tickSkill(delay);
		tickBullet(delay);

		//客户端驱动才刷
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
					//死亡结束
					onDeadOver();
				}
			}
		}
	}

	public override void onFixedUpdate()
	{
		base.onFixedUpdate();

		bulletFixedUpdate();
	}

	private void tickSkill(int delay)
	{
		if(!isSkilling())
			return;

		if(_fightExData.currentSkillStep>0)
		{
			if(_stepTimeMax>0)
			{
				stepFrameTime(_stepTimePass+=(delay*_stepTimeSpeed));

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

		if(_currentSkillLastTime>0)
		{
			if((_currentSkillLastTime-=delay)<=0)
			{
				_currentSkillLastTime=0;

				skillOver();
			}
		}
	}

	private void tickBullet(int delay)
	{
		if(_bullets.isEmpty())
			return;

		IntObjectMap<Bullet> fDic;
		if(!(fDic=_bullets).isEmpty())
		{
			int safeIndex=fDic.getLastFreeIndex();
			Bullet[] values=fDic.getValues();
			Bullet v;

			for(int i=safeIndex - 1;i>=0;--i)
			{
				if((v=values[i])!=null)
				{
					try
					{
						v.onFrame(delay);
					}
					catch(Exception e)
					{
						Ctrl.errorLog(e);
					}

					if(v!=values[i])
					{
						++i;
					}
				}
			}

			for(int i=values.Length - 1;i>safeIndex;--i)
			{
				if((v=values[i])!=null)
				{
					try
					{
						v.onFrame(delay);
					}
					catch(Exception e)
					{
						Ctrl.errorLog(e);
					}

					if(v!=values[i])
					{
						++i;
					}
				}
			}
		}
	}

	private void bulletFixedUpdate()
	{
		if(!_bullets.isEmpty())
		{
			foreach(Bullet bullet in _bullets)
			{
				if(bullet.enabled)
				{
					try
					{
						bullet.onFixedUpdate();
					}
					catch(Exception e)
					{
						Ctrl.errorLog(e);
					}
				}
			}
		}
	}

	public override void onPiece(int delay)
	{
		base.onPiece(delay);

		if(_dataLogic!=null)
		{
			_dataLogic.onPieceEx(delay);
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
	public bool isAlive()
	{
		return _statusDataLogic.isAlive();
	}

	/** 是否在释放技能中 */
	public bool isSkilling()
	{
		return _fightExData.currentSkillID>0;
	}

	/** 当前技能是否可移动 */
	public bool isSkillCanMove()
	{
		if(!isAlive())
			return false;

		if(_statusDataLogic.cantMove())
			return false;

		if(isSkilling())
			return _currentSkillConfig.canMove;

		return true;
	}

	/** 获取当前技能目标 */
	public SkillTargetData getCurrentTarget()
	{
		return _fightExData.currentTarget;
	}

	//源

	/** 获取判定源单位(角色) */
	public Unit getSourceCharacter()
	{
		long playerID;

		if((playerID=_fightIdentityData.playerID)==-1)
			return null;

		//获取
		return _scene.getCharacterByPlayerID(playerID);
	}

	/** 获取伤害源单位 */
	public Unit getAttackerUnit()
	{
		if(_unit.type==UnitType.Puppet)
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
	public bool isFighting()
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
	public virtual SRect getSelfBox()
	{
		ModelConfig modelConfig;

		if((modelConfig=_unit.avatar.getModelConfig())!=null)
			return modelConfig.defaultHitRectT;

		return null;
	}

	public virtual SRect getAttackBox(AttackLevelConfig config)
	{
		return config.hitRect;
	}

	public virtual SRect getAttackAround2DBox(float radius,float height)
	{
		return null;
	}

	/** 停止释放技能(中断) */
	public void stopSkill()
	{
		toStopSkill(true);
	}

	/** 停止释放技能(中断) */
	public void stopSkillAndBreak()
	{
		toStopSkill(true,true);
	}

	private void toStopSkill(bool reIdle)
	{
		toStopSkill(reIdle,false);
	}

	/** 停止释放技能 */
	private void toStopSkill(bool reIdle,bool needBreak)
	{
		if(!isSkilling())
		{
			return;
		}

		if(_scene.isDriveAll())
		{
			//有所属组
			if(_currentSkillConfig.groups.Length>0)
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
	protected virtual bool checkOneSkillUseCondition(int[] args)
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
	protected virtual bool checkOneSkillCostCondition(DIntData data)
	{
		switch(data.key)
		{
			case SkillCostType.Hp:
			{
				return _attributeDataLogic.getHp()>=data.value;
			}
			case SkillCostType.Mp:
			{
				return _attributeDataLogic.getMp()>=data.value;
			}
		}

		return true;
	}

	/** 检查是否敌对 */
	public bool checkIsEnemy(Unit target)
	{
		return checkAgainst(target)==UnitAgainstType.Enemy;
	}

	/** 检查与目标敌对关系 */
	public int checkAgainst(Unit target)
	{
		bool isNeutral=false;

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
				else
				{
					//_unit.fight.getSourceCharacter()
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

	/** 检查目标影响类型 */
	public bool checkTargetInfluence(Unit target,bool[] influences)
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
						//不可成为敌对目标
						if(targetStatus.cantByEmemyTarget())
							return false;

						if(!influences[SkillInfluenceType.Enemy])
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
	protected virtual bool checkTargetInfluenceEx(Unit target,bool[] influences)
	{
		return true;
	}

	/** 检查是否可使用技能 */
	public bool checkCanUseSkill(SkillData data)
	{
		return checkCanUseSkill(data.id,data.level);
	}

	/** 检查是否可使用技能(主动才会走) */
	public virtual bool checkCanUseSkill(int skillID,int skillLevel)
	{
		//死了不能放技能
		if(!isAlive())
			return false;

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

		if(isSkilling())
		{
			bool can=false;

			int[] canCancelSkillGroups=_currentSkillConfig.canCancelSkillGroups;

			if(canCancelSkillGroups.Length>0)
			{
				foreach(int v in config.groups)
				{
					if(ObjectUtils.arrayContains(canCancelSkillGroups,v))
					{
						can=true;
						break;
					}
				}
			}

			if(!can)
			{
				return false;
			}
		}

		//检查技能释放条件
		foreach(int[] v in config.useConditions)
		{
			if(!checkOneSkillUseCondition(v))
			{
				return false;
			}
		}

		//冷却中
		if(!_dataLogic.cd.isSkillReady(skillID))
		{
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
		foreach(DIntData v in levelConfig.cost)
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

		foreach(DIntData v in levelConfig.cost)
		{
			costOneSkill(v);
		}
	}

	protected virtual void costOneSkill(DIntData data)
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

	/** 获取技能目标数据的战斗单位(使用影响类型判定) */
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
				targetUnit=_scene.getFightUnit(tData.targetInstanceID);

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
	public bool randomProb(int probID)
	{
		return MathUtils.randomProb(_buffDataLogic.getUseSkillProb(probID));
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

	/** 是否施法距离满足条件 */
	public bool isSkillDistanceAble(SkillData sData,SkillTargetData tData)
	{
		return isSkillDistanceAble(sData.id,sData.level,tData);
	}

	/** 是否施法距离满足条件 */
	public bool isSkillDistanceAble(int skillID,int skillLevel,SkillTargetData tData)
	{
		return isSkillDistanceAble(SkillConfig.get(skillID),SkillLevelConfig.get(skillID,skillLevel),tData);
	}

	/** 是否施法距离满足条件 */
	private bool isSkillDistanceAble(SkillConfig config,SkillLevelConfig levelConfig,SkillTargetData tData)
	{
		float sDis=getSkillUseDistance(levelConfig);

		if(config.targetType==SkillTargetType.Single)
		{
			Unit targetUnit=getSkillTargetDataUnit(config,tData);

			if(targetUnit==null)
				return false;

			float dis=_scene.pos.calculatePosDistance(_unit.pos.getPos(),targetUnit.pos.getPos());

			return dis<=sDis+targetUnit.avatar.getCollideRadius();
		}
		else if(config.targetType==SkillTargetType.Ground)
		{
			float dq=_unit.pos.calculateDistanceSq(tData.pos);

			return dq<=sDis*sDis;
		}

		return true;
	}

	/** 对目标释放技能 */
	public void useSkillTo(SkillData sData,int targetInstanceID)
	{
		useSkill(sData,SkillTargetData.createByTargetUnit(targetInstanceID));
	}

	/** 客户端主动使用技能(快捷方式) */
	public void useSkill(int id)
	{
		SkillData sData=_data.fight.skills.get(id);

		if(sData!=null)
		{
			useSkill(sData.id,sData.level);
		}
	}

	/** 客户端主动释放技能(对自身释放) */
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

		preUseSkill(id,level,tData,true);
	}

	/** 客户端主动释放技能(基础入口) */
	public void useSkill(int skillID,int skillLevel,SkillTargetData tData)
	{
		preUseSkill(skillID,skillLevel,tData,true);
	}

	/** 客户端主动释放技能(基础入口) */
	public void useSkill(SkillData sData,SkillTargetData tData)
	{
		preUseSkill(sData.id,sData.level,tData,true);
	}

	/** 预使用技能(主动) */
	protected void preUseSkill(int skillID,int skillLevel,SkillTargetData tData,bool isInitiative)
	{
		//技能等级(替换前的技能ID)
		skillLevel=_buffDataLogic.getSkillUseLevel(skillID,skillLevel);
		//技能替换
		int replaceID=_buffDataLogic.getSkillReplaceID(skillID);

		SkillConfig config=SkillConfig.get(replaceID);

		makeSkillTarget(tData,config);

		if(isInitiative)
		{
			if(!checkCanUseSkill(replaceID,skillLevel))
			{
				Ctrl.print("当前不可使用技能");

				return;
			}

			if(!isSkillDistanceAble(replaceID,skillLevel,tData))
			{
				Ctrl.print("施法距离不足");
				return;
			}
		}

		bool hasProbReplace=false;
		int useID=replaceID;

		//没有固定替换
		if(replaceID==skillID)
		{
			SList<int[]> list=_buffDataLogic.getSkillProbReplaceList(skillID);

			if(list!=null && !list.isEmpty())
			{
				//几率替换
				if((useID=doSkillProbReplace(list,isInitiative,skillID))==-1)
				{
					return;
				}

				hasProbReplace=true;
			}
		}

		//技能消费
		if(_scene.isDriveAll())
		{
			//技能消费(替换技能)
			foreach(DIntData v in SkillLevelConfig.get(replaceID,skillLevel).cost)
			{
				costOneSkill(v);
			}
		}

		//客户端先行走CD
		_cdDataLogic.startSkillCD(replaceID,skillLevel);

		if(isInitiative && !_scene.isDriveAll())
		{
			sendClientUseSkill(skillID,useID,tData,hasProbReplace,isSkilling());
		}

		toUseSkill(useID,skillLevel,tData,isInitiative);
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

	protected int doSkillProbReplace(SList<int[]> list,bool isInitiative,int skillID)
	{
		if(isInitiative && !_unit.isDriveAll())
		{
			//不是自己的单位
			if(!_unit.isSelfControl())
			{
				Ctrl.throwError("不是自己的单位");
				return skillID;
			}

			int[] v;

			for(int i=0,len=list.size();i<len;++i)
			{
				v=list.get(i);

				if(GameC.player.system.getClientRandom(_buffDataLogic.getUseSkillProb(v[3])))
				{
					skillID=v[2];
					break;
				}
			}
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

	/** 释放技能(isInitiative:是否自己主动释放) */
	private void toUseSkill(int skillID,int skillLevel,SkillTargetData tData,bool isInitiative)
	{
		if(isSkilling())
		{
			toStopSkill(false);
		}

		UnitFightExData fData=_fightExData;
		fData.currentSkillID=skillID;
		fData.currentSkillLevel=skillLevel;
		fData.currentTarget=tData;

		//默认第一步骤
		toUseSkillNext(skillID,skillLevel,tData,isInitiative,true);
	}

	/** 释放技能下一阶段 */
	private void toUseSkillNext(int skillID,int skillLevel,SkillTargetData tData,bool isInitiative,bool isNewOne)
	{
		_currentSkillIsInitiative=isInitiative;

		SkillConfig config=_currentSkillConfig=SkillConfig.get(skillID);
		SkillLevelConfig levelConfig=_currentSkillLevelConfig=SkillLevelConfig.get(skillID,skillLevel);

		if(config==null || levelConfig==null)
		{
			Ctrl.throwError("技能配置不存在");
			return;
		}

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

		onUseSkill();

		if(_unit.isDriveAll())
		{
			IntObjectMap<int[]> useSkillProbActions=_buffDataLogic.getUseSkillProbActions();

			if(!useSkillProbActions.isEmpty())
			{
				int[][] values;
				int[] v;

				for(int i=(values=useSkillProbActions.getValues()).Length-1;i>=0;--i)
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
			skillOver();
		}
		else
		{
			_currentStepNeedAttack=_unit.isSelfDriveAttackHapen() && _currentSkillIsInitiative;

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

		if((attacks=_currentSkillStepConfig.attacks).Length>0)
		{
			for(int i=0;i<attacks.Length;i++)
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

		if((bullets=_currentSkillStepConfig.bullets).Length>0)
		{
			for(int i=0;i<bullets.Length;i++)
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

			for(int i=0;i<frameActions.Length;i++)
			{
				//第一个未到时间
				if(startTime<=frameActions[i][0])
				{
					_stepFrameActionIndex=i;
					break;
				}
			}
		}

		_unit.show.playMotionAbs(_currentSkillStepConfig.motionID,_stepTimeSpeed,startTime,false);

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

	/** 刷新当前帧率 */
	private void refreshNowFps()
	{
		_unit.show.setSpeed(_stepTimeSpeed);
	}

	/** 步骤每帧时间 */
	private void stepFrameTime(float time)
	{
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
				if((++_stepAttackIndex)>=attacks.Length)
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
				if((++_stepBulletIndex)>=bullets.Length)
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
				if((++_stepFrameActionIndex)>=frameActions.Length)
				{
					_stepFrameActionIndex=-1;
					break;
				}
			}
		}
	}

	private void onStepOver()
	{
		//引导技能不靠这个结束
		if(_currentSkillConfig.useType==SkillUseType.Facilitation)
		{
			resetStep(0);

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
		if(switchType.Length==0)
		{
			//客户端自行停止技能
			skillOver();
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
						//下个步骤
						doSkillStep(switchType[1],0f);
					}
					else
					{
						//超时停止
						skillOver();
					}
				}
					break;
			}
		}
	}

	/** 技能下一步 */
	public void skillStep()
	{
		//没在技能中
		if(!isSkilling())
			return;

		//不是客户端主动释放
		if(!_currentSkillIsInitiative)
			return;

		int[] switchType=_currentSkillStepConfig.switchType;

		//技能驱动
		if(switchType.Length>0 && switchType[0]==SkillStepSwitchType.SkillCommand)
		{
			//标记
			_nextStepSign=true;

			//推送服务器
			sendClientSkillStep();

			//超过切换时间
			if(_stepTimePass>=switchType[2])
			{
				nextStep();
			}
		}
	}

	/** 技能结束 */
	protected virtual void skillOver()
	{
		_stepLastTimeMillis=0f;

		//damage模式
		if(_scene.driveType==SceneDriveType.ServerDriveDamage && _currentSkillIsInitiative)
		{
			CUnitSkillOverRequest.create(_unit.instanceID).send();
		}

		toStopSkill(true);
		onSkillOver();
	}

	protected virtual void onUseSkill()
	{
		//TODO:看看
	}

	/** 技能释放完毕 */
	protected virtual void onSkillOver()
	{
		UnitAILogic ai;
		if((ai=_unit.ai)!=null)
			ai.onSkillOver();
	}

	/** 技能释放中断 */
	protected virtual void onSkillBreak()
	{
		UnitAILogic ai;
		if((ai=_unit.ai)!=null)
			ai.onSkillBreak();
	}

	/** 执行一个技能帧动作 */
	protected virtual void doOneSkillAction(int[] args,int off)
	{
		bool isDriveAll=_scene.isDriveAll();

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

				_scene.unitFactory.createAddPuppet(args[off+1],args[off+2],_tempPos,_unit,args.Length>off+3 ? args[off+3] : 0);
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


			}
				break;
			case SkillActionType.SelfRemoveGroupBuff:
			{
				if(!isDriveAll)
					return;

				_buffDataLogic.removeBuffByGroup(args[off+1]);
			}
				break;
		}
	}

	/** 执行单个buff结束动作 */
	public void doOneBuffOverActionEx(int[] args)
	{
		bool isDriveAll=_scene.isDriveAll();

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

	//--伤害部分--//

	/** 执行bullet(动作上附带的) */
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

	/** 执行attack */
	protected virtual void doAttack(int id,int level)
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

	/** 获取某变量值表示的值(总值)(自身) */
	public int getSkillVarValue(int varID)
	{
		return BaseGameUtils.calculateSkillVarValueFull(varID,_dataLogic,null);
	}

	/** 获取某变量值表示的值(总值)(战斗用) */
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
	public int toGetSkillVarSourceValue(int[] args,bool isSelf)
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


	//--其他部分--//

	public void preKillSelfBySkill()
	{
		//模式限定
		if(_scene.driveType==SceneDriveType.ServerDriveDamage)
		{
			if(_unit.isSelfDriveAttackHapen())
			{
				//推送
				CUnitKillSelfRequest.create(_unit.instanceID).send();
			}
			else
			{
				return;
			}
		}

		if(!_unit.isDriveAll())
			return;

		doDead(_unit,UnitDeadType.Skill);
	}

	/** 执行死亡 */
	public void doDead(Unit attacker)
	{
		doDead(attacker,UnitDeadType.System,false);
	}

	/** 执行死亡 */
	public void doDead(Unit attacker,int type)
	{
		doDead(attacker,type,false);
	}

	/** 执行死亡 */
	public void doDead(Unit attacker,int type,bool isAbs)
	{
		//已死
		if(!isAlive())
			return;

		//标记死亡
		_statusDataLogic.setStatus(StatusType.IsDead,true);
		_statusDataLogic.refreshStatus();//立即更新

		//TODO:判断是否有复活
		bool isReal=true;

		//死亡消息
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

		onRevive();
	}

	/** 执行一次间隔攻击 */
	public void doBuffIntervalAttack(BuffIntervalActionData mData)
	{
		if(!_unit.isSelfDriveAttackHapen())
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

		AttackData aData=attacker.fight.createAttackData(mData.key,mData.value,SkillTargetData.createByTargetUnit(_unit.instanceID));

		mData.setAttackData(aData);

		_scene.fight.executeAndReleaseAttack(aData);
	}

	/** 执行buff攻击 */
	public void doBuffAttack(int id,int level)
	{
		if(!_unit.isSelfDriveAttackHapen())
			return;

		createAndExecuteAttack(id,level,SkillTargetData.createByTargetUnit(_unit.instanceID));
	}

	//--攻击--//

	/** 创建攻击数据 */
	public AttackData createAttackData(int id,int level,SkillTargetData tData)
	{
		AttackData aData=GameC.pool.attackDataPool.getOne();
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
		if(!_unit.isSelfDriveAttackHapen())
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
	private void toCreateAndExecuteBullet(BulletLevelConfig levelConfig,SkillTargetData tData,bool fromMotion,bool isInitiative)
	{
		bool isSimple=BaseC.constlist.bulletCast_isSimpleBullet(levelConfig.castType);

		bool needBullet=false;
		bool needSend=false;

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

		if(!needBullet)
			return;

		BulletData data=new BulletData();
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
			CUnitAddBulletRequest.create(_unit.instanceID,data).send();
		}

		addBullet(data);
	}

	/** 添加bullet */
	public Bullet addBullet(BulletData data)
	{
		if(ShineSetting.openCheck)
		{
			if(_bullets.contains(data.instanceID))
			{
				Ctrl.throwError("子弹已存在",data.instanceID,data.id,data.level);
				return null;
			}
		}

		Bullet bullet=GameC.pool.bulletPool.getOne();

		bullet.setData(data);
		bullet.setScene(_scene);
		bullet.setUnit(_unit);

		_bullets.put(data.instanceID,bullet);

		bullet.enabled=true;

		bullet.init();

		return bullet;
	}

	/** 删除子弹 */
	public void removeBullet(int instanceID)
	{
		Bullet bullet=_bullets.get(instanceID);

		if(bullet==null)
		{
			//			//这个检测关了
			//			if(ShineSetting.openCheck)
			//			{
			//				Ctrl.throwError("子弹不存在",instanceID);
			//			}

			return;
		}

		bullet.dispose();

		bullet.enabled=false;

		_bullets.remove(instanceID);

		bullet.setScene(null);
		bullet.setUnit(null);
		bullet.setData(null);

		GameC.pool.bulletPool.back(bullet);
	}

	//接口组

	/** 状态变化接口 */
	public virtual void onStatusChange(bool[] changeSet)
	{
		//自己驱动
		if(_unit.isSelfControl())
		{
			if(changeSet[StatusType.CantMove] || changeSet[StatusType.Vertigo])
			{
				//停止移动
				if(_statusDataLogic.cantMove())
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

	/** 属性变化接口 */
	public virtual void onAttributeChange(int[] changeList,int num,bool[] changeSet,int[] lastAttributes)
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
				refreshNowFps();
			}
		}

		if(changeSet[AttributeType.CastSpeed])
		{
			calculateUseCastSpeed();

			if(_currentSkillStepConfig!=null)
			{
				calculateStepSpeed();
				refreshNowFps();
			}
		}

		_unit.show.onAttributeChange(changeSet);
	}

	/** 开始一个组CD */
	public virtual void onStartGroupCD(int groupID)
	{

	}

	/** 结束一个组CD */
	public virtual void onEndGroupCD(int groupID)
	{

	}

	/** 添加buff时 */
	public void onAddBuff(BuffData data)
	{
		_unit.show.addBuffShow(data);
	}

	/** 删除buff时 */
	public void onRemoveBuff(BuffData data)
	{
		_unit.show.removeBuffShow(data);
	}

	/** 刷新buff时 */
	public void onRefreshBuff(BuffData data)
	{

	}

	/** 受到伤害 */
	public void onDamage(Unit from,Unit attacker,AttackConfig config,DamageOneData data)
	{
		//显示
		_unit.show.onDamage(from,config,data);

		onDamageForAttack(attacker,config);
	}

	public void onDamageForAttack(Unit attacker,AttackConfig config)
	{
		//自己控制的单位
		if(_unit.isSelfControl() && _unit.ai.isAIRunning())
		{
			if(attacker!=null && !config.isGain)
			{
				if(_scene.isDriveAll())
				{
					attacker.fight.enterFightState();
					enterFightState();
				}

				_unit.ai.beAttack(attacker);
			}
		}
	}

	/** 单位真死亡 */
	public virtual void onDead(Unit attacker,int type,bool isReal)
	{
		//停止移动(不推送)
		_unit.move.toStopMove(true,false);
		//停止当前技能(不推送)
		toStopSkill(true);

		_unit.move.onDead();

		if(_unit.isDriveAll())
		{
			_buffDataLogic.removeBuffAtDead();

			_deadKeepTime=_fightUnitConfig.deathKeepTime;

			if(_deadKeepTime<=0)
				_deadKeepTime=1;
		}

		if(isReal)
		{
			onRealDead(attacker);
			//玩法单位死亡
			_scene.play.onUnitDead(_unit,attacker);
		}
	}

	/** 单位真死亡 */
	protected virtual void onRealDead(Unit attacker)
	{

	}

	/** 单位死亡结束 */
	protected virtual void onDeadOver()
	{
		_scene.play.onUnitDeadOver(_unit);
	}

	/** 单位复活 */
	public virtual void onRevive()
	{
		_unit.move.onRevive();
	}

	/** 获取距离最近的敌方单位(找不到返回null) */
	public Unit getNearestEnemy(float radius)
	{
		return _scene.fight.getNearestFightUnits(_unit.pos.getPos(),radius,Global.attackScopeDefaultHeight,_unit,SkillInfluenceType.enemyE);
	}

	//--skillBar--//

	/** 当前是否技能读条中 */
	public bool isSkillBaring()
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

		onShowSkillBar();
	}

	protected void toStopSkillBar()
	{
		if(_currentSkillBarConfig==null)
			return;

		onHideSkillBar();

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

	/** 显示skillBar */
	protected virtual void onShowSkillBar()
	{
		if(_currentSkillBarConfig.motionID>0)
		{
			_unit.show.playMotionAbs(_currentSkillBarConfig.motionID,1f,_fightExData.currentSkillBarTimePass,true);
		}
	}

	protected virtual void onHideSkillBar()
	{
		if(_currentSkillBarConfig.motionID>0)
		{
			_unit.show.playMotion(MotionType.Idle);
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
		if(!ObjectUtils.arrayContains(_currentSkillConfig.groups,groupID))
			return;

		stopSkillAndBreak();
	}

	//--服务器--//

	/** 服务器释放技能 */
	public void onServerUseSkill(int skillID,int skillLevel,SkillTargetData tData,PosDirData posDir)
	{
		// //非自己单位，进行校对
		// if(!_unit.isSelfControl())
		// {
		//
		// }

		//位置校对
		_unit.pos.setByPosDir(posDir);

		toUseSkill(skillID,skillLevel,tData,false);
	}

	/** 服务器停止技能 */
	public void onServerStopSkill(bool needBreak)
	{
		toStopSkill(true,needBreak);
		onSkillOver();
	}

	/** 服务器技能失败 */
	public void onServerSkillFailed(int skillID)
	{
		if(!isSkilling())
			return;

		//停止技能
		toStopSkill(true);
	}


	//--推送部分--//

	/** 推送服务器使用技能 */
	protected void sendClientUseSkill(int skillID,int useSkillID,SkillTargetData tData,bool hasProbReplace,bool isSuspend)
	{
		//有几率替换技能
		if(hasProbReplace)
		{
			CUnitUseSkillExRequest.create(_unit.instanceID,skillID,tData,_unit.pos.getPosDir(),isSuspend,useSkillID,GameC.player.system.getSeedIndex()).send();
		}
		else
		{
			CUnitUseSkillRequest.create(_unit.instanceID,skillID,tData,_unit.pos.getPosDir(),isSuspend).send();
		}
	}

	/** 推送客户端主动发起攻击 */
	public virtual void sendCUnitAttack(int id,int level,SkillTargetData tData,SList<Unit> targets,bool isBulletFirstHit)
	{
		IntList sendList=new IntList();

		Unit[] values=targets.getValues();
		Unit v;

		for(int i=0,len=targets.size();i<len;++i)
		{
			v=values[i];
			sendList.add(v.instanceID);
		}

		CUnitAttackRequest.create(_unit.instanceID,id,level,tData,sendList,isBulletFirstHit).send();
	}

	/** 推送服务器使用技能 */
	protected void sendClientSkillStep()
	{
		CUnitSkillStepRequest.create(_unit.instanceID).send();
	}

	public int currentSkillLastTime
	{
		get {return _currentSkillLastTime;}
	}
}