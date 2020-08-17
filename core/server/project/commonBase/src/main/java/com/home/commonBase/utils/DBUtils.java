package com.home.commonBase.utils;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineGlobal;
import com.home.shine.serverConfig.ServerConfig;
import com.home.shine.support.collection.SSet;
import com.home.shine.table.DBConnect;
import com.home.shine.utils.FileUtils;

public class DBUtils
{
	public static String getDBNameByURL(String url)
	{
		String last=url.substring(url.lastIndexOf("/") + 1,url.length());
		
		String aa=last.substring(0,last.indexOf(","));
		
		int wIndex;
		
		if((wIndex=aa.indexOf("?"))!=-1)
		{
			aa=aa.substring(0,wIndex);
		}
		
		return aa.toLowerCase();//全小写
	}
	
	/** 执行mysql指令 */
	public static void executeQuery(String url,boolean needNuetual,String sqlPath)
	{
		String sql=FileUtils.readFileForUTF(sqlPath);
		
		int lastIndex=sql.lastIndexOf(';');
		
		if(lastIndex!=-1)
		{
			sql=sql.substring(0,lastIndex+1);
		}
		
		if(needNuetual)
		{
			url=url.substring(0,url.lastIndexOf("/"))+"/mysql"+url.substring(url.indexOf(","),url.length());
		}
		
		DBConnect connect=new DBConnect(url);
		
		String[] arr=sql.split(";");
		
		for(int i=0;i<arr.length;i++)
		{
			connect.getExecutor().executeQuerySync(arr[i]+';');
		}
	}
	
	/** 生成数据库 */
	public static void dbExport()
	{
		ServerConfig.init();
		
		String centerURL=ServerConfig.getCenterConfig().mysql;
		String centerDBName=getDBNameByURL(centerURL);
		
		Ctrl.print("center:");
		executeQuery(centerURL,false,ShineGlobal.serverSqlPath + "/"+centerDBName+".sql");
		
		SSet<String> gameMysqlSet=new SSet<>();
		
		ServerConfig.getGameConfigDic().forEachValue(v->
		{
			if(!v.isAssist)
			{
				String gameURL=v.mysql;
				String gameDBName=getDBNameByURL(gameURL);
				
				if(gameMysqlSet.add(gameDBName))
				{
					Ctrl.print("game_"+v.id+":");
					executeQuery(gameURL,false,ShineGlobal.serverSqlPath + "/"+gameDBName+".sql");
				}
			}
		});
		
		Ctrl.print("OK");
	}
	
	/** 生成数据库 */
	public static void dbTruncate()
	{
		ServerConfig.init();
		
		String centerURL=ServerConfig.getCenterConfig().mysql;
		String centerDBName=getDBNameByURL(centerURL);
		
		Ctrl.print("center:");
		executeQuery(centerURL,false,ShineGlobal.serverSqlPath + "/truncate_"+centerDBName+".sql");
		
		SSet<String> gameMysqlSet=new SSet<>();
		
		ServerConfig.getGameConfigDic().forEachValue(v->
		{
			if(!v.isAssist)
			{
				String gameURL=v.mysql;
				String gameDBName=getDBNameByURL(gameURL);
				
				if(gameMysqlSet.add(gameDBName))
				{
					Ctrl.print("game_"+v.id+":");
					executeQuery(gameURL,false,ShineGlobal.serverSqlPath + "/truncate_"+gameDBName+".sql");
				}
			}
		});
		
		FileUtils.clearDir(ShineGlobal.dbFilePath);
		
		Ctrl.print("OK");
	}
}
