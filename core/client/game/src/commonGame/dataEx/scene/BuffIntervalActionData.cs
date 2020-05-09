using System;
using ShineEngine;

/// <summary>
/// buff间隔攻击数据
/// </summary>
public class BuffIntervalActionData:PoolObject
{
	/** 添加者实例ID */
	public int adderInstanceID=-1;
	/** 时间经过(ms) */
	public int timePass;
	/** 间隔 */
	public int delay;

	/** 类型(见BuffIntervalActionType) */
	public int type;

	/** 键 */
	public int key;
	/** 值 */
	public int value;

	/** 自身攻击值组(对应levelConfig的damages) */
	public int[] selfAttackValues;
	/** 是否记录了数值 */
	public bool isRecorded=false;

	/** 从配置读取 */
	public void readFromConfig(int[] args)
	{
		delay=args[1];
		key=args[2];
		value=args[3];
		timePass=0;
	}

	/** 计算自身攻击值 */
	public void calculateSelfAttackValue(UnitFightDataLogic attackerLogic)
	{
		isRecorded=true;

		AttackLevelConfig levelConfig=AttackLevelConfig.get(key,value);

		int aLen=levelConfig.varNumT;

		if(selfAttackValues==null || selfAttackValues.Length<aLen)
		{
			selfAttackValues=new int[aLen];
		}

		int i=0;
		int[] values=selfAttackValues;

		foreach(SkillVarConfig v in levelConfig.varConfigT)
		{
			foreach(int[] v2 in v.args)
			{
				values[i++]=BaseGameUtils.calculateOneSkillVarValue(v2,attackerLogic,null);
			}
		}
	}

	/** 设置攻击数据 */
	public void setAttackData(AttackData data)
	{
		if(isRecorded)
		{
			data.isRecorded=true;
			data.selfAttackValues=selfAttackValues;
		}
	}

	public override void clear()
	{
		adderInstanceID=-1;
		isRecorded=false;
	}
}