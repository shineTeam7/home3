package com.home.shine.utils;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.dataEx.BytesCompressData;
import com.home.shine.support.pool.SafeObjectPool;
import com.home.shine.support.pool.StringBuilderPool;
import io.netty.buffer.PooledByteBufAllocator;

import java.nio.charset.Charset;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/** 字节方法 */
public class BytesUtils
{
	/** 空字节 */
	public static final byte[] EmptyByteArr=new byte[0];
	/** UTF Charset */
	public static final Charset UTFCharset=Charset.forName("UTF-8");
	
	/** 二进制转string */
	public static String bytesToString(byte[] buf,int off,int length)
	{
		StringBuilder sb=StringBuilderPool.create();
		
		for(int i=off;i<length;++i)
		{
			String temp=Integer.toHexString(buf[i] & 0xff);
			
			if(temp.length()<2)
			{
				sb.append('0');
			}
			
			sb.append(temp);
		}
		
		return StringBuilderPool.releaseStr(sb);
	}
	
	/** 获取某一段的字节hash(short) */
	public static short getHashCheck(byte[] buf,int pos,int length)
	{
		int mark=0;
		
		byte b;
		
		int end=pos + length;
		
		for(int i=pos;i<end;++i)
		{
			b=buf[i];
			
			mark=(mark << 4) ^ (mark >> 3) ^ (b << 1) ^ (b >> 2);
		}
		
		return (short)mark;
	}
	
	/** 字节压缩buffer池 */
	private static SafeObjectPool<BytesCompressData> _compressBufferPool=new SafeObjectPool<BytesCompressData>(BytesCompressData::new,16);
	
	/** 压缩字节 */
	public static byte[] compressByteArr(byte[] bytes,int off,int len)
	{
		BytesCompressData data=_compressBufferPool.getOne();
		
		data.clearOut();
		
		Deflater df=data.compresser;
		
		df.reset();
		df.setInput(bytes,off,len);
		df.finish();
		
		while(!df.finished())
		{
			int ll=df.deflate(data.buffer);
			
			data.writeOut(ll);
		}
		
		byte[] output=data.getResult();
		
		//		data.compresser.end();
		
		_compressBufferPool.back(data);
		
		return output;
	}
	
	/** 解压缩字节 */
	public static byte[] uncompressByteArr(byte[] bytes,int off,int len)
	{
		BytesCompressData data=_compressBufferPool.getOne();
		
		data.clearOut();
		
		Inflater df=data.decompresser;
		
		df.reset();
		df.setInput(bytes,off,len);
		
		try
		{
			while(!df.finished())
			{
				int ll=df.inflate(data.buffer);
				
				data.writeOut(ll);
			}
		}
		catch(DataFormatException e)
		{
			Ctrl.errorLog(e);
		}
		
		byte[] output=data.getResult();
		
		//		data.decompresser.end();
		
		_compressBufferPool.back(data);
		
		return output;
	}
}
