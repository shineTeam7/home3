using System;

namespace ShineEngine
{
	/** 加载信息对象(一波加载数据) */
	public class LoadInfoObj:PoolObject
	{
		/** 加载编号(给逻辑用) */
		public int index;

		/** 所需加载总数(资源个数) */
		public int max;
		/** 当前完成数目(资源个数) */
		public int num;

		/** 完成回调 */
		public Action complete;

		//TODO:资源加载进度

		public LoadInfoObj()
		{

		}

		/** 析构 */
		public override void clear()
		{
			complete=null;
		}
	}
}