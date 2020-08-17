using UnityEngine;

namespace ShineEngine
{
	/// <summary>
	/// 全局配置
	/// </summary>
	public class ShineSetting
	{
		//--系统相关--//
		/** 是否在Editor工程 */
		public static bool isEditor=false;
		/** 是否发布模式(不是本地调试运行)(打出手机包) */
		public static bool isRelease=false;
		/** 是否正式线上 */
		public static bool isOfficial=false;
		/** 是否是完整客户端，否则只有数据，配置，协议等非显示内容。 */
		public static bool isWholeClient=true;
		/** 是否需要跑错 */
		public static bool needError=true;
		/** 是否启用对象池 */
		public static bool useObjectPool=true;
		/** 是否开启问题检测 */
		public static bool openCheck=true;
		/** 是否需要GM指令界面 */
		public static bool needGMCommandUI=true;
		/** 默认池大小(客户端的要小一些) */
		public static int defaultPoolSize=128;
		/** 系统时区 */
		public static string timeZone="Asia/Shanghai";
		/** stringBuilder池保存尺寸上限 */
		public static int stringBuilderPoolKeepSize=512;
		/** stringBuilder池保存尺寸上限 */
		public static int bytesWriteStreamPoolKeepSize=512;
		/** 通信消息是否池化(客户端默认启用池化) */
		public static bool messageUsePool=true;

		//--日志相关--//
		/** 是否需要日志(全部,包括print) */
		public static bool needLog=true;
		/** 是否需要调试日志 */
		public static bool needDebugLog=true;
		/** 日志是否需要控制台输出 */
		public static bool logNeedConsole=true;
		/** 控制台输出是否需要时间戳 */
		public static bool consoleNeedTimestamp=true;
		/** 日志是否需要发送到服务器 */
		public static bool needLogSendServer=false;
		/** 是否需要data日志在一行 */
		public static bool needDataStringOneLine=false;

		//--配置相关--//
		/** 是否启用配置增量 */
		public static bool needConfigIncrement=false;

		//--网络相关--//
		/** Bytes中的Boolean读写是否使用位存储(未实现) */
		public static bool isBytesBooleanUseBit=false;
		/** 是否需要协议存在性检查 */
		public static bool needMessageExistCheck=true;
		/** 是否启用自定义(变长协议长度解析)(否则就是定长解析) */
		public static bool needCustomLengthBasedFrameDecoder=true;
		/** 是否显示消息详细内容 */
		public static bool needShowMessageDetail=true;
		/** 是否显示来往协议 */
		public static bool needShowMessage=false;
		/** 客户端是否缓存断线期间消息 */
		public static bool needCacheDisconnectRequest=true;
		/** 是否开启ping包断线 */
		public static bool needPingCut=true;
		/** socket通信是否使用UDP协议 */
		public static bool socketUseUDP=false;
		/** socket通信是否异步方式(现在默认启用同步) */
		public static bool socketUseAsync=false;
		/** 客户端消息是否使用full兼容 */
		public static bool clientMessageUseFull=false;
		/** 是否需要字节读写超限的时候报错 */
		public static bool needBytesLenLimitError=true;
		/** 是否使用kcp通信 */
		public static bool useKCP=false;
		/** 字节流序列化是否启动bitBoolean(c++中强制不使用) */
		public static bool bytesUseBitBoolean=false;

		/** 二进制http头 */
		public static string bytesHttpCmd="b";
		/** socket发送buf大小 */
		public static int socketSendBufSize=1024 * 4;
		/** socket接受buf大小 */
		public static int socketReceiveBufSize=1024 * 8;
		/** socket连接间隔时间(s) */
		public static int socketConnectDelay=3;
		/** 收包缓冲区大小(最大单包) */
		public static int msgBufSize=1024 * 8;
		/** 消息最大尺寸(4M) */
		public static int msgBufSizeMax=1024 * 1024 * 4;
		/** kcp窗口大小 */
		public static int kcpWndSize=64;
		/** kcp mtu */
		public static int kcpMTU=1024;

		/** ping包时间(s)(3秒)(客户端到服务器) */
		public static int pingTime=3;
		/** 心跳保留时间(s)(30秒) */
		public static int pingKeepTime=30;
		/** udp包缓存数目 */
		public const int udpPacketSingleLength=512;
		/** udp包缓存数目 */
		public const int udpPackCacheLength=256;
		/** udp包缓存数目 */
		public const int udpPackCacheLengthMark=udpPackCacheLength-1;
		/** udp包缓存数目一半 */
		public const int udpPackCacheLengthHalf=udpPackCacheLength/2;

		/** 断线重连保留时间(s) */
		public static int socketReConnectKeepTime=10;//10秒
		/** 消息包缓存数目(客户端用) */
		public static int requestCacheLen=256;
		/** 消息包缓存标记(客户端用) */
		public static int requestCacheMark=requestCacheLen-1;
		/** 消息包缓存时间(ms)(客户端用) */
		public static int requestCacheTime=5000;
		
