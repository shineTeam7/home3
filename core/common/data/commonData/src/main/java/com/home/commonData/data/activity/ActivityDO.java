package com.home.commonData.data.activity;

/** 活动数据(个人)(与存库数据一致) */
public class ActivityDO
{
	/** id */
	int id;
	/** 是否运行中(存库无效) */
	boolean isRunning;
	/** 已参与次数 */
	int joinTimes;
	/** 下个重置时间(0为未重置过) */
	long nextResetTime;
	/** 上次触发时间 */
	long lastTurnTime;
	/** 下次触发时间 */
	long nextTurnTime;
}
