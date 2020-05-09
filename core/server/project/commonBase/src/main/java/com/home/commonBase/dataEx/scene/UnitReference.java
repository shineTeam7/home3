package com.home.commonBase.dataEx.scene;

import com.home.commonBase.scene.base.Unit;
import com.home.shine.support.pool.IPoolObject;

/** 单位引用 */
public class UnitReference implements IPoolObject
{
	/** 单位 */
	private Unit _unit;
	/** 版本号 */
	private int _version;
	
	public void setUnit(Unit unit)
	{
		_unit=unit;
		_version=unit.version;
	}
	
	public Unit getUnit()
	{
		if(_unit==null)
		{
			return null;
		}
		
		if(_unit.version!=_version)
		{
			_unit=null;
			return null;
		}
		
		return _unit;
	}
	
	/** 清空 */
	@Override
	public void clear()
	{
		_unit=null;
		_version=0;
	}
	
	public boolean isEmpty()
	{
		return getUnit()==null;
	}
}