		/** wifi下是否需要弹出下载提示 */
		public static bool needAlertInWifiMode = false;


		/** 获取消息长度限制 */
		public static int getMsgBufSize(bool isLongMessage)
		{
			if(isLongMessage)
			{
				return msgBufSizeMax;
			}

			return msgBufSize;
		}

		//--线程/时间相关--//
		/** 系统帧率(各种系统计时器用) */
		public static int systemFPS=30;
		/** 系统间隔(毫秒)(各种系统计时器用) */
		public static int systemFrameDelay=1000/systemFPS;
		/** 系统每份时间(1秒10次,逻辑用) */
		public static int pieceTime=100;
		/** 1秒3次时间 */
		public static int threeTime=300;
		/** 默认线程sleep间隔(毫秒) */
		public static int defaultThreadSleepDelay=5;
		/** 日期时间戳更新间隔(毫秒) */
		public static int dateFixDelay=500;
		/** 是否需要线程func唤醒 */
		public static bool needThreadNotify=false;

		//--业务相关--//
		/** 最大序号值(20亿) */
		public static int indexMax=2000000000;
		/** 最大序号值一半(10亿) */
		public static int indexMaxHalf=indexMax/2;
		/** 事务默认执行时间(s) */
		public static int affairDefaultExecuteTime=10;//10秒
		/** cron表达式prev时间数 */
		public static long cronExpressionPrevTime=1000*60*60*24;//1天
		/** cron表达式prev时间轮数 */
		public static long cronExpressionPrevTimeRound=6;//32天


		//--加载相关--//
		/** 是否启用加载过的资源优先策略 */
		public static bool useFastLoadedResource=false;
		/** bundle包是否全解析(补丁) */
		public static bool bundleAnalysisAll=false;
		/** 是否启用热更 */
		public static bool useHotFix=false;
		/** 是否使用反射加载dll */
		public static bool useReflectDll=false;
		/** 是否使用unityWebRequest替换掉www */
		public static bool useUnityWebRequestInsteadOfWWW=false;
		/** 是否跳过版本控制阶段 */
		public static bool debugJumpResourceVersion=false;
		/** 是否需要本地调试版本更新(开启以后，才可本地模拟资源加载，缓存等) */
		public static bool needDebugResourceVersion=false;
		/** loader超时检测间隔 */
		public static int loaderTimeOutCheckDelay=10 * 1000;
		/** 资源引用检查间隔(s) */
		public static int resourceKeepCheckDelay=5;
		/** 资源无引用后保留多久(s)(0为无限) */
		public static int resourceKeepTime=60;
		/** ui析构保留时间(s)(0为立刻析构,-1为永不析构) */
		public static int uiDisposeKeepTime=60;
		/** loader回调是否异步 */
		public static bool loaderCallbackAsync=false;
		/** 客户端资源自增序号起始(百万) */
		public static int resourceAutoIndex=1000000;

		/** 本地加载是否不使用bundle(直接用AssetDatabase.Load) */
		public static bool localLoadWithOutBundle=false;
		/** 新包下载，是否启用覆盖 */
		public static bool newPackNeedCover=false;
		/** loadControl默认资源大小大小 */
		public static int resourceDefaultSize = 1024;

		//--路径相关--//
		/** 游戏名字(程序名) */
		public static string gameName="game";
		/** 场景主对象名字 */
		public static string mainObjectName="Main";
		/** ui容器根名字 */
		public static string uiContainerName="UIContainer";
		/** ui根名字 */
		public static string uiRootName="UIRoot";
		/** ui摄像机名字 */
		public static string uiCameraName="UICamera";
		/** 池根名字 */
		public static string poolRootName="PoolRoot";
		/** ui渲染的场景特效层 */
		public static string uiSceneEffectLayer="SceneEffectLayer";
		/** 根场景名字 */
		public static string rootSceneName="root";
		/** uimask名字 */
		public static string uiMaskName="UIMask";
		/** uitouchmask名字 */
		public static string uiTouchMaskName="UITouchMask";

		//--UI相关--//
		/** ui是否需要重置位置 */
		public static bool needUIResize=false;
		/** 点击间隔时间(ms) */
		public static int clickTime=500;
		/** 点击位置x+y偏移 */
		public static float clickOffSum=5f;
		/** 按钮长按首次反馈延迟(ms) */
		public static int buttonLongPressFirstDelay = 800;
		/** 按钮长按反馈间隔(ms) */
		public static int buttonLongPressInterval = 150;
		
		/** 是否启用指定UI元素生成 */
		public static bool useUIElementExport=true;
		/** 是否开启bundle循环检测 */
		public static bool openBundleCheckLoop=false;
		
		//--工具相关--//

		/** 本地开启无bundle运行 */
		public static void setLocalLoadWithOutBundle()
		{
			localLoadWithOutBundle=true;
			debugJumpResourceVersion=true;
		}
	}
}