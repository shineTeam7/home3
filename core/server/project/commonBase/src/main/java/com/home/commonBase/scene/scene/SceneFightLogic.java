package com.home.commonBase.scene.scene;

import com.home.commonBase.config.game.AttackConfig;
import com.home.commonBase.config.game.AttackLevelConfig;
import com.home.commonBase.config.game.SkillVarConfig;
import com.home.commonBase.constlist.generate.AttackActionType;
import com.home.commonBase.constlist.generate.AttackMomentType;
import com.home.commonBase.constlist.generate.AttackScopeType;
import com.home.commonBase.constlist.generate.AttributeType;
import com.home.commonBase.constlist.generate.BuffSubNumType;
import com.home.commonBase.constlist.generate.SkillDamageType;
import com.home.commonBase.constlist.generate.SkillTargetType;
import com.home.commonBase.constlist.generate.StatusType;
import com.home.commonBase.constlist.generate.UnitDeadType;
import com.home.commonBase.data.scene.base.DirData;
import com.home.commonBase.data.scene.base.PosData;
import com.home.commonBase.data.scene.fight.DamageOneData;
import com.home.commonBase.data.scene.fight.SkillTargetData;
import com.home.commonBase.dataEx.scene.AttackData;
import com.home.commonBase.dataEx.scene.SRect;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.global.Global;
import com.home.commonBase.logic.unit.AttributeDataLogic;
import com.home.commonBase.logic.unit.BuffDataLogic;
import com.home.commonBase.logic.unit.StatusDataLogic;
import com.home.commonBase.scene.base.SceneLogicBase;
import com.home.commonBase.scene.base.Unit;
import com.home.commonBase.utils.BaseGameUtils;
import com.home.shine.data.DIntData;
import com.home.shine.support.collection.IntList;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.collection.SList;
import com.home.shine.support.pool.ObjectPool;
import com.home.shine.utils.MathUtils;

import java.util.Comparator;

/** 场景战斗逻辑 */
public class SceneFightLogic extends SceneLogicBase
{
	//temp
	
	private DirData _tempDir=new DirData();
	private DirData _tempDir2=new DirData();
	
	private PosData _tempPos=new PosData();
	private PosData _tempPos2=new PosData();
	
	private Comparator<Unit> _unitNearComparator;
	
	protected ScenePosLogic.FindCircleNearestTemp _circleNearestTemp=new ScenePosLogic.FindCircleNearestTemp();
	
	/** 单位list池(attack过程用) */
	private ObjectPool<SList<Unit>> _unitListPool=new ObjectPool<>(()->new SList<>(Unit[]::new));
	
	private SList<Unit> _tempUnitList=new SList<>(Unit[]::new);
	
	private int _tempRealDamage;
	
	@Override
	public void construct()
	{
		_unitNearComparator=this::unitNearComparator;
		
		_unitListPool.setEnable(CommonSetting.sceneLogicUsePool);
	}
	
	@Override
	public void init()
	{
	
	}
	
	@Override
	public void dispose()
	{
	
	}
	
	@Override
	public void onFrame(int delay)
	{
	
	}
	
	/** 获取临时单位组 */
	public SList<Unit> getTempUnitList()
	{
		return _tempUnitList;
	}

	/** 击杀单位(系统击杀) */
	public void killUnit(Unit unit)
	{
		unit.fight.doDead(null);
	}
	
	//公式部分
	
	/** 计算完整技能值 */
	public int calculateSkillVar(int varID,Unit attacker,Unit target)
	{
		return BaseGameUtils.calculateSkillVarValueFull(varID,attacker.fight.getDataLogic(),target.fight.getDataLogic());
	}
	
	/** 计算命中 */
	protected boolean calculateHitRate(int hitRate,int dodge,Unit attacker,Unit target,AttackData data)
	{
		float ratio=(hitRate-dodge+Global.hitRateArg0)/1000f;
		
		if(ratio<=0)
			return false;
		
		if(ratio>=1f)
			return true;
		
		return MathUtils.randomFloat()<=ratio;
	}
	
	/** 计算暴击 */
	protected boolean calculateCrit(int crit,int tenacity,Unit attacker,Unit target,AttackData data)
	{
		float ratio=(crit-tenacity+Global.critArg0)/1000f;
		
		if(ratio<=0)
			return false;
		
		if(ratio>=1f)
			return true;
		
		return MathUtils.randomFloat()<=ratio;
	}
	
