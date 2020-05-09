using System;
using ShineEngine;

/// <summary>
/// 游戏服分服方式
/// </summary>
public class GameAreaDivideType
{
	/** 分区分服(大区小区都分)(有合服,每个game服单独数据库) */
	public const int Split=1;
	/** 自动绑定game服(有合服,每个game服单独数据库)(只是由center自动挑选进入game) */
	public const int AutoBindGame=2;
	/** 自动进入game服(有合服,所有game服数据库实例可唯一，也可分片)(player所在信息在pivot上) */
	public const int AutoEnterGame=3;
}