package com.home.commonBase.global;

import com.home.commonBase.constlist.generate.LanguageType;
import com.home.commonBase.constlist.generate.PushNotifyPlatformType;
import com.home.commonBase.constlist.scene.PathFindingType;
import com.home.commonBase.constlist.system.CVersionEnum;
import com.home.commonBase.constlist.system.GameAreaDivideType;
import com.home.commonBase.constlist.system.SceneDriveType;

/** 游戏设置 */
public class CommonSetting
{
	//--系统相关--//
	/** 是否为客户端(机器人) */
	public static boolean isClient=false;
	/** 是否正式(无gm) */
	public static boolean isOfficial=false;
	/** 是否启用离线游戏 */
	public static boolean useOfflineGame=false;
	/** 是否需要trigger日志 */
	public static boolean needTriggerLog=false;
	/** 客户端是否需要热更 */
	public static boolean clientNeedHotfix=true;
	/** 是否开启url重定向 */
	public static boolean openRedirectURL=true;
	/** 客户端partData是否开启initDefault */
	public static boolean isClientPartDataInitDefault=true;
	/** 是否需要给客户端推送warningLog(调试用) */
	public static boolean needSendWarningLog=false;
	/** 是否需要外部库(JNI lib) */
	public static boolean needExternLib=false;
	
	//pool
	/** 业务逻辑是否使用池化（场景除外） */
	public static boolean logicUsePool=true;
	/** 场景逻辑是否使用池化 */
	public static boolean sceneLogicUsePool=true;
	/** 场景对象是否使用池 */
	public static boolean sceneUsePool=true;
	/** trigger是否使用池化 */
	public static boolean triggerUsePool=true;
	/** (角色/玩家群）是否使用池 */
	public static boolean playerUsePool=true;
	
	//--配置表--//
	/** 配置表是否启用压缩 */
	public static boolean configNeedCompress=true;
	/** 是否启用配置增量 */
	public static boolean needConfigIncrement=false;
	
	//--网络--//
	/** 客户端是否开启断线重连 */
	public static boolean clientOpenReconnect=false;
	/** 是否开启技能抵御网络延迟策略 */
	public static boolean openSkillPreventNetDelay=true;
	/** 客户端是否使用webSocket */
	public static boolean clientSocketUseWebSocket=false;
	/** 客户端http连接是否启用base64Encode */
	public static boolean clientHttpUseBase64=false;
	
	//--db--//
	/** 是否开启数据库写入超时检测 */
	public static boolean openDBWriteTimeOutCheck=true;
	/** 离线事务是否使用表执行 */
	public static boolean offlineWorkUseTable=true;
	/** 是否需要在创建角色后立刻存库(如出现用户表数据为空,可开启此策略) */
	public static boolean needSaveDBAfterCreateNewPlayer=false;
	/** 数据库写库间隔(s) */
	public static int dbWriteDelay=30;//30秒
	/** 数据库保持连接间隔(s) */
	public static int dbKeepDelay=10 * 60;//10分钟
	/** 一次DB写入最多花费时间 */
	public static int dbWriteOnceMaxTime=20;//20秒
	
	//--版本--//
	/** c层版本号(数据) */
	public static int cVersion=CVersionEnum.Version2;
	/** g层版本号(数据) */
	public static int gVersion=1;
	
	//--登录--//
	/** 客户端是否需要消息版本检查 */
	public static boolean needClientMessageVersionCheck=true;
	/** 角色离线检查间隔(s) */
	public static int playerOfflineCheckDelay=60;
	/** 角色离线保留时间(s)(从离线组到table层)(此时间减一个检查间隔的范围) */
	public static int playerOfflineKeepTime=2 * 60;
	/** 角色table保留时间(s)(从table层到db层)(此时间减一个检查间隔的范围) */
	public static int playerTableKeepTime=10 * 60;//10分钟
	/** 玩家社交数据保留时间(s)(从离线组到)(此时间减一个检查间隔的范围) */
	public static int roleSocialDataKeepTime=2 * 60;
	/** 清理角色最长等待时间(s) */
	public static int clearPlayerMaxDelay=20;//20秒
	/** 角色登录阶段允许时长(s) */
	public static int playerLoginTimeMax=300;//5分钟
	/** 是否启用重连登录(第二形态,当前角色不下线，直接重接socket) */
	public static boolean useReconnectLogin=true;
	/** 登录事务锁时间(s) */
	public static int loginAffairTime=10;
	/** 中心服账号登录最长时间(s) */
	public static int centerUserLoginMaxTime=3600;//1小时
	
