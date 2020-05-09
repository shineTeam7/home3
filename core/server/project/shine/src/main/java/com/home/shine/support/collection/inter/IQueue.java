package com.home.shine.support.collection.inter;

/** 队列通用接口 */
public interface IQueue<V>
{
	public boolean offer(V v);
	
	public V poll();
}
