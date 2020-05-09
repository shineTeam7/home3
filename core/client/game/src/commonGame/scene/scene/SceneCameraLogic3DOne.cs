using ShineEngine;
using UnityEngine;
using UnityEngine.AI;

/// <summary>
/// 3D摄像机逻辑(方案一,mmo用)
/// </summary>
public class SceneCameraLogic3DOne:SceneCameraLogic
{
	/** 无 */
	private const int None=0;
	/** 单点 */
	private const int One=1;
	/** 两点 */
	private const int Two=2;

	//args
	/** 滚轮缩放速度 */
	private float _wheelSpeed;
	/** 默认距离 */
	private float _defaultDistance;
	/** 摄像机旋转速度X */
	private float _rotateSpeedX;
	/** 摄像机旋转速度Y */
	private float _rotateSpeedY;
	/** touch缩放速度 */
	private float _scaleSpeed;

	/** 触摸方式 */
	private int _touchType=None;

	//one
	private float _touchOneX;
	private float _touchOneY;
	/** 触摸点1序号 */
	private int _touchOneIndex=-1;
	/** 触摸点1是否移动过 */
	private bool _touchOneMoved;

	private float _oldAxisX;
	private float _oldAxisY;

	//two
	private float _touchTwoX;
	private float _touchTwoY;
	/** 触摸点2序号 */
	private int _touchTwoIndex=-1;

	private float _oldDistance;

	private float _touchSq;

	/** 点击计时 */
	private int _clickTime;


	public override void init()
	{
		base.init();

		//读取配置
		readConfig();
	}

	private void readConfig()
	{
		//设置当前配置
		_wheelSpeed=Global.cameraWheelSpeed;
		_defaultDistance=Global.cameraDefaultDistance;
		_rotateSpeedX=Global.cameraRotateSpeedX/1000f;
		_rotateSpeedY=Global.cameraRotateSpeedY/1000f;
		_scaleSpeed=Global.cameraScaleSpeed;
	}

	public override void onFrame(int delay)
	{
		base.onFrame(delay);

		//点击计时
		if(_clickTime>0)
		{
			if((_clickTime-=delay)<=0)
			{
				_clickTime=0;
			}
		}

		//只有第一点
		if(_mainCamera.mode!=CameraModeType.Custom)
		{
			if(_touchType==One)
			{
				Vector2 mPos;

				if(SystemControl.isPC)
				{
					mPos=STouchControl.mousePosition;
				}
				else
				{
					mPos=Input.GetTouch(_touchOneIndex).position;
				}

				//没移动过,并超范围
				if(!_touchOneMoved && Mathf.Abs(mPos.x - _touchOneX) + Mathf.Abs(mPos.y - _touchOneY)>ShineSetting.clickOffSum)
				{
					_touchOneMoved=true;
					//点击失效
					_clickTime=0;
				}

				//移动过后
				if(_touchOneMoved)
				{
					//y轴旋转
					_mainCamera.axisY=_oldAxisY + (mPos.x - _touchOneX) * _rotateSpeedY;

					//2.5D
					if(_mainCamera.mode!=CameraModeType.D25)
					{
						_mainCamera.axisX=_oldAxisX - (mPos.y - _touchOneY) * _rotateSpeedX;
					}
				}
			}
			else if(_touchType==Two)
			{
				float dsq=MathUtils.distanceBetweenPoint(_touchOneX,_touchOneY,_touchTwoX,_touchTwoY)-_touchSq;

				_mainCamera.distance=_oldDistance + dsq*_scaleSpeed;
			}
		}
	}

	public override void updateHeroPos(in Vector3 vec)
	{
		_mainCamera.setTargetPos(vec);
	}

	private void touchOneUp(Vector3 pos)
	{
		_touchType=None;
		_touchOneIndex=-1;

		//还在
		if(_clickTime>0)
		{
			_clickTime=0;

			//没移动过
			if(!_touchOneMoved)
			{
				clickScene(pos);
			}
		}
	}

