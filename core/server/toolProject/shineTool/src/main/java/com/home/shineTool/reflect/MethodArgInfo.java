package com.home.shineTool.reflect;

/** 方法参数信息 */
public class MethodArgInfo
{
	/** 参数名 */
	public String name;
	/** 参数类型 */
	public String type;
	/** 默认值 */
	public String defaultValue="";
	/** 是否变长参数 */
	public boolean autoLength=false;
	
	/** 是否为ref类型(C#用) */
	public boolean isRef;
	/** 是否为in类型(C#用) */
	public boolean isIn;
	/** 是否为out类型(C#用) */
	public boolean isOut;
	
	public MethodArgInfo()
	{
	
	}
	
	public MethodArgInfo clone()
	{
		MethodArgInfo re=new MethodArgInfo();
		re.name=name;
		re.type=type;
		re.defaultValue=defaultValue;
		re.autoLength=autoLength;
		re.isRef=isRef;
		re.isIn=isIn;
		re.isOut=isOut;
		return re;
	}
	
	public MethodArgInfo(String name,String type)
	{
		this(name,type,false);
	}
	
	public MethodArgInfo(String name,String type,boolean autoLength)
	{
		this.name=name;
		this.type=type;
		this.autoLength=autoLength;
	}
	
	public MethodArgInfo(String name,String type,String defaultValue)
	{
		this.name=name;
		this.type=type;
		this.defaultValue=defaultValue;
	}
}
