package com.home.commonData.data.social;

import com.home.commonData.data.role.RoleShowDO;
import com.home.commonData.data.scene.scene.SceneLocationDO;
import com.home.shineData.support.MaybeNull;

/** 角色社交数据 */
public class RoleSocialDO
{
	/** 显示数据 */
	RoleShowDO showData;
	/** 是否在线 */
	boolean isOnline;
	/** 上次在线时间(如已在线则为-1) */
	long lastOnlineTime;
	/** 所在场景位置数据 */
	@MaybeNull
	SceneLocationDO location;
}
