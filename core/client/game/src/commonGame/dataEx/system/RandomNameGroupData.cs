using System;
using System.Text;
using ShineEngine;

/// <summary>
/// 随机名字组数据
/// </summary>
public class RandomNameGroupData
{
	/** 首名组 */
	public SList<string> firstNames=new SList<string>();
	/** 次名组 */
	public SList<string> secondNames=new SList<string>();

	/** 随机一个名字 */
	public String randomName()
	{
		StringBuilder sb=StringBuilderPool.create();

		if(!firstNames.isEmpty())
		{
			sb.Append(firstNames.get(MathUtils.randomInt(firstNames.size())));
		}

		if(!secondNames.isEmpty())
		{
			sb.Append(secondNames.get(MathUtils.randomInt(secondNames.size())));
		}

		return StringBuilderPool.releaseStr(sb);
	}
}