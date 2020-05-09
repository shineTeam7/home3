package com.home.shineTool.reflect.cls;

import com.home.shineTool.constlist.CodeType;
import com.home.shineTool.constlist.SGType;
import com.home.shineTool.reflect.FieldInfo;
import com.home.shineTool.reflect.MethodArgInfo;
import com.home.shineTool.reflect.MethodInfo;
import com.home.shineTool.reflect.PropertyInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** AS3类信息 */
public class AS3ClassInfo extends ClassInfo
{
	/** 包正则 */
	private static Pattern _packageReg=Pattern.compile("package (.*?)" + regCheckEnter);
	/** 值属性正则 */
	private static Pattern _asFieldReg=Pattern.compile("\\n\\t\\t((public |private |protected |static )*)(var|const) ([^ ^\\n^\\t]+?)\\:(.+?);"+ endCheck);
	/** 方法正则 */
	private static Pattern _asMethodReg=Pattern.compile("\\n[ \\t]+?((public |private |protected |static |override )+) function ([^\\n^\\t]*?)\\((.*?)\\)\\:(.*)" + regCheckEnter);
	/** AS3值属性右侧正则 */
	private static Pattern _asFieldRightReg=Pattern.compile("(var|const) ([^ ^\\n^\\t]+?)\\:(.+?);");
	
	public AS3ClassInfo()
	{
		_codeType=CodeType.AS3;
		_fieldReg=_asFieldReg;
		_methodReg=_asMethodReg;
	}
	
	@Override
	protected void initDefault()
	{
		
	}
	
	@Override
	protected int getTabNum()
	{
		return 2;
	}
	
	@Override
	protected void toReadPackage()
	{
		Matcher m=_packageReg.matcher(_clsStr);
		
		m.find();
		
		packageStr=m.group(1);
	}

	@Override
	protected Pattern getMainMethodReg()
	{
		return Pattern.compile("public function " + clsName + "\\(.*?\\)");
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
		Matcher m2=_asFieldRightReg.matcher(str);
		
		if(!m2.find())
		{
			return;
		}
		
		String mm=m2.group(1);
		field.name=m2.group(2);
		String ds=m2.group(3);
		
		int dIndex=ds.indexOf("=");
		
		if(dIndex!=-1)
		{
			String left=ds.substring(0,dIndex);
			String right=ds.substring(dIndex + 1,ds.length());
			
			field.defaultValue=right.trim();
			
			ds=left.trim();
		}
		
		field.type=ds;
		
		if(mm.equals("const"))
		{
			field.isConst=true;
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
		
		if(field.isConst)
		{
			sb.append("const ");
		}
		else
		{
			sb.append("var ");
		}
		
		sb.append(field.name);
		sb.append(":");
		sb.append(field.type);
		
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
		sb.append("function ");
		sb.append(clsName);
		sb.append("(");
		writeMethodArgs(sb,_mainMethod);
		sb.append(")");
		sb.append(Enter);
		
		if(_mainMethod.superArgs.size()>0)
		{
			writeLeftTab(sb);
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
			sb.append(Enter);
		}
		
		writeLeftTab(sb);
		sb.append(_mainMethod.content);
		sb.append(Enter);
		
		writeLeftTab(sb);
		sb.append(Enter);
	}

	@Override
	protected void toWriteMethod(StringBuilder sb,MethodInfo method)
	{
		writeLeftTab(sb);
		method.writeOverrideStr(sb);
		method.writeVisitStr(sb);
		method.writeStaticStr(sb);
		sb.append("function ");
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
		StringBuilder sb2=new StringBuilder();
		sb2.append("package ");
		sb2.append(packageStr);
		sb2.append(Enter);
		sb2.append("{");
		sb2.append(Enter);
		
		sb.insert(0,sb2.toString());
		sb.append("}");
		sb.append(Enter);
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
				
				return new MethodArgInfo(temp.trim(),"*",true);
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
				sb.append(arg.name);
			}
			else
			{
				sb.append(arg.name);
				sb.append(":");
				sb.append(arg.type);
			}
			
			++i;
		}
	}
}
