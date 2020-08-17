package com.home.shine.control;

import com.home.shine.ShineSetup;
import com.home.shine.constlist.ThreadType;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.collection.SList;
import com.home.shine.thread.AbstractThread;
import com.home.shine.thread.BaseThread;
import com.home.shine.thread.CoreThread;
import com.home.shine.thread.DBPoolThread;
import com.home.shine.thread.IOThread;
import com.home.shine.thread.PoolThread;
import com.home.shine.thread.ThreadWatcher;
import com.home.shine.timer.TimeDriver;
import com.home.shine.utils.MathUtils;

/** 线程控制 */
public class ThreadControl
{
	/** io线程数标记(数目-1) */
	public static int ioThreadNumMark;
	/** db线程数标记(数目-1) */
	public static int dbPoolThreadNumMark;
	/** 池线程数标记(数目-1) */
	public static int poolThreadNumMark;
	/** 线程数目 */
	public static int threadLength;
	
	/** 观测守护线程*/
	private static ThreadWatcher _threadWatcher;
	
	/** 全部使用线程列表 */
	private static SList<AbstractThread> _threadList;
	
	/** 主线程 */
	private static CoreThread _mainThread;
	/** 杂项线程 */
	private static BaseThread _mixedThread;
	/** 日志线程 */
	private static CoreThread _logThread;
	/** io线程组 */
	private static IOThread[] _ioThreadList;
	/** 池线程组 */
	private static PoolThread[] _poolThreadList;
	/** DB池线程组 */
	private static DBPoolThread[] _dbPoolThreadList;
	/** DB写线程 */
	private static BaseThread _dbWriteThread;
	
	/** 当前是否正在暂停逻辑线程中 */
	private static boolean _isPauseLogicing=false;
	/** 暂停逻辑计数 */
	private static int _pauseLogicCount=0;
	/** 暂停完毕回调 */
	private static Runnable _pauseOverFunc;
	
	/** 初始化 */
	public static void init()
	{
		if(!MathUtils.isPowerOf2(ShineSetting.ioThreadNum) || !MathUtils.isPowerOf2(ShineSetting.poolThreadNum) || !MathUtils.isPowerOf2(ShineSetting.dbThreadNum))
		{
			Ctrl.throwError("线程数需为2^n");
			return;
		}
		
		ioThreadNumMark=ShineSetting.ioThreadNum - 1;
		poolThreadNumMark=ShineSetting.poolThreadNum - 1;
		dbPoolThreadNumMark=ShineSetting.dbThreadNum - 1;
		
		_threadList=new SList<>();
		_threadWatcher=new ThreadWatcher();
		
		addThread(_mainThread=new CoreThread("mainThread",ThreadType.Main,0));
		addThread(_mixedThread=new BaseThread("mixedThread",ThreadType.Mixed,0));
		_mixedThread.setNeedNotify(ShineSetting.needThreadNotify);
		addThread(_logThread=new CoreThread("logThread",ThreadType.Log,0));
		
		_ioThreadList=new IOThread[ShineSetting.ioThreadNum];
		
		for(int i=0;i<ShineSetting.ioThreadNum;i++)
		{
			addThread(_ioThreadList[i]=new IOThread(i));
		}
		
		if(ShineSetting.poolThreadNum>0)
		{
			if(ShineSetting.poolThreadNum!=ShineSetting.ioThreadNum)
			{
				Ctrl.throwError("池线程数需与io线程数一致");
			}
			
			_poolThreadList=new PoolThread[ShineSetting.poolThreadNum];
			
			for(int i=0;i<_poolThreadList.length;++i)
			{
				addThread(_poolThreadList[i]=new PoolThread(i));
			}
		}
		
		if(ShineSetting.dbThreadNum>0)
		{
			_dbPoolThreadList=new DBPoolThread[ShineSetting.dbThreadNum];
			
			for(int i=0;i<_dbPoolThreadList.length;i++)
			{
				addThread(_dbPoolThreadList[i]=new DBPoolThread(i));
				_dbPoolThreadList[i].setTickDelay(ShineSetting.poolThreadFixedFrameDelay);
				_dbPoolThreadList[i].setNeedNotify(ShineSetting.needThreadNotify);
			}
			
			addThread(_dbWriteThread=new BaseThread("dbWriteThread",ThreadType.DBWrite,0));
		}
		
		threadLength=_threadList.size();
		
		_threadList.forEach(v->
		{
			v.init();
			v.start();
			_threadWatcher.addThread(v);
		});
		
		//切到主线程
		_mainThread.addFunc(ThreadControl::mainInit);
	}
	
	private static void addThread(AbstractThread thread)
	{
		thread.instanceIndex=(byte)_threadList.size();
		_threadList.add(thread);
	}
	
	/** 初始化(主线程) */
	private static void mainInit()
	{
		_mainThread.getTimeDriver().setFrame(ThreadControl::onFrame);
	}
	
	/** 线程监控开始 */
	public static void watchStart()
	{
		if(ShineSetting.needThreadWatch)
		{
			openDeadCheck();
			_threadWatcher.start();
		}
	}
	
