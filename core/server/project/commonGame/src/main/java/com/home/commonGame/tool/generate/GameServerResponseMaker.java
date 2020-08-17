package com.home.commonGame.tool.generate;
import com.home.commonBase.constlist.generate.ServerMessageType;
import com.home.commonGame.net.serverResponse.center.activity.ActivityResetToGameServerResponse;
import com.home.commonGame.net.serverResponse.center.activity.ActivitySwitchToGameServerResponse;
import com.home.commonGame.net.serverResponse.center.activity.ChangeActivityForceCloseToGameServerResponse;
import com.home.commonGame.net.serverResponse.center.base.PlayerToGameServerResponse;
import com.home.commonGame.net.serverResponse.center.func.auction.FuncRefreshAuctionItemPriceToGameServerResponse;
import com.home.commonGame.net.serverResponse.center.func.base.FuncCenterToGameServerResponse;
import com.home.commonGame.net.serverResponse.center.func.base.FuncPlayerToGameServerResponse;
import com.home.commonGame.net.serverResponse.center.func.match.FuncMatchTimeOutToGameServerResponse;
import com.home.commonGame.net.serverResponse.center.func.pageShow.FuncReGetPageShowToPlayerServerResponse;
import com.home.commonGame.net.serverResponse.center.func.rank.FuncAddRankToGameServerResponse;
import com.home.commonGame.net.serverResponse.center.func.rank.FuncRefreshRankToGameServerResponse;
import com.home.commonGame.net.serverResponse.center.func.rank.FuncRemoveRankToGameServerResponse;
import com.home.commonGame.net.serverResponse.center.func.rank.FuncResetRankToGameServerResponse;
import com.home.commonGame.net.serverResponse.center.func.rank.subsection.FuncAddSubsectionRankToGameServerResponse;
import com.home.commonGame.net.serverResponse.center.func.rank.subsection.FuncRefreshSubsectionIndexToGameServerResponse;
import com.home.commonGame.net.serverResponse.center.func.rank.subsection.FuncRefreshSubsectionRankToGameServerResponse;
import com.home.commonGame.net.serverResponse.center.func.rank.subsection.FuncRemoveSubsectionRankToGameServerResponse;
import com.home.commonGame.net.serverResponse.center.func.rank.subsection.FuncResetSubsectionRankToGameServerResponse;
import com.home.commonGame.net.serverResponse.center.func.social.FuncReGetRandomPlayerListFromRoleSocialPoolToCenterServerResponse;
import com.home.commonGame.net.serverResponse.center.login.KickPlayerFromCenterServerResponse;
import com.home.commonGame.net.serverResponse.center.mail.SendMailToGameServerResponse;
import com.home.commonGame.net.serverResponse.center.scene.CreateSignedSceneToGameServerResponse;
import com.home.commonGame.net.serverResponse.center.scene.EnterSignedSceneToGameServerResponse;
import com.home.commonGame.net.serverResponse.center.social.RefreshRoleGroupChangeToGameServerResponse;
import com.home.commonGame.net.serverResponse.center.social.RefreshRoleShowDataChangeToGameServerResponse;
import com.home.commonGame.net.serverResponse.center.social.RemoveCenterRoleSocialDataToPlayerServerResponse;
import com.home.commonGame.net.serverResponse.center.social.SendGetRoleSocialDataToGameServerResponse;
import com.home.commonGame.net.serverResponse.center.system.CenterTransCenterToGameServerResponse;
import com.home.commonGame.net.serverResponse.center.system.GameExitServerResponse;
import com.home.commonGame.net.serverResponse.center.system.GameReloadConfigServerResponse;
import com.home.commonGame.net.serverResponse.center.system.PlayerToGameTransCenterToGameServerResponse;
import com.home.commonGame.net.serverResponse.center.system.ReBeGameToCenterServerResponse;
import com.home.commonGame.net.serverResponse.center.system.ReceiptWorkToGameFromCenterServerResponse;
import com.home.commonGame.net.serverResponse.center.system.RefreshServerOffTimeToGameServerResponse;
import com.home.commonGame.net.serverResponse.center.system.SendAreaWorkToGameFromCenterServerResponse;
import com.home.commonGame.net.serverResponse.center.system.SendPlayerWorkCompleteServerResponse;
import com.home.commonGame.net.serverResponse.center.system.SendPlayerWorkServerResponse;
import com.home.commonGame.net.serverResponse.center.system.SendWorkFailedServerResponse;
import com.home.commonGame.net.serverResponse.game.base.PlayerGameToGameServerResponse;
import com.home.commonGame.net.serverResponse.game.base.SignedSceneGameToGameServerResponse;
import com.home.commonGame.net.serverResponse.game.func.auction.FuncSendAuctionBuyItemToSourceGameServerResponse;
import com.home.commonGame.net.serverResponse.game.func.auction.FuncSendAuctionCancelSellItemToSourceGameServerResponse;
import com.home.commonGame.net.serverResponse.game.func.auction.FuncSendAuctionSellItemToSourceGameServerResponse;
import com.home.commonGame.net.serverResponse.game.func.base.FuncPlayerGameToGameServerResponse;
import com.home.commonGame.net.serverResponse.game.func.base.FuncPlayerRoleGroupGameToGameServerResponse;
import com.home.commonGame.net.serverResponse.game.func.base.FuncRoleGroupToGameServerResponse;
import com.home.commonGame.net.serverResponse.game.func.base.FuncToGameServerResponse;
import com.home.commonGame.net.serverResponse.game.func.pageShow.FuncReGetPageShowGameToPlayerServerResponse;
import com.home.commonGame.net.serverResponse.game.func.pageShow.FuncSendGetPageShowToGameServerResponse;
import com.home.commonGame.net.serverResponse.game.func.rank.FuncCommitRankValueToSourceGameServerResponse;
import com.home.commonGame.net.serverResponse.game.func.rank.FuncRefreshRankForRoleGroupServerResponse;
import com.home.commonGame.net.serverResponse.game.func.rank.FuncResetRankForRoleGroupServerResponse;
import com.home.commonGame.net.serverResponse.game.func.roleGroup.FuncAddRoleGroupSimpleToGameServerResponse;
import com.home.commonGame.net.serverResponse.game.func.roleGroup.FuncAgreeApplyNextRoleGroupToGameServerResponse;
import com.home.commonGame.net.serverResponse.game.func.roleGroup.FuncAgreeInviteCreateRoleGroupToGameServerResponse;
import com.home.commonGame.net.serverResponse.game.func.roleGroup.FuncAgreeInviteRoleGroupToGameServerResponse;
import com.home.commonGame.net.serverResponse.game.func.roleGroup.FuncApplyRoleGroupToGameServerResponse;
import com.home.commonGame.net.serverResponse.game.func.roleGroup.FuncChangeLeaderRoleGroupToGameServerResponse;
import com.home.commonGame.net.serverResponse.game.func.roleGroup.FuncChangeRoleGroupApplyTypeToGameServerResponse;
import com.home.commonGame.net.serverResponse.game.func.roleGroup.FuncChangeRoleGroupNameToGameServerResponse;
import com.home.commonGame.net.serverResponse.game.func.roleGroup.FuncChangeRoleGroupNoticeToGameServerResponse;
import com.home.commonGame.net.serverResponse.game.func.roleGroup.FuncDisbandRoleGroupToGameServerResponse;
import com.home.commonGame.net.serverResponse.game.func.roleGroup.FuncHandleApplyRoleGroupToGameServerResponse;
import com.home.commonGame.net.serverResponse.game.func.roleGroup.FuncKickMemberRoleGroupToGameServerResponse;
import com.home.commonGame.net.serverResponse.game.func.roleGroup.FuncLeaveRoleGroupToGameServerResponse;
import com.home.commonGame.net.serverResponse.game.func.roleGroup.FuncRefreshTitleRoleGroupToPlayerServerResponse;
import com.home.commonGame.net.serverResponse.game.func.roleGroup.FuncRemoveRoleGroupSimpleToGameServerResponse;
import com.home.commonGame.net.serverResponse.game.func.roleGroup.FuncRoleGroupChangeSimpleToGameServerResponse;
import com.home.commonGame.net.serverResponse.game.func.roleGroup.FuncRoleGroupChangeToPlayerServerResponse;
import com.home.commonGame.net.serverResponse.game.func.roleGroup.FuncRoleGroupEnterOwnSceneToGameServerResponse;
import com.home.commonGame.net.serverResponse.game.func.roleGroup.FuncRoleGroupMemberChangeToPlayerServerResponse;
import com.home.commonGame.net.serverResponse.game.func.roleGroup.FuncRoleGroupReEnterOwnSceneArgToPlayerServerResponse;
import com.home.commonGame.net.serverResponse.game.func.roleGroup.FuncRoleGroupRefreshRoleShowDataToGameServerResponse;
import com.home.commonGame.net.serverResponse.game.func.roleGroup.FuncRoleGroupRefreshRoleShowDataToPlayerServerResponse;
import com.home.commonGame.net.serverResponse.game.func.roleGroup.FuncSendChangeLeaderRoleGroupToPlayerServerResponse;
import com.home.commonGame.net.serverResponse.game.func.roleGroup.FuncSendRoleGroupAddMemberToPlayerServerResponse;
import com.home.commonGame.net.serverResponse.game.func.roleGroup.FuncSendRoleGroupJoinResultServerResponse;
import com.home.commonGame.net.serverResponse.game.func.roleGroup.FuncSendRoleGroupRemoveMemberToPlayerServerResponse;
import com.home.commonGame.net.serverResponse.game.func.roleGroup.FuncSetTitleRoleGroupToGameServerResponse;
import com.home.commonGame.net.serverResponse.game.login.PlayerCallSwitchBackToGameServerResponse;
import com.home.commonGame.net.serverResponse.game.login.PlayerExitOverToSourceServerResponse;
import com.home.commonGame.net.serverResponse.game.login.PlayerExitSwitchBackServerResponse;
import com.home.commonGame.net.serverResponse.game.login.PlayerLoginToEachGameServerResponse;
import com.home.commonGame.net.serverResponse.game.login.PlayerPreExitToGameServerResponse;
import com.home.commonGame.net.serverResponse.game.login.PlayerPreSwitchGameToGameServerResponse;
import com.home.commonGame.net.serverResponse.game.login.PlayerSwitchGameCompleteToSourceServerResponse;
import com.home.commonGame.net.serverResponse.game.login.PlayerSwitchGameReceiveResultToSourceServerResponse;
import com.home.commonGame.net.serverResponse.game.login.PreSwitchGameServerResponse;
import com.home.commonGame.net.serverResponse.game.login.RePlayerLoginFromEachGameServerResponse;
import com.home.commonGame.net.serverResponse.game.login.RePlayerPreExitToGameServerResponse;
import com.home.commonGame.net.serverResponse.game.login.RePlayerPreSwitchGameToGameServerResponse;
import com.home.commonGame.net.serverResponse.game.login.RePreSwitchGameFailedServerResponse;
import com.home.commonGame.net.serverResponse.game.login.RePreSwitchGameServerResponse;
import com.home.commonGame.net.serverResponse.game.login.RefreshGameLoginLimitToGameServerResponse;
import com.home.commonGame.net.serverResponse.game.social.GetRoleSocialDataToPlayerServerResponse;
import com.home.commonGame.net.serverResponse.game.social.RadioPlayerChatToGameServerResponse;
import com.home.commonGame.net.serverResponse.game.social.RefreshRoleShowToSourceGameServerResponse;
import com.home.commonGame.net.serverResponse.game.social.SendPlayerChatToPlayerServerResponse;
import com.home.commonGame.net.serverResponse.game.system.BeGameToGameServerResponse;
import com.home.commonGame.net.serverResponse.game.system.PlayerToGameTransGameToGameServerResponse;
import com.home.commonGame.net.serverResponse.game.system.ReBeGameToGameServerResponse;
import com.home.commonGame.net.serverResponse.game.system.ReceiptWorkToGameServerResponse;
import com.home.commonGame.net.serverResponse.game.system.SaveSwitchedPlayerListServerResponse;
import com.home.commonGame.net.serverResponse.game.system.SendAreaWorkCompleteToGameServerResponse;
import com.home.commonGame.net.serverResponse.game.system.SendAreaWorkToGameServerResponse;
import com.home.commonGame.net.serverResponse.game.system.SendGameRequestToPlayerServerResponse;
import com.home.commonGame.net.serverResponse.game.system.SendPlayerCenterRequestListToGameServerResponse;
import com.home.commonGame.net.serverResponse.game.system.SendPlayerCenterRequestToGameServerResponse;
import com.home.commonGame.net.serverResponse.game.system.SendPlayerToGameRequestListToGameServerResponse;
import com.home.commonGame.net.serverResponse.game.system.SendPlayerToGameRequestToGameServerResponse;
import com.home.commonGame.net.serverResponse.game.system.SendPlayerWorkCompleteListToGameServerResponse;
import com.home.commonGame.net.serverResponse.game.system.SendPlayerWorkCompleteToGameServerResponse;
import com.home.commonGame.net.serverResponse.game.system.SendPlayerWorkListToGameServerResponse;
import com.home.commonGame.net.serverResponse.game.system.SendPlayerWorkToGameServerResponse;
import com.home.commonGame.net.serverResponse.login.login.PlayerBindPlatformToGameServerResponse;
import com.home.commonGame.net.serverResponse.login.login.UserLoginToGameServerResponse;
import com.home.commonGame.net.serverResponse.login.system.BeLoginToGameServerResponse;
import com.home.commonGame.net.serverResponse.login.system.ReceiptUserWorkToGameServerResponse;
import com.home.commonGame.net.serverResponse.login.system.SendInfoCodeFromLoginServerResponse;
import com.home.commonGame.net.serverResponse.manager.HotfixToGameServerResponse;
import com.home.commonGame.net.serverResponse.manager.ManagerToGameCommandServerResponse;
import com.home.commonGame.net.serverResponse.manager.ReBeGameToManagerServerResponse;
import com.home.commonGame.net.serverResponse.manager.ReloadServerConfigToGameServerResponse;
import com.home.commonGame.net.serverResponse.scene.base.PlayerSceneToGameServerResponse;
import com.home.commonGame.net.serverResponse.scene.login.PlayerLeaveSceneOverToGameServerResponse;
import com.home.commonGame.net.serverResponse.scene.login.PlayerSwitchToSceneOverServerResponse;
import com.home.commonGame.net.serverResponse.scene.login.RePlayerSwitchToSceneServerResponse;
import com.home.commonGame.net.serverResponse.scene.system.ReBeGameToSceneServerResponse;
import com.home.shine.data.BaseData;
import com.home.shine.tool.CreateDataFunc;
import com.home.shine.tool.DataMaker;

