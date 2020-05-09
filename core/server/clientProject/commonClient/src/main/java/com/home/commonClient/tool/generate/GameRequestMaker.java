package com.home.commonClient.tool.generate;
import com.home.commonClient.constlist.generate.GameRequestType;
import com.home.commonClient.net.request.activity.UseActivationCodeRequest;
import com.home.commonClient.net.request.base.LoginRequest;
import com.home.commonClient.net.request.func.aution.FuncAuctionBuyItemRequest;
import com.home.commonClient.net.request.func.aution.FuncAuctionCancelSellItemRequest;
import com.home.commonClient.net.request.func.aution.FuncAuctionQueryRequest;
import com.home.commonClient.net.request.func.aution.FuncAuctionSellItemRequest;
import com.home.commonClient.net.request.func.aution.FuncGetAuctionItemSuggestPriceRequest;
import com.home.commonClient.net.request.func.base.FuncPlayerRoleGroupRRequest;
import com.home.commonClient.net.request.func.base.FuncRRequest;
import com.home.commonClient.net.request.func.item.FuncCleanUpItemRequest;
import com.home.commonClient.net.request.func.item.FuncItemRemoveRedPointRequest;
import com.home.commonClient.net.request.func.item.FuncMoveEquipRequest;
import com.home.commonClient.net.request.func.item.FuncMoveItemRequest;
import com.home.commonClient.net.request.func.item.FuncPutOffEquipRequest;
import com.home.commonClient.net.request.func.item.FuncPutOnEquipRequest;
import com.home.commonClient.net.request.func.item.FuncSplitItemRequest;
import com.home.commonClient.net.request.func.item.FuncUseItemRequest;
import com.home.commonClient.net.request.func.item.SellItemRequest;
import com.home.commonClient.net.request.func.match.FuncAcceptMatchRequest;
import com.home.commonClient.net.request.func.match.FuncApplyCancelMatchRequest;
import com.home.commonClient.net.request.func.match.FuncApplyMatchRequest;
import com.home.commonClient.net.request.func.rank.FuncGetPageShowRequest;
import com.home.commonClient.net.request.func.rank.subsection.FuncGetSubsectionPageShowRequest;
import com.home.commonClient.net.request.func.roleGroup.FuncApplyRoleGroupRequest;
import com.home.commonClient.net.request.func.roleGroup.FuncChangeLeaderRoleGroupRequest;
import com.home.commonClient.net.request.func.roleGroup.FuncChangeRoleGroupCanApplyInAbsRequest;
import com.home.commonClient.net.request.func.roleGroup.FuncChangeRoleGroupCanInviteInAbsRequest;
import com.home.commonClient.net.request.func.roleGroup.FuncChangeRoleGroupNameRequest;
import com.home.commonClient.net.request.func.roleGroup.FuncChangeRoleGroupNoticeRequest;
import com.home.commonClient.net.request.func.roleGroup.FuncCreateRoleGroupRequest;
import com.home.commonClient.net.request.func.roleGroup.FuncDisbandRoleGroupRequest;
import com.home.commonClient.net.request.func.roleGroup.FuncGetRoleGroupDataRequest;
import com.home.commonClient.net.request.func.roleGroup.FuncHandleApplyRoleGroupRequest;
import com.home.commonClient.net.request.func.roleGroup.FuncHandleInviteRoleGroupRequest;
import com.home.commonClient.net.request.func.roleGroup.FuncInviteRoleGroupRequest;
import com.home.commonClient.net.request.func.roleGroup.FuncKickMemberRoleGroupRequest;
import com.home.commonClient.net.request.func.roleGroup.FuncLeaveRoleGroupRequest;
import com.home.commonClient.net.request.func.roleGroup.FuncRoleGroupEnterOwnSceneRequest;
import com.home.commonClient.net.request.func.roleGroup.FuncSetTitleRoleGroupRequest;
import com.home.commonClient.net.request.guide.SetMainGuideStepRequest;
import com.home.commonClient.net.request.item.ExchangeRequest;
import com.home.commonClient.net.request.login.ApplyBindPlatformRequest;
import com.home.commonClient.net.request.login.CreatePlayerRequest;
import com.home.commonClient.net.request.login.DeletePlayerRequest;
import com.home.commonClient.net.request.login.LoginGameRequest;
import com.home.commonClient.net.request.login.PlayerExitRequest;
import com.home.commonClient.net.request.login.PlayerLoginForOfflineRequest;
import com.home.commonClient.net.request.login.PlayerLoginRequest;
import com.home.commonClient.net.request.login.PlayerReconnectLoginRequest;
import com.home.commonClient.net.request.login.PlayerSwitchGameRequest;
import com.home.commonClient.net.request.mail.ClientSendMailRequest;
import com.home.commonClient.net.request.mail.DeleteMailRequest;
import com.home.commonClient.net.request.mail.GetAllMailRequest;
import com.home.commonClient.net.request.mail.MailReadedRequest;
import com.home.commonClient.net.request.mail.MailRemoveRedPointRequest;
import com.home.commonClient.net.request.mail.TakeMailRequest;
import com.home.commonClient.net.request.quest.AcceptQuestRequest;
import com.home.commonClient.net.request.quest.ClientTaskEventRequest;
import com.home.commonClient.net.request.quest.CommitQuestRequest;
import com.home.commonClient.net.request.quest.GetAchievementRewardRequest;
import com.home.commonClient.net.request.quest.GiveUpQuestRequest;
import com.home.commonClient.net.request.role.ApplyChangeRoleNameRequest;
import com.home.commonClient.net.request.role.MUnitAttributeSwitchNormalSendRequest;
import com.home.commonClient.net.request.role.MUnitRRequest;
import com.home.commonClient.net.request.role.pet.PetRestRequest;
import com.home.commonClient.net.request.role.pet.PetWorkRequest;
import com.home.commonClient.net.request.scene.base.CRoleRRequest;
import com.home.commonClient.net.request.scene.base.CUnitRRequest;
import com.home.commonClient.net.request.scene.base.SceneRRequest;
import com.home.commonClient.net.request.scene.scene.ApplyEnterSceneRequest;
import com.home.commonClient.net.request.scene.scene.ApplyLeaveSceneRequest;
import com.home.commonClient.net.request.scene.scene.ClientSceneRadioRequest;
import com.home.commonClient.net.request.scene.scene.PreEnterSceneReadyRequest;
import com.home.commonClient.net.request.scene.syncScene.CUnitPreBattleSureRequest;
import com.home.commonClient.net.request.scene.syncScene.FrameSyncOneRequest;
import com.home.commonClient.net.request.scene.unit.CUnitAddBulletRequest;
import com.home.commonClient.net.request.scene.unit.CUnitAttackRequest;
import com.home.commonClient.net.request.scene.unit.CUnitBulletHitRequest;
import com.home.commonClient.net.request.scene.unit.CUnitChatRequest;
import com.home.commonClient.net.request.scene.unit.CUnitDriveRequest;
import com.home.commonClient.net.request.scene.unit.CUnitGetOffVehicleRequest;
import com.home.commonClient.net.request.scene.unit.CUnitGetOnVehicleRequest;
import com.home.commonClient.net.request.scene.unit.CUnitKillSelfRequest;
import com.home.commonClient.net.request.scene.unit.CUnitMoveDirRequest;
import com.home.commonClient.net.request.scene.unit.CUnitMovePosListRequest;
import com.home.commonClient.net.request.scene.unit.CUnitMovePosRequest;
import com.home.commonClient.net.request.scene.unit.CUnitOperateRequest;
import com.home.commonClient.net.request.scene.unit.CUnitPickUpItemBagAllRequest;
import com.home.commonClient.net.request.scene.unit.CUnitPickUpItemRequest;
import com.home.commonClient.net.request.scene.unit.CUnitRemoveBulletRequest;
import com.home.commonClient.net.request.scene.unit.CUnitSkillOverRequest;
import com.home.commonClient.net.request.scene.unit.CUnitSkillStepRequest;
import com.home.commonClient.net.request.scene.unit.CUnitSpecialMoveRequest;
import com.home.commonClient.net.request.scene.unit.CUnitStopMoveRequest;
import com.home.commonClient.net.request.scene.unit.CUnitSyncCommandRequest;
import com.home.commonClient.net.request.scene.unit.CUnitUseSkillExRequest;
import com.home.commonClient.net.request.scene.unit.CUnitUseSkillRequest;
import com.home.commonClient.net.request.scene.unit.building.CBuildingCancelLevelUpRequest;
import com.home.commonClient.net.request.scene.unit.building.CBuildingLevelUpRequest;
import com.home.commonClient.net.request.social.QueryPlayerRequest;
import com.home.commonClient.net.request.social.SearchPlayerRequest;
import com.home.commonClient.net.request.social.UpdateRoleSocialDataRequest;
import com.home.commonClient.net.request.social.chat.PlayerChatRequest;
import com.home.commonClient.net.request.social.chat.PlayerReceiveChatIndexRequest;
import com.home.commonClient.net.request.social.friend.AddAllFriendRequest;
import com.home.commonClient.net.request.social.friend.AddFriendBlackListRequest;
import com.home.commonClient.net.request.social.friend.AddFriendRequest;
import com.home.commonClient.net.request.social.friend.AgreeAddFriendRequest;
import com.home.commonClient.net.request.social.friend.RefuseApplyAddFriendRequest;
import com.home.commonClient.net.request.social.friend.RemoveFriendBlackListRequest;
import com.home.commonClient.net.request.social.friend.RemoveFriendRequest;
import com.home.commonClient.net.request.system.CenterTransClientToGameRequest;
import com.home.commonClient.net.request.system.ClientGMRequest;
import com.home.commonClient.net.request.system.SaveBooleanRequest;
import com.home.commonClient.net.request.system.SaveIntRequest;
import com.home.commonClient.net.request.system.SaveLongRequest;
import com.home.commonClient.net.request.system.SaveStringRequest;
import com.home.commonClient.net.request.system.SendClientLogRequest;
import com.home.commonClient.net.request.system.SendClientOfflineWorkRequest;
import com.home.shine.data.BaseData;
import com.home.shine.tool.CreateDataFunc;
import com.home.shine.tool.DataMaker;

