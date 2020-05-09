using System;

namespace ShineEngine
{
	/// <summary>
	/// 需要热更的类标记(会生成adapter和工厂方法)
	/// </summary>
	[AttributeUsage(AttributeTargets.Class)]
	public class Hotfix:Attribute
	{

	}
}