package com.home.commonBase.tool.generate;
import com.home.commonBase.constlist.generate.BaseDataType;
import com.home.commonBase.data.activity.ActivityData;
import com.home.commonBase.data.activity.ActivityServerData;
import com.home.commonBase.data.activity.UseActivationCodeSuccessOWData;
import com.home.commonBase.data.func.FuncInfoLogData;
import com.home.commonBase.data.func.FuncToolData;
import com.home.commonBase.data.func.PlayerFuncWorkData;
import com.home.commonBase.data.gm.GMAddCurrencyOWData;
import com.home.commonBase.data.gm.QueryPlayerLevelWData;
import com.home.commonBase.data.item.EquipContainerData;
import com.home.commonBase.data.item.ItemContainerData;
import com.home.commonBase.data.item.ItemData;
import com.home.commonBase.data.item.ItemDicContainerData;
import com.home.commonBase.data.item.ItemEquipData;
import com.home.commonBase.data.item.ItemIdentityData;
import com.home.commonBase.data.item.PlayerMailData;
import com.home.commonBase.data.item.UseItemArgData;
import com.home.commonBase.data.item.auction.AuctionBuyItemData;
import com.home.commonBase.data.item.auction.AuctionItemData;
import com.home.commonBase.data.item.auction.AuctionItemRecordData;
import com.home.commonBase.data.item.auction.AuctionQueryConditionData;
import com.home.commonBase.data.item.auction.AuctionReBuyItemOWData;
import com.home.commonBase.data.item.auction.AuctionReSellItemOWData;
import com.home.commonBase.data.item.auction.AuctionRemoveSellItemOWData;
import com.home.commonBase.data.item.auction.AuctionSoldItemOWData;
import com.home.commonBase.data.item.auction.AuctionSoldLogData;
import com.home.commonBase.data.item.auction.AuctionToolData;
import com.home.commonBase.data.item.auction.GameAuctionToolData;
import com.home.commonBase.data.item.auction.IntAuctionQueryConditionData;
import com.home.commonBase.data.item.auction.PlayerAuctionToolData;
import com.home.commonBase.data.login.CenterInitServerData;
import com.home.commonBase.data.login.ClientLoginData;
import com.home.commonBase.data.login.ClientLoginExData;
import com.home.commonBase.data.login.ClientLoginResultData;
import com.home.commonBase.data.login.ClientLoginServerInfoData;
import com.home.commonBase.data.login.ClientVersionData;
import com.home.commonBase.data.login.CreatePlayerData;
import com.home.commonBase.data.login.GameInitServerData;
import com.home.commonBase.data.login.GameLoginData;
import com.home.commonBase.data.login.GameLoginToCenterData;
import com.home.commonBase.data.login.GameLoginToGameData;
import com.home.commonBase.data.login.LoginInitServerData;
import com.home.commonBase.data.login.PlayerBindPlatformAWData;
import com.home.commonBase.data.login.PlayerCreatedWData;
import com.home.commonBase.data.login.PlayerDeletedToCenterWData;
import com.home.commonBase.data.login.PlayerDeletedWData;
import com.home.commonBase.data.login.PlayerLoginData;
import com.home.commonBase.data.login.PlayerLoginToEachGameData;
import com.home.commonBase.data.login.PlayerSwitchGameData;
import com.home.commonBase.data.login.RePlayerLoginFromEachGameData;
import com.home.commonBase.data.login.SceneInitServerData;
import com.home.commonBase.data.mail.AddMailOWData;
import com.home.commonBase.data.mail.MailData;
import com.home.commonBase.data.queryResult.IntQueryResultData;
import com.home.commonBase.data.quest.AchievementCompleteData;
import com.home.commonBase.data.quest.AchievementData;
import com.home.commonBase.data.quest.AchievementSaveData;
import com.home.commonBase.data.quest.QuestCompleteData;
import com.home.commonBase.data.quest.QuestData;
import com.home.commonBase.data.quest.TaskData;
import com.home.commonBase.data.role.CharacterSaveData;
import com.home.commonBase.data.role.CharacterUseData;
import com.home.commonBase.data.role.MUnitCacheData;
import com.home.commonBase.data.role.MUnitSaveData;
import com.home.commonBase.data.role.MUnitUseData;
import com.home.commonBase.data.role.PetSaveData;
import com.home.commonBase.data.role.PetUseData;
import com.home.commonBase.data.role.RoleShowChangeData;
import com.home.commonBase.data.role.RoleShowData;
import com.home.commonBase.data.role.RoleSimpleShowData;
import com.home.commonBase.data.scene.base.BuffData;
import com.home.commonBase.data.scene.base.BulletData;
import com.home.commonBase.data.scene.base.CDData;
import com.home.commonBase.data.scene.base.DirData;
import com.home.commonBase.data.scene.base.DriveData;
import com.home.commonBase.data.scene.base.PosData;
import com.home.commonBase.data.scene.base.PosDirData;
import com.home.commonBase.data.scene.base.RectData;
import com.home.commonBase.data.scene.base.RegionData;
import com.home.commonBase.data.scene.base.SkillData;
import com.home.commonBase.data.scene.fight.DamageOneData;
import com.home.commonBase.data.scene.fight.FrameSyncCommandData;
import com.home.commonBase.data.scene.fight.FrameSyncData;
import com.home.commonBase.data.scene.fight.SkillTargetData;
import com.home.commonBase.data.scene.match.MatchSceneData;
import com.home.commonBase.data.scene.match.PlayerMatchData;
import com.home.commonBase.data.scene.match.PlayerMatchSuccessWData;
import com.home.commonBase.data.scene.role.RoleAttributeData;
import com.home.commonBase.data.scene.role.RoleForceData;
import com.home.commonBase.data.scene.role.SceneRoleData;
import com.home.commonBase.data.scene.scene.BattleSceneData;
import com.home.commonBase.data.scene.scene.CreateSceneData;
import com.home.commonBase.data.scene.scene.FieldItemBagBindData;
import com.home.commonBase.data.scene.scene.SceneEnterArgData;
import com.home.commonBase.data.scene.scene.SceneEnterData;
import com.home.commonBase.data.scene.scene.SceneLocationData;
import com.home.commonBase.data.scene.scene.SceneLocationRoleShowChangeData;
import com.home.commonBase.data.scene.scene.ScenePreInfoData;
import com.home.commonBase.data.scene.scene.SceneServerEnterData;
import com.home.commonBase.data.scene.scene.SceneServerExitData;
import com.home.commonBase.data.scene.unit.UnitAIData;
import com.home.commonBase.data.scene.unit.UnitAvatarData;
import com.home.commonBase.data.scene.unit.UnitData;
import com.home.commonBase.data.scene.unit.UnitFightData;
import com.home.commonBase.data.scene.unit.UnitFightExData;
import com.home.commonBase.data.scene.unit.UnitFuncData;
import com.home.commonBase.data.scene.unit.UnitIdentityData;
import com.home.commonBase.data.scene.unit.UnitInfoData;
import com.home.commonBase.data.scene.unit.UnitMoveData;
import com.home.commonBase.data.scene.unit.UnitNormalData;
import com.home.commonBase.data.scene.unit.UnitPosData;
import com.home.commonBase.data.scene.unit.UnitSimpleData;
import com.home.commonBase.data.scene.unit.identity.BuildingIdentityData;
import com.home.commonBase.data.scene.unit.identity.CharacterIdentityData;
import com.home.commonBase.data.scene.unit.identity.FieldItemBagIdentityData;
import com.home.commonBase.data.scene.unit.identity.FieldItemIdentityData;
import com.home.commonBase.data.scene.unit.identity.FightUnitIdentityData;
import com.home.commonBase.data.scene.unit.identity.MUnitIdentityData;
import com.home.commonBase.data.scene.unit.identity.MonsterIdentityData;
import com.home.commonBase.data.scene.unit.identity.NPCIdentityData;
import com.home.commonBase.data.scene.unit.identity.OperationIdentityData;
import com.home.commonBase.data.scene.unit.identity.PetIdentityData;
import com.home.commonBase.data.scene.unit.identity.PuppetIdentityData;
import com.home.commonBase.data.scene.unit.identity.SceneEffectIdentityData;
import com.home.commonBase.data.scene.unit.identity.VehicleIdentityData;
import com.home.commonBase.data.social.QueryPlayerAWData;
import com.home.commonBase.data.social.ReGetRoleSocialDataWData;
import com.home.commonBase.data.social.ReQueryPlayerOWData;
import com.home.commonBase.data.social.RefreshPartRoleShowDataWData;
import com.home.commonBase.data.social.RoleSocialData;
import com.home.commonBase.data.social.RoleSocialPoolData;
import com.home.commonBase.data.social.RoleSocialPoolToolData;
import com.home.commonBase.data.social.base.QueryPlayerResultData;
import com.home.commonBase.data.social.base.RoleShowLogData;
import com.home.commonBase.data.social.chat.ChatChannelData;
import com.home.commonBase.data.social.chat.ChatData;
import com.home.commonBase.data.social.chat.ChatElementData;
import com.home.commonBase.data.social.chat.ItemChatElementData;
import com.home.commonBase.data.social.chat.RoleChatData;
import com.home.commonBase.data.social.chat.SendPlayerChatOWData;
import com.home.commonBase.data.social.friend.AddBeFriendOWData;
import com.home.commonBase.data.social.friend.AddFriendFailedForOvonicAWData;
import com.home.commonBase.data.social.friend.AddFriendForOvonicAWData;
import com.home.commonBase.data.social.friend.ApplyAddFriendData;
import com.home.commonBase.data.social.friend.ApplyAddFriendOWData;
import com.home.commonBase.data.social.friend.ContactData;
import com.home.commonBase.data.social.friend.FriendData;
import com.home.commonBase.data.social.friend.RemoveBeFriendOWData;
import com.home.commonBase.data.social.friend.RemoveFriendOWData;
import com.home.commonBase.data.social.rank.PlayerRankData;
import com.home.commonBase.data.social.rank.PlayerRankToolData;
import com.home.commonBase.data.social.rank.PlayerSubsectionRankToolData;
import com.home.commonBase.data.social.rank.RankData;
import com.home.commonBase.data.social.rank.RankSimpleData;
import com.home.commonBase.data.social.rank.RankToolData;
import com.home.commonBase.data.social.rank.RoleGroupRankData;
import com.home.commonBase.data.social.rank.SubsectionRankSimpleData;
import com.home.commonBase.data.social.rank.SubsectionRankToolData;
import com.home.commonBase.data.social.roleGroup.CenterRoleGroupToolData;
import com.home.commonBase.data.social.roleGroup.CreateRoleGroupData;
import com.home.commonBase.data.social.roleGroup.InviteRoleGroupReceiveData;
import com.home.commonBase.data.social.roleGroup.PlayerApplyRoleGroupData;
import com.home.commonBase.data.social.roleGroup.PlayerApplyRoleGroupSelfData;
import com.home.commonBase.data.social.roleGroup.PlayerRoleGroupClientToolData;
import com.home.commonBase.data.social.roleGroup.PlayerRoleGroupData;
import com.home.commonBase.data.social.roleGroup.PlayerRoleGroupExData;
import com.home.commonBase.data.social.roleGroup.PlayerRoleGroupMemberData;
import com.home.commonBase.data.social.roleGroup.PlayerRoleGroupSaveData;
import com.home.commonBase.data.social.roleGroup.PlayerRoleGroupToolData;
import com.home.commonBase.data.social.roleGroup.RoleGroupChangeData;
import com.home.commonBase.data.social.roleGroup.RoleGroupCreateSceneData;
import com.home.commonBase.data.social.roleGroup.RoleGroupData;
import com.home.commonBase.data.social.roleGroup.RoleGroupMemberChangeData;
import com.home.commonBase.data.social.roleGroup.RoleGroupMemberData;
import com.home.commonBase.data.social.roleGroup.RoleGroupSimpleData;
import com.home.commonBase.data.social.roleGroup.RoleGroupToolData;
import com.home.commonBase.data.social.roleGroup.work.AddApplyRoleGroupSelfWData;
import com.home.commonBase.data.social.roleGroup.work.CreateRoleGroupResultOWData;
import com.home.commonBase.data.social.roleGroup.work.CreateRoleGroupToCenterWData;
import com.home.commonBase.data.social.roleGroup.work.CreateRoleGroupWData;
import com.home.commonBase.data.social.roleGroup.work.HandleApplyRoleGroupWData;
import com.home.commonBase.data.social.roleGroup.work.InviteRoleGroupWData;
import com.home.commonBase.data.social.roleGroup.work.JoinRoleGroupWData;
import com.home.commonBase.data.social.roleGroup.work.LeaveRoleGroupWData;
import com.home.commonBase.data.social.roleGroup.work.PlayerToRoleGroupTCCResultWData;
import com.home.commonBase.data.social.roleGroup.work.PlayerToRoleGroupTCCWData;
import com.home.commonBase.data.social.roleGroup.work.RemoveRoleGroupToCenterWData;
import com.home.commonBase.data.social.roleGroup.work.RoleGroupWorkData;
import com.home.commonBase.data.social.team.CreateTeamData;
import com.home.commonBase.data.social.team.PlayerTeamData;
import com.home.commonBase.data.social.team.TeamData;
import com.home.commonBase.data.social.team.TeamMemberData;
import com.home.commonBase.data.social.team.TeamSimpleData;
import com.home.commonBase.data.social.union.CreateUnionData;
import com.home.commonBase.data.social.union.PlayerUnionData;
import com.home.commonBase.data.social.union.PlayerUnionSaveData;
import com.home.commonBase.data.social.union.UnionData;
import com.home.commonBase.data.social.union.UnionMemberData;
import com.home.commonBase.data.social.union.UnionSimpleData;
import com.home.commonBase.data.system.AreaClientData;
import com.home.commonBase.data.system.AreaGlobalWorkCompleteData;
import com.home.commonBase.data.system.AreaGlobalWorkData;
import com.home.commonBase.data.system.AreaServerData;
import com.home.commonBase.data.system.BigFloatData;
import com.home.commonBase.data.system.CenterGlobalWorkData;
import com.home.commonBase.data.system.ClientOfflineWorkData;
import com.home.commonBase.data.system.ClientOfflineWorkListData;
import com.home.commonBase.data.system.ClientPlayerLocalCacheData;
import com.home.commonBase.data.system.CountData;
import com.home.commonBase.data.system.GameGlobalWorkData;
import com.home.commonBase.data.system.GameServerClientSimpleData;
import com.home.commonBase.data.system.GameServerInfoData;
import com.home.commonBase.data.system.GameServerRunData;
import com.home.commonBase.data.system.GameServerSimpleInfoData;
import com.home.commonBase.data.system.InfoLogData;
import com.home.commonBase.data.system.InfoLogWData;
import com.home.commonBase.data.system.KeepSaveData;
import com.home.commonBase.data.system.KeyData;
import com.home.commonBase.data.system.PlayerPrimaryKeyData;
import com.home.commonBase.data.system.PlayerToPlayerTCCResultWData;
import com.home.commonBase.data.system.PlayerToPlayerTCCWData;
import com.home.commonBase.data.system.PlayerWorkCompleteData;
import com.home.commonBase.data.system.PlayerWorkData;
import com.home.commonBase.data.system.PlayerWorkListData;
import com.home.commonBase.data.system.RoleShowInfoLogData;
import com.home.commonBase.data.system.SaveVersionData;
import com.home.commonBase.data.system.ServerInfoData;
import com.home.commonBase.data.system.ServerSimpleInfoData;
import com.home.commonBase.data.system.UserWorkData;
import com.home.commonBase.data.system.WorkCompleteData;
import com.home.commonBase.data.system.WorkData;
import com.home.commonBase.data.system.WorkReceiverData;
import com.home.commonBase.data.system.WorkSenderData;
import com.home.commonBase.data.test.Test2Data;
import com.home.commonBase.data.test.TestData;
import com.home.shine.data.BaseData;
import com.home.shine.tool.CreateDataFunc;
import com.home.shine.tool.DataMaker;

