using System;
using ShineEngine;

/// <summary>
/// 角色身份逻辑
/// </summary>
public class CharacterIdentityLogic:UnitIdentityLogic
{
	private CharacterIdentityData _data;

	public CharacterIdentityLogic()
	{
		
	}

	public override void init()
	{
		base.init();
		_data=(CharacterIdentityData)_unit.getUnitData().identity;
		_unitName=_data.roleShowData.name;
	}

	public override void dispose()
	{
		base.dispose();

		_data=null;
	}
}