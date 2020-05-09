using System;
using ShineEngine;

/// <summary>
/// 子弹实体
/// </summary>
public class Bullet:SceneObject
{
	/** 单位 */
	private Unit _unit;
	/** 数据 */
	private BulletData _data;

	/** 位置逻辑 */
	public BulletPosLogic pos;

	/** 显示逻辑 */
	public BulletShowLogic show;

	public Bullet()
	{

	}

	protected override void registLogics()
	{
		pos=createPosLogic();
		addLogic(pos);

		show=createShowLogic();
		addLogic(show);
	}

	protected virtual BulletPosLogic createPosLogic()
	{
		return new BulletPosLogic();
	}

	protected virtual BulletShowLogic createShowLogic()
	{
		return new BulletShowLogic();
	}

	public void setData(BulletData data)
	{
		_data=data;
	}

	public BulletData getData()
	{
		return _data;
	}

	/** 设置单位 */
	public void setUnit(Unit unit)
	{
		_unit=unit;
	}

	/** 获取单位 */
	public Unit getUnit()
	{
		return _unit;
	}

	public override void removeAbs()
	{
		_unit.fight.removeBullet(_data.instanceID);
	}
}