using System;

namespace ShineEngine
{
	/// <summary>
	/// 计时器驱动
	/// </summary>
	public class TimeDriver
	{
		private static readonly TimeDriver _instance=new TimeDriver();

		/// <summary>
		/// 单例
		/// </summary>
		public static TimeDriver instance
		{
			get {return _instance;}
		}

		//timeEx
		/** 时间执行组 */
		private IntObjectMap<TimeExecuteData> _timeExDic=new IntObjectMap<TimeExecuteData>();
		/** 计数 */
		private IndexMaker _timeExIndexMaker=new IndexMaker();

		//frame
		/** 每帧调用 */
		private IntObjectMap<Action<int>> _frameDic=new IntObjectMap<Action<int>>();
		/** 每帧调用计数 */
		private IndexMaker _frameIndexMaker=new IndexMaker();

		/** update */
		private IntObjectMap<Action> _updateDic=new IntObjectMap<Action>();
		/** fixedUpdate */
		private IntObjectMap<Action> _fixedUpdateDic=new IntObjectMap<Action>();

		//callLater
		/** 延时调用 */
		private SList<Action> _callLaters=new SList<Action>();
		/** callLater锁 */
		private bool _callLaterForEaching=false;
		/** 延时调用临时组 */
		private SList<Action> _callLaterTemps=new SList<Action>();

		private TimeDriver()
		{

		}

		public void tick(int delay)
		{
			IntObjectMap<TimeExecuteData> timeExDic;

			if(!(timeExDic=_timeExDic).isEmpty())
			{
				foreach(TimeExecuteData tData in timeExDic)
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
				}
			}


			//frame

			IntObjectMap<Action<int>> frameDic;

			if(!(frameDic=_frameDic).isEmpty())
			{
				foreach(Action<int> v in frameDic)
				{
					try
					{
						v(delay);
					}
					catch(Exception e)
					{
						Ctrl.errorLog(e);
					}
				}
			}

			//callLater(放在最下面，用来在一次tick的最后执行逻辑)

			SList<Action> callLaters;

			if(!(callLaters=_callLaters).isEmpty())
			{
				_callLaterForEaching=true;

				Action[] values=callLaters.getValues();

				for(int i=0,len=callLaters.size();i<len;++i)
				{
					try
					{
						values[i]();
					}
					catch(Exception e)
					{
						Ctrl.errorLog(e);
					}

					values[i]=null;
				}

				callLaters.justClearSize();

				_callLaterForEaching=false;

				SList<Action> callLaterTemps;

				if(!(callLaterTemps=_callLaterTemps).isEmpty())
				{
					callLaters.addAll(callLaterTemps);

					callLaterTemps.clear();
				}
			}
		}

		/** update */
		public void update()
		{
			IntObjectMap<Action> frameDic;

			if(!(frameDic=_updateDic).isEmpty())
			{
				foreach(Action v in frameDic)
				{
					try
					{
						v();
					}
					catch(Exception e)
					{
						Ctrl.errorLog(e);
					}
				}
			}
		}

		/** 固定Update */
		public void fixedUpdate()
		{
			IntObjectMap<Action> frameDic;

			if(!(frameDic=_fixedUpdateDic).isEmpty())
			{
				foreach(Action v in frameDic)
				{
					try
					{
						v();
					}
					catch(Exception e)
					{
						Ctrl.errorLog(e);
					}
				}
			}
		}

