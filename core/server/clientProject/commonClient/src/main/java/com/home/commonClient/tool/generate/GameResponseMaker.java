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
import com.home.commonClient.net.response.func.rank.subsection.FuncRefreshSubsectionIndexResponse;
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
import com.home.commonClient.net.response.login.SwitchSceneResponse;
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
import com.home.commonClient.net.response.scene.EnterNoneSceneResponse;
import com.home.commonClient.net.response.scene.EnterSceneFailedResponse;
import com.home.commonClient.net.response.scene.EnterSceneResponse;
import com.home.commonClient.net.response.scene.LeaveSceneResponse;
import com.home.commonClient.net.response.scene.PreEnterSceneNextResponse;
import com.home.commonClient.net.response.scene.PreEnterSceneResponse;
import com.home.commonClient.net.response.scene.RefreshCurrentLineResponse;
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
		list[GameResponseType.ActivityCompleteOnce-offSet]=this::createActivityCompleteOnceResponse;
		list[GameResponseType.ActivityReset-offSet]=this::createActivityResetResponse;
		list[GameResponseType.ActivitySwitch-offSet]=this::createActivitySwitchResponse;
		list[GameResponseType.AddMail-offSet]=this::createAddMailResponse;
		list[GameResponseType.AddPet-offSet]=this::createAddPetResponse;
		list[GameResponseType.AddReward-offSet]=this::createAddRewardResponse;
		list[GameResponseType.CenterTransGameToClient-offSet]=this::createCenterTransGameToClientResponse;
		list[GameResponseType.ChangeRoleName-offSet]=this::createChangeRoleNameResponse;
		list[GameResponseType.ClientHotfixConfig-offSet]=this::createClientHotfixConfigResponse;
		list[GameResponseType.ClientHotfix-offSet]=this::createClientHotfixResponse;
		list[GameResponseType.CreatePlayerSuccess-offSet]=this::createCreatePlayerSuccessResponse;
		list[GameResponseType.Daily-offSet]=this::createDailyResponse;
		list[GameResponseType.DeletePlayerSuccess-offSet]=this::createDeletePlayerSuccessResponse;
		list[GameResponseType.EnterNoneScene-offSet]=this::createEnterNoneSceneResponse;
		list[GameResponseType.EnterSceneFailed-offSet]=this::createEnterSceneFailedResponse;
		list[GameResponseType.EnterScene-offSet]=this::createEnterSceneResponse;
		list[GameResponseType.FuncAddItem-offSet]=this::createFuncAddItemResponse;
		list[GameResponseType.FuncAddOneItem-offSet]=this::createFuncAddOneItemResponse;
		list[GameResponseType.FuncAddOneItemNum-offSet]=this::createFuncAddOneItemNumResponse;
		list[GameResponseType.FuncAuctionAddSaleItem-offSet]=this::createFuncAuctionAddSaleItemResponse;
		list[GameResponseType.FuncAuctionReQuery-offSet]=this::createFuncAuctionReQueryResponse;
		list[GameResponseType.FuncAuctionRefreshSaleItem-offSet]=this::createFuncAuctionRefreshSaleItemResponse;
		list[GameResponseType.FuncAuctionRemoveSaleItem-offSet]=this::createFuncAuctionRemoveSaleItemResponse;
		list[GameResponseType.FuncCancelMatch-offSet]=this::createFuncCancelMatchResponse;
		list[GameResponseType.FuncClose-offSet]=this::createFuncCloseResponse;
		list[GameResponseType.FuncMatchOver-offSet]=this::createFuncMatchOverResponse;
		list[GameResponseType.FuncMatchSuccess-offSet]=this::createFuncMatchSuccessResponse;
		list[GameResponseType.FuncMatchTimeOut-offSet]=this::createFuncMatchTimeOutResponse;
		list[GameResponseType.FuncOpen-offSet]=this::createFuncOpenResponse;
		list[GameResponseType.FuncPlayerRoleGroupS-offSet]=this::createFuncPlayerRoleGroupSResponse;
		list[GameResponseType.FuncReAddMatch-offSet]=this::createFuncReAddMatchResponse;
		list[GameResponseType.FuncReGetAuctionItemSuggestPrice-offSet]=this::createFuncReGetAuctionItemSuggestPriceResponse;
		list[GameResponseType.FuncReGetPageShowList-offSet]=this::createFuncReGetPageShowListResponse;
		list[GameResponseType.FuncReGetPageShow-offSet]=this::createFuncReGetPageShowResponse;
		list[GameResponseType.FuncReGetRoleGroupData-offSet]=this::createFuncReGetRoleGroupDataResponse;
		list[GameResponseType.FuncReGetSelfPageShow-offSet]=this::createFuncReGetSelfPageShowResponse;
		list[GameResponseType.FuncReGetSubsectionPageShowList-offSet]=this::createFuncReGetSubsectionPageShowListResponse;
		list[GameResponseType.FuncRefeshTitleRoleGroup-offSet]=this::createFuncRefeshTitleRoleGroupResponse;
		list[GameResponseType.FuncRefreshItemGridNum-offSet]=this::createFuncRefreshItemGridNumResponse;
		list[GameResponseType.FuncRefreshRank-offSet]=this::createFuncRefreshRankResponse;
		list[GameResponseType.FuncRefreshRoleGroupRank-offSet]=this::createFuncRefreshRoleGroupRankResponse;
		list[GameResponseType.FuncRefreshSubsectionRank-offSet]=this::createFuncRefreshSubsectionRankResponse;
		list[GameResponseType.FuncRemoveItem-offSet]=this::createFuncRemoveItemResponse;
		list[GameResponseType.FuncRemoveOneItem-offSet]=this::createFuncRemoveOneItemResponse;
		list[GameResponseType.FuncResetRank-offSet]=this::createFuncResetRankResponse;
		list[GameResponseType.FuncResetRoleGroupRank-offSet]=this::createFuncResetRoleGroupRankResponse;
		list[GameResponseType.FuncS-offSet]=this::createFuncSResponse;
		list[GameResponseType.FuncSendAcceptMatch-offSet]=this::createFuncSendAcceptMatchResponse;
		list[GameResponseType.FuncSendAddApplyRoleGroup-offSet]=this::createFuncSendAddApplyRoleGroupResponse;
		list[GameResponseType.FuncSendAddApplyRoleGroupSelf-offSet]=this::createFuncSendAddApplyRoleGroupSelfResponse;
		list[GameResponseType.FuncSendChangeCanInviteInAbsRoleGroup-offSet]=this::createFuncSendChangeCanInviteInAbsRoleGroupResponse;
		list[GameResponseType.FuncSendChangeLeaderRoleGroup-offSet]=this::createFuncSendChangeLeaderRoleGroupResponse;
		list[GameResponseType.FuncSendCleanUpItem-offSet]=this::createFuncSendCleanUpItemResponse;
		list[GameResponseType.FuncSendHandleApplyResultRoleGroup-offSet]=this::createFuncSendHandleApplyResultRoleGroupResponse;
		list[GameResponseType.FuncSendHandleApplyResultToMember-offSet]=this::createFuncSendHandleApplyResultToMemberResponse;
		list[GameResponseType.FuncSendHandleInviteResultRoleGroup-offSet]=this::createFuncSendHandleInviteResultRoleGroupResponse;
		list[GameResponseType.FuncSendInviteRoleGroup-offSet]=this::createFuncSendInviteRoleGroupResponse;
		list[GameResponseType.FuncSendMoveEquip-offSet]=this::createFuncSendMoveEquipResponse;
		list[GameResponseType.FuncSendMoveItem-offSet]=this::createFuncSendMoveItemResponse;
		list[GameResponseType.FuncSendPlayerJoinRoleGroup-offSet]=this::createFuncSendPlayerJoinRoleGroupResponse;
		list[GameResponseType.FuncSendPlayerLeaveRoleGroup-offSet]=this::createFuncSendPlayerLeaveRoleGroupResponse;
		list[GameResponseType.FuncSendPutOffEquip-offSet]=this::createFuncSendPutOffEquipResponse;
		list[GameResponseType.FuncSendPutOnEquip-offSet]=this::createFuncSendPutOnEquipResponse;
		list[GameResponseType.FuncSendRoleGroupAddMember-offSet]=this::createFuncSendRoleGroupAddMemberResponse;
		list[GameResponseType.FuncSendRoleGroupChange-offSet]=this::createFuncSendRoleGroupChangeResponse;
		list[GameResponseType.FuncSendRoleGroupInfoLog-offSet]=this::createFuncSendRoleGroupInfoLogResponse;
		list[GameResponseType.FuncSendRoleGroupMemberChange-offSet]=this::createFuncSendRoleGroupMemberChangeResponse;
		list[GameResponseType.FuncSendRoleGroupMemberRoleShowChange-offSet]=this::createFuncSendRoleGroupMemberRoleShowChangeResponse;
		list[GameResponseType.FuncSendRoleGroupRemoveMember-offSet]=this::createFuncSendRoleGroupRemoveMemberResponse;
		list[GameResponseType.FuncStartMatch-offSet]=this::createFuncStartMatchResponse;
		list[GameResponseType.FuncUseItemResult-offSet]=this::createFuncUseItemResultResponse;
		list[GameResponseType.GameTransGameToClient-offSet]=this::createGameTransGameToClientResponse;
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
		list[GameResponseType.MUnitS-offSet]=this::createMUnitSResponse;
		list[GameResponseType.MUnitStartCDs-offSet]=this::createMUnitStartCDsResponse;
		list[GameResponseType.PreEnterScene-offSet]=this::createPreEnterSceneResponse;
		list[GameResponseType.PreEnterSceneNext-offSet]=this::createPreEnterSceneNextResponse;
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
		list[GameResponseType.RefreshPetIsWorking-offSet]=this::createRefreshPetIsWorkingResponse;
		list[GameResponseType.RefreshServerTime-offSet]=this::createRefreshServerTimeResponse;
		list[GameResponseType.RefreshTask-offSet]=this::createRefreshTaskResponse;
		list[GameResponseType.RemovePet-offSet]=this::createRemovePetResponse;
		list[GameResponseType.SendAcceptAchievement-offSet]=this::createSendAcceptAchievementResponse;
		list[GameResponseType.SendAcceptQuest-offSet]=this::createSendAcceptQuestResponse;
		list[GameResponseType.SendAchievementComplete-offSet]=this::createSendAchievementCompleteResponse;
		list[GameResponseType.SendAddFriendBlackList-offSet]=this::createSendAddFriendBlackListResponse;
		list[GameResponseType.SendAddFriend-offSet]=this::createSendAddFriendResponse;
		list[GameResponseType.SendApplyAddFriend-offSet]=this::createSendApplyAddFriendResponse;
		list[GameResponseType.SendBindPlatform-offSet]=this::createSendBindPlatformResponse;
		list[GameResponseType.SendClearAllQuestByGM-offSet]=this::createSendClearAllQuestByGMResponse;
		list[GameResponseType.SendCommitQuest-offSet]=this::createSendCommitQuestResponse;
		list[GameResponseType.SendDeleteMail-offSet]=this::createSendDeleteMailResponse;
		list[GameResponseType.SendGameReceiptToClient-offSet]=this::createSendGameReceiptToClientResponse;
		list[GameResponseType.SendGetAchievementRewardSuccess-offSet]=this::createSendGetAchievementRewardSuccessResponse;
		list[GameResponseType.SendGiveUpQuest-offSet]=this::createSendGiveUpQuestResponse;
		list[GameResponseType.SendInfoCode-offSet]=this::createSendInfoCodeResponse;
		list[GameResponseType.SendInfoCodeWithArgs-offSet]=this::createSendInfoCodeWithArgsResponse;
		list[GameResponseType.SendInfoLog-offSet]=this::createSendInfoLogResponse;
		list[GameResponseType.SendPlayerChat-offSet]=this::createSendPlayerChatResponse;
		list[GameResponseType.SendQuestFailed-offSet]=this::createSendQuestFailedResponse;
		list[GameResponseType.SendRemoveAcceptQuest-offSet]=this::createSendRemoveAcceptQuestResponse;
		list[GameResponseType.SendRemoveFriendBlackList-offSet]=this::createSendRemoveFriendBlackListResponse;
		list[GameResponseType.SendRemoveFriend-offSet]=this::createSendRemoveFriendResponse;
		list[GameResponseType.SendWarningLog-offSet]=this::createSendWarningLogResponse;
		list[GameResponseType.SwitchGame-offSet]=this::createSwitchGameResponse;
		list[GameResponseType.SwitchScene-offSet]=this::createSwitchSceneResponse;
		list[GameResponseType.TakeMailSuccess-offSet]=this::createTakeMailSuccessResponse;
		list[GameResponseType.FuncRefreshSubsectionIndex-offSet]=this::createFuncRefreshSubsectionIndexResponse;
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
	
	private BaseData createMUnitAddSkillResponse()
	{
		return new MUnitAddSkillResponse();
	}
	
	private BaseData createMUnitRemoveSkillResponse()
	{
		return new MUnitRemoveSkillResponse();
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
	
	private BaseData createSendPlayerChatResponse()
	{
		return new SendPlayerChatResponse();
	}
	
	private BaseData createFuncSendRoleGroupMemberRoleShowChangeResponse()
	{
		return new FuncSendRoleGroupMemberRoleShowChangeResponse();
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
	
	private BaseData createSwitchSceneResponse()
	{
		return new SwitchSceneResponse();
	}
	
	private BaseData createFuncRefreshSubsectionIndexResponse()
	{
		return new FuncRefreshSubsectionIndexResponse();
	}
	
}
