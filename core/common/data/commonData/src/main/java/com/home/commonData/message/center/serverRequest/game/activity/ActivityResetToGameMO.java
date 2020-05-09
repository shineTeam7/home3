package com.home.commonData.message.center.serverRequest.game.activity;

/** 活动重置 */
public class ActivityResetToGameMO
{
	int id;
	/** 下个重置时间 */
	long nextTime;
	/** 是否到时间自然触发 */
	boolean atTime;
}
