using System;
using ShineEngine;

/// <summary>
/// 玩家工会
/// </summary>
public class PlayerUnion:PlayerRoleGroup
{
	/** 创建公会简版数据 */
	protected override RoleGroupSimpleData toCreateRoleGroupSimpleData()
	{
		return new UnionSimpleData();
	}
}