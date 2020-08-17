package com.home.shine.net.base;

import com.home.shine.bytes.BytesReadStream;
import com.home.shine.constlist.ThreadType;
import com.home.shine.control.BytesControl;
import com.home.shine.control.ThreadControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.data.BaseData;
import com.home.shine.global.ShineSetting;
import com.home.shine.net.socket.BaseSocket;
import com.home.shine.server.BaseServer;
import com.home.shine.thread.AbstractThread;

/** 响应基类 */
public abstract class BaseResponse extends BaseData implements Runnable
{
	/** 连接 */
	protected BaseSocket socket;
	
	/** 是否完整读 */
	private boolean _needFullRead=false;
	/** 是否为长消息 */
	private boolean _needMessageLong=false;
	/** 是否需要回执 */
	private boolean _needReceipt=false;
	/** 是否需要回收 */
	private boolean _needRelease=false;
	
	//分配
	/** 池序号 */
	protected int _poolIndex=0;
	/** 线程类型(默认主线程) */
	protected int _threadType=ThreadType.Main;
	
	/** 创建线程序号 */
	public byte createThreadInstance=-1;
	/** 回收链表尾 */
	public BaseResponse releaseLinkTail;
	
	public BaseResponse()
	{
		
	}
	
	public int getThreadType()
	{
		return _threadType;
	}
	
	/** 设置使用主线程 */
	protected void setUseMainThread()
	{
		_threadType=ThreadType.Main;
	}
	
	/** 设置使用池线程 */
	protected void setUsePoolThread()
	{
		_threadType=ThreadType.Pool;
		_poolIndex=this.socket.id & ThreadControl.poolThreadNumMark;
	}
	
	/** 是否需要完整读 */
	protected void setNeedFullRead(boolean value)
	{
		_needFullRead=value;
	}
	
	/** 设置为长消息 */
	protected void setLongMessage()
	{
		_needMessageLong=true;
	}
	
	/** 是否是长消息 */
	public boolean isLongMessage()
	{
		return _needMessageLong;
	}
	
	public void setNeedReceipt(boolean value)
	{
		_needReceipt=value;
	}
	
	public void setNeedRelease()
	{
		_needRelease=true;
	}
	
	public boolean needRelease()
	{
		return _needRelease;
	}
	
	/** 设置socket */
	public void setSocket(BaseSocket socket)
	{
		this.socket=socket;
	}
	
	/** 从流读取 */
	public BaseResponse readFromStream(BytesReadStream stream,BaseServer server)
	{
		doReadFromStream(stream);
		
		return this;
	}
	
	/** 从流读取 */
	protected void doReadFromStream(BytesReadStream stream)
	{
		if(ShineSetting.messageUsePool)
		{
			AbstractThread thread;
			if(_needRelease && (thread=ThreadControl.getCurrentShineThread())!=null)
			{
				if(thread.instanceIndex!=this.createThreadInstance)
				{
					Ctrl.errorLog("Response的readFromStream线程与创建线程不同",this.getDataID(),thread.getName());
				}
				
				stream.setDataPool(thread.pool);
			}
		}
		
		if(_needFullRead)
			readBytesFull(stream);
		else
			readBytesSimple(stream);
	}
	
	/** 派发 */
	public void dispatch()
	{
		doDispatch();
	}
	
	/** 执行派发 */
	protected void doDispatch()
	{
		//丢到对应线程
		//关注点 解析完数据丢回主线程处理
		ThreadControl.addFuncByType(_threadType,_poolIndex,this);
	}
	
	@Override
	public void run()
	{
		//统计部分
		preExecute();
		
		BytesControl.preReleaseResponse(this);
	}
	
	/** 预备执行(第一层) */
	protected void preExecute()
	{
		doExecute();
	}
	
	/** 预备执行(第二层) */
	protected void doExecute()
	{
		execute();
		
		if(_needReceipt)
		{
			sendReceipt();
		}
	}
	
	/** 执行 */
	protected abstract void execute();
	
	/** 发送回执 */
	protected void sendReceipt()
	{
	
	}
	
	/** 析构 */
	public void dispose()
	{
		socket=null;
	}
}
