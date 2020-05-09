package com.home.shine.serverConfig;

import com.home.shine.support.XML;
import com.home.shine.utils.StringUtils;

public class GameServerConfig extends BaseServerConfig
{
	/** 国家ID */
	public int countryID;
	/** 是否必须 */
	public boolean isNecessary;
	/** 是否辅助(跨服) */
	public boolean isAssist;
	
	@Override
	public void readByXML(XML xml)
	{
		super.readByXML(xml);
		
		isAssist=StringUtils.strToBoolean(xml.getProperty("isAssist"));
		isNecessary=StringUtils.strToBoolean(xml.getProperty("isNecessary"));
		
		String cc=xml.getProperty("countryID");
		countryID=cc.isEmpty() ? -1 : Integer.parseInt(cc);
	}
}
