using UnityEngine;

namespace ShineEngine
{
	/** 推送基类 */
	public class BaseRequest:BaseData
	{
		/** 是否写过type */
		private bool _maked=false;

		/** 是否写过type */
		private bool _writed=false;

		/** 是否需要构造复制(默认复制,不必复制的地方自己设置) */
		private bool _needMakeCopy=true;

		/** 是否需要回收 */
		private bool _needRelease=false;

		/** 是否在断开连时不缓存 */
		private bool _needCacheOnDisConnect=true;

		/** 是否需要完整写入 */
		private bool _needFullWrite=false;

		/** 是否为长消息 */
		private bool _needMessageLong=false;

		/** 写入缓存流 */
		private BytesWriteStream _stream;

		/** 此次的号 */
		public int sendMsgIndex=0;

		/** 是否已发送 */
		public volatile bool sended=false;

		/** 发送时间 */
		public long sendTime;

		public BaseRequest()
		{

		}

		/** 设置不在构造时拷贝 */
		protected void setDontCopy()
		{
			_needMakeCopy=false;
		}

		protected void setDontCache()
		{
			_needCacheOnDisConnect=false;
		}

		protected void setNeedRelease()
		{
			_needRelease=true;
		}

		public bool needRelease()
		{
			return _needRelease;
		}

		/** 是否需要在断开连接时缓存 */
		public bool needCacheOnDisConnect()
		{
			return _needCacheOnDisConnect;
		}

		/** 设置是否需要在构造时拷贝 */
		protected void setNeedMakeCopy(bool value)
		{
			_needMakeCopy=value;
		}

		/** 设置是否需要完整写入 */
		public void setNeedFullWrite(bool value)
		{
			_needFullWrite=value;
		}

		/** 设置为长消息 */
		protected void setLongMessage()
		{
			_needMessageLong=true;
		}

		/** 构造(制造数据副本) */
		protected void make()
		{
			if(_maked)
				return;

			_maked=true;

			if(_needMakeCopy)
			{
				copyData();
			}
		}

		/** 把数据拷贝下 */
		protected virtual void copyData()
		{

		}

		/** 执行写入到流 */
		protected virtual void doWriteToStream(BytesWriteStream stream)
		{
			//写协议体
			doWriteBytesSimple(stream);
		}

		/** 执行写入 */
		protected virtual void doWriteBytesSimple(BytesWriteStream stream)
		{
			beforeWrite();

			if(_needFullWrite)
				writeBytesFull(stream);
			else
				writeBytesSimple(stream);
		}

		/// <summary>
		/// 写出字节流(直接写的话不必再构造)
		/// </summary>
		public void write()
		{
			if(_writed)
				return;

			//视为构造完成
			_maked=true;

			_writed=true;

			_stream=BytesWriteStreamPool.create();
			_stream.setWriteLenLimit(getMessageLimit());

			doWriteToStream(_stream);
		}

		public BytesWriteStream getWriteStream()
		{
			return _stream;
		}

		/// <summary>
		/// 直接写出byte[](sendAbs用)
		/// </summary>
		/// <returns></returns>
		public byte[] writeToBytes()
		{
			write();

			return _stream.getByteArray();
		}

		/// <summary>
		/// 写到流里
		/// </summary>
		public void writeToStream(BytesWriteStream stream)
		{
			if(_writed)
			{
				stream.writeBytesStream(_stream,0,_stream.length());
			}
			else
			{
				doWriteToStream(stream);
			}
		}

		//send

		public int getMessageLimit()
		{
			return ShineSetting.getMsgBufSize(_needMessageLong);
		}

		/** 预备推送(系统用) */
		public void preSend()
		{
			//构造一下
			make();
		}

		public void dispose()
		{
			_maked=false;
			_writed=false;
			_stream=null;
			sended=false;
			sendMsgIndex=-1;
		}

		public bool released=false;

		/** 执行释放(主线程) */
		public void doRelease()
		{
			if(!ShineSetting.messageUsePool)
				return;

			//还没发送，就跳过回收
			if(!sended)
			{
				Ctrl.warnLog("出现一次Request释放时还没发送的情况",getDataID());
				return;
			}

			if(released)
			{
				Ctrl.errorLog("析构了两次");
			}

			released=true;
			BytesControl.releaseRequest(this);
		}
	}
}