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
import com.home.commonGame.net.response.scene.ApplyEnterSceneResponse;
import com.home.commonGame.net.response.scene.ApplyLeaveSceneResponse;
import com.home.commonGame.net.response.scene.PreEnterSceneReadyResponse;
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
		list[GameResponseType.CenterTransClientToGame-offSet]=this::createCenterTransClientToGameResponse;
		list[GameResponseType.ClientGM-offSet]=this::createClientGMResponse;
		list[GameResponseType.ClientSendMail-offSet]=this::createClientSendMailResponse;
		list[GameResponseType.ClientTaskEvent-offSet]=this::createClientTaskEventResponse;
		list[GameResponseType.CommitQuest-offSet]=this::createCommitQuestResponse;
		list[GameResponseType.CreatePlayer-offSet]=this::createCreatePlayerResponse;
		list[GameResponseType.DeleteMail-offSet]=this::createDeleteMailResponse;
		list[GameResponseType.DeletePlayer-offSet]=this::createDeletePlayerResponse;
		list[GameResponseType.Exchange-offSet]=this::createExchangeResponse;
		list[GameResponseType.FuncAcceptMatch-offSet]=this::createFuncAcceptMatchResponse;
		list[GameResponseType.FuncApplyCancelMatch-offSet]=this::createFuncApplyCancelMatchResponse;
		list[GameResponseType.FuncApplyMatch-offSet]=this::createFuncApplyMatchResponse;
		list[GameResponseType.FuncApplyRoleGroup-offSet]=this::createFuncApplyRoleGroupResponse;
		list[GameResponseType.FuncAuctionBuyItem-offSet]=this::createFuncAuctionBuyItemResponse;
		list[GameResponseType.FuncAuctionCancelSellItem-offSet]=this::createFuncAuctionCancelSellItemResponse;
		list[GameResponseType.FuncAuctionQuery-offSet]=this::createFuncAuctionQueryResponse;
		list[GameResponseType.FuncAuctionSellItem-offSet]=this::createFuncAuctionSellItemResponse;
		list[GameResponseType.FuncChangeLeaderRoleGroup-offSet]=this::createFuncChangeLeaderRoleGroupResponse;
		list[GameResponseType.FuncChangeRoleGroupCanApplyInAbs-offSet]=this::createFuncChangeRoleGroupCanApplyInAbsResponse;
		list[GameResponseType.FuncChangeRoleGroupCanInviteInAbs-offSet]=this::createFuncChangeRoleGroupCanInviteInAbsResponse;
		list[GameResponseType.FuncChangeRoleGroupName-offSet]=this::createFuncChangeRoleGroupNameResponse;
		list[GameResponseType.FuncChangeRoleGroupNotice-offSet]=this::createFuncChangeRoleGroupNoticeResponse;
		list[GameResponseType.FuncCleanUpItem-offSet]=this::createFuncCleanUpItemResponse;
		list[GameResponseType.FuncCreateRoleGroup-offSet]=this::createFuncCreateRoleGroupResponse;
		list[GameResponseType.FuncDisbandRoleGroup-offSet]=this::createFuncDisbandRoleGroupResponse;
		list[GameResponseType.FuncGetAuctionItemSuggestPrice-offSet]=this::createFuncGetAuctionItemSuggestPriceResponse;
		list[GameResponseType.FuncGetPageShow-offSet]=this::createFuncGetPageShowResponse;
		list[GameResponseType.FuncGetRoleGroupData-offSet]=this::createFuncGetRoleGroupDataResponse;
		list[GameResponseType.FuncGetSubsectionPageShow-offSet]=this::createFuncGetSubsectionPageShowResponse;
		list[GameResponseType.FuncHandleApplyRoleGroup-offSet]=this::createFuncHandleApplyRoleGroupResponse;
		list[GameResponseType.FuncHandleInviteRoleGroup-offSet]=this::createFuncHandleInviteRoleGroupResponse;
		list[GameResponseType.FuncInviteRoleGroup-offSet]=this::createFuncInviteRoleGroupResponse;
		list[GameResponseType.FuncItemRemoveRedPoint-offSet]=this::createFuncItemRemoveRedPointResponse;
		list[GameResponseType.FuncKickMemberRoleGroup-offSet]=this::createFuncKickMemberRoleGroupResponse;
		list[GameResponseType.FuncLeaveRoleGroup-offSet]=this::createFuncLeaveRoleGroupResponse;
		list[GameResponseType.FuncMoveEquip-offSet]=this::createFuncMoveEquipResponse;
		list[GameResponseType.FuncMoveItem-offSet]=this::createFuncMoveItemResponse;
		list[GameResponseType.FuncPlayerRoleGroupR-offSet]=this::createFuncPlayerRoleGroupRResponse;
		list[GameResponseType.FuncPutOffEquip-offSet]=this::createFuncPutOffEquipResponse;
		list[GameResponseType.FuncPutOnEquip-offSet]=this::createFuncPutOnEquipResponse;
		list[GameResponseType.FuncR-offSet]=this::createFuncRResponse;
		list[GameResponseType.FuncRoleGroupEnterOwnScene-offSet]=this::createFuncRoleGroupEnterOwnSceneResponse;
		list[GameResponseType.FuncSetTitleRoleGroup-offSet]=this::createFuncSetTitleRoleGroupResponse;
		list[GameResponseType.FuncSplitItem-offSet]=this::createFuncSplitItemResponse;
		list[GameResponseType.FuncUseItem-offSet]=this::createFuncUseItemResponse;
		list[GameResponseType.GetAchievementReward-offSet]=this::createGetAchievementRewardResponse;
		list[GameResponseType.GetAllMail-offSet]=this::createGetAllMailResponse;
		list[GameResponseType.GiveUpQuest-offSet]=this::createGiveUpQuestResponse;
		list[GameResponseType.LoginGame-offSet]=this::createLoginGameResponse;
		list[GameResponseType.Login-offSet]=this::createLoginResponse;
		list[GameResponseType.MUnitAttributeSwitchNormalSend-offSet]=this::createMUnitAttributeSwitchNormalSendResponse;
		list[GameResponseType.MUnitR-offSet]=this::createMUnitRResponse;
		list[GameResponseType.MailReaded-offSet]=this::createMailReadedResponse;
		list[GameResponseType.MailRemoveRedPoint-offSet]=this::createMailRemoveRedPointResponse;
		list[GameResponseType.PetRest-offSet]=this::createPetRestResponse;
		list[GameResponseType.PetWork-offSet]=this::createPetWorkResponse;
		list[GameResponseType.PlayerChat-offSet]=this::createPlayerChatResponse;
		list[GameResponseType.PlayerExit-offSet]=this::createPlayerExitResponse;
		list[GameResponseType.PlayerLoginForOffline-offSet]=this::createPlayerLoginForOfflineResponse;
		list[GameResponseType.PlayerLogin-offSet]=this::createPlayerLoginResponse;
		list[GameResponseType.PlayerReceiveChatIndex-offSet]=this::createPlayerReceiveChatIndexResponse;
		list[GameResponseType.PlayerReconnectLogin-offSet]=this::createPlayerReconnectLoginResponse;
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
		list[GameResponseType.SearchPlayer-offSet]=this::createSearchPlayerResponse;
		list[GameResponseType.SellItem-offSet]=this::createSellItemResponse;
		list[GameResponseType.SendClientLog-offSet]=this::createSendClientLogResponse;
		list[GameResponseType.SendClientOfflineWork-offSet]=this::createSendClientOfflineWorkResponse;
		list[GameResponseType.SetMainGuideStep-offSet]=this::createSetMainGuideStepResponse;
		list[GameResponseType.TakeMail-offSet]=this::createTakeMailResponse;
		list[GameResponseType.UpdateRoleSocialData-offSet]=this::createUpdateRoleSocialDataResponse;
		list[GameResponseType.UseActivationCode-offSet]=this::createUseActivationCodeResponse;
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
	
	private BaseData createClientGMResponse()
	{
		return new ClientGMResponse();
	}
	
	private BaseData createLoginResponse()
	{
		return new LoginResponse();
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
	
	private BaseData createMUnitAttributeSwitchNormalSendResponse()
	{
		return new MUnitAttributeSwitchNormalSendResponse();
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
	
	private BaseData createPlayerChatResponse()
	{
		return new PlayerChatResponse();
	}
	
	private BaseData createPlayerReceiveChatIndexResponse()
	{
		return new PlayerReceiveChatIndexResponse();
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
