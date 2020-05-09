using ShineEngine;

namespace ShineEditor
{
	/// <summary>
	///
	/// </summary>
	public class BundleInfoExData
	{
		/** id */
		public int id;

		/** 名字 */
		public string name;

		/** 资源序号组 */
		public IntList assets=new IntList();

		/** 依赖序号组 */
		public IntList depends=new IntList();
	}
}