package com.home.commonData.data.login;

import com.home.commonData.data.system.GameServerInfoDO;
import com.home.commonData.data.system.GameServerSimpleInfoDO;
import com.home.commonData.data.system.ServerSimpleInfoDO;
import com.home.shineData.support.MapKeyInValue;
import com.home.shineData.support.OnlyS;

import java.util.Map;

/** 逻辑服初始化服务器数据 */
@OnlyS
public class GameInitServerDO
{
	/** 服务器信息(自身) */
	GameServerInfoDO info;
	/** 服务器信息(中心服) */
	ServerSimpleInfoDO centerInfo;
	/** 全部游戏服简版信息 */
	@MapKeyInValue("id")
	Map<Integer,GameServerSimpleInfoDO> gameServerDic;
	/** 全部场景服简版信息 */
	@MapKeyInValue("id")
	Map<Integer,ServerSimpleInfoDO> sceneServerDic;
	/** 登陆服id组 */
	int[] loginList;
	/** 客户端版本 */
	@MapKeyInValue("type")
	Map<Integer,ClientVersionDO> clientVersion;
	/** 是否正式版本(无gm) */
	boolean isOfficial;
}
