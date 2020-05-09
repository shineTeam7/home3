package com.home.shineTool.tool.hotfix;

import com.home.shine.support.collection.IntSet;
import com.home.shine.support.pool.StringBuilderPool;
import com.home.shine.utils.FileUtils;
import com.home.shineTool.constlist.VisitType;
import com.home.shineTool.global.ShineToolGlobal;
import com.home.shineTool.reflect.MethodArgInfo;
import com.home.shineTool.reflect.MethodInfo;
import com.home.shineTool.reflect.cls.ClassInfo;
import com.home.shineTool.reflect.code.CodeWriter;

/** ILRunTime 适配器 生成工具 */
public class ILRuntimeAdapterTool
{
	/** 回车 */
	private static final String Enter="\r\n";
	/** 换行 */
	private static final String Tab="\t";
	
	/** 模板 */
	private static String _template;
	
	/** 初始化 */
	public static void init()
	{
		if(_template==null)
		{
			_template=FileUtils.readFileForUTF(ShineToolGlobal.ILRuntimeAdapterTemplatePath);
		}
	}
	
	/** 执行一个class */
	public static void doOneClass(ILClassInfo clsInfo,String path,boolean isShine,CodeWriter writer)
	{
		doOneClsOnly(clsInfo,path,isShine);
		doOneWriter(clsInfo.clsName,writer);
	}
	
	public static void doOneClsOnly(ILClassInfo clsInfo,String path,boolean isShine)
	{
		String str=_template;
		
		if(isShine)
		{
			str=str.replace("$namespace","namespace ShineEngine" + Enter + "{");
			str=str.replace("$_namespace","}");
		}
		else
		{
			str=str.replace("$namespace","using ShineEngine;"+Enter);
			str=str.replace("$_namespace","");
		}
		
		str=str.replace("$clsName",clsInfo.clsName);
		
		StringBuilder sb1=StringBuilderPool.create();
		StringBuilder sb2=StringBuilderPool.create();
		IntSet marks=new IntSet();
		
		for(int i=0,len=clsInfo.methods.size();i<len;++i)
		{
			doOneMethod(clsInfo.methods.get(i),i,sb1,sb2,marks);
		}
		
		str=str.replace("$1",StringBuilderPool.releaseStr(sb1));
		str=str.replace("$2",StringBuilderPool.releaseStr(sb2));
		
		FileUtils.writeFileForUTF(path+"/"+clsInfo.clsName+"Adapter.cs",str);
	}
	
	public static void doOneWriter(String clsName,CodeWriter writer)
	{
		writer.writeCustom("appdomain.RegisterCrossBindingAdaptor(new "+clsName+"Adapter());");
	}
	
	private static void endLine(StringBuilder sb)
	{
		sb.append(Enter);
		sb.append(Tab);
		sb.append(Tab);
		sb.append(Tab);
	}
	
	private static void endLine(StringBuilder sb,int off)
	{
		sb.append(Enter);
		
		int len=3 + off;
		
		for(int i=0;i<len;i++)
		{
			sb.append(Tab);
		}
	}
	
