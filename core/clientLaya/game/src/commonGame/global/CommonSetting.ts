namespace Shine
{
	/** 公共配置 */
	export class CommonSetting
	{
	    //--系统相关--//
		/** 是否启用reporter */
		public static useReporter:boolean=false;
		/** 是否启用debug */
		public static useDebug:boolean=true;
		/** 本地存储是否使用unity原生 */
		public static localSaveUsePlayerPrefs:boolean=false;
		/** 是否需要trigger日志 */
		public static needTriggerLog:boolean=false;
		/** 离线数据保存间隔(2s) */
		public static offlineSaveDelay:number=2000;
		/** trigger方法数目 */
		public static triggerFunctionMax:number=2000;
		/** 客户端是否需要热更 */
		public static clientNeedHotfix:boolean=true;

		//--配置表--//
		/** 配置表是否启用压缩(基本不改了) */
		public static configNeedCompress:boolean=true;

		//--网络--//
		/** 客户端是否开启断线重连 */
		public static clientOpenReconnect:boolean=true;
		/** 客户端是否启用base64Encode */
		public static clientUseBase64:boolean=false;

		//--版本--//
		/** c层版本号(数据) */
		public static cVersion:number=CVersionEnum.Version1;
		/** g层版本号(数据) */
		public static gVersion:number=1;


		//--加载--//
		/** 是否需要给玩家更新必须资源的提示 */
		public static needResourceUpdateNotice:boolean=false;

		//--登陆--//


		//--系统模块(业务)--//
		/** 语言类型 */
		public static languageType:number=1;//LanguageType.Zh_CN
		/** 是否启用国际化 */
		public static useMultiLanguage:boolean=true;
		/** 是否启用离线游戏 */
		public static useOfflineGame:boolean=false;

		//--场景--//
		/** buff流水ID上限 */
		public static buffInstanceIDMax:number=20000000;
		/** buff动作序号左移量(16) */
		public static buffActionIndexOff:number=4;
		/** buff动作序号左移量(16) */
		public static buffActionIndexMax:number=16;
		/** 是否场景服务器驱动(默认) */
		public static isSceneServerDrive:boolean=true;
		/** 服务器场景驱动模式 */
		public static sceneDriveType:number=SceneDriveType.ServerDriveMost;
		/** 是否由客户端驱动简版子弹在服务器启动的模式下(开此策略客户端可能会有急速子弹的挂,但是影响可忽略) */
		public static isClientDriveSimpleBulletForServerDriveMost:boolean=false;
		/** 是否有主城概念(离开当前场景是否一定回主城场景) */
		public static hasTown:boolean=true;
		/** buff数据缓存数目 */
		public static buffDataCacheNum:number=32;

		/** 设置启用离线游戏 */
		public static setUseOfflineGame():void
		{
			this.useOfflineGame=true;
			this.isClientDriveLogic=true;//同时修改
		}

		//--玩法策略部分--//
		/** 是否分区服 */
		public static areaDivideType:number=2;//GameAreaDivideType.AutoBindGame;

		/** 是否3D项目 */
		public static is3D:boolean=true;
		/** 是否Z轴作为高度轴(否则就是y轴为高度轴) */
		public static isZHeight:boolean=false;
		/** 是否启用act部分 */
		public static useAct:boolean=true;
		/** 是否客户端驱动逻辑(客户端权威性逻辑)(离线模式需要同时开这个) */
		public static isClientDriveLogic:boolean=false;
		/** 朝向偏移(服务器0朝向为x正向,而客户端0朝向为z正向) */
		public static rotationOff:number=MathUtils.fPI_half;
		/** 场景射线检测长度 */
		public static sceneRayCastLength:number=50;
		/** 是否字典背包(无格背包) */
		public static useDicBag:boolean=false;


		//--显示部分--//

		/** 字符串替换标记 */
		public static replaceTextMark:string="$";
		/** 字符串颜色替换标记 */
		public static replaceTextColorMark:string="#";
		/** 字符串颜色替换长度 */
		public static replaceTextColorLength:number=6;
		/** uiLogo背景名字 */
		public static uiLogoName:string="Logo";
	    
	}
}