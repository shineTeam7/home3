using System;
using ShineEngine;

/// <summary>
/// 场景mesh区域类型
/// </summary>
public class SceneNavMeshAreaType
{
	/** 不可走 */
	public const int NotWalkable=0;
	/** 可走 */
	public const int Walkable=1;
	/** 跳 */
	public const int Jump=2;
	/** 岸边 */
	public const int LandSide=3;
	/** 水 */
	public const int Water=4;
}