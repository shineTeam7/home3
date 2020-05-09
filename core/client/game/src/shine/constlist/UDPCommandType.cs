namespace ShineEngine
{
	public class UDPCommandType
	{
		/** 发包 */
		public const int Pack=1;
		/** 请求包 */
		public const int RequestPack=2;
		/** 出错(包等待超上限)(被动关闭) */
		public const int Error=3;
		/** 主动关闭 */
		public const int Close=4;
		/** 连接 */
		public const int Connect=5;
		/** 连接成功 */
		public const int ConnectSuccess=6;
	}
}