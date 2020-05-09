using System;
using ShineEngine;

/// <summary>
/// 玩家群事件
/// </summary>
public class RoleGroupEvt:FuncEvt
{
	/** 群id */
	public long groupID=-1;
	/** 目标id */
	public long targetID=-1;
	/** inout类型 */
	public int type;
	/** 结果(invite/apply) */
	public int result;

	/** 旧职位 */
	public int oldTitle;
	/** 新职位 */
	public int newTitle;

	public PlayerApplyRoleGroupData applyData;

	public RoleGroupChangeData changeData;

	public RoleGroupMemberChangeData memberChangeData;

	public RoleShowData roleShowData;

	/** 不清funcID */
	public override void clear()
	{
		groupID=-1;
		targetID=-1;
		type=0;
		result=0;
		oldTitle=0;
		newTitle=0;
		applyData=null;
		changeData=null;
		roleShowData=null;
	}
}