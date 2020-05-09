package com.home.commonBase.dataEx.system;

import com.home.shine.support.collection.SList;
import com.home.shine.support.pool.StringBuilderPool;
import com.home.shine.utils.MathUtils;

/** 随机名字数据 */
public class RandomNameGroupData
{
	/** 首名组 */
	public SList<String> firstNames=new SList<>(String[]::new);
	/** 次名组 */
	public SList<String> secondNames=new SList<>(String[]::new);
	
	/** 随机一个名字 */
	public String randomName()
	{
		StringBuilder sb=StringBuilderPool.create();
		
		if(!firstNames.isEmpty())
		{
			sb.append(firstNames.get(MathUtils.randomInt(firstNames.size())));
		}
		
		if(!secondNames.isEmpty())
		{
			sb.append(secondNames.get(MathUtils.randomInt(secondNames.size())));
		}
		
		return StringBuilderPool.releaseStr(sb);
	}
}
