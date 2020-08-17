package com.home.commonData.message.scene.serverRequest.game.login;

import com.home.commonData.message.scene.serverRequest.game.base.PlayerSceneToGameMO;

/** 回复玩家切换到场景 */
public class RePlayerSwitchToSceneMO extends PlayerSceneToGameMO
{
	/** 令牌 */
	int token;
	/** 地址 */
	String host;
	/** 端口 */
	int port;
}
