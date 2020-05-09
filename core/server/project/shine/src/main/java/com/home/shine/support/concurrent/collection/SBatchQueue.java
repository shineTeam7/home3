package com.home.shine.support.concurrent.collection;

/** 多生产单消费batch队列 */
public abstract class SBatchQueue<V> extends BaseBatchQueue<V>
{
	public SBatchQueue(int queueSize)
	{
		super(queueSize);
	}
	
}