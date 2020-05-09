package com.home.commonData.player.client;

import com.home.commonData.data.role.PetSaveDO;
import com.home.commonData.data.role.PetUseDO;
import com.home.shineData.support.MapKeyInValue;

import java.util.Map;

/** 宠物 */
public class PetCPO
{
	/** 宠物字典 */
	@MapKeyInValue("mIndex")
	Map<Integer,PetUseDO> petDic;
}
