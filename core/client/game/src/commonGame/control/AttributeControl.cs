using System;
using ShineEngine;

/// <summary>
/// 属性控制
/// </summary>
public class AttributeControl
{
	/** 单位属性 */
	public static AttributeCalculateInfo attribute;
	/** 单位状态 */
	public static StatusCalculateInfo status;

	/** 角色属性 */
	public static AttributeCalculateInfo roleAttribute;

	/** 初始化 */
	public static void init()
	{
		attribute=new AttributeCalculateInfo();
		attribute.formulaFunc=BaseC.logic.calculateAttribute;

		status=new StatusCalculateInfo();

		roleAttribute=new AttributeCalculateInfo();
		roleAttribute.formulaFunc=BaseC.logic.calculateAttribute;
	}
}