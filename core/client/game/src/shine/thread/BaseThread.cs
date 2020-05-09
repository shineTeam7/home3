using System;
using System.Threading;

namespace ShineEngine
{
	/// <summary>
	/// 基础线程
	/// </summary>
	public class BaseThread
	{
		private string _name;

		/** 睡多久(ms) */
		protected int _sleepTime=ShineSetting.defaultThreadSleepDelay;

		private SFuncQueue _queue=new SFuncQueue();

		private Action<int> _tickFunc;

		/** 线程 */
		private Thread _thread;

		/** 是否运行中 */
		private bool _running=false;

		public BaseThread(string name)
		{
			_name=name;
			_thread=new Thread(run);
			_thread.Name=name;
			_thread.IsBackground=true;
		}

		/** 设置睡眠时间 */
		public void setSleepTime(int time)
		{
			_sleepTime=time;
		}

		public void setTickFunc(Action<int> func)
		{
			_tickFunc=func;
		}

		public void check()
		{
			if(!_running)
				return;

			//重启一个
			if(!_thread.IsAlive)
			{
				Ctrl.errorLog("线程重启",_name);
				_thread=new Thread(run);
				_thread.IsBackground=true;
				_thread.Start();
			}
		}

		private void run()
		{
			try
			{
				SFuncQueue queue=_queue;

				_running=true;

				long lastTime=Ctrl.getTimer();
				int delay;
				long time;

				while(_running)
				{
					//TODO:死循环检测

					time=Ctrl.getTimer();
					delay=(int)(time - lastTime);
					lastTime=time;

					//先时间
					tick(delay);

					//再事务
					queue.runOnce();

					//最后睡
					Thread.Sleep(_sleepTime);
				}
			}
			catch(ThreadAbortException)
			{
				//线程结束
			}
			catch(Exception e)
			{
				Ctrl.printExceptionForIO(e);
			}

		}

		/// <summary>
		/// 启动
		/// </summary>
		public void start()
		{
			_thread.Start();
		}

		/// <summary>
		/// 退出
		/// </summary>
		public void exit()
		{
			_running=false;
		}

		protected virtual void tick(int delay)
		{
			if(_tickFunc!=null)
				_tickFunc(delay);
		}

		/// <summary>
		/// 添加执行方法
		/// </summary>
		public void addFunc(Action func)
		{
			_queue.add(func);
		}
	}
}