package com.home.commonSceneBase.tool.generate;
import com.home.commonSceneBase.constlist.generate.SceneBaseRequestType;
import com.home.commonSceneBase.net.sceneBaseRequest.base.RoleSRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.base.SceneSRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.base.UnitSRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.role.RoleRefreshAttributeRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.scene.AOITowerRefreshRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.scene.AddBindVisionUnitRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.scene.AddFieldItemBagBindRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.scene.AddUnitRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.scene.RemoveBindVisionUnitRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.scene.RemoveFieldItemBagBindRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.scene.RemoveUnitRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.scene.SceneRadioRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.scene.SendBattleStateRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.syncScene.FrameSyncFrameRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.syncScene.FrameSyncStartRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.syncScene.UnitPreBattleSureRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.AddBulletRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.AttackDamageOneRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.AttackDamageRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.CharacterRefreshPartRoleShowDataRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.ReCUnitPullBackRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.ReCUnitSkillFailedExRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.ReCUnitSkillFailedRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.RefreshOperationStateRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.RefreshSimpleUnitAttributeRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.RefreshSimpleUnitPosRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.RefreshUnitAttributesRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.RefreshUnitAvatarPartRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.RefreshUnitAvatarRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.RefreshUnitStatusRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.RemoveBulletRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.UnitAddBuffRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.UnitAddGroupTimeMaxPercentRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.UnitAddGroupTimeMaxValueRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.UnitAddGroupTimePassRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.UnitChatRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.UnitDeadRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.UnitDriveRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.UnitGetOffVehicleRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.UnitGetOnVehicleRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.UnitMoveDirRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.UnitMovePosListRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.UnitMovePosRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.UnitRefreshBuffLastNumRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.UnitRefreshBuffRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.UnitRemoveBuffRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.UnitRemoveGroupCDRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.UnitReviveRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.UnitSetPosDirRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.UnitSkillOverRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.UnitSpecialMoveRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.UnitStartCDsRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.UnitStopMoveRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.UnitSyncCommandRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.UnitUseSkillRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.building.BuildingBuildCompleteRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.building.BuildingCancelLevelUpRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.building.BuildingLevelUpingCompleteRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.building.BuildingStartLevelUpRequest;
import com.home.shine.data.BaseData;
import com.home.shine.tool.CreateDataFunc;
import com.home.shine.tool.DataMaker;