/** (generated by shine) */
public class BaseDataMaker extends DataMaker
{
	public BaseDataMaker()
	{
		offSet=BaseDataType.off;
		list=new CreateDataFunc[BaseDataType.count-offSet];
		list[BaseDataType.AchievementComplete-offSet]=this::createAchievementCompleteData;
		list[BaseDataType.Achievement-offSet]=this::createAchievementData;
		list[BaseDataType.AchievementSave-offSet]=this::createAchievementSaveData;
		list[BaseDataType.Activity-offSet]=this::createActivityData;
		list[BaseDataType.ActivityServer-offSet]=this::createActivityServerData;
		list[BaseDataType.AddBeFriendOW-offSet]=this::createAddBeFriendOWData;
		list[BaseDataType.AddFriendFailedForOvonicAW-offSet]=this::createAddFriendFailedForOvonicAWData;
		list[BaseDataType.AddFriendForOvonicAW-offSet]=this::createAddFriendForOvonicAWData;
		list[BaseDataType.AddMailOW-offSet]=this::createAddMailOWData;
		list[BaseDataType.ApplyAddFriend-offSet]=this::createApplyAddFriendData;
		list[BaseDataType.ApplyAddFriendOW-offSet]=this::createApplyAddFriendOWData;
		list[BaseDataType.AreaClient-offSet]=this::createAreaClientData;
		list[BaseDataType.SceneServerEnter-offSet]=this::createSceneServerEnterData;
		list[BaseDataType.Buff-offSet]=this::createBuffData;
		list[BaseDataType.Bullet-offSet]=this::createBulletData;
		list[BaseDataType.CD-offSet]=this::createCDData;
		list[BaseDataType.CharacterIdentity-offSet]=this::createCharacterIdentityData;
		list[BaseDataType.CharacterSave-offSet]=this::createCharacterSaveData;
		list[BaseDataType.CharacterUse-offSet]=this::createCharacterUseData;
		list[BaseDataType.Chat-offSet]=this::createChatData;
		list[BaseDataType.ChatElement-offSet]=this::createChatElementData;
		list[BaseDataType.ClientLogin-offSet]=this::createClientLoginData;
		list[BaseDataType.ClientLoginEx-offSet]=this::createClientLoginExData;
		list[BaseDataType.ClientOfflineWork-offSet]=this::createClientOfflineWorkData;
		list[BaseDataType.ClientOfflineWorkList-offSet]=this::createClientOfflineWorkListData;
		list[BaseDataType.ClientPlayerLocalCache-offSet]=this::createClientPlayerLocalCacheData;
		list[BaseDataType.ClientVersion-offSet]=this::createClientVersionData;
		list[BaseDataType.Contact-offSet]=this::createContactData;
		list[BaseDataType.CreatePlayer-offSet]=this::createCreatePlayerData;
		list[BaseDataType.DamageOne-offSet]=this::createDamageOneData;
		list[BaseDataType.Dir-offSet]=this::createDirData;
		list[BaseDataType.FrameSyncCommand-offSet]=this::createFrameSyncCommandData;
		list[BaseDataType.FrameSync-offSet]=this::createFrameSyncData;
		list[BaseDataType.Friend-offSet]=this::createFriendData;
		list[BaseDataType.FuncTool-offSet]=this::createFuncToolData;
		list[BaseDataType.GMAddCurrencyOW-offSet]=this::createGMAddCurrencyOWData;
		list[BaseDataType.GameGlobalWork-offSet]=this::createGameGlobalWorkData;
		list[BaseDataType.GameLogin-offSet]=this::createGameLoginData;
		list[BaseDataType.GameServerClientSimple-offSet]=this::createGameServerClientSimpleData;
		list[BaseDataType.GameServerInfo-offSet]=this::createGameServerInfoData;
		list[BaseDataType.GameServerRun-offSet]=this::createGameServerRunData;
		list[BaseDataType.GameServerSimpleInfo-offSet]=this::createGameServerSimpleInfoData;
		list[BaseDataType.IntQueryResult-offSet]=this::createIntQueryResultData;
		list[BaseDataType.ItemContainer-offSet]=this::createItemContainerData;
		list[BaseDataType.Item-offSet]=this::createItemData;
		list[BaseDataType.ItemEquip-offSet]=this::createItemEquipData;
		list[BaseDataType.ItemIdentity-offSet]=this::createItemIdentityData;
		list[BaseDataType.KeepSave-offSet]=this::createKeepSaveData;
		list[BaseDataType.Key-offSet]=this::createKeyData;
		list[BaseDataType.MUnitCache-offSet]=this::createMUnitCacheData;
		list[BaseDataType.MUnitUse-offSet]=this::createMUnitUseData;
		list[BaseDataType.Mail-offSet]=this::createMailData;
		list[BaseDataType.MonsterIdentity-offSet]=this::createMonsterIdentityData;
		list[BaseDataType.NPCIdentity-offSet]=this::createNPCIdentityData;
		list[BaseDataType.MUnitIdentity-offSet]=this::createMUnitIdentityData;
		list[BaseDataType.PetUse-offSet]=this::createPetUseData;
		list[BaseDataType.GameAuctionTool-offSet]=this::createGameAuctionToolData;
		list[BaseDataType.PlayerLogin-offSet]=this::createPlayerLoginData;
		list[BaseDataType.PlayerMail-offSet]=this::createPlayerMailData;
		list[BaseDataType.PlayerMatch-offSet]=this::createPlayerMatchData;
		list[BaseDataType.PlayerPrimaryKey-offSet]=this::createPlayerPrimaryKeyData;
		list[BaseDataType.PlayerRank-offSet]=this::createPlayerRankData;
		list[BaseDataType.PlayerRankTool-offSet]=this::createPlayerRankToolData;
		list[BaseDataType.PlayerSwitchGame-offSet]=this::createPlayerSwitchGameData;
		list[BaseDataType.PlayerWork-offSet]=this::createPlayerWorkData;
		list[BaseDataType.PlayerWorkList-offSet]=this::createPlayerWorkListData;
		list[BaseDataType.Pos-offSet]=this::createPosData;
		list[BaseDataType.PosDir-offSet]=this::createPosDirData;
		list[BaseDataType.PuppetIdentity-offSet]=this::createPuppetIdentityData;
		list[BaseDataType.QueryPlayerLevelW-offSet]=this::createQueryPlayerLevelWData;
		list[BaseDataType.QuestComplete-offSet]=this::createQuestCompleteData;
		list[BaseDataType.Quest-offSet]=this::createQuestData;
		list[BaseDataType.Rank-offSet]=this::createRankData;
		list[BaseDataType.RankSimple-offSet]=this::createRankSimpleData;
		list[BaseDataType.RankTool-offSet]=this::createRankToolData;
		list[BaseDataType.ReGetRoleSocialDataW-offSet]=this::createReGetRoleSocialDataWData;
		list[BaseDataType.RefreshPartRoleShowDataW-offSet]=this::createRefreshPartRoleShowDataWData;
		list[BaseDataType.RemoveBeFriendOW-offSet]=this::createRemoveBeFriendOWData;
		list[BaseDataType.RemoveFriendOW-offSet]=this::createRemoveFriendOWData;
		list[BaseDataType.RoleShowChange-offSet]=this::createRoleShowChangeData;
		list[BaseDataType.RoleShow-offSet]=this::createRoleShowData;
		list[BaseDataType.RoleSocial-offSet]=this::createRoleSocialData;
		list[BaseDataType.SaveVersion-offSet]=this::createSaveVersionData;
		list[BaseDataType.SceneEffectIdentity-offSet]=this::createSceneEffectIdentityData;
		list[BaseDataType.SceneEnterArg-offSet]=this::createSceneEnterArgData;
		list[BaseDataType.SceneEnter-offSet]=this::createSceneEnterData;
		list[BaseDataType.ScenePreInfo-offSet]=this::createScenePreInfoData;
		list[BaseDataType.ServerInfo-offSet]=this::createServerInfoData;
		list[BaseDataType.ServerSimpleInfo-offSet]=this::createServerSimpleInfoData;
		list[BaseDataType.Skill-offSet]=this::createSkillData;
		list[BaseDataType.SkillTarget-offSet]=this::createSkillTargetData;
		list[BaseDataType.Task-offSet]=this::createTaskData;
		list[BaseDataType.Test2-offSet]=this::createTest2Data;
		list[BaseDataType.Test-offSet]=this::createTestData;
		list[BaseDataType.UnitAI-offSet]=this::createUnitAIData;
		list[BaseDataType.UnitAvatar-offSet]=this::createUnitAvatarData;
		list[BaseDataType.Unit-offSet]=this::createUnitData;
		list[BaseDataType.UnitFight-offSet]=this::createUnitFightData;
		list[BaseDataType.UnitFightEx-offSet]=this::createUnitFightExData;
		list[BaseDataType.UnitIdentity-offSet]=this::createUnitIdentityData;
		list[BaseDataType.UnitInfo-offSet]=this::createUnitInfoData;
		list[BaseDataType.UnitPos-offSet]=this::createUnitPosData;
		list[BaseDataType.UseItemArg-offSet]=this::createUseItemArgData;
		list[BaseDataType.VehicleIdentity-offSet]=this::createVehicleIdentityData;
		list[BaseDataType.Work-offSet]=this::createWorkData;
		list[BaseDataType.WorkReceiver-offSet]=this::createWorkReceiverData;
		list[BaseDataType.WorkSender-offSet]=this::createWorkSenderData;
		list[BaseDataType.AuctionReSellItemOW-offSet]=this::createAuctionReSellItemOWData;
		list[BaseDataType.InviteRoleGroupReceive-offSet]=this::createInviteRoleGroupReceiveData;
		list[BaseDataType.AuctionBuyItem-offSet]=this::createAuctionBuyItemData;
		list[BaseDataType.ItemDicContainer-offSet]=this::createItemDicContainerData;
		list[BaseDataType.AuctionReBuyItemOW-offSet]=this::createAuctionReBuyItemOWData;
		list[BaseDataType.RoleSocialPool-offSet]=this::createRoleSocialPoolData;
		list[BaseDataType.RoleSocialPoolTool-offSet]=this::createRoleSocialPoolToolData;
		list[BaseDataType.QueryPlayerAW-offSet]=this::createQueryPlayerAWData;
		list[BaseDataType.QueryPlayerResult-offSet]=this::createQueryPlayerResultData;
		list[BaseDataType.ReQueryPlayerOW-offSet]=this::createReQueryPlayerOWData;
		list[BaseDataType.AuctionSoldItemOW-offSet]=this::createAuctionSoldItemOWData;
		list[BaseDataType.BigFloat-offSet]=this::createBigFloatData;
		list[BaseDataType.FuncInfoLog-offSet]=this::createFuncInfoLogData;
		list[BaseDataType.AuctionRemoveSellItemOW-offSet]=this::createAuctionRemoveSellItemOWData;
		list[BaseDataType.OperationIdentity-offSet]=this::createOperationIdentityData;
		list[BaseDataType.FieldItemIdentity-offSet]=this::createFieldItemIdentityData;
		list[BaseDataType.FightUnitIdentity-offSet]=this::createFightUnitIdentityData;
		list[BaseDataType.EquipContainer-offSet]=this::createEquipContainerData;
		list[BaseDataType.MUnitSave-offSet]=this::createMUnitSaveData;
		list[BaseDataType.PetSave-offSet]=this::createPetSaveData;
		list[BaseDataType.RoleAttribute-offSet]=this::createRoleAttributeData;
		list[BaseDataType.SceneRole-offSet]=this::createSceneRoleData;
		list[BaseDataType.BuildingIdentity-offSet]=this::createBuildingIdentityData;
		list[BaseDataType.RoleForce-offSet]=this::createRoleForceData;
		list[BaseDataType.SubsectionRankTool-offSet]=this::createSubsectionRankToolData;
		list[BaseDataType.ClientLoginServerInfo-offSet]=this::createClientLoginServerInfoData;
		list[BaseDataType.PlayerRoleGroupTool-offSet]=this::createPlayerRoleGroupToolData;
		list[BaseDataType.RoleGroup-offSet]=this::createRoleGroupData;
		list[BaseDataType.RoleGroupMember-offSet]=this::createRoleGroupMemberData;
		list[BaseDataType.RoleGroupTool-offSet]=this::createRoleGroupToolData;
		list[BaseDataType.ClientLoginResult-offSet]=this::createClientLoginResultData;
		list[BaseDataType.AreaServer-offSet]=this::createAreaServerData;
		list[BaseDataType.PlayerBindPlatformAW-offSet]=this::createPlayerBindPlatformAWData;
		list[BaseDataType.SubsectionRankSimple-offSet]=this::createSubsectionRankSimpleData;
		list[BaseDataType.PlayerSubsectionRankTool-offSet]=this::createPlayerSubsectionRankToolData;
		list[BaseDataType.PlayerLoginToEachGame-offSet]=this::createPlayerLoginToEachGameData;
		list[BaseDataType.RePlayerLoginFromEachGame-offSet]=this::createRePlayerLoginFromEachGameData;
		list[BaseDataType.PetIdentity-offSet]=this::createPetIdentityData;
		list[BaseDataType.PlayerWorkComplete-offSet]=this::createPlayerWorkCompleteData;
		list[BaseDataType.WorkComplete-offSet]=this::createWorkCompleteData;
		list[BaseDataType.CenterInitServer-offSet]=this::createCenterInitServerData;
		list[BaseDataType.AreaGlobalWork-offSet]=this::createAreaGlobalWorkData;
		list[BaseDataType.SceneInitServer-offSet]=this::createSceneInitServerData;
		list[BaseDataType.BattleScene-offSet]=this::createBattleSceneData;
		list[BaseDataType.AreaGlobalWorkComplete-offSet]=this::createAreaGlobalWorkCompleteData;
		list[BaseDataType.CreateRoleGroup-offSet]=this::createCreateRoleGroupData;
		list[BaseDataType.PlayerRoleGroupSave-offSet]=this::createPlayerRoleGroupSaveData;
		list[BaseDataType.SceneServerExit-offSet]=this::createSceneServerExitData;
		list[BaseDataType.PlayerRoleGroup-offSet]=this::createPlayerRoleGroupData;
		list[BaseDataType.PlayerApplyRoleGroup-offSet]=this::createPlayerApplyRoleGroupData;
		list[BaseDataType.MatchScene-offSet]=this::createMatchSceneData;
		list[BaseDataType.RoleGroupSimple-offSet]=this::createRoleGroupSimpleData;
		list[BaseDataType.CreateRoleGroupResultOW-offSet]=this::createCreateRoleGroupResultOWData;
		list[BaseDataType.CreateRoleGroupW-offSet]=this::createCreateRoleGroupWData;
		list[BaseDataType.InviteRoleGroupW-offSet]=this::createInviteRoleGroupWData;
		list[BaseDataType.PlayerFuncWork-offSet]=this::createPlayerFuncWorkData;
		list[BaseDataType.PlayerApplyRoleGroupSelf-offSet]=this::createPlayerApplyRoleGroupSelfData;
		list[BaseDataType.AddApplyRoleGroupSelfW-offSet]=this::createAddApplyRoleGroupSelfWData;
		list[BaseDataType.HandleApplyRoleGroupW-offSet]=this::createHandleApplyRoleGroupWData;
		list[BaseDataType.RoleGroupChange-offSet]=this::createRoleGroupChangeData;
		list[BaseDataType.PlayerRoleGroupClientTool-offSet]=this::createPlayerRoleGroupClientToolData;
		list[BaseDataType.PlayerUnion-offSet]=this::createPlayerUnionData;
		list[BaseDataType.Union-offSet]=this::createUnionData;
		list[BaseDataType.UnionMember-offSet]=this::createUnionMemberData;
		list[BaseDataType.CreateUnion-offSet]=this::createCreateUnionData;
		list[BaseDataType.UnionSimple-offSet]=this::createUnionSimpleData;
		list[BaseDataType.PlayerUnionSave-offSet]=this::createPlayerUnionSaveData;
		list[BaseDataType.JoinRoleGroupW-offSet]=this::createJoinRoleGroupWData;
		list[BaseDataType.LeaveRoleGroupW-offSet]=this::createLeaveRoleGroupWData;
		list[BaseDataType.UseActivationCodeSuccessOW-offSet]=this::createUseActivationCodeSuccessOWData;
		list[BaseDataType.Team-offSet]=this::createTeamData;
		list[BaseDataType.CreateTeam-offSet]=this::createCreateTeamData;
		list[BaseDataType.PlayerTeam-offSet]=this::createPlayerTeamData;
		list[BaseDataType.PlayerMatchSuccessW-offSet]=this::createPlayerMatchSuccessWData;
		list[BaseDataType.TeamMember-offSet]=this::createTeamMemberData;
		list[BaseDataType.TeamSimple-offSet]=this::createTeamSimpleData;
		list[BaseDataType.PlayerCreatedW-offSet]=this::createPlayerCreatedWData;
		list[BaseDataType.UserWork-offSet]=this::createUserWorkData;
		list[BaseDataType.PlayerDeletedW-offSet]=this::createPlayerDeletedWData;
		list[BaseDataType.InfoLog-offSet]=this::createInfoLogData;
		list[BaseDataType.RoleShowLog-offSet]=this::createRoleShowLogData;
		list[BaseDataType.InfoLogW-offSet]=this::createInfoLogWData;
		list[BaseDataType.GameInitServer-offSet]=this::createGameInitServerData;
		list[BaseDataType.LoginInitServer-offSet]=this::createLoginInitServerData;
		list[BaseDataType.GameLoginToCenter-offSet]=this::createGameLoginToCenterData;
		list[BaseDataType.GameLoginToGame-offSet]=this::createGameLoginToGameData;
		list[BaseDataType.PlayerToPlayerTCCW-offSet]=this::createPlayerToPlayerTCCWData;
		list[BaseDataType.PlayerToRoleGroupTCCW-offSet]=this::createPlayerToRoleGroupTCCWData;
		list[BaseDataType.RoleGroupWork-offSet]=this::createRoleGroupWorkData;
		list[BaseDataType.PlayerToRoleGroupTCCResultW-offSet]=this::createPlayerToRoleGroupTCCResultWData;
		list[BaseDataType.PlayerToPlayerTCCResultW-offSet]=this::createPlayerToPlayerTCCResultWData;
		list[BaseDataType.RoleShowInfoLog-offSet]=this::createRoleShowInfoLogData;
		list[BaseDataType.CreateScene-offSet]=this::createCreateSceneData;
		list[BaseDataType.RoleGroupCreateScene-offSet]=this::createRoleGroupCreateSceneData;
		list[BaseDataType.PlayerRoleGroupMember-offSet]=this::createPlayerRoleGroupMemberData;
		list[BaseDataType.RoleGroupMemberChange-offSet]=this::createRoleGroupMemberChangeData;
		list[BaseDataType.PlayerRoleGroupEx-offSet]=this::createPlayerRoleGroupExData;
		list[BaseDataType.UnitFunc-offSet]=this::createUnitFuncData;
		list[BaseDataType.UnitMove-offSet]=this::createUnitMoveData;
		list[BaseDataType.FieldItemBagIdentity-offSet]=this::createFieldItemBagIdentityData;
		list[BaseDataType.UnitNormal-offSet]=this::createUnitNormalData;
		list[BaseDataType.FieldItemBagBind-offSet]=this::createFieldItemBagBindData;
		list[BaseDataType.Rect-offSet]=this::createRectData;
		list[BaseDataType.ItemChatElement-offSet]=this::createItemChatElementData;
		list[BaseDataType.ChatChannel-offSet]=this::createChatChannelData;
		list[BaseDataType.RoleChat-offSet]=this::createRoleChatData;
		list[BaseDataType.RoleSimpleShow-offSet]=this::createRoleSimpleShowData;
		list[BaseDataType.SendPlayerChatOW-offSet]=this::createSendPlayerChatOWData;
		list[BaseDataType.SceneLocation-offSet]=this::createSceneLocationData;
		list[BaseDataType.SceneLocationRoleShowChange-offSet]=this::createSceneLocationRoleShowChangeData;
		list[BaseDataType.Region-offSet]=this::createRegionData;
		list[BaseDataType.UnitSimple-offSet]=this::createUnitSimpleData;
		list[BaseDataType.CenterRoleGroupTool-offSet]=this::createCenterRoleGroupToolData;
		list[BaseDataType.CreateRoleGroupToCenterW-offSet]=this::createCreateRoleGroupToCenterWData;
		list[BaseDataType.CenterGlobalWork-offSet]=this::createCenterGlobalWorkData;
		list[BaseDataType.RemoveRoleGroupToCenterW-offSet]=this::createRemoveRoleGroupToCenterWData;
		list[BaseDataType.RoleGroupRank-offSet]=this::createRoleGroupRankData;
		list[BaseDataType.Drive-offSet]=this::createDriveData;
		list[BaseDataType.PlayerDeletedToCenterW-offSet]=this::createPlayerDeletedToCenterWData;
		list[BaseDataType.AuctionItem-offSet]=this::createAuctionItemData;
		list[BaseDataType.AuctionSoldLog-offSet]=this::createAuctionSoldLogData;
		list[BaseDataType.AuctionItemRecord-offSet]=this::createAuctionItemRecordData;
		list[BaseDataType.AuctionTool-offSet]=this::createAuctionToolData;
		list[BaseDataType.Count-offSet]=this::createCountData;
		list[BaseDataType.AuctionQueryCondition-offSet]=this::createAuctionQueryConditionData;
		list[BaseDataType.IntAuctionQueryCondition-offSet]=this::createIntAuctionQueryConditionData;
		list[BaseDataType.PlayerAuctionTool-offSet]=this::createPlayerAuctionToolData;
	}
	
