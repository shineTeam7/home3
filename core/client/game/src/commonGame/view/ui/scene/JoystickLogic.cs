using System;
using ShineEngine;
using ShineEngine.scene;
using UnityEngine;
using UnityEngine.EventSystems;
using UnityEngine.UI;

/// <summary>
/// 摇杆逻辑
/// </summary>
public class JoystickLogic:GameUILogicBase
{
	/** 是否自由位置 */
	private bool _isFreePosition=true;

	/** 空闲时alpha值 */
	public float freeAlpha=0.3f;
	/** 不动的判定半径 */
	private float _stopRadius=5f;
	/** 不动的判定半径平方 */
	private float _stopRadiusQ;

	/** 按钮半径 */
	public float thumbRadius;

	/** 开始回调 */
	public Action startFunc;
	/** 过程回调(弧度以x轴正向为0) */
	public Action<float> progressFunc;
	/** 结束回调 */
	public Action stopFunc;

	private RectTransform _rootTransform;

	protected Vector2 _rootPos;
	/** 背景 */
	private Image _chassis;
	/** 背景 */
	private RectTransform _chassisTransform;
	/** 摇杆 */
	private Image _thumb;
	/** 摇杆 */
	private RectTransform _thumbTransform;

	/** 原点位置 */
	private Vector2 _chassisOriginPos;

	private Rect _chassisRange;

	/** 当前是否生效 */
	private bool _enabled=true;

	/** 是否可用键盘控制 */
	private bool _canKeyboardOperate=true;

	//当前部分

	private Color _alphaColor = Color.white;

	private int _touchID=-1;

	private bool _showAlpha=false;

	private bool _dragStart;
	/** 当前位置 */
	private Vector2 _chassisPos;
	/** 是否开始移动 */
	private bool _isMoving=false;

	private float _lastValue;

	private int _updateIndex=-1;
	private int _keyDownIndex=-1;
	private int _keyUpIndex=-1;

	private int _axisX;
	private int _axisY;

	/** 绑定节点 */
	public void setNode(UIContainer node)
	{
		_rootTransform=(RectTransform)node.gameObject.transform;
		_rootPos=_rootTransform.position;

		JoystickComponent joyComponent=node.gameObject.AddComponent<JoystickComponent>();
		joyComponent.logic=this;

		_chassis=node.getChild<UIImage>("chassis").image;
		_chassisTransform=_chassis.rectTransform;
		_thumb=_chassisTransform.Find("thumb").GetComponent<Image>();
		_thumbTransform=_thumb.rectTransform;

		Vector2 halfThumbSize=_thumbTransform.rect.size / 2;

		Rect rect;

		UIObject rectObj=node.getChild("rect");

		if(rectObj!=null)
		{
			rect=rectObj.transform.rect;
			Vector2 rectLocalPos=rectObj.transform.localPosition;
			rect.x+=rectLocalPos.x;
			rect.y+=rectLocalPos.y;
		}
		else
		{
			rect=_rootTransform.rect;
			rect.x=0;
			rect.y=0;
		}

		Vector2 chassisSize=_chassisTransform.rect.size;
		Vector2 halfChassisSize=chassisSize / 2;

		thumbRadius=Math.Max(halfChassisSize.x,halfChassisSize.y)-Math.Max(halfThumbSize.x,halfThumbSize.y);

		_chassisRange=new Rect(rect.x+halfChassisSize.x,rect.y+halfChassisSize.y,rect.width-chassisSize.x,rect.height-chassisSize.y);

		if(_chassisRange.width<0 || _chassisRange.height<0)
		{
			Ctrl.throwError("资源位置出错");
		}

		_chassisOriginPos=_chassisTransform.localPosition;

		setStopRadius(_stopRadius);
		setAlpha(false);
	}

	protected override void onEnter()
	{
		base.onEnter();

		_updateIndex=TimeDriver.instance.setUpdate(onUpdate);
		_keyDownIndex=GameC.keyboard.addListener(KeyEventType.KeyDown,onKeyDown);
		_keyUpIndex=GameC.keyboard.addListener(KeyEventType.KeyUp,onKeyUp);
	}

	protected override void onExit()
	{
		base.onExit();

		TimeDriver.instance.clearUpdate(_updateIndex);
		_updateIndex=-1;
		GameC.keyboard.removeListener(_keyDownIndex);
		_keyDownIndex=-1;
		GameC.keyboard.removeListener(_keyUpIndex);
		_keyUpIndex=-1;
	}

