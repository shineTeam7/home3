package com.home.commonData.data.login;

import com.home.commonData.data.system.AreaClientDO;
import com.home.shineData.support.MapKeyInValue;
import com.home.shineData.support.MaybeNull;

import java.util.Map;

/** 客户端登陆结果数据 */
public class ClientLoginResultDO
{
	/** 登录服指定信息 */
	ClientLoginServerInfoDO loginInfo;
	/** 版本数据(空为服务器并无此版本) */
	@MaybeNull
	ClientVersionDO version;
	/** 区服列表 */
	@MaybeNull
	@MapKeyInValue("areaID")
	Map<Integer,AreaClientDO> areas;
	/** 上次登陆区服 */
	int lastAreaID;
	/** 逻辑服地址(直接登陆模式用) */
	@MaybeNull
	ClientLoginServerInfoDO gameInfo;
}
