package com.home.commonBase.tool;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.SMap;
import com.home.shine.support.collection.StringIntMap;

import java.lang.reflect.Method;

/** 类方法继承标记工具(不支持同名) */
public class ClassMethodMarkTool
{
	private Class<?> _superCls;
	
	private int _len;
	private StringIntMap _methodNameDic=new StringIntMap();
	
	private static SMap<Class<?>,boolean[]> _markDic=new SMap<>();
	
	public ClassMethodMarkTool(Class<?> cls,int len)
	{
		_superCls=cls;
		_len=len;
	}
	
	public void addMethod(int type,String methodName)
	{
		_methodNameDic.put(methodName,type);
	}
	
	public boolean[] getClassMark(Class<?> cls)
	{
		boolean[] re=_markDic.get(cls);
		
		if(re!=null)
			return re;
		
		return toGetSync(cls);
	}
	
	private synchronized boolean[] toGetSync(Class<?> cls)
	{
		boolean[] re=_markDic.get(cls);
		
		if(re!=null)
			return re;
		
		re=new boolean[_len];
		
		int rd=0;
		
		while(cls!=_superCls)
		{
			Method[] methods=cls.getDeclaredMethods();
			
			for(Method method : methods)
			{
				int index=_methodNameDic.getOrDefault(method.getName(),-1);
				
				if(index!=-1)
				{
					re[index]=true;
				}
			}
			
			cls=cls.getSuperclass();
			
			if((++rd)>10)
			{
				Ctrl.errorLog("不允许出现10个以上的继承关系");
				break;
			}
		}
		
		_markDic.put(cls,re);
		return re;
	}
}
