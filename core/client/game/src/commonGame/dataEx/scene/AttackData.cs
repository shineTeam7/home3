using System;
using ShineEngine;

/// <summary>
/// 攻击数据
/// </summary>
public class AttackData:PoolObject
{
	/** 攻击配置 */
	public AttackConfig config;

	/** 攻击等级配置 */
	public AttackLevelConfig levelConfig;

	/** 目标数据 */
	public SkillTargetData targetData;

	/** 释放者 */
	public int fromInstanceID;
	/** 是否第一个被子弹打击到的单位 */
	public bool isBulletFirstHit=false;

	/** 自身攻击值组(对应levelConfig的damages) */
	public int[] selfAttackValues;
	/** 是否记录了数值 */
	public bool isRecorded=false;

	/** 命中值 */
	public int hitRate;
	/** 暴击值 */
	public int crit;

	/** 清空 */
	public override void clear()
	{
		config=null;
		levelConfig=null;
		targetData=null;
		fromInstanceID=-1;
		isBulletFirstHit=false;
		isRecorded=false;
	}
}