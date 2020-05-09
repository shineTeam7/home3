package com.home.shine.net.socket;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.utils.MathUtils;
import io.netty.buffer.ByteBuf;

/** 字节缓冲区(用来做网络接收) */
public abstract class LengthBasedFrameBytesBuffer
{
	private boolean _canGrow;
	
	private byte[] _buf;
	
	private int _length=0;
	
	private int _position=0;
	
	/** 头阶段or身体阶段 */
	private boolean _isHead=true;
	/** 头类型,也是所需长度(1:byte,2:short,4:int,5:wholeInt) */
	private int _headNeedLen=1;
	
	private int _bodyLen;
	
	public LengthBasedFrameBytesBuffer(int capacity,boolean canGrow)
	{
		_buf=new byte[capacity];
		_canGrow=canGrow;
		
		_headNeedLen=ShineSetting.needCustomLengthBasedFrameDecoder ? 1 : 2;//short头长
	}
	
	/** 添加缓冲 */
	public void append(byte[] bs,int off,int length)
	{
		int dLen;
		
		int i=0;
		
		while(i<length)
		{
			System.arraycopy(bs,off+i,_buf,_length,dLen=Math.min(length-i,_buf.length-_length));
			i+=dLen;
			_length+=dLen;
			
			if(!toRead())
				return;
		}
	}
	
	/** 添加netty流(java用)(netty线程) */
	public void append(ByteBuf buf)
	{
		int len;
		int dLen;
		
		while((len=buf.readableBytes())>0)
		{
			buf.readBytes(_buf,_length,dLen=Math.min(_buf.length-_length,len));
			_length+=dLen;
			
			if(!toRead())
				return;
		}
	}
	
	private boolean toRead()
	{
		//读一轮
		if(!read())
			return false;
		
		if(_length==_buf.length)
		{
			clean();
			
			//依然没位置
			if(_length==_buf.length)
			{
				Ctrl.warnLog("超出预设buffer大小(单包超上限)",_buf.length);
				
				if(_canGrow)
				{
					grow(_buf.length<<1);
				}
				else
				{
					onError("单包超上限,强制关闭连接");
					return false;
				}
			}
		}
		
		return true;
	}
	
	/** 添加缓冲 */
	private boolean read()
	{
		while(true)
		{
			if(_isHead)
			{
				//判定位
				
				int available=bytesAvailable();
				
				if(available >= _headNeedLen)
				{
					if(ShineSetting.needCustomLengthBasedFrameDecoder)
					{
						//读byte头
						if(_headNeedLen==1)
						{
							int n=_buf[_position] & 0xff;
							
							if(n >= 0x80)
							{
								++_position;
								_bodyLen=n & 0x7f;
								_isHead=false;
								
							}
							else if(n >= 0x40)
							{
								_headNeedLen=2;
							}
							else if(n >= 0x20)
							{
								_headNeedLen=4;
							}
							else if(n >= 0x10)
							{
								_headNeedLen=5;
							}
							else
							{
								onError("readLen,invalid number:" + n);
								return false;
							}
						}
						else
						{
							switch(_headNeedLen)
							{
								case 2:
								{
									_bodyLen=natureReadUnsignedShort() & 0x3fff;
								}
								break;
								case 4:
								{
									_bodyLen=natureReadInt() & 0x1fffffff;
								}
								break;
								case 5:
								{
									++_position;
									_bodyLen=natureReadInt();
								}
								break;
							}
							
							_isHead=false;
						}
					}
					else
					{
						_bodyLen=natureReadUnsignedShort();

						_isHead=false;
					}
				}
				else
				{
					break;
				}
			}
			else
			{
				if(bytesAvailable() >= _bodyLen)
				{
					//byte[] bb=new byte[_bodyLen];
					//int p=_position;
					//int len=_bodyLen;
					//System.arraycopy(_buf,_position,bb,0,_bodyLen);
					
					onePiece(_buf,_position,_bodyLen);
					
					_position+=_bodyLen;
					_isHead=true;
					_bodyLen=0;
					_headNeedLen=ShineSetting.needCustomLengthBasedFrameDecoder ? 1 : 4;
				}
				else
				{
					break;
				}
			}
		}
		
		return true;
	}
	
	/** 清理一下 */
	private void clean()
	{
		if(_position==0)
			return;
		
		int len=_length - _position;
		
		if(len>0)
		{
			System.arraycopy(_buf,_position,_buf,0,len);
		}
		
		_position=0;
		_length=len;
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
	
	/** 剩余可读数目 */
	private int bytesAvailable()
	{
		return _length - _position;
	}
	
	private int natureReadUnsignedShort()
	{
		int pos=_position;
		
		_position+=2;
		
		byte[] buf=_buf;
		
		return (buf[pos + 1] & 0xff) | ((buf[pos] & 0xff) << 8);
	}
	
	private int natureReadInt()
	{
		int pos=_position;
		
		_position+=4;
		
		byte[] buf=_buf;
		
		return (buf[pos + 3] & 0xff) | ((buf[pos + 2] & 0xff) << 8) | ((buf[pos + 1] & 0xff) << 16) | ((buf[pos] & 0xff) << 24);
	}
	
	/** 一片ready(netty线程) */
	protected abstract void onePiece(byte[] bytes,int pos,int len);
	
	/** 出错误 */
	protected abstract void onError(String msg);
}
