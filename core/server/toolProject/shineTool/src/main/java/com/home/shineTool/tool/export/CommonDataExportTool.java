package com.home.shineTool.tool.export;

import com.home.shineTool.global.ShineProjectPath;
import com.home.shineTool.global.ShineToolSetting;

public class CommonDataExportTool extends NormalDataExportTool
{
	public CommonDataExportTool()
	{
		setIsCommon(true);
		
		//shine预留200
		setStart(ShineDataExportTool.shineDataMax);
	}
	
	@Override
	protected void initNext()
	{
		_dataProjectRoot=ShineProjectPath.commonDataDirPath;
		
		projectRoots[ServerBase]=ShineProjectPath.serverCommonBaseDirPath;
		projectRoots[ServerGame]=ShineProjectPath.serverCommonGameDirPath;
		projectRoots[ServerCenter]=ShineProjectPath.serverCommonCenterDirPath;
		projectRoots[ServerLogin]=ShineProjectPath.serverCommonLoginDirPath;
		projectRoots[ServerManager]=ShineProjectPath.serverCommonManagerDirPath;
		
		projectRoots[GMClient]=ShineProjectPath.serverCommonGMClientDirPath;
		projectRoots[ServerRobot]=ShineProjectPath.serverCommonRobotDirPath;
		
		if(ShineToolSetting.needClient)
		{
			projectRoots[ClientGame]=ShineProjectPath.clientCommonGameDirPath;
			//projectRoots[ClientHotfix]="";
		}
	}
}
