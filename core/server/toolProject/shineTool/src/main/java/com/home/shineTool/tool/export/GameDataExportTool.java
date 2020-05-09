package com.home.shineTool.tool.export;

import com.home.shineTool.global.ShineProjectPath;
import com.home.shineTool.global.ShineToolSetting;

/** 游戏数据导出 */
public class GameDataExportTool extends NormalDataExportTool
{
	public GameDataExportTool()
	{
		setIsCommon(false);
	}
	
	@Override
	protected void initNext()
	{
		_dataProjectRoot=ShineProjectPath.dataDirPath;
		
		projectRoots[ServerBase]=ShineProjectPath.serverBaseDirPath;
		projectRoots[ServerGame]=ShineProjectPath.serverGameDirPath;
		projectRoots[ServerCenter]=ShineProjectPath.serverCenterDirPath;
		projectRoots[ServerLogin]=ShineProjectPath.serverLoginDirPath;
		projectRoots[ServerManager]=ShineProjectPath.serverManagerDirPath;
		
		projectRoots[GMClient]=ShineProjectPath.serverGMClientDirPath;
		projectRoots[ServerRobot]=ShineProjectPath.serverRobotDirPath;
		
		if(ShineToolSetting.needClient)
		{
			projectRoots[ClientGame]=ShineProjectPath.clientGameDirPath;
			
			if(ShineToolSetting.needHotfix)
				projectRoots[ClientHotfix]=ShineProjectPath.clientHotfixDirPath;
			else
				projectRoots[ClientHotfix]=projectRoots[ClientGame];
		}
	}
	
	public void execute()
	{
		//从common赋值
		hasBigModifieds[ClientMessage]=_commonExport.hasBigModifieds[ClientMessage];
		versionStrs[ClientMessage]=_commonExport.versionStrs[ClientMessage];
		
		super.execute();
	}
}
