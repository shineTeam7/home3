package com.home.commonBase.scene.scene;

import com.home.commonBase.data.scene.unit.UnitData;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.scene.base.Region;
import com.home.commonBase.scene.base.SceneLogicBase;
import com.home.commonBase.scene.base.Unit;
import com.home.commonBase.scene.unit.UnitFightLogic;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.net.base.BaseRequest;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.collection.LongObjectMap;
import com.home.shine.support.collection.inter.IObjectConsumer;

/** 场景AOI逻辑 */
public abstract class SceneAOILogic extends SceneLogicBase
{
	protected int _aoiType=0;
	
	@Override
	public void construct()
	{
		
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

	/** aoi单位添加 */
	public void unitAdd(Unit unit,boolean needSelf)
	{
		radioUnitAdd(unit,needSelf);
	}
	
	protected void radioUnitAdd(Unit unit,boolean needSelf)
	{
		//是否可战斗
		if(unit.canFight())
		{
			boolean wrote=false;
			
			UnitFightLogic fight=unit.fight;
			
			//是c单位
			if(needSelf && unit.hasSocket())// && unit.isCUnitNotM()
			{
				unit.beforeWrite();
				wrote=true;
				
				fight.switchSendSelf();
				unit.send(createAddUnitRequest(unit.getUnitData()));
				fight.endSwitchSend();
				
				if(CommonSetting.openAOICheck)
					unit.aoi.recordAddUnit(unit.instanceID);
			}
			
			if(unitNeedRatio(unit))
			{
				if(!wrote)
					unit.beforeWrite();
				
				fight.switchSendOther();
				radioMessage(unit,createAddUnitRequest(unit.getUnitData()),false);
				fight.endSwitchSend();
			}
		}
		else
		{
			if(unitNeedRatio(unit))
			{
				unit.beforeWrite();
				radioMessage(unit,createAddUnitRequest(unit.getUnitData()),needSelf);
			}
		}
	}

	/** aoi单位移除 */
	public void unitRemove(Unit unit,boolean needSelf)
	{
		radioUnitRemove(unit,needSelf);
	}
	
	protected void radioUnitRemove(Unit unit,boolean needSelf)
	{
		radioMessage(unit,createUnitRemoveRequest(unit.instanceID),needSelf);
	}
	
	/** 创建单位添加消息 */
	protected BaseRequest createUnitAddRequestForOther(Unit unit)
	{
		unit.beforeWrite();
		
		//是否可战斗
		if(unit.canFight())
		{
			UnitFightLogic fight=unit.fight;
			
			fight.switchSendOther();
			BaseRequest request=createAddUnitRequest(unit.getUnitData());
			fight.endSwitchSend();
			return request;
		}
		else
		{
			return createAddUnitRequest(unit.getUnitData());
		}
	}
	
	/** 创建添加单位消息 */
	protected abstract BaseRequest createAddUnitRequest(UnitData data);
	
	/** 创建删除单位消息 */
	protected abstract BaseRequest createUnitRemoveRequest(int instanceID);
	
	/** 获取周围单位(基础方式,无缓存) */
	public abstract void getAroundUnitsBase(Unit unit,IObjectConsumer<Unit> func);
	
	///** 获取周围单位(可缓存的) */
	//public abstract void getAroundUnits(Unit unit,IObjectConsumer<Unit> func);

	/** 单位是否需要广播(有接收者) */
	public abstract boolean unitNeedRatio(Unit unit);
	
	/** 广播消息 */
	public abstract void radioMessage(Unit unit,BaseRequest request,boolean needSelf);
	
	///** 广播消息 */
	//public abstract void radioMessageFunc(Unit unit,ObjectFunc<BaseRequest> func,boolean needSelf);
	
	/** 广播消息对所有人 */
	public void radioMessageAll(BaseRequest request)
	{
		LongObjectMap<Unit> dic=_scene.getCharacterDic();
		
		if(!dic.isEmpty())
		{
			toRadioMessage(-1L,request,dic.getValues());
		}
	}
	
	/** 获取aoi类型 */
	public int getAOIType()
	{
		return _aoiType;
	}
	
	/** 单位位置更新 */
	public void unitRefreshPos(Unit unit)
	{
	
	}
	
	@Override
	public void onRemoveUnit(Unit unit)
	{
		IntObjectMap<Region> dic=unit.pos.getInRegions();
		
		if(!dic.isEmpty())
		{
			dic.forEachValueS(v->
			{
				v.doLeaveRegion(unit);
			});
		}
	}
	
	protected void toRadioMessage(long selfID,BaseRequest request,Unit[] values)
	{
		toRadioMessage(selfID,request,values,values.length);
	}
	
	/** 实际广播消息(排除controlID为selfID的) */
	protected void toRadioMessage(long selfID,BaseRequest request,Unit[] values,int length)
	{
		_scene.method.doRadioMessage(selfID,request,values,length);
	}
}
