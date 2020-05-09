package com.home.shine.net.socket;

import com.home.shine.global.ShineSetting;
import com.home.shine.net.NettyGroup;
import io.netty.channel.ChannelPipeline;

public abstract class WebSendSocket extends SendSocket
{
	
	@Override
	protected BaseSocketContent toCreateNewContent()
	{
		return new WebSendSocketContent(this);
	}
	
	/** 初始化流水线 */
	public void initPipeline(ChannelPipeline pipeline)
	{
		if(ShineSetting.clientWebSocketUseWSS)
		{
			NettyGroup.addClientSSLHandler(pipeline);
		}
	}
	
}
