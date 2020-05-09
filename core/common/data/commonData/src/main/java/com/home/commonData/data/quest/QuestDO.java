package com.home.commonData.data.quest;

/** 任务数据(已接) */
public class QuestDO
{
	/** id */
	int id;
	/** 任务目标组 */
	TaskDO[] tasks;
	/** 是否失败 */
	boolean isFailed;
	/** 有效时间(0为无限) */
	long enableTime;
}
