package com.home.commonBase.scene.scene;

import com.home.commonBase.scene.base.Region;
import com.home.commonBase.scene.base.SceneLogicBase;
import com.home.commonBase.scene.base.Unit;
import com.home.shine.net.base.BaseRequest;
import com.home.shine.support.collection.IntObjectMap;
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
	public abstract void unitAdd(Unit unit,boolean needSelf);

	/** aoi单位移除 */
	public abstract void unitRemove(Unit unit,boolean needSelf);
	
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
	public abstract void radioMessageAll(BaseRequest request);
	
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
}
