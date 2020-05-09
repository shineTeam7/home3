package com.home.commonData.centerGlobal.server;

import com.home.commonData.data.func.FuncToolDO;
import com.home.shineData.support.MaybeNull;

import java.util.Map;

/** 中心服功能数据 */
public class CenterFuncSPO
{
	/** 插件数据组(key1:funcToolType,key2:funcID) */
	@MaybeNull
	Map<Integer,Map<Integer,FuncToolDO>> funcTools;
}
