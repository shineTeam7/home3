package com.home.commonGame.tool.generate;
import com.home.commonBase.constlist.generate.ServerMessageType;
import com.home.commonGame.net.serverRequest.center.activity.UseActivationCodeToCenterServerRequest;
import com.home.commonGame.net.serverRequest.center.base.FuncToCenterServerRequest;
import com.home.commonGame.net.serverRequest.center.base.PlayerToCenterServerRequest;
import com.home.commonGame.net.serverRequest.center.func.auction.FuncSendAuctionBuyItemToCenterServerRequest;
import com.home.commonGame.net.serverRequest.center.func.auction.FuncSendAuctionCancelSellItemToCenterServerRequest;
import com.home.commonGame.net.serverRequest.center.func.auction.FuncSendAuctionSellItemToCenterServerRequest;
import com.home.commonGame.net.serverRequest.center.func.base.FuncPlayerToCenterServerRequest;
import com.home.commonGame.net.serverRequest.center.func.match.FuncAcceptMatchToCenterServerRequest;
import com.home.commonGame.net.serverRequest.center.func.match.FuncApplyCancelMatchToCenterServerRequest;
import com.home.commonGame.net.serverRequest.center.func.match.FuncApplyMatchToCenterServerRequest;
import com.home.commonGame.net.serverRequest.center.func.pageShow.FuncSendGetPageShowToCenterServerRequest;
import com.home.commonGame.net.serverRequest.center.func.rank.FuncCommitRankValueToCenterServerRequest;
import com.home.commonGame.net.serverRequest.center.func.rank.FuncRemoveRankToCenterServerRequest;
import com.home.commonGame.net.serverRequest.center.func.rank.subsection.FuncCommitSubsectionRankValueToCenterServerRequest;
import com.home.commonGame.net.serverRequest.center.func.rank.subsection.FuncRemoveSubsectionRankToCenterServerRequest;
import com.home.commonGame.net.serverRequest.center.func.social.FuncGetRandomPlayerListFromRoleSocialPoolToCenterServerRequest;
import com.home.commonGame.net.serverRequest.center.scene.ReCreateSignedSceneToCenterServerRequest;
import com.home.commonGame.net.serverRequest.center.social.CommitCustomRoleSocialToCenterServerRequest;
import com.home.commonGame.net.serverRequest.center.social.CommitRoleGroupToCenterServerRequest;
import com.home.commonGame.net.serverRequest.center.social.CommitRoleSocialToCenterServerRequest;
import com.home.commonGame.net.serverRequest.center.social.FuncRoleGroupChangeSimpleToCenterServerRequest;
import com.home.commonGame.net.serverRequest.center.social.RefreshRoleSocialToCenterServerRequest;
import com.home.commonGame.net.serverRequest.center.system.BeGameToCenterServerRequest;
import com.home.commonGame.net.serverRequest.center.system.CenterTransGameToCenterServerRequest;
import com.home.commonGame.net.serverRequest.center.system.ClientGMToCenterServerRequest;
import com.home.commonGame.net.serverRequest.center.system.GameLoginToCenterServerRequest;
import com.home.commonGame.net.serverRequest.center.system.ReceiptWorkToCenterServerRequest;
import com.home.commonGame.net.serverRequest.center.system.SendCenterWorkCompleteToCenterServerRequest;
import com.home.commonGame.net.serverRequest.center.system.SendCenterWorkToCenterServerRequest;
import com.home.commonGame.net.serverRequest.center.system.SendMQueryPlayerWorkResultToCenterServerRequest;
import com.home.commonGame.net.serverRequest.center.system.SendPlayerOnlineNumToCenterServerRequest;
import com.home.commonGame.net.serverRequest.center.system.SendPlayerWorkToCenterServerRequest;
import com.home.commonGame.net.serverRequest.game.base.PlayerGameToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.base.SignedSceneGameToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.func.auction.FuncSendAuctionBuyItemToSourceGameServerRequest;
import com.home.commonGame.net.serverRequest.game.func.auction.FuncSendAuctionCancelSellItemToSourceGameServerRequest;
import com.home.commonGame.net.serverRequest.game.func.auction.FuncSendAuctionSellItemToSourceGameServerRequest;
import com.home.commonGame.net.serverRequest.game.func.base.FuncPlayerGameToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.func.base.FuncPlayerRoleGroupGameToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.func.base.FuncRoleGroupToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.func.base.FuncToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.func.pageShow.FuncReGetPageShowGameToPlayerServerRequest;
import com.home.commonGame.net.serverRequest.game.func.pageShow.FuncSendGetPageShowToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.func.rank.FuncCommitRankValueToSourceGameServerRequest;
import com.home.commonGame.net.serverRequest.game.func.rank.FuncRefreshRankForRoleGroupServerRequest;
import com.home.commonGame.net.serverRequest.game.func.rank.FuncResetRankForRoleGroupServerRequest;
import com.home.commonGame.net.serverRequest.game.func.roleGroup.FuncAddRoleGroupSimpleToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.func.roleGroup.FuncAgreeApplyNextRoleGroupToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.func.roleGroup.FuncAgreeInviteCreateRoleGroupToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.func.roleGroup.FuncAgreeInviteRoleGroupToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.func.roleGroup.FuncApplyRoleGroupToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.func.roleGroup.FuncChangeLeaderRoleGroupToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.func.roleGroup.FuncChangeRoleGroupApplyTypeToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.func.roleGroup.FuncChangeRoleGroupNameToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.func.roleGroup.FuncChangeRoleGroupNoticeToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.func.roleGroup.FuncDisbandRoleGroupToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.func.roleGroup.FuncHandleApplyRoleGroupToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.func.roleGroup.FuncKickMemberRoleGroupToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.func.roleGroup.FuncLeaveRoleGroupToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.func.roleGroup.FuncRefreshTitleRoleGroupToPlayerServerRequest;
import com.home.commonGame.net.serverRequest.game.func.roleGroup.FuncRemoveRoleGroupSimpleToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.func.roleGroup.FuncRoleGroupChangeSimpleToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.func.roleGroup.FuncRoleGroupChangeToPlayerServerRequest;
import com.home.commonGame.net.serverRequest.game.func.roleGroup.FuncRoleGroupEnterOwnSceneToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.func.roleGroup.FuncRoleGroupMemberChangeToPlayerServerRequest;
import com.home.commonGame.net.serverRequest.game.func.roleGroup.FuncRoleGroupReEnterOwnSceneArgToPlayerServerRequest;
import com.home.commonGame.net.serverRequest.game.func.roleGroup.FuncRoleGroupRefreshRoleShowDataToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.func.roleGroup.FuncRoleGroupRefreshRoleShowDataToPlayerServerRequest;
import com.home.commonGame.net.serverRequest.game.func.roleGroup.FuncSendChangeLeaderRoleGroupToPlayerServerRequest;
import com.home.commonGame.net.serverRequest.game.func.roleGroup.FuncSendRoleGroupAddMemberToPlayerServerRequest;
import com.home.commonGame.net.serverRequest.game.func.roleGroup.FuncSendRoleGroupJoinResultServerRequest;
import com.home.commonGame.net.serverRequest.game.func.roleGroup.FuncSendRoleGroupRemoveMemberToPlayerServerRequest;
import com.home.commonGame.net.serverRequest.game.func.roleGroup.FuncSetTitleRoleGroupToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.login.PlayerCallSwitchBackToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.login.PlayerExitOverToSourceServerRequest;
import com.home.commonGame.net.serverRequest.game.login.PlayerExitSwitchBackServerRequest;
import com.home.commonGame.net.serverRequest.game.login.PlayerLoginToEachGameServerRequest;
import com.home.commonGame.net.serverRequest.game.login.PlayerPreExitToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.login.PlayerPreSwitchGameToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.login.PlayerSwitchGameCompleteToSourceServerRequest;
import com.home.commonGame.net.serverRequest.game.login.PlayerSwitchGameReceiveResultToSourceServerRequest;
import com.home.commonGame.net.serverRequest.game.login.PreSwitchGameServerRequest;
import com.home.commonGame.net.serverRequest.game.login.RePlayerLoginFromEachGameServerRequest;
import com.home.commonGame.net.serverRequest.game.login.RePlayerPreExitToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.login.RePlayerPreSwitchGameToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.login.RePreSwitchGameFailedServerRequest;
import com.home.commonGame.net.serverRequest.game.login.RePreSwitchGameServerRequest;
import com.home.commonGame.net.serverRequest.game.login.RefreshGameLoginLimitToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.social.GetRoleSocialDataToPlayerServerRequest;
import com.home.commonGame.net.serverRequest.game.social.RadioPlayerChatToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.social.RefreshRoleShowToSourceGameServerRequest;
import com.home.commonGame.net.serverRequest.game.social.SendPlayerChatToPlayerServerRequest;
import com.home.commonGame.net.serverRequest.game.system.BeGameToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.system.PlayerToGameTransGameToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.system.ReBeGameToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.system.ReceiptWorkToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.system.SaveSwitchedPlayerListServerRequest;
import com.home.commonGame.net.serverRequest.game.system.SendAreaWorkCompleteToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.system.SendAreaWorkToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.system.SendGameRequestToPlayerServerRequest;
import com.home.commonGame.net.serverRequest.game.system.SendPlayerCenterRequestListToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.system.SendPlayerCenterRequestToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.system.SendPlayerToGameRequestListToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.system.SendPlayerToGameRequestToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.system.SendPlayerWorkCompleteListToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.system.SendPlayerWorkCompleteToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.system.SendPlayerWorkListToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.system.SendPlayerWorkToGameServerRequest;
import com.home.commonGame.net.serverRequest.login.login.ClientApplyBindPlatformToLoginServerRequest;
import com.home.commonGame.net.serverRequest.login.login.ReUserLoginToLoginServerRequest;
import com.home.commonGame.net.serverRequest.login.login.RefreshGameLoginLimitToLoginServerRequest;
import com.home.commonGame.net.serverRequest.login.system.LimitAreaToLoginServerRequest;
import com.home.commonGame.net.serverRequest.login.system.ReBeLoginToGameServerRequest;
import com.home.commonGame.net.serverRequest.login.system.SendUserWorkToLoginServerRequest;
import com.home.commonGame.net.serverRequest.manager.BeGameToManagerServerRequest;
import com.home.commonGame.net.serverRequest.scene.base.PlayerGameToSceneServerRequest;
import com.home.commonGame.net.serverRequest.scene.login.PlayerEnterServerSceneServerRequest;
import com.home.commonGame.net.serverRequest.scene.login.PlayerLeaveSceneToSceneServerRequest;
import com.home.commonGame.net.serverRequest.scene.login.PlayerSwitchToSceneServerRequest;
import com.home.commonGame.net.serverRequest.scene.system.BeGameToSceneServerRequest;
import com.home.commonGame.net.serverRequest.scene.system.ClientGMToSceneServerRequest;
import com.home.shine.data.BaseData;
import com.home.shine.tool.CreateDataFunc;
import com.home.shine.tool.DataMaker;

