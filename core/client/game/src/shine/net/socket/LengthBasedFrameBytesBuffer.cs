using System;
using System.Net.Sockets;

namespace ShineEngine
{
	public class LengthBasedFrameBytesBuffer
	{
		private byte[] _buf;

		private int _length;

		private int _position=0;

		/** 头阶段or身体阶段 */
		private bool _isHead=true;

		/** 头类型,也是所需长度(1:byte,2:short,4:int,5:wholeInt) */
		private int _headNeedLen=1;

		private int _bodyLen;

		private Action<byte[],int,int> _pieceCall;

		private Action<string> _errorCall;


		public LengthBasedFrameBytesBuffer(int capacity)
		{
			_buf=new byte[capacity];
			_headNeedLen=ShineSetting.needCustomLengthBasedFrameDecoder ? 1 : 2;//short头长
		}

		/** 添加缓冲 */
		public void append(byte[] bs,int off,int length)
		{
			int dLen=0;

			int i=0;

			while(i<length)
			{
				try
				{
					Buffer.BlockCopy(bs,off+i,_buf,_length,dLen=Math.Min(length-i,_buf.Length-_length));
				}
				catch(Exception e)
				{
					Ctrl.printExceptionForIO(e);
					Ctrl.print(off,length,i,_buf.Length,_length);
				}

				i+=dLen;
				_length+=dLen;

				if(!toRead())
					return;
			}
		}

		/// <summary>
		/// 读socket数据一次(IO线程)
		/// </summary>
		public bool readSocketOne(Socket socket)
		{
			int space=_buf.Length - _length;

			int len;

			try
			{
				len=socket.Receive(_buf,_length,space,SocketFlags.None);
			}
			catch(Exception e)
			{
				// Ctrl.printExceptionForIO(e);
				return false;
			}

			if(len>0)
			{
				_length+=len;

				toRead();
			}

			return true;
		}

		public byte[] getBuf()
		{
			return _buf;
		}

		public int getSapce()
		{
			return _buf.Length - _length;
		}

		public int getLength()
		{
			return _length;
		}

		public void doReadLength(int len)
		{
			_length+=len;

			toRead();
		}

		private bool toRead()
		{
			if(!read())
				return false;

			if(_length==_buf.Length)
			{
				clean();

				//依然没位置
				if(_length==_buf.Length)
				{
					Ctrl.warnLogForIO("超出预设buffer大小(单包超上限)",_buf.Length);

					grow(_buf.Length<<1);
					
					// if(ShineSetting.needGrowOnByteLimit)
					// {
					// 	grow(_buf.Length<<1);
					// }
					// else
					// {
					// 	onError("单包超上限,强制关闭连接");
					// 	return false;
					// }
				}
			}

			return true;
		}

		/** gc一下 */
		public void clean()
		{
			if(_position==0)
				return;

			int len;

			if((len=_length - _position)>0)
			{
				Buffer.BlockCopy(_buf,_position,_buf,0,len);
			}

			_position=0;
			_length=len;
		}

		private void grow(int len)
		{
			int cap=MathUtils.getPowerOf2(len);

			if(cap>_buf.Length)
			{
				byte[] bs=new byte[cap];

				Buffer.BlockCopy(_buf,0,bs,0,_buf.Length);

				_buf=bs;
			}
		}

		private bool read()
		{
			while(true)
			{
				if(_isHead)
				{
					//判定位

					int available=bytesAvailable();

					if(available>=_headNeedLen)
					{
						if(ShineSetting.needCustomLengthBasedFrameDecoder)
						{
							//读byte头
							if(_headNeedLen==1)
							{
								int n=_buf[_position] & 0xff;

								if(n>=0x80)
								{
									++_position;
									_bodyLen=n & 0x7f;
									_isHead=false;
								}
								else if(n>=0x40)
								{
									_headNeedLen=2;
								}
								else if(n>=0x20)
								{
									_headNeedLen=4;
								}
								else if(n>=0x10)
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
					if(bytesAvailable()>=_bodyLen)
					{
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

		/// <summary>
		/// 每片回调
		/// </summary>
		public void setPieceCall(Action<byte[],int,int> func)
		{
			_pieceCall=func;
		}

		/// <summary>
		/// 出错回调
		/// </summary>
		public void setErrorCall(Action<string> func)
		{
			_errorCall=func;
		}

		/** 一片ready */
		private void onePiece(byte[] bytes,int pos,int len)
		{
			try
			{
				if(_pieceCall!=null)
				{
					_pieceCall(bytes,pos,len);
				}
			}
			catch(Exception e)
			{
				Ctrl.errorLog(e);
			}
		}

		/** 出错误 */
		private void onError(string msg)
		{
			if(_errorCall!=null)
			{
				_errorCall(msg);
			}
		}
	}
}