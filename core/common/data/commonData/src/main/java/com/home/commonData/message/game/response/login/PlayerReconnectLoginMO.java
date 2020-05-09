package com.home.commonData.message.game.response.login;

import com.home.commonData.message.game.response.base.LoginMO;

/** 角色重连消息 */
public class PlayerReconnectLoginMO extends LoginMO
{
	/** 角色ID */
	long playerID;
	/** 令牌 */
	int token;
	/** c层msg版本号 */
	int cMsgVersion;
	/** g层msg版本号 */
	int gMsgVersion;
	/** 资源版本号 */
	int resourceVersion;
}
