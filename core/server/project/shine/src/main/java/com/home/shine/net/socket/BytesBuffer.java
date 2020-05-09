package com.home.shine.net.socket;

import com.home.shine.bytes.BytesReadStream;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.utils.BytesUtils;
import com.home.shine.utils.MathUtils;
import io.netty.buffer.ByteBuf;

/** 字节缓冲区 */
public class BytesBuffer
{
	private byte[] _buf;
	
	private int _length=0;
	
	private int _position=0;
	
	public BytesBuffer()
	{
		_buf=BytesUtils.EmptyByteArr;
	}
	
	private void grow(int len)
	{
		int cap=MathUtils.getPowerOf2(len);
		
		if(cap>_buf.length)
		{
			byte[] bs=new byte[cap];
			
			System.arraycopy(_buf,0,bs,0,_buf.length);
			
			_buf=bs;
		}
	}
	
	/** 添加缓冲 */
	public void append(byte[] bs,int off,int length)
	{
		int dLen=length-off;
		
		int tLen=_length + dLen;
		
		if(tLen>_buf.length)
		{
			grow(tLen);
		}
		
		System.arraycopy(bs,off,_buf,_length,dLen);
		_length=tLen;
	}
	
	/** 添加netty流(java用)(netty线程) */
	public void append(ByteBuf buf)
	{
		int length=buf.readableBytes();

		int tLen=_length + length;

		if(tLen>_buf.length)
		{
			grow(tLen);
		}

		buf.readBytes(_buf,_length,length);
		_length=tLen;
	}
	
	/** 从当前字节创建读流 */
	public BytesReadStream createReadStream()
	{
		return BytesReadStream.create(_buf,_position,_length);
	}
}
