package com.home.commonBase.scene.base;

import com.home.commonBase.data.scene.base.BulletData;
import com.home.commonBase.scene.thing.BulletPosLogic;

/** 子弹 */
public class Bullet extends SceneObject
{
	/** 单位 */
	private Unit _unit;
	/** 数据 */
	private BulletData _data;
	/** 位置逻辑 */
	public BulletPosLogic pos;
	
	@Override
	protected void registLogics()
	{
		pos=createPosLogic();
		addLogic(pos);
	}
	
	protected BulletPosLogic createPosLogic()
	{
		return new BulletPosLogic();
	}
	
	/** 设置子弹数据 */
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
	
	@Override
	public void removeAbs()
	{
		_unit.fight.removeBullet(_data.instanceID,false,false);
	}
}
