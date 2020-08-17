package com.home.shineTool.global;

import com.home.shineTool.utils.ToolFileUtils;

public class ShineProjectPath
{
	//组合path
	public static String commonDataDirPath;
	public static String dataDirPath;
	public static String serverShineDirPath;
	public static String serverCommonBaseDirPath;
	public static String serverBaseDirPath;
	public static String serverCommonGameDirPath;
	public static String serverGameDirPath;
	public static String serverCommonLoginDirPath;
	public static String serverLoginDirPath;
	public static String serverCommonCenterDirPath;
	public static String serverCenterDirPath;
	public static String serverCommonSceneBaseDirPath;
	public static String serverSceneBaseDirPath;
	public static String serverCommonSceneDirPath;
	public static String serverSceneDirPath;
	public static String serverCommonRobotDirPath;
	public static String serverRobotDirPath;
	public static String serverCommonManagerDirPath;
	public static String serverManagerDirPath;
	public static String serverCommonGMClientDirPath;
	public static String serverGMClientDirPath;
	
	public static String clientCommonGameDirPath;
	public static String clientGameDirPath;
	public static String clientCommonMainDirPath;
	public static String clientMainDirPath;
	public static String clientHotfixDirPath;
	
	public static void init()
	{
		commonDataDirPath=ShineToolGlobal.dataPath+"/"+ToolFileUtils.getJavaCodePath("commonData");
		dataDirPath=ShineToolGlobal.dataPath+"/"+ToolFileUtils.getJavaCodePath("gameData");
		serverShineDirPath=ShineToolGlobal.serverProjectPath+"/"+ToolFileUtils.getJavaCodePath("shine");
		serverCommonBaseDirPath=ShineToolGlobal.serverProjectPath+"/"+ToolFileUtils.getJavaCodePath("commonBase");
		serverBaseDirPath=ShineToolGlobal.serverProjectPath+"/"+ToolFileUtils.getJavaCodePath("base");
		serverCommonGameDirPath=ShineToolGlobal.serverProjectPath+"/"+ToolFileUtils.getJavaCodePath("commonGame");
		serverGameDirPath=ShineToolGlobal.serverProjectPath+"/"+ToolFileUtils.getJavaCodePath("game");
		serverCommonLoginDirPath=ShineToolGlobal.serverProjectPath+"/"+ToolFileUtils.getJavaCodePath("commonLogin");
		serverLoginDirPath=ShineToolGlobal.serverProjectPath+"/"+ToolFileUtils.getJavaCodePath("login");
		serverCommonCenterDirPath=ShineToolGlobal.serverProjectPath+"/"+ToolFileUtils.getJavaCodePath("commonCenter");
		serverCenterDirPath=ShineToolGlobal.serverProjectPath+"/"+ToolFileUtils.getJavaCodePath("center");
		serverCommonSceneBaseDirPath=ShineToolGlobal.serverProjectPath+"/"+ToolFileUtils.getJavaCodePath("commonSceneBase");
		serverSceneBaseDirPath=ShineToolGlobal.serverProjectPath+"/"+ToolFileUtils.getJavaCodePath("sceneBase");
		serverCommonSceneDirPath=ShineToolGlobal.serverProjectPath+"/"+ToolFileUtils.getJavaCodePath("commonScene");
		serverSceneDirPath=ShineToolGlobal.serverProjectPath+"/"+ToolFileUtils.getJavaCodePath("scene");
		serverCommonRobotDirPath=ShineToolGlobal.robotProjectPath+"/"+ToolFileUtils.getJavaCodePath("commonClient");
		serverRobotDirPath=ShineToolGlobal.robotProjectPath+"/"+ToolFileUtils.getJavaCodePath("client");
		serverCommonManagerDirPath=ShineToolGlobal.serverProjectPath+"/"+ToolFileUtils.getJavaCodePath("commonManager");
		serverManagerDirPath=ShineToolGlobal.serverProjectPath+"/"+ToolFileUtils.getJavaCodePath("manager");
		serverCommonGMClientDirPath=ShineToolGlobal.gmClientProjectPath+"/"+ToolFileUtils.getJavaCodePath("commonGMClient");
		serverGMClientDirPath=ShineToolGlobal.gmClientProjectPath+"/"+ToolFileUtils.getJavaCodePath("gmClient");
		
		
		clientCommonGameDirPath=ShineToolGlobal.clientMainSrcPath + "/commonGame";
		clientGameDirPath=ShineToolGlobal.clientMainSrcPath + "/game";
		clientCommonMainDirPath=ShineToolGlobal.clientMainSrcPath + "/commonMain";
		clientMainDirPath=ShineToolGlobal.clientMainSrcPath + "/main";
		clientHotfixDirPath=ShineToolGlobal.clientHotfixSrcPath + "/hotfix";
	}
	
	public static String getDataPath(boolean isCommon)
	{
		return isCommon ? commonDataDirPath : dataDirPath;
	}
	
	public static String getServerBasePath(boolean isCommon)
	{
		return isCommon ? serverCommonBaseDirPath : serverBaseDirPath;
	}
	
	public static String getServerGamePath(boolean isCommon)
	{
		return isCommon ? serverCommonGameDirPath : serverGameDirPath;
	}
	
	public static String getServerLoginPath(boolean isCommon)
	{
		return isCommon ? serverCommonLoginDirPath : serverLoginDirPath;
	}
	
	public static String getServerCenterPath(boolean isCommon)
	{
		return isCommon ? serverCommonCenterDirPath : serverCenterDirPath;
	}
	
	public static String getServerManagerPath(boolean isCommon)
	{
		return isCommon ? serverCommonManagerDirPath : serverManagerDirPath;
	}
	
	public static String getServerGMClientPath(boolean isCommon)
	{
		return isCommon ? serverCommonGMClientDirPath : serverGMClientDirPath;
	}
	
	public static String getServerRobotPath(boolean isCommon)
	{
		return isCommon ? serverCommonRobotDirPath : serverRobotDirPath;
	}
	
	public static String getClientMainPath(boolean isCommon)
	{
		return isCommon ? clientCommonMainDirPath : clientMainDirPath;
	}
	
	public static String getClientGamePath(boolean isCommon)
	{
		return isCommon ? clientCommonGameDirPath : clientGameDirPath;
	}
}
