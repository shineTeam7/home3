package com.home.commonBase.scene.role;

import com.home.commonBase.scene.base.RoleLogicBase;

/** 角色势力数据 */
public class RoleForceLogic extends RoleLogicBase
{
	/** 势力 */
	public int force=-1;
	
	@Override
	public void init()
	{
		super.init();
		
		force=_data.force.force;
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		
		force=-1;
	}
}
