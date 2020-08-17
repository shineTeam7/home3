namespace ShineEngine
{
	public class VisitType
	{
		/** 没写 */
		public const int None=0;
		/** 公有 */
		public const int Public=1;
		/** 私有 */
		public const int Private=2;
		/** 保护 */
		public const int Protected=3;

		public static int getTypeByName(string name)
		{
			switch(name)
			{
				case "public":
					return Public;
				case "private":
					return Private;
				case "protected":
					return Protected;
			}

			return 0;
		}
	}
}