	private BaseData createRoleShowData()
	{
		return new RoleShowData();
	}
	
	private BaseData createCharacterSaveData()
	{
		return new CharacterSaveData();
	}
	
	private BaseData createMUnitCacheData()
	{
		return new MUnitCacheData();
	}
	
	private BaseData createMailData()
	{
		return new MailData();
	}
	
	private BaseData createAddMailOWData()
	{
		return new AddMailOWData();
	}
	
	private BaseData createAddBeFriendOWData()
	{
		return new AddBeFriendOWData();
	}
	
	private BaseData createAddFriendForOvonicAWData()
	{
		return new AddFriendForOvonicAWData();
	}
	
	private BaseData createApplyAddFriendData()
	{
		return new ApplyAddFriendData();
	}
	
	private BaseData createRemoveFriendOWData()
	{
		return new RemoveFriendOWData();
	}
	
	private BaseData createRemoveBeFriendOWData()
	{
		return new RemoveBeFriendOWData();
	}
	
	private BaseData createReGetRoleSocialDataWData()
	{
		return new ReGetRoleSocialDataWData();
	}
	
	private BaseData createPlayerRankToolData()
	{
		return new PlayerRankToolData();
	}
	
	private BaseData createPlayerRankData()
	{
		return new PlayerRankData();
	}
	
