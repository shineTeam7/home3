using System;
using ShineEngine;
using UnityEngine;

/// <summary>
/// 固定角度摄像机(2.5D)
/// </summary>
public class SceneCameraFixedLogic:SceneCameraLogic
{
	public override void init()
	{
		base.init();

		_mainCamera.mode=CameraModeType.D25;
		_mainCamera.distance=Global.cameraDefaultDistance;
		_mainCamera.axisX=_mainCamera.config.d25AxisX;
		_mainCamera.axisY=0f;
	}

	// public override void updateHeroPos(in Vector3 vec)
	// {
	// 	_mainCamera.setTargetPos(vec);
	// }
}