package com.home.commonData.message.center.serverRequest.game.func.social;

import com.home.commonData.data.social.RoleSocialDO;
import com.home.commonData.message.center.serverRequest.game.func.base.FuncPlayerToGameMO;

import java.util.List;

public class FuncReGetRandomPlayerListFromRoleSocialPoolToCenterMO extends FuncPlayerToGameMO
{
	/** 回复数据组 */
	List<RoleSocialDO> list;
	/** 获取参数(自行定义) */
	int arg;
}
