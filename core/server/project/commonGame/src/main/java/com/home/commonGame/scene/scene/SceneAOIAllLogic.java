package com.home.commonGame.scene.scene;

import com.home.commonBase.constlist.scene.SceneAOIType;
import com.home.commonBase.scene.base.Region;
import com.home.commonBase.scene.base.Unit;
import com.home.commonGame.net.request.scene.scene.RemoveUnitRequest;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.net.base.BaseRequest;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.collection.LongObjectMap;
import com.home.shine.support.collection.inter.IObjectConsumer;

/** 全部可见AOILogic */
public class SceneAOIAllLogic extends GameSceneAOILogic
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
	
	
	//@Override
	//public void getAroundUnits(SList<Unit> list,Unit unit)
	//{
	//	Unit[] values=_scene.getUnitDic().getValues();
	//
	//	Unit v;
	//
	//	for(int i=values.length - 1;i >= 0;--i)
	//	{
	//		if((v=values[i])!=null)
	//		{
	//			//不是自己
	//			if(v.instanceID!=unit.instanceID)
	//			{
	//				list.add(v);
	//			}
	//		}
	//	}
	//}
	
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
	
	
}
