package com.home.commonClient.tool.generate;
import com.home.commonClient.constlist.generate.GameResponseType;
import com.home.commonClient.net.response.activity.ActivityCompleteOnceResponse;
import com.home.commonClient.net.response.activity.ActivityResetResponse;
import com.home.commonClient.net.response.activity.ActivitySwitchResponse;
import com.home.commonClient.net.response.func.auction.FuncAuctionAddSaleItemResponse;
import com.home.commonClient.net.response.func.auction.FuncAuctionReQueryResponse;
import com.home.commonClient.net.response.func.auction.FuncAuctionRefreshSaleItemResponse;
import com.home.commonClient.net.response.func.auction.FuncAuctionRemoveSaleItemResponse;
import com.home.commonClient.net.response.func.auction.FuncReGetAuctionItemSuggestPriceResponse;
import com.home.commonClient.net.response.func.base.FuncCloseResponse;
import com.home.commonClient.net.response.func.base.FuncOpenResponse;
import com.home.commonClient.net.response.func.base.FuncPlayerRoleGroupSResponse;
import com.home.commonClient.net.response.func.base.FuncSResponse;
import com.home.commonClient.net.response.func.item.FuncAddItemResponse;
import com.home.commonClient.net.response.func.item.FuncAddOneItemNumResponse;
import com.home.commonClient.net.response.func.item.FuncAddOneItemResponse;
import com.home.commonClient.net.response.func.item.FuncRefreshItemGridNumResponse;
import com.home.commonClient.net.response.func.item.FuncRemoveItemResponse;
import com.home.commonClient.net.response.func.item.FuncRemoveOneItemResponse;
import com.home.commonClient.net.response.func.item.FuncSendCleanUpItemResponse;
import com.home.commonClient.net.response.func.item.FuncSendMoveEquipResponse;
import com.home.commonClient.net.response.func.item.FuncSendMoveItemResponse;
import com.home.commonClient.net.response.func.item.FuncSendPutOffEquipResponse;
import com.home.commonClient.net.response.func.item.FuncSendPutOnEquipResponse;
import com.home.commonClient.net.response.func.item.FuncUseItemResultResponse;
import com.home.commonClient.net.response.func.match.FuncCancelMatchResponse;
import com.home.commonClient.net.response.func.match.FuncMatchOverResponse;
import com.home.commonClient.net.response.func.match.FuncMatchSuccessResponse;
import com.home.commonClient.net.response.func.match.FuncMatchTimeOutResponse;
import com.home.commonClient.net.response.func.match.FuncReAddMatchResponse;
import com.home.commonClient.net.response.func.match.FuncSendAcceptMatchResponse;
import com.home.commonClient.net.response.func.match.FuncStartMatchResponse;
import com.home.commonClient.net.response.func.rank.FuncReGetPageShowListResponse;
import com.home.commonClient.net.response.func.rank.FuncReGetPageShowResponse;
import com.home.commonClient.net.response.func.rank.FuncReGetSelfPageShowResponse;
import com.home.commonClient.net.response.func.rank.FuncRefreshRankResponse;
import com.home.commonClient.net.response.func.rank.FuncRefreshRoleGroupRankResponse;
import com.home.commonClient.net.response.func.rank.FuncResetRankResponse;
import com.home.commonClient.net.response.func.rank.FuncResetRoleGroupRankResponse;
import com.home.commonClient.net.response.func.rank.subsection.FuncReGetSubsectionPageShowListResponse;
import com.home.commonClient.net.response.func.rank.subsection.FuncRefreshSubsectionRankResponse;
import com.home.commonClient.net.response.func.roleGroup.FuncReGetRoleGroupDataResponse;
import com.home.commonClient.net.response.func.roleGroup.FuncRefeshTitleRoleGroupResponse;
import com.home.commonClient.net.response.func.roleGroup.FuncSendAddApplyRoleGroupResponse;
import com.home.commonClient.net.response.func.roleGroup.FuncSendAddApplyRoleGroupSelfResponse;
import com.home.commonClient.net.response.func.roleGroup.FuncSendChangeCanInviteInAbsRoleGroupResponse;
import com.home.commonClient.net.response.func.roleGroup.FuncSendChangeLeaderRoleGroupResponse;
import com.home.commonClient.net.response.func.roleGroup.FuncSendHandleApplyResultRoleGroupResponse;
import com.home.commonClient.net.response.func.roleGroup.FuncSendHandleApplyResultToMemberResponse;
import com.home.commonClient.net.response.func.roleGroup.FuncSendHandleInviteResultRoleGroupResponse;
import com.home.commonClient.net.response.func.roleGroup.FuncSendInviteRoleGroupResponse;
import com.home.commonClient.net.response.func.roleGroup.FuncSendPlayerJoinRoleGroupResponse;
import com.home.commonClient.net.response.func.roleGroup.FuncSendPlayerLeaveRoleGroupResponse;
import com.home.commonClient.net.response.func.roleGroup.FuncSendRoleGroupAddMemberResponse;
import com.home.commonClient.net.response.func.roleGroup.FuncSendRoleGroupChangeResponse;
import com.home.commonClient.net.response.func.roleGroup.FuncSendRoleGroupInfoLogResponse;
import com.home.commonClient.net.response.func.roleGroup.FuncSendRoleGroupMemberChangeResponse;
import com.home.commonClient.net.response.func.roleGroup.FuncSendRoleGroupMemberRoleShowChangeResponse;
import com.home.commonClient.net.response.func.roleGroup.FuncSendRoleGroupRemoveMemberResponse;
import com.home.commonClient.net.response.guide.RefreshMainGuideStepResponse;
import com.home.commonClient.net.response.item.AddRewardResponse;
import com.home.commonClient.net.response.login.ClientHotfixResponse;
import com.home.commonClient.net.response.login.CreatePlayerSuccessResponse;
import com.home.commonClient.net.response.login.DeletePlayerSuccessResponse;
import com.home.commonClient.net.response.login.InitClientResponse;
import com.home.commonClient.net.response.login.RePlayerListResponse;
import com.home.commonClient.net.response.login.SendBindPlatformResponse;
import com.home.commonClient.net.response.login.SwitchGameResponse;
import com.home.commonClient.net.response.mail.AddMailResponse;
import com.home.commonClient.net.response.mail.ReGetAllMailResponse;
import com.home.commonClient.net.response.mail.SendDeleteMailResponse;
import com.home.commonClient.net.response.mail.TakeMailSuccessResponse;
import com.home.commonClient.net.response.quest.RefreshTaskResponse;
import com.home.commonClient.net.response.quest.SendAcceptAchievementResponse;
import com.home.commonClient.net.response.quest.SendAcceptQuestResponse;
import com.home.commonClient.net.response.quest.SendAchievementCompleteResponse;
import com.home.commonClient.net.response.quest.SendClearAllQuestByGMResponse;
import com.home.commonClient.net.response.quest.SendCommitQuestResponse;
import com.home.commonClient.net.response.quest.SendGetAchievementRewardSuccessResponse;
import com.home.commonClient.net.response.quest.SendGiveUpQuestResponse;
import com.home.commonClient.net.response.quest.SendQuestFailedResponse;
import com.home.commonClient.net.response.quest.SendRemoveAcceptQuestResponse;
import com.home.commonClient.net.response.role.ChangeRoleNameResponse;
import com.home.commonClient.net.response.role.LevelUpResponse;
import com.home.commonClient.net.response.role.RefreshCurrencyResponse;
import com.home.commonClient.net.response.role.RefreshExpResponse;
import com.home.commonClient.net.response.role.RefreshFightForceResponse;
import com.home.commonClient.net.response.role.munit.MUnitAddBuffResponse;
import com.home.commonClient.net.response.role.munit.MUnitAddGroupTimeMaxPercentResponse;
import com.home.commonClient.net.response.role.munit.MUnitAddGroupTimeMaxValueResponse;
import com.home.commonClient.net.response.role.munit.MUnitAddGroupTimePassResponse;
import com.home.commonClient.net.response.role.munit.MUnitAddSkillResponse;
import com.home.commonClient.net.response.role.munit.MUnitRefreshAttributesResponse;
import com.home.commonClient.net.response.role.munit.MUnitRefreshAvatarPartResponse;
import com.home.commonClient.net.response.role.munit.MUnitRefreshAvatarResponse;
import com.home.commonClient.net.response.role.munit.MUnitRefreshBuffLastNumResponse;
import com.home.commonClient.net.response.role.munit.MUnitRefreshBuffResponse;
import com.home.commonClient.net.response.role.munit.MUnitRefreshStatusResponse;
import com.home.commonClient.net.response.role.munit.MUnitRemoveBuffResponse;
import com.home.commonClient.net.response.role.munit.MUnitRemoveGroupCDResponse;
import com.home.commonClient.net.response.role.munit.MUnitRemoveSkillResponse;
import com.home.commonClient.net.response.role.munit.MUnitSResponse;
import com.home.commonClient.net.response.role.munit.MUnitStartCDsResponse;
import com.home.commonClient.net.response.role.pet.AddPetResponse;
import com.home.commonClient.net.response.role.pet.RefreshPetIsWorkingResponse;
import com.home.commonClient.net.response.role.pet.RemovePetResponse;
import com.home.commonClient.net.response.scene.base.RefreshCurrentLineResponse;
import com.home.commonClient.net.response.scene.base.RoleSResponse;
import com.home.commonClient.net.response.scene.base.SceneSResponse;
import com.home.commonClient.net.response.scene.base.UnitSResponse;
import com.home.commonClient.net.response.scene.role.RoleRefreshAttributeResponse;
import com.home.commonClient.net.response.scene.scene.AOITowerRefreshResponse;
import com.home.commonClient.net.response.scene.scene.AddBindVisionUnitResponse;
import com.home.commonClient.net.response.scene.scene.AddFieldItemBagBindResponse;
import com.home.commonClient.net.response.scene.scene.AddUnitResponse;
import com.home.commonClient.net.response.scene.scene.EnterNoneSceneResponse;
import com.home.commonClient.net.response.scene.scene.EnterSceneFailedResponse;
import com.home.commonClient.net.response.scene.scene.EnterSceneResponse;
import com.home.commonClient.net.response.scene.scene.LeaveSceneResponse;
import com.home.commonClient.net.response.scene.scene.PreEnterSceneNextResponse;
import com.home.commonClient.net.response.scene.scene.PreEnterSceneResponse;
import com.home.commonClient.net.response.scene.scene.RemoveBindVisionUnitResponse;
import com.home.commonClient.net.response.scene.scene.RemoveFieldItemBagBindResponse;
import com.home.commonClient.net.response.scene.scene.RemoveUnitResponse;
import com.home.commonClient.net.response.scene.scene.SceneRadioResponse;
import com.home.commonClient.net.response.scene.scene.SendBattleStateResponse;
import com.home.commonClient.net.response.scene.syncScene.FrameSyncFrameResponse;
import com.home.commonClient.net.response.scene.syncScene.FrameSyncStartResponse;
import com.home.commonClient.net.response.scene.syncScene.UnitPreBattleSureResponse;
import com.home.commonClient.net.response.scene.unit.AddBulletResponse;
import com.home.commonClient.net.response.scene.unit.AttackDamageOneResponse;
import com.home.commonClient.net.response.scene.unit.AttackDamageResponse;
import com.home.commonClient.net.response.scene.unit.CharacterRefreshPartRoleShowDataResponse;
import com.home.commonClient.net.response.scene.unit.ReCUnitPullBackResponse;
import com.home.commonClient.net.response.scene.unit.ReCUnitSkillFailedExResponse;
import com.home.commonClient.net.response.scene.unit.ReCUnitSkillFailedResponse;
import com.home.commonClient.net.response.scene.unit.RefreshOperationStateResponse;
import com.home.commonClient.net.response.scene.unit.RefreshSimpleUnitAttributeResponse;
import com.home.commonClient.net.response.scene.unit.RefreshSimpleUnitPosResponse;
import com.home.commonClient.net.response.scene.unit.RefreshUnitAttributesResponse;
import com.home.commonClient.net.response.scene.unit.RefreshUnitAvatarPartResponse;
import com.home.commonClient.net.response.scene.unit.RefreshUnitAvatarResponse;
import com.home.commonClient.net.response.scene.unit.RefreshUnitStatusResponse;
import com.home.commonClient.net.response.scene.unit.RemoveBulletResponse;
import com.home.commonClient.net.response.scene.unit.UnitAddBuffResponse;
import com.home.commonClient.net.response.scene.unit.UnitAddGroupTimeMaxPercentResponse;
import com.home.commonClient.net.response.scene.unit.UnitAddGroupTimeMaxValueResponse;
import com.home.commonClient.net.response.scene.unit.UnitAddGroupTimePassResponse;
import com.home.commonClient.net.response.scene.unit.UnitChatResponse;
import com.home.commonClient.net.response.scene.unit.UnitDeadResponse;
import com.home.commonClient.net.response.scene.unit.UnitDriveResponse;
import com.home.commonClient.net.response.scene.unit.UnitGetOffVehicleResponse;
import com.home.commonClient.net.response.scene.unit.UnitGetOnVehicleResponse;
import com.home.commonClient.net.response.scene.unit.UnitMoveDirResponse;
import com.home.commonClient.net.response.scene.unit.UnitMovePosListResponse;
import com.home.commonClient.net.response.scene.unit.UnitMovePosResponse;
import com.home.commonClient.net.response.scene.unit.UnitRefreshBuffLastNumResponse;
import com.home.commonClient.net.response.scene.unit.UnitRefreshBuffResponse;
import com.home.commonClient.net.response.scene.unit.UnitRemoveBuffResponse;
import com.home.commonClient.net.response.scene.unit.UnitRemoveGroupCDResponse;
import com.home.commonClient.net.response.scene.unit.UnitReviveResponse;
import com.home.commonClient.net.response.scene.unit.UnitSetPosDirResponse;
import com.home.commonClient.net.response.scene.unit.UnitSkillOverResponse;
import com.home.commonClient.net.response.scene.unit.UnitSpecialMoveResponse;
import com.home.commonClient.net.response.scene.unit.UnitStartCDsResponse;
import com.home.commonClient.net.response.scene.unit.UnitStopMoveResponse;
import com.home.commonClient.net.response.scene.unit.UnitSyncCommandResponse;
import com.home.commonClient.net.response.scene.unit.UnitUseSkillResponse;
import com.home.commonClient.net.response.scene.unit.building.BuildingBuildCompleteResponse;
import com.home.commonClient.net.response.scene.unit.building.BuildingCancelLevelUpResponse;
import com.home.commonClient.net.response.scene.unit.building.BuildingLevelUpingCompleteResponse;
import com.home.commonClient.net.response.scene.unit.building.BuildingStartLevelUpResponse;
import com.home.commonClient.net.response.social.ReQueryPlayerResponse;
import com.home.commonClient.net.response.social.ReSearchPlayerResponse;
import com.home.commonClient.net.response.social.ReUpdateRoleSocialDataOneResponse;
import com.home.commonClient.net.response.social.ReUpdateRoleSocialDataResponse;
import com.home.commonClient.net.response.social.chat.SendPlayerChatResponse;
import com.home.commonClient.net.response.social.friend.SendAddFriendBlackListResponse;
import com.home.commonClient.net.response.social.friend.SendAddFriendResponse;
import com.home.commonClient.net.response.social.friend.SendApplyAddFriendResponse;
import com.home.commonClient.net.response.social.friend.SendRemoveFriendBlackListResponse;
import com.home.commonClient.net.response.social.friend.SendRemoveFriendResponse;
import com.home.commonClient.net.response.system.CenterTransGameToClientResponse;
import com.home.commonClient.net.response.system.ClientHotfixConfigResponse;
import com.home.commonClient.net.response.system.DailyResponse;
import com.home.commonClient.net.response.system.GameTransGameToClientResponse;
import com.home.commonClient.net.response.system.ReceiveClientOfflineWorkResponse;
import com.home.commonClient.net.response.system.RefreshServerTimeResponse;
import com.home.commonClient.net.response.system.SendGameReceiptToClientResponse;
import com.home.commonClient.net.response.system.SendInfoCodeResponse;
import com.home.commonClient.net.response.system.SendInfoCodeWithArgsResponse;
import com.home.commonClient.net.response.system.SendInfoLogResponse;
import com.home.commonClient.net.response.system.SendWarningLogResponse;
import com.home.shine.data.BaseData;
import com.home.shine.tool.CreateDataFunc;
import com.home.shine.tool.DataMaker;

