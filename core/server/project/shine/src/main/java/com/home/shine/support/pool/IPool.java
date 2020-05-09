package com.home.shine.support.pool;

/** 对象池接口 */
public interface IPool<T>
{
	public T getOne();
	
	public void back(T obj);
}