	private BaseData createKeepSaveData()
	{
		return new KeepSaveData();
	}
	
	private BaseData createSaveVersionData()
	{
		return new SaveVersionData();
	}
	
	private BaseData createWorkData()
	{
		return new WorkData();
	}
	
	private BaseData createGameServerSimpleInfoData()
	{
		return new GameServerSimpleInfoData();
	}
	
	private BaseData createItemEquipData()
	{
		return new ItemEquipData();
	}
	
	private BaseData createUseItemArgData()
	{
		return new UseItemArgData();
	}
	
	private BaseData createItemContainerData()
	{
		return new ItemContainerData();
	}
	
	private BaseData createQuestCompleteData()
	{
		return new QuestCompleteData();
	}
	
	private BaseData createTaskData()
	{
		return new TaskData();
	}
	
	private BaseData createAchievementCompleteData()
	{
		return new AchievementCompleteData();
	}
	
	private BaseData createFrameSyncData()
	{
		return new FrameSyncData();
	}
	
	private BaseData createSkillTargetData()
	{
		return new SkillTargetData();
	}
	
	private BaseData createFrameSyncCommandData()
	{
		return new FrameSyncCommandData();
	}
	
	private BaseData createPuppetIdentityData()
	{
		return new PuppetIdentityData();
	}
	
