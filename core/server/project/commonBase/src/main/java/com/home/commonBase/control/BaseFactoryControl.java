package com.home.commonBase.control;

import com.home.commonBase.config.base.ConfigReadData;
import com.home.commonBase.data.item.EquipContainerData;
import com.home.commonBase.data.item.ItemData;
import com.home.commonBase.data.item.ItemEquipData;
import com.home.commonBase.data.item.ItemIdentityData;
import com.home.commonBase.data.item.UseItemArgData;
import com.home.commonBase.data.item.auction.AuctionItemData;
import com.home.commonBase.data.item.auction.PlayerAuctionToolData;
import com.home.commonBase.data.login.ClientLoginData;
import com.home.commonBase.data.login.GameLoginData;
import com.home.commonBase.data.login.GameLoginToCenterData;
import com.home.commonBase.data.login.GameLoginToGameData;
import com.home.commonBase.data.login.PlayerLoginData;
import com.home.commonBase.data.login.PlayerLoginToEachGameData;
import com.home.commonBase.data.login.PlayerSwitchGameData;
import com.home.commonBase.data.login.RePlayerLoginFromEachGameData;
import com.home.commonBase.data.mail.MailData;
import com.home.commonBase.data.quest.QuestData;
import com.home.commonBase.data.quest.TaskData;
import com.home.commonBase.data.role.CharacterSaveData;
import com.home.commonBase.data.role.CharacterUseData;
import com.home.commonBase.data.role.PetSaveData;
import com.home.commonBase.data.role.PetUseData;
import com.home.commonBase.data.role.RoleShowData;
import com.home.commonBase.data.role.RoleSimpleShowData;
import com.home.commonBase.data.scene.match.MatchSceneData;
import com.home.commonBase.data.scene.match.PlayerMatchData;
import com.home.commonBase.data.scene.role.SceneRoleData;
import com.home.commonBase.data.scene.scene.BattleSceneData;
import com.home.commonBase.data.scene.scene.CreateSceneData;
import com.home.commonBase.data.scene.scene.SceneEnterArgData;
import com.home.commonBase.data.scene.scene.SceneServerEnterData;
import com.home.commonBase.data.scene.unit.UnitData;
import com.home.commonBase.data.scene.unit.UnitSimpleData;
import com.home.commonBase.data.scene.unit.identity.BuildingIdentityData;
import com.home.commonBase.data.scene.unit.identity.CharacterIdentityData;
import com.home.commonBase.data.scene.unit.identity.FieldItemBagIdentityData;
import com.home.commonBase.data.scene.unit.identity.FieldItemIdentityData;
import com.home.commonBase.data.scene.unit.identity.MonsterIdentityData;
import com.home.commonBase.data.scene.unit.identity.NPCIdentityData;
import com.home.commonBase.data.scene.unit.identity.OperationIdentityData;
import com.home.commonBase.data.scene.unit.identity.PetIdentityData;
import com.home.commonBase.data.scene.unit.identity.PuppetIdentityData;
import com.home.commonBase.data.scene.unit.identity.VehicleIdentityData;
import com.home.commonBase.data.social.RoleSocialData;
import com.home.commonBase.data.social.friend.ContactData;
import com.home.commonBase.data.social.friend.FriendData;
import com.home.commonBase.data.social.rank.PlayerRankData;
import com.home.commonBase.data.social.rank.RoleGroupRankData;
import com.home.commonBase.logic.unit.BuffDataLogic;
import com.home.commonBase.logic.unit.CDDataLogic;
import com.home.commonBase.logic.unit.StatusDataLogic;
import com.home.commonBase.logic.unit.UnitFightDataLogic;
import com.home.commonBase.part.centerGlobal.list.CenterGlobalListData;
import com.home.commonBase.part.gameGlobal.list.GameGlobalListData;
import com.home.commonBase.part.player.list.PlayerListData;
import com.home.commonBase.scene.base.Bullet;
import com.home.commonBase.scene.base.Region;
import com.home.commonBase.scene.base.Role;
import com.home.commonBase.scene.base.Unit;
import com.home.commonBase.scene.path.GridSceneMap;
import com.home.commonBase.table.table.ActivationCodeTable;
import com.home.commonBase.table.table.AuctionItemTable;
import com.home.commonBase.table.table.GlobalTable;
import com.home.commonBase.table.table.PlayerNameTable;
import com.home.commonBase.table.table.PlayerTable;
import com.home.commonBase.table.table.RoleGroupTable;
import com.home.commonBase.table.table.RoleSocialTable;
import com.home.commonBase.table.table.ServerTable;
import com.home.commonBase.table.table.UnionNameTable;
import com.home.commonBase.table.table.UserTable;
import com.home.commonBase.table.table.WhiteListTable;
import com.home.commonBase.tool.BaseDataRegister;
import com.home.commonBase.trigger.SceneTriggerExecutor;

