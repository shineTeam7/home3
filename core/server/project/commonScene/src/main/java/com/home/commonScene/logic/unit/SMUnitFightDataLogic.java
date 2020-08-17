package com.home.commonScene.logic.unit;

import com.home.commonBase.config.game.CDConfig;
import com.home.commonBase.constlist.generate.BuffKeepType;
import com.home.commonBase.constlist.generate.SkillVarSourceType;
import com.home.commonBase.control.AttributeControl;
import com.home.commonBase.data.role.MUnitCacheData;
import com.home.commonBase.data.scene.base.BuffData;
import com.home.commonBase.data.scene.base.CDData;
import com.home.commonBase.data.scene.base.SkillData;
import com.home.commonBase.logic.unit.UnitFightDataLogic;
import com.home.commonBase.utils.BaseGameUtils;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.RefreshUnitAttributesRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.RefreshUnitStatusRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.UnitRefreshBuffLastNumRequest;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.IntBooleanMap;
import com.home.shine.support.collection.IntIntMap;
import com.home.shine.support.collection.IntObjectMap;

public class SMUnitFightDataLogic extends UnitFightDataLogic
{
	/** 使用逻辑体 */
	private SMUnitUseLogic _useLogic;
	/** 单位编号(默认主角0) */
	public int index=0;
	
	@Override
	public void construct()
	{
		super.construct();
		
		attribute.setIsM(true);
		status.setIsM(true);
	}
	
	/** 设置使用逻辑 */
	public void setUseLogic(SMUnitUseLogic logic)
	{
		_useLogic=logic;
	}
	
	public void clear()
	{
		Ctrl.throwError("不该调到这里");
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
					Ctrl.errorLog("不支持 SelfCurrency");
					return 0;
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
	
	@Override
	public void sendSelfAttribute(IntIntMap dic)
	{
		if(_unit==null)
			return;
		
		_useLogic.send(RefreshUnitAttributesRequest.create(_unit.instanceID,dic));
	}
	
	@Override
	public void sendSelfStatus(IntBooleanMap dic)
	{
		if(_unit==null)
			return;
		
		_useLogic.send(RefreshUnitStatusRequest.create(_unit.instanceID,dic));
	}
	
	/** 推送刷新buff剩余次数 */
	@Override
	public void sendRefreshBuffLastNum(int instanceID,int num,int type)
	{
		if(_unit!=null)
			_useLogic.send(UnitRefreshBuffLastNumRequest.create(_unit.instanceID,instanceID,num));
		
		super.sendRefreshBuffLastNum(instanceID,num,type);
	}
	
	///** 造型改变 */
	//public void onAvatarChange(int modelID,IntIntMap dic)
	//{
	//	super.onAvatarChange(modelID,dic);
	//}
	//
	///** 造型部件改变 */
	//public void onAvatarPartChange(IntIntMap dic)
	//{
	//	super.onAvatarPartChange(dic);
	//}
	
	@Override
	public void sendAddSkill(SkillData data)
	{
		//TODO:补充addSkill
		//_useLogic.send(MUnitAddSkillRequest.create(index,data));
		//super.sendAddSkill(data);
	}
	
	@Override
	public void sendRemoveSkill(int id)
	{
		//TODO:补充removeSkill
		
		//_useLogic.send(MUnitRemoveSkillRequest.create(index,id));
		//super.sendRemoveSkill(id);
	}
	
	//属性开关部分
	
	/** 设置普通推送开关(界面开关用) */
	public void setNormalSendOpen(boolean bb)
	{
		attribute.setNormalSendOpen(bb);
	}
	
	/** 获取当前时间 */
	public long getTimeMillis()
	{
		return _useLogic.getTimeMillis();
	}
	
	/** 保存到缓存数据(见BuffKeepType) */
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
		buffs.clear();
		
		buff.getBuffDatas().forEachValue(v->
		{
			if(v.config.keepType==keepType)
			{
				buffs.put(v.instanceID,(BuffData)v.clone());//clone
			}
		});
		
		//cd
		IntObjectMap<CDData> cds=data.cds;
		cds.clear();
		
		cd.getCDs().forEachValue(v->
		{
			if(CDConfig.get(v.id).keepType==keepType)
			{
				cds.put(v.id,(CDData)v.clone());//clone
			}
		});
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
