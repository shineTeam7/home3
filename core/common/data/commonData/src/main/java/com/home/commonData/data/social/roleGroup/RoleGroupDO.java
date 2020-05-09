package com.home.commonData.data.social.roleGroup;

import com.home.commonData.data.func.FuncToolDO;
import com.home.commonData.data.system.InfoLogDO;
import com.home.shineData.support.MapKeyInValue;
import com.home.shineData.support.MaybeNull;

import java.util.Map;
import java.util.Queue;

/** 玩家群数据(单个群) */
public class RoleGroupDO
{
	/** 群id */
	long groupID;
	/** 等级 */
	int level;
	/** 成员组 */
	@MapKeyInValue("playerID")
	Map<Long,RoleGroupMemberDO> members;
	/** 群名 */
	String name;
	/** 公告 */
	String notice;
	/** 申请加入组 */
	Map<Long,PlayerApplyRoleGroupDO> applyDic;
	/** 申请时是否可直接入群(无需同意) */
	boolean canApplyInAbs;
	/** 经验值 */
	long exp;
	/** 日志信息队列 */
	Queue<InfoLogDO> logQueue;
	/** 下一个0点时刻 */
	long nextDailyTime;
	
	/** 插件数据组(key1:funcToolType,key2:funcID) */
	@MaybeNull
	Map<Integer,Map<Integer,FuncToolDO>> funcTools;
}
