package com.home.commonBase.logic.unit;


import com.home.commonBase.config.game.FightUnitConfig;
import com.home.commonBase.config.game.FightUnitLevelConfig;
import com.home.commonBase.constlist.generate.SkillVarSourceType;
import com.home.commonBase.constlist.scene.BuffSendType;
import com.home.commonBase.control.AttributeControl;
import com.home.commonBase.data.scene.base.BuffData;
import com.home.commonBase.data.scene.base.CDData;
import com.home.commonBase.data.scene.base.SkillData;
import com.home.commonBase.data.scene.unit.UnitAvatarData;
import com.home.commonBase.data.scene.unit.UnitFightData;
import com.home.commonBase.data.scene.unit.identity.FightUnitIdentityData;
import com.home.commonBase.dataEx.scene.BuffIntervalActionData;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.scene.base.Scene;
import com.home.commonBase.scene.base.Unit;
import com.home.commonBase.utils.BaseGameUtils;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.data.DIntData;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.collection.IntBooleanMap;
import com.home.shine.support.collection.IntIntMap;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.collection.SList;
import com.home.shine.utils.OtherUtils;

/** 单位战斗数据逻辑体 */
public class UnitFightDataLogic
{
	protected UnitFightData _data;
	
	/** 状态子逻辑 */
	public StatusDataLogic status;
	/** 属性子逻辑 */
	public AttributeDataLogic attribute;
	/** cd子逻辑 */
	public CDDataLogic cd;
	/** buff子逻辑 */
	public BuffDataLogic buff;
	
	//造型逻辑暂行归在战斗里
	/** 造型逻辑 */
	public AvatarDataLogic avatar;
	
	//unit部分
	///** 绑定场景 */
	//private Scene _scene;
	/** 单位 */
	protected Unit _unit;
	
	/** 空技能组 */
	private static IntObjectMap<SkillData> _emptySkills=new IntObjectMap<>();
	private IntObjectMap<SkillData> _tempSkills;
	
	/** 空CD组 */
	private static IntObjectMap<CDData> _emptyCDs=new IntObjectMap<>();
	private IntObjectMap<CDData> _tempCDs;
	
	/** 是否驱动全部 */
	private boolean _isDriveAll;
	
	public UnitFightDataLogic()
	{
	
	}
	
	/** 构造 */
	public void construct()
	{
		_isDriveAll=!CommonSetting.isClient || !CommonSetting.isSceneServerDrive;
		
		status=BaseC.factory.createStatusDataLogic();
		status.setParent(this);
		attribute=new AttributeDataLogic(this);
		(cd=BaseC.factory.createCDDataLogic()).setParent(this);
		(buff=BaseC.factory.createBuffDataLogic()).setParent(this);
		avatar=new AvatarDataLogic(this);
	}
	
	/** 是否客户端驱动全部战斗 */
	public boolean isDriveAll()
	{
		return _isDriveAll;
	}
	
	/** 设置数据(初始赋值) */
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
	
	public void reloadConfig()
	{
		buff.reloadConfig();
		cd.reloadConfig();
	}

	/** 刷新数据 */
	public void onPiece(int delay)
	{
		status.refreshStatus();
		attribute.onPiece(delay);
		avatar.refreshAvatar();
	}
	
	/** 每份时间 */
	public void onPieceEx(int delay)
	{
		cd.onPiece(delay);
		buff.onPiece(delay);
	}
	
	/** 清理数据(即将回池) */
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
	
	/** 添加/刷新技能 */
	public void addSkill(int id,int level)
	{
		addSkill(SkillData.create(id,level));
	}
	
	/** 添加/刷新技能 */
	public void addSkill(SkillData data)
	{
		_data.skills.put(data.id,data);
		sendAddSkill(data);
	}
	
	/** 删除技能 */
	public void removeSkill(int id)
	{
		if(ShineSetting.openCheck)
		{
			if(!_data.skills.contains(id))
			{
				Ctrl.throwError("技能删除时，不存在:",id);
			}
		}
		
		_data.skills.remove(id);
		sendRemoveSkill(id);
	}
	
