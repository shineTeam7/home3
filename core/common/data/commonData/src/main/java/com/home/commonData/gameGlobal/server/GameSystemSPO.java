package com.home.commonData.gameGlobal.server;

import com.home.commonData.data.system.GameGlobalWorkDO;
import com.home.commonData.data.system.SaveVersionDO;
import com.home.commonData.data.system.WorkReceiverDO;
import com.home.commonData.data.system.WorkSenderDO;

import java.util.List;
import java.util.Map;

/** 系统数据 */
public class GameSystemSPO
{
	/** 版本数据 */
	SaveVersionDO version;
	/** playerID自增序号组(key:createAreaID原区ID) */
	Map<Integer,Integer> playerIndexDic;
	/** 开服时间 */
	long serverStartTime;
	/** 下一个0点时刻 */
	long nextDailyTime;
	/** 服务器运行序号(每次起服+1) */
	int serverRunIndex;
	/** 事务发起者数据 */
	WorkSenderDO workSenderData;
	/** 全服离线事务组(废弃) */
	List<GameGlobalWorkDO> offlineWorks;
	/** 临时角色记录(为了debug) */
	Map<Long,Integer> tempPlayerRecord;
	/** 事务接收者数据 */
	WorkReceiverDO workReceiverData;
}
