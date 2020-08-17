package com.home.shineTool.reflect.cls;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.utils.StringUtils;
import com.home.shineTool.constlist.CodeType;
import com.home.shineTool.reflect.AnnotationInfo;
import com.home.shineTool.reflect.FieldInfo;
import com.home.shineTool.reflect.MethodArgInfo;
import com.home.shineTool.reflect.MethodInfo;
import com.home.shineTool.reflect.PropertyInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** java类信息 */
public class JavaClassInfo extends ClassInfo
{
	/** 方法正则 */
	private static Pattern _javaMethodReg=Pattern.compile("\\n[ \\t]+?((public |private |protected |static |abstract |override |final )+)([^ ^\\n^\\t^=]+) ([^ ^\\n^\\t^=]+)\\(([^\\n^\\t^;]*?)\\)(;?) *?" + endCheck);
	/** 值属性正则 */
	private static Pattern _javaFieldReg=Pattern.compile("\\n( {4}|\\t)((public |private |protected |static |final |const |volatile )*)([^ ^\\n^\\t^=]+) ([^ ^\\n^\\t^=]+)( ?)(=?)( ?)([^;^\\n^\\t]*?);"+ endCheck);
	/** 内部类正则 */
	private static Pattern _javaExClsReg=Pattern.compile("\\n[ \\t]*?(public |private |protected )((static )?)class ([^ ^\\n^\\t]+?)(.*?)" + endCheck);
	/** 注解正则 */
	private static Pattern _javaAnnotationReg=Pattern.compile("@([^\\(^\\)^ ^\\n^\\t^\\r]*)\\(?([^\\(^\\)^ ^\\n^\\t^\\r]*)\\)?" + regCheckEnter);
	
	/** 内部类组 */
	private List<JavaExClsInfo> _exClsList=new ArrayList<>();
	
	public JavaClassInfo()
	{
		_codeType=CodeType.Java;
		_fieldReg=_javaFieldReg;
		_methodReg=_javaMethodReg;
	}
	
	@Override
	protected void initDefault()
	{
		
	}
	
	@Override
	protected int getTabNum()
	{
		return 1;
	}
	
	@Override
	protected void toReadPackage()
	{
		int index=_clsStr.indexOf(";");
		
		try
		{
			packageStr=_clsStr.substring(8,index);
		}
		catch(Exception e)
		{
			Ctrl.print("出错",e);
		}
	}
	
	@Override
	protected Pattern getMainMethodReg()
	{
		return Pattern.compile("public " + clsName + "\\(.*?\\)");
	}
	
	@Override
	protected void toReadMainMethod(String str,MethodInfo method)
	{
		readMethodName(str,method);
		readMethodArgs(str,method);
		
		method.returnType="";
	}

	@Override
	protected void readHeadNext()
	{
		super.readHeadNext();
		
		readAnnotations();
	}
	
	@Override
	protected void readEx()
	{
		readExCls();
	}

	/** 读取类注解 */
	private void readAnnotations()
	{
		String temp=_clsStr.substring(0,_clsIndex);
		
		Matcher m=_javaAnnotationReg.matcher(temp);
		
		while(m.find())
		{
			_annotationDic.put(m.group(1),m.group(2));
		}
	}
	
