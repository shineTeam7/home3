using System;
using UnityEngine;
using Random = System.Random;

namespace ShineEngine
{
	/// <summary>
	/// 数学方法
	/// </summary>
	public static class MathUtils
	{
		/** float PI */
		public const float fPI=(float)Math.PI;
		/** float PI*2 */
		public const float fPI2=(float)(Math.PI+Math.PI);
		/** float PI/2 */
		public const float fPI_half=(float)Math.PI/2;

		/** 弧度转角度 */
		public const float RadToDeg=180f/fPI;
		/** 角度转弧度 */
		public const float DegToRad=fPI/180f;

		private static Random _rand=new Random();

		/** float相等比较 */
		public static bool floatEquals(float a,float b)
		{
			if(a==b)
				return true;

			float d=a - b;

			return d>-0.000001f && d<0.000001f;
		}

		/** 是否2次幂 */
		public static bool isPowerOf2(int n)
		{
			return (n & (n - 1))==0;
		}

		/** 取2为底对数 */
		public static int log2(int i)
		{
			int r=0;
			while((i>>=1)!=0)
			{
				++r;
			}

			return r;
		}

		/** 获取该数字的字节使用位数 */
		public static int getIntBitNum(int n)
		{
			return (int)(32-numberOfLeadingZeros((uint)n));
		}

		public static uint numberOfLeadingZeros(uint i)
		{
			// HD, Figure 5-6
			if(i==0u)
				return 32u;

			uint n=1u;
			if(i >> 16==0u)
			{
				n+=16u;
				i<<=16;
			}
			if(i >> 24==0u)
			{
				n+=8u;
				i<<=8;
			}
			if(i >> 28==0u)
			{
				n+=4u;
				i<<=4;
			}
			if(i >> 30==0u)
			{
				n+=2u;
				i<<=2;
			}
			n-=i >> 31;
			return n;
		}

		/** 获取该数字的2次幂(re>=n) */
		public static int getPowerOf2(int n)
		{
			if((n & (n - 1))==0)
				return n;

			int wei=(int)(32U - numberOfLeadingZeros(Convert.ToUInt32(n)));

			return 1 << wei;
		}

		/// <summary>
		/// 循环取模运算
		/// （区别于%运算，例：absMode(-1,3) = 2)
		/// </summary>
		public static int absMod(int a,int b)
		{
			return a - (int)Math.Floor((float)a / b) * b;
		}

		/// <summary>
		/// 循环取模运算
		/// （区别于%运算，例：absMode(-1,3) = 2)
		/// </summary>
		public static float absMode(float a,float b)
		{
			return a - (int)Math.Floor(a / b) * b;
		}

		/// <summary>
		/// 产生一个(0-1)的double
		/// </summary>
		public static double random()
		{
			return _rand.NextDouble();
		}

		/** 随一0-1的float */
		public static float randomFloat()
		{
			return (float)_rand.NextDouble();
		}

		/** 随一个完全的整形 */
		public static int randomInt()
		{
			return _rand.Next();
		}

		/** 随一个完全的整形 */
		public static long randomLong()
		{
			return _rand.Next();
		}

		/// <summary>
		/// 产生一个整形(0<=value<range)
		/// </summary>
		/// <param name="len"></param>
		/// <returns></returns>
		public static int randomInt(int len)
		{
			return _rand.Next(len);
		}

		/** 随一整形(start<=value<end) */
		public static int randomRange(int start,int end)
		{
			if(end<=start)
				return -1;

			return start+randomInt(end-start);
		}

		/** 随一整形(start<=value<=end)(包括结尾) */
		public static int randomRange2(int start,int end)
		{
			return randomRange(start,end+1);
		}

		/** 随机一个朝向 */
		public static float randomDirection()
		{
			return randomFloat()*fPI-fPI2;
		}

		/** 随机上下浮动 */
		public static int randomOffSet(int value,int offset)
		{
			return value-offset+randomInt(offset<<1);
		}

		/** 随机上下浮动 */
		public static float randomOffSetF(float value,float offset)
		{
			return value-offset+(randomFloat()*offset*2);
		}

		/** 随机上下浮动(千分比) */
		public static int randomOffSetPercent(int value,int offset)
		{
			return (int)(value*(1f+(randomInt(offset<<1)-offset)/1000f));
		}

		/// <summary>
		/// ran一bool值
		/// </summary>
		public static bool randomBoolean()
		{
			return _rand.NextDouble()>=0.5;
		}

