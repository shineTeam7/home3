package com.home.shine.support.func;

/** 方法默认实现 */
public abstract class Function implements Runnable
{
	/** 唯一整参 */
	public int intArg;
	/** 唯一bool参 */
	public boolean boolArg;
	/** 唯一字符串参 */
	public String strArg;
	/** 数组参数 */
	public Object[] arrArgs;
	
	public Function()
	{
		
	}
	
	abstract public void run();
}
