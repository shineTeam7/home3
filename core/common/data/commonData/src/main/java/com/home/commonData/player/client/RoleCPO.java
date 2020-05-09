package com.home.commonData.player.client;

public class RoleCPO
{
	/** 角色ID */
	long playerID;
	/** 名字 */
	String name;
	/** 用户ID */
	long userID;
	/** 用户UID */
	String uid;
	/** 原区ID */
	int createAreaID;
	/** 平台 */
	String platform;

	
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
	/** 货币上限组 */
	long[] currenciesMax;
	/** 累计获得货币组(货币),累计消耗通过累计增加减去当前货币 */
	long[] totalAddCurrencies;
	/** 当前战斗力 */
	long fightForce;
}
