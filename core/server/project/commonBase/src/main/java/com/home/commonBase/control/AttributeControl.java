package com.home.commonBase.control;

import com.home.commonBase.dataEx.role.AttributeCalculateInfo;
import com.home.commonBase.dataEx.role.StatusCalculateInfo;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.tool.AttributeTool;

/** 属性控制类 */
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
		attribute=new AttributeCalculateInfo()
		{
			@Override
			public int calculateAttribute(AttributeTool tool,int[] formula)
			{
				return BaseC.logic.calculateAttribute(tool,formula);
			}
		};
		
		status=new StatusCalculateInfo();
		
		roleAttribute=new AttributeCalculateInfo()
		{
			@Override
			public int calculateAttribute(AttributeTool tool,int[] formula)
			{
				return BaseC.logic.calculateAttribute(tool,formula);
			}
		};
	}
}
