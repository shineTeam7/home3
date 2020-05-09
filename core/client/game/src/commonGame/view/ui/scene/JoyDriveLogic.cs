using System;
using ShineEngine;
using UnityEngine;

/// <summary>
/// 驾驶UI逻辑
/// </summary>
public class JoyDriveLogic:GameUILogicBase
{
	/** 当前是否生效 */
	private bool _enabled=true;

	/**  */
	private int _forwards=0;

	private int _turn=0;

	private int _keyDownIndex=-1;
	private int _keyUpIndex=-1;

	/** 调用 */
	public Action<int,int> callFunc;

	protected override void onEnter()
	{
		base.onEnter();

		_keyDownIndex=GameC.keyboard.addListener(KeyEventType.KeyDown,onKeyDown);
		_keyUpIndex=GameC.keyboard.addListener(KeyEventType.KeyUp,onKeyUp);
	}

	protected override void onExit()
	{
		base.onExit();

		GameC.keyboard.removeListener(_keyDownIndex);
		_keyDownIndex=-1;
		GameC.keyboard.removeListener(_keyUpIndex);
		_keyUpIndex=-1;
	}

	private void onKeyDown(KeyCode code)
	{
		if(!_enabled)
			return;

		switch(code)
		{
			case KeyCode.W:
			{
				_forwards=1;
			}
				break;
			case KeyCode.S:
			{
				_forwards=-1;
			}
				break;
			case KeyCode.A:
			{
				_turn=-1;
			}
				break;
			case KeyCode.D:
			{
				_turn=1;
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

		switch(code)
		{
			case KeyCode.W:
			{
				if(_forwards==1)
				{
					_forwards=GameC.keyboard.isKeyDown(KeyCode.S) ? -1 : 0;
				}
			}
				break;
			case KeyCode.S:
			{
				if(_forwards==-1)
				{
					_forwards=GameC.keyboard.isKeyDown(KeyCode.W) ? 1 : 0;
				}
			}
				break;
			case KeyCode.A:
			{
				if(_turn==-1)
				{
					_turn=GameC.keyboard.isKeyDown(KeyCode.D) ? 1 : 0;
				}
			}
				break;
			case KeyCode.D:
			{
				if(_turn==1)
				{
					_turn=GameC.keyboard.isKeyDown(KeyCode.A) ? -1 : 0;
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
		callFunc(_forwards,_turn);
	}

	public bool enabled
	{
		get {return _enabled;}
		set {_enabled=value;}
	}
}