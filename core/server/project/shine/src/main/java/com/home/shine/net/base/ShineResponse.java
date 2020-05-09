package com.home.shine.net.base;

public abstract class ShineResponse extends BaseResponse
{
	@Override
	public void dispatch()
	{
		//直接IO线程执行
		this.run();
	}
}
