package com.home.commonBase.config.base;
import com.home.commonBase.config.game.AchievementConfig;
import com.home.commonBase.config.game.ActivationCodeConfig;
import com.home.commonBase.config.game.ActivityConfig;
import com.home.commonBase.config.game.AreaInfoConfig;
import com.home.commonBase.config.game.AreaLoadConfig;
import com.home.commonBase.config.game.AttackConfig;
import com.home.commonBase.config.game.AttackGroupConfig;
import com.home.commonBase.config.game.AttackLevelConfig;
import com.home.commonBase.config.game.AttributeConfig;
import com.home.commonBase.config.game.AuctionConfig;
import com.home.commonBase.config.game.AvatarPartConfig;
import com.home.commonBase.config.game.BattleConfig;
import com.home.commonBase.config.game.BigFloatRankConfig;
import com.home.commonBase.config.game.BuffConfig;
import com.home.commonBase.config.game.BuffGroupConfig;
import com.home.commonBase.config.game.BuffLevelConfig;
import com.home.commonBase.config.game.BuildingConfig;
import com.home.commonBase.config.game.BuildingLevelConfig;
import com.home.commonBase.config.game.BulletConfig;
import com.home.commonBase.config.game.BulletLevelConfig;
import com.home.commonBase.config.game.CDConfig;
import com.home.commonBase.config.game.CDGroupConfig;
import com.home.commonBase.config.game.CallWayConfig;
import com.home.commonBase.config.game.CharacterConfig;
import com.home.commonBase.config.game.ChatChannelConfig;
import com.home.commonBase.config.game.CostConfig;
import com.home.commonBase.config.game.CountryCodeConfig;
import com.home.commonBase.config.game.CreateItemConfig;
import com.home.commonBase.config.game.CurrencyConfig;
import com.home.commonBase.config.game.ExchangeConfig;
import com.home.commonBase.config.game.ExchangeGroupConfig;
import com.home.commonBase.config.game.FacadeConfig;
import com.home.commonBase.config.game.FightUnitConfig;
import com.home.commonBase.config.game.FightUnitLevelConfig;
import com.home.commonBase.config.game.FlowStepConfig;
import com.home.commonBase.config.game.FunctionConfig;
import com.home.commonBase.config.game.InfoCodeConfig;
import com.home.commonBase.config.game.InfoLogConfig;
import com.home.commonBase.config.game.InitCreateConfig;
import com.home.commonBase.config.game.InternationalResourceConfig;
import com.home.commonBase.config.game.ItemConfig;
import com.home.commonBase.config.game.LanguageConfig;
import com.home.commonBase.config.game.MailConfig;
import com.home.commonBase.config.game.ModelConfig;
import com.home.commonBase.config.game.MonsterConfig;
import com.home.commonBase.config.game.MonsterLevelConfig;
import com.home.commonBase.config.game.NPCConfig;
import com.home.commonBase.config.game.OperationConfig;
import com.home.commonBase.config.game.PetConfig;
import com.home.commonBase.config.game.PuppetConfig;
import com.home.commonBase.config.game.PuppetLevelConfig;
import com.home.commonBase.config.game.PushNotifyConfig;
import com.home.commonBase.config.game.QuestConfig;
import com.home.commonBase.config.game.RandomItemConfig;
import com.home.commonBase.config.game.RandomItemListConfig;
import com.home.commonBase.config.game.RandomNameConfig;
import com.home.commonBase.config.game.RegionConfig;
import com.home.commonBase.config.game.RewardConfig;
import com.home.commonBase.config.game.RobotFlowStepConfig;
import com.home.commonBase.config.game.RobotTestModeConfig;
import com.home.commonBase.config.game.RoleAttributeConfig;
import com.home.commonBase.config.game.RoleGroupConfig;
import com.home.commonBase.config.game.RoleGroupLevelConfig;
import com.home.commonBase.config.game.RoleGroupTitleConfig;
import com.home.commonBase.config.game.RoleLevelConfig;
import com.home.commonBase.config.game.SceneConfig;
import com.home.commonBase.config.game.SceneEffectConfig;
import com.home.commonBase.config.game.SceneMapConfig;
import com.home.commonBase.config.game.ScenePlaceElementConfig;
import com.home.commonBase.config.game.SceneRoleAttributeConfig;
import com.home.commonBase.config.game.SensitiveWordConfig;
import com.home.commonBase.config.game.SkillBarConfig;
import com.home.commonBase.config.game.SkillConfig;
import com.home.commonBase.config.game.SkillGroupConfig;
import com.home.commonBase.config.game.SkillLevelConfig;
import com.home.commonBase.config.game.SkillProbConfig;
import com.home.commonBase.config.game.SkillStepConfig;
import com.home.commonBase.config.game.SkillStepLevelConfig;
import com.home.commonBase.config.game.SkillVarConfig;
import com.home.commonBase.config.game.SpecialMoveConfig;
import com.home.commonBase.config.game.StatusConfig;
import com.home.commonBase.config.game.SubsectionRankConfig;
import com.home.commonBase.config.game.TaskConfig;
import com.home.commonBase.config.game.TeamTargetConfig;
import com.home.commonBase.config.game.TextConfig;
import com.home.commonBase.config.game.VehicleConfig;
import com.home.commonBase.config.game.VocationConfig;
import com.home.commonBase.config.game.enumT.AuctionQueryConditionTypeConfig;
import com.home.commonBase.config.game.enumT.BuffActionTypeConfig;
import com.home.commonBase.config.game.enumT.ClientPlatformTypeConfig;
import com.home.commonBase.config.game.enumT.EquipSlotTypeConfig;
import com.home.commonBase.config.game.enumT.GMTypeConfig;
import com.home.commonBase.config.game.enumT.MailTypeConfig;
import com.home.commonBase.config.game.enumT.MapBlockTypeConfig;
import com.home.commonBase.config.game.enumT.MapMoveTypeConfig;
import com.home.commonBase.config.game.enumT.PushTopicTypeConfig;
import com.home.commonBase.config.game.enumT.RoleGroupChangeTypeConfig;
import com.home.commonBase.config.game.enumT.RoleGroupMemberChangeTypeConfig;
import com.home.commonBase.config.game.enumT.RoleShowDataPartTypeConfig;
import com.home.commonBase.config.game.enumT.SceneForceTypeConfig;
import com.home.commonBase.config.game.enumT.SceneInstanceTypeConfig;
import com.home.commonBase.config.game.enumT.SceneTypeConfig;
import com.home.commonBase.config.game.enumT.SkillInfluenceTypeConfig;
import com.home.commonBase.config.game.enumT.TaskTypeConfig;
import com.home.commonBase.config.game.enumT.UnitMoveTypeConfig;
import com.home.commonBase.config.game.enumT.UnitSpecialMoveTypeConfig;
import com.home.commonBase.config.other.GridMapInfoConfig;
import com.home.commonBase.config.other.MapInfoConfig;
import com.home.commonBase.config.other.ScenePlaceConfig;
import com.home.commonBase.constlist.generate.ConfigType;
import com.home.commonBase.constlist.scene.PathFindingType;
import com.home.commonBase.control.BaseLogicControl;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.global.Global;
import com.home.commonBase.global.GlobalReadData;
import com.home.commonBase.trigger.TriggerConfig;
import com.home.shine.bytes.BytesReadStream;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.data.trigger.TriggerConfigData;
import com.home.shine.support.collection.IntList;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.collection.LongObjectMap;
import com.home.shine.support.collection.SMap;

/** (generated by shine) */
public class ConfigReadData
{
	/** 本次类型组 */
	private IntList _list=new IntList();
	
	/** 机器人流程步骤类型字典 */
	public RobotFlowStepConfig[] robotFlowStepDic;
	
	/** CD表字典 */
	public IntObjectMap<CDConfig> cdDic;
	
	/** 活动表字典 */
	public IntObjectMap<ActivityConfig> activityDic;
	
	/** 调用方式表字典 */
	public IntObjectMap<CallWayConfig> callWayDic;
	
	/** buff表字典 */
	public IntObjectMap<BuffConfig> buffDic;
	
	/** 场景特效表字典 */
	public IntObjectMap<SceneEffectConfig> sceneEffectDic;
	
	/** 技能几率表字典 */
	public IntObjectMap<SkillProbConfig> skillProbDic;
	
	/** 特殊移动表字典 */
	public IntObjectMap<SpecialMoveConfig> specialMoveDic;
	
	/** 随机单个物品配置字典 */
	public IntObjectMap<RandomItemConfig> randomItemDic;
	
	/** 文本表(程序用)(需国际化)字典 */
	public SMap<String,TextConfig> textDic;
	
	/** 流程步骤类型字典 */
	public IntObjectMap<FlowStepConfig> flowStepDic;
	
	/** 角色表字典 */
	public IntObjectMap<CharacterConfig> characterDic;
	
	/** 怪物表字典 */
	public IntObjectMap<MonsterConfig> monsterDic;
	
	/** 状态类型表字典 */
	public StatusConfig[] statusDic;
	
	/** 成就表字典 */
	public IntObjectMap<AchievementConfig> achievementDic;
	
	/** 随机物品组配置字典 */
	public IntObjectMap<RandomItemListConfig> randomItemListDic;
	
	/** 技能等级表字典 */
	public LongObjectMap<SkillLevelConfig> skillLevelDic;
	
	/** 技能攻击等级表字典 */
	public LongObjectMap<AttackLevelConfig> attackLevelDic;
	
	/** 技能攻击表字典 */
	public IntObjectMap<AttackConfig> attackDic;
	
	/** 邮件表字典 */
	public IntObjectMap<MailConfig> mailDic;
	
	/** 副本基础表字典 */
	public IntObjectMap<BattleConfig> battleDic;
	
	/** 场景类型字典 */
	public SceneTypeConfig[] sceneTypeDic;
	
	/** 任务目标类型字典 */
	public TaskTypeConfig[] taskTypeDic;
	
	/** 单位移动方式字典 */
	public UnitMoveTypeConfig[] unitMoveTypeDic;
	
	/** 角色显示数据部件类型字典 */
	public RoleShowDataPartTypeConfig[] roleShowDataPartTypeDic;
	
	/** 邮件类型字典 */
	public MailTypeConfig[] mailTypeDic;
	
	/** 单位特殊移动类型字典 */
	public UnitSpecialMoveTypeConfig[] unitSpecialMoveTypeDic;
	
	/** 玩家等级表字典 */
	public IntObjectMap<RoleLevelConfig> roleLevelDic;
	
	/** 随机名字表字典 */
	public IntObjectMap<RandomNameConfig> randomNameDic;
	
	/** 技能变量表字典 */
	public IntObjectMap<SkillVarConfig> skillVarDic;
	
	/** 技能表字典 */
	public IntObjectMap<SkillConfig> skillDic;
	
	/** 属性类型表字典 */
	public AttributeConfig[] attributeDic;
	
	/** buff等级表字典 */
	public LongObjectMap<BuffLevelConfig> buffLevelDic;
	
	/** 任务目标表字典 */
	public IntObjectMap<TaskConfig> taskDic;
	
	/** cd组表字典 */
	public IntObjectMap<CDGroupConfig> cdGroupDic;
	
	/** 单位模型表字典 */
	public IntObjectMap<ModelConfig> modelDic;
	
	/** 技能步骤表字典 */
	public LongObjectMap<SkillStepConfig> skillStepDic;
	
	/** 机器人测试模式字典 */
	public RobotTestModeConfig[] robotTestModeDic;
	
	/** 技能组表字典 */
	public IntObjectMap<SkillGroupConfig> skillGroupDic;
	
	/** 初始化创建表字典 */
	public IntObjectMap<InitCreateConfig> initCreateDic;
	
	/** 攻击组表字典 */
	public IntObjectMap<AttackGroupConfig> attackGroupDic;
	
	/** buff组表字典 */
	public IntObjectMap<BuffGroupConfig> buffGroupDic;
	
	/** 任务表字典 */
	public IntObjectMap<QuestConfig> questDic;
	
	/** bullet表字典 */
	public IntObjectMap<BulletConfig> bulletDic;
	
	/** NPC表字典 */
	public IntObjectMap<NPCConfig> npcDic;
	
	/** 货币表字典 */
	public CurrencyConfig[] currencyDic;
	
	/** 技能步骤等级表字典 */
	public LongObjectMap<SkillStepLevelConfig> skillStepLevelDic;
	
	/** 傀儡表字典 */
	public IntObjectMap<PuppetConfig> puppetDic;
	
	/** 傀儡等级表字典 */
	public LongObjectMap<PuppetLevelConfig> puppetLevelDic;
	
	/** 单位显示部件表字典 */
	public LongObjectMap<AvatarPartConfig> avatarPartDic;
	
	/** 国际化表字典 */
	public SMap<String,LanguageConfig> languageDic;
	
	/** 单位外观表字典 */
	public IntObjectMap<FacadeConfig> facadeDic;
	
	/** 宠物表字典 */
	public IntObjectMap<PetConfig> petDic;
	
	/** 道具表字典 */
	public IntObjectMap<ItemConfig> itemDic;
	
	/** 子弹等级表字典 */
	public LongObjectMap<BulletLevelConfig> bulletLevelDic;
	
	/** 战斗单位表字典 */
	public IntObjectMap<FightUnitConfig> fightUnitDic;
	
	/** 信息码类型表字典 */
	public IntObjectMap<InfoCodeConfig> infoCodeDic;
	
	/** 战斗单位等级表字典 */
	public LongObjectMap<FightUnitLevelConfig> fightUnitLevelDic;
	
	/** 国际化资源表字典 */
	public SMap<String,InternationalResourceConfig> internationalResourceDic;
	
	/** 场景布置元素表(编辑器对应)字典 */
	public LongObjectMap<ScenePlaceElementConfig> scenePlaceElementDic;
	
	/** 创建单个物品配置(不包含数量)字典 */
	public IntObjectMap<CreateItemConfig> createItemDic;
	
	/** 场景表字典 */
	public IntObjectMap<SceneConfig> sceneDic;
	
	/** 功能表字典 */
	public IntObjectMap<FunctionConfig> functionDic;
	
	/** 奖励表字典 */
	public IntObjectMap<RewardConfig> rewardDic;
	
	/** 全局配置表 */
	public GlobalReadData global;
	
	/** trigger表 */
	public IntObjectMap<TriggerConfigData> triggerDic;
	
	/** 场景摆放配置(editor部分) */
	public IntObjectMap<ScenePlaceConfig> scenePlaceEditorDic;
	
	/** 地图信息配置(editor部分) */
	public IntObjectMap<MapInfoConfig> mapInfoDic;
	
	/** 敏感词表字典 */
	public IntObjectMap<SensitiveWordConfig> sensitiveWordDic;
	
	/** 国家地区表(登录用)字典 */
	public IntObjectMap<CountryCodeConfig> countryCodeDic;
	
	/** gm类型字典 */
	public GMTypeConfig[] gMTypeDic;
	
	/** 区服信息字典 */
	public IntObjectMap<AreaInfoConfig> areaInfoDic;
	
	/** 区服负载值字典 */
	public IntObjectMap<AreaLoadConfig> areaLoadDic;
	
	/** 消耗表字典 */
	public IntObjectMap<CostConfig> costDic;
	
	/** 兑换表字典 */
	public IntObjectMap<ExchangeConfig> exchangeDic;
	
	/** 大浮点数阶位字典 */
	public IntObjectMap<BigFloatRankConfig> bigFloatRankDic;
	
	/** 职业表字典 */
	public VocationConfig[] vocationDic;
	
	/** 场景地图表字典 */
	public IntObjectMap<SceneMapConfig> sceneMapDic;
	
	/** 推送表字典 */
	public PushNotifyConfig[] pushNotifyDic;
	
	/** 操作体表字典 */
	public IntObjectMap<OperationConfig> operationDic;
	
	/** 装备槽位类型字典 */
	public EquipSlotTypeConfig[] equipSlotTypeDic;
	
	/** 场景角色属性类型表字典 */
	public SceneRoleAttributeConfig[] sceneRoleAttributeDic;
	
	/** 技能影响类型字典 */
	public SkillInfluenceTypeConfig[] skillInfluenceTypeDic;
	
	/** 建筑等级表字典 */
	public LongObjectMap<BuildingLevelConfig> buildingLevelDic;
	
