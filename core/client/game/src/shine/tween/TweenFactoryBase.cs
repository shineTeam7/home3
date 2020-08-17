using System;

namespace ShineEngine
{
	/** Tween工厂基类 */
	public abstract class TweenFactoryBase<T>
	{
		private ObjectPool<TweenBase<T>> _pool;
		/** 运行中字典 */
		private IntObjectMap<TweenBase<T>> _dic=new IntObjectMap<TweenBase<T>>();

		private int _indexMaker=0;

		public TweenFactoryBase()
		{
			_pool=new ObjectPool<TweenBase<T>>(()=>
			{
				TweenBase<T> tween=new TweenBase<T>();
				tween.setFactory(this);
				return tween;
			});
			_pool.setEnable(CommonSetting.viewUsePool);
		}

		public void release(TweenBase<T> tween)
		{
			_pool.back(tween);

		}

		public void tick(int delay)
		{
			foreach(TweenBase<T> v in _dic)
			{
				if(v.isEnbaled())
				{
					v.onFrame(delay);
				}
			}
		}

		/// <summary>
		/// 创建tween并执行
		/// </summary>
		public int create(T start,T end,int delay,Action<T> func,Action overFunc=null,int ease=EaseType.Linear)
		{
			return createTween(start,end,delay,func,overFunc,ease).index;
		}

		/// <summary>
		/// 创建实际tween对象，但不执行
		/// </summary>
		public TweenBase<T> createTween(T start,T end,int delay,Action<T> func,Action overFunc=null,int ease=EaseType.Linear)
		{
			TweenBase<T> tween=_pool.getOne();
			int index=++_indexMaker;

			tween.init(index,getValueFunc,start,end,delay,func,overFunc,ease);
			_dic.put(index,tween);

			return tween;
		}

		public void kill(int index)
		{
			TweenBase<T> tween;

			if((tween=_dic.remove(index))!=null)
			{
				tween.dispose();
				_pool.back(tween);
			}
		}

		/** this function should be override */
		protected virtual T getValueFunc(T start,T end,float progress)
		{
			return default(T);
		}
	}
}