package com.home.commonBase.constlist.generate;

/** act动作状态类型 */
public class ActStateType
{
	/** 待机(地面自由) */
	public static final int Idle=1;
	
	/** 死亡(倒地) */
	public static final int Death=2;
	
	/** 走 */
	public static final int Walk=3;
	
	/** 跑 */
	public static final int Run=4;
	
	/** 跳跃 */
	public static final int Jump=5;
	
	/** 飞行 */
	public static final int Fly=6;
	
	/** 倒地 */
	public static final int Lie=7;
	
	/** 地面攻击 */
	public static final int Attack=8;
	
	/** 地面受击(僵直) */
	public static final int Hurt=9;
	
	/** 空中受击(僵直) */
	public static final int AirHurt=10;
	
	/** 浮空受击 */
	public static final int AirBlow=11;
	
	/** 空中攻击 */
	public static final int AirAttack=12;
	
	/** 倒地浮空 */
	public static final int LieBlow=13;
	
	/** 起身 */
	public static final int GetUp=14;
	
	/** 长度 */
	public static int size=15;
	
}
