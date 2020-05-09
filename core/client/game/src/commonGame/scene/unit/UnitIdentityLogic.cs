using ShineEngine;
using System;

/// <summary>
/// 单位身份逻辑
/// </summary>
public class UnitIdentityLogic:UnitLogicBase
{
	/** 归属角色ID */
	public long playerID=-1;
	/** 控制角色ID */
	public long controlPlayerID=-1;
	/** 身份数据 */
	private UnitIdentityData _iData;
	/** 是否是C单位(客户端控制)但是不是M单位(主单位) */
	private bool _isCUnitNotM=false;

	/** 是否构造过主控单位逻辑 */
	private bool _makedControlLogic=false;
	/** 主控单位身份逻辑 */
	private UnitIdentityLogic _controlLogic;

	/** 单位名字 */
	protected string _unitName="";

	public UnitIdentityLogic()
	{
		
	}

	public override void init()
	{
		base.init();

		UnitIdentityData identity=_iData=_data.identity;

		playerID=identity.playerID;


		if(BaseC.constlist.unit_canFight(identity.type))
		{
			FightUnitIdentityData fData=(FightUnitIdentityData)identity;

			//有控制单位
			if((controlPlayerID=fData.controlPlayerID)!=-1)
			{
				//控主分离
				if(fData.playerID!=fData.controlPlayerID)
				{
					_isCUnitNotM=true;
				}
				else
				{
					_isCUnitNotM=!BaseC.constlist.unit_isMUnit(_unit.type);
				}
			}
		}
	}

	public override void dispose()
	{
		base.dispose();

		controlPlayerID=-1;
		playerID=-1;
		_iData=null;
		_unitName="";
	}

	/** 获取角色身份数据 */
	public CharacterIdentityData GetCharacterIdentityData()
	{
		return (CharacterIdentityData)_data.identity;
	}

	/** 获取单位名字 */
	public string getUnitName()
	{
		return _unitName;
	}

	/** 是否为角色 */
	public bool isCharacter()
	{
		return _iData.type==UnitType.Character;
	}

	/** 是否是怪物 */
	public bool isMonster()
	{
		return _iData.type==UnitType.Monster;
	}

	/** 是否是宠物 */
	public bool isPet()
	{
		return _iData.type==UnitType.Pet;
	}
}