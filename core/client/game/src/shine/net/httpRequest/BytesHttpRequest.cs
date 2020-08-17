namespace ShineEngine
{
	/// <summary>
	/// 字节http消息
	/// </summary>
	[Hotfix(needChildren = false)]
	public abstract class BytesHttpRequest:BaseHttpRequest
	{
		/** -1:io问题,0:成功,>0:逻辑问题 */
		protected int _result=-1;

		/** 是否完整写 */
		private bool _needFullWrite=false;

		public BytesHttpRequest()
		{
			_method=HttpMethodType.Post;
		}

		protected void setNeedFullWrite(bool value)
		{
			_needFullWrite=value;
		}

		protected override void onError()
		{
			onComplete();
		}

		protected override void write()
		{
			_postStream=new BytesWriteStream();

			_postStream.setWriteLenLimit(ShineSetting.msgBufSize);
			//协议号
			_postStream.natureWriteUnsignedShort(this.getDataID());

			if(_needFullWrite)
				writeBytesFull(_postStream);
			else
				writeBytesSimple(_postStream);
		}

		protected override void read()
		{
			if(_resultStream==null || _resultStream.bytesAvailable()==0)
				return;

			_result=_resultStream.readByte();

			if(_resultStream.bytesAvailable()>0)
			{
				toRead();
			}
		}

		protected virtual void toRead()
		{

		}

		protected void readResult(BaseData data,BytesReadStream stream)
		{
			if(_needFullWrite)
				data.readBytesFull(stream);
			else
				data.readBytesSimple(stream);
		}

		protected virtual void doSendSync()
		{
			Ctrl.throwError("不支持同步");
		}
	}
}