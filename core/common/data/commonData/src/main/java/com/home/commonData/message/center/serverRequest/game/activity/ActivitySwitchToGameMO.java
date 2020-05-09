package com.home.commonData.message.center.serverRequest.game.activity;

/** 活动切换开启/关闭 */
public class ActivitySwitchToGameMO
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
