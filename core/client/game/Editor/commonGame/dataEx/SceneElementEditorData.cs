using System;
using ShineEngine;
using UnityEngine;

/// <summary>
/// 场景元素数据
/// </summary>
public class SceneElementEditorData
{
	/** 配置 */
	public ScenePlaceElementConfig config;

	/** 显示元素 */
	public GameObject gameObject;

	/** 是否临时单位 */
	public bool isTemp=false;

	/** 是否选择 */
	public bool isPicked=false;

	//config
	/** 是否可拾取 */
	public bool canPick=true;
}