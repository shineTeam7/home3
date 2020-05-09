package com.home.commonData.data.system;

import java.util.Map;

/** 事务发起者数据 */
public final class WorkSenderDO
{
	/** 事务分配序号(自增) */
	long workInstanceID;
	/** 已发送事务记录字典 */
	Map<Long,WorkDO> workRecordDic;
}
