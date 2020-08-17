namespace ShineEngine
{
	/// <summary>
	/// 响应基类
	/// </summary>
	[Hotfix(needChildren = false)]
	public abstract class BaseResponse:BaseData
	{
		/** 是否执行过 */
		public volatile bool executed=false;

		public bool isNewOne=true;

		/** 连接 */
		public BaseSocket socket;

		/** 是否完整读 */
		private bool _needFullRead=false;

		/** 是否为长消息 */
		private bool _needMessageLong=false;

		/** 是否需要回收 */
		private bool _needRelease=false;

		public BaseResponse()
		{

		}

		/** 是否需要完整读 */
		protected void setNeedFullRead(bool value)
		{
			_needFullRead=value;
		}

		/** 设置为长消息 */
		protected void setLongMessage()
		{
			_needMessageLong=true;
		}

		/** 是否是长消息 */
		public bool isLongMessage()
		{
			return _needMessageLong;
		}

		public void setNeedRelease()
		{
			_needRelease=true;
		}

		public bool needRelease()
		{
			return _needRelease;
		}

		/// <summary>
		/// 从流读取
		/// </summary>
		/// <param name="stream"></param>
		public virtual BaseResponse readFromStream(BytesReadStream stream)
		{
			doReadFromStream(stream);

			return this;
		}

		/** 从流读取 */
		protected void doReadFromStream(BytesReadStream stream)
		{
			if(_needFullRead)
				readBytesFull(stream);
			else
				readBytesSimple(stream);
		}

		/// <summary>
		/// 执行入口
		/// </summary>
		public void run()
		{
			//统计部分
			preExecute();

			executed=true;
		}

		/// <summary>
		/// 预备执行
		/// </summary>
		protected virtual void preExecute()
		{
			execute();
		}

		/// <summary>
		/// 执行
		/// </summary>
		protected virtual void execute()
		{

		}

		/** 析构 */
		public void dispose()
		{
			socket=null;
			isNewOne=false;
			executed=false;
		}
	}
}