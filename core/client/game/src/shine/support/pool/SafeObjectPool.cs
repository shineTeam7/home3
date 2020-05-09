using System;

namespace ShineEngine
{
	/// <summary>
	/// 对象池(线程安全)
	/// </summary>
	public class SafeObjectPool<T>:ObjectPool<T>
	{
		public SafeObjectPool(Func<T> createFunc):base(createFunc)
		{
		}

		public SafeObjectPool(Func<T> createFunc,int size):base(createFunc,size)
		{
		}

		public override T getOne()
		{
			lock(this)
			{
				return base.getOne();
			}
		}

		public override void back(T obj)
		{
			lock(this)
			{
				base.back(obj);
			}
		}
	}
}