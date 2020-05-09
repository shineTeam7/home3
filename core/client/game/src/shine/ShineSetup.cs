using System;
using UnityEngine;

namespace ShineEngine
{
	/// <summary>
	/// 引擎启动类
	/// </summary>
	public class ShineSetup
	{
		private static bool _inited=false;
		/** 根对象 */
		private static GameObject _root;

		private static ShineBehavious _rootBehavious;

		private static Action _exitRun;

		private static bool _exited=false;

		/// <summary>
		/// 启动
		/// </summary>
		public static void setup(GameObject root,Action exitRun=null)
		{
			if(_inited)
				return;

			_inited=true;

			_root=root;

			GameObject.DontDestroyOnLoad(_root);

			_exitRun=exitRun;

			_rootBehavious=_root.AddComponent<ShineBehavious>();

			SystemControl.init();
			ThreadControl.init();
			DateControl.init();
			NetControl. init();
			BytesControl.init();

			//显示部分
			if(ShineSetting.isWholeClient)
			{
				SKeyboardControl.init();
				STouchControl.init();
				UIControl.init();

				CameraControl.init();
				Tween.init();

				LoadControl.init();
				ResourceInfoControl.init();
				AssetPoolControl.init();
			}
			else
			{
				//gm指令所需
				if(ShineSetting.needGMCommandUI)
				{
					SKeyboardControl.init();
					STouchControl.init();
					UIControl.init();
				}

				ShineSetting.debugJumpResourceVersion=true;
				ResourceInfoControl.initBase();
			}
		}

		public static void setupForEditor()
		{
			SystemControl.init();
			DateControl.init();
			BytesControl.init();

			if(ShineSetting.isWholeClient)
			{
				ResourceInfoControl.init();
			}
			else
			{
				ResourceInfoControl.initBase();
			}
		}

		/** 是否初始化过 */
		public static bool inited()
		{
			return _inited;
		}

		/** 关闭(主动调用)(主线程) */
		public static void exit(String str)
		{
			ThreadControl.addMainFunc(()=>
			{
				Ctrl.errorLog(str);
				doExit();
			});
		}

		/** 退出(入口) */
		public static void exit()
		{
			ThreadControl.addMainFunc(()=>
			{
				doExit();
			});
		}

		private static void doExit()
		{
			if(!_inited)
				return;

			if(_exited)
				return;

			_exited=true;

			Ctrl.print("shine exit");

			if(_exitRun!=null)
				_exitRun();

			NetControl.dispose();
			ThreadControl.dispose();
			SystemControl.dispose();

#if UNITY_EDITOR
			UnityEditor.EditorApplication.isPlaying = false;
#else
			Application.Quit();
#endif
		}

		/// <summary>
		/// 根对象
		/// </summary>
		public static GameObject getRoot()
		{
			return _root;
		}

		/** 根行为 */
		public static ShineBehavious getRootBehavious()
		{
			return _rootBehavious;
		}
	}
}