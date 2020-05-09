using System;
using System.Net.Sockets;

namespace ShineEngine
{
	/// <summary>
	/// 字节缓冲区(用来做网络接收)
	/// </summary>
	public class BytesBuffer
	{
		private byte[] _buf;

		private int _length;

		private int _position=0;

		public BytesBuffer()
		{
			_buf=BytesUtils.EmptyByteArr;
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

		/** 添加缓冲 */
		public void append(byte[] bs,int off,int length)
		{
			int dLen=length-off;

			int tLen=_length + dLen;

			if(tLen>_buf.Length)
			{
				grow(tLen);
			}

			Buffer.BlockCopy(bs,off,_buf,_length,dLen);
			_length=tLen;
		}

		/** 从当前字节创建读流 */
		public BytesReadStream createReadStream()
		{
			return new BytesReadStream(_buf,_position,_length);
		}
	}
}