/** (generated by shine) */
public class GameResponseMaker extends DataMaker
{
	public GameResponseMaker()
	{
		offSet=GameResponseType.off;
		list=new CreateDataFunc[GameResponseType.count-offSet];
		list[GameResponseType.AOITowerRefresh-offSet]=this::createAOITowerRefreshResponse;
		list[GameResponseType.ActivityCompleteOnce-offSet]=this::createActivityCompleteOnceResponse;
		list[GameResponseType.ActivityReset-offSet]=this::createActivityResetResponse;
		list[GameResponseType.ActivitySwitch-offSet]=this::createActivitySwitchResponse;
		list[GameResponseType.AddBullet-offSet]=this::createAddBulletResponse;
		list[GameResponseType.AddMail-offSet]=this::createAddMailResponse;
		list[GameResponseType.AddReward-offSet]=this::createAddRewardResponse;
		list[GameResponseType.AddUnit-offSet]=this::createAddUnitResponse;
		list[GameResponseType.AttackDamage-offSet]=this::createAttackDamageResponse;
		list[GameResponseType.BuildingBuildComplete-offSet]=this::createBuildingBuildCompleteResponse;
		list[GameResponseType.BuildingCancelLevelUp-offSet]=this::createBuildingCancelLevelUpResponse;
		list[GameResponseType.BuildingLevelUpingComplete-offSet]=this::createBuildingLevelUpingCompleteResponse;
		list[GameResponseType.BuildingStartLevelUp-offSet]=this::createBuildingStartLevelUpResponse;
		list[GameResponseType.CenterTransGameToClient-offSet]=this::createCenterTransGameToClientResponse;
		list[GameResponseType.ChangeRoleName-offSet]=this::createChangeRoleNameResponse;
		list[GameResponseType.CharacterRefreshPartRoleShowData-offSet]=this::createCharacterRefreshPartRoleShowDataResponse;
		list[GameResponseType.ClientHotfix-offSet]=this::createClientHotfixResponse;
		list[GameResponseType.CreatePlayerSuccess-offSet]=this::createCreatePlayerSuccessResponse;
		list[GameResponseType.Daily-offSet]=this::createDailyResponse;
		list[GameResponseType.DeletePlayerSuccess-offSet]=this::createDeletePlayerSuccessResponse;
		list[GameResponseType.EnterNoneScene-offSet]=this::createEnterNoneSceneResponse;
		list[GameResponseType.EnterSceneFailed-offSet]=this::createEnterSceneFailedResponse;
		list[GameResponseType.EnterScene-offSet]=this::createEnterSceneResponse;
		list[GameResponseType.FrameSyncFrame-offSet]=this::createFrameSyncFrameResponse;
		list[GameResponseType.FrameSyncStart-offSet]=this::createFrameSyncStartResponse;
		list[GameResponseType.FuncAuctionRefreshSaleItem-offSet]=this::createFuncAuctionRefreshSaleItemResponse;
		list[GameResponseType.FuncAddItem-offSet]=this::createFuncAddItemResponse;
		list[GameResponseType.FuncAddOneItem-offSet]=this::createFuncAddOneItemResponse;
		list[GameResponseType.FuncAddOneItemNum-offSet]=this::createFuncAddOneItemNumResponse;
		list[GameResponseType.FuncCancelMatch-offSet]=this::createFuncCancelMatchResponse;
		list[GameResponseType.FuncMatchOver-offSet]=this::createFuncMatchOverResponse;
		list[GameResponseType.FuncMatchSuccess-offSet]=this::createFuncMatchSuccessResponse;
		list[GameResponseType.FuncMatchTimeOut-offSet]=this::createFuncMatchTimeOutResponse;
		list[GameResponseType.FuncReAddMatch-offSet]=this::createFuncReAddMatchResponse;
		list[GameResponseType.FuncReGetPageShow-offSet]=this::createFuncReGetPageShowResponse;
		list[GameResponseType.FuncReGetSelfPageShow-offSet]=this::createFuncReGetSelfPageShowResponse;
		list[GameResponseType.FuncRefreshRank-offSet]=this::createFuncRefreshRankResponse;
		list[GameResponseType.FuncRemoveItem-offSet]=this::createFuncRemoveItemResponse;
		list[GameResponseType.FuncRemoveOneItem-offSet]=this::createFuncRemoveOneItemResponse;
		list[GameResponseType.FuncResetRank-offSet]=this::createFuncResetRankResponse;
		list[GameResponseType.FuncS-offSet]=this::createFuncSResponse;
		list[GameResponseType.FuncSendAcceptMatch-offSet]=this::createFuncSendAcceptMatchResponse;
		list[GameResponseType.FuncSendCleanUpItem-offSet]=this::createFuncSendCleanUpItemResponse;
		list[GameResponseType.FuncSendMoveEquip-offSet]=this::createFuncSendMoveEquipResponse;
		list[GameResponseType.FuncSendPutOffEquip-offSet]=this::createFuncSendPutOffEquipResponse;
		list[GameResponseType.FuncSendPutOnEquip-offSet]=this::createFuncSendPutOnEquipResponse;
		list[GameResponseType.FuncStartMatch-offSet]=this::createFuncStartMatchResponse;
		list[GameResponseType.FuncUseItemResult-offSet]=this::createFuncUseItemResultResponse;
		list[GameResponseType.InitClient-offSet]=this::createInitClientResponse;
		list[GameResponseType.LeaveScene-offSet]=this::createLeaveSceneResponse;
		list[GameResponseType.LevelUp-offSet]=this::createLevelUpResponse;
		list[GameResponseType.MUnitAddBuff-offSet]=this::createMUnitAddBuffResponse;
		list[GameResponseType.MUnitAddGroupTimeMaxPercent-offSet]=this::createMUnitAddGroupTimeMaxPercentResponse;
		list[GameResponseType.MUnitAddGroupTimeMaxValue-offSet]=this::createMUnitAddGroupTimeMaxValueResponse;
		list[GameResponseType.MUnitAddGroupTimePass-offSet]=this::createMUnitAddGroupTimePassResponse;
		list[GameResponseType.MUnitAddSkill-offSet]=this::createMUnitAddSkillResponse;
		list[GameResponseType.MUnitRefreshAttributes-offSet]=this::createMUnitRefreshAttributesResponse;
		list[GameResponseType.MUnitRefreshAvatar-offSet]=this::createMUnitRefreshAvatarResponse;
		list[GameResponseType.MUnitRefreshAvatarPart-offSet]=this::createMUnitRefreshAvatarPartResponse;
		list[GameResponseType.MUnitRefreshBuffLastNum-offSet]=this::createMUnitRefreshBuffLastNumResponse;
		list[GameResponseType.MUnitRefreshBuff-offSet]=this::createMUnitRefreshBuffResponse;
		list[GameResponseType.MUnitRefreshStatus-offSet]=this::createMUnitRefreshStatusResponse;
		list[GameResponseType.MUnitRemoveBuff-offSet]=this::createMUnitRemoveBuffResponse;
		list[GameResponseType.MUnitRemoveGroupCD-offSet]=this::createMUnitRemoveGroupCDResponse;
		list[GameResponseType.MUnitRemoveSkill-offSet]=this::createMUnitRemoveSkillResponse;
		list[GameResponseType.MUnitStartCDs-offSet]=this::createMUnitStartCDsResponse;
		list[GameResponseType.PreEnterScene-offSet]=this::createPreEnterSceneResponse;
		list[GameResponseType.PreEnterSceneNext-offSet]=this::createPreEnterSceneNextResponse;
		list[GameResponseType.ReCUnitPullBack-offSet]=this::createReCUnitPullBackResponse;
		list[GameResponseType.ReCUnitSkillFailedEx-offSet]=this::createReCUnitSkillFailedExResponse;
		list[GameResponseType.ReCUnitSkillFailed-offSet]=this::createReCUnitSkillFailedResponse;
		list[GameResponseType.ReGetAllMail-offSet]=this::createReGetAllMailResponse;
		list[GameResponseType.RePlayerList-offSet]=this::createRePlayerListResponse;
		list[GameResponseType.ReQueryPlayer-offSet]=this::createReQueryPlayerResponse;
		list[GameResponseType.ReSearchPlayer-offSet]=this::createReSearchPlayerResponse;
		list[GameResponseType.ReUpdateRoleSocialData-offSet]=this::createReUpdateRoleSocialDataResponse;
		list[GameResponseType.ReUpdateRoleSocialDataOne-offSet]=this::createReUpdateRoleSocialDataOneResponse;
		list[GameResponseType.ReceiveClientOfflineWork-offSet]=this::createReceiveClientOfflineWorkResponse;
		list[GameResponseType.RefreshCurrency-offSet]=this::createRefreshCurrencyResponse;
		list[GameResponseType.RefreshCurrentLine-offSet]=this::createRefreshCurrentLineResponse;
		list[GameResponseType.RefreshExp-offSet]=this::createRefreshExpResponse;
		list[GameResponseType.RefreshFightForce-offSet]=this::createRefreshFightForceResponse;
		list[GameResponseType.RefreshMainGuideStep-offSet]=this::createRefreshMainGuideStepResponse;
		list[GameResponseType.RefreshOperationState-offSet]=this::createRefreshOperationStateResponse;
		list[GameResponseType.RefreshServerTime-offSet]=this::createRefreshServerTimeResponse;
		list[GameResponseType.RefreshTask-offSet]=this::createRefreshTaskResponse;
		list[GameResponseType.RefreshUnitAttributes-offSet]=this::createRefreshUnitAttributesResponse;
		list[GameResponseType.RefreshUnitAvatar-offSet]=this::createRefreshUnitAvatarResponse;
		list[GameResponseType.RefreshUnitAvatarPart-offSet]=this::createRefreshUnitAvatarPartResponse;
		list[GameResponseType.RefreshUnitStatus-offSet]=this::createRefreshUnitStatusResponse;
		list[GameResponseType.RemoveBullet-offSet]=this::createRemoveBulletResponse;
		list[GameResponseType.RemoveUnit-offSet]=this::createRemoveUnitResponse;
		list[GameResponseType.SceneRadio-offSet]=this::createSceneRadioResponse;
		list[GameResponseType.SceneS-offSet]=this::createSceneSResponse;
		list[GameResponseType.SendAcceptAchievement-offSet]=this::createSendAcceptAchievementResponse;
		list[GameResponseType.SendAcceptQuest-offSet]=this::createSendAcceptQuestResponse;
		list[GameResponseType.SendAchievementComplete-offSet]=this::createSendAchievementCompleteResponse;
		list[GameResponseType.SendAddFriendBlackList-offSet]=this::createSendAddFriendBlackListResponse;
		list[GameResponseType.SendAddFriend-offSet]=this::createSendAddFriendResponse;
		list[GameResponseType.SendApplyAddFriend-offSet]=this::createSendApplyAddFriendResponse;
		list[GameResponseType.SendBattleState-offSet]=this::createSendBattleStateResponse;
		list[GameResponseType.SendBindPlatform-offSet]=this::createSendBindPlatformResponse;
		list[GameResponseType.SendClearAllQuestByGM-offSet]=this::createSendClearAllQuestByGMResponse;
		list[GameResponseType.SendCommitQuest-offSet]=this::createSendCommitQuestResponse;
		list[GameResponseType.SendDeleteMail-offSet]=this::createSendDeleteMailResponse;
		list[GameResponseType.SendGameReceiptToClient-offSet]=this::createSendGameReceiptToClientResponse;
		list[GameResponseType.SendGetAchievementRewardSuccess-offSet]=this::createSendGetAchievementRewardSuccessResponse;
		list[GameResponseType.SendGiveUpQuest-offSet]=this::createSendGiveUpQuestResponse;
		list[GameResponseType.SendInfoCode-offSet]=this::createSendInfoCodeResponse;
		list[GameResponseType.SendInfoCodeWithArgs-offSet]=this::createSendInfoCodeWithArgsResponse;
		list[GameResponseType.SendQuestFailed-offSet]=this::createSendQuestFailedResponse;
		list[GameResponseType.SendRemoveAcceptQuest-offSet]=this::createSendRemoveAcceptQuestResponse;
		list[GameResponseType.SendRemoveFriendBlackList-offSet]=this::createSendRemoveFriendBlackListResponse;
		list[GameResponseType.SendRemoveFriend-offSet]=this::createSendRemoveFriendResponse;
		list[GameResponseType.SwitchGame-offSet]=this::createSwitchGameResponse;
		list[GameResponseType.TakeMailSuccess-offSet]=this::createTakeMailSuccessResponse;
		list[GameResponseType.UnitAddBuff-offSet]=this::createUnitAddBuffResponse;
		list[GameResponseType.UnitAddGroupTimeMaxPercent-offSet]=this::createUnitAddGroupTimeMaxPercentResponse;
		list[GameResponseType.UnitAddGroupTimeMaxValue-offSet]=this::createUnitAddGroupTimeMaxValueResponse;
		list[GameResponseType.UnitAddGroupTimePass-offSet]=this::createUnitAddGroupTimePassResponse;
		list[GameResponseType.UnitChat-offSet]=this::createUnitChatResponse;
		list[GameResponseType.UnitDead-offSet]=this::createUnitDeadResponse;
		list[GameResponseType.UnitMoveDir-offSet]=this::createUnitMoveDirResponse;
		list[GameResponseType.UnitMovePosList-offSet]=this::createUnitMovePosListResponse;
		list[GameResponseType.UnitMovePos-offSet]=this::createUnitMovePosResponse;
		list[GameResponseType.UnitPreBattleSure-offSet]=this::createUnitPreBattleSureResponse;
		list[GameResponseType.UnitRefreshBuffLastNum-offSet]=this::createUnitRefreshBuffLastNumResponse;
		list[GameResponseType.UnitRefreshBuff-offSet]=this::createUnitRefreshBuffResponse;
		list[GameResponseType.UnitRemoveBuff-offSet]=this::createUnitRemoveBuffResponse;
		list[GameResponseType.UnitRemoveGroupCD-offSet]=this::createUnitRemoveGroupCDResponse;
		list[GameResponseType.UnitRevive-offSet]=this::createUnitReviveResponse;
		list[GameResponseType.UnitS-offSet]=this::createUnitSResponse;
		list[GameResponseType.UnitSkillOver-offSet]=this::createUnitSkillOverResponse;
		list[GameResponseType.UnitSpecialMove-offSet]=this::createUnitSpecialMoveResponse;
		list[GameResponseType.UnitStartCDs-offSet]=this::createUnitStartCDsResponse;
		list[GameResponseType.UnitStopMove-offSet]=this::createUnitStopMoveResponse;
		list[GameResponseType.UnitSyncCommand-offSet]=this::createUnitSyncCommandResponse;
		list[GameResponseType.UnitUseSkill-offSet]=this::createUnitUseSkillResponse;
		list[GameResponseType.FuncSendPlayerJoinRoleGroup-offSet]=this::createFuncSendPlayerJoinRoleGroupResponse;
		list[GameResponseType.FuncSendPlayerLeaveRoleGroup-offSet]=this::createFuncSendPlayerLeaveRoleGroupResponse;
		list[GameResponseType.FuncSendRoleGroupAddMember-offSet]=this::createFuncSendRoleGroupAddMemberResponse;
		list[GameResponseType.FuncSendRoleGroupRemoveMember-offSet]=this::createFuncSendRoleGroupRemoveMemberResponse;
		list[GameResponseType.FuncPlayerRoleGroupS-offSet]=this::createFuncPlayerRoleGroupSResponse;
		list[GameResponseType.FuncSendAddApplyRoleGroup-offSet]=this::createFuncSendAddApplyRoleGroupResponse;
		list[GameResponseType.FuncSendHandleApplyResultRoleGroup-offSet]=this::createFuncSendHandleApplyResultRoleGroupResponse;
		list[GameResponseType.FuncSendHandleInviteResultRoleGroup-offSet]=this::createFuncSendHandleInviteResultRoleGroupResponse;
		list[GameResponseType.FuncSendInviteRoleGroup-offSet]=this::createFuncSendInviteRoleGroupResponse;
		list[GameResponseType.FuncReGetPageShowList-offSet]=this::createFuncReGetPageShowListResponse;
		list[GameResponseType.FuncRefeshTitleRoleGroup-offSet]=this::createFuncRefeshTitleRoleGroupResponse;
		list[GameResponseType.FuncSendChangeLeaderRoleGroup-offSet]=this::createFuncSendChangeLeaderRoleGroupResponse;
		list[GameResponseType.FuncRefreshSubsectionRank-offSet]=this::createFuncRefreshSubsectionRankResponse;
		list[GameResponseType.GameTransGameToClient-offSet]=this::createGameTransGameToClientResponse;
		list[GameResponseType.FuncSendHandleApplyResultToMember-offSet]=this::createFuncSendHandleApplyResultToMemberResponse;
		list[GameResponseType.FuncSendAddApplyRoleGroupSelf-offSet]=this::createFuncSendAddApplyRoleGroupSelfResponse;
		list[GameResponseType.FuncSendRoleGroupChange-offSet]=this::createFuncSendRoleGroupChangeResponse;
		list[GameResponseType.SendInfoLog-offSet]=this::createSendInfoLogResponse;
		list[GameResponseType.FuncReGetSubsectionPageShowList-offSet]=this::createFuncReGetSubsectionPageShowListResponse;
		list[GameResponseType.FuncSendChangeCanInviteInAbsRoleGroup-offSet]=this::createFuncSendChangeCanInviteInAbsRoleGroupResponse;
		list[GameResponseType.ClientHotfixConfig-offSet]=this::createClientHotfixConfigResponse;
		list[GameResponseType.FuncSendRoleGroupInfoLog-offSet]=this::createFuncSendRoleGroupInfoLogResponse;
		list[GameResponseType.FuncSendRoleGroupMemberChange-offSet]=this::createFuncSendRoleGroupMemberChangeResponse;
		list[GameResponseType.FuncReGetRoleGroupData-offSet]=this::createFuncReGetRoleGroupDataResponse;
		list[GameResponseType.UnitSetPosDir-offSet]=this::createUnitSetPosDirResponse;
		list[GameResponseType.AddFieldItemBagBind-offSet]=this::createAddFieldItemBagBindResponse;
		list[GameResponseType.RemoveFieldItemBagBind-offSet]=this::createRemoveFieldItemBagBindResponse;
		list[GameResponseType.RoleRefreshAttribute-offSet]=this::createRoleRefreshAttributeResponse;
		list[GameResponseType.RoleS-offSet]=this::createRoleSResponse;
		list[GameResponseType.SendPlayerChat-offSet]=this::createSendPlayerChatResponse;
		list[GameResponseType.AddPet-offSet]=this::createAddPetResponse;
		list[GameResponseType.FuncSendRoleGroupMemberRoleShowChange-offSet]=this::createFuncSendRoleGroupMemberRoleShowChangeResponse;
		list[GameResponseType.AddBindVisionUnit-offSet]=this::createAddBindVisionUnitResponse;
		list[GameResponseType.RemoveBindVisionUnit-offSet]=this::createRemoveBindVisionUnitResponse;
		list[GameResponseType.RefreshSimpleUnitAttribute-offSet]=this::createRefreshSimpleUnitAttributeResponse;
		list[GameResponseType.RefreshSimpleUnitPos-offSet]=this::createRefreshSimpleUnitPosResponse;
		list[GameResponseType.FuncSendMoveItem-offSet]=this::createFuncSendMoveItemResponse;
		list[GameResponseType.FuncRefreshItemGridNum-offSet]=this::createFuncRefreshItemGridNumResponse;
		list[GameResponseType.FuncRefreshRoleGroupRank-offSet]=this::createFuncRefreshRoleGroupRankResponse;
		list[GameResponseType.FuncResetRoleGroupRank-offSet]=this::createFuncResetRoleGroupRankResponse;
		list[GameResponseType.SendWarningLog-offSet]=this::createSendWarningLogResponse;
		list[GameResponseType.UnitGetOffVehicle-offSet]=this::createUnitGetOffVehicleResponse;
		list[GameResponseType.UnitGetOnVehicle-offSet]=this::createUnitGetOnVehicleResponse;
		list[GameResponseType.UnitDrive-offSet]=this::createUnitDriveResponse;
		list[GameResponseType.FuncClose-offSet]=this::createFuncCloseResponse;
		list[GameResponseType.FuncOpen-offSet]=this::createFuncOpenResponse;
		list[GameResponseType.FuncAuctionAddSaleItem-offSet]=this::createFuncAuctionAddSaleItemResponse;
		list[GameResponseType.FuncAuctionReQuery-offSet]=this::createFuncAuctionReQueryResponse;
		list[GameResponseType.MUnitS-offSet]=this::createMUnitSResponse;
		list[GameResponseType.RefreshPetIsWorking-offSet]=this::createRefreshPetIsWorkingResponse;
		list[GameResponseType.FuncReGetAuctionItemSuggestPrice-offSet]=this::createFuncReGetAuctionItemSuggestPriceResponse;
		list[GameResponseType.RemovePet-offSet]=this::createRemovePetResponse;
		list[GameResponseType.FuncAuctionRemoveSaleItem-offSet]=this::createFuncAuctionRemoveSaleItemResponse;
		list[GameResponseType.AttackDamageOne-offSet]=this::createAttackDamageOneResponse;
	}
	
