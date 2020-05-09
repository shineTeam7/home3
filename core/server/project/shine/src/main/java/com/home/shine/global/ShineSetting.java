package com.home.shine.global;

/** 全局配置 */
public class ShineSetting
{
	//--系统相关--//
	/** 是否发布模式(不是本地调试运行) */
	public static boolean isRelease=false;
	/** 是否合一进程 */
	public static boolean isAllInOne=false;
	/** 是否db升级过程 */
	public static boolean isDBUpdate=false;
	/** 是否需要抛错 */
	public static boolean needError=true;
	/** 报错后结束程序(Editor用) */
	public static boolean needExitAfterError=false;
	/** 是否开启问题检测 */
	public static boolean openCheck=true;
	/** 是否启用对象池 */
	public static boolean useObjectPool=true;
	/** 默认池大小 */
	public static int defaultPoolSize=128;
	/** 系统时区(默认东八区,中国时间) */
	public static String timeZone="GMT+8:00";
	/** stringBuilder池保存尺寸上限 */
	public static int stringBuilderPoolKeepSize=512;
	/** stringBuilder池保存尺寸上限 */
	public static int bytesWriteStreamPoolKeepSize=512;
	/** stringBuilderLog保存尺寸上限 */
	public static int logStringBuilderKeepSize=1024*64;
	/** 通信消息是否池化(服务器先关，还未完全做好) */
	public static boolean messageUsePool=false;
	/** 消息缓存回池间隔 */
	public static int messageCacheFlushDelay=500;
	/** ac字符串匹配是否保留英文字完整 */
	public static boolean acStringFilterKeepWholeWords=false;
	
	//--日志相关--//
	/** 是否需要日志(各类) */
	public static boolean needLog=true;
	/** 是否需要调试日志 */
	public static boolean needDebugLog=false;
	/** 是否需要细节日志 */
	public static boolean needDetailLog=true;
	/** 各类log是否需要独立目录 */
	public static boolean logNeedDir=false;
	/** 打印是否使用当前线程输出 */
	public static boolean printUseCurrentThread=false;
	/** 日志是否需要控制台输出 */
	public static boolean logNeedConsole=true;
	/** 控制台输出是否需要时间戳 */
	public static boolean consoleNeedTimestamp=true;
	/** 日志输出间隔(毫秒) */
	public static int logFlushDelay=1000;
	/** 是否需要data日志在一行 */
	public static boolean needDataStringOneLine=false;
	/** 是否需要输出调用代码行号 */
	public static boolean needLogLineNumber=true;
	/** 输出调用代码行号时是否需要包名 */
	public static boolean isLogLineNeedPackage=false;
	
	//--db相关--//
	/** sql语句是否加;结尾 */
	public static boolean sqlUseEnd=false;
	/** 是否启用dbcp连接池 */
	public static boolean useDBCP=true;
	/** dbTask是否需要失败重试 */
	public static boolean dbTaskNeedRetry=false;
	/** 数据库表最大数目(100应该够用) */
	public static int tableMaxNum=100;
	/** 每个失败的db任务,最大尝试时间(ms) */
	public static int dbTaskMaxTryTime=1000*30;//30ms
	/** mysql最大允许单包大小(默认4M，我们用3M) */
	public static int mysqlMaxAllowedPacket=3*1024*1024;
	
