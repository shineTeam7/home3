package com.home.commonData.message.game.request.login;

import com.home.commonData.data.login.PlayerLoginDO;
import com.home.shineData.support.MessageDontCopy;

import java.util.List;

/** 回复角色列表消息 */
@MessageDontCopy
public class RePlayerListMO
{
	/** 角色列表 */
	List<PlayerLoginDO> roles;
	/** 服务器出生码 */
	int serverBornCode;
}
