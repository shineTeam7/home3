package com.home.shine.serverConfig;

import com.home.shine.support.XML;

/** 基础服务器配置 */
public class BaseServerConfig
{
	/** id */
	public int id=0;
	/** 内网地址 */
	public String serverHost;
	/** 外网地址 */
	public String clientHost;
	/** 服务器端口 */
	public int serverPort;
	/** 客户端端口 */
	public int clientPort;
	/** 服务器http端口 */
	public int serverHttpPort;
	/** 客户端http端口 */
	public int clientHttpPort;
	/** mysql */
	public String mysql;
	/** 客戶端使用端口 */
	public int clientUsePort;
	
	/** 从xml读取 */
	public void readByXML(XML xml)
	{
		String temp;
		
		temp=xml.getProperty("id");
		id=temp.isEmpty() ? 0 : Integer.parseInt(temp);
		
		serverHost=xml.getProperty("serverHost");
		clientHost=xml.getProperty("clientHost");
		
		temp=xml.getProperty("serverPort");
		serverPort=temp.isEmpty() ? -1 : Integer.parseInt(temp);
		temp=xml.getProperty("serverHttpPort");
		serverHttpPort=temp.isEmpty() ? -1 : Integer.parseInt(temp);
		temp=xml.getProperty("clientPort");
		clientPort=temp.isEmpty() ? -1 : Integer.parseInt(temp);
		temp=xml.getProperty("clientHttpPort");
		clientHttpPort=temp.isEmpty() ? -1 : Integer.parseInt(temp);
		
		mysql=xml.getProperty("mysql");
		
		temp=xml.getProperty("clientUsePort");
		clientUsePort=temp.isEmpty() ? clientPort : Integer.parseInt(temp);
	}
}
