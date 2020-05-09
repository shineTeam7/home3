package com.home.commonBase.utils;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineGlobal;
import com.home.shine.serverConfig.ServerConfig;
import com.home.shine.table.DBConnect;
import com.home.shine.utils.FileUtils;

public class DBUtils
{
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
		
		Ctrl.print("center:");
		executeQuery(centerURL,true,ShineGlobal.serverSqlPath + "/center.sql");
		
		ServerConfig.getGameConfigDic().forEachValue(v->
		{
			String gameURL=v.mysql;
			
			String qid="game_"+v.id;
			
			Ctrl.print(qid+":");
			executeQuery(gameURL,true,ShineGlobal.serverSqlPath + "/"+qid+".sql");
		});
		
		Ctrl.print("OK");
	}
	
	/** 生成数据库 */
	public static void dbTruncate()
	{
		ServerConfig.init();
		
		String centerURL=ServerConfig.getCenterConfig().mysql;
		
		Ctrl.print("center:");
		executeQuery(centerURL,false,ShineGlobal.serverSqlPath + "/truncateCenter.sql");
		
		ServerConfig.getGameConfigDic().forEachValue(v->
		{
			String gameURL=v.mysql;
			
			Ctrl.print("game_"+v.id+":");
			executeQuery(gameURL,false,ShineGlobal.serverSqlPath + "/truncateGame.sql");
		});
		
		FileUtils.clearDir(ShineGlobal.dbFilePath);
		
		Ctrl.print("OK");
	}
}
