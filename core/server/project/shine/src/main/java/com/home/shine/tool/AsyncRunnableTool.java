package com.home.shine.tool;

/** 异步方法执行工具 */
public abstract class AsyncRunnableTool extends AsyncExecuteTool<Runnable>
{
	@Override
	protected void onSuccess(long key,Runnable value)
	{
		value.run();
	}
}
