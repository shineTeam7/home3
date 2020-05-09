package com.home.commonBase.logic.role;

import com.home.commonBase.control.AttributeControl;
import com.home.commonBase.scene.role.RoleAttributeLogic;
import com.home.commonBase.tool.AttributeTool;
import com.home.shine.support.collection.IntIntMap;

/** 角色属性数据逻辑 */
public class RoleAttributeDataLogic extends AttributeTool
{
	/** 父属性逻辑 */
	private RoleAttributeLogic _parent;
	
	public RoleAttributeDataLogic(RoleAttributeLogic parent)
	{
		_parent=parent;
		
		setInfo(AttributeControl.roleAttribute);
		setIsM(true);
	}
	
	@Override
	protected void toSendSelf(IntIntMap dic)
	{
		_parent.sendSelfAttribute(dic);
	}
	
	@Override
	protected void toSendOther(IntIntMap dic)
	{
		_parent.sendOtherAttribute(dic);
	}
	
	@Override
	protected void toDispatchAttribute(int[] changeList,int num,boolean[] changeSet,int[] lastAttributes)
	{
		//先buff
		//_parent.buff.onAttributeChange(changeList,num,changeSet,lastAttributes);
		_parent.onAttributeChange(changeList,num,changeSet,lastAttributes);
	}
}