	/** 开启死循环检测 */
	public static void openDeadCheck()
	{
		_threadList.forEach(v->
		{
			v.setNeedDeadCheck(true);
		});
	}
	
	/** 析构 */
	public static void dispose()
	{
		_mainThread.exit();
		_mixedThread.exit();
		_logThread.exit();
		
		for(IOThread thread : _ioThreadList)
		{
			thread.exit();
		}
		
		if(_poolThreadList!=null)
		{
			for(PoolThread thread : _poolThreadList)
			{
				thread.exit();
			}
		}
		
		if(_dbPoolThreadList!=null)
		{
			for(DBPoolThread thread : _dbPoolThreadList)
			{
				thread.exit();
			}
		}
		
		if(_dbWriteThread!=null)
		{
			_dbWriteThread.exit();
		}
		
		_threadWatcher.exit();
	}
	
	private static void onFrame(int t)
	{
		Ctrl.makeFixDirty();
		
		DateControl.makeFixDirty();
	}
	
	//方法

	/** 获取当前引擎线程(如不是,返回null) */
	public static final AbstractThread getCurrentShineThread()
	{
		Thread thread=Thread.currentThread();
		
		if(thread instanceof AbstractThread)
		{
			return (AbstractThread)thread;
		}
		
		return null;
	}
	
	/** 获取主线程 */
	public static final CoreThread getMainThread()
	{
		return _mainThread;
	}
	
	/** 获取主时间驱动 */
	public static final TimeDriver getMainTimeDriver()
	{
		return _mainThread.getTimeDriver();
	}
	
	/** 获取日志线程 */
	public static final CoreThread getLogThread()
	{
		return _logThread;
	}
	
	/** 获取IO线程 */
	public static IOThread getIOThread(int index)
	{
		return _ioThreadList[index];
	}
	
	/** 获取池线程 */
	public static CoreThread getPoolThread(int index)
	{
		return _poolThreadList[index];
	}
	
	/** 获取观测线程 */
	public static ThreadWatcher getThreadWatcher()
	{
		return _threadWatcher;
	}
	
	/** 添加主方法执行 */
	public static void addMainFunc(Runnable func)
	{
		_mainThread.addFunc(func);
	}
	
	/** 添加杂项执行 */
	public static void addMixedFunc(Runnable func)
	{
		_mixedThread.addFunc(func);
	}
	
	/** 添加日志执行 */
	public static void addLogFunc(Runnable func)
	{
		_logThread.addFunc(func);
	}
	
	/** 添加io执行(index是%过的) */
	public static void addIOFunc(int index,Runnable func)
	{
		_ioThreadList[index].addFunc(func);
	}
	
	/** 添加io执行(netty部分)(index是%过的) */
	public static void addOtherIOFunc(int index,Runnable func)
	{
		_ioThreadList[index].addOtherFunc(func);
	}
	
	/** 添加io执行(index是%过的) */
	public static void addIOFuncAbs(int index,Runnable func)
	{
		if(ThreadControl.getCurrentShineThread()!=null)
			_ioThreadList[index].addFunc(func);
		else
			_ioThreadList[index].addOtherFunc(func);
	}
	
	/** 添加池方法执行(index是%过的) */
	public static void addPoolFunc(int index,Runnable func)
	{
		_poolThreadList[index].addFunc(func);
	}
	
	/** 添加DB写执行 */
	public static void addDBWriteFunc(Runnable func)
	{
		_dbWriteThread.addFunc(func);
	}
	
	/** 添加dbPool执行(index是%过的)(系统用) */
	public static void addDBPoolFunc(int index,Runnable func)
	{
		_dbPoolThreadList[index].addFunc(func);
	}
	
	/** 添加监控线程执行 */
	public static void addWatchFunc(Runnable func)
	{
		_threadWatcher.addFunc(func);
	}
	
	/** 添加临时线程执行 */
	public static void addTempFunc(Runnable func)
	{
		new Thread(func).start();
	}
	
	/** 通过类型添加 */
	public static void addFuncByType(int type,int index,Runnable func)
	{
		getThreadByType(type,index).addFunc(func);
	}
	
	/** 通过实例序号获取线程 */
	public static AbstractThread getThreadByInstance(int instanceIndex)
	{
		return _threadList.get(instanceIndex);
	}
	
	/** 通过类型获取线程 */
	public static AbstractThread getThreadByType(int type,int index)
	{
		switch(type)
		{
			case ThreadType.Main:
				return _mainThread;
			case ThreadType.Mixed:
				return _mixedThread;
			case ThreadType.Log:
				return _logThread;
			case ThreadType.IO:
				return _ioThreadList[index];
			case ThreadType.Pool:
				return _poolThreadList[index];
			case ThreadType.DBPool:
				return _dbPoolThreadList[index];
			case ThreadType.DBWrite:
				return _dbWriteThread;
			case ThreadType.Watcher:
				return _threadWatcher;
			default:
			{
				Ctrl.errorLog("不支持其他类型的访问");
			}
		}
		
		return null;
	}
	
	/** 当前是否是主线程 */
	public static boolean isMainThread()
	{
		return Thread.currentThread()==_mainThread;
	}
	
