package com.home.commonLogin.net.serverRequest.center.base;

import com.home.commonLogin.global.LoginC;
import com.home.commonLogin.net.base.LoginServerRequest;

public class LoginToCenterServerRequest extends LoginServerRequest
{
	/** 发送到中心服 */
	public void send()
	{
		sendTo(LoginC.server.getCenterSocket());
	}
}
