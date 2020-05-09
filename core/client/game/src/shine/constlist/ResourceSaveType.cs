namespace ShineEngine
{
	/** 资源保存方式 */
	public class ResourceSaveType
	{
		/** 在资源目录中 */
		public const int InStreamingAsset=1;
		/** 在持久化目录中 */
		public const int InPersistentAsset=2;
		/** 在网络上还未下载 */
		public const int InNet=3;
		/** 直接进包，并且只在包中 */
		public const int OnlyStreamingAsset=4;

		/** 是否需要首次下载 */
		public static bool needFirst(int type)
		{
			return type==InStreamingAsset || type==InPersistentAsset;
		}

		/** 是否为包内资源 */
		public static bool isStreamingAsset(int type)
		{
			return type==InStreamingAsset || type==OnlyStreamingAsset;
		}
	}
}