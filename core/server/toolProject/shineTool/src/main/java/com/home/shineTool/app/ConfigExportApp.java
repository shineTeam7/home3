package com.home.shineTool.app;

import com.home.shineTool.constlist.ExecuteReleaseType;
import com.home.shineTool.global.ShineProjectPath;
import com.home.shineTool.tool.export.CommonDataExportTool;
import com.home.shineTool.tool.export.NormalConfigMakeTool;
import com.home.shine.ctrl.Ctrl;
import com.home.shineTool.ShineToolSetup;
import com.home.shineTool.constlist.CodeType;
import com.home.shineTool.constlist.ProjectType;
import com.home.shineTool.global.ShineToolGlobal;
import com.home.shineTool.global.ShineToolSetting;
import com.home.shineTool.tool.config.ConfigMakeTool;

/** 配置表导出 */
public class ConfigExportApp
{
	//common工程
	protected ConfigMakeTool _commonTool;
	//game工程
	protected ConfigMakeTool _gameTool;
	//gameFix工程
	protected ConfigMakeTool _logicTool;
	
	public ConfigExportApp()
	{
		_commonTool=new NormalConfigMakeTool();
		_commonTool.init(ProjectType.Common,ShineToolGlobal.commonConfigPath,
				ShineToolGlobal.serverConfigPath,ShineProjectPath.serverCommonBaseDirPath,CodeType.Java,
				ShineToolGlobal.clientConfigPath,ShineProjectPath.clientCommonGameDirPath,ShineToolSetting.getClientCodeType(),
				ShineToolGlobal.recordPath + "/commonConfigRecord.xml");
		
		
		_gameTool=new NormalConfigMakeTool();
		_gameTool.init(ProjectType.Game,ShineToolGlobal.gameConfigPath,
				ShineToolGlobal.serverConfigPath,ShineProjectPath.serverBaseDirPath,CodeType.Java,
				ShineToolGlobal.clientConfigPath,ShineProjectPath.clientGameDirPath,ShineToolSetting.getClientCodeType(),
				ShineToolGlobal.recordPath + "/gameConfigRecord.xml");
		_gameTool.setParentTool(_commonTool);
		
		if(ShineToolSetting.needHotfix)
		{
			_logicTool=new NormalConfigMakeTool();
			_logicTool.init(ProjectType.HotFix,ShineToolGlobal.configPath+"/hotfix",
					ShineToolGlobal.serverConfigPath,ShineProjectPath.serverBaseDirPath,CodeType.Java,
					ShineToolGlobal.clientConfigPath,ShineProjectPath.clientHotfixDirPath,ShineToolSetting.getClientCodeType(),
					ShineToolGlobal.recordPath + "/hotfixConfigRecord.xml");
			_logicTool.setParentTool(_gameTool);
		}
	}
	
	public void executeAll(boolean isAll,int releaseType)
	{
		ShineToolSetting.isAll=isAll;
		ShineToolSetting.releaseType=releaseType;
		
		CommonDataExportTool.makeShinePackage();
		//注册data定义
		new DataExportApp().registDefine();
		
		_commonTool.setStart(1);
		_commonTool.execute();
		
		_gameTool.setStart(_commonTool.getEnd());
		_gameTool.execute();
		
		if(ShineToolSetting.needHotfix)
		{
			_logicTool.setStart(_gameTool.getEnd());
			_logicTool.execute();
		}
		
		_commonTool.writeRecord();
		_gameTool.writeRecord();
		
		if(ShineToolSetting.needHotfix)
		{
			_logicTool.writeRecord();
		}
		
		Ctrl.print("OK");
	}
	
	public static void doConfig()
	{
		new ConfigExportApp().executeAll(false,ExecuteReleaseType.Debug);
	}
	
	public static void main(String[] args)
	{
		ShineToolSetup.init();

		//ShineToolSetting.isAllRefresh=true;
//		ShineToolSetting.needTrace=true;
		
		new ConfigExportApp().executeAll(false,ExecuteReleaseType.Debug);
		
		//makeUI();
	}
}
