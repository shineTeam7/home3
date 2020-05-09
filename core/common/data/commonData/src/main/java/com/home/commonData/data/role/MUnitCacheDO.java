package com.home.commonData.data.role;

import com.home.commonData.data.scene.base.BuffDO;
import com.home.commonData.data.scene.base.CDDO;
import com.home.shineData.support.MapKeyInValue;

import java.util.Map;

/** 主单位缓存数据 */
public class MUnitCacheDO
{
	/** 当前属性组 */
	Map<Integer,Integer> currentAttributes;
	/** 保存buff组 */
	@MapKeyInValue("instanceID")
	Map<Integer,BuffDO> buffs;
	/** 保存CD组 */
	@MapKeyInValue("id")
	Map<Integer,CDDO> cds;
	/** 缓存的时刻 */
	long cacheTime;
}