/** (generated by shine) */
public class GameRequestMaker extends DataMaker
{
	public GameRequestMaker()
	{
		offSet=GameRequestType.off;
		list=new CreateDataFunc[GameRequestType.count-offSet];
		list[GameRequestType.AcceptQuest-offSet]=this::createAcceptQuestRequest;
		list[GameRequestType.AddAllFriend-offSet]=this::createAddAllFriendRequest;
		list[GameRequestType.AddFriendBlackList-offSet]=this::createAddFriendBlackListRequest;
		list[GameRequestType.AddFriend-offSet]=this::createAddFriendRequest;
		list[GameRequestType.AgreeAddFriend-offSet]=this::createAgreeAddFriendRequest;
		list[GameRequestType.ApplyBindPlatform-offSet]=this::createApplyBindPlatformRequest;
		list[GameRequestType.ApplyChangeRoleName-offSet]=this::createApplyChangeRoleNameRequest;
		list[GameRequestType.ApplyEnterScene-offSet]=this::createApplyEnterSceneRequest;
		list[GameRequestType.ApplyLeaveScene-offSet]=this::createApplyLeaveSceneRequest;
		list[GameRequestType.CBuildingCancelLevelUp-offSet]=this::createCBuildingCancelLevelUpRequest;
		list[GameRequestType.CBuildingLevelUp-offSet]=this::createCBuildingLevelUpRequest;
		list[GameRequestType.CUnitAddBullet-offSet]=this::createCUnitAddBulletRequest;
		list[GameRequestType.CUnitAttack-offSet]=this::createCUnitAttackRequest;
		list[GameRequestType.CUnitBulletHit-offSet]=this::createCUnitBulletHitRequest;
		list[GameRequestType.CUnitChat-offSet]=this::createCUnitChatRequest;
		list[GameRequestType.CUnitKillSelf-offSet]=this::createCUnitKillSelfRequest;
		list[GameRequestType.CUnitMoveDir-offSet]=this::createCUnitMoveDirRequest;
		list[GameRequestType.CUnitMovePosList-offSet]=this::createCUnitMovePosListRequest;
		list[GameRequestType.CUnitMovePos-offSet]=this::createCUnitMovePosRequest;
		list[GameRequestType.CUnitOperate-offSet]=this::createCUnitOperateRequest;
		list[GameRequestType.CUnitPickUpItem-offSet]=this::createCUnitPickUpItemRequest;
		list[GameRequestType.CUnitPreBattleSure-offSet]=this::createCUnitPreBattleSureRequest;
		list[GameRequestType.CUnitR-offSet]=this::createCUnitRRequest;
		list[GameRequestType.CUnitRemoveBullet-offSet]=this::createCUnitRemoveBulletRequest;
		list[GameRequestType.CUnitSkillOver-offSet]=this::createCUnitSkillOverRequest;
		list[GameRequestType.CUnitSkillStep-offSet]=this::createCUnitSkillStepRequest;
		list[GameRequestType.CUnitSpecialMove-offSet]=this::createCUnitSpecialMoveRequest;
		list[GameRequestType.CUnitStopMove-offSet]=this::createCUnitStopMoveRequest;
		list[GameRequestType.CUnitSyncCommand-offSet]=this::createCUnitSyncCommandRequest;
		list[GameRequestType.CUnitUseSkillEx-offSet]=this::createCUnitUseSkillExRequest;
		list[GameRequestType.CUnitUseSkill-offSet]=this::createCUnitUseSkillRequest;
		list[GameRequestType.CenterTransClientToGame-offSet]=this::createCenterTransClientToGameRequest;
		list[GameRequestType.ClientGM-offSet]=this::createClientGMRequest;
		list[GameRequestType.ClientSceneRadio-offSet]=this::createClientSceneRadioRequest;
		list[GameRequestType.ClientSendMail-offSet]=this::createClientSendMailRequest;
		list[GameRequestType.ClientTaskEvent-offSet]=this::createClientTaskEventRequest;
		list[GameRequestType.CommitQuest-offSet]=this::createCommitQuestRequest;
		list[GameRequestType.CreatePlayer-offSet]=this::createCreatePlayerRequest;
		list[GameRequestType.DeleteMail-offSet]=this::createDeleteMailRequest;
		list[GameRequestType.DeletePlayer-offSet]=this::createDeletePlayerRequest;
		list[GameRequestType.Exchange-offSet]=this::createExchangeRequest;
		list[GameRequestType.FrameSyncOne-offSet]=this::createFrameSyncOneRequest;
		list[GameRequestType.FuncAcceptMatch-offSet]=this::createFuncAcceptMatchRequest;
		list[GameRequestType.FuncApplyCancelMatch-offSet]=this::createFuncApplyCancelMatchRequest;
		list[GameRequestType.FuncApplyMatch-offSet]=this::createFuncApplyMatchRequest;
		list[GameRequestType.FuncCleanUpItem-offSet]=this::createFuncCleanUpItemRequest;
		list[GameRequestType.FuncCreateRoleGroup-offSet]=this::createFuncCreateRoleGroupRequest;
		list[GameRequestType.FuncGetPageShow-offSet]=this::createFuncGetPageShowRequest;
		list[GameRequestType.FuncGetSubsectionPageShow-offSet]=this::createFuncGetSubsectionPageShowRequest;
		list[GameRequestType.FuncItemRemoveRedPoint-offSet]=this::createFuncItemRemoveRedPointRequest;
		list[GameRequestType.FuncMoveEquip-offSet]=this::createFuncMoveEquipRequest;
		list[GameRequestType.FuncMoveItem-offSet]=this::createFuncMoveItemRequest;
		list[GameRequestType.FuncPutOffEquip-offSet]=this::createFuncPutOffEquipRequest;
		list[GameRequestType.FuncPutOnEquip-offSet]=this::createFuncPutOnEquipRequest;
		list[GameRequestType.FuncR-offSet]=this::createFuncRRequest;
		list[GameRequestType.FuncSplitItem-offSet]=this::createFuncSplitItemRequest;
		list[GameRequestType.FuncUseItem-offSet]=this::createFuncUseItemRequest;
		list[GameRequestType.GetAchievementReward-offSet]=this::createGetAchievementRewardRequest;
		list[GameRequestType.GetAllMail-offSet]=this::createGetAllMailRequest;
		list[GameRequestType.GiveUpQuest-offSet]=this::createGiveUpQuestRequest;
		list[GameRequestType.LoginGame-offSet]=this::createLoginGameRequest;
		list[GameRequestType.Login-offSet]=this::createLoginRequest;
		list[GameRequestType.MUnitAttributeSwitchNormalSend-offSet]=this::createMUnitAttributeSwitchNormalSendRequest;
		list[GameRequestType.MailReaded-offSet]=this::createMailReadedRequest;
		list[GameRequestType.MailRemoveRedPoint-offSet]=this::createMailRemoveRedPointRequest;
		list[GameRequestType.PlayerExit-offSet]=this::createPlayerExitRequest;
		list[GameRequestType.PlayerLoginForOffline-offSet]=this::createPlayerLoginForOfflineRequest;
		list[GameRequestType.PlayerLogin-offSet]=this::createPlayerLoginRequest;
		list[GameRequestType.PlayerSwitchGame-offSet]=this::createPlayerSwitchGameRequest;
		list[GameRequestType.PreEnterSceneReady-offSet]=this::createPreEnterSceneReadyRequest;
		list[GameRequestType.QueryPlayer-offSet]=this::createQueryPlayerRequest;
		list[GameRequestType.RefuseApplyAddFriend-offSet]=this::createRefuseApplyAddFriendRequest;
		list[GameRequestType.RemoveFriendBlackList-offSet]=this::createRemoveFriendBlackListRequest;
		list[GameRequestType.RemoveFriend-offSet]=this::createRemoveFriendRequest;
		list[GameRequestType.SaveBoolean-offSet]=this::createSaveBooleanRequest;
		list[GameRequestType.SaveInt-offSet]=this::createSaveIntRequest;
		list[GameRequestType.SaveLong-offSet]=this::createSaveLongRequest;
		list[GameRequestType.SaveString-offSet]=this::createSaveStringRequest;
		list[GameRequestType.SceneR-offSet]=this::createSceneRRequest;
		list[GameRequestType.SearchPlayer-offSet]=this::createSearchPlayerRequest;
		list[GameRequestType.SellItem-offSet]=this::createSellItemRequest;
		list[GameRequestType.SendClientLog-offSet]=this::createSendClientLogRequest;
		list[GameRequestType.SendClientOfflineWork-offSet]=this::createSendClientOfflineWorkRequest;
		list[GameRequestType.SetMainGuideStep-offSet]=this::createSetMainGuideStepRequest;
		list[GameRequestType.TakeMail-offSet]=this::createTakeMailRequest;
		list[GameRequestType.UpdateRoleSocialData-offSet]=this::createUpdateRoleSocialDataRequest;
		list[GameRequestType.FuncApplyRoleGroup-offSet]=this::createFuncApplyRoleGroupRequest;
		list[GameRequestType.FuncDisbandRoleGroup-offSet]=this::createFuncDisbandRoleGroupRequest;
		list[GameRequestType.FuncInviteRoleGroup-offSet]=this::createFuncInviteRoleGroupRequest;
		list[GameRequestType.FuncLeaveRoleGroup-offSet]=this::createFuncLeaveRoleGroupRequest;
		list[GameRequestType.FuncPlayerRoleGroupR-offSet]=this::createFuncPlayerRoleGroupRRequest;
		list[GameRequestType.FuncHandleApplyRoleGroup-offSet]=this::createFuncHandleApplyRoleGroupRequest;
		list[GameRequestType.FuncHandleInviteRoleGroup-offSet]=this::createFuncHandleInviteRoleGroupRequest;
		list[GameRequestType.FuncChangeLeaderRoleGroup-offSet]=this::createFuncChangeLeaderRoleGroupRequest;
		list[GameRequestType.FuncKickMemberRoleGroup-offSet]=this::createFuncKickMemberRoleGroupRequest;
		list[GameRequestType.FuncSetTitleRoleGroup-offSet]=this::createFuncSetTitleRoleGroupRequest;
		list[GameRequestType.FuncChangeRoleGroupName-offSet]=this::createFuncChangeRoleGroupNameRequest;
		list[GameRequestType.FuncChangeRoleGroupNotice-offSet]=this::createFuncChangeRoleGroupNoticeRequest;
		list[GameRequestType.UseActivationCode-offSet]=this::createUseActivationCodeRequest;
		list[GameRequestType.MUnitR-offSet]=this::createMUnitRRequest;
		list[GameRequestType.FuncChangeRoleGroupCanInviteInAbs-offSet]=this::createFuncChangeRoleGroupCanInviteInAbsRequest;
		list[GameRequestType.FuncChangeRoleGroupCanApplyInAbs-offSet]=this::createFuncChangeRoleGroupCanApplyInAbsRequest;
		list[GameRequestType.FuncGetRoleGroupData-offSet]=this::createFuncGetRoleGroupDataRequest;
		list[GameRequestType.CUnitPickUpItemBagAll-offSet]=this::createCUnitPickUpItemBagAllRequest;
		list[GameRequestType.CRoleR-offSet]=this::createCRoleRRequest;
		list[GameRequestType.PlayerChat-offSet]=this::createPlayerChatRequest;
		list[GameRequestType.PlayerReceiveChatIndex-offSet]=this::createPlayerReceiveChatIndexRequest;
		list[GameRequestType.CUnitGetOffVehicle-offSet]=this::createCUnitGetOffVehicleRequest;
		list[GameRequestType.CUnitGetOnVehicle-offSet]=this::createCUnitGetOnVehicleRequest;
		list[GameRequestType.CUnitDrive-offSet]=this::createCUnitDriveRequest;
		list[GameRequestType.PlayerReconnectLogin-offSet]=this::createPlayerReconnectLoginRequest;
		list[GameRequestType.FuncAuctionBuyItem-offSet]=this::createFuncAuctionBuyItemRequest;
		list[GameRequestType.PetRest-offSet]=this::createPetRestRequest;
		list[GameRequestType.PetWork-offSet]=this::createPetWorkRequest;
		list[GameRequestType.FuncAuctionSellItem-offSet]=this::createFuncAuctionSellItemRequest;
		list[GameRequestType.FuncGetAuctionItemSuggestPrice-offSet]=this::createFuncGetAuctionItemSuggestPriceRequest;
		list[GameRequestType.FuncAuctionCancelSellItem-offSet]=this::createFuncAuctionCancelSellItemRequest;
		list[GameRequestType.FuncAuctionQuery-offSet]=this::createFuncAuctionQueryRequest;
		list[GameRequestType.FuncRoleGroupEnterOwnScene-offSet]=this::createFuncRoleGroupEnterOwnSceneRequest;
	}
	