	//--系统模块(业务)--//
	/** 语言类型(默认中文) */
	public static int languageType=LanguageType.Zh_CN;
	/** 是否启用国际化 */
	public static boolean useMultiLanguage=true;
	/** 是否需要记录步 */
	public static boolean needRecordStep=false;
	/** 逻辑帧间隔(毫秒) */
	public static int logicFrameDelay=33;
	/** 单服注册上限(1亿) */
	public static int areaRegistMax=100000000;
	/** 单服注册上限(9999万) */
	public static int areaRegistAllow=100000000-10000;
	/** 区服数上限(1万) */
	public static int areaMax=10000;
	/** 角色对象池数目 */
	public static int playerPoolSize=1000;
	/** 场景池尺寸 */
	public static int scenePoolSize=128;
	/** trigger方法数目 */
	public static int triggerFunctionMax=2000;
	/** 事务检查重放间隔(ms) */
	public static int workCheckReSendDelay=5000;//5秒
	/** 事务接收端超时时间(天) */
	public static int workReceiveTimeOut=7;//7天
	/** 客户端行为次数限制 */
	public static int clientNumMax=10000;
	
	/** 设置启用离线游戏 */
	public static void setUseOfflineGame()
	{
		useOfflineGame=true;
		needClientRandomSeeds=true;
	}
	
	/** 客户端协议是否需要功能开关限制 */
	public static boolean needClientResponseFunctionLimit=true;
	
	//--推送--//
	/** 是否开启推送功能 */
	public static boolean openPushNotification=false;
	/** 推送平台类型 */
	public static int pushNotifyPlatformType=PushNotifyPlatformType.Normal;
	/** 推送环境，是否为开发环境 */
	public static boolean pushNotifyDevelopEnvironment =true;
	/** 信鸽accessId */
	public static long xingeAccessID=-1;
	/** 信鸽SecretKey */
	public static String xingeSecretKey="";
	/** 信鸽accessId android */
	public static long xingeAndroidAccessID=-1;
	/** 信鸽SecretKey android*/
	public static String xingeAndroidSecretKey="";
	/** firebase推送数据url */
	public static String firebaseDatabaseUrl="";
	//--场景--//
	/** 场景是否启用2D计算 */
	public static boolean sceneCalculateUse2D=true;
	/** 场景编辑器使用序号上限(不可达到) */
	public static int sceneEditorIndexMax=20000;
	/** buff流水ID上限 */
	public static int buffInstanceIDMax=20000000;
	/** buff动作序号左移量(16) */
	public static int buffActionIndexOff=4;
	/** buff动作序号左移量(16) */
	public static int buffActionIndexMax=1<<buffActionIndexOff;
	/** 技能步骤,通过技能指令切换的方式，额外保留时间(ms) */
	public static int skillStepSwitchSkillCommandKeepTime=200;
	/** 场景自动分线极限(用奇数) */
	public static int sceneAutoLineMax=1001;
	/** 是否开启延迟抵御 */
	public static boolean openLagDefence=true;
	/** 场景延迟抵御时间上限(ms) */
	public static int lagDefenceMaxTime=200;
	/** 伤害是否只广播自身和目标 */
	public static boolean isDamageOnlyRadioSelfAndTarget=false;
	
	/** 是否场景服务器驱动(默认) */
	public static boolean isSceneServerDrive=true;
	/** 服务器场景驱动模式(默认) */
	public static int sceneDriveType=SceneDriveType.ServerDriveMost;
	/** 是否由客户端驱动简版子弹在服务器启动的模式下(开此策略客户端可能会有急速子弹的挂,但是影响可忽略) */
	public static boolean isClientDriveSimpleBulletForServerDriveMost=false;
	/** 是否使用独立场景服务器 */
	public static boolean useSceneServer=false;
	
