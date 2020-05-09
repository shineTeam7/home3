package com.home.shine.tool;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.LongObjectMap;
import com.home.shine.support.collection.SList;

/** 异步执行工具 */
public abstract class AsyncExecuteTool<T>
{
	/** 状态(0:未开始,1:执行中，2:已就绪) */
	private int _state=0;
	
	private LongObjectMap<T> _dic=new LongObjectMap<>();
	
	private SList<T> _list=new SList<>();
	
	public AsyncExecuteTool()
	{
		//this(ShineSetting.affairDefaultExecuteTime);
	}
	
	/** 是否正在执行中 */
	public boolean isDoing()
	{
		return _state==1;
	}
	
	/** 析构 */
	public void clear()
	{
		reset();
	}
	
	/** 回归 */
	public void reset()
	{
		_state=0;
		_dic.clear();
		_list.clear();
	}
	
	/** 完成 */
	public void complete()
	{
		if(_state==1)
		{
			_state=2;
			
			if(!_dic.isEmpty())
			{
				_dic.forEach((k,v)->
				{
					onSuccess(k,v);
				});
				
				_dic.clear();
			}
			
			if(!_list.isEmpty())
			{
				_list.forEach(v->
				{
					onSuccess(0,v);
				});
				
				_list.clear();
			}
		}
		else
		{
			Ctrl.errorLog("状态不正确");
		}
	}
	
	/** 添加带key的，防止重复 */
	public void add(long key,T value)
	{
		switch(_state)
		{
			case 2:
			{
				onSuccess(key,value);
			}
				break;
			case 1:
			{
				_dic.put(key,value);
			}
				break;
			case 0:
			{
				_dic.put(key,value);
				_state=1;
				asyncDo();
			}
				break;
		}
	}
	
	/** 添加不带key的 */
	public void add(T value)
	{
		switch(_state)
		{
			case 2:
			{
				onSuccess(0,value);
			}
				break;
			case 1:
			{
				_list.add(value);
			}
				break;
			case 0:
			{
				_list.add(value);
				_state=1;
				asyncDo();
			}
				break;
		}
	}
	
	/** 异步主执行 */
	protected abstract void asyncDo();
	/** 成功一个 */
	protected abstract void onSuccess(long key,T value);
}
