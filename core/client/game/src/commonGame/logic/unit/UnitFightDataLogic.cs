using System;
using ShineEngine;

/// <summary>
/// 战斗单位数据逻辑
/// </summary>
public class UnitFightDataLogic
{
	private UnitFightData _data;

	/** 状态子逻辑 */
	public StatusDataLogic status;

	/** 属性子逻辑 */
	public AttributeDataLogic attribute;

	/** cd子逻辑 */
	public CDDataLogic cd;

	/** buff逻辑 */
	public BuffDataLogic buff;
	
	//造型逻辑暂行归在战斗里
	/** 造型逻辑 */
	public AvatarDataLogic avatar;

	/** 是否自己控制标记 */
	public bool isSelfControl=false;

	/** 是否自己驱动所有 */
	private bool _isDriveAll;

	/** 绑定场景 */
	private Scene _scene;
	//unit部分
	protected Unit _unit;
	
	public UnitFightDataLogic()
	{

	}

	/** 构造 */
	public virtual void construct()
	{
		_isDriveAll=!CommonSetting.isSceneServerDrive;

		status=new StatusDataLogic(this);
		attribute=new AttributeDataLogic(this);
		cd=new CDDataLogic(this);
		(buff=GameC.factory.createBuffDataLogic()).setParent(this);
		avatar=new AvatarDataLogic(this);
	}

	public void reloadConfig()
	{
		buff.reloadConfig();
		cd.reloadConfig();
	}

	/** 设置buff数据逻辑 */
	public void setBuffDataLogic(BuffDataLogic logic)
	{
		(buff=logic).setParent(this);
	}

	/** 是否客户端驱动全部战斗 */
	public virtual bool isDriveAll()
	{
		return _isDriveAll;
	}

	/** 设置是否驱动全部(临时用) */
	public void setIsDriveAll(bool value)
	{
		_isDriveAll=value;
	}

	/** 是否自己驱动攻击发生 */
	public virtual bool isSelfDriveAttackHapen()
	{
		if(_isDriveAll)
			return true;

		if(_unit!=null)
		{
			return _unit.isSelfDriveAttackHapen();
		}

		return !CommonSetting.isSceneServerDrive || isSelfControl;
	}

	/** 设置数据 */
	public void setData(UnitFightData fightData,UnitAvatarData facadeData)
	{
		if((_data=fightData)!=null)
		{
			status.setData(fightData.status);
			attribute.setData(fightData.attributes);
			cd.setData(fightData.cds);
			buff.setData(fightData.buffs);
			avatar.setData(facadeData);
		}
		else
		{
			status.setData(null);
			attribute.setData(null);
			cd.setData(null);
			buff.setData(null);
			avatar.setData(null);
		}
	}

	/** 获取战斗数据 */
	public UnitFightData getData()
	{
		return _data;
	}

	//操作方法组
	
	/** 刷新数据 */
	public void onPiece(int delay)
	{
		status.refreshStatus();
		attribute.onPiece(delay);
		avatar.refreshAvatar();
	}

	public void onPieceEx(int delay)
	{
		cd.onPiece(delay);
		buff.onPiece(delay);
	}

	/** 清理数据 */
	public void clear()
	{
		status.clear();
		attribute.clear();
		cd.clear();
		buff.clear();
		avatar.clear();

		_data.skills.clear();
	}


	//技能部分

	/** 添加技能 */
	public void addSkill(int id,int level)
	{
		addSkill(SkillData.create(id,level));
	}

	/** 添加技能 */
	public void addSkill(SkillData data)
	{
		_data.skills.put(data.id,data);

		onAddSkill(data);
	}

	/** 删除技能 */
	public void removeSkill(int id)
	{
		SkillData data=_data.skills.remove(id);

		if(data!=null)
		{
			onRemoveSkill(data);
		}
	}

	/** 获取技能数据 */
	public SkillData getSkill(int id)
	{
		return _data.skills.get(id);
	}

	/** 添加技能组 */
	public void addSkills(DIntData[] list)
	{
		foreach(DIntData v in list)
		{
			addSkill(SkillData.createByDInt(v));
		}
	}

	public void addSkillByServer(SkillData data)
	{
		addSkill(data);
	}

	public void removeSkillByServer(int id)
	{
		removeSkill(id);
	}

	/** 从另一个数据逻辑上复制数据(非数据的其他内容) */
	public void copyFrom(UnitFightDataLogic logic)
	{
		
	}
	
	//单位部分
	
	/** 绑定单位 */
	public void setUnit(Unit unit)
	{
		_unit=unit;
		setScene(unit!=null ? unit.getScene() : null);
	}

	/** 获取单位 */
	public Unit getUnit()
	{
		return _unit;
	}

	/** 绑定场景 */
	private void setScene(Scene scene)
	{
		_scene=scene;
		_isDriveAll=scene!=null ? scene.isDriveAll() : !CommonSetting.isSceneServerDrive;
	}

	//接口

	/** 触发buffAttack */
	public virtual void doBuffIntervalAttack(BuffIntervalActionData data)
	{
		Unit unit;
		
		if((unit=_unit)!=null)
		{
			unit.fight.doBuffIntervalAttack(data);
		}
	}

	/** 触发buff攻击 */
	public void doBuffAttack(int id,int level)
	{
		Unit unit;

		if((unit=_unit)!=null)
		{
			unit.fight.doBuffAttack(id,level);
		}
	}

	/** 获取某变量值表示的值(总值)(自身) */
	public int getSkillVarValue(int varID)
	{
		return BaseGameUtils.calculateSkillVarValueFull(varID,this,null);
	}