	private BaseData createCreatePlayerSuccessResponse()
	{
		return new CreatePlayerSuccessResponse();
	}
	
	private BaseData createInitClientResponse()
	{
		return new InitClientResponse();
	}
	
	private BaseData createRePlayerListResponse()
	{
		return new RePlayerListResponse();
	}
	
	private BaseData createAddUnitResponse()
	{
		return new AddUnitResponse();
	}
	
	private BaseData createEnterSceneFailedResponse()
	{
		return new EnterSceneFailedResponse();
	}
	
	private BaseData createEnterSceneResponse()
	{
		return new EnterSceneResponse();
	}
	
	private BaseData createPreEnterSceneResponse()
	{
		return new PreEnterSceneResponse();
	}
	
	private BaseData createRemoveUnitResponse()
	{
		return new RemoveUnitResponse();
	}
	
	private BaseData createSceneRadioResponse()
	{
		return new SceneRadioResponse();
	}
	
	private BaseData createAttackDamageResponse()
	{
		return new AttackDamageResponse();
	}
	
	private BaseData createRefreshUnitAttributesResponse()
	{
		return new RefreshUnitAttributesResponse();
	}
	
	private BaseData createRefreshUnitStatusResponse()
	{
		return new RefreshUnitStatusResponse();
	}
	
