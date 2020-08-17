package com.home.shineTool.app;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineGlobal;
import com.home.shine.serverConfig.ServerConfig;
import com.home.shine.support.collection.SSet;
import com.home.shine.utils.FileUtils;
import com.home.shineTool.ShineToolSetup;
import com.home.shineTool.global.ShineToolGlobal;
import com.home.shineTool.utils.ToolFileUtils;

/** 清空数据库 */
public class DBTruncateApp
{
	public void execute()
	{
		//初始化ServerConfig
		ShineGlobal.serverConfigPath=ShineToolGlobal.serverServerConfigPath + "/server.xml";
		ShineGlobal.serverLocalSettingPath=ShineToolGlobal.serverServerConfigPath + "/serverLocal.xml";
		
		ServerConfig.init();
		
		String centerURL=ServerConfig.getCenterConfig().mysql;
		String centerDBName=ToolFileUtils.getDBNameByURL(centerURL);
		
		Ctrl.print("center:");
		DBExportApp.executeQuery(centerURL,ShineGlobal.serverSqlPath + "/truncate_"+centerDBName+".sql");
		
		SSet<String> gameMysqlSet=new SSet<>();
		
		ServerConfig.getGameConfigDic().forEachValue(v->
		{
			if(!v.isAssist)
			{
				String gameURL=v.mysql;
				String gameDBName=ToolFileUtils.getDBNameByURL(gameURL);
				
				if(gameMysqlSet.add(gameDBName))
				{
					Ctrl.print("game_"+v.id+":");
					DBExportApp.executeQuery(gameURL,ShineGlobal.serverSqlPath + "/truncate_"+gameDBName+".sql");
				}
			}
		});
		
		FileUtils.clearDir(ShineToolGlobal.serverBinPath+"/save/db");
		
		Ctrl.print("OK");
	}
	
	public static void main(String[] args)
	{
		ShineToolSetup.init();
		new DBTruncateApp().execute();
	}
}