/** (generated by shine) */
public class GameServerResponseMaker extends DataMaker
{
	public GameServerResponseMaker()
	{
		offSet=ServerMessageType.off;
		list=new CreateDataFunc[ServerMessageType.count-offSet];
		list[ServerMessageType.HotfixToGame-offSet]=this::createHotfixToGameServerResponse;
		list[ServerMessageType.ManagerToGameCommand-offSet]=this::createManagerToGameCommandServerResponse;
		list[ServerMessageType.ReBeGameToManager-offSet]=this::createReBeGameToManagerServerResponse;
		list[ServerMessageType.ReloadServerConfigToGame-offSet]=this::createReloadServerConfigToGameServerResponse;
		list[ServerMessageType.ActivityResetToGame-offSet]=this::createActivityResetToGameServerResponse;
		list[ServerMessageType.ActivitySwitchToGame-offSet]=this::createActivitySwitchToGameServerResponse;
		list[ServerMessageType.CenterTransCenterToGame-offSet]=this::createCenterTransCenterToGameServerResponse;
		list[ServerMessageType.ChangeActivityForceCloseToGame-offSet]=this::createChangeActivityForceCloseToGameServerResponse;
		list[ServerMessageType.CreateSignedSceneToGame-offSet]=this::createCreateSignedSceneToGameServerResponse;
		list[ServerMessageType.EnterSignedSceneToGame-offSet]=this::createEnterSignedSceneToGameServerResponse;
		list[ServerMessageType.FuncAddRankToGame-offSet]=this::createFuncAddRankToGameServerResponse;
		list[ServerMessageType.FuncAddSubsectionRankToGame-offSet]=this::createFuncAddSubsectionRankToGameServerResponse;
		list[ServerMessageType.FuncCenterToGame-offSet]=this::createFuncCenterToGameServerResponse;
		list[ServerMessageType.FuncMatchTimeOutToGame-offSet]=this::createFuncMatchTimeOutToGameServerResponse;
		list[ServerMessageType.FuncPlayerToGame-offSet]=this::createFuncPlayerToGameServerResponse;
		list[ServerMessageType.FuncReGetPageShowToPlayer-offSet]=this::createFuncReGetPageShowToPlayerServerResponse;
		list[ServerMessageType.FuncReGetRandomPlayerListFromRoleSocialPoolToCenter-offSet]=this::createFuncReGetRandomPlayerListFromRoleSocialPoolToCenterServerResponse;
		list[ServerMessageType.FuncRefreshAuctionItemPriceToGame-offSet]=this::createFuncRefreshAuctionItemPriceToGameServerResponse;
		list[ServerMessageType.FuncRefreshRankToGame-offSet]=this::createFuncRefreshRankToGameServerResponse;
		list[ServerMessageType.FuncRefreshSubsectionIndexToGame-offSet]=this::createFuncRefreshSubsectionIndexToGameServerResponse;
		list[ServerMessageType.FuncRefreshSubsectionRankToGame-offSet]=this::createFuncRefreshSubsectionRankToGameServerResponse;
		list[ServerMessageType.FuncRemoveRankToGame-offSet]=this::createFuncRemoveRankToGameServerResponse;
		list[ServerMessageType.FuncRemoveSubsectionRankToGame-offSet]=this::createFuncRemoveSubsectionRankToGameServerResponse;
		list[ServerMessageType.FuncResetRankToGame-offSet]=this::createFuncResetRankToGameServerResponse;
		list[ServerMessageType.FuncResetSubsectionRankToGame-offSet]=this::createFuncResetSubsectionRankToGameServerResponse;
		list[ServerMessageType.GameExit-offSet]=this::createGameExitServerResponse;
		list[ServerMessageType.GameReloadConfig-offSet]=this::createGameReloadConfigServerResponse;
		list[ServerMessageType.KickPlayerFromCenter-offSet]=this::createKickPlayerFromCenterServerResponse;
		list[ServerMessageType.PlayerToGame-offSet]=this::createPlayerToGameServerResponse;
		list[ServerMessageType.PlayerToGameTransCenterToGame-offSet]=this::createPlayerToGameTransCenterToGameServerResponse;
		list[ServerMessageType.ReBeGameToCenter-offSet]=this::createReBeGameToCenterServerResponse;
		list[ServerMessageType.ReceiptWorkToGameFromCenter-offSet]=this::createReceiptWorkToGameFromCenterServerResponse;
		list[ServerMessageType.RefreshRoleGroupChangeToGame-offSet]=this::createRefreshRoleGroupChangeToGameServerResponse;
		list[ServerMessageType.RefreshRoleShowDataChangeToGame-offSet]=this::createRefreshRoleShowDataChangeToGameServerResponse;
		list[ServerMessageType.RefreshServerOffTimeToGame-offSet]=this::createRefreshServerOffTimeToGameServerResponse;
		list[ServerMessageType.RemoveCenterRoleSocialDataToPlayer-offSet]=this::createRemoveCenterRoleSocialDataToPlayerServerResponse;
		list[ServerMessageType.SendAreaWorkToGameFromCenter-offSet]=this::createSendAreaWorkToGameFromCenterServerResponse;
		list[ServerMessageType.SendGetRoleSocialDataToGame-offSet]=this::createSendGetRoleSocialDataToGameServerResponse;
		list[ServerMessageType.SendMailToGame-offSet]=this::createSendMailToGameServerResponse;
		list[ServerMessageType.SendPlayerWorkComplete-offSet]=this::createSendPlayerWorkCompleteServerResponse;
		list[ServerMessageType.SendPlayerWork-offSet]=this::createSendPlayerWorkServerResponse;
		list[ServerMessageType.SendWorkFailed-offSet]=this::createSendWorkFailedServerResponse;
		list[ServerMessageType.BeLoginToGame-offSet]=this::createBeLoginToGameServerResponse;
		list[ServerMessageType.PlayerBindPlatformToGame-offSet]=this::createPlayerBindPlatformToGameServerResponse;
		list[ServerMessageType.ReceiptUserWorkToGame-offSet]=this::createReceiptUserWorkToGameServerResponse;
		list[ServerMessageType.SendInfoCodeFromLogin-offSet]=this::createSendInfoCodeFromLoginServerResponse;
		list[ServerMessageType.UserLoginToGame-offSet]=this::createUserLoginToGameServerResponse;
		list[ServerMessageType.PlayerSceneToGame-offSet]=this::createPlayerSceneToGameServerResponse;
		list[ServerMessageType.PlayerSwitchToSceneOver-offSet]=this::createPlayerSwitchToSceneOverServerResponse;
		list[ServerMessageType.ReBeGameToScene-offSet]=this::createReBeGameToSceneServerResponse;
		list[ServerMessageType.RePlayerSwitchToScene-offSet]=this::createRePlayerSwitchToSceneServerResponse;
		list[ServerMessageType.BeGameToGame-offSet]=this::createBeGameToGameServerResponse;
		list[ServerMessageType.FuncAddRoleGroupSimpleToGame-offSet]=this::createFuncAddRoleGroupSimpleToGameServerResponse;
		list[ServerMessageType.FuncAgreeApplyNextRoleGroupToGame-offSet]=this::createFuncAgreeApplyNextRoleGroupToGameServerResponse;
		list[ServerMessageType.FuncAgreeInviteCreateRoleGroupToGame-offSet]=this::createFuncAgreeInviteCreateRoleGroupToGameServerResponse;
		list[ServerMessageType.FuncAgreeInviteRoleGroupToGame-offSet]=this::createFuncAgreeInviteRoleGroupToGameServerResponse;
		list[ServerMessageType.FuncApplyRoleGroupToGame-offSet]=this::createFuncApplyRoleGroupToGameServerResponse;
		list[ServerMessageType.FuncChangeLeaderRoleGroupToGame-offSet]=this::createFuncChangeLeaderRoleGroupToGameServerResponse;
		list[ServerMessageType.FuncChangeRoleGroupApplyTypeToGame-offSet]=this::createFuncChangeRoleGroupApplyTypeToGameServerResponse;
		list[ServerMessageType.FuncChangeRoleGroupNameToGame-offSet]=this::createFuncChangeRoleGroupNameToGameServerResponse;
		list[ServerMessageType.FuncChangeRoleGroupNoticeToGame-offSet]=this::createFuncChangeRoleGroupNoticeToGameServerResponse;
		list[ServerMessageType.FuncCommitRankValueToSourceGame-offSet]=this::createFuncCommitRankValueToSourceGameServerResponse;
		list[ServerMessageType.FuncDisbandRoleGroupToGame-offSet]=this::createFuncDisbandRoleGroupToGameServerResponse;
		list[ServerMessageType.FuncHandleApplyRoleGroupToGame-offSet]=this::createFuncHandleApplyRoleGroupToGameServerResponse;
		list[ServerMessageType.FuncKickMemberRoleGroupToGame-offSet]=this::createFuncKickMemberRoleGroupToGameServerResponse;
		list[ServerMessageType.FuncLeaveRoleGroupToGame-offSet]=this::createFuncLeaveRoleGroupToGameServerResponse;
		list[ServerMessageType.FuncPlayerGameToGame-offSet]=this::createFuncPlayerGameToGameServerResponse;
		list[ServerMessageType.FuncPlayerRoleGroupGameToGame-offSet]=this::createFuncPlayerRoleGroupGameToGameServerResponse;
		list[ServerMessageType.FuncReGetPageShowGameToPlayer-offSet]=this::createFuncReGetPageShowGameToPlayerServerResponse;
		list[ServerMessageType.FuncRefreshRankForRoleGroup-offSet]=this::createFuncRefreshRankForRoleGroupServerResponse;
		list[ServerMessageType.FuncRefreshTitleRoleGroupToPlayer-offSet]=this::createFuncRefreshTitleRoleGroupToPlayerServerResponse;
		list[ServerMessageType.FuncRemoveRoleGroupSimpleToGame-offSet]=this::createFuncRemoveRoleGroupSimpleToGameServerResponse;
		list[ServerMessageType.FuncResetRankForRoleGroup-offSet]=this::createFuncResetRankForRoleGroupServerResponse;
		list[ServerMessageType.FuncRoleGroupChangeSimpleToGame-offSet]=this::createFuncRoleGroupChangeSimpleToGameServerResponse;
		list[ServerMessageType.FuncRoleGroupChangeToPlayer-offSet]=this::createFuncRoleGroupChangeToPlayerServerResponse;
		list[ServerMessageType.FuncRoleGroupEnterOwnSceneToGame-offSet]=this::createFuncRoleGroupEnterOwnSceneToGameServerResponse;
		list[ServerMessageType.FuncRoleGroupMemberChangeToPlayer-offSet]=this::createFuncRoleGroupMemberChangeToPlayerServerResponse;
		list[ServerMessageType.FuncRoleGroupReEnterOwnSceneArgToPlayer-offSet]=this::createFuncRoleGroupReEnterOwnSceneArgToPlayerServerResponse;
		list[ServerMessageType.FuncRoleGroupRefreshRoleShowDataToGame-offSet]=this::createFuncRoleGroupRefreshRoleShowDataToGameServerResponse;
		list[ServerMessageType.FuncRoleGroupRefreshRoleShowDataToPlayer-offSet]=this::createFuncRoleGroupRefreshRoleShowDataToPlayerServerResponse;
		list[ServerMessageType.FuncRoleGroupToGame-offSet]=this::createFuncRoleGroupToGameServerResponse;
		list[ServerMessageType.FuncSendAuctionBuyItemToSourceGame-offSet]=this::createFuncSendAuctionBuyItemToSourceGameServerResponse;
		list[ServerMessageType.FuncSendAuctionCancelSellItemToSourceGame-offSet]=this::createFuncSendAuctionCancelSellItemToSourceGameServerResponse;
		list[ServerMessageType.FuncSendAuctionSellItemToSourceGame-offSet]=this::createFuncSendAuctionSellItemToSourceGameServerResponse;
		list[ServerMessageType.FuncSendChangeLeaderRoleGroupToPlayer-offSet]=this::createFuncSendChangeLeaderRoleGroupToPlayerServerResponse;
		list[ServerMessageType.FuncSendGetPageShowToGame-offSet]=this::createFuncSendGetPageShowToGameServerResponse;
		list[ServerMessageType.FuncSendRoleGroupAddMemberToPlayer-offSet]=this::createFuncSendRoleGroupAddMemberToPlayerServerResponse;
		list[ServerMessageType.FuncSendRoleGroupJoinResult-offSet]=this::createFuncSendRoleGroupJoinResultServerResponse;
		list[ServerMessageType.FuncSendRoleGroupRemoveMemberToPlayer-offSet]=this::createFuncSendRoleGroupRemoveMemberToPlayerServerResponse;
		list[ServerMessageType.FuncSetTitleRoleGroupToGame-offSet]=this::createFuncSetTitleRoleGroupToGameServerResponse;
		list[ServerMessageType.FuncToGame-offSet]=this::createFuncToGameServerResponse;
		list[ServerMessageType.GetRoleSocialDataToPlayer-offSet]=this::createGetRoleSocialDataToPlayerServerResponse;
		list[ServerMessageType.PlayerCallSwitchBackToGame-offSet]=this::createPlayerCallSwitchBackToGameServerResponse;
		list[ServerMessageType.PlayerExitOverToSource-offSet]=this::createPlayerExitOverToSourceServerResponse;
		list[ServerMessageType.PlayerExitSwitchBack-offSet]=this::createPlayerExitSwitchBackServerResponse;
		list[ServerMessageType.PlayerGameToGame-offSet]=this::createPlayerGameToGameServerResponse;
		list[ServerMessageType.PlayerLoginToEachGame-offSet]=this::createPlayerLoginToEachGameServerResponse;
		list[ServerMessageType.PlayerPreExitToGame-offSet]=this::createPlayerPreExitToGameServerResponse;
		list[ServerMessageType.PlayerPreSwitchGameToGame-offSet]=this::createPlayerPreSwitchGameToGameServerResponse;
		list[ServerMessageType.PlayerSwitchGameCompleteToSource-offSet]=this::createPlayerSwitchGameCompleteToSourceServerResponse;
		list[ServerMessageType.PlayerSwitchGameReceiveResultToSource-offSet]=this::createPlayerSwitchGameReceiveResultToSourceServerResponse;
		list[ServerMessageType.PlayerToGameTransGameToGame-offSet]=this::createPlayerToGameTransGameToGameServerResponse;
		list[ServerMessageType.PreSwitchGame-offSet]=this::createPreSwitchGameServerResponse;
		list[ServerMessageType.RadioPlayerChatToGame-offSet]=this::createRadioPlayerChatToGameServerResponse;
		list[ServerMessageType.ReBeGameToGame-offSet]=this::createReBeGameToGameServerResponse;
		list[ServerMessageType.RePlayerLoginFromEachGame-offSet]=this::createRePlayerLoginFromEachGameServerResponse;
		list[ServerMessageType.RePlayerPreExitToGame-offSet]=this::createRePlayerPreExitToGameServerResponse;
		list[ServerMessageType.RePlayerPreSwitchGameToGame-offSet]=this::createRePlayerPreSwitchGameToGameServerResponse;
		list[ServerMessageType.RePreSwitchGameFailed-offSet]=this::createRePreSwitchGameFailedServerResponse;
		list[ServerMessageType.RePreSwitchGame-offSet]=this::createRePreSwitchGameServerResponse;
		list[ServerMessageType.ReceiptWorkToGame-offSet]=this::createReceiptWorkToGameServerResponse;
		list[ServerMessageType.RefreshGameLoginLimitToGame-offSet]=this::createRefreshGameLoginLimitToGameServerResponse;
		list[ServerMessageType.RefreshRoleShowToSourceGame-offSet]=this::createRefreshRoleShowToSourceGameServerResponse;
		list[ServerMessageType.SaveSwitchedPlayerList-offSet]=this::createSaveSwitchedPlayerListServerResponse;
		list[ServerMessageType.SendAreaWorkCompleteToGame-offSet]=this::createSendAreaWorkCompleteToGameServerResponse;
		list[ServerMessageType.SendAreaWorkToGame-offSet]=this::createSendAreaWorkToGameServerResponse;
		list[ServerMessageType.SendGameRequestToPlayer-offSet]=this::createSendGameRequestToPlayerServerResponse;
		list[ServerMessageType.SendPlayerCenterRequestListToGame-offSet]=this::createSendPlayerCenterRequestListToGameServerResponse;
		list[ServerMessageType.SendPlayerCenterRequestToGame-offSet]=this::createSendPlayerCenterRequestToGameServerResponse;
		list[ServerMessageType.SendPlayerChatToPlayer-offSet]=this::createSendPlayerChatToPlayerServerResponse;
		list[ServerMessageType.SendPlayerToGameRequestListToGame-offSet]=this::createSendPlayerToGameRequestListToGameServerResponse;
		list[ServerMessageType.SendPlayerToGameRequestToGame-offSet]=this::createSendPlayerToGameRequestToGameServerResponse;
		list[ServerMessageType.SendPlayerWorkCompleteListToGame-offSet]=this::createSendPlayerWorkCompleteListToGameServerResponse;
		list[ServerMessageType.SendPlayerWorkCompleteToGame-offSet]=this::createSendPlayerWorkCompleteToGameServerResponse;
		list[ServerMessageType.SendPlayerWorkListToGame-offSet]=this::createSendPlayerWorkListToGameServerResponse;
		list[ServerMessageType.SendPlayerWorkToGame-offSet]=this::createSendPlayerWorkToGameServerResponse;
		list[ServerMessageType.SignedSceneGameToGame-offSet]=this::createSignedSceneGameToGameServerResponse;
		list[ServerMessageType.PlayerLeaveSceneOverToGame-offSet]=this::createPlayerLeaveSceneOverToGameServerResponse;
	}
	
