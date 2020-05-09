using System;
using ShineEngine;

/// <summary>
/// 角色功能插件接口
/// </summary>
public interface IPlayerFuncTool
{
	void setMe(Player player);

	/** 从库中读完数据后(做数据的补充解析)(onNewCreate后也会调用一次) */
	void afterReadDataSecond();
}