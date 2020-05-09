using System;
using ShineEngine;

/// <summary>
/// 模块调用阶段类型
/// </summary>
public class PartCallPhaseType
{
	/** 无 */
	public const int None=0;
	/** 构造阶段 */
	public const int Construct=1;
	/** 初始化阶段 */
	public const int Init=2;
	/** 回收阶段 */
	public const int Dispose=3;
}