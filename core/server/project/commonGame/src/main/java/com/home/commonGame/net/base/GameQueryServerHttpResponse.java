package com.home.commonGame.net.base;

import com.home.shine.constlist.ThreadType;
import com.home.shine.control.ThreadControl;
import com.home.shine.net.httpResponse.BaseHttpResponse;

public abstract class GameQueryServerHttpResponse extends BaseHttpResponse
{
	@Override
	public void dispatch()
	{
		_threadType=ThreadType.Pool;
		_poolIndex=this.httpInstanceID & ThreadControl.poolThreadNumMark;
		
		super.dispatch();
	}
}
