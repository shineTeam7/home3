package com.home.commonBase.scene.base;

import com.home.commonBase.data.scene.unit.UnitData;

/** 单位逻辑体基类 */
public class UnitLogicBase extends SceneObjectLogicBase
{
	protected UnitData _data;
	
	protected Unit _unit;
	
	@Override
	public void setObject(SceneObject obj)
	{
		super.setObject(obj);
		
		_unit=(Unit)obj;
	}

	@Override
	public void construct()
	{
		
	}

	@Override
	public void init()
	{
		super.init();
		_data=_unit.getUnitData();
	}

	@Override
	public void dispose()
	{
		super.dispose();
		_data=null;
	}
	
	/** 每份时间 */
	public void onPiece(int delay)
	{
		
	}
	
	
}
