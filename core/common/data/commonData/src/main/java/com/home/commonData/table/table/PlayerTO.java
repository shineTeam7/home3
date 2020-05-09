package com.home.commonData.table.table;

import com.home.shineData.data.DateDO;
import com.home.shineData.support.IndexKey;
import com.home.shineData.support.PrimaryKey;

/** 角色表 */
public class PlayerTO
{
	/** 角色id */
	@PrimaryKey
	long playerID;
	/** 名字 */
	@IndexKey("name")
	String name;
	/** userID */
	@IndexKey("userID_createAreaID")
	long userID;
	/** 原区ID */
	@IndexKey("userID_createAreaID")
	int createAreaID;
	/** uid */
	@IndexKey("uid")
	String uid;
	/** 平台 */
	String platform;
	/** app版本 */
	int appVersion;
	/** source版本 */
	int sourceVersion;
	/** 创建日期 */
	DateDO createDate;
	/** 上次保存时间 */
	DateDO saveDate;
	/** 登录数据 */
	byte[] loginData;
	/** 数据 */
	byte[] data;
	/** 离线事务数据 */
	byte[] offlineData;
}
