using System;
using ShineEngine;
using UnityEngine;

/// <summary>
/// 镜头控制类
/// </summary>
public class SceneCameraLogic:SceneLogicBase
{
	/** 主摄像机 */
	protected CameraTool _mainCamera;

	public SceneCameraLogic()
	{

	}

	public override void construct()
	{

	}

	public override void init()
	{
		_mainCamera=CameraControl.mainCamera;
	}

	public override void dispose()
	{

	}

	/** 主摄像机 */
	public CameraTool mainCamera
	{
		get {return _mainCamera;}
	}

	public override void onFrame(int delay)
	{

	}

	/** 鼠标 */
	public virtual void onMouse(bool isDown)
	{

	}

	/** 触摸 */
	public virtual void onTouch(Touch touch,bool isDown)
	{

	}

	/** 鼠标滚轮 */
	public virtual void onMouseWheel(float wheel)
	{

	}

	/** 键盘按下 */
	public virtual void onKey(KeyCode code,bool isDown)
	{

	}

	/** 更新主角位置(显示位置) */
	public virtual void updateHeroPos(in Vector3 vec)
	{

	}

	/** 回归默认 */
	public virtual void cameraToDefault()
	{

	}

	/** 点是否在摄像机范围内 */
	public bool isPointInView(Vector3 vec)
	{
		Transform camTransform = _mainCamera.camera.transform;
		Vector2 viewPos = _mainCamera.camera.WorldToViewportPoint(vec);
		Vector3 dir = (vec - camTransform.position).normalized;
		float dot = Vector3.Dot(camTransform.forward, dir);     //判断物体是否在相机前面

		if (dot > 0 && viewPos.x >= 0 && viewPos.x <= 1 && viewPos.y >= 0 && viewPos.y <= 1)
			return true;

		return false;
	}
}