		/** 判定千分位几率 */
		public static bool randomProb(int prob)
		{
			return randomProb(prob,1000);
		}

		/** 判定几率 */
		public static bool randomProb(int prob,int max)
		{
			if(prob >= max)
			{
				return true;
			}

			if(prob<=0)
			{
				return false;
			}

			return randomFloat()<(float)prob / max;
		}

		/** 上下限获取 */
		public static int clamp(int value,int min,int max)
		{
			if (value < min)
				value = min;
			else if (value > max)
				value = max;
			return value;
		}

		/** 上下限获取 */
		public static float clamp(float value,float min,float max)
		{
			if (value < min)
				value = min;
			else if (value > max)
				value = max;
			return value;
		}

		/** 上下限获取 */
		public static double clamp(double value,double min,double max)
		{
			if (value < min)
				value = min;
			else if (value > max)
				value = max;
			return value;
		}

		//--以下是图像计算--//

		/** 弧度归到0-2PI */
		public static float cutRadian(float value)
		{
			while(value<0)
			{
				value+=fPI2;
			}

			while(value>fPI2)
			{
				value-=fPI2;
			}

			return value;
		}

		/** 拷贝Vecter3 */
		public static void copyVec3(ref Vector3 vec,in Vector3 value)
		{
			vec.Set(value.x,value.y,value.z);
		}

		/** 两点间距离 */
		public static float distanceBetweenPoint(Vector2 v0,Vector2 v1)
		{
			float dx=v1.x-v0.x;
			float dy=v1.y-v0.y;
			return Mathf.Sqrt(dx * dx + dy * dy);
		}

		/** 两点间距离 */
		public static float distanceBetweenPoint(float x0,float y0,float x1,float y1)
		{
			float dx=x1-x0;
			float dy=y1-y0;
			return Mathf.Sqrt(dx * dx + dy * dy);
		}

		/** 弧度归位(-π 到 π) */
		public static float directionCut(float direction)
		{
			while(direction>fPI)
				direction-=fPI2;

			while(direction<-fPI)
				direction+=fPI2;

			return direction;
		}

		/** 角度归位(-180 到 180) */
		public static float angleCut(float angle)
		{
			while(angle>180)
				angle-=360;

			while(angle<-180)
				angle+=360;

			return angle;
		}

		/** 反向 */
		public static float directionInverse(float direction)
		{
			return directionCut(direction+fPI);
		}

		/** 弧度转角度(带cut) */
		public static float directionToAngle(float direction)
		{
			return angleCut(direction*RadToDeg);
		}

		/** 角度转弧度(带cut) */
		public static float angleToDirection(float angle)
		{
			return directionCut(angle*DegToRad);
		}

		/** 返回量朝向1 与 朝向2之差，带cut */
		public static float getDirectionD(float direction0,float direction1)
		{
			return angleCut(direction1 - direction0);
		}

		/** 返回量朝向1 与 朝向2之差绝对值 */
		public static float getDirectionDAbs(float direction0,float direction1)
		{
			return Math.Abs(angleCut(direction1 - direction0));
		}




		public static int intCompare(int x,int y)
		{
			return (x < y) ? -1 : ((x == y) ? 0 : 1);
		}

		public static int doubleCompare(double x,double y)
		{
			return (x < y) ? -1 : ((x == y) ? 0 : 1);
		}

		public static int longCompare(long x,long y)
		{
			return (x < y) ? -1 : ((x == y) ? 0 : 1);
		}

		/** 将pos安置于rect内 */
		public static void makePosInRect(ref Vector2 pos,Rect rect)
		{
			if(pos.x<rect.x)
				pos.x=rect.x;

			if(pos.x>rect.xMax)
				pos.x=rect.xMax;

			if(pos.y<rect.y)
				pos.y=rect.y;

			if(pos.y>rect.yMax)
				pos.y=rect.yMax;
		}

		/** 符号相同 */
		public static bool sameSymbol(float a,float b)
		{
			return (a<0 && b<0) || (a>=0 && b>=0);
		}

		public static bool doubleIsFinite(double d)
		{
			return (BitConverter.DoubleToInt64Bits(d) & long.MaxValue) < 9218868437227405312L;
		}

		public static unsafe bool floatIsFinite(float d)
		{
			int a=*(int*)&d;

			return (a & int.MaxValue) < 2139095040;
		}
	}
}