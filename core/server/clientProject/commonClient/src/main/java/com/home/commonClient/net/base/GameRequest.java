package com.home.commonClient.net.base;

import com.home.shine.global.ShineSetting;
import com.home.shine.net.base.BaseRequest;

public class GameRequest extends BaseRequest
{
	public GameRequest()
	{
		//客户端request
		setNeedFullWrite(ShineSetting.clientMessageUseFull);
	}
}
