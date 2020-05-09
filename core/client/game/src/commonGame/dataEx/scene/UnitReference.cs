using System;
using ShineEngine;

/// <summary>
/// 单位引用
/// </summary>
public struct UnitReference:IPoolObject
{
	private Unit _unit;

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
	public void clear()
	{
		_unit=null;
		_version=0;
	}

	public bool isEmpty()
	{
		return getUnit()==null;
	}

	public static UnitReference create(Unit unit)
	{
		UnitReference re=new UnitReference();
		re.setUnit(unit);
		return re;
	}
}