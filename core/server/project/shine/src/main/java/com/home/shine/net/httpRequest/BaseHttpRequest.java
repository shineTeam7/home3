package com.home.shine.net.httpRequest;

import com.home.shine.bytes.BytesReadStream;
import com.home.shine.bytes.BytesWriteStream;
import com.home.shine.constlist.HttpContentType;
import com.home.shine.constlist.HttpMethodType;
import com.home.shine.constlist.ThreadType;
import com.home.shine.control.ThreadControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.data.BaseData;
import com.home.shine.global.ShineSetting;
import com.home.shine.net.http.HttpSend;
import com.home.shine.support.collection.SMap;

/** 基础http连接 */
public abstract class BaseHttpRequest extends BaseData
{
	/** 是否客户端消息 */
	protected boolean _isClient=false;
	
	/** 地址 */
	protected String _url=null;
	/** 方法 */
	protected int _method=HttpMethodType.Get;
	
	/** http头 */
	private SMap<CharSequence,Object> _headers;
	
	//分配
	/** 池序号 */
	protected int _poolIndex=0;
	/** 协议类型 */
	protected int _threadType=ThreadType.Main;
	
	/** 结果数据 */
	protected BytesReadStream _resultStream;
	
	//	/** 返回结果 */
	//	protected BaseData _re;
	
	/** http推送 */
	private HttpSend _http;
	
	/** 存在时间(ms) */
	public int timeOut=ShineSetting.httpRequestKeepTime;
	
	/** 是否删除了 */
	public volatile boolean removed=false;
	
	/** 是否同步调用 */
	private boolean _isSync=false;
	
	protected volatile boolean _syncReceipt=false;
	
	public BaseHttpRequest()
	{
		construct();
	}
	
	protected void construct()
	{
		_http=new HttpSend()
		{
			@Override
			public void onReceiveData(BytesReadStream stream)
			{
				preComplete(stream);
			}
			
			@Override
			public void onConnectFailed()
			{
				onIOError();
			}
			
			@Override
			public void writeToStream(BytesWriteStream stream)
			{
				doWriteToStream(stream);
			}
		};
		
	}
	
	/** 是否为客户端 */
	public void setIsClient(boolean bool)
	{
		_isClient=bool;
		_http.setIsClient(bool);
	}
	
	public void setNeedBase64(boolean value)
	{
		_http.setNeedBase64(value);
	}
	
	public void setIsHttps(boolean value)
	{
		_http.setIsHttps(value);
	}
	
	/** 推送(isSync:是否同步) */
	public void send()
	{
		send(false);
	}
	
	/** 推送(isSync:是否同步) */
	public void send(boolean isSync)
	{
		if(isSync && ThreadControl.getCurrentShineThread()!=null)
		{
			Ctrl.throwError("不能在shine线程调用此方法");
			return;
		}
		
		_isSync=isSync;
		
		copyData();
		
		ThreadControl.addIOFuncAbs(_http.ioIndex,()->
		{
			ThreadControl.getIOThread(_http.ioIndex).addHttpRequest(this);
		});

		_http.send(_url,_method,_headers);
		
		if(isSync)
		{
			while(!_syncReceipt && !removed)
			{
				try
				{
					Thread.sleep(1);
				}
				catch(InterruptedException e)
				{
					e.printStackTrace();
					return;
				}
			}
			
			//收到不是删除
			if(_syncReceipt)
			{
				onComplete();
			}
		}
	}
	
	/** 执行同步发送 */
	protected void doSendSync()
	{
		Ctrl.throwError("should be override");
	}
	
	/** 析构 */
	public void dispose()
	{
		removed=true;
		
		_http.close();
	}
	
	public void onTimeOut()
	{
		onIOError();
		dispose();
	}
	
	/** 把数据拷贝下 */
	protected void copyData()
	{
		
	}
	
	/** 写到流里(IO线程) */
	protected void doWriteToStream(BytesWriteStream stream)
	{
	
	}
	
	/** 写(IO线程) */
	protected void write()
	{
		
	}
	
	/** 预完成(IO线程) */
	private void preComplete(BytesReadStream stream)
	{
		if(removed)
			return;
		
		_resultStream=stream;
		
		_http.close();
		
		read();
		
		doComplete();
	}
	
	protected void doComplete()
	{
		if(_isSync)
		{
			_syncReceipt=true;
			removed=true;
		}
		else
		{
			removed=true;
			ThreadControl.addFuncByType(_threadType,_poolIndex,this::onComplete);
		}
	}
	
	protected void read()
	{
		
	}
	
	/** IO错误 */
	protected abstract void onIOError();

	/** 回调 */
	protected abstract void onComplete();
	
	public SMap<CharSequence,Object> getHeaders()
	{
		if(_headers==null)
			_headers=new SMap<>();
		
		return _headers;
	}
}
