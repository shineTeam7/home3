using System;
using System.IO;
using ShineEngine;
using UnityEngine;

/// <summary>
/// 本地配置
/// </summary>
public class LocalSetting
{
	/** 登录连接组 */
	public static SList<string> loginURLs=new SList<string>();

	/** 登录服短链地址 */
	public static string loginHttpURL;

	/** cdn路径 */
	public static string cdnURL;

	/** 服务器列表(1 key,1 value) */
	public static SList<string> serverList=new SList<string>();

	private static ShineLoader _loader;

	/** 初始化 */
	public static void load(Action func)
	{
		_loader=new ShineLoader();

		_loader.setCompleteCall(()=>
		{
			XML xml=_loader.getXML();

			foreach(XML xl in xml.getChildrenByName("loginURL"))
			{
				loginURLs.add(xl.getProperty("value"));
			}

			loginHttpURL=getRandomLoginURL();
			cdnURL=xml.getChildrenByNameOne("cdnURL").getProperty("value");

			//正式版本
			if(ShineSetting.isRelease)
			{
				ShineGlobal.cdnSourcePath=cdnURL;
			}

			XML tt;

			if((tt=xml.getChildrenByNameOne("isOfficial"))!=null)
				ShineSetting.isOfficial=StringUtils.strToBoolean(tt.getProperty("value"));

			_loader.unload();
			func();
		});

		_loader.loadStreamingAsset(ShineGlobal.settingPath);
	}

	/** 初始化服务器列表 */
	public static void loadServerList(Action func)
	{
		_loader.setCompleteCall(()=>
		{
			XML xml=_loader.getXML();

			if(xml!=null)
			{
				foreach(XML xl in xml.getChildrenByName("server"))
				{
					serverList.add(xl.getProperty("name"));
					serverList.add(xl.getProperty("value"));
				}
			}
			else
			{
				Ctrl.throwError("未找到服务器列表配置:",ShineGlobal.serverListPath);
			}

			_loader.unload();
			func();
		});

		_loader.loadStreamingAsset(ShineGlobal.serverListPath);
	}

	/** 获取一个随机的loginURL */
	public static string getRandomLoginURL()
	{
		return loginURLs.get(MathUtils.randomInt(loginURLs.length()));
	}

	/** 设置登录url */
	public static void setLoginURL(string host,int port)
	{
		loginHttpURL="http://" + host + ":" + port;
	}
}