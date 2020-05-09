package com.home.commonData.gameGlobal.server;

import com.home.commonData.data.func.FuncToolDO;
import com.home.shineData.support.MaybeNull;

import java.util.Map;

/** 通用功能数据组 */
public class GameFuncSPO
{
	/** 插件数据组(key1:funcToolType,key2:funcID) */
	@MaybeNull
	Map<Integer,Map<Integer,FuncToolDO>> funcTools;
	/** roleGroupID自增序号组(key:createAreaID原区ID) */
	Map<Integer,Integer> roleGroupIndexDic;
}
