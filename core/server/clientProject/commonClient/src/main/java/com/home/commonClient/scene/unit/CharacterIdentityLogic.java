package com.home.commonClient.scene.unit;

import com.home.commonBase.data.scene.unit.identity.CharacterIdentityData;
import com.home.commonBase.scene.unit.UnitIdentityLogic;

public class CharacterIdentityLogic extends UnitIdentityLogic
{
	private CharacterIdentityData _data;
	
	@Override
	public void init()
	{
		super.init();
		
		_data=(CharacterIdentityData)_unit.getUnitData().identity;
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		
		_data=null;
	}
	
	/** 获取身份数据 */
	public CharacterIdentityData getData()
	{
		return _data;
	}
}
