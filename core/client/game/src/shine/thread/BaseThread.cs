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

		/** tick间隔 */
		protected int _tickDelay=ShineSetting.defaultThreadSleepDelay;

		/** 睡多久(ms) */
		protected int _sleepTime=ShineSetting.defaultThreadSleepDelay;
		/** 休息中 */
		private volatile bool _sleeping=false;

		private SFuncQueue _queue=new SFuncQueue();

		private Action _runCall;

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

		public void setRunCall(Action func)
		{
			_runCall=func;
		}

		public void setSleepTime(int time)
		{
			_sleepTime=time;
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
				long time;
				int delay;
				int tickMax=_tickDelay;
				int tickTime=0;

				while(_running)
				{
					time=Ctrl.getTimer();
					delay=(int)(time - lastTime);
					lastTime=time;

					//防止系统时间改小
					if(delay<0)
						delay=0;

					if(delay>0)
					{
						if((tickTime+=delay)>=tickMax)
						{
							try
							{
								tick(tickTime);
							}
							catch(Exception e)
							{
								Ctrl.errorLog("线程tick出错",e);
							}

							tickTime=0;
						}

					}

					try
					{
						runEx();
					}
					catch(Exception e)
					{
						Ctrl.errorLog("线程runEx出错",e);
					}

					//再事务
					queue.runOnce();

					//最后睡
					threadSleep();
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

		/** 休息 */
		protected void threadSleep()
		{
			_sleeping=true;

			try
			{
				Thread.Sleep(_sleepTime);
			}
			catch
			{
				Ctrl.print("interrupt");
			}

			_sleeping=false;

			// int delay=(int)(Ctrl.getTimer()-t);
			//
			// if(delay>0)
			// {
			// 	_restTime+=delay;
			// }
		}

		/** 方法唤醒 */
		public void notifyFunc()
		{

			if(!ShineSetting.needThreadNotify)
				return;

			if(_sleeping)
			{
				_thread.Interrupt();
			}
		}

		protected virtual void runEx()
		{
			if(_runCall!=null)
				_runCall();
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