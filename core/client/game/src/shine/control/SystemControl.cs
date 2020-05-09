using System;
using System.Threading;

#if(UNITY_EDITOR)
using UnityEditor;
#endif

using UnityEngine;

namespace ShineEngine
{
	/// <summary>
	/// 系统方法
	/// </summary>
	public class SystemControl
	{
		/** 运行平台 */
		public static RuntimePlatform platform;
		/** 客户端平台 */
		public static int clientPlatform;

		/** 是否在pc上(mac也算)(运行平台) */
		public static bool isPC=false;
		/** 是否在手机上运行(运行平台) */
		public static bool isPhone=false;

		private static NetworkReachability _lastNet;

		/** 网络改变回调 */
		public static event Action netChangeFunc;

		/** 游戏暂停回调 */
		public static event Action<bool> applicationPauseFunc;

		/** 游戏暂停回调 */
		public static event Action applicationQuitFunc;
		/// <summary>
		/// 初始化
		/// </summary>
		public static void init()
		{
			//后台运行
			Application.runInBackground=true;

			platform=getPlatformByActive();
			clientPlatform=ClientPlatformType.getTypeByRuntime(platform);

			int runningPt=ClientPlatformType.getTypeByRuntime(Application.platform);

			switch(runningPt)
			{
				case ClientPlatformType.Windows:
				case ClientPlatformType.Mac:
				{
					isPC=true;
				}
					break;
				case ClientPlatformType.Android:
				case ClientPlatformType.Ios:
				{
					isPhone=ShineSetting.isRelease;
				}
					break;
			}

			_lastNet=Application.internetReachability;
		}

		public static void dispose()
		{
			netChangeFunc=null;
			applicationPauseFunc=null;
			applicationQuitFunc=null;
		}

		public static void onFrame()
		{
			if(_lastNet!=Application.internetReachability)
			{
				_lastNet=Application.internetReachability;

				if(netChangeFunc!=null)
					netChangeFunc();

				
			}
		}

		public static void onApplicationPause(bool pauseStatus)
		{
			if (applicationPauseFunc != null)
				applicationPauseFunc(pauseStatus);
		}

		public static void onApplicationQuit()
		{
			if (applicationQuitFunc != null)
				applicationQuitFunc();


		}

		//TODO:ipv4/ipv6的区别

		//public static bool isSupportIpv4()
		//{
		//    //return 
		//}

		/** 网络wifi是否open */
		public static bool isNetWifiOpen()
		{
			return Application.internetReachability==NetworkReachability.ReachableViaLocalAreaNetwork;
		}

		/** 网络流量是否open */
		public static bool isNetFlowOpen()
		{
			return Application.internetReachability==NetworkReachability.ReachableViaCarrierDataNetwork;
		}

		/** 用户当前网络是否开放 */
		public static bool isNetOpen()
		{
			//本地调试视为有网
			if(!ShineSetting.isRelease)
				return true;

			return Application.internetReachability!=NetworkReachability.NotReachable;
		}

		/** 获取当前激活的平台(editor用) */
		private static RuntimePlatform getPlatformByActive()
		{
			RuntimePlatform re=Application.platform;

			//非发布模式
			if(!ShineSetting.isRelease)
			{
				#if(UNITY_EDITOR)
				{
					BuildTarget activeBuildTarget=EditorUserBuildSettings.activeBuildTarget;

					switch(activeBuildTarget)
					{
						case BuildTarget.Android:
						{
							re=RuntimePlatform.Android;
						}
							break;
						case BuildTarget.iOS:
						{
							re=RuntimePlatform.IPhonePlayer;
						}
							break;
					}
				}
				#endif
			}

			return re;
		}
		
		/** 是否为某种网络类型 */
		public static bool isSomeNetWork(NetworkReachability net)
		{
			return _lastNet == net;
		}
	}
}