	/** 建筑表字典 */
	public IntObjectMap<BuildingConfig> buildingDic;
	
	/** 技能读条表字典 */
	public IntObjectMap<SkillBarConfig> skillBarDic;
	
	/** 场景实例类型字典 */
	public SceneInstanceTypeConfig[] sceneInstanceTypeDic;
	
	/** 玩家群表字典 */
	public IntObjectMap<RoleGroupConfig> roleGroupDic;
	
	/** 玩家群等级表字典 */
	public LongObjectMap<RoleGroupLevelConfig> roleGroupLevelDic;
	
	/** 玩家群职位表字典 */
	public RoleGroupTitleConfig[] roleGroupTitleDic;
	
	/** 激活码表字典 */
	public IntObjectMap<ActivationCodeConfig> activationCodeDic;
	
	/** 信息日志表字典 */
	public IntObjectMap<InfoLogConfig> infoLogDic;
	
	/** 玩家群数据变更类型字典 */
	public RoleGroupChangeTypeConfig[] roleGroupChangeTypeDic;
	
	/** 玩家群数据变更类型字典 */
	public RoleGroupMemberChangeTypeConfig[] roleGroupMemberChangeTypeDic;
	
	/** 兑换组表字典 */
	public IntObjectMap<ExchangeGroupConfig> exchangeGroupDic;
	
	/** 怪物等级表字典 */
	public LongObjectMap<MonsterLevelConfig> monsterLevelDic;
	
	/** 角色属性类型表字典 */
	public RoleAttributeConfig[] roleAttributeDic;
	
	/** 聊天频道表字典 */
	public ChatChannelConfig[] chatChannelDic;
	
	/** 区域表字典 */
	public IntObjectMap<RegionConfig> regionDic;
	
	/** 客户端平台类型字典 */
	public ClientPlatformTypeConfig[] clientPlatformTypeDic;
	
	/** 地图格子阻挡类型类型字典 */
	public MapBlockTypeConfig[] mapBlockTypeDic;
	
	/** buff行为类型字典 */
	public BuffActionTypeConfig[] buffActionTypeDic;
	
	/** 场景势力类型字典 */
	public SceneForceTypeConfig[] sceneForceTypeDic;
	
	/** 载具表字典 */
	public IntObjectMap<VehicleConfig> vehicleDic;
	
	/** 地图移动类型字典 */
	public MapMoveTypeConfig[] mapMoveTypeDic;
	
	/** 拍卖行表字典 */
	public IntObjectMap<AuctionConfig> auctionDic;
	
	/** 拍卖行查询条件类型字典 */
	public AuctionQueryConditionTypeConfig[] auctionQueryConditionTypeDic;
	
	/** 队伍目标表字典 */
	public TeamTargetConfig[] teamTargetDic;
	
	/** 分段排行表字典 */
	public IntObjectMap<SubsectionRankConfig> subsectionRankDic;
	
	/** 推送标签类型字典 */
	public PushTopicTypeConfig[] pushTopicTypeDic;
	
	/** 从流读取 */
	public void readBytes(BytesReadStream stream)
	{
		_list.clear();
		int len=stream.readLen();
		int type;
		
		for(int i=0;i<len;i++)
		{
			stream.clearBooleanPos();
			_list.add((type=stream.readShort()));
			readBytesOne(type,stream);
		}
	}
	
	/** 刷新数据 */
	public void refreshData()
	{
		int[] values=_list.getValues();
		
		for(int i=0, len=_list.size();i<len;++i)
		{
			refreshDataOne(values[i]);
		}
	}
	
	/** 设置值到Config上 */
	public void setToConfig()
	{
		int[] values=_list.getValues();
		
		for(int i=0, len=_list.size();i<len;++i)
		{
			setToConfigOne(values[i]);
		}
	}
	
	/** 读完所有配置 */
	public void afterReadConfigAll()
	{
		int[] values=_list.getValues();
		
		for(int i=0, len=_list.size();i<len;++i)
		{
			afterReadConfigAllOne(values[i]);
		}
	}
	
	/** 构造常量size */
	public void makeConstSize()
	{
		
	}
	
	/** 设置值到Config上 */
	public void setToConfigOne(int type)
	{
		switch(type)
		{
			case ConfigType.Global:
			{
				Global.readFromData(global);
				Global.afterReadConfig();
			}
				break;
			case ConfigType.Trigger:
			{
				setToConfigTrigger();
			}
				break;
			case ConfigType.ScenePlaceEditor:
			{
				setToConfigScenePlaceEditor();
			}
				break;
			case ConfigType.MapInfo:
			{
				setToConfigMapInfo();
			}
				break;
			case ConfigType.Language:
			{
				LanguageConfig.setDic(languageDic);
			}
				break;
			case ConfigType.BigFloatRank:
			{
				BigFloatRankConfig.setDic(bigFloatRankDic);
			}
				break;
			case ConfigType.SkillInfluenceType:
			{
				SkillInfluenceTypeConfig.setDic(skillInfluenceTypeDic);
			}
				break;
			case ConfigType.Achievement:
			{
				AchievementConfig.setDic(achievementDic);
			}
				break;
			case ConfigType.ActivationCode:
			{
				ActivationCodeConfig.setDic(activationCodeDic);
			}
				break;
			case ConfigType.Activity:
			{
				ActivityConfig.setDic(activityDic);
			}
				break;
			case ConfigType.AreaInfo:
			{
				AreaInfoConfig.setDic(areaInfoDic);
			}
				break;
			case ConfigType.AreaLoad:
			{
				AreaLoadConfig.setDic(areaLoadDic);
			}
				break;
			case ConfigType.Attack:
			{
				AttackConfig.setDic(attackDic);
			}
				break;
			case ConfigType.AttackGroup:
			{
				AttackGroupConfig.setDic(attackGroupDic);
			}
				break;
			case ConfigType.AttackLevel:
			{
				AttackLevelConfig.setDic(attackLevelDic);
			}
				break;
			case ConfigType.Attribute:
			{
				AttributeConfig.setDic(attributeDic);
			}
				break;
			case ConfigType.Auction:
			{
				AuctionConfig.setDic(auctionDic);
			}
				break;
			case ConfigType.AuctionQueryConditionType:
			{
				AuctionQueryConditionTypeConfig.setDic(auctionQueryConditionTypeDic);
			}
				break;
			case ConfigType.AvatarPart:
			{
				AvatarPartConfig.setDic(avatarPartDic);
			}
				break;
			case ConfigType.Battle:
			{
				BattleConfig.setDic(battleDic);
			}
				break;
			case ConfigType.Buff:
			{
				BuffConfig.setDic(buffDic);
			}
				break;
			case ConfigType.BuffActionType:
			{
				BuffActionTypeConfig.setDic(buffActionTypeDic);
			}
				break;
			case ConfigType.BuffGroup:
			{
				BuffGroupConfig.setDic(buffGroupDic);
			}
				break;
			case ConfigType.BuffLevel:
			{
				BuffLevelConfig.setDic(buffLevelDic);
			}
				break;
			case ConfigType.Building:
			{
				BuildingConfig.setDic(buildingDic);
			}
				break;
			case ConfigType.BuildingLevel:
			{
				BuildingLevelConfig.setDic(buildingLevelDic);
			}
				break;
			case ConfigType.Bullet:
			{
				BulletConfig.setDic(bulletDic);
			}
				break;
			case ConfigType.BulletLevel:
			{
				BulletLevelConfig.setDic(bulletLevelDic);
			}
				break;
			case ConfigType.CallWay:
			{
				CallWayConfig.setDic(callWayDic);
			}
				break;
			case ConfigType.CD:
			{
				CDConfig.setDic(cdDic);
			}
				break;
			case ConfigType.CDGroup:
			{
				CDGroupConfig.setDic(cdGroupDic);
			}
				break;
			case ConfigType.Character:
			{
				CharacterConfig.setDic(characterDic);
			}
				break;
			case ConfigType.ChatChannel:
			{
				ChatChannelConfig.setDic(chatChannelDic);
			}
				break;
			case ConfigType.ClientPlatformType:
			{
				ClientPlatformTypeConfig.setDic(clientPlatformTypeDic);
			}
				break;
			case ConfigType.Cost:
			{
				CostConfig.setDic(costDic);
			}
				break;
			case ConfigType.CountryCode:
			{
				CountryCodeConfig.setDic(countryCodeDic);
			}
				break;
			case ConfigType.CreateItem:
			{
				CreateItemConfig.setDic(createItemDic);
			}
				break;
			case ConfigType.Currency:
			{
				CurrencyConfig.setDic(currencyDic);
			}
				break;
			case ConfigType.EquipSlotType:
			{
				EquipSlotTypeConfig.setDic(equipSlotTypeDic);
			}
				break;
			case ConfigType.Exchange:
			{
				ExchangeConfig.setDic(exchangeDic);
			}
				break;
			case ConfigType.ExchangeGroup:
			{
				ExchangeGroupConfig.setDic(exchangeGroupDic);
			}
				break;
			case ConfigType.Facade:
			{
				FacadeConfig.setDic(facadeDic);
			}
				break;
			case ConfigType.FightUnit:
			{
				FightUnitConfig.setDic(fightUnitDic);
			}
				break;
			case ConfigType.FightUnitLevel:
			{
				FightUnitLevelConfig.setDic(fightUnitLevelDic);
			}
				break;
			case ConfigType.FlowStep:
			{
				FlowStepConfig.setDic(flowStepDic);
			}
				break;
			case ConfigType.Function:
			{
				FunctionConfig.setDic(functionDic);
			}
				break;
			case ConfigType.GMType:
			{
				GMTypeConfig.setDic(gMTypeDic);
			}
				break;
			case ConfigType.InfoCode:
			{
				InfoCodeConfig.setDic(infoCodeDic);
			}
				break;
			case ConfigType.InfoLog:
			{
				InfoLogConfig.setDic(infoLogDic);
			}
				break;
			case ConfigType.InitCreate:
			{
				InitCreateConfig.setDic(initCreateDic);
			}
				break;
			case ConfigType.InternationalResource:
			{
				InternationalResourceConfig.setDic(internationalResourceDic);
			}
				break;
			case ConfigType.Item:
			{
				ItemConfig.setDic(itemDic);
			}
				break;
			case ConfigType.Mail:
			{
				MailConfig.setDic(mailDic);
			}
				break;
			case ConfigType.MailType:
			{
				MailTypeConfig.setDic(mailTypeDic);
			}
				break;
			case ConfigType.MapBlockType:
			{
				MapBlockTypeConfig.setDic(mapBlockTypeDic);
			}
				break;
			case ConfigType.MapMoveType:
			{
				MapMoveTypeConfig.setDic(mapMoveTypeDic);
			}
				break;
			case ConfigType.Model:
			{
				ModelConfig.setDic(modelDic);
			}
				break;
			case ConfigType.Monster:
			{
				MonsterConfig.setDic(monsterDic);
			}
				break;
			case ConfigType.MonsterLevel:
			{
				MonsterLevelConfig.setDic(monsterLevelDic);
			}
				break;
			case ConfigType.NPC:
			{
				NPCConfig.setDic(npcDic);
			}
				break;
			case ConfigType.Operation:
			{
				OperationConfig.setDic(operationDic);
			}
				break;
			case ConfigType.Pet:
			{
				PetConfig.setDic(petDic);
			}
				break;
			case ConfigType.Puppet:
			{
				PuppetConfig.setDic(puppetDic);
			}
				break;
			case ConfigType.PuppetLevel:
			{
				PuppetLevelConfig.setDic(puppetLevelDic);
			}
				break;
			case ConfigType.PushNotify:
			{
				PushNotifyConfig.setDic(pushNotifyDic);
			}
				break;
			case ConfigType.PushTopicType:
			{
				PushTopicTypeConfig.setDic(pushTopicTypeDic);
			}
				break;
			case ConfigType.Quest:
			{
				QuestConfig.setDic(questDic);
			}
				break;
			case ConfigType.RandomItem:
			{
				RandomItemConfig.setDic(randomItemDic);
			}
				break;
			case ConfigType.RandomItemList:
			{
				RandomItemListConfig.setDic(randomItemListDic);
			}
				break;
			case ConfigType.RandomName:
			{
				RandomNameConfig.setDic(randomNameDic);
			}
				break;
			case ConfigType.Region:
			{
				RegionConfig.setDic(regionDic);
			}
				break;
			case ConfigType.Reward:
			{
				RewardConfig.setDic(rewardDic);
			}
				break;
			case ConfigType.RobotFlowStep:
			{
				RobotFlowStepConfig.setDic(robotFlowStepDic);
			}
				break;
			case ConfigType.RobotTestMode:
			{
				RobotTestModeConfig.setDic(robotTestModeDic);
			}
				break;
			case ConfigType.RoleAttribute:
			{
				RoleAttributeConfig.setDic(roleAttributeDic);
			}
				break;
			case ConfigType.RoleGroup:
			{
				RoleGroupConfig.setDic(roleGroupDic);
			}
				break;
			case ConfigType.RoleGroupChangeType:
			{
				RoleGroupChangeTypeConfig.setDic(roleGroupChangeTypeDic);
			}
				break;
			case ConfigType.RoleGroupLevel:
			{
				RoleGroupLevelConfig.setDic(roleGroupLevelDic);
			}
				break;
			case ConfigType.RoleGroupMemberChangeType:
			{
				RoleGroupMemberChangeTypeConfig.setDic(roleGroupMemberChangeTypeDic);
			}
				break;
			case ConfigType.RoleGroupTitle:
			{
				RoleGroupTitleConfig.setDic(roleGroupTitleDic);
			}
				break;
			case ConfigType.RoleLevel:
			{
				RoleLevelConfig.setDic(roleLevelDic);
			}
				break;
			case ConfigType.RoleShowDataPartType:
			{
				RoleShowDataPartTypeConfig.setDic(roleShowDataPartTypeDic);
			}
				break;
			case ConfigType.Scene:
			{
				SceneConfig.setDic(sceneDic);
			}
				break;
			case ConfigType.SceneEffect:
			{
				SceneEffectConfig.setDic(sceneEffectDic);
			}
				break;
			case ConfigType.SceneForceType:
			{
				SceneForceTypeConfig.setDic(sceneForceTypeDic);
			}
				break;
			case ConfigType.SceneInstanceType:
			{
				SceneInstanceTypeConfig.setDic(sceneInstanceTypeDic);
			}
				break;
			case ConfigType.SceneMap:
			{
				SceneMapConfig.setDic(sceneMapDic);
			}
				break;
			case ConfigType.ScenePlaceElement:
			{
				ScenePlaceElementConfig.setDic(scenePlaceElementDic);
			}
				break;
			case ConfigType.SceneRoleAttribute:
			{
				SceneRoleAttributeConfig.setDic(sceneRoleAttributeDic);
			}
				break;
			case ConfigType.SceneType:
			{
				SceneTypeConfig.setDic(sceneTypeDic);
			}
				break;
			case ConfigType.SensitiveWord:
			{
				SensitiveWordConfig.setDic(sensitiveWordDic);
			}
				break;
			case ConfigType.Skill:
			{
				SkillConfig.setDic(skillDic);
			}
				break;
			case ConfigType.SkillBar:
			{
				SkillBarConfig.setDic(skillBarDic);
			}
				break;
			case ConfigType.SkillGroup:
			{
				SkillGroupConfig.setDic(skillGroupDic);
			}
				break;
			case ConfigType.SkillLevel:
			{
				SkillLevelConfig.setDic(skillLevelDic);
			}
				break;
			case ConfigType.SkillProb:
			{
				SkillProbConfig.setDic(skillProbDic);
			}
				break;
			case ConfigType.SkillStep:
			{
				SkillStepConfig.setDic(skillStepDic);
			}
				break;
			case ConfigType.SkillStepLevel:
			{
				SkillStepLevelConfig.setDic(skillStepLevelDic);
			}
				break;
			case ConfigType.SkillVar:
			{
				SkillVarConfig.setDic(skillVarDic);
			}
				break;
			case ConfigType.SpecialMove:
			{
				SpecialMoveConfig.setDic(specialMoveDic);
			}
				break;
			case ConfigType.Status:
			{
				StatusConfig.setDic(statusDic);
			}
				break;
			case ConfigType.SubsectionRank:
			{
				SubsectionRankConfig.setDic(subsectionRankDic);
			}
				break;
			case ConfigType.Task:
			{
				TaskConfig.setDic(taskDic);
			}
				break;
			case ConfigType.TaskType:
			{
				TaskTypeConfig.setDic(taskTypeDic);
			}
				break;
			case ConfigType.TeamTarget:
			{
				TeamTargetConfig.setDic(teamTargetDic);
			}
				break;
			case ConfigType.Text:
			{
				TextConfig.setDic(textDic);
			}
				break;
			case ConfigType.UnitMoveType:
			{
				UnitMoveTypeConfig.setDic(unitMoveTypeDic);
			}
				break;
			case ConfigType.UnitSpecialMoveType:
			{
				UnitSpecialMoveTypeConfig.setDic(unitSpecialMoveTypeDic);
			}
				break;
			case ConfigType.Vehicle:
			{
				VehicleConfig.setDic(vehicleDic);
			}
				break;
			case ConfigType.Vocation:
			{
				VocationConfig.setDic(vocationDic);
			}
				break;
		}
	}
	