/** (generated by shine) */
public class GameServerRequestMaker extends DataMaker
{
	public GameServerRequestMaker()
	{
		offSet=ServerMessageType.off;
		list=new CreateDataFunc[ServerMessageType.count-offSet];
		list[ServerMessageType.BeGameToManager-offSet]=this::createBeGameToManagerServerRequest;
		list[ServerMessageType.BeGameToCenter-offSet]=this::createBeGameToCenterServerRequest;
		list[ServerMessageType.CenterTransGameToCenter-offSet]=this::createCenterTransGameToCenterServerRequest;
		list[ServerMessageType.ClientGMToCenter-offSet]=this::createClientGMToCenterServerRequest;
		list[ServerMessageType.CommitCustomRoleSocialToCenter-offSet]=this::createCommitCustomRoleSocialToCenterServerRequest;
		list[ServerMessageType.CommitRoleGroupToCenter-offSet]=this::createCommitRoleGroupToCenterServerRequest;
		list[ServerMessageType.CommitRoleSocialToCenter-offSet]=this::createCommitRoleSocialToCenterServerRequest;
		list[ServerMessageType.FuncAcceptMatchToCenter-offSet]=this::createFuncAcceptMatchToCenterServerRequest;
		list[ServerMessageType.FuncApplyCancelMatchToCenter-offSet]=this::createFuncApplyCancelMatchToCenterServerRequest;
		list[ServerMessageType.FuncApplyMatchToCenter-offSet]=this::createFuncApplyMatchToCenterServerRequest;
		list[ServerMessageType.FuncCommitRankValueToCenter-offSet]=this::createFuncCommitRankValueToCenterServerRequest;
		list[ServerMessageType.FuncCommitSubsectionRankValueToCenter-offSet]=this::createFuncCommitSubsectionRankValueToCenterServerRequest;
		list[ServerMessageType.FuncGetRandomPlayerListFromRoleSocialPoolToCenter-offSet]=this::createFuncGetRandomPlayerListFromRoleSocialPoolToCenterServerRequest;
		list[ServerMessageType.FuncPlayerToCenter-offSet]=this::createFuncPlayerToCenterServerRequest;
		list[ServerMessageType.FuncRemoveRankToCenter-offSet]=this::createFuncRemoveRankToCenterServerRequest;
		list[ServerMessageType.FuncRemoveSubsectionRankToCenter-offSet]=this::createFuncRemoveSubsectionRankToCenterServerRequest;
		list[ServerMessageType.FuncRoleGroupChangeSimpleToCenter-offSet]=this::createFuncRoleGroupChangeSimpleToCenterServerRequest;
		list[ServerMessageType.FuncSendAuctionBuyItemToCenter-offSet]=this::createFuncSendAuctionBuyItemToCenterServerRequest;
		list[ServerMessageType.FuncSendAuctionCancelSellItemToCenter-offSet]=this::createFuncSendAuctionCancelSellItemToCenterServerRequest;
		list[ServerMessageType.FuncSendAuctionSellItemToCenter-offSet]=this::createFuncSendAuctionSellItemToCenterServerRequest;
		list[ServerMessageType.FuncSendGetPageShowToCenter-offSet]=this::createFuncSendGetPageShowToCenterServerRequest;
		list[ServerMessageType.FuncToCenter-offSet]=this::createFuncToCenterServerRequest;
		list[ServerMessageType.GameLoginToCenter-offSet]=this::createGameLoginToCenterServerRequest;
		list[ServerMessageType.PlayerToCenter-offSet]=this::createPlayerToCenterServerRequest;
		list[ServerMessageType.ReCreateSignedSceneToCenter-offSet]=this::createReCreateSignedSceneToCenterServerRequest;
		list[ServerMessageType.ReceiptWorkToCenter-offSet]=this::createReceiptWorkToCenterServerRequest;
		list[ServerMessageType.RefreshRoleSocialToCenter-offSet]=this::createRefreshRoleSocialToCenterServerRequest;
		list[ServerMessageType.SendCenterWorkCompleteToCenter-offSet]=this::createSendCenterWorkCompleteToCenterServerRequest;
		list[ServerMessageType.SendCenterWorkToCenter-offSet]=this::createSendCenterWorkToCenterServerRequest;
		list[ServerMessageType.SendMQueryPlayerWorkResultToCenter-offSet]=this::createSendMQueryPlayerWorkResultToCenterServerRequest;
		list[ServerMessageType.SendPlayerOnlineNumToCenter-offSet]=this::createSendPlayerOnlineNumToCenterServerRequest;
		list[ServerMessageType.SendPlayerWorkToCenter-offSet]=this::createSendPlayerWorkToCenterServerRequest;
		list[ServerMessageType.UseActivationCodeToCenter-offSet]=this::createUseActivationCodeToCenterServerRequest;
		list[ServerMessageType.ClientApplyBindPlatformToLogin-offSet]=this::createClientApplyBindPlatformToLoginServerRequest;
		list[ServerMessageType.LimitAreaToLogin-offSet]=this::createLimitAreaToLoginServerRequest;
		list[ServerMessageType.ReBeLoginToGame-offSet]=this::createReBeLoginToGameServerRequest;
		list[ServerMessageType.ReUserLoginToLogin-offSet]=this::createReUserLoginToLoginServerRequest;
		list[ServerMessageType.RefreshGameLoginLimitToLogin-offSet]=this::createRefreshGameLoginLimitToLoginServerRequest;
		list[ServerMessageType.SendUserWorkToLogin-offSet]=this::createSendUserWorkToLoginServerRequest;
		list[ServerMessageType.BeGameToScene-offSet]=this::createBeGameToSceneServerRequest;
		list[ServerMessageType.PlayerEnterServerScene-offSet]=this::createPlayerEnterServerSceneServerRequest;
		list[ServerMessageType.PlayerGameToScene-offSet]=this::createPlayerGameToSceneServerRequest;
		list[ServerMessageType.PlayerSwitchToScene-offSet]=this::createPlayerSwitchToSceneServerRequest;
		list[ServerMessageType.BeGameToGame-offSet]=this::createBeGameToGameServerRequest;
		list[ServerMessageType.FuncAddRoleGroupSimpleToGame-offSet]=this::createFuncAddRoleGroupSimpleToGameServerRequest;
		list[ServerMessageType.FuncAgreeApplyNextRoleGroupToGame-offSet]=this::createFuncAgreeApplyNextRoleGroupToGameServerRequest;
		list[ServerMessageType.FuncAgreeInviteCreateRoleGroupToGame-offSet]=this::createFuncAgreeInviteCreateRoleGroupToGameServerRequest;
		list[ServerMessageType.FuncAgreeInviteRoleGroupToGame-offSet]=this::createFuncAgreeInviteRoleGroupToGameServerRequest;
		list[ServerMessageType.FuncApplyRoleGroupToGame-offSet]=this::createFuncApplyRoleGroupToGameServerRequest;
		list[ServerMessageType.FuncChangeLeaderRoleGroupToGame-offSet]=this::createFuncChangeLeaderRoleGroupToGameServerRequest;
		list[ServerMessageType.FuncChangeRoleGroupApplyTypeToGame-offSet]=this::createFuncChangeRoleGroupApplyTypeToGameServerRequest;
		list[ServerMessageType.FuncChangeRoleGroupNameToGame-offSet]=this::createFuncChangeRoleGroupNameToGameServerRequest;
		list[ServerMessageType.FuncChangeRoleGroupNoticeToGame-offSet]=this::createFuncChangeRoleGroupNoticeToGameServerRequest;
		list[ServerMessageType.FuncCommitRankValueToSourceGame-offSet]=this::createFuncCommitRankValueToSourceGameServerRequest;
		list[ServerMessageType.FuncDisbandRoleGroupToGame-offSet]=this::createFuncDisbandRoleGroupToGameServerRequest;
		list[ServerMessageType.FuncHandleApplyRoleGroupToGame-offSet]=this::createFuncHandleApplyRoleGroupToGameServerRequest;
		list[ServerMessageType.FuncKickMemberRoleGroupToGame-offSet]=this::createFuncKickMemberRoleGroupToGameServerRequest;
		list[ServerMessageType.FuncLeaveRoleGroupToGame-offSet]=this::createFuncLeaveRoleGroupToGameServerRequest;
		list[ServerMessageType.FuncPlayerGameToGame-offSet]=this::createFuncPlayerGameToGameServerRequest;
		list[ServerMessageType.FuncPlayerRoleGroupGameToGame-offSet]=this::createFuncPlayerRoleGroupGameToGameServerRequest;
		list[ServerMessageType.FuncReGetPageShowGameToPlayer-offSet]=this::createFuncReGetPageShowGameToPlayerServerRequest;
		list[ServerMessageType.FuncRefreshRankForRoleGroup-offSet]=this::createFuncRefreshRankForRoleGroupServerRequest;
		list[ServerMessageType.FuncRefreshTitleRoleGroupToPlayer-offSet]=this::createFuncRefreshTitleRoleGroupToPlayerServerRequest;
		list[ServerMessageType.FuncRemoveRoleGroupSimpleToGame-offSet]=this::createFuncRemoveRoleGroupSimpleToGameServerRequest;
		list[ServerMessageType.FuncResetRankForRoleGroup-offSet]=this::createFuncResetRankForRoleGroupServerRequest;
		list[ServerMessageType.FuncRoleGroupChangeSimpleToGame-offSet]=this::createFuncRoleGroupChangeSimpleToGameServerRequest;
		list[ServerMessageType.FuncRoleGroupChangeToPlayer-offSet]=this::createFuncRoleGroupChangeToPlayerServerRequest;
		list[ServerMessageType.FuncRoleGroupEnterOwnSceneToGame-offSet]=this::createFuncRoleGroupEnterOwnSceneToGameServerRequest;
		list[ServerMessageType.FuncRoleGroupMemberChangeToPlayer-offSet]=this::createFuncRoleGroupMemberChangeToPlayerServerRequest;
		list[ServerMessageType.FuncRoleGroupReEnterOwnSceneArgToPlayer-offSet]=this::createFuncRoleGroupReEnterOwnSceneArgToPlayerServerRequest;
		list[ServerMessageType.FuncRoleGroupRefreshRoleShowDataToGame-offSet]=this::createFuncRoleGroupRefreshRoleShowDataToGameServerRequest;
		list[ServerMessageType.FuncRoleGroupRefreshRoleShowDataToPlayer-offSet]=this::createFuncRoleGroupRefreshRoleShowDataToPlayerServerRequest;
		list[ServerMessageType.FuncRoleGroupToGame-offSet]=this::createFuncRoleGroupToGameServerRequest;
		list[ServerMessageType.FuncSendAuctionBuyItemToSourceGame-offSet]=this::createFuncSendAuctionBuyItemToSourceGameServerRequest;
		list[ServerMessageType.FuncSendAuctionCancelSellItemToSourceGame-offSet]=this::createFuncSendAuctionCancelSellItemToSourceGameServerRequest;
		list[ServerMessageType.FuncSendAuctionSellItemToSourceGame-offSet]=this::createFuncSendAuctionSellItemToSourceGameServerRequest;
		list[ServerMessageType.FuncSendChangeLeaderRoleGroupToPlayer-offSet]=this::createFuncSendChangeLeaderRoleGroupToPlayerServerRequest;
		list[ServerMessageType.FuncSendGetPageShowToGame-offSet]=this::createFuncSendGetPageShowToGameServerRequest;
		list[ServerMessageType.FuncSendRoleGroupAddMemberToPlayer-offSet]=this::createFuncSendRoleGroupAddMemberToPlayerServerRequest;
		list[ServerMessageType.FuncSendRoleGroupJoinResult-offSet]=this::createFuncSendRoleGroupJoinResultServerRequest;
		list[ServerMessageType.FuncSendRoleGroupRemoveMemberToPlayer-offSet]=this::createFuncSendRoleGroupRemoveMemberToPlayerServerRequest;
		list[ServerMessageType.FuncSetTitleRoleGroupToGame-offSet]=this::createFuncSetTitleRoleGroupToGameServerRequest;
		list[ServerMessageType.FuncToGame-offSet]=this::createFuncToGameServerRequest;
		list[ServerMessageType.GetRoleSocialDataToPlayer-offSet]=this::createGetRoleSocialDataToPlayerServerRequest;
		list[ServerMessageType.PlayerCallSwitchBackToGame-offSet]=this::createPlayerCallSwitchBackToGameServerRequest;
		list[ServerMessageType.PlayerExitOverToSource-offSet]=this::createPlayerExitOverToSourceServerRequest;
		list[ServerMessageType.PlayerExitSwitchBack-offSet]=this::createPlayerExitSwitchBackServerRequest;
		list[ServerMessageType.PlayerGameToGame-offSet]=this::createPlayerGameToGameServerRequest;
		list[ServerMessageType.PlayerLoginToEachGame-offSet]=this::createPlayerLoginToEachGameServerRequest;
		list[ServerMessageType.PlayerPreExitToGame-offSet]=this::createPlayerPreExitToGameServerRequest;
		list[ServerMessageType.PlayerPreSwitchGameToGame-offSet]=this::createPlayerPreSwitchGameToGameServerRequest;
		list[ServerMessageType.PlayerSwitchGameCompleteToSource-offSet]=this::createPlayerSwitchGameCompleteToSourceServerRequest;
		list[ServerMessageType.PlayerSwitchGameReceiveResultToSource-offSet]=this::createPlayerSwitchGameReceiveResultToSourceServerRequest;
		list[ServerMessageType.PlayerToGameTransGameToGame-offSet]=this::createPlayerToGameTransGameToGameServerRequest;
		list[ServerMessageType.PreSwitchGame-offSet]=this::createPreSwitchGameServerRequest;
		list[ServerMessageType.RadioPlayerChatToGame-offSet]=this::createRadioPlayerChatToGameServerRequest;
		list[ServerMessageType.ReBeGameToGame-offSet]=this::createReBeGameToGameServerRequest;
		list[ServerMessageType.RePlayerLoginFromEachGame-offSet]=this::createRePlayerLoginFromEachGameServerRequest;
		list[ServerMessageType.RePlayerPreExitToGame-offSet]=this::createRePlayerPreExitToGameServerRequest;
		list[ServerMessageType.RePlayerPreSwitchGameToGame-offSet]=this::createRePlayerPreSwitchGameToGameServerRequest;
		list[ServerMessageType.RePreSwitchGameFailed-offSet]=this::createRePreSwitchGameFailedServerRequest;
		list[ServerMessageType.RePreSwitchGame-offSet]=this::createRePreSwitchGameServerRequest;
		list[ServerMessageType.ReceiptWorkToGame-offSet]=this::createReceiptWorkToGameServerRequest;
		list[ServerMessageType.RefreshGameLoginLimitToGame-offSet]=this::createRefreshGameLoginLimitToGameServerRequest;
		list[ServerMessageType.RefreshRoleShowToSourceGame-offSet]=this::createRefreshRoleShowToSourceGameServerRequest;
		list[ServerMessageType.SaveSwitchedPlayerList-offSet]=this::createSaveSwitchedPlayerListServerRequest;
		list[ServerMessageType.SendAreaWorkCompleteToGame-offSet]=this::createSendAreaWorkCompleteToGameServerRequest;
		list[ServerMessageType.SendAreaWorkToGame-offSet]=this::createSendAreaWorkToGameServerRequest;
		list[ServerMessageType.SendGameRequestToPlayer-offSet]=this::createSendGameRequestToPlayerServerRequest;
		list[ServerMessageType.SendPlayerCenterRequestListToGame-offSet]=this::createSendPlayerCenterRequestListToGameServerRequest;
		list[ServerMessageType.SendPlayerCenterRequestToGame-offSet]=this::createSendPlayerCenterRequestToGameServerRequest;
		list[ServerMessageType.SendPlayerChatToPlayer-offSet]=this::createSendPlayerChatToPlayerServerRequest;
		list[ServerMessageType.SendPlayerToGameRequestListToGame-offSet]=this::createSendPlayerToGameRequestListToGameServerRequest;
		list[ServerMessageType.SendPlayerToGameRequestToGame-offSet]=this::createSendPlayerToGameRequestToGameServerRequest;
		list[ServerMessageType.SendPlayerWorkCompleteListToGame-offSet]=this::createSendPlayerWorkCompleteListToGameServerRequest;
		list[ServerMessageType.SendPlayerWorkCompleteToGame-offSet]=this::createSendPlayerWorkCompleteToGameServerRequest;
		list[ServerMessageType.SendPlayerWorkListToGame-offSet]=this::createSendPlayerWorkListToGameServerRequest;
		list[ServerMessageType.SendPlayerWorkToGame-offSet]=this::createSendPlayerWorkToGameServerRequest;
		list[ServerMessageType.SignedSceneGameToGame-offSet]=this::createSignedSceneGameToGameServerRequest;
		list[ServerMessageType.PlayerLeaveSceneToScene-offSet]=this::createPlayerLeaveSceneToSceneServerRequest;
		list[ServerMessageType.ClientGMToScene-offSet]=this::createClientGMToSceneServerRequest;
	}
	