	private BaseData createSceneEffectIdentityData()
	{
		return new SceneEffectIdentityData();
	}
	
	private BaseData createCharacterIdentityData()
	{
		return new CharacterIdentityData();
	}
	
	private BaseData createMonsterIdentityData()
	{
		return new MonsterIdentityData();
	}
	
	private BaseData createUnitIdentityData()
	{
		return new UnitIdentityData();
	}
	
	private BaseData createUnitPosData()
	{
		return new UnitPosData();
	}
	
	private BaseData createUnitFightExData()
	{
		return new UnitFightExData();
	}
	
	private BaseData createUnitInfoData()
	{
		return new UnitInfoData();
	}
	
	private BaseData createUnitAIData()
	{
		return new UnitAIData();
	}
	
	private BaseData createPlayerMatchData()
	{
		return new PlayerMatchData();
	}
	
	private BaseData createSceneEnterArgData()
	{
		return new SceneEnterArgData();
	}
	
	private BaseData createBuffData()
	{
		return new BuffData();
	}
	
	private BaseData createCDData()
	{
		return new CDData();
	}
	
	private BaseData createPosData()
	{
		return new PosData();
	}
	
	private BaseData createBulletData()
	{
		return new BulletData();
	}
	
	private BaseData createSkillData()
	{
		return new SkillData();
	}
	