	private BaseData createFuncCenterToGameServerResponse()
	{
		return new FuncCenterToGameServerResponse();
	}
	
	private BaseData createFuncRefreshAuctionItemPriceToGameServerResponse()
	{
		return new FuncRefreshAuctionItemPriceToGameServerResponse();
	}
	
	private BaseData createFuncCommitRankValueToSourceGameServerResponse()
	{
		return new FuncCommitRankValueToSourceGameServerResponse();
	}
	
	private BaseData createFuncSendAuctionSellItemToSourceGameServerResponse()
	{
		return new FuncSendAuctionSellItemToSourceGameServerResponse();
	}
	
	private BaseData createActivityResetToGameServerResponse()
	{
		return new ActivityResetToGameServerResponse();
	}
	
	private BaseData createActivitySwitchToGameServerResponse()
	{
		return new ActivitySwitchToGameServerResponse();
	}
	
	private BaseData createCenterTransCenterToGameServerResponse()
	{
		return new CenterTransCenterToGameServerResponse();
	}
	
	private BaseData createChangeActivityForceCloseToGameServerResponse()
	{
		return new ChangeActivityForceCloseToGameServerResponse();
	}
	
	private BaseData createCreateSignedSceneToGameServerResponse()
	{
		return new CreateSignedSceneToGameServerResponse();
	}
	
