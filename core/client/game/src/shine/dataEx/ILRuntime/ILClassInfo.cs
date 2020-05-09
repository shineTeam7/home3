namespace ShineEngine
{
	/** ILRuntime类信息 */
	public class ILClassInfo
	{
		/** 类名 */
		public string clsName="";
		/** 是否虚类 */
		public bool isAbstract=false;

		public SList<ILMethodInfo> methods=new SList<ILMethodInfo>();

		/** 已存在方法key组 */
		public SMap<string,ILMethodInfo> methodKeys=new SMap<string,ILMethodInfo>();

		public ILClassInfo()
		{

		}

		public ILClassInfo(string name)
		{
			this.clsName=name;
		}

//		public void addMethod(string name,params string[] args)
//		{
//			addMethod(name,"",VisitType.Public,true,args);
//		}

		public void addMethod(string name,int visitType,params string[] args)
		{
			addMethod(name,"",visitType,true,args);
		}

		public void addMethod(string name,string returnType,int visitType,bool needBaseCall,params string[] args)
		{
			if(returnType==null)
				returnType="";

			ILMethodInfo m=new ILMethodInfo();
			m.name=name;
			m.returnType=returnType;
			m.visitType=visitType;
			m.needBaseCall=needBaseCall;

			if(args.Length>0)
			{
				MethodArgInfo argInfo;

				for(int i=0;i<args.Length;i+=2)
				{
					argInfo=new MethodArgInfo();
					argInfo.type=args[i];//先类型
					argInfo.name=args[i + 1];//再名字

					m.args.add(argInfo);
				}
			}

			toAddMethod(m);
		}

		/** 执行添加方法 */
		public void toAddMethod(ILMethodInfo method)
		{
			string key=method.getKey();

			//不重复添加
			if(methodKeys.contains(key))
				return;

			methods.add(method);
			methodKeys.put(key,method);
		}
	}
}