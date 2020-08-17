using System;

namespace ShineEngine
{
	/// <summary>
	/// 需要热更的类标记(会生成adapter和工厂方法)
	/// </summary>
	[AttributeUsage(AttributeTargets.Class)]
	public class Hotfix:Attribute
	{
		/** 是否需要创建工厂方法 */
		public bool needFactory=true;
		/** 是否需要传递给子对象 */
		public bool needChildren=true;

		public Hotfix()
		{

		}
	}
}