package com.home.commonData.message.game.serverRequest.game.login;

import com.home.commonData.data.login.PlayerSwitchGameDO;
import com.home.commonData.data.system.PlayerPrimaryKeyDO;
import com.home.shineData.data.BaseDO;
import com.home.shineData.support.MessageDontCopy;

/** 准备切换游戏服消息 */
@MessageDontCopy
public class PreSwitchGameMO
{
	/** 角色主键数据 */
	PlayerPrimaryKeyDO keyData;
	/** 角色列表数据 */
	BaseDO listData;
	/** 切换数据 */
	PlayerSwitchGameDO switchData;
}
