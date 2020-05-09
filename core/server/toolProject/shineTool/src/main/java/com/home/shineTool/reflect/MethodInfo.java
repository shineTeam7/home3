package com.home.shineTool.reflect;

import com.home.shine.support.collection.SList;
import com.home.shineTool.constlist.SGType;
import com.home.shineTool.reflect.code.CodeInfo;
import com.home.shineTool.reflect.code.CodeWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 方法信息 */
public class MethodInfo extends PropertyInfo
{
	/** 空方法正则 */
	private static Pattern _emptyReg=Pattern.compile("[^ ^\\r^\\n^\\t]+?");
	
	/** 返回值类型 */
	public String returnType="void";
	/** 参数列表 */
	public SList<MethodArgInfo> args=new SList<>();
	/** 方法内容(包括大括号)(以大括号开头和结尾) */
	public String content;
	/** 是否为复写 */
	public boolean isOverride=false;
	/** 是否为虚方法(java/C#用) */
	public boolean isAbstract=false;
	/** sg类型(AS3/C#用) */
	public int sgType=SGType.None;
	/** 是否可被继承(C#用) */
	public boolean isVirtual=false;
	
	/** 泛型方法字符串(java用) */
	public String genericTypeStr="";
	
	public MethodInfo()
	{
		
	}
	
	@Override
	public void copy(PropertyInfo target)
	{
		super.copy(target);
		
		MethodInfo tt=(MethodInfo)target;
		returnType=tt.returnType;
		
		tt.args.forEach(v->
		{
			args.add(v.clone());
		});
		
		content=tt.content;
		isOverride=tt.isOverride;
		isAbstract=tt.isAbstract;
		sgType=tt.sgType;
		isVirtual=tt.isVirtual;
		genericTypeStr=tt.genericTypeStr;
	}
	
	/** 写abstract文字 */
	public void writeAbstractStr(StringBuilder sb)
	{
		if(isAbstract)
		{
			sb.append("abstract ");
		}
	}
	
	/** 写override文字 */
	public void writeOverrideStr(StringBuilder sb)
	{
		if(isOverride)
		{
			sb.append("override ");
		}
	}
	
	/** 写virtual文字(写的时候只有C#会用到) */
	public void writeVirtualStr(StringBuilder sb)
	{
		if(isVirtual)
		{
			sb.append("virtual ");
		}
	}
	
	/** 获取方法key */
	public String getKey()
	{
		if(args.isEmpty())
		{
			return name;
		}
		
		StringBuilder sb=new StringBuilder();
		sb.append(name);
		
		for(MethodArgInfo v : args)
		{
			sb.append(",");
			sb.append(v.type);
		}
		
		return sb.toString();
	}
	
	//	/** 克隆 */
	//	public MethodInfo clone()
	//	{
	//		MethodInfo re=new MethodInfo();
	//		re.name=this.name;
	//		re.describe=this.describe;
	//		re.visitType=this.visitType;
	//		re.isStatic=this.isStatic;
	//		re.returnType=this.returnType;
	//		re.args=this.args;
	//		re.content=this.content;
	//		re.isOverride=this.isOverride;
	//		re.isAbstract=this.isAbstract;
	//		re.sgType=this.sgType;
	//
	//		return re;
	//	}
	
	/** 构造空方法 */
	public void makeEmptyMethod(int codeType)
	{
		CodeWriter writer=CodeWriter.createByType(codeType);
		writer.writeEnd();
		content=writer.toString();
	}
	
	/** 构造空方法带super */
	public void makeEmptyMethodWithSuper(int codeType)
	{
		CodeWriter writer=CodeWriter.createByType(codeType);
		CodeInfo code=CodeInfo.getCode(codeType);
		
		StringBuilder sb=new StringBuilder();
		sb.append('(');
		
		for(int i=0;i<this.args.size();++i)
		{
			if(i>0)
			{
				sb.append(',');
			}
			
			sb.append(this.args.get(i).name);
		}
		sb.append(')');
		
		writer.writeCustom(code.Super + "." + name + sb.toString() + ";");
		writer.writeEmptyLine();
		writer.writeEnd();
		
		content=writer.toString();
	}
	
	/** 是否为空 */
	public boolean isEmpty(int codeType)
	{
		String str=content.substring(1,content.length()-1);
		
		String pp=CodeInfo.getCode(codeType).Super;
		
		int index=str.indexOf(pp);
		
		if(index!=-1)
		{
			int next=str.indexOf(";",index);
			
			if(next==-1)
				return false;
			
			str=str.substring(next+1);
		}
		
		Matcher matcher=_emptyReg.matcher(str);
		
		//找到了
		if(matcher.find())
		{
			return false;
		}
		
		return true;
	}
	
	/** 设置是否override */
	public void setOverride(boolean value)
	{
		isVirtual=!value;
		isOverride=value;
	}
	
	public MethodInfo clone()
	{
		MethodInfo re=new MethodInfo();
		re.copy(this);
		return re;
	}
}
