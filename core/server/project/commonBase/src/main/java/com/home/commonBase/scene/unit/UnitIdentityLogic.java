package com.home.commonBase.scene.unit;

import com.home.commonBase.constlist.generate.UnitType;
import com.home.commonBase.data.scene.unit.UnitIdentityData;
import com.home.commonBase.data.scene.unit.identity.CharacterIdentityData;
import com.home.commonBase.data.scene.unit.identity.FightUnitIdentityData;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.scene.base.Role;
import com.home.commonBase.scene.base.Unit;
import com.home.commonBase.scene.base.UnitLogicBase;
import com.home.shine.net.socket.BaseSocket;
import com.home.shine.support.pool.StringBuilderPool;

/** 单位身份逻辑 */
public class UnitIdentityLogic extends UnitLogicBase
{
	/** 归属角色ID */
	public long playerID=-1;
	/** 控制角色ID */
	public long controlPlayerID=-1;
	/** 消息是否就绪(避免EnterScene过程的额外消息推送) */
	public boolean socketReady=true;
	
	/** 身份数据 */
	private UnitIdentityData _iData;
	/** 是否是C单位(客户端控制)但是不是M单位(主单位) */
	private boolean _isCUnitNotM=false;
	
	/** 主控单位身份逻辑 */
	private UnitIdentityLogic _controlLogic;
	
	/** 单位名字 */
	protected String _unitName="";
	
	@Override
	public void init()
	{
		super.init();
		
		UnitIdentityData identity=_iData=_data.identity;
		
		playerID=identity.playerID;
		
		if(BaseC.constlist.unit_canFight(identity.type))
		{
			FightUnitIdentityData fData=(FightUnitIdentityData)identity;
			
			//有控制单位
			if((controlPlayerID=fData.controlPlayerID)!=-1)
			{
				//控主分离
				if(fData.playerID!=controlPlayerID)
				{
					_isCUnitNotM=true;
				}
				else
				{
					_isCUnitNotM=!BaseC.constlist.unit_isMUnit(_unit.getType());
				}
			}
		}
		
	}
	
	@Override
	public void dispose()
	{
		unbindControlLogic();
		
		super.dispose();
		
		controlPlayerID=-1;
		playerID=-1;
		_iData=null;
		_isCUnitNotM=false;
		socketReady=true;
	}
	
	/** 获取角色身份数据 */
	public CharacterIdentityData getCharacterIdentityData()
	{
		return (CharacterIdentityData)_data.identity;
	}
	
	/** 获取控制单位身份逻辑 */
	public UnitIdentityLogic getControlCharacterLogic()
	{
		//有人控制
		if(_controlLogic==null && controlPlayerID>0)
		{
			Unit unit;
			
			if((unit=_scene.getCharacterByPlayerID(controlPlayerID))!=null)
			{
				_controlLogic=unit.identity;
				//添加控制单位
				_controlLogic.addControlUnit(_data.instanceID);
			}
		}
		
		return _controlLogic;
	}
	
	/** 清空控制逻辑 */
	public void unbindControlLogic()
	{
		if(_controlLogic!=null)
		{
			_controlLogic.removeControlUnit(_data.instanceID);
			_controlLogic=null;
			
			//socket置dirty
			_unit.makeSocketDirty();
		}
	}
	
	/** 实际获取socket */
	protected BaseSocket toGetSocket()
	{
		return null;
	}
	
	/** 获取socket */
	public BaseSocket getSocket()
	{
		//有控制角色
		if(controlPlayerID!=-1)
		{
			UnitIdentityLogic cLogic;
			
			if((cLogic=getControlCharacterLogic())!=null)
			{
				return cLogic.toGetSocket();
			}
		}
		
		return null;
	}
	
	/** 获取描述信息 */
	public String getInfo()
	{
		StringBuilder sb=StringBuilderPool.create();
		writeInfo(sb);
		return StringBuilderPool.releaseStr(sb);
	}
	
	/** 写描述信息 */
	public void writeInfo(StringBuilder sb)
	{
		
	}
	
	/** 是否是C单位(客户端控制)但是不是M单位(主单位) */
	public boolean isCUnitNotM()
	{
		return _isCUnitNotM;
	}
	
	/** 是否为角色 */
	public boolean isCharacter()
	{
		return _iData.type==UnitType.Character;
	}
	
	/** 是否是怪物 */
	public boolean isMonster()
	{
		return _iData.type==UnitType.Monster;
	}
	
	/** 是否是宠物 */
	public boolean isPet()
	{
		return _iData.type==UnitType.Pet;
	}
	
	/** 添加被控制单位 */
	public void addControlUnit(int instanceID)
	{
	
	}
	
	/** 移除被控制单位 */
	public void removeControlUnit(int instanceID)
	{
	
	}
	
	public Role getRole()
	{
		return _scene.role.getRole(playerID);
	}
}