	/** 获取某变量值表示的值(总值)(战斗用) */
	public virtual int getSkillVarValueT(int varID,int adderID)
	{
		Unit unit;

		if((unit=_unit)!=null)
		{
			return unit.fight.getSkillVarValueT(varID,adderID);
		}

		return 0;
	}

	/** 获取变量源值 */
	public virtual int getSkillVarSourceValue(int[] args,bool isSelf)
	{
		if(isSelf)
		{
			switch(args[0])
			{
				case SkillVarSourceType.SelfAttribute:
				{
					return attribute.getAttribute(args[1]);
				}
				case SkillVarSourceType.SelfCurrentAttributePercent:
				{
					return attribute.getCurrentPercent(args[1]);
				}
				case SkillVarSourceType.SelfCurrentAttributeLostPercent:
				{
					return 1000-attribute.getCurrentPercent(args[1]);
				}
				case SkillVarSourceType.SelfBuffFloor:
				{
					return buff.getBuffFloor(args[1]);
				}
			}
		}
		else
		{
			switch(args[0])
			{
				case SkillVarSourceType.TargetAttribute:
				{
					return attribute.getAttribute(args[1]);
				}
				case SkillVarSourceType.TargetCurrentAttributePercent:
				{
					return attribute.getCurrentPercent(args[1]);
				}
				case SkillVarSourceType.TargetCurrentAttributeLostPercent:
				{
					return 1000-attribute.getCurrentPercent(args[1]);
				}
				case SkillVarSourceType.TargetBuffFloor:
				{
					return buff.getBuffFloor(args[1]);
				}
			}
		}

		Unit unit;

		if((unit=_unit)!=null)
		{
			return unit.fight.toGetSkillVarSourceValue(args,isSelf);
		}

		return 0;
	}

	/** 执行单个buff结束方法 */
	public void doOneBuffOverActionEx(int[] args)
	{
		Unit unit;

		if((unit=_unit)!=null)
		{
			unit.fight.doOneBuffOverActionEx(args);
		}
	}

	/** 通过身份数据初始化战斗单位 */
	public void initByFightUnit(FightUnitIdentityData data)
	{
		int fightUnitID=data.getFightUnitID();

		if(fightUnitID<=0)
		{
			Ctrl.throwError("不能没有战斗单位ID");
		}

		//主控标记
		isSelfControl=data.controlPlayerID==GameC.player.role.playerID;

		initByFightUnitConfig(fightUnitID);
		initByFightUnitLevelConfig(fightUnitID,data.level);
	}

	/** 通过战斗单位配置初始化 */
	public void initByFightUnitConfig(int id)
	{
		FightUnitConfig config=FightUnitConfig.get(id);

		//默认模型ID
		avatar.setDefaultModelID(config.modelID>0 ? config.modelID : -1);
	}

	/** 通过战斗单位等级初始化 */
	public void initByFightUnitLevelConfig(int id,int level)
	{
		FightUnitLevelConfig levelConfig=FightUnitLevelConfig.get(id,level);

		//属性
		attribute.addAttributes(levelConfig.attributes);
		//技能
		addSkills(levelConfig.skills);
		//buff
		buff.addBuffs(levelConfig.buffs);
		//当前属性
		attribute.makeCurrentToDefault();
	}

	//--接口部分--//

	/** 状态变化接口 */
	public virtual void onStatusChange(bool[] changeSet)
	{
		Unit unit;
		
		if((unit=_unit)!=null)
		{
			unit.fight.onStatusChange(changeSet);
		}
	}

	/** 属性变化接口 */
	public virtual void onAttributeChange(int[] changeList,int num,bool[] changeSet,int[] lastAttributes)
	{
		Unit unit;
		
		if((unit=_unit)!=null)
		{
			unit.fight.onAttributeChange(changeList,num,changeSet,lastAttributes);
		}
	}

	/** 开始一个组CD */
	public void onStartGroupCD(int groupID)
	{
		Unit unit;

		if((unit=_unit)!=null)
		{
			unit.fight.onStartGroupCD(groupID);
		}
	}

	/** 结束一个组CD */
	public void onEndGroupCD(int groupID)
	{
		Unit unit;

		if((unit=_unit)!=null)
		{
			unit.fight.onEndGroupCD(groupID);
		}
	}

	/** cd变化接口(表现用) */
	public virtual void onCDChange()
	{
		
	}

	/** 添加buff时 */
	public virtual void onAddBuff(BuffData data)
	{
		Unit unit;
		
		if((unit=_unit)!=null)
		{
			unit.fight.onAddBuff(data);
		}
	}

	/** 删除buff时 */
	public virtual void onRemoveBuff(BuffData data)
	{
		Unit unit;
		
		if((unit=_unit)!=null)
		{
			unit.fight.onRemoveBuff(data);
		}
	}

	/** 刷新buff时 */
	public virtual void onRefreshBuff(BuffData data)
	{
		Unit unit;
		
		if((unit=_unit)!=null)
		{
			unit.fight.onRefreshBuff(data);
		}
	}
	
	/** 造型改变 */
	public virtual void onAvatarChange(int modelID)
	{
		Unit unit;
		
		if((unit=_unit)!=null)
		{
			unit.avatar.onAvatarChange(modelID);
		}
	}
	
	/** 造型部件改变 */
	public virtual void onAvatarPartChange(IntIntMap dic)
	{
		Unit unit;
		
		if((unit=_unit)!=null)
		{
			unit.avatar.onAvatarPartChange(dic);
		}
	}

	/** 添加/更新技能 */
	public virtual void onAddSkill(SkillData data)
	{

	}

	/** 技能移除 */
	public virtual void onRemoveSkill(SkillData data)
	{

	}
}