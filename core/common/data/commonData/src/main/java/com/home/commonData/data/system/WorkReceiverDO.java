package com.home.commonData.data.system;

import java.util.List;
import java.util.Map;

/** 事务接收者数据 */
public final class WorkReceiverDO
{
	/** 事务执行记录(key:发起者序号->key:instanceID,value:时间戳) */
	Map<Integer,Map<Long,Long>> workExecuteRecordDic;
}