	//--网络相关--//
	/** 客户端消息是否使用full兼容 */
	public static boolean clientMessageUseFull=false;
	/** 是否显示消息详细内容 */
	public static boolean needShowMessageDetail=true;
	/** 是否显示客户端消息 */
	public static boolean needShowClientMessage=true;
	/** 是否显示来往协议(包含客户端部分) */
	public static boolean needShowMessage=false;
	/** 是否开启广播消息逻辑线程序列化 */
	public static boolean openRadioMessageWrite=true;
	/** Bytes中的Boolean读写是否使用位存储(未实现) */
	public static boolean isBytesBooleanUseBit=false;
	/** 是否需要协议存在性检查 */
	public static boolean needMessageExistCheck=true;
	/** 是否需要网络流量观测 */
	public static boolean needNetFlowWatch=false;
	/** 是否开启ping包断线 */
	public static boolean needPingCut=true;
	/** 是否启用自定义(变长协议长度解析)(否则就是定长解析) */
	public static boolean needCustomLengthBasedFrameDecoder=true;
	/** 是否需要字节读写超限的时候报错 */
	public static boolean needBytesLenLimitError=true;
	/** 是否使用kcp通信 */
	public static boolean useKCP=false;
	/** 客户端http连接是否启用https */
	public static boolean clientHttpUseHttps=false;
	/** 客户端http连接是否启用base64Encode */
	public static boolean clientHttpUseBase64=false;
	/** 客户端webSocket是否使用wss */
	public static boolean clientWebSocketUseWSS=false;
	/** ssl是否使用自签名 */
	public static boolean sslUseSelfSigned=true;
	/** 字节流序列化是否启动bitBoolean */
	public static boolean bytesUseBitBoolean=false;
	
	/** 二进制http标示 */
	public static String bytesHttpCmd="b";
	/** 网络消息统计数上限 */
	public static int netMessageCountMax=20;
	/** 网络包头长度(TCP+IP) */
	public static final int netTCPPacketHeadLength=40;
	/** 网络包头长度(UDP+IP) */
	public static final int netUDPPacketHeadLength=28;
	/** 广播消息默认流长 */
	public static int radioMessageDefaultLength=16;
	/** udp网络包单包上限 */
	public static final int udpPacketSingleLength=512;
	/** udp包缓存数目 */
	public static final int udpPackCacheLength=256;
	/** udp包缓存数目 */
	public static final int udpPackCacheLengthMark=udpPackCacheLength-1;
	/** udp包缓存数目一半 */
	public static final int udpPackCacheLengthHalf=udpPackCacheLength/2;
	/** http连接重试次数 */
	public static int httpSendTryTimes=1;
	/** ping包时间(s)(10秒)(服务器间) */
	public static int pingTime=10;
	/** 心跳保留时间(s)(30秒) */
	public static int pingKeepTimeClient=30;
	/** 心跳保留时间(s)(120秒) */
	public static int pingKeepTimeServer=30;//120
	
	/** netty缓冲连接数 */
	public static int nettyBackLog=1024 * 2;
	/** 客户端发http缓冲区大小(netty) */
	public static int clientHttpSendBuf=1024 * 2;
	/** 客户端收http缓冲区大小(netty) */
	public static int clientHttpReceiveBuf=1024 * 4;
	/** 服务器发http缓冲区大小(netty) */
	public static int serverHttpSendBuf=1024 * 4;
	/** 服务器收http缓冲区大小(netty) */
	public static int serverHttpReceiveBuf=1024 * 8;
	
	/** 客户端发缓冲区大小(netty) */
	public static int clientSocketSendBuf=1024 * 8;
	/** 客户端收缓冲区大小(netty) */
	public static int clientSocketReceiveBuf=1024 * 4;
	/** 服务器缓冲区大小(双向)(netty) */
	public static int serverSocketBuf=1024 * 64;
	
	
	/** 客户端收消息缓冲大小(最大单包) */
	public static int clientMsgBufSize=1024 * 4;
	/** 服务器收消息缓冲大小(最大单包) */
	public static int serverMsgBufSize=1024 * 32;
	/** 双端消息流长度上限(4m) */
	public static int msgBufSizeMax=1024 * 1024 * 4;
	/** 客户端buffer是否可扩容 */
	public static boolean needClientBufGrow=false;
	/** kcp缓存Segment尺寸 */
	public static int kcpWndSize=64;
	/** kcp mtu */
	public static int kcpMTU=1024;
	
