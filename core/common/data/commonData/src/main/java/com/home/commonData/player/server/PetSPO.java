package com.home.commonData.player.server;

import com.home.commonData.data.role.PetSaveDO;
import com.home.shineData.support.MapKeyInValue;

import java.util.Map;

/** 宠物 */
public class PetSPO
{
	/** 宠物字典 */
	@MapKeyInValue("mIndex")
	Map<Integer,PetSaveDO> petDic;
}
