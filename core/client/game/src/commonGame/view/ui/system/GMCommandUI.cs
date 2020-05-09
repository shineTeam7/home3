using System.Text;
using UnityEngine;
using UnityEngine.UI;
using ShineEngine;

/// <summary>
/// GM指令界面(外UI)
/// </summary>
public class GMCommandUI:NatureUIBase
{
	private const int _printMaxCacheLength=15000;
	private const int _MAX_CMD_CACHE=10;

	private SQueue<int> _printQueue=new SQueue<int>();

	private StringBuilder _printSb=new StringBuilder();

	private SList<string> _cmdCache=new SList<string>();

	private int _curCmdCacheIndex;

	private InputField _inputField;
	private Text _text;

	//touch
	private bool _preShow=false;

	protected override GameObject getGameObject()
	{
		Transform transform=UIControl.getUIContainer().transform.Find("gmCommandUI");

		if(transform==null)
		{
			Ctrl.throwError("找不到:gmCommandUI");
			return null;
		}

		return transform.gameObject;
	}

	protected override void onInit()
	{
		base.onInit();

		Transform transform=_gameObj.transform;

		transform.Find("cancelBt").gameObject.AddComponent<PointerBehaviour>().onClick=onClickCancel;
		transform.Find("sendBt").gameObject.AddComponent<PointerBehaviour>().onClick=onClickSend;
		_inputField=transform.Find("cmdInput").gameObject.GetComponent<InputField>();
		_text=transform.Find("helpTip").Find("txt").gameObject.GetComponent<Text>();

		SKeyboardControl.keyFunc+=onKey;
		STouchControl.touchOneFunc+=onTouchOne;
	}

	protected override void onShow()
	{
		_inputField.ActivateInputField();
		_inputField.text="";

		//TODO:过滤文本框输入
		// BaseInputModule currentCurrentInputModule=EventSystem.current.currentInputModule;
		// UnityEngine.EventSystems.EventSystem.current.currentSelectedGameObject

		refreshPrintShow();
	}

	private void onKey(KeyCode code,bool isDown)
	{
		if(ShineSetting.isOfficial)
			return;

		if(!isDown)
		{
			if(!_isShow)
			{
				if(code==KeyCode.BackQuote)
				{
					show();
				}
			}
			else
			{
				switch(code)
				{
					case KeyCode.Return:
					case KeyCode.KeypadEnter:
					{
						Ctrl.print("发送");
						onClickSend();

						_inputField.ActivateInputField();
					}
						break;
					case KeyCode.BackQuote:
					{
						hide();
					}
						break;
					case KeyCode.UpArrow:
					{
						if(_cmdCache.length()>0 && _inputField.isFocused)
						{
							_curCmdCacheIndex--;
							if(_curCmdCacheIndex<0)
								_curCmdCacheIndex=0;
							_inputField.text=_cmdCache[_curCmdCacheIndex];
						}
					}
						break;
					case KeyCode.DownArrow:
					{
						if(_cmdCache.length()>0 && _inputField.isFocused)
						{
							_curCmdCacheIndex++;
							if(_curCmdCacheIndex>_cmdCache.length() - 1)
								_curCmdCacheIndex=_cmdCache.length() - 1;
							_inputField.text=_cmdCache[_curCmdCacheIndex];
						}
					}
						break;
				}
			}
		}
	}

	private void onTouchOne(bool isDown)
	{
		if(ShineSetting.isOfficial && ShineSetting.isRelease)
			return;

		if(isDown)
		{
			Vector2 pos=STouchControl.mousePosition;

			//左下角
			if(pos.x<=Screen.width*0.1 && pos.y<=Screen.height*0.1)
			{
				_preShow=true;
			}
			else
			{
				_preShow=false;
			}
		}
		else
		{
			if(_preShow)
			{
				_preShow=false;

				Vector2 pos=STouchControl.mousePosition;

				//右上角
				if(pos.x>=Screen.width*0.9 && pos.y>=Screen.height*0.9)
				{
					show();
				}
			}
		}
	}

	private void refreshPrintShow()
	{
		_text.text=_printSb.ToString();
	}

	private void setCmdCache(string cmd)
	{
		int index=_cmdCache.indexOf(cmd);
		if(index!=-1)
		{
			_cmdCache.remove(index);
		}
		else if(_cmdCache.length()>=_MAX_CMD_CACHE)
		{
			_cmdCache.shift();
		}
		_cmdCache.add(cmd);
	}

	private void onClickSend()
	{
		string cmd=_inputField.text;
		if(string.IsNullOrEmpty(cmd))
			return;

		setCmdCache(cmd);
		_curCmdCacheIndex=_cmdCache.length();

		_inputField.text="";

		//客户端有
		if(GameC.clientGm.hasCmd(cmd))
		{
			doClientCMD(cmd);
		}
		else
		{
			ClientGMRequest.create(cmd).send();
		}
	}

	private void onClickCancel()
	{
		hide();
	}

	private void onClickDebugButton()
	{
		showOrHide();
	}

	/** 输出 */
	public void onPrint(string str)
	{
		_printQueue.offer(str.Length+1);
		_printSb.Append(str);
		_printSb.Append('\n');

		while(_printSb.Length>_printMaxCacheLength)
		{
			_printSb.Remove(0,_printQueue.poll());
		}

		if(!_isShow)
			return;

		refreshPrintShow();
	}

	private void doClientCMD(string msg)
	{
		GameC.clientGm.execute(msg);

		switch(msg)
		{
			case "help":
			{
				ClientGMRequest.create(msg).send();
			}
				break;
		}
	}
}
