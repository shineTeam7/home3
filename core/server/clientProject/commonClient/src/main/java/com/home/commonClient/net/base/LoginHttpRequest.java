package com.home.commonClient.net.base;

import com.home.commonBase.global.CommonSetting;
import com.home.commonClient.part.player.Player;
import com.home.shine.constlist.ThreadType;
import com.home.shine.global.ShineSetting;
import com.home.shine.net.httpRequest.BytesHttpRequest;

public abstract class LoginHttpRequest extends BytesHttpRequest
{
	public LoginHttpRequest()
	{
		setNeedFullWrite(ShineSetting.clientMessageUseFull);
		
		if(ShineSetting.clientHttpUseHttps)
		{
			setIsHttps(true);
		}
		
		if(ShineSetting.clientHttpUseBase64)
		{
			setNeedBase64(true);
		}
	}
	
	/** 通过角色index发送 */
	public void sendByPlayer(Player me)
	{
		_threadType=ThreadType.Pool;
		_poolIndex=me.system.executorIndex;
		
		sendToURL(me.system.getLoginURL(),false);
	}
}
