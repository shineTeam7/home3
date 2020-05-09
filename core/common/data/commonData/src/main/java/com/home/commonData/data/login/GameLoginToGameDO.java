package com.home.commonData.data.login;

import com.home.commonData.data.social.roleGroup.RoleGroupSimpleDO;
import com.home.shineData.support.OnlyS;

import java.util.Map;

/** 逻辑服登陆到逻辑服消息(发过去) */
@OnlyS
public class GameLoginToGameDO
{
	/** 玩家群数据组(key1:funcID,key2:groupID) */
	Map<Integer,Map<Long,RoleGroupSimpleDO>> roleGroups;
	/** 登录限制 */
	boolean isLoginLimit;
}
