package com.home.commonClient.global;

import com.home.commonBase.config.game.RobotTestModeConfig;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineGlobal;
import com.home.shine.support.XML;
import com.home.shine.support.collection.SList;
import com.home.shine.utils.FileUtils;

/** 客户端全局信息 */
public class ClientGlobal
{
	//settings
	/** 是否保持机器人数目 */
	public static boolean needKeepClient=true;
	
	
	/** 是否初始化过 */
	private static boolean _inited=false;
	
	/** 登陆服列表 */
	private static SList<String> _loginURLList=new SList<>(String[]::new);
	
	/** 行为模式 */
	public static int mode=-1;
	
	/** 登录区服ID */
	public static int areaID=1;
	/** 登录国家ID */
	public static int countryID=-1;
	/** 名字前缀 */
	public static String nameFront="robot";
	
	/** 模式配置 */
	public static RobotTestModeConfig config;
	///** 模式参数组 */
	//public static int[] modeArgs=new int[0];
	/** 序号从多少开始 */
	public static int startNum=0;
	/** 登录数目 */
	public static int num=1;
	/** 登录间隔 */
	public static int delay=0;
	/** 线程数目 */
	public static int threadNum=4;
	
	/** 指令端口 */
	public static int port=12321;
	
	/** 从输入参数读取 */
	public static void readByMainArgs(String[] args)
	{
		for(String v:args)
		{
			if(v.startsWith("-"))
			{
				String[] arr=v.substring(1).split("=");
				
				switch(arr[0])
				{
					case "areaID":
					{
						areaID=Integer.parseInt(arr[1]);
					}
						break;
					case "nameFront":
					{
						nameFront=arr[1];
					}
						break;
					case "mode":
					{
						mode=Integer.parseInt(arr[1]);
					}
						break;
					case "num":
					{
						num=Integer.parseInt(arr[1]);
					}
						break;
					case "delay":
					{
						delay=Integer.parseInt(arr[1]);
					}
						break;
					case "threadNum":
					{
						threadNum=Integer.parseInt(arr[1]);
					}
						break;
					case "port":
					{
						port=Integer.parseInt(arr[1]);
					}
						break;
					default:
					{
						Ctrl.throwError("参数不正确");
					}
						break;
				}
			}
			else
			{
				Ctrl.throwError("参数不正确");
			}
		}
	}
	
	public static void init()
	{
		if(_inited)
			return;
		
		_inited=true;
		
		//setting
		XML xml=FileUtils.readFileForXML(ShineGlobal.robotSettingPath);
		XML tt;
		
		for(XML aXML:xml.getChildrenByName("loginURL"))
		{
			_loginURLList.add(aXML.getProperty("value"));
		}
		
		if((tt=xml.getChildrenByNameOne("num"))!=null)
			num=tt.getPropertyInt("value");
		
		if((tt=xml.getChildrenByNameOne("areaID"))!=null)
			areaID=tt.getPropertyInt("value");
		
		if((tt=xml.getChildrenByNameOne("nameFront"))!=null)
			nameFront=tt.getProperty("value");
		
		if((tt=xml.getChildrenByNameOne("mode"))!=null)
			mode=tt.getPropertyInt("value");
		
		if((tt=xml.getChildrenByNameOne("delay"))!=null)
			delay=tt.getPropertyInt("value");
		
		if((tt=xml.getChildrenByNameOne("threadNum"))!=null)
			threadNum=tt.getPropertyInt("value");
	}
	
	public static SList<String> getLoginURLList()
	{
		return _loginURLList;
	}
	
	/** 测试添加 */
	public static void addHost(String host)
	{
		_loginURLList.add("http://"+host+":20014");
	}
	
	/** 设置唯一主机 */
	public static void setHost(String host)
	{
		_loginURLList.clear();
		addHost(host);
	}
}
