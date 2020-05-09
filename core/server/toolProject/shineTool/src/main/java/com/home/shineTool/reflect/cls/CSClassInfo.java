package com.home.shineTool.reflect.cls;

import com.home.shine.utils.ObjectUtils;
import com.home.shine.utils.StringUtils;
import com.home.shineTool.constlist.CodeType;
import com.home.shineTool.constlist.SGType;
import com.home.shineTool.constlist.VisitType;
import com.home.shineTool.reflect.FieldInfo;
import com.home.shineTool.reflect.MainMethodInfo;
import com.home.shineTool.reflect.MethodArgInfo;
import com.home.shineTool.reflect.MethodInfo;
import com.home.shineTool.reflect.PropertyInfo;
import com.home.shineTool.global.ShineToolSetting;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** C#类文件 */
public class CSClassInfo extends ClassInfo
{
	private static Pattern _usingReg=Pattern.compile("using (.*?);");
	
	private static Pattern _ignoreWarnReg=Pattern.compile("\\#pragma warning disable (.*?)" + regCheckEnter);
	
	private static Pattern _namespaceReg=Pattern.compile("namespace (.*?)" + regCheckEnter);
	
	private static Pattern _describeReg=Pattern.compile("/// <summary>.*?/// (.*?)\\r.*?/// </summary>",Pattern.DOTALL);
	/** 方法正则 */
	private static Pattern _csMethodReg=Pattern.compile("\\n[ \\t]*?((abstract |public |private |protected |static |override |virtual |sealed )+?)([^ ^;^\\n^\\t^=]+) ([^ ^;^\\n^\\t]+?)\\((.*?)\\)( where )?([^ ^;^\\n^\\t]*?)" + regCheckEnter);//加上结束符
	/** 值属性正则 */
	private static Pattern _csFieldReg=Pattern.compile("\\n[ \\t]+?((public |private |protected |static |final |const |volatile |readonly )+)([^ ^\\n^\\t^=]+) ([^ ^\\n^\\t^=]+)( ?)(=?)( ?)([^;^\\n^\\t]*?);"+ endCheck);//CS不支持不写修饰符的属性,除非每行前面的空格能都换成\t
	
	/** 值属性正则 */
	private static Pattern _csFieldRegInner=Pattern.compile("((public |private |protected |static |final |const |volatile |readonly )+)([^ ^\\n^\\t^=]+) ([^ ^\\n^\\t^=]+)( ?)(=?)( ?)([^;^\\n^\\t]*?);");
	
	/** 内部类正则 */
	private static Pattern _exClsReg=Pattern.compile("\\n[ \\t]*?private( static | )class ([^ ^\\n^\\t]+?):([^ ^\\n^\\t]+?)" + regCheckEnter);
	/** 注解正则 */
	private static Pattern _annotationReg=Pattern.compile("\\[(.*?)\\]" + regCheckEnter);
	
	private static Pattern _sgReg=Pattern.compile("(\\n[ \\t]+?)((abstract |public |private |protected |static |override |virtual )+?)([^ ^\\n^\\t]+?) ([^;^\\n^\\t]+?)" + regCheckEnter);
	
	private static Pattern _setterReg=Pattern.compile("set( *?)\\{");
	private static Pattern _getterReg=Pattern.compile("get( *?)\\{");
	
	private String _namespace="";
	
	/** usingSet */
	private Set<String> _usingSet=new HashSet<>();
	/** 屏蔽警告组 */
	private Set<String> _ignoreWarnSet=new HashSet<>();
	
	/** 内部类组 */
	private List<CSExClsInfo> _exClsList=new ArrayList<>();
	
	//
	/** 写过的sg */
	private Set<String> _writedSG=new HashSet<>();
	
	public CSClassInfo()
	{
		_codeType=CodeType.CS;
		_fieldReg=_csFieldReg;
		_methodReg=_csMethodReg;
		
		//TODO:从语言层分不清继承的第一个是类还是接口，这是残留的问题
		
		//添加默认导入
		//		addUsing("System");
		//		addUsing("System.Collections.Generic");
		//		addUsing("System.Linq");
		//		addUsing("System.Text");
		
		if(ShineToolSetting.isCSHasShineThing)
		{
			addShineThings();
		}
	}
	
	@Override
	protected void initDefault()
	{
		//		addUsing("System");
		//		addUsing("System.Collections.Generic");
		//		addUsing("System.Linq");
		//		addUsing("System.Text");
	}
	
	public void addShineThings()
	{
		addUsing(ShineToolSetting.csShineNamespace);
	}
	
