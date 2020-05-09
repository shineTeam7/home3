package com.home.commonData.player.server;

import com.home.commonData.data.func.FuncToolDO;
import com.home.shineData.support.MaybeNull;

import java.util.Map;
import java.util.Set;

/** 通用功能数据 */
public class FuncSPO
{
	/** 插件数据组(key1:funcToolType,key2:funcID) */
	@MaybeNull
	Map<Integer,Map<Integer,FuncToolDO>> funcTools;
	/** 功能开启组 */
	Set<Integer> funcOpenSet;
}