/** (generated by shine) */
public class SceneBaseRequestMaker extends DataMaker
{
	public SceneBaseRequestMaker()
	{
		offSet=SceneBaseRequestType.off;
		list=new CreateDataFunc[SceneBaseRequestType.count-offSet];
		list[SceneBaseRequestType.AOITowerRefresh-offSet]=this::createAOITowerRefreshRequest;
		list[SceneBaseRequestType.AddBindVisionUnit-offSet]=this::createAddBindVisionUnitRequest;
		list[SceneBaseRequestType.AddBullet-offSet]=this::createAddBulletRequest;
		list[SceneBaseRequestType.AddFieldItemBagBind-offSet]=this::createAddFieldItemBagBindRequest;
		list[SceneBaseRequestType.AddUnit-offSet]=this::createAddUnitRequest;
		list[SceneBaseRequestType.AttackDamage-offSet]=this::createAttackDamageRequest;
		list[SceneBaseRequestType.AttackDamageOne-offSet]=this::createAttackDamageOneRequest;
		list[SceneBaseRequestType.BuildingBuildComplete-offSet]=this::createBuildingBuildCompleteRequest;
		list[SceneBaseRequestType.BuildingCancelLevelUp-offSet]=this::createBuildingCancelLevelUpRequest;
		list[SceneBaseRequestType.BuildingLevelUpingComplete-offSet]=this::createBuildingLevelUpingCompleteRequest;
		list[SceneBaseRequestType.BuildingStartLevelUp-offSet]=this::createBuildingStartLevelUpRequest;
		list[SceneBaseRequestType.CharacterRefreshPartRoleShowData-offSet]=this::createCharacterRefreshPartRoleShowDataRequest;
		list[SceneBaseRequestType.FrameSyncFrame-offSet]=this::createFrameSyncFrameRequest;
		list[SceneBaseRequestType.FrameSyncStart-offSet]=this::createFrameSyncStartRequest;
		list[SceneBaseRequestType.ReCUnitPullBack-offSet]=this::createReCUnitPullBackRequest;
		list[SceneBaseRequestType.ReCUnitSkillFailedEx-offSet]=this::createReCUnitSkillFailedExRequest;
		list[SceneBaseRequestType.ReCUnitSkillFailed-offSet]=this::createReCUnitSkillFailedRequest;
		list[SceneBaseRequestType.RefreshOperationState-offSet]=this::createRefreshOperationStateRequest;
		list[SceneBaseRequestType.RefreshSimpleUnitAttribute-offSet]=this::createRefreshSimpleUnitAttributeRequest;
		list[SceneBaseRequestType.RefreshSimpleUnitPos-offSet]=this::createRefreshSimpleUnitPosRequest;
		list[SceneBaseRequestType.RefreshUnitAttributes-offSet]=this::createRefreshUnitAttributesRequest;
		list[SceneBaseRequestType.RefreshUnitAvatar-offSet]=this::createRefreshUnitAvatarRequest;
		list[SceneBaseRequestType.RefreshUnitAvatarPart-offSet]=this::createRefreshUnitAvatarPartRequest;
		list[SceneBaseRequestType.RefreshUnitStatus-offSet]=this::createRefreshUnitStatusRequest;
		list[SceneBaseRequestType.RemoveBindVisionUnit-offSet]=this::createRemoveBindVisionUnitRequest;
		list[SceneBaseRequestType.RemoveBullet-offSet]=this::createRemoveBulletRequest;
		list[SceneBaseRequestType.RemoveFieldItemBagBind-offSet]=this::createRemoveFieldItemBagBindRequest;
		list[SceneBaseRequestType.RemoveUnit-offSet]=this::createRemoveUnitRequest;
		list[SceneBaseRequestType.RoleRefreshAttribute-offSet]=this::createRoleRefreshAttributeRequest;
		list[SceneBaseRequestType.RoleS-offSet]=this::createRoleSRequest;
		list[SceneBaseRequestType.SceneRadio-offSet]=this::createSceneRadioRequest;
		list[SceneBaseRequestType.SceneS-offSet]=this::createSceneSRequest;
		list[SceneBaseRequestType.SendBattleState-offSet]=this::createSendBattleStateRequest;
		list[SceneBaseRequestType.UnitAddBuff-offSet]=this::createUnitAddBuffRequest;
		list[SceneBaseRequestType.UnitAddGroupTimeMaxPercent-offSet]=this::createUnitAddGroupTimeMaxPercentRequest;
		list[SceneBaseRequestType.UnitAddGroupTimeMaxValue-offSet]=this::createUnitAddGroupTimeMaxValueRequest;
		list[SceneBaseRequestType.UnitAddGroupTimePass-offSet]=this::createUnitAddGroupTimePassRequest;
		list[SceneBaseRequestType.UnitChat-offSet]=this::createUnitChatRequest;
		list[SceneBaseRequestType.UnitDead-offSet]=this::createUnitDeadRequest;
		list[SceneBaseRequestType.UnitDrive-offSet]=this::createUnitDriveRequest;
		list[SceneBaseRequestType.UnitGetOffVehicle-offSet]=this::createUnitGetOffVehicleRequest;
		list[SceneBaseRequestType.UnitGetOnVehicle-offSet]=this::createUnitGetOnVehicleRequest;
		list[SceneBaseRequestType.UnitMoveDir-offSet]=this::createUnitMoveDirRequest;
		list[SceneBaseRequestType.UnitMovePosList-offSet]=this::createUnitMovePosListRequest;
		list[SceneBaseRequestType.UnitMovePos-offSet]=this::createUnitMovePosRequest;
		list[SceneBaseRequestType.UnitPreBattleSure-offSet]=this::createUnitPreBattleSureRequest;
		list[SceneBaseRequestType.UnitRefreshBuffLastNum-offSet]=this::createUnitRefreshBuffLastNumRequest;
		list[SceneBaseRequestType.UnitRefreshBuff-offSet]=this::createUnitRefreshBuffRequest;
		list[SceneBaseRequestType.UnitRemoveBuff-offSet]=this::createUnitRemoveBuffRequest;
		list[SceneBaseRequestType.UnitRemoveGroupCD-offSet]=this::createUnitRemoveGroupCDRequest;
		list[SceneBaseRequestType.UnitRevive-offSet]=this::createUnitReviveRequest;
		list[SceneBaseRequestType.UnitS-offSet]=this::createUnitSRequest;
		list[SceneBaseRequestType.UnitSetPosDir-offSet]=this::createUnitSetPosDirRequest;
		list[SceneBaseRequestType.UnitSkillOver-offSet]=this::createUnitSkillOverRequest;
		list[SceneBaseRequestType.UnitSpecialMove-offSet]=this::createUnitSpecialMoveRequest;
		list[SceneBaseRequestType.UnitStartCDs-offSet]=this::createUnitStartCDsRequest;
		list[SceneBaseRequestType.UnitStopMove-offSet]=this::createUnitStopMoveRequest;
		list[SceneBaseRequestType.UnitSyncCommand-offSet]=this::createUnitSyncCommandRequest;
		list[SceneBaseRequestType.UnitUseSkill-offSet]=this::createUnitUseSkillRequest;
	}
	