	private BaseData createAcceptQuestRequest()
	{
		return new AcceptQuestRequest();
	}
	
	private BaseData createAddAllFriendRequest()
	{
		return new AddAllFriendRequest();
	}
	
	private BaseData createAddFriendBlackListRequest()
	{
		return new AddFriendBlackListRequest();
	}
	
	private BaseData createAddFriendRequest()
	{
		return new AddFriendRequest();
	}
	
	private BaseData createAgreeAddFriendRequest()
	{
		return new AgreeAddFriendRequest();
	}
	
	private BaseData createApplyBindPlatformRequest()
	{
		return new ApplyBindPlatformRequest();
	}
	
	private BaseData createApplyChangeRoleNameRequest()
	{
		return new ApplyChangeRoleNameRequest();
	}
	
	private BaseData createApplyEnterSceneRequest()
	{
		return new ApplyEnterSceneRequest();
	}
	
	private BaseData createApplyLeaveSceneRequest()
	{
		return new ApplyLeaveSceneRequest();
	}
	
	private BaseData createCBuildingCancelLevelUpRequest()
	{
		return new CBuildingCancelLevelUpRequest();
	}
	
	private BaseData createCBuildingLevelUpRequest()
	{
		return new CBuildingLevelUpRequest();
	}
	
	private BaseData createCUnitAddBulletRequest()
	{
		return new CUnitAddBulletRequest();
	}
	
