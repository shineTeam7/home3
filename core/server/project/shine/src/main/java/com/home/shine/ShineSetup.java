package com.home.shine;

import com.home.shine.control.BytesControl;
import com.home.shine.control.LogControl;
import com.home.shine.control.ThreadControl;
import com.home.shine.control.WatchControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineGlobal;
import com.home.shine.global.ShineSetting;
import com.home.shine.net.NettyGroup;
import com.home.shine.support.SLogger;
import com.home.shine.support.collection.SList;

import java.util.concurrent.CountDownLatch;

/** 引擎启动 */
public class ShineSetup
{
	private static boolean _setuped=false;
	
	/** 退出执行 */
	private static SList<Runnable> _exitRuns=new SList<>(Runnable[]::new);
	
	private static Thread _exitThread;
	
	/** 是否是钩子退出 */
	private static volatile boolean _hookExiting=false;
	
	private static volatile CountDownLatch _cd = new CountDownLatch(1);
	
	/** 是否正在退出 */
	private static volatile boolean _exiting=false;
	
	/** 是否退出完毕 */
	private static volatile boolean _exitOver=false;
	
	private static int _exitCompleteNum=0;
	
	/** 是否启动 */
	public static boolean isSetuped()
	{
		return _setuped;
	}
	
	/** 启动(默认) */
	public static void setup()
	{
		setup(null);
	}
	
	/** 启动(默认) */
	public static void setupForTemp()
	{
		ShineGlobal.init();
		setup(null);
	}
	
	/** 启动单元测试 */
	public static void setupForUnitTest()
	{
		ShineSetting.isRelease=false;
		ShineSetting.needLog=false;
		ShineGlobal.init();
		setup(null);
	}
	
	/** 启动(java主线程) */
	public static synchronized void setup(Runnable exitRun)
	{
		if(exitRun!=null)
			_exitRuns.add(exitRun);
		
		if(_setuped)
			return;
		
		_setuped=true;
		
		//控制初始化
		Ctrl.makeFixDirty();
		//日期初始化
		//DateControl.init();
		//观测控制
		WatchControl.init();
		//先log
		LogControl.init();
		//再netty基础
		NettyGroup.init();
		//关注点 每个App实例都有自己的线程管理
		//线程管理
		ThreadControl.init();
		//数据管理
		BytesControl.init();
		
		//启动log
		LogControl.start();
		
		_exitThread=new Thread(ShineSetup::preExit);
		
		Runtime.getRuntime().addShutdownHook(_exitThread);
	}
	
	/** 是否退出中 */
	public static boolean isExiting()
	{
		return _exiting;
	}
	
	/** 关闭(主动调用)(主线程) */
	public static void exit(String str)
	{
		Ctrl.errorLog(str);
		exit();
	}
	
	/** 关闭(主动调用)(主线程) */
	public static synchronized void exit()
	{
		if(_exiting)
		{
			return;
		}
		
		_exiting=true;
		
		//关了检查
		ShineSetting.openCheck=false;
		
		if(_setuped)
		{
			doExitNext();
		}
		else
		{
			//不能在钩子调用System.exit()，否则卡住JVM的关闭过程 by renchao
			if(_hookExiting)
			{
				_exitCompleteNum=1;
				exitOver();
			}
			else
			{
				Ctrl.exit();
			}
		}
	}
	
	/** 执行最后关闭 */
	private static synchronized void doExitLast()
	{
		ThreadControl.dispose();
		NettyGroup.exit();
		
		if(_hookExiting)
		{
			_cd.countDown();
		}
		else
		{
			Runtime.getRuntime().removeShutdownHook(_exitThread);
			//自然关闭
		}
	}
	
	private static void preExit()
	{
		_hookExiting=true;

		Ctrl.log("Shutting down");

		//shutdown的时候偶尔会出现死锁,目前不清楚原因,怀疑跟重复new CountDownLatch有关系, 暂时在属性里面初始化，避免二次new by renchao

		//没在退出中,就退出
		if(!_exiting)
		{
			ThreadControl.addMainFunc(ShineSetup::doExitFromKill);
		}
		
		try
		{
			_cd.await();
		}
		catch(InterruptedException e)
		{
			Ctrl.errorLog(e);
		}
	}
	
	/** 从kill指令执行(主线程) */
	private static void doExitFromKill()
	{
		exit();
	}
	
	private static void doExitNext()
	{
		if(_exitRuns.isEmpty())
		{
			_exitCompleteNum=1;
			exitOver();
		}
		else
		{
			_exitCompleteNum=_exitRuns.size();
			
			_exitRuns.forEach(v->
			{
				ThreadControl.addMainFunc(v);
			});
			
			Ctrl.throwBreak("exitBreak");
		}
	}
	
	/** 进程结束完毕(关闭所有线程,包括netty)(主线程) */
	public static synchronized void exitOver()
	{
		if((--_exitCompleteNum)==0)
		{
			if(_exitOver)
			{
				return;
			}
			
			_exitOver=true;
			
			Ctrl.log("process exitOver");
			
			//立刻刷一次日志
			SLogger.flushOnce();
			
			//给一个netty线程池和日志输出的最后工作时间
			ThreadControl.getMainTimeDriver().setTimeOut(ShineSetup::doExitLast,ShineSetting.exitLastTime);
		}
	}
}
