namespace ShineEngine
{
	/** 资源保存状态类型 */
	public class ResourceSaveStateType
	{
		/** 未处理 */
		public const int None=0;
		/** 已下载好 */
		public const int Downloaded=1;
		/** 在streamingAssets中ready */
		public const int StreamingAssetsReady=2;
	}
}