/** 基础工厂控制 */
public class BaseFactoryControl
{
	//--control组--//
	
	/** 创建baseData注册机 */
	public BaseDataRegister createBaseDataRegister()
	{
		return new BaseDataRegister();
	}
	
	/** 配置表控制 */
	public ConfigControl createConfigControl()
	{
		return new ConfigControl();
	}
	
	/** 逻辑控制 */
	public BaseLogicControl createLogicControl()
	{
		return new BaseLogicControl();
	}
	
	/** 常量方法控制 */
	public BaseConstControl createConstControl()
	{
		return new BaseConstControl();
	}
	
	/** 类控制 */
	public BaseClassControl createClassControl()
	{
		return new BaseClassControl();
	}
	
	/** trigger控制 */
	public TriggerControl createTriggerControl()
	{
		return new TriggerControl();
	}
	
	/** push控制 */
	public PushNotifyControl createPushNotificationControl()
	{
		return new PushNotifyControl();
	}
	
	/** 数据库升级控制 */
	public DBUpdateControl createDBUpdateControl()
	{
		return new DBUpdateControl();
	}
	
	//--table组--//
	
	/** 账号表 */
	public UserTable createUserTable()
	{
		return new UserTable();
	}
	
	/** 角色表 */
	public PlayerTable createPlayerTable()
	{
		return new PlayerTable();
	}
	
	/** 激活码表 */
	public ActivationCodeTable createActivationCodeTable()
	{
		return new ActivationCodeTable();
	}
	
	/** 逻辑服global表 */
	public GlobalTable createGameGlobalTable()
	{
		return new GlobalTable();
	}
	
	/** 中心服global表 */
	public GlobalTable createCenterGlobalTable()
	{
		return new GlobalTable();
	}
	
	/** 角色名字表 */
	public PlayerNameTable createPlayerNameTable()
	{
		return new PlayerNameTable();
	}
	
	/** 玩家群表 */
	public RoleGroupTable createRoleGroupTable()
	{
		return new RoleGroupTable();
	}
	
	/** 玩家社交数据表 */
	public RoleSocialTable createRoleSocialTable()
	{
		return new RoleSocialTable();
	}
	
	/** 服务器统计表 */
	public ServerTable createServerTable()
	{
		return new ServerTable();
	}
	
	/** 工会名字表 */
	public UnionNameTable createUnionNameTable()
	{
		return new UnionNameTable();
	}
	
	/** 白名单表 */
	public WhiteListTable createWhiteListTable()
	{
		return new WhiteListTable();
	}
	
	/** 拍卖行物品表 */
	public AuctionItemTable createAuctionItemTable()
	{
		return new AuctionItemTable();
	}
	
	//--数据组--//
	
	/** configReadData */
	public ConfigReadData createConfigReadData()
	{
		return new ConfigReadData();
	}
	
	/** 创建角色列表数据 */
	public PlayerListData createPlayerListData()
	{
		return new PlayerListData();
	}
	
	/** 创建中心服全局列表数据 */
	public CenterGlobalListData createCenterGlobalListData()
	{
		return new CenterGlobalListData();
	}
	
