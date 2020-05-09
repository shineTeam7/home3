using UnityEngine;

namespace ShineEngine
{
	/** 摄像机工具 */
	public class CameraTool
	{
		/** unity摄像机 */
		private Camera _camera;
		/** 摄像机位置 */
		private Transform _cameraTransform;

		private int _frameTimeIndex=-1;

		/** 模式 */
		private int _mode=CameraModeType.D3;

		//temp
		private Quaternion _quaternion=new Quaternion();

		private Vector3 _tempVec=new Vector3();

		//args

		/** 配置 */
		private CameraToolConfig _config=new CameraToolConfig();

		//target

		/** 目标位置 */
		private Vector3 _targetPos=new Vector3();
		/** y轴角度 */
		private float _axisY;
		/** x轴角度 */
		private float _axisX;
		/** 与目标距离 */
		private float _distance=1f;

		/** 与目标距离(使用距离) */
		private float _useDistance;
		/** 目标是否改变 */
		private bool _targetChanged=true;

		//current


		/** 当前位置 */
		private Vector3 _currentPos=new Vector3();
		/** y轴角度 */
		private float _currentAxisY;
		/** x轴角度 */
		private float _currentAxisX;
		/** 与目标距离 */
		private float _currentDistance;

		private float _velocityAxisX=0f;
		private float _velocityAxisY=0f;
		private float _velocityDistance=0f;

		/** 当前位置是否完成移动 */
		private bool _currentComplete=false;

		/** 当前帧是否有变化 */
		private bool _currentFrameChanged=false;

		public CameraTool()
		{

		}

		/** 初始化 */
		public void init(Camera camera)
		{
			_cameraTransform=(_camera=camera).transform;

			_frameTimeIndex=TimeDriver.instance.setFrame(onFrame);
		}

		/** 析构 */
		public void dispose()
		{
			if(_frameTimeIndex!=-1)
			{
				TimeDriver.instance.clearFrame(_frameTimeIndex);
				_frameTimeIndex=-1;
			}
		}

		/** 设置配置 */
		public void setConfig(CameraToolConfig config)
		{
			_config=config;
		}

		public CameraToolConfig config
		{
			get {return _config;}
		}

		//模式
		public int mode
		{
			get {return _mode;}
			set {_mode=value;}
		}

		/** 摄像机 */
		public Camera camera
		{
			get {return _camera;}
		}

		private void onFrame(int delay)
		{
			_currentFrameChanged=false;

			if(_targetChanged)
			{
				_targetChanged=false;


				//TODO:计算useDistance
				_useDistance=_distance;

				_currentAxisY=MathUtils.cutRadian(_currentAxisY);

				//超了
				if(_axisY-_currentAxisY>=MathUtils.fPI)
				{
					_currentAxisY+=MathUtils.fPI2;
				}
				else if(_currentAxisY-_axisY>=MathUtils.fPI)
				{
					_currentAxisY-=MathUtils.fPI2;
				}

				_currentComplete=false;
			}

			if(!_currentComplete)
			{
				//pos

				if(_mode==CameraModeType.Custom)
				{
					//走缓动
				}
				else
				{
					MathUtils.copyVec3(ref _currentPos,_targetPos);
				}

				bool b1=smoothDamp(ref _currentAxisX,_axisX,ref _velocityAxisX,_config.tweenTime);
				bool b2=smoothDamp(ref _currentAxisY,_axisY,ref _velocityAxisY,_config.tweenTime);
				bool b3=smoothDamp(ref _currentDistance,_useDistance,ref _velocityDistance,_config.tweenTime);

				_currentFrameChanged=true;
				updateCamera();

				if(b1 && b2 && b3)
				{
					_currentComplete=true;
				}
			}
		}

		private bool smoothDamp(ref float current, float target,ref float velocity,float smoothTime,float adjust = 0.01f)
		{
			if(Mathf.Abs(current - target)<=adjust)
			{
				velocity=0f;
				return true;
			}

			current=Mathf.SmoothDamp(current,target,ref velocity,smoothTime);
			return false;
		}

		private void updateCamera()
		{
			_quaternion.SetEulerRotation(_currentAxisX,_currentAxisY,0f);

			_cameraTransform.position=_currentPos-(_quaternion*Vector3.forward*_currentDistance);
			_cameraTransform.rotation=_quaternion;
		}

		/** 设置目标位置 */
		public void setTargetPos(in Vector3 vec3)
		{
			MathUtils.copyVec3(ref _targetPos,vec3);
			_targetChanged=true;
		}

		/** x轴旋转 */
		public float axisX
		{
			get {return _axisX;}
			set
			{
				if(_mode==CameraModeType.D25)
				{
					_axisX=_config.d25AxisX;
				}
				else
				{
					_axisX=Mathf.Clamp(value,_config.minAxisX,_config.maxAxisX);
				}

				_targetChanged=true;
			}
		}

		/** y轴旋转 */
		public float axisY
		{
			get {return _axisY;}
			set
			{
				_axisY=MathUtils.cutRadian(value);
				_targetChanged=true;
			}
		}

		/** 当前y轴 */
		public float currentAxisY
		{
			get {return _currentAxisY;}
		}

		/** 当前x轴 */
		public float currentAxisX
		{
			get {return _currentAxisX;}
		}

		/** 与目标距离 */
		public float distance
		{
			get {return _distance;}
			set
			{
				_distance=Mathf.Clamp(value,_config.minDistance,_config.maxDistance);
				_targetChanged=true;
			}
		}

		/** 当前帧是否有变化 */
		public bool currentFrameChanged()
		{
			return _currentFrameChanged;
		}

	}

	public class CameraToolConfig
	{
		public float minDistance=2f;
		public float maxDistance=20f;

		public float d25AxisX=45f*MathUtils.DegToRad;

		public float minAxisX=-80f*MathUtils.DegToRad;
		public float maxAxisX=80f*MathUtils.DegToRad;

		/** 缓动时间 */
		public float tweenTime=0.1f;


	}
}