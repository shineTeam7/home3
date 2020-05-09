using UnityEngine;

namespace ShineEngine
{
	/// <summary>
	/// 全局数据
	/// </summary>
	public class ShineGlobal
	{
		/** 资源路径 */
		public static string sourcePath="/../../source";
		/** 持久化资源路径 */
		public static string persistentSourcePath="/source";
		/** cdn资源路径 */
		public static string cdnSourcePath="/../../cdnSource";
		/** bundleInfo文件路径 */
		public static string bundleInfoPath="bundleInfo.bin";
		/** 版本信息文件路径 */
		public static string versionInfoPath="versionInfo.bin";
		/** config文件路径 */
		public static string configPath="config.bin";
		/** config分散路径前缀 */
		public static string configDirPath="config";
		/** config文件Editor用路径 */
		public static string configForEditorPath="configForEditor.bin";
		/** 本地配置文件路径 */
		public static string settingPath="setting.xml";
		/** 服务器路径 */
		public static string serverListPath="serverList.xml";
		/** 逻辑工程dll路径 */
		public static string hotfixDllPath="hotfix.dll";
		/** 逻辑工程dll路径 */
		public static string hotfixPDBPath="hotfix.pdb";

		/** bundle资源头 */
		public static string sourceHeadU="Assets/source/";
		public static string sourceHeadL="assets/source/";

		//version组

		public static int bundleInfoVersion=1;
		public static int versionInfoVersion=1;
		public static int configVersion=1;
		public static int playerSaveVersion=1;
		public static int localSaveVersion=1;
		public static int loginDataVersion=1;

		//layer

		public static string unitLayer="unitLayer";

		public static void init()
		{

		}
	}
}