	/** 断线重连保留时间(s) */
	public static int socketReConnectKeepTime=10;//10秒
	/** 消息包缓存数目(客户端用) */
	public static int requestCacheLen=256;
	/** 消息包缓存标记(客户端用) */
	public static int requestCacheMark=requestCacheLen-1;
	/** 消息包缓存时间(客户端用) */
	public static int requestCacheTime=5;
	
	/** http消息检查超时间隔 */
	public static int httpRequestCheckDelay=2000;
	/** http消息保留时间(s) */
	public static int httpRequestKeepTime=15000;
	/** http消息单包最长限制 */
	public static int httpBufferSizeMax=65536;
	
	/** 获取消息长度限制 */
	public static int getMsgBufSize(boolean isClient,boolean isLongMessage)
	{
		if(isLongMessage)
		{
			return msgBufSizeMax;
		}
		
		return isClient ? clientMsgBufSize : serverMsgBufSize;
	}
	
	//--线程/时间相关--//
	/** 是否需要线程死循环检测 */
	public static boolean needThreadDeadCheck=false;
	/** 是否需要线程观测 */
	public static boolean needThreadWatch=true;
	/** netty工作线程数目 */
	public static int nettyWorkGroupThreadNum=8;
	/** io线程数(2^x) */
	public static int ioThreadNum=4;
	/** 池线程数(2^x)(设置为0,即不启用) */
	public static int poolThreadNum=4;
	/** db线程数(2^x)(设置为0,即不启用) */
	public static int dbThreadNum=4;
	/** 默认线程sleep间隔(毫秒) */
	public static int defaultThreadSleepDelay=5;
	/** io线程帧间隔(毫秒) */
	public static final int ioThreadFrameDelay=5;
	/** 系统帧率(各种系统计时器用) */
	public static final int systemFPS=30;
	/** 系统间隔(毫秒)(各种系统计时器用) */
	public static final int systemFrameDelay=1000/systemFPS;
	/** 池线程固定帧间隔(毫秒)(LogicExecutor执行器间隔) */
	public static final int poolThreadFixedFrameDelay=10;
	/** 系统每份时间(1秒10次,逻辑用) */
	public static int pieceTime=100;
	/** 每秒3次调用 */
	public static int threeTime=300;
	/** s线程队列尺寸(SThread用) */
	public static int sThreadQueueSize=8192;
	/** s线程满时等待次数(大约1次1ms) */
	public static int sThreadWaitCount=30;
	/** s线程满时最长等待轮数 */
	public static int sThreadWaitRound=3;
	/** 线程每轮方法上限(CThread用) */
	public static int cThreadRoundFuncMax=0;
	/** 逻辑线程监控间隔 */
	public static int logicThreadWatchDelay=5000;
	/** io线程监控间隔 */
	public static int ioThreadWatchDelay=10000;
	/** 监控检查线程死循环的时间(15s) */
	public static int watchCheckDeadTreadTime=15;
	
	
	//--业务相关--//
	/** data构造上限值(目前设定最高值12010) */
	public static int dataMaxNum=15000;
	/** 最大序号值(20亿) */
	public static final int indexMax=2000000000;
	/** 最大序号值一半(10亿) */
	public static final int indexMaxHalf=indexMax/2;
	/** 时间验证允许误差(ms) */
	public static int timeCheckDeviation=1000;
	/** 退出阶段缓冲时间(ms) */
	public static int exitLastTime=200;
	/** 事务默认执行时间(s) */
	public static int affairDefaultExecuteTime=10;//10秒
	/** 大数单个数字位数 */
	public static int bigNumberWei=3;//1000
	/** 大数单个数字位值 */
	public static int bigNumberWeiValue=1000;//1000
	/** cron表达式prev时间数 */
	public static long cronExpressionPrevTime=1000*60*60*24;//1天
	/** cron表达式prev时间轮数 */
	public static long cronExpressionPrevTimeRound=6;//32天
	/** 外接buf尺寸 */
	public static int externBufSize=1024;
	
	//--其他--//
	
	
	
	
	
	
	
}
