package com.home.commonClient.scene.scene;

import com.home.commonBase.scene.base.Unit;
import com.home.commonBase.scene.scene.SceneAOILogic;
import com.home.shine.net.base.BaseRequest;
import com.home.shine.support.collection.inter.IObjectConsumer;

/** 客户端AOI逻辑 */
public class SceneClientAOILogic extends SceneAOILogic
{
	@Override
	public void unitAdd(Unit unit,boolean needSelf)
	{
	
	}
	
	@Override
	public void unitRemove(Unit unit,boolean needSelf)
	{
	
	}
	
	@Override
	public void getAroundUnitsBase(Unit unit,IObjectConsumer<Unit> func)
	{
		_scene.getUnitDic().forEachValueS(func);
	}
	
	@Override
	public boolean unitNeedRatio(Unit unit)
	{
		return false;
	}
	
	@Override
	public void radioMessage(Unit unit,BaseRequest request,boolean needSelf)
	{
	
	}
	
	@Override
	public void radioMessageAll(BaseRequest request)
	{
	
	}
}
