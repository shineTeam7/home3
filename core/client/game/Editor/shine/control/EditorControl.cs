using System;
using System.Linq;
using ShineEngine;
using UnityEditor;
using UnityEngine;
using MathUtils = ShineEngine.MathUtils;

namespace ShineEditor
{
	/** 编辑控制 */
	[InitializeOnLoad]
	public class EditorControl
	{
		static EditorControl()
		{
			#if(UNITY_EDITOR)
			{
				init();
			}
			#endif
		}

		private static bool _isNewOnce=false;

		/** 此次编译版本号 */
		private static int _version;

		/** 每帧执行列表 */
		private static SList<Action> _updateList;

		/** 下一帧执行列表 */
		private static SList<Action> _callLaterList;

		/** 初始化 */
		private static void init()
		{
			if(!ShineSetting.isRelease)
			{
				//编辑器模式
				ShineSetting.isEditor=true;
			}

			//如果运行中，就停止，防止unity的内存清空导致一堆无意义报错
			if(EditorApplication.isPlaying)
			{
				EditorApplication.isPlaying=false;
			}

			EditorApplication.update+=Update;
			EditorApplication.hierarchyChanged+=OnHierarchyChanged;
			EditorApplication.hierarchyWindowItemOnGUI+=HierarchyWindowItemOnGUI;
			EditorApplication.projectChanged+=OnProjectChanged;
			EditorApplication.projectWindowItemOnGUI+=ProjectWindowItemOnGUI;
			EditorApplication.modifierKeysChanged+=OnModifierKeysChanged;

			EditorApplication.searchChanged+=OnSearchChanged;
			EditorApplication.pauseStateChanged+=OnPauseStateChanged;
			EditorApplication.playModeStateChanged+=OnPlayModeStateChanged;

			PrefabUtility.prefabInstanceUpdated+=onPrefabUpdated;

			_version=MathUtils.randomInt();

			_callLaterList=new SList<Action>();
			_updateList=new SList<Action>();

			_isNewOnce=true;
		}

		public static int getVersion()
		{
			return _version;
		}

		/** 帧回调 */
		private static void Update()
		{
			if(_isNewOnce)
			{
				_isNewOnce=false;

				afterCompile();
			}
			// if (!EditorApplication.isCompiling && _isCompiling)
			// {
			//
			// }

			if(_callLaterList.size()!=0)
			{
				SList<Action> list=_callLaterList.clone();
				_callLaterList.clear();
				for(var i=0;i<list.Count;i++)
				{
					Action action=list.get(i);
					action?.Invoke();
				}
			}

			if(ShineSetting.isEditor)
			{

			}
		}

		/** 编译完毕 */
		private static void afterCompile()
		{

		}

		/** Hierarchy视图发生改变 */
		private static void OnHierarchyChanged()
		{
//			Ctrl.print("Hierarchy视图发生改变");
		}

		/** Hierarchy视图中的元素发生刷新 */
		private static void HierarchyWindowItemOnGUI(int instanceID,Rect selectionRect)
		{
//			Ctrl.print(string.Format("{0} : {1} - {2}",EditorUtility.InstanceIDToObject(instanceID),instanceID,selectionRect));
		}

		/** Project视图发生改变 */
		private static void OnProjectChanged()
		{
//			Ctrl.print("Project视图发生改变");
		}

		/** Project视图中的元素刷新 */
		private static void ProjectWindowItemOnGUI(string guid,Rect selectionRect)
		{
//			Ctrl.print(string.Format("{0} : {1} - {2}",AssetDatabase.GUIDToAssetPath(guid),guid,selectionRect));
		}

		/** 触发键盘事件 */
		private static void OnModifierKeysChanged()
		{
			// Ctrl.print("触发键盘事件");
		}

		/** 搜索栏发生改变 */
		private static void OnSearchChanged()
		{
//			Ctrl.print("搜索栏发生改变");
		}

		/** 编辑器暂停模式发生改变（是否暂停） */
		private static void OnPauseStateChanged(PauseState state)
		{
//			Ctrl.print("暂停模式改变：" + state);
		}

		/** 编辑器运行模式发生改变 */
		private static void OnPlayModeStateChanged(PlayModeStateChange state)
		{
//			Ctrl.print("运行模式改变：" + state);
		}

		private static void onPrefabUpdated(GameObject instance)
		{
			// Ctrl.print("看保存路径",PrefabUtility.GetPrefabAssetPathOfNearestInstanceRoot(instance));
		}

		/** 下一帧执行 */
		public static void callLater(Action action)
		{
			_callLaterList.add(action);
		}
	}
}