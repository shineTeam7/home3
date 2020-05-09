package com.home.shine.tool;

public class StatisticsLTool<V> extends BaseStatisticsTool<V>
{
	/** 次数 */
	private int _num;
	/** 总值 */
	private long _total;
	
	private long _min;
	private long _max;
	
	public void clear()
	{
		super.clear();
		
		_num=0;
		_total=0L;
		_min=-Long.MAX_VALUE;
		_max=Long.MAX_VALUE;
	}
	
	public void add(long value)
	{
		add(value,null);
	}
	
	public void add(long value,V v)
	{
		_total+=value;
		++_num;
		
		if(_num==1)
		{
			_max=_min=value;
			_minV=_maxV=v;
		}
		else
		{
			if(value>_max)
			{
				_max=value;
				_maxV=v;
			}
			
			if(value<_min)
			{
				_min=value;
				_minV=v;
			}
		}
	}
	
	public long getAverage()
	{
		if(_num==0)
			return 0L;
		
		return _total/_num;
	}
	
	public long getMin()
	{
		return _min;
	}
	
	public long getMax()
	{
		return _max;
	}
	
	
}