	private BaseData createFuncSendAuctionSellItemToCenterServerRequest()
	{
		return new FuncSendAuctionSellItemToCenterServerRequest();
	}
	
	private BaseData createFuncCommitRankValueToSourceGameServerRequest()
	{
		return new FuncCommitRankValueToSourceGameServerRequest();
	}
	
	private BaseData createFuncSendAuctionSellItemToSourceGameServerRequest()
	{
		return new FuncSendAuctionSellItemToSourceGameServerRequest();
	}
	
	private BaseData createFuncSendAuctionCancelSellItemToCenterServerRequest()
	{
		return new FuncSendAuctionCancelSellItemToCenterServerRequest();
	}
	
	private BaseData createFuncSendAuctionCancelSellItemToSourceGameServerRequest()
	{
		return new FuncSendAuctionCancelSellItemToSourceGameServerRequest();
	}
	
	private BaseData createBeGameToCenterServerRequest()
	{
		return new BeGameToCenterServerRequest();
	}
	
	private BaseData createCenterTransGameToCenterServerRequest()
	{
		return new CenterTransGameToCenterServerRequest();
	}
	
	private BaseData createClientGMToCenterServerRequest()
	{
		return new ClientGMToCenterServerRequest();
	}
	
	private BaseData createCommitRoleSocialToCenterServerRequest()
	{
		return new CommitRoleSocialToCenterServerRequest();
	}
	