	private void onUpdate()
	{
		if(!_dragStart)
			return;

		Vector2 pos;
		//鼠标
		if(_touchID<0)
		{
			pos=Input.mousePosition;
		}
		else
		{
			pos=Input.GetTouch(_touchID).position;
		}

		Vector2 re=pos - _rootPos -_chassisPos;

		//在半径内
		if(re.sqrMagnitude<_stopRadiusQ)
		{
			doCancel();
		}
		else
		{
			doMove(re);
		}
	}

	private void doCancel()
	{
		if(_isMoving)
		{
			_isMoving=false;

			_thumbTransform.localPosition=Vector2.zero;

			if(stopFunc!=null)
				stopFunc();
		}
	}

	private void doMove(Vector2 re)
	{
		float direction=Mathf.Atan2(re.y,re.x);

		bool need=false;

		if(!_isMoving)
		{
			_isMoving=true;
			need=true;

			if(startFunc!=null)
				startFunc();
		}

		if(need || _lastValue!=direction)
		{
			_lastValue=direction;

			re.Normalize();

			_thumbTransform.localPosition=re*thumbRadius;

			if(progressFunc!=null)
				progressFunc(direction);
		}
	}

	private void onKeyDown(KeyCode code)
	{
		if(!_enabled)
			return;

		if(!_canKeyboardOperate)
			return;

		switch(code)
		{
			case KeyCode.W:
			{
				_axisY=1;
			}
				break;
			case KeyCode.S:
			{
				_axisY=-1;
			}
				break;
			case KeyCode.A:
			{
				_axisX=-1;
			}
				break;
			case KeyCode.D:
			{
				_axisX=1;
			}
				break;
			default:
			{
				return;
			}
		}

		countKeyAxis();
	}

	private void onKeyUp(KeyCode code)
	{
		if(!_enabled)
			return;

		if(!_canKeyboardOperate)
			return;

		switch(code)
		{
			case KeyCode.W:
			{
				if(_axisY==1)
				{
					_axisY=GameC.keyboard.isKeyDown(KeyCode.S) ? -1 : 0;
				}
			}
				break;
			case KeyCode.S:
			{
				if(_axisY==-1)
				{
					_axisY=GameC.keyboard.isKeyDown(KeyCode.W) ? 1 : 0;
				}
			}
				break;
			case KeyCode.A:
			{
				if(_axisX==-1)
				{
					_axisX=GameC.keyboard.isKeyDown(KeyCode.D) ? 1 : 0;
				}
			}
				break;
			case KeyCode.D:
			{
				if(_axisX==1)
				{
					_axisX=GameC.keyboard.isKeyDown(KeyCode.A) ? -1 : 0;
				}
			}
				break;
			default:
			{
				return;
			}
		}

		countKeyAxis();
	}

	private void countKeyAxis()
	{
		_dragStart=false;

		if(_axisX==0 && _axisY==0)
		{
			doCancel();
			hideAlpha();
		}
		else
		{
			Vector2 re=new Vector2(_axisX,_axisY);
			doMove(re);
			showAlpha();
		}
	}

	public void OnPointerDown(PointerEventData eventData)
	{
		if(!_enabled)
			return;

		if(_touchID!=-1)
			return;

		Ctrl.print("OnPointerDown",_touchID);

		_touchID=eventData.pointerId;

		showAlpha();

		Vector2 re=eventData.position - _rootPos;

		MathUtils.makePosInRect(ref re,_chassisRange);

		_chassisTransform.localPosition=_chassisPos=re;
	}

	public void OnPointerUp(PointerEventData eventData)
	{
		if(eventData.pointerId!=_touchID)
			return;

		_touchID=-1;
		_dragStart=false;

		doCancel();
		hideAlpha();
		_chassisTransform.localPosition=_chassisOriginPos;
	}

	public void OnBeginDrag(PointerEventData eventData)
	{
		if(!_enabled)
			return;

		_dragStart=true;

		Ctrl.print("OnBeginDrag",_touchID);
	}

	private void showAlpha()
	{
		if(_showAlpha)
			return;

		_showAlpha=true;
		setAlpha(true);
	}

	private void hideAlpha()
	{
		if(!_showAlpha)
			return;

		_showAlpha=false;
		setAlpha(false);
	}

	private void setAlpha(bool value)
	{
		_alphaColor.a=value ? 1f : freeAlpha;
		_chassis.color=_alphaColor;
		_thumb.color=_alphaColor;
	}

	public void setStopRadius(float value)
	{
		_stopRadius=value;
		_stopRadiusQ=value * value;
	}

	public bool enabled
	{
		get {return _enabled;}
		set {_enabled=value;}
	}

	public void setCanKeyboardOperate(bool value)
	{
		_canKeyboardOperate=value;
	}
}