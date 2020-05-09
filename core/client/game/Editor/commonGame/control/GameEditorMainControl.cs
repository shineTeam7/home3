using System;
using ShineEngine;
using UnityEditor;
using UnityEngine;

/// <summary>
/// 游戏主入口
/// </summary>
public class GameEditorMainControl
{
	public virtual void init()
	{
		initControl();
		EditorC.factory.createMainApp().startE();
	}

	public virtual void initControl()
	{

	}

	public virtual void showSceneEditorWindow()
	{
		EditorWindow.GetWindowWithRect<SceneEditorWindow>(new Rect(0,0,400,300),true,"场景编辑器",true).show();
	}
}