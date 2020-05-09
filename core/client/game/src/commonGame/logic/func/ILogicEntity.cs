using System;
using ShineEngine;

/// <summary>
/// 逻辑主体
/// </summary>
public interface ILogicEntity:ITimeEntity
{
	/** 随机一个整形 */
	int randomInt(int range);

	/** 判定几率 */
	bool randomProb(int prob,int max);

	/** 随一整形(start<=value<end) */
	int randomRange(int start,int end);

	/** 随一整形(start<=value<=end)(包括结尾) */
	int randomRange2(int start,int end);
}