	private BaseData createEnterSignedSceneToGameServerResponse()
	{
		return new EnterSignedSceneToGameServerResponse();
	}
	
	private BaseData createFuncAddRankToGameServerResponse()
	{
		return new FuncAddRankToGameServerResponse();
	}
	
	private BaseData createFuncMatchTimeOutToGameServerResponse()
	{
		return new FuncMatchTimeOutToGameServerResponse();
	}
	
	private BaseData createFuncPlayerToGameServerResponse()
	{
		return new FuncPlayerToGameServerResponse();
	}
	
	private BaseData createFuncReGetRandomPlayerListFromRoleSocialPoolToCenterServerResponse()
	{
		return new FuncReGetRandomPlayerListFromRoleSocialPoolToCenterServerResponse();
	}
	
	private BaseData createFuncRefreshRankToGameServerResponse()
	{
		return new FuncRefreshRankToGameServerResponse();
	}
	
	private BaseData createFuncRemoveRankToGameServerResponse()
	{
		return new FuncRemoveRankToGameServerResponse();
	}
	
	private BaseData createFuncResetRankToGameServerResponse()
	{
		return new FuncResetRankToGameServerResponse();
	}
	
	private BaseData createGameExitServerResponse()
	{
		return new GameExitServerResponse();
	}
	
