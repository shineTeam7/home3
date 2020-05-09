using System;
using ShineEngine;

/// <summary>
/// 逻辑体
/// </summary>
public interface ILogic
{
	/// <summary>
	/// 构造
	/// </summary>
	void construct();

	/// <summary>
	/// 初始化
	/// </summary>
	void init();

	/// <summary>
	/// 析构
	/// </summary>
	void dispose();

	/// <summary>
	/// 刷帧
	/// </summary>
	void onFrame(int delay);
}