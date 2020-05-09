package com.home.commonData.data.social.chat;

import com.home.shineData.support.MaybeNull;

import java.util.List;

/** 聊天数据 */
public class ChatDO
{
	/** 类型 */
	int type;
	/** 文字 */
	String text;
	/** 聊天元素组 */
	@MaybeNull
	List<ChatElementDO> elements;
}