	/** 创建逻辑服全局列表数据 */
	public GameGlobalListData createGameGlobalListData()
	{
		return new GameGlobalListData();
	}
	
	/** 角色登录到其他逻辑服的数据 */
	public PlayerLoginToEachGameData createPlayerLoginToEachGameData()
	{
		return new PlayerLoginToEachGameData();
	}
	
	/** 角色登录到其他逻辑服的返回数据 */
	public RePlayerLoginFromEachGameData createRePlayerLoginFromEachGameData()
	{
		return new RePlayerLoginFromEachGameData();
	}
	
	/** 创建角色切换数据 */
	public PlayerSwitchGameData createPlayerSwitchGameData()
	{
		return new PlayerSwitchGameData();
	}
	
	public ClientLoginData createClientLoginData()
	{
		return new ClientLoginData();
	}
	
	/** 单位数据 */
	public UnitData createUnitData()
	{
		return new UnitData();
	}
	
	/** 单位简版数据 */
	public UnitSimpleData createUnitSimpleData()
	{
		return new UnitSimpleData();
	}
	
	/** 怪物身份数据 */
	public MonsterIdentityData createMonsterIdentityData()
	{
		return new MonsterIdentityData();
	}
	
	/** npc身份数据 */
	public NPCIdentityData createNPCIdentityData()
	{
		return new NPCIdentityData();
	}
	
	/** 傀儡身份数据 */
	public PuppetIdentityData createPuppetIdentityData()
	{
		return new PuppetIdentityData();
	}
	
	/** 掉落物品身份数据 */
	public FieldItemIdentityData createFieldItemIdentityData()
	{
		return new FieldItemIdentityData();
	}
	
	/** 掉落物品包身份数据 */
	public FieldItemBagIdentityData createFieldItemBagIdentityData()
	{
		return new FieldItemBagIdentityData();
	}
	
	/** 建筑身份数据 */
	public BuildingIdentityData createBuildingIdentityData()
	{
		return new BuildingIdentityData();
	}
	
	/** 操作体身份数据 */
	public OperationIdentityData createOperationIdentityData()
	{
		return new OperationIdentityData();
	}
	
	/** 载具身份数据 */
	public VehicleIdentityData createVehicleIdentityData()
	{
		return new VehicleIdentityData();
	}
	
	/** 场景角色数据 */
	public SceneRoleData createSceneRoleData()
	{
		return new SceneRoleData();
	}
	
	/** 角色展示数据 */
	public RoleShowData createRoleShowData()
	{
		return new RoleShowData();
	}
	
	/** 角色简版展示数据 */
	public RoleSimpleShowData createRoleSimpleShowData()
	{
		return new RoleSimpleShowData();
	}
	
	/** 角色社交数据 */
	public RoleSocialData createRoleSocialData()
	{
		return new RoleSocialData();
	}
	
	/** 角色排行数据 */
	public PlayerRankData createPlayerRankData()
	{
		return new PlayerRankData();
	}
	
	/** 角色匹配数据 */
	public PlayerMatchData createPlayerMatchData()
	{
		return new PlayerMatchData();
	}
	
	/** 玩家群排行数据 */
	public RoleGroupRankData createRoleGroupRankData()
	{
		return new RoleGroupRankData();
	}
	
	/** 创建角色身份数据 */
	public CharacterIdentityData createCharacterIdentityData()
	{
		return new CharacterIdentityData();
	}
	
	/** 创建角色使用数据 */
	public CharacterUseData createCharacterUseData()
	{
		return new CharacterUseData();
	}
	
	/** 创建角色保存数据 */
	public CharacterSaveData createCharacterSaveData()
	{
		return new CharacterSaveData();
	}
	
	/** 创建宠物身份数据 */
	public PetIdentityData createPetIdentityData()
	{
		return new PetIdentityData();
	}
	
	/** 创建宠物使用数据 */
	public PetUseData createPetUseData()
	{
		return new PetUseData();
	}
	
	/** 创建宠物使用数据 */
	public PetSaveData createPetSaveData()
	{
		return new PetSaveData();
	}
	
