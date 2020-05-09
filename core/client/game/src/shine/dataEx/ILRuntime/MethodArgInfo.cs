namespace ShineEngine
{
	public class MethodArgInfo
	{
		/** 参数名 */
		public string name;
		/** 参数类型 */
		public string type;
		/** 默认值 */
		public string defaultValue="";
		/** 是否变长参数 */
		public bool autoLength=false;

		/** 是否为引用类型(C#用) */
		public bool isRef;
		/** 是否为输出类型(C#用) */
		public bool isOut;

		public MethodArgInfo()
		{

		}

		public MethodArgInfo(string name,string type)
		{
			this.name=name;
			this.type=type;
			this.autoLength=false;
		}

		public MethodArgInfo(string name,string type,bool autoLength)
		{
			this.name=name;
			this.type=type;
			this.autoLength=autoLength;
		}

		public MethodArgInfo(string name,string type,string defaultValue)
		{
			this.name=name;
			this.type=type;
			this.defaultValue=defaultValue;
		}
	}
}