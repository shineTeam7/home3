namespace Shine
{
	/** 引擎设置 */
	export class ShineSetting
	{
	    //--系统相关--//
		/** 是否在Editor工程(以后可以替换Application.isPlaying) */
		public static isEditor:boolean=false;
		/** 是否发布模式(不是本地调试运行)(打出手机包) */
		public static isRelease:boolean=false;
		/** 是否正式线上 */
		public static isOfficial:boolean=false;
		/** 是否需要跑错 */
		public static needError:boolean=true;
		/** 是否启用对象池 */
		public static useObjectPool:boolean=true;
		/** 是否开启问题检测 */
		public static openCheck:boolean=true;
		/** 默认池大小(客户端的要小一些) */
		public static defaultPoolSize:number=256;
		/** 系统时区 */
		public static timeZone:string="UTC";

		//--日志相关--//
		/** 是否需要调试日志 */
		public static needDebugLog:boolean=true;
		/** 日志是否需要控制台输出 */
		public static logNeedConsole:boolean=true;
		/** 是否需要data日志在一行 */
		public static needDataStringOneLine:boolean=false;

		//--配置相关--//
		/** 是否启用配置增量 */
		public static needConfigIncrement:boolean=false;

		//--网络相关--//
		/** Bytes中的Boolean读写是否使用位存储(未实现) */
		public static isBytesBooleanUseBit:boolean=false;
		/** 是否需要协议存在性检查 */
		public static needMessageExistCheck:boolean=true;
		/** 是否启用自定义(变长协议长度解析)(否则就是定长解析) */
		public static needCustomLengthBasedFrameDecoder:boolean=true;
		/** 是否显示消息详细内容 */
		public static needShowMessageDetail:boolean=false;
		/** 是否显示来往协议 */
		public static needShowMessage:boolean=false;
		/** 客户端是否缓存断线期间消息 */
		public static needCacheDisconnectRequest:boolean=true;
		/** 是否开启ping包断线 */
		public static needPingCut:boolean=true;
		/** socket通信是否使用UDP协议 */
		public static socketUseUDP:boolean=false;
		/** 客户端消息是否使用full兼容 */
		public static clientMessageUseFull:boolean=false;
		/** 是否使用ipv6地址 */
		public static useInterNetworkV6 :boolean= false;
		/** 二进制http头 */
		public static bytesHttpCmd:string="b";
		/** socket发送buf大小 */
		public static socketSendBufSize:number=1024 * 4;
		/** socket接受buf大小 */
		public static socketReceiveBufSize:number=1024 * 8;
		/** socket连接间隔时间(s) */
		public static socketConnectDelay:number=3;
		/** 收包缓冲区大小(最大单包) */
		public static msgBufSize:number=1024 * 8;
		/** 消息最大尺寸(4M) */
		public static msgBufSizeMax:number=1024 * 1024 * 4;
		/** 断线重连缓存推送上限(客户端) */
		public static cutCacheRequestMaxClient:number=200;
		/** ping包时间(s)(10秒) */
		public static pingTime:number=3;
		/** 心跳保留时间(s)(30秒) */
		public static pingKeepTime:number=30;
		/** udp包缓存数目 */
		public static udpPacketSingleLength:number=512;
		/** udp包缓存数目 */
		public static udpPackCacheLength:number=256;
		/** udp包缓存数目 */
		public static udpPackCacheLengthMark:number=255;
		/** udp包缓存数目一半 */
		public static udpPackCacheLengthHalf:number=128;
		/** 大端写入 */
		public static endian:string=Laya.Byte.BIG_ENDIAN;

		/** 断线重连保留时间(s) */
		public static socketReConnectKeepTime:number=10;//10秒
		/** 消息包缓存数目(客户端用) */
		public static requestCacheLen:number=256;
		/** 消息包缓存标记(客户端用) */
		public static requestCacheMark:number=255;

		/** 客户端通信采用base64Encode */
		public static clientUseBase64:boolean = true;
		/** 客户端webSocket是否使用wss */
		public static clientWebSocketUseWSS:boolean=false;

		/** 获取消息长度限制 */
		public static getMsgBufSize(isLongMessage:boolean):number
		{
			if(isLongMessage)
			{
				return this.msgBufSizeMax;
			}

			return this.msgBufSize;
		}

		//--线程/时间相关--//
		/** 系统帧率(各种系统计时器用) */
		public static systemFPS:number=30;
		/** 系统间隔(毫秒)(各种系统计时器用) */
		public static systemFrameDelay:number=33;
		/** 系统每份时间(1秒5次,逻辑用) */
		public static pieceTime:number=200;
		/** 默认线程sleep间隔(毫秒) */
		public static defaultThreadSleepDelay:number=5;
		/** 日期时间戳更新间隔(毫秒) */
		public static dateFixDelay:number=500;

		//--业务相关--//
		/** data构造上限值 */
		public static dataMaxNum:number=15000;
		/** 最大序号值(20亿) */
		public static indexMax:number=2000000000;
		/** 最大序号值一半(10亿) */
		public static indexMaxHalf:number=1000000000;
		/** io线程帧间隔(毫秒) */
		public static ioThreadFrameDelay:number=5;
		/** 事务默认执行时间(s) */
		public static affairDefaultExecuteTime:number=10;//10秒



		//--加载相关--//
		/** 是否启用加载过的资源优先策略 */
		public static useFastLoadedResource:boolean=false;
		/** bundle包是否全解析(补丁) */
		public static bundleAnalysisAll:boolean=false;
		/** 是否启用热更 */
		public static useHotFix:boolean=false;
		/** 是否使用反射加载dll */
		public static useReflectDll:boolean=false;
		/** 是否使用unityWebRequest替换掉www */
		public static useUnityWebRequestInsteadOfWWW:boolean=false;
		/** 是否跳过版本控制阶段 */
		public static debugJumpResourceVersion:boolean=false;
		/** 是否需要本地调试版本更新(开启以后，才可本地模拟资源加载，缓存等) */
		public static needDebugResourceVersion:boolean=false;
		/** loader超时检测间隔 */
		public static loaderTimeOutCheckDelay:number=10 * 1000;
		/** 资源引用检查间隔(s) */
		public static resourceKeepCheckDelay:number=5;
		/** 资源无引用后保留多久(s)(0为无限) */
		public static resourceKeepTime:number=60;
		/** ui析构保留时间(s)(0为立刻析构,-1为永不析构) */
		public static uiDisposeKeepTime:number=60;
		/** loader回调是否异步 */
		public static loaderCallbackAsync:boolean=false;
		/** 客户端资源自定义序号起始 */
		public static resourceCustomIndex:number=8000;
		/** 本地加载是否不使用bundle */
		public static localLoadWithOutBundle:boolean=false;

		//--路径相关--//
		/** bundle资源头 */
		public static bundleSourceHead:string="assets/source/";
		/** 游戏名字(程序名) */
		public static gameName:string="game";
		/** 场景主对象名字 */
		public static mainObjectName:string="Main";
		/** ui根名字 */
		public static uiRootName:string="UIRoot";
		/** ui摄像机名字 */
		public static uiCameraName:string="UICamera";
		/** 池根名字 */
		public static poolRootName:string="PoolRoot";
		/** ui渲染的场景特效层 */
		public static uiSceneEffectLayer:string="SceneEffectLayer";
		/** 模态对话框背景名字 */
		public static modalDialogBackground:string="modelDialogBackground";
		/** 根场景名字 */
		public static rootSceneName:string="root";

		//--UI相关--//
		/** 点击间隔时间(ms) */
		public static clickTime:number=500;
		/** 点击位置x+y偏移 */
		public static clickOffSum:number=5;
		/** 按钮长按首次反馈延迟(ms) */
		public static buttonLongPressFirstDelay :number= 800;
		/** 按钮长按反馈间隔(ms) */
		public static buttonLongPressInterval :number= 150;
	}
}