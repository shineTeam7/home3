using UnityEngine;

namespace ShineEngine
{
	/// <summary>
	/// 基础http请求
	/// </summary>
	[Hotfix(needChildren = false)]
	public class BaseHttpRequest:BaseData
	{
		/** www对象 */
		private WWW _www;

		/** 地址 */
		protected string _url;

		/** httpMethod */
		protected int _method=HttpMethodType.Get;

		/** post数据 */
		protected BytesWriteStream _postStream;

		/** 结果数据 */
		protected BytesReadStream _resultStream;

		/// <summary>
		/// 超时时间,默认30秒
		/// </summary>
		public int timeOut=30 * 1000;

		/** 是否处理 */
		private bool _dided=false;

		public BaseHttpRequest()
		{
		}

		/** 把数据拷贝下 */
		protected virtual void copyData()
		{
		}

		/// <summary>
		/// 发送
		/// </summary>
		public void send()
		{
			write();

			if(_method==HttpMethodType.Get)
			{
				//WWWForm f = new WWWForm();
				_www=new WWW(_url);
			}
			else if(_method==HttpMethodType.Post)
			{
				_www=new WWW(_url,_postStream.getByteArray());
			}

			NetControl.addHttpRequest(this);
		}

		protected virtual void write()
		{
		}

		/// <summary>
		/// 是否完成
		/// </summary>
		public bool isDone()
		{
			return _www.isDone;
		}

		/** 超时 */
		public void onTimeOut()
		{
			dispose();
		}

		/// <summary>
		/// 析构
		/// </summary>
		public void dispose()
		{
			if(_dided)
				return;

			_dided=true;

			onError();

			if(_www!=null)
			{
				_www.Dispose();
				_www=null;
			}
		}

		/** 预完成 */
		public void preComplete()
		{
			if(_dided)
				return;

			_dided=true;

			if(!string.IsNullOrEmpty(_www.error))
			{
				onError();
			}
			else
			{
				_resultStream=new BytesReadStream(_www.bytes);

				read();

				onComplete();
			}

			if(_www!=null)
			{
				_www.Dispose();
				_www=null;
			}
		}

		protected virtual void read()
		{

		}

		/// <summary>
		/// IO出错
		/// </summary>
		protected virtual void onError()
		{

		}

		/// <summary>
		/// 完成
		/// </summary>
		protected virtual void onComplete()
		{

		}
	}
}