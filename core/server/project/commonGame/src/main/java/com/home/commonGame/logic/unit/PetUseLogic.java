package com.home.commonGame.logic.unit;

import com.home.commonBase.constlist.generate.UnitType;
import com.home.commonBase.data.scene.unit.identity.MUnitIdentityData;
import com.home.commonBase.data.scene.unit.identity.PetIdentityData;
import com.home.commonBase.global.BaseC;

public class PetUseLogic extends MUnitUseLogic
{
	@Override
	protected MUnitIdentityData toCreateIdentityData()
	{
		PetIdentityData data=BaseC.factory.createPetIdentityData();
		data.type=UnitType.Pet;
		return data;
	}
	
	@Override
	public int getMUnitFuncID(int funcID)
	{
		return _data.id*(UnitType.Pet*10000)+funcID;
	}
}
