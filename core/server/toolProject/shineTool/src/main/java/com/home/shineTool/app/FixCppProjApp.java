package com.home.shineTool.app;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.XML;
import com.home.shine.support.collection.SList;
import com.home.shine.utils.FileUtils;
import com.home.shine.utils.StringUtils;
import com.home.shineTool.ShineToolSetup;
import com.home.shineTool.global.ShineToolGlobal;

import java.io.File;
import java.util.List;

/** 修复C++工程的vcxproj */
public class FixCppProjApp
{
	public void fix()
	{
		fixOne("shine");
		fixOne("commonGame");
		fixOne("game");
		
		Ctrl.print("OK");
	}
	
	private void fixOne(String projectName)
	{
		String pPath=ShineToolGlobal.serverProjectCPath + "/" + projectName;
		
		String projPath=pPath+"/"+projectName+".vcxproj";
		String projFilterPath=pPath+"/"+projectName+".vcxproj.filters";
		
		XML xml=FileUtils.readFileForXML(projPath);
		XML xmlF=FileUtils.readFileForXML(projFilterPath);
		
		XML includeNode=null;
		XML compileNode=null;
		
		XML includeNodeF=null;
		XML compileNodeF=null;
		
		for (XML xl : xml.getChildrenByName("ItemGroup"))
		{
			if (xl.getChildrenByNameOne("ClInclude")!=null)
			{
				includeNode=xl;
			}
			else if (xl.getChildrenByNameOne("ClCompile")!=null)
			{
				compileNode=xl;
			}
		}
		
		if(includeNode==null)
		{
			includeNode=new XML();
			includeNode.setName("ItemGroup");
			xml.appendChild(includeNode);
		}
		
		if(compileNode==null)
		{
			compileNode=new XML();
			compileNode.setName("ItemGroup");
			xml.appendChild(compileNode);
		}
		
		for (XML xl : xmlF.getChildrenByName("ItemGroup"))
		{
			if (xl.getChildrenByNameOne("ClInclude")!=null)
			{
				includeNodeF=xl;
			}
			else if (xl.getChildrenByNameOne("ClCompile")!=null)
			{
				compileNodeF=xl;
			}
		}
		
		if(includeNodeF==null)
		{
			includeNodeF=new XML();
			includeNodeF.setName("ItemGroup");
			xmlF.appendChild(includeNode);
		}
		
		if(compileNodeF==null)
		{
			compileNodeF=new XML();
			compileNodeF.setName("ItemGroup");
			xmlF.appendChild(compileNodeF);
		}
		
		File pFile=new File(pPath);
		String rootPath=pFile.getAbsolutePath();
		
		List<File> list=FileUtils.getDeepFileList(pPath + "/src","h");
		list.sort(File::compareTo);
		
		includeNode.removeAllChildren();
		includeNodeF.removeAllChildren();
		
		
		for(File f : list)
		{
			String absolutePath=f.getAbsolutePath();
			
			String fPath=absolutePath.substring(rootPath.length() + 1);
			
			
			XML x=new XML();
			x.setName("ClInclude");
			x.setProperty("Include",fPath);
			includeNode.appendChild(x);
			
			x=new XML();
			x.setName("ClInclude");
			x.setProperty("Include",fPath);
			
			String dirPath=fPath.substring(0,fPath.lastIndexOf(File.separatorChar));
			
			XML x2=new XML();
			x2.setName("Filter");
			x2.setValue(dirPath);
			x.appendChild(x2);
			
			includeNodeF.appendChild(x);
		}
		
		compileNode.removeAllChildren();
		compileNodeF.removeAllChildren();
		
		list=FileUtils.getDeepFileList(pPath + "/src","cpp","c");
		list.sort(File::compareTo);
		
		for(File f : list)
		{
			String absolutePath=f.getAbsolutePath();
			
			String fPath=absolutePath.substring(rootPath.length() + 1);
			
			XML x=new XML();
			x.setName("ClCompile");
			x.setProperty("Include",fPath);
			compileNode.appendChild(x);
			
			x=new XML();
			x.setName("ClCompile");
			x.setProperty("Include",fPath);
			
			String dirPath=fPath.substring(0,fPath.lastIndexOf(File.separatorChar));
			
			XML x2=new XML();
			x2.setName("Filter");
			x2.setValue(dirPath);
			x.appendChild(x2);
			
			compileNodeF.appendChild(x);
		}
		
		FileUtils.writeFileForXML(projPath,xml);
		FileUtils.writeFileForXML(projFilterPath,xmlF);
	}
	
	public static void main(String[] args)
	{
		ShineToolSetup.init();
		new FixCppProjApp().fix();
	}
}
