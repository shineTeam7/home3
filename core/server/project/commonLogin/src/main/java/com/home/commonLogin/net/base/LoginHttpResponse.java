package com.home.commonLogin.net.base;

import com.home.shine.global.ShineSetting;
import com.home.shine.net.httpResponse.BytesHttpResponse;

public abstract class LoginHttpResponse extends BytesHttpResponse
{
	public LoginHttpResponse()
	{
		setNeedFullWrite(ShineSetting.clientMessageUseFull);
	}
	
	//@Override
	//public void dispatch()
	//{
	//	_threadType=ThreadType.Pool;
	//	_poolIndex=this.httpInstanceID & ThreadControl.poolThreadNumMark;
	//
	//	super.dispatch();
	//}
}