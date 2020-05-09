using System;
using ShineEngine;

/// <summary>
/// 怪物单位身份逻辑
/// </summary>
public class MonsterIdentityLogic:UnitIdentityLogic
{
	private MonsterConfig _config;

	public override void init()
	{
		base.init();
		_config=MonsterConfig.get(((MonsterIdentityData)_data.identity).id);
		_unitName=_config.name;
	}

	public override void onReloadConfig()
	{
		_config=MonsterConfig.get(((MonsterIdentityData)_data.identity).id);
		_unitName=_config.name;
	}

	/** 获取怪物配置 */
	public MonsterConfig getMonsterConfig()
	{
		return _config;
	}
}