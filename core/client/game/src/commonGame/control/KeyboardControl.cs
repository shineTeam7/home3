using System;
using ShineEngine;
using UnityEngine;

/// <summary>
/// 键盘控制
/// </summary>
[Hotfix]
public class KeyboardControl:SBaseEventRegister<KeyCode>
{
	public void init()
	{
		SKeyboardControl.keyFunc+=onSKey;
	}

	public bool isKeyDown(KeyCode code)
	{
		return SKeyboardControl.isKeyDown(code);
	}

	private void onSKey(KeyCode code,bool isDown)
	{
		onKey(code,isDown);

		dispatch(isDown ? KeyEventType.KeyDown : KeyEventType.KeyUp,code);
	}

	/** 键盘操作 */
	protected virtual void onKey(KeyCode code,bool isDown)
	{
		switch(code)
		{
			case KeyCode.Escape:
			{
				if(!isDown)
				{
					escDown();
				}
			}
				break;
		}
	}

	/** esc退出方法 */
	protected virtual bool escDown()
	{
        if (GameC.nativeUI == null)
            return false;
        Reporter reporter = GameC.nativeUI.getReporter();
        
		if(CommonSetting.useReporter && reporter!=null && reporter.show)
		{
			reporter.hideReporter();
			return true;
		}
        GMCommandUI gMCommandUI = GameC.nativeUI.getGMCommandUI();
        if (CommonSetting.useDebug && gMCommandUI != null && gMCommandUI.isShow())
		{
			gMCommandUI.hide();
			return true;
		}
        if (CommonSetting.useReporter && reporter!=null && !reporter.show)
		{
			reporter.showReporter();
			return true;
		}
        //其他

        return false;
	}
}