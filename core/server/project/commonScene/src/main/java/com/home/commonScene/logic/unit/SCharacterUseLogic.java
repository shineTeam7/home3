package com.home.commonScene.logic.unit;

import com.home.commonBase.constlist.generate.BuffKeepType;
import com.home.commonBase.constlist.generate.UnitType;
import com.home.commonBase.data.role.CharacterUseData;
import com.home.commonBase.data.role.MUnitCacheData;
import com.home.commonBase.data.role.RoleShowData;
import com.home.commonBase.data.scene.unit.identity.CharacterIdentityData;
import com.home.commonBase.data.scene.unit.identity.MUnitIdentityData;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.part.player.data.ScenePartData;

public class SCharacterUseLogic extends SMUnitUseLogic
{
	//robot
	/** 指定角色显示数据 */
	private RoleShowData _signedRoleShowData;
	
	public SCharacterUseLogic()
	{
	
	}
	
	@Override
	public boolean isCharacter()
	{
		return true;
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
			cData.roleShowData=_player.createRoleShowData();
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
	
	/** 构建角色外显数据 */
	public RoleShowData createRoleShowData()
	{
		if(_player!=null)
		{
			return _player.createRoleShowData();
		}
		else
		{
			return _signedRoleShowData;
		}
	}
	
	//--下面是机器人部分--//
	
	/** 设置指定角色外显数据 */
	public void setSignedRoleShowData(RoleShowData data)
	{
		_signedRoleShowData=data;
	}
}
