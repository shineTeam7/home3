package com.home.shineTool.tool.hotfix;

import com.home.shine.support.collection.SList;
import com.home.shine.support.collection.SSet;
import com.home.shineTool.reflect.MethodArgInfo;

/** IL类 */
public class ILClassInfo
{
	/** 类名 */
	public String clsName="";
	
	/** 方法组 */
	public SList<ILMethodInfo> methods=new SList<>();
	
	/** 已存在方法key组 */
	public SSet<String> methodKeys=new SSet<>();
	
	public ILClassInfo()
	{
	
	}
	
	public ILClassInfo(String name)
	{
		this.clsName=name;
	}
	
	//public void addMethod(String name,String... args)
	//{
	//	addMethod(name,"",VisitType.Public,true,args);
	//}
	
	public void addMethod(String name,int visitType,String... args)
	{
		addMethod(name,"",visitType,true,args);
	}
	
	public void addMethod(String name,String returnType,int visitType,boolean needBaseCall,String... args)
	{
		if(returnType==null)
			returnType="";
		
		ILMethodInfo m=new ILMethodInfo();
		m.name=name;
		m.returnType=returnType;
		m.visitType=visitType;
		m.needBaseCall=needBaseCall;
		
		if(args.length>0)
		{
			MethodArgInfo argInfo;
			
			for(int i=0;i<args.length;i+=2)
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
		String key=method.getKey();
		
		//不重复添加
		if(methodKeys.contains(key))
			return;
		
		methods.add(method);
		methodKeys.add(key);
	}
}