	private BaseData createFuncAcceptMatchToCenterServerRequest()
	{
		return new FuncAcceptMatchToCenterServerRequest();
	}
	
	private BaseData createFuncApplyCancelMatchToCenterServerRequest()
	{
		return new FuncApplyCancelMatchToCenterServerRequest();
	}
	
	private BaseData createFuncApplyMatchToCenterServerRequest()
	{
		return new FuncApplyMatchToCenterServerRequest();
	}
	
	private BaseData createFuncCommitRankValueToCenterServerRequest()
	{
		return new FuncCommitRankValueToCenterServerRequest();
	}
	
	private BaseData createFuncGetRandomPlayerListFromRoleSocialPoolToCenterServerRequest()
	{
		return new FuncGetRandomPlayerListFromRoleSocialPoolToCenterServerRequest();
	}
	
	private BaseData createFuncPlayerToCenterServerRequest()
	{
		return new FuncPlayerToCenterServerRequest();
	}
	
	private BaseData createFuncRemoveRankToCenterServerRequest()
	{
		return new FuncRemoveRankToCenterServerRequest();
	}
	
	private BaseData createFuncSendAuctionBuyItemToCenterServerRequest()
	{
		return new FuncSendAuctionBuyItemToCenterServerRequest();
	}
	
	private BaseData createPlayerToCenterServerRequest()
	{
		return new PlayerToCenterServerRequest();
	}
	
