
using ShineEngine;

public class GameEditorFactoryControl
{
	public virtual MainApp createMainApp()
	{
		return new MainApp();
	}

	public virtual GameEditorMainControl createMainControl()
	{
		return new GameEditorMainControl();
	}

	public virtual SceneEditorWindow createSceneEditorWindow()
	{
		return new SceneEditorWindow();
	}
}