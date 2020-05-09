

/// <summary>
/// 玩家队伍
/// </summary>
public class PlayerTeam:PlayerRoleGroup
{
	/** 创建队伍简版数据 */
	protected override RoleGroupSimpleData toCreateRoleGroupSimpleData()
	{
		return new TeamSimpleData();
	}
}