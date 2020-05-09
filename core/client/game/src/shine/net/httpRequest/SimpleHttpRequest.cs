using System;

namespace ShineEngine
{
	/// <summary>
	/// 简版http请求
	/// </summary>
	public class SimpleHttpRequest:BaseHttpRequest
	{
		private Action<string> _completeFunc;

		private Action _errorFunc;

		private string _postData;

		private string _resultData;

		private SimpleHttpRequest()
		{
			_method=HttpMethodType.Get;
		}

		protected override void write()
		{
			_postStream=new BytesWriteStream();
			_postStream.writeUTFBytes(_postData);
		}

		protected override void read()
		{
			_resultData=_resultStream.readUTFBytes(_resultStream.bytesAvailable());
		}

		protected override void onComplete()
		{
			if(_completeFunc!=null)
				_completeFunc(_resultData);
		}

		protected override void onError()
		{
			if(_errorFunc!=null)
				_errorFunc();
		}

		public static SimpleHttpRequest create()
		{
			return new SimpleHttpRequest();
		}

		/// <summary>
		/// http请求
		/// </summary>
		public static void httpRequest(string url,Action<string> completeFunc)
		{
			httpRequest(url,HttpMethodType.Get,null,completeFunc);
		}

		/// <summary>
		/// http请求
		/// </summary>
		public static void httpRequest(string url,int method=HttpMethodType.Get,string data=null,Action<string> completeFunc=null,Action errorFunc=null)
		{
			if(url.IndexOf('?')==-1)
			{
				url+="?v=" + MathUtils.randomInt(1000000);
			}
			else
			{
				url+="&v=" + MathUtils.randomInt(1000000);
			}

			SimpleHttpRequest request=SimpleHttpRequest.create();
			request._url=url;
			request._method=method;
			request._postData=data;
			request._completeFunc=completeFunc;
			request._errorFunc=errorFunc;

			request.send();
		}
	}
}