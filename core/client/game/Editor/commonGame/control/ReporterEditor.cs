using System.IO;
using ShineEditor;
using ShineEngine;
using UnityEditor;
using UnityEngine;

public class ReporterEditor
{
	public static void CreateReporter()
	{
		const int ReporterExecOrder = -12000;

		GameObject main=GameObject.Find(ShineSetting.mainObjectName);

		if(main==null)
		{
			Ctrl.throwError("找不到主对象");
			return;
		}

		Reporter reporter=main.GetComponent<Reporter>();

		if(reporter==null)
		{
			reporter=main.AddComponent<Reporter>();
		}

		// Register root object for undo.
		Undo.RegisterCreatedObjectUndo(main, "Create Reporter");

		MonoScript reporterScript = MonoScript.FromMonoBehaviour(reporter);

		if (MonoImporter.GetExecutionOrder(reporterScript) != ReporterExecOrder) {
			MonoImporter.SetExecutionOrder(reporterScript, ReporterExecOrder);
			//Debug.Log("Fixing exec order for " + reporterScript.name);
		}

		string reporterPath=ShineToolGlobal.assetSourceStr+"/commonGame/reporter/";

		reporter.images = new Images();
		reporter.images.clearImage           = (Texture2D)AssetDatabase.LoadAssetAtPath(Path.Combine(reporterPath, "reporter_clear.png"), typeof(Texture2D));
		reporter.images.collapseImage        = (Texture2D)AssetDatabase.LoadAssetAtPath(Path.Combine(reporterPath, "reporter_collapse.png"), typeof(Texture2D));
		reporter.images.clearOnNewSceneImage = (Texture2D)AssetDatabase.LoadAssetAtPath(Path.Combine(reporterPath, "reporter_clearOnSceneLoaded.png"), typeof(Texture2D));
		reporter.images.showTimeImage        = (Texture2D)AssetDatabase.LoadAssetAtPath(Path.Combine(reporterPath, "reporter_timer_1.png"), typeof(Texture2D));
		reporter.images.showSceneImage       = (Texture2D)AssetDatabase.LoadAssetAtPath(Path.Combine(reporterPath, "reporter_UnityIcon.png"), typeof(Texture2D));
		reporter.images.userImage            = (Texture2D)AssetDatabase.LoadAssetAtPath(Path.Combine(reporterPath, "reporter_user.png"), typeof(Texture2D));
		reporter.images.showMemoryImage      = (Texture2D)AssetDatabase.LoadAssetAtPath(Path.Combine(reporterPath, "reporter_memory.png"), typeof(Texture2D));
		reporter.images.softwareImage        = (Texture2D)AssetDatabase.LoadAssetAtPath(Path.Combine(reporterPath, "reporter_software.png"), typeof(Texture2D));
		reporter.images.dateImage            = (Texture2D)AssetDatabase.LoadAssetAtPath(Path.Combine(reporterPath, "reporter_date.png"), typeof(Texture2D));
		reporter.images.showFpsImage         = (Texture2D)AssetDatabase.LoadAssetAtPath(Path.Combine(reporterPath, "reporter_fps.png"), typeof(Texture2D));
		//reporter.images.graphImage           = (Texture2D)AssetDatabase.LoadAssetAtPath(Path.Combine(reporterPath, "chart.png"), typeof(Texture2D));
		reporter.images.infoImage            = (Texture2D)AssetDatabase.LoadAssetAtPath(Path.Combine(reporterPath, "reporter_info.png"), typeof(Texture2D));
		reporter.images.searchImage          = (Texture2D)AssetDatabase.LoadAssetAtPath(Path.Combine(reporterPath, "reporter_search.png"), typeof(Texture2D));
		reporter.images.closeImage           = (Texture2D)AssetDatabase.LoadAssetAtPath(Path.Combine(reporterPath, "reporter_close.png"), typeof(Texture2D));
		reporter.images.buildFromImage       = (Texture2D)AssetDatabase.LoadAssetAtPath(Path.Combine(reporterPath, "reporter_buildFrom.png"), typeof(Texture2D));
		reporter.images.systemInfoImage      = (Texture2D)AssetDatabase.LoadAssetAtPath(Path.Combine(reporterPath, "reporter_ComputerIcon.png"), typeof(Texture2D));
		reporter.images.graphicsInfoImage    = (Texture2D)AssetDatabase.LoadAssetAtPath(Path.Combine(reporterPath, "reporter_graphicCard.png"), typeof(Texture2D));
		reporter.images.backImage            = (Texture2D)AssetDatabase.LoadAssetAtPath(Path.Combine(reporterPath, "reporter_back.png"), typeof(Texture2D));
		reporter.images.logImage             = (Texture2D)AssetDatabase.LoadAssetAtPath(Path.Combine(reporterPath, "reporter_log_icon.png"), typeof(Texture2D));
		reporter.images.warningImage         = (Texture2D)AssetDatabase.LoadAssetAtPath(Path.Combine(reporterPath, "reporter_warning_icon.png"), typeof(Texture2D));
		reporter.images.errorImage           = (Texture2D)AssetDatabase.LoadAssetAtPath(Path.Combine(reporterPath, "reporter_error_icon.png"), typeof(Texture2D));
		reporter.images.barImage             = (Texture2D)AssetDatabase.LoadAssetAtPath(Path.Combine(reporterPath, "reporter_bar.png"), typeof(Texture2D));
		reporter.images.button_activeImage   = (Texture2D)AssetDatabase.LoadAssetAtPath(Path.Combine(reporterPath, "reporter_button_active.png"), typeof(Texture2D));
		reporter.images.even_logImage        = (Texture2D)AssetDatabase.LoadAssetAtPath(Path.Combine(reporterPath, "reporter_even_log.png"), typeof(Texture2D));
		reporter.images.odd_logImage         = (Texture2D)AssetDatabase.LoadAssetAtPath(Path.Combine(reporterPath, "reporter_odd_log.png"), typeof(Texture2D));
		reporter.images.selectedImage        = (Texture2D)AssetDatabase.LoadAssetAtPath(Path.Combine(reporterPath, "reporter_selected.png"), typeof(Texture2D));

		reporter.images.reporterScrollerSkin = (GUISkin)AssetDatabase.LoadAssetAtPath(Path.Combine(reporterPath, "reporterScrollerSkin.guiskin"), typeof(GUISkin));
	}
}
