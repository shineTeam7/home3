using UnityEngine;
using System;

namespace ShineEngine
{

	/// <summary>
	/// 引擎根行为
	/// </summary>
	public class ShineBehavious:MonoBehaviour
	{
		private long _lastTime;

		private int _dateFixDelayTick;

		private bool _testPause=false;
		private long _resumeTime;

		private void Start()
		{
			_lastTime=Ctrl.getTimer();
		}

		private void Update()
		{
			if(_testPause)
			{
				if(Ctrl.getTimer()>=_resumeTime)
				{
					Ctrl.print("暂停恢复");
					_testPause=false;
				}
				else
				{
					return;
				}
			}

			Ctrl.makeFixDirty();

			long now=Ctrl.getTimer();
			int dd=(int)(now - _lastTime);
			_lastTime=now;

			if(dd>0)
			{
				if((_dateFixDelayTick-=dd)<=0)
				{
					_dateFixDelayTick=ShineSetting.dateFixDelay;

					DateControl.makeFixDirty();
				}

				//系统逻辑
				SystemControl.onFrame();
				//计时器
				TimeDriver.instance.tick(dd);
			}

			TimeDriver.instance.update();

			//线程事务最后执行
			if(dd>0)
			{
				//线程
				ThreadControl.onFrame();
			}
		}

		private void FixedUpdate()
		{
			TimeDriver.instance.fixedUpdate();
		}

		private void OnGUI()
		{
			
		}

		private void OnDestroy()
		{
			ShineSetup.exit();
		}

		private void OnApplicationFocus(bool hasFocus)
		{
//			if(hasFocus)
//			{
//				Ctrl.print("获得焦点");
//			}
//			else
//			{
//				Ctrl.print("失去焦点");
//			}
		}

		private void OnApplicationPause(bool pauseStatus)
		{
			if(pauseStatus)
			{
				Ctrl.print("暂停游戏");
			}
			else
			{
				Ctrl.print("暂停结束，恢复游戏");
			}

			SystemControl.onApplicationPause(pauseStatus);
		}

		private void OnApplicationQuit()
		{
			//退出就开编辑器模式

			if(!ShineSetting.isRelease)
			{
				ShineSetting.isEditor=true;
			}

			SystemControl.onApplicationQuit();

			Ctrl.print("应用结束");
		}

		/** 测试暂停 */
		public void testPause(int time)
		{
			Ctrl.print("测试暂停");

			_resumeTime=Ctrl.getTimer() + time;
			_testPause=true;
		}

		/// <summary>
		/// 创建预设实例
		/// </summary>
		public UnityEngine.Object createPrefabObj(UnityEngine.Object prefab)
		{
			return Instantiate(prefab);
		}

		/// <summary>
		/// 创建预设gameobject实例(如果预设并非gameobject，则返回null)
		/// </summary>
		public GameObject createPrefabGameObj(UnityEngine.Object prefab)
		{
			UnityEngine.Object obj=Instantiate(prefab);
			if(obj is GameObject)
				return obj as GameObject;

			return null;
		}
	}
}