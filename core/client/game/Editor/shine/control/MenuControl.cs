using System;
using System.Collections.Generic;
using System.IO;
using System.Reflection;
using ShineEngine;
using UnityEditor;
using UnityEditor.SceneManagement;
using UnityEngine;

namespace ShineEditor
{
	/// <summary>
	/// unity菜单控制
	/// </summary>
	public class MenuControl
	{
		[MenuItem(ShineToolGlobal.menuRoot + "/隐藏进度条",false,12)]
		static void hideProgressBar()
		{
			EditorUtility.ClearProgressBar();
		}

#if USE_DEFINE_PACK
		#else
		[MenuItem(ShineToolGlobal.menuRoot + "/资源打包",false,10)]
		#endif
		static void bundleTool()
		{
			PackWindow.showWindow();
		}
		
		[MenuItem(ShineToolGlobal.menuRoot + "/assetbundle分析(win版)",false,11)]
		static void assetbundleAnalysis()
		{
			//WuHuan.AssetBundleFilesAnalyze.analyzeExport = true;
			//WuHuan.AssetBundleFilesAnalyze.analyzeOnlyScene = true;

			string targetName = PackControl.instance().getTargetMap().get(EditorUserBuildSettings.activeBuildTarget);
			
			string bundlePath = ShineToolGlobal.bundleTempPath + "/" + targetName;

			string targetPath = ShineToolGlobal.bundleTempPath + "/assetBundleReporter/";
			
			if(!Directory.Exists(targetPath))
				Directory.CreateDirectory(targetPath);
			
			string outputPath = targetPath + targetName + "_"+DateTime.Now.ToString("yyyyMMdd_HHmmss") + ".xlsx";
			WuHuan.AssetBundleReporter.AnalyzePrint(bundlePath, outputPath, () => System.Diagnostics.Process.Start(outputPath));
		}

		[MenuItem(ShineToolGlobal.menuRoot + "/快速资源打包 %#h",false,11)]
		static void bundleToolQuick()
		{
			PackControl.instance().packNormal();
		}

		[MenuItem(ShineToolGlobal.menuRoot + "/发布包",false,12)]
		static void buildToolQuick()
		{
			PackControl.instance().buildNormal();
		}

		[MenuItem(ShineToolGlobal.menuRoot + "/启动游戏 %#w",false,13)]
		static void startMainQuick()
		{
			if(EditorApplication.isPlaying)
			{
				Ctrl.print("正在运行中");
			}
			else
			{
				UnityEngine.SceneManagement.Scene activeScene=EditorSceneManager.GetActiveScene();

				if(activeScene.path!=ShineToolGlobal.mainScenePath)
				{
					EditorSceneManager.OpenScene(ShineToolGlobal.mainScenePath);
				}

				EditorApplication.isPlaying=true;
			}
		}
		
		[MenuItem(ShineToolGlobal.menuRoot + "/findClassByYAMLID",false,14)]
		static void findClassByYAMLID()
		{
			YAMLIDSearchWindow.showWindow();
		}

		[MenuItem(ShineToolGlobal.menuRoot + "/UI/美术字生成",false,20)]
		static void BMFontTool()
		{
			BMFontWindow.showWindow();
		}

		[MenuItem(ShineToolGlobal.menuRoot + "/UI/MakeUITextureImport %#k",false,21)]
		static void MakeUITextuxreImport()
		{
			EditorUIControl.makeUITextureImport();
		}

		[MenuItem(ShineToolGlobal.menuRoot + "/UI/makeModel  %#m",false,30)]
		static void makeModel()
		{
			MakeModelControl.make();
		}

		[MenuItem(ShineToolGlobal.menuRoot + "/UI/generateUIModelAll",false,40)]
		static void generateUIPrefab()
		{
			EditorUIControl.make(true);
			Ctrl.print("OK");
		}

		// [MenuItem(ShineToolGlobal.menuRoot + "/UI/替换掉SPrefab",false,50)]
		// static void replacePrefab()
		// {
		// 	PrefabReplaceControl.make();
		// 	Ctrl.print("OK");
		// }

		[MenuItem(ShineToolGlobal.menuRoot + "/Scene/添加所有场景到BuildSettings",false,51)]
		static void addAllSceneToBuildSettings()
		{
			// string[] unityList=FileUtils.getDeepFileList(ShineToolGlobal.assetSourcePath + "/unity","unity");
			string[] sceneList=FileUtils.getDeepFileList(ShineToolGlobal.assetSourcePath + "/scene","unity");

			EditorBuildSettingsScene[] newSettings = new EditorBuildSettingsScene[sceneList.Length+2];

			int i=0;

			newSettings[i++]=new EditorBuildSettingsScene(ShineGlobal.sourceHeadU+"unity/main.unity",true);
			newSettings[i++]=new EditorBuildSettingsScene(ShineGlobal.sourceHeadU+"unity/root.unity",true);

			foreach(string v in sceneList)
			{
				string sceneName=ToolFileUtils.getAssetsPath(v);
				Ctrl.print("看添加:",sceneName);
				newSettings[i++]=new EditorBuildSettingsScene(sceneName,true);
			}

			EditorBuildSettings.scenes = newSettings;
		}

