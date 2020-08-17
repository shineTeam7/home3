package com.home.shine.thread;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.ThreadDataCache;
import com.home.shine.support.pool.BigNumberPool;
import com.home.shine.support.pool.BytesWriteStreamPool;
import com.home.shine.support.pool.DataPool;
import com.home.shine.support.pool.StringBuilderPool;

/** 最基线程 */
public abstract class AbstractThread extends Thread
{
	/** 线程类型 */
	public final int type;
	/** 线程序号 */
	public final int index;
	
	/** 线程实例序号 */
	public byte instanceIndex;
	/** 是否需要唤醒 */
	protected boolean _needNotify=false;
	
	/** tick间隔 */
	protected int _tickDelay=ShineSetting.defaultThreadTickDelay;
	/** 睡多久(ms) */
	private int _sleepTime=ShineSetting.defaultThreadSleepDelay;
	/** 休息中 */
	private volatile boolean _sleeping=false;
	
	/** 是否运行中 */
	protected boolean _running=false;
	/** 是否暂停 */
	protected boolean _pause=false;
	/** 是否恢复 */
	protected volatile boolean _resume=false;
	/** 是否恢复 */
	protected volatile Runnable _resumeFunc;
	
	//统计
	/** 执行方法数 */
	protected int _funcNum=0;
	/** 经过时间 */
	protected int _passTime=0;
	/** 睡眠时间 */
	protected int _restTime=0;
	/** 唤醒次数 */
	protected int _notifyNum=0;
	
	//死循环检测
	/** 运行序号 */
	protected int _runIndex=0;
	/** 是否需要死循环检测(默认开) */
	private boolean _needDeadCheck=false;
	
	//额外
	/** stringBuilder池 */
	public StringBuilderPool stringBuilderPool=new StringBuilderPool();
	/** 字节写流池 */
	public BytesWriteStreamPool bytesWriteStreamPool=new BytesWriteStreamPool();
	/** 大数池 */
	public BigNumberPool bigNumberPool=new BigNumberPool();
	/** 数据对象池 */
	public DataPool pool=new DataPool();
	
	public ThreadDataCache dataCache=new ThreadDataCache();
	
	private int _dataCacheTimeIndex=0;
	
	public AbstractThread(String name,int type,int index)
	{
		super(name);
		
		this.type=type;
		this.index=index;
	}
	
	public void init()
	{
		dataCache.init();
	}
	
	protected void tick(int delay)
	{
		if(ShineSetting.messageUsePool && !dataCache.isEmpty())
		{
			if((_dataCacheTimeIndex+=delay)>=ShineSetting.messageCacheFlushDelay)
			{
				_dataCacheTimeIndex=0;
				dataCache.flushCache();
			}
		}
	}
	
	protected void runEx()
	{

	}
	
	/** 休息 */
	protected void threadSleep()
	{
		_sleeping=true;
		
		try
		{
			Thread.sleep(_sleepTime);
		}
		catch(InterruptedException e)
		{
		
		}
		
		_sleeping=false;
	}
	
	/** 添加执行方法 */
	public void addFunc(Runnable func)
	{
		if(func==null)
			return;
		
		if((Thread.currentThread())==this)
		{
			try
			{
				func.run();
			}
			catch(Exception e)
			{
				Ctrl.errorLog(e);
			}
		}
		else
		{
			toAddFunc(func,null);
			
			if(_needNotify)
				notifyFunc();
		}
	}
	
	/** 添加执行方法 */
	protected abstract void toAddFunc(Runnable func,AbstractThread from);
	
	/** 方法唤醒 */
	public void notifyFunc()
	{
		if(_pause)
			return;
		
		if(!_needNotify)
			return;
		
		if(_sleeping)
		{
			++_notifyNum;
			this.interrupt();
		}
	}
	
	/** 退出 */
	public void exit()
	{
		addFunc(()->
		{
			stopRunning();
		});
	}
	
	/** 结束(本线程调用) */
	protected void stopRunning()
	{
		_running=false;
	}
	
	/** 结束(回调回目标线程) */
	public void pause(BaseThread thread,Runnable func)
	{
		addFunc(()->
		{
			_pause=true;
			thread.addFunc(func);
		});
	}
	
	/** 恢复暂停 */
	public void resumePause(Runnable func)
	{
		_resumeFunc=func;
		_resume=true;
	}
	
	/** 设置睡眠间隔 */
	public void setTickDelay(int time)
	{
		_tickDelay=time;
	}
	
	public void setNeedNotify(boolean value)
	{
		_needNotify=value;
	}
	
	//统计
	
	/** 获取使用率 */
	public int getUsePercent()
	{
		if(_passTime==0)
			return 0;
		
		return (_passTime - _restTime) * 100 / _passTime;
	}
	
	/** 本轮方法执行数 */
	public int getFuncNum()
	{
		return _funcNum;
	}
	
	/** 唤醒次数 */
	public int getNotifyNum()
	{
		return _notifyNum;
	}
	
	/** 清空统计 */
	public void clearCount()
	{
		_funcNum=0;
		_passTime=0;
		_restTime=0;
		_notifyNum=0;
	}
	
	//检测
	
	/** 获取执行序号(加synchronized是为了刷_runIndex的可见) */
	public int getRunIndex()
	{
		return _runIndex;
	}
	
	/** 是否暂停 */
	public boolean isPause()
	{
		return _pause;
	}
	
	/** 是否需要死循环检测(引擎用) */
	public void setNeedDeadCheck(boolean value)
	{
		_needDeadCheck=value;
	}
	
	/** 是否需要死循环检测 */
	public boolean needDeadCheck()
	{
		return _needDeadCheck && !_pause;
	}
	
	/** 从目标线程拷贝数据(重启线程用) */
	public void copy(AbstractThread thread)
	{
		_tickDelay=thread._tickDelay;
		_sleepTime=thread._sleepTime;
		_needDeadCheck=thread._needDeadCheck;
		stringBuilderPool=thread.stringBuilderPool;
		bytesWriteStreamPool=thread.bytesWriteStreamPool;
	}
}