	private BaseData createGameReloadConfigServerResponse()
	{
		return new GameReloadConfigServerResponse();
	}
	
	private BaseData createFuncSendAuctionCancelSellItemToSourceGameServerResponse()
	{
		return new FuncSendAuctionCancelSellItemToSourceGameServerResponse();
	}
	
	private BaseData createKickPlayerFromCenterServerResponse()
	{
		return new KickPlayerFromCenterServerResponse();
	}
	
	private BaseData createPlayerToGameServerResponse()
	{
		return new PlayerToGameServerResponse();
	}
	
	private BaseData createPlayerToGameTransCenterToGameServerResponse()
	{
		return new PlayerToGameTransCenterToGameServerResponse();
	}
	
	private BaseData createReBeGameToCenterServerResponse()
	{
		return new ReBeGameToCenterServerResponse();
	}
	
	private BaseData createRefreshServerOffTimeToGameServerResponse()
	{
		return new RefreshServerOffTimeToGameServerResponse();
	}
	
	private BaseData createSendGetRoleSocialDataToGameServerResponse()
	{
		return new SendGetRoleSocialDataToGameServerResponse();
	}
	
	private BaseData createSendMailToGameServerResponse()
	{
		return new SendMailToGameServerResponse();
	}
	
	private BaseData createSendPlayerWorkCompleteServerResponse()
	{
		return new SendPlayerWorkCompleteServerResponse();
	}
	