	private BaseData createCUnitAttackRequest()
	{
		return new CUnitAttackRequest();
	}
	
	private BaseData createCUnitBulletHitRequest()
	{
		return new CUnitBulletHitRequest();
	}
	
	private BaseData createCUnitChatRequest()
	{
		return new CUnitChatRequest();
	}
	
	private BaseData createCUnitKillSelfRequest()
	{
		return new CUnitKillSelfRequest();
	}
	
	private BaseData createCUnitMoveDirRequest()
	{
		return new CUnitMoveDirRequest();
	}
	
	private BaseData createCUnitMovePosListRequest()
	{
		return new CUnitMovePosListRequest();
	}
	
	private BaseData createCUnitMovePosRequest()
	{
		return new CUnitMovePosRequest();
	}
	
	private BaseData createCUnitOperateRequest()
	{
		return new CUnitOperateRequest();
	}
	
	private BaseData createCUnitPickUpItemRequest()
	{
		return new CUnitPickUpItemRequest();
	}
	
	private BaseData createCUnitPreBattleSureRequest()
	{
		return new CUnitPreBattleSureRequest();
	}
	
	private BaseData createCUnitRRequest()
	{
		return new CUnitRRequest();
	}
	
	private BaseData createCUnitRemoveBulletRequest()
	{
		return new CUnitRemoveBulletRequest();
	}
	
