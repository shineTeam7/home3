package com.home.commonGame.logic.unit;

import com.home.commonBase.constlist.generate.BuffKeepType;
import com.home.commonBase.constlist.generate.UnitType;
import com.home.commonBase.data.role.MUnitCacheData;
import com.home.commonBase.data.role.RoleShowData;
import com.home.commonBase.data.scene.unit.identity.CharacterIdentityData;
import com.home.commonBase.data.role.CharacterUseData;
import com.home.commonBase.data.scene.unit.identity.FightUnitIdentityData;
import com.home.commonBase.data.scene.unit.identity.MUnitIdentityData;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.part.player.data.ScenePartData;

/** 角色数据使用逻辑 */
public class CharacterUseLogic extends MUnitUseLogic
{
	//robot
	/** 指定角色显示数据 */
	private RoleShowData _signedRoleShowData;
	
	public CharacterUseLogic()
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
	
	/** 构建角色外显数据 */
	public RoleShowData createRoleShowData()
	{
		if(_player!=null)
		{
			return _player.role.createRoleShowData();
		}
		else
		{
			return _signedRoleShowData;
		}
	}
	
	@Override
	public int getMUnitFuncID(int funcID)
	{
		return _data.id*10000+funcID;
	}
	
	//--下面是机器人部分--//
	
	/** 设置指定角色外显数据 */
	public void setSignedRoleShowData(RoleShowData data)
	{
		_signedRoleShowData=data;
	}
	
	/** 缓存主城类数据 */
	public void cacheForTown()
	{
		ScenePartData partData=_player.scene.getPartData();
		
		if(partData.lastTownSaveData==null)
		{
			(partData.lastTownSaveData=new MUnitCacheData()).initDefault();
		}
		
		_fightLogic.saveCache(partData.lastTownSaveData,BuffKeepType.InTown);
	}
	
	/** 读取主城类缓存 */
	public void loadTownCache()
	{
		ScenePartData partData=_player.scene.getPartData();
		
		if(partData.lastTownSaveData!=null)
		{
			_fightLogic.loadCache(partData.lastTownSaveData);
			//保存到cache
			_saveData.cache=partData.lastTownSaveData;
			//直接清空
			partData.lastTownSaveData=null;
		}
	}
}
