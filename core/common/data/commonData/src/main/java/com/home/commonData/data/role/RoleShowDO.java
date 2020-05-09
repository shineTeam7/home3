package com.home.commonData.data.role;

/** 玩家展示数据 */
public class RoleShowDO
{
	/** 角色ID */
	long playerID;
	/** 创建区服 */
	int createAreaID;
	/** 名字 */
	String name;
	
	/** 性别 */
	int sex;
	/** 职业 */
	int vocation;
	/** 玩家等级(不是角色等级) */
	int level;
	/** 战斗力 */
	long fightForce;
	
	/** 工会id */
	long unionID;
	/** 工会名 */
	String unionName;
}
