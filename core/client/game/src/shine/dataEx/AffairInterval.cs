using System;

namespace ShineEngine
{
	/** 间隔重放事务 */
	public class AffairInterval
	{
		private Action _func;

		private int _time;

		private int _index=-1;

		public AffairInterval(Action func,int time)
		{
			_func=func;
			_time=time;
		}

		/** 当前是否运行中 */
		public bool isRunning()
		{
			return _index!=-1;
		}

		/** 开始间隔执行 */
		public void start()
		{
			if(_index==-1)
			{
				_index=TimeDriver.instance.setInterval(onInterval,_time);
			}
		}

		/** 停止间隔执行 */
		public void stop()
		{
			if(_index!=-1)
			{
				TimeDriver.instance.clearInterval(_index);
				_index=-1;
			}
		}

		/** 开始并执行一次 */
		public void startAndExecuteOnce()
		{
			start();
			onInterval(0);
		}

		private void onInterval(int delay)
		{
			_func();
		}
	}
}