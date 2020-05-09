using System.Text;

namespace ShineEngine
{
	public class ILMethodInfo
	{
		/** 方法名 */
		public string name;
		/** 返回类型 */
		public string returnType="";
		/** 访问类型 */
		public int visitType=VisitType.Public;
		/** 是否虚函数 */
		public bool isAbstract;
		/** 是否需要考虑base调用 */
		public bool needBaseCall=true;
		/** 参数组 */
		public SList<MethodArgInfo> args=new SList<MethodArgInfo>();

		private string _key;

		/** 获取方法key */
		public string getKey()
		{
			if(_key==null)
			{
				if(args.isEmpty())
				{
					_key=name;
				}
				else
				{
					StringBuilder sb=new StringBuilder();
					sb.Append(name);

					foreach(MethodArgInfo v in args)
					{
						sb.Append(",");
						sb.Append(v.type);
					}

					_key=sb.ToString();
				}
			}

			return _key;
		}

		public ILMethodInfo clone()
		{
			ILMethodInfo re=new ILMethodInfo();
			re.name=name;
			re.returnType=returnType;
			re.visitType=visitType;
			re.isAbstract=isAbstract;
			re.needBaseCall=needBaseCall;
			re.args=args;
			re._key=_key;
			return re;
		}
	}
}