package com.home.commonData.message.game.serverRequest.game.login;

import com.home.shineData.support.MessageUseMainThread;

/** 角色预备退出消息(发到源服) */
@MessageUseMainThread
public class PlayerPreExitToGameMO
{
	/** 角色ID */
	long playerID;
}
