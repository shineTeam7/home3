package com.home.shine.tool;

public class BaseStatisticsTool<V>
{
	protected V _minV;
	protected V _maxV;
	
	public void clear()
	{
		_minV=null;
		_maxV=null;
	}
	
	public V getMinV()
	{
		return _minV;
	}
	
	public V getMaxV()
	{
		return _maxV;
	}
}
