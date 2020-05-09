package com.home.shineTool.reflect.cls;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.utils.StringUtils;
import com.home.shineTool.constlist.CodeType;
import com.home.shineTool.constlist.SGType;
import com.home.shineTool.global.ShineToolSetting;
import com.home.shineTool.reflect.*;
import com.home.shineTool.reflect.code.TSCodeInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TSClassInfo extends ClassInfo
{
	/** 值属性正则 */
	private static Pattern _tsFieldReg=Pattern.compile("\\n[ \\t]*?((public |private |protected |static |final |const |volatile |readonly )+)([^ ^\\n^\\t]+?):([^ ^\\n^\\t^=]+)( ?)(=?)( ?)([^;^\\n^\\t]*?);"+ endCheck);
	/** 方法正则 */
	private static Pattern _tsMethodReg=Pattern.compile("\\n[ \\t]+?((public |private |protected |static |override )+)([^ ^\\n^\\t]+?)\\((.*?)\\):([^ ^\\n^\\t]+?)" + endCheck);
	///** AS3值属性右侧正则 */
	//private static Pattern _tsFieldRightReg=Pattern.compile("(var|const) ([^ ^\\n^\\t]+?)\\:(.+?);");
	
	private static Pattern _tsClsReg=Pattern.compile("((final|abstract|sealed|export| )*)class");
	
	private static Pattern _namespaceReg=Pattern.compile("namespace (.*?)" + regCheckEnter);
	
	private String _namespace="";
	
	public TSClassInfo()
	{
		_codeType=CodeType.TS;
		_fieldReg=_tsFieldReg;
		_methodReg=_tsMethodReg;
		_clsReg=_tsClsReg;
		
		if(ShineToolSetting.TSUseShineNamespace)
		{
			_namespace="Shine";
		}
	}
	
	@Override
	protected void initDefault()
	{
	
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
	
	@Override
	protected void toReadPackage()
	{
		//TS不需要package
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
		readNamespace();
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
	
	@Override
	protected Pattern getMainMethodReg()
	{
		return Pattern.compile("constructor\\(.*?\\)");
	}
	
	@Override
	protected void toReadMainMethod(String str,MethodInfo method)
	{
		readMethodName(str,method);
		readMethodArgs(str,method);
		readMethodOther(str,method);
		
		method.returnType="";
	}
	
	@Override
	protected void readEx()
	{
	
	}
	
	@Override
	protected void toReadMethod(String str,Matcher matcher,MethodInfo method)
	{
		readMethodName(str,method);
		readMethodArgs(str,method);
		readMethodOther(str,method);
		
		int last=str.lastIndexOf(":");
		
		method.returnType=str.substring(last + 1,str.length());
	}
	
	@Override
	protected void toReadField(String str,FieldInfo field,Matcher m)
	{
		String str2=m.group(8);
		
		if(!str2.isEmpty())
		{
			field.defaultValue=str2.trim();
		}
		
		field.name=m.group(3).trim();
		field.type=m.group(4).replace(" ","");//去掉空格
		
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
	
	@Override
	protected void toWriteField(StringBuilder sb,FieldInfo field)
	{
		writeLeftTab(sb);
		field.writeVisitStr(sb);
		field.writeStaticStr(sb);
		
		//if(field.isConst)
		//{
		//	sb.append("const ");
		//}
		//else
		//{
		//	sb.append("var ");
		//}
		
		sb.append(field.name);
		sb.append(":");
		sb.append(field.type);
		
		if(!field.defaultValue.isEmpty())
		{
			sb.append("=");
			// 打个补丁
			if (field.defaultValue.endsWith("L")) {
				field.defaultValue = field.defaultValue.substring(0, field.defaultValue.length() - 1);
			}
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
		
		sb.append("constructor");
		sb.append("(");
		writeMethodArgs(sb,_mainMethod);
		sb.append(")");
		sb.append(Enter);

		writeLeftTab(sb);

		if(!extendsClsName.isEmpty())
		{
			sb.append("{");
			sb.append(Enter);
			writeMethodTab(sb,0);
			
			sb.append(getCode().Super + "(");

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

			sb.append(");");

			//去掉"{"
			sb.append(_mainMethod.content.substring(1));
		}
		else
		{
			sb.append(_mainMethod.content);
		}

		sb.append(Enter);
		writeLeftTab(sb);
		sb.append(Enter);
	}
	@Override
	protected void toWriteMethod(StringBuilder sb,MethodInfo method)
	{
		writeLeftTab(sb);
		//不需要override
		//method.writeOverrideStr(sb);
		method.writeVisitStr(sb);
		method.writeStaticStr(sb);
		//sb.append("function ");
		sb.append(method.name);
		sb.append("(");
		writeMethodArgs(sb,method);
		sb.append("):");
		
		if(!method.returnType.isEmpty())
		{
			sb.append(method.returnType);
		}
		else
		{
			sb.append(getCode().Void);
		}
		
		sb.append(Enter);
		
		writeLeftTab(sb);
		sb.append(method.content);
		sb.append(Enter);
		
		writeLeftTab(sb);
		sb.append(Enter);
	}
	
	@Override
	protected void writeEx(StringBuilder sb)
	{
	
	}
	
	@Override
	protected void toWritePackage(StringBuilder sb)
	{
		//TS没有包
	}
	
	@Override
	protected void writeImports(StringBuilder sb)
	{
		//TS没有import
	}
	
	/** 读取方法名 */
	private void readMethodName(String str,MethodInfo method)
	{
		int left=str.indexOf("(");
		
		int lsp=str.lastIndexOf(" ",left - 1);
		
		method.name=str.substring(lsp + 1,left);
	}
	
	/** 读取方法参数 */
	private void readMethodArgs(String str,MethodInfo method)
	{
		preReadMethodArgs(str,method,tt->
		{
			//变长
			if(tt.indexOf("...")==0)
			{
				String temp=tt.substring(3,tt.length());
				
				String[] ar=temp.split(":");
				
				return new MethodArgInfo(ar[0].trim(),ar[1].trim(),true);
			}
			else
			{
				String[] ar=tt.split(":");
				
				String[] ar2=ar[1].split("=");
				
				if(ar2.length>1)
				{
					return new MethodArgInfo(ar[0].trim(),ar2[0].trim(),ar2[1].trim());
				}
				else
				{
					return new MethodArgInfo(ar[0].trim(),ar[1].trim());
				}
			}
		});
	}
	
	/** 读方法其他(复写和setter/getter) */
	private void readMethodOther(String str,MethodInfo method)
	{
		String[] arr=str.split(" ");
		
		for(String s : arr)
		{
			switch(s)
			{
				case "override":
				{
					method.isOverride=true;
				}
				break;
				case "set":
				{
					method.sgType=SGType.Setter;
				}
				break;
				case "get":
				{
					method.sgType=SGType.Getter;
				}
				break;
			}
		}
	}
	
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
				sb.append("...");
			}
			
			sb.append(arg.name);
			sb.append(":");
			sb.append(arg.type);
			
			if(!arg.defaultValue.isEmpty())
			{
				sb.append("=");
				sb.append(arg.defaultValue);
			}
			
			++i;
		}
	}
	
	@Override
	protected void writeClassVisitType(StringBuilder sb)
	{
		//无修饰
		
		if(!_namespace.isEmpty())
		{
			sb.append("export ");
		}
	}
	
	@Override
	protected void writeHead(StringBuilder sb)
	{
		writeNamespace(sb);
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

	/** 读继承和接口 */
	@Override
	protected void readExtends()
	{
		int end=_clsStr.indexOf("{",_clsIndex);

		int ccIndex=_clsStr.indexOf(" class ",_clsIndex);

		int offset = 7;
		if (ccIndex == -1){
			offset = 6;
			ccIndex=_clsStr.indexOf("class ",_clsIndex);
		}

		if (ccIndex == -1){
			System.out.println(_clsStr);
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		}
		int nameS=ccIndex + offset;

		int nameE=StringUtils.indexOf(_clsStr,_clsNameEndStrs,nameS);


		clsName=_clsStr.substring(nameS,nameE);

		extendsClsName="";
		_extendsEnd=nameE;

		int superS=_clsStr.indexOf(getExtendsStr(),nameE);

		if(superS!=-1 && superS<=end)
		{
			superS+=getExtendsStr().length();

			int superE=StringUtils.indexOf(_clsStr,_superEndStrs,superS);

			if(superE!=-1 && superE<=end)
			{
				extendsClsName=_clsStr.substring(superS,superE).trim();

				_extendsEnd=superE;
			}
		}
	}
}