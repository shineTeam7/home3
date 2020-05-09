package com.home.shine.server;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.net.socket.ReceiveSocket;
import com.home.shine.net.socket.ServerSocket;
import com.home.shine.utils.BytesUtils;

/** 843端口服务器 */
public class CrossDomainServer
{
	/** 策略文件字符串 */
	public static final String crossDomain="<cross-domain-policy>" + "<site-control permitted-cross-domain-policies=\"all\"/>" + "<allow-http-request-headers-from domain=\"*\" headers=\"*\"/>" + "<allow-access-from domain=\"*\" to-ports=\"*\" />" + "</cross-domain-policy>";

	/** 策略文件字符串(http) */
	public static final String crossDomainForHttp="<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<cross-domain-policy>" + "<allow-access-from domain=\"*\"/>" + "</cross-domain-policy>";
	
	private ServerSocket _server;
	
	private byte[] _crossDomain0Bytes;
	
	public CrossDomainServer()
	{
		_server=new ServerSocket(843)
		{
			@Override
			public void onStartFailed()
			{
				Ctrl.warnLog("843端口启动失败");
				
				onStartCrossFailed();
			}
			
			@Override
			public void onReceiveSocketData(ReceiveSocket socket,byte[] data)
			{
				on843RecieveSocketData(socket,data);
			}
			
			@Override
			public void onReceiveSocketClose(ReceiveSocket socket)
			{
				
			}
			
			@Override
			public void onNewReceiveSocket(ReceiveSocket socket)
			{
				socket.id=0;
			}
		};
		
		_server.setIsClient(false);
		_server.setNeedAnalyze(false);
		
		String crossDomain0=crossDomain + "\0";
		
		_crossDomain0Bytes=crossDomain0.getBytes();
	}
	
	/** 843连接收到数据 */
	private void on843RecieveSocketData(ReceiveSocket socket,byte[] data)
	{
		String str=new String(data,BytesUtils.UTFCharset);
		
		//请求
		if(str.startsWith("<policy-file-request/>"))
		{
			socket.sendBytesAbs(_crossDomain0Bytes);
		}
		else if(str.startsWith("closeServer") || str.startsWith("crossExit"))
		{
			dispose();
		}
	}
	
	protected void onStartCrossFailed()
	{
		
	}
	
	/** 启动 */
	public void init()
	{
		_server.start();
	}
	
	/** 析构 */
	public void dispose()
	{
		_server.close();
	}
}
