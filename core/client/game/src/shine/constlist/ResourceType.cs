namespace ShineEngine
{
	/// <summary>
	/// 资源类型
	/// </summary>
	public class ResourceType
	{
		/** 未知 */
		public const int Unknown=0;

		/** xml */
		public const int XML=1;

		/** bundle */
		public const int Bundle=2;

		/** 预制 */
		public const int Prefab=3;

		/** 场景 */
		public const int Scene=4;

		/** 字体 */
		public const int Font=5;

		/** 位图 */
		public const int Texture2D=6;

		/** 其他 */
		public const int Other=9;

		/** 二进制 */
		public const int Bin=10;

		/** 获取文件的资源类型  */
		public static int getFileResourceType(string name)
		{
			//取扩展名
			string exName=FileUtils.getFileExName(name);

			int re;

			switch(exName)
			{
				case "xml":
				{
					re=XML;
				}
					break;
				case "bundle":
				{
					re=Bundle;
				}
					break;
				case "unity":
				{
					re=Scene;
				}
					break;
				case "prefab":
				{
					re=Prefab;
				}
					break;
				case "png":
				case "jpg":
				{
					re=Texture2D;
				}
					break;
				case "bin":
				{
					re=Bin;
				}
					break;
				default:
				{
					re=Other;
				}
					break;
			}

			return re;
		}

		/** 是否是资源类型 */
		public static bool isAsset(int type)
		{
			switch(type)
			{
				case Bin:
				case XML:
				{
					return false;
				}
			}

			return true;
		}
	}
}