	private BaseData createCUnitSkillOverRequest()
	{
		return new CUnitSkillOverRequest();
	}
	
	private BaseData createCUnitSkillStepRequest()
	{
		return new CUnitSkillStepRequest();
	}
	
	private BaseData createCUnitSpecialMoveRequest()
	{
		return new CUnitSpecialMoveRequest();
	}
	
	private BaseData createCUnitStopMoveRequest()
	{
		return new CUnitStopMoveRequest();
	}
	
	private BaseData createCUnitSyncCommandRequest()
	{
		return new CUnitSyncCommandRequest();
	}
	
	private BaseData createCUnitUseSkillExRequest()
	{
		return new CUnitUseSkillExRequest();
	}
	
	private BaseData createCUnitUseSkillRequest()
	{
		return new CUnitUseSkillRequest();
	}
	
	private BaseData createCenterTransClientToGameRequest()
	{
		return new CenterTransClientToGameRequest();
	}
	
	private BaseData createClientGMRequest()
	{
		return new ClientGMRequest();
	}
	
	private BaseData createClientSceneRadioRequest()
	{
		return new ClientSceneRadioRequest();
	}
	
	private BaseData createClientSendMailRequest()
	{
		return new ClientSendMailRequest();
	}
	
	private BaseData createClientTaskEventRequest()
	{
		return new ClientTaskEventRequest();
	}
	
