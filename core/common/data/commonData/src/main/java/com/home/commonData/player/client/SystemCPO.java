package com.home.commonData.player.client;

import com.home.commonData.data.system.InfoLogDO;
import com.home.commonData.data.system.KeepSaveDO;
import com.home.commonData.data.system.SaveVersionDO;
import com.home.shineData.data.DateDO;
import com.home.shineData.support.MaybeNull;

import java.util.Queue;
import java.util.Set;

public class SystemCPO
{
	/** 版本数据 */
	SaveVersionDO version;
	/** 服务器时间戳(毫秒) */
	long serverTime;
	/** 是否封号 */
	boolean isBlock;
	/** 流程步 */
	int flowStep;
	/** 保存数据 */
	KeepSaveDO keepSave;
	/** 客户端随机种子 */
	@Deprecated
	@MaybeNull
	int[] clientRandomSeeds;
	/** 客户端随机种子序号 */
	int clientRandomSeedIndex;
	/** 客户端离线事务收到的客户端序号 */
	int clientOfflineWorkReceiveIndex;
	/** gm指令组(开发期有意义,已废弃) */
	@Deprecated
	@MaybeNull
	Set<String> gmCommandSet;
	/** GM类型 */
	int gmType;
	/** 服务器出生码(每个新服生成一次) */
	int serverBornCode;
	/** 服务器开服时间戳(毫秒) */
	long serverStartTime;
	/** 游戏日志信息队列 */
	Queue<InfoLogDO> logQueue;
	/** 下一个0点时刻 */
	long nextDailyTime;
	/** 创建日期 */
	DateDO createDate;
	/** 当前随机种子key */
	int clientRandomSeedKey;
}
