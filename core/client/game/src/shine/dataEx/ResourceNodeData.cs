using UnityEngine;

namespace ShineEngine
{
	/** 资源节点数据 */
	public class ResourceNodeData:PoolObject
	{
		/** id(-1来标记回收) */
		public int id=-1;
		/** 包信息(包资源才有) */
		public BundleInfoData packInfo;
		/** 包ID(没有包为-1) */
		public int packID=-1;
		/** 引用计数 */
		public int refCount=0;
		/** 被依赖计数 */
		public int beDependCount=0;
		/** 超时计时(s) */
		public int timeOut=0;
		/** 资源 */
		public object asset;

		public override void clear()
		{
			id=-1;
			asset=null;

			packInfo=null;
			packID=-1;
			timeOut=0;
			refCount=0;
			beDependCount=0;
		}

		/** 是否有资源 */
		public bool hasResource()
		{
			return asset!=null;
		}

		/** 是否包资源 */
		public bool isPack()
		{
			return packInfo!=null;
		}

		/** 是否无引用 */
		public bool isFree()
		{
			return refCount==0 && beDependCount==0;
		}

		/** 刷新保留时间 */
		public void refreshTimeOut()
		{
			timeOut=ShineSetting.resourceKeepTime;
		}

		public bool isEnable()
		{
			return id>0;
		}

		/** 获取自身包ID(自身为包或独立资源) */
		public int getSingleOrPackID()
		{
			if(packID!=-1 && packID!=id)
				return -1;

			return id;
		}
	}
}