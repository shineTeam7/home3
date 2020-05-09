using System;

namespace ShineEngine
{
	public class WeakReferenceDic<T>
	{
		private SSet<WeakReference> _dic=new SSet<WeakReference>();

		public void add(T value)
		{
			_dic.add(new WeakReference(value));
		}

		public void check()
		{
			foreach(WeakReference weakReference in _dic)
			{
				if(!weakReference.IsAlive)
				{
					_dic.remove(weakReference);
				}
			}
		}

		public int size()
		{
			return _dic.size();
		}
	}
}