	private BaseData createCreatePlayerData()
	{
		return new CreatePlayerData();
	}
	
	private BaseData createPlayerSwitchGameData()
	{
		return new PlayerSwitchGameData();
	}
	
	private BaseData createGameLoginData()
	{
		return new GameLoginData();
	}
	
	private BaseData createFuncToolData()
	{
		return new FuncToolData();
	}
	
	private BaseData createChatElementData()
	{
		return new ChatElementData();
	}
	
	private BaseData createClientVersionData()
	{
		return new ClientVersionData();
	}
	
	private BaseData createPetUseData()
	{
		return new PetUseData();
	}
	
	private BaseData createMUnitUseData()
	{
		return new MUnitUseData();
	}
	
	private BaseData createRoleShowChangeData()
	{
		return new RoleShowChangeData();
	}
	
	private BaseData createCharacterUseData()
	{
		return new CharacterUseData();
	}
	
	private BaseData createTest2Data()
	{
		return new Test2Data();
	}
	
	private BaseData createTestData()
	{
		return new TestData();
	}
	
	private BaseData createActivityServerData()
	{
		return new ActivityServerData();
	}
	
	private BaseData createActivityData()
	{
		return new ActivityData();
	}
	
	private BaseData createFriendData()
	{
		return new FriendData();
	}
	
	private BaseData createApplyAddFriendOWData()
	{
		return new ApplyAddFriendOWData();
	}
	
	private BaseData createAddFriendFailedForOvonicAWData()
	{
		return new AddFriendFailedForOvonicAWData();
	}
	
	private BaseData createContactData()
	{
		return new ContactData();
	}
	
	private BaseData createChatData()
	{
		return new ChatData();
	}
	
	private BaseData createRoleSocialData()
	{
		return new RoleSocialData();
	}
	
	private BaseData createRefreshPartRoleShowDataWData()
	{
		return new RefreshPartRoleShowDataWData();
	}
	
	private BaseData createRankToolData()
	{
		return new RankToolData();
	}
	
	private BaseData createRankData()
	{
		return new RankData();
	}
	
	private BaseData createRankSimpleData()
	{
		return new RankSimpleData();
	}
	
	private BaseData createGameServerRunData()
	{
		return new GameServerRunData();
	}
	
	private BaseData createPlayerWorkListData()
	{
		return new PlayerWorkListData();
	}
	
	private BaseData createAreaClientData()
	{
		return new AreaClientData();
	}
	
	private BaseData createServerInfoData()
	{
		return new ServerInfoData();
	}
	
	private BaseData createGameServerInfoData()
	{
		return new GameServerInfoData();
	}
	
	private BaseData createKeyData()
	{
		return new KeyData();
	}
	
	private BaseData createGameServerClientSimpleData()
	{
		return new GameServerClientSimpleData();
	}
	
	private BaseData createPlayerPrimaryKeyData()
	{
		return new PlayerPrimaryKeyData();
	}
	
	private BaseData createServerSimpleInfoData()
	{
		return new ServerSimpleInfoData();
	}
	
	private BaseData createPlayerWorkData()
	{
		return new PlayerWorkData();
	}
	
	private BaseData createItemIdentityData()
	{
		return new ItemIdentityData();
	}
	
	private BaseData createItemData()
	{
		return new ItemData();
	}
	
	private BaseData createPlayerMailData()
	{
		return new PlayerMailData();
	}
	
	private BaseData createQuestData()
	{
		return new QuestData();
	}
	
	private BaseData createAchievementSaveData()
	{
		return new AchievementSaveData();
	}
	
	private BaseData createAchievementData()
	{
		return new AchievementData();
	}
	
	private BaseData createDamageOneData()
	{
		return new DamageOneData();
	}
	
	private BaseData createNPCIdentityData()
	{
		return new NPCIdentityData();
	}
	
	private BaseData createVehicleIdentityData()
	{
		return new VehicleIdentityData();
	}
	
