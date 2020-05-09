package com.home.shineTool.app;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.utils.FileUtils;
import com.home.shineTool.ShineToolSetup;
import com.home.shineTool.constlist.CodeType;
import com.home.shineTool.global.ShineToolGlobal;
import com.home.shineTool.reflect.MethodInfo;
import com.home.shineTool.reflect.cls.ClassInfo;

/** 清空适配器app */
public class ClearAdaptersApp
{
	public static void main(String[] args)
	{
		new ClearAdaptersApp().execute(true);
	}
	
	public void execute(boolean isAll)
	{
		ShineToolSetup.init();
		
		//doOneProject(0);
		
		if(isAll)
		{
			doOneProject(1);
		}
		
		doOneProject(2);
		
		Ctrl.print("OK");
	}
	
	private void doOneProject(int type)
	{
		String project="";
		String mark2="";
		
		switch(type)
		{
			case 0:
			{
				project="shine";
				mark2="";
			}
			break;
			case 1:
			{
				project="commonGame";
				mark2="C";
			}
			break;
			case 2:
			{
				project="game";
				mark2="G";
			}
			break;
		}
		
		
		ClassInfo mCls=ClassInfo.getClassInfoFromPath(ShineToolGlobal.clientMainSrcPath+"/"+project+"/control/"+mark2 +"ILRuntimeControl."+CodeType.getExName(CodeType.CS));
		
		MethodInfo mMethod=mCls.getMethodByName("initGenerateAdapters");
		
		if(mMethod!=null)
		{
			mMethod.makeEmptyMethodWithSuper(mCls.getCodeType());
		}
		
		mMethod=mCls.getMethodByName("initOtherAdapters");
		
		if(mMethod!=null)
		{
			mMethod.makeEmptyMethodWithSuper(mCls.getCodeType());
		}
		
		mCls.write();
		
		FileUtils.clearDir(ShineToolGlobal.clientMainSrcPath + "/" + project + "/adapters");
	}
}
