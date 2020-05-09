package com.home.commonBase.scene.base;

/** 逻辑体接口 */
public interface ILogic
{
	/** 构造 */
	public void construct();

	/** 初始化(与dispose成对) */
	public void init();

	/** 析构(与dispose成对) */
	public void dispose();

	/** 刷帧 */
	public void onFrame(int delay);
}
