package com.home.shine.net.base;

import com.home.shine.bytes.BytesWriteStream;
import com.home.shine.control.BytesControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.data.BaseData;
import com.home.shine.global.ShineSetting;
import com.home.shine.net.socket.BaseSocket;

/** 推送基类 */
public class BaseRequest extends BaseData
{
	/** 是否写过type */
	private boolean _maked=false;
	
	/** 是否写过type */
	private boolean _writed=false;
	
	/** 是否需要构造复制(默认复制,不必复制的地方自己设置) */
	private boolean _needMakeCopy=true;
	
	/** 是否需要回收 */
	private boolean _needRelease=false;
	
	/** 是否在断开连时不缓存 */
	private boolean _needCacheOnDisConnect=true;
	
	/** 是否为客户端消息 */
	private boolean _needFullWrite=false;
	
	/** 是否为长消息 */
	private boolean _needMessageLong=false;
	
	/** 写入缓存流 */
	private BytesWriteStream _stream;
	
	//check
	
	/** 此次的号 */
	private int _sendMsgIndex;
	
	public BaseRequest()
	{
		
	}
	
	/** 设置构造时不拷贝 */
	public void setDontCopy()
	{
		_needMakeCopy=false;
	}
	
	/** 设置断开连接时不缓存 */
	protected void setDontCache()
	{
		_needCacheOnDisConnect=false;
	}
	
	protected void setNeedRelease()
	{
		_needRelease=true;
	}
	
	public boolean needRelease()
	{
		return _needRelease;
	}
	
	/** 设置为长消息 */
	protected void setLongMessage()
	{
		_needMessageLong=true;
	}
	
	/** 是否在断开连时缓存 */
	public boolean needCacheOnDisConnect()
	{
		return _needCacheOnDisConnect;
	}
	
	/** 设置是否需要在构造时拷贝 */
	public void setNeedMakeCopy(boolean bool)
	{
		_needMakeCopy=bool;
	}
	
	/** 设置是否需要完整写入 */
	public void setNeedFullWrite(boolean bool)
	{
		_needFullWrite=bool;
	}
	
	/** 是否需要序号 */
	public boolean needIndex()
	{
		return true;
	}
	
	/** 构造(制造数据副本) */
	protected void make()
	{
		if(_maked)
			return;
		
		_maked=true;
		
		if(_needMakeCopy)
		{
			copyData();
		}
	}
	
	/** 把数据拷贝下 */
	protected void copyData()
	{
		
	}
	
	/** 执行写入到流 */
	protected void doWriteToStream(BytesWriteStream stream)
	{
		//写协议体
		doWriteBytesSimple(stream);
	}
	
	/** 写协议内容 */
	protected void doWriteBytesSimple(BytesWriteStream stream)
	{
		if(_needFullWrite)
			writeBytesFull(stream);
		else
			writeBytesSimple(stream);
	}
	
	/** 写出字节流(直接写的话不必再构造) */
	public final void write()
	{
		if(!ShineSetting.openRadioMessageWrite)
			return;
		
		if(_writed)
			return;
		
		//视为构造完成
		_maked=true;
		
		_writed=true;
		
		doWrite();
	}
	
	private void doWrite()
	{
		_stream=BytesWriteStream.create(ShineSetting.radioMessageDefaultLength);
		
		doWriteToStream(_stream);
	}
	
	/** 直接写出byte[](sendAbs用) */
	public byte[] writeToBytes()
	{
		doWrite();
		
		return _stream.getByteArray();
	}
	
	public byte[] getWriteBytes()
	{
		return _stream.getByteArray();
	}
	
	/** 写到流里(IO线程) */
	public void writeToStream(BytesWriteStream stream)
	{
		if(_writed)
		{
			stream.writeBytesStream(_stream,0,_stream.length());
		}
		else
		{
			doWriteToStream(stream);
		}
	}
	
	//send
	
	public int getMessageLimit()
	{
		return ShineSetting.getMsgBufSize(_needFullWrite,_needMessageLong);
	}
	
	/** 预备推送(系统用) */
	public void preSend()
	{
		//构造一下
		make();
	}
	
	/** 推送消息到socket(糖，少吃) */
	public void sendTo(BaseSocket socket)
	{
		if(socket!=null)
		{
			socket.send(this);
		}
	}
}