	/** 计算伤害 */
	protected int calculateDamage(int[] damages,int attackValue,boolean isCrit,AttributeDataLogic attackerAttribute,AttributeDataLogic targetAttribute,Unit attacker,Unit target,AttackData data)
	{
		int damageType;
		float damage=0f;
		
		switch((damageType=damages[0]))
		{
			case SkillDamageType.PhysicsDamage:
			case SkillDamageType.MagicDamage:
			{
				int defence;
				int addPercent;
				int deratePercent;
				
				if(damageType==SkillDamageType.PhysicsDamage)
				{
					defence=targetAttribute.getAttribute(AttributeType.PhysicsDefence);
					addPercent=attackerAttribute.getAttribute(AttributeType.PhysicsDamageAddPercent);
					deratePercent=targetAttribute.getAttribute(AttributeType.PhysicsDamageDeratePercent);
				}
				else
				{
					defence=targetAttribute.getAttribute(AttributeType.MagicDefence);
					addPercent=attackerAttribute.getAttribute(AttributeType.MagicDamageAddPercent);
					deratePercent=targetAttribute.getAttribute(AttributeType.MagicDamageDeratePercent);
				}
				
				damage=toCalculateBaseDamage(damages,attackValue,defence);
				
				if(isCrit)
				{
					damage=damageCrit(damage,attackerAttribute.getAttribute(AttributeType.CritDamagePercentAdd));
				}
				
				damage=damageRandom(damage,attackerAttribute.getAttribute(AttributeType.DamageRandomRatio));
				
				//伤害增加
				if(addPercent!=0)
				{
					float percent=(1+addPercent/1000f);
					
					if(percent>0f)
						damage*=percent;
					else
						damage=0f;
				}
				
				//伤害减少
				if(deratePercent!=0)
				{
					float percent=(1-deratePercent/1000f);
					
					if(percent>0f)
						damage*=percent;
					else
						damage=0f;
				}
			}
				break;
			case SkillDamageType.HolyDamage:
			{
				damage=toCalculateBaseDamage(damages,attackValue,0);
				
				if(isCrit)
				{
					damage=damageCrit(damage,attackerAttribute.getAttribute(AttributeType.CritDamagePercentAdd));
				}
				
				damage=damageRandom(damage,attackerAttribute.getAttribute(AttributeType.DamageRandomRatio));
			}
				break;
		}
		
		int allDeratePercent=targetAttribute.getAttribute(AttributeType.AllDemageDeratePercent);
		
		if(allDeratePercent!=0)
		{
			float percent=(1-allDeratePercent/1000f);
			
			if(percent>0f)
				damage*=percent;
			else
				damage=0f;
		}
		
		return (int)damage;
	}
	
	/** 伤害暴击增加 */
	protected float damageCrit(float damage,int critDamageAdd)
	{
		float ratio=Global.critDamageBaseRatio+critDamageAdd/1000f;
		
		//最少1倍暴击
		if(ratio<1f)
		{
			ratio=1f;
		}
		
		return damage*ratio;
	}
	
	/** 伤害随机比率 */
	protected float damageRandom(float damage,int randomRatio)
	{
		if(randomRatio!=0)
		{
			float percent=MathUtils.randomFloat()*randomRatio;
			
			if(MathUtils.randomBoolean())
				percent=-percent;
			
			percent=(1+percent/1000f);
			
			if(percent>0f)
				damage*=percent;
			else
				damage=0f;
		}
		
		return damage;
	}
	
	/** 计算基础伤害 */
	protected float toCalculateBaseDamage(int[] damages,int attackValue,int defenceValue)
	{
		float re=(attackValue * (damages.length>2 ? damages[2]/1000f : 1)) + (damages.length>3 ? damages[3] : 0);
		
		re-=defenceValue;
		
		if(re<1)
			re=1;
		
		return re;
	}
	
	/** 生效伤害(返回实际伤害(实际扣除血量)) */
	protected int takeDamage(int damageType,int damageValue,AttributeDataLogic targetAttribute,Unit target)
	{
		switch(damageType)
		{
			case SkillDamageType.PhysicsDamage:
			case SkillDamageType.MagicDamage:
			{
				int shieldType=damageType==SkillDamageType.PhysicsDamage ? AttributeType.PhysicsShield : AttributeType.MagicShield;
				
				int shield=targetAttribute.getAttribute(shieldType);
				
				if(shield>0)
				{
					if(shield>=damageValue)
					{
						targetAttribute.subOneAttribute(shieldType,damageValue);
						target.fight.getBuffLogic().onShieldDamage(shieldType,damageValue);
						damageValue=0;
					}
					else
					{
						targetAttribute.subOneAttribute(shieldType,shield);
						target.fight.getBuffLogic().onShieldDamage(shieldType,shield);
						
						damageValue-=shield;
					}
				}
				
				if(damageValue<=0)
					return 0;
				
				return takeRealDamage(damageValue,targetAttribute);
			}
			case SkillDamageType.HolyDamage:
			{
				return takeRealDamage(damageValue,targetAttribute);
			}
		}
		
		return 0;
	}
	
	protected int takeRealDamage(int damageValue,AttributeDataLogic targetAttribute)
	{
		if(damageValue<=0)
			return 0;
		
		int hp=targetAttribute.getAttribute(AttributeType.Hp);
		
		int re=damageValue>hp ? hp : damageValue;
		
		targetAttribute.subOneAttribute(AttributeType.Hp,damageValue);
		
		return re;
	}
	
	/** 单位附近比较 */
	private int unitNearComparator(Unit arg0,Unit arg1)
	{
		float f0;
		float f1;
		
		if((f0=arg0.pos.tempSortKey)<(f1=arg1.pos.tempSortKey))
			return -1;
		
		if(f0>f1)
			return 1;
		
		return 0;
	}
	
	/** 取圆形战斗单位组 */
	public void getCircleFightUnits(SList<Unit> list,PosData pos,float radius,float height,Unit selfUnit,boolean[] influences)
	{
		list.clear();
		
		ScenePosLogic posLogic=_scene.pos;
		float sq=radius*radius;
		
		selfUnit.aoi.forEachAroundUnits(k->
		{
			if(k.canFight())
			{
				PosData kPos=k.pos.getPos();
				
				//高度合适
				if(height<=0 || posLogic.getDHeight(kPos,pos)<=height)
				{
					//在范围内
					if(posLogic.calculatePosDistanceSq2D(kPos,pos)<=sq)
					{
						//符合影响类型
						if(selfUnit.fight.checkTargetInfluence(k,influences))
						{
							list.add(k);
						}
					}
				}
			}
		});
	}
	