	private BaseData createAOITowerRefreshRequest()
	{
		return new AOITowerRefreshRequest();
	}
	
	private BaseData createAddBindVisionUnitRequest()
	{
		return new AddBindVisionUnitRequest();
	}
	
	private BaseData createAddBulletRequest()
	{
		return new AddBulletRequest();
	}
	
	private BaseData createAddFieldItemBagBindRequest()
	{
		return new AddFieldItemBagBindRequest();
	}
	
	private BaseData createAddUnitRequest()
	{
		return new AddUnitRequest();
	}
	
	private BaseData createAttackDamageRequest()
	{
		return new AttackDamageRequest();
	}
	
	private BaseData createAttackDamageOneRequest()
	{
		return new AttackDamageOneRequest();
	}
	
	private BaseData createBuildingBuildCompleteRequest()
	{
		return new BuildingBuildCompleteRequest();
	}
	
	private BaseData createBuildingCancelLevelUpRequest()
	{
		return new BuildingCancelLevelUpRequest();
	}
	
	private BaseData createBuildingLevelUpingCompleteRequest()
	{
		return new BuildingLevelUpingCompleteRequest();
	}
	
	private BaseData createBuildingStartLevelUpRequest()
	{
		return new BuildingStartLevelUpRequest();
	}
	
	private BaseData createCharacterRefreshPartRoleShowDataRequest()
	{
		return new CharacterRefreshPartRoleShowDataRequest();
	}
	
	private BaseData createFrameSyncFrameRequest()
	{
		return new FrameSyncFrameRequest();
	}
	
	private BaseData createFrameSyncStartRequest()
	{
		return new FrameSyncStartRequest();
	}
	
	private BaseData createReCUnitPullBackRequest()
	{
		return new ReCUnitPullBackRequest();
	}
	
	private BaseData createReCUnitSkillFailedExRequest()
	{
		return new ReCUnitSkillFailedExRequest();
	}
	
	private BaseData createReCUnitSkillFailedRequest()
	{
		return new ReCUnitSkillFailedRequest();
	}
	
	private BaseData createRefreshOperationStateRequest()
	{
		return new RefreshOperationStateRequest();
	}
	
	private BaseData createRefreshSimpleUnitAttributeRequest()
	{
		return new RefreshSimpleUnitAttributeRequest();
	}
	
	private BaseData createRefreshSimpleUnitPosRequest()
	{
		return new RefreshSimpleUnitPosRequest();
	}
	
	private BaseData createRefreshUnitAttributesRequest()
	{
		return new RefreshUnitAttributesRequest();
	}
	
	private BaseData createRefreshUnitAvatarRequest()
	{
		return new RefreshUnitAvatarRequest();
	}
	
	private BaseData createRefreshUnitAvatarPartRequest()
	{
		return new RefreshUnitAvatarPartRequest();
	}
	