	/** 读完所有配置 */
	public void afterReadConfigAllOne(int type)
	{
		switch(type)
		{
			case ConfigType.Global:
			{
				Global.afterReadConfigAll();
			}
				break;
			case ConfigType.Trigger:
			{
				afterReadConfigAllTrigger();
			}
				break;
			case ConfigType.ScenePlaceEditor:
			{
				afterReadConfigAllScenePlaceEditor();
			}
				break;
			case ConfigType.MapInfo:
			{
				afterReadConfigAllMapInfo();
			}
				break;
			case ConfigType.Language:
			{
				LanguageConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.BigFloatRank:
			{
				BigFloatRankConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.SkillInfluenceType:
			{
				SkillInfluenceTypeConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.Achievement:
			{
				AchievementConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.ActivationCode:
			{
				ActivationCodeConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.Activity:
			{
				ActivityConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.AreaInfo:
			{
				AreaInfoConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.AreaLoad:
			{
				AreaLoadConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.Attack:
			{
				AttackConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.AttackGroup:
			{
				AttackGroupConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.AttackLevel:
			{
				AttackLevelConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.Attribute:
			{
				AttributeConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.Auction:
			{
				AuctionConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.AuctionQueryConditionType:
			{
				AuctionQueryConditionTypeConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.AvatarPart:
			{
				AvatarPartConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.Battle:
			{
				BattleConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.Buff:
			{
				BuffConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.BuffActionType:
			{
				BuffActionTypeConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.BuffGroup:
			{
				BuffGroupConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.BuffLevel:
			{
				BuffLevelConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.Building:
			{
				BuildingConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.BuildingLevel:
			{
				BuildingLevelConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.Bullet:
			{
				BulletConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.BulletLevel:
			{
				BulletLevelConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.CallWay:
			{
				CallWayConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.CD:
			{
				CDConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.CDGroup:
			{
				CDGroupConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.Character:
			{
				CharacterConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.ChatChannel:
			{
				ChatChannelConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.ClientPlatformType:
			{
				ClientPlatformTypeConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.Cost:
			{
				CostConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.CountryCode:
			{
				CountryCodeConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.CreateItem:
			{
				CreateItemConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.Currency:
			{
				CurrencyConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.EquipSlotType:
			{
				EquipSlotTypeConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.Exchange:
			{
				ExchangeConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.ExchangeGroup:
			{
				ExchangeGroupConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.Facade:
			{
				FacadeConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.FightUnit:
			{
				FightUnitConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.FightUnitLevel:
			{
				FightUnitLevelConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.FlowStep:
			{
				FlowStepConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.Function:
			{
				FunctionConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.GMType:
			{
				GMTypeConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.InfoCode:
			{
				InfoCodeConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.InfoLog:
			{
				InfoLogConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.InitCreate:
			{
				InitCreateConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.InternationalResource:
			{
				InternationalResourceConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.Item:
			{
				ItemConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.Mail:
			{
				MailConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.MailType:
			{
				MailTypeConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.MapBlockType:
			{
				MapBlockTypeConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.MapMoveType:
			{
				MapMoveTypeConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.Model:
			{
				ModelConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.Monster:
			{
				MonsterConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.MonsterLevel:
			{
				MonsterLevelConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.NPC:
			{
				NPCConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.Operation:
			{
				OperationConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.Pet:
			{
				PetConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.Puppet:
			{
				PuppetConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.PuppetLevel:
			{
				PuppetLevelConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.PushNotify:
			{
				PushNotifyConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.PushTopicType:
			{
				PushTopicTypeConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.Quest:
			{
				QuestConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.RandomItem:
			{
				RandomItemConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.RandomItemList:
			{
				RandomItemListConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.RandomName:
			{
				RandomNameConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.Region:
			{
				RegionConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.Reward:
			{
				RewardConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.RobotFlowStep:
			{
				RobotFlowStepConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.RobotTestMode:
			{
				RobotTestModeConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.RoleAttribute:
			{
				RoleAttributeConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.RoleGroup:
			{
				RoleGroupConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.RoleGroupChangeType:
			{
				RoleGroupChangeTypeConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.RoleGroupLevel:
			{
				RoleGroupLevelConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.RoleGroupMemberChangeType:
			{
				RoleGroupMemberChangeTypeConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.RoleGroupTitle:
			{
				RoleGroupTitleConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.RoleLevel:
			{
				RoleLevelConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.RoleShowDataPartType:
			{
				RoleShowDataPartTypeConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.Scene:
			{
				SceneConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.SceneEffect:
			{
				SceneEffectConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.SceneForceType:
			{
				SceneForceTypeConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.SceneInstanceType:
			{
				SceneInstanceTypeConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.SceneMap:
			{
				SceneMapConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.ScenePlaceElement:
			{
				ScenePlaceElementConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.SceneRoleAttribute:
			{
				SceneRoleAttributeConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.SceneType:
			{
				SceneTypeConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.SensitiveWord:
			{
				SensitiveWordConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.Skill:
			{
				SkillConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.SkillBar:
			{
				SkillBarConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.SkillGroup:
			{
				SkillGroupConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.SkillLevel:
			{
				SkillLevelConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.SkillProb:
			{
				SkillProbConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.SkillStep:
			{
				SkillStepConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.SkillStepLevel:
			{
				SkillStepLevelConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.SkillVar:
			{
				SkillVarConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.SpecialMove:
			{
				SpecialMoveConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.Status:
			{
				StatusConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.SubsectionRank:
			{
				SubsectionRankConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.Task:
			{
				TaskConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.TaskType:
			{
				TaskTypeConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.TeamTarget:
			{
				TeamTargetConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.Text:
			{
				TextConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.UnitMoveType:
			{
				UnitMoveTypeConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.UnitSpecialMoveType:
			{
				UnitSpecialMoveTypeConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.Vehicle:
			{
				VehicleConfig.afterReadConfigAll();
			}
				break;
			case ConfigType.Vocation:
			{
				VocationConfig.afterReadConfigAll();
			}
				break;
		}
	}
	
	/** 从流读取单个 */
	protected void readBytesOne(int type,BytesReadStream stream)
	{
		switch(type)
		{
			case ConfigType.Global:
			{
				readGlobal(stream);
			}
				break;
			case ConfigType.Trigger:
			{
				readTrigger(stream);
			}
				break;
			case ConfigType.ScenePlaceEditor:
			{
				readScenePlaceEditor(stream);
			}
				break;
			case ConfigType.MapInfo:
			{
				readMapInfo(stream);
			}
				break;
			case ConfigType.Language:
			{
				readLanguage(stream);
			}
				break;
			case ConfigType.BigFloatRank:
			{
				readBigFloatRank(stream);
			}
				break;
			case ConfigType.SkillInfluenceType:
			{
				readSkillInfluenceType(stream);
			}
				break;
			case ConfigType.Achievement:
			{
				readAchievement(stream);
			}
				break;
			case ConfigType.ActivationCode:
			{
				readActivationCode(stream);
			}
				break;
			case ConfigType.Activity:
			{
				readActivity(stream);
			}
				break;
			case ConfigType.AreaInfo:
			{
				readAreaInfo(stream);
			}
				break;
			case ConfigType.AreaLoad:
			{
				readAreaLoad(stream);
			}
				break;
			case ConfigType.Attack:
			{
				readAttack(stream);
			}
				break;
			case ConfigType.AttackGroup:
			{
				readAttackGroup(stream);
			}
				break;
			case ConfigType.AttackLevel:
			{
				readAttackLevel(stream);
			}
				break;
			case ConfigType.Attribute:
			{
				readAttribute(stream);
			}
				break;
			case ConfigType.Auction:
			{
				readAuction(stream);
			}
				break;
			case ConfigType.AuctionQueryConditionType:
			{
				readAuctionQueryConditionType(stream);
			}
				break;
			case ConfigType.AvatarPart:
			{
				readAvatarPart(stream);
			}
				break;
			case ConfigType.Battle:
			{
				readBattle(stream);
			}
				break;
			case ConfigType.Buff:
			{
				readBuff(stream);
			}
				break;
			case ConfigType.BuffActionType:
			{
				readBuffActionType(stream);
			}
				break;
			case ConfigType.BuffGroup:
			{
				readBuffGroup(stream);
			}
				break;
			case ConfigType.BuffLevel:
			{
				readBuffLevel(stream);
			}
				break;
			case ConfigType.Building:
			{
				readBuilding(stream);
			}
				break;
			case ConfigType.BuildingLevel:
			{
				readBuildingLevel(stream);
			}
				break;
			case ConfigType.Bullet:
			{
				readBullet(stream);
			}
				break;
			case ConfigType.BulletLevel:
			{
				readBulletLevel(stream);
			}
				break;
			case ConfigType.CallWay:
			{
				readCallWay(stream);
			}
				break;
			case ConfigType.CD:
			{
				readCD(stream);
			}
				break;
			case ConfigType.CDGroup:
			{
				readCDGroup(stream);
			}
				break;
			case ConfigType.Character:
			{
				readCharacter(stream);
			}
				break;
			case ConfigType.ChatChannel:
			{
				readChatChannel(stream);
			}
				break;
			case ConfigType.ClientPlatformType:
			{
				readClientPlatformType(stream);
			}
				break;
			case ConfigType.Cost:
			{
				readCost(stream);
			}
				break;
			case ConfigType.CountryCode:
			{
				readCountryCode(stream);
			}
				break;
			case ConfigType.CreateItem:
			{
				readCreateItem(stream);
			}
				break;
			case ConfigType.Currency:
			{
				readCurrency(stream);
			}
				break;
			case ConfigType.EquipSlotType:
			{
				readEquipSlotType(stream);
			}
				break;
			case ConfigType.Exchange:
			{
				readExchange(stream);
			}
				break;
			case ConfigType.ExchangeGroup:
			{
				readExchangeGroup(stream);
			}
				break;
			case ConfigType.Facade:
			{
				readFacade(stream);
			}
				break;
			case ConfigType.FightUnit:
			{
				readFightUnit(stream);
			}
				break;
			case ConfigType.FightUnitLevel:
			{
				readFightUnitLevel(stream);
			}
				break;
			case ConfigType.FlowStep:
			{
				readFlowStep(stream);
			}
				break;
			case ConfigType.Function:
			{
				readFunction(stream);
			}
				break;
			case ConfigType.GMType:
			{
				readGMType(stream);
			}
				break;
			case ConfigType.InfoCode:
			{
				readInfoCode(stream);
			}
				break;
			case ConfigType.InfoLog:
			{
				readInfoLog(stream);
			}
				break;
			case ConfigType.InitCreate:
			{
				readInitCreate(stream);
			}
				break;
			case ConfigType.InternationalResource:
			{
				readInternationalResource(stream);
			}
				break;
			case ConfigType.Item:
			{
				readItem(stream);
			}
				break;
			case ConfigType.Mail:
			{
				readMail(stream);
			}
				break;
			case ConfigType.MailType:
			{
				readMailType(stream);
			}
				break;
			case ConfigType.MapBlockType:
			{
				readMapBlockType(stream);
			}
				break;
			case ConfigType.MapMoveType:
			{
				readMapMoveType(stream);
			}
				break;
			case ConfigType.Model:
			{
				readModel(stream);
			}
				break;
			case ConfigType.Monster:
			{
				readMonster(stream);
			}
				break;
			case ConfigType.MonsterLevel:
			{
				readMonsterLevel(stream);
			}
				break;
			case ConfigType.NPC:
			{
				readNPC(stream);
			}
				break;
			case ConfigType.Operation:
			{
				readOperation(stream);
			}
				break;
			case ConfigType.Pet:
			{
				readPet(stream);
			}
				break;
			case ConfigType.Puppet:
			{
				readPuppet(stream);
			}
				break;
			case ConfigType.PuppetLevel:
			{
				readPuppetLevel(stream);
			}
				break;
			case ConfigType.PushNotify:
			{
				readPushNotify(stream);
			}
				break;
			case ConfigType.PushTopicType:
			{
				readPushTopicType(stream);
			}
				break;
			case ConfigType.Quest:
			{
				readQuest(stream);
			}
				break;
			case ConfigType.RandomItem:
			{
				readRandomItem(stream);
			}
				break;
			case ConfigType.RandomItemList:
			{
				readRandomItemList(stream);
			}
				break;
			case ConfigType.RandomName:
			{
				readRandomName(stream);
			}
				break;
			case ConfigType.Region:
			{
				readRegion(stream);
			}
				break;
			case ConfigType.Reward:
			{
				readReward(stream);
			}
				break;
			case ConfigType.RobotFlowStep:
			{
				readRobotFlowStep(stream);
			}
				break;
			case ConfigType.RobotTestMode:
			{
				readRobotTestMode(stream);
			}
				break;
			case ConfigType.RoleAttribute:
			{
				readRoleAttribute(stream);
			}
				break;
			case ConfigType.RoleGroup:
			{
				readRoleGroup(stream);
			}
				break;
			case ConfigType.RoleGroupChangeType:
			{
				readRoleGroupChangeType(stream);
			}
				break;
			case ConfigType.RoleGroupLevel:
			{
				readRoleGroupLevel(stream);
			}
				break;
			case ConfigType.RoleGroupMemberChangeType:
			{
				readRoleGroupMemberChangeType(stream);
			}
				break;
			case ConfigType.RoleGroupTitle:
			{
				readRoleGroupTitle(stream);
			}
				break;
			case ConfigType.RoleLevel:
			{
				readRoleLevel(stream);
			}
				break;
			case ConfigType.RoleShowDataPartType:
			{
				readRoleShowDataPartType(stream);
			}
				break;
			case ConfigType.Scene:
			{
				readScene(stream);
			}
				break;
			case ConfigType.SceneEffect:
			{
				readSceneEffect(stream);
			}
				break;
			case ConfigType.SceneForceType:
			{
				readSceneForceType(stream);
			}
				break;
			case ConfigType.SceneInstanceType:
			{
				readSceneInstanceType(stream);
			}
				break;
			case ConfigType.SceneMap:
			{
				readSceneMap(stream);
			}
				break;
			case ConfigType.ScenePlaceElement:
			{
				readScenePlaceElement(stream);
			}
				break;
			case ConfigType.SceneRoleAttribute:
			{
				readSceneRoleAttribute(stream);
			}
				break;
			case ConfigType.SceneType:
			{
				readSceneType(stream);
			}
				break;
			case ConfigType.SensitiveWord:
			{
				readSensitiveWord(stream);
			}
				break;
			case ConfigType.Skill:
			{
				readSkill(stream);
			}
				break;
			case ConfigType.SkillBar:
			{
				readSkillBar(stream);
			}
				break;
			case ConfigType.SkillGroup:
			{
				readSkillGroup(stream);
			}
				break;
			case ConfigType.SkillLevel:
			{
				readSkillLevel(stream);
			}
				break;
			case ConfigType.SkillProb:
			{
				readSkillProb(stream);
			}
				break;
			case ConfigType.SkillStep:
			{
				readSkillStep(stream);
			}
				break;
			case ConfigType.SkillStepLevel:
			{
				readSkillStepLevel(stream);
			}
				break;
			case ConfigType.SkillVar:
			{
				readSkillVar(stream);
			}
				break;
			case ConfigType.SpecialMove:
			{
				readSpecialMove(stream);
			}
				break;
			case ConfigType.Status:
			{
				readStatus(stream);
			}
				break;
			case ConfigType.SubsectionRank:
			{
				readSubsectionRank(stream);
			}
				break;
			case ConfigType.Task:
			{
				readTask(stream);
			}
				break;
			case ConfigType.TaskType:
			{
				readTaskType(stream);
			}
				break;
			case ConfigType.TeamTarget:
			{
				readTeamTarget(stream);
			}
				break;
			case ConfigType.Text:
			{
				readText(stream);
			}
				break;
			case ConfigType.UnitMoveType:
			{
				readUnitMoveType(stream);
			}
				break;
			case ConfigType.UnitSpecialMoveType:
			{
				readUnitSpecialMoveType(stream);
			}
				break;
			case ConfigType.Vehicle:
			{
				readVehicle(stream);
			}
				break;
			case ConfigType.Vocation:
			{
				readVocation(stream);
			}
				break;
		}
	}
	
	/** 刷新数据 */
	public void refreshDataOne(int type)
	{
		switch(type)
		{
			case ConfigType.Language:
			{
				refreshLanguage();
			}
				break;
			case ConfigType.BigFloatRank:
			{
				refreshBigFloatRank();
			}
				break;
			case ConfigType.SkillInfluenceType:
			{
				refreshSkillInfluenceType();
			}
				break;
			case ConfigType.Achievement:
			{
				refreshAchievement();
			}
				break;
			case ConfigType.ActivationCode:
			{
				refreshActivationCode();
			}
				break;
			case ConfigType.Activity:
			{
				refreshActivity();
			}
				break;
			case ConfigType.AreaInfo:
			{
				refreshAreaInfo();
			}
				break;
			case ConfigType.AreaLoad:
			{
				refreshAreaLoad();
			}
				break;
			case ConfigType.Attack:
			{
				refreshAttack();
			}
				break;
			case ConfigType.AttackGroup:
			{
				refreshAttackGroup();
			}
				break;
			case ConfigType.AttackLevel:
			{
				refreshAttackLevel();
			}
				break;
			case ConfigType.Attribute:
			{
				refreshAttribute();
			}
				break;
			case ConfigType.Auction:
			{
				refreshAuction();
			}
				break;
			case ConfigType.AuctionQueryConditionType:
			{
				refreshAuctionQueryConditionType();
			}
				break;
			case ConfigType.AvatarPart:
			{
				refreshAvatarPart();
			}
				break;
			case ConfigType.Battle:
			{
				refreshBattle();
			}
				break;
			case ConfigType.Buff:
			{
				refreshBuff();
			}
				break;
			case ConfigType.BuffActionType:
			{
				refreshBuffActionType();
			}
				break;
			case ConfigType.BuffGroup:
			{
				refreshBuffGroup();
			}
				break;
			case ConfigType.BuffLevel:
			{
				refreshBuffLevel();
			}
				break;
			case ConfigType.Building:
			{
				refreshBuilding();
			}
				break;
			case ConfigType.BuildingLevel:
			{
				refreshBuildingLevel();
			}
				break;
			case ConfigType.Bullet:
			{
				refreshBullet();
			}
				break;
			case ConfigType.BulletLevel:
			{
				refreshBulletLevel();
			}
				break;
			case ConfigType.CallWay:
			{
				refreshCallWay();
			}
				break;
			case ConfigType.CD:
			{
				refreshCD();
			}
				break;
			case ConfigType.CDGroup:
			{
				refreshCDGroup();
			}
				break;
			case ConfigType.Character:
			{
				refreshCharacter();
			}
				break;
			case ConfigType.ChatChannel:
			{
				refreshChatChannel();
			}
				break;
			case ConfigType.ClientPlatformType:
			{
				refreshClientPlatformType();
			}
				break;
			case ConfigType.Cost:
			{
				refreshCost();
			}
				break;
			case ConfigType.CountryCode:
			{
				refreshCountryCode();
			}
				break;
			case ConfigType.CreateItem:
			{
				refreshCreateItem();
			}
				break;
			case ConfigType.Currency:
			{
				refreshCurrency();
			}
				break;
			case ConfigType.EquipSlotType:
			{
				refreshEquipSlotType();
			}
				break;
			case ConfigType.Exchange:
			{
				refreshExchange();
			}
				break;
			case ConfigType.ExchangeGroup:
			{
				refreshExchangeGroup();
			}
				break;
			case ConfigType.Facade:
			{
				refreshFacade();
			}
				break;
			case ConfigType.FightUnit:
			{
				refreshFightUnit();
			}
				break;
			case ConfigType.FightUnitLevel:
			{
				refreshFightUnitLevel();
			}
				break;
			case ConfigType.FlowStep:
			{
				refreshFlowStep();
			}
				break;
			case ConfigType.Function:
			{
				refreshFunction();
			}
				break;
			case ConfigType.GMType:
			{
				refreshGMType();
			}
				break;
			case ConfigType.InfoCode:
			{
				refreshInfoCode();
			}
				break;
			case ConfigType.InfoLog:
			{
				refreshInfoLog();
			}
				break;
			case ConfigType.InitCreate:
			{
				refreshInitCreate();
			}
				break;
			case ConfigType.InternationalResource:
			{
				refreshInternationalResource();
			}
				break;
			case ConfigType.Item:
			{
				refreshItem();
			}
				break;
			case ConfigType.Mail:
			{
				refreshMail();
			}
				break;
			case ConfigType.MailType:
			{
				refreshMailType();
			}
				break;
			case ConfigType.MapBlockType:
			{
				refreshMapBlockType();
			}
				break;
			case ConfigType.MapMoveType:
			{
				refreshMapMoveType();
			}
				break;
			case ConfigType.Model:
			{
				refreshModel();
			}
				break;
			case ConfigType.Monster:
			{
				refreshMonster();
			}
				break;
			case ConfigType.MonsterLevel:
			{
				refreshMonsterLevel();
			}
				break;
			case ConfigType.NPC:
			{
				refreshNPC();
			}
				break;
			case ConfigType.Operation:
			{
				refreshOperation();
			}
				break;
			case ConfigType.Pet:
			{
				refreshPet();
			}
				break;
			case ConfigType.Puppet:
			{
				refreshPuppet();
			}
				break;
			case ConfigType.PuppetLevel:
			{
				refreshPuppetLevel();
			}
				break;
			case ConfigType.PushNotify:
			{
				refreshPushNotify();
			}
				break;
			case ConfigType.PushTopicType:
			{
				refreshPushTopicType();
			}
				break;
			case ConfigType.Quest:
			{
				refreshQuest();
			}
				break;
			case ConfigType.RandomItem:
			{
				refreshRandomItem();
			}
				break;
			case ConfigType.RandomItemList:
			{
				refreshRandomItemList();
			}
				break;
			case ConfigType.RandomName:
			{
				refreshRandomName();
			}
				break;
			case ConfigType.Region:
			{
				refreshRegion();
			}
				break;
			case ConfigType.Reward:
			{
				refreshReward();
			}
				break;
			case ConfigType.RobotFlowStep:
			{
				refreshRobotFlowStep();
			}
				break;
			case ConfigType.RobotTestMode:
			{
				refreshRobotTestMode();
			}
				break;
			case ConfigType.RoleAttribute:
			{
				refreshRoleAttribute();
			}
				break;
			case ConfigType.RoleGroup:
			{
				refreshRoleGroup();
			}
				break;
			case ConfigType.RoleGroupChangeType:
			{
				refreshRoleGroupChangeType();
			}
				break;
			case ConfigType.RoleGroupLevel:
			{
				refreshRoleGroupLevel();
			}
				break;
			case ConfigType.RoleGroupMemberChangeType:
			{
				refreshRoleGroupMemberChangeType();
			}
				break;
			case ConfigType.RoleGroupTitle:
			{
				refreshRoleGroupTitle();
			}
				break;
			case ConfigType.RoleLevel:
			{
				refreshRoleLevel();
			}
				break;
			case ConfigType.RoleShowDataPartType:
			{
				refreshRoleShowDataPartType();
			}
				break;
			case ConfigType.Scene:
			{
				refreshScene();
			}
				break;
			case ConfigType.SceneEffect:
			{
				refreshSceneEffect();
			}
				break;
			case ConfigType.SceneForceType:
			{
				refreshSceneForceType();
			}
				break;
			case ConfigType.SceneInstanceType:
			{
				refreshSceneInstanceType();
			}
				break;
			case ConfigType.SceneMap:
			{
				refreshSceneMap();
			}
				break;
			case ConfigType.ScenePlaceElement:
			{
				refreshScenePlaceElement();
			}
				break;
			case ConfigType.SceneRoleAttribute:
			{
				refreshSceneRoleAttribute();
			}
				break;
			case ConfigType.SceneType:
			{
				refreshSceneType();
			}
				break;
			case ConfigType.SensitiveWord:
			{
				refreshSensitiveWord();
			}
				break;
			case ConfigType.Skill:
			{
				refreshSkill();
			}
				break;
			case ConfigType.SkillBar:
			{
				refreshSkillBar();
			}
				break;
			case ConfigType.SkillGroup:
			{
				refreshSkillGroup();
			}
				break;
			case ConfigType.SkillLevel:
			{
				refreshSkillLevel();
			}
				break;
			case ConfigType.SkillProb:
			{
				refreshSkillProb();
			}
				break;
			case ConfigType.SkillStep:
			{
				refreshSkillStep();
			}
				break;
			case ConfigType.SkillStepLevel:
			{
				refreshSkillStepLevel();
			}
				break;
			case ConfigType.SkillVar:
			{
				refreshSkillVar();
			}
				break;
			case ConfigType.SpecialMove:
			{
				refreshSpecialMove();
			}
				break;
			case ConfigType.Status:
			{
				refreshStatus();
			}
				break;
			case ConfigType.SubsectionRank:
			{
				refreshSubsectionRank();
			}
				break;
			case ConfigType.Task:
			{
				refreshTask();
			}
				break;
			case ConfigType.TaskType:
			{
				refreshTaskType();
			}
				break;
			case ConfigType.TeamTarget:
			{
				refreshTeamTarget();
			}
				break;
			case ConfigType.Text:
			{
				refreshText();
			}
				break;
			case ConfigType.UnitMoveType:
			{
				refreshUnitMoveType();
			}
				break;
			case ConfigType.UnitSpecialMoveType:
			{
				refreshUnitSpecialMoveType();
			}
				break;
			case ConfigType.Vehicle:
			{
				refreshVehicle();
			}
				break;
			case ConfigType.Vocation:
			{
				refreshVocation();
			}
				break;
		}
	}
	
	/** 读取机器人流程步骤类型 */
	protected void readRobotFlowStep(BytesReadStream stream)
	{
		RobotFlowStepConfig config;
		int len=stream.readLen();
		int size=stream.readLen();
		robotFlowStepDic=new RobotFlowStepConfig[size];
		for(int i=0;i<len;++i)
		{
			config=new RobotFlowStepConfig();
			config.readBytesSimple(stream);
			robotFlowStepDic[config.id]=config;
		}
	}
	
	/** 刷新机器人流程步骤类型 */
	private void refreshRobotFlowStep()
	{
		for(int configI=0,configLen=robotFlowStepDic.length;configI<configLen;++configI)
		{
			RobotFlowStepConfig config=robotFlowStepDic[configI];
			if(config!=null)
				config.refresh();
		}
	}
	
	/** 读取CD表 */
	protected void readCD(BytesReadStream stream)
	{
		CDConfig config;
		int len=stream.readLen();
		cdDic=new IntObjectMap<CDConfig>(CDConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new CDConfig();
			config.readBytesSimple(stream);
			cdDic.put(config.id,config);
		}
	}
	
	/** 刷新CD表 */
	private void refreshCD()
	{
		if(!cdDic.isEmpty())
		{
			Object[] configValues=cdDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					CDConfig config=(CDConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取活动表 */
	protected void readActivity(BytesReadStream stream)
	{
		ActivityConfig config;
		int len=stream.readLen();
		activityDic=new IntObjectMap<ActivityConfig>(ActivityConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new ActivityConfig();
			config.readBytesSimple(stream);
			activityDic.put(config.id,config);
		}
	}
	
	/** 刷新活动表 */
	private void refreshActivity()
	{
		if(!activityDic.isEmpty())
		{
			Object[] configValues=activityDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					ActivityConfig config=(ActivityConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取调用方式表 */
	protected void readCallWay(BytesReadStream stream)
	{
		CallWayConfig config;
		int len=stream.readLen();
		callWayDic=new IntObjectMap<CallWayConfig>(CallWayConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new CallWayConfig();
			config.readBytesSimple(stream);
			callWayDic.put(config.id,config);
		}
	}
	
	/** 刷新调用方式表 */
	private void refreshCallWay()
	{
		if(!callWayDic.isEmpty())
		{
			Object[] configValues=callWayDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					CallWayConfig config=(CallWayConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取buff表 */
	protected void readBuff(BytesReadStream stream)
	{
		BuffConfig config;
		int len=stream.readLen();
		buffDic=new IntObjectMap<BuffConfig>(BuffConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new BuffConfig();
			config.readBytesSimple(stream);
			buffDic.put(config.id,config);
		}
	}
	
	/** 刷新buff表 */
	private void refreshBuff()
	{
		if(!buffDic.isEmpty())
		{
			Object[] configValues=buffDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					BuffConfig config=(BuffConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取场景特效表 */
	protected void readSceneEffect(BytesReadStream stream)
	{
		SceneEffectConfig config;
		int len=stream.readLen();
		sceneEffectDic=new IntObjectMap<SceneEffectConfig>(SceneEffectConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new SceneEffectConfig();
			config.readBytesSimple(stream);
			sceneEffectDic.put(config.id,config);
		}
	}
	
	/** 刷新场景特效表 */
	private void refreshSceneEffect()
	{
		if(!sceneEffectDic.isEmpty())
		{
			Object[] configValues=sceneEffectDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					SceneEffectConfig config=(SceneEffectConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取技能几率表 */
	protected void readSkillProb(BytesReadStream stream)
	{
		SkillProbConfig config;
		int len=stream.readLen();
		skillProbDic=new IntObjectMap<SkillProbConfig>(SkillProbConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new SkillProbConfig();
			config.readBytesSimple(stream);
			skillProbDic.put(config.id,config);
		}
	}
	
	/** 刷新技能几率表 */
	private void refreshSkillProb()
	{
		if(!skillProbDic.isEmpty())
		{
			Object[] configValues=skillProbDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					SkillProbConfig config=(SkillProbConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取特殊移动表 */
	protected void readSpecialMove(BytesReadStream stream)
	{
		SpecialMoveConfig config;
		int len=stream.readLen();
		specialMoveDic=new IntObjectMap<SpecialMoveConfig>(SpecialMoveConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new SpecialMoveConfig();
			config.readBytesSimple(stream);
			specialMoveDic.put(config.id,config);
		}
	}
	
	/** 刷新特殊移动表 */
	private void refreshSpecialMove()
	{
		if(!specialMoveDic.isEmpty())
		{
			Object[] configValues=specialMoveDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					SpecialMoveConfig config=(SpecialMoveConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取随机单个物品配置 */
	protected void readRandomItem(BytesReadStream stream)
	{
		RandomItemConfig config;
		int len=stream.readLen();
		randomItemDic=new IntObjectMap<RandomItemConfig>(RandomItemConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new RandomItemConfig();
			config.readBytesSimple(stream);
			randomItemDic.put(config.id,config);
		}
	}
	
	/** 刷新随机单个物品配置 */
	private void refreshRandomItem()
	{
		if(!randomItemDic.isEmpty())
		{
			Object[] configValues=randomItemDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					RandomItemConfig config=(RandomItemConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取文本表(程序用)(需国际化) */
	protected void readText(BytesReadStream stream)
	{
		TextConfig config;
		int len=stream.readLen();
		textDic=new SMap<String,TextConfig>(len);
		for(int i=0;i<len;++i)
		{
			config=new TextConfig();
			config.readBytesSimple(stream);
			textDic.put(config.key,config);
		}
	}
	
	/** 刷新文本表(程序用)(需国际化) */
	private void refreshText()
	{
		if(!textDic.isEmpty())
		{
			Object[] configTable=textDic.getTable();
			for(int configI=configTable.length-2;configI>=0;configI-=2)
			{
				if(configTable[configI]!=null)
				{
					TextConfig config=(TextConfig)configTable[configI+1];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取流程步骤类型 */
	protected void readFlowStep(BytesReadStream stream)
	{
		FlowStepConfig config;
		int len=stream.readLen();
		flowStepDic=new IntObjectMap<FlowStepConfig>(FlowStepConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new FlowStepConfig();
			config.readBytesSimple(stream);
			flowStepDic.put(config.id,config);
		}
	}
	
	/** 刷新流程步骤类型 */
	private void refreshFlowStep()
	{
		if(!flowStepDic.isEmpty())
		{
			Object[] configValues=flowStepDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					FlowStepConfig config=(FlowStepConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取角色表 */
	protected void readCharacter(BytesReadStream stream)
	{
		CharacterConfig config;
		int len=stream.readLen();
		characterDic=new IntObjectMap<CharacterConfig>(CharacterConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new CharacterConfig();
			config.readBytesSimple(stream);
			characterDic.put(config.id,config);
		}
	}
	
	/** 刷新角色表 */
	private void refreshCharacter()
	{
		if(!characterDic.isEmpty())
		{
			Object[] configValues=characterDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					CharacterConfig config=(CharacterConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取怪物表 */
	protected void readMonster(BytesReadStream stream)
	{
		MonsterConfig config;
		int len=stream.readLen();
		monsterDic=new IntObjectMap<MonsterConfig>(MonsterConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new MonsterConfig();
			config.readBytesSimple(stream);
			monsterDic.put(config.id,config);
		}
	}
	
	/** 刷新怪物表 */
	private void refreshMonster()
	{
		if(!monsterDic.isEmpty())
		{
			Object[] configValues=monsterDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					MonsterConfig config=(MonsterConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取状态类型表 */
	protected void readStatus(BytesReadStream stream)
	{
		StatusConfig config;
		int len=stream.readLen();
		int size=stream.readLen();
		statusDic=new StatusConfig[size];
		for(int i=0;i<len;++i)
		{
			config=new StatusConfig();
			config.readBytesSimple(stream);
			statusDic[config.id]=config;
		}
	}
	
	/** 刷新状态类型表 */
	private void refreshStatus()
	{
		for(int configI=0,configLen=statusDic.length;configI<configLen;++configI)
		{
			StatusConfig config=statusDic[configI];
			if(config!=null)
				config.refresh();
		}
	}
	
	/** 读取成就表 */
	protected void readAchievement(BytesReadStream stream)
	{
		AchievementConfig config;
		int len=stream.readLen();
		achievementDic=new IntObjectMap<AchievementConfig>(AchievementConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new AchievementConfig();
			config.readBytesSimple(stream);
			achievementDic.put(config.id,config);
		}
	}
	
	/** 刷新成就表 */
	private void refreshAchievement()
	{
		if(!achievementDic.isEmpty())
		{
			Object[] configValues=achievementDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					AchievementConfig config=(AchievementConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取随机物品组配置 */
	protected void readRandomItemList(BytesReadStream stream)
	{
		RandomItemListConfig config;
		int len=stream.readLen();
		randomItemListDic=new IntObjectMap<RandomItemListConfig>(RandomItemListConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new RandomItemListConfig();
			config.readBytesSimple(stream);
			randomItemListDic.put(config.id,config);
		}
	}
	
	/** 刷新随机物品组配置 */
	private void refreshRandomItemList()
	{
		if(!randomItemListDic.isEmpty())
		{
			Object[] configValues=randomItemListDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					RandomItemListConfig config=(RandomItemListConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取技能等级表 */
	protected void readSkillLevel(BytesReadStream stream)
	{
		SkillLevelConfig config;
		int len=stream.readLen();
		skillLevelDic=new LongObjectMap<SkillLevelConfig>(SkillLevelConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new SkillLevelConfig();
			config.readBytesSimple(stream);
			skillLevelDic.put((long)config.id << 16 | (long)config.level,config);
		}
	}
	
	/** 刷新技能等级表 */
	private void refreshSkillLevel()
	{
		if(!skillLevelDic.isEmpty())
		{
			Object[] configValues=skillLevelDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					SkillLevelConfig config=(SkillLevelConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取技能攻击等级表 */
	protected void readAttackLevel(BytesReadStream stream)
	{
		AttackLevelConfig config;
		int len=stream.readLen();
		attackLevelDic=new LongObjectMap<AttackLevelConfig>(AttackLevelConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new AttackLevelConfig();
			config.readBytesSimple(stream);
			attackLevelDic.put((long)config.id << 16 | (long)config.level,config);
		}
	}
	
	/** 刷新技能攻击等级表 */
	private void refreshAttackLevel()
	{
		if(!attackLevelDic.isEmpty())
		{
			Object[] configValues=attackLevelDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					AttackLevelConfig config=(AttackLevelConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取技能攻击表 */
	protected void readAttack(BytesReadStream stream)
	{
		AttackConfig config;
		int len=stream.readLen();
		attackDic=new IntObjectMap<AttackConfig>(AttackConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new AttackConfig();
			config.readBytesSimple(stream);
			attackDic.put(config.id,config);
		}
	}
	
	/** 刷新技能攻击表 */
	private void refreshAttack()
	{
		if(!attackDic.isEmpty())
		{
			Object[] configValues=attackDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					AttackConfig config=(AttackConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取邮件表 */
	protected void readMail(BytesReadStream stream)
	{
		MailConfig config;
		int len=stream.readLen();
		mailDic=new IntObjectMap<MailConfig>(MailConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new MailConfig();
			config.readBytesSimple(stream);
			mailDic.put(config.id,config);
		}
	}
	
	/** 刷新邮件表 */
	private void refreshMail()
	{
		if(!mailDic.isEmpty())
		{
			Object[] configValues=mailDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					MailConfig config=(MailConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取副本基础表 */
	protected void readBattle(BytesReadStream stream)
	{
		BattleConfig config;
		int len=stream.readLen();
		battleDic=new IntObjectMap<BattleConfig>(BattleConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new BattleConfig();
			config.readBytesSimple(stream);
			battleDic.put(config.id,config);
		}
	}
	
	/** 刷新副本基础表 */
	private void refreshBattle()
	{
		if(!battleDic.isEmpty())
		{
			Object[] configValues=battleDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					BattleConfig config=(BattleConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取场景类型 */
	protected void readSceneType(BytesReadStream stream)
	{
		SceneTypeConfig config;
		int len=stream.readLen();
		int size=stream.readLen();
		sceneTypeDic=new SceneTypeConfig[size];
		for(int i=0;i<len;++i)
		{
			config=new SceneTypeConfig();
			config.readBytesSimple(stream);
			sceneTypeDic[config.id]=config;
		}
	}
	
	/** 刷新场景类型 */
	private void refreshSceneType()
	{
		for(int configI=0,configLen=sceneTypeDic.length;configI<configLen;++configI)
		{
			SceneTypeConfig config=sceneTypeDic[configI];
			if(config!=null)
				config.refresh();
		}
	}
	
	/** 读取任务目标类型 */
	protected void readTaskType(BytesReadStream stream)
	{
		TaskTypeConfig config;
		int len=stream.readLen();
		int size=stream.readLen();
		taskTypeDic=new TaskTypeConfig[size];
		for(int i=0;i<len;++i)
		{
			config=new TaskTypeConfig();
			config.readBytesSimple(stream);
			taskTypeDic[config.id]=config;
		}
	}
	
	/** 刷新任务目标类型 */
	private void refreshTaskType()
	{
		for(int configI=0,configLen=taskTypeDic.length;configI<configLen;++configI)
		{
			TaskTypeConfig config=taskTypeDic[configI];
			if(config!=null)
				config.refresh();
		}
	}
	
	/** 读取单位移动方式 */
	protected void readUnitMoveType(BytesReadStream stream)
	{
		UnitMoveTypeConfig config;
		int len=stream.readLen();
		int size=stream.readLen();
		unitMoveTypeDic=new UnitMoveTypeConfig[size];
		for(int i=0;i<len;++i)
		{
			config=new UnitMoveTypeConfig();
			config.readBytesSimple(stream);
			unitMoveTypeDic[config.id]=config;
		}
	}
	
	/** 刷新单位移动方式 */
	private void refreshUnitMoveType()
	{
		for(int configI=0,configLen=unitMoveTypeDic.length;configI<configLen;++configI)
		{
			UnitMoveTypeConfig config=unitMoveTypeDic[configI];
			if(config!=null)
				config.refresh();
		}
	}
	
	/** 读取角色显示数据部件类型 */
	protected void readRoleShowDataPartType(BytesReadStream stream)
	{
		RoleShowDataPartTypeConfig config;
		int len=stream.readLen();
		int size=stream.readLen();
		roleShowDataPartTypeDic=new RoleShowDataPartTypeConfig[size];
		for(int i=0;i<len;++i)
		{
			config=new RoleShowDataPartTypeConfig();
			config.readBytesSimple(stream);
			roleShowDataPartTypeDic[config.id]=config;
		}
	}
	
	/** 刷新角色显示数据部件类型 */
	private void refreshRoleShowDataPartType()
	{
		for(int configI=0,configLen=roleShowDataPartTypeDic.length;configI<configLen;++configI)
		{
			RoleShowDataPartTypeConfig config=roleShowDataPartTypeDic[configI];
			if(config!=null)
				config.refresh();
		}
	}
	
	/** 读取邮件类型 */
	protected void readMailType(BytesReadStream stream)
	{
		MailTypeConfig config;
		int len=stream.readLen();
		int size=stream.readLen();
		mailTypeDic=new MailTypeConfig[size];
		for(int i=0;i<len;++i)
		{
			config=new MailTypeConfig();
			config.readBytesSimple(stream);
			mailTypeDic[config.id]=config;
		}
	}
	
	/** 刷新邮件类型 */
	private void refreshMailType()
	{
		for(int configI=0,configLen=mailTypeDic.length;configI<configLen;++configI)
		{
			MailTypeConfig config=mailTypeDic[configI];
			if(config!=null)
				config.refresh();
		}
	}
	
	/** 读取单位特殊移动类型 */
	protected void readUnitSpecialMoveType(BytesReadStream stream)
	{
		UnitSpecialMoveTypeConfig config;
		int len=stream.readLen();
		int size=stream.readLen();
		unitSpecialMoveTypeDic=new UnitSpecialMoveTypeConfig[size];
		for(int i=0;i<len;++i)
		{
			config=new UnitSpecialMoveTypeConfig();
			config.readBytesSimple(stream);
			unitSpecialMoveTypeDic[config.id]=config;
		}
	}
	
	/** 刷新单位特殊移动类型 */
	private void refreshUnitSpecialMoveType()
	{
		for(int configI=0,configLen=unitSpecialMoveTypeDic.length;configI<configLen;++configI)
		{
			UnitSpecialMoveTypeConfig config=unitSpecialMoveTypeDic[configI];
			if(config!=null)
				config.refresh();
		}
	}
	
	/** 读取玩家等级表 */
	protected void readRoleLevel(BytesReadStream stream)
	{
		RoleLevelConfig config;
		int len=stream.readLen();
		roleLevelDic=new IntObjectMap<RoleLevelConfig>(RoleLevelConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new RoleLevelConfig();
			config.readBytesSimple(stream);
			roleLevelDic.put(config.level,config);
		}
	}
	
	/** 刷新玩家等级表 */
	private void refreshRoleLevel()
	{
		if(!roleLevelDic.isEmpty())
		{
			Object[] configValues=roleLevelDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					RoleLevelConfig config=(RoleLevelConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取随机名字表 */
	protected void readRandomName(BytesReadStream stream)
	{
		RandomNameConfig config;
		int len=stream.readLen();
		randomNameDic=new IntObjectMap<RandomNameConfig>(RandomNameConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new RandomNameConfig();
			config.readBytesSimple(stream);
			randomNameDic.put(config.id,config);
		}
	}
	
	/** 刷新随机名字表 */
	private void refreshRandomName()
	{
		if(!randomNameDic.isEmpty())
		{
			Object[] configValues=randomNameDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					RandomNameConfig config=(RandomNameConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取技能变量表 */
	protected void readSkillVar(BytesReadStream stream)
	{
		SkillVarConfig config;
		int len=stream.readLen();
		skillVarDic=new IntObjectMap<SkillVarConfig>(SkillVarConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new SkillVarConfig();
			config.readBytesSimple(stream);
			skillVarDic.put(config.id,config);
		}
	}
	
	/** 刷新技能变量表 */
	private void refreshSkillVar()
	{
		if(!skillVarDic.isEmpty())
		{
			Object[] configValues=skillVarDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					SkillVarConfig config=(SkillVarConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取技能表 */
	protected void readSkill(BytesReadStream stream)
	{
		SkillConfig config;
		int len=stream.readLen();
		skillDic=new IntObjectMap<SkillConfig>(SkillConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new SkillConfig();
			config.readBytesSimple(stream);
			skillDic.put(config.id,config);
		}
	}
	
	/** 刷新技能表 */
	private void refreshSkill()
	{
		if(!skillDic.isEmpty())
		{
			Object[] configValues=skillDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					SkillConfig config=(SkillConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取属性类型表 */
	protected void readAttribute(BytesReadStream stream)
	{
		AttributeConfig config;
		int len=stream.readLen();
		int size=stream.readLen();
		attributeDic=new AttributeConfig[size];
		for(int i=0;i<len;++i)
		{
			config=new AttributeConfig();
			config.readBytesSimple(stream);
			attributeDic[config.id]=config;
		}
	}
	
	/** 刷新属性类型表 */
	private void refreshAttribute()
	{
		for(int configI=0,configLen=attributeDic.length;configI<configLen;++configI)
		{
			AttributeConfig config=attributeDic[configI];
			if(config!=null)
				config.refresh();
		}
	}
	
	/** 读取buff等级表 */
	protected void readBuffLevel(BytesReadStream stream)
	{
		BuffLevelConfig config;
		int len=stream.readLen();
		buffLevelDic=new LongObjectMap<BuffLevelConfig>(BuffLevelConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new BuffLevelConfig();
			config.readBytesSimple(stream);
			buffLevelDic.put((long)config.id << 16 | (long)config.level,config);
		}
	}
	
	/** 刷新buff等级表 */
	private void refreshBuffLevel()
	{
		if(!buffLevelDic.isEmpty())
		{
			Object[] configValues=buffLevelDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					BuffLevelConfig config=(BuffLevelConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取任务目标表 */
	protected void readTask(BytesReadStream stream)
	{
		TaskConfig config;
		int len=stream.readLen();
		taskDic=new IntObjectMap<TaskConfig>(TaskConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new TaskConfig();
			config.readBytesSimple(stream);
			taskDic.put(config.id,config);
		}
	}
	
	/** 刷新任务目标表 */
	private void refreshTask()
	{
		if(!taskDic.isEmpty())
		{
			Object[] configValues=taskDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					TaskConfig config=(TaskConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取cd组表 */
	protected void readCDGroup(BytesReadStream stream)
	{
		CDGroupConfig config;
		int len=stream.readLen();
		cdGroupDic=new IntObjectMap<CDGroupConfig>(CDGroupConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new CDGroupConfig();
			config.readBytesSimple(stream);
			cdGroupDic.put(config.id,config);
		}
	}
	
	/** 刷新cd组表 */
	private void refreshCDGroup()
	{
		if(!cdGroupDic.isEmpty())
		{
			Object[] configValues=cdGroupDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					CDGroupConfig config=(CDGroupConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取单位模型表 */
	protected void readModel(BytesReadStream stream)
	{
		ModelConfig config;
		int len=stream.readLen();
		modelDic=new IntObjectMap<ModelConfig>(ModelConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new ModelConfig();
			config.readBytesSimple(stream);
			modelDic.put(config.id,config);
		}
	}
	
	/** 刷新单位模型表 */
	private void refreshModel()
	{
		if(!modelDic.isEmpty())
		{
			Object[] configValues=modelDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					ModelConfig config=(ModelConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取技能步骤表 */
	protected void readSkillStep(BytesReadStream stream)
	{
		SkillStepConfig config;
		int len=stream.readLen();
		skillStepDic=new LongObjectMap<SkillStepConfig>(SkillStepConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new SkillStepConfig();
			config.readBytesSimple(stream);
			skillStepDic.put((long)config.id << 8 | (long)config.step,config);
		}
	}
	
	/** 刷新技能步骤表 */
	private void refreshSkillStep()
	{
		if(!skillStepDic.isEmpty())
		{
			Object[] configValues=skillStepDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					SkillStepConfig config=(SkillStepConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取机器人测试模式 */
	protected void readRobotTestMode(BytesReadStream stream)
	{
		RobotTestModeConfig config;
		int len=stream.readLen();
		int size=stream.readLen();
		robotTestModeDic=new RobotTestModeConfig[size];
		for(int i=0;i<len;++i)
		{
			config=new RobotTestModeConfig();
			config.readBytesSimple(stream);
			robotTestModeDic[config.id]=config;
		}
	}
	
	/** 刷新机器人测试模式 */
	private void refreshRobotTestMode()
	{
		for(int configI=0,configLen=robotTestModeDic.length;configI<configLen;++configI)
		{
			RobotTestModeConfig config=robotTestModeDic[configI];
			if(config!=null)
				config.refresh();
		}
	}
	
	/** 读取技能组表 */
	protected void readSkillGroup(BytesReadStream stream)
	{
		SkillGroupConfig config;
		int len=stream.readLen();
		skillGroupDic=new IntObjectMap<SkillGroupConfig>(SkillGroupConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new SkillGroupConfig();
			config.readBytesSimple(stream);
			skillGroupDic.put(config.id,config);
		}
	}
	
	/** 刷新技能组表 */
	private void refreshSkillGroup()
	{
		if(!skillGroupDic.isEmpty())
		{
			Object[] configValues=skillGroupDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					SkillGroupConfig config=(SkillGroupConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取初始化创建表 */
	protected void readInitCreate(BytesReadStream stream)
	{
		InitCreateConfig config;
		int len=stream.readLen();
		initCreateDic=new IntObjectMap<InitCreateConfig>(InitCreateConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new InitCreateConfig();
			config.readBytesSimple(stream);
			initCreateDic.put(config.vocation,config);
		}
	}
	
	/** 刷新初始化创建表 */
	private void refreshInitCreate()
	{
		if(!initCreateDic.isEmpty())
		{
			Object[] configValues=initCreateDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					InitCreateConfig config=(InitCreateConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取攻击组表 */
	protected void readAttackGroup(BytesReadStream stream)
	{
		AttackGroupConfig config;
		int len=stream.readLen();
		attackGroupDic=new IntObjectMap<AttackGroupConfig>(AttackGroupConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new AttackGroupConfig();
			config.readBytesSimple(stream);
			attackGroupDic.put(config.id,config);
		}
	}
	
	/** 刷新攻击组表 */
	private void refreshAttackGroup()
	{
		if(!attackGroupDic.isEmpty())
		{
			Object[] configValues=attackGroupDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					AttackGroupConfig config=(AttackGroupConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取buff组表 */
	protected void readBuffGroup(BytesReadStream stream)
	{
		BuffGroupConfig config;
		int len=stream.readLen();
		buffGroupDic=new IntObjectMap<BuffGroupConfig>(BuffGroupConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new BuffGroupConfig();
			config.readBytesSimple(stream);
			buffGroupDic.put(config.id,config);
		}
	}
	
	/** 刷新buff组表 */
	private void refreshBuffGroup()
	{
		if(!buffGroupDic.isEmpty())
		{
			Object[] configValues=buffGroupDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					BuffGroupConfig config=(BuffGroupConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取任务表 */
	protected void readQuest(BytesReadStream stream)
	{
		QuestConfig config;
		int len=stream.readLen();
		questDic=new IntObjectMap<QuestConfig>(QuestConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new QuestConfig();
			config.readBytesSimple(stream);
			questDic.put(config.id,config);
		}
	}
	
	/** 刷新任务表 */
	private void refreshQuest()
	{
		if(!questDic.isEmpty())
		{
			Object[] configValues=questDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					QuestConfig config=(QuestConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取bullet表 */
	protected void readBullet(BytesReadStream stream)
	{
		BulletConfig config;
		int len=stream.readLen();
		bulletDic=new IntObjectMap<BulletConfig>(BulletConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new BulletConfig();
			config.readBytesSimple(stream);
			bulletDic.put(config.id,config);
		}
	}
	
	/** 刷新bullet表 */
	private void refreshBullet()
	{
		if(!bulletDic.isEmpty())
		{
			Object[] configValues=bulletDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					BulletConfig config=(BulletConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取NPC表 */
	protected void readNPC(BytesReadStream stream)
	{
		NPCConfig config;
		int len=stream.readLen();
		npcDic=new IntObjectMap<NPCConfig>(NPCConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new NPCConfig();
			config.readBytesSimple(stream);
			npcDic.put(config.id,config);
		}
	}
	
	/** 刷新NPC表 */
	private void refreshNPC()
	{
		if(!npcDic.isEmpty())
		{
			Object[] configValues=npcDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					NPCConfig config=(NPCConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取货币表 */
	protected void readCurrency(BytesReadStream stream)
	{
		CurrencyConfig config;
		int len=stream.readLen();
		int size=stream.readLen();
		currencyDic=new CurrencyConfig[size];
		for(int i=0;i<len;++i)
		{
			config=new CurrencyConfig();
			config.readBytesSimple(stream);
			currencyDic[config.id]=config;
		}
	}
	
	/** 刷新货币表 */
	private void refreshCurrency()
	{
		for(int configI=0,configLen=currencyDic.length;configI<configLen;++configI)
		{
			CurrencyConfig config=currencyDic[configI];
			if(config!=null)
				config.refresh();
		}
	}
	
	/** 读取技能步骤等级表 */
	protected void readSkillStepLevel(BytesReadStream stream)
	{
		SkillStepLevelConfig config;
		int len=stream.readLen();
		skillStepLevelDic=new LongObjectMap<SkillStepLevelConfig>(SkillStepLevelConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new SkillStepLevelConfig();
			config.readBytesSimple(stream);
			skillStepLevelDic.put((long)config.id << 24 | (long)config.step << 16 | (long)config.level,config);
		}
	}
	
	/** 刷新技能步骤等级表 */
	private void refreshSkillStepLevel()
	{
		if(!skillStepLevelDic.isEmpty())
		{
			Object[] configValues=skillStepLevelDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					SkillStepLevelConfig config=(SkillStepLevelConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取傀儡表 */
	protected void readPuppet(BytesReadStream stream)
	{
		PuppetConfig config;
		int len=stream.readLen();
		puppetDic=new IntObjectMap<PuppetConfig>(PuppetConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new PuppetConfig();
			config.readBytesSimple(stream);
			puppetDic.put(config.id,config);
		}
	}
	
	/** 刷新傀儡表 */
	private void refreshPuppet()
	{
		if(!puppetDic.isEmpty())
		{
			Object[] configValues=puppetDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					PuppetConfig config=(PuppetConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取傀儡等级表 */
	protected void readPuppetLevel(BytesReadStream stream)
	{
		PuppetLevelConfig config;
		int len=stream.readLen();
		puppetLevelDic=new LongObjectMap<PuppetLevelConfig>(PuppetLevelConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new PuppetLevelConfig();
			config.readBytesSimple(stream);
			puppetLevelDic.put((long)config.id << 16 | (long)config.level,config);
		}
	}
	
	/** 刷新傀儡等级表 */
	private void refreshPuppetLevel()
	{
		if(!puppetLevelDic.isEmpty())
		{
			Object[] configValues=puppetLevelDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					PuppetLevelConfig config=(PuppetLevelConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取单位显示部件表 */
	protected void readAvatarPart(BytesReadStream stream)
	{
		AvatarPartConfig config;
		int len=stream.readLen();
		avatarPartDic=new LongObjectMap<AvatarPartConfig>(AvatarPartConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new AvatarPartConfig();
			config.readBytesSimple(stream);
			avatarPartDic.put((long)config.type << 32 | (long)config.id,config);
		}
	}
	
	/** 刷新单位显示部件表 */
	private void refreshAvatarPart()
	{
		if(!avatarPartDic.isEmpty())
		{
			Object[] configValues=avatarPartDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					AvatarPartConfig config=(AvatarPartConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取国际化表 */
	protected void readLanguage(BytesReadStream stream)
	{
		LanguageConfig config;
		int len=stream.readLen();
		languageDic=new SMap<String,LanguageConfig>(len);
		for(int i=0;i<len;++i)
		{
			config=new LanguageConfig();
			config.readBytesSimple(stream);
			languageDic.put(config.key,config);
		}
	}
	
	/** 刷新国际化表 */
	private void refreshLanguage()
	{
		if(!languageDic.isEmpty())
		{
			Object[] configTable=languageDic.getTable();
			for(int configI=configTable.length-2;configI>=0;configI-=2)
			{
				if(configTable[configI]!=null)
				{
					LanguageConfig config=(LanguageConfig)configTable[configI+1];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取单位外观表 */
	protected void readFacade(BytesReadStream stream)
	{
		FacadeConfig config;
		int len=stream.readLen();
		facadeDic=new IntObjectMap<FacadeConfig>(FacadeConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new FacadeConfig();
			config.readBytesSimple(stream);
			facadeDic.put(config.id,config);
		}
	}
	
	/** 刷新单位外观表 */
	private void refreshFacade()
	{
		if(!facadeDic.isEmpty())
		{
			Object[] configValues=facadeDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					FacadeConfig config=(FacadeConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取宠物表 */
	protected void readPet(BytesReadStream stream)
	{
		PetConfig config;
		int len=stream.readLen();
		petDic=new IntObjectMap<PetConfig>(PetConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new PetConfig();
			config.readBytesSimple(stream);
			petDic.put(config.id,config);
		}
	}
	
	/** 刷新宠物表 */
	private void refreshPet()
	{
		if(!petDic.isEmpty())
		{
			Object[] configValues=petDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					PetConfig config=(PetConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取道具表 */
	protected void readItem(BytesReadStream stream)
	{
		ItemConfig config;
		int len=stream.readLen();
		itemDic=new IntObjectMap<ItemConfig>(ItemConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new ItemConfig();
			config.readBytesSimple(stream);
			itemDic.put(config.id,config);
		}
	}
	
	/** 刷新道具表 */
	private void refreshItem()
	{
		if(!itemDic.isEmpty())
		{
			Object[] configValues=itemDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					ItemConfig config=(ItemConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取子弹等级表 */
	protected void readBulletLevel(BytesReadStream stream)
	{
		BulletLevelConfig config;
		int len=stream.readLen();
		bulletLevelDic=new LongObjectMap<BulletLevelConfig>(BulletLevelConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new BulletLevelConfig();
			config.readBytesSimple(stream);
			bulletLevelDic.put((long)config.id << 16 | (long)config.level,config);
		}
	}
	
	/** 刷新子弹等级表 */
	private void refreshBulletLevel()
	{
		if(!bulletLevelDic.isEmpty())
		{
			Object[] configValues=bulletLevelDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					BulletLevelConfig config=(BulletLevelConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取战斗单位表 */
	protected void readFightUnit(BytesReadStream stream)
	{
		FightUnitConfig config;
		int len=stream.readLen();
		fightUnitDic=new IntObjectMap<FightUnitConfig>(FightUnitConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new FightUnitConfig();
			config.readBytesSimple(stream);
			fightUnitDic.put(config.id,config);
		}
	}
	
	/** 刷新战斗单位表 */
	private void refreshFightUnit()
	{
		if(!fightUnitDic.isEmpty())
		{
			Object[] configValues=fightUnitDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					FightUnitConfig config=(FightUnitConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取信息码类型表 */
	protected void readInfoCode(BytesReadStream stream)
	{
		InfoCodeConfig config;
		int len=stream.readLen();
		infoCodeDic=new IntObjectMap<InfoCodeConfig>(InfoCodeConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new InfoCodeConfig();
			config.readBytesSimple(stream);
			infoCodeDic.put(config.id,config);
		}
	}
	
	/** 刷新信息码类型表 */
	private void refreshInfoCode()
	{
		if(!infoCodeDic.isEmpty())
		{
			Object[] configValues=infoCodeDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					InfoCodeConfig config=(InfoCodeConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取战斗单位等级表 */
	protected void readFightUnitLevel(BytesReadStream stream)
	{
		FightUnitLevelConfig config;
		int len=stream.readLen();
		fightUnitLevelDic=new LongObjectMap<FightUnitLevelConfig>(FightUnitLevelConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new FightUnitLevelConfig();
			config.readBytesSimple(stream);
			fightUnitLevelDic.put((long)config.id << 16 | (long)config.level,config);
		}
	}
	
	/** 刷新战斗单位等级表 */
	private void refreshFightUnitLevel()
	{
		if(!fightUnitLevelDic.isEmpty())
		{
			Object[] configValues=fightUnitLevelDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					FightUnitLevelConfig config=(FightUnitLevelConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取国际化资源表 */
	protected void readInternationalResource(BytesReadStream stream)
	{
		InternationalResourceConfig config;
		int len=stream.readLen();
		internationalResourceDic=new SMap<String,InternationalResourceConfig>(len);
		for(int i=0;i<len;++i)
		{
			config=new InternationalResourceConfig();
			config.readBytesSimple(stream);
			internationalResourceDic.put(config.key,config);
		}
	}
	
	/** 刷新国际化资源表 */
	private void refreshInternationalResource()
	{
		if(!internationalResourceDic.isEmpty())
		{
			Object[] configTable=internationalResourceDic.getTable();
			for(int configI=configTable.length-2;configI>=0;configI-=2)
			{
				if(configTable[configI]!=null)
				{
					InternationalResourceConfig config=(InternationalResourceConfig)configTable[configI+1];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取场景布置元素表(编辑器对应) */
	protected void readScenePlaceElement(BytesReadStream stream)
	{
		ScenePlaceElementConfig config;
		int len=stream.readLen();
		scenePlaceElementDic=new LongObjectMap<ScenePlaceElementConfig>(ScenePlaceElementConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new ScenePlaceElementConfig();
			config.readBytesSimple(stream);
			scenePlaceElementDic.put((long)config.sceneID << 32 | (long)config.instanceID,config);
		}
	}
	
	/** 刷新场景布置元素表(编辑器对应) */
	private void refreshScenePlaceElement()
	{
		if(!scenePlaceElementDic.isEmpty())
		{
			Object[] configValues=scenePlaceElementDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					ScenePlaceElementConfig config=(ScenePlaceElementConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取创建单个物品配置(不包含数量) */
	protected void readCreateItem(BytesReadStream stream)
	{
		CreateItemConfig config;
		int len=stream.readLen();
		createItemDic=new IntObjectMap<CreateItemConfig>(CreateItemConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new CreateItemConfig();
			config.readBytesSimple(stream);
			createItemDic.put(config.id,config);
		}
	}
	
	/** 刷新创建单个物品配置(不包含数量) */
	private void refreshCreateItem()
	{
		if(!createItemDic.isEmpty())
		{
			Object[] configValues=createItemDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					CreateItemConfig config=(CreateItemConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取场景表 */
	protected void readScene(BytesReadStream stream)
	{
		SceneConfig config;
		int len=stream.readLen();
		sceneDic=new IntObjectMap<SceneConfig>(SceneConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new SceneConfig();
			config.readBytesSimple(stream);
			sceneDic.put(config.id,config);
		}
	}
	
	/** 刷新场景表 */
	private void refreshScene()
	{
		if(!sceneDic.isEmpty())
		{
			Object[] configValues=sceneDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					SceneConfig config=(SceneConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取功能表 */
	protected void readFunction(BytesReadStream stream)
	{
		FunctionConfig config;
		int len=stream.readLen();
		functionDic=new IntObjectMap<FunctionConfig>(FunctionConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new FunctionConfig();
			config.readBytesSimple(stream);
			functionDic.put(config.id,config);
		}
	}
	
	/** 刷新功能表 */
	private void refreshFunction()
	{
		if(!functionDic.isEmpty())
		{
			Object[] configValues=functionDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					FunctionConfig config=(FunctionConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取奖励表 */
	protected void readReward(BytesReadStream stream)
	{
		RewardConfig config;
		int len=stream.readLen();
		rewardDic=new IntObjectMap<RewardConfig>(RewardConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new RewardConfig();
			config.readBytesSimple(stream);
			rewardDic.put(config.id,config);
		}
	}
	
	/** 刷新奖励表 */
	private void refreshReward()
	{
		if(!rewardDic.isEmpty())
		{
			Object[] configValues=rewardDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					RewardConfig config=(RewardConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取全局配置表 */
	protected void readGlobal(BytesReadStream stream)
	{
		global=new GlobalReadData();
		global.readBytesSimple(stream);
	}
	
	/** 读取trigger */
	private void readTrigger(BytesReadStream stream)
	{
		TriggerConfigData config;
		int len=stream.readLen();
		triggerDic=new IntObjectMap<TriggerConfigData>(TriggerConfigData[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new TriggerConfigData();
			config.readBytesSimple(stream);
			triggerDic.put(config.id,config);
		}
	}
	
	/** 从数据设置到控制类上 */
	private void setToConfigTrigger()
	{
		TriggerConfig.setDic(triggerDic);
	}
	
	/** 读完全部trigger */
	private void afterReadConfigAllTrigger()
	{
		TriggerConfig.afterReadConfigAll();
	}
	
	private void readScenePlaceEditor(BytesReadStream stream)
	{
		int len=stream.readLen();
		
		scenePlaceEditorDic=new IntObjectMap<ScenePlaceConfig>(ScenePlaceConfig[]::new);
		ScenePlaceConfig config;
		ScenePlaceElementConfig eConfig;
		int len2;
		
		for(int i=0;i<len;++i)
		{
			config=new ScenePlaceConfig();
			config.id=stream.readInt();
			config.elements=new IntObjectMap<ScenePlaceElementConfig>(ScenePlaceElementConfig[]::new);
			
			len2=stream.readLen();
			
			for(int j=0;j<len2;j++)
			{
				eConfig=new ScenePlaceElementConfig();
				eConfig.readBytesSimple(stream);
				config.elements.put(eConfig.instanceID,eConfig);
			}
			
			scenePlaceEditorDic.put(config.id,config);
		}
	}
	
	private void setToConfigScenePlaceEditor()
	{
		ScenePlaceConfig.setDic(scenePlaceEditorDic);
	}
	
	private void afterReadConfigAllScenePlaceEditor()
	{
		ScenePlaceConfig.afterReadConfigAll();
	}
	
	private void readMapInfo(BytesReadStream stream)
	{
		int len=stream.readLen();
		
		mapInfoDic=new IntObjectMap<>(MapInfoConfig[]::new);
		MapInfoConfig config;
		
		for(int i=0;i<len;++i)
		{
			config=new MapInfoConfig();
			config.readBytesSimple(stream);
			mapInfoDic.put(config.id,config);
		}
	}
	
	private void setToConfigMapInfo()
	{
		MapInfoConfig.setDic(mapInfoDic);
	}
	
	private void afterReadConfigAllMapInfo()
	{
		MapInfoConfig.afterReadConfigAll();
	}
	
	/** 读取敏感词表 */
	protected void readSensitiveWord(BytesReadStream stream)
	{
		SensitiveWordConfig config;
		int len=stream.readLen();
		sensitiveWordDic=new IntObjectMap<SensitiveWordConfig>(SensitiveWordConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new SensitiveWordConfig();
			config.readBytesSimple(stream);
			sensitiveWordDic.put(config.key,config);
		}
	}
	
	/** 刷新敏感词表 */
	private void refreshSensitiveWord()
	{
		if(!sensitiveWordDic.isEmpty())
		{
			Object[] configValues=sensitiveWordDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					SensitiveWordConfig config=(SensitiveWordConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取国家地区表(登录用) */
	protected void readCountryCode(BytesReadStream stream)
	{
		CountryCodeConfig config;
		int len=stream.readLen();
		countryCodeDic=new IntObjectMap<CountryCodeConfig>(CountryCodeConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new CountryCodeConfig();
			config.readBytesSimple(stream);
			countryCodeDic.put(config.id,config);
		}
	}
	
	/** 刷新国家地区表(登录用) */
	private void refreshCountryCode()
	{
		if(!countryCodeDic.isEmpty())
		{
			Object[] configValues=countryCodeDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					CountryCodeConfig config=(CountryCodeConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取gm类型 */
	protected void readGMType(BytesReadStream stream)
	{
		GMTypeConfig config;
		int len=stream.readLen();
		int size=stream.readLen();
		gMTypeDic=new GMTypeConfig[size];
		for(int i=0;i<len;++i)
		{
			config=new GMTypeConfig();
			config.readBytesSimple(stream);
			gMTypeDic[config.id]=config;
		}
	}
	
	/** 刷新gm类型 */
	private void refreshGMType()
	{
		for(int configI=0,configLen=gMTypeDic.length;configI<configLen;++configI)
		{
			GMTypeConfig config=gMTypeDic[configI];
			if(config!=null)
				config.refresh();
		}
	}
	
	/** 读取区服信息 */
	protected void readAreaInfo(BytesReadStream stream)
	{
		AreaInfoConfig config;
		int len=stream.readLen();
		areaInfoDic=new IntObjectMap<AreaInfoConfig>(AreaInfoConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new AreaInfoConfig();
			config.readBytesSimple(stream);
			areaInfoDic.put(config.id,config);
		}
	}
	
	/** 刷新区服信息 */
	private void refreshAreaInfo()
	{
		if(!areaInfoDic.isEmpty())
		{
			Object[] configValues=areaInfoDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					AreaInfoConfig config=(AreaInfoConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取区服负载值 */
	protected void readAreaLoad(BytesReadStream stream)
	{
		AreaLoadConfig config;
		int len=stream.readLen();
		areaLoadDic=new IntObjectMap<AreaLoadConfig>(AreaLoadConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new AreaLoadConfig();
			config.readBytesSimple(stream);
			areaLoadDic.put(config.id,config);
		}
	}
	
	/** 刷新区服负载值 */
	private void refreshAreaLoad()
	{
		if(!areaLoadDic.isEmpty())
		{
			Object[] configValues=areaLoadDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					AreaLoadConfig config=(AreaLoadConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取消耗表 */
	protected void readCost(BytesReadStream stream)
	{
		CostConfig config;
		int len=stream.readLen();
		costDic=new IntObjectMap<CostConfig>(CostConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new CostConfig();
			config.readBytesSimple(stream);
			costDic.put(config.id,config);
		}
	}
	
	/** 刷新消耗表 */
	private void refreshCost()
	{
		if(!costDic.isEmpty())
		{
			Object[] configValues=costDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					CostConfig config=(CostConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取兑换表 */
	protected void readExchange(BytesReadStream stream)
	{
		ExchangeConfig config;
		int len=stream.readLen();
		exchangeDic=new IntObjectMap<ExchangeConfig>(ExchangeConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new ExchangeConfig();
			config.readBytesSimple(stream);
			exchangeDic.put(config.id,config);
		}
	}
	
	/** 刷新兑换表 */
	private void refreshExchange()
	{
		if(!exchangeDic.isEmpty())
		{
			Object[] configValues=exchangeDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					ExchangeConfig config=(ExchangeConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取大浮点数阶位 */
	protected void readBigFloatRank(BytesReadStream stream)
	{
		BigFloatRankConfig config;
		int len=stream.readLen();
		bigFloatRankDic=new IntObjectMap<BigFloatRankConfig>(BigFloatRankConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new BigFloatRankConfig();
			config.readBytesSimple(stream);
			bigFloatRankDic.put(config.id,config);
		}
	}
	
	/** 刷新大浮点数阶位 */
	private void refreshBigFloatRank()
	{
		if(!bigFloatRankDic.isEmpty())
		{
			Object[] configValues=bigFloatRankDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					BigFloatRankConfig config=(BigFloatRankConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取职业表 */
	protected void readVocation(BytesReadStream stream)
	{
		VocationConfig config;
		int len=stream.readLen();
		int size=stream.readLen();
		vocationDic=new VocationConfig[size];
		for(int i=0;i<len;++i)
		{
			config=new VocationConfig();
			config.readBytesSimple(stream);
			vocationDic[config.id]=config;
		}
	}
	
	/** 刷新职业表 */
	private void refreshVocation()
	{
		for(int configI=0,configLen=vocationDic.length;configI<configLen;++configI)
		{
			VocationConfig config=vocationDic[configI];
			if(config!=null)
				config.refresh();
		}
	}
	
	/** 读取场景地图表 */
	protected void readSceneMap(BytesReadStream stream)
	{
		SceneMapConfig config;
		int len=stream.readLen();
		sceneMapDic=new IntObjectMap<SceneMapConfig>(SceneMapConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new SceneMapConfig();
			config.readBytesSimple(stream);
			sceneMapDic.put(config.id,config);
		}
	}
	
	/** 刷新场景地图表 */
	private void refreshSceneMap()
	{
		if(!sceneMapDic.isEmpty())
		{
			Object[] configValues=sceneMapDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					SceneMapConfig config=(SceneMapConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取推送表 */
	protected void readPushNotify(BytesReadStream stream)
	{
		PushNotifyConfig config;
		int len=stream.readLen();
		int size=stream.readLen();
		pushNotifyDic=new PushNotifyConfig[size];
		for(int i=0;i<len;++i)
		{
			config=new PushNotifyConfig();
			config.readBytesSimple(stream);
			pushNotifyDic[config.id]=config;
		}
	}
	
	/** 刷新推送表 */
	private void refreshPushNotify()
	{
		for(int configI=0,configLen=pushNotifyDic.length;configI<configLen;++configI)
		{
			PushNotifyConfig config=pushNotifyDic[configI];
			if(config!=null)
				config.refresh();
		}
	}
	
	/** 读取操作体表 */
	protected void readOperation(BytesReadStream stream)
	{
		OperationConfig config;
		int len=stream.readLen();
		operationDic=new IntObjectMap<OperationConfig>(OperationConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new OperationConfig();
			config.readBytesSimple(stream);
			operationDic.put(config.id,config);
		}
	}
	
	/** 刷新操作体表 */
	private void refreshOperation()
	{
		if(!operationDic.isEmpty())
		{
			Object[] configValues=operationDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					OperationConfig config=(OperationConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取装备槽位类型 */
	protected void readEquipSlotType(BytesReadStream stream)
	{
		EquipSlotTypeConfig config;
		int len=stream.readLen();
		int size=stream.readLen();
		equipSlotTypeDic=new EquipSlotTypeConfig[size];
		for(int i=0;i<len;++i)
		{
			config=new EquipSlotTypeConfig();
			config.readBytesSimple(stream);
			equipSlotTypeDic[config.id]=config;
		}
	}
	
	/** 刷新装备槽位类型 */
	private void refreshEquipSlotType()
	{
		for(int configI=0,configLen=equipSlotTypeDic.length;configI<configLen;++configI)
		{
			EquipSlotTypeConfig config=equipSlotTypeDic[configI];
			if(config!=null)
				config.refresh();
		}
	}
	
	/** 读取场景角色属性类型表 */
	protected void readSceneRoleAttribute(BytesReadStream stream)
	{
		SceneRoleAttributeConfig config;
		int len=stream.readLen();
		int size=stream.readLen();
		sceneRoleAttributeDic=new SceneRoleAttributeConfig[size];
		for(int i=0;i<len;++i)
		{
			config=new SceneRoleAttributeConfig();
			config.readBytesSimple(stream);
			sceneRoleAttributeDic[config.id]=config;
		}
	}
	
	/** 刷新场景角色属性类型表 */
	private void refreshSceneRoleAttribute()
	{
		for(int configI=0,configLen=sceneRoleAttributeDic.length;configI<configLen;++configI)
		{
			SceneRoleAttributeConfig config=sceneRoleAttributeDic[configI];
			if(config!=null)
				config.refresh();
		}
	}
	
	/** 读取技能影响类型 */
	protected void readSkillInfluenceType(BytesReadStream stream)
	{
		SkillInfluenceTypeConfig config;
		int len=stream.readLen();
		int size=stream.readLen();
		skillInfluenceTypeDic=new SkillInfluenceTypeConfig[size];
		for(int i=0;i<len;++i)
		{
			config=new SkillInfluenceTypeConfig();
			config.readBytesSimple(stream);
			skillInfluenceTypeDic[config.id]=config;
		}
	}
	
	/** 刷新技能影响类型 */
	private void refreshSkillInfluenceType()
	{
		for(int configI=0,configLen=skillInfluenceTypeDic.length;configI<configLen;++configI)
		{
			SkillInfluenceTypeConfig config=skillInfluenceTypeDic[configI];
			if(config!=null)
				config.refresh();
		}
	}
	
	/** 读取建筑等级表 */
	protected void readBuildingLevel(BytesReadStream stream)
	{
		BuildingLevelConfig config;
		int len=stream.readLen();
		buildingLevelDic=new LongObjectMap<BuildingLevelConfig>(BuildingLevelConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new BuildingLevelConfig();
			config.readBytesSimple(stream);
			buildingLevelDic.put((long)config.id << 16 | (long)config.level,config);
		}
	}
	
	/** 刷新建筑等级表 */
	private void refreshBuildingLevel()
	{
		if(!buildingLevelDic.isEmpty())
		{
			Object[] configValues=buildingLevelDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					BuildingLevelConfig config=(BuildingLevelConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取建筑表 */
	protected void readBuilding(BytesReadStream stream)
	{
		BuildingConfig config;
		int len=stream.readLen();
		buildingDic=new IntObjectMap<BuildingConfig>(BuildingConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new BuildingConfig();
			config.readBytesSimple(stream);
			buildingDic.put(config.id,config);
		}
	}
	
	/** 刷新建筑表 */
	private void refreshBuilding()
	{
		if(!buildingDic.isEmpty())
		{
			Object[] configValues=buildingDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					BuildingConfig config=(BuildingConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取技能读条表 */
	protected void readSkillBar(BytesReadStream stream)
	{
		SkillBarConfig config;
		int len=stream.readLen();
		skillBarDic=new IntObjectMap<SkillBarConfig>(SkillBarConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new SkillBarConfig();
			config.readBytesSimple(stream);
			skillBarDic.put(config.id,config);
		}
	}
	
	/** 刷新技能读条表 */
	private void refreshSkillBar()
	{
		if(!skillBarDic.isEmpty())
		{
			Object[] configValues=skillBarDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					SkillBarConfig config=(SkillBarConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取场景实例类型 */
	protected void readSceneInstanceType(BytesReadStream stream)
	{
		SceneInstanceTypeConfig config;
		int len=stream.readLen();
		int size=stream.readLen();
		sceneInstanceTypeDic=new SceneInstanceTypeConfig[size];
		for(int i=0;i<len;++i)
		{
			config=new SceneInstanceTypeConfig();
			config.readBytesSimple(stream);
			sceneInstanceTypeDic[config.id]=config;
		}
	}
	
	/** 刷新场景实例类型 */
	private void refreshSceneInstanceType()
	{
		for(int configI=0,configLen=sceneInstanceTypeDic.length;configI<configLen;++configI)
		{
			SceneInstanceTypeConfig config=sceneInstanceTypeDic[configI];
			if(config!=null)
				config.refresh();
		}
	}
	
	/** 读取玩家群表 */
	protected void readRoleGroup(BytesReadStream stream)
	{
		RoleGroupConfig config;
		int len=stream.readLen();
		roleGroupDic=new IntObjectMap<RoleGroupConfig>(RoleGroupConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new RoleGroupConfig();
			config.readBytesSimple(stream);
			roleGroupDic.put(config.id,config);
		}
	}
	
	/** 刷新玩家群表 */
	private void refreshRoleGroup()
	{
		if(!roleGroupDic.isEmpty())
		{
			Object[] configValues=roleGroupDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					RoleGroupConfig config=(RoleGroupConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取玩家群等级表 */
	protected void readRoleGroupLevel(BytesReadStream stream)
	{
		RoleGroupLevelConfig config;
		int len=stream.readLen();
		roleGroupLevelDic=new LongObjectMap<RoleGroupLevelConfig>(RoleGroupLevelConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new RoleGroupLevelConfig();
			config.readBytesSimple(stream);
			roleGroupLevelDic.put((long)config.id << 32 | (long)config.level,config);
		}
	}
	
	/** 刷新玩家群等级表 */
	private void refreshRoleGroupLevel()
	{
		if(!roleGroupLevelDic.isEmpty())
		{
			Object[] configValues=roleGroupLevelDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					RoleGroupLevelConfig config=(RoleGroupLevelConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取玩家群职位表 */
	protected void readRoleGroupTitle(BytesReadStream stream)
	{
		RoleGroupTitleConfig config;
		int len=stream.readLen();
		int size=stream.readLen();
		roleGroupTitleDic=new RoleGroupTitleConfig[size];
		for(int i=0;i<len;++i)
		{
			config=new RoleGroupTitleConfig();
			config.readBytesSimple(stream);
			roleGroupTitleDic[config.id]=config;
		}
	}
	
	/** 刷新玩家群职位表 */
	private void refreshRoleGroupTitle()
	{
		for(int configI=0,configLen=roleGroupTitleDic.length;configI<configLen;++configI)
		{
			RoleGroupTitleConfig config=roleGroupTitleDic[configI];
			if(config!=null)
				config.refresh();
		}
	}
	
	/** 读取激活码表 */
	protected void readActivationCode(BytesReadStream stream)
	{
		ActivationCodeConfig config;
		int len=stream.readLen();
		activationCodeDic=new IntObjectMap<ActivationCodeConfig>(ActivationCodeConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new ActivationCodeConfig();
			config.readBytesSimple(stream);
			activationCodeDic.put(config.id,config);
		}
	}
	
	/** 刷新激活码表 */
	private void refreshActivationCode()
	{
		if(!activationCodeDic.isEmpty())
		{
			Object[] configValues=activationCodeDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					ActivationCodeConfig config=(ActivationCodeConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取信息日志表 */
	protected void readInfoLog(BytesReadStream stream)
	{
		InfoLogConfig config;
		int len=stream.readLen();
		infoLogDic=new IntObjectMap<InfoLogConfig>(InfoLogConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new InfoLogConfig();
			config.readBytesSimple(stream);
			infoLogDic.put(config.id,config);
		}
	}
	
	/** 刷新信息日志表 */
	private void refreshInfoLog()
	{
		if(!infoLogDic.isEmpty())
		{
			Object[] configValues=infoLogDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					InfoLogConfig config=(InfoLogConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取玩家群数据变更类型 */
	protected void readRoleGroupChangeType(BytesReadStream stream)
	{
		RoleGroupChangeTypeConfig config;
		int len=stream.readLen();
		int size=stream.readLen();
		roleGroupChangeTypeDic=new RoleGroupChangeTypeConfig[size];
		for(int i=0;i<len;++i)
		{
			config=new RoleGroupChangeTypeConfig();
			config.readBytesSimple(stream);
			roleGroupChangeTypeDic[config.id]=config;
		}
	}
	
	/** 刷新玩家群数据变更类型 */
	private void refreshRoleGroupChangeType()
	{
		for(int configI=0,configLen=roleGroupChangeTypeDic.length;configI<configLen;++configI)
		{
			RoleGroupChangeTypeConfig config=roleGroupChangeTypeDic[configI];
			if(config!=null)
				config.refresh();
		}
	}
	
	/** 读取玩家群数据变更类型 */
	protected void readRoleGroupMemberChangeType(BytesReadStream stream)
	{
		RoleGroupMemberChangeTypeConfig config;
		int len=stream.readLen();
		int size=stream.readLen();
		roleGroupMemberChangeTypeDic=new RoleGroupMemberChangeTypeConfig[size];
		for(int i=0;i<len;++i)
		{
			config=new RoleGroupMemberChangeTypeConfig();
			config.readBytesSimple(stream);
			roleGroupMemberChangeTypeDic[config.id]=config;
		}
	}
	
	/** 刷新玩家群数据变更类型 */
	private void refreshRoleGroupMemberChangeType()
	{
		for(int configI=0,configLen=roleGroupMemberChangeTypeDic.length;configI<configLen;++configI)
		{
			RoleGroupMemberChangeTypeConfig config=roleGroupMemberChangeTypeDic[configI];
			if(config!=null)
				config.refresh();
		}
	}
	
	/** 读取兑换组表 */
	protected void readExchangeGroup(BytesReadStream stream)
	{
		ExchangeGroupConfig config;
		int len=stream.readLen();
		exchangeGroupDic=new IntObjectMap<ExchangeGroupConfig>(ExchangeGroupConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new ExchangeGroupConfig();
			config.readBytesSimple(stream);
			exchangeGroupDic.put(config.id,config);
		}
	}
	
	/** 刷新兑换组表 */
	private void refreshExchangeGroup()
	{
		if(!exchangeGroupDic.isEmpty())
		{
			Object[] configValues=exchangeGroupDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					ExchangeGroupConfig config=(ExchangeGroupConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取怪物等级表 */
	protected void readMonsterLevel(BytesReadStream stream)
	{
		MonsterLevelConfig config;
		int len=stream.readLen();
		monsterLevelDic=new LongObjectMap<MonsterLevelConfig>(MonsterLevelConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new MonsterLevelConfig();
			config.readBytesSimple(stream);
			monsterLevelDic.put((long)config.id << 32 | (long)config.level,config);
		}
	}
	
	/** 刷新怪物等级表 */
	private void refreshMonsterLevel()
	{
		if(!monsterLevelDic.isEmpty())
		{
			Object[] configValues=monsterLevelDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					MonsterLevelConfig config=(MonsterLevelConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取角色属性类型表 */
	protected void readRoleAttribute(BytesReadStream stream)
	{
		RoleAttributeConfig config;
		int len=stream.readLen();
		int size=stream.readLen();
		roleAttributeDic=new RoleAttributeConfig[size];
		for(int i=0;i<len;++i)
		{
			config=new RoleAttributeConfig();
			config.readBytesSimple(stream);
			roleAttributeDic[config.id]=config;
		}
	}
	
	/** 刷新角色属性类型表 */
	private void refreshRoleAttribute()
	{
		for(int configI=0,configLen=roleAttributeDic.length;configI<configLen;++configI)
		{
			RoleAttributeConfig config=roleAttributeDic[configI];
			if(config!=null)
				config.refresh();
		}
	}
	
	/** 读取聊天频道表 */
	protected void readChatChannel(BytesReadStream stream)
	{
		ChatChannelConfig config;
		int len=stream.readLen();
		int size=stream.readLen();
		chatChannelDic=new ChatChannelConfig[size];
		for(int i=0;i<len;++i)
		{
			config=new ChatChannelConfig();
			config.readBytesSimple(stream);
			chatChannelDic[config.id]=config;
		}
	}
	
	/** 刷新聊天频道表 */
	private void refreshChatChannel()
	{
		for(int configI=0,configLen=chatChannelDic.length;configI<configLen;++configI)
		{
			ChatChannelConfig config=chatChannelDic[configI];
			if(config!=null)
				config.refresh();
		}
	}
	
	/** 读取区域表 */
	protected void readRegion(BytesReadStream stream)
	{
		RegionConfig config;
		int len=stream.readLen();
		regionDic=new IntObjectMap<RegionConfig>(RegionConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new RegionConfig();
			config.readBytesSimple(stream);
			regionDic.put(config.id,config);
		}
	}
	
	/** 刷新区域表 */
	private void refreshRegion()
	{
		if(!regionDic.isEmpty())
		{
			Object[] configValues=regionDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					RegionConfig config=(RegionConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取客户端平台类型 */
	protected void readClientPlatformType(BytesReadStream stream)
	{
		ClientPlatformTypeConfig config;
		int len=stream.readLen();
		int size=stream.readLen();
		clientPlatformTypeDic=new ClientPlatformTypeConfig[size];
		for(int i=0;i<len;++i)
		{
			config=new ClientPlatformTypeConfig();
			config.readBytesSimple(stream);
			clientPlatformTypeDic[config.id]=config;
		}
	}
	
	/** 刷新客户端平台类型 */
	private void refreshClientPlatformType()
	{
		for(int configI=0,configLen=clientPlatformTypeDic.length;configI<configLen;++configI)
		{
			ClientPlatformTypeConfig config=clientPlatformTypeDic[configI];
			if(config!=null)
				config.refresh();
		}
	}
	
	/** 读取地图格子阻挡类型类型 */
	protected void readMapBlockType(BytesReadStream stream)
	{
		MapBlockTypeConfig config;
		int len=stream.readLen();
		int size=stream.readLen();
		mapBlockTypeDic=new MapBlockTypeConfig[size];
		for(int i=0;i<len;++i)
		{
			config=new MapBlockTypeConfig();
			config.readBytesSimple(stream);
			mapBlockTypeDic[config.id]=config;
		}
	}
	
	/** 刷新地图格子阻挡类型类型 */
	private void refreshMapBlockType()
	{
		for(int configI=0,configLen=mapBlockTypeDic.length;configI<configLen;++configI)
		{
			MapBlockTypeConfig config=mapBlockTypeDic[configI];
			if(config!=null)
				config.refresh();
		}
	}
	
	/** 读取buff行为类型 */
	protected void readBuffActionType(BytesReadStream stream)
	{
		BuffActionTypeConfig config;
		int len=stream.readLen();
		int size=stream.readLen();
		buffActionTypeDic=new BuffActionTypeConfig[size];
		for(int i=0;i<len;++i)
		{
			config=new BuffActionTypeConfig();
			config.readBytesSimple(stream);
			buffActionTypeDic[config.id]=config;
		}
	}
	
	/** 刷新buff行为类型 */
	private void refreshBuffActionType()
	{
		for(int configI=0,configLen=buffActionTypeDic.length;configI<configLen;++configI)
		{
			BuffActionTypeConfig config=buffActionTypeDic[configI];
			if(config!=null)
				config.refresh();
		}
	}
	
	/** 读取场景势力类型 */
	protected void readSceneForceType(BytesReadStream stream)
	{
		SceneForceTypeConfig config;
		int len=stream.readLen();
		int size=stream.readLen();
		sceneForceTypeDic=new SceneForceTypeConfig[size];
		for(int i=0;i<len;++i)
		{
			config=new SceneForceTypeConfig();
			config.readBytesSimple(stream);
			sceneForceTypeDic[config.id]=config;
		}
	}
	
	/** 刷新场景势力类型 */
	private void refreshSceneForceType()
	{
		for(int configI=0,configLen=sceneForceTypeDic.length;configI<configLen;++configI)
		{
			SceneForceTypeConfig config=sceneForceTypeDic[configI];
			if(config!=null)
				config.refresh();
		}
	}
	
	/** 读取载具表 */
	protected void readVehicle(BytesReadStream stream)
	{
		VehicleConfig config;
		int len=stream.readLen();
		vehicleDic=new IntObjectMap<VehicleConfig>(VehicleConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new VehicleConfig();
			config.readBytesSimple(stream);
			vehicleDic.put(config.id,config);
		}
	}
	
	/** 刷新载具表 */
	private void refreshVehicle()
	{
		if(!vehicleDic.isEmpty())
		{
			Object[] configValues=vehicleDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					VehicleConfig config=(VehicleConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取地图移动类型 */
	protected void readMapMoveType(BytesReadStream stream)
	{
		MapMoveTypeConfig config;
		int len=stream.readLen();
		int size=stream.readLen();
		mapMoveTypeDic=new MapMoveTypeConfig[size];
		for(int i=0;i<len;++i)
		{
			config=new MapMoveTypeConfig();
			config.readBytesSimple(stream);
			mapMoveTypeDic[config.id]=config;
		}
	}
	
	/** 刷新地图移动类型 */
	private void refreshMapMoveType()
	{
		for(int configI=0,configLen=mapMoveTypeDic.length;configI<configLen;++configI)
		{
			MapMoveTypeConfig config=mapMoveTypeDic[configI];
			if(config!=null)
				config.refresh();
		}
	}
	
	/** 读取拍卖行表 */
	protected void readAuction(BytesReadStream stream)
	{
		AuctionConfig config;
		int len=stream.readLen();
		auctionDic=new IntObjectMap<AuctionConfig>(AuctionConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new AuctionConfig();
			config.readBytesSimple(stream);
			auctionDic.put(config.id,config);
		}
	}
	
	/** 刷新拍卖行表 */
	private void refreshAuction()
	{
		if(!auctionDic.isEmpty())
		{
			Object[] configValues=auctionDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					AuctionConfig config=(AuctionConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取拍卖行查询条件类型 */
	protected void readAuctionQueryConditionType(BytesReadStream stream)
	{
		AuctionQueryConditionTypeConfig config;
		int len=stream.readLen();
		int size=stream.readLen();
		auctionQueryConditionTypeDic=new AuctionQueryConditionTypeConfig[size];
		for(int i=0;i<len;++i)
		{
			config=new AuctionQueryConditionTypeConfig();
			config.readBytesSimple(stream);
			auctionQueryConditionTypeDic[config.id]=config;
		}
	}
	
	/** 刷新拍卖行查询条件类型 */
	private void refreshAuctionQueryConditionType()
	{
		for(int configI=0,configLen=auctionQueryConditionTypeDic.length;configI<configLen;++configI)
		{
			AuctionQueryConditionTypeConfig config=auctionQueryConditionTypeDic[configI];
			if(config!=null)
				config.refresh();
		}
	}
	
	/** 读取队伍目标表 */
	protected void readTeamTarget(BytesReadStream stream)
	{
		TeamTargetConfig config;
		int len=stream.readLen();
		int size=stream.readLen();
		teamTargetDic=new TeamTargetConfig[size];
		for(int i=0;i<len;++i)
		{
			config=new TeamTargetConfig();
			config.readBytesSimple(stream);
			teamTargetDic[config.id]=config;
		}
	}
	
	/** 刷新队伍目标表 */
	private void refreshTeamTarget()
	{
		for(int configI=0,configLen=teamTargetDic.length;configI<configLen;++configI)
		{
			TeamTargetConfig config=teamTargetDic[configI];
			if(config!=null)
				config.refresh();
		}
	}
	
	/** 读取分段排行表 */
	protected void readSubsectionRank(BytesReadStream stream)
	{
		SubsectionRankConfig config;
		int len=stream.readLen();
		subsectionRankDic=new IntObjectMap<SubsectionRankConfig>(SubsectionRankConfig[]::new,len);
		for(int i=0;i<len;++i)
		{
			config=new SubsectionRankConfig();
			config.readBytesSimple(stream);
			subsectionRankDic.put(config.id,config);
		}
	}
	
	/** 刷新分段排行表 */
	private void refreshSubsectionRank()
	{
		if(!subsectionRankDic.isEmpty())
		{
			Object[] configValues=subsectionRankDic.getValues();
			for(int configI=configValues.length-1;configI>=0;--configI)
			{
				if(configValues[configI]!=null)
				{
					SubsectionRankConfig config=(SubsectionRankConfig)configValues[configI];
					config.refresh();
				}
			}
		}
	}
	
	/** 读取推送标签类型 */
	protected void readPushTopicType(BytesReadStream stream)
	{
		PushTopicTypeConfig config;
		int len=stream.readLen();
		int size=stream.readLen();
		pushTopicTypeDic=new PushTopicTypeConfig[size];
		for(int i=0;i<len;++i)
		{
			config=new PushTopicTypeConfig();
			config.readBytesSimple(stream);
			pushTopicTypeDic[config.id]=config;
		}
	}
	
	/** 刷新推送标签类型 */
	private void refreshPushTopicType()
	{
		for(int configI=0,configLen=pushTopicTypeDic.length;configI<configLen;++configI)
		{
			PushTopicTypeConfig config=pushTopicTypeDic[configI];
			if(config!=null)
				config.refresh();
		}
	}
	
}
