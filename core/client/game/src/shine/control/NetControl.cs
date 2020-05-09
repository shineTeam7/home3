namespace ShineEngine
{
	/// <summary>
	/// 网络控制
	/// </summary>
	public class NetControl
	{
		/** httpRequest组 */
		private static SSet<BaseHttpRequest> _httpRequestDic=new SSet<BaseHttpRequest>();

		// /** socket组(需要,为了exit) */
		private static SSet<BaseSocket> _socketDic=new SSet<BaseSocket>();

		/** 是否析构了 */
		private static bool _disposed=false;

		/// <summary>
		/// 初始化
		/// </summary>
		public static void init()
		{
			TimeDriver.instance.setFrame(onFrame);
		}

		/// <summary>
		/// 析构
		/// </summary>
		public static void dispose()
		{
			if(_disposed)
				return;

			_disposed=true;

			_httpRequestDic.forEachS(v=>
			{
				v.dispose();
			});

			_httpRequestDic.clear();

			_socketDic.forEachS(v=>
			{
				v.dispose();
			});
			
			_socketDic.clear();
		}

		/** 检查http消息 */
		private static void onFrame(int delay)
		{
			if(!_httpRequestDic.isEmpty())
			{
				foreach(var v in _httpRequestDic)
				{
					if(v.isDone())
					{
						v.preComplete();
						_httpRequestDic.remove(v);
					}
					else
					{
						v.timeOut-=delay;

						if(v.timeOut<=0)
						{
							v.onTimeOut();
							_httpRequestDic.remove(v);
						}
					}
				}
			}

			//socket

			// _socketDic.forEachS(v=>
			// {
			// 	v.onFrame(delay);
			// });
		}

		/// <summary>
		/// 添加httpRequest
		/// </summary>
		public static void addHttpRequest(BaseHttpRequest request)
		{
			_httpRequestDic.add(request);
		}

		/// <summary>
		/// 添加socket
		/// </summary>
		public static void addSocket(BaseSocket socket)
		{
			_socketDic.add(socket);
		}
		
		/// <summary>
		/// 删除socket
		/// </summary>
		public static void removeSocket(BaseSocket socket)
		{
			_socketDic.remove(socket);
		}
	}
}