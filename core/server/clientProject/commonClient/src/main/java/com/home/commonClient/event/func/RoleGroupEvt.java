package com.home.commonClient.event.func;

import com.home.commonBase.data.role.RoleShowData;
import com.home.commonBase.data.social.roleGroup.PlayerApplyRoleGroupData;
import com.home.commonBase.data.social.roleGroup.RoleGroupChangeData;

public class RoleGroupEvt extends FuncEvt
{
	/** 群id */
	public long groupID=-1;
	/** 目标id */
	public long targetID=-1;
	/** inout类型 */
	public int type;
	/** 结果(invite/apply) */
	public int result;
	
	/** 旧职位 */
	public int oldTitle;
	/** 新职位 */
	public int newTitle;
	
	public PlayerApplyRoleGroupData applyData;
	
	public RoleGroupChangeData changeData;
	
	public RoleShowData roleShowData;
	
	/** 不清funcID */
	@Override
	public void clear()
	{
		groupID=-1;
		targetID=-1;
		type=0;
		result=0;
		oldTitle=0;
		newTitle=0;
		applyData=null;
		changeData=null;
		roleShowData=null;
	}
}
