using System;
using UnityEngine;
using UnityEngine.EventSystems;

namespace ShineEngine
{
	/** 触摸控制（包括鼠标） */
	public class STouchControl
	{
		/** 输入是否可用(键,鼠,touch) */
		private static bool _inputEnbaled=true;

		//mouse部分
		/** 鼠标位置 */
		private static Vector2 _mousePosition;
		/** 鼠标左键按下 */
		private static bool _isMouseDown;

		/** 鼠标回调 */
		private static Action<bool> _mouseFunc;
		/** 鼠标滚轮行为 */
		private static Action<float> _mouseWheelFunc;

		//touch部分
		/** 触摸回调 */
		private static Action<Touch,bool> _touchFunc;

		/** 单点响应组 */
		public static event Action<bool> touchOneFunc;

		public static void init()
		{
			TimeDriver.instance.setUpdate(onUpdate);
		}

		private static void onUpdate()
		{
			if(SystemControl.isPC)
			{
				_mousePosition=Input.mousePosition;

				if(Input.GetMouseButtonDown(0))
				{
					if(_inputEnbaled)
					{
						if(_mouseFunc!=null)
							_mouseFunc(true);

						if(touchOneFunc!=null)
							touchOneFunc(true);
					}
				}

				if(Input.GetMouseButtonUp(0))
				{
					if(_mouseFunc!=null)
						_mouseFunc(false);

					if(touchOneFunc!=null)
						touchOneFunc(false);
				}

				float wheel=Input.GetAxis("Mouse ScrollWheel");

				if(wheel!=0f)
				{
					if(_inputEnbaled)
					{
						if(_mouseWheelFunc!=null)
						{
							_mouseWheelFunc(wheel);
						}
					}
				}
			}
			else if(SystemControl.isPhone)
			{
				int touchCount=Input.touchCount;

				for(int i=0;i<touchCount;i++)
				{
					Touch touch=Input.GetTouch(i);

					if(i==0)
					{
						_mousePosition=touch.position;
					}

					switch(touch.phase)
					{
						case TouchPhase.Began:
						{
							if(_inputEnbaled)
							{
								if(i==0)
								{
									_isMouseDown=true;
									
									if(touchOneFunc!=null)
										touchOneFunc(true);
								}

								if(_touchFunc!=null)
									_touchFunc(touch,true);
							}

						}
							break;
						case TouchPhase.Ended:
						case TouchPhase.Canceled:
						{
							if(i==0)
							{
								_isMouseDown=false;
								
								if(touchOneFunc!=null)
									touchOneFunc(false);
							}

							if(_touchFunc!=null)
								_touchFunc(touch,false);
						}
							break;
					}
				}
			}
		}

		/** 设置输入是否可用 */
		public static void setInputEnbaled(bool value)
		{
			if(_inputEnbaled==value)
				return;

			_inputEnbaled=value;

			UIControl.setTouchEnabled(value);
		}

		public static bool inputEnbaled()
		{
			return _inputEnbaled;
		}

		/** 触摸回调 */
		public static void setTouchFunc(Action<Touch,bool> func)
		{
			_touchFunc=func;
		}

		/** 鼠标回调 */
		public static void setMouseFunc(Action<bool> func)
		{
			_mouseFunc=func;
		}

		/** 鼠标滚轮行为 */
		public static void setMouseWheelFunc(Action<float> func)
		{
			_mouseWheelFunc=func;
		}

		/** 鼠标点 */
		public static Vector2 mousePosition
		{
			get {return _mousePosition;}
		}

		/** 是否鼠标按下 */
		public static bool isMouseDown
		{
			get {return _isMouseDown;}
		}

		//判定

		/** 当前是否在UI上 */
		public static bool isTouchOnUI()
		{
			EventSystem eventSystem=EventSystem.current;

			if(eventSystem==null)
				return false;

			if(SystemControl.isPC)
			{
				return eventSystem.IsPointerOverGameObject();
			}
			else if(SystemControl.isPhone)
			{
				int touchCount=Input.touchCount;

				for(int i=0;i<touchCount;i++)
				{
					if(eventSystem.IsPointerOverGameObject(i))
						return true;
				}

				return false;
			}

			return false;
		}

		/** 是否某触摸点在UI上 */
		public static bool isTouchOnUI(int index)
		{
			EventSystem eventSystem=EventSystem.current;

			if(eventSystem==null)
				return false;

			return eventSystem.IsPointerOverGameObject(index);
		}
	}
}