	/** 取圆形战斗单位组(需要自行处理list.clear) */
	public void getCircleFightUnits(IntObjectMap<Unit> dic,PosData pos,float radius,float height,Unit selfUnit,boolean[] influences)
	{
		dic.clear();
		
		ScenePosLogic posLogic=_scene.pos;
		float sq=radius*radius;
		
		selfUnit.aoi.forEachAroundUnits(k->
		{
			if(k.canFight())
			{
				PosData kPos=k.pos.getPos();
				
				//高度合适
				if(height<=0 || posLogic.getDHeight(kPos,pos)<=height)
				{
					//在范围内
					if(posLogic.calculatePosDistanceSq2D(kPos,pos)<=sq)
					{
						//符合影响类型
						if(selfUnit.fight.checkTargetInfluence(k,influences))
						{
							dic.put(k.instanceID,k);
						}
					}
				}
			}
		});
	}
	
	/** 取矩形战斗单位组 */
	public void getRectFightUnits(SList<Unit> list,PosData pos,DirData dir,float length,float width,float height,Unit selfUnit,boolean[] influences)
	{
		list.clear();
		
		ScenePosLogic posLogic=_scene.pos;
		
		PosData tempPos=_tempPos2;
		DirData tempDir=_tempDir2;
		
		//向后延伸
		posLogic.polar2D(tempPos,Global.attackScopeBackLength,dir);
		posLogic.addPos2D(tempPos,pos);
		
		float halfWidth=width/2f;
		
		float useLength=length+Global.attackScopeBackLength;
		
		float sq=halfWidth*halfWidth+useLength*useLength;
		
		selfUnit.aoi.forEachAroundUnits(k->
		{
			if(k.canFight())
			{
				PosData kPos=k.pos.getPos();
				
				//高度值合适
				if(height<=0 || posLogic.getDHeight(kPos,pos)<=height)
				{
					float dq;
					//在圆形范围内
					if((dq=posLogic.calculatePosDistanceSq2D(kPos,pos))<=sq)
					{
						//符合影响类型
						if(selfUnit.fight.checkTargetInfluence(k,influences))
						{
							float d=(float)Math.sqrt(dq);
							
							posLogic.calculateDirByPos2D(tempDir,tempPos,kPos);
							
							float dirV=tempDir.direction-dir.direction;
							
							float h=(float)(Math.cos(dirV)*d);
							float w=(float)(Math.sin(dirV)*d);
							
							if(Math.abs(w)<=halfWidth)
							{
								if(h>=0 && h<=useLength)
								{
									list.add(k);
								}
							}
						}
					}
				}
			}
		});
	}
	
	/** 取扇形战斗单位组(sectorAngle:角度值) */
	public void getSectorFightUnits(SList<Unit> list,PosData pos,DirData dir,float radius,float sectorAngle,float height,Unit selfUnit,boolean[] influences)
	{
		list.clear();
		ScenePosLogic posLogic=_scene.pos;
		
		PosData tempPos=_tempPos2;
		DirData tempDir=_tempDir2;
		
		//向后延伸
		posLogic.polar2D(tempPos,Global.attackScopeBackLength,dir);
		posLogic.addPos2D(tempPos,pos);
		
		//半角弧度
		float halfDirection=MathUtils.angleToDirection(sectorAngle)/2f;
		
		float useRadius=radius+Global.attackScopeBackLength;
		
		float sq=useRadius*useRadius;
		
		selfUnit.aoi.forEachAroundUnits(k->
		{
			if(k.canFight())
			{
				PosData kPos=k.pos.getPos();
				
				//高度值合适
				if(height<=0 || posLogic.getDHeight(kPos,pos)<=height)
				{
					//在圆形范围内
					if((posLogic.calculatePosDistanceSq2D(kPos,pos))<=sq)
					{
						//符合影响类型
						if(selfUnit.fight.checkTargetInfluence(k,influences))
						{
							posLogic.calculateDirByPos2D(tempDir,tempPos,kPos);
							
							float dirV=MathUtils.directionCut(dir.direction-tempDir.direction);
							
							if(Math.abs(dirV)<=halfDirection)
							{
								list.add(k);
							}
						}
					}
				}
			}
		});
	}
	
	/** 取圆形最近的战斗单位组 */
	public void getCircleNearFightUnits(SList<Unit> list,PosData pos,float radius,int max,float height,Unit selfUnit,boolean[] influences)
	{
		list.clear();
		
		ScenePosLogic posLogic=_scene.pos;
		float sq=radius*radius;
		
		selfUnit.aoi.forEachAroundUnits(k->
		{
			if(k.canFight())
			{
				PosData kPos=k.pos.getPos();
				
				//高度值合适
				if(height<=0 || posLogic.getDHeight(kPos,pos)<=height)
				{
					float dq;
					//在范围内
					if((dq=posLogic.calculatePosDistanceSq2D(kPos,pos))<=sq)
					{
						//符合影响类型
						if(selfUnit.fight.checkTargetInfluence(k,influences))
						{
							k.pos.tempSortKey=dq;
							list.add(k);
						}
					}
				}
			}
		});
		
		if(max>0 && list.length()>max)
		{
			list.sort(_unitNearComparator);
			list.cutToLength(max);
		}
	}
	
