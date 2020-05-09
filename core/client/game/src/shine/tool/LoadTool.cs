using System;

namespace ShineEngine
{
	/** 资源加载器 */
	public class LoadTool
	{
		/** 完成回调 */
		private Action _completeCall;
		/** 加载优先级 */
		private int _priority=-1;
		/** loadControl的版本 */
		private int _loadVersion;
		/** 加载序号 */
		private int _index;

		/** 资源ID */
		private int _resourceID=-1;

		private bool _isLoading=false;

		public LoadTool()
		{

		}

		public LoadTool(Action completeCall,int priority=-1)
		{
			_completeCall=completeCall;
			_priority=priority;
		}

		private void onComplete()
		{
			_isLoading=false;

			if(_completeCall!=null)
				_completeCall();
		}

		/** 清除占用 */
		public void clear()
		{
			if(_resourceID!=-1)
			{
				++_index;

				//版本还对
				if(LoadControl.getVersion()==_loadVersion)
				{
					LoadControl.unloadOne(_resourceID);
				}

				_resourceID=-1;
			}

			_isLoading=false;
		}

		/** 获取此时资源ID */
		public int getResourceID()
		{
			return _resourceID;
		}

		public bool isLoading()
		{
			return _isLoading;
		}

		/** 加载一个 */
		public void loadOne(int id)
		{
			//-1不加载
			if(id==-1)
			{
				clear();
				//直接返回
				onComplete();
				return;
			}

			//相同资源,加载中跳过
			if(_isLoading && _resourceID==id)
				return;

			clear();

			_loadVersion=LoadControl.getVersion();
			int index=++_index;
			_isLoading=true;

			LoadControl.loadOne(_resourceID=id,()=>
			{
				if(_index==index && LoadControl.getVersion()==_loadVersion)
				{
					onComplete();
				}
			},_priority);
		}

		/** 加载一个(带回调) */
		public void loadOne(int id,Action func)
		{
			_completeCall=func;

			loadOne(id);
		}
	}
}