using ShineEngine;
using UnityEditor;
using UnityEngine;
using ShineEditor;

/// <summary>
///
/// </summary>
public class GameMenuControl
{
	[MenuItem(ShineToolGlobal.menuRoot + "/other/生成Reporter",false,500)]
	static void createReporter()
	{
		ReporterEditor.CreateReporter();
		Ctrl.print("OK");
	}

	//--//


	[MenuItem(ShineToolGlobal.menuRoot + "/Scene/场景编辑器",false,520)]
	static void openSceneEditor()
	{
		EditorC.main.showSceneEditorWindow();
	}

	[MenuItem(ShineToolGlobal.menuRoot + "/Scene/清空场景编辑器数据",false,521)]
	static void clearSceneEditorData()
	{
		SceneEditorWindow.clearSceneEditorData();
	}
}