	public void addUnityEngine()
	{
		addUsing(ShineToolSetting.csUnityNamespace);
	}
	
	@Override
	protected int getTabNum()
	{
		if(_namespace.isEmpty())
		{
			return 1;
		}
		
		return 2;
	}
	
	//元方法
	
	public void addUsing(String value)
	{
		_usingSet.add(value);
	}
	
	public void addIgnoreWarn(String value)
	{
		_ignoreWarnSet.add(value);
	}
	
	public void removeIgnoreWarn(String value)
	{
		_ignoreWarnSet.remove(value);
	}
	
	public void ignoreHidingWarn()
	{
		addIgnoreWarn("CS1692");
		addIgnoreWarn("CS0114");
		addIgnoreWarn("CS0108");
	}
	
	@Override
	protected void toReadPackage()
	{
		//c#不需要package
	}
	
	@Override
	protected void readPreHead()
	{
		Matcher m=_namespaceReg.matcher(_clsStr);
		
		if(m.find())
		{
			_namespace=m.group(1);
		}
		else
		{
			_namespace="";
			//没有namespace?
		}
	}
	
	@Override
	protected void readHeadNext()
	{
		readUsings();
		readIgnoreWarns();
		readNamespace();
		readAnnotations();
	}

	/** 读取using */
	private void readUsings()
	{
		String temp=_clsStr.substring(0,_clsIndex);
		
		Matcher m=_usingReg.matcher(temp);
		
		while(m.find())
		{
			String zz=m.group(1);
			
			_usingSet.add(zz);
		}
	}
	
	private void readIgnoreWarns()
	{
		String temp=_clsStr.substring(0,_clsIndex);
		
		Matcher m=_ignoreWarnReg.matcher(temp);
		
		while(m.find())
		{
			String zz=m.group(1);
			
			_ignoreWarnSet.add(zz);
		}
	}
	
	private void readNamespace()
	{
		String temp=_clsStr.substring(0,_clsIndex);
		
		Matcher m=_namespaceReg.matcher(temp);
		
		if(m.find())
		{
			_namespace=m.group(1);
		}
		else
		{
			//无namespace
			_namespace="";
		}
	}
	
	/** 读取类注解 */
	private void readAnnotations()
	{
		String temp=_clsStr.substring(0,_clsIndex);
		
		Matcher m=_annotationReg.matcher(temp);
		
		while(m.find())
		{
			String zz=m.group(1);
			
			_annotationDic.put(zz,"");
		}
	}
	
	@Override
	protected String getDescribByStr(String str)
	{
		String re="";
		
		int desS=str.indexOf("/**");
		
		if(desS!=-1)
		{
			int desE=str.indexOf("*/");
			
			if(desE!=-1)
			{
				re=str.substring(desS + 3,desE);
				
				re=re.trim();
			}
		}
		else
		{
			Matcher m=_describeReg.matcher(str);
			
			if(m.find())
			{
				re=m.group(1);
			}
		}
		
		return re;
	}
	
	@Override
	protected String getExtendsStr()
	{
		return ":";
	}
	
	protected String writeExtendsStr()
	{
		return ":";
	}
	
	@Override
	protected void readImplements()
	{
		int end=_clsStr.indexOf("{",_clsIndex);
		
		int imE=StringUtils.indexOf(_clsStr,_implementsEndStrs,_extendsEnd);
		
		if(imE!=-1 && imE<=end)
		{
			String last=_clsStr.substring(_extendsEnd,imE);
			
			if(!last.isEmpty())
			{
				String[] list=last.split(",");
				
				for(String imA : list)
				{
					_implementsSet.add(imA.trim());
				}
			}
		}
	}
	
	@Override
	protected Pattern getMainMethodReg()
	{
		return Pattern.compile("public " + clsName + "\\(.*?\\)");
	}

	@Override
	protected void readMainMethod()
	{
		Pattern baseReg=Pattern.compile("public " + clsName + ".*?\\(.*?\\):base\\((.*?)\\)");
		
		Matcher m=baseReg.matcher(_content);
		
		MainMethodInfo method=new MainMethodInfo();
		
		String str;
		
		//有基类构造
		if(m.find())
		{
			method.startIndex=m.start();
			
			String bb=m.group(1);
			
			String[] ar2=bb.split(",");
			
			for(String b2 : ar2)
			{
				method.superArgs.add(b2.trim());
			}
		}
		else
		{
			m=getMainMethodReg().matcher(_content);
			
			if(m.find())
			{
				method.startIndex=m.start();
			}
			else
			{
				//没有构造函数
				return;
			}
		}
		
		str=m.group();
		
		String[] ar=str.split(" ");
		
		toMakeMethodVisit(method,ar);
		
		toReadMainMethod(str,method);
		
		int left=_content.indexOf("{",method.startIndex + str.length() + 1);
		int right=StringUtils.getAnotherIndex(_content,'{','}',left);
		
		method.endIndex=right;
		
		method.content=_content.substring(left,right + 1);
		
		addMainMethod(method);
	}
	