	private BaseData createSendPlayerWorkServerResponse()
	{
		return new SendPlayerWorkServerResponse();
	}
	
	private BaseData createSendWorkFailedServerResponse()
	{
		return new SendWorkFailedServerResponse();
	}
	
	private BaseData createBeLoginToGameServerResponse()
	{
		return new BeLoginToGameServerResponse();
	}
	
	private BaseData createFuncSendAuctionBuyItemToSourceGameServerResponse()
	{
		return new FuncSendAuctionBuyItemToSourceGameServerResponse();
	}
	
	private BaseData createBeGameToGameServerResponse()
	{
		return new BeGameToGameServerResponse();
	}
	
	private BaseData createFuncAddSubsectionRankToGameServerResponse()
	{
		return new FuncAddSubsectionRankToGameServerResponse();
	}
	
	private BaseData createPlayerExitOverToSourceServerResponse()
	{
		return new PlayerExitOverToSourceServerResponse();
	}
	
	private BaseData createPlayerExitSwitchBackServerResponse()
	{
		return new PlayerExitSwitchBackServerResponse();
	}
	
	private BaseData createPlayerGameToGameServerResponse()
	{
		return new PlayerGameToGameServerResponse();
	}
	
	private BaseData createPlayerLoginToEachGameServerResponse()
	{
		return new PlayerLoginToEachGameServerResponse();
	}
	
	private BaseData createPlayerPreExitToGameServerResponse()
	{
		return new PlayerPreExitToGameServerResponse();
	}
	
	private BaseData createPlayerPreSwitchGameToGameServerResponse()
	{
		return new PlayerPreSwitchGameToGameServerResponse();
	}
	
	private BaseData createPlayerSwitchGameCompleteToSourceServerResponse()
	{
		return new PlayerSwitchGameCompleteToSourceServerResponse();
	}
	
	private BaseData createPlayerSwitchGameReceiveResultToSourceServerResponse()
	{
		return new PlayerSwitchGameReceiveResultToSourceServerResponse();
	}
	
	private BaseData createPlayerToGameTransGameToGameServerResponse()
	{
		return new PlayerToGameTransGameToGameServerResponse();
	}
	
	private BaseData createPreSwitchGameServerResponse()
	{
		return new PreSwitchGameServerResponse();
	}
	
	private BaseData createReBeGameToGameServerResponse()
	{
		return new ReBeGameToGameServerResponse();
	}
	