	private BaseData createUnitFightData()
	{
		return new UnitFightData();
	}
	
	private BaseData createUnitData()
	{
		return new UnitData();
	}
	
	private BaseData createUnitAvatarData()
	{
		return new UnitAvatarData();
	}
	
	private BaseData createSceneEnterData()
	{
		return new SceneEnterData();
	}
	
	private BaseData createScenePreInfoData()
	{
		return new ScenePreInfoData();
	}
	
	private BaseData createDirData()
	{
		return new DirData();
	}
	
	private BaseData createPosDirData()
	{
		return new PosDirData();
	}
	
	private BaseData createClientLoginExData()
	{
		return new ClientLoginExData();
	}
	
	private BaseData createClientLoginData()
	{
		return new ClientLoginData();
	}
	
	private BaseData createPlayerLoginData()
	{
		return new PlayerLoginData();
	}
	
	private BaseData createClientOfflineWorkData()
	{
		return new ClientOfflineWorkData();
	}
	
	private BaseData createClientOfflineWorkListData()
	{
		return new ClientOfflineWorkListData();
	}
	
	private BaseData createClientPlayerLocalCacheData()
	{
		return new ClientPlayerLocalCacheData();
	}
	
	private BaseData createWorkSenderData()
	{
		return new WorkSenderData();
	}
	
	private BaseData createWorkReceiverData()
	{
		return new WorkReceiverData();
	}
	
	private BaseData createQueryPlayerLevelWData()
	{
		return new QueryPlayerLevelWData();
	}
	
	private BaseData createGMAddCurrencyOWData()
	{
		return new GMAddCurrencyOWData();
	}
	
	private BaseData createIntQueryResultData()
	{
		return new IntQueryResultData();
	}
	
	private BaseData createItemDicContainerData()
	{
		return new ItemDicContainerData();
	}
	
	private BaseData createRoleSocialPoolData()
	{
		return new RoleSocialPoolData();
	}
	
	private BaseData createRoleSocialPoolToolData()
	{
		return new RoleSocialPoolToolData();
	}
	
	private BaseData createQueryPlayerAWData()
	{
		return new QueryPlayerAWData();
	}
	
	private BaseData createQueryPlayerResultData()
	{
		return new QueryPlayerResultData();
	}
	
	private BaseData createReQueryPlayerOWData()
	{
		return new ReQueryPlayerOWData();
	}
	
	private BaseData createBigFloatData()
	{
		return new BigFloatData();
	}
	
	private BaseData createOperationIdentityData()
	{
		return new OperationIdentityData();
	}
	
	private BaseData createFieldItemIdentityData()
	{
		return new FieldItemIdentityData();
	}
	
	private BaseData createFightUnitIdentityData()
	{
		return new FightUnitIdentityData();
	}
	
	private BaseData createEquipContainerData()
	{
		return new EquipContainerData();
	}
	
	private BaseData createMUnitSaveData()
	{
		return new MUnitSaveData();
	}
	
	private BaseData createPetSaveData()
	{
		return new PetSaveData();
	}
	
	private BaseData createRoleAttributeData()
	{
		return new RoleAttributeData();
	}
	
	private BaseData createSceneRoleData()
	{
		return new SceneRoleData();
	}
	
	private BaseData createBuildingIdentityData()
	{
		return new BuildingIdentityData();
	}
	
	private BaseData createRoleForceData()
	{
		return new RoleForceData();
	}
	
	private BaseData createClientLoginServerInfoData()
	{
		return new ClientLoginServerInfoData();
	}
	
	private BaseData createPlayerRoleGroupToolData()
	{
		return new PlayerRoleGroupToolData();
	}
	
	private BaseData createRoleGroupData()
	{
		return new RoleGroupData();
	}
	
	private BaseData createRoleGroupMemberData()
	{
		return new RoleGroupMemberData();
	}
	
	private BaseData createRoleGroupToolData()
	{
		return new RoleGroupToolData();
	}
	
	private BaseData createClientLoginResultData()
	{
		return new ClientLoginResultData();
	}
	
	private BaseData createAreaServerData()
	{
		return new AreaServerData();
	}
	
	private BaseData createPlayerBindPlatformAWData()
	{
		return new PlayerBindPlatformAWData();
	}
	
	private BaseData createPlayerLoginToEachGameData()
	{
		return new PlayerLoginToEachGameData();
	}
	
	private BaseData createRePlayerLoginFromEachGameData()
	{
		return new RePlayerLoginFromEachGameData();
	}
	
	private BaseData createPlayerWorkCompleteData()
	{
		return new PlayerWorkCompleteData();
	}
	
	private BaseData createWorkCompleteData()
	{
		return new WorkCompleteData();
	}
	
	private BaseData createAreaGlobalWorkData()
	{
		return new AreaGlobalWorkData();
	}
	
	private BaseData createGameGlobalWorkData()
	{
		return new GameGlobalWorkData();
	}
	
	private BaseData createAreaGlobalWorkCompleteData()
	{
		return new AreaGlobalWorkCompleteData();
	}
	
	private BaseData createCreateRoleGroupData()
	{
		return new CreateRoleGroupData();
	}
	
	private BaseData createPlayerRoleGroupSaveData()
	{
		return new PlayerRoleGroupSaveData();
	}
	
	private BaseData createPlayerRoleGroupData()
	{
		return new PlayerRoleGroupData();
	}
	
	private BaseData createPlayerApplyRoleGroupData()
	{
		return new PlayerApplyRoleGroupData();
	}
	
	private BaseData createRoleGroupSimpleData()
	{
		return new RoleGroupSimpleData();
	}
	
	private BaseData createCreateRoleGroupResultOWData()
	{
		return new CreateRoleGroupResultOWData();
	}
	
	private BaseData createCreateRoleGroupWData()
	{
		return new CreateRoleGroupWData();
	}
	
	private BaseData createInviteRoleGroupWData()
	{
		return new InviteRoleGroupWData();
	}
	
	private BaseData createPlayerFuncWorkData()
	{
		return new PlayerFuncWorkData();
	}
	
	private BaseData createPlayerApplyRoleGroupSelfData()
	{
		return new PlayerApplyRoleGroupSelfData();
	}
	
	private BaseData createAddApplyRoleGroupSelfWData()
	{
		return new AddApplyRoleGroupSelfWData();
	}
	
	private BaseData createHandleApplyRoleGroupWData()
	{
		return new HandleApplyRoleGroupWData();
	}
	
	private BaseData createRoleGroupChangeData()
	{
		return new RoleGroupChangeData();
	}
	
	private BaseData createPlayerRoleGroupClientToolData()
	{
		return new PlayerRoleGroupClientToolData();
	}
	
	private BaseData createPlayerUnionData()
	{
		return new PlayerUnionData();
	}
	
	private BaseData createUnionData()
	{
		return new UnionData();
	}
	
	private BaseData createUnionMemberData()
	{
		return new UnionMemberData();
	}
	
	private BaseData createCreateUnionData()
	{
		return new CreateUnionData();
	}
	
	private BaseData createUnionSimpleData()
	{
		return new UnionSimpleData();
	}
	
	private BaseData createPlayerUnionSaveData()
	{
		return new PlayerUnionSaveData();
	}
	
	private BaseData createJoinRoleGroupWData()
	{
		return new JoinRoleGroupWData();
	}
	
	private BaseData createLeaveRoleGroupWData()
	{
		return new LeaveRoleGroupWData();
	}
	
	private BaseData createUseActivationCodeSuccessOWData()
	{
		return new UseActivationCodeSuccessOWData();
	}
	
	private BaseData createTeamData()
	{
		return new TeamData();
	}
	
	private BaseData createCreateTeamData()
	{
		return new CreateTeamData();
	}
	
