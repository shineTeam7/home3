namespace ShineEngine
{
	/** 日志类型 */
	public class SLogType
	{
		/** 普通日志 */
		public const int Normal=1;
		/** 警告日志 */
		public const int Warning=2;
		/** 错误日志 */
		public const int Error=3;
		/** debug日志 */
		public const int Debug=4;

		public static bool isErrorLog(int type)
		{
			return type==Error;
		}
	}
}