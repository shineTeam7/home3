package com.home.shine.timer;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.IndexMaker;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.collection.SList;
import com.home.shine.support.func.IntCall;

/** 时间驱动 */
public class TimeDriver
{
	private final int _delayRound;
	private final int _delayRoundMax;
	
	private int _delayLast=0;

	/** 时间执行组 */
	private IntObjectMap<TimeExecuteData> _timeExDic=new IntObjectMap<>(TimeExecuteData[]::new);
	/** 计数 */
	private IndexMaker _timeExIndexMaker=new IndexMaker();
	/** 每帧执行 */
	private IntObjectMap<IntCall> _frameDic=new IntObjectMap<>(IntCall[]::new);
	/** 每帧调用计数 */
	private IndexMaker _frameIndexMaker=new IndexMaker();
	/** 延时调用 */
	private SList<Runnable> _callLaters=new SList<>(Runnable[]::new);
	/** 延时调用遍历中 */
	private boolean _callLaterForEaching=false;
	/** 延时调用临时组 */
	private SList<Runnable> _callLaterTemps=new SList<>(Runnable[]::new);
	
	public TimeDriver()
	{
		_delayRound=ShineSetting.systemFrameDelay;
		_delayRoundMax=_delayRound*2;
	}
	
	///** 设置间隔下限 */
	//public void setDelayRound(int value)
	//{
	//	_delayRound=value;
	//}
	
	/** 走时间(自备try catch) */
	public void tick(int delay)
	{
		_delayLast+=delay;
		
		if(_delayLast >= _delayRound)
		{
			//防止系统唤醒的bug
			if(_delayLast>=_delayRoundMax)
			{
				onFrame(_delayLast);
				_delayLast=0;
			}
			else
			{
				_delayLast-=_delayRound;
				onFrame(_delayRound);
			}
		}
	}
	
	/** 清空所有 */
	public void clear()
	{
		_timeExDic.clear();
		_timeExIndexMaker.reset();
		_frameDic.clear();
		_frameIndexMaker.reset();
		_callLaters.clear();
	}
	
	/** 每帧(间隔按delayRound算) */
	private void onFrame(int delay)
	{
		//timeEx;
		IntObjectMap<TimeExecuteData> timeExDic;
		
		if(!(timeExDic=_timeExDic).isEmpty())
		{
			_timeExDic.forEachValueS(tData->
			{
				if(!tData.pause)
				{
					tData.time+=delay;
					
					if(tData.time >= tData.timeMax)
					{
						tData.intArg=tData.time;
						
						if(tData.isCut)
							tData.time=0;
						else
							tData.time-=tData.timeMax;
						
						if(!tData.isGoOn)
						{
							//移除
							timeExDic.remove(tData.index);
						}
						
						executeTimeExFunc(tData);
					}
				}
			});
		}
		
		//frame
		
		IntObjectMap<IntCall> frameDic;
		
		if(!(frameDic=_frameDic).isEmpty())
		{
			frameDic.forEachValueS(v->
			{
				try
				{
					v.call(delay);
				}
				catch(Exception e)
				{
					Ctrl.errorLog(e);
				}
			});
		}
		
		//callLater

		SList<Runnable> callLaters;
		
		if(!(callLaters=_callLaters).isEmpty())
		{
			callLaters.forEachAndClear(v->
			{
				try
				{
					v.run();
				}
				catch(Exception e)
				{
					Ctrl.errorLog(e);
				}
			});
			
			_callLaterForEaching=false;
			
			SList<Runnable> callLaterTemps;
			
			if(!(callLaterTemps=_callLaterTemps).isEmpty())
			{
				callLaters.addAll(callLaterTemps);
				
				callLaterTemps.clear();
			}
		}
	}
	
	/** 添加timeEx */
	private int addTimeEx(TimeExecuteData tData)
	{
		int index=_timeExIndexMaker.get();
		
		tData.index=index;
		
		_timeExDic.put(index,tData);
		
		return index;
	}
	
