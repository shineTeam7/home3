package com.home.commonGame.net.base;

import com.home.shine.net.base.BaseResponse;

public abstract class GameServerResponse extends BaseResponse
{
	public GameServerResponse()
	{
	
	}
	
	@Override
	public void dispatch()
	{
		doDispatch();
	}
}
