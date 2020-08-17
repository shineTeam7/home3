package com.home.commonSceneBase.scene.unit;

import com.home.commonBase.config.game.BuildingLevelConfig;
import com.home.commonBase.constlist.generate.CallWayType;
import com.home.commonBase.data.scene.unit.identity.BuildingIdentityData;
import com.home.commonBase.scene.unit.BuildingIdentityLogic;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.building.BuildingBuildCompleteRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.building.BuildingCancelLevelUpRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.building.BuildingLevelUpingCompleteRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.building.BuildingStartLevelUpRequest;
import com.home.shine.ctrl.Ctrl;

public class BBuildingIdentityLogic extends BuildingIdentityLogic
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
	
	
}
