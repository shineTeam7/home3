package com.home.commonData.message.login.serverRequest.login.login;

import com.home.commonData.data.login.ClientLoginDO;

/** 客户端登陆转移消息 */
public class ClientLoginTransferMO
{
	/** 登录数据 */
	ClientLoginDO data;
	/** http消息ID */
	int httpID;
	/** ip地址 */
	String ip;
}
