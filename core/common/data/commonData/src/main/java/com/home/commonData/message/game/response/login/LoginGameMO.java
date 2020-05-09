package com.home.commonData.message.game.response.login;

import com.home.commonData.message.game.response.base.LoginMO;

/** 客户端登录逻辑服 */
public class LoginGameMO extends LoginMO
{
	/** 令牌 */
	int token;
	/** c层msg版本号 */
	int cMsgVersion;
	/** g层msg版本号 */
	int gMsgVersion;
	/** 资源版本号 */
	int resourceVersion;
}
