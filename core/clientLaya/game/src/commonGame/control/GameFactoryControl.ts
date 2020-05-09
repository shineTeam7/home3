namespace Shine
{
	/** 游戏工程工厂 */
	export class GameFactoryControl 
	{
		public createDataRegister():GameDataRegister
		{
			return new GameDataRegister();
		}

	    public createConfigControl():ConfigControl 
	    {
	        return new ConfigControl();
	    }

		public createBaseLogicControl():BaseLogicControl
		{
			return new BaseLogicControl();
		}
	
	    public createGameMainControl():GameMainControl 
	    {
	        return new GameMainControl();
	    }
	
	    public createGameServer(): GameServer 
	    {
	        return new GameServer();
	    }
	
	    public createPlayer(): Player 
	    {
	        return new Player();
	    }
	
	    public createGameUIControl(): GameUIControl 
	    {
	        return new GameUIControl();
	    }
	
	    public createInfoControl(): InfoControl 
	    {
	        return new InfoControl();
	    }
	
	    public createKeyboardControl(): KeyboardControl 
	    {
	        return new KeyboardControl();
	    }

	    public createSceneControl(): SceneControl 
	    {
	        return new SceneControl();
	    }
	
	    // public createGameOfflineControl(): GameOfflineControl 
	    // {
	    //     return new GameOfflineControl();
	    // }
	
	    // public createPlayerSaveControl(): PlayerSaveControl 
	    // {
	    //     return new PlayerSaveControl();
	    // }
	
	    // public createGameLogControl(): GameLogControl 
	    // {
	    //     return new GameLogControl();
	    // }
	
	    public createGamePoolControl(): GamePoolControl 
	    {
	        return new GamePoolControl();
	    }
	
	    public createConfigReadData(): ConfigReadData 
	    {
	        return new ConfigReadData();
	    }
	
	    public createGlobalReadData(): GlobalReadData 
	    {
	        return new GlobalReadData();
	    }
	
	    public createRoleShowData(): RoleShowData 
	    {
	        return new RoleShowData();
	    }
	
	    public createUnitData(): UnitData 
	    {
	        return new UnitData();
	    }
	
	    public createCharacterIdentityData(): CharacterIdentityData 
	    {
	        return new CharacterIdentityData();
	    }
	
	    public createItemData(): ItemData 
	    {
	        return new ItemData();
	    }
	
	    public createItemIdentityData(): ItemIdentityData 
	    {
	        return new ItemIdentityData();
	    }
	
	    public createCharacterUseData(): CharacterUseData
	     {
	        return new CharacterUseData();
	    }
	
	    public createClientLoginData(): ClientLoginData 
	    {
	        return new ClientLoginData();
	    }
	
	    public createRoleSocialData(): RoleSocialData 
	    {
	        return new RoleSocialData();
	    }
	
	    public createPlayerRankData(): PlayerRankData 
	    {
	        return new PlayerRankData();
	    }
	
	    public createPlayerMatchData(): PlayerMatchData 
	    {
	        return new PlayerMatchData();
	    }
	
	    public createItemEquipData(): ItemEquipData 
	    {
	        return new ItemEquipData();
	    }
	
	    public createMailData(): MailData 
	    {
	        return new MailData();
	    }
	
	    public createFriendData(): FriendData 
	    {
	        return new FriendData();
	    }
	
	
	    // public createUnitEffect(): UnitEffect 
	    // {
	    //     return new UnitEffect();
	    // }
	
	    // public createCharacterUseLogic(): CharacterUseLogic 
	    // {
	    //     return new CharacterUseLogic();
	    // }
	
	    // public createItemTipsReplaceTextTool(): ItemTipsReplaceTextTool
	    //  {
	    //     return new ItemTipsReplaceTextTool();
	    // }
	
	    // public createTaskDescribeReplaceTextTool(): TaskDescribeReplaceTextTool 
	    // {
	    //     return new TaskDescribeReplaceTextTool();
	    // }
	
	    public createAchievementCompleteData(): AchievementCompleteData 
	    {
	        return new AchievementCompleteData();
	    }
	
	    public createAchievementData(): AchievementData 
	    {
	        return new AchievementData();
	    }
	
	    public createAchievementPart(): AchievementPart 
	    {
	        return new AchievementPart();
	    }
	
	    public createAchievementSaveData(): AchievementSaveData 
	    {
	        return new AchievementSaveData();
	    }
	
	    public createActivityData(): ActivityData 
	    {
	        return new ActivityData();
	    }
	
	    public createActivityPart(): ActivityPart 
	    {
	        return new ActivityPart();
	    }
	
	    public createActivityServerData(): ActivityServerData 
	    {
	        return new ActivityServerData();
	    }
	
	    public createAddBeFriendOWData(): AddBeFriendOWData 
	    {
	        return new AddBeFriendOWData();
	    }
	
	    public createAddFriendFailedForOvonicAWData(): AddFriendFailedForOvonicAWData 
	    {
	        return new AddFriendFailedForOvonicAWData();
	    }
	
	    public createAddFriendForOvonicAWData(): AddFriendForOvonicAWData
	    {
	        return new AddFriendForOvonicAWData();
	    }
	
	    public createAddMailOWData(): AddMailOWData 
	    {
	        return new AddMailOWData();
	    }
	
	    public createApplyAddFriendData(): ApplyAddFriendData 
	    {
	        return new ApplyAddFriendData();
	    }
	
	    public createApplyAddFriendOWData(): ApplyAddFriendOWData 
	    {
	        return new ApplyAddFriendOWData();
	    }
	
	    public createAreaClientData(): AreaClientData 
	    {
	        return new AreaClientData();
	    }
	
	    public createBagPart(): BagPart 
	    {
	        return new BagPart();
	    }
	
	    public createBagUI(): BagUI 
	    {
	        return new BagUI();
	    }
	
	    public createBaseServer(): BaseServer 
	    {
	        return new BaseServer();
	    }
	
	    public createBattleSceneEnterData(): BattleSceneEnterData 
	    {
	        return new BattleSceneEnterData();
	    }
	
	
	    public createBuffData(): BuffData 
	    {
	        return new BuffData();
	    }
	
	    public createBulletData(): BulletData 
	    {
	        return new BulletData();
	    }
	
	
	    public createCDData(): CDData {
	        return new CDData();
	    }
	
	    // public createCharacterIdentityLogic(): CharacterIdentityLogic 
	    // {
	    //     return new CharacterIdentityLogic();
	    // }
	
	    public createCharacterPart(): CharacterPart 
	    {
	        return new CharacterPart();
	    }
	
	    public createCharacterSaveData(): CharacterSaveData 
	    {
	        return new CharacterSaveData();
	    }
	
	    public createChatData(): ChatData 
	    {
	        return new ChatData();
	    }
	
	    public createChatElementData(): ChatElementData 
	    {
	        return new ChatElementData();
	    }
	
	    public createClientLoginExData(): ClientLoginExData 
	    {
	        return new ClientLoginExData();
	    }
	
	    // public createClientSimpleScene(): ClientSimpleScene 
	    // {
	    //     return new ClientSimpleScene();
	    // }
	
	    public createContactData(): ContactData 
	    {
	        return new ContactData();
	    }
	
	    public createCreatePlayerData(): CreatePlayerData 
	    {
	        return new CreatePlayerData();
	    }
	
	    // public createCScene(): CScene {
	    //     return new CScene();
	    // }
	
	    public createDamageOneData(): DamageOneData 
	    {
	        return new DamageOneData();
	    }
	
	    public createDirData(): DirData 
	    {
	        return new DirData();
	    }
	
	    public createEquipPart(): EquipPart 
	    {
	        return new EquipPart();
	    }
	
	    public createFrameSyncCommandData(): FrameSyncCommandData 
	    {
	        return new FrameSyncCommandData();
	    }
	
	    public createFrameSyncData(): FrameSyncData 
	    {
	        return new FrameSyncData();
	    }
	
	    public createFriendPart(): FriendPart
	    {
	        return new FriendPart();
	    }
	
	    public createFuncPart(): FuncPart 
	    {
	        return new FuncPart();
	    }
	
	    public createFuncToolData(): FuncToolData 
	    {
	        return new FuncToolData();
	    }
	
	    // public createGameApp(): GameApp 
	    // {
	    //     return new GameApp();
	    // }
	
	    // public createGameDataRegister(): GameDataRegister
	    // {
	    //     return new GameDataRegister();
	    // }
	
	    public createGameFactoryControl(): GameFactoryControl 
	    {
	        return new GameFactoryControl();
	    }
	
	    public createGameGlobalWorkData(): GameGlobalWorkData 
	    {
	        return new GameGlobalWorkData();
	    }
	
	    public createGameLoginData(): GameLoginData 
	    {
	        return new GameLoginData();
	    }
	
	    public createGameServerClientSimpleData(): GameServerClientSimpleData
	    {
	        return new GameServerClientSimpleData();
	    }
	
	    public createGameServerInfoData(): GameServerInfoData
	     {
	        return new GameServerInfoData();
	    }
	
	    public createGameServerRunData(): GameServerRunData
	    {
	        return new GameServerRunData();
	    }
	
	    public createGameServerSimpleInfoData(): GameServerSimpleInfoData 
	    {
	        return new GameServerSimpleInfoData();
	    }
	
	   
	
	    public createItemContainerData(): ItemContainerData 
	    {
	        return new ItemContainerData();
	    }
	
	    // public createItemRecordData(): ItemRecordData 
	    // {
	    //     return new ItemRecordData();
	    // }
	
	    public createKeepSaveData(): KeepSaveData 
	    {
	        return new KeepSaveData();
	    }
	
	    public createKeyData(): KeyData
	    {
	        return new KeyData();
	    }
	
	    public createMailPart(): MailPart {
	        return new MailPart();
	    }
	
	    public createMonsterIdentityData(): MonsterIdentityData 
	    {
	        return new MonsterIdentityData();
	    }
	
	    // public createMonsterIdentityLogic(): MonsterIdentityLogic 
	    // {
	    //     return new MonsterIdentityLogic();
	    // }
	
	    // public createMUnitCacheData(): MUnitCacheData 
	    // {
	    //     return new MUnitCacheData();
	    // }
	
	    // public createMUnitFightDataLogic(): MUnitFightDataLogic 
	    // {
	    //     return new MUnitFightDataLogic();
	    // }
	
	    // public createMUnitUseData(): MUnitUseData 
	    // {
	    //     return new MUnitUseData();
	    // }
	
	    // public createMUnitUseLogic(): MUnitUseLogic 
	    // {
	    //     return new MUnitUseLogic();
	    // }
	
	    public createNPCIdentityData(): NPCIdentityData 
	    {
	        return new NPCIdentityData();
	    }
	
	    public createParterIdentityData(): ParterIdentityData 
	    {
	        return new ParterIdentityData();
	    }
	
	    public createPetUseData(): PetUseData 
	    {
	        return new PetUseData();
	    }
	
	    public createPlayerMailData(): PlayerMailData 
	    {
	        return new PlayerMailData();
	    }
	
	    public createPlayerPrimaryKeyData(): PlayerPrimaryKeyData 
	    {
	        return new PlayerPrimaryKeyData();
	    }
	
	    public createPlayerRankToolData(): PlayerRankToolData 
	    {
	        return new PlayerRankToolData();
	    }
	
	    public createPlayerSwitchGameData(): PlayerSwitchGameData 
	    {
	        return new PlayerSwitchGameData();
	    }
	
	    public createPlayerWorkData(): PlayerWorkData 
	    {
	        return new PlayerWorkData();
	    }
	
	    public createPlayerWorkListData(): PlayerWorkListData 
	    {
	        return new PlayerWorkListData();
	    }
	
	    public createPosData(): PosData 
	    {
	        return new PosData();
	    }
	
	    public createPosDirData(): PosDirData 
	    {
	        return new PosDirData();
	    }
	
	    // public createPreBattleScenePlayLogic(): PreBattleScenePlayLogic 
	    // {
	    //     return new PreBattleScenePlayLogic();
	    // }
	
	    // public createPuppetIdentityData(): PuppetIdentityData 
	    // {
	    //     return new PuppetIdentityData();
	    // }
	
	    // public createPuppetIdentityLogic(): PuppetIdentityLogic 
	    // {
	    //     return new PuppetIdentityLogic();
	    // }
	
	    public createQuestCompleteData(): QuestCompleteData 
	    {
	        return new QuestCompleteData();
	    }
	
	    public createQuestData(): QuestData 
	    {
	        return new QuestData();
	    }
	
	    public createQuestPart(): QuestPart 
	    {
	        return new QuestPart();
	    }
	
	    public createRankData(): RankData 
	    {
	        return new RankData();
	    }
	
	    public createRankSimpleData(): RankSimpleData 
	    {
	        return new RankSimpleData();
	    }
	
	    public createRankToolData(): RankToolData 
	    {
	        return new RankToolData();
	    }
	
	    public createRefreshPartRoleShowDataWData(): RefreshPartRoleShowDataWData 
	    {
	        return new RefreshPartRoleShowDataWData();
	    }
	
	    public createReGetRoleSocialDataWData(): ReGetRoleSocialDataWData 
	    {
	        return new ReGetRoleSocialDataWData();
	    }
	
	    public createRemoveBeFriendOWData(): RemoveBeFriendOWData 
	    {
	        return new RemoveBeFriendOWData();
	    }
	
	    public createRemoveFriendOWData(): RemoveFriendOWData 
	    {
	        return new RemoveFriendOWData();
	    }
	
	    // public createReplaceTextTool(): ReplaceTextTool 
	    // {
	    //     return new ReplaceTextTool();
	    // }
	
	    // public createRewardShowData(): RewardShowData 
	    // {
	    //     return new RewardShowData();
	    // }
	
	    public createRolePart(): RolePart 
	    {
	        return new RolePart();
	    }
	
	    public createRoleShowChangeData(): RoleShowChangeData 
	    {
	        return new RoleShowChangeData();
	    }
	
	    // public createRoleShowWrapData(): RoleShowWrapData 
	    // {
	    //     return new RoleShowWrapData();
	    // }
	
	    // public createSaveVersionData(): SaveVersionData 
	    // {
	    //     return new SaveVersionData();
	    // }
	
	    // public createSceneCameraLogic(): SceneCameraLogic 
	    // {
	    //     return new SceneCameraLogic();
	    // }
	
	    // public createSceneCameraLogic3D(): SceneCameraLogic3D 
	    // {
	    //     return new SceneCameraLogic3D();
	    // }
	
	    public createSceneEffectIdentityData(): SceneEffectIdentityData 
	    {
	        return new SceneEffectIdentityData();
	    }
	
	    public createSceneEnterArgData(): SceneEnterArgData 
	    {
	        return new SceneEnterArgData();
	    }
	
	    public createSceneEnterData(): SceneEnterData 
	    {
	        return new SceneEnterData();
	    }
	
	    // public createSceneFightLogic(): SceneFightLogic 
	    // {
	    //     return new SceneFightLogic();
	    // }
	
	    // public createSceneInOutLogic(): SceneInOutLogic 
	    // {
	    //     return new SceneInOutLogic();
	    // }
	
	    // public createSceneLoadLogic(): SceneLoadLogic 
	    // {
	    //     return new SceneLoadLogic();
	    // }
	
	    // public createSceneObject(): SceneObject 
	    // {
	    //     return new SceneObject();
	    // }
	
	    // public createSceneObjectLogicBase(): SceneObjectLogicBase 
	    // {
	    //     return new SceneObjectLogicBase();
	    // }
	
	    // public createScenePart(): ScenePart 
	    // {
	    //     return new ScenePart();
	    // }
	
	    // public createScenePlaceConfig(): ScenePlaceConfig 
	    // {
	    //     return new ScenePlaceConfig();
	    // }
	
	    // public createScenePlayLogic(): ScenePlayLogic 
	    // {
	    //     return new ScenePlayLogic();
	    // }
	
	    // public createScenePosLogic(): ScenePosLogic 
	    // {
	    //     return new ScenePosLogic();
	    // }
	
	    // public createScenePosLogic3D(): ScenePosLogic3D 
	    // {
	    //     return new ScenePosLogic3D();
	    // }
	
	    // public createScenePreInfoData(): ScenePreInfoData 
	    // {
	    //     return new ScenePreInfoData();
	    // }
	
	    // public createSceneShowLogic(): SceneShowLogic 
	    // {
	    //     return new SceneShowLogic();
	    // }
	
	    // public createSceneShowLogic2D(): SceneShowLogic2D 
	    // {
	    //     return new SceneShowLogic2D();
	    // }
	
	    // public createSceneShowLogic3D(): SceneShowLogic3D 
	    // {
	    //     return new SceneShowLogic3D();
	    // }
	
	    // public createSceneUIShowLogic(): SceneUIShowLogic 
	    // {
	    //     return new SceneUIShowLogic();
	    // }
	
	    public createServerInfoData(): ServerInfoData 
	    {
	        return new ServerInfoData();
	    }
	
	    public createServerSimpleInfoData(): ServerSimpleInfoData 
	    {
	        return new ServerSimpleInfoData();
	    }
	
	    // public createSignedSceneInOutLogic(): SignedSceneInOutLogic 
	    // {
	    //     return new SignedSceneInOutLogic();
	    // }
	
	    public createSingleBagPart(): SingleBagPart 
	    {
	        return new SingleBagPart();
	    }
	
	    public createSingleCharacterPart(): SingleCharacterPart 
	    {
	        return new SingleCharacterPart();
	    }
	
	    public createSkillData(): SkillData
	    {
	        return new SkillData();
	    }
	
	    public createSkillTargetData(): SkillTargetData 
	    {
	        return new SkillTargetData();
	    }
	
	    public createSocialPart(): SocialPart 
	    {
	        return new SocialPart();
	    }
	
	    public createSystemPart(): SystemPart 
	    {
	        return new SystemPart();
	    }
	
	    public createTaskData(): TaskData 
	    {
	        return new TaskData();
	    }
	
	    public createTeamPart(): TeamPart 
	    {
	        return new TeamPart();
	    }
	
	    public createTest2Data(): Test2Data 
	    {
	        return new Test2Data();
	    }
	
	    public createTestData(): TestData 
	    {
	        return new TestData();
	    }
	
	
	    public createUnitFightData(): UnitFightData 
	    {
	        return new UnitFightData();
	    }
	
	    public createUnitFightExData(): UnitFightExData 
	    {
	        return new UnitFightExData();
	    }
	
	  
	
	    public createUseItemArgData(): UseItemArgData 
	    {
	        return new UseItemArgData();
	    }
	
	    public createVehicleIdentityData(): VehicleIdentityData 
	    {
	        return new VehicleIdentityData();
	    }
	
	    public createWorkData(): WorkData 
	    {
	        return new WorkData();
	    }
	
	    public createSEventRegister(): SEventRegister 
	    {
	        return new SEventRegister();
	    }
	
	    public createClientVersionData(): ClientVersionData 
	    {
	        return new ClientVersionData();
	    }
	
	    public createClientLoginCacheData(): ClientLoginCacheData 
	    {
	        return new ClientLoginCacheData();
	    }
	
	    public createClientOfflineWorkData(): ClientOfflineWorkData 
	    {
	        return new ClientOfflineWorkData();
	    }
	
	    public createClientOfflineWorkListData(): ClientOfflineWorkListData 
	    {
	        return new ClientOfflineWorkListData();
	    }
	
	    public createPlayerOfflineCacheExData(): PlayerOfflineCacheExData 
	    {
	        return new PlayerOfflineCacheExData();
	    }
	
	    public createClientPlayerLocalCacheData(): ClientPlayerLocalCacheData 
	    {
	        return new ClientPlayerLocalCacheData();
	    }
	}
}