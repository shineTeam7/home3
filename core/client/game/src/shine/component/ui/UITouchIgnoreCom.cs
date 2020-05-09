using UnityEngine;

namespace ShineEngine
{
	/** UI touch忽略 */
	public class UITouchIgnoreCom:MonoBehaviour,ICanvasRaycastFilter
	{
		public bool IsRaycastLocationValid(Vector2 sp,Camera eventCamera)
		{
			return false;
		}
	}
}