	/** 取圆形最近的战斗单位组 */
	public void getCircleRandomFightUnits(SList<Unit> list,PosData pos,float radius,int max,float height,Unit selfUnit,boolean[] influences)
	{
		getCircleFightUnits(list,pos,radius,height,selfUnit,influences);
		
		if(max>0 && list.length()>max)
		{
			//随机排序
			Unit[] values=list.getValues();
			int len=list.length();
			Unit temp;
			
			for(int i=0;i<max;++i)
			{
				int j=MathUtils.randomRange(i,len);
				
				if(i!=j)
				{
					temp=values[i];
					values[i]=values[j];
					values[j]=temp;
				}
			}
			
			list.cutToLength(max);
		}
	}
	
	/** 取圆形范围内最近的一个单位 */
	public Unit getNearestFightUnits(PosData pos,float radius,float height,Unit selfUnit,boolean[] influences)
	{
		ScenePosLogic posLogic=_scene.pos;
		float sq=radius*radius;
		
		ScenePosLogic.FindCircleNearestTemp cTemp=_circleNearestTemp;
		cTemp.dis=Float.MAX_VALUE;
		cTemp.unit=null;
		
		selfUnit.aoi.forEachAroundUnits(k->
		{
			if(k.canFight())
			{
				PosData kPos=k.pos.getPos();
				
				//高度值合适
				if(height<=0 || posLogic.getDHeight(kPos,pos)<=height)
				{
					float dq;
					//在范围内
					if((dq=posLogic.calculatePosDistanceSq2D(kPos,pos))<=sq)
					{
						//符合影响类型
						if(selfUnit.fight.checkTargetInfluence(k,influences))
						{
							if(dq<cTemp.dis)
							{
								cTemp.dis=dq;
								cTemp.unit=k;
							}
						}
					}
				}
			}
		});
		
		Unit re=cTemp.unit;
		cTemp.unit=null;
		
		return re;
	}
	
	/** 执行攻击数据 */
	public void executeAttack(AttackData data,IntList targetInstanceIDs)
	{
		if(_scene.isDriveAll())
		{
			toExecuteAttack(data,targetInstanceIDs);
		}
		else
		{
			Unit mTarget=_scene.getFightUnit(data.targetData.targetInstanceID);
			Unit from=_scene.getFightUnit(data.fromInstanceID);
			
			if(from.isSelfDriveAttackHappen())
			{
				SList<Unit> targets=new SList<>(Unit[]::new);
				toMakeTargets(targets,from,from.fight.getAttackerUnit(),mTarget,data.targetData,data.config,data.levelConfig);
				from.fight.sendCUnitAttack(data.levelConfig.id,data.levelConfig.level,data.targetData,targets,data.isBulletFirstHit);
			}
		}
	}
	
	/** 选择出目标 */
	protected void toMakeTargets(SList<Unit> list,Unit from,Unit attacker,Unit mTarget,SkillTargetData tData,AttackConfig config,AttackLevelConfig levelConfig)
	{
		float[] scopeArgs=levelConfig.scopeArgs;
		
		switch(levelConfig.scope)
		{
			case AttackScopeType.None:
			{
			
			}
				break;
			case AttackScopeType.Self:
			{
				if(attacker.fight.checkTargetInfluence(attacker,config.influenceTypeT))
				{
					list.add(attacker);
				}
			}
				break;
			case AttackScopeType.Single:
			{
				if(mTarget==null)
				{
					return;
				}
				
				if(attacker.fight.checkTargetInfluence(mTarget,config.influenceTypeT))
				{
					list.add(mTarget);
				}
			}
				break;
			case AttackScopeType.SelfCircle:
			{
				getCircleFightUnits(list,from.pos.getPos(),scopeArgs[0],scopeArgs.length>1?scopeArgs[1]:0,attacker,config.influenceTypeT);
			}
				break;
			case AttackScopeType.TargetCircle:
			{
				PosData posData;
				
				if((posData=tData.pos)!=null)
				{
					getCircleFightUnits(list,posData,scopeArgs[0],scopeArgs.length>1?scopeArgs[1]:0,attacker,config.influenceTypeT);
				}
				else if(mTarget!=null)
				{
					getCircleFightUnits(list,mTarget.pos.getPos(),scopeArgs[0],scopeArgs.length>1?scopeArgs[1]:0,attacker,config.influenceTypeT);
				}
			}
				break;
			case AttackScopeType.SelfRect:
			{
				PosData fromPos=from.pos.getPos();
				DirData dir;
				PosData posData;
				
				if((posData=tData.pos)!=null)
				{
					_scene.pos.calculateDirByPos(dir=_tempDir,fromPos,posData);
				}
				else if((dir=tData.dir)!=null)
				{
				
				}
				else if(mTarget!=null)
				{
					if(from==mTarget)
					{
						dir=from.pos.getDir();
					}
					else
					{
						_scene.pos.calculateDirByPos(dir=_tempDir,fromPos,mTarget.pos.getPos());
					}
				}
				else
				{
					dir=from.pos.getDir();
				}
				
				getRectFightUnits(list,fromPos,dir,scopeArgs[0],scopeArgs[1],scopeArgs.length>2?scopeArgs[2]:0,attacker,config.influenceTypeT);
			}
				break;
			case AttackScopeType.SelfSector:
			{
				PosData fromPos=from.pos.getPos();
				DirData dir;
				PosData posData;
				
				if((posData=tData.pos)!=null)
				{
					_scene.pos.calculateDirByPos(dir=_tempDir,fromPos,posData);
				}
				else if((dir=tData.dir)!=null)
				{
				
				}
				else if(mTarget!=null)
				{
					if(from==mTarget)
					{
						dir=from.pos.getDir();
					}
					else
					{
						_scene.pos.calculateDirByPos(dir=_tempDir,fromPos,mTarget.pos.getPos());
					}
				}
				else
				{
					dir=from.pos.getDir();
				}
				
				getSectorFightUnits(list,fromPos,dir,scopeArgs[0],scopeArgs[1],scopeArgs.length>2?scopeArgs[2]:0,attacker,config.influenceTypeT);
			}
				break;
			case AttackScopeType.SelfCircleNear:
			{
				getCircleNearFightUnits(list,from.pos.getPos(),scopeArgs[0],(int)scopeArgs[1],scopeArgs.length>2?scopeArgs[2]:0,attacker,config.influenceTypeT);
			}
				break;
			case AttackScopeType.SelfCircleRandom:
			{
				getCircleRandomFightUnits(list,from.pos.getPos(),scopeArgs[0],(int)scopeArgs[1],scopeArgs.length>2?scopeArgs[2]:0,attacker,config.influenceTypeT);
			}
				break;
			case AttackScopeType.SelfNearest:
			{
				Unit reUnit=getNearestFightUnits(from.pos.getPos(),scopeArgs[0],scopeArgs.length>1 ? scopeArgs[1] : 0,attacker,config.influenceTypeT);
				
				if(reUnit!=null)
				{
					list.add(reUnit);
				}
			}
				break;
			case AttackScopeType.AttackRect:
			case AttackScopeType.Around2D:
			{
				SRect rect;
				
				if(levelConfig.scope==AttackScopeType.AttackRect)
					rect=from.fight.getAttackBox(levelConfig);
				else
					rect=from.fight.getAttackAround2DBox(scopeArgs[0],scopeArgs[1]);
				
				from.aoi.forEachAroundUnits(unit->
				{
					if(attacker.fight.checkTargetInfluence(unit,config.influenceTypeT))
					{
						if(rect.overlap(unit.fight.getSelfBox()))
						{
							list.add(unit);
						}
					}
				});
			}
				break;
		}
	}
	
