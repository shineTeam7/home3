package com.home.commonSceneBase.scene.scene;

import com.home.commonBase.constlist.scene.SceneAOIType;
import com.home.commonBase.data.scene.unit.UnitData;
import com.home.commonBase.scene.base.Region;
import com.home.commonBase.scene.base.Unit;
import com.home.commonBase.scene.scene.SceneAOILogic;
import com.home.commonSceneBase.net.sceneBaseRequest.scene.AddUnitRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.scene.RemoveUnitRequest;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.net.base.BaseRequest;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.collection.LongObjectMap;
import com.home.shine.support.collection.inter.IObjectConsumer;

/** 全部可见AOILogic */
public class SceneAOIAllLogic extends SceneAOILogic
{
	@Override
	public void construct()
	{
		super.construct();
		_aoiType=SceneAOIType.All;
	}
	
	@Override
	public void unitRefreshPos(Unit unit)
	{
		IntObjectMap<Region> dic=_scene.getRegions();
		
		if(!dic.isEmpty())
		{
			dic.forEachValue(v->
			{
				if(v.containsUnit(unit))
				{
					if(!v.isInRegion(unit))
					{
						v.doLeaveRegion(unit);
					}
				}
				else
				{
					if(v.isInRegion(unit))
					{
						v.doEnterRegion(unit);
					}
				}
			});
		}
	}
	
	@Override
	public void onAddUnit(Unit unit)
	{
		_scene.getRegions().forEachValue(v->
		{
			if(v.isInRegion(unit))
			{
				v.doEnterRegion(unit);
			}
		});
	}
	
	@Override
	public void getAroundUnitsBase(Unit unit,IObjectConsumer<Unit> func)
	{
		_scene.getUnitDic().forEachValueS(func);
	}
	
	@Override
	public boolean unitNeedRatio(Unit unit)
	{
		return !_scene.getCharacterDic().isEmpty();
	}
	
	@Override
	public void radioMessage(Unit unit,BaseRequest request,boolean needSelf)
	{
		if(ShineSetting.openCheck)
		{
			if(!unit.enabled && !(request instanceof RemoveUnitRequest))
			{
				Ctrl.warnLog("不该给移除单位推送消息",unit.getInfo());
			}
		}
		
		LongObjectMap<Unit> dic=_scene.getCharacterDic();
		
		if(!dic.isEmpty())
		{
			long selfID=needSelf ? -1L : unit.identity.controlPlayerID;
			
			toRadioMessage(selfID,request,dic.getValues());
		}
	}
	
	@Override
	protected BaseRequest createAddUnitRequest(UnitData data)
	{
		return AddUnitRequest.create(data);
	}
	
	@Override
	protected BaseRequest createUnitRemoveRequest(int instanceID)
	{
		return RemoveUnitRequest.create(instanceID);
	}
}
