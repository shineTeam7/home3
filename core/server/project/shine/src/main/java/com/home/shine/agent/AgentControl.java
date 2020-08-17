package com.home.shine.agent;

import java.io.File;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineGlobal;
import com.home.shine.utils.FileUtils;

/** 热更控制 */
public class AgentControl
{
	private static volatile Instrumentation _inst;
	
	private static boolean _isEditor=false;
	
	/** 初始化 */
	public static void init(Instrumentation inst)
	{
		_inst=inst;
	}
	
	/** 是否初始化了 */
	public static boolean inited()
	{
		return _inst!=null;
	}
	
	/** IDEA调试模式初始化 */
	public static void initForIDEA()
	{
		if(inited())
			return;
		
		try
		{
			Class<?> aClass=Class.forName("com.intellij.rt.debugger.agent.CaptureAgent");
			
			Field field=aClass.getDeclaredField("ourInstrumentation");
			
			field.setAccessible(true);
			
			_inst=(Instrumentation)field.get(aClass);
			_isEditor=true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/** 获取agent */
	public static Instrumentation getInstrumentation()
	{
		return _inst;
	}
	
	/** 刷全部类 */
	public static boolean agentClass()
	{
		if(_isEditor)
			return false;
		
		if(_inst==null)
		{
			Ctrl.warnLog("热更code时,agent不存在");
			return false;
		}
		
		Ctrl.log("开始热更类");
		
		List<File> list=FileUtils.getDeepFileList(ShineGlobal.classPath,"class");
		
		List<ClassDefinition> defines=new LinkedList<ClassDefinition>();
		
		String rootPath=new File(ShineGlobal.classPath).getPath();
		
		String fPath;
		String mName;
		byte[] bb;
		Class<?> cls=null;
		ClassDefinition clsd;
		
		for(File f : list)
		{
			//名字中有$的是临时文件
			if(f.getName().indexOf('$')==-1)
			{
				fPath=f.getPath();
				
				mName=fPath.substring(rootPath.length() + 1,fPath.lastIndexOf('.'));
				
				mName=mName.replace(File.separatorChar,'.');
				
				Ctrl.log("agent一个类",mName);
				
				cls=null;
				
				//没有就跳过
				try
				{
					cls=Class.forName(mName);
				}
				catch(ClassNotFoundException e)
				{
					Ctrl.warnLog("找不到类:",mName);
				}
				
				if(cls!=null)
				{
					bb=FileUtils.loadFileForBytes(f.getPath());
					
					clsd=new ClassDefinition(cls,bb);
					
					defines.add(clsd);
				}
			}
		}
		
		ClassDefinition[] arr=new ClassDefinition[defines.size()];
		
		defines.toArray(arr);
		
		try
		{
			_inst.redefineClasses(arr);
		}
		catch(Exception e)
		{
			Ctrl.warnLog("热交换时失败");
			Ctrl.errorLog(e);
			
			return false;
		}
		
		Ctrl.log("热更类完毕");
		
		return true;
	}
}
