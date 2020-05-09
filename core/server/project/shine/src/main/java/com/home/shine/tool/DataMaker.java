package com.home.shine.tool;

import com.home.shine.data.BaseData;

/** 数据构造器 */
public class DataMaker extends ArrayDic<CreateDataFunc>
{
	public DataMaker()
	{
		super(CreateDataFunc[]::new);
	}
	
	/** 通过id取得Data类型 */
	public BaseData getDataByID(int dataID)
	{
		CreateDataFunc func;
		
		if((func=get(dataID))==null)
			return null;
		
		return func.create();
	}
	
	/** 是否存在某id */
	public boolean contains(int dataID)
	{
		return get(dataID)!=null;
	}
}
