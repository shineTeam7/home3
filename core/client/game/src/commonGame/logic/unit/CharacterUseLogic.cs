using System;
using ShineEngine;

/// <summary>
/// 角色使用逻辑
/// </summary>
public class CharacterUseLogic:MUnitUseLogic
{
	//robot
	/** 指定角色显示数据 */
	private RoleShowData _signedRoleShowData;

	private MUnitCacheData _townCacheData;

	public CharacterUseLogic()
	{

	}

	public override bool isCharacter()
	{
		return true;
	}

	protected override MUnitIdentityData toCreateIdentityData()
	{
		CharacterIdentityData data=GameC.factory.createCharacterIdentityData();
		data.type=UnitType.Character;
		return data;
	}

	protected override void makeIdentityData(MUnitIdentityData data)
	{
		base.makeIdentityData(data);

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

	public override int getMUnitFuncID(int funcID)
	{
		return _data.id*10000+funcID;
	}

	//--下面是机器人部分--//

	/** 设置指定角色外显数据 */
	public void setSignedRoleShowData(RoleShowData data)
	{
		_signedRoleShowData=data;
	}

	/** 获取货币 */
	public override int getCurrency(int type)
	{
		if(_player!=null)
		{
			if(_player.role.playerID==GameC.player.role.playerID)
			{
				return (int)GameC.player.role.getCurrency(type);
			}
		}

		return 0;
	}

	/** 缓存主城类数据 */
	public void cacheForTown()
	{
		if(_townCacheData==null)
		{
			(_townCacheData=new MUnitCacheData()).initDefault();
		}

		_fightLogic.saveCache(_townCacheData,BuffKeepType.InTown);
	}

	/** 读取主城类缓存 */
	public void loadTownCache()
	{
		if(_townCacheData!=null)
		{
			_fightLogic.loadCache(_townCacheData);
			//直接清空
			_townCacheData=null;
		}
	}
}