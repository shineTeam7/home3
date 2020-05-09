using System;
using ShineEngine;

/// <summary>
/// 
/// </summary>
public class BulletLogicBase:SceneObjectLogicBase
{
	protected Bullet _bullet;

	protected Unit _unit;

	protected BulletData _data;

	protected BulletConfig _config;

	protected BulletLevelConfig _levelConfig;

	public override void setObject(SceneObject obj)
	{
		base.setObject(obj);

		_bullet=(Bullet)obj;
	}

	public override void init()
	{
		base.init();

		_data=_bullet.getData();
		_unit=_bullet.getUnit();
		_config=BulletConfig.get(_data.id);
		_levelConfig=BulletLevelConfig.get(_data.id,_data.level);
	}

	public override void dispose()
	{
		base.dispose();

		_data=null;
		_unit=null;
		_config=null;
		_levelConfig=null;
	}
}