package com.home.commonData.data.social.roleGroup.work;

import com.home.commonData.data.social.RoleSocialDO;
import com.home.commonData.data.social.roleGroup.CreateRoleGroupDO;
import com.home.commonData.data.system.AreaGlobalWorkDO;

/** 创建玩家群事务 */
public class CreateRoleGroupWDO extends AreaGlobalWorkDO
{
	/** 功能id */
	int funcID;
	/** 创建者社交数据 */
	RoleSocialDO data;
	/** 创建数据 */
	CreateRoleGroupDO createData;
}