	@Override
	protected void toReadMethod(String str,Matcher matcher,MethodInfo method)
	{
		String nameStr=matcher.group(3);
		
		if(nameStr.startsWith("<"))
		{
			method.genericTypeStr=nameStr.substring(1,nameStr.indexOf(">"));
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
		////去掉末尾的;和\r
		//str=str.substring(0,str.length() - 2);
		//
		//int dIndex=str.indexOf("=");
		//
		//if(dIndex!=-1)
		//{
		//	String left=str.substring(0,dIndex);
		//	String right=str.substring(dIndex + 1,str.length());
		//
		//	field.defaultValue=right.trim();
		//
		//	str=left.trim();
		//}
		
		String str2=m.group(9);
		
		if(!str2.isEmpty())
		{
			field.defaultValue=str2.trim();
		}
		
		field.name=m.group(5).trim();
		char nameC=field.name.charAt(field.name.length() - 1);
		
		if(nameC=='<' || nameC==',')
		{
			Ctrl.throwError(clsName,"字段名非法:",field.name);
		}
		
		field.type=m.group(4).trim();
		char typeC=field.type.charAt(field.type.length() - 1);
		
		if(typeC=='<' || typeC==',')
		{
			Ctrl.throwError(clsName,"字段类型非法:",field.type);
		}
		
		String[] arr=str.split(" ");
		
		for(String s : arr)
		{
			switch(s)
			{
				case "final":
				{
					field.isConst=true;
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
		//目前只读@Override,@MaybeNull,@MaybeExtends;
		
		Matcher m=_javaAnnotationReg.matcher(str);
		
		while(m.find())
		{
			AnnotationInfo aInfo=new AnnotationInfo();
			aInfo.name=m.group(1);
			String arg=m.group(2);
			
			if(!arg.isEmpty())
			{
				aInfo.args=arg.split(",");
				
				for(int i=0;i<aInfo.args.length;i++)
				{
					aInfo.args[i]=aInfo.args[i].trim();
				}
			}
			
			if(prop instanceof MethodInfo)
			{
				MethodInfo method=(MethodInfo)prop;
				
				if(aInfo.name.equals("Override"))
				{
					method.isOverride=true;
					continue;
				}
			}
			else if(prop instanceof FieldInfo)
			{
				FieldInfo field=(FieldInfo)prop;
				
				if(aInfo.name.equals("MaybeNull"))
				{
					field.maybeNull=true;
				}
				else if(aInfo.name.equals("MapKeyInValue"))
				{
					String arg1=aInfo.args[0];
					field.mapKeyInValueKey=StringUtils.cutOutsideOne(arg1);//去掉首尾的"
				}
				else if(aInfo.name.equals("NoUpgrade"))
				{
					field.noUpgrade=true;
				}
			}
			
			prop.addAnnotation(aInfo);
		}
		
	}
	
	protected void writeAnnotation(StringBuilder sb,PropertyInfo prop,String except)
	{
		for(AnnotationInfo v : prop.annotationList)
		{
			if(!v.name.equals(except))
			{
				writeLeftTab(sb);
				sb.append("@");
				sb.append(v.name);
				
				if(v.args.length>0)
				{
					sb.append('(');
					
					for(int i=0;i<v.args.length;i++)
					{
						if(i>0)
							sb.append(',');
						
						sb.append(v.args[i]);
					}
					
					sb.append(')');
				}
				
				sb.append(Enter);
			}
		}
	}

	@Override
	protected void toWriteField(StringBuilder sb,FieldInfo field)
	{
		writeAnnotation(sb,field,"");
		
		writeLeftTab(sb);
		field.writeVisitStr(sb);
		field.writeStaticStr(sb);
		
		if(field.isVolatile)
		{
			sb.append("volatile ");
		}
		
		if(field.isConst)
		{
			sb.append("final ");
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
		sb.append(Enter);
		writeLeftTab(sb);
		
		if(_mainMethod.superArgs.size()>0)
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
		writeAnnotation(sb,method,"Override");
		
		//override最后写
		if(method.isOverride)
		{
			writeLeftTab(sb);
			sb.append("@Override");
			sb.append(Enter);
		}
		
		writeLeftTab(sb);
		method.writeVisitStr(sb);
		method.writeAbstractStr(sb);
		method.writeStaticStr(sb);
		method.writeFinalStr(sb);
		
		if(!method.genericTypeStr.isEmpty())
		{
			sb.append("<"+method.genericTypeStr+"> ");
		}
		
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

	@Override
	protected void afterWriteClassDescribe(StringBuilder sb)
	{
		writeAnnotations(sb);
	}
	
	private void writeAnnotations(StringBuilder sb)
	{
		for(String k : _annotationDic.getSortedKeyList())
		{
			String v=_annotationDic.get(k);
			
			writeClassTab(sb);
			sb.append("@" + k);
			
			if(!v.isEmpty())
			{
				sb.append('(');
				sb.append(v);
				sb.append(')');
			}
			
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
		StringBuilder sb2=new StringBuilder();
		sb2.append("package ");
		sb2.append(packageStr);
		sb2.append(";");
		sb2.append(Enter);
		
		sb.insert(0,sb2.toString());
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
			if(tt.indexOf("...")!=-1)
			{
				String[] ar=tt.split("\\.\\.\\.");
				
				return new MethodArgInfo(ar[1].trim(),ar[0].trim(),true);
			}
			else
			{
				String[] ar=tt.split(" ");
				
				return new MethodArgInfo(ar[1].trim(),ar[0].trim());
			}
		});
	}
	
	@Override
	protected void toMakeOneMethodVisit(MethodInfo method,String str)
	{
		super.toMakeOneMethodVisit(method,str);
		
		switch(str)
		{
			case "abstract":
			{
				method.isAbstract=true;
			}
				break;
			case "final":
			{
				method.isFinal=true;
			}
				break;
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
				sb.append(arg.type);
				sb.append("...");
				sb.append(arg.name);
			}
			else
			{
				sb.append(arg.type);
				sb.append(" ");
				sb.append(arg.name);
			}
			
			++i;
		}
	}
	
	/** 读内部类 */
	private void readExCls()
	{
		_exClsList.clear();
		
		Matcher m=_javaExClsReg.matcher(_content);
		
		while(m.find())
		{
			String str=m.group();
			
			int index=m.start();
			
			int start=_content.indexOf("{",index + str.length() + 1);
			
			int end=StringUtils.getAnotherIndex(_content,'{','}',start);
			
			JavaExClsInfo ex=new JavaExClsInfo();
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
		for(JavaExClsInfo ex : _exClsList)
		{
			writeLeftTab(sb);
			sb.append(ex.content);
			sb.append(Enter);
			
			writeLeftTab(sb);
			sb.append(Enter);
		}
	}
	
	@Override
	protected boolean toCheckMethodReadIndex(int index)
	{
		for(JavaExClsInfo ex : _exClsList)
		{
			//在中间
			if(ex.startIndex<=index && index<=ex.endIndex)
			{
				return false;
			}
		}
		
		return true;
	}
	
	private class JavaExClsInfo
	{
		public String content;
		
		public int startIndex;
		
		public int endIndex;
	}
	
	//special
}
