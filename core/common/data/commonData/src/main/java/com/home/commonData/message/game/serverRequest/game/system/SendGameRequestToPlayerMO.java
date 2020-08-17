package com.home.commonData.message.game.serverRequest.game.system;

import com.home.commonData.message.game.serverRequest.game.base.PlayerGameToGameMO;

public class SendGameRequestToPlayerMO extends PlayerGameToGameMO
{
	/** 消息ID */
	int requestID;
	/** 数据 */
	byte[] data;
}
