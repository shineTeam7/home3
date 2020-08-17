using ShineEngine;

/// <summary>
/// 角色部件基础
/// </summary>
[Hotfix(needFactory = false)]
public abstract class PlayerBasePart:BasePart
{
	/// <summary>
	/// 角色自身
	/// </summary>
	public Player me;

	/// <summary>
	/// 设置主
	/// </summary>
	public virtual void setMe(Player player)
	{
		me=player;
	}

	/** 新创建时(为兼容服务器基类的该方法) */
	public override void onNewCreate()
	{

	}

	/// <summary>
	/// 功能开启
	/// </summary>
	public abstract void onFunctionOpen(int id);

	/// <summary>
	/// 功能关闭
	/// </summary>
	public abstract void onFunctionClose(int id);

	/// <summary>
	/// 活动开启
	/// </summary>
	public virtual void onActivityOpen(int id,bool atTime)
	{

	}

	/// <summary>
	/// 活动关闭
	/// </summary>
	public virtual void onActivityClose(int id,bool atTime)
	{

	}

	/// <summary>
	/// 活动重置
	/// </summary>
	public virtual void onActivityReset(int id,bool atTime)
	{

	}

	/** 升级 */
	public virtual void onLevelUp(int oldLevel)
	{

	}
}