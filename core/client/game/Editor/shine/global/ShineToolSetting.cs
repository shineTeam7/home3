using System.Security;

namespace ShineEditor
{
	/// <summary>
	/// 编辑器全局设置
	/// </summary>
	public class ShineToolSetting
	{
		/** bundle打包时，是否需要将依赖资源一并入包 */
		public static bool bundlePackNeedPutDependAlsoIntoStreamingAssets=false;

		/** 打包版本号 */
		public static string bundlePackVersion="1.1";


	}
}
