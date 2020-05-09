using System;
using ShineEngine;

/// <summary>
/// 奖励显示数据
/// </summary>
[Hotfix]
public class RewardShowData
{
	/** 方式 */
	public int way;
	/** 奖励配置 */
	public RewardConfig config;
	/** 奖励物品组 */
	public SList<ItemData> items;
	/** 奖励货币组 */
	public IntIntMap currency;
}