	private BaseData createUnitDeadResponse()
	{
		return new UnitDeadResponse();
	}
	
	private BaseData createUnitReviveResponse()
	{
		return new UnitReviveResponse();
	}
	
	private BaseData createUnitUseSkillResponse()
	{
		return new UnitUseSkillResponse();
	}
	
	private BaseData createUnitSpecialMoveResponse()
	{
		return new UnitSpecialMoveResponse();
	}
	
	private BaseData createAddBulletResponse()
	{
		return new AddBulletResponse();
	}
	
	private BaseData createMUnitAddBuffResponse()
	{
		return new MUnitAddBuffResponse();
	}
	
	private BaseData createMUnitAddGroupTimePassResponse()
	{
		return new MUnitAddGroupTimePassResponse();
	}
	
	private BaseData createMUnitRefreshAttributesResponse()
	{
		return new MUnitRefreshAttributesResponse();
	}
	
	private BaseData createMUnitRefreshBuffResponse()
	{
		return new MUnitRefreshBuffResponse();
	}
	
	private BaseData createMUnitRefreshStatusResponse()
	{
		return new MUnitRefreshStatusResponse();
	}
	
	private BaseData createMUnitRemoveBuffResponse()
	{
		return new MUnitRemoveBuffResponse();
	}
	
