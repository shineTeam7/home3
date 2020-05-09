package com.home.commonData.data.social.chat;

import com.home.commonData.data.role.RoleSimpleShowDO;

/** 角色聊天数据 */
public class RoleChatDO
{
	/** 显示数据 */
	RoleSimpleShowDO showData;
	/** 聊天数据 */
	ChatDO chatData;
	/** 时间 */
	long time;
	/** 发送序号 */
	int sendIndex;
}