	private BaseData createCommitQuestRequest()
	{
		return new CommitQuestRequest();
	}
	
	private BaseData createCreatePlayerRequest()
	{
		return new CreatePlayerRequest();
	}
	
	private BaseData createDeleteMailRequest()
	{
		return new DeleteMailRequest();
	}
	
	private BaseData createDeletePlayerRequest()
	{
		return new DeletePlayerRequest();
	}
	
	private BaseData createExchangeRequest()
	{
		return new ExchangeRequest();
	}
	
	private BaseData createFrameSyncOneRequest()
	{
		return new FrameSyncOneRequest();
	}
	
	private BaseData createFuncAcceptMatchRequest()
	{
		return new FuncAcceptMatchRequest();
	}
	
	private BaseData createFuncApplyCancelMatchRequest()
	{
		return new FuncApplyCancelMatchRequest();
	}
	
	private BaseData createFuncApplyMatchRequest()
	{
		return new FuncApplyMatchRequest();
	}
	
	private BaseData createFuncCleanUpItemRequest()
	{
		return new FuncCleanUpItemRequest();
	}
	
	private BaseData createFuncCreateRoleGroupRequest()
	{
		return new FuncCreateRoleGroupRequest();
	}
	
	private BaseData createFuncGetPageShowRequest()
	{
		return new FuncGetPageShowRequest();
	}
	
