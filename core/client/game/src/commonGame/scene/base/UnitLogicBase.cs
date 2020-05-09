using System;
using ShineEngine;

/// <summary>
/// 单位逻辑体基类
/// </summary>
public class UnitLogicBase:SceneObjectLogicBase
{
	protected UnitData _data;

	protected Unit _unit;

	public UnitLogicBase()
	{
	}

	public override void setObject(SceneObject obj)
	{
		base.setObject(obj);

		_unit=obj as Unit;
	}

	/** 初始化 */
	public override void init()
	{
		base.init();
		_data=_unit.getUnitData();
	}

	/** 析构 */
	public override void dispose()
	{
		base.dispose();

		_data=null;
	}

	/** 每秒十次 */
	public virtual void onPiece(int delay)
	{

	}
}