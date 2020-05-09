package com.home.commonCenter.part.centerGlobal.base;

import com.home.commonBase.baseData.BasePart;
import com.home.commonBase.data.login.GameLoginData;
import com.home.commonBase.data.login.GameLoginToCenterData;
import com.home.commonCenter.part.centerGlobal.CenterGlobal;

/** 中心服global基类 */
public abstract class CenterGlobalBasePart extends BasePart
{
	/** global自己 */
	public CenterGlobal me;
	
	/** 设置主 */
	public void setMe(CenterGlobal value)
	{
		me=value;
	}
	
	/** 开始启动时 */
	public void onStart()
	{
	
	}
	
	/** 游戏服登录 */
	public void makeGameLoginData(int gameID,GameLoginData data)
	{
	
	}
	
	/** 游戏服登录数据到达 */
	public void readGameLoginData(int gameID,GameLoginToCenterData data)
	{
	
	}
	
	/** 角色删除(主线程) */
	public void onPlayerDelete(long playerID)
	{
	
	}
	
	/** 玩家群删除(主线程) */
	public void onRoleGroupDelete(int funcID,long groupID)
	{
	
	}
}
