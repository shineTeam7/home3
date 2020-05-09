using ShineEngine;

/// <summary>
/// cd保留类型
/// </summary>
public class CDKeepType
{
	/// <summary>
	/// 一直存在,并存库(道具技能类,长CD类)
	/// </summary>
	public const int Always=1;
	
	/// <summary>
	/// 在线存在(下线删除)(道具技能类)
	/// </summary>
	public const int Online=2;
	
	/// <summary>
	/// 在主程类场景中存在(离开主城类场景删除)(中长时间CD类)
	/// </summary>
	public const int InTown=3;
	
	/// <summary>
	/// 在当前场景中存在(副本类)(离开当前类场景时删除)(当前场景战斗CD类)
	/// </summary>
	public const int InCurrentScene=4;
	
	/// <summary>
	/// 长度
	/// </summary>
	public static int size=5;
	
}
