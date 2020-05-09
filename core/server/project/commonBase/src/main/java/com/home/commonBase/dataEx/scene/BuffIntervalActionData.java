package com.home.commonBase.dataEx.scene;

import com.home.commonBase.config.game.AttackLevelConfig;
import com.home.commonBase.config.game.SkillVarConfig;
import com.home.commonBase.logic.unit.UnitFightDataLogic;
import com.home.commonBase.utils.BaseGameUtils;
import com.home.shine.support.pool.PoolObject;

/** buff间隔行为数据 */
public class BuffIntervalActionData extends PoolObject
{
	/** 添加者实例ID(-1为自己) */
	public int adderInstanceID=-1;
	
	/** 类型(见BuffIntervalActionType) */
	public int type;
	
	/** 时间经过(ms) */
	public int timePass;
	/** 间隔 */
	public int delay;
	/** 键 */
	public int key;
	/** 值 */
	public int value;
	
	/** 自身攻击值组(对应levelConfig的damages) */
	public int[] selfAttackValues;
	/** 是否记录了数值 */
	public boolean isRecorded=false;
	
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
		
		if(selfAttackValues==null || selfAttackValues.length<aLen)
		{
			selfAttackValues=new int[aLen];
		}
		
		int i=0;
		int[] values=selfAttackValues;
		
		for(SkillVarConfig v:levelConfig.varConfigT)
		{
			for(int[] v2:v.args)
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
	
	@Override
	public void clear()
	{
		adderInstanceID=-1;
		isRecorded=false;
	}
}
