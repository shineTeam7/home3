namespace ShineEngine
{
	/// <summary>
	/// UI元素
	/// </summary>
	[Hotfix(needChildren = false)]
	public class UIElement:UIContainer
	{
		public UIElement()
		{
			_type=UIElementType.Element;
		}
	}
}