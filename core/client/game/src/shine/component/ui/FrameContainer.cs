using UnityEngine;
using UnityEngine.UI;

namespace ShineEngine
{

	public class FrameContainer:MonoBehaviour
	{
		/** 当前帧 */
		private int _frame;

		/** 帧列表 */
		private SList<GameObject> _frames;

		private void OnDestroy()
		{
			Ctrl.print("destroy FrameContainer");
		}

		/** 刷新重置当前帧列表 */
		public void resetFrameList()
		{

		}
	}
}