	/** 计算是否命中 */
	protected boolean toCountIsHit(AttackData data,Unit attacker,Unit target)
	{
		if(data.config.canDodge)
		{
			if(attacker.fight.getStatusLogic().getStatus(StatusType.AbsHit))
			{
				return true;
			}
			else
			{
				if(target.fight.getStatusLogic().getStatus(StatusType.AbsDodge))
				{
					return false;
				}
				else
				{
					return calculateHitRate(attacker.fight.getAttributeLogic().getAttribute(AttributeType.HitRate),target.fight.getAttributeLogic().getAttribute(AttributeType.Dodge),attacker,target,data);
				}
			}
		}
		else
		{
			return true;
		}
	}
	
	/** 计算是否暴击 */
	protected boolean toCountIsCrit(AttackData data,Unit attacker,Unit target)
	{
		if(data.config.canCrit)
		{
			if(target.fight.getStatusLogic().getStatus(StatusType.AbsTenacity))
			{
				return false;
			}
			else
			{
				if(attacker.fight.getStatusLogic().getStatus(StatusType.AbsCrit))
				{
					return true;
				}
				else
				{
					return calculateCrit(attacker.fight.getAttributeLogic().getAttribute(AttributeType.Crit),target.fight.getAttributeLogic().getAttribute(AttributeType.Tenacity),attacker,target,data);
				}
			}
		}
		else
		{
			return false;
		}
	}
	
	/** 伤害类型是否产生实际伤害 */
	protected boolean needDamageHurt(int damageType)
	{
		switch(damageType)
		{
			case SkillDamageType.PhysicsDamage:
			case SkillDamageType.MagicDamage:
			case SkillDamageType.HolyDamage:
			{
				return true;
			}
		}
		
		return false;
	}
	
	/** 计算对单个单位的伤害(范围伤害值) */
	protected int toCalculateDamage(AttackData data,int damageType,int[] damages,int attackValue,boolean isCrit,AttributeDataLogic attackerAttribute,AttributeDataLogic targetAttribute,Unit attacker,Unit target)
	{
		int damageValue;
		
		switch(damageType)
		{
			case SkillDamageType.PhysicsDamage:
			case SkillDamageType.MagicDamage:
			case SkillDamageType.HolyDamage:
			{
				damageValue=calculateDamage(damages,attackValue,isCrit,attackerAttribute,targetAttribute,attacker,target,data);
			}
				break;
			case SkillDamageType.HpAdd:
			{
				damageValue=attackValue;
				
				if(!target.fight.getStatusLogic().cantBeHeal())
				{
					int healV=targetAttribute.getAddRatioResult(AttributeType.HealAddPercent,damageValue);
					targetAttribute.addOneAttribute(AttributeType.Hp,healV);
				}
			}
				break;
			case SkillDamageType.MpAdd:
			{
				damageValue=attackValue;
				targetAttribute.addOneAttribute(AttributeType.Mp,damageValue);
			}
				break;
			case SkillDamageType.MpSub:
			{
				damageValue=attackValue;
				targetAttribute.subOneAttribute(AttributeType.Mp,damageValue);
			}
				break;
			default:
			{
				damageValue=calculateDamage(damages,attackValue,isCrit,attackerAttribute,targetAttribute,attacker,target,data);
			}
				break;
		}
		
		return damageValue;
	}
	
