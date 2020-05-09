package com.home.commonData.data.system;

/** 角色主键数据 */
public final class PlayerPrimaryKeyDO
{
	/** 角色ID */
	long playerID;
	/** 名字 */
	String name;
	/** 用户ID */
	long userID;
	/** 用户uid */
	String uid;
	/** 平台 */
	String platform;
	/** 创建区ID(分区服用) */
	int createAreaID;
	/** 源游戏服ID(登录时所在) */
	int sourceGameID;
}