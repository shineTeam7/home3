using System;
using ShineEngine;

/// <summary>
/// 玩家工会工具
/// </summary>
public class PlayerUnionTool:PlayerRoleGroupTool
{
	public PlayerUnionTool():base(FunctionType.Union,Global.unionRoleGroupID)
	{

	}

	/** 创建角色自身玩家群 */
	protected override PlayerRoleGroup toCreatePlayerRoleGroup()
	{
		return new PlayerUnion();
	}
}