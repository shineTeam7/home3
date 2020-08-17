using System;
using ShineEngine;

/// <summary>
/// 角色属性逻辑
/// </summary>
public class RoleAttributeDataLogic:AttributeTool
{
	/** 父属性逻辑 */
	private RoleAttributeLogic _parent;

	public void setParent(RoleAttributeLogic parent)
	{
		_parent=parent;

		setInfo(AttributeControl.roleAttribute);
		setIsM(true);
	}

	protected override void toDispatchAttribute(int[] changeList,int num,bool[] changeSet,int[] lastAttributes)
	{
		_parent.onAttributeChange(changeList,num,changeSet,lastAttributes);
	}
}