using System;

namespace ShineEngine
{
	/** 加载资源包数据 */
	public class LoadPackObj:PoolObject
	{
		/** 资源序号 */
		public int id;
		/** 单个信息组 */
		public SList<LoadOneObj> objs=new SList<LoadOneObj>();
		/** 加载优先级 */
		public int priority;
		/** 是否一直加载 */
		public bool needForever;
		/** 依赖组 */
		public SSet<LoadPackObj> depends=new SSet<LoadPackObj>();
		/** 被依赖组 */
		public SSet<LoadPackObj> beDepends=new SSet<LoadPackObj>();

		//loader
		/** 通道 */
		public int index;
		/** 是否移除 */
		public bool removed=false;

		public LoadPackObj()
		{

		}

		/** 析构 */
		public override void clear()
		{
			removed=false;
			objs.clear();
			depends.clear();
			beDepends.clear();
		}

		/** 是否依赖和被依赖全空 */
		public bool isDependAndBeDependEmpty()
		{
			return depends.isEmpty() && beDepends.isEmpty();
		}

		//传递组数据
		public void swap(LoadPackObj target)
		{
			SList<LoadOneObj> tObjs=objs;
			objs=target.objs;
			target.objs=tObjs;

			SSet<LoadPackObj> tDepends=depends;
			depends=target.depends;
			target.depends=tDepends;

			SSet<LoadPackObj> tBeDepends=beDepends;
			beDepends=target.beDepends;
			target.beDepends=tBeDepends;
		}
	}
}