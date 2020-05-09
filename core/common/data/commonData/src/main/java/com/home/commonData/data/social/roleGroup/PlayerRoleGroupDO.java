package com.home.commonData.data.social.roleGroup;

import com.home.commonData.data.func.FuncToolDO;
import com.home.commonData.data.system.InfoLogDO;
import com.home.shineData.support.MapKeyInValue;
import com.home.shineData.support.MaybeNull;

import java.util.Map;
import java.util.Queue;

/** 角色玩家群数据(推送客户端的) */
public class PlayerRoleGroupDO
{
	/** 所在群id */
	long groupID;
	/** 等级 */
	int level;
	/** 群名 */
	String name;
	/** 公告 */
	String notice;
	/** 成员数 */
	int memberNum;
	/** 成员组 */
	@MapKeyInValue("playerID")
	Map<Long,PlayerRoleGroupMemberDO> members;
	/** 申请加入组(如有权限则有值) */
	@MaybeNull
	Map<Long,PlayerApplyRoleGroupDO> applyDic;
	/** 申请时是否可直接入群(无需同意) */
	boolean canApplyInAbs;
	/** 经验值 */
	long exp;
	/** 日志信息队列 */
	Queue<InfoLogDO> logQueue;
	
	/** 插件数据组(key1:funcToolType,key2:funcID) */
	@MaybeNull
	Map<Integer,Map<Integer,FuncToolDO>> funcTools;
}
