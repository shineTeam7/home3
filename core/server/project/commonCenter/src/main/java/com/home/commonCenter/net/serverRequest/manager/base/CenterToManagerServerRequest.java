package com.home.commonCenter.net.serverRequest.manager.base;

import com.home.commonCenter.global.CenterC;
import com.home.commonCenter.net.base.CenterServerRequest;

public class CenterToManagerServerRequest extends CenterServerRequest
{
	public void send()
	{
		CenterC.server.sendToManager(this);
	}
}
