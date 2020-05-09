package com.home.commonBase.config.base;

import com.home.shine.data.BaseData;

/** 配置基类 */
public class BaseConfig extends BaseData
{
	@Override
	protected final void afterRead()
	{
		afterReadConfig();
	}
	
	/** 读完配置 */
	protected void afterReadConfig()
	{
		
	}
	
	/** 更新内容 */
	public void refresh()
	{
		generateRefresh();
	}
	
	/** 生成的刷新 */
	protected void generateRefresh()
	{
	
	}
}
