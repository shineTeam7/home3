using ShineEngine;
using UnityEditor;
using UnityEngine;

namespace ShineEditor
{
	/** 打包界面 */
	public class PackWindow:BaseWindow
	{
		private BuildTarget[] _targetArr;
		private string[] _targetNameArr;
		private SMap<BuildTarget,int> _targetIndexMap;


		/** 是否全部执行 */
		private bool _isAll;
		/** 是否打包测试 */
		private bool _isReleasePack;
		/** 是否新app */
		private bool _isNewApp;
		/** 是否是必须更新的app */
		private bool _isAppNeedUpdate;
		/** 是否有必须更新的资源 */
		private bool _isResourceNeedUpdate;
		/** 显示版本号 */
		private string _version="";

		private int _selectTargetIndex;

		public static void showWindow()
		{
			GetWindowWithRect<PackWindow>(new Rect(0,0,400,300),true,"资源打包",true).init().show();
		}

		protected PackWindow init()
		{
			SMap<BuildTarget,string> targetMap=PackControl.instance().getTargetMap();

			_targetArr=new BuildTarget[targetMap.size()];
			_targetNameArr=new string[targetMap.size()];
			_targetIndexMap=new SMap<BuildTarget,int>();
			int i=0;

			targetMap.forEach((k,v)=>
			{
				_targetArr[i]=k;
				_targetNameArr[i]=v;
				_targetIndexMap[k]=i++;
			});

			selectNormalTarget();

			_version=EditorPrefs.GetString("AssetBundleWindow_version");

			return this;
		}

		/** 选择默认平台（默认选择当前平台） */
		private void selectNormalTarget()
		{
			BuildTarget target;

			switch(EditorUserBuildSettings.activeBuildTarget)
			{
				case BuildTarget.Android:
				case BuildTarget.iOS:
				case BuildTarget.StandaloneOSX:
					target=EditorUserBuildSettings.activeBuildTarget;
					break;
				default:
					target=BuildTarget.StandaloneWindows;
					break;
			}

			_selectTargetIndex=_targetIndexMap[target];
		}

		protected override void OnGUI()
		{
			GUILayout.Label("选择平台",EditorUtils.labelCenterStyle);
			_selectTargetIndex=GUILayout.Toolbar(_selectTargetIndex,_targetNameArr);

			GUILayout.Space(18);

			_isAll=GUILayout.Toggle(_isAll,new GUIContent("是否全部执行", "是:全量执行,否:增量执行"),GUILayout.Width(100));
			_isReleasePack=GUILayout.Toggle(_isReleasePack,new GUIContent("是否正式打包", "是:会成成到cdn目录,并且覆盖版本记录文件，否:本地调试用"),GUILayout.Width(100));

			if(_isReleasePack)
			{
				_isNewApp=GUILayout.Toggle(_isNewApp,new GUIContent("是否新app", "正式发布用,标记是否有主工程更新"),GUILayout.Width(100));

				if(_isNewApp)
					_isAppNeedUpdate=GUILayout.Toggle(_isAppNeedUpdate,new GUIContent("是否是必须更新的app", "正式发布用,标记主工程的更新是否客户端必须下载"),GUILayout.Width(200));
				else
					_isAppNeedUpdate=false;

				_isResourceNeedUpdate=GUILayout.Toggle(_isResourceNeedUpdate,new GUIContent("是否有必更资源", "正式发布用,标记是否变更最低允许资源版本"),GUILayout.Width(100));

				makeVersionText();
			}
			else
			{
				_isNewApp=false;
				_isAppNeedUpdate=false;
				_isResourceNeedUpdate=false;
			}

			bool clickPack;
			GUILayout.BeginHorizontal();
			{
				GUILayout.Space(150);
				GUIStyle tmpBtStyle=new GUIStyle(GUI.skin.button) {fontSize=20};
				// ReSharper disable once UnusedVariable
				using(BgColor bg=new BgColor(Color.green))
				{
					clickPack=GUILayout.Button("资源打包",tmpBtStyle,GUILayout.Width(100),GUILayout.Height(30));
				}
			}
			GUILayout.EndHorizontal();

			GUILayout.Space(30);

			bool clickBuild;
			GUILayout.BeginHorizontal();
			{
				GUILayout.Space(150);
				GUIStyle tmpBtStyle=new GUIStyle(GUI.skin.button) {fontSize=20};
				// ReSharper disable once UnusedVariable
				using(BgColor bg=new BgColor(Color.yellow))
				{
					clickBuild=GUILayout.Button("一键出包",tmpBtStyle,GUILayout.Width(100),GUILayout.Height(30));
				}
			}

			GUILayout.EndHorizontal();

			if(clickPack)
			{
				if(string.IsNullOrEmpty(_version))
				{
					_version="1.01";
				}

				PackControl.instance().pack(_targetArr[_selectTargetIndex],_isAll,_isReleasePack,_isNewApp,_isAppNeedUpdate,_isResourceNeedUpdate,makeVersion);
				Close();
			}

			if(clickBuild)
			{
				if(string.IsNullOrEmpty(_version))
				{
					_version="1.01";
				}

				PackControl.instance().pack(_targetArr[_selectTargetIndex],_isAll,_isReleasePack,_isNewApp,_isAppNeedUpdate,_isResourceNeedUpdate,makeVersion);
				PackControl.instance().build(_targetArr[_selectTargetIndex]);
				Close();
			}
		}
		
		protected virtual void makeVersionText()
		{
			GUILayout.Label("显示版本号");
			_version=EditorGUILayout.TextField(_version);
		}
		
		protected virtual string makeVersion(int appVersion,int leastAppVersion,int resourceVersion,int leastResourceVersion)
		{
			if(string.IsNullOrEmpty(_version))
			{
				_version="1.01";
			}

			return _version;
		}
	}
}