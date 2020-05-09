using System;

namespace ShineEngine
{
	/// <summary>
	/// 需要热更的类标记(只会生成adapter)
	/// </summary>
	[AttributeUsage(AttributeTargets.Class)]
	public class HotfixAdapter:Attribute
	{

	}
}