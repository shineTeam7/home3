package com.home.commonData.message.game.serverRequest.center.base;


import com.home.shineData.support.MessageUseMainThread;

/** 角色到中心服消息(主线程执行) */
@MessageUseMainThread
public class PlayerToCenterMO
{
	/** 角色ID */
	long playerID;
}
