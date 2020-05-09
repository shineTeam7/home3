package com.home.commonData.message.game.request.activity;

/** 活动开启/关闭消息 */
public class ActivitySwitchMO
{
	/** 活动ID */
	int id;
	/** 是否开启 */
	boolean isRunning;
	/** 上次触发时间 */
	long lastTurnTime;
	/** 下次触发时间 */
	long nextTurnTime;
	/** 是否到时间自然触发 */
	boolean atTime;
}
