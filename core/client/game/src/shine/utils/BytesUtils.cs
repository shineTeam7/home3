using System;
using System.IO;
using System.Text;
using UnityEngine;
using zlib;

namespace ShineEngine
{
	/// <summary>
	/// 字节方法
	/// </summary>
	public static class BytesUtils
	{
		/// <summary>
		/// 空字节
		/// </summary>
		public static readonly byte[] EmptyByteArr=new byte[0];

		/** 二进制转string */
		public static string bytesToString(byte[] buf,int off,int length)
		{
			StringBuilder sb=StringBuilderPool.create();

			for(int i=off;i<length;++i)
			{
				string temp=Convert.ToString(buf[i] & 0xff,16);

				if(temp.Length<2)
				{
					sb.Append('0');
				}

				sb.Append(temp);
			}

			return StringBuilderPool.releaseStr(sb);
		}

		/// <summary>
		/// 获取某一段的字节hash(short)
		/// </summary>
		public static short getHashCheck(byte[] buf,int pos,int length)
		{
			int mark=0;
			byte b;

			int b2;

			int end=pos + length;

			for(int i=pos;i<end;++i)
			{
				b=buf[i];

				//这里的一个适应,因为C#里的byte就是unsignedByte,没有负值
				b2=b>127 ? b - 256 : b;

				mark=(mark << 4) ^ (mark >> 3) ^ (b2 << 1) ^ (b2 >> 2);
			}

			return (short)mark;
		}

		/// <summary>
		/// 获取容量大小(2^x)
		/// </summary>
		public static int getCapacitySize(int size)
		{
			if(size==0)
				return 0;

			--size;

			int times=0;

			while(size!=0)
			{
				size>>=1;
				++times;
			}

			return 1 << times;
		}

		/** 字节压缩buffer池 */
		//private static ObjectPoolSafe<BytesCompressData> _compressBufferPool = new ObjectPoolSafe<BytesCompressData>(()=> { return new BytesCompressData(); });

		/// <summary>
		/// 字节压缩(暂时未优化性能)
		/// </summary>
		public static byte[] compressByteArr(byte[] bytes,int off,int len)
		{
			MemoryStream stream=new MemoryStream();
			ZOutputStream zs=new ZOutputStream(stream,zlibConst.Z_BEST_COMPRESSION);
			zs.Write(bytes,off,len);
			zs.finish();

			byte[] re=stream.ToArray();

			zs.Close();
			stream.Close();

			return re;
		}

		/// <summary>
		/// 字节解压缩(暂时未优化性能)
		/// </summary>
		public static byte[] uncompressByteArr(byte[] bytes,int off,int len)
		{
			MemoryStream stream=new MemoryStream();
			ZOutputStream zs=new ZOutputStream(stream);
			zs.Write(bytes,off,len);
			zs.finish();

			byte[] re=stream.ToArray();

			zs.Close();
			stream.Close();

			return re;
		}

		
	}
}