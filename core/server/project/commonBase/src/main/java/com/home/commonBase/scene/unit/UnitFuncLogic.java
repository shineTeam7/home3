package com.home.commonBase.scene.unit;

import com.home.commonBase.data.scene.unit.UnitFuncData;
import com.home.commonBase.logic.unit.UnitItemDicContainerTool;
import com.home.commonBase.scene.base.UnitLogicBase;

/** 单位功能数据 */
public class UnitFuncLogic extends UnitLogicBase
{
	/** 数据 */
	protected UnitFuncData _d;
	
	/** 物品容器 */
	protected UnitItemDicContainerTool _itemTool;
	
	@Override
	public void init()
	{
		super.init();
		
		_d=_data.func;
		
		//有物品数据
		if(_d.itemDic!=null)
		{
			_itemTool=_scene.getExecutor().unitItemDicContainerToolPool.getOne();
			//设置新数据
			_itemTool.setData(_d.itemDic);
			_itemTool.afterReadData();
			_itemTool.afterReadDataSecond();
		}
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		
		//智控
		_d.itemDic=null;
		_d=null;
		
		if(_itemTool!=null)
		{
			_itemTool.setData(null);
			_itemTool.dispose();
			_scene.getExecutor().unitItemDicContainerToolPool.back(_itemTool);
			_itemTool=null;
		}
	}
	
	/** 绝对获取物品容器 */
	public UnitItemDicContainerTool getItemDicAbs()
	{
		if(_itemTool==null)
		{
			_itemTool=_scene.getExecutor().unitItemDicContainerToolPool.getOne();
			//设置新数据
			_itemTool.setData(null);
			_d.itemDic=_itemTool.getData();
		}
		
		return _itemTool;
	}
}
