package com.home.commonBase.constlist.system;

/** 场景驱动方式 */
public class SceneDriveType
{
	/** 服务器驱动多数(服务器权威,客户端的移动和技能动作先行)(通用的MMO) */
	public static final int ServerDriveMost=1;
	/** 服务器驱动伤害(服务器只处理伤害计算,客户端处理位置和伤害发生) */
	public static final int ServerDriveDamage=2;
	/** 客户端驱动(服务器不管,在服务器不会出现)(完全单人场景用)(新手引导) */
	public static final int ClientDriveAll=3;
	
	/** 是否客户端驱动攻击发生 */
	public static boolean isClientDriveAttackHappen(int type)
	{
		return type==ServerDriveDamage || type==ClientDriveAll;
	}
	
	/** 是否服务器驱动攻击发生 */
	public static boolean isServerDriveAttackHappen(int type)
	{
		return type!=ServerDriveDamage;
	}
}