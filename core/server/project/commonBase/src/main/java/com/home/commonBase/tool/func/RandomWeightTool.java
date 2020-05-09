package com.home.commonBase.tool.func;

import com.home.commonBase.constlist.generate.ClientRandomSeedType;
import com.home.commonBase.logic.LogicEntity;
import com.home.shine.data.DIntData;
import com.home.shine.utils.MathUtils;

/** 权重随机插件 */
public class RandomWeightTool
{
	private DIntData[] _dintArr;
	private int[][] _intArr;
	
	private int _weight;
	
	/** (key:权重,value:值) */
	public RandomWeightTool(DIntData[] arr)
	{
		_dintArr=arr;
		
		for(DIntData v:arr)
		{
			_weight+=v.key;
		}
	}
	
	/** arr[0]:权重 */
	public RandomWeightTool(int[][] arr)
	{
		_intArr=arr;
		
		for(int[] v:arr)
		{
			_weight+=v[0];
		}
	}
	
	/** 权重随机一个(dint)(重置随机) */
	public int randomForDInt()
	{
		DIntData[] dintArr;
		
		if((dintArr=_dintArr).length==0)
			return -1;
		
		int rd=MathUtils.randomInt(_weight);
		
		int index=0;
		
		while(rd>=dintArr[index].key)
		{
			rd-=dintArr[index++].key;
		}
		
		return dintArr[index].value;
	}
	
	/** 权重随机一个(intArr)(重置随机) */
	public int[] randomForIntArr(LogicEntity entity,int seedType)
	{
		int[][] intArr;
		
		if((intArr=_intArr).length==0)
			return null;

		int rd=0;

		if (seedType == ClientRandomSeedType.Server)
			rd=MathUtils.randomInt(_weight);
		else
			rd=entity.randomInt(_weight);
		
		int index=0;
		
		while(rd>=intArr[index][0])
		{
			rd-=intArr[index++][0];
		}
		
		return intArr[index];
	}
	/** 权重随机一个(索引)(重置随机) */
	public int randomIndexForIntArr(LogicEntity entity)
	{
		int[][] intArr;
		
		if((intArr=_intArr).length==0)
			return -1;
		
		int rd=entity.randomInt(_weight);
		
		int index=0;
		
		while(rd>=intArr[index][0])
		{
			rd-=intArr[index++][0];
		}
		
		return index;
	}
}
