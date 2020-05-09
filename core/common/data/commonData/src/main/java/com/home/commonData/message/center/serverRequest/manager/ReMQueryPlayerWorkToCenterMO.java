package com.home.commonData.message.center.serverRequest.manager;

import com.home.shineData.data.BaseDO;
import com.home.shineData.support.MaybeNull;

/** 查询事务到中心服 */
public class ReMQueryPlayerWorkToCenterMO
{
	int httpID;
	/** 返回数据 */
	@MaybeNull
	BaseDO data;
}
