using System;
using UnityEngine;

namespace ShineEngine
{
	/// <summary>
	/// 键盘控制类
	/// </summary>
	public class SKeyboardControl
	{
		/** 按键枚举存储 */
		private static KeyCode[] _keys;

		/** 按下字典 */
		private static SSet<KeyCode> _downSet=new SSet<KeyCode>();

		private static int _ctrlCount=0;
		private static int _shiftCount=0;
		private static int _altCount=0;
		private static int _commondCount=0;

		/** 键盘响应组 */
		public static event Action<KeyCode,bool> keyFunc;

		public static void init()
		{
			/** 初始化将枚举存储为数组 */
//			Array keyList=Enum.GetValues(typeof(KeyCode));
//			_keys=new KeyCode[keyList.Length];
//			for(int i=keyList.Length - 1;i>=0;--i)
//			{
//				_keys[i]=(KeyCode)keyList.GetValue(i);
//			}

			SList<KeyCode> list=new SList<KeyCode>();

			//a-z
			for(int i=(int)KeyCode.A,len=(int)KeyCode.Z;i<=len;++i)
			{
				list.add((KeyCode)i);
			}

			//0-9
			for(int i=(int)KeyCode.Alpha0,len=(int)KeyCode.Alpha9;i<=len;++i)
			{
				list.add((KeyCode)i);
			}

			//0-9
			for(int i=(int)KeyCode.Keypad0,len=(int)KeyCode.Keypad9;i<=len;++i)
			{
				list.add((KeyCode)i);
			}

			//esc
			list.add(KeyCode.Escape);
			list.add(KeyCode.BackQuote);
			//enter
			list.add(KeyCode.Return);
			list.add(KeyCode.KeypadEnter);

			list.add(KeyCode.LeftControl);
			list.add(KeyCode.LeftCommand);
			list.add(KeyCode.LeftShift);
			list.add(KeyCode.LeftAlt);

			list.add(KeyCode.RightControl);
			list.add(KeyCode.RightCommand);
			list.add(KeyCode.RightShift);
			list.add(KeyCode.RightAlt);

			_keys=list.toArray();

			TimeDriver.instance.setUpdate(onUpdate);
		}

		/** 每帧回调 */
		private static void onUpdate()
		{
			KeyCode keyCode;
			for(int i=_keys.Length - 1;i>=0;--i)
			{
				keyCode=_keys[i];
				if(Input.GetKeyDown(keyCode))
					onKeyDown(keyCode);
				if(Input.GetKeyUp(keyCode))
					onKeyUp(keyCode);
			}
		}

		/** 键盘按下 */
		private static void onKeyDown(KeyCode code)
		{
			if(_downSet.contains(code))
				return;

			_downSet.add(code);

			onKey(code,true);
		}

		/** 设置键盘弹起 */
		private static void onKeyUp(KeyCode code)
		{
			if(!_downSet.contains(code))
				return;

			_downSet.remove(code);

			onKey(code,false);
		}

		private static void onKey(KeyCode code,bool isDown)
		{
			switch(code)
			{
				case KeyCode.LeftControl:
				case KeyCode.RightControl:
				{
					if(isDown)
						_ctrlCount++;
					else
						_ctrlCount--;
				}
					break;
				case KeyCode.LeftShift:
				case KeyCode.RightShift:
				{
					if(isDown)
						_shiftCount++;
					else
						_shiftCount--;
				}
					break;
				case KeyCode.LeftAlt:
				case KeyCode.RightAlt:
				{
					if(isDown)
						_altCount++;
					else
						_altCount--;
				}
					break;
				case KeyCode.LeftCommand:
				case KeyCode.RightCommand:
				{
					if(isDown)
						_commondCount++;
					else
						_commondCount--;
				}
					break;
			}

			//TODO:其他esc

			if(!isDown || STouchControl.inputEnbaled() ||  code==KeyCode.Escape)
			{
				if(keyFunc!=null)
				{
					keyFunc(code,isDown);
				}
				else
				{
					Ctrl.warnLog("SKeyBoardControl未赋值keyFunc");
				}
			}
		}

		/** ctrl是否按下 */
		public static bool isCtrlDown()
		{
			return _ctrlCount!=0;
		}

		/** shift是否按下 */
		public static bool isShiftDown()
		{
			return _shiftCount!=0;
		}

		/** alt是否按下 */
		public static bool isAltDown()
		{
			return _altCount!=0;
		}

		/** command是否按下 */
		public static bool isCommoandDown()
		{
			return _commondCount!=0;
		}

		/** 某键是否按下 */
		public static bool isKeyDown(KeyCode code)
		{
			return _downSet.contains(code);
		}
	}
}