	private BaseData createMUnitRemoveGroupCDResponse()
	{
		return new MUnitRemoveGroupCDResponse();
	}
	
	private BaseData createMUnitStartCDsResponse()
	{
		return new MUnitStartCDsResponse();
	}
	
	private BaseData createRemoveBulletResponse()
	{
		return new RemoveBulletResponse();
	}
	
	private BaseData createUnitAddBuffResponse()
	{
		return new UnitAddBuffResponse();
	}
	
	private BaseData createUnitRefreshBuffResponse()
	{
		return new UnitRefreshBuffResponse();
	}
	
	private BaseData createUnitRemoveBuffResponse()
	{
		return new UnitRemoveBuffResponse();
	}
	
	private BaseData createSendBattleStateResponse()
	{
		return new SendBattleStateResponse();
	}
	
	private BaseData createLeaveSceneResponse()
	{
		return new LeaveSceneResponse();
	}
	
	private BaseData createLevelUpResponse()
	{
		return new LevelUpResponse();
	}
	
	private BaseData createRefreshExpResponse()
	{
		return new RefreshExpResponse();
	}
	
	private BaseData createRefreshCurrencyResponse()
	{
		return new RefreshCurrencyResponse();
	}
	
	private BaseData createSendInfoCodeResponse()
	{
		return new SendInfoCodeResponse();
	}
	