	/** 执行攻击数据 */
	private void toExecuteAttack(AttackData data,IntList targetInstanceIDs)
	{
		Unit from=_scene.getFightUnit(data.fromInstanceID);
		
		if(from==null)
			return;
		
		Unit attacker=from.fight.getAttackerUnit();
		
		if(attacker==null)
			return;
		
		int attackerInstanceID=attacker.instanceID;
		
		SkillTargetData tData=data.targetData;
		AttackConfig config=data.config;
		AttackLevelConfig levelConfig=data.levelConfig;
		
		Unit target;
		
		//主目标
		Unit mTarget=_scene.getFightUnit(tData.targetInstanceID);
		
		SList<Unit> tempTargets=_unitListPool.getOne();
		
		if(targetInstanceIDs!=null)
		{
			int[] values=targetInstanceIDs.getValues();
			
			for(int i=0,len=targetInstanceIDs.length();i<len;++i)
			{
				target=_scene.getFightUnit(values[i]);
				
				if(target!=null && attacker.fight.checkTargetInfluence(target,config.influenceTypeT))
				{
					tempTargets.add(target);
				}
			}
		}
		else
		{
			toMakeTargets(tempTargets,from,attacker,mTarget,tData,config,levelConfig);
		}
		
		//TODO:目标过滤
		
		
		//TODO:攻击前动作组
		
		//如果跳过伤害阶段
		if(config.passDamage)
		{
			Unit[] values=tempTargets.getValues();
			
			for(int i=0,len=tempTargets.size();i<len;++i)
			{
				target=values[i];
				
				//不可被攻击
				if(target.fight.getStatusLogic().cantBeAttackTarget())
					continue;
				
				if(levelConfig.addBuffProbID>0)
				{
					if(attacker.fight.randomProb(levelConfig.addBuffProbID))
					{
						int adderInstanceID=attackerInstanceID==target.instanceID ? -1 :attackerInstanceID;
						
						for(DIntData v:levelConfig.addBuffs)
						{
							target.fight.getBuffLogic().addBuff(v.key,v.value,adderInstanceID);
						}
					}
				}
				
				//有子弹
				DIntData levelConfigBullet;
				
				if((levelConfigBullet=levelConfig.bullet).key>0 && _scene.isServerDriveAttackHappen())
				{
					//要推送
					from.fight.createAndExecuteBullet(levelConfigBullet.key,levelConfigBullet.value,tData);
				}
			}
		}
		else
		{
			doAttackMoment(AttackMomentType.AttackBeforeHit,from,attacker,null,data,null);
			
			if(data.isBulletFirstHit)
			{
				//只生效一个目标
				data.isBulletFirstHit=false;
				
				doAttackMoment(AttackMomentType.AttackBeforeHitForBulletFirstHit,from,attacker,null,data,null);
			}
			
			if(!tempTargets.isEmpty())
			{
				Unit[] values=tempTargets.getValues();
				
				//(击杀组不缓存为了递归调用)
				SList<Unit> tempKilledUnits=null;
				
				SList<DamageOneData> damageDataList=new SList<>();
				
				StatusDataLogic attackerStatus=attacker.fight.getStatusLogic();
				AttributeDataLogic attackerAttribute=attacker.fight.getAttributeLogic();
				BuffDataLogic attackerBuffLogic=attacker.fight.getBuffLogic();
				StatusDataLogic targetStatus;
				AttributeDataLogic targetAttribute;
				
				boolean momentHitted=false;
				
				for(int i=0,len=tempTargets.size();i<len;++i)
				{
					target=values[i];
					
					targetStatus=target.fight.getStatusLogic();
					targetAttribute=target.fight.getAttributeLogic();
					
					//不可被攻击
					if(targetStatus.cantBeAttackTarget())
						continue;
					
					//上次血量
					int lastHp=targetAttribute.getHp();
					
					DamageOneData damageOneData=new DamageOneData();
					damageOneData.instanceID=target.instanceID;
					
					damageDataList.add(damageOneData);
					
					SList<DIntData> damageList=new SList<>();
					damageOneData.damages=damageList;
					
					damageOneData.isHit=toCountIsHit(data,attacker,target);
					
					doAttackMoment(AttackMomentType.BeAttackBeforeHit,target,target,attacker,data,damageOneData);
					
					int realDamage=0;
					
					if(damageOneData.isHit)
					{
						if(!momentHitted)
						{
							momentHitted=true;
							doAttackMoment(AttackMomentType.AttackOnHitAnyTarget,from,attacker,target,data,damageOneData);
							_scene.method.onUnitAttackOnHitAnyTarget(attacker,target,data);
						}
						
						if(target==mTarget)
						{
							doAttackMoment(AttackMomentType.AttackBeforeDamageMainTarget,from,attacker,target,data,damageOneData);
						}
						
						doAttackMoment(AttackMomentType.AttackBeforeDamageEachTarget,from,attacker,target,data,damageOneData);
						doAttackMoment(AttackMomentType.BeAttackBeforeDamage,target,target,attacker,data,damageOneData);
						
						//添加buff
						if(levelConfig.addBuffProbID>0)
						{
							if(attacker.fight.randomProb(levelConfig.addBuffProbID))
							{
								int adderInstanceID=attackerInstanceID==target.instanceID ? -1 :attackerInstanceID;
								
								for(DIntData v:levelConfig.addBuffs)
								{
									target.fight.getBuffLogic().addBuff(v.key,v.value,adderInstanceID);
								}
							}
						}
						
						//有子弹
						DIntData levelConfigBullet;
						
						if((levelConfigBullet=levelConfig.bullet).key>0 && _scene.isServerDriveAttackHappen())
						{
							//要推送
							from.fight.createAndExecuteBullet(levelConfigBullet.key,levelConfigBullet.value,tData);
						}
						
						boolean isCrit=damageOneData.isCrit=toCountIsCrit(data,attacker,target);
						
						SkillVarConfig varConfig;
						int[] damages;
						int damageType;
						int damageValue;
						int attackValue;
						int damageVarIndex=0;
						
						//伤害组
						for(int j=0,jLen=levelConfig.damages.length;j<jLen;++j)
						{
							varConfig=levelConfig.varConfigT[j];
							damages=levelConfig.damages[j];
							damageType=damages[0];
							
							if(data.isRecorded)
							{
								attackValue=BaseGameUtils.calculateSkillVarValueForSelf(varConfig,data.selfAttackValues,damageVarIndex,target.fight.getDataLogic());
								damageVarIndex+=varConfig.args.length;
							}
							else
							{
								attackValue=BaseGameUtils.calculateSkillVarValueFull(varConfig,attacker.fight.getDataLogic(),target.fight.getDataLogic());
							}
							
							damageValue=toCalculateDamage(data,damageType,damages,attackValue,isCrit,attackerAttribute,targetAttribute,attacker,target);
							
							if(damageValue>0)
							{
								if(needDamageHurt(damageType))
								{
									int rd=0;
									
									if(!targetStatus.isDamageImmun())
									{
										rd=takeDamage(damageType,damageValue,targetAttribute,target);
									}
									
									if(Global.damageValueUseRealDamage)
									{
										damageValue=rd;
									}
									
									realDamage+=rd;
								}
								
								//TODO:反伤类
								
								damageList.add(DIntData.create(damageType,damageValue));
							}
						}
						
						if(realDamage>0)
						{
							_scene.method.onUnitTakeDamage(target,realDamage,attacker);
						}
						
						//onDamage接口
						
						if(target==mTarget)
						{
							doAttackMoment(AttackMomentType.AttackAfterDamageMainTarget,from,attacker,target,data,damageOneData);
						}
						
						doAttackMoment(AttackMomentType.AttackAfterDamageEachTarget,from,attacker,target,data,damageOneData);
						doAttackMoment(AttackMomentType.BeAttackAfterDamage,target,target,attacker,data,damageOneData);
						
						//扣除buff次数
						attackerBuffLogic.subBuffNumArr(BuffSubNumType.UseAttackFromGroup,config.groups);
						attackerBuffLogic.subBuffNumArr(BuffSubNumType.MakeDamage,levelConfig.damageTypesT);
						target.fight.getBuffLogic().subBuffNumArr(BuffSubNumType.BeDamage,levelConfig.damageTypesT);
						
						//上次血量>0，并且本次血减到0
						if(lastHp>0 && targetAttribute.getAttribute(AttributeType.Hp)<=0 && targetStatus.isAlive())
						{
							if(tempKilledUnits==null)
							{
								tempKilledUnits=new SList<>(Unit[]::new);
							}
							
							tempKilledUnits.add(target);
						}
					}
					
					target.fight.onDamage(realDamage,from,attacker,config);
				}
				
				//推送伤害
				from.fight.sendAttackDamage(levelConfig.id,levelConfig.level,tData,damageDataList,tempTargets);
				
				//处理击杀
				if(tempKilledUnits!=null)
				{
					values=tempKilledUnits.getValues();
					
					for(int i=0,len=tempKilledUnits.size();i<len;++i)
					{
						//活着
						if((target=values[i]).fight.isAlive())
						{
							//被击杀时刻
							doAttackMoment(AttackMomentType.BeforeBeKill,target,target,attacker,data,null);
							
							//依旧小于0(BeforeBeKill期间没有治疗)
							if(target.fight.getAttributeLogic().getHp()<=0)
							{
								//可被伤害击杀
								if(!target.fight.getStatusLogic().cantBeKillByDamage())
								{
									target.fight.doDead(attacker,config.killType);
								}
								
								//击杀时刻
								doAttackMoment(AttackMomentType.AfterKill,from,attacker,target,data,null);
							}
						}
					}
				}
			}
			else
			{
				//推送伤害
				from.fight.sendAttackDamage(levelConfig.id,levelConfig.level,tData,null,null);
			}
			
			doAttackMoment(AttackMomentType.AttackAfterHit,from,attacker,null,data,null);
		}
		
		tempTargets.clear();
		_unitListPool.back(tempTargets);
	}
	
