using System;
using System.Security;
using ShineEngine;

/// <summary>
/// 公共设置
/// </summary>
public class CommonSetting
{
	//--系统相关--//
	/** 是否启用reporter(默认关闭) */
	public static bool useReporter=true;
	/** 是否启用debug */
	public static bool useDebug=true;
	/** 本地存储是否使用unity原生 */
	public static bool localSaveUsePlayerPrefs=false;
	/** 是否需要trigger日志 */
	public static bool needTriggerLog=false;
	/** 离线数据保存间隔(2s) */
	public static int offlineSaveDelay=2000;
	/** trigger方法数目 */
	public static int triggerFunctionMax=2000;
	/** 客户端是否需要热更 */
	public static bool clientNeedHotfix=true;
	/** 是否开启推送功能 */
	public static bool openPushNotification=false;

	/** 业务逻辑是否使用池化（场景除外） */
	public static bool logicUsePool=true;
	/** 场景逻辑是否使用池化 */
	public static bool sceneLogicUsePool=true;
	/** trigger是否使用池化 */
	public static bool triggerUsePool=true;
	/** 显示逻辑是否使用池化 */
	public static bool viewUsePool=true;

	//--配置表--//
	/** 配置表是否启用压缩(基本不改了) */
	public static bool configNeedCompress=true;

	//--网络--//
	/** 客户端是否开启断线重连 */
	public static bool clientOpenReconnect=true;

	//--版本--//
	/** c层版本号(数据) */
	public static int cVersion=CVersionEnum.Version2;
	/** g层版本号(数据) */
	public static int gVersion=1;


	//--加载--//
	/** 是否需要给玩家更新必须资源的提示 */
	public static bool needResourceUpdateNotice=false;

	//--登陆--//
	/** 游客的uid是否与buildID绑定 */
	public static bool isVisitorUIDBindByBuildID=false;


	//--系统模块(业务)--//
	/** 语言类型 */
	public static int languageType=LanguageType.Zh_CN;
	/** 是否启用国际化 */
	public static bool useMultiLanguage=true;
	/** 是否启用离线游戏 */
	public static bool useOfflineGame=false;
	/** 是否单机游戏 */
    public static bool isSingleGame=false;
	/** 单服注册上限(1亿) */
	public static int areaRegistMax=100000000;
	/** 区服数上限(1万) */
	public static int areaMax=10000;

	//--场景--//
	/** 场景是否启用3D计算 */
	public static bool sceneCalculateUse2D=true;
	/** 场景编辑器使用序号上限(不可达到) */
	public static int sceneEditorIndexMax=20000;
	/** buff流水ID上限 */
	public static int buffInstanceIDMax=20000000;
	/** buff动作序号左移量(16) */
	public static int buffActionIndexOff=4;
	/** buff动作序号左移量(16) */
	public static int buffActionIndexMax=1<<buffActionIndexOff;
	/** 是否场景服务器驱动(默认) */
	public static bool isSceneServerDrive=true;
	/** 服务器场景驱动模式 */
	public static int sceneDriveType=SceneDriveType.ServerDriveMost;
	/** 是否由客户端驱动简版子弹在服务器启动的模式下(开此策略客户端可能会有急速子弹的挂,但是影响可忽略) */
	public static bool isClientDriveSimpleBulletForServerDriveMost=false;
	/** 是否有主城概念(离开当前场景是否一定回主城场景) */
	public static bool hasTown=true;
	/** buff数据缓存数目 */
	public static int buffDataCacheNum=32;
	/** 客户端朝向移动是否使用点预测(否则就是使用baseMoveDir) */
	public static bool clientMoveDirUseForecast=true;
	/** 查找地形默认高度 */
	public static float findTerrainHeight=10f;
	/** 各种射线检测最长距离 */
	public static float maxDistance=10000f;
	/** 是否开启移动差值(在每次移动的时候，用移动时间同步) */
	public static bool needMoveLerp=false;
	/** 摄像机移动时，是否保持上次朝向 */
	public static bool cameraCacheDir=false;
	/** 移动点组发送间隔(ms) */
	public static int moveListSendDelay=5000;//默认5s

	/** 服务器寻路是否需要第二格子 */
	public static bool serverMapNeedSecondGrid=true;
	/** 客户端是否需要grid数据 */
	public static bool clientMapNeedGrid=false;
	/** 服务器是否需要grid数据 */
	public static bool serverMapNeedGrid=false;
	/** 服务器寻路是否需要recast寻路 */
	public static bool serverMapNeedRecast=false;
	/** 客户端是否需要场景编辑器导出摆放数据 */
	public static bool clientNeedScenePlaceEditor=true;
	/** 服务器navmesh寻路是否需要obj辅助文件 */
	public static bool serverMapRecastNeedObjFile=true;

	//--玩法策略部分--//
	/** 是否分区服 */
	public static int areaDivideType=GameAreaDivideType.AutoBindGame;
	/** 是否3D项目 */
	public static bool is3D=true;
	/** 是否Z轴作为高度轴(否则就是y轴为高度轴) */
	public static bool isZHeight=false;
	/** 是否启用act部分 */
	public static bool useAct=true;
	/** 是否客户端驱动逻辑(客户端权威性逻辑)(离线模式需要同时开这个) */
	public static bool isClientDriveLogic=false;
	/** 朝向偏移(服务器0朝向为x正向,而客户端0朝向为z正向) */
	public static float rotationOff=MathUtils.fPI_half;
	/** 场景射线检测长度 */
	public static float sceneRayCastLength=100f;
	/** 是否字典背包(无格背包) */
	public static bool useDicBag=false;
	/** 是否需要客户端随机种子 */
	public static bool needClientRandomSeeds=false;
	/** 用户表是否需要多平台绑定支持 */
	public static bool UserNeedMultiPlatformBind=false;


	/** 设置启用离线游戏 */
	public static void setUseOfflineGame()
	{
		useOfflineGame=true;
		isClientDriveLogic=true;//同时修改
		needClientRandomSeeds=true;
	}

	//--显示部分--//

	/** 字符串替换标记 */
	public static char replaceTextMark='$';
	/** 字符串颜色替换标记 */
	public static char replaceTextColorMark='#';
	/** 字符串颜色替换长度 */
	public static int replaceTextColorLength=6;
	/** uiLogo背景名字 */
	public static string uiLogoName="Logo";
	/** 置灰Mat */
	public static string colorGrayMat = "";

	public static string scenePlaceEditor="scenePlaceEditor";
	public static string mapInfo="mapInfo";

	//--工具部分--//

	/** 系统名字 */
	public static string systemUserName="";
	/** 系统密码 */
	public static SecureString systemPassword;

	public static void init()
	{
		if(isSingleGame)
		{
			setUseOfflineGame();
		}
	}

	/** 是否是分服的 */
	public static bool isAreaSplit()
	{
		return areaDivideType==GameAreaDivideType.Split;
	}
}