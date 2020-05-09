using System;

namespace ShineEngine
{
	/// <summary>
	/// 缓动类
	/// </summary>
	public class TweenBase<T>
	{
		private TweenFactoryBase<T> _factory;
		public int index=-1;

		protected T _start;
		protected T _end;
		private int _delay;
		private int _time;

		private bool _needRecycle;
		private bool _needRevert;

		private Func<T,T,float,T> _getValueFunc;
		private Action<T> _func;
		private Action _overFunc;

		/**
		 * 缓动函数，需要传入4个参数，t，b，c，d
		 * t time 缓动已经过时间(单位：毫秒)
		 * b start (must always be 0) 启始值，永远传0
		 * c change (must always be 1) 改变值，永远传1
		 * d duration 缓动总时间(单位：毫秒)
		 */
		private Func<float,float,float,float,float> _easeFunc;

		public TweenBase()
		{

		}

		public void setFactory(TweenFactoryBase<T> factory)
		{
			_factory=factory;
		}

		/** 是否生效中 */
		public bool isEnbaled()
		{
			return index!=-1;
		}

		/** 初始化(系统用) */
		public void init(int index,Func<T,T,float,T> getValueFunc,T start,T end,int delay,Action<T> func,Action overFunc,int easeType)
		{
			if(delay<=0)
			{
				Ctrl.throwError("tween delay不能小于0");
			}

			this.index=index;
			_start=start;
			_end=end;
			_delay=delay;
			_getValueFunc=getValueFunc;
			_func=func;
			_overFunc=overFunc;
			_easeFunc=Ease.getEaseFunc(easeType);

			_time=0;
		}

		public void dispose()
		{
			_func=null;
			_overFunc=null;
			_easeFunc=null;
			index=-1;

			_needRecycle=false;
			_needRevert=false;
		}

		public void onFrame(int delay)
		{
			if(index==-1)
				return;

			if((_time+=delay)>=_delay)
			{
				_func(_end);

				Action overFunc=_overFunc;

				if(_needRecycle)
				{
					if(_needRevert)
					{
						T temp=_start;
						_start=_end;
						_end=temp;
					}
					else
					{
						_func(_start);
					}

					_time=0;
				}
				else
				{
					_factory.kill(index);

					if(overFunc!=null)
					{
						overFunc();
					}
				}
			}
			else
			{
				_func(_getValueFunc(_start,_end,_easeFunc(_time,0,1,_delay)));
			}
		}

		/** 设置需要循环 */
		public TweenBase<T> setRecycle(bool needRevert)
		{
			_needRecycle=true;
			_needRevert=needRevert;

			return this;
		}
	}
}