	private BaseData createRePlayerLoginFromEachGameServerResponse()
	{
		return new RePlayerLoginFromEachGameServerResponse();
	}
	
	private BaseData createFuncRefreshSubsectionIndexToGameServerResponse()
	{
		return new FuncRefreshSubsectionIndexToGameServerResponse();
	}
	
	private BaseData createRePlayerPreSwitchGameToGameServerResponse()
	{
		return new RePlayerPreSwitchGameToGameServerResponse();
	}
	
	private BaseData createRePreSwitchGameFailedServerResponse()
	{
		return new RePreSwitchGameFailedServerResponse();
	}
	
	private BaseData createRePreSwitchGameServerResponse()
	{
		return new RePreSwitchGameServerResponse();
	}
	
	private BaseData createReceiptWorkToGameServerResponse()
	{
		return new ReceiptWorkToGameServerResponse();
	}
	
	private BaseData createRefreshRoleShowToSourceGameServerResponse()
	{
		return new RefreshRoleShowToSourceGameServerResponse();
	}
	
	private BaseData createSaveSwitchedPlayerListServerResponse()
	{
		return new SaveSwitchedPlayerListServerResponse();
	}
	
	private BaseData createSendPlayerCenterRequestListToGameServerResponse()
	{
		return new SendPlayerCenterRequestListToGameServerResponse();
	}
	
	private BaseData createSendPlayerCenterRequestToGameServerResponse()
	{
		return new SendPlayerCenterRequestToGameServerResponse();
	}
	
	private BaseData createSendPlayerToGameRequestListToGameServerResponse()
	{
		return new SendPlayerToGameRequestListToGameServerResponse();
	}
	
	private BaseData createSendPlayerToGameRequestToGameServerResponse()
	{
		return new SendPlayerToGameRequestToGameServerResponse();
	}
	
	private BaseData createSendPlayerWorkCompleteListToGameServerResponse()
	{
		return new SendPlayerWorkCompleteListToGameServerResponse();
	}
	
	private BaseData createSendPlayerWorkCompleteToGameServerResponse()
	{
		return new SendPlayerWorkCompleteToGameServerResponse();
	}
	
	private BaseData createSendPlayerWorkListToGameServerResponse()
	{
		return new SendPlayerWorkListToGameServerResponse();
	}
	
	private BaseData createSendPlayerWorkToGameServerResponse()
	{
		return new SendPlayerWorkToGameServerResponse();
	}
	
	private BaseData createFuncRefreshSubsectionRankToGameServerResponse()
	{
		return new FuncRefreshSubsectionRankToGameServerResponse();
	}
	
	private BaseData createFuncRemoveSubsectionRankToGameServerResponse()
	{
		return new FuncRemoveSubsectionRankToGameServerResponse();
	}
	
	private BaseData createSendAreaWorkToGameServerResponse()
	{
		return new SendAreaWorkToGameServerResponse();
	}
	
	private BaseData createSendAreaWorkCompleteToGameServerResponse()
	{
		return new SendAreaWorkCompleteToGameServerResponse();
	}
	
	private BaseData createFuncResetSubsectionRankToGameServerResponse()
	{
		return new FuncResetSubsectionRankToGameServerResponse();
	}
	
	private BaseData createSendGameRequestToPlayerServerResponse()
	{
		return new SendGameRequestToPlayerServerResponse();
	}
	
	private BaseData createRemoveCenterRoleSocialDataToPlayerServerResponse()
	{
		return new RemoveCenterRoleSocialDataToPlayerServerResponse();
	}
	
	private BaseData createReceiptUserWorkToGameServerResponse()
	{
		return new ReceiptUserWorkToGameServerResponse();
	}
	
	private BaseData createSendInfoCodeFromLoginServerResponse()
	{
		return new SendInfoCodeFromLoginServerResponse();
	}
	
	private BaseData createFuncReGetPageShowToPlayerServerResponse()
	{
		return new FuncReGetPageShowToPlayerServerResponse();
	}
	
	private BaseData createFuncReGetPageShowGameToPlayerServerResponse()
	{
		return new FuncReGetPageShowGameToPlayerServerResponse();
	}
	
	private BaseData createFuncSendGetPageShowToGameServerResponse()
	{
		return new FuncSendGetPageShowToGameServerResponse();
	}
	
	private BaseData createFuncAddRoleGroupSimpleToGameServerResponse()
	{
		return new FuncAddRoleGroupSimpleToGameServerResponse();
	}
	
	private BaseData createFuncRemoveRoleGroupSimpleToGameServerResponse()
	{
		return new FuncRemoveRoleGroupSimpleToGameServerResponse();
	}
	
	private BaseData createFuncSendRoleGroupRemoveMemberToPlayerServerResponse()
	{
		return new FuncSendRoleGroupRemoveMemberToPlayerServerResponse();
	}
	
	private BaseData createFuncRoleGroupChangeSimpleToGameServerResponse()
	{
		return new FuncRoleGroupChangeSimpleToGameServerResponse();
	}
	
	private BaseData createFuncChangeRoleGroupApplyTypeToGameServerResponse()
	{
		return new FuncChangeRoleGroupApplyTypeToGameServerResponse();
	}
	
	private BaseData createFuncChangeRoleGroupNameToGameServerResponse()
	{
		return new FuncChangeRoleGroupNameToGameServerResponse();
	}
	
	private BaseData createFuncRoleGroupEnterOwnSceneToGameServerResponse()
	{
		return new FuncRoleGroupEnterOwnSceneToGameServerResponse();
	}
	
	private BaseData createFuncRoleGroupReEnterOwnSceneArgToPlayerServerResponse()
	{
		return new FuncRoleGroupReEnterOwnSceneArgToPlayerServerResponse();
	}
	
	private BaseData createFuncHandleApplyRoleGroupToGameServerResponse()
	{
		return new FuncHandleApplyRoleGroupToGameServerResponse();
	}
	
	private BaseData createFuncPlayerRoleGroupGameToGameServerResponse()
	{
		return new FuncPlayerRoleGroupGameToGameServerResponse();
	}
	
	private BaseData createFuncRoleGroupChangeToPlayerServerResponse()
	{
		return new FuncRoleGroupChangeToPlayerServerResponse();
	}
	
	private BaseData createFuncSendChangeLeaderRoleGroupToPlayerServerResponse()
	{
		return new FuncSendChangeLeaderRoleGroupToPlayerServerResponse();
	}
	
	private BaseData createFuncSendRoleGroupAddMemberToPlayerServerResponse()
	{
		return new FuncSendRoleGroupAddMemberToPlayerServerResponse();
	}
	
