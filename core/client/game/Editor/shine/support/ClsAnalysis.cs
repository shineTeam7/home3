using System.Text;
using System.Text.RegularExpressions;

namespace ShineEngine
{
	/** 类分析 */
	public class ClsAnalysis
	{
		/** 回车 */
		private const string Enter="\r\n";
		/** 换行 */
		private const string Tab="\t";

		public class FactoryClassInfo
		{
			public string clsStr;
			/** 第一个大括号位置 */
			public int startIndex;

			public SList<FMethod> methodList=new SList<FMethod>();

			public SMap<string,FMethod> methodDic=new SMap<string,FMethod>();

			public SMap<string,FMethod> parentMethodDic=new SMap<string,FMethod>();

			public void toAddMethod(FMethod method)
			{
				//有了
				if(methodDic.contains(method.name))
					return;

				methodList.add(method);
				methodDic.put(method.name,method);
			}

			public void writeToPath(string path)
			{
				StringBuilder sb=new StringBuilder();
				sb.Append(clsStr.Substring(0,startIndex));
				sb.Append(Enter);

				foreach(FMethod method in methodList)
				{
					sb.Append(Tab);
					sb.Append("public ");

					if(method.isOverride)
						sb.Append("override ");
					else
						sb.Append("virtual ");

					sb.Append(method.returnType);
					sb.Append(" create");
					sb.Append(method.name);
					sb.Append("()");
					sb.Append(Enter);
					sb.Append(Tab);
					sb.Append("{");
					sb.Append(Enter);
					sb.Append(Tab);
					sb.Append(Tab);
					sb.Append("return new ");
					sb.Append(method.useClsName);
					sb.Append("();");
					sb.Append(Enter);
					sb.Append(Tab);
					sb.Append("}");
					sb.Append(Enter);
					sb.Append(Enter);
				}

				sb.Append("}");
				sb.Append(Enter);

				FileUtils.writeFileForUTF(path,sb.ToString());
			}

			/** 添加父记录 */
			public void addParent(FactoryClassInfo cls)
			{
				cls.methodDic.forEach((k,v)=>
				{
					parentMethodDic.put(k,v);
				});
			}

			/** 添加方法 */
			public FMethod addMethod(string type,string mark)
			{
				bool isOverride=false;
				string name=type;
				string rType=type;

				if(mark!="" && type.StartsWith(mark))
				{
					string last=type.Substring(1);
					string first=last[0].ToString();

					//首字母是大写的
					if(first==first.ToUpper())
					{
						name=last;

						FMethod pMethod=parentMethodDic.get(last);

						if(pMethod!=null)
						{
							rType=pMethod.returnType;
							isOverride=true;
						}
					}
				}

				FMethod method=new FMethod();
				method.name=name;
				method.returnType=rType;
				method.isOverride=isOverride;
				method.useClsName=type;

				toAddMethod(method);

				return method;
			}
		}

		public class FMethod
		{
			public bool isOverride;

			/** 本名 */
			public string name;

			public string returnType;

			public string useClsName;
		}

		/** 读一个工厂类 */
		public static FactoryClassInfo readFactory(string path)
		{
			string clsStr=FileUtils.readFileForUTF(path);

			FactoryClassInfo re=new FactoryClassInfo();
			re.clsStr=clsStr;
			re.startIndex=clsStr.IndexOf('{')+1;

			Regex reg1=new Regex("public (virtual|override) (.*?) create(.*?)\\(\\)");
			Regex reg2=new Regex("return new (.*?)\\(\\);");

			Match match1=reg1.Match(clsStr);
			Match match2=reg2.Match(clsStr);

			while(match1.Success)
			{
				FMethod method=new FMethod();
				method.isOverride=match1.Groups[1].Value.Equals("override");
				method.name=match1.Groups[3].Value;
				method.returnType=match1.Groups[2].Value;
				method.useClsName=match2.Groups[1].Value;

				re.toAddMethod(method);

				match1=match1.NextMatch();
				match2=match2.NextMatch();

			}

			return re;
		}
	}
}