
/// <summary>
/// 玩家队伍工具
/// </summary>
public class PlayerTeamTool:PlayerRoleGroupTool
{
	public PlayerTeamTool():base(FunctionType.Team,Global.teamRoleGroupID)
	{

	}

	/** 创建角色队伍 */
	protected override PlayerRoleGroup toCreatePlayerRoleGroup()
	{
		return new PlayerTeam();
	}
}