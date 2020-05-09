using System;

namespace ShineEngine
{
	/** 泛型容器 */
	public class UIGenericityContainer<T>:UIObject where T:UIObject,new()
	{
		private Func<UIObject> _createFunc;

		/** 设置创建回调 */
		public void setCreateFunc(Func<UIObject> func)
		{
			_createFunc=func;
		}

		protected T createOne()
		{
			if(_createFunc!=null)
				return (T)_createFunc();

			return new T();
		}
	}
}