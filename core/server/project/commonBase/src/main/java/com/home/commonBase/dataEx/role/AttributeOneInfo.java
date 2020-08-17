package com.home.commonBase.dataEx.role;

/** 单个配置数据 */
public class AttributeOneInfo
{
	/** 属性id */
	public int id;
	/** 公式类型 */
	public int[] formula;
	/** 是否为千分比属性 */
	public boolean isPercent;
	/** 自增属性ID */
	public int increaseID;
	/** 当前属性的上限值ID */
	public int currentMaxID;
	/** 当前属性默认空满 */
	public boolean isCurrentDefaultFull;
	/** 当前属性是否可超过上限 */
	public boolean isCurrentCanOverMax;
	/** 逻辑层是否派发 */
	public boolean needDispatchChange;
	/** 推送角色自己方式 */
	public int sendSelfType;
	/** 其他单位是否推送 */
	public boolean needSendOther;
	/** 简版单位是否需要 */
	public boolean isSimpleUnitNeed;
}