	private BaseData createPlayerTeamData()
	{
		return new PlayerTeamData();
	}
	
	private BaseData createTeamMemberData()
	{
		return new TeamMemberData();
	}
	
	private BaseData createTeamSimpleData()
	{
		return new TeamSimpleData();
	}
	
	private BaseData createPlayerCreatedWData()
	{
		return new PlayerCreatedWData();
	}
	
	private BaseData createUserWorkData()
	{
		return new UserWorkData();
	}
	
	private BaseData createPlayerDeletedWData()
	{
		return new PlayerDeletedWData();
	}
	
	private BaseData createInfoLogData()
	{
		return new InfoLogData();
	}
	
	private BaseData createRoleShowLogData()
	{
		return new RoleShowLogData();
	}
	
	private BaseData createInfoLogWData()
	{
		return new InfoLogWData();
	}
	
	private BaseData createGameInitServerData()
	{
		return new GameInitServerData();
	}
	
	private BaseData createLoginInitServerData()
	{
		return new LoginInitServerData();
	}
	
	private BaseData createGameLoginToCenterData()
	{
		return new GameLoginToCenterData();
	}
	
	private BaseData createGameLoginToGameData()
	{
		return new GameLoginToGameData();
	}
	
	private BaseData createPlayerToPlayerTCCWData()
	{
		return new PlayerToPlayerTCCWData();
	}
	
	private BaseData createPlayerToRoleGroupTCCWData()
	{
		return new PlayerToRoleGroupTCCWData();
	}
	
	private BaseData createRoleGroupWorkData()
	{
		return new RoleGroupWorkData();
	}
	
	private BaseData createPlayerToRoleGroupTCCResultWData()
	{
		return new PlayerToRoleGroupTCCResultWData();
	}
	
	private BaseData createPlayerToPlayerTCCResultWData()
	{
		return new PlayerToPlayerTCCResultWData();
	}
	
	private BaseData createRoleShowInfoLogData()
	{
		return new RoleShowInfoLogData();
	}
	
	private BaseData createCreateSceneData()
	{
		return new CreateSceneData();
	}
	
	private BaseData createRoleGroupCreateSceneData()
	{
		return new RoleGroupCreateSceneData();
	}
	
	private BaseData createPlayerRoleGroupMemberData()
	{
		return new PlayerRoleGroupMemberData();
	}
	
	private BaseData createRoleGroupMemberChangeData()
	{
		return new RoleGroupMemberChangeData();
	}
	
	private BaseData createPlayerRoleGroupExData()
	{
		return new PlayerRoleGroupExData();
	}
	
	private BaseData createUnitFuncData()
	{
		return new UnitFuncData();
	}
	
	private BaseData createUnitMoveData()
	{
		return new UnitMoveData();
	}
	
	private BaseData createFieldItemBagIdentityData()
	{
		return new FieldItemBagIdentityData();
	}
	
	private BaseData createUnitNormalData()
	{
		return new UnitNormalData();
	}
	
	private BaseData createFieldItemBagBindData()
	{
		return new FieldItemBagBindData();
	}
	
	private BaseData createRectData()
	{
		return new RectData();
	}
	
	private BaseData createItemChatElementData()
	{
		return new ItemChatElementData();
	}
	
	private BaseData createChatChannelData()
	{
		return new ChatChannelData();
	}
	
	private BaseData createRoleChatData()
	{
		return new RoleChatData();
	}
	
	private BaseData createRoleSimpleShowData()
	{
		return new RoleSimpleShowData();
	}
	
	private BaseData createSendPlayerChatOWData()
	{
		return new SendPlayerChatOWData();
	}
	
	private BaseData createSceneLocationData()
	{
		return new SceneLocationData();
	}
	
	private BaseData createSceneLocationRoleShowChangeData()
	{
		return new SceneLocationRoleShowChangeData();
	}
	
	private BaseData createRegionData()
	{
		return new RegionData();
	}
	
	private BaseData createUnitSimpleData()
	{
		return new UnitSimpleData();
	}
	
	private BaseData createCenterRoleGroupToolData()
	{
		return new CenterRoleGroupToolData();
	}
	
	private BaseData createCreateRoleGroupToCenterWData()
	{
		return new CreateRoleGroupToCenterWData();
	}
	
	private BaseData createCenterGlobalWorkData()
	{
		return new CenterGlobalWorkData();
	}
	
	private BaseData createRemoveRoleGroupToCenterWData()
	{
		return new RemoveRoleGroupToCenterWData();
	}
	
	private BaseData createRoleGroupRankData()
	{
		return new RoleGroupRankData();
	}
	
	private BaseData createDriveData()
	{
		return new DriveData();
	}
	
	private BaseData createPlayerDeletedToCenterWData()
	{
		return new PlayerDeletedToCenterWData();
	}
	
	private BaseData createAuctionItemData()
	{
		return new AuctionItemData();
	}
	
	private BaseData createAuctionSoldLogData()
	{
		return new AuctionSoldLogData();
	}
	
	private BaseData createAuctionItemRecordData()
	{
		return new AuctionItemRecordData();
	}
	
	private BaseData createAuctionToolData()
	{
		return new AuctionToolData();
	}
	
	private BaseData createCountData()
	{
		return new CountData();
	}
	
	private BaseData createAuctionQueryConditionData()
	{
		return new AuctionQueryConditionData();
	}
	
	private BaseData createIntAuctionQueryConditionData()
	{
		return new IntAuctionQueryConditionData();
	}
	
	private BaseData createPlayerAuctionToolData()
	{
		return new PlayerAuctionToolData();
	}
	
	private BaseData createGameAuctionToolData()
	{
		return new GameAuctionToolData();
	}
	
	private BaseData createAuctionReSellItemOWData()
	{
		return new AuctionReSellItemOWData();
	}
	
	private BaseData createAuctionBuyItemData()
	{
		return new AuctionBuyItemData();
	}
	
	private BaseData createAuctionReBuyItemOWData()
	{
		return new AuctionReBuyItemOWData();
	}
	
	private BaseData createAuctionSoldItemOWData()
	{
		return new AuctionSoldItemOWData();
	}
	
	private BaseData createFuncInfoLogData()
	{
		return new FuncInfoLogData();
	}
	
	private BaseData createAuctionRemoveSellItemOWData()
	{
		return new AuctionRemoveSellItemOWData();
	}
	
	private BaseData createInviteRoleGroupReceiveData()
	{
		return new InviteRoleGroupReceiveData();
	}
	
	private BaseData createSubsectionRankToolData()
	{
		return new SubsectionRankToolData();
	}
	
	private BaseData createPlayerSubsectionRankToolData()
	{
		return new PlayerSubsectionRankToolData();
	}
	
	private BaseData createSubsectionRankSimpleData()
	{
		return new SubsectionRankSimpleData();
	}
	
	private BaseData createPetIdentityData()
	{
		return new PetIdentityData();
	}
	
	private BaseData createMUnitIdentityData()
	{
		return new MUnitIdentityData();
	}
	
	private BaseData createCenterInitServerData()
	{
		return new CenterInitServerData();
	}
	
	private BaseData createSceneInitServerData()
	{
		return new SceneInitServerData();
	}
	
	private BaseData createBattleSceneData()
	{
		return new BattleSceneData();
	}
	
	private BaseData createSceneServerEnterData()
	{
		return new SceneServerEnterData();
	}
	
	private BaseData createSceneServerExitData()
	{
		return new SceneServerExitData();
	}
	
	private BaseData createMatchSceneData()
	{
		return new MatchSceneData();
	}
	
	private BaseData createPlayerMatchSuccessWData()
	{
		return new PlayerMatchSuccessWData();
	}
	
}