	/** 移除timeEx */
	private void removeTimeEx(int index)
	{
		if(index<=0)
		{
			Ctrl.errorLog("timeIndex不能<=0");
			return;
		}
		
		TimeExecuteData tData=_timeExDic.get(index);
		
		if(tData==null)
			return;
		
		_timeExDic.remove(index);
	}
	
	private void executeTimeExFunc(TimeExecuteData tData)
	{
		try
		{
			if(tData.isCut)
			{
				tData.intFunc.call(tData.intArg);
			}
			else
			{
				tData.func.run();
			}
		}
		catch(Exception e)
		{
			Ctrl.errorLog(e);
		}
	}
	
	/** 暂停时间ex */
	public void pauseTimeEx(int index,boolean bool)
	{
		TimeExecuteData te=_timeExDic.get(index);
		
		if(te==null)
		{
			return;
		}
		
		te.pause=bool;
	}
	
	/** 延迟delay毫秒，执行方法 */
	public int setTimeOut(Runnable func,int delay)
	{
		if(delay<=0)
		{
			Ctrl.errorLog("传入的时间间隔<=0");
			return -1;
		}
		
		TimeExecuteData tData=new TimeExecuteData();
		
		tData.func=func;
		tData.timeMax=delay;
		tData.isGoOn=false;
		
		return addTimeEx(tData);
	}
	
	/** 取消timeOut */
	public void clearTimeOut(int index)
	{
		removeTimeEx(index);
	}
	
	/** 立即结束timeOut */
	public void finishTimeOut(int index)
	{
		TimeExecuteData tData=_timeExDic.get(index);
		
		if(tData==null)
		{
			return;
		}
		
		clearTimeOut(index);
		
		executeTimeExFunc(tData);
	}
	
	/** 间隔delay毫秒，执行方法(固定间隔) */
	public int setIntervalFixed(Runnable func,int delay)
	{
		if(delay<=0)
		{
			Ctrl.errorLog("传入的时间间隔<=0");
			return -1;
		}
		
		TimeExecuteData tData=new TimeExecuteData();
		
		tData.func=func;
		tData.timeMax=delay;
		tData.isGoOn=true;
		tData.isCut=false;
		
		return addTimeEx(tData);
	}
	
	/** 间隔delay毫秒,执行方法(如掉帧,只回调一次) */
	public int setInterval(IntCall func,int delay)
	{
		if(delay<=0)
		{
			Ctrl.errorLog("传入的时间间隔<=0");
			return -1;
		}
		
		TimeExecuteData tData=new TimeExecuteData();
		
		tData.intFunc=func;
		tData.timeMax=delay;
		tData.isGoOn=true;
		tData.isCut=true;
		
		return addTimeEx(tData);
	}
	
	/** 重置timeEx */
	public void resetTimeEx(int index,int delay)
	{
		TimeExecuteData tData=_timeExDic.get(index);
		
		if(tData==null)
		{
			return;
		}
		
		tData.time=0;
		tData.timeMax=delay;
	}
	
	/** 取消间隔 */
	public void clearInterval(int index)
	{
		removeTimeEx(index);
	}
	
	//frame
	
	/** 设置每帧回调 */
	public int setFrame(IntCall func)
	{
		int index=_frameIndexMaker.get();
		
		_frameDic.put(index,func);

		return index;
	}
	
	/** 取消每帧回调 */
	public void clearFrame(int index)
	{
		_frameDic.remove(index);
	}
	
	//callLater
	
	/** 下帧回调 */
	public void callLater(Runnable func)
	{
		if(_callLaterForEaching)
		{
			_callLaterTemps.add(func);
		}
		else
		{
			_callLaters.add(func);
		}
	}
	
	/** 时间执行数据 */
	private class TimeExecuteData
	{
		/** 序号 */
		public int index;
		/** 回调 */
		public Runnable func;
		/** 整形回调(isCut) */
		public IntCall intFunc;
		/** 整形回调参 */
		public int intArg;
		
		/** 当前时间 */
		public int time;
		/** 总时间 */
		public int timeMax;
		/** 用来决定是interval还是timeOut */
		public boolean isGoOn=false;
		/** 在interval的情况下,是否每次只调用一遍 */
		public boolean isCut=false;
		/** 是否暂停 */
		public boolean pause=false;
	}
}
