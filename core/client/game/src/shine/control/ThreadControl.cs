using System;
using System.Threading;

namespace ShineEngine
{
	/** 线程控制 */
	public class ThreadControl
	{
		/** unity主线程 */
		private static Thread _mainThread;

		private static SList<BaseThread> _threadList=new SList<BaseThread>();

		/** 主线程事务队列 */
		private static SFuncQueue _mainFuncQueue=new SFuncQueue();
		/** 辅助线程 */
		private static BaseThread _assistThread;
		/** 自定义io线程 */
		private static BaseThread _ioThread;

		private static int _secondIndex;

		/** 初始化 */
		public static void init()
		{
			_mainThread=Thread.CurrentThread;

			_assistThread=new BaseThread("assistThread");
			_assistThread.start();
			addThread(_assistThread);

			_ioThread=new BaseThread("ioThread");
			_ioThread.start();
			addThread(_ioThread);
		}

		/** 析构 */
		public static void dispose()
		{
			_assistThread.exit();
			_ioThread.exit();
			_threadList.clear();
		}

		public static void addThread(BaseThread thread)
		{
			_threadList.add(thread);
		}

		public static void removeThread(BaseThread thread)
		{
			_threadList.removeObj(thread);
		}

		public static void onFrame()
		{
			_mainFuncQueue.runOnce();

			if((++_secondIndex)>=ShineSetting.systemFPS)
			{
				_secondIndex=0;
				onSecond();
			}
		}

		private static void onSecond()
		{
			BaseThread[] values=_threadList.getValues();

			for(int i=0,len=_threadList.size();i<len;++i)
			{
				values[i].check();
			}
		}

		/** 添加主线程(unity)执行 */
		public static void addMainFunc(Action func)
		{
			if(isCurrentMainThread())
			{
				func();
				return;
			}

			_mainFuncQueue.add(func);
		}

		/** 添加辅助执行 */
		public static void addAssistFunc(Action func)
		{
			_assistThread.addFunc(func);
		}

		/** 添加辅助执行 */
		public static void addIOFunc(Action func)
		{
			_ioThread.addFunc(func);
		}

		/** 当前是否在主线程 */
		public static bool isCurrentMainThread()
		{
			return Thread.CurrentThread==_mainThread;
		}

		/** 检查当前在主线程 */
		public static void checkCurrentIsMainThread()
		{
			if(ShineSetting.openCheck)
			{
				if(Thread.CurrentThread!=_mainThread)
				{
					Ctrl.throwError("不是主线程");
				}
			}
		}
	}
}