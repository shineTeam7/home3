using System;
using ShineEngine;

/// <summary>
///
/// </summary>
public class PetUseLogic:MUnitUseLogic
{
	public override int getMUnitFuncID(int funcID)
	{
		return _data.id*(UnitType.Pet*10000)+funcID;
	}

	protected override MUnitIdentityData toCreateIdentityData()
	{
		PetIdentityData data=GameC.factory.createPetIdentityData();
		data.type=UnitType.Pet;
		return data;
	}
}