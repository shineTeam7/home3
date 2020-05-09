package com.home.commonData.data.system;

import com.home.shineData.support.OnlyS;

/** 基础事务数据(用dataID区分) */
@OnlyS
public class WorkDO
{
	/** 时间戳 */
	long timestamp;
	/** 实例ID */
	long workInstanceID;
	/** 事务类型(见WorkType)(离线,在线,立即) */
	int workType;
	/** 发起者索引(type+id) */
	int senderIndex;
	/** 发送者携带ID(做其他业务回执用) */
	int senderCarryID;
}
