using System;
using ShineEngine;

/// <summary>
/// 主角单位战斗数据逻辑
/// </summary>
public class MUnitFightDataLogic:UnitFightDataLogic
{
	private Player _me;

	/** 使用逻辑体 */
	private MUnitUseLogic _useLogic;

	/** 单位编号(默认主角0) */
	public int index=0;

	//消息相关

	private MUnitAttributesChangeEventObj _attributeEventObj=new MUnitAttributesChangeEventObj();
	private MUnitBuffChangeEventObj _buffEventObj=new MUnitBuffChangeEventObj();

	public MUnitFightDataLogic()
	{

	}

	public override void construct()
	{
		base.construct();

		attribute.setIsM(true);
		status.setIsM(true);

		isSelfControl=true;

		_me=GameC.player;
	}

	/** 设置使用逻辑 */
	public void setUseLogic(MUnitUseLogic logic)
	{
		_useLogic=logic;
	}

	public override int getSkillVarValueT(int varID,int adderID)
	{
		if(_unit==null)
		{
			UnitFightDataLogic self=null;

			if(adderID==-1)
			{
				self=this;
			}

			return BaseGameUtils.calculateSkillVarValueFull(varID,self,this);
		}

		return base.getSkillVarValueT(varID,adderID);
	}

	/** 获取变量源值 */
	public override int getSkillVarSourceValue(int[] args,bool isSelf)
	{
		if(isSelf)
		{
			switch(args[0])
			{
				case SkillVarSourceType.SelfLevel:
				{
					return _useLogic.getLevel();
				}
				case SkillVarSourceType.SelfCurrency:
				{
					return _useLogic.getCurrency(args[1]);
				}
			}
		}
		else
		{
			switch(args[0])
			{
				case SkillVarSourceType.TargetLevel:
				{
					return _useLogic.getLevel();
				}
			}
		}

		return base.getSkillVarSourceValue(args,isSelf);
	}

	public override void onStatusChange(bool[] changeSet)
	{
		base.onStatusChange(changeSet);

		_attributeEventObj.index=index;
		_attributeEventObj.changeSet=changeSet;

		_me.dispatch(GameEventType.MUnitStatusChange,_attributeEventObj);
	}

	public override void onAttributeChange(int[] changeList,int num,bool[] changeSet,int[] lastAttributes)
	{
		base.onAttributeChange(changeList,num,changeSet,lastAttributes);

		_attributeEventObj.index=index;
		_attributeEventObj.changeSet=changeSet;

		_me.dispatch(GameEventType.MUnitAttributeChange,_attributeEventObj);
	}

	public override void onCDChange()
	{
		base.onCDChange();

		_me.dispatch(GameEventType.MUnitCDChange,index);
	}

	public override void onAddBuff(BuffData data)
	{
		base.onAddBuff(data);

		_buffEventObj.index=index;
		_buffEventObj.instanceID=data.instanceID;
		_me.dispatch(GameEventType.MUnitBuffAdd,_buffEventObj);
	}

	public override void onRemoveBuff(BuffData data)
	{
		base.onRemoveBuff(data);

		_buffEventObj.index=index;
		_buffEventObj.instanceID=data.instanceID;
		_me.dispatch(GameEventType.MUnitBuffRemove,_buffEventObj);
	}

	public override void onRefreshBuff(BuffData data)
	{
		base.onRefreshBuff(data);

		_buffEventObj.index=index;
		_buffEventObj.instanceID=data.instanceID;
		_me.dispatch(GameEventType.MUnitBuffRefresh,_buffEventObj);
	}
	
	/** 造型改变 */
	public override void onAvatarChange(int modelID)
	{
		base.onAvatarChange(modelID);
		
		_me.dispatch(GameEventType.MUnitAvatarModelChange ,index);
	}
	
	/** 造型部件改变 */
	public override void onAvatarPartChange(IntIntMap dic)
	{
		base.onAvatarPartChange(dic);
		
		_me.dispatch(GameEventType.MUnitAvatarModelChange,index);
	}

	public override void onAddSkill(SkillData data)
	{
		base.onAddSkill(data);

		_me.dispatch(GameEventType.MUnitSkillChange,index);
	}

	public override void onRemoveSkill(SkillData data)
	{
		base.onRemoveSkill(data);

		_me.dispatch(GameEventType.MUnitSkillChange,index);
	}

	public long getTimeMillis()
	{
		return DateControl.getTimeMillis();
	}

	/** 缓存到当前数据(见BuffKeepType) */
	public void saveCache(MUnitCacheData data,int keepType)
	{
		data.cacheTime=getTimeMillis();

		//当前属性
		IntIntMap currentAttributes=data.currentAttributes;

		currentAttributes.clear();

		int[] currentList=AttributeControl.attribute.currentList;

		for(int i=currentList.Length-1;i>=0;--i)
		{
			currentAttributes.put(currentList[i],attribute.getAttribute(currentList[i]));
		}

		//buff
		IntObjectMap<BuffData> buffs=data.buffs;
		buffs.clear();

		BuffData[] values=buff.getBuffDatas().getValues();
		BuffData v;

		for(int i=values.Length-1;i>=0;--i)
		{
			if((v=values[i])!=null)
			{
				if(v.config.keepType==keepType)
				{
					buffs.put(v.instanceID,(BuffData)v.clone());//clone
				}
			}
		}

		//cd
		IntObjectMap<CDData> cds=data.cds;
		cds.clear();

		CDData[] cdValues=cd.getCDs().getValues();
		CDData cdV;

		for(int i=cdValues.Length-1;i>=0;--i)
		{
			if((cdV=cdValues[i])!=null)
			{
				if(CDConfig.get(cdV.id).keepType==keepType)
				{
					cds.put(cdV.id,(CDData)cdV.clone());//clone
				}
			}
		}
	}

	/** 从缓存上读取属性 */
	public void loadCache(MUnitCacheData data)
	{
		if(data==null)
			return;

		int dTime=(int)(getTimeMillis()-data.cacheTime);

		if(!data.buffs.isEmpty())
		{
			foreach(BuffData v in data.buffs)
			{
				v.reloadConfig();

				//有持续时间
				if(v.levelConfig.lastTime>0)
				{
					//超时了
					if((v.lastTime-=dTime)<=0)
					{
						continue;
					}
				}

				buff.addBuffByData(v);
			}
		}

		if(!data.cds.isEmpty())
		{
			cd.reAddCDs(data.cds,dTime);
		}

		//最后当前属性
		if(!data.currentAttributes.isEmpty())
		{
			data.currentAttributes.forEach((k,v)=>
			{
				attribute.setOneAttribute(k,v);
			});
		}
	}

	/** 清空数据到独立场景(客户端主驱用) */
	public void clearDataForIndependent()
	{
		attribute.makeCurrentToDefault();
		buff.removeBuffByKeep(BuffKeepType.InTown);
		cd.removeCDByKeep(BuffKeepType.InTown);
	}

	/** 清空当前场景数据(客户端主驱用) */
	public void clearDataForCurrentScene()
	{
		buff.removeBuffByKeep(BuffKeepType.InCurrentScene);
		cd.removeCDByKeep(BuffKeepType.InCurrentScene);
	}
}