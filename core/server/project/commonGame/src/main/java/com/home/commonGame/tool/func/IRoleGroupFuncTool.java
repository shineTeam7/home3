package com.home.commonGame.tool.func;

import com.home.commonGame.logic.func.RoleGroup;

public interface IRoleGroupFuncTool
{
	/** 设置玩家 */
	void setMe(RoleGroup roleGroup);
	
	/** 从库中读完数据后(做数据的补充解析)(onNewCreate后也会调用一次) */
	void afterReadDataSecond();
	
	/** 登录前(主线程) */
	void beforeLogin();
	
	/** 统计该模块的战斗力 */
	long countFightForce();
}
