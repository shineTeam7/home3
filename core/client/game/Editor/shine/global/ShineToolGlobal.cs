using ShineEngine;
using UnityEngine;

namespace ShineEditor
{
	/// <summary>
	/// 编辑器全局
	/// </summary>
	public class ShineToolGlobal
	{
		/** 菜单入口名 */
		public const string menuRoot = "Shine";

		//version组
		public static int uiRecordVersion=1;
		public static int sceneEditorBinVersion=1;


		//path组
		/** 工程目录 */
		public static string gamePath = FileUtils.fixAndFullPath(Application.dataPath+"/..");
		/** 资源目录 */
		public static string assetsPath = FileUtils.fixAndFullPath(Application.dataPath);
		/** 资源目录 */
		public static string assetSourcePath=assetsPath + "/source";
		/** ui元素目录名 */
		public static string uiElementsPath = assetSourcePath+"/ui/elements";
		/** ui模型目录名 */
		public static string uiModelsPath = assetSourcePath + "/ui/models";
		/** ui生产模型目录 */
		public static string uiGenerateModelsPath = assetSourcePath+ "/ui/generateModels";
		/** ui预处理记录文件路径 */
		public static string uiRecordPath=FileUtils.fixAndFullPath(gamePath + "/../../record/uiGenerateRecord.bin");
		/** 客户端打包工具版本记录路径 */
		public static string clientBundleRecordPath=FileUtils.fixAndFullPath(gamePath + "/../../record/clientBundleRecord.xml");
		/** 服务器目录 */
		public static string serverPath = FileUtils.fixAndFullPath(gamePath+"/../../server");
		/** 服务器版本号目录 */
		public static string serverVersionPath = serverPath+"/bin/serverConfig/clientVersion.xml";
		/** 服务器目录 */
		public static string serverToolsPath = serverPath+"/tools";
		/** 服务器目录(mac) */
		public static string serverToolsMacPath = serverPath+"/toolsMac";
		/** 服务器保存目录 */
		public static string serverSavePath=serverPath + "/save";
		/** 服务器临时输出目录 */
		public static string serverTempPath=serverPath + "/temp";
		/** 输出资源目录 */
		public static string outSourcePath = FileUtils.fixAndFullPath(gamePath + "/../source");
		/** 资源输出目录(通用部分) */
		public static string sourceCommonPath=outSourcePath + "/common";
		/** 配置输出目录(通用部分) */
		public static string sourceCommonConfigDirPath=sourceCommonPath + "/config";
		/** 客户端临时输出目录 */
		public static string clientTempPath = FileUtils.fixAndFullPath(gamePath + "/../temp");
		/** 客户端保存目录 */
		public static string clientSavePath = FileUtils.fixAndFullPath(gamePath + "/../save");
		/** 流资源目录 */
		public static string streamingAssetsPath = assetsPath+"/StreamingAssets";
		/** cdn生成资源目录 */
		public static string cdnSourcePath = FileUtils.fixAndFullPath(gamePath + "/../cdnSource");
		/** 打包临时输出目录 */
		public static string bundleTempPath=clientTempPath + "/" +bundleDirName;
		// /** 打包配置路径 */
		// public static string buildConfigPath=gamePath+"/config/buildConfig.xml";
		/** 打包配置路径 */
		public static string buildConfigPath=gamePath+"/config/BuildConfig.xlsx";
		// /** 资源信息配置路径 */
		public static string resourceInfoPath=gamePath+"/config/ResourceInfo.xlsx";
		/** 客户端打包工具版本记录路径 */
		public static string commonPath=FileUtils.fixAndFullPath(gamePath + "/../../common");
		/** 场景布局存储目录 */
		public static string scenePlacePath=commonPath + "/save/scenePlace";
		/** 服务器地图信息目录 */
		public static string mapInfoPath=commonPath + "/save/mapInfo";



		//相对路径组
		/** 资源目录字符串 */
		public static string assetSourceStr="Assets/source";
		/** bundle文件扩展名 */
		public static string bundleFileExName=".bundle";
		/** bundle目录名 */
		public static string bundleDirName="bundle";
		/** 主场景位置 */
		public static string mainScenePath=assetSourceStr + "/unity/main.unity";
		/** 场景编辑器位置 */
		public static string sceneEditorScenePath=assetSourceStr + "/unity/sceneEditor.unity";
	}
}