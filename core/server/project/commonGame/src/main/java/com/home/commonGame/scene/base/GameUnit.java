package com.home.commonGame.scene.base;

import com.home.commonBase.constlist.generate.UnitType;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.scene.unit.UnitAICommandLogic;
import com.home.commonBase.scene.unit.UnitFightLogic;
import com.home.commonBase.scene.unit.UnitIdentityLogic;
import com.home.commonBase.scene.unit.UnitPosLogic;
import com.home.commonGame.net.request.system.SendInfoCodeRequest;
import com.home.commonGame.net.request.system.SendWarningLogRequest;
import com.home.commonGame.part.player.Player;
import com.home.commonGame.scene.unit.GameCharacterIdentityLogic;
import com.home.commonGame.scene.unit.GameBuildingIdentityLogic;
import com.home.commonGame.scene.unit.GameMonsterIdentityLogic;
import com.home.commonGame.scene.unit.GameUnitAICommandLogic;
import com.home.commonGame.scene.unit.GameUnitFightLogic;
import com.home.commonGame.scene.unit.GameUnitPosLogic;
import com.home.commonSceneBase.scene.base.BUnit;

public class GameUnit extends BUnit
{
	/** 创建身份逻辑 */
	@Override
	protected UnitIdentityLogic createIdentityLogic()
	{
		switch(_type)
		{
			case UnitType.Character:
			{
				return new GameCharacterIdentityLogic();
			}
			case UnitType.Monster:
			{
				return new GameMonsterIdentityLogic();
			}
			case UnitType.Building:
			{
				return new GameBuildingIdentityLogic();
			}
		}
		
		return super.createIdentityLogic();
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
	public GameCharacterIdentityLogic getCharacterIdentityLogic()
	{
		return (GameCharacterIdentityLogic)identity;
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
