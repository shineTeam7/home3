package com.home.shineTool.tool.config;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.pool.StringBuilderPool;
import com.home.shine.utils.StringUtils;
import com.home.shineTool.global.ShineToolSetting;

/** 配置记录信息 */
public class ConfigRecordInfo
{
	/** 横表or纵表 */
	public boolean isH;
	/** 是否有客户端 */
	public boolean hasClient;
	/** 是否有服务器 */
	public boolean hasServer;
	/** 是否必须表 */
	public boolean isNecessary;
	/** 是否h客户端新文件 */
	public boolean isHClientNewFile;
	/** 是否h服务器新文件 */
	public boolean isHServerNewFile;
	/** 是否h客户端新类 */
	public boolean isHClientNewClass;
	/** 是否h服务器新类 */
	public boolean isHServerNewClass;
	/** 客户端类md5 */
	public String clientClassMD5="";
	/** 服务器类md5 */
	public String serverClassMD5="";
	
	/** 常亮类名 */
	public String constName="";
	/** 是否有客户端常量 */
	public boolean hasClientConst;
	/** 是否有服务器常量 */
	public boolean hasServerConst;
	
	/** 配置类型编号 */
	public int configType;
	/** 工程类型(表定义所在工程) */
	public int projectType;
	
	public void readByString(String str)
	{
		if(str.isEmpty())
		{
			if(!ShineToolSetting.needClient)
			{
				Ctrl.throwError("不可为空");
			}
			
			return;
		}
		
		String[] arr=str.split(",");
		
		int p=0;
		
		isH=StringUtils.strToBoolean(arr[p++]);
		hasClient=StringUtils.strToBoolean(arr[p++]);
		hasServer=StringUtils.strToBoolean(arr[p++]);
		isNecessary=StringUtils.strToBoolean(arr[p++]);
		isHClientNewFile=StringUtils.strToBoolean(arr[p++]);
		isHServerNewFile=StringUtils.strToBoolean(arr[p++]);
		isHClientNewClass=StringUtils.strToBoolean(arr[p++]);
		isHServerNewClass=StringUtils.strToBoolean(arr[p++]);
		clientClassMD5=arr[p++];
		serverClassMD5=arr[p++];
		constName=arr[p++];
		hasClientConst=StringUtils.strToBoolean(arr[p++]);
		hasServerConst=StringUtils.strToBoolean(arr[p++]);
		configType=Integer.parseInt(arr[p++]);
		projectType=Integer.parseInt(arr[p++]);
	}
	
	public String writeToString()
	{
		StringBuilder sb=StringBuilderPool.create();
		sb.append(StringUtils.booleanToStr(isH));
		sb.append(',');
		sb.append(StringUtils.booleanToStr(hasClient));
		sb.append(',');
		sb.append(StringUtils.booleanToStr(hasServer));
		sb.append(',');
		sb.append(StringUtils.booleanToStr(isNecessary));
		sb.append(',');
		sb.append(StringUtils.booleanToStr(isHClientNewFile));
		sb.append(',');
		sb.append(StringUtils.booleanToStr(isHServerNewFile));
		sb.append(',');
		sb.append(StringUtils.booleanToStr(isHClientNewClass));
		sb.append(',');
		sb.append(StringUtils.booleanToStr(isHServerNewClass));
		sb.append(',');
		sb.append(clientClassMD5);
		sb.append(',');
		sb.append(serverClassMD5);
		sb.append(',');
		sb.append(constName);
		sb.append(',');
		sb.append(StringUtils.booleanToStr(hasClientConst));
		sb.append(',');
		sb.append(StringUtils.booleanToStr(hasServerConst));
		sb.append(',');
		sb.append(String.valueOf(configType));
		sb.append(',');
		sb.append(String.valueOf(projectType));
		
		
		return StringBuilderPool.releaseStr(sb);
	}
}