	@Override
	protected void toReadMainMethod(String str,MethodInfo method)
	{
		readMethodName(str,method);
		readMethodArgs(str,method);
		
		method.returnType="";
	}

	@Override
	protected void readEx()
	{
		readExCls();
		readSGMethods();
	}
	
	@Override
	protected void readMethods()
	{
		super.readMethods();
		
		sortMethodsByStartIndex();
	}

	@Override
	protected void toReadMethod(String str,Matcher matcher,MethodInfo method)
	{
		String genericStr=matcher.group(7);
		
		if(!genericStr.isEmpty())
		{
			method.genericTypeStr=genericStr;
		}
		
		readMethodName(str,method);
		readMethodArgs(str,method);
		
		int left=str.indexOf("(");
		
		int lsp=str.lastIndexOf(" ",left - 1);
		
		int llsp=str.lastIndexOf(" ",lsp - 1);
		
		method.returnType=str.substring(llsp + 1,lsp);
	}

	@Override
	protected void toReadField(String str,FieldInfo field,Matcher m)
	{
		str=formatFiledStr(str);
		//重新match
		m=_csFieldRegInner.matcher(str);
		m.find();
		
		String str2=m.group(8);
		
		if(!str2.isEmpty())
		{
			field.defaultValue=str2.trim();
		}
		
		field.name=m.group(4).trim();
		field.type=m.group(3).replace(" ","");//去掉空格
		
		String[] arr=str.split(" ");
		
		
		for(String s : arr)
		{
			switch(s)
			{
				case "final":
				case "const":
				{
					field.isConst=true;
				}
					break;
				case "readonly":
				{
					field.isReadOnly=true;
				}
					break;
				case "volatile":
				{
					field.isVolatile=true;
				}
					break;
			}
		}
	}

	@Override
	protected void toReadPropAnnotation(String str,PropertyInfo prop)
	{
		
	}

	//--write--//
	
	@Override
	protected void beginWrite()
	{
		super.beginWrite();
		
		_writedSG.clear();
	}
	
	@Override
	protected void toWriteField(StringBuilder sb,FieldInfo field)
	{
		writeLeftTab(sb);
		field.writeVisitStr(sb);
		
		//const的不需要加static了
		if(!field.isConst)
		{
			field.writeStaticStr(sb);
		}
		
		if(field.isVolatile)
		{
			sb.append("volatile ");
		}
		
		if(field.isConst)
		{
			sb.append("const ");
		}
		
		if(field.isReadOnly)
		{
			sb.append("readonly ");
		}
		
		sb.append(field.type);
		sb.append(" ");
		sb.append(field.name);
		
		if(!field.defaultValue.isEmpty())
		{
			sb.append("=");
			sb.append(field.defaultValue);
		}
		
		sb.append(";");
		sb.append(Enter);
	}

	@Override
	protected void toWriteMainMethod(StringBuilder sb)
	{
		if(_mainMethod==null)
		{
			return;
		}
		
		writeLeftTab(sb);
		sb.append("public ");
		sb.append(clsName);
		sb.append("(");
		writeMethodArgs(sb,_mainMethod);
		sb.append(")");
		
		if(_mainMethod.superArgs.size()>0)
		{
			sb.append(":base(");
			
			int i=0;
			
			for(String s : _mainMethod.superArgs)
			{
				if(i>0)
				{
					sb.append(",");
				}
				
				sb.append(s);
				
				++i;
			}
			
			sb.append(")");
		}
		
		sb.append(Enter);
		
		writeLeftTab(sb);
		sb.append(_mainMethod.content);
		sb.append(Enter);
		
		writeLeftTab(sb);
		sb.append(Enter);
	}

	@Override
	protected void preWriteMethod(StringBuilder sb,MethodInfo method)
	{
		if(method.sgType==SGType.None)
		{
			super.preWriteMethod(sb,method);
		}
		else
		{
			//没写过
			if(!_writedSG.contains(method.name))
			{
				_writedSG.add(method.name);
				
				writeSGMethod(sb,method);
			}
		}
	}
	
