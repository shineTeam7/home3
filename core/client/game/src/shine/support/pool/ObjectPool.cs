using System;
using System.Threading;
using UnityEngine;

namespace ShineEngine
{
	/// <summary>
	/// 对象池(线程非安全)
	/// </summary>
	public class ObjectPool<T>
	{
		/** 是否开启 */
		protected bool _enable=true;
		/** 入池是否需要执行clear */
		private bool _needClear=true;
		/** 列表 */
		private SQueue<T> _queue;
		/** 最大尺寸 */
		private int _maxSize;

		private Func<T> _createFunc;

		private SSet<T> _checkSet;

		private SMap<T,string> _callStackDic;

		/** 析构函数 */
		private Action<T> _releaseFunc;

		public ObjectPool(Func<T> createFunc):this(createFunc,ShineSetting.defaultPoolSize)
		{

		}

		public ObjectPool(Func<T> createFunc,int size)
		{
			_maxSize=size;

			_queue=new SQueue<T>();

			_createFunc=createFunc;

			if(ShineSetting.openCheck)
			{
				_checkSet=new SSet<T>();
				_callStackDic=new SMap<T,string>();
			}

			if(!ShineSetting.useObjectPool)
			{
				_enable=false;
			}
		}

		public void setEnable(bool value)
		{
			if(!ShineSetting.useObjectPool)
			{
				_enable=false;
			}
			else
			{
				_enable=value;
			}
		}

		public void setNeedClear(bool value)
		{
			_needClear=value;
		}

		/** 设置析构回调 */
		public void setReleaseFunc(Action<T> func)
		{
			_releaseFunc=func;
		}

		/** 清空 */
		public void clear()
		{
			if(_queue.isEmpty())
				return;

			foreach(T v in _queue)
			{
				if(_releaseFunc!=null)
				{
					_releaseFunc(v);
				}
			}

			_queue.clear();

			if(ShineSetting.openCheck)
			{
				_checkSet.clear();
				_callStackDic.clear();
			}

		}


		/// <summary>
		/// 取出一个
		/// </summary>
		public virtual T getOne()
		{
			//有
			if(!_queue.isEmpty())
			{
				T obj=_queue.pop();

				if(ShineSetting.openCheck)
				{
					_checkSet.remove(obj);
					_callStackDic.remove(obj);
				}

				return obj;
			}
			else
			{
				return _createFunc();
			}

		}

		/// <summary>
		/// 放回一个
		/// </summary>
		public virtual void back(T obj)
		{
			if(obj==null)
			{
				Ctrl.throwError("对象池添加空对象");
				return;
			}

			if(!_enable || _queue.size() >= _maxSize)
			{
				if(_releaseFunc!=null)
				{
					_releaseFunc(obj);
				}

				return;
			}

			if(_needClear)
			{
				if(obj is IPoolObject)
				{
					((IPoolObject)obj).clear();
				}
			}


			if(ShineSetting.openCheck)
			{
				if(_checkSet.contains(obj))
				{
					Ctrl.print("上次调用",_callStackDic.get(obj));
					Ctrl.throwError("对象池重复添加!",obj);
					return;
				}

				_checkSet.add(obj);
				string stackTrace=Ctrl.getStackTrace();
				stackTrace=stackTrace.Replace("\n"," ");
				_callStackDic.put(obj,stackTrace);
			}


			_queue.add(obj);
		}
	}
}