package com.home.shine.net.httpResponse;

import com.home.shine.constlist.HttpContentType;
import com.home.shine.constlist.ThreadType;
import com.home.shine.control.ThreadControl;
import com.home.shine.data.BaseData;
import com.home.shine.global.ShineSetting;
import com.home.shine.net.http.HttpReceive;
import com.home.shine.server.BaseServer;
import com.home.shine.support.collection.SMap;
import org.json.JSONObject;

/** http响应 */
public abstract class BaseHttpResponse extends BaseData implements Runnable
{
	/** 流水ID */
	public int httpInstanceID;
	
	/** 源地址 */
	public String url;
	/** 指令名(去掉/的) */
	public String cmd;
	/** 参数组 */
	public SMap<String,String> args;
	/** json参数 */
	public JSONObject jsonArgs;
	
	//args
	/** 是否忽略重复请求 */
	protected boolean _igorneRepeated=false;
	
	
	//分配
	/** 池序号 */
	protected int _poolIndex=0;
	/** 协议类型 */
	protected int _threadType=ThreadType.Main;
	
	//
	
	/** 服务器 */
	protected BaseServer _server;
	/** http连接 */
	protected HttpReceive _http;
	
	/** 添加时间 */
	public long addTime;
	
	//result
	//	/** 回复结果 */
	//	protected BaseData _re;
	
	/** 移除了 */
	public boolean removed=false;
	
	protected boolean _resulted=false;
	
	public BaseHttpResponse()
	{
		
	}
	
	/** 是否忽略重复请求 */
	public boolean igorneRepeated()
	{
		return _igorneRepeated;
	}
	
	/** 获取IP */
	public String remoteIP()
	{
		return _http.remoteIP();
	}
	
	/** 初始化 */
	public void init(BaseServer server,HttpReceive http)
	{
		_server=server;
		_http=http;
	}
	
	/** 默认失败返回 */
	public void failed()
	{
		result("failed");
	}
	
	protected void toRemove()
	{
		removed=true;
		
		if(httpInstanceID>0)
		{
			//删除该协议
			_server.removeHttpResponse(httpInstanceID);
			httpInstanceID=-1;
		}
	}
	
	/** 回复boolean结果(不切io线程,直接回复) */
	protected void result(boolean re)
	{
		result(re,HttpContentType.Application);
	}
	
	/** 回复boolean结果(不切io线程,直接回复) */
	protected void result(boolean re,String contentType)
	{
		result(re ? "OK" : "Failed",contentType);
	}
	
	/** 回复str结果(不切io线程,直接回复) */
	protected void result(String re)
	{
		result(re,HttpContentType.Application);
	}
	
	/** 回复str结果(不切io线程,直接回复) */
	protected void result(String re,String contentType)
	{
		if(_resulted)
		{
			return;
		}
		
		_resulted=true;
		
		_http.result(re,contentType);
		
		toRemove();
	}
	
	/** 回复结果下一步(主/逻辑线程) */
	protected void resultNext()
	{
	
	}
	
	/** 析构(到时间) */
	public synchronized void dispose()
	{
		_resulted=true;
		removed=true;
		
		toRemove();
		
		_http.dispose();
	}
	
	/** 预读取(io线程) */
	public void preRead()
	{
		read();
	}
	
	/** 读取(io线程) */
	protected void read()
	{
		if(_http.postStream!=null)
		{
			readBytesSimple(_http.postStream);
		}
	}
	
	/** 分派 */
	public void dispatch()
	{
		ThreadControl.addFuncByType(_threadType,_poolIndex,this);
	}
	
	@Override
	public void run()
	{
		//统计部分
		
		preExecute();
	}
	
	/** 预执行(逻辑线程) */
	public void preExecute()
	{
		execute();
	}
	
	/** 执行(逻辑线程) */
	protected abstract void execute();
	
	public String getArgStr(String key)
	{
		return args.get(key);
	}
	
	public long getArgLong(String key)
	{
		return Long.parseLong(args.get(key));
	}
	
	public int getArgInt(String key)
	{
		return Integer.parseInt(args.get(key));
	}
	
	public boolean getArgBoolean(String key)
	{
		return "1".equals(args.get(key));
	}
}
