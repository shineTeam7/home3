package com.home.commonClient.tool.generate;
import com.home.commonClient.constlist.generate.SceneBaseResponseType;
import com.home.commonClient.net.sceneBaseResponse.base.RoleSResponse;
import com.home.commonClient.net.sceneBaseResponse.base.SceneSResponse;
import com.home.commonClient.net.sceneBaseResponse.base.UnitSResponse;
import com.home.commonClient.net.sceneBaseResponse.role.RoleRefreshAttributeResponse;
import com.home.commonClient.net.sceneBaseResponse.scene.AOITowerRefreshResponse;
import com.home.commonClient.net.sceneBaseResponse.scene.AddBindVisionUnitResponse;
import com.home.commonClient.net.sceneBaseResponse.scene.AddFieldItemBagBindResponse;
import com.home.commonClient.net.sceneBaseResponse.scene.AddUnitResponse;
import com.home.commonClient.net.sceneBaseResponse.scene.RemoveBindVisionUnitResponse;
import com.home.commonClient.net.sceneBaseResponse.scene.RemoveFieldItemBagBindResponse;
import com.home.commonClient.net.sceneBaseResponse.scene.RemoveUnitResponse;
import com.home.commonClient.net.sceneBaseResponse.scene.SceneRadioResponse;
import com.home.commonClient.net.sceneBaseResponse.scene.SendBattleStateResponse;
import com.home.commonClient.net.sceneBaseResponse.syncScene.FrameSyncFrameResponse;
import com.home.commonClient.net.sceneBaseResponse.syncScene.FrameSyncStartResponse;
import com.home.commonClient.net.sceneBaseResponse.syncScene.UnitPreBattleSureResponse;
import com.home.commonClient.net.sceneBaseResponse.unit.AddBulletResponse;
import com.home.commonClient.net.sceneBaseResponse.unit.AttackDamageOneResponse;
import com.home.commonClient.net.sceneBaseResponse.unit.AttackDamageResponse;
import com.home.commonClient.net.sceneBaseResponse.unit.CharacterRefreshPartRoleShowDataResponse;
import com.home.commonClient.net.sceneBaseResponse.unit.ReCUnitPullBackResponse;
import com.home.commonClient.net.sceneBaseResponse.unit.ReCUnitSkillFailedExResponse;
import com.home.commonClient.net.sceneBaseResponse.unit.ReCUnitSkillFailedResponse;
import com.home.commonClient.net.sceneBaseResponse.unit.RefreshOperationStateResponse;
import com.home.commonClient.net.sceneBaseResponse.unit.RefreshSimpleUnitAttributeResponse;
import com.home.commonClient.net.sceneBaseResponse.unit.RefreshSimpleUnitPosResponse;
import com.home.commonClient.net.sceneBaseResponse.unit.RefreshUnitAttributesResponse;
import com.home.commonClient.net.sceneBaseResponse.unit.RefreshUnitAvatarPartResponse;
import com.home.commonClient.net.sceneBaseResponse.unit.RefreshUnitAvatarResponse;
import com.home.commonClient.net.sceneBaseResponse.unit.RefreshUnitStatusResponse;
import com.home.commonClient.net.sceneBaseResponse.unit.RemoveBulletResponse;
import com.home.commonClient.net.sceneBaseResponse.unit.UnitAddBuffResponse;
import com.home.commonClient.net.sceneBaseResponse.unit.UnitAddGroupTimeMaxPercentResponse;
import com.home.commonClient.net.sceneBaseResponse.unit.UnitAddGroupTimeMaxValueResponse;
import com.home.commonClient.net.sceneBaseResponse.unit.UnitAddGroupTimePassResponse;
import com.home.commonClient.net.sceneBaseResponse.unit.UnitChatResponse;
import com.home.commonClient.net.sceneBaseResponse.unit.UnitDeadResponse;
import com.home.commonClient.net.sceneBaseResponse.unit.UnitDriveResponse;
import com.home.commonClient.net.sceneBaseResponse.unit.UnitGetOffVehicleResponse;
import com.home.commonClient.net.sceneBaseResponse.unit.UnitGetOnVehicleResponse;
import com.home.commonClient.net.sceneBaseResponse.unit.UnitMoveDirResponse;
import com.home.commonClient.net.sceneBaseResponse.unit.UnitMovePosListResponse;
import com.home.commonClient.net.sceneBaseResponse.unit.UnitMovePosResponse;
import com.home.commonClient.net.sceneBaseResponse.unit.UnitRefreshBuffLastNumResponse;
import com.home.commonClient.net.sceneBaseResponse.unit.UnitRefreshBuffResponse;
import com.home.commonClient.net.sceneBaseResponse.unit.UnitRemoveBuffResponse;
import com.home.commonClient.net.sceneBaseResponse.unit.UnitRemoveGroupCDResponse;
import com.home.commonClient.net.sceneBaseResponse.unit.UnitReviveResponse;
import com.home.commonClient.net.sceneBaseResponse.unit.UnitSetPosDirResponse;
import com.home.commonClient.net.sceneBaseResponse.unit.UnitSkillOverResponse;
import com.home.commonClient.net.sceneBaseResponse.unit.UnitSpecialMoveResponse;
import com.home.commonClient.net.sceneBaseResponse.unit.UnitStartCDsResponse;
import com.home.commonClient.net.sceneBaseResponse.unit.UnitStopMoveResponse;
import com.home.commonClient.net.sceneBaseResponse.unit.UnitSyncCommandResponse;
import com.home.commonClient.net.sceneBaseResponse.unit.UnitUseSkillResponse;
import com.home.commonClient.net.sceneBaseResponse.unit.building.BuildingBuildCompleteResponse;
import com.home.commonClient.net.sceneBaseResponse.unit.building.BuildingCancelLevelUpResponse;
import com.home.commonClient.net.sceneBaseResponse.unit.building.BuildingLevelUpingCompleteResponse;
import com.home.commonClient.net.sceneBaseResponse.unit.building.BuildingStartLevelUpResponse;
import com.home.shine.data.BaseData;
import com.home.shine.tool.CreateDataFunc;
import com.home.shine.tool.DataMaker;

