using System;
using ShineEngine;

/// <summary>
/// 角色存储版本控制
/// </summary>
[Hotfix]
public class PlayerVersionControl:BaseVersionControl<Player>
{
	public virtual void init()
	{

	}

	protected override SaveVersionData getVersionData(Player me)
	{
		return me.system.getPartData().version;
	}
}