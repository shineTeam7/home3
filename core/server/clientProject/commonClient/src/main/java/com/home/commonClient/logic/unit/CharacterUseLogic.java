package com.home.commonClient.logic.unit;

import com.home.commonBase.constlist.generate.UnitType;
import com.home.commonBase.data.role.CharacterUseData;
import com.home.commonBase.data.role.RoleShowData;
import com.home.commonBase.data.scene.unit.identity.CharacterIdentityData;
import com.home.commonBase.data.scene.unit.identity.MUnitIdentityData;
import com.home.commonBase.global.BaseC;

/** 角色使用逻辑 */
public class CharacterUseLogic extends MUnitUseLogic
{
	//robot
	/** 指定角色显示数据 */
	private RoleShowData _signedRoleShowData;
	
	public CharacterUseLogic()
	{
		
	}
	
	@Override
	public int getMUnitFuncID(int funcID)
	{
		return _data.id*10000+funcID;
	}
	
	@Override
	protected MUnitIdentityData toCreateIdentityData()
	{
		CharacterIdentityData data=BaseC.factory.createCharacterIdentityData();
		data.type=UnitType.Character;
		return data;
	}
	
	@Override
	protected void makeIdentityData(MUnitIdentityData data)
	{
		super.makeIdentityData(data);
		
		CharacterIdentityData cData=(CharacterIdentityData)data;
		
		if(_player!=null)
		{
			cData.roleShowData=_player.role.createRoleShowData();
		}
		else
		{
			cData.roleShowData=_signedRoleShowData;
		}
	}
	
	public CharacterUseData getCharacterUseData()
	{
		return (CharacterUseData)_data;
	}
}