		/// <summary>
		/// 析构
		/// </summary>
		public void dispose()
		{

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
				Ctrl.throwError("timeIndex不能<=0");

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
					tData.intFunc(tData.intArg);
				}
				else
				{
					tData.func();
				}
			}
			catch(Exception e)
			{
				Ctrl.errorLog(e);
			}
		}

		/// <summary>
		/// 暂停时间ex
		/// </summary>
		public void pauseTimeEx(int index,bool value)
		{
			TimeExecuteData tData=_timeExDic.get(index);

			if(tData==null)
				return;

			tData.pause=value;
		}

		/// <summary>
		/// 延迟delay毫秒,执行方法
		/// </summary>
		public int setTimeOut(Action func,int delay)
		{
			if(delay<=0)
			{
				Ctrl.throwError("传入的时间间隔<=0",delay);
				return -1;
			}

			TimeExecuteData tData=new TimeExecuteData();

			tData.func=func;
			tData.timeMax=delay;
			tData.isGoOn=false;

			return addTimeEx(tData);
		}

		/// <summary>
		/// 取消timeOut
		/// </summary>
		public void clearTimeOut(int index)
		{
			removeTimeEx(index);
		}

		/// <summary>
		/// 立即结束timeOut
		/// </summary>
		public void finishTimeOut(int index)
		{
			TimeExecuteData tData=_timeExDic.get(index);

			if(tData==null)
				return;

			clearTimeOut(index);

			executeTimeExFunc(tData);
		}

		/// <summary>
		/// 间隔delay毫秒，执行方法
		/// </summary>
		public int setIntervalFixed(Action func,int delay)
		{
			if(delay<=0)
			{
				Ctrl.throwError("传入的时间间隔<=0");
				return -1;
			}

			TimeExecuteData tData=new TimeExecuteData();

			tData.func=func;
			tData.timeMax=delay;
			tData.isGoOn=true;
			tData.isCut=false;

			return addTimeEx(tData);
		}

		/// <summary>
		/// 间隔delay毫秒,执行方法(如掉帧,只回调一次)
		/// </summary>
		public int setInterval(Action<int> func,int delay)
		{
			if(delay<=0)
			{
				Ctrl.throwError("传入的时间间隔<=0",delay);
				return -1;
			}

			TimeExecuteData tData=new TimeExecuteData();

			tData.intFunc=func;
			tData.timeMax=delay;
			tData.isGoOn=true;
			tData.isCut=true;

			return addTimeEx(tData);
		}

		/// <summary>
		/// 重置timeEx
		/// </summary>
		public void resetTimeEx(int index,int delay)
		{
			TimeExecuteData tData=_timeExDic.get(index);

			if(tData==null)
				return;

			tData.time=0;
			tData.timeMax=delay;
		}

		/// <summary>
		/// 取消间隔
		/// </summary>
		public void clearInterval(int index)
		{
			removeTimeEx(index);
		}

		//frame

		/// <summary>
		/// 设置每帧调用
		/// </summary>
		public int setFrame(Action<int> func)
		{
			int index=_frameIndexMaker.get();

			_frameDic.put(index,func);

			return index;
		}

		/// <summary>
		/// 取消每帧调用
		/// </summary>
		public void clearFrame(int index)
		{
			_frameDic.remove(index);
		}

		//update

		/// <summary>
		/// 设置每帧调用(update)
		/// </summary>
		public int setUpdate(Action func)
		{
			int index=_frameIndexMaker.get();

			_updateDic.put(index,func);

			return index;
		}

		/// <summary>
		/// 取消每帧调用(update)
		/// </summary>
		public void clearUpdate(int index)
		{
			_updateDic.remove(index);
		}

		//fixedUpdate

		/// <summary>
		/// 设置固定每帧调用(fixedUpdate)
		/// </summary>
		public int setFixedUpdate(Action func)
		{
			int index=_frameIndexMaker.get();

			_fixedUpdateDic.put(index,func);

			return index;
		}

		/// <summary>
		/// 取消固定每帧调用(fixedUpdate)
		/// </summary>
		public void clearFixedUpdate(int index)
		{
			_fixedUpdateDic.remove(index);
		}

		//callLater
		/// <summary>
		/// 延迟调用
		/// </summary>
		public void callLater(Action func)
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
			public Action func;

			/** 回调 */
			public Action<int> intFunc;

			/** 整形参 */
			public int intArg;

			/** 当前时间 */
			public int time;

			/** 总时间 */
			public int timeMax;

			/** 用来决定是interval还是timeOut */
			public bool isGoOn=false;

			/** 在interval的情况下,是否每次只调用一遍 */
			public bool isCut=false;

			/** 是否暂停 */
			public bool pause=false;
		}
	}
}