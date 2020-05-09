package com.home.commonData.data.social.roleGroup;

import com.home.commonData.data.func.FuncToolDO;
import com.home.shineData.support.MapKeyInValue;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/** 玩家身上的玩家群工具数据 */
public class PlayerRoleGroupToolDO extends FuncToolDO
{
	/** 群数据组 */
	@MapKeyInValue("groupID")
	Map<Long,PlayerRoleGroupSaveDO> groups;
	/** 操作中占位数(废弃) */
	@Deprecated
	int operateNum;
	/** 申请记录字典 */
	@MapKeyInValue("data.groupID")
	Map<Long,PlayerApplyRoleGroupSelfDO> applyDic;
	/** 被邀请时是否可直接入群(无需同意) */
	boolean canInviteInAbs;
	/** 创建玩家群操作计数 */
	Queue<Long> createOperateNums;
	/** 被邀请记录字典 */
	List<InviteRoleGroupReceiveDO> inviteList;
}