/** (generated by shine) */
public class SceneBaseResponseMaker extends DataMaker
{
	public SceneBaseResponseMaker()
	{
		offSet=SceneBaseResponseType.off;
		list=new CreateDataFunc[SceneBaseResponseType.count-offSet];
		list[SceneBaseResponseType.AOITowerRefresh-offSet]=this::createAOITowerRefreshResponse;
		list[SceneBaseResponseType.AddBindVisionUnit-offSet]=this::createAddBindVisionUnitResponse;
		list[SceneBaseResponseType.AddBullet-offSet]=this::createAddBulletResponse;
		list[SceneBaseResponseType.AddFieldItemBagBind-offSet]=this::createAddFieldItemBagBindResponse;
		list[SceneBaseResponseType.AddUnit-offSet]=this::createAddUnitResponse;
		list[SceneBaseResponseType.AttackDamage-offSet]=this::createAttackDamageResponse;
		list[SceneBaseResponseType.AttackDamageOne-offSet]=this::createAttackDamageOneResponse;
		list[SceneBaseResponseType.BuildingBuildComplete-offSet]=this::createBuildingBuildCompleteResponse;
		list[SceneBaseResponseType.BuildingCancelLevelUp-offSet]=this::createBuildingCancelLevelUpResponse;
		list[SceneBaseResponseType.BuildingLevelUpingComplete-offSet]=this::createBuildingLevelUpingCompleteResponse;
		list[SceneBaseResponseType.BuildingStartLevelUp-offSet]=this::createBuildingStartLevelUpResponse;
		list[SceneBaseResponseType.CharacterRefreshPartRoleShowData-offSet]=this::createCharacterRefreshPartRoleShowDataResponse;
		list[SceneBaseResponseType.FrameSyncFrame-offSet]=this::createFrameSyncFrameResponse;
		list[SceneBaseResponseType.FrameSyncStart-offSet]=this::createFrameSyncStartResponse;
		list[SceneBaseResponseType.ReCUnitPullBack-offSet]=this::createReCUnitPullBackResponse;
		list[SceneBaseResponseType.ReCUnitSkillFailedEx-offSet]=this::createReCUnitSkillFailedExResponse;
		list[SceneBaseResponseType.ReCUnitSkillFailed-offSet]=this::createReCUnitSkillFailedResponse;
		list[SceneBaseResponseType.RefreshOperationState-offSet]=this::createRefreshOperationStateResponse;
		list[SceneBaseResponseType.RefreshSimpleUnitAttribute-offSet]=this::createRefreshSimpleUnitAttributeResponse;
		list[SceneBaseResponseType.RefreshSimpleUnitPos-offSet]=this::createRefreshSimpleUnitPosResponse;
		list[SceneBaseResponseType.RefreshUnitAttributes-offSet]=this::createRefreshUnitAttributesResponse;
		list[SceneBaseResponseType.RefreshUnitAvatar-offSet]=this::createRefreshUnitAvatarResponse;
		list[SceneBaseResponseType.RefreshUnitAvatarPart-offSet]=this::createRefreshUnitAvatarPartResponse;
		list[SceneBaseResponseType.RefreshUnitStatus-offSet]=this::createRefreshUnitStatusResponse;
		list[SceneBaseResponseType.RemoveBindVisionUnit-offSet]=this::createRemoveBindVisionUnitResponse;
		list[SceneBaseResponseType.RemoveBullet-offSet]=this::createRemoveBulletResponse;
		list[SceneBaseResponseType.RemoveFieldItemBagBind-offSet]=this::createRemoveFieldItemBagBindResponse;
		list[SceneBaseResponseType.RemoveUnit-offSet]=this::createRemoveUnitResponse;
		list[SceneBaseResponseType.RoleRefreshAttribute-offSet]=this::createRoleRefreshAttributeResponse;
		list[SceneBaseResponseType.RoleS-offSet]=this::createRoleSResponse;
		list[SceneBaseResponseType.SceneRadio-offSet]=this::createSceneRadioResponse;
		list[SceneBaseResponseType.SceneS-offSet]=this::createSceneSResponse;
		list[SceneBaseResponseType.SendBattleState-offSet]=this::createSendBattleStateResponse;
		list[SceneBaseResponseType.UnitAddBuff-offSet]=this::createUnitAddBuffResponse;
		list[SceneBaseResponseType.UnitAddGroupTimeMaxPercent-offSet]=this::createUnitAddGroupTimeMaxPercentResponse;
		list[SceneBaseResponseType.UnitAddGroupTimeMaxValue-offSet]=this::createUnitAddGroupTimeMaxValueResponse;
		list[SceneBaseResponseType.UnitAddGroupTimePass-offSet]=this::createUnitAddGroupTimePassResponse;
		list[SceneBaseResponseType.UnitChat-offSet]=this::createUnitChatResponse;
		list[SceneBaseResponseType.UnitDead-offSet]=this::createUnitDeadResponse;
		list[SceneBaseResponseType.UnitDrive-offSet]=this::createUnitDriveResponse;
		list[SceneBaseResponseType.UnitGetOffVehicle-offSet]=this::createUnitGetOffVehicleResponse;
		list[SceneBaseResponseType.UnitGetOnVehicle-offSet]=this::createUnitGetOnVehicleResponse;
		list[SceneBaseResponseType.UnitMoveDir-offSet]=this::createUnitMoveDirResponse;
		list[SceneBaseResponseType.UnitMovePosList-offSet]=this::createUnitMovePosListResponse;
		list[SceneBaseResponseType.UnitMovePos-offSet]=this::createUnitMovePosResponse;
		list[SceneBaseResponseType.UnitPreBattleSure-offSet]=this::createUnitPreBattleSureResponse;
		list[SceneBaseResponseType.UnitRefreshBuffLastNum-offSet]=this::createUnitRefreshBuffLastNumResponse;
		list[SceneBaseResponseType.UnitRefreshBuff-offSet]=this::createUnitRefreshBuffResponse;
		list[SceneBaseResponseType.UnitRemoveBuff-offSet]=this::createUnitRemoveBuffResponse;
		list[SceneBaseResponseType.UnitRemoveGroupCD-offSet]=this::createUnitRemoveGroupCDResponse;
		list[SceneBaseResponseType.UnitRevive-offSet]=this::createUnitReviveResponse;
		list[SceneBaseResponseType.UnitS-offSet]=this::createUnitSResponse;
		list[SceneBaseResponseType.UnitSetPosDir-offSet]=this::createUnitSetPosDirResponse;
		list[SceneBaseResponseType.UnitSkillOver-offSet]=this::createUnitSkillOverResponse;
		list[SceneBaseResponseType.UnitSpecialMove-offSet]=this::createUnitSpecialMoveResponse;
		list[SceneBaseResponseType.UnitStartCDs-offSet]=this::createUnitStartCDsResponse;
		list[SceneBaseResponseType.UnitStopMove-offSet]=this::createUnitStopMoveResponse;
		list[SceneBaseResponseType.UnitSyncCommand-offSet]=this::createUnitSyncCommandResponse;
		list[SceneBaseResponseType.UnitUseSkill-offSet]=this::createUnitUseSkillResponse;
	}
	
