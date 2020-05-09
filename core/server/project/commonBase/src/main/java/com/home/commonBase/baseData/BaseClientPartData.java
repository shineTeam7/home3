package com.home.commonBase.baseData;

import com.home.shine.data.BaseData;

/** 客户端模块数据 */
public class BaseClientPartData extends BaseData
{
	//TODO:如果只是初始化用的话,可以考虑改成潜拷,回头验证一下
	
	/** 从服务器数据上复制数据(深拷) */
	public final void copyFromServer(BaseData data)
	{
		toCopyFromServer(data);
	}
	
	protected void toCopyFromServer(BaseData data)
	{
		
	}
}