	/** 当前是否是池线程 */
	public static boolean isPoolThread()
	{
		return getCurrentThreadType()==ThreadType.Pool;
	}
	
	/** 获取当前线程类型 */
	public static int getCurrentThreadType()
	{
		AbstractThread currentThread;
		
		if((currentThread=getCurrentShineThread())==null)
			return ThreadType.None;
		
		return currentThread.type;
	}
	
	/** 检查当前是否主线程 */
	public static void checkCurrentIsMainThread()
	{
		if(ShineSetting.openCheck)
		{
			if(Thread.currentThread()!=_mainThread)
			{
				Ctrl.throwError("不是主线程");
			}
		}
	}
	
	/** 暂停所有逻辑线程(主线程+池线程) */
	public static void pauseAllLogicThread(Runnable func)
	{
		if(Thread.currentThread()!=_mixedThread)
		{
			Ctrl.errorLog("不是mix线程");
			return;
		}
		
		if(_isPauseLogicing)
		{
			Ctrl.errorLog("上次暂停还未完成");
			return;
		}
		
		_isPauseLogicing=true;
		
		_pauseLogicCount=_poolThreadList.length+1;
		
		_pauseOverFunc=func;
		
		_mainThread.pause(_mixedThread,ThreadControl::toPauseOne);
		
		for(int i=0;i<_poolThreadList.length;i++)
		{
			_poolThreadList[i].pause(_mixedThread,ThreadControl::toPauseOne);
		}
	}
	
	private static void toPauseOne()
	{
		if((--_pauseLogicCount)==0)
		{
			Runnable func=_pauseOverFunc;
			_pauseOverFunc=null;
			func.run();
		}
	}
	
	/** 恢复所有逻辑线程(主线程+池线程)(func会被每个逻辑线程第一时间执行，可用getCurrentShineThread来获取当前线程) */
	public static void resumePauseAllLogicThread(Runnable func)
	{
		if(!_isPauseLogicing)
			return;
		
		_isPauseLogicing=false;
		
		_mainThread.resumePause(func);
		
		for(int i=0;i<_poolThreadList.length;i++)
		{
			_poolThreadList[i].resumePause(func);
		}
	}
	
	private static AbstractThread createThread(int type,int index)
	{
		switch(type)
		{
			case ThreadType.Main:
				return new CoreThread("mainThread",type,0);
			case ThreadType.Mixed:
				return new BaseThread("mixedThread",type,0);
			case ThreadType.Log:
				return new CoreThread("logThread",ThreadType.Log,0);
			case ThreadType.IO:
				return new IOThread(index);
			case ThreadType.Pool:
				return new PoolThread(index);
			case ThreadType.DBPool:
				return new DBPoolThread(index);
			case ThreadType.DBWrite:
				return new BaseThread("dbWriteThread",ThreadType.DBWrite,0);
			case ThreadType.Watcher:
				return new ThreadWatcher();
		}
		
		return null;
	}
	
	/** 重启某线程 */
	public static synchronized void restartThread(int type,int index)
	{
		if(ShineSetup.isExiting())
			return;
		
		AbstractThread thread=getThreadByType(type,index);
		
		//活着
		if(thread.isAlive())
			return;
		
		AbstractThread next=createThread(type,index);
		next.copy(thread);
		
		String name=thread.getName();
		
		if(name.endsWith(")"))
		{
			int ii=name.indexOf('(');
			
			String front=name.substring(0,ii);
			name=front+'('+(Integer.parseInt(name.substring(ii+1,name.length()-1))+1)+')';
		}
		else
		{
			name=name+"(1)";
		}
		
		next.setName(name);
		
		Ctrl.errorLog("线程重启"+name);
		
		switch(thread.type)
		{
			case ThreadType.Main:
			{
				_mainThread=(CoreThread)next;
			}
				break;
			case ThreadType.Mixed:
			{
				_mixedThread=(BaseThread)next;
			}
				break;
			case ThreadType.Log:
			{
				_logThread=(CoreThread)next;
			}
				break;
			case ThreadType.IO:
			{
				_ioThreadList[index]=(IOThread)next;
			}
				break;
			case ThreadType.Pool:
			{
				_poolThreadList[index]=(PoolThread)next;
			}
				break;
			case ThreadType.DBPool:
			{
				_dbPoolThreadList[index]=(DBPoolThread)next;
			}
				break;
			case ThreadType.DBWrite:
			{
				_dbWriteThread=(BaseThread)next;
			}
				break;
			case ThreadType.Watcher:
			{
				_threadWatcher=(ThreadWatcher)next;
			}
				break;
		}
		
		if(type!=ThreadType.Watcher)
		{
			_threadWatcher.removeThread(thread);
			_threadWatcher.addThread(next);
		}
		
		next.start();
	}
	
	/** 检测守护线程是否活着 */
	public static void checkWatchAlive()
	{
		if(ShineSetup.isExiting())
			return;
		
		ThreadWatcher watcher=_threadWatcher;
		
		if(watcher.isStarted() && !watcher.isAlive())
		{
			restartThread(watcher.type,watcher.index);
		}
	}
	
	
}