	private BaseData createFuncGetSubsectionPageShowRequest()
	{
		return new FuncGetSubsectionPageShowRequest();
	}
	
	private BaseData createFuncItemRemoveRedPointRequest()
	{
		return new FuncItemRemoveRedPointRequest();
	}
	
	private BaseData createFuncMoveEquipRequest()
	{
		return new FuncMoveEquipRequest();
	}
	
	private BaseData createFuncMoveItemRequest()
	{
		return new FuncMoveItemRequest();
	}
	
	private BaseData createFuncPutOffEquipRequest()
	{
		return new FuncPutOffEquipRequest();
	}
	
	private BaseData createFuncPutOnEquipRequest()
	{
		return new FuncPutOnEquipRequest();
	}
	
	private BaseData createFuncRRequest()
	{
		return new FuncRRequest();
	}
	
	private BaseData createFuncSplitItemRequest()
	{
		return new FuncSplitItemRequest();
	}
	
	private BaseData createFuncUseItemRequest()
	{
		return new FuncUseItemRequest();
	}
	
	private BaseData createGetAchievementRewardRequest()
	{
		return new GetAchievementRewardRequest();
	}
	
	private BaseData createGetAllMailRequest()
	{
		return new GetAllMailRequest();
	}
	
	private BaseData createGiveUpQuestRequest()
	{
		return new GiveUpQuestRequest();
	}
	
	private BaseData createLoginGameRequest()
	{
		return new LoginGameRequest();
	}
	
	private BaseData createLoginRequest()
	{
		return new LoginRequest();
	}
	
	private BaseData createMUnitAttributeSwitchNormalSendRequest()
	{
		return new MUnitAttributeSwitchNormalSendRequest();
	}
	
	private BaseData createMailReadedRequest()
	{
		return new MailReadedRequest();
	}
	
	private BaseData createMailRemoveRedPointRequest()
	{
		return new MailRemoveRedPointRequest();
	}
	
	private BaseData createPlayerExitRequest()
	{
		return new PlayerExitRequest();
	}
	
	private BaseData createPlayerLoginForOfflineRequest()
	{
		return new PlayerLoginForOfflineRequest();
	}
	
	private BaseData createPlayerLoginRequest()
	{
		return new PlayerLoginRequest();
	}
	
	private BaseData createPlayerSwitchGameRequest()
	{
		return new PlayerSwitchGameRequest();
	}
	
	private BaseData createPreEnterSceneReadyRequest()
	{
		return new PreEnterSceneReadyRequest();
	}
	
	private BaseData createQueryPlayerRequest()
	{
		return new QueryPlayerRequest();
	}
	
	private BaseData createRefuseApplyAddFriendRequest()
	{
		return new RefuseApplyAddFriendRequest();
	}
	
	private BaseData createRemoveFriendBlackListRequest()
	{
		return new RemoveFriendBlackListRequest();
	}
	
	private BaseData createRemoveFriendRequest()
	{
		return new RemoveFriendRequest();
	}
	
	private BaseData createSaveBooleanRequest()
	{
		return new SaveBooleanRequest();
	}
	
	private BaseData createSaveIntRequest()
	{
		return new SaveIntRequest();
	}
	
	private BaseData createSaveLongRequest()
	{
		return new SaveLongRequest();
	}
	
	private BaseData createSaveStringRequest()
	{
		return new SaveStringRequest();
	}
	
	private BaseData createSceneRRequest()
	{
		return new SceneRRequest();
	}
	
	private BaseData createSearchPlayerRequest()
	{
		return new SearchPlayerRequest();
	}
	
	private BaseData createSellItemRequest()
	{
		return new SellItemRequest();
	}
	
	private BaseData createSendClientLogRequest()
	{
		return new SendClientLogRequest();
	}
	
	private BaseData createSendClientOfflineWorkRequest()
	{
		return new SendClientOfflineWorkRequest();
	}
	
