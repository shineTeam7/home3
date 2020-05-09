package com.home.commonData.centerGlobal.server;

import com.home.commonData.data.system.SaveVersionDO;
import com.home.commonData.data.system.WorkDO;
import com.home.commonData.data.system.WorkReceiverDO;
import com.home.commonData.data.system.WorkSenderDO;

import java.util.Map;

/** 中心服系统数据 */
public class CenterSystemSPO
{
	/** 版本数据 */
	SaveVersionDO version;
	/** 开服时间 */
	long serverStartTime;
	/** 服务器时间偏移 */
	long serverOffTime;
	/** 下一个0点时刻 */
	long nextDailyTime;
	/** 服务器运行序号(每次起服+1) */
	int serverRunIndex;
	/** 事务发起者数据 */
	WorkSenderDO workSenderData;
	/** 服务器出生码(每个新服生成一次) */
	int serverBornCode;
	/** 事务接收数据 */
	WorkReceiverDO workReceiverData;
}
