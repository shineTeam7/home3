package com.home.commonGame.scene.unit;

import com.home.commonBase.config.game.BuildingLevelConfig;
import com.home.commonBase.constlist.generate.CallWayType;
import com.home.commonBase.data.scene.unit.identity.BuildingIdentityData;
import com.home.commonBase.scene.unit.BuildingIdentityLogic;
import com.home.commonGame.net.request.scene.unit.building.BuildingBuildCompleteRequest;
import com.home.commonGame.net.request.scene.unit.building.BuildingCancelLevelUpRequest;
import com.home.commonGame.net.request.scene.unit.building.BuildingLevelUpingCompleteRequest;
import com.home.commonGame.net.request.scene.unit.building.BuildingStartLevelUpRequest;
import com.home.commonGame.part.player.Player;
import com.home.commonGame.scene.base.GameUnit;
import com.home.shine.ctrl.Ctrl;

public class GameBuildingIdentityLogic extends BuildingIdentityLogic
{
	@Override
	protected void sendBuildComplete()
	{
		_unit.radioMessage(BuildingBuildCompleteRequest.create(_unit.instanceID),true);
	}
	
	@Override
	protected void sendLevelUpComplete()
	{
		BuildingIdentityData iData=(BuildingIdentityData)_unit.getUnitData().identity;
		
		_unit.radioMessage(BuildingLevelUpingCompleteRequest.create(_unit.instanceID,iData.level),true);
	}
	
	@Override
	protected void sendCancelLevelUp()
	{
		_unit.radioMessage(BuildingCancelLevelUpRequest.create(_unit.instanceID),true);
	}
	
	/** 发送开始建筑升级 */
	@Override
	protected void sendStartLevelUp()
	{
		_unit.radioMessage(BuildingStartLevelUpRequest.create(_unit.instanceID), true);
	}
	
	@Override
	protected boolean checkCanLevelUp(BuildingLevelConfig nextLevelConfig)
	{
		Player player=((GameUnit)_unit).getPlayer();
		
		//没有cost
		if(!player.bag.hasCost(nextLevelConfig.costID))
		{
			Ctrl.log("消耗不足");
			return false;
		}
		
		//TODO:将cost和占用资源都做好
		
		return true;
	}
	
	protected void doLevelUpCost(BuildingLevelConfig nextLevelConfig)
	{
		Player player=((GameUnit)_unit).getPlayer();
		
		player.bag.doCost(nextLevelConfig.costID,CallWayType.BuildingLevelUp);
	}
}
