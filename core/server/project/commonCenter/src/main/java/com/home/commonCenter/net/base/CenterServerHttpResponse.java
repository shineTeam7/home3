package com.home.commonCenter.net.base;

import com.home.shine.constlist.ThreadType;
import com.home.shine.net.httpResponse.BaseHttpResponse;

public abstract class CenterServerHttpResponse extends BaseHttpResponse
{
	@Override
	public void dispatch()
	{
		_threadType=ThreadType.Main;
		
		super.dispatch();
	}
}