	private BaseData createFuncRoleGroupRefreshRoleShowDataToGameServerResponse()
	{
		return new FuncRoleGroupRefreshRoleShowDataToGameServerResponse();
	}
	
	private BaseData createFuncRoleGroupRefreshRoleShowDataToPlayerServerResponse()
	{
		return new FuncRoleGroupRefreshRoleShowDataToPlayerServerResponse();
	}
	
	private BaseData createFuncRoleGroupMemberChangeToPlayerServerResponse()
	{
		return new FuncRoleGroupMemberChangeToPlayerServerResponse();
	}
	
	private BaseData createSignedSceneGameToGameServerResponse()
	{
		return new SignedSceneGameToGameServerResponse();
	}
	
	private BaseData createFuncAgreeInviteCreateRoleGroupToGameServerResponse()
	{
		return new FuncAgreeInviteCreateRoleGroupToGameServerResponse();
	}
	
	private BaseData createFuncAgreeInviteRoleGroupToGameServerResponse()
	{
		return new FuncAgreeInviteRoleGroupToGameServerResponse();
	}
	
	private BaseData createFuncApplyRoleGroupToGameServerResponse()
	{
		return new FuncApplyRoleGroupToGameServerResponse();
	}
	
	private BaseData createFuncChangeLeaderRoleGroupToGameServerResponse()
	{
		return new FuncChangeLeaderRoleGroupToGameServerResponse();
	}
	
	private BaseData createFuncChangeRoleGroupNoticeToGameServerResponse()
	{
		return new FuncChangeRoleGroupNoticeToGameServerResponse();
	}
	
	private BaseData createFuncDisbandRoleGroupToGameServerResponse()
	{
		return new FuncDisbandRoleGroupToGameServerResponse();
	}
	
	private BaseData createFuncKickMemberRoleGroupToGameServerResponse()
	{
		return new FuncKickMemberRoleGroupToGameServerResponse();
	}
	
	private BaseData createFuncLeaveRoleGroupToGameServerResponse()
	{
		return new FuncLeaveRoleGroupToGameServerResponse();
	}
	
	private BaseData createFuncPlayerGameToGameServerResponse()
	{
		return new FuncPlayerGameToGameServerResponse();
	}
	
	private BaseData createFuncRefreshTitleRoleGroupToPlayerServerResponse()
	{
		return new FuncRefreshTitleRoleGroupToPlayerServerResponse();
	}
	
	private BaseData createFuncRoleGroupToGameServerResponse()
	{
		return new FuncRoleGroupToGameServerResponse();
	}
	
	private BaseData createFuncSetTitleRoleGroupToGameServerResponse()
	{
		return new FuncSetTitleRoleGroupToGameServerResponse();
	}
	
	private BaseData createFuncToGameServerResponse()
	{
		return new FuncToGameServerResponse();
	}
	
	private BaseData createRadioPlayerChatToGameServerResponse()
	{
		return new RadioPlayerChatToGameServerResponse();
	}
	
	private BaseData createSendPlayerChatToPlayerServerResponse()
	{
		return new SendPlayerChatToPlayerServerResponse();
	}
	
	private BaseData createRefreshGameLoginLimitToGameServerResponse()
	{
		return new RefreshGameLoginLimitToGameServerResponse();
	}
	
	private BaseData createGetRoleSocialDataToPlayerServerResponse()
	{
		return new GetRoleSocialDataToPlayerServerResponse();
	}
	
	private BaseData createReBeGameToManagerServerResponse()
	{
		return new ReBeGameToManagerServerResponse();
	}
	
	private BaseData createPlayerBindPlatformToGameServerResponse()
	{
		return new PlayerBindPlatformToGameServerResponse();
	}
	
	private BaseData createUserLoginToGameServerResponse()
	{
		return new UserLoginToGameServerResponse();
	}
	
	private BaseData createPlayerCallSwitchBackToGameServerResponse()
	{
		return new PlayerCallSwitchBackToGameServerResponse();
	}
	
	private BaseData createRePlayerPreExitToGameServerResponse()
	{
		return new RePlayerPreExitToGameServerResponse();
	}
	
	private BaseData createManagerToGameCommandServerResponse()
	{
		return new ManagerToGameCommandServerResponse();
	}
	
	private BaseData createHotfixToGameServerResponse()
	{
		return new HotfixToGameServerResponse();
	}
	
	private BaseData createReceiptWorkToGameFromCenterServerResponse()
	{
		return new ReceiptWorkToGameFromCenterServerResponse();
	}
	
	private BaseData createFuncRefreshRankForRoleGroupServerResponse()
	{
		return new FuncRefreshRankForRoleGroupServerResponse();
	}
	
	private BaseData createFuncResetRankForRoleGroupServerResponse()
	{
		return new FuncResetRankForRoleGroupServerResponse();
	}
	
	private BaseData createSendAreaWorkToGameFromCenterServerResponse()
	{
		return new SendAreaWorkToGameFromCenterServerResponse();
	}
	
	private BaseData createRefreshRoleGroupChangeToGameServerResponse()
	{
		return new RefreshRoleGroupChangeToGameServerResponse();
	}
	
	private BaseData createRefreshRoleShowDataChangeToGameServerResponse()
	{
		return new RefreshRoleShowDataChangeToGameServerResponse();
	}
	
	private BaseData createFuncSendRoleGroupJoinResultServerResponse()
	{
		return new FuncSendRoleGroupJoinResultServerResponse();
	}
	
	private BaseData createFuncAgreeApplyNextRoleGroupToGameServerResponse()
	{
		return new FuncAgreeApplyNextRoleGroupToGameServerResponse();
	}
	
	private BaseData createReloadServerConfigToGameServerResponse()
	{
		return new ReloadServerConfigToGameServerResponse();
	}
	
	private BaseData createReBeGameToSceneServerResponse()
	{
		return new ReBeGameToSceneServerResponse();
	}
	
	private BaseData createPlayerSwitchToSceneOverServerResponse()
	{
		return new PlayerSwitchToSceneOverServerResponse();
	}
	
	private BaseData createRePlayerSwitchToSceneServerResponse()
	{
		return new RePlayerSwitchToSceneServerResponse();
	}
	
	private BaseData createPlayerSceneToGameServerResponse()
	{
		return new PlayerSceneToGameServerResponse();
	}
	
	private BaseData createPlayerLeaveSceneOverToGameServerResponse()
	{
		return new PlayerLeaveSceneOverToGameServerResponse();
	}
	
}
