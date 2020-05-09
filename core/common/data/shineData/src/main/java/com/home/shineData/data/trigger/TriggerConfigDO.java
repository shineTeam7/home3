package com.home.shineData.data.trigger;

/** 单个trigger数据 */
public class TriggerConfigDO
{
	/** id */
	int id;
	/** 名字 */
	String name;
	/** 组类型 */
	int groupType;
	/** 组ID */
	int groupID;
	/** 是否开启 */
	boolean isOpen;
	/** 事件优先级(默认0) */
	int priority;
	/** 事件组 */
	TriggerFuncDO[] events;
	/** 环境组 */
	TriggerFuncDO[] conditions;
	/** 动作组 */
	TriggerFuncDO[] actions;
}
