package com.home.shine.net.httpRequest;

import com.home.shine.bytes.BytesReadStream;
import com.home.shine.bytes.BytesWriteStream;
import com.home.shine.constlist.HttpMethodType;
import com.home.shine.data.BaseData;
import com.home.shine.global.ShineSetting;

/** 二进制HttpRequest */
public abstract class BytesHttpRequest extends BaseHttpRequest
{
	/** -1:io问题,0:成功,>0:逻辑问题 */
	protected int _result=-1;
	
	private boolean _needFullWrite=false;
	
	public BytesHttpRequest()
	{
		_method=HttpMethodType.Post;
	}
	
	public void setIsClient(boolean bool)
	{
		super.setIsClient(bool);
		
		_needFullWrite=bool;
	}
	
	public void setNeedFullWrite(boolean value)
	{
		_needFullWrite=value;
	}
	
	@Override
	protected void onIOError()
	{
		doComplete();
	}
	
	@Override
	protected void doWriteToStream(BytesWriteStream stream)
	{
		stream.setWriteLenLimit(ShineSetting.getMsgBufSize(_needFullWrite,false));
		
		//写协议号
		stream.natureWriteUnsignedShort(_dataID);
		
		if(_needFullWrite)
			writeBytesFull(stream);
		else
			writeBytesSimple(stream);
	}
	
	@Override
	protected void read()
	{
		if(_resultStream==null || _resultStream.isEmpty())
		{
			return;
		}
		
		_result=_resultStream.readByte();
		
		if(!_resultStream.isEmpty())
		{
			toRead();
		}
	}
	
	/** 继续读 */
	protected void toRead()
	{
		
	}
	
	protected void readResult(BaseData data,BytesReadStream stream)
	{
		if(_needFullWrite)
			data.readBytesFull(stream);
		else
			data.readBytesSimple(stream);
	}
	
	/** 发送到某URL(bytes用) */
	public void sendToURL(String url,boolean isSync)
	{
		_url=url+"/"+ ShineSetting.bytesHttpCmd;
		send(isSync);
	}
	
	/** 发送到某URL(bytes用) */
	public void sendToHP(String host,int port,boolean isSync)
	{
		_url="http://"+host+":"+port+"/"+ ShineSetting.bytesHttpCmd;
		send(isSync);
	}
}
