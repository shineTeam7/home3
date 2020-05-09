package com.home.commonData.message.manager.httpResponseResult.role;

import com.home.commonData.data.system.PlayerPrimaryKeyDO;
import com.home.commonData.data.system.PlayerWorkListDO;
import com.home.shineData.data.BaseDO;
import com.home.shineData.support.MaybeNull;

public class MQueryPlayerRO
{
	/** key数据 */
	@MaybeNull
	PlayerPrimaryKeyDO key;
	/** 主数据 */
	@MaybeNull
	BaseDO data;
	/** 离线事务数据 */
	@MaybeNull
	PlayerWorkListDO workList;
}