	private BaseData createAOITowerRefreshResponse()
	{
		return new AOITowerRefreshResponse();
	}
	
	private BaseData createAddBindVisionUnitResponse()
	{
		return new AddBindVisionUnitResponse();
	}
	
	private BaseData createAddBulletResponse()
	{
		return new AddBulletResponse();
	}
	
	private BaseData createAddFieldItemBagBindResponse()
	{
		return new AddFieldItemBagBindResponse();
	}
	
	private BaseData createAddUnitResponse()
	{
		return new AddUnitResponse();
	}
	
	private BaseData createAttackDamageResponse()
	{
		return new AttackDamageResponse();
	}
	
	private BaseData createAttackDamageOneResponse()
	{
		return new AttackDamageOneResponse();
	}
	
	private BaseData createBuildingBuildCompleteResponse()
	{
		return new BuildingBuildCompleteResponse();
	}
	
	private BaseData createBuildingCancelLevelUpResponse()
	{
		return new BuildingCancelLevelUpResponse();
	}
	
	private BaseData createBuildingLevelUpingCompleteResponse()
	{
		return new BuildingLevelUpingCompleteResponse();
	}
	
	private BaseData createBuildingStartLevelUpResponse()
	{
		return new BuildingStartLevelUpResponse();
	}
	
