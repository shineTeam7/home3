package com.home.commonBase.scene.base;

import com.home.commonBase.data.scene.role.SceneRoleData;

/** 场景玩家逻辑基类 */
public class RoleLogicBase extends SceneObjectLogicBase
{
	/** 角色 */
	protected Role _role;
	/** 数据 */
	protected SceneRoleData _data;
	
	@Override
	public void setObject(SceneObject obj)
	{
		super.setObject(obj);
		
		_role=(Role)obj;
	}
	
	@Override
	public void init()
	{
		super.init();
		_data=_role.getData();
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		_data=null;
	}
}