	private BaseData createDailyResponse()
	{
		return new DailyResponse();
	}
	
	private BaseData createUnitChatResponse()
	{
		return new UnitChatResponse();
	}
	
	private BaseData createMUnitAddGroupTimeMaxPercentResponse()
	{
		return new MUnitAddGroupTimeMaxPercentResponse();
	}
	
	private BaseData createMUnitAddGroupTimeMaxValueResponse()
	{
		return new MUnitAddGroupTimeMaxValueResponse();
	}
	
	private BaseData createMUnitRefreshAvatarResponse()
	{
		return new MUnitRefreshAvatarResponse();
	}
	
	private BaseData createRefreshUnitAvatarResponse()
	{
		return new RefreshUnitAvatarResponse();
	}
	
	private BaseData createRefreshUnitAvatarPartResponse()
	{
		return new RefreshUnitAvatarPartResponse();
	}
	
	private BaseData createMUnitRefreshAvatarPartResponse()
	{
		return new MUnitRefreshAvatarPartResponse();
	}
	
	private BaseData createFuncReGetPageShowResponse()
	{
		return new FuncReGetPageShowResponse();
	}
	
	private BaseData createFuncRefreshRankResponse()
	{
		return new FuncRefreshRankResponse();
	}
	
	private BaseData createFuncResetRankResponse()
	{
		return new FuncResetRankResponse();
	}
	
	private BaseData createFuncCancelMatchResponse()
	{
		return new FuncCancelMatchResponse();
	}
	
	private BaseData createFuncMatchOverResponse()
	{
		return new FuncMatchOverResponse();
	}
	
	private BaseData createFuncMatchSuccessResponse()
	{
		return new FuncMatchSuccessResponse();
	}
	
	private BaseData createFuncSendAcceptMatchResponse()
	{
		return new FuncSendAcceptMatchResponse();
	}
	
	private BaseData createFuncStartMatchResponse()
	{
		return new FuncStartMatchResponse();
	}
	
	private BaseData createChangeRoleNameResponse()
	{
		return new ChangeRoleNameResponse();
	}
	
	private BaseData createMUnitRefreshBuffLastNumResponse()
	{
		return new MUnitRefreshBuffLastNumResponse();
	}
	
	private BaseData createUnitRefreshBuffLastNumResponse()
	{
		return new UnitRefreshBuffLastNumResponse();
	}
	
	private BaseData createReCUnitSkillFailedResponse()
	{
		return new ReCUnitSkillFailedResponse();
	}
	
	private BaseData createUnitSyncCommandResponse()
	{
		return new UnitSyncCommandResponse();
	}
	
	private BaseData createUnitPreBattleSureResponse()
	{
		return new UnitPreBattleSureResponse();
	}
	
	private BaseData createFrameSyncStartResponse()
	{
		return new FrameSyncStartResponse();
	}
	
	private BaseData createFrameSyncFrameResponse()
	{
		return new FrameSyncFrameResponse();
	}
	
