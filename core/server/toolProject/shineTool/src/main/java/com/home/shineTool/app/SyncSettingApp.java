package com.home.shineTool.app;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.XML;
import com.home.shine.support.collection.SList;
import com.home.shine.utils.FileUtils;
import com.home.shineTool.ShineToolSetup;
import com.home.shineTool.global.ShineToolGlobal;

import java.io.File;
import java.nio.file.CopyOption;
import java.nio.file.Files;

/** 同步shine设定到各个工程 */
public class SyncSettingApp
{
	private static SList<XML> _gameXML=new SList<>();
	
	private static SList<XML> _toolXML=new SList<>();
	private static XML _toolXML2;
	
	/** 工程列表 */
	private static SList<String> _projectList=new SList<>();
	
	public static void main(String[] args)
	{
		//需要自己手改的，game.xml,tools.xml 和 encrypt.cfg
		
		ShineToolSetup.init();
		
		//第一个为主工程
		_projectList.add("limitlessGit");
		_projectList.add("h1Git");
		_projectList.add("jm1Git");
		_projectList.add("zc1Git");
		_projectList.add("tryGit");
		
		//makeAnt();
		//makeGitIgnore();
		makeLink();
		
		Ctrl.print("OK");
	}
	
	/** 获取develop目录 */
	private static String getDevelopPath(String project)
	{
		return ShineToolGlobal.codePath + "/../../" + project + "/develop";
	}
	
	/** copyAnt部分 */
	private static void makeAnt()
	{
		XML xml=FileUtils.readFileForXML(ShineToolGlobal.serverPath + "/tools/ant/game.xml");
		
		for(XML ff : xml.getChildrenByNameAndProperty("target","name","game_jar").get(0).getChildrenByNameOne("jar").getChildrenByName("zipfileset"))
		{
			_gameXML.add(ff);
		}
		
		xml=FileUtils.readFileForXML(ShineToolGlobal.serverPath + "/tools/ant/tools.xml");
		
		for(XML ff : xml.getChildrenByNameAndProperty("target","name","preTools_jar").get(0).getChildrenByName("copy"))
		{
			_toolXML.add(ff);
		}
		
		_toolXML2=xml.getChildrenByNameAndProperty("target","name","preTools_jar").get(0).getChildrenByNameOne("jar").getChildrenByNameOne("manifest").getChildrenByNameAndProperty("attribute","name","Class-Path").get(0);
		
		makeAntGameOne(ShineToolGlobal.serverPath + "/tools/ant/client.xml","client_jar");
		
		for(String project : _projectList)
		{
			String developPath=getDevelopPath(project);
			
			String front=developPath+"/server/toolsMac/ant/";
			
			makeAntGameOne(front + "game.xml","game_jar");
			makeAntGameOne(front + "client.xml","client_jar");
			makeAntToolOne(front+"tools.xml","preTools_jar");
		}
	}
	
	private static void makeAntGameOne(String path,String targetName)
	{
		XML xml=FileUtils.readFileForXML(path);
		
		XML targetJar=xml.getChildrenByNameAndProperty("target","name",targetName).get(0).getChildrenByName("jar").get(0);
		
		for(XML zz : targetJar.getChildrenByName("zipfileset"))
		{
			targetJar.removeChild(zz);
		}
		
		for(XML zz : _gameXML)
		{
			targetJar.appendChild(zz);
		}
		
		FileUtils.writeFileForXML(path,xml);
	}
	
	private static void makeAntToolOne(String path,String targetName)
	{
		XML xml=FileUtils.readFileForXML(path);
		
		XML manifestXML=xml.getChildrenByNameAndProperty("target","name","preTools_jar").get(0).getChildrenByNameOne("jar").getChildrenByNameOne("manifest");
		
		manifestXML.removeChild(manifestXML.children().get(1));//删除第二个
		
		manifestXML.appendChild(_toolXML2);
		
		XML targetJar=xml.getChildrenByNameAndProperty("target","name",targetName).get(0);
		
		for(XML zz : targetJar.getChildrenByName("copy"))
		{
			targetJar.removeChild(zz);
		}
		
		for(XML zz : _toolXML)
		{
			targetJar.appendChild(zz);
		}
		
		FileUtils.writeFileForXML(path,xml);
	}
	
	/** copy gitIgnore部分 */
	private static void makeGitIgnore()
	{
		File file=new File(getDevelopPath(_projectList.get(0)) + "/.gitignore");
		
		for(int i=1;i<_projectList.size();i++)
		{
			FileUtils.copyFile(file,new File(getDevelopPath(_projectList.get(i))+"/.gitignore"));
		}
	}
	
	/** copy makeSvn部分 */
	private static void makeLink()
	{
		File file=new File(getDevelopPath(_projectList.get(0)) + "/link");
		
		File[] files=file.listFiles();
		
		for(int i=1;i<_projectList.size();i++)
		{
			File targetFile=new File(getDevelopPath(_projectList.get(i)) + "/link");
			
			FileUtils.clearDir(targetFile.getPath(),false);
			
			for(File f:files)
			{
				//只拷贝python
				if(f.getName().endsWith(".py"))
				{
					FileUtils.copyFile(f,new File(targetFile.getPath()+"/"+f.getName()));
				}
			}
		}
	}
	
}
