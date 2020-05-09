package com.home.commonGame.scene.base;

import com.home.commonBase.constlist.generate.UnitType;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.scene.base.Unit;
import com.home.commonBase.scene.unit.UnitAICommandLogic;
import com.home.commonBase.scene.unit.UnitAILogic;
import com.home.commonBase.scene.unit.UnitAOILogic;
import com.home.commonBase.scene.unit.UnitAvatarLogic;
import com.home.commonBase.scene.unit.UnitFightLogic;
import com.home.commonBase.scene.unit.UnitIdentityLogic;
import com.home.commonBase.scene.unit.UnitMoveLogic;
import com.home.commonBase.scene.unit.UnitPosLogic;
import com.home.commonGame.net.request.system.SendInfoCodeRequest;
import com.home.commonGame.net.request.system.SendWarningLogRequest;
import com.home.commonGame.part.player.Player;
import com.home.commonGame.scene.unit.CharacterIdentityLogic;
import com.home.commonGame.scene.unit.GameBuildingIdentityLogic;
import com.home.commonGame.scene.unit.GameMonsterIdentityLogic;
import com.home.commonGame.scene.unit.GameOperationIdentityLogic;
import com.home.commonGame.scene.unit.GameUnitAICommandLogic;
import com.home.commonGame.scene.unit.GameUnitAOITowerLogic;
import com.home.commonGame.scene.unit.GameUnitAvatarLogic;
import com.home.commonGame.scene.unit.GameUnitFightLogic;
import com.home.commonGame.scene.unit.GameUnitMoveLogic;
import com.home.commonGame.scene.unit.GameUnitPosLogic;

public class GameUnit extends Unit
{
	/** 创建身份逻辑 */
	protected UnitIdentityLogic createIdentityLogic()
	{
		switch(_type)
		{
			case UnitType.Character:
			{
				return new CharacterIdentityLogic();
			}
			case UnitType.Monster:
			{
				return new GameMonsterIdentityLogic();
			}
			case UnitType.Operation:
			{
				return new GameOperationIdentityLogic();
			}
			case UnitType.Building:
			{
				return new GameBuildingIdentityLogic();
			}
		}
		
		return super.createIdentityLogic();
	}
	
	@Override
	protected UnitAOILogic createAOILogic()
	{
		//TODO:根据场景类型，注册AOI
		//未来如有需求可以在preInit和afterDispose里做
		//目前这版本先做成绑定aoiTower
		
		return new GameUnitAOITowerLogic();
	}
	
	@Override
	protected UnitMoveLogic createMoveLogic()
	{
		return new GameUnitMoveLogic();
	}
	
	@Override
	protected UnitPosLogic createPosLogic()
	{
		return new GameUnitPosLogic();
	}
	
	/** 创建战斗逻辑 */
	@Override
	protected UnitFightLogic createFightLogic()
	{
		return new GameUnitFightLogic();
	}
	
	@Override
	protected UnitAvatarLogic createAvatarLogic()
	{
		return new GameUnitAvatarLogic();
	}
	
	@Override
	protected UnitAICommandLogic createAICommandLogic()
	{
		return new GameUnitAICommandLogic();
	}
	
	/** 获取控制角色(归属角色) */
	public Player getControlPlayer()
	{
		return ((GameScene)_scene).gameInOut.getPlayer(identity.controlPlayerID);
	}
	
	@Override
	public void sendInfoCode(int code)
	{
		send(SendInfoCodeRequest.create(code));
	}
	
	@Override
	protected void sendWarnLog(String str)
	{
		if(CommonSetting.needSendWarningLog)
		{
			send(SendWarningLogRequest.create(str));
		}
	}
	
	/** 角色身份逻辑 */
	public CharacterIdentityLogic getCharacterIdentityLogic()
	{
		return (CharacterIdentityLogic)identity;
	}
	
	/** 获取所属角色(不是Character也可取到) */
	public Player getPlayer()
	{
		if(identity.isCharacter())
		{
			return getCharacterIdentityLogic().getPlayer();
		}
		
		return ((GameScene)_scene).gameInOut.getPlayer(identity.playerID);
	}
}
