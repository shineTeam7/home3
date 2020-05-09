package com.home.commonBase.constlist.system;

/** 事务类型 */
public class WorkType
{
	/** 在线角色(仅角色在线才执行) */
	public static final int PlayerOnline=1;
	/** 离线角色(在线就立即执行,不在线就等下次上线前(或执行立即事务前)执行) */
	public static final int PlayerOffline=2;
	/** 立即角色(在线立即执行，不在线就从数据库中反序列化出来执行(并且之前会执行掉所有离线事务)) */
	public static final int PlayerAbs=3;
	/** 中心服事务 */
	public static final int Center=4;
	/** 逻辑服事务 */
	public static final int Game=5;
	/** user事务(login服) */
	public static final int User=6;
}
