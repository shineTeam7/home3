using ShineEngine;
using System;

/// <summary>
/// (generated by shine)
/// </summary>
public class SceneBaseResponseMaker:DataMaker
{
	public SceneBaseResponseMaker()
	{
		offSet=SceneBaseResponseType.off;
		list=new Func<BaseData>[SceneBaseResponseType.count-offSet];
		list[SceneBaseResponseType.AOITowerRefresh-offSet]=createAOITowerRefreshResponse;
		list[SceneBaseResponseType.AddBindVisionUnit-offSet]=createAddBindVisionUnitResponse;
		list[SceneBaseResponseType.AddBullet-offSet]=createAddBulletResponse;
		list[SceneBaseResponseType.AddFieldItemBagBind-offSet]=createAddFieldItemBagBindResponse;
		list[SceneBaseResponseType.AddUnit-offSet]=createAddUnitResponse;
		list[SceneBaseResponseType.AttackDamage-offSet]=createAttackDamageResponse;
		list[SceneBaseResponseType.AttackDamageOne-offSet]=createAttackDamageOneResponse;
		list[SceneBaseResponseType.BuildingBuildComplete-offSet]=createBuildingBuildCompleteResponse;
		list[SceneBaseResponseType.BuildingCancelLevelUp-offSet]=createBuildingCancelLevelUpResponse;
		list[SceneBaseResponseType.BuildingLevelUpingComplete-offSet]=createBuildingLevelUpingCompleteResponse;
		list[SceneBaseResponseType.BuildingStartLevelUp-offSet]=createBuildingStartLevelUpResponse;
		list[SceneBaseResponseType.CharacterRefreshPartRoleShowData-offSet]=createCharacterRefreshPartRoleShowDataResponse;
		list[SceneBaseResponseType.FrameSyncFrame-offSet]=createFrameSyncFrameResponse;
		list[SceneBaseResponseType.FrameSyncStart-offSet]=createFrameSyncStartResponse;
		list[SceneBaseResponseType.ReCUnitPullBack-offSet]=createReCUnitPullBackResponse;
		list[SceneBaseResponseType.ReCUnitSkillFailedEx-offSet]=createReCUnitSkillFailedExResponse;
		list[SceneBaseResponseType.ReCUnitSkillFailed-offSet]=createReCUnitSkillFailedResponse;
		list[SceneBaseResponseType.RefreshOperationState-offSet]=createRefreshOperationStateResponse;
		list[SceneBaseResponseType.RefreshSimpleUnitAttribute-offSet]=createRefreshSimpleUnitAttributeResponse;
		list[SceneBaseResponseType.RefreshSimpleUnitPos-offSet]=createRefreshSimpleUnitPosResponse;
		list[SceneBaseResponseType.RefreshUnitAttributes-offSet]=createRefreshUnitAttributesResponse;
		list[SceneBaseResponseType.RefreshUnitAvatar-offSet]=createRefreshUnitAvatarResponse;
		list[SceneBaseResponseType.RefreshUnitAvatarPart-offSet]=createRefreshUnitAvatarPartResponse;
		list[SceneBaseResponseType.RefreshUnitStatus-offSet]=createRefreshUnitStatusResponse;
		list[SceneBaseResponseType.RemoveBindVisionUnit-offSet]=createRemoveBindVisionUnitResponse;
		list[SceneBaseResponseType.RemoveBullet-offSet]=createRemoveBulletResponse;
		list[SceneBaseResponseType.RemoveFieldItemBagBind-offSet]=createRemoveFieldItemBagBindResponse;
		list[SceneBaseResponseType.RemoveUnit-offSet]=createRemoveUnitResponse;
		list[SceneBaseResponseType.RoleRefreshAttribute-offSet]=createRoleRefreshAttributeResponse;
		list[SceneBaseResponseType.RoleS-offSet]=createRoleSResponse;
		list[SceneBaseResponseType.SceneRadio-offSet]=createSceneRadioResponse;
		list[SceneBaseResponseType.SceneS-offSet]=createSceneSResponse;
		list[SceneBaseResponseType.SendBattleState-offSet]=createSendBattleStateResponse;
		list[SceneBaseResponseType.UnitAddBuff-offSet]=createUnitAddBuffResponse;
		list[SceneBaseResponseType.UnitAddGroupTimeMaxPercent-offSet]=createUnitAddGroupTimeMaxPercentResponse;
		list[SceneBaseResponseType.UnitAddGroupTimeMaxValue-offSet]=createUnitAddGroupTimeMaxValueResponse;
		list[SceneBaseResponseType.UnitAddGroupTimePass-offSet]=createUnitAddGroupTimePassResponse;
		list[SceneBaseResponseType.UnitChat-offSet]=createUnitChatResponse;
		list[SceneBaseResponseType.UnitDead-offSet]=createUnitDeadResponse;
		list[SceneBaseResponseType.UnitDrive-offSet]=createUnitDriveResponse;
		list[SceneBaseResponseType.UnitGetOffVehicle-offSet]=createUnitGetOffVehicleResponse;
		list[SceneBaseResponseType.UnitGetOnVehicle-offSet]=createUnitGetOnVehicleResponse;
		list[SceneBaseResponseType.UnitMoveDir-offSet]=createUnitMoveDirResponse;
		list[SceneBaseResponseType.UnitMovePosList-offSet]=createUnitMovePosListResponse;
		list[SceneBaseResponseType.UnitMovePos-offSet]=createUnitMovePosResponse;
		list[SceneBaseResponseType.UnitPreBattleSure-offSet]=createUnitPreBattleSureResponse;
		list[SceneBaseResponseType.UnitRefreshBuffLastNum-offSet]=createUnitRefreshBuffLastNumResponse;
		list[SceneBaseResponseType.UnitRefreshBuff-offSet]=createUnitRefreshBuffResponse;
		list[SceneBaseResponseType.UnitRemoveBuff-offSet]=createUnitRemoveBuffResponse;
		list[SceneBaseResponseType.UnitRemoveGroupCD-offSet]=createUnitRemoveGroupCDResponse;
		list[SceneBaseResponseType.UnitRevive-offSet]=createUnitReviveResponse;
		list[SceneBaseResponseType.UnitS-offSet]=createUnitSResponse;
		list[SceneBaseResponseType.UnitSetPosDir-offSet]=createUnitSetPosDirResponse;
		list[SceneBaseResponseType.UnitSkillOver-offSet]=createUnitSkillOverResponse;
		list[SceneBaseResponseType.UnitSpecialMove-offSet]=createUnitSpecialMoveResponse;
		list[SceneBaseResponseType.UnitStartCDs-offSet]=createUnitStartCDsResponse;
		list[SceneBaseResponseType.UnitStopMove-offSet]=createUnitStopMoveResponse;
		list[SceneBaseResponseType.UnitSyncCommand-offSet]=createUnitSyncCommandResponse;
		list[SceneBaseResponseType.UnitUseSkill-offSet]=createUnitUseSkillResponse;
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
