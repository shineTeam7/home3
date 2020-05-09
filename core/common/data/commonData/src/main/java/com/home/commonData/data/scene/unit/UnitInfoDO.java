package com.home.commonData.data.scene.unit;

import com.home.shineData.support.MaybeNull;

/** 单位信息数据 */
public class UnitInfoDO
{
	/** 身份数据 */
	UnitIdentityDO identity;
	/** 造型数据 */
	@MaybeNull
	UnitAvatarDO avatar;
}