	/** 获取技能数据 */
	public SkillData getSkill(int id)
	{
		return _data.skills.get(id);
	}
	
	/** 添加技能组 */
	public void addSkills(DIntData[] list)
	{
		for(DIntData v:list)
		{
			addSkill(SkillData.createByDInt(v));
		}
	}
	
	/** 移除技能组 */
	public void removeSkills(DIntData[] list)
	{
		for(DIntData v:list)
		{
			removeSkill(v.key);
		}
	}
	
	/** 从另一个data上复制数据(复制服务器数据用) */
	public void copyData(UnitFightData data)
	{
		//直接潜拷
		_data.shadowCopy(data);
	}
	
	//单位方法
	
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
		//_scene=unit;
		_isDriveAll=scene!=null ? scene.isDriveAll() : (!CommonSetting.isClient || !CommonSetting.isSceneServerDrive);
	}
	
	/** 切换拷贝 */
	public void switchCopy()
	{
		status.writeForCopy();
		attribute.writeForCopy();
		
		buff.writeForCopy();
		
		_tempSkills=null;
		_tempCDs=null;
	}
	
	/** 切换推送(isSelf:是否给自己推) */
	public void switchSendSelf()
	{
		status.writeForSelf();
		attribute.writeForSelf();
		
		buff.writeSendDic(true);
		
		_tempSkills=null;
		_tempCDs=null;
	}
	
	/** 切换推送(其他人,通过unit识别) */
	public void switchSendOther()
	{
		status.writeForOther();
		attribute.writeForOther();
		
		buff.writeSendDic(false);
		
		UnitFightData data=_data;
		
		_tempSkills=data.skills;
		data.skills=_emptySkills;
		
		_tempCDs=data.cds;
		data.cds=_emptyCDs;
	}
	
	/** 结束切换推送 */
	public void endSwitchSend()
	{
		if(_tempSkills!=null)
		{
			_data.skills=_tempSkills;
			_tempSkills=null;
		}
		
		if(_tempCDs!=null)
		{
			_data.cds=_tempCDs;
			_tempCDs=null;
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
	
	/** 战斗单位升级 */
	public void fightUnitLevelUp(int id,int oldLevel,int newLevel)
	{
		FightUnitLevelConfig oldConfig=FightUnitLevelConfig.get(id,oldLevel);
		FightUnitLevelConfig newConfig=FightUnitLevelConfig.get(id,newLevel);
		
		attribute.subAttributes(oldConfig.attributes);
		attribute.addAttributes(newConfig.attributes);
		
		//不等
		if(!OtherUtils.dataArrEquals(oldConfig.skills,newConfig.skills))
		{
			removeSkills(oldConfig.skills);
			addSkills(newConfig.skills);
		}
	}
	
	//接口
	
	/** 触发buff间隔Attack */
	public void doBuffIntervalAttack(BuffIntervalActionData data)
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
	public int getSkillVarValueT(int varID,int adderID)
	{
		Unit unit;
		
		if((unit=_unit)!=null)
		{
			return unit.fight.getSkillVarValueT(varID,adderID);
		}
		
		return 0;
	}
	
	/** 获取变量源值 */
	public int getSkillVarSourceValue(int[] args,boolean isSelf)
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
	
	/** 推送自己属性(M单位使用) */
	public void sendSelfAttribute(IntIntMap dic)
	{
	
	}
	
	/** 推送别人属性 */
	public void sendOtherAttribute(IntIntMap dic)
	{
		Unit unit;
		
		if((unit=_unit)!=null)
		{
			unit.fight.sendOtherAttribute(dic);
		}
	}
	
	/** 排放属性 */
	public void dispatchAttribute(int[] changeList,int num,boolean[] changeSet,int[] lastAttributes)
	{
		Unit unit;
		
		if((unit=_unit)!=null)
		{
			unit.fight.onAttributeChange(changeList,num,changeSet,lastAttributes);
		}
	}
	
	/** 推送自己状态(M单位使用) */
	public void sendSelfStatus(IntBooleanMap dic)
	{
	
	}
	
	/** 推送别人状态 */
	public void sendOtherStatus(IntBooleanMap dic)
	{
		Unit unit;
		
		if((unit=_unit)!=null)
		{
			unit.fight.sendOtherStatus(dic);
		}
	}
	
	/** 派发状态 */
	public void dispatchStatus(boolean[] changeSet)
	{
		Unit unit;
		
		if((unit=_unit)!=null)
		{
			unit.fight.onStatusChange(changeSet);
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
	
	/** 推送CD */
	public void sendStartCDs(SList<CDData> cds)
	{
		Unit unit;
		
		if((unit=_unit)!=null)
		{
			unit.fight.sendStartCDs(cds);
		}
	}
	
	/** 推送移除组CD */
	public void sendRemoveGroupCD(int groupID)
	{
		Unit unit;
		
		if((unit=_unit)!=null)
		{
			unit.fight.sendRemoveGroupCD(groupID);
		}
	}
	
	/** 推送增加组CD上限百分比 */
	public void sendAddGroupTimeMaxPercent(int groupID,int value)
	{
		Unit unit;
		
		if((unit=_unit)!=null)
		{
			unit.fight.sendAddGroupTimeMaxPercent(groupID,value);
		}
	}
	
	/** 推送增加组CD上限值 */
	public void sendAddGroupTimeMaxValue(int groupID,int value)
	{
		Unit unit;
		
		if((unit=_unit)!=null)
		{
			unit.fight.sendAddGroupTimeMaxValue(groupID,value);
		}
	}
	
	/** 推送增加组CD时间经过 */
	public void sendAddGroupTimePass(int groupID,int value)
	{
		Unit unit;
		
		if((unit=_unit)!=null)
		{
			unit.fight.sendAddGroupTimePass(groupID,value);
		}
	}
	
	/** 推送添加buff */
	public void sendAddBuff(BuffData data,int type)
	{
		Unit unit;
		
		if((unit=_unit)!=null && type==BuffSendType.Radio)
		{
			unit.fight.sendAddBuff(data);
		}
	}
	
	/** 推送删除buff */
	public void sendRemoveBuff(int instanceID,int type)
	{
		Unit unit;
		
		if((unit=_unit)!=null && type==BuffSendType.Radio)
		{
			unit.fight.sendRemoveBuff(instanceID);
		}
	}
	
	/** 推送刷新buff */
	public void sendRefreshBuff(int instanceID,int lastTime,int lastNum,int type)
	{
		Unit unit;
		
		if((unit=_unit)!=null && type==BuffSendType.Radio)
		{
			unit.fight.sendRefreshBuff(instanceID,lastTime,lastNum);
		}
	}
	
	/** 推送刷新buff剩余次数 */
	public void sendRefreshBuffLastNum(int instanceID,int num,int type)
	{
		Unit unit;
		
		if((unit=_unit)!=null && type==BuffSendType.Radio)
		{
			unit.fight.sendRefreshBuffLastNum(instanceID,num);
		}
	}
	
	/** 造型改变 */
	public void onAvatarChange(int modelID,IntIntMap dic)
	{
		Unit unit;
		
		if((unit=_unit)!=null)
		{
			unit.avatar.onAvatarChange(modelID,dic);
		}
	}
	
	/** 造型部件改变 */
	public void onAvatarPartChange(IntIntMap dic)
	{
		Unit unit;
		
		if((unit=_unit)!=null)
		{
			unit.avatar.onAvatarPartChange(dic);
		}
	}
	
	/** 推送添加/刷新skill */
	public void sendAddSkill(SkillData data)
	{
	
	}
	
	/** 推送移除技能 */
	public void sendRemoveSkill(int id)
	{
	
	}
	
	/** 添加光环类buff */
	public void onAddRingLightBuff(BuffData data,int[] args)
	{
		Unit unit;
		
		if((unit=_unit)!=null)
		{
			unit.fight.onAddRingLightBuff(data,args);
		}
	}
	
	/** 移除光环类buff */
	public void onRemoveRingLightBuff(BuffData data,int[] args)
	{
		Unit unit;
		
		if((unit=_unit)!=null)
		{
			unit.fight.onRemoveRingLightBuff(data,args);
		}
	}
	
	//client部分

}
