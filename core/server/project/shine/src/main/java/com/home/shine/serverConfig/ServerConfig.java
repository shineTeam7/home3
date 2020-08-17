package com.home.shine.serverConfig;

import com.home.shine.global.ShineGlobal;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.XML;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.utils.FileUtils;

/** 服务器配置(实际环境只有manager加载) */
public class ServerConfig
{
	/** 是否初始化过 */
	private static boolean _inited=false;
	
	/** manager配置 */
	private static BaseServerConfig _managerConfig;
	/** 中心服配置 */
	private static BaseServerConfig _centerConfig;
	/** 登录服配置组 */
	private static IntObjectMap<BaseServerConfig> _loginConfigDic;
	/** 区服配置组 */
	private static IntObjectMap<GameServerConfig> _gameConfigDic;
	/** 场景服配置组 */
	private static IntObjectMap<BaseServerConfig> _sceneConfigDic;
	
	/** 初始化 */
	public static synchronized void init()
	{
		if(_inited)
		{
			return;
		}
		
		_inited=true;
		
		load();
	}
	
	/** 重读 */
	public static synchronized void load()
	{
		String ip=null;
		String mysql=null;
		
		//debug模式下
		if(!ShineSetting.isRelease)
		{
			XML localXML=FileUtils.readFileForXML(ShineGlobal.serverLocalSettingPath);
			
			if(localXML!=null)
			{
				ip=localXML.getProperty("ip");
				mysql=localXML.getProperty("mysql");
			}
		}
		
		XML xml=FileUtils.readFileForXML(ShineGlobal.serverConfigPath);
		
		BaseServerConfig centerConfig=new BaseServerConfig();
		centerConfig.readByXML(xml.getChildrenByNameOne("center"));
		
		if(ip!=null)
			makeLocalConfig(centerConfig,ip,mysql);
		
		IntObjectMap<BaseServerConfig> loginConfigDic=new IntObjectMap<>();
		
		for(XML xl : xml.getChildrenByName("login"))
		{
			BaseServerConfig config=new BaseServerConfig();
			config.readByXML(xl);
			
			if(ip!=null)
				makeLocalConfig(config,ip,mysql);
			
			loginConfigDic.put(config.id,config);
		}
		
		IntObjectMap<GameServerConfig> gameConfigDic=new IntObjectMap<>();
		
		for(XML xl : xml.getChildrenByName("game"))
		{
			GameServerConfig config=new GameServerConfig();
			config.readByXML(xl);
			
			if(ip!=null)
				makeLocalConfig(config,ip,mysql);
			
			gameConfigDic.put(config.id,config);
		}
		
		IntObjectMap<BaseServerConfig> sceneConfigDic=new IntObjectMap<>();
		
		for(XML xl : xml.getChildrenByName("scene"))
		{
			BaseServerConfig config=new BaseServerConfig();
			config.readByXML(xl);
			
			if(ip!=null)
				makeLocalConfig(config,ip,mysql);
			
			sceneConfigDic.put(config.id,config);
		}
		
		BaseServerConfig managerConfig=new BaseServerConfig();
		XML manager=xml.getChildrenByNameOne("manager");
		if(manager!=null)
			managerConfig.readByXML(manager);
		
		
		_centerConfig=centerConfig;
		_loginConfigDic=loginConfigDic;
		_gameConfigDic=gameConfigDic;
		_sceneConfigDic=sceneConfigDic;
		_managerConfig=managerConfig;
	}
	
	private static void makeLocalConfig(BaseServerConfig config,String ip,String mysql)
	{
		config.clientHost=ip;
		config.mysql=config.mysql.substring(0,config.mysql.indexOf(",")+1)+mysql;
	}
	
	/** 获取game查询配置 */
	public static BaseServerConfig getManagerConfig()
	{
		return _managerConfig;
	}
	
	/** 中心服配置 */
	public static BaseServerConfig getCenterConfig()
	{
		return _centerConfig;
	}
	
	/** 获取登录服配置 */
	public static BaseServerConfig getLoginConfig(int id)
	{
		return _loginConfigDic.get(id);
	}
	
	/** 获取全部登录服配置 */
	public static IntObjectMap<BaseServerConfig> getLoginConfigDic()
	{
		return _loginConfigDic;
	}
	
	/** 获取区服配置 */
	public static GameServerConfig getGameConfig(int id)
	{
		return _gameConfigDic.get(id);
	}
	
	/** 获取全部区服配置 */
	public static IntObjectMap<GameServerConfig> getGameConfigDic()
	{
		return _gameConfigDic;
	}
	
	/** 获取全部区服配置 */
	public static IntObjectMap<BaseServerConfig> getSceneConfigDic()
	{
		return _sceneConfigDic;
	}
}
