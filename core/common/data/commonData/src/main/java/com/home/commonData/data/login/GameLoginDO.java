package com.home.commonData.data.login;

import com.home.commonData.data.activity.ActivityServerDO;
import com.home.commonData.data.func.FuncToolDO;
import com.home.commonData.data.social.rank.RankToolDO;
import com.home.shineData.support.MapKeyInValue;
import com.home.shineData.support.MaybeNull;
import com.home.shineData.support.OnlyS;

import java.util.Map;
import java.util.Set;

/** 游戏服登录中心服返回数据 */
@OnlyS
public class GameLoginDO
{
	/** 服务器偏移时间 */
	long serverOffTime;
	/** 插件数据组(key1:funcToolType,key2:funcID) */
	@MaybeNull
	Map<Integer,Map<Integer,FuncToolDO>> funcTools;
	/** 活动数据组 */
	@MapKeyInValue("id")
	Map<Integer,ActivityServerDO> activities;
	/** 服务器出生码 */
	int serverBornCode;
	/** 客户端gm指令组 */
	@MaybeNull
	Set<String> clientGMSet;
	/** 中心服运行序号 */
	int serverRunIndex;
}