	private BaseData createReCreateSignedSceneToCenterServerRequest()
	{
		return new ReCreateSignedSceneToCenterServerRequest();
	}
	
	private BaseData createReceiptWorkToCenterServerRequest()
	{
		return new ReceiptWorkToCenterServerRequest();
	}
	
	private BaseData createRefreshRoleSocialToCenterServerRequest()
	{
		return new RefreshRoleSocialToCenterServerRequest();
	}
	
	private BaseData createSendCenterWorkToCenterServerRequest()
	{
		return new SendCenterWorkToCenterServerRequest();
	}
	
	private BaseData createSendMQueryPlayerWorkResultToCenterServerRequest()
	{
		return new SendMQueryPlayerWorkResultToCenterServerRequest();
	}
	
	private BaseData createSendPlayerWorkToCenterServerRequest()
	{
		return new SendPlayerWorkToCenterServerRequest();
	}
	
	private BaseData createFuncSendAuctionBuyItemToSourceGameServerRequest()
	{
		return new FuncSendAuctionBuyItemToSourceGameServerRequest();
	}
	
	private BaseData createFuncCommitSubsectionRankValueToCenterServerRequest()
	{
		return new FuncCommitSubsectionRankValueToCenterServerRequest();
	}
	
	private BaseData createClientApplyBindPlatformToLoginServerRequest()
	{
		return new ClientApplyBindPlatformToLoginServerRequest();
	}
	
