using System;
using ShineEngine;

/// <summary>
/// 单个配置数据
/// </summary>
public class AttributeOneInfo
{
	/** 属性id */
	public int id;
	/** 公式类型 */
	public int[] formula;
	/** 是否为千分比属性 */
	public bool isPercent;
	/** 自增属性ID */
	public int increaseID;
	/** 当前属性的上限值ID */
	public int currentMaxID;
	/** 当前属性默认空满 */
	public bool isCurrentDefaultFull;
	/** 当前属性是否可超过上限 */
	public bool isCurrentCanOverMax;
	/** 推送角色自己方式 */
	public int sendSelfType;
	/** 是否简版单位所需 */
	public bool isSimpleUnitNeed;
}