	private BaseData createSetMainGuideStepRequest()
	{
		return new SetMainGuideStepRequest();
	}
	
	private BaseData createTakeMailRequest()
	{
		return new TakeMailRequest();
	}
	
	private BaseData createUpdateRoleSocialDataRequest()
	{
		return new UpdateRoleSocialDataRequest();
	}
	
	private BaseData createFuncApplyRoleGroupRequest()
	{
		return new FuncApplyRoleGroupRequest();
	}
	
	private BaseData createFuncDisbandRoleGroupRequest()
	{
		return new FuncDisbandRoleGroupRequest();
	}
	
	private BaseData createFuncInviteRoleGroupRequest()
	{
		return new FuncInviteRoleGroupRequest();
	}
	
	private BaseData createFuncLeaveRoleGroupRequest()
	{
		return new FuncLeaveRoleGroupRequest();
	}
	
	private BaseData createFuncPlayerRoleGroupRRequest()
	{
		return new FuncPlayerRoleGroupRRequest();
	}
	
	private BaseData createFuncHandleApplyRoleGroupRequest()
	{
		return new FuncHandleApplyRoleGroupRequest();
	}
	
	private BaseData createFuncHandleInviteRoleGroupRequest()
	{
		return new FuncHandleInviteRoleGroupRequest();
	}
	
	private BaseData createFuncChangeLeaderRoleGroupRequest()
	{
		return new FuncChangeLeaderRoleGroupRequest();
	}
	
	private BaseData createFuncKickMemberRoleGroupRequest()
	{
		return new FuncKickMemberRoleGroupRequest();
	}
	
	private BaseData createFuncSetTitleRoleGroupRequest()
	{
		return new FuncSetTitleRoleGroupRequest();
	}
	
	private BaseData createFuncChangeRoleGroupNameRequest()
	{
		return new FuncChangeRoleGroupNameRequest();
	}
	
	private BaseData createFuncChangeRoleGroupNoticeRequest()
	{
		return new FuncChangeRoleGroupNoticeRequest();
	}
	
	private BaseData createUseActivationCodeRequest()
	{
		return new UseActivationCodeRequest();
	}
	
	private BaseData createMUnitRRequest()
	{
		return new MUnitRRequest();
	}
	
	private BaseData createFuncChangeRoleGroupCanInviteInAbsRequest()
	{
		return new FuncChangeRoleGroupCanInviteInAbsRequest();
	}
	
	private BaseData createFuncChangeRoleGroupCanApplyInAbsRequest()
	{
		return new FuncChangeRoleGroupCanApplyInAbsRequest();
	}
	
	private BaseData createFuncGetRoleGroupDataRequest()
	{
		return new FuncGetRoleGroupDataRequest();
	}
	
	private BaseData createCUnitPickUpItemBagAllRequest()
	{
		return new CUnitPickUpItemBagAllRequest();
	}
	
	private BaseData createCRoleRRequest()
	{
		return new CRoleRRequest();
	}
	
	private BaseData createPlayerChatRequest()
	{
		return new PlayerChatRequest();
	}
	
	private BaseData createPlayerReceiveChatIndexRequest()
	{
		return new PlayerReceiveChatIndexRequest();
	}
	
	private BaseData createCUnitGetOffVehicleRequest()
	{
		return new CUnitGetOffVehicleRequest();
	}
	
	private BaseData createCUnitGetOnVehicleRequest()
	{
		return new CUnitGetOnVehicleRequest();
	}
	
	private BaseData createCUnitDriveRequest()
	{
		return new CUnitDriveRequest();
	}
	
	private BaseData createPlayerReconnectLoginRequest()
	{
		return new PlayerReconnectLoginRequest();
	}
	
	private BaseData createFuncAuctionBuyItemRequest()
	{
		return new FuncAuctionBuyItemRequest();
	}
	
	private BaseData createPetRestRequest()
	{
		return new PetRestRequest();
	}
	
	private BaseData createPetWorkRequest()
	{
		return new PetWorkRequest();
	}
	
	private BaseData createFuncAuctionSellItemRequest()
	{
		return new FuncAuctionSellItemRequest();
	}
	
	private BaseData createFuncGetAuctionItemSuggestPriceRequest()
	{
		return new FuncGetAuctionItemSuggestPriceRequest();
	}
	
	private BaseData createFuncAuctionCancelSellItemRequest()
	{
		return new FuncAuctionCancelSellItemRequest();
	}
	
	private BaseData createFuncAuctionQueryRequest()
	{
		return new FuncAuctionQueryRequest();
	}
	
	private BaseData createFuncRoleGroupEnterOwnSceneRequest()
	{
		return new FuncRoleGroupEnterOwnSceneRequest();
	}
	
}
