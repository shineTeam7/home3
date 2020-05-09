using System;
using ShineEngine;
using UnityEngine;

/// <summary>
/// 触摸控制
/// </summary>
[Hotfix]
public class TouchControl
{
	public void init()
	{
		STouchControl.setMouseFunc(onMouse);
		STouchControl.setTouchFunc(onTouch);
		STouchControl.setMouseWheelFunc(onMouseWheel);

		STouchControl.touchOneFunc+=onTouchOne;
	}

	/** 初始化主摄像机 */
	public void initByConfig()
	{
		CameraToolConfig config=new CameraToolConfig();
		config.minDistance=Global.cameraMinDistance;
		config.maxDistance=Global.cameraMaxDistance;
		config.d25AxisX=Global.camera25DAngle*MathUtils.DegToRad;
		config.minAxisX=Global.cameraMinAxisXAngle*MathUtils.DegToRad;
		config.maxAxisX=Global.cameraMaxAxisXAngle*MathUtils.DegToRad;
		config.tweenTime=Global.cameraTweenTime;

		//TODO:这个可以以后只设置一遍
		//设置基础配置
		CameraControl.mainCamera.setConfig(config);
	}


	/** 鼠标 */
	protected virtual void onMouse(bool isDown)
	{
		Scene scene=GameC.scene.getScene();

		if(isDown)
		{
			//在ui上
			if(STouchControl.isTouchOnUI())
			{
				return;
			}
		}

		if(scene!=null && scene.isInit)
		{
			scene.camera.onMouse(isDown);
		}
	}

	/** 鼠标滚轮 */
	protected virtual void onMouseWheel(float wheel)
	{
		Scene scene=GameC.scene.getScene();

		//在ui上
		if(STouchControl.isTouchOnUI())
		{
			return;
		}

		if(scene!=null && scene.isInit)
		{
			scene.camera.onMouseWheel(wheel);
		}
	}

	/** 触摸 */
	protected virtual void onTouch(Touch touch,bool isDown)
	{
		Scene scene=GameC.scene.getScene();

		if(isDown)
		{
			//在ui上
			if(STouchControl.isTouchOnUI(touch.fingerId))
			{
				return;
			}
		}

		if(scene!=null && scene.isInit)
		{
			scene.camera.onTouch(touch,isDown);
		}
	}

	/** 单点响应 */
	protected virtual void onTouchOne(bool isDown)
	{

	}
}