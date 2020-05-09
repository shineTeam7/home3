package com.home.shine.global;

import com.home.shine.constlist.ServerOSType;

import java.io.File;

/** 全局信息(各种路径相关) */
public class ShineGlobal
{
	/** 进程名(需要赋值) */
	public static String processName="game";
	/** 主路径(bin目录) */
	public static String binPath;
	/** 库路径(lib目录) */
	public static String libPath;
	/** 日志路径(需要赋值) */
	public static String logPath="";
	/** 类路径 */
	public static String classPath="";
	
	/** 配置路径 */
	public static String configPath="";
	/** 配置主文件路径 */
	public static String configMainFilePath="";
	/** 配置增量路径 */
	public static String configIncrementFilePath="";
	/** 配置客户端热更文件路径 */
	public static String configHotfixFilePath="";
	/** 服务器配置根目录 */
	public static String serverConfigRoot="";
	/** 服务器配置路径(manager用) */
	public static String serverSettingPath="";
	/** 服务器配置路径 */
	public static String serverConfigPath="";
	/** 服务器单个节点配置路径 */
	public static String serverNodeConfigPath="";
	/** 服务器本地设置路径 */
	public static String serverLocalSettingPath="";
	/** 客户端资源配置路径 */
	public static String clientVersionPath="";
	/** 机器人配置路径 */
	public static String robotSettingPath="";
	/** 服务器sql存放目录 */
	public static String serverSqlPath="";
	/** 本地DB文件路径 */
	public static String dbFilePath="";
	/** 客户端ssl证书路径 */
	public static String clientSSLPath="";
	/** 服务器ssl证书路径 */
	public static String serverSSLPath="";
	
	//version组
	/** 配置版本 */
	public static int configVersion=1;
	/** 本地DB文件版本 */
	public static int localDBFileVersion=2;
	
	/** 操作系统类型 */
	public static int osType;
	
	/** kcp类完全限定名 */
	public static String kcpClassQName="io.netty.bootstrap.UkcpServerBootstrap";
	
	/** 初始化 */
	public static void init()
	{
		if(ShineSetting.isRelease)
		{
			File file=new File(System.getProperty("user.dir"));
			binPath=file.getParent();
		}
		else
		{
			File file=new File(System.getProperty("user.dir"));
			File binFile=file.getParentFile();
			
			//输出目录
			if(binFile.getName().equals("bin"))
			{
				binPath=binFile.getPath();
			}
			else
			{
				binPath=file.getParentFile().getParent() + "/bin";
				binFile=new File(binPath);
				
				//idea IDE
				if(!binFile.exists())
				{
					binPath=file.getParent() + "/bin";
				}
			}
		}
		
		setPathsByBin();
	}
	
	/** 通过bin路径设置其他路径(通过binPath) */
	public static void setPathsByBin()
	{
		osType=ServerOSType.getType(System.getProperty("os.name"));
		
		libPath=binPath+"/lib";
		configPath=binPath + "/config";
		configMainFilePath=configPath + "/config.bin";
		configIncrementFilePath=configPath + "/configIncrement.bin";
		configHotfixFilePath=configPath + "/configHotfix.bin";
		classPath=binPath + "/class";
		serverConfigRoot=binPath + "/serverConfig";
		serverSettingPath=serverConfigRoot+"/setting.xml";
		serverConfigPath=serverConfigRoot+"/server.xml";
		serverNodeConfigPath=serverConfigRoot+"/node.xml";
		serverLocalSettingPath=serverConfigRoot+"/serverLocal.xml";
		clientVersionPath=serverConfigRoot+"/clientVersion.xml";
		robotSettingPath=serverConfigRoot+"/robotSetting.xml";
		clientSSLPath=serverConfigRoot+"/client.jks";
		serverSSLPath=serverConfigRoot+"/server.jks";
		logPath=binPath + "/log";
		serverSqlPath=binPath+"/sql";
		dbFilePath=binPath+"/save/db";
	}
	
	public static void loadLib()
	{
		//-Djava.library.path
		String libPath=ShineGlobal.libPath+"/"+ServerOSType.getLibFolder(ShineGlobal.osType);
		//System.loadLibrary("game");
		System.load(libPath+"/"+ServerOSType.getLibName("game",ShineGlobal.osType));
	}
}
