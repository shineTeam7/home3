package com.home.shine.net.httpResponse;

import com.home.shine.bytes.BytesWriteStream;
import com.home.shine.control.ThreadControl;
import com.home.shine.data.BaseData;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.pool.BytesWriteStreamPool;

/** 二进制http */
public abstract class BytesHttpResponse extends BaseHttpResponse
{
	private BaseData _resultData;
	
	private int _resultCode;
	
	/** 是否完整写 */
	private boolean _needFullWrite=false;
	
	public BytesHttpResponse()
	{
	
	}
	
	protected void setNeedFullWrite(boolean value)
	{
		_needFullWrite=value;
	}
	
	@Override
	protected void read()
	{
		if(_http.postStream!=null)
		{
			if(_needFullWrite)
				readBytesFull(_http.postStream);
			else
				readBytesSimple(_http.postStream);
		}
	}
	
	@Override
	protected void resultNext()
	{
		BytesWriteStream stream=BytesWriteStreamPool.create();
		
		stream.writeByte(_resultCode);
		
		if(_resultData!=null)
		{
			if(_needFullWrite)
				_resultData.writeBytesFull(stream);
			else
				_resultData.writeBytesSimple(stream);
		}
		
		_http.result(stream);
		
		BytesWriteStreamPool.release(stream);
	}
	
	/** 回复错误code */
	public void resultError(int code)
	{
		if(_resulted)
			return;
		
		_resulted=true;
		_resultCode=code;
		
		toRemove();
		
		//直接发送
		resultNext();
	}
	
	/** 回复结果数据 */
	public void resultData(BaseData data)
	{
		if(_resulted)
			return;
		
		_resulted=true;
		
		_resultCode=0;
		_resultData=data;
		
		toRemove();
		
		//切io线程
		ThreadControl.addIOFunc(_http.ioIndex,this::resultNext);
	}
	
	/** 回复写好的流数据(写好code的) */
	public void resultStream(BytesWriteStream stream)
	{
		if(_resulted)
			return;
		
		_resulted=true;
		
		toRemove();
		_http.result(stream);
	}
	
	/** 回复写好的流数据(写好code的) */
	public void resultByteArr(byte[] arr)
	{
		if(_resulted)
			return;
		
		_resulted=true;
		
		toRemove();
		_http.result(arr);
	}
}