	private BaseData createLimitAreaToLoginServerRequest()
	{
		return new LimitAreaToLoginServerRequest();
	}
	
	private BaseData createReBeLoginToGameServerRequest()
	{
		return new ReBeLoginToGameServerRequest();
	}
	
	private BaseData createFuncRemoveSubsectionRankToCenterServerRequest()
	{
		return new FuncRemoveSubsectionRankToCenterServerRequest();
	}
	
	private BaseData createBeGameToGameServerRequest()
	{
		return new BeGameToGameServerRequest();
	}
	
	private BaseData createPlayerExitOverToSourceServerRequest()
	{
		return new PlayerExitOverToSourceServerRequest();
	}
	
	private BaseData createPlayerExitSwitchBackServerRequest()
	{
		return new PlayerExitSwitchBackServerRequest();
	}
	
	private BaseData createPlayerGameToGameServerRequest()
	{
		return new PlayerGameToGameServerRequest();
	}
	
	private BaseData createPlayerLoginToEachGameServerRequest()
	{
		return new PlayerLoginToEachGameServerRequest();
	}
	
	private BaseData createPlayerPreExitToGameServerRequest()
	{
		return new PlayerPreExitToGameServerRequest();
	}
	
	private BaseData createPlayerPreSwitchGameToGameServerRequest()
	{
		return new PlayerPreSwitchGameToGameServerRequest();
	}
	
	private BaseData createPlayerSwitchGameCompleteToSourceServerRequest()
	{
		return new PlayerSwitchGameCompleteToSourceServerRequest();
	}
	
	private BaseData createPlayerSwitchGameReceiveResultToSourceServerRequest()
	{
		return new PlayerSwitchGameReceiveResultToSourceServerRequest();
	}
	
	private BaseData createPlayerToGameTransGameToGameServerRequest()
	{
		return new PlayerToGameTransGameToGameServerRequest();
	}
	
	private BaseData createPreSwitchGameServerRequest()
	{
		return new PreSwitchGameServerRequest();
	}
	
	private BaseData createReBeGameToGameServerRequest()
	{
		return new ReBeGameToGameServerRequest();
	}
	
	private BaseData createRePlayerLoginFromEachGameServerRequest()
	{
		return new RePlayerLoginFromEachGameServerRequest();
	}
	
	private BaseData createRePlayerPreSwitchGameToGameServerRequest()
	{
		return new RePlayerPreSwitchGameToGameServerRequest();
	}
	
	private BaseData createRePreSwitchGameFailedServerRequest()
	{
		return new RePreSwitchGameFailedServerRequest();
	}
	
	private BaseData createRePreSwitchGameServerRequest()
	{
		return new RePreSwitchGameServerRequest();
	}
	