	@Override
	protected void toWriteMethod(StringBuilder sb,MethodInfo method)
	{
		writeLeftTab(sb);
		method.writeVisitStr(sb);
		method.writeOverrideStr(sb);
		method.writeVirtualStr(sb);
		method.writeAbstractStr(sb);
		method.writeStaticStr(sb);
		
		if(!method.returnType.isEmpty())
		{
			sb.append(method.returnType);
		}
		else
		{
			sb.append(getCode().Void);
		}
		
		sb.append(" ");
		sb.append(method.name);
		sb.append("(");
		
		writeMethodArgs(sb,method);
		
		sb.append(")");
		
		if(!method.genericTypeStr.isEmpty())
		{
			sb.append(" where "+method.genericTypeStr);
		}
		
		if(method.isAbstract)
		{
			sb.append(";");
			sb.append(Enter);
		}
		else
		{
			sb.append(Enter);
			writeLeftTab(sb);
			sb.append(method.content);
			sb.append(Enter);
		}
		
		writeLeftTab(sb);
		sb.append(Enter);
	}

	/** 写一个sg */
	private void writeSGMethod(StringBuilder sb,MethodInfo method)
	{
		MethodInfo setter;
		MethodInfo getter;
		
		String type;
		MethodInfo describeMethod=null;
		
		if(method.sgType==SGType.Setter)
		{
			setter=method;
			type=method.args.get(0).type;
			getter=getMethod(method.name);
		}
		else
		{
			getter=method;
			type=method.returnType;
			setter=getMethod(method.name + "," + type);
		}
		
		if(setter!=null && !setter.describe.isEmpty())
		{
			describeMethod=setter;
		}
		
		if(getter!=null && !getter.describe.isEmpty())
		{
			describeMethod=getter;
		}
		
		if(describeMethod!=null)
		{
			writePropDescribe(sb,describeMethod);
		}
		
		writeLeftTab(sb);
		
		method.writeVisitStr(sb);
		method.writeOverrideStr(sb);
		method.writeVirtualStr(sb);
		method.writeAbstractStr(sb);
		method.writeStaticStr(sb);
		
		
		sb.append(type);
		sb.append(" ");
		sb.append(method.name);
		
		sb.append(Enter);
		writeLeftTab(sb);
		sb.append("{");
		sb.append(Enter);
		
		if(setter!=null)
		{
			writeMethodTab(sb,0);
			sb.append("set ");
			sb.append(setter.content);
			sb.append(Enter);
		}
		
		if(getter!=null)
		{
			writeMethodTab(sb,0);
			sb.append("get ");
			sb.append(getter.content);
			sb.append(Enter);
		}
		
		writeLeftTab(sb);
		sb.append("}");
		sb.append(Enter);
		
		writeLeftTab(sb);
		sb.append(Enter);
	}
	
	@Override
	protected void afterWriteClassDescribe(StringBuilder sb)
	{
		writeAnnotations(sb);
	}
	
	private void writeAnnotations(StringBuilder sb)
	{
		for(String k : _annotationDic.getSortedKeyList())
		{
			writeClassTab(sb);
			sb.append("[");
			sb.append(k);
			sb.append("]");
			sb.append(Enter);
		}
	}
	
	@Override
	protected void writeEx(StringBuilder sb)
	{
		writeExCls(sb);
	}

	@Override
	protected void toWritePackage(StringBuilder sb)
	{
		//C#没有包
	}
	
	/** 读方法名 */
	private void readMethodName(String str,MethodInfo method)
	{
		int left=str.indexOf("(");
		
		int lsp=str.lastIndexOf(" ",left - 1);
		
		method.name=str.substring(lsp + 1,left);
	}
	
	/** 读方法参数 */
	private void readMethodArgs(String str,MethodInfo method)
	{
		preReadMethodArgs(str,method,tt->
		{
			if(tt.indexOf("params ")!=-1)
			{
				String[] ar=tt.substring("params ".length(),tt.length()).split("\\[\\] ");
				
				return new MethodArgInfo(ar[1].trim(),ar[0].trim(),true);
			}
			else
			{
				MethodArgInfo argInfo=new MethodArgInfo();
				
				int startIndex=0;
				
				if(tt.startsWith("ref "))
				{
					startIndex=4;
					argInfo.isRef=true;
				}
				else if(tt.startsWith("in "))
				{
					startIndex=3;
					argInfo.isIn=true;
				}
				else if(tt.startsWith("out "))
				{
					startIndex=4;
					argInfo.isOut=true;
				}
				
				int sIndex=tt.indexOf(" ",startIndex);
				
				String s1=tt.substring(startIndex,sIndex);
				String s2=tt.substring(sIndex+1,tt.length());
				
				String[] ar2=s2.split("=");
				
				if(ar2.length>1)
				{
					argInfo.name=ar2[0].trim();
					argInfo.type=s1.trim();
					argInfo.defaultValue=ar2[1].trim();
				}
				else
				{
					argInfo.name=s2.trim();
					argInfo.type=s1.trim();
				}
				
				return argInfo;
			}
		});
	}
	
