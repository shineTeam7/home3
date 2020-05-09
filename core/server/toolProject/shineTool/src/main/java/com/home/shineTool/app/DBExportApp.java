package com.home.shineTool.app;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineGlobal;
import com.home.shine.serverConfig.ServerConfig;
import com.home.shine.support.XML;
import com.home.shine.table.DBConnect;
import com.home.shine.utils.FileUtils;
import com.home.shine.utils.StringUtils;
import com.home.shineTool.ShineToolSetup;
import com.home.shineTool.global.ShineToolGlobal;
import com.home.shineTool.global.ShineToolSetting;
import com.home.shineTool.tool.export.CommonDataExportTool;
import com.home.shineTool.tool.export.NormalDBExportTool;

/** 数据库导出 */
public class DBExportApp
{
	public DBExportApp()
	{
		
	}
	
	public void executeAll(boolean isAll)
	{
		ShineToolSetting.isAll=isAll;
		
		//初始化ServerConfig
		ShineGlobal.serverConfigPath=ShineToolGlobal.serverServerConfigPath + "/server.xml";
		ShineGlobal.serverLocalSettingPath=ShineToolGlobal.serverServerConfigPath + "/serverLocal.xml";
		ServerConfig.init();
		//构造shine包
		CommonDataExportTool.makeShinePackage();
		
		NormalDBExportTool commonTool=new NormalDBExportTool();
		commonTool.setIsCommon(true);
		commonTool.initNormal("commonDBRecord");
		commonTool.execute();
		
		NormalDBExportTool gameTool=new NormalDBExportTool();
		gameTool.setIsCommon(false);
		gameTool.setCommonTool(commonTool);
		gameTool.initNormal("dbRecord");
		gameTool.execute();
		
		String md5=StringUtils.md5(commonTool.getMd5()+gameTool.getMd5());
		
		String sqlVersionPath=ShineToolGlobal.serverSavePath + "/sqlVersion.xml";
		
		//刷版本号
		XML xml=FileUtils.readFileForXML(sqlVersionPath);
		
		if(xml==null)
		{
			xml=new XML();
			xml.setName("sqlVersion");
			xml.setProperty("md5",md5);
			xml.setProperty("version","1");
			FileUtils.writeFileForXML(sqlVersionPath,xml);
		}
		else
		{
			String oldMd5=xml.getProperty("md5");
			
			if(!oldMd5.equals(md5))
			{
				int newVersion=Integer.parseInt(xml.getProperty("version"))+1;

				Ctrl.print("表内容有变动，版本号增加:"+newVersion);
				
				xml.setProperty("md5",md5);
				xml.setProperty("version",String.valueOf(newVersion));
				
				FileUtils.writeFileForXML(sqlVersionPath,xml);
			}
		}
		
		FileUtils.writeFileForXML(ShineToolGlobal.serverTempPath + "/sql/localVersion.xml",xml);
		
		Ctrl.print("OK");
	}
	
	public static void main(String[] args)
	{
		//ShineToolSetting.isAllRefresh=true;
		ShineToolSetup.init();
		new DBExportApp().executeAll(true);
	}
	
	/** 执行mysql指令 */
	public static void executeQuery(String url,String sqlPath)
	{
		String sql=FileUtils.readFileForUTF(sqlPath);
		
		int lastIndex=sql.lastIndexOf(';');
		
		if(lastIndex!=-1)
		{
			sql=sql.substring(0,lastIndex+1);
		}
		
		DBConnect connect=new DBConnect(url);
		
		String[] arr=sql.split(";");
		
		for(int i=0;i<arr.length;i++)
		{
			connect.getExecutor().executeQuerySync(arr[i]+';');
		}
		
		connect.close();
	}
}
