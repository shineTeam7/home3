using UnityEngine;

namespace ShineEngine
{
	/// <summary>
	/// 摄像机控制
	/// </summary>
	public class CameraControl
	{
		/** 主摄像机 */
		public static CameraTool mainCamera=new CameraTool();

		public static void init()
		{
			GameObject gameObject=GameObject.Find("Main Camera");

			if(gameObject==null)
			{
				Ctrl.print("找不到主摄像机");
				return;
			}

			mainCamera.init(gameObject.GetComponent<Camera>());
		}

		public static void dispose()
		{
			mainCamera.dispose();
		}
	}
}