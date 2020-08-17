package com.home.shineTool.tool.export;

import com.home.shineTool.constlist.DataSectionType;
import com.home.shineTool.global.ShineProjectPath;
import com.home.shineTool.global.ShineToolSetting;

public class CommonDataExportTool extends NormalDataExportTool
{
	public CommonDataExportTool()
	{
		setIsCommon(true);
		
		//shine预留200
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
		projectRoots[ServerSceneBase]=ShineProjectPath.serverCommonSceneBaseDirPath;
		projectRoots[ServerScene]=ShineProjectPath.serverCommonSceneDirPath;
		
		projectRoots[GMClient]=ShineProjectPath.serverCommonGMClientDirPath;
		projectRoots[ServerRobot]=ShineProjectPath.serverCommonRobotDirPath;
		
		if(ShineToolSetting.needClient)
		{
			projectRoots[ClientGame]=ShineProjectPath.clientCommonGameDirPath;
			//projectRoots[ClientHotfix]="";
		}
		
		//
		////默认值
		//_gameDataLen=500;
		//_hotfixDataLen=300;
		//
		//_gameMessageLen=1000;
		//_hotfixMessageLen=500;
		//_centerMessageLen=300;
		//_managerHttpMessageLen=200;
		//_loginHttpMessageLen=100;
		//_serverMessageLen=1000;
		//
		//_listLen=1;//实际就1个
		//_centerGlobalPartLen=100;
		//_centerPlayerPartLen=100;
		//_gameGlobalPartLen=100;
		//_playerPartLen=200;
		
		
		addSection(DataSectionType.Data,ShineDataExportTool.shineDataMax,1000);
		addNextSection(DataSectionType.SceneBaseRequest,500);
		addNextSection(DataSectionType.SceneBaseResponse,500);
		addNextSection(DataSectionType.SceneRequest,300);
		addNextSection(DataSectionType.SceneResponse,300);
		addNextSection(DataSectionType.CenterRequest,200);
		addNextSection(DataSectionType.CenterResponse,200);
		
		//100
		addSection(DataSectionType.ClientLoginResult,3300,100);
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
		
		//end 5605
		
		addSection(DataSectionType.GameRequest,13312,1000);
		addNextSection(DataSectionType.GameResponse,1000);
		
		//end2 15312
	}
}
