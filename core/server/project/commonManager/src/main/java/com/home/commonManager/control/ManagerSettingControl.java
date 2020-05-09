package com.home.commonManager.control;

import com.home.commonBase.constlist.generate.ClientPlatformType;
import com.home.commonBase.data.login.ClientVersionData;
import com.home.commonBase.global.BaseC;
import com.home.shine.global.ShineGlobal;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.XML;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.utils.FileUtils;

public class ManagerSettingControl
{
	/** 是否正式线上 */
	public boolean isOfficial=false;
	
	/** 是否启用就开服 */
	public boolean isOpenOnStart=true;
	
	/** 客户端app版本 */
	public IntObjectMap<ClientVersionData> clientVersionDic;
	/** 重定向的url组 */
	public IntObjectMap<IntObjectMap<String>> redirectURLDic;
	
	//area区服部分
	
	/** 初始化 */
	public synchronized void init()
	{
		load();
	}
	
	/** 读取 */
	public synchronized void load()
	{
		toLoad();
	}
	
	protected void toLoad()
	{
		//setting
		XML xml=FileUtils.readFileForXML(ShineGlobal.serverSettingPath);
		XML tt;
		
		if((tt=xml.getChildrenByNameOne("isOfficial"))!=null)
			isOfficial=tt.getPropertyBoolean("value");
		
		if((tt=xml.getChildrenByNameOne("isOpenOnStart"))!=null)
			isOpenOnStart=tt.getPropertyBoolean("value");
		
		redirectURLDic=new IntObjectMap<>();
		
		for(XML xl : xml.getChildrenByName("redirectURL"))
		{
			redirectURLDic.computeIfAbsent(BaseC.constlist.clientPlatform_getTypeByName(xl.getProperty("type")),k->new IntObjectMap<>())
					.put(xl.getPropertyInt("resourceVersion"),xl.getProperty("url"));
		}
		
		//clientVersion
		xml=FileUtils.readFileForXML(ShineGlobal.clientVersionPath);
		
		clientVersionDic=new IntObjectMap<>(ClientVersionData[]::new);
		
		if(xml!=null)
		{
			xml.getChildrenByName("platform").forEach(v->
			{
				ClientVersionData data=new ClientVersionData();
				data.currentAppVersion=v.getPropertyInt("currentAppVersion");
				data.leastAppVersion=v.getPropertyInt("leastAppVersion");
				data.leastResourceVersion=v.getPropertyInt("leastResourceVersion");
				data.currentResourceVersion=v.getPropertyInt("currentResourceVersion");
				data.version=v.getProperty("version");
				data.type=BaseC.constlist.clientPlatform_getTypeByName(v.getProperty("type"));
				
				clientVersionDic.put(data.type,data);
			});
		}
	}
}
