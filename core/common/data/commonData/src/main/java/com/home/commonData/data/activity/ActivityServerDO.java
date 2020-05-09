package com.home.commonData.data.activity;

import com.home.shineData.support.OnlyS;

/** 活动服务器保存数据 */
@OnlyS
public class ActivityServerDO
{
	/** 活动id */
	int id;
	/** 是否运行中(存库时 无效) */
	boolean isRunning;
	/** 下次重置时间 */
	long nextResetTime=-1L;
	/** 是否强制关闭了 */
	boolean isForceClosed;
	/** 上次触发时间 */
	long lastTurnTime;
	/** 下次触发时间 */
	long nextTurnTime;
}
