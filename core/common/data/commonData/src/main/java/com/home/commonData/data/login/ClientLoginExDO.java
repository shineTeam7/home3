package com.home.commonData.data.login;

/** 客户端登录扩展数据 */
public class ClientLoginExDO
{
	/** 数据 */
	ClientLoginDO data;
	/** 账号唯一ID */
	long userID;
	/** ip地址 */
	String ip;
	/** 是否成人 */
	boolean isAdult;
	/** 选择区服ID */
	int areaID=-1;
}
