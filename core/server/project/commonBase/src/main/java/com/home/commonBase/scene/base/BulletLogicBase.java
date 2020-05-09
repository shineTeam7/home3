package com.home.commonBase.scene.base;

import com.home.commonBase.config.game.BulletConfig;
import com.home.commonBase.config.game.BulletLevelConfig;
import com.home.commonBase.data.scene.base.BulletData;

public class BulletLogicBase extends SceneObjectLogicBase
{
	protected Bullet _bullet;
	
	protected Unit _unit;
	
	protected BulletData _data;
	
	protected BulletConfig _config;
	
	protected BulletLevelConfig _levelConfig;
	
	@Override
	public void setObject(SceneObject obj)
	{
		super.setObject(obj);
		
		_bullet=(Bullet)obj;
	}
	
	@Override
	public void init()
	{
		super.init();
		
		_unit=_bullet.getUnit();
		
		BulletData data=_data=_bullet.getData();
		
		_config=BulletConfig.get(data.id);
		_levelConfig=BulletLevelConfig.get(data.id,data.level);
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		
		_unit=null;
		_data=null;
		_config=null;
		_levelConfig=null;
	}
}
