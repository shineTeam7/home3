using System;
using ShineEngine;

/// <summary>
/// 傀儡身份逻辑
/// </summary>
public class PuppetIdentityLogic:UnitIdentityLogic
{
	protected PuppetIdentityData _iData;

	protected PuppetConfig _config;

	public override void init()
	{
		base.init();

		_iData=(PuppetIdentityData)_data.identity;
		_config=PuppetConfig.get(_iData.id);
	}

	public override void afterInit()
	{
		base.afterInit();

		if(_config.isClientDrive || _scene.isDriveAll())
		{
			initAI();
		}
	}

	public PuppetConfig config
	{
		get
		{
			return _config;
		}
	}

	public override void dispose()
	{
		base.dispose();

		_iData=null;
		_config=null;
	}

	public override void onFrame(int delay)
	{
		base.onFrame(delay);

		if(_iData.lastTime>0)
		{
			if((_iData.lastTime-=delay)<=0)
			{
				_iData.lastTime=0;

				timeUp();
			}
		}
	}

	/** 获取主 */
	public Unit getMaster()
	{
		return _scene.getFightUnit(_iData.masterInstanceID);
	}

	/** 获取攻击者 */
	public Unit getAttacker()
	{
		if(_config.isIndependentAttacker)
			return _unit;

		return getMaster();
	}

	protected virtual void initAI()
	{
		switch(_config.aiType)
		{
			case PuppetAIType.MoveStraight:
			{

			}
				break;
		}
	}

	/** 时间到 */
	private void timeUp()
	{
		if(!_scene.isDriveAll())
			return;

		PuppetLevelConfig levelConfig=PuppetLevelConfig.get(_iData.id,_iData.level);

		foreach(int[] v in levelConfig.timeUpActions)
		{
			doOneTimeUpAction(v);
		}

		_unit.removeLater();
	}

	protected void doOneTimeUpAction(int[] args)
	{
		switch(args[0])
		{
			case PuppetTimeUpActionType.UseSkill:
			{
				_unit.fight.useSkill(args[1],args[2]);
			}
				break;
		}
	}
}