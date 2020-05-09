package com.home.shineTool.app;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.utils.FileUtils;
import com.home.shineTool.ShineToolSetup;
import com.home.shineTool.global.ShineToolGlobal;

import java.io.File;
import java.util.List;

public class TempExportApp
{
	public static void main(String[] args)
	{
		ShineToolSetup.init();
		
		//doOneRoot(ShineToolGlobal.clientMainSrcPath + "/hotfix");
		//doOneRoot(ShineToolGlobal.clientMainSrcPath + "/commonGame");
		//doOneRoot(ShineToolGlobal.clientMainSrcPath + "/game");
		
		
		//getClassConstructor(ShineToolGlobal.clientMainSrcPath + "/shine");
		//getClassConstructor(ShineToolGlobal.clientMainSrcPath + "/commonGame");
		//getClassConstructor(ShineToolGlobal.clientMainSrcPath + "/game");
		
		Ctrl.print("OK");
	}
	
	private static void getClassConstructor(String path)
	{
		List<File> list=FileUtils.getDeepFileList(path,"ts");
		for(File f:list)
		{
			String content=FileUtils.readFileForUTF(f.getPath());
			
			String[] tt=content.split("\\r\\n");
			
			String[] strName1=f.getPath().split("\\\\");
			
			String className=strName1[strName1.length-1].replace(".ts", "");
			
			for(int i=0;i<tt.length;i++)
			{
				if(tt[i].indexOf(className+"()")!=-1)
				{
					System.out.println(f.getPath());
					i=tt.length+1;
				}
			}
		}
	}
	private static void doOneRoot(String path)
	{
		List<File> list=FileUtils.getDeepFileList(path,"ts");
		
		StringBuilder sb=new StringBuilder();
		
		for(File f:list)
		{
			String content=FileUtils.readFileForUTF(f.getPath());
			
			String[] tt=content.split("\\r\\n");
			
			sb.setLength(0);
			
			sb.append("namespace Shine");
			sb.append("\r\n");
			sb.append("{");
			sb.append("\r\n");
			
			for(int i=0;i<tt.length;i++)
			{
				sb.append("\t");
				
				if(tt[i].startsWith("class "))
					sb.append("export ");
				
				sb.append(tt[i]);
				sb.append("\r\n");
			}
			
			sb.append("}");
			
			FileUtils.writeFileForUTF(f.getPath(),sb.toString());
		}
	}
	
	private static void makeUI()
	{
		List<File> list=FileUtils.getDeepFileList("/Users/sunming/E/home/limitlessGit/develop/client/game/Assets/src/game/view/ui","cs");
		
		for(File v:list)
		{
			String fName=FileUtils.getFileFrontName(v.getName());
			
			String ss=String.valueOf(fName.charAt(1));
			
			if(fName.startsWith("G") && ss.equals(ss.toUpperCase()))
			{
				String kName="H"+fName.substring(1);
				
				String content=FileUtils.readFileForUTF(v.getPath());
				
				content=content.replaceAll(fName,kName);
				
				String path=v.getPath();
				path=path.replace(fName,kName);
				
				FileUtils.writeFileForUTF(path,content);
				
				v.delete();
			}
		}
	}
}
