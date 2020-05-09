package com.home.shine.dataEx;

import java.util.zip.Deflater;
import java.util.zip.Inflater;


/** 字节压缩数据 */
public class BytesCompressData
{
	public Deflater compresser=new Deflater();
	
	public Inflater decompresser=new Inflater();
	
	public byte[] buffer=new byte[1024];
	
	private byte[] _outBuffer=new byte[1024];
	
	private int _outPosition=0;
	
	public void clearOut()
	{
		_outPosition=0;
	}
	
	/** 将buffer的内容写到out里 */
	public void writeOut(int bufferLen)
	{
		while(_outPosition + bufferLen>_outBuffer.length)
		{
			byte[] bs=new byte[_outBuffer.length << 1];
			
			System.arraycopy(_outBuffer,0,bs,0,_outBuffer.length);
			
			_outBuffer=bs;
		}
		
		System.arraycopy(buffer,0,_outBuffer,_outPosition,bufferLen);
		
		_outPosition+=bufferLen;
	}
	
	/** 获取结果 */
	public byte[] getResult()
	{
		byte[] bs=new byte[_outPosition];
		
		System.arraycopy(_outBuffer,0,bs,0,_outPosition);
		
		return bs;
	}
}