	private BaseData createRefreshUnitStatusRequest()
	{
		return new RefreshUnitStatusRequest();
	}
	
	private BaseData createRemoveBindVisionUnitRequest()
	{
		return new RemoveBindVisionUnitRequest();
	}
	
	private BaseData createRemoveBulletRequest()
	{
		return new RemoveBulletRequest();
	}
	
	private BaseData createRemoveFieldItemBagBindRequest()
	{
		return new RemoveFieldItemBagBindRequest();
	}
	
	private BaseData createRemoveUnitRequest()
	{
		return new RemoveUnitRequest();
	}
	
	private BaseData createRoleRefreshAttributeRequest()
	{
		return new RoleRefreshAttributeRequest();
	}
	
	private BaseData createRoleSRequest()
	{
		return new RoleSRequest();
	}
	
	private BaseData createSceneRadioRequest()
	{
		return new SceneRadioRequest();
	}
	
	private BaseData createSceneSRequest()
	{
		return new SceneSRequest();
	}
	
	private BaseData createSendBattleStateRequest()
	{
		return new SendBattleStateRequest();
	}
	
	private BaseData createUnitAddBuffRequest()
	{
		return new UnitAddBuffRequest();
	}
	
	private BaseData createUnitAddGroupTimeMaxPercentRequest()
	{
		return new UnitAddGroupTimeMaxPercentRequest();
	}
	
	private BaseData createUnitAddGroupTimeMaxValueRequest()
	{
		return new UnitAddGroupTimeMaxValueRequest();
	}
	
	private BaseData createUnitAddGroupTimePassRequest()
	{
		return new UnitAddGroupTimePassRequest();
	}
	
	private BaseData createUnitChatRequest()
	{
		return new UnitChatRequest();
	}
	
	private BaseData createUnitDeadRequest()
	{
		return new UnitDeadRequest();
	}
	
	private BaseData createUnitDriveRequest()
	{
		return new UnitDriveRequest();
	}
	
	private BaseData createUnitGetOffVehicleRequest()
	{
		return new UnitGetOffVehicleRequest();
	}
	
	private BaseData createUnitGetOnVehicleRequest()
	{
		return new UnitGetOnVehicleRequest();
	}
	
	private BaseData createUnitMoveDirRequest()
	{
		return new UnitMoveDirRequest();
	}
	
	private BaseData createUnitMovePosListRequest()
	{
		return new UnitMovePosListRequest();
	}
	
	private BaseData createUnitMovePosRequest()
	{
		return new UnitMovePosRequest();
	}
	
	private BaseData createUnitPreBattleSureRequest()
	{
		return new UnitPreBattleSureRequest();
	}
	
	private BaseData createUnitRefreshBuffLastNumRequest()
	{
		return new UnitRefreshBuffLastNumRequest();
	}
	
	private BaseData createUnitRefreshBuffRequest()
	{
		return new UnitRefreshBuffRequest();
	}
	
	private BaseData createUnitRemoveBuffRequest()
	{
		return new UnitRemoveBuffRequest();
	}
	
	private BaseData createUnitRemoveGroupCDRequest()
	{
		return new UnitRemoveGroupCDRequest();
	}
	
	private BaseData createUnitReviveRequest()
	{
		return new UnitReviveRequest();
	}
	
	private BaseData createUnitSRequest()
	{
		return new UnitSRequest();
	}
	
	private BaseData createUnitSetPosDirRequest()
	{
		return new UnitSetPosDirRequest();
	}
	
	private BaseData createUnitSkillOverRequest()
	{
		return new UnitSkillOverRequest();
	}
	
	private BaseData createUnitSpecialMoveRequest()
	{
		return new UnitSpecialMoveRequest();
	}
	
	private BaseData createUnitStartCDsRequest()
	{
		return new UnitStartCDsRequest();
	}
	
	private BaseData createUnitStopMoveRequest()
	{
		return new UnitStopMoveRequest();
	}
	
	private BaseData createUnitSyncCommandRequest()
	{
		return new UnitSyncCommandRequest();
	}
	
	private BaseData createUnitUseSkillRequest()
	{
		return new UnitUseSkillRequest();
	}
	
}