	/** 主触摸 */
	public override void onMouse(bool isDown)
	{
		if(isDown)
		{
			_touchType=One;
			_clickTime=ShineSetting.clickTime;
			_touchOneMoved=false;

			Vector2 mousePosition=STouchControl.mousePosition;
			_touchOneX=mousePosition.x;
			_touchOneY=mousePosition.y;

			_oldAxisX=_mainCamera.axisX;
			_oldAxisY=_mainCamera.axisY;
		}
		else
		{
			if(_touchType==One)
			{
				touchOneUp(STouchControl.mousePosition);
			}
		}
	}

	/** 触摸事件 */
	public override void onTouch(Touch touch,bool isDown)
	{
		if(isDown)
		{
			if(_touchType==None)
			{
				_touchType=One;
				_clickTime=ShineSetting.clickTime;
				_touchOneMoved=false;

				Vector2 vec=touch.position;

				_touchOneX=vec.x;
				_touchOneY=vec.y;
				_touchOneIndex=touch.fingerId;

				_oldAxisX=_mainCamera.axisX;
				_oldAxisY=_mainCamera.axisY;
			}
			else if(_touchType==One)
			{
				_touchType=Two;

				Vector2 vec=touch.position;

				_touchTwoX=vec.x;
				_touchTwoY=vec.y;
				_touchTwoIndex=touch.fingerId;

				_oldDistance=_mainCamera.distance;

				_touchSq=MathUtils.distanceBetweenPoint(_touchOneX,_touchOneY,_touchTwoX,_touchTwoY);
			}
		}
		else
		{
			if(_touchType==One)
			{
				if(_touchOneIndex==touch.fingerId)
				{
					touchOneUp(touch.position);
				}
			}
			else if(_touchType==Two)
			{
				//是任意一点点
				if(_touchOneIndex==touch.fingerId || _touchTwoIndex==touch.fingerId)
				{
					_touchType=None;
					_touchOneIndex=-1;
					_touchTwoIndex=-1;
					_clickTime=0;
				}
			}
		}
	}

	/** 鼠标滚轮 */
	public override void onMouseWheel(float wheel)
	{
		_mainCamera.distance+=wheel*_wheelSpeed;
	}

	/** 回归默认 */
	public override void cameraToDefault()
	{
		Unit hero=_scene.hero;

		switch(_mainCamera.mode)
		{
			case CameraModeType.D3:
			case CameraModeType.D25:
			{
				Vector3 cameraPosition=hero.show.getCameraPosition();

				_mainCamera.setTargetPos(cameraPosition);

				_mainCamera.axisX=_mainCamera.config.d25AxisX;

				_mainCamera.axisY=hero.pos.getDir().direction+CommonSetting.rotationOff;

				_mainCamera.distance=_defaultDistance;
			}
				break;
		}
	}

	protected void clickScene(Vector2 pos)
	{

		Ray ray=_mainCamera.camera.ScreenPointToRay(pos);
		RaycastHit[] hitArr = Physics.RaycastAll(ray, CommonSetting.sceneRayCastLength);

		GameObject hitObj;

		foreach(RaycastHit hit in hitArr)
		{
			hitObj=hit.collider.gameObject;
			int layer=hitObj.layer;

			//点击了单位
			if(layer==LayerType.Unit)
			{
				//TODO:点击单位操作
			}
			else if(LayerType.containLayer(LayerType.TerrainMask,layer))
			{
				//TODO:这里可以走预先寻路，然后指令传入,解决点击时，无法到达的问题

				if(NavMesh.SamplePosition(hit.point,out NavMeshHit hit2,Global.mapSamplePositionRadius,NavMesh.AllAreas))
				{
					_scene.hero.control.moveToByClick(hit2.position);
				}
				else
				{
					Ctrl.print("无法到达");
				}

				break;
			}
		}
	}

	public Vector3 getScreenTerrainPos(Vector2 pos)
	{
		Ray ray=_mainCamera.camera.ScreenPointToRay(pos);
		RaycastHit[] hitArr = Physics.RaycastAll(ray, CommonSetting.sceneRayCastLength);

		GameObject hitObj;

		foreach(RaycastHit hit in hitArr)
		{
			hitObj=hit.collider.gameObject;
			int layer=hitObj.layer;

			if(LayerType.containLayer(LayerType.TerrainMask,layer))
			{
				return hit.point;
			}
		}

		return Vector3.zero;
	}

}