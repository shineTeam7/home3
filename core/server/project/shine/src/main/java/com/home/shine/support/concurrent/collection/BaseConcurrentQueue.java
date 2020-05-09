package com.home.shine.support.concurrent.collection;

public class BaseConcurrentQueue
{
	protected boolean _running=true;
	
	/** 执行数 */
	protected int _executeNum;
	
	public void run()
	{
	
	}
	
	public void addFunc(Runnable func)
	{
	
	}
	
	/** 清空 */
	public void clear()
	{
	
	}
	
	/** 设置是否运行 */
	public void setRunning(boolean value)
	{
		_running=value;
	}
	
	/** 执行数目 */
	public int getExecuteNum()
	{
		return _executeNum;
	}
}