	private BaseData createReceiptWorkToGameServerRequest()
	{
		return new ReceiptWorkToGameServerRequest();
	}
	
	private BaseData createRefreshRoleShowToSourceGameServerRequest()
	{
		return new RefreshRoleShowToSourceGameServerRequest();
	}
	
	private BaseData createSaveSwitchedPlayerListServerRequest()
	{
		return new SaveSwitchedPlayerListServerRequest();
	}
	
	private BaseData createSendPlayerCenterRequestListToGameServerRequest()
	{
		return new SendPlayerCenterRequestListToGameServerRequest();
	}
	
	private BaseData createSendPlayerCenterRequestToGameServerRequest()
	{
		return new SendPlayerCenterRequestToGameServerRequest();
	}
	
	private BaseData createSendPlayerToGameRequestListToGameServerRequest()
	{
		return new SendPlayerToGameRequestListToGameServerRequest();
	}
	
	private BaseData createSendPlayerToGameRequestToGameServerRequest()
	{
		return new SendPlayerToGameRequestToGameServerRequest();
	}
	
	private BaseData createSendPlayerWorkCompleteListToGameServerRequest()
	{
		return new SendPlayerWorkCompleteListToGameServerRequest();
	}
	
	private BaseData createSendPlayerWorkCompleteToGameServerRequest()
	{
		return new SendPlayerWorkCompleteToGameServerRequest();
	}
	
	private BaseData createSendPlayerWorkListToGameServerRequest()
	{
		return new SendPlayerWorkListToGameServerRequest();
	}
	
	private BaseData createSendPlayerWorkToGameServerRequest()
	{
		return new SendPlayerWorkToGameServerRequest();
	}
	
	private BaseData createSendAreaWorkToGameServerRequest()
	{
		return new SendAreaWorkToGameServerRequest();
	}
	
	private BaseData createSendAreaWorkCompleteToGameServerRequest()
	{
		return new SendAreaWorkCompleteToGameServerRequest();
	}
	
	private BaseData createSendGameRequestToPlayerServerRequest()
	{
		return new SendGameRequestToPlayerServerRequest();
	}
	
	private BaseData createUseActivationCodeToCenterServerRequest()
	{
		return new UseActivationCodeToCenterServerRequest();
	}
	
	private BaseData createSendUserWorkToLoginServerRequest()
	{
		return new SendUserWorkToLoginServerRequest();
	}
	
	private BaseData createFuncSendGetPageShowToCenterServerRequest()
	{
		return new FuncSendGetPageShowToCenterServerRequest();
	}
	
	private BaseData createFuncReGetPageShowGameToPlayerServerRequest()
	{
		return new FuncReGetPageShowGameToPlayerServerRequest();
	}
	
	private BaseData createFuncSendGetPageShowToGameServerRequest()
	{
		return new FuncSendGetPageShowToGameServerRequest();
	}
	
	private BaseData createGameLoginToCenterServerRequest()
	{
		return new GameLoginToCenterServerRequest();
	}
	
	private BaseData createFuncAddRoleGroupSimpleToGameServerRequest()
	{
		return new FuncAddRoleGroupSimpleToGameServerRequest();
	}
	
	private BaseData createFuncRemoveRoleGroupSimpleToGameServerRequest()
	{
		return new FuncRemoveRoleGroupSimpleToGameServerRequest();
	}
	
	private BaseData createFuncSendRoleGroupRemoveMemberToPlayerServerRequest()
	{
		return new FuncSendRoleGroupRemoveMemberToPlayerServerRequest();
	}
	
	private BaseData createFuncRoleGroupChangeSimpleToGameServerRequest()
	{
		return new FuncRoleGroupChangeSimpleToGameServerRequest();
	}
	
	private BaseData createFuncChangeRoleGroupApplyTypeToGameServerRequest()
	{
		return new FuncChangeRoleGroupApplyTypeToGameServerRequest();
	}
	
	private BaseData createFuncChangeRoleGroupNameToGameServerRequest()
	{
		return new FuncChangeRoleGroupNameToGameServerRequest();
	}
	
	private BaseData createFuncRoleGroupEnterOwnSceneToGameServerRequest()
	{
		return new FuncRoleGroupEnterOwnSceneToGameServerRequest();
	}
	
	private BaseData createFuncRoleGroupReEnterOwnSceneArgToPlayerServerRequest()
	{
		return new FuncRoleGroupReEnterOwnSceneArgToPlayerServerRequest();
	}
	
	private BaseData createCommitCustomRoleSocialToCenterServerRequest()
	{
		return new CommitCustomRoleSocialToCenterServerRequest();
	}
	
	private BaseData createFuncHandleApplyRoleGroupToGameServerRequest()
	{
		return new FuncHandleApplyRoleGroupToGameServerRequest();
	}
	
	private BaseData createFuncPlayerRoleGroupGameToGameServerRequest()
	{
		return new FuncPlayerRoleGroupGameToGameServerRequest();
	}
	
	private BaseData createFuncRoleGroupChangeToPlayerServerRequest()
	{
		return new FuncRoleGroupChangeToPlayerServerRequest();
	}
	
	private BaseData createFuncSendChangeLeaderRoleGroupToPlayerServerRequest()
	{
		return new FuncSendChangeLeaderRoleGroupToPlayerServerRequest();
	}
	
	private BaseData createFuncSendRoleGroupAddMemberToPlayerServerRequest()
	{
		return new FuncSendRoleGroupAddMemberToPlayerServerRequest();
	}
	
	private BaseData createFuncRoleGroupRefreshRoleShowDataToGameServerRequest()
	{
		return new FuncRoleGroupRefreshRoleShowDataToGameServerRequest();
	}
	
	private BaseData createFuncRoleGroupRefreshRoleShowDataToPlayerServerRequest()
	{
		return new FuncRoleGroupRefreshRoleShowDataToPlayerServerRequest();
	}
	