	/** 是否有主城概念(离开当前场景是否一定回主城场景) */
	public static boolean hasTown=true;
	/** buff数据缓存数目 */
	public static int buffDataCacheNum=32;
	
	/** 切换场景最长等待时间(s) */
	public static int enterSceneMaxWaitTime=20;//20秒
	/** 是否改变时立即刷新AOI */
	public static boolean refreshAOIAbs=true;
	/** 是否开启AOI检测 */
	public static boolean openAOICheck=false;
	/** 怪物复活是否启用新instanceID */
	public static boolean monsterReviveUseNewInstanceID=false;
	/** 是否同步社交数据的场景位置部分 */
	public static boolean needSyncRoleSocialSceneLocation=false;
	
	/** 是否需要立即计算其他单位光环buff影响，在大幅位置变更后 */
	public static boolean needCalculateRingLightBuffAbs=false;
	/** 是否开启移动差值(在每次移动的时候，用移动时间同步) */
	public static boolean needMoveLerp=false;

	/** 怪物是否需要格子拥挤 */
	public static boolean monsterNeedCrowedGrid=false;
	/** 是否需要重置到可用格子点 */
	public static boolean needResetBlockGrid=true;
	/** 是否需要怪物AI激活策略 */
	public static boolean needMonsterAIActive=true;
	/** 怪物AI激活超时时间(s) */
	public static int monsterAIActiveTime=10;
	/** 格子重置位置半径 */
	public static int gridResetPosLen=20;
	/** 格子重置位置半径环尺寸 */
	public static float gridResetPosCircleLen=1f;
	/** 客户端移动朝向单次最长时间 */
	public static int clientMoveDirTimeMax=5000;
	
	//--玩法策略部分--//
	/** 是否分区服 */
	public static int areaDivideType=GameAreaDivideType.AutoBindGame;
	/** 是否启用按国家划分区服 */
	public static boolean useCountryArea=false;
	/** 大区同服下，是否开启玩家满后自动转到其他服的策略 */
	public static boolean needPlayerFullTransToOtherGame=true;
	/** 是否3D项目 */
	public static boolean is3D=true;
	/** 是否Z轴作为高度轴(否则就是y轴为高度轴) */
	public static boolean isZHeight=false;
	/** 是否需要客户端随机种子 */
	public static boolean needClientRandomSeeds=false;
	/** 是否需要客户端随机种子重置(用完) */
	public static boolean needClientRandomSeedsReset=false;
	/** 是否字典背包(无格背包) */
	public static boolean useDicBag=false;

	/** 服务器寻路是否需要第二格子 */
	public static boolean serverMapNeedSecondGrid=true;
	/** 服务器寻路是否需要grid寻路 */
	public static boolean serverMapNeedGrid=false;
	/** 服务器寻路是否需要recast寻路 */
	public static boolean serverMapNeedRecast=false;
	/** 寻路方案 */
	public static int pathFindingType=PathFindingType.None;
	/** 是否启用连通区计算 */
	public static boolean usePathAreaDic=true;
	/** 用户表是否需要多平台绑定支持 */
	public static boolean UserNeedMultiPlatformBind=false;
	
	//--玩法参数部分--//
	/** 是否为测试跨服场景 */
	public static boolean isTestCenterTown=false;
	/** 角色社交数据是否实时刷新 */
	public static boolean needUpdateSocialDataImmediately=false;
	/** 是否客户端驱动逻辑 */
	public static boolean isClientDriveLogic=false;
	/** 是否支持平台自动绑定游客 */
	public static boolean canPlatformAutoBind=false;
	
	/** 初始化 */
	public static void init()
	{
	
	}
	
	/** 是否是分服的 */
	public static boolean isAreaSplit()
	{
		return areaDivideType==GameAreaDivideType.Split;
	}
	
	//test
	
	public static boolean isTestPlayerFullTransToOtherGame=false;
}
