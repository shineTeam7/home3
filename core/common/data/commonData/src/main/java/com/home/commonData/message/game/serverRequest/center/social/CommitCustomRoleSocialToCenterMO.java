package com.home.commonData.message.game.serverRequest.center.social;

import com.home.commonData.data.social.RoleSocialDO;
import com.home.commonData.message.game.serverRequest.center.base.PlayerToCenterMO;

import java.util.Map;

/** 提交自定义社交数据到中心服 */
public class CommitCustomRoleSocialToCenterMO extends PlayerToCenterMO
{
	/** 数据组(key:funcID) */
	Map<Integer,RoleSocialDO> datas;
}
