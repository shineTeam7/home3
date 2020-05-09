package com.home.shine.net.httpRequest;

import com.home.shine.bytes.BytesWriteStream;
import com.home.shine.constlist.HttpContentType;
import com.home.shine.constlist.HttpMethodType;
import com.home.shine.support.func.ObjectCall;

/** 简版http请求 */
public class SimpleHttpRequest extends BaseHttpRequest
{
	private ObjectCall<String> _complete;
	
	private Runnable _ioError;
	
	/** 发送str */
	private String _postString;
	
	private String _resultData;
	
	public SimpleHttpRequest()
	{
		
	}
	
	@Override
	protected void doWriteToStream(BytesWriteStream stream)
	{
		stream.writeUTFBytes(_postString);
	}
	
	@Override
	protected void read()
	{
		_resultData=_resultStream.readUTFBytes(_resultStream.bytesAvailable());
	}
	
	@Override
	protected void onIOError()
	{
		if(_ioError!=null)
		{
			_ioError.run();
		}
	}

	@Override
	protected void onComplete()
	{
		if(_complete!=null)
		{
			_complete.apply(_resultData);
		}
	}
	
	public static SimpleHttpRequest create()
	{
		return new SimpleHttpRequest();
	}
	
	/** http推送 */
	public static void httpRequest(String url)
	{
		httpRequest(url,HttpMethodType.Get);
	}
	
	/** http推送 */
	public static void httpRequest(String url,int method)
	{
		httpRequest(url,method,null,null,null);
	}
	
	/** http推送 */
	public static void httpRequest(String url,int method,String data,ObjectCall<String> complete,Runnable ioError)
	{
		sendHttpRequest(url,method,data,complete,ioError,false);
	}
	
	/** https推送 */
	public static void httpsRequest(String url,int method,String data,ObjectCall<String> complete,Runnable ioError)
	{
		sendHttpRequest(url,method,data,complete,ioError,true);
	}
	
	/** http,https推送 */
	public static void sendHttpRequest(String url,int method,String data,ObjectCall<String> complete,Runnable ioError,boolean isHttps)
	{
		createHttpRequest(url,method,data,complete,ioError,isHttps).send();
	}
	
	public static SimpleHttpRequest createHttpRequest(String url,int method,String data,ObjectCall<String> complete,Runnable ioError,boolean isHttps)
	{
		SimpleHttpRequest request=SimpleHttpRequest.create();
		
		//视为客户端
		request.setIsClient(true);
		
		request._method=method;
		request._url=url;
		request._postString=data;
		
		request._complete=complete;
		request._ioError=ioError;
		request.setIsHttps(isHttps);
		
		return request;
	}
}