	@Override
	protected void toMakeOneMethodVisit(MethodInfo method,String str)
	{
		super.toMakeOneMethodVisit(method,str);
		
		switch(str)
		{
			case "override":
			{
				method.isOverride=true;
			}
				break;
			case "abstract":
			{
				method.isAbstract=true;
			}
				break;
			case "virtual":
			{
				method.isVirtual=true;
			}
				break;
		}
	}
	
	/** 读内部类 */
	private void readExCls()
	{
		_exClsList.clear();
		
		Matcher m=_exClsReg.matcher(_content);
		
		while(m.find())
		{
			String str=m.group();
			
			int index=m.start();
			
			int start=_content.indexOf("{",index + str.length() + 1);
			
			int end=StringUtils.getAnotherIndex(_content,'{','}',start);
			
			CSExClsInfo ex=new CSExClsInfo();
			ex.content=_content.substring(index,end + 1);
			ex.startIndex=start;
			ex.endIndex=end;
			
			_exClsList.add(ex);
			
			for(MethodInfo method : getMethodDic())
			{
				//在中间
				if(start<=method.startIndex && method.endIndex<=end)
				{
					//删掉
					removeMethod(method);
				}
			}
		}
	}
	
	private void writeExCls(StringBuilder sb)
	{
		for(CSExClsInfo ex : _exClsList)
		{
			writeLeftTab(sb);
			sb.append(ex.content);
			sb.append(Enter);
			
			writeLeftTab(sb);
			sb.append(Enter);
		}
	}
	
	private void readSGMethods()
	{
		Matcher m=_sgReg.matcher(_content);
		
		while(m.find())
		{
			String str=m.group();
			
			int index=m.start();
			
			int start=_content.indexOf("{",index + str.length() + 1);
			
			int end=StringUtils.getAnotherIndex(_content,'{','}',start);
			
			//
			
			//去掉前后缀\r
			str=str.substring(m.group(1).length(),str.length() - 1);
			
			String[] arr=str.split(" ");
			
			String name=arr[arr.length - 1];
			String type=arr[arr.length - 2];
			
			//
			
			Matcher m2=_setterReg.matcher(_content);
			
			if(m2.find(start))
			{
				int sIndex=m2.start();
				
				if(sIndex<end)
				{
					int sStart=_content.indexOf("{",sIndex);
					int sEnd=StringUtils.getAnotherIndex(_content,'{','}',sStart);
					
					MethodInfo setter=new MethodInfo();
					setter.name=name;
					setter.args.add(new MethodArgInfo("value",type));
					setter.returnType=getCode().Void;
					setter.sgType=SGType.Setter;
					
					toMakeMethodVisit(setter,arr);
					
					setter.startIndex=sStart;
					setter.endIndex=sEnd;
					setter.content=_content.substring(sStart,sEnd + 1);
					
					addMethod(setter);
				}
			}
			
			m2=_getterReg.matcher(_content);
			
			if(m2.find(start))
			{
				int sIndex=m2.start();
				
				if(sIndex<end)
				{
					int sStart=_content.indexOf("{",sIndex);
					int sEnd=StringUtils.getAnotherIndex(_content,'{','}',sStart);
					
					MethodInfo getter=new MethodInfo();
					getter.name=name;
					getter.returnType=type;
					getter.sgType=SGType.Getter;
					
					toMakeMethodVisit(getter,arr);
					
					getter.startIndex=sStart;
					getter.endIndex=sEnd;
					getter.content=_content.substring(sStart,sEnd + 1);
					
					addMethod(getter);
				}
			}
		}
	}
	
	@Override
	protected boolean toCheckMethodReadIndex(int index)
	{
		for(CSExClsInfo ex : _exClsList)
		{
			//在中间
			if(ex.startIndex<=index && index<=ex.endIndex)
			{
				return false;
			}
		}
		
		return true;
	}
	