	/** 创建物品数据 */
	public ItemData createItemData()
	{
		return new ItemData();
	}
	
	/** 创建物品身份数据 */
	public ItemIdentityData createItemIdentityData()
	{
		return new ItemIdentityData();
	}
	
	/** 创建装备身份数据 */
	public ItemEquipData createEquipData()
	{
		return new ItemEquipData();
	}
	
	/** 创建邮件数据 */
	public MailData createMailData()
	{
		return new MailData();
	}
	
	/** 创建任务数据 */
	public QuestData createQuestData()
	{
		return new QuestData();
	}
	
	/** 创建联系人数据 */
	public ContactData createContactData()
	{
		return new ContactData();
	}
	
	/** 创建好友数据 */
	public FriendData createFriendData()
	{
		return new FriendData();
	}

	/** 创建任务数据 */
	public TaskData createTaskData()
	{
		return new TaskData();
	}

	/** 创建使用物品参数数据 */
	public UseItemArgData createUseItemArgData()
	{
		return new UseItemArgData();
	}
	
	/** 创建角色登录数据 */
	public PlayerLoginData createPlayerLoginData()
	{
		return new PlayerLoginData();
	}
	
	/** 创建游戏服登录中心服返回数据 */
	public GameLoginData createGameLoginData()
	{
		return new GameLoginData();
	}
	
	/** 创建游戏服登录到中心服返回数据 */
	public GameLoginToCenterData createGameLoginToCenterData()
	{
		return new GameLoginToCenterData();
	}
	
	/** 创建游戏服登录到逻辑服返回数据 */
	public GameLoginToGameData createGameLoginToGameData()
	{
		return new GameLoginToGameData();
	}
	
	/** 创建创建场景数据 */
	public CreateSceneData createCreateSceneData()
	{
		return new CreateSceneData();
	}
	
	/** 创建匹配后场景数据 */
	public MatchSceneData createMatchSceneData()
	{
		return new MatchSceneData();
	}
	
	/** 创建装备容器数据 */
	public EquipContainerData createEquipContainerData()
	{
		return new EquipContainerData();
	}
	
	/** 创建拍卖行玩家数据 */
	public PlayerAuctionToolData createPlayerAuctionToolData()
	{
		return new PlayerAuctionToolData();
	}
	
	/** 创建拍卖行物品数据 */
	public AuctionItemData createAuctionItemData()
	{
		return new AuctionItemData();
	}
	
	public SceneEnterArgData createSceneEnterArgData()
	{
		return new SceneEnterArgData();
	}
	
	public BattleSceneData createBattleSceneData()
	{
		return new BattleSceneData();
	}
	
	public SceneServerEnterData createSceneServerEnterData()
	{
		return new SceneServerEnterData();
	}
	
	//--逻辑组--//
	
	/** 战斗单位数据逻辑 */
	public UnitFightDataLogic createUnitFightDataLogic()
	{
		return new UnitFightDataLogic();
	}
	
	/** buff数据逻辑 */
	public BuffDataLogic createBuffDataLogic()
	{
		return new BuffDataLogic();
	}
	
	/** cd数据逻辑 */
	public CDDataLogic createCDDataLogic()
	{
		return new CDDataLogic();
	}
	
	/** 单位 */
	public Unit createUnit()
	{
		return new Unit();
	}
	
	/** 创建子弹 */
	public Bullet createBullet()
	{
		return new Bullet();
	}
	
	/** 创建场景玩家 */
	public Role createRole()
	{
		return new Role();
	}
	
	/** 创建场景触发器 */
	public SceneTriggerExecutor createSceneTriggerExecutor()
	{
		return new SceneTriggerExecutor();
	}
	
	/** 创建区域 */
	public Region createRegion()
	{
		return new Region();
	}
	
	/** 场景格子地图 */
	public GridSceneMap createGridSceneMap()
	{
		return new GridSceneMap();
	}
	
	/** 状态数据逻辑 */
	public StatusDataLogic createStatusDataLogic()
	{
		return new StatusDataLogic();
	}
}
