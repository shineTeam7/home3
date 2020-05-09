package com.home.commonGame.net.serverRequest.center.base;

import com.home.commonGame.global.GameC;
import com.home.commonGame.net.base.GameServerRequest;

public class GameToCenterServerRequest extends GameServerRequest
{
	/** 发送到中心服 */
	public void send()
	{
		GameC.server.getCenterSocket().send(this);
	}
}