		[MenuItem(ShineToolGlobal.menuRoot+"/ILRuntime/Generate CLR Binding Code",false,60)]
		static void GenerateCLRBinding()
		{
			ILRuntimeCLRBinding.GenerateCLRBinding();
			Ctrl.print("OK");
		}

		[MenuItem(ShineToolGlobal.menuRoot+"/ILRuntime/Generate CLR Binding Code by Analysis",false,70)]
		static void GenerateCLRBindingByAnalysis()
		{
			ILRuntimeCLRBinding.GenerateCLRBindingByAnalysis();
			Ctrl.print("OK");
		}

		[MenuItem(ShineToolGlobal.menuRoot+"/ILRuntime/Generate Adapter",false,80)]
		static void GenerateAdapter()
		{
			ILRuntimeCLRBinding.GenerateAll();
		}

		// [MenuItem(ShineToolGlobal.menuRoot+"/ILRuntime/Generate Common",false,80)]
		// static void GenerateCommon()
		// {
		// 	ILRuntimeCLRBinding.GenerateCommon();
		// }
  //
		// [MenuItem(ShineToolGlobal.menuRoot+"/ILRuntime/Generate Game",false,90)]
		// static void GenerateGame()
		// {
		// 	ILRuntimeCLRBinding.GenerateGame();
		// }

		[MenuItem(ShineToolGlobal.menuRoot + "/clean/清空Persistent目录",false,100)]
		static void clearPersistent()
		{
			FileUtils.clearDir(Application.persistentDataPath);
			Ctrl.print("OK");
		}

		[MenuItem(ShineToolGlobal.menuRoot + "/clean/清空TemporaryCache目录",false,110)]
		static void clearTemporaryCache()
		{
			FileUtils.clearDir(Application.temporaryCachePath);
			Ctrl.print("OK");
		}

		[MenuItem(ShineToolGlobal.menuRoot + "/clean/清空CDN版本记录从0开始(慎用)",false,110)]
		static void clearCDNRecord()
		{
			// FileUtils.clearDir(Application.temporaryCachePath);
			Ctrl.print("OK");
		}

		//--增加部分--//

		[MenuItem("GameObject/ShineUI/FrameContainer",false,10)]
		static void CreateFramaeContainerGameObject()
		{
			EditorUIControl.CreateFramaeContainerGameObject();
		}

		[MenuItem("GameObject/ShineUI/BloodBar",false,20)]
		static void CreateBloodBarGameObject()
		{
			EditorUIControl.CreateBloodBarGameObject();
		}

		[MenuItem("GameObject/ShineUI/I18NText",false,30)]
		static void CreateI18NTextGameObject()
		{
			EditorUIControl.CreateI18NTextGameObject();
		}

		[MenuItem("GameObject/ShineUI/AdvancedText",false,31)]
		static void CreateAdvancedTextGameObject()
		{
			EditorUIControl.CreateAdvancedTextGameObject();
		}

		[MenuItem("GameObject/ShineUI/SScrollView",false,40)]
		static void CreateSScrollViewGameObject()
		{
			EditorUIControl.CreateSScrollViewGameObject();
		}
		
		[MenuItem("GameObject/ShineUI/SCustomScrollView",false,50)]
		static void CreateSCustomScrollViewGameObject()
		{
			EditorUIControl.CreateSCustomScrollViewGameObject();
		}

		[MenuItem("GameObject/ShineUI/SPageView",false,60)]
		static void CreateSPageViewGameObject()
		{
			EditorUIControl.CreateSPageViewGameObject();
		}

		[MenuItem("GameObject/ShineUI/ImageFrameContainer",false,70)]
		static void CreateImageFrameContainerGameObject()
		{
			EditorUIControl.CreateImageFrameContainerGameObject();
		}

		[MenuItem("GameObject/ShineUI/GuideMask",false,80)]
		static void CreateGuideMaskGameObject()
		{
			EditorUIControl.CreateGuideMaskGameObject();
		}

		[MenuItem("GameObject/ShineUI/ImageLoader",false,90)]
		static void CreateImageLoaderGameObject()
		{
			EditorUIControl.CreateImageLoaderGameObject();
		}

		[MenuItem("GameObject/ShineUI/SkeletonGraphicLoader",false,100)]
		static void CreateSkeletonGraphicLoaderGameObject()
		{
			EditorUIControl.CreateSkeletonGraphicLoaderGameObject();
		}

		[MenuItem("GameObject/ShineUI/CreateElement",false,110)]
		static void CreateElement()
		{
			EditorUIControl.CreateElement();
		}

		[MenuItem("GameObject/ShineUI/CreateModel",false,120)]
		static void CreateModel()
		{
			EditorUIControl.CreateModel();
		}
		[MenuItem("GameObject/ShineUI/RawImageLoader",false,130)]
		static void CreateRawImageLoaderGameObject()
		{
			EditorUIControl.CreateRawImageLoaderGameObject();
		}

	}
}