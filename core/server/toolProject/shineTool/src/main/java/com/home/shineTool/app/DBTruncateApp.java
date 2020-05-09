package com.home.shineTool.app;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineGlobal;
import com.home.shine.serverConfig.ServerConfig;
import com.home.shine.support.collection.SSet;
import com.home.shine.utils.FileUtils;
import com.home.shineTool.ShineToolSetup;
import com.home.shineTool.global.ShineToolGlobal;

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
		
		Ctrl.print("center:");
		DBExportApp.executeQuery(centerURL,ShineToolGlobal.serverSqlPath + "/truncateCenter.sql");
		
		SSet<String> gameMysqlSet=new SSet<>();
		
		ServerConfig.getGameConfigDic().forEachValue(v->
		{
			if(!v.isAssist)
			{
				String gameURL=v.mysql;
				
				if(gameMysqlSet.add(gameURL))
				{
					Ctrl.print("game_"+v.id+":");
					DBExportApp.executeQuery(gameURL,ShineToolGlobal.serverSqlPath + "/truncateGame.sql");
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
