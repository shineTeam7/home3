namespace ShineEngine
{
	/** 录取方式 */
	public class LoadMethodType
	{
		/** 读取资源 */
		public const int Resource=1;
		/** 从包内加载 */
		public const int StreamingAssets=2;
		/** 从网络读取(体系外,外部资源用) */
		public const int Net=3;
	}
}