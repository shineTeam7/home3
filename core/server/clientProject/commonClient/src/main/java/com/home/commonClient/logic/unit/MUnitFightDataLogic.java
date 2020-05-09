package com.home.commonClient.logic.unit;

import com.home.commonBase.config.game.CDConfig;
import com.home.commonBase.constlist.generate.BuffKeepType;
import com.home.commonBase.constlist.generate.SkillVarSourceType;
import com.home.commonBase.control.AttributeControl;
import com.home.commonBase.data.role.MUnitCacheData;
import com.home.commonBase.data.scene.base.BuffData;
import com.home.commonBase.data.scene.base.CDData;
import com.home.commonBase.logic.unit.UnitFightDataLogic;
import com.home.commonBase.utils.BaseGameUtils;
import com.home.commonClient.part.player.Player;
import com.home.shine.support.collection.IntIntMap;
import com.home.shine.support.collection.IntObjectMap;

public class MUnitFightDataLogic extends UnitFightDataLogic
{
	/** 玩家 */
	private Player _me;
	
	/** 使用逻辑体 */
	private MUnitUseLogic _useLogic;

	/** 单位编号(默认主角0) */
	public int index=0;

	public MUnitFightDataLogic()
	{

	}
	
	/** 设置使用逻辑 */
	public void setUseLogic(MUnitUseLogic logic)
	{
		_useLogic=logic;
	}

	public void setPlayer(Player player)
	{
		_me=player;
	}
	
	@Override
	public int getSkillVarValueT(int varID,int adderID)
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
		
		return super.getSkillVarValueT(varID,adderID);
	}
	
	/** 获取变量源值 */
	@Override
	public int getSkillVarSourceValue(int[] args,boolean isSelf)
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
		
		return super.getSkillVarSourceValue(args,isSelf);
	}
	
	/** 获取当前时间 */
	public long getTimeMillis()
	{
		return _useLogic.getTimeMillis();
	}

	/** 缓存到当前数据(见BuffKeepType) */
	public void saveCache(MUnitCacheData data,int keepType)
	{
		data.cacheTime=getTimeMillis();
		
		//当前属性
		IntIntMap currentAttributes=data.currentAttributes;
		
		currentAttributes.clear();
		
		int[] currentList=AttributeControl.attribute.currentList;
		
		for(int i=currentList.length-1;i>=0;--i)
		{
			currentAttributes.put(currentList[i],attribute.getAttribute(currentList[i]));
		}
		
		//buff
		IntObjectMap<BuffData> buffs=data.buffs;
		
		BuffData[] values=buff.getBuffDatas().getValues();
		BuffData v;
		
		for(int i=values.length-1;i>=0;--i)
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
		
		CDData[] cdValues=cd.getCDs().getValues();
		CDData cdV;
		
		for(int i=cdValues.length-1;i>=0;--i)
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
			data.buffs.forEachValue(v->
			{
				v.reloadConfig();
				
				//有持续时间
				if(v.levelConfig.lastTime>0)
				{
					//超时了
					if((v.lastTime-=dTime)<=0)
					{
						return;
					}
				}
				
				buff.addBuffByData(v);
			});
		}
		
		if(!data.cds.isEmpty())
		{
			cd.reAddCDs(data.cds,dTime);
		}
		
		//最后当前属性
		if(!data.currentAttributes.isEmpty())
		{
			data.currentAttributes.forEach((k,v)->
			{
				attribute.setOneAttribute(k,v);
			});
		}
	}
	
	/** 清空数据到独立场景 */
	public void clearDataForIndependent()
	{
		attribute.makeCurrentToDefault();
		buff.removeBuffByKeep(BuffKeepType.InTown);
		cd.removeCDByKeep(BuffKeepType.InTown);
	}
	
	/** 清空当前场景数据 */
	public void clearDataForCurrentScene()
	{
		buff.removeBuffByKeep(BuffKeepType.InCurrentScene);
		cd.removeCDByKeep(BuffKeepType.InCurrentScene);
	}
}
