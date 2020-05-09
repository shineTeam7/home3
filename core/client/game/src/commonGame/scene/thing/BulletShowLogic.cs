using System;
using ShineEngine;
using UnityEngine;

/// <summary>
/// 子弹显示逻辑
/// </summary>
public class BulletShowLogic:BulletLogicBase
{
	protected UnitEffect _effect;

	public BulletShowLogic()
	{

	}

	public override void init()
	{
		base.init();

		if(_config.effectID>0)
		{
			//绑自己的
			if(BaseC.constlist.bulletCast_isSelfHit(_levelConfig.castType))
			{
				_unit.show.addEffect(_config.effectID);
			}
			else
			{
				EffectConfig config=EffectConfig.get(_config.effectID);

				_effect=GameC.pool.unitEffectPool.getOne();
				_effect.setConfig(config);
				_effect.setScene(_scene);
				_effect.bindBullet(_bullet);

				_effect.init();
			}
		}
	}

	public override void dispose()
	{
		if(_config.effectID>0)
		{
			//绑自己的
			if(BaseC.constlist.bulletCast_isSelfHit(_levelConfig.castType))
			{
				_unit.show.removeEffect(_config.effectID);
			}
			else
			{
				if(_effect!=null)
				{
					_effect.dispose();
					GameC.pool.unitEffectPool.back(_effect);
					_effect=null;
				}
			}
		}

		base.dispose();

	}

	public virtual void effectLoadOver()
	{

	}

	public virtual void effectPlayOver()
	{

	}

	public void setPos(PosData pos)
	{
		if(_effect!=null)
		{
			_effect.onSetPos(pos);
		}
	}
}