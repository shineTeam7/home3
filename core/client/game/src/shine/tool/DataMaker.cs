using System;

namespace ShineEngine
{
	/// <summary>
	/// 数据构造器
	/// </summary>
	[Hotfix(needChildren = false)]
	public class DataMaker:ArrayDic<Func<BaseData>>
	{
		/** 通过id取得Data类型 */
		public BaseData getDataByID(int dataID)
		{
			Func<BaseData> func;

			if((func=get(dataID))==null)
				return null;

			return func();
		}

		/** 是否存在某id */
		public bool contains(int dataID)
		{
			return get(dataID)!=null;
		}
	}
}