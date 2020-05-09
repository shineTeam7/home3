namespace ShineEngine
{
	/// <summary>
	/// 容器
	/// </summary>
	public class UIContainer:UIObject
	{
		/** 子项组 */
		private SMap<string,UIObject> _childrenDic=new SMap<string,UIObject>();

		public UIContainer()
		{
			_type=UIElementType.Container;
		}

		protected override void dispose()
		{
			base.dispose();

			if(!_childrenDic.isEmpty())
			{
				UIObject[] values;
				UIObject v;

				for(int i=(values=_childrenDic.getValues()).Length-1;i>=0;--i)
				{
					if((v=values[i])!=null)
					{
						v.doDispose();
					}
				}
			}

		}

		protected void registChild(UIObject child)
		{
			_childrenDic.put(child.name,child);
			child.setParent(this);
		}

		/** 获取子项 */
		public UIObject getChild(string name)
		{
			return _childrenDic.get(name);
		}

		/** 获取子项 */
		public T getChild<T>(string name) where T:UIObject
		{
			return (T)_childrenDic.get(name);
		}

		public UIContainer getNode(string name)
		{
			return getChild<UIContainer>(name);
		}
	}
}