	/** 处理攻击时刻 */
	protected void doAttackMoment(int moment,Unit from,Unit attacker,Unit target,AttackData lastData,DamageOneData damageOneData)
	{
		int[][] intArr;
		
		if((intArr=lastData.levelConfig.momentActionsT[moment])!=null)
		{
			for(int[] args:intArr)
			{
				if(args[0]==moment)
				{
					if(attacker.fight.randomProb(args[1]))
					{
						doOneAttackAction(args,2,from,attacker,target,lastData);
					}
				}
			}
		}
		
		IntObjectMap<int[]> momentDic=attacker.fight.getBuffLogic().getAttackProbActionDic(moment);
		
		if(momentDic!=null && !momentDic.isEmpty())
		{
			int[][] momentValues=momentDic.getValues();
			int[] momentV;
			
			for(int j=momentValues.length - 1;j >= 0;--j)
			{
				if((momentV=momentValues[j])!=null)
				{
					if(momentV[2]<=0 || lastData.config.hasGroup(momentV[2]))
					{
						if(attacker.fight.randomProb(momentV[3]))
						{
							doOneAttackAction(momentV,4,from,attacker,target,lastData);
						}
					}
				}
			}
		}
	}
	
	/** 执行攻击动作 */
	protected void doOneAttackAction(int[] args,int off,Unit from,Unit attacker,Unit target,AttackData lastData)
	{
		switch(args[off])
		{
			case AttackActionType.Attack:
			{
				SkillTargetData tData=SkillTargetData.createByTargetUnit(target!=null ? target.instanceID : -1);
				from.fight.createAndExecuteAttack(args[off+1],args[off+2],tData);
			}
				break;
			case AttackActionType.Bullet:
			{
				SkillTargetData tData=SkillTargetData.createByTargetUnit(target!=null ? target.instanceID : -1);
				
				//要推送
				from.fight.createAndExecuteBullet(args[off+1],args[off+2],tData);
			}
				break;
			case AttackActionType.SelfAddAttribute:
			{
				attacker.fight.getAttributeLogic().addOneAttribute(args[off+1],args[off+2]);
			}
				break;
			case AttackActionType.TargetAddAttribute:
			{
				if(target!=null)
				{
					target.fight.getAttributeLogic().addOneAttribute(args[off+1],args[off+2]);
				}
			}
				break;
			case AttackActionType.SelfAddAttributeVar:
			{
				attacker.fight.getAttributeLogic().addOneAttribute(args[off+1],calculateSkillVar(args[off+2],attacker,target));
			}
				break;
			case AttackActionType.TargetAddAttributeVar:
			{
				if(target!=null)
				{
					target.fight.getAttributeLogic().addOneAttribute(args[off+1],calculateSkillVar(args[off+2],target,attacker));
				}
			}
				break;
			case AttackActionType.SelfSpecialMove:
			{
				attacker.move.specialMoveTarget(args[off+1],target);
			}
				break;
			case AttackActionType.TargetSpecialMove:
			{
				target.move.specialMoveTarget(args[off+1],attacker);
			}
				break;
			case AttackActionType.SelfAddBuff:
			{
				attacker.fight.getBuffLogic().addBuff(args[off+1],args[off+2]);
			}
				break;
			case AttackActionType.TargetAddBuff:
			{
				if(target!=null)
				{
					target.fight.getBuffLogic().addBuff(args[off+1],args[off+2],attacker.instanceID);
				}
			}
				break;
			case AttackActionType.AddGroupTimePass:
			{
				attacker.fight.getCDDataLogic().addGroupTimePass(args[off+1],args[off+2]);
			}
				break;
			case AttackActionType.RemoveGroupCD:
			{
				attacker.fight.getCDDataLogic().removeGroupCD(args[off+1]);
			}
				break;
			case AttackActionType.AddPuppetAtTarget:
			{
				SkillTargetData tData=SkillTargetData.createByTargetUnit(target!=null ? target.instanceID : -1);
				
				getSkillTargetPos(_tempPos,tData);
				
				_scene.unitFactory.createAddPuppet(args[1],args[2],_tempPos,attacker,args.length>3 ? args[3] : 0);
			}
				break;
			case AttackActionType.KillSelf:
			{
				attacker.fight.doDead(attacker,UnitDeadType.Skill);
			}
				break;
			case AttackActionType.KillTarget:
			{
				target.fight.doDead(attacker,UnitDeadType.Skill);
			}
				break;
			case AttackActionType.SelfRemoveGroupBuff:
			{
				attacker.fight.getBuffLogic().removeBuffByGroup(args[1]);
			}
				break;
			case AttackActionType.TargetRemoveGroupBuff:
			{
				target.fight.getBuffLogic().removeBuffByGroup(args[1]);
			}
				break;
		}
	}
	
