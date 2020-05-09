package com.home.shine.support.pool;

import com.home.shine.control.BytesControl;
import com.home.shine.data.BaseData;
import com.home.shine.net.base.BaseRequest;
import com.home.shine.support.collection.IntObjectMap;

/** 数据池 */
public class DataPool
{
	/** 数据池 */
	private IntObjectMap<ObjectPool<BaseData>> _dataDic=new IntObjectMap<>(ObjectPool[]::new);
	/** 发消息池 */
	private IntObjectMap<ObjectPool<BaseData>> _requestDic=new IntObjectMap<>(ObjectPool[]::new);
	
	public DataPool()
	{
	
	}
	
	private ObjectPool<BaseData> createDataPool(int type)
	{
		ObjectPool<BaseData> pool=new ObjectPool<>(()->BytesControl.getDataByID(type));
		pool.setNeedClear(false);
		return pool;
	}
	
	private ObjectPool<BaseData> createRequestPool(int type)
	{
		ObjectPool<BaseData> pool=new ObjectPool<>(()->BytesControl.getRequestByID(type));
		pool.setNeedClear(false);
		return pool;
	}
	
	public BaseData createData(int type)
	{
		ObjectPool<BaseData> pool=_dataDic.get(type);
		
		if(pool==null)
		{
			return BytesControl.getDataByID(type);
		}
		
		return pool.getOne();
	}
	
	public BaseData createRequest(int type)
	{
		ObjectPool<BaseData> pool=_requestDic.get(type);
		
		if(pool==null)
		{
			return BytesControl.getRequestByID(type);
		}
		
		return pool.getOne();
	}
	
	public void releaseData(BaseData data)
	{
		if(data instanceof BaseRequest)
		{
			toReleaseRequest(data);
		}
		else
		{
			toReleaseData(data);
		}
	}
	
	private void toReleaseData(BaseData data)
	{
		ObjectPool<BaseData> pool=_dataDic.get(data.getDataID());
		
		if(pool==null)
		{
			_dataDic.put(data.getDataID(),pool=createDataPool(data.getDataID()));
		}
		
		pool.back(data);
	}
	
	private void toReleaseRequest(BaseData data)
	{
		ObjectPool<BaseData> pool=_requestDic.get(data.getDataID());
		
		if(pool==null)
		{
			_requestDic.put(data.getDataID(),pool=createRequestPool(data.getDataID()));
		}
		
		pool.back(data);
	}
}
