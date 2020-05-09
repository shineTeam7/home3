package com.home.commonData.message.game.response.login;

import com.home.commonData.data.login.CreatePlayerDO;
import com.home.commonData.message.game.response.base.LoginMO;
import com.home.shineData.support.ResponseBind;

/** 创建角色消息 */
@ResponseBind({})
public class CreatePlayerMO extends LoginMO
{
	/** 数据 */
	CreatePlayerDO data;
}
