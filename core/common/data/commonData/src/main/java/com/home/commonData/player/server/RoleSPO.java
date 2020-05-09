package com.home.commonData.player.server;

/** 玩家模块 */
public class RoleSPO
{
	//防沉迷
	/** 是否成人 */
	boolean isAdult;
	//逻辑相关
	/** 性别 */
	int sex;
	/** 职业 */
	int vocation;
	/** 等级 */
	int level;
	/** 经验 */
	long exp;
	/** 货币组(货币) */
	long[] currencies;
	/** 累计获得货币组(货币),累计消耗通过累计增加减去当前货币 */
	long[] totalAddCurrencies;
}