	private BaseData createReCUnitSkillFailedExResponse()
	{
		return new ReCUnitSkillFailedExResponse();
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
	
	private BaseData createUnitRemoveGroupCDResponse()
	{
		return new UnitRemoveGroupCDResponse();
	}
	
	private BaseData createUnitStartCDsResponse()
	{
		return new UnitStartCDsResponse();
	}
	
	private BaseData createUnitStopMoveResponse()
	{
		return new UnitStopMoveResponse();
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
	
	private BaseData createReCUnitPullBackResponse()
	{
		return new ReCUnitPullBackResponse();
	}
	
	private BaseData createUnitSkillOverResponse()
	{
		return new UnitSkillOverResponse();
	}
	
	private BaseData createEnterNoneSceneResponse()
	{
		return new EnterNoneSceneResponse();
	}
	
	private BaseData createCenterTransGameToClientResponse()
	{
		return new CenterTransGameToClientResponse();
	}
	
	private BaseData createSwitchGameResponse()
	{
		return new SwitchGameResponse();
	}
	
	private BaseData createCharacterRefreshPartRoleShowDataResponse()
	{
		return new CharacterRefreshPartRoleShowDataResponse();
	}
	
	private BaseData createPreEnterSceneNextResponse()
	{
		return new PreEnterSceneNextResponse();
	}
	
	private BaseData createFuncReAddMatchResponse()
	{
		return new FuncReAddMatchResponse();
	}
	
	private BaseData createFuncMatchTimeOutResponse()
	{
		return new FuncMatchTimeOutResponse();
	}
	
	private BaseData createRefreshCurrentLineResponse()
	{
		return new RefreshCurrentLineResponse();
	}
	
	private BaseData createDeletePlayerSuccessResponse()
	{
		return new DeletePlayerSuccessResponse();
	}
	
	private BaseData createFuncRemoveItemResponse()
	{
		return new FuncRemoveItemResponse();
	}
	
	private BaseData createFuncAddItemResponse()
	{
		return new FuncAddItemResponse();
	}
	
	private BaseData createFuncRemoveOneItemResponse()
	{
		return new FuncRemoveOneItemResponse();
	}
	
	private BaseData createFuncAddOneItemResponse()
	{
		return new FuncAddOneItemResponse();
	}
	
	private BaseData createFuncSResponse()
	{
		return new FuncSResponse();
	}
	
	private BaseData createFuncSendCleanUpItemResponse()
	{
		return new FuncSendCleanUpItemResponse();
	}
	
	private BaseData createFuncAddOneItemNumResponse()
	{
		return new FuncAddOneItemNumResponse();
	}
	
	private BaseData createSendInfoCodeWithArgsResponse()
	{
		return new SendInfoCodeWithArgsResponse();
	}
	
	private BaseData createRefreshTaskResponse()
	{
		return new RefreshTaskResponse();
	}
	
	private BaseData createSendAcceptQuestResponse()
	{
		return new SendAcceptQuestResponse();
	}
	
	private BaseData createSendCommitQuestResponse()
	{
		return new SendCommitQuestResponse();
	}
	
	private BaseData createSendQuestFailedResponse()
	{
		return new SendQuestFailedResponse();
	}
	
	private BaseData createSendGiveUpQuestResponse()
	{
		return new SendGiveUpQuestResponse();
	}
	
	private BaseData createSendClearAllQuestByGMResponse()
	{
		return new SendClearAllQuestByGMResponse();
	}
	
	private BaseData createFuncReGetSelfPageShowResponse()
	{
		return new FuncReGetSelfPageShowResponse();
	}
	
	private BaseData createSendDeleteMailResponse()
	{
		return new SendDeleteMailResponse();
	}
	
	private BaseData createAddMailResponse()
	{
		return new AddMailResponse();
	}
	
	private BaseData createSendAchievementCompleteResponse()
	{
		return new SendAchievementCompleteResponse();
	}
	
	private BaseData createSendGetAchievementRewardSuccessResponse()
	{
		return new SendGetAchievementRewardSuccessResponse();
	}
	
	private BaseData createFuncUseItemResultResponse()
	{
		return new FuncUseItemResultResponse();
	}
	
	private BaseData createSendAddFriendResponse()
	{
		return new SendAddFriendResponse();
	}
	
	private BaseData createSendRemoveFriendResponse()
	{
		return new SendRemoveFriendResponse();
	}
	
	private BaseData createActivityResetResponse()
	{
		return new ActivityResetResponse();
	}
	
	private BaseData createActivityCompleteOnceResponse()
	{
		return new ActivityCompleteOnceResponse();
	}
	
	private BaseData createSendApplyAddFriendResponse()
	{
		return new SendApplyAddFriendResponse();
	}
	
	private BaseData createReUpdateRoleSocialDataResponse()
	{
		return new ReUpdateRoleSocialDataResponse();
	}
	
	private BaseData createSendRemoveFriendBlackListResponse()
	{
		return new SendRemoveFriendBlackListResponse();
	}
	
	private BaseData createSendAddFriendBlackListResponse()
	{
		return new SendAddFriendBlackListResponse();
	}
	
	private BaseData createRefreshServerTimeResponse()
	{
		return new RefreshServerTimeResponse();
	}
	
	private BaseData createSendBindPlatformResponse()
	{
		return new SendBindPlatformResponse();
	}
	
	private BaseData createAddRewardResponse()
	{
		return new AddRewardResponse();
	}
	
	private BaseData createSceneSResponse()
	{
		return new SceneSResponse();
	}
	
	private BaseData createClientHotfixResponse()
	{
		return new ClientHotfixResponse();
	}
	
	private BaseData createReSearchPlayerResponse()
	{
		return new ReSearchPlayerResponse();
	}
	
	private BaseData createRefreshMainGuideStepResponse()
	{
		return new RefreshMainGuideStepResponse();
	}
	
	private BaseData createReUpdateRoleSocialDataOneResponse()
	{
		return new ReUpdateRoleSocialDataOneResponse();
	}
	
	private BaseData createReceiveClientOfflineWorkResponse()
	{
		return new ReceiveClientOfflineWorkResponse();
	}
	
	private BaseData createRefreshFightForceResponse()
	{
		return new RefreshFightForceResponse();
	}
	
	private BaseData createReQueryPlayerResponse()
	{
		return new ReQueryPlayerResponse();
	}
	
	private BaseData createReGetAllMailResponse()
	{
		return new ReGetAllMailResponse();
	}
	
	private BaseData createTakeMailSuccessResponse()
	{
		return new TakeMailSuccessResponse();
	}
	
	private BaseData createAOITowerRefreshResponse()
	{
		return new AOITowerRefreshResponse();
	}
	
	private BaseData createSendAcceptAchievementResponse()
	{
		return new SendAcceptAchievementResponse();
	}
	
	private BaseData createSendGameReceiptToClientResponse()
	{
		return new SendGameReceiptToClientResponse();
	}
	
	private BaseData createFuncSendMoveEquipResponse()
	{
		return new FuncSendMoveEquipResponse();
	}
	
	private BaseData createFuncSendPutOffEquipResponse()
	{
		return new FuncSendPutOffEquipResponse();
	}
	
	private BaseData createFuncSendPutOnEquipResponse()
	{
		return new FuncSendPutOnEquipResponse();
	}
	
	private BaseData createActivitySwitchResponse()
	{
		return new ActivitySwitchResponse();
	}
	
	private BaseData createSendRemoveAcceptQuestResponse()
	{
		return new SendRemoveAcceptQuestResponse();
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
	
	private BaseData createUnitSResponse()
	{
		return new UnitSResponse();
	}
	
	private BaseData createMUnitAddSkillResponse()
	{
		return new MUnitAddSkillResponse();
	}
	
	private BaseData createMUnitRemoveSkillResponse()
	{
		return new MUnitRemoveSkillResponse();
	}
	
	private BaseData createRefreshOperationStateResponse()
	{
		return new RefreshOperationStateResponse();
	}
	
	private BaseData createFuncSendPlayerJoinRoleGroupResponse()
	{
		return new FuncSendPlayerJoinRoleGroupResponse();
	}
	
	private BaseData createFuncSendPlayerLeaveRoleGroupResponse()
	{
		return new FuncSendPlayerLeaveRoleGroupResponse();
	}
	
	private BaseData createFuncSendRoleGroupAddMemberResponse()
	{
		return new FuncSendRoleGroupAddMemberResponse();
	}
	
	private BaseData createFuncSendRoleGroupRemoveMemberResponse()
	{
		return new FuncSendRoleGroupRemoveMemberResponse();
	}
	
	private BaseData createFuncPlayerRoleGroupSResponse()
	{
		return new FuncPlayerRoleGroupSResponse();
	}
	
	private BaseData createFuncSendAddApplyRoleGroupResponse()
	{
		return new FuncSendAddApplyRoleGroupResponse();
	}
	
	private BaseData createFuncSendHandleApplyResultRoleGroupResponse()
	{
		return new FuncSendHandleApplyResultRoleGroupResponse();
	}
	
	private BaseData createFuncSendHandleInviteResultRoleGroupResponse()
	{
		return new FuncSendHandleInviteResultRoleGroupResponse();
	}
	
	private BaseData createFuncSendInviteRoleGroupResponse()
	{
		return new FuncSendInviteRoleGroupResponse();
	}
	
	private BaseData createFuncRefeshTitleRoleGroupResponse()
	{
		return new FuncRefeshTitleRoleGroupResponse();
	}
	
	private BaseData createFuncSendChangeLeaderRoleGroupResponse()
	{
		return new FuncSendChangeLeaderRoleGroupResponse();
	}
	
	private BaseData createGameTransGameToClientResponse()
	{
		return new GameTransGameToClientResponse();
	}
	
	private BaseData createFuncSendHandleApplyResultToMemberResponse()
	{
		return new FuncSendHandleApplyResultToMemberResponse();
	}
	
	private BaseData createFuncSendAddApplyRoleGroupSelfResponse()
	{
		return new FuncSendAddApplyRoleGroupSelfResponse();
	}
	
	private BaseData createFuncSendRoleGroupChangeResponse()
	{
		return new FuncSendRoleGroupChangeResponse();
	}
	
	private BaseData createSendInfoLogResponse()
	{
		return new SendInfoLogResponse();
	}
	
	private BaseData createFuncSendChangeCanInviteInAbsRoleGroupResponse()
	{
		return new FuncSendChangeCanInviteInAbsRoleGroupResponse();
	}
	
	private BaseData createClientHotfixConfigResponse()
	{
		return new ClientHotfixConfigResponse();
	}
	
	private BaseData createFuncSendRoleGroupInfoLogResponse()
	{
		return new FuncSendRoleGroupInfoLogResponse();
	}
	
	private BaseData createFuncSendRoleGroupMemberChangeResponse()
	{
		return new FuncSendRoleGroupMemberChangeResponse();
	}
	
	private BaseData createFuncReGetRoleGroupDataResponse()
	{
		return new FuncReGetRoleGroupDataResponse();
	}
	
	private BaseData createUnitSetPosDirResponse()
	{
		return new UnitSetPosDirResponse();
	}
	
	private BaseData createAddFieldItemBagBindResponse()
	{
		return new AddFieldItemBagBindResponse();
	}
	
	private BaseData createRemoveFieldItemBagBindResponse()
	{
		return new RemoveFieldItemBagBindResponse();
	}
	
	private BaseData createRoleRefreshAttributeResponse()
	{
		return new RoleRefreshAttributeResponse();
	}
	
	private BaseData createRoleSResponse()
	{
		return new RoleSResponse();
	}
	
	private BaseData createSendPlayerChatResponse()
	{
		return new SendPlayerChatResponse();
	}
	
	private BaseData createFuncSendRoleGroupMemberRoleShowChangeResponse()
	{
		return new FuncSendRoleGroupMemberRoleShowChangeResponse();
	}
	
	private BaseData createAddBindVisionUnitResponse()
	{
		return new AddBindVisionUnitResponse();
	}
	
	private BaseData createRemoveBindVisionUnitResponse()
	{
		return new RemoveBindVisionUnitResponse();
	}
	
	private BaseData createRefreshSimpleUnitAttributeResponse()
	{
		return new RefreshSimpleUnitAttributeResponse();
	}
	
	private BaseData createRefreshSimpleUnitPosResponse()
	{
		return new RefreshSimpleUnitPosResponse();
	}
	
	private BaseData createFuncSendMoveItemResponse()
	{
		return new FuncSendMoveItemResponse();
	}
	
	private BaseData createFuncRefreshItemGridNumResponse()
	{
		return new FuncRefreshItemGridNumResponse();
	}
	
	private BaseData createFuncRefreshRoleGroupRankResponse()
	{
		return new FuncRefreshRoleGroupRankResponse();
	}
	
	private BaseData createFuncResetRoleGroupRankResponse()
	{
		return new FuncResetRoleGroupRankResponse();
	}
	
	private BaseData createSendWarningLogResponse()
	{
		return new SendWarningLogResponse();
	}
	
	private BaseData createUnitGetOffVehicleResponse()
	{
		return new UnitGetOffVehicleResponse();
	}
	
	private BaseData createUnitGetOnVehicleResponse()
	{
		return new UnitGetOnVehicleResponse();
	}
	
	private BaseData createUnitDriveResponse()
	{
		return new UnitDriveResponse();
	}
	
	private BaseData createFuncCloseResponse()
	{
		return new FuncCloseResponse();
	}
	
	private BaseData createFuncOpenResponse()
	{
		return new FuncOpenResponse();
	}
	
	private BaseData createFuncAuctionAddSaleItemResponse()
	{
		return new FuncAuctionAddSaleItemResponse();
	}
	
	private BaseData createFuncAuctionReQueryResponse()
	{
		return new FuncAuctionReQueryResponse();
	}
	
	private BaseData createFuncReGetAuctionItemSuggestPriceResponse()
	{
		return new FuncReGetAuctionItemSuggestPriceResponse();
	}
	
	private BaseData createFuncAuctionRemoveSaleItemResponse()
	{
		return new FuncAuctionRemoveSaleItemResponse();
	}
	
	private BaseData createFuncAuctionRefreshSaleItemResponse()
	{
		return new FuncAuctionRefreshSaleItemResponse();
	}
	
	private BaseData createFuncReGetPageShowListResponse()
	{
		return new FuncReGetPageShowListResponse();
	}
	
	private BaseData createFuncRefreshSubsectionRankResponse()
	{
		return new FuncRefreshSubsectionRankResponse();
	}
	
	private BaseData createFuncReGetSubsectionPageShowListResponse()
	{
		return new FuncReGetSubsectionPageShowListResponse();
	}
	
	private BaseData createAddPetResponse()
	{
		return new AddPetResponse();
	}
	
	private BaseData createMUnitSResponse()
	{
		return new MUnitSResponse();
	}
	
	private BaseData createRefreshPetIsWorkingResponse()
	{
		return new RefreshPetIsWorkingResponse();
	}
	
	private BaseData createRemovePetResponse()
	{
		return new RemovePetResponse();
	}
	
	private BaseData createAttackDamageOneResponse()
	{
		return new AttackDamageOneResponse();
	}
	
}
