namespace ShineEngine
{
	public class DataPool
	{
		private IntObjectMap<ObjectPool<BaseData>> _dataDic=new IntObjectMap<ObjectPool<BaseData>>();

		private IntObjectMap<ObjectPool<BaseData>> _requestDic=new IntObjectMap<ObjectPool<BaseData>>();

		public DataPool()
		{

		}

		private ObjectPool<BaseData> createDataPool(int type)
		{
			ObjectPool<BaseData> pool=new ObjectPool<BaseData>(()=>BytesControl.getDataByID(type));
			pool.setNeedClear(false);

			return pool;
		}

		private ObjectPool<BaseData> createRequestPool(int type)
		{
			ObjectPool<BaseData> pool=new ObjectPool<BaseData>(()=>BytesControl.getRequestByID(type));
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
			if(data is BaseRequest)
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
}