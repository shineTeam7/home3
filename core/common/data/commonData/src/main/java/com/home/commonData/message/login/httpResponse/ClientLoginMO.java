package com.home.commonData.message.login.httpResponse;

import com.home.commonData.data.login.ClientLoginDO;

/** 客户端登录(自有登录) */
public class ClientLoginMO
{
	/** c层msg版本号 */
	int cMsgVersion;
	/** g层msg版本号 */
	int gMsgVersion;
	/** 登录数据 */
	ClientLoginDO data;
}
