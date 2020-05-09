package com.home.commonBase.global;

import com.home.shine.ShineSetup;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineGlobal;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.XML;
import com.home.shine.utils.FileUtils;

public class AppSetting
{
	/** 初始化 */
	public static void init()
	{
		ShineGlobal.init();
		CommonSetting.init();
		
		if(CommonSetting.isClient)
		{
			//机器人不显示接受消息
			ShineSetting.needShowClientMessage=false;
		}
		
		if(ShineSetting.isRelease)
			initRelease();
		else
			initDebug();
	}
	
	/** 设置调试参数 */
	private static void initDebug()
	{
		//ShineSetting.needError=false;//不再抛错
		ShineSetting.printUseCurrentThread=true;
		ShineSetting.openCheck=true;
		ShineSetting.needDebugLog=true;
		
		//debug下需要断点,所以以下两项关闭
		ShineSetting.needThreadDeadCheck=false;
		ShineSetting.logNeedConsole=true;
		ShineSetting.needPingCut=false;
		
		//debug关了不看
		ShineSetting.needThreadWatch=false;
		ShineSetting.needNetFlowWatch=false;
		
		ShineSetting.needDetailLog=false;
		
		ShineSetting.socketReConnectKeepTime=3;
		
		CommonSetting.needSendWarningLog=true;
		//db写库时间5秒
		CommonSetting.dbWriteDelay=5;
		CommonSetting.dbWriteOnceMaxTime=3;
		
		CommonSetting.openDBWriteTimeOutCheck=false;
		
		//离线保留4秒
		CommonSetting.playerOfflineCheckDelay=2;
		CommonSetting.playerOfflineKeepTime=4;
		CommonSetting.roleSocialDataKeepTime=4;
		
		//table保留时间10秒
		CommonSetting.playerTableKeepTime=10;
		
		checkSQLVersion();
	}
	
	/** 设置发布参数 */
	private static void initRelease()
	{
		//ShineSetting.needError=false;
		ShineSetting.logNeedConsole=false;
		ShineSetting.openCheck=false;
		ShineSetting.needDebugLog=false;
		
		//观测部分
		ShineSetting.needThreadDeadCheck=true;
		ShineSetting.needThreadWatch=true;
		
		//服务器开
		ShineSetting.needNetFlowWatch=!CommonSetting.isClient;
		
		ShineSetting.needPingCut=true;
		
		ShineSetting.needDetailLog=true;
	}
	
	/** 检查数据库表结构版本(开发期用) */
	public static void checkSQLVersion()
	{
		//机器人不检查
		if(CommonSetting.isClient)
			return;
		
		XML local=FileUtils.readFileForXML(ShineGlobal.binPath + "/../temp/sql/localVersion.xml");
		XML server=FileUtils.readFileForXML(ShineGlobal.binPath + "/../save/sqlVersion.xml");
		
		if(local==null || !local.getProperty("version").equals(server.getProperty("version")))
		{
			ShineSetup.exit("数据库版本不匹配,需要执行dbExport!");
		}
	}
}
