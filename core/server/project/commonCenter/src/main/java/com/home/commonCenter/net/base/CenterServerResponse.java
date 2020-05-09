package com.home.commonCenter.net.base;

import com.home.shine.constlist.ThreadType;
import com.home.shine.control.ThreadControl;
import com.home.shine.net.base.BaseResponse;

public abstract class CenterServerResponse extends BaseResponse
{
	public CenterServerResponse()
	{
		_threadType=ThreadType.Main;
	}
	
	/** 缓存执行(主线程) */
	public void cacheExecute()
	{
		execute();
	}
}
