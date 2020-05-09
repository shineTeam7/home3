package com.home.shineTool.reflect;

import com.home.shine.support.collection.SList;
import com.home.shine.support.collection.SSet;
import com.home.shineTool.constlist.VisitType;

/** 属性信息 */
public class PropertyInfo
{
	/** 名字 */
	public String name="";
	/** 注释(不带前后符号和空格) */
	public String describe="";
	/** 访问类型(见VisitType) */
	public int visitType=VisitType.None;
	/** 是否静态 */
	public boolean isStatic=false;
	
	/** 注解Key(java用) */
	public SSet<String> annotationKeySet=new SSet<>();
	/** 注解Key(java用) */
	public SList<AnnotationInfo> annotationList=new SList<>();
	
	//temp
	/** 开始位置 */
	public int startIndex;
	/** 结束位置 */
	public int endIndex;
	
	public void copy(PropertyInfo target)
	{
		name=target.name;
		describe=target.describe;
		visitType=target.visitType;
		isStatic=target.isStatic;
		annotationKeySet=target.annotationKeySet.clone();
		annotationList=target.annotationList.clone();
		startIndex=target.startIndex;
		endIndex=target.endIndex;
	}
	
	/** 是否有某注解 */
	public boolean hasAnnotation(String str)
	{
		return annotationKeySet.contains(str);
	}
	
	public void addAnnotation(AnnotationInfo info)
	{
		annotationKeySet.add(info.name);
		annotationList.add(info);
	}
	
	/** 写入访问文字 */
	public void writeVisitStr(StringBuilder sb)
	{
		String v=VisitType.getString(visitType);
		
		if(!v.isEmpty())
		{
			sb.append(v);
			sb.append(" ");
		}
	}
	
	/** 写入static文字 */
	public void writeStaticStr(StringBuilder sb)
	{
		if(isStatic)
		{
			sb.append("static ");
		}
	}
	
	public boolean needRelease()
	{
		return hasAnnotation("FieldNeedRelease");
	}
}