	private BaseData createFuncRoleGroupMemberChangeToPlayerServerRequest()
	{
		return new FuncRoleGroupMemberChangeToPlayerServerRequest();
	}
	
	private BaseData createSignedSceneGameToGameServerRequest()
	{
		return new SignedSceneGameToGameServerRequest();
	}
	
	private BaseData createFuncAgreeInviteCreateRoleGroupToGameServerRequest()
	{
		return new FuncAgreeInviteCreateRoleGroupToGameServerRequest();
	}
	
	private BaseData createFuncAgreeInviteRoleGroupToGameServerRequest()
	{
		return new FuncAgreeInviteRoleGroupToGameServerRequest();
	}
	
	private BaseData createFuncApplyRoleGroupToGameServerRequest()
	{
		return new FuncApplyRoleGroupToGameServerRequest();
	}
	
	private BaseData createFuncChangeLeaderRoleGroupToGameServerRequest()
	{
		return new FuncChangeLeaderRoleGroupToGameServerRequest();
	}
	
	private BaseData createFuncChangeRoleGroupNoticeToGameServerRequest()
	{
		return new FuncChangeRoleGroupNoticeToGameServerRequest();
	}
	
	private BaseData createFuncDisbandRoleGroupToGameServerRequest()
	{
		return new FuncDisbandRoleGroupToGameServerRequest();
	}
	
	private BaseData createFuncKickMemberRoleGroupToGameServerRequest()
	{
		return new FuncKickMemberRoleGroupToGameServerRequest();
	}
	
	private BaseData createFuncLeaveRoleGroupToGameServerRequest()
	{
		return new FuncLeaveRoleGroupToGameServerRequest();
	}
	
	private BaseData createFuncPlayerGameToGameServerRequest()
	{
		return new FuncPlayerGameToGameServerRequest();
	}
	
	private BaseData createFuncRefreshTitleRoleGroupToPlayerServerRequest()
	{
		return new FuncRefreshTitleRoleGroupToPlayerServerRequest();
	}
	
	private BaseData createFuncRoleGroupToGameServerRequest()
	{
		return new FuncRoleGroupToGameServerRequest();
	}
	
	private BaseData createFuncSetTitleRoleGroupToGameServerRequest()
	{
		return new FuncSetTitleRoleGroupToGameServerRequest();
	}
	
	private BaseData createFuncToGameServerRequest()
	{
		return new FuncToGameServerRequest();
	}
	
	private BaseData createSendPlayerOnlineNumToCenterServerRequest()
	{
		return new SendPlayerOnlineNumToCenterServerRequest();
	}
	
	private BaseData createReUserLoginToLoginServerRequest()
	{
		return new ReUserLoginToLoginServerRequest();
	}
	
	private BaseData createRadioPlayerChatToGameServerRequest()
	{
		return new RadioPlayerChatToGameServerRequest();
	}
	
	private BaseData createSendPlayerChatToPlayerServerRequest()
	{
		return new SendPlayerChatToPlayerServerRequest();
	}
	
	private BaseData createRefreshGameLoginLimitToGameServerRequest()
	{
		return new RefreshGameLoginLimitToGameServerRequest();
	}
	
	private BaseData createGetRoleSocialDataToPlayerServerRequest()
	{
		return new GetRoleSocialDataToPlayerServerRequest();
	}
	
	private BaseData createBeGameToManagerServerRequest()
	{
		return new BeGameToManagerServerRequest();
	}
	
	private BaseData createPlayerCallSwitchBackToGameServerRequest()
	{
		return new PlayerCallSwitchBackToGameServerRequest();
	}
	
	private BaseData createRePlayerPreExitToGameServerRequest()
	{
		return new RePlayerPreExitToGameServerRequest();
	}
	
	private BaseData createCommitRoleGroupToCenterServerRequest()
	{
		return new CommitRoleGroupToCenterServerRequest();
	}
	
	private BaseData createSendCenterWorkCompleteToCenterServerRequest()
	{
		return new SendCenterWorkCompleteToCenterServerRequest();
	}
	
	private BaseData createFuncRoleGroupChangeSimpleToCenterServerRequest()
	{
		return new FuncRoleGroupChangeSimpleToCenterServerRequest();
	}
	
	private BaseData createFuncToCenterServerRequest()
	{
		return new FuncToCenterServerRequest();
	}
	
	private BaseData createFuncRefreshRankForRoleGroupServerRequest()
	{
		return new FuncRefreshRankForRoleGroupServerRequest();
	}
	
	private BaseData createFuncResetRankForRoleGroupServerRequest()
	{
		return new FuncResetRankForRoleGroupServerRequest();
	}
	
	private BaseData createFuncSendRoleGroupJoinResultServerRequest()
	{
		return new FuncSendRoleGroupJoinResultServerRequest();
	}
	
	private BaseData createFuncAgreeApplyNextRoleGroupToGameServerRequest()
	{
		return new FuncAgreeApplyNextRoleGroupToGameServerRequest();
	}
	
	private BaseData createRefreshGameLoginLimitToLoginServerRequest()
	{
		return new RefreshGameLoginLimitToLoginServerRequest();
	}
	
	private BaseData createBeGameToSceneServerRequest()
	{
		return new BeGameToSceneServerRequest();
	}
	
	private BaseData createPlayerEnterServerSceneServerRequest()
	{
		return new PlayerEnterServerSceneServerRequest();
	}
	
	private BaseData createPlayerSwitchToSceneServerRequest()
	{
		return new PlayerSwitchToSceneServerRequest();
	}
	
	private BaseData createPlayerGameToSceneServerRequest()
	{
		return new PlayerGameToSceneServerRequest();
	}
	
	private BaseData createPlayerLeaveSceneToSceneServerRequest()
	{
		return new PlayerLeaveSceneToSceneServerRequest();
	}
	
	private BaseData createClientGMToSceneServerRequest()
	{
		return new ClientGMToSceneServerRequest();
	}
	
}
