using UnityEngine;

namespace ShineEngine
{
	/// <summary>
	/// UI模型
	/// </summary>
	[Hotfix(needChildren = false)]
	public class UIModel:UIContainer
	{
		public UIModel()
		{
			_type=UIElementType.Model;
		}
	}
}