	//write
	
	/** 写方法参数 */
	private void writeMethodArgs(StringBuilder sb,MethodInfo method)
	{
		int i=0;
		
		for(MethodArgInfo arg : method.args)
		{
			if(i>0)
			{
				sb.append(",");
			}
			
			if(arg.autoLength)
			{
				sb.append("params ");
				sb.append(arg.type);
				sb.append("[] ");
				sb.append(arg.name);
			}
			else
			{
				if(arg.isRef)
				{
					sb.append("ref ");
				}
				else if(arg.isIn)
				{
					sb.append("in ");
				}
				else if(arg.isOut)
				{
					sb.append("out ");
				}
				
				sb.append(arg.type);
				sb.append(" ");
				sb.append(arg.name);
				
				if(!arg.defaultValue.isEmpty())
				{
					sb.append("=");
					sb.append(arg.defaultValue);
				}
			}
			
			++i;
		}
	}
	
	@Override
	protected void writeClsDescribe(StringBuilder sb,String des)
	{
		if(!des.isEmpty())
		{
			writeClassTab(sb);
			sb.append("/// <summary>");
			sb.append(Enter);
			writeClassTab(sb);
			sb.append("/// ");
			sb.append(clsDescribe);
			sb.append(Enter);
			writeClassTab(sb);
			sb.append("/// </summary>");
			sb.append(Enter);
		}
	}
	
	@Override
	protected void writePropDescribe(StringBuilder sb,PropertyInfo prop)
	{
		if(prop.describe.isEmpty())
		{
			return;
		}
		
		if(prop.visitType==VisitType.Public || prop.visitType==VisitType.Protected)
		{
			writeLeftTab(sb);
			sb.append("/// <summary>");
			sb.append(Enter);
			writeLeftTab(sb);
			sb.append("/// ");
			sb.append(prop.describe);
			sb.append(Enter);
			writeLeftTab(sb);
			sb.append("/// </summary>");
			sb.append(Enter);
		}
		else
		{
			writeLeftTab(sb);
			sb.append("/** ");
			sb.append(prop.describe);
			sb.append(" */");
			sb.append(Enter);
		}
	}
	
	@Override
	protected void writeImplements(StringBuilder sb)
	{
		if(_implementsSet.size()>0)
		{
			//没有继承类
			if(extendsClsName.isEmpty())
			{
				sb.append(":");
			}
			
			List<String> list=ObjectUtils.getSortSetKeys(_implementsSet);
			
			int i=0;
			
			for(String s : list)
			{
				if(i>0)
				{
					sb.append(",");
				}
				
				sb.append(s);
				
				++i;
			}
		}
	}
	
	@Override
	protected void writeHead(StringBuilder sb)
	{
		writeNamespace(sb);
		writeIgnoreWarn(sb);
		writeUsing(sb);
	}
	
	private void writeUsing(StringBuilder sb)
	{
		StringBuilder sb2=new StringBuilder();
		
		for(String s : ObjectUtils.getSortSetKeys(_usingSet))
		{
			sb2.append("using ");
			sb2.append(s);
			sb2.append(";");
			sb2.append(Enter);
		}
		
		sb2.append(Enter);
		
		sb.insert(0,sb2.toString());
	}
	
	private void writeIgnoreWarn(StringBuilder sb)
	{
		StringBuilder sb2=new StringBuilder();
		
		//置顶
		if(_ignoreWarnSet.contains("CS1692"))
		{
			sb2.append("#pragma warning disable ");
			sb2.append("CS1692");
			sb2.append(Enter);
		}
		
		for(String s : ObjectUtils.getSortSetKeys(_ignoreWarnSet))
		{
			if(!s.equals("CS1692"))
			{
				sb2.append("#pragma warning disable ");
				sb2.append(s);
				sb2.append(Enter);
			}
		}
		
		//		sb2.append(Enter);
		
		sb.insert(0,sb2.toString());
	}
	
	private void writeNamespace(StringBuilder sb)
	{
		if(!_namespace.isEmpty())
		{
			StringBuilder sb2=new StringBuilder();
			
			sb2.append("namespace ");
			sb2.append(_namespace);
			sb2.append(Enter);
			sb2.append("{");
			sb2.append(Enter);
			
			sb.insert(0,sb2.toString());
			
			sb.append("}");
			sb.append(Enter);
		}
	}
	
	private class CSExClsInfo
	{
		public String content;
		
		public int startIndex;
		
		public int endIndex;
	}
}