	/** 获取技能目标数据位置 */
	public void getSkillTargetPos(PosData re,SkillTargetData tData)
	{
		if(tData.targetInstanceID!=-1)
		{
			Unit unit=_scene.getFightUnit(tData.targetInstanceID);
			re.copyPos(unit.pos.getPos());
		}
		else
		{
			PosData pp;
			
			if((pp=tData.pos)!=null)
			{
				re.copyPos(pp);
			}
			else
			{
				re.clear();
			}
		}
	}
	
	/** 获取技能目标数据位置 */
	public void getSkillTargetDirection(DirData re,PosData pos,SkillTargetData tData)
	{
		if(tData.type==SkillTargetType.None || tData.type==SkillTargetType.AttackScope)
		{
			Unit unit=_scene.getFightUnit(tData.targetInstanceID);
			
			if(unit!=null)
				re.copyDir(unit.pos.getDir());
			else
				re.clear();
			
			return;
		}
		
		if(tData.targetInstanceID!=-1)
		{
			Unit unit=_scene.getFightUnit(tData.targetInstanceID);
			
			if(unit!=null)
			{
				_scene.pos.calculateDirByPos(re,pos,unit.pos.getPos());
			}
			else
			{
				re.clear();
			}
		}
		else
		{
			PosData pp=tData.pos;
			
			if(pp!=null)
			{
				_scene.pos.calculateDirByPos(re,pos,pp);
			}
			else
			{
				if(tData.dir==null)
					re.clear();
				else
					re.copyDir(tData.dir);
			}
		}
	}
	
	/** 执行并析构攻击数据 */
	public void executeAndReleaseAttack(AttackData data)
	{
		executeAttack(data,null);
		_scene.getExecutor().attackDataPool.back(data);
	}
}
