using System;
using ShineEngine;

/// <summary>
/// 场景角色
/// </summary>
public class Role:SceneObject
{
	/** 角色ID */
	public long playerID;

	/** 场景角色数据 */
	protected SceneRoleData _data;
	
	/** 属性逻辑 */
	public RoleAttributeLogic attribute;

	/** 势力逻辑 */
	public RoleForceLogic force;

	/** 建造逻辑 */
	public RoleBuildLogic build;

	/** 设置数据 */
	public void setData(SceneRoleData data)
	{
		_data=data;
		playerID=data!=null ? data.playerID : -1;
	}

	/** 获取数据 */
	public SceneRoleData getData()
	{
		return _data;
	}

	protected override void registLogics()
	{
		addLogic(attribute=createRoleAttributeLogic());
		addLogic(force=createRoleForceLogic());
		addLogic(build=createRoleBuildLogic());
	}

	protected virtual RoleAttributeLogic createRoleAttributeLogic()
	{
		return new RoleAttributeLogic();
	}

	protected virtual RoleForceLogic createRoleForceLogic()
	{
		return new RoleForceLogic();
	}

	protected virtual RoleBuildLogic createRoleBuildLogic()
	{
		return new RoleBuildLogic();
	}

	/** 是否是自己 */
	public bool isSelf()
	{
		return playerID==GameC.player.role.playerID;
	}
}