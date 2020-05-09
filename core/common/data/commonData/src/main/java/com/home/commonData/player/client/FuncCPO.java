package com.home.commonData.player.client;

import com.home.commonData.data.func.FuncToolDO;
import com.home.shineData.support.MaybeNull;

import java.util.Map;
import java.util.Set;

/** 通用功能数据 */
public class FuncCPO
{
	/** 插件数据组(key1:funcToolType,key2:funcID) */
	@MaybeNull
	Map<Integer,Map<Integer,FuncToolDO>> funcTools;
	/** 功能开启组 */
	Set<Integer> funcOpenSet;
}
