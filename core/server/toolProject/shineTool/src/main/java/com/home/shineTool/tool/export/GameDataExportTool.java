package com.home.shineTool.tool.export;

import com.home.shine.ctrl.Ctrl;
import com.home.shineTool.constlist.DataSectionType;
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
		projectRoots[ServerSceneBase]=ShineProjectPath.serverSceneBaseDirPath;
		projectRoots[ServerScene]=ShineProjectPath.serverSceneDirPath;
		
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
		
		
		addSection(DataSectionType.Data,5605,ShineToolSetting.useOldHDataSection ? 500:1000);
		addNextSection(DataSectionType.HotfixData,ShineToolSetting.useOldHDataSection ? 900 :400);
		addNextSection(DataSectionType.GameRequest,1000);
		addNextSection(DataSectionType.GameResponse,1000);
		addNextSection(DataSectionType.SceneBaseRequest,300);
		addNextSection(DataSectionType.SceneBaseResponse,300);
		addNextSection(DataSectionType.SceneRequest,300);
		addNextSection(DataSectionType.SceneResponse,300);
		addNextSection(DataSectionType.CenterRequest,200);
		addNextSection(DataSectionType.CenterResponse,200);
		
		addSection(DataSectionType.ClientLoginResult,10605,100);
		addNextSection(DataSectionType.ClientLoginHttp,100);
		addNextSection(DataSectionType.ManegeHttpResult,200);
		addNextSection(DataSectionType.ManegeHttp,200);
		addNextSection(DataSectionType.SererMessage,1000);
		
		addNextSection(DataSectionType.CenterGlobalPart,100);
		addNextSection(DataSectionType.CenterGlobalList,1);
		
		addSection(DataSectionType.GameGlobalPart,getLastDataSectionEnd()+101,100);
		addNextSection(DataSectionType.GameGlobalList,1);
		
		addNextSection(DataSectionType.PlayerPart,200);
		addNextSection(DataSectionType.ClientPlayerPart,200);
		addNextSection(DataSectionType.PlayerList,1);
		addNextSection(DataSectionType.ClientPlayerList,1);
		
		addNextSection(DataSectionType.HotfixPlayerPart,200);
		addNextSection(DataSectionType.HotfixClientPlayerPart,200);
		addNextSection(DataSectionType.HotfixPlayerList,1);
		addNextSection(DataSectionType.HotfixClientPlayerList,1);
		
		//end 13312
		
		
		//end2 15312
		
		addSection(DataSectionType.HotfixGameRequest,15312,500);
		addNextSection(DataSectionType.HotfixGameResponse,500);
		addNextSection(DataSectionType.HotfixSceneBaseRequest,200);
		addNextSection(DataSectionType.HotfixSceneBaseResponse,200);
		addNextSection(DataSectionType.HotfixSceneRequest,200);
		addNextSection(DataSectionType.HotfixSceneResponse,200);
		addNextSection(DataSectionType.HotfixCenterRequest,100);
		addNextSection(DataSectionType.HotfixCenterResponse,100);
		
		//end3 17312
	}
	
	public void execute()
	{
		//从common赋值
		hasBigModifieds[ClientMessage]=_commonExport.hasBigModifieds[ClientMessage];
		versionStrs[ClientMessage]=_commonExport.versionStrs[ClientMessage];
		
		super.execute();
	}
}
