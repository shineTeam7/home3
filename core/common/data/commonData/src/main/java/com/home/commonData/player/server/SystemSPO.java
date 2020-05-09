package com.home.commonData.player.server;

import com.home.commonData.data.system.InfoLogDO;
import com.home.commonData.data.system.KeepSaveDO;
import com.home.commonData.data.system.SaveVersionDO;
import com.home.commonData.data.system.WorkReceiverDO;
import com.home.shineData.data.DateDO;
import com.home.shineData.support.MaybeNull;

import java.util.Queue;

/** 系统模块 */
public class SystemSPO
{
	/** 版本数据 */
	SaveVersionDO version;
	/** 创建日期 */
	DateDO createDate;
	/** 是否封号 */
	boolean isBlock;
	/** 解禁时间 */
	long openBlockDate;
	/** 是否初次登录过 */
	boolean firstLogined;
	/** 在线标记(为了做精准离线) */
	boolean onLineMark;
	/** 上次上线时间(ms) */
	long loginDate;
	/** 上次下线时间(ms) */
	long logoutDate;
	/** 下一个0点时刻 */
	long nextDailyTime;
	/** 上次(在线时)的服务器运行序号 */
	int serverRunIndex;
	/** 流程步 */
	int flowStep;
	/** 保存数据 */
	KeepSaveDO keepSave;
	/** 客户端随机种子(废弃) */
	@Deprecated
	@MaybeNull
	int[] clientRandomSeeds;
	/** 客户端随机种子序号 */
	int clientRandomSeedIndex;
	/** 上次登录客户端设备平台类型 */
	int lastLoginClientPlatformType;
	/** 事务接收者数据 */
	WorkReceiverDO workReceiverData;
	
	/** game全局数据离线事务索引(未实现) */
	int gameGlobalOfflineWorkIndex;
	/** 客户端离线事务收到的客户端序号 */
	int clientOfflineWorkReceiveIndex;
	/** 上次执行客户端离线事务时间(ms) */
	long lastClientOfflineTime;
	
	/** GM类型 */
	int gmType;
	/** 客户端离线事务每日时间可修改次数 */
	int clientOfflineWorkTimeChangeNum;
	
	/** 上次登录客户端设备类型 */
	String lastLoginDeviceType;
	/** 上次登录客户端设备唯一标识 */
	String lastLoginDeviceUniqueIdentifier;
	/** 游戏日志信息队列 */
	Queue<InfoLogDO> logQueue;


	
	/** 客户端离线事务累计修改次数(作弊次数) */
	int clientOfflineWorkTimeChangeTotalNum;
	
	/** 上次登陆App版本 */
	int lastLoginAppVersion;
	/** 上次登陆资源版本 */
	int lastLoginResourceVersion;

	/** 当前随机种子key */
	int clientRandomSeedKey;
	/** 当前随机种子是否需要重置 */
	boolean clientRandomSeedNeedReset;
}
