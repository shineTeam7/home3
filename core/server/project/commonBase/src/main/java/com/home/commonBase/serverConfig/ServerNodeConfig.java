package com.home.commonBase.serverConfig;

import com.home.shine.global.ShineGlobal;
import com.home.shine.serverConfig.BaseServerConfig;
import com.home.shine.support.XML;
import com.home.shine.utils.FileUtils;

/** 单个节点配置 */
public class ServerNodeConfig
{
	/** 是否初始化过 */
	private static boolean _inited=false;
	
	public static BaseServerConfig managerConfig;
	
	/** manager地址 */
	public static String managerHost;
	/** manager端口 */
	public static int managerPort;
	
	/** 初始化 */
	public static synchronized void init()
	{
		if(_inited)
		{
			return;
		}
		
		_inited=true;
		
		XML xml=FileUtils.readFileForXML(ShineGlobal.serverNodeConfigPath);
		
		if(xml!=null)
		{
			XML manager=xml.getChildrenByNameOne("manager");
			
			managerConfig=new BaseServerConfig();
			managerConfig.readByXML(manager);
		}
	}
}