	private BaseData createCharacterRefreshPartRoleShowDataResponse()
	{
		return new CharacterRefreshPartRoleShowDataResponse();
	}
	
	private BaseData createFrameSyncFrameResponse()
	{
		return new FrameSyncFrameResponse();
	}
	
	private BaseData createFrameSyncStartResponse()
	{
		return new FrameSyncStartResponse();
	}
	
	private BaseData createReCUnitPullBackResponse()
	{
		return new ReCUnitPullBackResponse();
	}
	
	private BaseData createReCUnitSkillFailedExResponse()
	{
		return new ReCUnitSkillFailedExResponse();
	}
	
	private BaseData createReCUnitSkillFailedResponse()
	{
		return new ReCUnitSkillFailedResponse();
	}
	
	private BaseData createRefreshOperationStateResponse()
	{
		return new RefreshOperationStateResponse();
	}
	
	private BaseData createRefreshSimpleUnitAttributeResponse()
	{
		return new RefreshSimpleUnitAttributeResponse();
	}
	
	private BaseData createRefreshSimpleUnitPosResponse()
	{
		return new RefreshSimpleUnitPosResponse();
	}
	
	private BaseData createRefreshUnitAttributesResponse()
	{
		return new RefreshUnitAttributesResponse();
	}
	
	private BaseData createRefreshUnitAvatarResponse()
	{
		return new RefreshUnitAvatarResponse();
	}
	
	private BaseData createRefreshUnitAvatarPartResponse()
	{
		return new RefreshUnitAvatarPartResponse();
	}
	
	private BaseData createRefreshUnitStatusResponse()
	{
		return new RefreshUnitStatusResponse();
	}
	
