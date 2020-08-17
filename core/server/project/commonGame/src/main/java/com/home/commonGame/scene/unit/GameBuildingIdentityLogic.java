package com.home.commonGame.scene.unit;

import com.home.commonBase.config.game.BuildingLevelConfig;
import com.home.commonBase.constlist.generate.CallWayType;
import com.home.commonBase.data.scene.unit.identity.BuildingIdentityData;
import com.home.commonBase.scene.unit.BuildingIdentityLogic;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.building.BuildingBuildCompleteRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.building.BuildingCancelLevelUpRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.building.BuildingLevelUpingCompleteRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.building.BuildingStartLevelUpRequest;
import com.home.commonGame.part.player.Player;
import com.home.commonGame.scene.base.GameUnit;
import com.home.commonSceneBase.scene.unit.BBuildingIdentityLogic;
import com.home.shine.ctrl.Ctrl;

public class GameBuildingIdentityLogic extends BBuildingIdentityLogic
{
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
	
	@Override
	protected void doLevelUpCost(BuildingLevelConfig nextLevelConfig)
	{
		Player player=((GameUnit)_unit).getPlayer();
		
		player.bag.doCost(nextLevelConfig.costID,CallWayType.BuildingLevelUp);
	}
}
