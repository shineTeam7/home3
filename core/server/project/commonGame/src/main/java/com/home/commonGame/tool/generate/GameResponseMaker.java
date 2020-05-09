package com.home.commonGame.tool.generate;
import com.home.commonGame.constlist.generate.GameResponseType;
import com.home.commonGame.net.response.activity.UseActivationCodeResponse;
import com.home.commonGame.net.response.base.LoginResponse;
import com.home.commonGame.net.response.func.aution.FuncAuctionBuyItemResponse;
import com.home.commonGame.net.response.func.aution.FuncAuctionCancelSellItemResponse;
import com.home.commonGame.net.response.func.aution.FuncAuctionQueryResponse;
import com.home.commonGame.net.response.func.aution.FuncAuctionSellItemResponse;
import com.home.commonGame.net.response.func.aution.FuncGetAuctionItemSuggestPriceResponse;
import com.home.commonGame.net.response.func.base.FuncPlayerRoleGroupRResponse;
import com.home.commonGame.net.response.func.base.FuncRResponse;
import com.home.commonGame.net.response.func.item.FuncCleanUpItemResponse;
import com.home.commonGame.net.response.func.item.FuncItemRemoveRedPointResponse;
import com.home.commonGame.net.response.func.item.FuncMoveEquipResponse;
import com.home.commonGame.net.response.func.item.FuncMoveItemResponse;
import com.home.commonGame.net.response.func.item.FuncPutOffEquipResponse;
import com.home.commonGame.net.response.func.item.FuncPutOnEquipResponse;
import com.home.commonGame.net.response.func.item.FuncSplitItemResponse;
import com.home.commonGame.net.response.func.item.FuncUseItemResponse;
import com.home.commonGame.net.response.func.item.SellItemResponse;
import com.home.commonGame.net.response.func.match.FuncAcceptMatchResponse;
import com.home.commonGame.net.response.func.match.FuncApplyCancelMatchResponse;
import com.home.commonGame.net.response.func.match.FuncApplyMatchResponse;
import com.home.commonGame.net.response.func.rank.FuncGetPageShowResponse;
import com.home.commonGame.net.response.func.rank.subsection.FuncGetSubsectionPageShowResponse;
import com.home.commonGame.net.response.func.roleGroup.FuncApplyRoleGroupResponse;
import com.home.commonGame.net.response.func.roleGroup.FuncChangeLeaderRoleGroupResponse;
import com.home.commonGame.net.response.func.roleGroup.FuncChangeRoleGroupCanApplyInAbsResponse;
import com.home.commonGame.net.response.func.roleGroup.FuncChangeRoleGroupCanInviteInAbsResponse;
import com.home.commonGame.net.response.func.roleGroup.FuncChangeRoleGroupNameResponse;
import com.home.commonGame.net.response.func.roleGroup.FuncChangeRoleGroupNoticeResponse;
import com.home.commonGame.net.response.func.roleGroup.FuncCreateRoleGroupResponse;
import com.home.commonGame.net.response.func.roleGroup.FuncDisbandRoleGroupResponse;
import com.home.commonGame.net.response.func.roleGroup.FuncGetRoleGroupDataResponse;
import com.home.commonGame.net.response.func.roleGroup.FuncHandleApplyRoleGroupResponse;
import com.home.commonGame.net.response.func.roleGroup.FuncHandleInviteRoleGroupResponse;
import com.home.commonGame.net.response.func.roleGroup.FuncInviteRoleGroupResponse;
import com.home.commonGame.net.response.func.roleGroup.FuncKickMemberRoleGroupResponse;
import com.home.commonGame.net.response.func.roleGroup.FuncLeaveRoleGroupResponse;
import com.home.commonGame.net.response.func.roleGroup.FuncRoleGroupEnterOwnSceneResponse;
import com.home.commonGame.net.response.func.roleGroup.FuncSetTitleRoleGroupResponse;
import com.home.commonGame.net.response.guide.SetMainGuideStepResponse;
import com.home.commonGame.net.response.item.ExchangeResponse;
import com.home.commonGame.net.response.login.ApplyBindPlatformResponse;
import com.home.commonGame.net.response.login.CreatePlayerResponse;
import com.home.commonGame.net.response.login.DeletePlayerResponse;
import com.home.commonGame.net.response.login.LoginGameResponse;
import com.home.commonGame.net.response.login.PlayerExitResponse;
import com.home.commonGame.net.response.login.PlayerLoginForOfflineResponse;
import com.home.commonGame.net.response.login.PlayerLoginResponse;
import com.home.commonGame.net.response.login.PlayerReconnectLoginResponse;
import com.home.commonGame.net.response.login.PlayerSwitchGameResponse;
import com.home.commonGame.net.response.mail.ClientSendMailResponse;
import com.home.commonGame.net.response.mail.DeleteMailResponse;
import com.home.commonGame.net.response.mail.GetAllMailResponse;
import com.home.commonGame.net.response.mail.MailReadedResponse;
import com.home.commonGame.net.response.mail.MailRemoveRedPointResponse;
import com.home.commonGame.net.response.mail.TakeMailResponse;
import com.home.commonGame.net.response.quest.AcceptQuestResponse;
import com.home.commonGame.net.response.quest.ClientTaskEventResponse;
import com.home.commonGame.net.response.quest.CommitQuestResponse;
import com.home.commonGame.net.response.quest.GetAchievementRewardResponse;
import com.home.commonGame.net.response.quest.GiveUpQuestResponse;
import com.home.commonGame.net.response.role.ApplyChangeRoleNameResponse;
import com.home.commonGame.net.response.role.MUnitAttributeSwitchNormalSendResponse;
import com.home.commonGame.net.response.role.MUnitRResponse;
import com.home.commonGame.net.response.role.pet.PetRestResponse;
import com.home.commonGame.net.response.role.pet.PetWorkResponse;
import com.home.commonGame.net.response.scene.base.CRoleRResponse;
import com.home.commonGame.net.response.scene.base.CUnitRResponse;
import com.home.commonGame.net.response.scene.base.SceneRResponse;
import com.home.commonGame.net.response.scene.scene.ApplyEnterSceneResponse;
import com.home.commonGame.net.response.scene.scene.ApplyLeaveSceneResponse;
import com.home.commonGame.net.response.scene.scene.ClientSceneRadioResponse;
import com.home.commonGame.net.response.scene.scene.PreEnterSceneReadyResponse;
import com.home.commonGame.net.response.scene.syncScene.CUnitPreBattleSureResponse;
import com.home.commonGame.net.response.scene.syncScene.FrameSyncOneResponse;
import com.home.commonGame.net.response.scene.unit.CUnitAddBulletResponse;
import com.home.commonGame.net.response.scene.unit.CUnitAttackResponse;
import com.home.commonGame.net.response.scene.unit.CUnitBulletHitResponse;
import com.home.commonGame.net.response.scene.unit.CUnitChatResponse;
import com.home.commonGame.net.response.scene.unit.CUnitDriveResponse;
import com.home.commonGame.net.response.scene.unit.CUnitGetOffVehicleResponse;
import com.home.commonGame.net.response.scene.unit.CUnitGetOnVehicleResponse;
import com.home.commonGame.net.response.scene.unit.CUnitKillSelfResponse;
import com.home.commonGame.net.response.scene.unit.CUnitMoveDirResponse;
import com.home.commonGame.net.response.scene.unit.CUnitMovePosListResponse;
import com.home.commonGame.net.response.scene.unit.CUnitMovePosResponse;
import com.home.commonGame.net.response.scene.unit.CUnitOperateResponse;
import com.home.commonGame.net.response.scene.unit.CUnitPickUpItemBagAllResponse;
import com.home.commonGame.net.response.scene.unit.CUnitPickUpItemResponse;
import com.home.commonGame.net.response.scene.unit.CUnitRemoveBulletResponse;
import com.home.commonGame.net.response.scene.unit.CUnitSkillOverResponse;
import com.home.commonGame.net.response.scene.unit.CUnitSkillStepResponse;
import com.home.commonGame.net.response.scene.unit.CUnitSpecialMoveResponse;
import com.home.commonGame.net.response.scene.unit.CUnitStopMoveResponse;
import com.home.commonGame.net.response.scene.unit.CUnitSyncCommandResponse;
import com.home.commonGame.net.response.scene.unit.CUnitUseSkillExResponse;
import com.home.commonGame.net.response.scene.unit.CUnitUseSkillResponse;
import com.home.commonGame.net.response.scene.unit.building.CBuildingCancelLevelUpResponse;
import com.home.commonGame.net.response.scene.unit.building.CBuildingLevelUpResponse;
import com.home.commonGame.net.response.social.QueryPlayerResponse;
import com.home.commonGame.net.response.social.SearchPlayerResponse;
import com.home.commonGame.net.response.social.UpdateRoleSocialDataResponse;
import com.home.commonGame.net.response.social.chat.PlayerChatResponse;
import com.home.commonGame.net.response.social.chat.PlayerReceiveChatIndexResponse;
import com.home.commonGame.net.response.social.friend.AddAllFriendResponse;
import com.home.commonGame.net.response.social.friend.AddFriendBlackListResponse;
import com.home.commonGame.net.response.social.friend.AddFriendResponse;
import com.home.commonGame.net.response.social.friend.AgreeAddFriendResponse;
import com.home.commonGame.net.response.social.friend.RefuseApplyAddFriendResponse;
import com.home.commonGame.net.response.social.friend.RemoveFriendBlackListResponse;
import com.home.commonGame.net.response.social.friend.RemoveFriendResponse;
import com.home.commonGame.net.response.system.CenterTransClientToGameResponse;
import com.home.commonGame.net.response.system.ClientGMResponse;
import com.home.commonGame.net.response.system.SaveBooleanResponse;
import com.home.commonGame.net.response.system.SaveIntResponse;
import com.home.commonGame.net.response.system.SaveLongResponse;
import com.home.commonGame.net.response.system.SaveStringResponse;
import com.home.commonGame.net.response.system.SendClientLogResponse;
import com.home.commonGame.net.response.system.SendClientOfflineWorkResponse;
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
		list[GameResponseType.AcceptQuest-offSet]=this::createAcceptQuestResponse;
		list[GameResponseType.AddAllFriend-offSet]=this::createAddAllFriendResponse;
		list[GameResponseType.AddFriendBlackList-offSet]=this::createAddFriendBlackListResponse;
		list[GameResponseType.AddFriend-offSet]=this::createAddFriendResponse;
		list[GameResponseType.AgreeAddFriend-offSet]=this::createAgreeAddFriendResponse;
		list[GameResponseType.ApplyBindPlatform-offSet]=this::createApplyBindPlatformResponse;
		list[GameResponseType.ApplyChangeRoleName-offSet]=this::createApplyChangeRoleNameResponse;
		list[GameResponseType.ApplyEnterScene-offSet]=this::createApplyEnterSceneResponse;
		list[GameResponseType.ApplyLeaveScene-offSet]=this::createApplyLeaveSceneResponse;
		list[GameResponseType.CBuildingCancelLevelUp-offSet]=this::createCBuildingCancelLevelUpResponse;
		list[GameResponseType.CBuildingLevelUp-offSet]=this::createCBuildingLevelUpResponse;
		list[GameResponseType.CUnitAddBullet-offSet]=this::createCUnitAddBulletResponse;
		list[GameResponseType.CUnitAttack-offSet]=this::createCUnitAttackResponse;
		list[GameResponseType.CUnitBulletHit-offSet]=this::createCUnitBulletHitResponse;
		list[GameResponseType.CUnitChat-offSet]=this::createCUnitChatResponse;
		list[GameResponseType.CUnitKillSelf-offSet]=this::createCUnitKillSelfResponse;
		list[GameResponseType.CUnitMoveDir-offSet]=this::createCUnitMoveDirResponse;
		list[GameResponseType.CUnitMovePosList-offSet]=this::createCUnitMovePosListResponse;
		list[GameResponseType.CUnitMovePos-offSet]=this::createCUnitMovePosResponse;
		list[GameResponseType.CUnitOperate-offSet]=this::createCUnitOperateResponse;
		list[GameResponseType.CUnitPickUpItem-offSet]=this::createCUnitPickUpItemResponse;
		list[GameResponseType.CUnitPreBattleSure-offSet]=this::createCUnitPreBattleSureResponse;
		list[GameResponseType.CUnitR-offSet]=this::createCUnitRResponse;
		list[GameResponseType.CUnitRemoveBullet-offSet]=this::createCUnitRemoveBulletResponse;
		list[GameResponseType.CUnitSkillOver-offSet]=this::createCUnitSkillOverResponse;
		list[GameResponseType.CUnitSkillStep-offSet]=this::createCUnitSkillStepResponse;
		list[GameResponseType.CUnitSpecialMove-offSet]=this::createCUnitSpecialMoveResponse;
		list[GameResponseType.CUnitStopMove-offSet]=this::createCUnitStopMoveResponse;
		list[GameResponseType.CUnitSyncCommand-offSet]=this::createCUnitSyncCommandResponse;
		list[GameResponseType.CUnitUseSkillEx-offSet]=this::createCUnitUseSkillExResponse;
		list[GameResponseType.CUnitUseSkill-offSet]=this::createCUnitUseSkillResponse;
		list[GameResponseType.CenterTransClientToGame-offSet]=this::createCenterTransClientToGameResponse;
		list[GameResponseType.ClientGM-offSet]=this::createClientGMResponse;
		list[GameResponseType.ClientSceneRadio-offSet]=this::createClientSceneRadioResponse;
		list[GameResponseType.ClientSendMail-offSet]=this::createClientSendMailResponse;
		list[GameResponseType.ClientTaskEvent-offSet]=this::createClientTaskEventResponse;
		list[GameResponseType.CommitQuest-offSet]=this::createCommitQuestResponse;
		list[GameResponseType.CreatePlayer-offSet]=this::createCreatePlayerResponse;
		list[GameResponseType.DeleteMail-offSet]=this::createDeleteMailResponse;
		list[GameResponseType.DeletePlayer-offSet]=this::createDeletePlayerResponse;
		list[GameResponseType.Exchange-offSet]=this::createExchangeResponse;
		list[GameResponseType.FrameSyncOne-offSet]=this::createFrameSyncOneResponse;
		list[GameResponseType.FuncAcceptMatch-offSet]=this::createFuncAcceptMatchResponse;
		list[GameResponseType.FuncApplyCancelMatch-offSet]=this::createFuncApplyCancelMatchResponse;
		list[GameResponseType.FuncApplyMatch-offSet]=this::createFuncApplyMatchResponse;
		list[GameResponseType.FuncCleanUpItem-offSet]=this::createFuncCleanUpItemResponse;
		list[GameResponseType.FuncCreateRoleGroup-offSet]=this::createFuncCreateRoleGroupResponse;
		list[GameResponseType.FuncGetPageShow-offSet]=this::createFuncGetPageShowResponse;
		list[GameResponseType.FuncGetSubsectionPageShow-offSet]=this::createFuncGetSubsectionPageShowResponse;
		list[GameResponseType.FuncItemRemoveRedPoint-offSet]=this::createFuncItemRemoveRedPointResponse;
		list[GameResponseType.FuncMoveEquip-offSet]=this::createFuncMoveEquipResponse;
		list[GameResponseType.FuncMoveItem-offSet]=this::createFuncMoveItemResponse;
		list[GameResponseType.FuncPutOffEquip-offSet]=this::createFuncPutOffEquipResponse;
		list[GameResponseType.FuncPutOnEquip-offSet]=this::createFuncPutOnEquipResponse;
		list[GameResponseType.FuncR-offSet]=this::createFuncRResponse;
		list[GameResponseType.FuncSplitItem-offSet]=this::createFuncSplitItemResponse;
		list[GameResponseType.FuncUseItem-offSet]=this::createFuncUseItemResponse;
		list[GameResponseType.GetAchievementReward-offSet]=this::createGetAchievementRewardResponse;
		list[GameResponseType.GetAllMail-offSet]=this::createGetAllMailResponse;
		list[GameResponseType.GiveUpQuest-offSet]=this::createGiveUpQuestResponse;
		list[GameResponseType.LoginGame-offSet]=this::createLoginGameResponse;
		list[GameResponseType.Login-offSet]=this::createLoginResponse;
		list[GameResponseType.MUnitAttributeSwitchNormalSend-offSet]=this::createMUnitAttributeSwitchNormalSendResponse;
		list[GameResponseType.MailReaded-offSet]=this::createMailReadedResponse;
		list[GameResponseType.MailRemoveRedPoint-offSet]=this::createMailRemoveRedPointResponse;
		list[GameResponseType.PlayerExit-offSet]=this::createPlayerExitResponse;
		list[GameResponseType.PlayerLoginForOffline-offSet]=this::createPlayerLoginForOfflineResponse;
		list[GameResponseType.PlayerLogin-offSet]=this::createPlayerLoginResponse;
		list[GameResponseType.PlayerSwitchGame-offSet]=this::createPlayerSwitchGameResponse;
		list[GameResponseType.PreEnterSceneReady-offSet]=this::createPreEnterSceneReadyResponse;
		list[GameResponseType.QueryPlayer-offSet]=this::createQueryPlayerResponse;
		list[GameResponseType.RefuseApplyAddFriend-offSet]=this::createRefuseApplyAddFriendResponse;
		list[GameResponseType.RemoveFriendBlackList-offSet]=this::createRemoveFriendBlackListResponse;
		list[GameResponseType.RemoveFriend-offSet]=this::createRemoveFriendResponse;
		list[GameResponseType.SaveBoolean-offSet]=this::createSaveBooleanResponse;
		list[GameResponseType.SaveInt-offSet]=this::createSaveIntResponse;
		list[GameResponseType.SaveLong-offSet]=this::createSaveLongResponse;
		list[GameResponseType.SaveString-offSet]=this::createSaveStringResponse;
		list[GameResponseType.SceneR-offSet]=this::createSceneRResponse;
		list[GameResponseType.SearchPlayer-offSet]=this::createSearchPlayerResponse;
		list[GameResponseType.SellItem-offSet]=this::createSellItemResponse;
		list[GameResponseType.SendClientLog-offSet]=this::createSendClientLogResponse;
		list[GameResponseType.SendClientOfflineWork-offSet]=this::createSendClientOfflineWorkResponse;
		list[GameResponseType.SetMainGuideStep-offSet]=this::createSetMainGuideStepResponse;
		list[GameResponseType.TakeMail-offSet]=this::createTakeMailResponse;
		list[GameResponseType.UpdateRoleSocialData-offSet]=this::createUpdateRoleSocialDataResponse;
		list[GameResponseType.FuncApplyRoleGroup-offSet]=this::createFuncApplyRoleGroupResponse;
		list[GameResponseType.FuncDisbandRoleGroup-offSet]=this::createFuncDisbandRoleGroupResponse;
		list[GameResponseType.FuncInviteRoleGroup-offSet]=this::createFuncInviteRoleGroupResponse;
		list[GameResponseType.FuncLeaveRoleGroup-offSet]=this::createFuncLeaveRoleGroupResponse;
		list[GameResponseType.FuncPlayerRoleGroupR-offSet]=this::createFuncPlayerRoleGroupRResponse;
		list[GameResponseType.FuncHandleApplyRoleGroup-offSet]=this::createFuncHandleApplyRoleGroupResponse;
		list[GameResponseType.FuncHandleInviteRoleGroup-offSet]=this::createFuncHandleInviteRoleGroupResponse;
		list[GameResponseType.FuncChangeLeaderRoleGroup-offSet]=this::createFuncChangeLeaderRoleGroupResponse;
		list[GameResponseType.FuncKickMemberRoleGroup-offSet]=this::createFuncKickMemberRoleGroupResponse;
		list[GameResponseType.FuncSetTitleRoleGroup-offSet]=this::createFuncSetTitleRoleGroupResponse;
		list[GameResponseType.FuncChangeRoleGroupName-offSet]=this::createFuncChangeRoleGroupNameResponse;
		list[GameResponseType.FuncChangeRoleGroupNotice-offSet]=this::createFuncChangeRoleGroupNoticeResponse;
		list[GameResponseType.UseActivationCode-offSet]=this::createUseActivationCodeResponse;
		list[GameResponseType.MUnitR-offSet]=this::createMUnitRResponse;
		list[GameResponseType.FuncChangeRoleGroupCanInviteInAbs-offSet]=this::createFuncChangeRoleGroupCanInviteInAbsResponse;
		list[GameResponseType.FuncChangeRoleGroupCanApplyInAbs-offSet]=this::createFuncChangeRoleGroupCanApplyInAbsResponse;
		list[GameResponseType.FuncGetRoleGroupData-offSet]=this::createFuncGetRoleGroupDataResponse;
		list[GameResponseType.CUnitPickUpItemBagAll-offSet]=this::createCUnitPickUpItemBagAllResponse;
		list[GameResponseType.CRoleR-offSet]=this::createCRoleRResponse;
		list[GameResponseType.PlayerChat-offSet]=this::createPlayerChatResponse;
		list[GameResponseType.PlayerReceiveChatIndex-offSet]=this::createPlayerReceiveChatIndexResponse;
		list[GameResponseType.CUnitGetOffVehicle-offSet]=this::createCUnitGetOffVehicleResponse;
		list[GameResponseType.CUnitGetOnVehicle-offSet]=this::createCUnitGetOnVehicleResponse;
		list[GameResponseType.CUnitDrive-offSet]=this::createCUnitDriveResponse;
		list[GameResponseType.PlayerReconnectLogin-offSet]=this::createPlayerReconnectLoginResponse;
		list[GameResponseType.FuncAuctionBuyItem-offSet]=this::createFuncAuctionBuyItemResponse;
		list[GameResponseType.PetRest-offSet]=this::createPetRestResponse;
		list[GameResponseType.PetWork-offSet]=this::createPetWorkResponse;
		list[GameResponseType.FuncAuctionSellItem-offSet]=this::createFuncAuctionSellItemResponse;
		list[GameResponseType.FuncGetAuctionItemSuggestPrice-offSet]=this::createFuncGetAuctionItemSuggestPriceResponse;
		list[GameResponseType.FuncAuctionCancelSellItem-offSet]=this::createFuncAuctionCancelSellItemResponse;
		list[GameResponseType.FuncAuctionQuery-offSet]=this::createFuncAuctionQueryResponse;
		list[GameResponseType.FuncRoleGroupEnterOwnScene-offSet]=this::createFuncRoleGroupEnterOwnSceneResponse;
	}
	
	private BaseData createCreatePlayerResponse()
	{
		return new CreatePlayerResponse();
	}
	
	private BaseData createLoginGameResponse()
	{
		return new LoginGameResponse();
	}
	
	private BaseData createPlayerLoginResponse()
	{
		return new PlayerLoginResponse();
	}
	
	private BaseData createClientSceneRadioResponse()
	{
		return new ClientSceneRadioResponse();
	}
	
	private BaseData createCUnitUseSkillResponse()
	{
		return new CUnitUseSkillResponse();
	}
	
	private BaseData createCUnitMoveDirResponse()
	{
		return new CUnitMoveDirResponse();
	}
	
	private BaseData createCUnitMovePosListResponse()
	{
		return new CUnitMovePosListResponse();
	}
	
	private BaseData createCUnitMovePosResponse()
	{
		return new CUnitMovePosResponse();
	}
	
	private BaseData createCUnitStopMoveResponse()
	{
		return new CUnitStopMoveResponse();
	}
	
	private BaseData createCUnitSpecialMoveResponse()
	{
		return new CUnitSpecialMoveResponse();
	}
	
	private BaseData createCUnitAttackResponse()
	{
		return new CUnitAttackResponse();
	}
	
	private BaseData createCUnitBulletHitResponse()
	{
		return new CUnitBulletHitResponse();
	}
	
	private BaseData createClientGMResponse()
	{
		return new ClientGMResponse();
	}
	
	private BaseData createLoginResponse()
	{
		return new LoginResponse();
	}
	
	private BaseData createCUnitChatResponse()
	{
		return new CUnitChatResponse();
	}
	
	private BaseData createFuncGetPageShowResponse()
	{
		return new FuncGetPageShowResponse();
	}
	
	private BaseData createFuncAcceptMatchResponse()
	{
		return new FuncAcceptMatchResponse();
	}
	
	private BaseData createFuncApplyCancelMatchResponse()
	{
		return new FuncApplyCancelMatchResponse();
	}
	
	private BaseData createFuncApplyMatchResponse()
	{
		return new FuncApplyMatchResponse();
	}
	
	private BaseData createApplyChangeRoleNameResponse()
	{
		return new ApplyChangeRoleNameResponse();
	}
	
	private BaseData createSaveBooleanResponse()
	{
		return new SaveBooleanResponse();
	}
	
	private BaseData createSaveIntResponse()
	{
		return new SaveIntResponse();
	}
	
	private BaseData createSaveStringResponse()
	{
		return new SaveStringResponse();
	}
	
	private BaseData createCUnitSkillOverResponse()
	{
		return new CUnitSkillOverResponse();
	}
	
	private BaseData createMUnitAttributeSwitchNormalSendResponse()
	{
		return new MUnitAttributeSwitchNormalSendResponse();
	}
	
	private BaseData createCUnitSyncCommandResponse()
	{
		return new CUnitSyncCommandResponse();
	}
	
	private BaseData createCUnitAddBulletResponse()
	{
		return new CUnitAddBulletResponse();
	}
	
	private BaseData createCUnitRemoveBulletResponse()
	{
		return new CUnitRemoveBulletResponse();
	}
	
	private BaseData createCUnitPreBattleSureResponse()
	{
		return new CUnitPreBattleSureResponse();
	}
	
	private BaseData createFrameSyncOneResponse()
	{
		return new FrameSyncOneResponse();
	}
	
	private BaseData createCUnitUseSkillExResponse()
	{
		return new CUnitUseSkillExResponse();
	}
	
	private BaseData createCUnitKillSelfResponse()
	{
		return new CUnitKillSelfResponse();
	}
	
	private BaseData createPlayerExitResponse()
	{
		return new PlayerExitResponse();
	}
	
	private BaseData createCenterTransClientToGameResponse()
	{
		return new CenterTransClientToGameResponse();
	}
	
	private BaseData createPlayerSwitchGameResponse()
	{
		return new PlayerSwitchGameResponse();
	}
	
	private BaseData createCUnitSkillStepResponse()
	{
		return new CUnitSkillStepResponse();
	}
	
	private BaseData createDeletePlayerResponse()
	{
		return new DeletePlayerResponse();
	}
	
	private BaseData createFuncRResponse()
	{
		return new FuncRResponse();
	}
	
	private BaseData createFuncCleanUpItemResponse()
	{
		return new FuncCleanUpItemResponse();
	}
	
	private BaseData createFuncUseItemResponse()
	{
		return new FuncUseItemResponse();
	}
	
	private BaseData createPlayerLoginForOfflineResponse()
	{
		return new PlayerLoginForOfflineResponse();
	}
	
	private BaseData createAcceptQuestResponse()
	{
		return new AcceptQuestResponse();
	}
	
	private BaseData createCommitQuestResponse()
	{
		return new CommitQuestResponse();
	}
	
	private BaseData createGiveUpQuestResponse()
	{
		return new GiveUpQuestResponse();
	}
	
	private BaseData createClientTaskEventResponse()
	{
		return new ClientTaskEventResponse();
	}
	
	private BaseData createClientSendMailResponse()
	{
		return new ClientSendMailResponse();
	}
	
	private BaseData createMailReadedResponse()
	{
		return new MailReadedResponse();
	}
	
	private BaseData createDeleteMailResponse()
	{
		return new DeleteMailResponse();
	}
	
	private BaseData createGetAllMailResponse()
	{
		return new GetAllMailResponse();
	}
	
	private BaseData createTakeMailResponse()
	{
		return new TakeMailResponse();
	}
	
	private BaseData createGetAchievementRewardResponse()
	{
		return new GetAchievementRewardResponse();
	}
	
	private BaseData createRemoveFriendResponse()
	{
		return new RemoveFriendResponse();
	}
	
	private BaseData createAddFriendResponse()
	{
		return new AddFriendResponse();
	}
	
	private BaseData createAgreeAddFriendResponse()
	{
		return new AgreeAddFriendResponse();
	}
	
	private BaseData createUpdateRoleSocialDataResponse()
	{
		return new UpdateRoleSocialDataResponse();
	}
	
	private BaseData createRemoveFriendBlackListResponse()
	{
		return new RemoveFriendBlackListResponse();
	}
	
	private BaseData createAddFriendBlackListResponse()
	{
		return new AddFriendBlackListResponse();
	}
	
	private BaseData createRefuseApplyAddFriendResponse()
	{
		return new RefuseApplyAddFriendResponse();
	}
	
	private BaseData createAddAllFriendResponse()
	{
		return new AddAllFriendResponse();
	}
	
	private BaseData createApplyBindPlatformResponse()
	{
		return new ApplyBindPlatformResponse();
	}
	
	private BaseData createMailRemoveRedPointResponse()
	{
		return new MailRemoveRedPointResponse();
	}
	
	private BaseData createFuncItemRemoveRedPointResponse()
	{
		return new FuncItemRemoveRedPointResponse();
	}
	
	private BaseData createApplyEnterSceneResponse()
	{
		return new ApplyEnterSceneResponse();
	}
	
	private BaseData createPreEnterSceneReadyResponse()
	{
		return new PreEnterSceneReadyResponse();
	}
	
	private BaseData createApplyLeaveSceneResponse()
	{
		return new ApplyLeaveSceneResponse();
	}
	
	private BaseData createSceneRResponse()
	{
		return new SceneRResponse();
	}
	
	private BaseData createSendClientOfflineWorkResponse()
	{
		return new SendClientOfflineWorkResponse();
	}
	
	private BaseData createSaveLongResponse()
	{
		return new SaveLongResponse();
	}
	
	private BaseData createSearchPlayerResponse()
	{
		return new SearchPlayerResponse();
	}
	
	private BaseData createSellItemResponse()
	{
		return new SellItemResponse();
	}
	
	private BaseData createSetMainGuideStepResponse()
	{
		return new SetMainGuideStepResponse();
	}
	
	private BaseData createSendClientLogResponse()
	{
		return new SendClientLogResponse();
	}
	
	private BaseData createQueryPlayerResponse()
	{
		return new QueryPlayerResponse();
	}
	
	private BaseData createExchangeResponse()
	{
		return new ExchangeResponse();
	}
	
	private BaseData createFuncMoveItemResponse()
	{
		return new FuncMoveItemResponse();
	}
	
	private BaseData createFuncSplitItemResponse()
	{
		return new FuncSplitItemResponse();
	}
	
	private BaseData createCUnitPickUpItemResponse()
	{
		return new CUnitPickUpItemResponse();
	}
	
	private BaseData createFuncMoveEquipResponse()
	{
		return new FuncMoveEquipResponse();
	}
	
	private BaseData createFuncPutOffEquipResponse()
	{
		return new FuncPutOffEquipResponse();
	}
	
	private BaseData createFuncPutOnEquipResponse()
	{
		return new FuncPutOnEquipResponse();
	}
	
	private BaseData createCBuildingCancelLevelUpResponse()
	{
		return new CBuildingCancelLevelUpResponse();
	}
	
	private BaseData createCBuildingLevelUpResponse()
	{
		return new CBuildingLevelUpResponse();
	}
	
	private BaseData createCUnitRResponse()
	{
		return new CUnitRResponse();
	}
	
	private BaseData createCUnitOperateResponse()
	{
		return new CUnitOperateResponse();
	}
	
	private BaseData createFuncCreateRoleGroupResponse()
	{
		return new FuncCreateRoleGroupResponse();
	}
	
	private BaseData createFuncApplyRoleGroupResponse()
	{
		return new FuncApplyRoleGroupResponse();
	}
	
	private BaseData createFuncDisbandRoleGroupResponse()
	{
		return new FuncDisbandRoleGroupResponse();
	}
	
	private BaseData createFuncInviteRoleGroupResponse()
	{
		return new FuncInviteRoleGroupResponse();
	}
	
	private BaseData createFuncLeaveRoleGroupResponse()
	{
		return new FuncLeaveRoleGroupResponse();
	}
	
	private BaseData createFuncPlayerRoleGroupRResponse()
	{
		return new FuncPlayerRoleGroupRResponse();
	}
	
	private BaseData createFuncHandleApplyRoleGroupResponse()
	{
		return new FuncHandleApplyRoleGroupResponse();
	}
	
	private BaseData createFuncHandleInviteRoleGroupResponse()
	{
		return new FuncHandleInviteRoleGroupResponse();
	}
	
	private BaseData createFuncChangeLeaderRoleGroupResponse()
	{
		return new FuncChangeLeaderRoleGroupResponse();
	}
	
	private BaseData createFuncKickMemberRoleGroupResponse()
	{
		return new FuncKickMemberRoleGroupResponse();
	}
	
	private BaseData createFuncSetTitleRoleGroupResponse()
	{
		return new FuncSetTitleRoleGroupResponse();
	}
	
	private BaseData createFuncChangeRoleGroupNameResponse()
	{
		return new FuncChangeRoleGroupNameResponse();
	}
	
	private BaseData createFuncChangeRoleGroupNoticeResponse()
	{
		return new FuncChangeRoleGroupNoticeResponse();
	}
	
	private BaseData createUseActivationCodeResponse()
	{
		return new UseActivationCodeResponse();
	}
	
	private BaseData createFuncChangeRoleGroupCanInviteInAbsResponse()
	{
		return new FuncChangeRoleGroupCanInviteInAbsResponse();
	}
	
	private BaseData createFuncChangeRoleGroupCanApplyInAbsResponse()
	{
		return new FuncChangeRoleGroupCanApplyInAbsResponse();
	}
	
	private BaseData createFuncGetRoleGroupDataResponse()
	{
		return new FuncGetRoleGroupDataResponse();
	}
	
	private BaseData createCUnitPickUpItemBagAllResponse()
	{
		return new CUnitPickUpItemBagAllResponse();
	}
	
	private BaseData createCRoleRResponse()
	{
		return new CRoleRResponse();
	}
	
	private BaseData createPlayerChatResponse()
	{
		return new PlayerChatResponse();
	}
	
	private BaseData createPlayerReceiveChatIndexResponse()
	{
		return new PlayerReceiveChatIndexResponse();
	}
	
	private BaseData createCUnitGetOffVehicleResponse()
	{
		return new CUnitGetOffVehicleResponse();
	}
	
	private BaseData createCUnitGetOnVehicleResponse()
	{
		return new CUnitGetOnVehicleResponse();
	}
	
	private BaseData createCUnitDriveResponse()
	{
		return new CUnitDriveResponse();
	}
	
	private BaseData createPlayerReconnectLoginResponse()
	{
		return new PlayerReconnectLoginResponse();
	}
	
	private BaseData createFuncAuctionBuyItemResponse()
	{
		return new FuncAuctionBuyItemResponse();
	}
	
	private BaseData createFuncAuctionSellItemResponse()
	{
		return new FuncAuctionSellItemResponse();
	}
	
	private BaseData createFuncGetAuctionItemSuggestPriceResponse()
	{
		return new FuncGetAuctionItemSuggestPriceResponse();
	}
	
	private BaseData createFuncAuctionCancelSellItemResponse()
	{
		return new FuncAuctionCancelSellItemResponse();
	}
	
	private BaseData createFuncAuctionQueryResponse()
	{
		return new FuncAuctionQueryResponse();
	}
	
	private BaseData createFuncGetSubsectionPageShowResponse()
	{
		return new FuncGetSubsectionPageShowResponse();
	}
	
	private BaseData createMUnitRResponse()
	{
		return new MUnitRResponse();
	}
	
	private BaseData createPetRestResponse()
	{
		return new PetRestResponse();
	}
	
	private BaseData createPetWorkResponse()
	{
		return new PetWorkResponse();
	}
	
	private BaseData createFuncRoleGroupEnterOwnSceneResponse()
	{
		return new FuncRoleGroupEnterOwnSceneResponse();
	}
	
}
