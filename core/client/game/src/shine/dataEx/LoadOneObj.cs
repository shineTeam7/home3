using UnityEngine;

namespace ShineEngine
{
	/// <summary>
	/// 加载单个对象
	/// </summary>
	public class LoadOneObj:PoolObject
	{
		/** 资源序号 */
		public int id;
		/** 资源信息组 */
		public SList<LoadInfoObj> infos=new SList<LoadInfoObj>();

		//解析部分
		/** 资源异步请求对象 */
		public AssetBundleRequest assetBundleRequest;

		/** 是否移除失效 */
		public bool removed=false;

		public LoadOneObj()
		{

		}

		/** 析构 */
		public override void clear()
		{
			removed=false;
			assetBundleRequest=null;
			infos.clear();
		}
	}
}