	/** 执行一个方法 */
	private static void doOneMethod(ILMethodInfo method,int index,StringBuilder sb1,StringBuilder sb2,IntSet marks)
	{
		int aSize;
		
		if((aSize=method.args.size())>0)
		{
			if(!marks.contains(aSize))
			{
				marks.add(aSize);
				
				sb1.append("private object[] _p"+aSize+"=new object["+aSize+"];");
				endLine(sb1);
				endLine(sb1);
			}
		}
		
		sb2.append("IMethod _m"+index+";");
		endLine(sb2);
		sb2.append("bool _g"+index+";");
		endLine(sb2);
		
		if(method.needBaseCall)
		{
			sb2.append("bool _b"+index+";");
			endLine(sb2);
		}
		
		switch(method.visitType)
		{
			case VisitType.Private:
			{
				//private不写
			}
				break;
			case VisitType.Public:
			{
				sb2.append("public ");
				sb2.append("override ");
			}
				break;
			case VisitType.Protected:
			{
				sb2.append("protected ");
				sb2.append("override ");
			}
				break;
		}
		
		boolean needReturn=!method.returnType.isEmpty();
		
		if(!needReturn)
		{
			sb2.append("void");
		}
		else
		{
			sb2.append(method.returnType);
		}
		
		sb2.append(" " + method.name + "(");
		
		MethodArgInfo arg;
		
		for(int i=0;i<method.args.size();i++)
		{
			arg=method.args.get(i);
			
			if(i>0)
				sb2.append(",");
			
			if(arg.isRef)
				sb2.append("ref ");
			
			if(arg.isIn)
				sb2.append("in ");
			
			if(arg.isOut)
				sb2.append("out ");
			
			sb2.append(arg.type);
			sb2.append(" ");
			sb2.append(arg.name);
		}
		
		sb2.append(")");
		endLine(sb2);
		sb2.append("{");
		endLine(sb2,1);
		
		sb2.append("if(!_g" + index + ")");
		endLine(sb2,1);
		sb2.append("{");
		endLine(sb2,2);
		sb2.append("_m" + index + "=instance.Type.GetMethod(\"" + method.name + "\"," + aSize + ");");
		endLine(sb2,2);
		sb2.append("_g" + index + "=true;");
		endLine(sb2,1);
		sb2.append("}");
		endLine(sb2,1);
		endLine(sb2,1);
		
		sb2.append("if(_m" + index + "!=null");
		
		if(method.needBaseCall)
		{
			sb2.append(" && !_b" + index);
		}
		
		sb2.append(")");
		endLine(sb2,1);
		sb2.append("{");
		endLine(sb2,2);
		
		if(method.needBaseCall)
		{
			sb2.append("_b" + index + "=true;");
			endLine(sb2,2);
		}
		
		if(aSize>0)
		{
			for(int i=0;i<aSize;i++)
			{
				arg=method.args.get(i);
				
				sb2.append("_p"+aSize+"["+i+"]="+arg.name+";");
				endLine(sb2,2);
			}
		}
		
		if(needReturn)
		{
			sb2.append(method.returnType + " re=(" + method.returnType + ")");
		}
		
		sb2.append("appdomain.Invoke(_m" + index + ",instance,");
		
		if(aSize>0)
		{
			sb2.append("_p" + aSize);
		}
		else
		{
			sb2.append("null");
		}
		
		sb2.append(");");
		endLine(sb2,2);
		
		if(aSize>0)
		{
			for(int i=0;i<aSize;i++)
			{
				sb2.append("_p"+aSize+"["+i+"]=null;");
				endLine(sb2,2);
			}
		}
		
		if(method.needBaseCall)
		{
			sb2.append("_b" + index + "=false;");
			endLine(sb2,2);
		}
		
		
		if(needReturn)
		{
			sb2.append("return re;");
			endLine(sb2,2);
		}
		
		endLine(sb2,1);
		sb2.append("}");
		
		if(method.needBaseCall || needReturn)
		{
			endLine(sb2,1);
			sb2.append("else");
			endLine(sb2,1);
			sb2.append("{");
			endLine(sb2,2);
			
			if(needReturn)
			{
				sb2.append("return ");
			}
			
			sb2.append("base." + method.name + "(");
			
			for(int i=0;i<aSize;i++)
			{
				arg=method.args.get(i);
				
				if(i>0)
					sb2.append(",");
				
				if(arg.isRef)
					sb2.append("ref ");
				
				if(arg.isIn)
					sb2.append("in ");
				
				if(arg.isOut)
					sb2.append("out ");
				
				sb2.append(arg.name);
			}
			
			sb2.append(");");
			endLine(sb2,1);
			sb2.append("}");
		}
		
		endLine(sb2);
		sb2.append("}");
		endLine(sb2);
		endLine(sb2);
	}
	
	/** 构造IL cls信息 */
	public static void makeILCls(ILClassInfo cls,ClassInfo sourceCls)
	{
		//按顺序
		for(String methodKey:sourceCls.getMethodKeyList())
		{
			MethodInfo v=sourceCls.getMethod(methodKey);
			
			//有继承的
			if(v.isVirtual || v.isOverride)
			{
				ILMethodInfo methodInfo=new ILMethodInfo();
				methodInfo.name=v.name;
				methodInfo.returnType=!v.returnType.equals("void") ? v.returnType : "";
				methodInfo.visitType=v.visitType;
				methodInfo.args=v.args;
				cls.toAddMethod(methodInfo);
			}
		}
	}
}
