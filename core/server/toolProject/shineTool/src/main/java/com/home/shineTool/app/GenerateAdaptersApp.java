package com.home.shineTool.app;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.XML;
import com.home.shine.support.collection.SList;
import com.home.shine.utils.FileUtils;
import com.home.shineTool.ShineToolSetup;
import com.home.shineTool.constlist.VisitType;
import com.home.shineTool.global.ShineToolGlobal;
import com.home.shineTool.reflect.MethodArgInfo;
import com.home.shineTool.reflect.MethodInfo;
import com.home.shineTool.reflect.cls.ClassInfo;
import com.home.shineTool.reflect.code.CodeWriter;
import com.home.shineTool.tool.hotfix.ILClassInfo;
import com.home.shineTool.tool.hotfix.ILRuntimeAdapterTool;

/** 生成类适配器(已废弃) */
public class GenerateAdaptersApp
{
	/** 当前的xml */
	private static XML _currentXML;
	private static String _projectStr;
	
	public static void main(String[] args)
	{
		ShineToolSetup.init();
		
		ILRuntimeAdapterTool.init();
		
		//TODO:g层继承c层类的处理
		
		//TODO:检查继承关系
		
		doOneProject(0);
		doOneProject(1);
		doOneProject(2);
		
		Ctrl.print("OK");
	}
	
	private static void doOneProject(int type)
	{
		String project="";
		String mark1="";
		String mark2="";
		
		switch(type)
		{
			case 0:
			{
				project="shine";
				mark1="s";
				mark2="";
			}
				break;
			case 1:
			{
				project="commonGame";
				mark1="c";
				mark2="C";
			}
				break;
			case 2:
			{
				project="game";
				mark1="g";
				mark2="G";
			}
				break;
		}
		
		_projectStr=project;
		
		XML xml=FileUtils.readFileForXML(ShineToolGlobal.clientMainSrcPath + "/"+project+"/"+mark1+"GenerateAdapters.xml");
		
		if(xml==null)
		{
			Ctrl.throwError("没找到xml配置文件");
			return;
		}
		
		_currentXML=xml;
		
		ClassInfo mCls=ClassInfo.getClassInfoFromPath(ShineToolGlobal.clientMainSrcPath+"/"+project+"/control/"+mark2 +"ILRuntimeControl.cs");
		
		MethodInfo mMethod=mCls.getMethodByName("initGenerateAdapters");
		
		if(mMethod==null)
		{
			mMethod=new MethodInfo();
			mMethod.name="initGenerateAdapters";
			mMethod.describe="初始化数据适配器";
			mMethod.returnType=mCls.getCode().Void;
			mMethod.visitType=VisitType.Protected;
			mMethod.isOverride=true;
			mMethod.args.add(new MethodArgInfo("appdomain","AppDomain"));
			
			mCls.addMethod(mMethod);
		}
		
		CodeWriter writer=mCls.createWriter();
		
		if(type!=0)
		{
			writer.writeObjFuc("base","initGenerateAdapters","appdomain");
			writer.writeEmptyLine();
		}
		
		for(XML xl:xml.getChildrenByName("cls"))
		{
			ILClassInfo clsInfo=new ILClassInfo();
			
			toDoOne(clsInfo,xl,true);
			
			ILRuntimeAdapterTool.doOneClass(clsInfo,ShineToolGlobal.clientMainSrcPath+"/"+project+"/adapters",type==0,writer);
		}
		
		writer.writeEnd();
		mMethod.content=writer.toString();
		
		mCls.write();
	}
	
	private static void toDoOne(ILClassInfo cls,XML xml,boolean isSelf)
	{
		String base=xml.getProperty("base");
		
		if(!base.isEmpty())
		{
			XML baseXML=_currentXML.getChildrenByNameAndPropertyOne("cls","path",base);
			
			if(baseXML!=null)
			{
				toDoOne(cls,baseXML,false);
			}
			else
			{
				toDoOneStr(cls,base,false);
			}
		}
		
		toDoOneStr(cls,xml.getProperty("path"),isSelf);
	}
	
	private static void toDoOneStr(ILClassInfo cls,String url,boolean isSelf)
	{
		String path=url.replaceAll("\\.","/");
		
		ClassInfo sourceCls=ClassInfo.getClassInfoFromPath(ShineToolGlobal.clientMainSrcPath+"/"+_projectStr+"/"+path+".cs");
		
		if(sourceCls==null)
		{
			Ctrl.throwError("未找到类:",url);
			Ctrl.exit();
			return;
		}
		
		if(isSelf)
		{
			cls.clsName=sourceCls.clsName;
		}
		
		ILRuntimeAdapterTool.makeILCls(cls,sourceCls);
	}
	
	
}
