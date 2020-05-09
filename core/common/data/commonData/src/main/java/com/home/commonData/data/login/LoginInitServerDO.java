package com.home.commonData.data.login;

import com.home.commonData.data.system.GameServerClientSimpleDO;
import com.home.commonData.data.system.GameServerSimpleInfoDO;
import com.home.commonData.data.system.ServerInfoDO;
import com.home.commonData.data.system.ServerSimpleInfoDO;
import com.home.shineData.support.MapKeyInValue;
import com.home.shineData.support.OnlyS;

import java.util.Map;

/** 登陆服初始化服务器数据 */
@OnlyS
public class LoginInitServerDO
{
	/** 本登录服信息 */
	ServerInfoDO info;
	/** 全部登陆服简版信息 */
	@MapKeyInValue("id")
	Map<Integer,ServerSimpleInfoDO> loginServerDic;
	/** 全部游戏服简版信息 */
	@MapKeyInValue("id")
	Map<Integer,GameServerSimpleInfoDO> gameServerDic;
	/** 游戏服组(客户端信息) */
	@MapKeyInValue("id")
	Map<Integer,GameServerClientSimpleDO> games;
	/** 客户端版本 */
	@MapKeyInValue("type")
	Map<Integer,ClientVersionDO> clientVersion;
	/** url重定向组 */
	Map<Integer,Map<Integer,String>> redirectURLDic;
	/** 当前是否开放 */
	boolean isOpen;
}
