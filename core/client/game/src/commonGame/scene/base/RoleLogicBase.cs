using System;
using ShineEngine;

/// <summary>
/// 场景角色逻辑
/// </summary>
public class RoleLogicBase:SceneObjectLogicBase
{
	/** 角色 */
	protected Role _role;
	/** 数据 */
	protected SceneRoleData _data;

	public override void setObject(SceneObject obj)
	{
		base.setObject(obj);

		_role=(Role)obj;
	}

	public override void init()
	{
		base.init();
		_data=_role.getData();
	}

	public override void dispose()
	{
		base.dispose();
		_data=null;
	}
}