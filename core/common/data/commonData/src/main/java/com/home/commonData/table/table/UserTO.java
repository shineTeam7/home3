package com.home.commonData.table.table;

import com.home.shineData.support.IndexKey;
import com.home.shineData.support.PrimaryKey;

/** 用户表 */
public class UserTO
{
	/** 用户ID */
	@PrimaryKey
	long userID;
	/** platform_uid */
	@IndexKey("puid")
	String puid;
	/** 平台类型 */
	String platform;
	/** 是否成人 */
	boolean isAdult;
	/** 绑定(上次选择)区服ID */
	int areaID;
	/** 拥有角色ID组(逗号分隔) */
	String playerIDs;
	/** 指向源userID(一账号多平台用) */
	long sourceUserID;
}
