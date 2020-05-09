using System;

namespace ShineEngine
{
	/// <summary>
	/// 字节控制(包括Data序列化/反序列化)(compress部分没封装完)
	/// </summary>
	public class BytesControl
	{
		/** 构造组 */
		private static DataMaker _dataMaker=new DataMaker();
		/** 发送消息 */
		private static DataMaker _requestMaker=new DataMaker();

		/** 协议忽略mid */
		private static IntSet _messageIgnoreSet=new IntSet();


		//pool
		public static DataPool mainDataPool=new DataPool();

		public static DataPool netDataPool=new DataPool();

		/// <summary>
		/// 添加构造器
		/// </summary>
		public static void addDataMaker(DataMaker maker)
		{
			_dataMaker.addDic(maker);
		}

		/** 添加消息maker */
		public static void addRequestMaker(DataMaker maker)
		{
			_requestMaker.addDic(maker);
		}

		/** 初始化 */
		public static void init()
		{
			addDataMaker(new ShineDataMaker());

			//添加shine消息
			for(int i=ShineRequestType.off;i<ShineRequestType.count;i++)
			{
				addIgnoreMessage(i);
			}

		}

		//---Data---//

		/** 通过id取得Data类型 */
		public static BaseData getDataByID(int dataID)
		{
			if(dataID==-1)
				return null;

			BaseData re=_dataMaker.getDataByID(dataID);

			if(re==null)
			{
				Ctrl.throwError("找不到Data类型" + dataID);
				return null;
			}

			return re;
		}

		/** 通过id取得Request类型 */
		public static BaseData getRequestByID(int dataID)
		{
			if(dataID==-1)
			{
				return null;
			}

			BaseData re=_requestMaker.getDataByID(dataID);

			if(re==null)
			{
				Ctrl.throwError("找不到Request类型" + dataID);
				return null;
			}

			return re;
		}

		/** 数组拷贝 */
		public static byte[] byteArrCopy(byte[] src)
		{
			byte[] re=new byte[src.Length];
			Buffer.BlockCopy(src,0,re,0,src.Length);
			return re;
		}

		/** 数组拷贝(从src拷贝到des) */
		public static void arrayCopy(Array src,Array des,int length)
		{
			Array.Copy(src,0,des,0,length);
		}

		/** 添加要被忽略的消息mid */
		public static void addIgnoreMessage(int mid)
		{
			_messageIgnoreSet.add(mid);
		}

		/** 是否shine消息 */
		public static bool isIgnoreMessage(int mid)
		{
			return _messageIgnoreSet.contains(mid);
		}

		/** 创建data(主线程) */
		public static BaseData createData(int dataID)
		{
			if(ShineSetting.messageUsePool)
			{
				BaseData data=mainDataPool.createData(dataID);
				return data;
			}

			return getDataByID(dataID);
		}

		/** 创建Request消息(主线程) */
		public static BaseRequest createRequest(int dataID)
		{
			if(ShineSetting.messageUsePool)
			{
				BaseRequest request=(BaseRequest)mainDataPool.createRequest(dataID);
				request.released=false;

				return request;
			}

			return (BaseRequest)getRequestByID(dataID);
		}

		/** 创建Request消息(netIO线程) */
		public static BaseResponse createResponse(int dataID)
		{
			if(ShineSetting.messageUsePool)
			{
				BaseData data=netDataPool.createData(dataID);
				return (BaseResponse)data;
			}
			else
			{
				return (BaseResponse)getDataByID(dataID);
			}
		}

		/** 析构消息(主线程) */
		public static void releaseRequest(BaseRequest request)
		{
			if(!ShineSetting.messageUsePool)
				return;

			if(!request.needRelease())
				return;

			request.dispose();

			request.release(mainDataPool);
			// mainDataPool.releaseData(request);
		}

		/** 回收Response(IO线程) */
		public static void releaseResponse(BaseResponse response)
		{
			if(!ShineSetting.messageUsePool)
				return;

			if(!response.needRelease())
				return;

			response.dispose();

			response.release(netDataPool);
			// netDataPool.releaseData(response);
		}
	}
}