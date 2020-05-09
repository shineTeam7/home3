package com.home.shine.support;

import com.home.shine.ctrl.Ctrl;

/** 序号构造器(上下限均不会达到)(min<value<max) */
public class IndexMaker
{
	private int _min;
	private int _max;
	
	private boolean _canRound;
	
	private int _index;
	
	public IndexMaker()
	{
		this(1,Integer.MAX_VALUE,false);
	}
	
	public IndexMaker(int min,int max,boolean canRound)
	{
		_min=min;
		_max=max;
		_canRound=canRound;
		
		_index=_min;
	}
	
	public void setMin(int min)
	{
		_min=min;
		
		_index=_min;
	}
	
	/** 重置 */
	public void reset()
	{
		_index=_min;
	}
	
	/** 取一个序号 */
	public int get()
	{
		int re=++_index;

		if(re >= _max)
		{
			if(_canRound)
			{
				_index=_min;
				re=++_index;
			}
			else
			{
				Ctrl.throwError("序号构造溢出");
			}
		}

		return re;
	}
}
