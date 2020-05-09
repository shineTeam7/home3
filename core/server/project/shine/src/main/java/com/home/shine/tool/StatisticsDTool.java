package com.home.shine.tool;

/** 统计工具 */
public class StatisticsDTool<V> extends BaseStatisticsTool<V>
{
	/** 次数 */
	private int _num;
	/** 总值 */
	private double _total;
	
	private double _min;
	private double _max;
	
	public void clear()
	{
		super.clear();
		
		_num=0;
		_total=0.0;
		_min=-Double.MAX_VALUE;
		_max=Double.MAX_VALUE;
	}
	
	public void add(double value)
	{
		add(value,null);
	}
	
	public void add(double value,V v)
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
				_maxV=v;
			}
		}
	}
	
	public double getAverage()
	{
		if(_num==0)
			return 0.0;
		
		return _total/_num;
	}
	
	public double getMin()
	{
		return _min;
	}
	
	public double getMax()
	{
		return _max;
	}
}
