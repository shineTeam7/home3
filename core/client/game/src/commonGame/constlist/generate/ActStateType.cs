using ShineEngine;

/// <summary>
/// act动作状态类型
/// </summary>
public class ActStateType
{
	/// <summary>
	/// 待机(地面自由)
	/// </summary>
	public const int Idle=1;
	
	/// <summary>
	/// 死亡(倒地)
	/// </summary>
	public const int Death=2;
	
	/// <summary>
	/// 走
	/// </summary>
	public const int Walk=3;
	
	/// <summary>
	/// 跑
	/// </summary>
	public const int Run=4;
	
	/// <summary>
	/// 跳跃
	/// </summary>
	public const int Jump=5;
	
	/// <summary>
	/// 飞行
	/// </summary>
	public const int Fly=6;
	
	/// <summary>
	/// 倒地
	/// </summary>
	public const int Lie=7;
	
	/// <summary>
	/// 地面攻击
	/// </summary>
	public const int Attack=8;
	
	/// <summary>
	/// 地面受击(僵直)
	/// </summary>
	public const int Hurt=9;
	
	/// <summary>
	/// 空中受击(僵直)
	/// </summary>
	public const int AirHurt=10;
	
	/// <summary>
	/// 浮空受击
	/// </summary>
	public const int AirBlow=11;
	
	/// <summary>
	/// 空中攻击
	/// </summary>
	public const int AirAttack=12;
	
	/// <summary>
	/// 倒地浮空
	/// </summary>
	public const int LieBlow=13;
	
	/// <summary>
	/// 起身
	/// </summary>
	public const int GetUp=14;
	
	/// <summary>
	/// 长度
	/// </summary>
	public static int size=15;
	
}
