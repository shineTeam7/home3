package com.home.shineTool.global;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineGlobal;
import com.home.shine.utils.FileUtils;
import com.home.shineTool.constlist.ToolClientType;

import java.io.File;

/** 工具全局 */
public class ShineToolGlobal
{
	/** 是否发布版(在jar下运行) */
	public static boolean isRelease;
	
	/** 总目录(code) */
	public static String codePath;
	/** 客户端根目录 */
	public static String clientPath;
	/** 服务器根目录 */
	public static String serverPath;
	/** 文件记录目录 */
	public static String recordPath;
	/** common目录 */
	public static String commonPath;
	/** 数据工程目录 */
	public static String dataPath;
	/** 配置表目录 */
	public static String configPath;
	/** 配置表保存目录 */
	public static String configSavePath;
	/** common配置表结构目录 */
	public static String commonConfigPath;
	/** 服务器工程目录 */
	public static String serverProjectPath;
	/** 服务器工程C++目录 */
	public static String serverProjectCPath;
	/** 服务器工具目录 */
	public static String serverToolsPath;
	/** 服务器bin目录 */
	public static String serverBinPath;
	/** 服务器save目录 */
	public static String serverSavePath;
	/** 服务器temp目录 */
	public static String serverTempPath;
	/** 服务器sql存放目录 */
	public static String serverSqlPath;
	/** 服务器db数据版本记录路径 */
	public static String serverDBRecordPath;
	/** 配置表版本记录路径 */
	public static String serverConfigRecordPath;
	/** 服务器config存放目录 */
	public static String serverConfigPath;
	/** 服务器serverConfig存放目录 */
	public static String serverServerConfigPath;
	/** 机器人工程目录 */
	public static String robotProjectPath;
	/** gm客户端工程目录 */
	public static String gmClientProjectPath;
	/** 客户端热更工程源码目录 */
	public static String clientHotfixSrcPath;
	/** 客户端主工程源码目录 */
	public static String clientMainSrcPath;
	/** 客户端save目录 */
	public static String clientSavePath;
	/** 客户端temp目录 */
	public static String clientTempPath;
	/** 客户端资源目录 */
	public static String clientSourcePath;
	/** 客户端config存放目录 */
	public static String clientConfigPath;
	/** 项目工具配置目录 */
	public static String toolSettingPath;
	/** ILRuntime适配器模板路径 */
	public static String ILRuntimeAdapterTemplatePath;
	/** 服务器地图信息目录 */
	public static String mapInfoPath;
	
	/** g层数据输入路径 */
	public static String gameConfigPath;
	
	//version组
	public static int serverDBRecordVersion=2;//2:升级csType
	public static int serverConfigRecordVersion=1;
	
	//组合path
	public static String commonDataDirPath;
	public static String dataDirPath;
	public static String commonBaseDirPath;
	public static String baseDirPath;
	public static String commonGameDirPath;
	public static String gameDirPath;
	public static String commonLoginDirPath;
	public static String loginDirPath;
	
	public static void init()
	{
		File file=new File(System.getProperty("user.dir"));
		File pFile=file.getParentFile();
		
		//工具输出目录启动
		if(file.getName().endsWith("jar") || file.getName().endsWith("Jar"))
		{
			isRelease=true;
			codePath=pFile.getParentFile().getParent();
		}
		//工程启动
		else if(pFile.getName().equals("toolProject"))
		{
			isRelease=false;
			codePath=pFile.getParentFile().getParent();
		}
		//idea 工程
		else if(file.getName().equals("toolProject"))
		{
			isRelease=false;
			codePath=pFile.getParent();
		}
		else
		{
			Ctrl.print("不是合法运行目录");
			Ctrl.exit();
		}
		
		codePath=FileUtils.fixPath(codePath);
		toolSettingPath=codePath+"/server/toolsSetting/setting.xml";
		
		//后续的外面调用
	}
	
	/** 设置其他路径,通过code */
	public static void setPathByCode()
	{
		clientPath=codePath + "/client";
		serverPath=codePath + "/server";
		recordPath=codePath + "/record";
		
		setPathByMain();
	}
	
	/** 设置其他路径,通过各类主路径 */
	public static void setPathByMain()
	{
		commonPath=codePath+"/common";
		dataPath=commonPath + "/data";
		configPath=commonPath + "/config";
		configSavePath=commonPath + "/save";
		mapInfoPath=configSavePath+"/mapInfo";
		commonConfigPath=commonPath + "/commonConfig";
		serverProjectPath=serverPath + "/project";
		serverProjectCPath=serverPath + "/projectC";
		serverToolsPath=serverPath + "/tools";
		serverBinPath=serverPath + "/bin";
		serverSavePath=serverPath + "/save";
		serverTempPath=serverPath + "/temp";
		serverSqlPath=serverBinPath + "/sql";
		serverDBRecordPath=serverSavePath+"/dbRecord.bin";
		serverConfigRecordPath=serverSavePath+"/configRecord.bin";
		serverConfigPath=serverBinPath + "/config";
		serverServerConfigPath=serverBinPath + "/serverConfig";
		robotProjectPath=serverPath + "/clientProject";
		gmClientProjectPath=serverPath + "/gmClientProject";
		
		clientSavePath=clientPath + "/save";
		clientTempPath=clientPath + "/temp";
		clientSourcePath=clientPath + "/source";
		clientConfigPath=clientSourcePath + "/common";
		
		
		gameConfigPath=configPath+"/game";
		
		//laya
		if(ShineToolSetting.clientType==ToolClientType.Laya)
		{
			clientMainSrcPath=clientPath + "/game/src";
			clientHotfixSrcPath=clientPath + "/game/src";
			clientConfigPath=clientPath+"/game/bin";
		}
		//unity
		else
		{
			clientMainSrcPath=clientPath + "/game/Assets/src";
			clientHotfixSrcPath=clientPath + "/hotfix/src";
		}
		
		if(!ShineToolSetting.needHotfix)
		{
			clientHotfixSrcPath=clientMainSrcPath;
		}
		
		
		ILRuntimeAdapterTemplatePath=clientMainSrcPath+"/shine/ILRuntime/adapterTemplate.txt";
		
		ShineGlobal.binPath=serverBinPath;
		ShineGlobal.setPathsByBin();
		ShineProjectPath.init();
	}
	
	/** 检查当前不是core路径 */
	public static void checkNotCorePath()
	{
		if(codePath.endsWith("core"))
		{
			Ctrl.throwError("不能在core目录运行");
		}
	}
}
