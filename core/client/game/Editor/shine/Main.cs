using System.Collections.Generic;
using ShineEngine;
using UnityEngine;
using UnityEngine.SceneManagement;

namespace ShineEditor
{
	/// <summary>
	///
	/// </summary>
	public class Main:MonoBehaviour
	{
		private void Start()
		{
			//启动
			ShineSetup.setup(this.gameObject);

			Ctrl.print("启动");

			// SceneManager.sceneLoaded+=onSceneLoaded;
//			UnityEditor.EditorApplication.LoadLevelInPlayMode("Assets/source/art/Scene/Yw_LiangShan.unity");
			// loadFromBundle();
		}

	}
}