	private BaseData createRemoveBindVisionUnitResponse()
	{
		return new RemoveBindVisionUnitResponse();
	}
	
	private BaseData createRemoveBulletResponse()
	{
		return new RemoveBulletResponse();
	}
	
	private BaseData createRemoveFieldItemBagBindResponse()
	{
		return new RemoveFieldItemBagBindResponse();
	}
	
	private BaseData createRemoveUnitResponse()
	{
		return new RemoveUnitResponse();
	}
	
	private BaseData createRoleRefreshAttributeResponse()
	{
		return new RoleRefreshAttributeResponse();
	}
	
	private BaseData createRoleSResponse()
	{
		return new RoleSResponse();
	}
	
	private BaseData createSceneRadioResponse()
	{
		return new SceneRadioResponse();
	}
	
	private BaseData createSceneSResponse()
	{
		return new SceneSResponse();
	}
	
	private BaseData createSendBattleStateResponse()
	{
		return new SendBattleStateResponse();
	}
	
	private BaseData createUnitAddBuffResponse()
	{
		return new UnitAddBuffResponse();
	}
	
	private BaseData createUnitAddGroupTimeMaxPercentResponse()
	{
		return new UnitAddGroupTimeMaxPercentResponse();
	}
	
	private BaseData createUnitAddGroupTimeMaxValueResponse()
	{
		return new UnitAddGroupTimeMaxValueResponse();
	}
	
	private BaseData createUnitAddGroupTimePassResponse()
	{
		return new UnitAddGroupTimePassResponse();
	}
	
	private BaseData createUnitChatResponse()
	{
		return new UnitChatResponse();
	}
	
	private BaseData createUnitDeadResponse()
	{
		return new UnitDeadResponse();
	}
	
	private BaseData createUnitDriveResponse()
	{
		return new UnitDriveResponse();
	}
	
	private BaseData createUnitGetOffVehicleResponse()
	{
		return new UnitGetOffVehicleResponse();
	}
	
	private BaseData createUnitGetOnVehicleResponse()
	{
		return new UnitGetOnVehicleResponse();
	}
	
	private BaseData createUnitMoveDirResponse()
	{
		return new UnitMoveDirResponse();
	}
	
	private BaseData createUnitMovePosListResponse()
	{
		return new UnitMovePosListResponse();
	}
	
	private BaseData createUnitMovePosResponse()
	{
		return new UnitMovePosResponse();
	}
	
	private BaseData createUnitPreBattleSureResponse()
	{
		return new UnitPreBattleSureResponse();
	}
	
	private BaseData createUnitRefreshBuffLastNumResponse()
	{
		return new UnitRefreshBuffLastNumResponse();
	}
	
	private BaseData createUnitRefreshBuffResponse()
	{
		return new UnitRefreshBuffResponse();
	}
	
	private BaseData createUnitRemoveBuffResponse()
	{
		return new UnitRemoveBuffResponse();
	}
	
	private BaseData createUnitRemoveGroupCDResponse()
	{
		return new UnitRemoveGroupCDResponse();
	}
	
	private BaseData createUnitReviveResponse()
	{
		return new UnitReviveResponse();
	}
	
	private BaseData createUnitSResponse()
	{
		return new UnitSResponse();
	}
	
	private BaseData createUnitSetPosDirResponse()
	{
		return new UnitSetPosDirResponse();
	}
	
	private BaseData createUnitSkillOverResponse()
	{
		return new UnitSkillOverResponse();
	}
	
	private BaseData createUnitSpecialMoveResponse()
	{
		return new UnitSpecialMoveResponse();
	}
	
	private BaseData createUnitStartCDsResponse()
	{
		return new UnitStartCDsResponse();
	}
	
	private BaseData createUnitStopMoveResponse()
	{
		return new UnitStopMoveResponse();
	}
	
	private BaseData createUnitSyncCommandResponse()
	{
		return new UnitSyncCommandResponse();
	}
	
	private BaseData createUnitUseSkillResponse()
	{
		return new UnitUseSkillResponse();
	}
	
}
